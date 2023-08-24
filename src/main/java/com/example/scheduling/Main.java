package com.example.scheduling;

import com.example.scheduling.solution.TaskRunner;
import com.example.scheduling.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
  /**
   * 主函数
   * @param args 命令行参数
   */
  public static void main(String[] args) throws IOException {
//    loop();
//    backtraceOnce();
//    greedySolution();
    checkScheduledResult();
  }

  /**
   * 校验排单是否符合要求
   */
  public static void checkScheduledResult() {
    String fileName = "C:\\Users\\admin\\Desktop\\scheduling\\beina-20230816.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    Utils.readExcel(fileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    List<Lane> laneList = Utils.putLibraryGroupInLane(libraryGroupMap);
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    sr.setLaneList(laneList);
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    sr.setUnscheduledLibraryGroupMap(unscheduledMap);
    Utils.setScheduledResultInfo(sr);
    if(sr.getSuccess()) {
      System.out.println("scheduling success");
    } else {
      System.out.println("scheduling fail" + sr.getNotes());
    }
    String outFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "-check.xlsx";
    Utils.writeExcel(outFileName, sr, false);
    System.out.println(outFileName + "====" + sr.getSuccess() + "====" + sr.getNotes());
  }
  public static void loop() throws IOException{
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String in;
    while(true){
      CommonComponent.SchedulingInfo.initInstance();
      System.out.println("请选择操作: 排单: 1; 退出: 2;");
      in = br.readLine();
      switch (in) {
        case "1", "" -> {
//          System.out.println("请输入Lane的理想数据量(默认1400): ");
//          in = br.readLine();
//          if(!in.equals("")) {
//            float idealLaneDataSize = Float.parseFloat(in);
//            CommonComponent.SchedulingInfo.getInstance().setIdealLaneDataSize(idealLaneDataSize);
//            CommonComponent.SchedulingInfo.getInstance().setLaneDataSizeCeiling(idealLaneDataSize + 50);
//            CommonComponent.SchedulingInfo.getInstance().setLaneDataSizeFloor(idealLaneDataSize - 50);
//          }
          System.out.println("请输入Lane的最大数据量默认1450: ");
          in = br.readLine();
          if(!in.equals("")) {
            CommonComponent.SchedulingInfo.getInstance().setLaneDataSizeCeiling(Float.parseFloat(in));
          }
          System.out.println("请输入Lane的最小数据量默认1350: ");
          in = br.readLine();
          if(!in.equals("")) {
            CommonComponent.SchedulingInfo.getInstance().setLaneDataSizeFloor(Float.parseFloat(in));
          }
          greedySolution();
//          backtraceOnce();
        }
        case("2") -> {
          System.exit(1);
        }
        default -> {}
      }
    }
  }
  public static void greedySolution() {
    List<CommonComponent.ScheduledResult> resultList = new ArrayList<>();
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\216.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\20230525.xlsx";
    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\20230816.xlsx";
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
    int solutionCount = 0;
    while (true) {
      indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
      solutionCount++;
      System.out.println("solution count: " + solutionCount + "--" + indexTypeList);

      // 获取当前lane列表的情况下，文库组受汉明距离限制而不能排到一个lane中的信息
//      Utils.setDynamicHammingDistantLimitCodeMap(libraryGroupMap, laneList, unscheduledMap);
      // 根据上面的限制，将肯定不能排入lane列表的文库组移到未排单map
//      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList, libraryGroupMap, unscheduledMap);
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
      System.out.println("solution count: " + solutionCount + "--" + indexTypeList + "--" + sr.getNotes());
      if(sr.getSuccess() || sr.getNotes().equals("数据量不符合;")) {
        resultList.add(sr);
        if(resultList.size()>=3) {
          System.out.println("scheduling success break");
          break;
        }
      }
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
  public static void backtraceOnce() {
    System.out.println("hello backtrace");
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\216.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州2.16.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州1.11.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\20230525.xlsx";
    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\20230816.xlsx";
//    String inFileName = "C:\\Users\\admin\\Desktop\\scheduling\\input表苏州3.22.xlsx";
    Map<String, LibraryGroup> libraryGroupMap = new HashMap<>();
    Utils.readExcel(inFileName, libraryGroupMap);
    Utils.preprocess(libraryGroupMap);
    int laneListSize = Utils.getLaneListSize(CommonComponent.SchedulingInfo.getInstance().getDataSize());
    List<Lane> laneList = Utils.initLaneList(laneListSize, null);
    List<CommonComponent.ScheduledResult> resultList = TaskRunner.backtrace(libraryGroupMap, laneList, 1);
    Utils.printResult(resultList, true);
    resultList.forEach(result -> {
      if(result.getSuccess()) {
        String fileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "-" +Utils.getCurrentTime() + "-backtrace.xlsx";
        System.out.println(fileName + "====" + result.getSuccess() + "====" + result.getNotes());
        Utils.writeExcel(fileName, result, true);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
