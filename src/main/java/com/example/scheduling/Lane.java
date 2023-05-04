package com.example.scheduling;

import com.example.scheduling.util.CommonComponent;

import java.util.ArrayList;
import java.util.List;

public class Lane {
  // index 类型
  private CommonComponent.IndexType indexType;
  // lane的数据量
  private Float dataSize;
  // lane的数据量上限
  private Float dataSizeCeiling;
  // lane的数据量下限
  private Float dataSizeFloor;
  // lane的不平衡数据量
  private Float unbalanceDataSize;
  // lane的不平衡数据量上限
  private Float unbalanceDataSizeCeiling;
  // lane中单端文库组数据量
  private Float singleEndDataSize;
  // index为双端时，lane中单端的比率上限
  private Float singleEndRatioLimit;
  // lane中的文库组列表
  private List<LibraryGroup> libraryGroupList;

  public Lane(){
    this.indexType = CommonComponent.IndexType.getFirst();
    this.dataSize = 0f;
    this.dataSizeCeiling = 1400f;
    this.dataSizeFloor = 1300f;
    this.unbalanceDataSize = 0f;
    this.unbalanceDataSizeCeiling = 300f;
    this.singleEndDataSize = 0f;
    this.singleEndRatioLimit = 0.5f;
    this.libraryGroupList = new ArrayList<>();
  };

  public Float getSingleEndDataSize() {
    return singleEndDataSize;
  }

  public void setSingleEndDataSize(Float singleEndDataSize) {
    this.singleEndDataSize = singleEndDataSize;
  }

  public Float getSingleEndRatioLimit() {
    return singleEndRatioLimit;
  }

  public void setSingleEndRatioLimit(Float singleEndRatioLimit) {
    this.singleEndRatioLimit = singleEndRatioLimit;
  }

  public Float getUnbalanceDataSizeCeiling() {
    return unbalanceDataSizeCeiling;
  }

  public void setUnbalanceDataSizeCeiling(Float unbalanceDataSizeCeiling) {
    this.unbalanceDataSizeCeiling = unbalanceDataSizeCeiling;
  }

  public CommonComponent.IndexType getIndexType() {
    return indexType;
  }

  public void setIndexType(CommonComponent.IndexType indexType) {
    this.indexType = indexType;
  }

  public Lane(Float dataSize) {
    this.dataSize = dataSize;
  }

  public Float getDataSize() {
    return dataSize;
  }

  public void setDataSize(Float dataSize) {
    this.dataSize = dataSize;
  }

  public Float getDataSizeCeiling() {
    return dataSizeCeiling;
  }

  public void setDataSizeCeiling(Float dataSizeCeiling) {
    this.dataSizeCeiling = dataSizeCeiling;
  }

  public Float getDataSizeFloor() {
    return dataSizeFloor;
  }

  public void setDataSizeFloor(Float dataSizeFloor) {
    this.dataSizeFloor = dataSizeFloor;
  }

  public Float getUnbalanceDataSize() {
    return unbalanceDataSize;
  }

  public void setUnbalanceDataSize(Float unbalanceDataSize) {
    this.unbalanceDataSize = unbalanceDataSize;
  }

  public List<LibraryGroup> getLibraryGroupList() {
    return libraryGroupList;
  }

  public void setLibraryGroupList(List<LibraryGroup> libraryGroupList) {
    this.libraryGroupList = libraryGroupList;
  }
}
