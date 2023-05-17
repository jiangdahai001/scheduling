package com.example.scheduling.solution;

import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;
import com.example.scheduling.util.CommonComponent;
import com.example.scheduling.util.Utils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Task implements Callable<CommonComponent.ScheduledResult> {
  // 线程超时时间值
  private static final int TIMEOUT = 1;
  // 线程超时单位
  private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
  private volatile boolean isCancelled;
  private final Map<String, LibraryGroup> libraryGroupMap;
  private final List<Lane> laneList;
  private final CountDownLatch countDownLatch;

  public Task(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, CountDownLatch countDownLatch) {
    this.libraryGroupMap = libraryGroupMap;
    this.laneList = laneList;
    this.countDownLatch = countDownLatch;
  }

  /**
   * 获取排单结果的线程任务，这里主要用作新启动一个线程用于实际排单，当前线程阻塞等待结果或超时
   * @return 排单结果
   */
  @Override
  public CommonComponent.ScheduledResult call() {
    CommonComponent.ScheduledResult sr = null;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<CommonComponent.ScheduledResult> future = executor.submit(this::callBusiness);
    try {
      sr = future.get(TIMEOUT, TIME_UNIT);
      if(sr.getSuccess()) countDownLatch.countDown();
    } catch (TimeoutException e) {
//      System.out.println("task timeout");
    } catch (Exception e) {
//      System.out.println("task interrupted cause thread been interrupted");
    } finally {
      executor.shutdownNow();
    }
    return sr;
  }


  /**
   * 实际的获取排单结果的任务
   * @return 排单结果
   */
  public CommonComponent.ScheduledResult callBusiness() {
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    // 将map转换为list，并排序
    List<LibraryGroup> libraryGroupList = libraryGroupMap.values().stream().sorted().collect(Collectors.toList());
    // 根据lane列表获取indextype列表
    List<CommonComponent.IndexType> itList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
    // 最后一个放到lane中的文库组的编号，初始是0，说明还没有放
    int lastNumber = 0;

    while (!isCancelled) {
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
      // 上一个放到lane中的文库组的编号
      lastNumber = Utils.traversalMemory(libraryGroupList, laneList, unscheduledMap, lastNumber);
      // 找到在libraryGroupList中，但不在unscheduledMap中的最大的number
      int largestNumber = Utils.getLargestNumber(libraryGroupList, unscheduledMap);
      // 全部文库组都排到lane中了
      if(lastNumber==largestNumber) {
        sr.setLaneList(laneList);
        sr.setUnscheduledLibraryGroupMap(unscheduledMap);
        Utils.setScheduledResultInfo(sr);
        if(!sr.getSuccess()) continue;
        System.out.println(itList + "======== success ======");
        break;
      }
      // 全部遍历了，无法排出来
      // lane中数据为空，说明全部都试了，无法排出来
      if(lastNumber == 0) {
        System.out.println(itList + "======== failed ======");
        break;
      }
    }
    return sr;
  }
  public void cancel() {
    isCancelled = true;
  }
}
