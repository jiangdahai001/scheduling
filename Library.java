package com.example.scheduling;

public class Library implements Cloneable{
  // 子文库名称
  private String libraryName;
  // index号
  private int indexNum;
  // F
  private String F;
  // R
  private String R;
  // 拆分规则
  private String splitRule;
  // 备注
  private String notes;
  // 用于计算的index
  private String indexSeq;
  // 实际数据量
  private Float dataSize;
  public Library(){};
  @Override
  protected Library clone() throws CloneNotSupportedException {
    Library l = (Library) super.clone();
    return l;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(String libraryName) {
    this.libraryName = libraryName;
  }

  public int getIndexNum() {
    return indexNum;
  }

  public void setIndexNum(int indexNum) {
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

  public String getIndexSeq() {
    return indexSeq;
  }

  public void setIndexSeq(String indexSeq) {
    this.indexSeq = indexSeq;
  }

  public Float getDataSize() {
    return dataSize;
  }

  public void setDataSize(Float dataSize) {
    this.dataSize = dataSize;
  }
}
