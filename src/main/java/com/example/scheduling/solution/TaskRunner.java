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
  // 线程超时时间值
  private static final int TIMEOUT = 60;
  // 线程超时单位
  private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

  /**
   * 使用多线程，回溯的方式获取排单列表
   * @param libraryGroupMap 待排单文库组
   * @param laneList lane列表
   * @param expectedCount 找到多少种可能，就停止排单
   * @return 排单结果列表
   */
  public static List<CommonComponent.ScheduledResult> backtrace(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, int expectedCount){
    List<CommonComponent.ScheduledResult> list = CommonComponent.SchedulingInfo.getInstance().getResultList();
    // 获取线程池
    ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);
    List<CommonComponent.IndexType> indexTypeList;
//    indexTypeList = new ArrayList<>();
//    indexTypeList.add(CommonComponent.IndexType.P8);
//    indexTypeList.add(CommonComponent.IndexType.P8);
//    indexTypeList.add(CommonComponent.IndexType.S8);
//    laneList = Utils.initLaneList(laneList.size(), indexTypeList);
    // 初始化countdownlatch，count是期望找到的可能性个数
    CountDownLatch countDownLatch = new CountDownLatch(expectedCount);
    // 根据indextype生成任务列表
    List<Task> tasks = new ArrayList<>();
    tasks.add(new Task(libraryGroupMap, laneList, countDownLatch));
    while(true) {
      indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
      boolean finished = indexTypeList.stream().allMatch(CommonComponent.IndexType::isLast);
      if(finished) break;
      Utils.indexTypeListPlus(indexTypeList, 1);
      laneList = Utils.initLaneList(laneList.size(), indexTypeList);
      tasks.add(new Task(libraryGroupMap, laneList, countDownLatch));
    }

    // 将任务列表提交给线程池，线程池开始调度运行任务
    List<Future<?>> futures = new ArrayList<>();
    for(Task task: tasks) {
      Future<?> future = threadPool.submit(task);
      futures.add(future);
    }
    // countdownlatch开始阻塞，等待任务完成或超时
    try {
      boolean finished = countDownLatch.await(TIMEOUT, TIME_UNIT);
      if(finished) {
        System.out.println("task finished in time");
      } else {
        System.out.println("task cancel caused by timeout");
      }
    } catch (InterruptedException e) {
      System.out.println("task interrupted");
    } finally {
      for(Task task:tasks) {
        task.cancel();
      }
      // shutdown thread pool
      threadPool.shutdownNow();
      System.out.println("shutdownNow");
    }
    return list;
  }
}