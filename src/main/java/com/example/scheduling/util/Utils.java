package com.example.scheduling.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.scheduling.Lane;
import com.example.scheduling.Library;
import com.example.scheduling.LibraryGroup;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
  /**
   * 计算字符串的汉明距离
   * @param str1 字符串1
   * @param str2 字符串2
   * @return 返回2个字符串的汉明距离
   */
  public static int calculateHammingDistant(String str1, String str2) {
    char[] s1 = str1.toCharArray();
    char[] s2 = str2.toCharArray();
    int longer = Math.max(s1.length, s2.length);
    int shorter = Math.min(s1.length, s2.length);
    int result = 0;
    for (int i=0; i<shorter; i++) {
      if (s1[i] != 'N' && s2[i] != 'N' && s1[i] != s2[i]) {
        result ++;
      }
    }
    result += longer - shorter;
    return result;
  }
  /**
   * 计算碱基比率
   * map{"A":[15%,12%...],"T":[12%,12%...],"C":[15%,12%...],"G":[12%,12%...],"N":[15%,12%...]}
   * 数组长度不限
   * @param list 用于计算碱基比率的列表
   * @return 碱基比率
   */
  public static Map<String, List<Float>> calculateBaseRatio(List<CommonComponent.BaseRatioInfo> list) {
    Map<String, List<Float>> map = new HashMap<>();
    map.put("A", new ArrayList<>());
    map.put("T", new ArrayList<>());
    map.put("C", new ArrayList<>());
    map.put("G", new ArrayList<>());
    map.put("N", new ArrayList<>());
    int length = list.get(0).getSequence().length();
    for(int i=0; i<length; i++) {
      float sumA=0,sumT=0,sumC=0,sumG=0,sumN=0,sum=0,sumATCG=0;
      for(CommonComponent.BaseRatioInfo bri:list) {
        char c = bri.getSequence().charAt(i);
        sum += bri.getDataSize();
        sumATCG += bri.getDataSize();
        switch (c) {
          case 'A' -> sumA += bri.getDataSize();
          case 'T' -> sumT += bri.getDataSize();
          case 'C' -> sumC += bri.getDataSize();
          case 'G' -> sumG += bri.getDataSize();
          default -> {
            sumN += bri.getDataSize();
            sumATCG -= bri.getDataSize();
          }
        }
      }
      map.get("A").add(sumA / sumATCG);
      map.get("T").add(sumT / sumATCG);
      map.get("C").add(sumC / sumATCG);
      map.get("G").add(sumG / sumATCG);
      map.get("N").add(sumN / sum);
    }
    return map;
  }
  /**
   * 根据提供的F/R及规则，生成用于计算的index序列
   * @param rule 拆分规则
   * @param F 对应的是I7
   * @param R 对应的是I5
   * @return 返回各种indextype下的index序列的map
   */
  public static Map<CommonComponent.IndexType, String> generateIndexSeqMap(String rule, String F, String R) {
    Map<CommonComponent.IndexType, String> map = new HashMap<>();
    CommonComponent.IndexType.stream().forEach(type -> {
      CommonComponent.FRPair frPair = CommonComponent.IndexType.decorateFR(type, F, R);
      map.put(type, generateSeq(rule, frPair.getF(), frPair.getR()));
    });
    return map;
  }
  /**
   * 根据提供的F/R及规则，生成用于计算的index序列
   * @param rule 拆分规则
   * @param F 对应的是I7
   * @param R 对应的是I5
   * @return index序列
   */
  public static String generateSeq(String rule, String F, String R) {
    StringBuilder sb = new StringBuilder();
    if(rule.equals("")) {
      if(R.length() > 0 && !R.contains("N")) {
        rule = "R正+F正";
      } else {
        rule = "F正";
      }
    }
    switch (rule) {
      case "I5正+I7正", "R正+F正", "R+F", "I7正", "F正", "F" -> {
        sb.append(R).append(F);
      }
      case "I5正+I7反", "R正+F反", "R+F反", "I7反", "F反" -> {
        sb.append(R).append(getReverseComplementaryString(F));
      }
      case "I5反+I7正", "R反+F正", "R反+F" -> {
        sb.append(getReverseComplementaryString(R)).append(F);
      }
      case "I5反+I7反", "R反+F反" -> {
        sb.append(getReverseComplementaryString(R)).append(getReverseComplementaryString(F));
      }
      case "F+R" -> {
        sb.append(F).append(R);
      }
      case "F反+R" -> {
        sb.append(getReverseComplementaryString(F)).append(R);
      }
      case "F+R反" -> {
        sb.append(F).append(getReverseComplementaryString(R));
      }
      case "F反+R反" -> {
        sb.append(getReverseComplementaryString(F)).append(getReverseComplementaryString(R));
      }
      default -> {
        System.out.println(rule);
        System.out.println("*************************************Oops! error: split rule error*************************************");
      }
    }
    return sb.toString();
  }
  /**
   * 获取碱基互补序列
   * @param source 原始序列
   * @return 返回碱基互补序列
   */
  public static String getReverseComplementaryString(String source) {
    Map<String, String> map = new HashMap<>();
    map.put("A", "T");
    map.put("T", "A");
    map.put("C", "G");
    map.put("G", "C");
    map.put("N", "N");
    StringBuilder sb = new StringBuilder();
    char[] array = source.toCharArray();
    for(int i=source.length()-1;i >= 0;i--) {
      sb.append(map.get(array[i]+""));
    }
    return sb.reverse().toString();
  }

  /**
   * 输出碱基比率时的表头
   * @param indexType index类型
   * @return 返回碱基比率的表头
   */
  public static List<List<String>> getBaseRatioExcelHead(CommonComponent.IndexType indexType) {
    List<List<String>> list = ListUtils.newArrayList();
    List<String> head0 = ListUtils.newArrayList();
    head0.add("Base");
    list.add(head0);
    int columnSize = 0;
    switch (indexType) {
      case P8 -> columnSize = 16;
      case P10 -> columnSize = 20;
      case S8 -> columnSize = 8;
      case S10 -> columnSize = 10;
    }
    for(int i=0;i<columnSize;i++) {
      List<String> head = ListUtils.newArrayList();
      head.add(i + "");
      list.add(head);
    }
    return list;
  }

  /**
   * 输出碱基比率时的数据
   * @return 碱基比率时的数据
   */
  public static List<List<Object>> getBaseRatioExcelDataList(Map<String, List<Float>> map) {
    List<List<Object>> list = new ArrayList<>();
    map.forEach((key, value) -> {
      List<Object> data = new ArrayList<>();
      data.add(key);
      data.addAll(value);
      list.add(data);
    });
    return list;
  }

  /**
   * 获取排单数据列表
   * @param lane lane
   * @param laneNumber lane的序号
   * @return 返回列表数据
   */
  public static List<ExcelDataOutput> getScheduledExcelDataList(Lane lane, int laneNumber) {
    List<ExcelDataOutput> list = new ArrayList<>();
    List<LibraryGroup> lgList = lane.getLibraryGroupList();
    for (LibraryGroup lg : lgList) {
      list.addAll(getScheduledExcelDataList(lg, laneNumber, lane.getIndexType().toString()));
    }
    return list;
  }

  /**
   * 按文库组获取排单数据列表
   * @param lg 文库组
   * @param laneNumber lane编号
   * @param indexType index类型
   * @return 返回数据列表
   */
  public static List<ExcelDataOutput> getScheduledExcelDataList(LibraryGroup lg, Integer laneNumber, String indexType) {
    List<ExcelDataOutput> list = new ArrayList<>();
    List<Library> lList = lg.getLibraryList();
    for (Library l : lList) {
      ExcelDataOutput edo = new ExcelDataOutput();
      edo.setProductName(l.getProductName());
      edo.setElutionLibraryName(l.getElutionLibraryName());
      edo.setGeneplusCode(l.getGeneplusCode());
      edo.setSubLibraryName(l.getLibraryName());
      edo.setF(l.getF());
      edo.setR(l.getR());
      edo.setDataSize(l.getDataSize());
      edo.setPoolingCode(l.getPoolingCode());
      edo.setSplitRule(l.getSplitRule());
      edo.setNotes(l.getNotes());
      if(laneNumber != null) {
        edo.setLaneNumber(laneNumber);
        edo.setIndexType(indexType);
      }
      list.add(edo);
    }
    return list;
  }

  /**
   * 获取当前时间，格式化时间
   * @return
   */
  public static String getCurrentTime() {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
    return dateFormat.format(calendar.getTime());
  }
  /**
   * 查看当前lane中，文库组列表能否加入到lane列表的其他lane中
   * @param currentLaneIndex 当前lane在列表中的index
   * @param targetList 文库组列表
   * @param laneList lane列表
   * @return 可否加入
   */
  public static Boolean canAddLibraryGroupListToLane(Integer currentLaneIndex, List<LibraryGroup> targetList, List<Lane> laneList) {
    Map<LibraryGroup, Lane> map = new HashMap<>();
    boolean allFit = true;
    for(LibraryGroup lg:targetList) {
      boolean singleFit = false;
      for(int i=0;i<laneList.size();i++) {
        if(i==currentLaneIndex) continue;
        Lane lane = laneList.get(i);
        if(canAddLibraryGroupToLane(lane, lg)) {
          addLibraryGroupToLane(lane, lg);
          singleFit = true;
          map.put(lg, lane);
        }
        if(singleFit) break;
      }
      if(!singleFit) {
        allFit = false;
        break;
      }
    }
    map.forEach((lg, lane) -> {
      removeLibraryGroupFromLane(lane, lg);
    });
    return allFit;
  }
  /**
   * 将当前lane中的文库组列表，从当前lane中除去，加入到lane列表的其他lane中
   * @param currentLaneIndex 当前lane在列表中的index
   * @param targetList 文库组列表
   * @param laneList lane列表
   */
  public static void addLibraryGroupListToLane(Integer currentLaneIndex, List<LibraryGroup> targetList, List<Lane> laneList) {
    // 先将目标列表移出当前的lane
    for(LibraryGroup lg:targetList) {
      for(int i=0;i<laneList.size();i++) {
        Lane lane = laneList.get(i);
        if(i == currentLaneIndex) {
          removeLibraryGroupFromLane(lane, lg);
        }
      }
    }
    for(LibraryGroup lg:targetList) {
      boolean singleFit = false;
      for(int i=0;i<laneList.size();i++) {
        Lane lane = laneList.get(i);
        if(i==currentLaneIndex) continue;
        if(canAddLibraryGroupToLane(lane, lg)) {
          addLibraryGroupToLane(lane, lg);
          singleFit = true;
        }
        if(singleFit) break;
      }
    }
  }
  /**
   * 判断能否将一个文库组加入到一个lane中，前提条件是去掉lane中的部分文库组
   * @param lane lane
   * @param target 目标文库组
   * @param excludeList 排除的文库组列表
   * @return 是否可以排入
   */
  public static Boolean canAddLibraryGroupToLane(Lane lane, LibraryGroup target, List<LibraryGroup> excludeList) {
    // 先把excludeList的内容剔除，后面再加进来
    excludeList.forEach(lg -> {
      removeLibraryGroupFromLane(lane, lg);
    });
    boolean flag = canAddLibraryGroupToLane(lane, target);
    // 把excludeList的内容再加进来
    excludeList.forEach(lg -> {
      addLibraryGroupToLane(lane, lg);
    });
    return flag;
  }
  /**
   * 将一个文库组的列表加入到一个lane中
   * @param lane 目标lane
   * @param list 目标文库组列表
   */
  public static void addLibraryGroupListToLane(Lane lane, List<LibraryGroup> list) {
    lane.getLibraryGroupList().addAll(list);
    Float size = 0f;
    Float unbalanceSize = 0f;
    Float singleEndSize = 0f;
    for (LibraryGroup libraryGroup : list) {
      size += libraryGroup.getDataSize();
      if(libraryGroup.getUnbalance()) {
        unbalanceSize += libraryGroup.getDataSize();
      }
      if(libraryGroup.getSingleEnd()) {
        singleEndSize += libraryGroup.getDataSize();
      }
    }
    lane.setDataSize(lane.getDataSize() + size);
    lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() + unbalanceSize);
    lane.setSingleEndDataSize(lane.getSingleEndDataSize() + singleEndSize);
  }
  /**
   * 判断能否将一个文库组加入到lane中
   * @param lane 目标lane
   * @param libraryGroup 目标文库组
   * @return 是否可以加入
   */
  public static boolean canAddLibraryGroupToLane(Lane lane, LibraryGroup libraryGroup) {
    Float lgDataSize = libraryGroup.getDataSize();
    CommonComponent.IndexType indexType = lane.getIndexType();
    // 判断数据量是否超过限制
    if(lane.getDataSize() + lgDataSize > lane.getDataSizeCeiling()) {
      return false;
    }
    // 不平衡文库限制
    if(libraryGroup.getUnbalance()) {
      if(libraryGroup.getUnbalanceMozhuo()) {
        if(lgDataSize + lane.getUnbalanceMozhuoDataSize() > lane.getUnbalanceMozhuoDataSizeCeiling()) return false;
      } else {
        // 如果lane中墨卓已有大于等于100G的数据，则不能排其他不平衡文库
        if(lane.getUnbalanceMozhuoDataSize() >= 100f) return false;
      }
      float laneUnbalanceDataSizeTmp = lgDataSize + lane.getUnbalanceDataSize();
      float laneDataSizeTmp = lgDataSize + lane.getDataSize();
      if(laneDataSizeTmp < lane.getDataSizeFloor()) {
        laneDataSizeTmp = lane.getDataSizeFloor();
      }
      if(laneUnbalanceDataSizeTmp / laneDataSizeTmp > lane.getUnbalanceDataSizeRatioLimit()) {
        // ToDO
        // 这里目前只是判断当前的数据需要满足情况，有一种情况未考虑，就是当前的情况不满足，但是后续在lane中新增文库组后，条件又满足了
//        System.out.println("**&&*&*&*&&(*(&*&*&*");
        return false;
      }
    }
    // 双端测序时，单端文库组的限制
    if(libraryGroup.getSingleEnd() && CommonComponent.IndexType.isPairEnd(indexType)) {
      float laneSingleEndDataSizeTmp = lgDataSize + lane.getSingleEndDataSize();
      float laneDataSizeTmp = lgDataSize + lane.getDataSize();
      if(laneDataSizeTmp < lane.getDataSizeFloor()) {
        laneDataSizeTmp = lane.getDataSizeFloor();
      }
      if(laneSingleEndDataSizeTmp / laneDataSizeTmp > lane.getSingleEndDataSizeRatioLimit()) {
        // ToDO
        // 这里目前只是判断当前的数据需要满足情况，有一种情况未考虑，就是当前的情况不满足，但是后续在lane中新增文库组后，条件又满足了
//        System.out.println("**&&*&*&*&&(*(&*&*&*");
        return false;
      }
    }
    List<LibraryGroup> laneLibraryGroupList = lane.getLibraryGroupList();
    for(LibraryGroup lg:laneLibraryGroupList) {
      // 查看同lane上机是否符合条件
      if(!lg.getSameLaneLimit().equals("") && !libraryGroup.getSameLaneLimit().equals("") &&
        !lg.getSameLaneLimit().equals("同lane上机") && !libraryGroup.getSameLaneLimit().equals("同lane上机") &&
        !lg.getSameLaneLimit().equals(libraryGroup.getSameLaneLimit())) {
        return false;
      }
      // 查看汉明距离是否符合条件
      if(lg.getHammingDistantLimitCodeMap().get(indexType).contains(libraryGroup.getCode())) {
        return false;
      }
    }
    return true;
  }
  /**
   * 将一个文库组加入到一个lane中
   * @param lane 目标lane
   * @param libraryGroup 目标文库组
   */
  public static void addLibraryGroupToLane(Lane lane, LibraryGroup libraryGroup) {
    lane.getLibraryGroupList().add(libraryGroup);
    Float size = libraryGroup.getDataSize();
    lane.setDataSize(lane.getDataSize() + size);
    if(libraryGroup.getUnbalance()) {
      lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() + size);
      if(libraryGroup.getUnbalanceMozhuo()) {
        lane.setUnbalanceMozhuoDataSize((lane.getUnbalanceMozhuoDataSize() + size));
      }
    }
    if(libraryGroup.getSingleEnd()) {
      lane.setSingleEndDataSize(lane.getSingleEndDataSize() + size);
    }
//    List<LibraryGroup> list = new ArrayList<>();
//    list.add(libraryGroup);
//    addLibraryGroupListToLane(lane, list);
  }
  /**
   * 将一个文库组移出lane
   * @param lane 目标lane
   * @param libraryGroup 目标文库组
   */
  public static void removeLibraryGroupFromLane(Lane lane, LibraryGroup libraryGroup) {
    List<LibraryGroup> list = lane.getLibraryGroupList();
    list.remove(libraryGroup);
    Float size = libraryGroup.getDataSize();
    lane.setDataSize(lane.getDataSize() - size);
    if(libraryGroup.getUnbalance()) {
      lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() - size);
      if(libraryGroup.getUnbalanceMozhuo()) {
        lane.setUnbalanceMozhuoDataSize(lane.getUnbalanceMozhuoDataSize() - size);
      }
    }
    if(libraryGroup.getSingleEnd()) {
      lane.setSingleEndDataSize(lane.getSingleEndDataSize() - size);
    }
  }

  /**
   * 通过毫秒数获取时长
   * @param millisecond 毫秒数
   * @return x时x分x秒
   */
  public static String getTimeString(final long millisecond) {
    if (millisecond < 1000) {
      return "0" + "秒";
    }
    long second = millisecond / 1000;
    long seconds = second % 60;
    long minutes = second / 60;
    long hours = 0;
    if (minutes >= 60) {
      hours = minutes / 60;
      minutes = minutes % 60;
    }
    String timeString = "";
    String secondString = "";
    String minuteString = "";
    String hourString = "";
    if (seconds < 10) {
      secondString = "0" + seconds + "秒";
    } else {
      secondString = seconds + "秒";
    }
    if (minutes < 10 && hours < 1) {
      minuteString = minutes + "分";
    } else if (minutes < 10){
      minuteString =  "0" + minutes + "分";
    } else {
      minuteString = minutes + "分";
    }
    if (hours < 10) {
      hourString = hours + "时";
    } else {
      hourString = hours + "" + "时";
    }
    if (hours != 0) {
      timeString = hourString + minuteString + secondString;
    } else {
      timeString = minuteString + secondString;
    }
    return timeString;
  }
  /**
   * 为文库组按排序规则添加序号
   * @param lgMap
   */
  public static void setLibraryGroupNumber(Map<String, LibraryGroup> lgMap) {
    List<LibraryGroup> libraryGroupList = new ArrayList<>(lgMap.values());
    libraryGroupList = libraryGroupList.stream().sorted().collect(Collectors.toList());
    for (int i = 0; i < libraryGroupList.size(); i++) {
      LibraryGroup lg = libraryGroupList.get(i);
      lg.setNumber(i + 1);
//      if(i==0) {
//        System.out.println(lg.getLibraryList().get(0).getSplitRule());
//        System.out.println(lg.getLibraryList().get(0).getF());
//        System.out.println(lg.getLibraryList().get(0).getR());
//      }
    }
  }

  /**
   * 为文库组设置汉明距离限制的文库组编号map
   * @param sourceMap 文库组map
   */
  public static void setHammingDistantLimitCodeMap(Map<String, LibraryGroup> sourceMap, Map<String, LibraryGroup> unscheduledMap) {
    // 通过文库组map获取文库组list
    List<LibraryGroup> lgList = sourceMap.values().stream().sorted().collect(Collectors.toList());
    // 遍历所有indextype，为每种indextype获取限制的code列表
    for(CommonComponent.IndexType type:CommonComponent.IndexType.values()) {
      for(int i=0;i<lgList.size();i++) {
        LibraryGroup lg1 = lgList.get(i);
        if(unscheduledMap.containsKey(lg1.getCode())) continue;
//        List<String> codeList = lg1.getHammingDistantLimitCodeMap().get(type);
        // 每次都要初始化，因为需要根据实际的lanelist，计算动态汉明距离限制列表，这个方法会多次调用
        List<String> codeList = new ArrayList<>();
        lg1.getHammingDistantLimitCodeMap().put(type, codeList);
        // 获取文库组1在当前indextype下的index序列
        List<String> list1 = lg1.getLibraryList().stream().map(library -> {
          return library.getIndexSeqMap().get(type);
        }).collect(Collectors.toList());
        for(int j=0;j<lgList.size();j++) {
          if(j==i) continue;
          LibraryGroup lg2 = lgList.get(j);
          if(unscheduledMap.containsKey(lg2.getCode())) continue;
          // 获取文库组2在当前indextype下的index序列
          List<String> list2 = lg2.getLibraryList().stream().map(library -> {
            return library.getIndexSeqMap().get(type);
          }).collect(Collectors.toList());
          // 默认情况下汉明距离限制为2
          int limit = 2;
          // 如果在双端测序的lane情况下，文库组1有标记F，则汉明距离限制为1
          if(CommonComponent.IndexType.isPairEnd(type) && (lg1.getHammingDistantF() || lg2.getHammingDistantF())) {
            limit = 1;
          }
          if(getSmallestHammingDistant(list1, list2) < limit) {
            codeList.add(lg2.getCode());
          }
        }
      }
    }
//    System.out.println("*********************** sethammingdistantlimitcode map: ");
//    List<LibraryGroup> libraryGroupList = sourceMap.values().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
//    libraryGroupList.forEach(lg -> {
//      System.out.println(lg.getNumber() + "\t" + Utils.getNumberList(sourceMap, lg.getHammingDistantLimitCodeMap().get(CommonComponent.IndexType.P8)));
//    });
  }
  /**
   * 为文库组设置汉明距离不符合的文库组吉因加编号列表
   * @param sourceMap 原始的文库组map
   */
  public static void setDynamicHammingDistantLimitCodeMap(Map<String, LibraryGroup> sourceMap, List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap) {
    // 先初始化汉明距离限制在各种indextype下的列表
    setHammingDistantLimitCodeMap(sourceMap, unscheduledMap);
    // 将map转换为list，加上排序
    List<LibraryGroup> libraryGroupList = sourceMap.values().stream().sorted().collect(Collectors.toList());
    // 通过lane列表获取indextype的list
    List<CommonComponent.IndexType> itList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
    for (LibraryGroup lg : libraryGroupList) {
      if(unscheduledMap.containsKey(lg.getCode())) continue;
      // 遍历当前indextype列表的indextype，将对应的汉明距离限制code列表取交集，设为当前文库组对应当前lane列表下的code限制列表
      List<List<String>> list = new ArrayList<>();
      for (CommonComponent.IndexType indexType : itList) {
        List<String> listTmp = lg.getHammingDistantLimitCodeMap().get(indexType);
        list.add(listTmp);
      }
      List<String> codeList = getIntersection(list);
      lg.getDynamicHammingDistantLimitCodeMap().put(itList, codeList);
    }
  }

  /**
   * 根据lane的列表和文库组间的汉明距离不符合的情况，将无法排上的文库组中按优先级（加急，不平衡，数据量；倒序）放到未排单map中
   * 核心：汉明距离冲突的相关文库组的编号列表的交集，应小于lane的数量，否则就需要丢到优先级最低的一个，然后递归，直至都可以排入
   * @param laneList lane的列表
   * @param sourceMap 文库组map
   * @param unscheduledMap 未排单文库组map
   */
  public static void moveToUnscheduledMapAccordingHammingDistance(List<Lane> laneList, Map<String, LibraryGroup> sourceMap, Map<String, LibraryGroup> unscheduledMap) {
    // 将map转换为list
    // 将list内容排序，按优先级倒序，这样最先处理的就是最可能丢到未排单map的
    List<LibraryGroup> libraryGroupList = sourceMap.values().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    // 当前lane列表对应的indextype的列表
    List<CommonComponent.IndexType> itList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
    // 判断是否需要递归
    boolean flag = false;
    for (LibraryGroup lg : libraryGroupList) {
      // 当前文库组在当前lane列表的情况下，汉明距离限制的code列表
      List<String> codeList = new ArrayList<>(List.copyOf(lg.getDynamicHammingDistantLimitCodeMap().get(itList)));
      // 当前文库组限制code列表的数据个数小于lane列表的个数，说明肯定可以排入
      if (codeList.size() < laneList.size()) continue;
      List<List<String>> list = new ArrayList<>();
      for (String s : codeList) {
        LibraryGroup lgTmp = sourceMap.get(s);
        List<String> codeListTmp = new ArrayList<>(List.copyOf(lgTmp.getDynamicHammingDistantLimitCodeMap().get(itList)));
        codeListTmp.add(lgTmp.getCode());
        list.add(codeListTmp);
      }
      codeList.add(lg.getCode());
      list.add(codeList);
      // 求汉明距离不符的数据的交集，如果交集中元素个数大于lane的数量，则需要从交集中选中一个优先级最低的放到未排单列表
      List<String> targetList = getIntersection(list);
      // 交集的数据个数小于lane列表的个数，说明肯定可以排入
      if(targetList.size() <= laneList.size()) continue;
      List<LibraryGroup> lgList = sourceMap.values().stream().filter(libraryGroup -> {
        return targetList.contains(libraryGroup.getCode());
      }).collect(Collectors.toList());
      lgList = lgList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
      String discardCode = lgList.get(0).getCode();
      unscheduledMap.put(discardCode, lgList.get(0));
//      sourceMap.remove(discardCode);
      flag = true;
      setDynamicHammingDistantLimitCodeMap(sourceMap, laneList, unscheduledMap);
      break;
    }
    if(flag) {
      moveToUnscheduledMapAccordingHammingDistance(laneList, sourceMap, unscheduledMap);
    }
  }
  /**
   * 计算两个字符串列表的最小汉明距离
   * @param list1 字符串列表1
   * @param list2 字符串列表2
   * @return 返回最小距离
   */
  public static int getSmallestHammingDistant(List<String> list1, List<String> list2) {
    int smallest = 100;
    for (String s1 : list1) {
      for (String s2 : list2) {
        smallest = Math.min(smallest, calculateHammingDistant(s1, s2));
        if (smallest == 0) {
          return smallest;
        }
      }
    }
    return smallest;
  }
  /**
   * 从有值的list中取交集
   * @param lists 待求交集的列表的列表
   * @return 交集
   */
  public static List<String> getIntersection(List<List<String>> lists) {
    if(lists == null || lists.size() == 0){
      return new ArrayList<>();
    }
//    ArrayList<List<String>> arrayList = new ArrayList<>(lists);
    ArrayList<List<String>> arrayList = new ArrayList<>();
    for (List<String> list : lists) {
      List<String> newList = new ArrayList<>();
      for(String s:list) {
        newList.add(s);
      }
      arrayList.add(newList);
    }
    for (int i = 0; i < arrayList.size(); i++) {
      List<String> list = arrayList.get(i);
      // 去除空集合
      if (list == null || list.size() == 0) {
        arrayList.remove(list);
        i-- ;
      }
    }
    // 都是空集合，返回空集合
    if(arrayList.size() == 0){
      return new ArrayList<>();
    }
    List<String> intersection = arrayList.get(0) ;
    // 只有一个非空集合，结果就是它本身
    if(arrayList.size() == 1){
      return intersection;
    }
    // 有多个非空集合，直接挨个求交集
    for (int i = 1; i < arrayList.size(); i++) {
      intersection.retainAll(arrayList.get(i));
    }
    return intersection;
  }

  /**
   * 获取lanelist和unscheduledMap中的文库组列表中，最大的序号的文库组
   * 返回值为0时，说明已经全部试过了，都不可以
   * @param laneList lanelist参数
   * @return 返回对应的序号
   */
  public static Integer getLastNumber(List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap) {
    int number = 0;
    List<LibraryGroup> list = new ArrayList<>();
    // list中加入所有lane中的文库组
    for(Lane lane:laneList) {
      if(lane.getLibraryGroupList().size() == 0) continue;
      LibraryGroup lg = lane.getLibraryGroupList().get(lane.getLibraryGroupList().size() - 1);
      list.add(lg);
    }
    // list中加入所有unscheduledMap中的文库组
    list.addAll(unscheduledMap.values());
    if(list.size() == 0) return 0;
    // list中的文库组按序号排序，获取最大的那个文库组的编号
    list = list.stream().sorted().collect(Collectors.toList());
    number = list.get(list.size() - 1).getNumber();
    return number;
  }

  /**
   * 根据上一个排入的文库组编号，结合未排单map，找到下一个要排单的文库组
   * @param lgList 文库组列表
   * @param lastNumber 上一个排入的文库组编号
   * @param unscheduledMap 未排单map
   * @return 待排单的文库组
   */
  public static LibraryGroup getNextLibraryGroup(List<LibraryGroup> lgList, Integer lastNumber, Map<String, LibraryGroup> unscheduledMap) {
    boolean flag = false;
    for(LibraryGroup lg: lgList) {
      if(lg.getNumber().equals(lastNumber)) {
        flag = true;
        continue;
      }
      if(flag && unscheduledMap.containsKey(lg.getCode())) {
        return lg;
      }
    }
    return null;
  }
  /**
   * 将文库组加入到laneList中
   * @param libraryGroupMap 文库组map
   */
  public static List<Lane> putLibraryGroupInLane(Map<String, LibraryGroup> libraryGroupMap) {
    List<Lane> laneList = new ArrayList<>();
    // 将map转换为list，按加急/不平衡/数据量排序
    List<LibraryGroup> lgList = libraryGroupMap.values().stream().collect(Collectors.toList());
    for(LibraryGroup lg:lgList) {
      Integer laneNumber = lg.getLaneNumber();
      List<Lane> ll = laneList.stream().filter(lane -> {
        return lane.getNumber().equals(laneNumber);
      }).collect(Collectors.toList());
      Lane lane = new Lane();
      if(ll.size()>0) {
        lane = ll.get(0);
      } else {
        lane.setNumber(laneNumber);
        lane.setIndexType(CommonComponent.IndexType.valueOf(lg.getLaneIndexType()));
        laneList.add(lane);
      }
      if(canAddLibraryGroupToLane(lane, lg)) {
        addLibraryGroupToLane(lane, lg);
      } else {
        System.out.println("无法将文库组放到lane中，排单同规则不一致");
      }
    };
    return laneList;
  }
  /**
   * 将文库组加入到laneList中
   * @param libraryGroupMap 文库组map
   * @param laneList lane列表
   * @param unscheduledMap 未排单文库组map
   */
  public static void putLibraryGroupInLane(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap) {
    // 将map转换为list，按加急/不平衡/数据量排序
    List<LibraryGroup> lgList = libraryGroupMap.values().stream().sorted().collect(Collectors.toList());
    for(LibraryGroup lg:lgList) {
      // 如果文库组已经被放到未排单map了，就跳过
      if(unscheduledMap.containsKey(lg.getCode())) continue;
      // 文库组是否已经加入到lane中的标识
      boolean inFlag = false;
      // lane的列表按数据量排序，从小到大
      laneList = laneList.stream().sorted(Comparator.comparing(Lane::getDataSize)).collect(Collectors.toList());
      for (Lane lane : laneList) {
        // 判断一个文库组是否可以放到某个lane中
        if (canAddLibraryGroupToLane(lane, lg)) {
          addLibraryGroupToLane(lane, lg);
          inFlag = true;
          break;
        }
      }
      // 文库组已经排入lane中，直接开始排下一个
      if(inFlag) continue;
      // 都加不进去，尝试将同这个文库组冲突的已经在lane中的文库组移动到别的lane，然后再试一下
      for (int i = 0; i < laneList.size(); i++) {
        Lane currentLane = laneList.get(i);
        // 当前文库组对应的限制code列表
        List<String> limitCodeList = lg.getHammingDistantLimitCodeMap().get(currentLane.getIndexType());
        // 当前lane对应的code列表
        List<String> currentLaneCodeList = currentLane.getLibraryGroupList().stream().map(LibraryGroup::getCode).collect(Collectors.toList());
        List<List<String>> listList = new ArrayList<>();
        listList.add(limitCodeList);
        listList.add(currentLaneCodeList);
        // 交集就是待移位的文库组code列表
        List<String> targetCodeList = getIntersection(listList);
        // 待移位的文库组列表
        List<LibraryGroup> targetLibraryGroupList = targetCodeList.stream().map(libraryGroupMap::get).collect(Collectors.toList());
        // 判断能否将待移位的文库组放到当前lane以外的其他lane中（可以是一个，也可以是多个）
        boolean targetListRearrangeFit = canAddLibraryGroupListToLane(i, targetLibraryGroupList, laneList);
        // 判断当前文库组在待移位文库组移出lane之后能否加入到lane中
        boolean currentFit = canAddLibraryGroupToLane(currentLane, lg, targetLibraryGroupList);
        // 如果条件满足，就把待移位的文库组放到其他lane，当前文库组放到当前lane
        if(targetListRearrangeFit && currentFit) {
          addLibraryGroupListToLane(i, targetLibraryGroupList, laneList);
          addLibraryGroupToLane(currentLane, lg);
          inFlag = true;
        }
        if(inFlag) break;
      }
      // 还有未排单的洗脱文库，则放到未排单文库中
      if(!inFlag) {
//        testPrint(libraryGroupMap, laneList, lg);
        addLibraryGroupToUnscheduledMap(lg, unscheduledMap);
      }
    };
  }

  public static void testPrint(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, LibraryGroup lg) {
    for (Lane lane : laneList) {
      StringBuilder sb = new StringBuilder();
      Float size = 0f;
      for (LibraryGroup lgTmp : lane.getLibraryGroupList()) {
        size += lgTmp.getDataSize();
        sb.append(lgTmp.getNumber()).append(",");
      }
      System.out.println("lane data size: " + size + "--library group(" + lane.getLibraryGroupList().size() + "): " + sb.toString());
    }
    System.out.println("放到unscheduled:" + lg.getNumber() + "--" + lg.getCode() + "--" + lg.getDataSize());
    List<CommonComponent.IndexType> its = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
    System.out.print(its);
    System.out.print(": ");
    System.out.print(lg.getDynamicHammingDistantLimitCodeMap().get(its));
    System.out.print(": ");
    List<Integer> numberList = lg.getDynamicHammingDistantLimitCodeMap().get(its).stream().map(code -> {
      return libraryGroupMap.get(code).getNumber();
    }).collect(Collectors.toList());
    System.out.println(numberList);
    System.out.println("***********************");
    System.out.print(lg.getHammingDistantLimitCodeMap().get(CommonComponent.IndexType.P8));
    System.out.print(": ");
    List<Integer> numberList2 = lg.getHammingDistantLimitCodeMap().get(CommonComponent.IndexType.P8).stream().map(code -> {
      return libraryGroupMap.get(code).getNumber();
    }).collect(Collectors.toList());
    System.out.println(numberList2);
  }
  /**
   * 从旧的indexTypeList获取新的indexTypeList
   * @param indexTypeList 旧的indexTypeList
   * @param increment 增量
   */
  public static void indexTypeListPlus(List<CommonComponent.IndexType> indexTypeList, int increment) {
    for (int m = 0; m < increment; m++) {
      for (int i = indexTypeList.size() - 1; i >= 0; i--) {
        CommonComponent.IndexType type = indexTypeList.get(i);
        if (CommonComponent.IndexType.isLast(type)) {
          indexTypeList.set(i, CommonComponent.IndexType.getFirst());
          continue;
        }
        CommonComponent.IndexType indexType = type.plus(1);
        indexTypeList.set(i, indexType);
        break;
      }
    }
  }

  /**
   * 在已经有排单列表和未排单map的情况下，填充其他数据
   * @param sr 排单结果对象
   */
  public static void setScheduledResultInfo(CommonComponent.ScheduledResult sr) {
    StringBuilder notes = new StringBuilder();
    Boolean baseBalance = checkBaseRatio(sr.getLaneList());
    Boolean dataSizeFlag = sr.getLaneList().stream().allMatch(lane -> {
      return lane.getDataSize() >= lane.getDataSizeFloor() && lane.getDataSize() <= lane.getDataSizeCeiling();
    });
    Boolean isUrgentMet = sr.getUnscheduledLibraryGroupMap().values().stream().allMatch(libraryGroup -> {
      return !libraryGroup.getUrgent();
    });
    Float unscheduledDataSize = 0f;
    for(LibraryGroup lg: sr.getUnscheduledLibraryGroupMap().values()) {
      unscheduledDataSize += lg.getDataSize();
    }
    sr.setSuccess(dataSizeFlag && baseBalance && isUrgentMet);
    sr.setUrgentMet(isUrgentMet);
    sr.setUnscheduledDataSize(unscheduledDataSize);
    if(!dataSizeFlag) {
      notes.append("数据量不符合;");
    }
    if(!baseBalance) {
      notes.append("碱基不平衡;");
    }
    if(!isUrgentMet) {
      notes.append("加急未排单;");
    }
    sr.setNotes(notes.toString());
  }
  /**
   * 获取一个lane的碱基比率数据
   * @param lane 目标lane
   * @return 返回碱基比率数据
   */
  public static Map<String, List<Float>> getBaseRatio(Lane lane) {
    List<CommonComponent.BaseRatioInfo> list = new ArrayList<>();
    for(LibraryGroup lg: lane.getLibraryGroupList()) {
      for(Library l:lg.getLibraryList()) {
        CommonComponent.BaseRatioInfo bri = new CommonComponent.BaseRatioInfo(l.getIndexSeqMap().get(lane.getIndexType()), l.getDataSize());
        list.add(bri);
      }
    }
    Map<String, List<Float>> map = calculateBaseRatio(list);
    return map;
  }
  /**
   * 检测碱基平衡
   * @return 碱基是否平衡
   */
  public static Boolean checkBaseRatio(List<Lane> laneList) {
    for(Lane lane: laneList) {
      Map<String, List<Float>> map = getBaseRatio(lane);
      for(Map.Entry<String, List<Float>> entry: map.entrySet()) {
        boolean b = false;
        switch (entry.getKey()) {
          case "A", "T", "C", "G" -> {
            b = entry.getValue().stream().allMatch(item -> {
              return (Float)item >= 0.03;
            });
          }
          case "N" -> {
            b = entry.getValue().stream().allMatch(item -> {
              return (Float)item < 0.5;
            });
          }
        }
        if(!b) {
          return false;
        }
      };
    }
    return true;
  }
  /**
   * 读取excel，获取基础数据
   * @param fileName 文件名
   * @param libraryGroupMap 待排单的文库组map，key为吉因加编号，value为文库组
   */
  public static void readExcel(String fileName, Map<String, LibraryGroup> libraryGroupMap) {
    // 排单信息对象，单例，当成全局变量，初始化基础的信息
    CommonComponent.SchedulingInfo si = CommonComponent.SchedulingInfo.getInstance();
    EasyExcel.read(fileName, ExcelData.class, new PageReadListener<ExcelData>(dataList -> {
      for (ExcelData excelData : dataList) {
        // 计算总数据量
        float itemSize = excelData.getDataSize();
        si.setDataSize(si.getDataSize() + itemSize);
        // 按行处理读取的数据
        LibraryGroup lg = null;
        String poolingCode = excelData.getPoolingCode()==null ? "":excelData.getPoolingCode();
        String productName = excelData.getProductName();
        String geneplusCode = excelData.getGeneplusCode();
        String notes = excelData.getNotes()==null ? "" : excelData.getNotes();
        Integer laneNumber = excelData.getLaneNumber()==null ? 0 : excelData.getLaneNumber();
        String laneIndexType = excelData.getLaneIndexType()==null ? "" : excelData.getLaneIndexType();
        // 判断是否在待排单的文库组中，不存在，新增；存在，加入新文库
        if(libraryGroupMap.containsKey(excelData.getGeneplusCode())) {
          lg = libraryGroupMap.get(excelData.getGeneplusCode());
        } else {
          // 判断是否需要新建一个文库组
          boolean needNew = true;

          List<String> noteList = Arrays.asList(notes.split(",|，"));
          String sameLaneLimit = "";
          for(String n: noteList) {
            if(n.matches("同lane上机\\d*?")) {
              sameLaneLimit = n;
              break;
            }
          }
          // 如果是同lane上机，并且之前已经有符合条件的同lane上机的文库组，那就直接使用之前的那个文库组
          for(LibraryGroup libraryGroup:libraryGroupMap.values()) {
            // 判断是否是同一个pooling编号，非空的同一个pooling编号放到一个文库组
            String lgPoolingCode = libraryGroup.getPoolingCode();
            if(!lgPoolingCode.equals("") && !poolingCode.equals("") && lgPoolingCode.equals(poolingCode)) {
              needNew = false;
              break;
            }
            // 获取同lane上机的限制：仅限：同lane上机
            if(libraryGroup.getProductName().equals(productName)
              && libraryGroup.getSameLaneLimit().equals("同lane上机")
              && sameLaneLimit.equals("同lane上机")) {
              lg = libraryGroup;
              needNew = false;
              break;
            }
            // 获取同lane上机限制：同lane上机1/同lane上机2。。。
            if(libraryGroup.getProductName().equals(productName)
              && libraryGroup.getCode().equals(geneplusCode)
              && sameLaneLimit.equals(libraryGroup.getSameLaneLimit())) {
              lg = libraryGroup;
              needNew = false;
              break;
            }
          }
          if(needNew) {
            si.setLibraryGroupSize(si.getLibraryGroupSize() + 1);
            lg = new LibraryGroup();
            lg.setProductName(excelData.getProductName());
            lg.setCode(excelData.getGeneplusCode());
            if(noteList.contains("加急")) {
              lg.setUrgent(true);
              si.setUrgentLibraryGroupSize(si.getUrgentLibraryGroupSize() + 1);
              si.setUrgentDataSize(si.getUrgentDataSize() + itemSize);
            } else {
              lg.setUrgent(false);
            }
            lg.setHammingDistantF(noteList.contains("F"));
            lg.setSameLaneLimit(sameLaneLimit);
            if(noteList.contains("墨卓")) {
              lg.setUnbalanceMozhuo(true);
              lg.setUnbalance(true);
            } else {
              lg.setUnbalanceMozhuo(false);
              lg.setUnbalance(noteList.contains("不平衡文库"));
            }
            lg.setPoolingCode(poolingCode);
            lg.setSingleEnd(excelData.getR()==null || excelData.getR().equals(""));
            lg.setDataSize(0f);
            lg.setLibraryList(new ArrayList<>());
            // 为文库组设置lane编号，用于检查排单结果是否正确
            lg.setLaneNumber(laneNumber);
            // 为文库组设置上机策略，用于检查排单结果是否正确
            lg.setLaneIndexType(laneIndexType);
            libraryGroupMap.put(excelData.getGeneplusCode(), lg);
          }
        }
        Library library = new Library();
        library.setProductName(excelData.getProductName());
        library.setGeneplusCode(excelData.getGeneplusCode());
        library.setElutionLibraryName(excelData.getElutionLibraryName());
        library.setLibraryName(excelData.getSubLibraryName());
        library.setDataSize(excelData.getDataSize());
        library.setIndexNum(excelData.getIndexNum());
        library.setF(excelData.getF());
        library.setR(Optional.ofNullable(excelData.getR()).orElse(""));
        library.setPoolingCode(poolingCode);
        library.setSplitRule(Optional.ofNullable(excelData.getSplitRule()).orElse(""));
        library.setNotes(excelData.getNotes());
        lg.getLibraryList().add(library);
        lg.setDataSize(lg.getDataSize() + excelData.getDataSize());
        // 计算碱基占比
        // TODO
      }
    })).sheet().doRead();
    System.out.println("data size: " + si.getDataSize());
    System.out.println("library group size: " + si.getLibraryGroupSize());
  }

  /**
   * 将结果打印出来
   * @param fileName 文件名
   */
  public static void writeExcel(String fileName, CommonComponent.ScheduledResult scheduledResult, Boolean justSuccess) {
    if(justSuccess) {
      if(!scheduledResult.getSuccess()) {
        System.out.println("排单不成功，未打印: " + fileName);
        return;
      }
    }
    List<Lane> laneList = scheduledResult.getLaneList();
    Map<String, LibraryGroup> unscheduledMap = scheduledResult.getUnscheduledLibraryGroupMap();
    ExcelWriter excelWriter = EasyExcel.write(fileName, ExcelDataOutput.class).build();
    // 排单结果
    List<ExcelDataOutput> dataList = new ArrayList<>();
    for(int i=0;i<laneList.size();i++) {
      Lane lane = laneList.get(i);
      // 如果时校验排单结果，那么这里就已经有序号了，可以直接使用
      Integer laneNumber = lane.getNumber();
      if(laneNumber == null) laneNumber = i + 1;
      dataList.addAll(getScheduledExcelDataList(lane, laneNumber));
    }
    WriteSheet sheet1 = EasyExcel.writerSheet("排单结果").build();
    // 碱基比率
    List<List<Object>> baseRatioData = new ArrayList<>();
    for(Lane lane: laneList) {
      Map<String, List<Float>> map = Utils.getBaseRatio(lane);
      baseRatioData.addAll(Utils.getBaseRatioExcelDataList(map));
      baseRatioData.add(new ArrayList<>());
    }
    WriteSheet sheet2 = EasyExcel.writerSheet("碱基比率")
      .head(Utils.getBaseRatioExcelHead(CommonComponent.IndexType.P10)).build();
    // 未排单结果
    List<ExcelDataOutput> unscheduledDataList = new ArrayList<>();
    unscheduledMap.forEach((key, value) -> {
      unscheduledDataList.addAll(Utils.getScheduledExcelDataList(value, null, null));
    });
    WriteSheet sheet3 = EasyExcel.writerSheet("未排单文库").build();
    // 写入excel
    excelWriter.write(dataList, sheet1).write(baseRatioData,sheet2).write(unscheduledDataList,sheet3).finish();
  }

  /**
   * 打印排单结果
   * @param resultList 排单结果
   */
  public static void printResult(List<CommonComponent.ScheduledResult> resultList, boolean justSuccess) {
    for(CommonComponent.ScheduledResult result: resultList) {
      if(justSuccess) {
        if(!result.getSuccess()) continue;
      }
      StringBuilder sb = new StringBuilder();
      if(result.getSuccess()) {
        sb.append("success:===");
      } else {
        sb.append("failed:");
      }
      result.getLaneList().stream().forEach(lane -> {
        sb.append("\t" + "lane: ").append(lane.getIndexType()).append(": ").append(lane.getDataSize());
        StringBuilder sbLane = new StringBuilder();
        Float size = 0f;
        for(LibraryGroup lg:lane.getLibraryGroupList()) {
          size += lg.getDataSize();
          sbLane.append(lg.getNumber()).append(",");
        }
        System.out.println("lane data size: "+size +"--library group("+lane.getLibraryGroupList().size() + "): "+sbLane.toString());
      });
      sb.append("\tunscheduled: ").append(result.getUnscheduledDataSize()).append("(").append(result.getUnscheduledLibraryGroupMap().size()).append("): ");
      for(LibraryGroup libraryGroup:result.getUnscheduledLibraryGroupMap().values()) {
        sb.append(libraryGroup.getNumber()).append(",");
      }
      sb.append("\t").append(result.getNotes());
      System.out.println(sb.toString());
      System.out.println("*******************************************************************");
    }
  }

  /**
   * 获取lane的数量
   * @return lane的数量
   */
  public static int getLaneListSize(float totalDataSize) {
    int count = 1;
    float floor = CommonComponent.SchedulingInfo.getInstance().getLaneDataSizeFloor();
    float ceiling = CommonComponent.SchedulingInfo.getInstance().getLaneDataSizeCeiling();
    for(int i=1;;i++) {
      // 数据量正好落在某个lane数量的最小和最大区间，直接用这个lane数量
      if(totalDataSize >= i*floor && totalDataSize <= i*ceiling) {
        count = i;
        break;
      }
      // 数据量正好落在某个lane数量的最大和lane数量+1的最小区间，也可以用这个lane数量
      if(totalDataSize > i*ceiling && totalDataSize < (i+1)*floor) {
        count = i;
        break;
      }
    }
    System.out.println("calculate lane size: " + count);
    return count;
  }

  /**
   * 根据lane的数量和每个lane的indextype，初始化lane列表
   * @param size lane的梳理
   * @param indexTypeList lane列表对应的indextype列表
   * @return 初始化的lane列表
   */
  public static List<Lane> initLaneList(int size, List<CommonComponent.IndexType> indexTypeList) {
    List<Lane> laneList = new ArrayList<>();
    for(int i=0;i<size;i++) {
      Lane lane = new Lane();
      lane.setDataSizeCeiling(1650f);
      lane.setDataSizeFloor(1550f);
      if(indexTypeList != null) {
        lane.setIndexType(indexTypeList.get(i));
      }
      laneList.add(lane);
    }
    return laneList;
  }
  /**
   * 对读取的数据进行预处理
   * 为文库增加修饰后的index序列
   * 判断文库组内是否有汉明距离不符合条件的
   * 为文库组按排序规则添加序号
   * 为文库组添加汉明距离不符合文库组列表
   * @param libraryGroupMap 文库组map
   */
  public static void preprocess(Map<String, LibraryGroup> libraryGroupMap) {
    // 为文库增加修饰后的的index序列
    libraryGroupMap.forEach((code, libraryGroup) -> {
      List<Library> libraryList = libraryGroup.getLibraryList();
      for(Library library: libraryList) {
        library.setIndexSeqMap(Utils.generateIndexSeqMap(library.getSplitRule(), library.getF(), library.getR()));
      }
    });
    Utils.setLibraryGroupNumber(libraryGroupMap);

    Utils.setHammingDistantLimitCodeMap(libraryGroupMap, new HashMap<>());
//    libraryGroupMap.values().stream().sorted().forEach(lg -> {
//      List<Integer> numberList = lg.getHammingDistantLimitCodeMap().get(CommonComponent.IndexType.P8).stream().map(code -> {
//        return libraryGroupMap.get(code).getNumber();
//      }).collect(Collectors.toList());
//      System.out.println(lg.getNumber() + "\t" + lg.getCode() +"\t" +lg.getDataSize()+"\t"+lg.getUrgent()+
//        "\t"+lg.getUnbalance()+"\t"+lg.getHammingDistantF()+
//        "\t"+numberList+
//        "\t"+lg.getHammingDistantLimitCodeMap().get(CommonComponent.IndexType.P8));
//    });
//    if(libraryGroupMap.size()>1) System.exit(0);
  }

  /**
   * 回溯的方式找出合适的排单结果，避免使用递归
   * 尝试将文库组列表中的文库组放到lane或unscheduledMap，并返回上一个放入的文库组的编号
   * @param libraryGroupList 待排单的文库组列表
   * @param laneList lane列表
   * @param unscheduledMap 未排单map
   * @param lastNumber 上一个排入lane中的文库组序号
   * @return 返回上一个排入lane或unscheduledMap中的文库组序号
   */
  public static int traversalMemory(List<LibraryGroup> libraryGroupList, List<Lane> laneList,Map<String, LibraryGroup> unscheduledMap, int lastNumber) {
    int number = lastNumber;
    // 找到在libraryGroupList中的最大的number，就是排单信息对象中的文库组数量
    int largestNumber = CommonComponent.SchedulingInfo.getInstance().getLibraryGroupSize();
    // 碱基不平衡的情况下，即使全部排完了，还是需要再排，将排进去的移出来换lane，再排
    if(number == largestNumber) {
      number = getBacktraceNumber(laneList, unscheduledMap, number);
    }
    // 遍历文库组列表，尝试往lane中添加
    // 从上次排入的文库组的下一个文库组开始，注意：文库组序号从1开始，文库组列表下标从0开始
    for(int i=number;i<libraryGroupList.size();i++) {
      LibraryGroup lg = libraryGroupList.get(i);
      boolean added = false;
      for (Lane lane : laneList) {
        // 判断一个文库组是否可以放到某个lane中
        if (canAddLibraryGroupToLane(lane, lg)) {
          addLibraryGroupToLane(lane, lg);
          added = true;
          number = lg.getNumber();
          break;
        }
      }
      if (added) continue;
      // 如果无法加入到lane中，就尝试加入到unscheduledMap中
      if(canAddLibraryGroupToUnscheduledMap(lg, unscheduledMap, laneList)) {
        addLibraryGroupToUnscheduledMap(lg, unscheduledMap);
        added = true;
        number = lg.getNumber();
      }
      if (added) continue;
      // 如果文库组无法加入到lane或unscheduledMap中，说明之前的排单是有问题的，要调整
      number = getBacktraceNumber(laneList, unscheduledMap, number);
//      System.out.println("***************************************************** 移位完成后：");
//      laneList.forEach(lane-> {
//        lane.getLibraryGroupList().forEach(libraryGroup1 -> {
//          System.out.print(libraryGroup1.getNumber()+";");
//        });
//        System.out.println("");
//      });
//      System.out.println("*****************************************************");
      break;
    }
    return number;
  }

  /**
   * 找到在文库组列表，但不在未排单map中的最大的文库组的number
   * @param libraryGroupList 文库组列表
   * @param unscheduledMap 未排单map
   * @return 最大的number
   */
  public static int getLargestNumber(List<LibraryGroup> libraryGroupList, Map<String, LibraryGroup> unscheduledMap) {
    // 找到在libraryGroupList中，但不在unscheduledMap中的最大的number
    int largestNumber = 1;
    for(LibraryGroup lg: libraryGroupList) {
      if(unscheduledMap.containsKey(lg.getCode())) continue;
      if(largestNumber<lg.getNumber()) largestNumber = lg.getNumber();
    }
    return largestNumber;
  }
  /**
   * 调整lane列表，并获取回溯编号
   * @param laneList lane列表
   * @param number 待移位的文库组number
   * @return 返回需要重新排的第一个文库组的number
   */
  public static Integer getBacktraceNumber(List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap, int number) {
//    System.out.println("number: "+ number);
//    StringBuilder sb = new StringBuilder();
//    for(Lane lane: laneList) {
//      sb.append("lane: ");
//      for(LibraryGroup libraryGroup:lane.getLibraryGroupList()) {
//        sb.append(libraryGroup.getNumber()).append(",");
//      }
//      System.out.println(sb.toString());
//      sb.setLength(0);
//    }
//    sb.append("unscheduled: ");
//    for(LibraryGroup libraryGroup:unscheduledMap.values()) {
//      sb.append(libraryGroup.getNumber()).append(",");
//    }
//    System.out.println(sb.toString());
    // 如果lane列表是空的，还需要调整，说明全部可能已经试过了
    if(number == 0) return number;
    // 待移位的libraryGroup
    LibraryGroup lg = null;
    // 待移位的libraryGroup所在的lane的序号
    int laneIndex = -1;
    // 找到待移位的libraryGroup，及其所在的lane的序号
    for (int i = 0; i < laneList.size(); i++) {
      Lane lane = laneList.get(i);
      List<LibraryGroup> lgListTmp = lane.getLibraryGroupList();
      for (LibraryGroup lgTmp : lgListTmp) {
        if (lgTmp.getNumber() == number) {
          laneIndex = i;
          lg = lgTmp;
          break;
        }
      }
      if(laneIndex >= 0) break;
    }
    // 如果待移位的libraryGroup在lane列表中
    if(laneIndex >= 0) {
      // 将待移位的libraryGroup移出之前所在的lane
      removeLibraryGroupFromLane(laneList.get(laneIndex), lg);
      // 如果待移位的libraryGroup不在最后一个lane，那就放到下一个lane中试试
      if(laneIndex < laneList.size() -1) {
        for(int i =laneIndex + 1;i<laneList.size();i++) {
          if(canAddLibraryGroupToLane(laneList.get(i), lg)) {
            addLibraryGroupToLane(laneList.get(i), lg);
            return lg.getNumber();
          }
        }
      }
      // 如果所有的lane都放不进去，就只能尝试放到unscheduledMap中
      if(canAddLibraryGroupToUnscheduledMap(lg, unscheduledMap, laneList)) {
        addLibraryGroupToUnscheduledMap(lg, unscheduledMap);
        return lg.getNumber();
      } else {
        // 如果lane和unscheduledMap都放不进去，就只能调整上一个放进去的文库组
        number = Utils.getLastNumber(laneList, unscheduledMap);
        return getBacktraceNumber(laneList, unscheduledMap, number);
      }
    }
    // 待移位的libraryGroup不在lane中，必然在unscheduledMap中
    for(LibraryGroup libraryGroup:unscheduledMap.values()) {
      if(libraryGroup.getNumber() == number) {
        lg = libraryGroup;
        break;
      }
    }
    // 将待移位的libraryGroup移出unscheduledMap
    removeLibraryGroupFromUnscheduledMap(lg, unscheduledMap);
    // 调整上一个libraryGroup了
    number = Utils.getLastNumber(laneList, unscheduledMap);
    return getBacktraceNumber(laneList, unscheduledMap, number);
  }

  /**
   * 判断文库组能否加入到未排单map中，条件1，不能是加急的；2，未排单map的数据量应小于总数量-所有lane的最小数据量之和
   * @param libraryGroup 待加入未排单map的文库组
   * @param unscheduledMap 未排单文库组
   * @param laneList lane列表
   * @return 返回能否加入
   */
  public static Boolean canAddLibraryGroupToUnscheduledMap(LibraryGroup libraryGroup, Map<String, LibraryGroup> unscheduledMap, List<Lane> laneList) {
    boolean flag = false;
    if(libraryGroup.getUrgent()) return false;
    Float datasize = 0f;
    Float laneDataSizeFloor = 0f;
    for(LibraryGroup lg:unscheduledMap.values()) {
      datasize += lg.getDataSize();
    }
    for(Lane l:laneList) {
      laneDataSizeFloor += l.getDataSizeFloor();
    }
    CommonComponent.SchedulingInfo si = CommonComponent.SchedulingInfo.getInstance();
    if(datasize < si.getDataSize() - laneDataSizeFloor) {
      flag = true;
    }
//    flag = 1 > 2;
    return flag;
  }

  /**
   * 将文库组加入到未排单map
   * @param libraryGroup 文库组
   * @param unscheduledMap 未排单map
   */
  public static void addLibraryGroupToUnscheduledMap(LibraryGroup libraryGroup, Map<String, LibraryGroup> unscheduledMap) {
    unscheduledMap.put(libraryGroup.getCode(), libraryGroup);
  }
  /**
   * 将文库组移出未排单map
   * @param libraryGroup 文库组
   * @param unscheduledMap 未排单map
   */
  public static void removeLibraryGroupFromUnscheduledMap(LibraryGroup libraryGroup, Map<String, LibraryGroup> unscheduledMap) {
    unscheduledMap.remove(libraryGroup.getCode());
  }
}
