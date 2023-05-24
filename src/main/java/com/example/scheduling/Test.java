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
    test5();
    long end = System.currentTimeMillis();
    System.out.println("*****************************************************************************");
    System.out.println((end - start) + "ms");
    System.out.println(Utils.getTimeString(end - start));
  }
  public static void test5() throws Exception {
    ForkJoinPool pool=new ForkJoinPool();
    // 创建异步执行任务:
    CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
      System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
      }
      System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
      return 1.2;
    },pool);
    //cf关联的异步任务的返回值作为方法入参，传入到thenApply的方法中
    //thenApply这里实际创建了一个新的CompletableFuture实例
    CompletableFuture<String> cf2=cf.thenApplyAsync((result)->{
      System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
      }
      System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
      return "test:"+result;
    });
    System.out.println("main thread start cf.get(),time->"+System.currentTimeMillis());
    //等待子任务执行完成
    System.out.println("run result->"+cf.get());
    System.out.println("main thread start cf2.get(),time->"+System.currentTimeMillis());
    System.out.println("run result->"+cf2.get());
    System.out.println("main thread exit,time->"+System.currentTimeMillis());
  }

}
