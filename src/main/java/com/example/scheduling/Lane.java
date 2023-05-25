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
  // lane的不平衡文库数据量比率上线
  private Float unbalanceDataSizeRatioLimit;
  // lane中墨卓文库的数据量
  private Float unbalanceMozhuoDataSize;
  // lane中墨卓文库的数据量上限
  private Float unbalanceMozhuoDataSizeCeiling;
  // lane中单端文库组数据量
  private Float singleEndDataSize;
  // index为双端时，lane中单端的比率上限
  private Float singleEndDataSizeRatioLimit;
  // lane中的文库组列表
  private List<LibraryGroup> libraryGroupList;

  public Lane(){
    this.indexType = CommonComponent.IndexType.getFirst();
    this.dataSize = 0f;
    this.dataSizeCeiling = CommonComponent.SchedulingInfo.getInstance().getLaneDataSizeCeiling();
    this.dataSizeFloor = CommonComponent.SchedulingInfo.getInstance().getLaneDataSizeFloor();
    this.unbalanceDataSize = 0f;
    this.unbalanceMozhuoDataSize = 0f;
    this.unbalanceMozhuoDataSizeCeiling = 150f;
    this.unbalanceDataSizeRatioLimit = 0.25f;
    this.singleEndDataSize = 0f;
    this.singleEndDataSizeRatioLimit = 0.5f;
    this.libraryGroupList = new ArrayList<>();
  };

  public Float getUnbalanceMozhuoDataSize() {
    return unbalanceMozhuoDataSize;
  }

  public void setUnbalanceMozhuoDataSize(Float unbalanceMozhuoDataSize) {
    this.unbalanceMozhuoDataSize = unbalanceMozhuoDataSize;
  }

  public Float getUnbalanceMozhuoDataSizeCeiling() {
    return unbalanceMozhuoDataSizeCeiling;
  }

  public void setUnbalanceMozhuoDataSizeCeiling(Float unbalanceMozhuoDataSizeCeiling) {
    this.unbalanceMozhuoDataSizeCeiling = unbalanceMozhuoDataSizeCeiling;
  }

  public Float getSingleEndDataSizeRatioLimit() {
    return singleEndDataSizeRatioLimit;
  }

  public void setSingleEndDataSizeRatioLimit(Float singleEndDataSizeRatioLimit) {
    this.singleEndDataSizeRatioLimit = singleEndDataSizeRatioLimit;
  }

  public Float getUnbalanceDataSizeRatioLimit() {
    return unbalanceDataSizeRatioLimit;
  }

  public void setUnbalanceDataSizeRatioLimit(Float unbalanceDataSizeRatioLimit) {
    this.unbalanceDataSizeRatioLimit = unbalanceDataSizeRatioLimit;
  }

  public Float getSingleEndDataSize() {
    return singleEndDataSize;
  }

  public void setSingleEndDataSize(Float singleEndDataSize) {
    this.singleEndDataSize = singleEndDataSize;
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
