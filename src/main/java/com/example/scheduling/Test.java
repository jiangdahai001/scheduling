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
    String a = "同lane上机2,F，同lane上机1，同lane上机,lane100";
//    a = "";
//    a = null;
    a = a==null? "":a;
    List<String> notes = Arrays.asList(a.replaceAll("，", ",").split(","));
    for (String s : notes) {
      if(s.equals("同lane上机")) {
        System.out.println("同lane上机");
      }
      if (s.matches("同lane上机\\d*?")) {
        System.out.println(s);
      }
    }
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

