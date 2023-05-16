package com.example.scheduling;

import com.example.scheduling.util.Utils;
import org.apache.poi.ss.formula.functions.T;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {
  public static void main(String[] args) throws Exception{
    long start = System.currentTimeMillis();
    test();
    long end = System.currentTimeMillis();
    System.out.println("*****************************************************************************");
    System.out.println((end - start) + "ms");
    System.out.println(Utils.getTimeString(end - start));
  }
  public static void test (){
    // 要不要在任务中sleep？
    final boolean[] flag = {true};
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        System.out.println("timer start");
        flag[0] = false;
        System.out.println("timer end");
      }
    };
    timer.schedule(timerTask, 10);
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Integer> future = executor.submit(() -> {
      System.out.println("task start");
//      for(int i=0;i<10;i++) {
//        if (flag[0]) {
//          break;
//        }
//        Thread.sleep(2000);
//      }
      if(flag[0]) Thread.sleep(2000);
      System.out.println("task finish");
      return 42;
    });

    try {
      System.out.println("main start");
      Thread.sleep(200);
      System.out.println("main end");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
// 调用 get 方法，等待任务完成并返回结果
    int result = 0;
    try {
      result = future.get(200, TimeUnit.MILLISECONDS);
      System.out.println("task ok");
    } catch (TimeoutException e) {
      future.cancel(true);
      System.out.println("task timeout");
    } catch (Exception e) {
      e.printStackTrace();
    }
    executor.shutdown();
    timer.cancel();
    System.out.println(result);
  }

  public static boolean doSomethingWithTimeout(int timeoutInSeconds) throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final AtomicBoolean result = new AtomicBoolean(false);

    Thread taskThread = new Thread(() -> {
      // 执行需要超时的任务
      boolean taskResult = executeTask();
      result.set(taskResult);
      latch.countDown();
    });

    taskThread.start();
    System.out.println("main 启动线程");
    boolean timeout = !latch.await(timeoutInSeconds, TimeUnit.SECONDS);
    if (timeout) {
      // 超时，中断任务线程
      taskThread.interrupt();
      return false;
    }
    System.out.println("main 结束");
    return result.get();
  }

  private static boolean executeTask() {
    // 执行需要超时的任务
    System.out.println("开始执行任务");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      System.out.println("执行任务超时");
      return false;
    }
    System.out.println("结束执行任务");
    return true;
  }
}

