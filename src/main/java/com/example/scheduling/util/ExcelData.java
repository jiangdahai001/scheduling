package com.example.scheduling.util;

import com.alibaba.excel.annotation.ExcelProperty;

public class ExcelData {
  @ExcelProperty("产品项目名称")
  private String productName;
  @ExcelProperty("*洗脱文库号")
  private String elutionLibraryName;
  @ExcelProperty("吉因加编号")
  private String geneplusCode;
  @ExcelProperty("子文库名称")
  private String subLibraryName;
  @ExcelProperty("index号")
  private String indexNum;
  @ExcelProperty("F")
  private String f;
  @ExcelProperty("R")
  private String r;
  @ExcelProperty("放大的数据量")
  private Float dataSize;
  @ExcelProperty("pooling编号")
  private String poolingCode;
  @ExcelProperty("拆分规则")
  private String splitRule;
  @ExcelProperty("备注")
  private String notes;

  @Override
  public String toString() {
    return "ExcelData{" +
      "productName='" + productName + '\'' +
      ", elutionLibraryName='" + elutionLibraryName + '\'' +
      ", geneplusCode='" + geneplusCode + '\'' +
      ", subLibraryName='" + subLibraryName + '\'' +
      ", f='" + f + '\'' +
      ", r='" + r + '\'' +
      ", dataSize=" + dataSize +
      ", splitRule='" + splitRule + '\'' +
      ", notes='" + notes + '\'' +
      '}';
  }

  public String getIndexNum() {
    return indexNum;
  }

  public void setIndexNum(String indexNum) {
    this.indexNum = indexNum;
  }

  public String getPoolingCode() {
    return poolingCode;
  }

  public void setPoolingCode(String poolingCode) {
    this.poolingCode = poolingCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getElutionLibraryName() {
    return elutionLibraryName;
  }

  public void setElutionLibraryName(String elutionLibraryName) {
    this.elutionLibraryName = elutionLibraryName;
  }

  public String getGeneplusCode() {
    return geneplusCode;
  }

  public void setGeneplusCode(String geneplusCode) {
    this.geneplusCode = geneplusCode;
  }

  public String getSubLibraryName() {
    return subLibraryName;
  }

  public void setSubLibraryName(String subLibraryName) {
    this.subLibraryName = subLibraryName;
  }

  public String getF() {
    return f;
  }

  public void setF(String f) {
    this.f = f;
  }

  public String getR() {
    return r;
  }

  public void setR(String r) {
    this.r = r;
  }

  public Float getDataSize() {
    return dataSize;
  }

  public void setDataSize(Float dataSize) {
    this.dataSize = dataSize;
  }

  public String getSplitRule() {
    return splitRule;
  }

  public void setSplitRule(String splitRule) {
    this.splitRule = splitRule;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
