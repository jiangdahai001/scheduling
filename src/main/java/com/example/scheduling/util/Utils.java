package com.example.scheduling.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
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
   * @param str1
   * @param str2
   * @return
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
      float sumA=0,sumT=0,sumC=0,sumG=0,sumN=0,sum=0;
      for(CommonComponent.BaseRatioInfo bri:list) {
        char c = bri.getSequence().charAt(i);
        sum += bri.getDataSize();
        switch (c) {
          case 'A' -> sumA += bri.getDataSize();
          case 'T' -> sumT += bri.getDataSize();
          case 'C' -> sumC += bri.getDataSize();
          case 'G' -> sumG += bri.getDataSize();
          default -> sumN += bri.getDataSize();
        }
      }
      map.get("A").add(sumA / sum);
      map.get("T").add(sumT / sum);
      map.get("C").add(sumC / sum);
      map.get("G").add(sumG / sum);
      map.get("N").add(sumN / sum);
    }
    return map;
  }
  /**
   * 根据提供的F/R及规则，生成用于计算的index序列
   * @param rule 拆分规则
   * @param F 对应的是I7
   * @param R 对应的是I5
   * @return
   */
  public static Map<CommonComponent.IndexType, String> generateIndexSeqMap(String rule, String F, String R) {
    Map<CommonComponent.IndexType, String> map = new HashMap<>();
    CommonComponent.IndexType.stream().forEach(type -> {
      CommonComponent.FRPair frPair = decorateFR(type, F, R);
      map.put(type, generateSeq(rule, frPair.getF(), frPair.getR()));
    });
    return map;
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
   * @param lane
   * @return
   */
  public static List<ExcelDataOutput> getScheduledExcelDataList(Lane lane) {
    List<ExcelDataOutput> list = new ArrayList<>();
    List<LibraryGroup> lgList = lane.getLibraryGroupList();
    ExcelDataOutput mark = new ExcelDataOutput();
    mark.setProductName("lane info[dataSize:" + lane.getDataSize() + "--indexType:" +lane.getIndexType()+"--libraryGroup count:" + lane.getLibraryGroupList().size()+"]");
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
      edo.setProductName(l.getProductName());
      edo.setElutionLibraryName(l.getElutionLibraryName());
      edo.setGeneplusCode(l.getGeneplusCode());
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
    Float lgSingleEndDataSize = 0f;
    // 判断数据量是否超过限制
    for (LibraryGroup lg : list) {
      lgDataSize += lg.getDataSize();
      if(lg.getUnbalance()) {
        lgUnbalanceDataSize += lg.getDataSize();
      }
      if(lg.getSingleEnd()) {
        lgSingleEndDataSize += lg.getDataSize();
      }
    }
    if(lane.getDataSize() + lgDataSize > lane.getDataSizeCeiling()) {
      return false;
    }
    if(lane.getUnbalanceDataSize() + lgUnbalanceDataSize > lane.getUnbalanceDataSizeCeiling()) {
      return false;
    }
    CommonComponent.IndexType indexType = lane.getIndexType();
    switch(indexType) {
      case P6,P8,P10 -> {
        float laneSingleEndDataSizeTmp = lgSingleEndDataSize + lane.getSingleEndDataSize();
        float laneDataSizeTmp = lgSingleEndDataSize + lane.getDataSize();
        if(laneDataSizeTmp < lane.getDataSizeFloor()) {
          laneDataSizeTmp = lane.getDataSizeFloor();
        }
        if(laneSingleEndDataSizeTmp / laneDataSizeTmp > lane.getSingleEndRatioLimit()) {
          return false;
        }
      }
    }
    for(LibraryGroup lg1: list) {
      for(LibraryGroup lg2:lane.getLibraryGroupList()) {
        if(lg2.getHammingDistantLimitCodeMap().get(indexType).contains(lg1.getCode())) {
          return false;
        }
      }
    }
    return true;
  }
  /**
   * 将一个文库组的列表加入到一个lane中
   * @param lane
   * @param list
   */
  public static void addLibraryGroupListToLane(Lane lane, List<LibraryGroup> list) {
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
   * @param sourceMap
   */
  public static void setHammingDistantLimitCodeMap(Map<String, LibraryGroup> sourceMap) {
    List<LibraryGroup> lgList = sourceMap.values().stream().sorted().collect(Collectors.toList());
    for(CommonComponent.IndexType type:CommonComponent.IndexType.values()) {
      for(int i=0;i<lgList.size();i++) {
        LibraryGroup lg1 = lgList.get(i);
        List<String> codeList = lg1.getHammingDistantLimitCodeMap().get(type);
        List<String> list1 = lg1.getLibraryList().stream().map(library -> {
          return library.getIndexSeqMap().get(type);
        }).collect(Collectors.toList());
        for(int j=0;j<lgList.size();j++) {
          if(j==i) continue;
          LibraryGroup lg2 = lgList.get(j);
          List<String> list2 = lg2.getLibraryList().stream().map(library -> {
            return library.getIndexSeqMap().get(type);
          }).collect(Collectors.toList());
          int limit = 2;
          if((type.equals(CommonComponent.IndexType.S6) ||
            type.equals(CommonComponent.IndexType.S8) ||
            type.equals(CommonComponent.IndexType.S10)) &&
            lg1.getHammingDistantF()) {
            limit = 1;
          }
          if(getSmallestHammingDistant(list1, list2) < limit) {
            codeList.add(lg2.getCode());
          }
        }
      }
    }
  }
  /**
   * 为文库组设置汉明距离不符合的文库组吉因加编号列表
   * @param sourceMap 原始的文库组map
   */
//  public static void setHDUnqualifiedGeneplusCodeList(Map<String, LibraryGroup> sourceMap) {
//    // 将map转换为list
//    List<LibraryGroup> libraryGroupList = new ArrayList<>(sourceMap.values());
//    // 将list内容按数据量排序
//    libraryGroupList = libraryGroupList.stream()
//      .sorted()
//      .collect(Collectors.toList());
//    for(int i=0;i<libraryGroupList.size();i++) {
//      LibraryGroup lg1 = libraryGroupList.get(i);
//      List<String> codeList = new ArrayList<>();
//      lg1.setHammingDistantUnqualifiedGeneplusCodeList(codeList);
//      List<String> list1 = lg1.getLibraryList().stream().map(Library::getIndexSeq).collect(Collectors.toList());
//      for(int j=0;j<libraryGroupList.size();j++) {
//        if(j == i) continue;
//        LibraryGroup lg2 = libraryGroupList.get(j);
//        List<String> list2 = lg2.getLibraryList().stream().map(Library::getIndexSeq).collect(Collectors.toList());
//        if(getSmallestHammingDistant(list1, list2) < 2) {
//          codeList.add(lg2.getCode());
//        }
//      }
//    }
//  }

  /**
   * 根据lane的数量和文库组间的汉明距离不符合的情况，将无法排上的文库组中按优先级（加急，不平衡，数据量；倒序）放到未排单map中
   * 核心：汉明距离冲突的相关文库组的编号列表的交集，应小于lane的数量，否则就需要丢到优先级最低的一个，然后递归，直至都可以排入
   * @param laneCount lane的梳理
   * @param sourceMap 文库组map
   * @param unscheduledMap 未排单文库组map
   */
//  public static void moveToUnscheduledMapAccordingHammingDistance(int laneCount, Map<String, LibraryGroup> sourceMap, Map<String, LibraryGroup> unscheduledMap) {
//    // 将map转换为list
//    List<LibraryGroup> libraryGroupList = new ArrayList<>(sourceMap.values());
//    // 将list内容排序，按优先级倒序，这样最先处理的就是最可能丢到未排单map的
//    libraryGroupList = libraryGroupList.stream()
//      .sorted(Comparator.reverseOrder())
//      .collect(Collectors.toList());
//    // 判断是否需要递归
//    boolean flag = false;
//    for (LibraryGroup lg : libraryGroupList) {
//      List<String> codeList = new ArrayList<>(List.copyOf(lg.getHammingDistantUnqualifiedGeneplusCodeList()));
//      if (codeList.size() < laneCount) continue;
//      List<List<String>> list = new ArrayList<>();
//      codeList.add(lg.getCode());
//      list.add(codeList);
//      for (String s : codeList) {
//        LibraryGroup lgTmp = sourceMap.get(s);
//        List<String> codeListTmp = new ArrayList<>(List.copyOf(lgTmp.getHammingDistantUnqualifiedGeneplusCodeList()));
//        codeListTmp.add(lgTmp.getCode());
//        list.add(codeListTmp);
//      }
//      // 求汉明距离不符的数据的交集，如果交集中元素个数大于lane的数量，则需要从交集中选中一个优先级最低的放到未排单列表
//      List<String> targetList = getIntersection(list);
//      if(targetList.size() <= laneCount) continue;
//      List<LibraryGroup> lgList = sourceMap.values().stream().filter(libraryGroup -> {
//        return targetList.contains(libraryGroup.getCode());
//      }).collect(Collectors.toList());
//      lgList = lgList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
//      String discardCode = lgList.get(0).getCode();
//      unscheduledMap.put(discardCode, lgList.get(0));
//      sourceMap.remove(discardCode);
//      flag = true;
//      setHDUnqualifiedGeneplusCodeList(sourceMap);
//      break;
//    }
//    if(flag) {
//      moveToUnscheduledMapAccordingHammingDistance(laneCount, sourceMap, unscheduledMap);
//    }
//  }
  /**
   * 将文库组列表放到未排单文库map中
   * @param libraryGroupList 未排单文库组列表
   * @param unscheduledMap 未排单map
   */
  public static void addLibraryGroupListToUnscheduledMap(List<LibraryGroup> libraryGroupList, Map<String, LibraryGroup> unscheduledMap) {
    for(LibraryGroup lg: libraryGroupList) {
      if(!unscheduledMap.keySet().contains(lg.getCode())) {
        unscheduledMap.put(lg.getCode(), lg);
      } else {
        LibraryGroup libraryGroup = unscheduledMap.get(lg.getCode());
        List<Library> slList = libraryGroup.getLibraryList();
        slList.addAll(lg.getLibraryList());
        libraryGroup.setLibraryList(slList);
      }
    }
  }
  public static void addLibraryGroupToUnscheduledMap(LibraryGroup libraryGroup, Map<String, LibraryGroup> unscheduledMap) {
    List<LibraryGroup> list = new ArrayList<>();
    list.add(libraryGroup);
    addLibraryGroupListToUnscheduledMap(list, unscheduledMap);
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
   * @param lists
   * @return
   */
  public static List<String> getIntersection(List<List<String>> lists) {
    if(lists == null || lists.size() == 0){
      return null;
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
   * 返回值为0时，说明已经全部试过了，都不可以
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
    if(list.size() == 0) return 0;
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
   * @param laneList lane列表
   * @param unscheduledMap 未排单文库组map
   */
  public static void putLibraryGroupInLane(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap) {
    // 将map转换为list
    List<LibraryGroup> lgList = new ArrayList<>(libraryGroupMap.values());
    // 将list内容按数据量排序
    lgList = lgList.stream().sorted().collect(Collectors.toList());
    for(LibraryGroup lg:lgList) {
      boolean inFlag = false;
      laneList = laneList.stream().sorted(Comparator.comparing(Lane::getDataSize)).collect(Collectors.toList());
      for (Lane lane : laneList) {
        // 判断一个洗脱文库列表是否可以放到某个lane中
        if (canAddLibraryGroupToLane(lane, lg)) {
          Utils.addLibraryGroupToLane(lane, lg);
          inFlag = true;
          break;
        }
      }
      if(inFlag) continue;
      // 都加不进去，尝试将同这个文库组冲突的已经在lane中的文库组移动到别的lane，然后再试一下
      for (int i = 0; i < laneList.size(); i++) {
        Lane previousLane = laneList.get(i);
        List<String> limitCodeList = lg.getHammingDistantLimitCodeMap().get(previousLane.getIndexType());
        List<String> previousLaneCodeList = previousLane.getLibraryGroupList().stream().map(lgTmp -> {
          return lgTmp.getCode();
        }).collect(Collectors.toList());
        List<List<String>> listList = new ArrayList<>();
        listList.add(limitCodeList);
        listList.add(previousLaneCodeList);
        // 待移位的文库组code列表
        List<String> targetCodeList = getIntersection(listList);
        // 待移位的文库组列表
        List<LibraryGroup> targetLibraryGroupList = targetCodeList.stream().map(code -> {
          return libraryGroupMap.get(code);
        }).collect(Collectors.toList());
        for(int j = 0;j < laneList.size(); j++) {
          if(j == i) continue;
          Lane targetLane = laneList.get(j);
          if(canAddLibraryGroupListToLane(targetLane, targetLibraryGroupList) &&
            canAddLibraryGroupToLane(previousLane, lg)) {
            // 如果可以加入到其他的lane，就加入到其他的lane，并从之前的lane中删掉，然后把当前文库组放到之前的lane中
            addLibraryGroupListToLane(targetLane, targetLibraryGroupList);
            targetLibraryGroupList.forEach(targetLibraryGroup -> {
              previousLane.getLibraryGroupList().remove(targetLibraryGroup);
            });
            addLibraryGroupToLane(previousLane, lg);
            inFlag = true;
            break;
          }
        }
        if(inFlag) break;
      }
      // 还有未排单的洗脱文库，则放到未排单文库中
      if(!inFlag) {
        addLibraryGroupToUnscheduledMap(lg, unscheduledMap);
      }
    };
  }

  /**
   * 从旧的indexTypeList获取新的indexTypeList
   * @param indexTypeList 旧的indexTypeList
   * @param increment 增量
   */
  public static void indexTypeListPlus(List<CommonComponent.IndexType> indexTypeList, int increment) {
    for(int m=0;m<increment;m++) {
      for (int i = indexTypeList.size() - 1; i >= 0; i--) {
        CommonComponent.IndexType type = indexTypeList.get(i);
          if (type.equals(CommonComponent.IndexType.S6)) {
            indexTypeList.set(i, CommonComponent.IndexType.P8);
            continue;
          }
        indexTypeList.set(i, type.plus(1)); break;
      }
    }
  }

  /**
   * 在已经有排单列表和未排单map的情况下，填充其他数据
   * @param sr
   */
  public static void setScheduledResultInfo(CommonComponent.ScheduledResult sr) {
    StringBuilder notes = new StringBuilder();
    Boolean baseBalance = checkBaseRatio(sr.getLaneList());
    Boolean dataSizeFlag = sr.getLaneList().stream().allMatch(lane -> {
      return lane.getDataSize() > lane.getDataSizeFloor() && lane.getDataSize() <= lane.getDataSizeCeiling();
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
   * @param lane
   * @return
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
   * @return
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
        // 判断是否在待排单的文库组中，不存在，新增；存在，加入新文库
        if(libraryGroupMap.containsKey(excelData.getGeneplusCode())) {
          lg = libraryGroupMap.get(excelData.getGeneplusCode());
        } else {
          String productName = excelData.getProductName();
          String geneplusCode = excelData.getGeneplusCode();
          // 判断是否需要新建一个文库组
          boolean needNew = true;
          if(excelData.getNotes() != null && excelData.getNotes().contains("同lane上机")) {
            // 如果是同lane上机，并且之前已经有符合条件的同lane上机的文库组，那就直接使用之前的那个文库组
            for(LibraryGroup libraryGroup:libraryGroupMap.values()) {
              if(libraryGroup.getProductName().equals(productName)
                && libraryGroup.getCode().equals(geneplusCode)
                && libraryGroup.getSameLane()) {
                lg = libraryGroup;
                needNew = false;
                break;
              }
            }
          }
          if(needNew) {
            si.setLibraryGroupSize(si.getLibraryGroupSize() + 1);
            lg = new LibraryGroup();
            lg.setProductName(excelData.getProductName());
            lg.setCode(excelData.getGeneplusCode());
            if (excelData.getNotes() != null) {
              if(excelData.getNotes().contains("加急")) {
                lg.setUrgent(true);
                si.setUrgentLibraryGroupSize(si.getUrgentLibraryGroupSize() + 1);
                si.setUrgentDataSize(si.getUrgentDataSize() + itemSize);
              } else {
                lg.setUrgent(false);
              }
              lg.setUnbalance(excelData.getNotes().contains("不平衡文库"));
              lg.setHammingDistantF(excelData.getNotes().contains("F"));
              lg.setSameLane(excelData.getNotes().contains("同lane上机"));
            } else {
              lg.setUrgent(false);
              lg.setUnbalance(false);
              lg.setHammingDistantF(false);
              lg.setSameLane(false);
            }
            lg.setSingleEnd(excelData.getR()==null || excelData.getR().equals(""));
            lg.setDataSize(0f);
            lg.setLibraryList(new ArrayList<>());
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
        library.setSplitRule(Optional.ofNullable(excelData.getSplitRule()).orElse(""));
        library.setNotes(excelData.getNotes());
        lg.getLibraryList().add(library);
        lg.setDataSize(lg.getDataSize() + excelData.getDataSize());
        // 计算碱基占比
        // TODO
      }
    })).sheet().doRead();
  }

  /**
   * 将结果打印出来
   * @param fileName 文件名
   */
  public static void writeExcel(String fileName, CommonComponent.ScheduledResult scheduledResult) {
    if(!scheduledResult.getSuccess()) {
      System.out.println("排单不成功，未打印: " + fileName);
      return;
    }
    List<Lane> laneList = scheduledResult.getLaneList();
    Map<String, LibraryGroup> unscheduledMap = scheduledResult.getUnscheduledLibraryGroupMap();
    ExcelWriter excelWriter = EasyExcel.write(fileName, ExcelDataOutput.class).build();
    // 排单结果
    List<ExcelDataOutput> dataList = new ArrayList<>();
    for(Lane lane:laneList) {
      dataList.addAll(getScheduledExcelDataList(lane));
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
      unscheduledDataList.addAll(Utils.getScheduledExcelDataList(value));
    });
    WriteSheet sheet3 = EasyExcel.writerSheet("未排单文库").build();
    // 写入excel
    excelWriter.write(dataList, sheet1).write(baseRatioData,sheet2).write(unscheduledDataList,sheet3).finish();
  }

  /**
   * 打印排单结果
   * @param resultList 排单结果
   */
  public static void printResult(List<CommonComponent.ScheduledResult> resultList) {
    for(CommonComponent.ScheduledResult result: resultList) {
//      if(!result.getSuccess()) continue;
      StringBuilder sb = new StringBuilder();
      if(result.getSuccess()) {
        sb.append("success:===");
      } else {
        sb.append("failed:");
      }
      result.getLaneList().stream().forEach(lane -> {
        sb.append("\t" + "lane: " + lane.getIndexType() + ": " + lane.getDataSize());
      });
      sb.append("\tunscheduled: " + result.getUnscheduledDataSize());
      sb.append("\t" +result.getNotes());
      System.out.println(sb.toString());
    }
  }

  /**
   * 获取lane的数量
   * @return lane的数量
   */
  public static int getLaneListSize(float totalDataSize) {
    int count = (int) (totalDataSize / 1450);
    if (totalDataSize % 1450 > 1250) {
      count++;
    }
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
//      lane.setDataSizeCeiling(1400f);
//      lane.setDataSizeFloor(1300f);
      lane.setDataSizeCeiling(1450f);
      lane.setDataSizeFloor(1350f);
      laneList.add(lane);
    }
    if(indexTypeList != null) {
      for(int i=0;i<indexTypeList.size();i++) {
        laneList.get(i).setIndexType(indexTypeList.get(i));
      }
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
    Utils.setHammingDistantLimitCodeMap(libraryGroupMap);
//    List<LibraryGroup> list = new ArrayList<>(libraryGroupMap.values());
//    list = list.stream().sorted().collect(Collectors.toList());
//    list.forEach(lg -> {
//      System.out.println(lg.getNumber() + "-----" + lg.getCode());
//      lg.getHammingDistantLimitCodeMap().forEach((key, value) -> System.out.println(key + "----------" + value));
//    });
  }

  /**
   * 全遍历的方式找出合适的排单结果，避免使用递归
   * @param libraryGroupList 待排单的文库组列表
   * @param laneList lane列表
   * @param lastNumber 上一个排入lane中的文库组序号
   * @return 返回上一个排入lane中的文库组序号
   */
  public static int traversalMemory(List<LibraryGroup> libraryGroupList, List<Lane> laneList, int lastNumber) {
    int number = lastNumber;
    // 碱基不平衡的情况下，即使全部排完了，还是需要再排，将排进去的移出来换lane，再排
    if(number == libraryGroupList.size()) {
      number = resetLane(laneList, number);
    }
    for(int i=number;i<libraryGroupList.size();i++) {
      LibraryGroup lg = libraryGroupList.get(i);
      boolean added = false;
      for (Lane lane : laneList) {
        // 判断一个洗脱文库列表是否可以放到某个lane中
        if (Utils.canAddLibraryGroupToLane(lane, lg)) {
          Utils.addLibraryGroupToLane(lane, lg);
          added = true;
          number = lg.getNumber();
          break;
        }
      }
      if (added) continue;
      // 如果文库组无法加入到lane中，说明之前的排单是有问题的，要调整
      number = resetLane(laneList, number);
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
   * 全遍历方式找出合适的排单结果
   * @param libraryGroupList 待排单的文库组列表
   * @param laneList lane列表
   * @param lastNumber 上一个排入lane中的文库组序号
   * @return 返回是否成功
   */
  public static Boolean traversal(List<LibraryGroup> libraryGroupList, List<Lane> laneList, int lastNumber) {
    // 最后一个加入lane的libraryGroup的序号
    int number = lastNumber;
    // 全部排完了，可以从碱基平衡的角度考虑了
    if(libraryGroupList.get(libraryGroupList.size()-1).getNumber() == lastNumber) {
      // 考虑碱基平衡 todo
      // 考虑数据量 todo
//      for(int i=0;i< laneList.size();i++) {
//        Lane lane = laneList.get(i);
//        System.out.println("************************ success!!!! *****************************" + lane.getDataSize());
//        if(lane.getDataSize() > lane.getDataSizeCeiling() || lane.getDataSize() < lane.getDataSizeFloor()) {
//          number = resetLane(laneList, lastNumber);
//          return traversal(libraryGroupList, laneList, number);
//        }
//      }
      return true;
    }
    // 最后一个排进去的是第一个，说明已经调整到最后都不可行了
    if(lastNumber == 1) {
      return false;
    }
    for(int i=lastNumber;i<libraryGroupList.size();i++) {
      boolean added = false;
      LibraryGroup libraryGroup = libraryGroupList.get(i);
      for (Lane lane : laneList) {
        if (Utils.canAddLibraryGroupToLane(lane, libraryGroup)) {
          Utils.addLibraryGroupToLane(lane, libraryGroup);
          number = libraryGroupList.get(i).getNumber();
          added = true;
          break;
        }
      }
      // 如果文库组无法加入到lane中，说明之前的排单是有问题的，要调整
      if(!added) {
        number = resetLane(laneList, number);
        break;
      }
    }
    return traversal(libraryGroupList, laneList, number);
  }
  /**
   * 调整lane列表
   * @param laneList lane列表
   * @param number 待移位的文库组number
   * @return 返回需要重新排的第一个文库组的number
   */
  public static Integer resetLane(List<Lane> laneList, int number) {
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
//    System.out.println("***************************************************** reset，lastNumber:" + number);
//    laneList.forEach(lane-> {
//      lane.getLibraryGroupList().forEach(libraryGroup1 -> {
//        System.out.print(libraryGroup1.getNumber()+";");
//      });
//      System.out.println("");
//    });
//    System.out.println("待移位文库组，lane: " + laneIndex + "--编号" + lg.getNumber());
//    System.out.println("*****************************************************");
    // 将待移位的libraryGroup移出之前所在的lane
    Utils.removeLibraryGroupFromLane(laneList.get(laneIndex), lg);
    // 如果待移位的libraryGroup不在最后一个lane，那就放到下一个lane中试试
    if(laneIndex < laneList.size() -1) {
      boolean flag = false;
      for(int i =laneIndex + 1;i<laneList.size();i++) {
        flag = Utils.canAddLibraryGroupToLane(laneList.get(i), lg);
        if(flag) {
          Utils.addLibraryGroupToLane(laneList.get(i), lg);
          return lg.getNumber();
        }
      }
    }
    // 如果所有的lane都放不进去，就只能调整上一个libraryGroup了
    // 如果本身这个libraryGroup就是在最后一个lane，就只能调整上一个libraryGroup了
    number = Utils.getLastNumber(laneList);
    return resetLane(laneList, number);
  }
}
