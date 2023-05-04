package com.example.scheduling;

import java.util.ArrayList;
import java.util.List;

public class Lane implements Cloneable{
  private Float dataSize;
  private Float dataSizeCeiling;
  private Float dataSizeFloor;
  private Float unbalanceDataSize;
  private List<LibraryGroup> libraryGroupList;

  @Override
  protected Lane clone() throws CloneNotSupportedException {
    Lane lane = (Lane) super.clone();
    List<LibraryGroup> libraryGroups = new ArrayList<>();
    libraryGroupList.forEach(libraryGroup -> {
      try {
        libraryGroups.add((LibraryGroup) libraryGroup.clone());
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    });
    return lane;
  }
  public Lane(){
    this.dataSize = 0f;
    this.dataSizeCeiling = 0f;
    this.dataSizeFloor = 0f;
    this.unbalanceDataSize = 0f;
    this.libraryGroupList = new ArrayList<>();
  };
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
