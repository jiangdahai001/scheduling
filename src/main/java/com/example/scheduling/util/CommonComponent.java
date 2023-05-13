package com.example.scheduling.util;

import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;
import org.apache.poi.ss.formula.functions.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CommonComponent {
  // index类型
  public enum IndexType {
    S6(6),S8(3),S10(4),P6(5),P8(1),P10(2);
    public static Stream<IndexType> stream() {
      return Stream.of(IndexType.values());
    }
    private int value;
    IndexType(int value) {
      this.value = value;
    }
    public IndexType plus(int num) {
      int newValue = (this.value + num - 1) % IndexType.values().length + 1;
      return fromValue(newValue);
    }
    private static IndexType fromValue(int value) {
      for(IndexType indexType:IndexType.values()) {
        if(indexType.value == value) {
          return indexType;
        }
      }
      return null;
    }
    public static Boolean isLast(IndexType indexType) {
      return indexType.equals(S6);
    }
    public static IndexType getFirst() {
      return P8;
    }
    public static Boolean isPairEnd(IndexType indexType) {
      return indexType.equals(P6)|| indexType.equals(P8) ||indexType.equals(P10);
    }
    public static Boolean isSingleEnd(IndexType indexType) {
      return indexType.equals(S6)|| indexType.equals(S8) ||indexType.equals(S10);
    }
  }
  // index的序列对
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
  // 排单结果
  public static class ScheduledResult {
    // 排单是否成功
    private Boolean success;
    // 排单备注
    private String notes;
    // 是否紧急的都排上
    private Boolean isUrgentMet;
    // 未排单的数据量
    private Float unscheduledDataSize;
    // 未排单文库，最小单位是文库，外层是文库组
    private Map<String, LibraryGroup> unscheduledLibraryGroupMap = new HashMap<>();
    // lane列表
    private List<Lane> laneList = new ArrayList<>();

    public ScheduledResult() {
      this.success = false;
    }

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
  // 用于计算碱基比率的基础信息
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
  // 全局变量，排单基础信息
  public static class SchedulingInfo {
    // 排单基础信息单例
    private static SchedulingInfo instance = null;
    // 排单数据量
    private float dataSize;
    // 加急排单数据量
    private float urgentDataSize;
    // 排单文库组数
    private int libraryGroupSize;
    // 加急排单文库组数
    private int urgentLibraryGroupSize;

    private SchedulingInfo(){
      this.dataSize = 0f;
      this.urgentDataSize = 0f;
      this.libraryGroupSize = 0;
      this.urgentLibraryGroupSize = 0;
    }
    public static SchedulingInfo getInstance() {
      if(instance == null) {
        instance = new SchedulingInfo();
      }
      return instance;
    }
    public float getDataSize() {
      return dataSize;
    }
    public void setDataSize(float dataSize) {
      this.dataSize = dataSize;
    }

    public float getUrgentDataSize() {
      return urgentDataSize;
    }

    public void setUrgentDataSize(float urgentDataSize) {
      this.urgentDataSize = urgentDataSize;
    }

    public int getUrgentLibraryGroupSize() {
      return urgentLibraryGroupSize;
    }

    public void setUrgentLibraryGroupSize(int urgentLibraryGroupSize) {
      this.urgentLibraryGroupSize = urgentLibraryGroupSize;
    }

    public int getLibraryGroupSize() {
      return libraryGroupSize;
    }

    public void setLibraryGroupSize(int libraryGroupSize) {
      this.libraryGroupSize = libraryGroupSize;
    }
  }
}
