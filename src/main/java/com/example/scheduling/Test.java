package com.example.scheduling;

import com.example.scheduling.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
    int ceiling = 1450;
    int floor = 1350;
    for(int i=1;i<15;i++) {
      System.out.println(i + "个lane：" + i*floor +"---"+i*ceiling);
    }
  }
}
