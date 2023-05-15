package com.example.scheduling.solution;
import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;
import com.example.scheduling.util.CommonComponent;
import com.example.scheduling.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TaskRunner {
  // 线程池最大线程数
  private static final int MAX_THREADS = 10;
  // 单线程超时时间值
  private static final int TIMEOUT = 1;
  // 单线程超时单位
  private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

  /**
   * 使用多线程，全遍历的方式获取排单列表
   * @param libraryGroupMap 待排单文库组
   * @param laneList lane列表
   * @param findOne 是否只找到一个，就停止排单
   * @return 排单结果列表
   */
  public static List<CommonComponent.ScheduledResult> multiBrute(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, Boolean findOne){
    List<CommonComponent.ScheduledResult> result = new ArrayList<>();
    // 创建线程池
    ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
    List<CommonComponent.IndexType> indexTypeList = null;
    // 任务列表
    List<Task> tasks = new ArrayList<>();
    tasks.add(new Task(libraryGroupMap, laneList));
    while(true) {
      indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
      boolean finished = indexTypeList.stream().allMatch(CommonComponent.IndexType::isLast);
      if(finished) break;
      Utils.indexTypeListPlus(indexTypeList, 1);
      laneList = Utils.initLaneList(laneList.size(), indexTypeList);
      tasks.add(new Task(libraryGroupMap, laneList));
    }

    // submit tasks to thread pool
    List<Future<?>> futures = new ArrayList<Future<?>>();
    for(Task task: tasks) {
      Future<?> future = executor.submit(task);
      futures.add(future);
    }
    try {
      for(int i=0;i<tasks.size();i++) {
        Task task = tasks.get(i);
        Future<?> future = futures.get(i);
        List<CommonComponent.IndexType> itList = task.getLaneList().stream().map(Lane::getIndexType).collect(Collectors.toList());
        try {
          CommonComponent.ScheduledResult sr = (CommonComponent.ScheduledResult) future.get(TIMEOUT, TIME_UNIT);
          result.add(sr);
          if(sr.getSuccess() && findOne) {
            for(Future<?> f:futures) {
              f.cancel(true);
            }
            for(Task t:tasks) {
              t.cancel();
            }
            System.out.println(itList + "========= find one exit =========");
            break;
          }
        } catch (TimeoutException e) {
          future.cancel(true);
          task.cancel();

//          laneList.stream().forEach(lane -> {
//            StringBuilder sbLane = new StringBuilder();
//            Float size = 0f;
//            for (LibraryGroup lg : lane.getLibraryGroupList()) {
//              size += lg.getDataSize();
//              sbLane.append(lg.getNumber()).append(",");
//            }
//            System.out.println("lane data size: " + size + "--library group(" + lane.getLibraryGroupList().size() + "): " + sbLane.toString());
//          });


          System.out.println(itList + "========= timeout exit =========");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // shutdown thread pool
      System.out.println("shutdown");
      executor.shutdown();
    }
    return result;
  }
}