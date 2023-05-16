package com.example.scheduling;

import com.example.scheduling.solution.TaskRunner;
import com.example.scheduling.util.*;

import java.util.*;
import java.util.stream.Collectors;


public class Main {
  // 排单结果列表
  public static List<CommonComponent.ScheduledResult> resultList = new ArrayList<>();
  public static Integer solutionCount = 0;
  /**
   * 主函数
   * @param args
   */
  public static void main(String[] args) {
//    greedySolution();
    bruteSolutionMulti();
//    bruteSolution();
  }
  public static void greedySolution() {
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\216.xlsx";
    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州1.11.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州3.22.xlsx";
    // 新建文库组map，用于存放本次排单的数据，key为吉因加code，value为文库组对象
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    // 读取文件，并将数据存入文库组map，同时获取排单基础信息
    Utils.readExcel(inFileName, libraryGroupMap);
    // 对文库组map做预处理
    Utils.preprocess(libraryGroupMap);
    // 未排单文库组map，用于存放未排单的数据，key为吉因加code，value为文库组对象
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    // indextype列表
    List<CommonComponent.IndexType> indexTypeList = null;
    // 根据排单基础信息获取lane的数量
    int laneListSize = Utils.getLaneListSize(CommonComponent.SchedulingInfo.getInstance().getDataSize());
    // 初始化lane列表
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);

    while (true) {
      indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
      solutionCount++;
      System.out.println("solution count: " + solutionCount + "--" + indexTypeList);

      // 获取当前lane列表的情况下，文库组受汉明距离限制而不能排到一个lane中的信息
      Utils.setDynamicHammingDistantLimitCodeMap(libraryGroupMap, laneList, unscheduledMap);
      // 根据上面的限制，将肯定不能排入lane列表的文库组移到未排单map
      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList, libraryGroupMap, unscheduledMap);
//      System.out.println("unscheduledMap:");
//      Float unscheduledMapSize = 0f;
//      for(Map.Entry<String, LibraryGroup> entry: unscheduledMap.entrySet()) {
//        unscheduledMapSize += entry.getValue().getDataSize();
//        System.out.println(entry.getKey() + ": " + entry.getValue().getNumber());
//      }
//      System.out.println("unscheduledMapSize: " + unscheduledMapSize);

      // 贪心算法将文库组放到lane列表中
      Utils.putLibraryGroupInLane(libraryGroupMap, laneList, unscheduledMap);
      // 新建结果对象，并赋值
      CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
      sr.setLaneList(laneList);
      sr.setUnscheduledLibraryGroupMap(unscheduledMap);
      Utils.setScheduledResultInfo(sr);
      resultList.add(sr);
      // 判断是否遍历完了所有可能的indextype组合，如果遍历完了，就跳出循环
      boolean finished = indexTypeList.stream().allMatch(CommonComponent.IndexType::isLast);
      if(finished) break;
      // 如果没有遍历完所有indextype组合，就初始化lane列表和未排单列表，继续下一次排单
      Utils.indexTypeListPlus(indexTypeList, 1);
      laneList = Utils.initLaneList(laneListSize, indexTypeList);
      unscheduledMap = new HashMap<>();
    }
    // 将排单结果打印出来
    Utils.printResult(resultList, false);
    // 将排单结果输出到excel
    resultList.forEach(result -> {
      String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "-greedy.xlsx";
      Utils.writeExcel(fileName, result, true);
      System.out.println(fileName + "====" + result.getSuccess() + "====" + result.getNotes());
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }
  public static void bruteSolutionMulti() {
    System.out.println("hello brute");
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\216.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州2.16.xlsx";
    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州1.11.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州3.22.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(CommonComponent.SchedulingInfo.getInstance().getDataSize());
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    resultList = TaskRunner.multiBrute(libraryGroupMap, laneList, 2);
    Utils.printResult(resultList, true);
//    resultList.forEach(result -> {
//      if(result.getSuccess()) {
//        String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "-multi.xlsx";
//        System.out.println(fileName + "====" + result.getSuccess() + "====" + result.getNotes());
//        Utils.writeExcel(fileName, result, true);
//        try {
//          Thread.sleep(1000);
//        } catch (InterruptedException e) {
//          e.printStackTrace();
//        }
//      }
//    });
  }
  public static void bruteSolution() {
    System.out.println("hello brute");
    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州3.22.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\input表苏州1.11.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(CommonComponent.SchedulingInfo.getInstance().getDataSize());
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    // 将map转换为list
    List<LibraryGroup> libraryGroupList = new ArrayList<>(libraryGroupMap.values());
    // 将list内容按数据量排序
    libraryGroupList = libraryGroupList.stream().sorted().collect(Collectors.toList());
    // 最后一个放到lane中的文库组的编号，初始是0，说明还没有放
    int lastNumber = 0;
    while (true) {
      solutionCount++;
      System.out.println("solution count: " + solutionCount);
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
      lastNumber = Utils.traversalMemory(libraryGroupList, laneList, unscheduledMap, lastNumber);
//      System.out.println("***************************************************** 移位完成后：");
//      laneList.forEach(lane-> {
//        lane.getLibraryGroupList().forEach(libraryGroup1 -> {
//          System.out.print(libraryGroup1.getNumber()+";");
//        });
//        System.out.println("");
//      });
//      System.out.println("*****************************************************");
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
    Utils.printResult(resultList, true);
  }
}
