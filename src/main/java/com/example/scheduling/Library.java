package com.example.scheduling;

import com.example.scheduling.util.CommonComponent;

import java.util.Map;

public class Library implements Cloneable{
  // 产品项目名称
  private String productName;
  // 洗脱文库号
  private String elutionLibraryName;
  // 吉因加编号
  private String geneplusCode;
  // 子文库名称
  private String libraryName;
  // index号
  private String indexNum;
  // F
  private String F;
  // R
  private String R;
  // 实际数据量
  private Float dataSize;
  // pooling编号
  private String poolingCode;
  // 拆分规则
  private String splitRule;
  // 备注
  private String notes;

  // 不同index type下的index序列
  private Map<CommonComponent.IndexType, String> indexSeqMap;
  public Library(){};
  @Override
  protected Library clone() throws CloneNotSupportedException {
    Library l = (Library) super.clone();
    return l;
  }

  public String getNotes() {
    return notes;
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

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Map<CommonComponent.IndexType, String> getIndexSeqMap() {
    return indexSeqMap;
  }

  public void setIndexSeqMap(Map<CommonComponent.IndexType, String> indexSeqMap) {
    this.indexSeqMap = indexSeqMap;
  }

  public String getPoolingCode() {
    return poolingCode;
  }

  public void setPoolingCode(String poolingCode) {
    this.poolingCode = poolingCode;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  public String getIndexNum() {
    return indexNum;
  }

  public void setIndexNum(String indexNum) {
    this.indexNum = indexNum;
  }

  public String getF() {
    return F;
  }

  public void setF(String f) {
    F = f;
  }

  public String getR() {
    return R;
  }

  public void setR(String r) {
    R = r;
  }

  public String getSplitRule() {
    return splitRule;
  }

  public void setSplitRule(String splitRule) {
    this.splitRule = splitRule;
  }

  public Float getDataSize() {
    return dataSize;
  }

  public void setDataSize(Float dataSize) {
    this.dataSize = dataSize;
  }
}
