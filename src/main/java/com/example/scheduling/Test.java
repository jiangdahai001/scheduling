package com.example.scheduling;

import com.example.scheduling.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {
  public static void main(String[] args) throws Exception{
    long start = System.currentTimeMillis();
    test();
    long end = System.currentTimeMillis();
    System.out.println("*****************************************************************************");
    System.out.println((end - start) + "ms");
    System.out.println(Utils.getTimeString(end - start));
  }
  public static void test () {
    int expected = 1350;
    int floor = expected - 50;
    int ceiling = expected + 50;
    for(int i=1;i<=8;i++) {
      System.out.println("number: " + i + "---floor:" + i*floor + "---" + "ceiling:" + i*ceiling);
    }
  }
}
