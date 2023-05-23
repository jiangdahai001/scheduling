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
  private final List<CommonComponent.IndexType> indexTypeList;
  private final CountDownLatch countDownLatch;

  public Task(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList, CountDownLatch countDownLatch) {
    this.libraryGroupMap = libraryGroupMap;
    this.laneList = laneList;
    this.countDownLatch = countDownLatch;
    this.indexTypeList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
  }

  /**
   * 获取排单结果的线程任务，这里主要用作新启动一个线程用于实际排单，当前线程阻塞等待结果或超时
   * @return 排单结果
   */
//  @Override
//  public CommonComponent.ScheduledResult call() {
//    CommonComponent.ScheduledResult sr = null;
//    ExecutorService executor = Executors.newSingleThreadExecutor();
  // 线程嵌套现在存在问题：多线程嵌套情况下，内层的方法callBusiness已经结束，但是外层的future.get方法却无法获取到数据，暂时改为timer后没有发现问题
//    Future<CommonComponent.ScheduledResult> future = executor.submit(this::callBusiness);
//    try {
//      sr = future.get(TIMEOUT, TIME_UNIT);
//      System.out.println(indexTypeList+"=== call ok");
//      if(sr.getSuccess()) countDownLatch.countDown();
//      List<CommonComponent.ScheduledResult> list = new ArrayList<>();
//      list.add(sr);
//      Utils.printResult(list, true);
//    } catch (Exception e) {
////      e.printStackTrace();
////      System.out.println(indexTypeList+"=== timeout");
//    }
//    finally {
////      System.out.println(indexTypeList+"==="+sr.getSuccess());
//      executor.shutdown();
//    }
//    return sr;
//  }


  /**
   * 实际的获取排单结果的任务
   * @return 排单结果
   */
  @Override
  public CommonComponent.ScheduledResult call() {
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
    timer.schedule(timerTask, 1000);

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
        countDownLatch.countDown();
        System.out.println(indexTypeList + "======== task success ======");
        break;
      }
      // 全部遍历了，无法排出来
      // lane中数据为空，说明全部都试了，无法排出来
      if(lastNumber == 0) {
        System.out.println(indexTypeList + "======== task failed ======");
        break;
      }
    }
    return sr;
  }
  public void cancel() {
    isCancelled = true;
  }
}
