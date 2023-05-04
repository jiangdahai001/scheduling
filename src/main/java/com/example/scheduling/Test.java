package com.example.scheduling;
import java.util.concurrent.*;
public class Test {
  public static void main(String[] args) {
    String a = method(1, 10, "xxxx");
    System.out.println("a:" + a);
  }
  public static String method(int threadNum, int timeOut, String abc) {
    String r = null;
    ExecutorService es = Executors.newFixedThreadPool(threadNum);
    Future<String> future = es.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        System.out.println(abc);
        return demo(abc);
      }
    });
    try {
      r = future.get(timeOut, TimeUnit.SECONDS);
    } catch (TimeoutException te) {
      future.cancel(true);
      System.out.println("输出异常：" + te.getMessage());
    } catch(Exception e) {
      System.out.println("其他异常！！！");
    }
    // 关闭线程池
    es.shutdown();
    return r;
  }
  public static void main1(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<?> future = executorService.submit(() -> {
      try {
        demo("aa");
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + "获取的结果 -- start");
//    Object result = future.get(100, TimeUnit.MILLISECONDS);
    try {
      Object result = future.get(10000, TimeUnit.MILLISECONDS);
      System.out.println(System.currentTimeMillis() + "," + threadName + "获取的结果 -- end :" + result);
    } catch (Exception e) {
      System.out.println(System.currentTimeMillis() + "," + threadName + "获取的结果异常:" + e.toString());
    }
    future.cancel(true);
    System.out.println(System.currentTimeMillis() + "," + threadName + "获取的结果 -- cancel");
    executorService.shutdown();
  }
  private static String demo(String aa) throws InterruptedException {
    String threadName = Thread.currentThread().getName();
    System.out.println(threadName + ",执行 demo -- start");
    TimeUnit.SECONDS.sleep(5);
    System.out.println(threadName + ",执行 demo -- end");
    return "test---"+ aa;
  }
}

