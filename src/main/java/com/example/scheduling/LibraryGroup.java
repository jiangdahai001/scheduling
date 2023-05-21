package com.example.scheduling;

import com.example.scheduling.util.CommonComponent;

import java.util.*;

public class LibraryGroup implements Comparable<LibraryGroup> {
  // 产品项目名称
  private String productName;
  // 分组编码
  private String code;
  // 是否加急（加急）
  private Boolean isUrgent;
  // 是否不平衡文库（不平衡文库）
  private Boolean isUnbalance;
  // 是否单端汉明距离放宽限制（F）
  private Boolean isHammingDistantF;
  // 是否单端index文库
  private Boolean isSingleEnd;
  // 同lane上机限制
  private String sameLaneLimit;
  // 是否墨卓样本
  private Boolean isMozhuo;
  // pooling编号
  private String poolingCode;

  // 总数据量
  private Float dataSize;
  // 子文库信息
  private List<Library> libraryList;
  // 文库组编号
  private Integer number;
  // 汉明距离受限制的map,key是indextype，value是文库组code列表
  private Map<CommonComponent.IndexType, List<String>> hammingDistantLimitCodeMap;
  // 汉明距离受限制的map，key是indextype的列表，value是文库组code列表，这个map的key是根据实际排单中的lane列表具体来填值的
  private Map<List<CommonComponent.IndexType>, List<String>> dynamicHammingDistantLimitCodeMap;

  public LibraryGroup(){
    this.libraryList = new ArrayList<>();
    Map<CommonComponent.IndexType, List<String>> map = new HashMap<>();
    for(CommonComponent.IndexType type:CommonComponent.IndexType.values()) {
      map.put(type, new ArrayList<>());
    }
    this.hammingDistantLimitCodeMap = map;
    Map<List<CommonComponent.IndexType>, List<String>> dynamicMap = new HashMap<>();
    this.dynamicHammingDistantLimitCodeMap = dynamicMap;
  };
  public Boolean getSingleEnd() {
    return isSingleEnd;
  }

  public void setSingleEnd(Boolean singleEnd) {
    isSingleEnd = singleEnd;
  }

  public Integer getNumber() {
    return number;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Map<CommonComponent.IndexType, List<String>> getHammingDistantLimitCodeMap() {
    return hammingDistantLimitCodeMap;
  }

  public Map<List<CommonComponent.IndexType>, List<String>> getDynamicHammingDistantLimitCodeMap() {
    return dynamicHammingDistantLimitCodeMap;
  }

  public void setDynamicHammingDistantLimitCodeMap(Map<List<CommonComponent.IndexType>, List<String>> dynamicHammingDistantLimitCodeMap) {
    this.dynamicHammingDistantLimitCodeMap = dynamicHammingDistantLimitCodeMap;
  }

  public void setHammingDistantLimitCodeMap(Map<CommonComponent.IndexType, List<String>> hammingDistantLimitCodeMap) {
    this.hammingDistantLimitCodeMap = hammingDistantLimitCodeMap;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Boolean getHammingDistantF() {
    return isHammingDistantF;
  }

  public void setHammingDistantF(Boolean hammingDistantF) {
    isHammingDistantF = hammingDistantF;
  }
  public Boolean getUrgent() {
    return isUrgent;
  }

  public String getSameLaneLimit() {
    return sameLaneLimit;
  }

  public void setSameLaneLimit(String sameLaneLimit) {
    this.sameLaneLimit = sameLaneLimit;
  }

  public Boolean getMozhuo() {
    return isMozhuo;
  }

  public void setMozhuo(Boolean mozhuo) {
    isMozhuo = mozhuo;
  }

  public String getPoolingCode() {
    return poolingCode;
  }

  public void setPoolingCode(String poolingCode) {
    this.poolingCode = poolingCode;
  }

  public void setUrgent(Boolean urgent) {
    isUrgent = urgent;
  }

  public Boolean getUnbalance() {
    return isUnbalance;
  }

  public void setUnbalance(Boolean unbalance) {
    isUnbalance = unbalance;
  }

  public Float getDataSize() {
    return dataSize;
  }

  public void setDataSize(Float dataSize) {
    this.dataSize = dataSize;
  }

  public List<Library> getLibraryList() {
    return libraryList;
  }

  public void setLibraryList(List<Library> libraryList) {
    this.libraryList = libraryList;
  }

  /**
   * 排列两个文库组的顺序，按是否加急，是否不平衡文库，数据量
   * 加急的排在前面，不平衡文库排在前面，数据量大的排在前面
   * 如果已经排过顺序，按排过的序号来排列
   * @param lg2 被比较的那个文库组
   * @return 返回比较结果
   */
  @Override
  public int compareTo(LibraryGroup lg2) {
    LibraryGroup lg1 = this;
    if(lg1.getNumber()!=null &&lg2.getNumber()!=null) {
      return lg1.getNumber() - lg2.getNumber();
    }
    if((lg1.getUrgent() && lg2.getUrgent()) || (!lg1.getUrgent() && !lg2.getUrgent())) {
      if(lg1.getUnbalance() && lg2.getUnbalance()) {
//        return lg2.getDataSize().compareTo(lg1.getDataSize());
        if(Math.abs(lg2.getDataSize()-lg1.getDataSize()) < 0.0001) {
          return lg1.getCode().compareTo(lg2.getCode());
        } else {
          return lg2.getDataSize().compareTo(lg1.getDataSize());
        }
      } else if(lg1.getUnbalance()) {
        return -1;
      } else if(lg2.getUnbalance()) {
        return 1;
      } else {
//        return lg2.getDataSize().compareTo(lg1.getDataSize());
        if(Math.abs(lg2.getDataSize()-lg1.getDataSize()) < 0.0001) {
          return lg1.getCode().compareTo(lg2.getCode());
        } else {
          return lg2.getDataSize().compareTo(lg1.getDataSize());
        }
      }
    } else if(lg1.getUrgent()) {
      return -1;
    } else if(lg2.getUrgent()) {
      return 1;
    } else {
      return 0;
    }
  }
}
