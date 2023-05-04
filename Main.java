package com.example.scheduling;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.scheduling.util.*;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
  // 总数据量
  public static Float totalDataSize = 0f;
  // 排单结果列表
  public static List<CommonComponent.ScheduledResult> resultList = new ArrayList<>();
  // traversal count
  public static Integer traversalCount = 0;
  public static Integer traversalConflictCount = 0;
  /**
   * 主函数
   * @param args
   */
  public static void main(String[] args) {
    greed(args);
  }
  public static void brute123(String[] args) {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
    List<CommonComponent.IndexType> indexTypes = new ArrayList<CommonComponent.IndexType>();
    indexTypes.add(CommonComponent.IndexType.P8);
    for(CommonComponent.IndexType indexType: indexTypes) {
      totalDataSize = 0f;
      Map<String, LibraryGroup> libraryGroupMap = readExcel(inFileName);
      System.out.println("*********************************************************************************************begin: " + indexType);
      CommonComponent.ScheduledResult sr = null;
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
      preprocess(libraryGroupMap, unscheduledMap, indexType);
      List<Lane> laneList = getLaneList();
      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList.size(), libraryGroupMap, unscheduledMap);
      // 将map转换为list
      Collection<LibraryGroup> values = libraryGroupMap.values();
      List<LibraryGroup> libraryGroupList = new ArrayList<>(values);
      // 将list内容按数据量排序
      libraryGroupList = libraryGroupList.stream()
        .sorted()
        .collect(Collectors.toList());
      libraryGroupList.forEach(lg -> {
        System.out.println(lg.getNumber() + "\t" + lg.getGeneplusCode() + "\t" + lg.getDataSize()
          + "--" + lg.getUrgent() + "--" + lg.getUnbalance()
          + "\t" + lg.getHammingDistantUnqualifiedGeneplusCodeList());
      });
      System.out.println("unscheduled:");
      List<LibraryGroup> unlibraryGroupList = new ArrayList<>(unscheduledMap.values());
      // 将list内容按数据量排序
      unlibraryGroupList = unlibraryGroupList.stream()
        .sorted()
        .collect(Collectors.toList());
      unlibraryGroupList.forEach(lg -> {
        System.out.println(lg.getNumber() + "\t" + lg.getGeneplusCode() + "\t" + lg.getDataSize()
          + "--" + lg.getUrgent() + "--" + lg.getUnbalance()
          + "\t" + lg.getHammingDistantUnqualifiedGeneplusCodeList());
      });
    }
  }
  public static void greed(String[] args) {
    System.out.println("hello smart");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
//    Map<String, LibraryGroup> libraryGroupMap = readExcel(inFileName);
    // 遍历可能的index类型，获取排单列表
//    for(CommonComponent.IndexType indexType: CommonComponent.IndexType.values()) {
    List<CommonComponent.IndexType> indexTypes = new ArrayList<CommonComponent.IndexType>();
//    indexTypes.add(CommonComponent.IndexType.S6);
//    indexTypes.add(CommonComponent.IndexType.P6);
    indexTypes.add(CommonComponent.IndexType.P8);
//    indexTypes.add(CommonComponent.IndexType.P10);
    for(CommonComponent.IndexType indexType: indexTypes) {
      totalDataSize = 0f;
      Map<String, LibraryGroup> libraryGroupMap = readExcel(inFileName);
      System.out.println("*********************************************************************************************begin: " + indexType);
      CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
      preprocess(libraryGroupMap, unscheduledMap, indexType);
      List<Lane> laneList = getLaneList();
      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList.size(), libraryGroupMap, unscheduledMap);
      setLaneData(libraryGroupMap, laneList, unscheduledMap);
      sr.setIndexType(indexType);
      sr.setUnscheduledLibraryGroupMap(unscheduledMap);
      sr.setLaneList(laneList);
      setScheduledResultInfo(sr);
      resultList.add(sr);
    }
    resultList.forEach(result -> {
      String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() +"-" + result.getIndexType() + ".xlsx";
      System.out.println(fileName);
      writeExcel(fileName, result);
    });
  }

  public static void brute(String[] args) {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    Map<String, LibraryGroup> libraryGroupMap = readExcel(inFileName);
    // 遍历可能的index类型，获取排单列表
//    for(CommonComponent.IndexType indexType: CommonComponent.IndexType.values()) {
    List<CommonComponent.IndexType> indexTypes = new ArrayList<CommonComponent.IndexType>();
//    indexTypes.add(CommonComponent.IndexType.P6);
    indexTypes.add(CommonComponent.IndexType.P8);
//    indexTypes.add(CommonComponent.IndexType.P10);
    for(CommonComponent.IndexType indexType: indexTypes) {
      totalDataSize = 0f;
      Map<String, LibraryGroup> libraryGroupMap = readExcel(inFileName);
      System.out.println("*********************************************************************************************begin: " + indexType);
//      Map<String, LibraryGroup> lgm = (HashMap<String, LibraryGroup>)libraryGroupMap.clone();
      CommonComponent.ScheduledResult sr = null;
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
      preprocess(libraryGroupMap, unscheduledMap, indexType);
      List<Lane> laneList = getLaneList();
      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList.size(), libraryGroupMap, unscheduledMap);
      // 将map转换为list
      Collection<LibraryGroup> values = libraryGroupMap.values();
      List<LibraryGroup> libraryGroupList = new ArrayList<>(values);
      // 将list内容按数据量排序
      libraryGroupList = libraryGroupList.stream().sorted().collect(Collectors.toList());
//      libraryGroupList = libraryGroupList.stream().filter(lg -> {
//        return lg.getHammingDistantUnqualifiedGeneplusCodeList().size()>0;
//      }).collect(Collectors.toList());
      libraryGroupList.forEach(lg -> {
        System.out.println(lg.getGeneplusCode() + "\t" + lg.getNumber() + "\t" + lg.getHammingDistantUnqualifiedGeneplusCodeList());
      });
      sr = traversal(libraryGroupList,laneList,0);
      sr.setIndexType(indexType);
      sr.setUnscheduledLibraryGroupMap(unscheduledMap);
      sr.setLaneList(laneList);
      setScheduledResultInfo(sr);
      resultList.add(sr);
    }
    resultList.forEach(result -> {
      String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() +"-" + result.getIndexType() + ".xlsx";
      System.out.println(fileName);
      writeExcel(fileName, result);
    });
    System.out.println("traversal count:" + traversalCount);
  }
  /**
   * 全遍历方式找出合适的排单结果
   * @param libraryGroupList 待排单的文库组列表
   * @param laneList lane列表
   * @param lastNumber 上一个排入lane中的文库组序号
   * @return
   */
  public static CommonComponent.ScheduledResult traversal(List<LibraryGroup> libraryGroupList, List<Lane> laneList, int lastNumber) {
    traversalCount++;
    System.out.println("traversal times: " + traversalCount);
//    System.out.println("***************************************************** traversal, lastNumber:" + lastNumber);
//    laneList.forEach(lane-> {
//      lane.getLibraryGroupList().forEach(libraryGroup1 -> {
//        System.out.print(libraryGroup1.getNumber()+",");
//      });
//      System.out.println("");
//    });
//    System.out.println("*****************************************************");
    if(traversalCount >= 1000000) {
      System.out.println("***********已经达到遍历阈值，无结果！！！************");
      System.exit(0);
    }
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
      CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
      sr.setLaneList(laneList);
      return sr;
    }
    // 最后一个排进去的是第一个，说明已经调整到最后都不可行了
    if(lastNumber == 1) {
      CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
      return sr;
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
//    System.out.println("***************************************************** reset，number:" + number);
//    laneList.forEach(lane-> {
//      lane.getLibraryGroupList().forEach(libraryGroup1 -> {
//        System.out.print(libraryGroup1.getNumber()+",");
//      });
//      System.out.println("");
//    });
//    System.out.println("*****************************************************");
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
      if(laneIndex >= 0) {
        break;
      }
    }
    System.out.println("find---" + laneIndex + "====" + lg.getNumber());
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

  /**
   * 将结果打印出来
   * @param fileName 文件名
   */
  public static void writeExcel(String fileName, CommonComponent.ScheduledResult scheduledResult) {
    List<Lane> laneList = scheduledResult.getLaneList();
    Map<String, LibraryGroup> unscheduledMap = scheduledResult.getUnscheduledLibraryGroupMap();
    ExcelWriter excelWriter = EasyExcel.write(fileName, ExcelDataOutput.class).build();
    // 排单结果
    List<ExcelDataOutput> dataList = new ArrayList<>();
    for(Lane lane:laneList) {
      dataList.addAll(Utils.getScheduledExcelDataList(lane));
    }
    WriteSheet sheet1 = EasyExcel.writerSheet("排单结果").build();
    // 碱基比率
    List<List<Object>> baseRatioData = new ArrayList<>();
    for(Lane lane: laneList) {
      JSONObject obj = getBaseRatio(lane);
      baseRatioData.addAll(Utils.getBaseRatioExcelDataList(obj));
      baseRatioData.add(new ArrayList<>());
    }
    WriteSheet sheet2 = EasyExcel.writerSheet("碱基比率")
      .head(Utils.getBaseRatioExcelHead(scheduledResult.getIndexType())).build();
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
   * 打印排单的结果数据
   */
  public static void printLaneInfo(List<Lane> laneList, Map<String, LibraryGroup> unscheduledMap) {
    for(int m=0;m<laneList.size();m++) {
      System.out.println("lane:" + m + "---" + laneList.get(m).getDataSize());
      List<LibraryGroup> lgList = laneList.get(m).getLibraryGroupList();
      for (LibraryGroup lg : lgList) {
        String urgent = lg.getUrgent()?"加急":"非加急";
        String unbalance = lg.getUnbalance()?"不平衡":"非不平衡";
        System.out.println(lg.getProductName() + "\t" + lg.getGeneplusCode() + "\t" + lg.getDataSize() + "\t" + urgent + "\t" + unbalance);
      }
    }
    System.out.println("未排单文库");
    unscheduledMap.forEach((geneplusCode, libraryGroup) -> {
      String urgent = libraryGroup.getUrgent()?"加急":"非加急";
      String unbalance = libraryGroup.getUnbalance()?"不平衡":"非不平衡";
      System.out.println(libraryGroup.getProductName() + "\t" + libraryGroup.getGeneplusCode() + "\t" + libraryGroup.getDataSize()+ "\t" + urgent + "\t" + unbalance);
    });
  }
  /**
   * 往laneList中加入数据
   * @param libraryGroupMap
   */
  public static void setLaneData(Map<String, LibraryGroup> libraryGroupMap, List<Lane> lanes, Map<String, LibraryGroup> unscheduledMap) {
    // 将map转换为list
    Collection<LibraryGroup> values = libraryGroupMap.values();
    List<LibraryGroup> libraryGroupList = new ArrayList<>(values);
    // 将list内容按数据量排序
    libraryGroupList = libraryGroupList.stream()
//      .sorted(Utils::compareLibraryGroup)
      .sorted()
      .collect(Collectors.toList());
//    System.out.println("************************************************************************************************************************************");
//    for(LibraryGroup lg:libraryGroupList) {
//      System.out.println(lg.getProductName() + "\t" + lg.getGeneplusCode() + "\t" + lg.getDataSize() + "\t" + lg.getUrgent() + "\t" + lg.getUnbalance());
//    }
//    System.out.println("************************************************************************************************************************************");
    // 用于保存一次性放入lane中的文库组列表
    ArrayList<LibraryGroup> lgList = new ArrayList<>();
    List<Lane> laneList = lanes;
    for(LibraryGroup lg:libraryGroupList) {
      lgList.add(lg);
      laneList = laneList.stream()
        .sorted(Comparator.comparing(Lane::getDataSize))
        .collect(Collectors.toList());
      for (Lane lane : laneList) {
        // 判断一个洗脱文库列表是否可以放到某个lane中
        if (Utils.canAddLibraryGroupListToLane(lane, lgList)) {
          Utils.addLibraryGroupListToLane(lane, lgList);
          lgList.clear();
          break;
        }
      }
      // 还有未排单的洗脱文库，则放到未排单文库中
      if(lgList.size()>0) {
        addLibraryGroupListToUnscheduledMap(lgList, unscheduledMap);
        lgList.clear();
      }
    };
  }
  /**
   * 将文库组列表放到未排单文库map中
   * @param libraryGroupList 未排单文库组列表
   * @param unscheduledMap 未排单map
   */
  public static void addLibraryGroupListToUnscheduledMap(List<LibraryGroup> libraryGroupList, Map<String, LibraryGroup> unscheduledMap) {
    for(LibraryGroup lg: libraryGroupList) {
      if(!unscheduledMap.keySet().contains(lg.getGeneplusCode())) {
        unscheduledMap.put(lg.getGeneplusCode(), lg);
      } else {
        LibraryGroup libraryGroup = unscheduledMap.get(lg.getGeneplusCode());
        List<Library> slList = libraryGroup.getLibraryList();
        slList.addAll(lg.getLibraryList());
        libraryGroup.setLibraryList(slList);
      }
    }
  }

  /**
   * 根据总数据量，规划lane的数量
   * 多种类型的lane？？
   * @return
   */
  public static List<Lane> getLaneList() {
    List<Lane> laneList = new ArrayList<>();
    int count = (int) (totalDataSize / 1400);
    if (totalDataSize % 1400 > 0) {
      count++;
    }
    for(int i=0;i<count;i++) {
      Lane lane = new Lane();
      lane.setDataSizeCeiling(1400f);
      lane.setDataSizeFloor(1300f);
      laneList.add(lane);
    }
    System.out.println("====================================================");
    System.out.println("laneArrayList-Size: "+laneList.size());
    System.out.println("====================================================");
    return laneList;
  }
  /**
   * 对读取的数据进行预处理
   * 为文库增加修饰后的index序列
   * 判断文库组内是否有汉明距离不符合条件的
   * 为文库组按排序规则添加序号
   * 为文库组添加汉明距离不符合文库组列表
   * @param libraryGroupMap 文库组map
   * @param unscheduledMap 未排单文库组map
   * @param type index类型
   * @return
   */
  public static Map<String, LibraryGroup> preprocess(Map<String, LibraryGroup> libraryGroupMap, Map<String, LibraryGroup> unscheduledMap, CommonComponent.IndexType type) {
    // 为文库增加修饰后的的index序列
    libraryGroupMap.forEach((geneplusCode, libraryGroup) -> {
      List<Library> libraryList = libraryGroup.getLibraryList();
      for(Library library: libraryList) {
        library.setIndexSeq(Utils.generateIndexSeq(type, library.getSplitRule(), library.getF(), library.getR()));
      }
    });
    // 计算汉明距离，不符合条件就把整个libraryGroup放到未排单列表
    List<String> unscheduledCode = new ArrayList<>();
    libraryGroupMap.forEach((geneplusCode, libraryGroup) -> {
      List<Library> libraryList = libraryGroup.getLibraryList();
      for(int i=0;i<libraryList.size();i++) {
        String seq1 = libraryList.get(i).getIndexSeq();
        boolean flag = false;
        for(int j=0;j<libraryList.size();j++) {
          if(j == i) continue;
          String seq2 = libraryList.get(j).getIndexSeq();
          int hammingDistance = Utils.getHammingDistant(seq1, seq2);
          if (hammingDistance < 2) {
            unscheduledMap.put(geneplusCode, libraryGroup);
            unscheduledCode.add(geneplusCode);
            flag = true;
            break;
          }
        }
        if(flag) {
          break;
        }
      }
    });
    System.out.println("inside conflict library group: ");
    unscheduledCode.forEach(System.out::println);
    unscheduledCode.forEach(libraryGroupMap::remove);
    Utils.setLibraryGroupNumber(libraryGroupMap);
    Utils.setHDUnqualifiedGeneplusCodeList(libraryGroupMap);
    return libraryGroupMap;
  }

  /**
   * 读取excel，获取基础数据
   * @param fileName 文件名
   * @return 返回文库组map
   */
  public static Map<String, LibraryGroup> readExcel(String fileName) {
    // 洗脱文库对象map，记录本次排单的所有洗脱文库数据
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    EasyExcel.read(fileName, ExcelData.class, new PageReadListener<ExcelData>(dataList -> {
      for (ExcelData excelData : dataList) {
        // 计算总数据量
        totalDataSize += excelData.getDataSize();
        // 按行处理读取的数据
        LibraryGroup lg = null;
        // 判断是否在待排单的文库组中，不存在，新增；存在，加入新文库
        if(!libraryGroupMap.keySet().contains(excelData.getGeneplusCode())) {
          lg = new LibraryGroup();
          lg.setProductName(excelData.getProductName());
          lg.setGeneplusCode(excelData.getGeneplusCode());
          lg.setElutionLibraryName(excelData.getElutionLibraryName());
          if (excelData.getNotes() != null) {
            Boolean isUrgent = excelData.getNotes().contains("加急");
            lg.setUrgent(isUrgent);
            Boolean isUnbalance = excelData.getNotes().contains("不平衡文库");
            lg.setUnbalance(isUnbalance);
            Boolean isHammingDistantF = excelData.getNotes().contains("F");
            lg.setHammingDistantF(isHammingDistantF);
          } else {
            lg.setUrgent(false);
            lg.setUnbalance(false);
            lg.setHammingDistantF(false);
          }
          lg.setDataSize(0f);
          lg.setLibraryList(new ArrayList<>());
          libraryGroupMap.put(excelData.getGeneplusCode(), lg);
        } else {
          lg = libraryGroupMap.get(excelData.getGeneplusCode());
        }
        Library library = new Library();
        library.setLibraryName(excelData.getSubLibraryName());
        library.setDataSize(excelData.getDataSize());
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
    System.out.println("====================================================");
    System.out.println("totalDataSize: "+totalDataSize);
    System.out.println("====================================================");
    return libraryGroupMap;
  }

  /**
   * 在已经有排单列表和未排单map的情况下，填充其他数据
   * @param sr
   */
  public static void setScheduledResultInfo(CommonComponent.ScheduledResult sr) {
    StringBuilder notes = new StringBuilder();
    Boolean baseBalance = checkBaseRatio(sr.getLaneList());
    Boolean dataSizeFlag = sr.getLaneList().stream().allMatch(lane -> {
      return lane.getDataSize() > 1300 && lane.getDataSize() <= 1400;
    });
    Boolean isUrgentMet = sr.getUnscheduledLibraryGroupMap().values().stream().allMatch(libraryGroup -> {
      return !libraryGroup.getUrgent();
    });
    Float unscheduledDataSize = 0f;
    for(LibraryGroup lg: sr.getUnscheduledLibraryGroupMap().values()) {
      unscheduledDataSize += lg.getDataSize();
    }
    sr.setSuccess(dataSizeFlag && baseBalance);
    sr.setUrgentMet(isUrgentMet);
    sr.setUnscheduledDataSize(unscheduledDataSize);
    if(!dataSizeFlag) {
      notes.append("数据量不符合;");
    }
    if(!baseBalance) {
      notes.append("碱基不平衡;");
    }
    sr.setNotes(notes.toString());
  }
  /**
   * 获取一个lane的碱基比率数据
   * @param lane
   * @return
   */
  public static JSONObject getBaseRatio(Lane lane) {
    List<CommonComponent.BaseRatioInfo> list = new ArrayList<>();
    for(LibraryGroup lg: lane.getLibraryGroupList()) {
      for(Library l:lg.getLibraryList()) {
        CommonComponent.BaseRatioInfo bri = new CommonComponent.BaseRatioInfo(l.getIndexSeq(), l.getDataSize());
        list.add(bri);
      }
    }
    JSONObject obj = Utils.getBaseRatio(list);
    return obj;
  }
  /**
   * 检测碱基平衡
   * @return
   */
  public static Boolean checkBaseRatio(List<Lane> laneList) {
    boolean result = false;
    for(Lane lane: laneList) {
      JSONObject obj = getBaseRatio(lane);
      System.out.println(obj);
      for(Map.Entry<String, Object> entry:obj.entrySet()) {
        String key = entry.getKey();
        JSONArray array = (JSONArray) entry.getValue();
        boolean b = false;
        switch (key) {
          case "A", "T", "C", "G" -> {
            b = Arrays.stream(array.toArray()).allMatch(item -> {
              return (Float)item >= 0.03;
            });
          }
          case "N" -> {
            b = Arrays.stream(array.toArray()).allMatch(item -> {
              return (Float)item < 0.5;
            });
          }
        }
        if(!b) {
          result = false;
          break;
        } else {
          result = true;
        }
      }
      if(!result) {
        break;
      }
    }
    return result;
  }
}
