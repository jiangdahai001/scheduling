package com.example.scheduling;

import com.example.scheduling.util.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.scheduling.solution.TaskRunner.multiBrute;

public class Main {
  // 排单结果列表
  public static List<CommonComponent.ScheduledResult> resultList = new ArrayList<>();
  // traversal count
  public static Integer traversalCount = 0;
  public static Integer solutionCount = 0;
  /**
   * 主函数
   * @param args
   */
  public static void main(String[] args) {
//    greed(args);
//    greedSolution();
//    brute(args);
//    bruteSolution();
    bruteSolutionMulti();
  }
  public static void greedSolution() {
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州3.22.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    float totalDataSize = Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    List<CommonComponent.IndexType> indexTypeList = null;
    int laneListSize = Utils.getLaneListSize(totalDataSize);
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    while (true) {
      indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
      solutionCount++;
      System.out.println("solution count: " + solutionCount + "--" + indexTypeList);
      Utils.putLibraryGroupInLane(libraryGroupMap, laneList, unscheduledMap);
      CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
      sr.setLaneList(laneList);
      sr.setUnscheduledLibraryGroupMap(unscheduledMap);
      Utils.setScheduledResultInfo(sr);
      resultList.add(sr);
      boolean finished = indexTypeList.stream().allMatch(indexType -> {
        return indexType.equals(CommonComponent.IndexType.P10);
      });
      if(finished) break;
      Utils.indexTypeListPlus(indexTypeList, 1);
      laneList = Utils.initLaneList(laneListSize, indexTypeList);
      unscheduledMap = new HashMap<>();
    }
    Utils.printResult(resultList);
  }
  public static void greed(String[] args) {
    System.out.println("hello greed");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    float totalDataSize = Utils.readExcel(inFileName, libraryGroupMap);
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(totalDataSize);
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    laneList.get(0).setIndexType(CommonComponent.IndexType.P8);
    laneList.get(1).setIndexType(CommonComponent.IndexType.P8);
    Utils.putLibraryGroupInLane(libraryGroupMap, laneList, unscheduledMap);
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    sr.setLaneList(laneList);
    sr.setUnscheduledLibraryGroupMap(unscheduledMap);
    Utils.setScheduledResultInfo(sr);
    resultList.add(sr);
//    resultList.forEach(result -> {
//      String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "greed.xlsx";
//      System.out.println(fileName + "====" + result.getSuccess() + "====" + result.getNotes());
//      writeExcel(fileName, result);
//    });
    resultList.forEach(result ->{
      StringBuilder sb = new StringBuilder();
      if(result.getSuccess()) {
        sb.append("success:================");
      } else {
        sb.append("failed:");
      }
      result.getLaneList().stream().forEach(lane -> {
        sb.append("\t" + "lane:::" + lane.getIndexType() + ":" + lane.getDataSize());
      });
      sb.append("\tunscheduled: " + result.getUnscheduledDataSize());
      sb.append("\t" +result.getNotes());
      System.out.println(sb.toString());
    });
  }
  public static void bruteSolutionMulti() {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州3.22.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    float totalDataSize = Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(totalDataSize);
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    resultList = multiBrute(libraryGroupMap, laneList, false);
    Utils.printResult(resultList);
//    resultList.forEach(result -> {
//      if(result.getSuccess()) {
//        String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "-multi.xlsx";
//        System.out.println(fileName + "====" + result.getSuccess() + "====" + result.getNotes());
//        Utils.writeExcel(fileName, result);
//      }
//    });
  }
  public static void bruteSolution() {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州3.22.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    float totalDataSize = Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    int laneListSize = Utils.getLaneListSize(totalDataSize);
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    laneList.get(0).setIndexType(CommonComponent.IndexType.P8);
    laneList.get(1).setIndexType(CommonComponent.IndexType.P8);
    // 将map转换为list
    List<LibraryGroup> libraryGroupList = new ArrayList<>(libraryGroupMap.values());
    // 将list内容按数据量排序
    libraryGroupList = libraryGroupList.stream().sorted().collect(Collectors.toList());
    // 最后一个放到lane中的文库组的编号，初始是0，说明还没有放
    int lastNumber = 0;
    while (true) {
      solutionCount++;
      System.out.println("solution count: " + solutionCount);
      lastNumber = Utils.traversalMemory(libraryGroupList, laneList, lastNumber);
      System.out.println("***************************************************** 移位完成后：");
      laneList.forEach(lane-> {
        lane.getLibraryGroupList().forEach(libraryGroup1 -> {
          System.out.print(libraryGroup1.getNumber()+";");
        });
        System.out.println("");
      });
      System.out.println("*****************************************************");
      // 全部文库组都排到lane中了
      if(lastNumber==libraryGroupList.size()) {
        CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
        sr.setLaneList(laneList);
        sr.setUnscheduledLibraryGroupMap(unscheduledMap);
        Utils.setScheduledResultInfo(sr);
        if(!sr.getSuccess()) continue;
        System.out.println("success ======");
        resultList.add(sr);
        break;
      }
      // 全部遍历了，无法排出来
      // lane中数据为空，说明全部都试了，无法排出来
      if(lastNumber == 0) {
        System.out.println("failed ======");
        break;
      }
    }
    Utils.printResult(resultList);
  }
  public static void brute(String[] args) {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    float totalDataSize = Utils.readExcel(inFileName, libraryGroupMap);
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(totalDataSize);
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    laneList.get(0).setIndexType(CommonComponent.IndexType.P8);
    laneList.get(1).setIndexType(CommonComponent.IndexType.P8);
    // 将map转换为list
    List<LibraryGroup> libraryGroupList = new ArrayList<>(libraryGroupMap.values());
    // 将list内容按数据量排序
    libraryGroupList = libraryGroupList.stream().sorted().collect(Collectors.toList());
    Utils.traversal(libraryGroupList,laneList,0);
    sr.setLaneList(laneList);
    sr.setUnscheduledLibraryGroupMap(unscheduledMap);
    sr.setLaneList(laneList);
    Utils.setScheduledResultInfo(sr);
    resultList.add(sr);
    resultList.forEach(result -> {
      String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() +"-" + ".xlsx";
      System.out.println(fileName);
      Utils.writeExcel(fileName, result);
    });
    System.out.println("traversal count:" + traversalCount);
  }

}
