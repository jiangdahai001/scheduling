package com.example.scheduling;

import java.util.concurrent.*;

public class Test {
  private static final Exchanger<String> exchanger = new Exchanger<>();
  private static final ExecutorService threadPool =  Executors.newFixedThreadPool(2);
  public static void main(String[] args) {
    threadPool.execute(() -> {
      String A = "银行流水";
      try {
        Thread.sleep(1000);
        System.out.println("A 完成录入");
        exchanger.exchange(A);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    threadPool.execute(() -> {
      String B = "银行流水";
      try {
        System.out.println("B 完成录入");
        String A = exchanger.exchange(B);
        System.out.println("A和B的数据是否一致: " + A.equals(B) + ",A录入的是: "+A + ",B录入的是: " +B);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    threadPool.shutdown();
  }
}
