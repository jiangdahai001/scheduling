package com.example.scheduling.solution;

import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;
import com.example.scheduling.util.CommonComponent;
import com.example.scheduling.util.Utils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Task implements Runnable {
  // 线程超时时间值
  private static final int TIMEOUT = 2000;
  private volatile boolean isCancelled;
  private final Map<String, LibraryGroup> libraryGroupMap;
  private final List<Lane> laneList;
  private final List<CommonComponent.IndexType> indexTypeList;
  private final CountDownLatch countDownLatch;

  public Task(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, CountDownLatch countDownLatch) {
    this.libraryGroupMap = libraryGroupMap;
    this.laneList = laneList;
    this.countDownLatch = countDownLatch;
    this.indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
  }
  /**
   * 实际的获取排单结果的任务
   * @return 排单结果
   */
  @Override
  public void run() {
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    // 将map转换为list，并排序
    List<LibraryGroup> libraryGroupList = libraryGroupMap.values().stream().sorted().collect(Collectors.toList());
    // 最后一个放到lane中的文库组的编号，初始是0，说明还没有放
    int lastNumber = 0;
    Map<String, LibraryGroup> unscheduledMap = new HashMap<>();
    // 加上定时器，设置每个任务的运行最大时长
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        isCancelled = true;
        timer.cancel();
      }
    };
    timer.schedule(timerTask, TIMEOUT);
    while (!isCancelled) {
      // 尝试将文库组列表中的文库组放到lane或unscheduledMap，并返回上一个放入的文库组的编号
      lastNumber = Utils.traversalMemory(libraryGroupList, laneList, unscheduledMap, lastNumber);
      // 找到在libraryGroupList中的最大的number，就是排单信息对象中的文库组数量
      int largestNumber = CommonComponent.SchedulingInfo.getInstance().getLibraryGroupSize();
      // 全部文库组都排到lane或unscheduledMap中了
      if(lastNumber==largestNumber) {
        sr.setLaneList(laneList);
        sr.setUnscheduledLibraryGroupMap(unscheduledMap);
        Utils.setScheduledResultInfo(sr);
        if(!sr.getSuccess()) continue;
        System.out.println(Thread.currentThread().getName() + "====>" + indexTypeList + "======== task success ======");
        CommonComponent.SchedulingInfo.getInstance().getResultList().add(sr);
        countDownLatch.countDown();
        break;
      }
      // 全部遍历了，无法排出来
      // lane中数据为空，说明全部都试了，无法排出来
      if(lastNumber == 0) {
        throw new RuntimeException(indexTypeList + "======== task failed === exhausted ======");
      }
    }
    if(isCancelled) {
      throw new RuntimeException(indexTypeList + "======== task failed === canceled ======");
    }
  }
  public void cancel() {
    isCancelled = true;
  }
}
