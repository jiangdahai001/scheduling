package com.example.scheduling;

import com.example.scheduling.util.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchedulingApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(SchedulingApplication.class, args);
  }
  @Override
  public void run(String... args) throws Exception {
    System.out.println("hello, world");
    long start = System.currentTimeMillis();
    Main.main(args);
    long end = System.currentTimeMillis();
    System.out.println("*****************************************************************************");
    System.out.println((end - start) + "ms");
    System.out.println(Utils.getTimeString(end - start));
  }
}
