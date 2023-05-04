package com.example.scheduling;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.scheduling.util.ExcelData;
import com.example.scheduling.util.ExcelDataOutput;
import com.example.scheduling.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Test {
  public static void main(String[] args) {
    System.out.println("hello test");
    List<List<String>> list = new ArrayList<>();
    List<String> list1 = new ArrayList<>();
    list1.add("abc");
    list1.add("mnk");
    List<String> list2 = new ArrayList<>();
    list2.add("abc");
    list2.add("opq");
    List<String> ll = new ArrayList<>();
    List<String> list3 = new ArrayList<>();
    list3.add("mnk");
    list3.add("abc");
    list.add(list1);
    list.add(list2);
    list.add(ll);
    list.add(list3);
    List<String> l = Utils.getIntersection(list);
    System.out.println(l);
  }

  public static void test() {
    String fileName = "C:\\Users\\admin\\Desktop\\input表苏州2.16.xlsx";
    EasyExcel.read(fileName, ExcelData.class, new PageReadListener<ExcelData>(dataList -> {
      for (ExcelData excelData : dataList) {
        System.out.println("读取到一条数据{}" + JSON.toJSONString(excelData));
      }
    })).sheet().doRead();
  }
  public static void test1() {
    String F = "ATCCGCGACC";
    String R = "";
    System.out.println("F:" +F);
    System.out.println("R:" +R);
  }

  public static void writeExcel(String fileName) {
    try (ExcelWriter excelWriter = EasyExcel.write(fileName, ExcelDataOutput.class).build()) {
      WriteSheet writeSheet = EasyExcel.writerSheet("排单结果").build();
        List<ExcelDataOutput> dataList = new ArrayList<>();
        ExcelDataOutput edo = new ExcelDataOutput();
        edo.setProductName("AAA");
        edo.setElutionLibraryName("lg.getElutionLibraryName()");
        edo.setGeneplusCode("lg.getGeneplusCode()");
        edo.setSubLibraryName("l.getLibraryName()");
        edo.setF("l.getF()");
        edo.setR("l.getR()");
        edo.setDataSize(100f);
        edo.setSplitRule("l.getSplitRule()");
        dataList.add(edo);
        excelWriter.write(dataList, writeSheet);
        excelWriter.write(dataList, writeSheet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
