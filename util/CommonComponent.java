package com.example.scheduling.util;

import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommonComponent {
  public enum IndexType {
    P6,P8,P10,S6,S8,S10;
    public static Stream<IndexType> stream() {
      return Stream.of(IndexType.values());
    }
  }
  public static class FRPair {
    private String F;
    private String R;
    public FRPair(String f, String r) {
      F = f;
      R = r;
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
  }
  public static class ScheduledResult {
    // 排单是否成功
    private Boolean success;
    // 排单备注
    private String notes;
    // index类型
    private IndexType indexType;
    // 是否紧急的都排上
    private Boolean isUrgentMet;
    // 未排单的数据量
    private Float unscheduledDataSize;
    // 未排单文库，最小单位是文库，外层是文库组
    private Map<String, LibraryGroup> unscheduledLibraryGroupMap = new HashMap<>();
    // lane列表
    private List<Lane> laneList = new ArrayList<>();

    public Boolean getSuccess() {
      return success;
    }

    public void setSuccess(Boolean success) {
      this.success = success;
    }

    public String getNotes() {
      return notes;
    }

    public void setNotes(String notes) {
      this.notes = notes;
    }

    public IndexType getIndexType() {
      return indexType;
    }

    public void setIndexType(IndexType indexType) {
      this.indexType = indexType;
    }

    public Boolean getUrgentMet() {
      return isUrgentMet;
    }

    public void setUrgentMet(Boolean urgentMet) {
      isUrgentMet = urgentMet;
    }

    public Float getUnscheduledDataSize() {
      return unscheduledDataSize;
    }

    public void setUnscheduledDataSize(Float unscheduledDataSize) {
      this.unscheduledDataSize = unscheduledDataSize;
    }

    public Map<String, LibraryGroup> getUnscheduledLibraryGroupMap() {
      return unscheduledLibraryGroupMap;
    }

    public void setUnscheduledLibraryGroupMap(Map<String, LibraryGroup> unscheduledLibraryGroupMap) {
      this.unscheduledLibraryGroupMap = unscheduledLibraryGroupMap;
    }

    public List<Lane> getLaneList() {
      return laneList;
    }

    public void setLaneList(List<Lane> laneList) {
      this.laneList = laneList;
    }
  }
  public static class BaseRatioInfo {
    private float dataSize;
    private String sequence;
    public float getDataSize() {
      return dataSize;
    }
    public void setDataSize(float dataSize) {
      this.dataSize = dataSize;
    }
    public String getSequence() {
      return sequence;
    }
    public void setSequence(String sequence) {
      this.sequence = sequence;
    }
    public BaseRatioInfo(String sequence, float dataSize) {
      this.sequence = sequence;
      this.dataSize = dataSize;
    }
  }
}
