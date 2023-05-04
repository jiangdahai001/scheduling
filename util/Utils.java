package com.example.scheduling.util;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.util.StringUtils;
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
   * @param str1
   * @param str2
   * @return
   */
  public static int getHammingDistant(String str1, String str2) {
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
   * JSONObject{"A":[15%,12%...],"T":[12%,12%...],"C":[15%,12%...],"G":[12%,12%...],"N":[15%,12%...]}
   * 数组长度不限
   * @param list
   * @return
   */
  public static JSONObject getBaseRatio(List<CommonComponent.BaseRatioInfo> list) {
    JSONObject result = new JSONObject();
    result.put("A", new JSONArray());
    result.put("T", new JSONArray());
    result.put("C", new JSONArray());
    result.put("G", new JSONArray());
    result.put("N", new JSONArray());
    int length = list.get(0).getSequence().length();
    for(int i=0; i<length; i++) {
      float sumA=0,sumT=0,sumC=0,sumG=0,sumN=0,sum=0;
      for(CommonComponent.BaseRatioInfo bri:list) {
        char c = bri.getSequence().charAt(i);
        sum += bri.getDataSize();
        switch(c) {
          case 'A':
            sumA += bri.getDataSize();
            break;
          case 'T':
            sumT += bri.getDataSize();
            break;
          case 'C':
            sumC += bri.getDataSize();
            break;
          case 'G':
            sumG += bri.getDataSize();
            break;
          default:
            sumN += bri.getDataSize();
        }
      }
      result.getJSONArray("A").add(sumA / sum);
      result.getJSONArray("T").add(sumT / sum);
      result.getJSONArray("C").add(sumC / sum);
      result.getJSONArray("G").add(sumG / sum);
      result.getJSONArray("N").add(sumN / sum);
    }
    return result;
  }

  /**
   * 根据提供的F/R及规则，生成用于计算的index序列
   * @param rule 拆分规则
   * @param F 对应的是I7
   * @param R 对应的是I5
   * @return
   */
  public static String generateIndexSeq(CommonComponent.IndexType type, String rule, String F, String R) {
    CommonComponent.FRPair frPair = decorateFR(type, F, R);
    String result = generateSeq(rule, frPair.getF(), frPair.getR());
    return result;
  }
  public static CommonComponent.FRPair decorateFR(CommonComponent.IndexType type, String F, String R) {
    if(type.equals(CommonComponent.IndexType.S6)) {
      // 处理S6的情况
      switch (F.length()) {
        case 6 -> {
          F = F;
        }
        case 8 -> {
          F = F.substring(2);
        }
        case 10 -> {
          F = F.substring(4);
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
      R = "";
    } else if(type.equals(CommonComponent.IndexType.S8)) {
      // 处理S8的情况
      switch (F.length()) {
        case 6 -> {
          F = "AC" + F;
        }
        case 8 -> {
          F = F;
        }
        case 10 -> {
          F = F.substring(2);
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
      R = "";
    } else if (type.equals(CommonComponent.IndexType.S10)) {
      // 处理S10的情况
      switch (F.length()) {
        case 6 -> {
          F = "TCAC" + F;
        }
        case 8 -> {
          F = "AC" + F;
        }
        case 10 -> {
          F = F;
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
      R = "";
    }else if(type.equals(CommonComponent.IndexType.P6)) {
      // 处理6+6的情况
      switch (F.length()) {
        case 6 -> {
          F = F;
          R = R.length() > 0 ? R : "NNNNNN";
        }
        case 8 -> {
          F = F.substring(2);
          R = R.length() > 0 ? R.substring(2) : "NNNNNN";
          ;
        }
        case 10 -> {
          F = F.substring(4);
          R = R.length() > 0 ? R.substring(4) : "NNNNNN";
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
    } else if(type.equals(CommonComponent.IndexType.P8)) {
      // 处理8+8的情况
      switch (F.length()) {
        case 6 -> {
          F = "AC" + F;
          R = R.length() > 0 ? "AC" + R : "NNNNNNNN";
        }
        case 8 -> {
          F = F;
          R = R.length() > 0 ? R : "NNNNNNNN";
          ;
        }
        case 10 -> {
          F = F.substring(2);
          R = R.length() > 0 ? R.substring(2) : "NNNNNNNN";
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
    } else if (type.equals(CommonComponent.IndexType.P10)) {
      // 处理10+10的情况
      switch (F.length()) {
        case 6 -> {
          F = "TCAC" + F;
          R = R.length() > 0 ? "TCAC" + R : "NNNNNNNNNN";
        }
        case 8 -> {
          F = "AC" + F;
          R = R.length() > 0 ? "AC" + R : "NNNNNNNNNN";
          ;
        }
        case 10 -> {
          F = F;
          R = R.length() > 0 ? R : "NNNNNNNNNN";
        }
        default -> System.out.println("*************************************Oops! error*************************************");
      }
    }
    return new CommonComponent.FRPair(F, R);
  };

  /**
   * 根据提供的F/R及规则，生成用于计算的index序列
   * @param rule 拆分规则
   * @param F 对应的是I7
   * @param R 对应的是I5
   * @return
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
      case "I5正+I7正", "R正+F正", "I7正", "F正" -> {
        sb.append(R);
        sb.append(F);
      }
      case "I5正+I7反", "R正+F反", "I7反", "F反" -> {
        sb.append(R);
        sb.append(getReverseComplementaryString(F));
      }
      case "I5反+I7正", "R反+F正" -> {
        sb.append(getReverseComplementaryString(R));
        sb.append(F);
      }
      case "I5反+I7反", "R反+F反" -> {
        sb.append(getReverseComplementaryString(R));
        sb.append(getReverseComplementaryString(F));
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
   * @return
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
   * 排列两个文库组的顺序，按是否加急，是否不平衡文库，数据量
   * 加急的排在前面，不平衡文库排在前面，数据量大的排在前面
   * @param lg1
   * @param lg2
   * @return
   */
//  public static int compareLibraryGroup(LibraryGroup lg1, LibraryGroup lg2) {
//    if(lg1.getNumber()!=null &&lg2.getNumber()!=null) {
//      return lg1.getNumber() - lg2.getNumber();
//    }
//    if((lg1.getUrgent() && lg2.getUrgent()) || (!lg1.getUrgent() && !lg2.getUrgent())) {
//      if(lg1.getUnbalance() && lg2.getUnbalance()) {
//        return lg2.getDataSize().compareTo(lg1.getDataSize());
//      } else if(lg1.getUnbalance()) {
//        return -1;
//      } else if(lg2.getUnbalance()) {
//        return 1;
//      } else {
//        return lg2.getDataSize().compareTo(lg1.getDataSize());
//      }
//    } else if(lg1.getUrgent()) {
//      return -1;
//    } else if(lg2.getUrgent()) {
//      return 1;
//    } else {
//      return 0;
//    }
//  }

  /**
   * 输出碱基比率时的表头
   * @return
   */
  public static List<List<String>> getBaseRatioExcelHead(CommonComponent.IndexType indexType) {
    List<List<String>> list = ListUtils.newArrayList();
    List<String> head0 = ListUtils.newArrayList();
    head0.add("Base");
    list.add(head0);
    int columnSize = 0;
    switch (indexType) {
      case P6 -> columnSize = 12;
      case P8 -> columnSize = 16;
      case P10 -> columnSize = 20;
      case S6 -> columnSize = 6;
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
   * @return
   */
  public static List<List<Object>> getBaseRatioExcelDataList(JSONObject obj) {
    List<List<Object>> list = ListUtils.newArrayList();
    for(Map.Entry<String, Object> entry:obj.entrySet()) {
      String key = entry.getKey();
      List<Object> data = ListUtils.newArrayList();
      data.add(key);
      JSONArray array = (JSONArray) entry.getValue();
      data.addAll(array);
      list.add(data);
    }
    return list;
  }

  /**
   * 获取排单数据列表
   * @param lane
   * @return
   */
  public static List<ExcelDataOutput> getScheduledExcelDataList(Lane lane) {
    List<ExcelDataOutput> list = new ArrayList<>();
    List<LibraryGroup> lgList = lane.getLibraryGroupList();
    ExcelDataOutput mark = new ExcelDataOutput();
    mark.setProductName("lane:" + lane.getDataSize());
    list.add(mark);
    for (LibraryGroup lg : lgList) {
      String urgent = lg.getUrgent() ? "加急" : "非加急";
      String unbalance = lg.getUnbalance() ? "不平衡" : "非不平衡";
      list.addAll(getScheduledExcelDataList(lg));
    }
    return list;
  }

  /**
   * 按文库组获取排单数据列表
   * @param lg
   * @return
   */
  public static List<ExcelDataOutput> getScheduledExcelDataList(LibraryGroup lg) {
    List<ExcelDataOutput> list = new ArrayList<>();
    List<Library> lList = lg.getLibraryList();
    for (Library l : lList) {
      ExcelDataOutput edo = new ExcelDataOutput();
      edo.setProductName(lg.getProductName());
      edo.setElutionLibraryName(lg.getElutionLibraryName());
      edo.setGeneplusCode(lg.getGeneplusCode());
      edo.setSubLibraryName(l.getLibraryName());
      edo.setF(l.getF());
      edo.setR(l.getR());
      edo.setDataSize(l.getDataSize());
      edo.setSplitRule(l.getSplitRule());
      edo.setNotes(l.getNotes());
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
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
    return dateFormat.format(calendar.getTime());
  }

  /**
   * 判断能否往lane中加入一个文库组的列表
   */
  public static Boolean canAddLibraryGroupListToLane(Lane lane, List<LibraryGroup> list) {
    Float lgDataSize = 0f;
    Float lgUnbalanceDataSize = 0f;
    // 判断数据量是否超过限制
    for (LibraryGroup lg : list) {
      lgDataSize += lg.getDataSize();
      if(lg.getUnbalance()) {
        lgUnbalanceDataSize += lg.getDataSize();
      }
    }
    if(lane.getDataSize() + lgDataSize > lane.getDataSizeCeiling()) {
      return false;
    }
    if(lane.getUnbalanceDataSize() + lgUnbalanceDataSize > 300) {
      return false;
    }
    for(LibraryGroup lg1: list) {
      for(LibraryGroup lg2:lane.getLibraryGroupList()) {
        if(lg2.getHammingDistantUnqualifiedGeneplusCodeList().contains(lg1.getGeneplusCode())) {
          return false;
        }
      }
    }
    return true;
  }
  /**
   * 判断能否往lane中加入一个文库组的列表
   */
  public static Boolean canAddLibraryGroupListToLane123(Lane lane, List<LibraryGroup> list) {
    boolean result = false;
    Float lgDataSize = 0f;
    Float lgUnbalanceDataSize = 0f;
    // 判断数据量是否超过限制
    for (LibraryGroup lg : list) {
      lgDataSize += lg.getDataSize();
      if(lg.getUnbalance()) {
        lgUnbalanceDataSize += lg.getDataSize();
      }
    }
    if(lane.getDataSize() + lgDataSize > lane.getDataSizeCeiling()) {
      return false;
    }
    if(lane.getUnbalanceDataSize() + lgUnbalanceDataSize > 300) {
      return false;
    }
    // 获取lane中的所有序列
    ArrayList<String> laneSeqList = new ArrayList<>();
    List<LibraryGroup> laneLibraryGroupList = lane.getLibraryGroupList();
    for (LibraryGroup libraryGroup : laneLibraryGroupList) {
      for (int j = 0; j < libraryGroup.getLibraryList().size(); j++) {
        laneSeqList.add(libraryGroup.getLibraryList().get(j).getIndexSeq());
      }
    }
    // 获取文库组列表的所有序列
    ArrayList<String> lgSeqList = new ArrayList<>();
    for (LibraryGroup lg : list) {
      List<Library> libraryList = lg.getLibraryList();
      for (Library library : libraryList) {
        lgSeqList.add(library.getIndexSeq());
      }
    }
    result = laneSeqList.stream().allMatch(seq -> {
      for(String lgSeq: lgSeqList) {
        if (Utils.getHammingDistant(lgSeq, seq) < 2) {
//          System.out.println("________________________________________________________________________________________________________");
//          System.out.println("hamming distance unfit:" + lgSeq + "---" + seq);
//          for(LibraryGroup lg:list) {
//            System.out.println(lg.getProductName()+"---"+lg.getGeneplusCode()+"---");
//            for(Library l:lg.getLibraryList()) {
//              System.out.println(l.getIndexSeq());
//            }
//          }
//          System.out.println("________________________________________________________________________________________________________");
          return false;
        }
      }
      return true;
    });
    return result;
  }
  /**
   * 将一个文库组的列表加入到一个lane中
   * @param lane
   * @param list
   */
  public static void addLibraryGroupListToLane(Lane lane, ArrayList<LibraryGroup> list) {
    lane.getLibraryGroupList().addAll(list);
    Float size = 0f;
    Float unbalanceSize = 0f;
    for (LibraryGroup libraryGroup : list) {
      size += libraryGroup.getDataSize();
      if(libraryGroup.getUnbalance()) {
        unbalanceSize += libraryGroup.getDataSize();
      }
    }
    lane.setDataSize(lane.getDataSize() + size);
    lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() + unbalanceSize);
  }
  /**
   * 判断能否将一个文库组加入到lane中
   * @param lane
   * @param libraryGroup
   * @return
   */
  public static boolean canAddLibraryGroupToLane(Lane lane, LibraryGroup libraryGroup) {
    List<LibraryGroup> list = new ArrayList<>();
    list.add(libraryGroup);
    return canAddLibraryGroupListToLane(lane, list);
  }
  /**
   * 将一个文库组加入到一个lane中
   * @param lane
   * @param libraryGroup
   */
  public static void addLibraryGroupToLane(Lane lane, LibraryGroup libraryGroup) {
    lane.getLibraryGroupList().add(libraryGroup);
    Float size = libraryGroup.getDataSize();
    Float unbalanceSize = 0f;
    if(libraryGroup.getUnbalance()) {
      unbalanceSize = libraryGroup.getDataSize();
    }
    lane.setDataSize(lane.getDataSize() + size);
    lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() + unbalanceSize);
  }
  /**
   * 将一个文库组移出lane
   * @param lane
   * @param libraryGroup
   */
  public static void removeLibraryGroupFromLane(Lane lane, LibraryGroup libraryGroup) {
    List<LibraryGroup> list = lane.getLibraryGroupList();
    list.remove(libraryGroup);
    Float size = libraryGroup.getDataSize();
    Float unbalanceSize = 0f;
    if(libraryGroup.getUnbalance()) {
      unbalanceSize = libraryGroup.getDataSize();
    }
    lane.setDataSize(lane.getDataSize() - size);
    lane.setUnbalanceDataSize(lane.getUnbalanceDataSize() - unbalanceSize);
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
    List<LibraryGroup> libraryGroupList = new ArrayList<LibraryGroup>(lgMap.values());
    libraryGroupList = libraryGroupList.stream()
//      .sorted(Utils::compareLibraryGroup)
      .sorted()
      .collect(Collectors.toList());
    for (int i = 0; i < libraryGroupList.size(); i++) {
      LibraryGroup lg = libraryGroupList.get(i);
      lg.setNumber(i + 1);
    }
  }
  /**
   * 为文库组设置汉明距离不符合的文库组吉因加编号列表
   * @param sourceMap 原始的文库组map
   */
  public static void setHDUnqualifiedGeneplusCodeList(Map<String, LibraryGroup> sourceMap) {
    // 将map转换为list
    List<LibraryGroup> libraryGroupList = new ArrayList<>(sourceMap.values());
    // 将list内容按数据量排序
    libraryGroupList = libraryGroupList.stream()
      .sorted()
      .collect(Collectors.toList());
    for(int i=0;i<libraryGroupList.size();i++) {
      LibraryGroup lg1 = libraryGroupList.get(i);
      List<String> codeList = new ArrayList<>();
      lg1.setHammingDistantUnqualifiedGeneplusCodeList(codeList);
      List<String> list1 = lg1.getLibraryList().stream().map(Library::getIndexSeq).collect(Collectors.toList());
      for(int j=0;j<libraryGroupList.size();j++) {
        if(j == i) continue;
        LibraryGroup lg2 = libraryGroupList.get(j);
        List<String> list2 = lg2.getLibraryList().stream().map(Library::getIndexSeq).collect(Collectors.toList());
        if(getSmallestHammingDistant(list1, list2) < 2) {
          codeList.add(lg2.getGeneplusCode());
        }
      }
    }
  }

  /**
   * 根据lane的数量和文库组间的汉明距离不符合的情况，将无法排上的文库组中按优先级（加急，不平衡，数据量；倒序）放到未排单map中
   * 核心：汉明距离冲突的相关文库组的吉因加编号列表的交集，应小于lane的数量，否则就需要丢到优先级最低的一个，然后递归，直至都可以排入
   * @param laneCount lane的梳理
   * @param sourceMap 文库组map
   * @param unscheduledMap 未排单文库组map
   */
  public static void moveToUnscheduledMapAccordingHammingDistance(int laneCount, Map<String, LibraryGroup> sourceMap, Map<String, LibraryGroup> unscheduledMap) {
    // 将map转换为list
    List<LibraryGroup> libraryGroupList = new ArrayList<>(sourceMap.values());
    // 将list内容排序，按优先级倒序，这样最先处理的就是最可能丢到未排单map的
    libraryGroupList = libraryGroupList.stream()
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toList());
    // 判断是否需要递归
    boolean flag = false;
    for (LibraryGroup lg : libraryGroupList) {
      List<String> codeList = new ArrayList<>(List.copyOf(lg.getHammingDistantUnqualifiedGeneplusCodeList()));
      if (codeList.size() < laneCount) continue;
      List<List<String>> list = new ArrayList<>();
      codeList.add(lg.getGeneplusCode());
      list.add(codeList);
      for (String s : codeList) {
        LibraryGroup lgTmp = sourceMap.get(s);
        List<String> codeListTmp = new ArrayList<>(List.copyOf(lgTmp.getHammingDistantUnqualifiedGeneplusCodeList()));
        codeListTmp.add(lgTmp.getGeneplusCode());
        list.add(codeListTmp);
      }
      // 求汉明距离不符的数据的交集，如果交集中元素个数大于lane的数量，则需要从交集中选中一个优先级最低的放到未排单列表
      List<String> targetList = getIntersection(list);
      if(targetList.size() <= laneCount) continue;
      List<LibraryGroup> lgList = sourceMap.values().stream().filter(libraryGroup -> {
        return targetList.contains(libraryGroup.getGeneplusCode());
      }).collect(Collectors.toList());
      lgList = lgList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
      String discardCode = lgList.get(0).getGeneplusCode();
      unscheduledMap.put(discardCode, lgList.get(0));
      sourceMap.remove(discardCode);
      flag = true;
      setHDUnqualifiedGeneplusCodeList(sourceMap);
      break;
    }
    if(flag) {
      moveToUnscheduledMapAccordingHammingDistance(laneCount, sourceMap, unscheduledMap);
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
        smallest = Math.min(smallest, getHammingDistant(s1, s2));
        if (smallest == 0) {
          return smallest;
        }
      }
    }
    return smallest;
  }
  /**
   * 从有值的list中取交集
   * @param lists
   * @return
   */
  public static List<String> getIntersection(List<List<String>> lists) {
    if(lists == null || lists.size() == 0){
      return null;
    }
    ArrayList<List<String>> arrayList = new ArrayList<>(lists);
    for (int i = 0; i < arrayList.size(); i++) {
      List<String> list = arrayList.get(i);
      // 去除空集合
      if (list == null || list.size() == 0) {
        arrayList.remove(list);
        i-- ;
      }
    }
    // 都是空集合，返回null
    if(arrayList.size() == 0){
      return null;
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
   * 获取lanelist中的文库组列表中，最大的序号的文库组
   * @param laneList lanelist参数
   * @return 返回对应的序号
   */
  public static Integer getLastNumber(List<Lane> laneList) {
    int number = 0;
    List<LibraryGroup> list = new ArrayList<>();
    for(Lane lane:laneList) {
      if(lane.getLibraryGroupList().size() == 0) continue;
      LibraryGroup lg = lane.getLibraryGroupList().get(lane.getLibraryGroupList().size() - 1);
      list.add(lg);
    }
    list = list.stream().sorted().collect(Collectors.toList());
    number = list.get(list.size() - 1).getNumber();
    return number;
  }
}
