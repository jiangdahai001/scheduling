package com.example.scheduling;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {
  public static void main(String[] args) throws Exception{
    doSomethingWithTimeout(2);
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

