package com.example.scheduling;

import com.example.scheduling.util.CommonComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibraryGroup implements Cloneable, Comparable<LibraryGroup> {
  // 产品项目名称
  private String productName;
  // 洗脱文库号
  private String elutionLibraryName;
  // 吉因加编号
  private String geneplusCode;
  // 拆分规则
  private String splitRule;
  // 是否加急
  private Boolean isUrgent;
  // 是否不平衡文库
  private Boolean isUnbalance;
  // 是否单边汉明距离放宽限制
  private Boolean isHammingDistantF;
  // 总数据量
  private Float dataSize;
  // 子文库信息
  private List<Library> libraryList;
  // 文库组编号
  private Integer number;
  // 汉明距离不符合条件的文库组吉因加编号列表
  private List<String> hammingDistantUnqualifiedGeneplusCodeList;
  // 汉明距离受限制的map,key是indextype，value是文库组id列表
  private Map<CommonComponent.IndexType, List<String>> hammingDistantLimitedMap;

  public LibraryGroup(){
    this.libraryList = new ArrayList<>();
    this.hammingDistantUnqualifiedGeneplusCodeList = new ArrayList<>();
  };
  @Override
  protected LibraryGroup clone() throws CloneNotSupportedException {
    LibraryGroup libraryGroup = (LibraryGroup) super.clone();
//    libraryGroup.libraryList = (ArrayList<Library>) libraryList.clone();
    List<Library> lList = new ArrayList<>();
    libraryList.forEach(library -> {
      try {
        lList.add((Library) library.clone());
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
    });
    return libraryGroup;
  }
  public Integer getNumber() {
    return number;
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

  public Map<CommonComponent.IndexType, List<String>> getHammingDistantLimitedMap() {
    return hammingDistantLimitedMap;
  }

  public void setHammingDistantLimitedMap(Map<CommonComponent.IndexType, List<String>> hammingDistantLimitedMap) {
    this.hammingDistantLimitedMap = hammingDistantLimitedMap;
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

  public String getSplitRule() {
    return splitRule;
  }

  public void setSplitRule(String splitRule) {
    this.splitRule = splitRule;
  }

  public Boolean getUrgent() {
    return isUrgent;
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

  public List<String> getHammingDistantUnqualifiedGeneplusCodeList() {
    return hammingDistantUnqualifiedGeneplusCodeList;
  }

  public void setHammingDistantUnqualifiedGeneplusCodeList(List<String> hammingDistantUnqualifiedGeneplusCodeList) {
    this.hammingDistantUnqualifiedGeneplusCodeList = hammingDistantUnqualifiedGeneplusCodeList;
  }

  /**
   * 排列两个文库组的顺序，按是否加急，是否不平衡文库，数据量
   * 加急的排在前面，不平衡文库排在前面，数据量大的排在前面
   * @param lg2
   * @return
   */
  @Override
  public int compareTo(LibraryGroup lg2) {
    LibraryGroup lg1 = this;
//    int size1 = lg1.getHammingDistantUnqualifiedGeneplusCodeList().size();
//    int size2 = lg2.getHammingDistantUnqualifiedGeneplusCodeList().size();
//    if(size1 != size2) {
//      return size2 - size1;
//    }
    if(lg1.getNumber()!=null &&lg2.getNumber()!=null) {
      return lg1.getNumber() - lg2.getNumber();
    }
    if((lg1.getUrgent() && lg2.getUrgent()) || (!lg1.getUrgent() && !lg2.getUrgent())) {
      if(lg1.getUnbalance() && lg2.getUnbalance()) {
        return lg2.getDataSize().compareTo(lg1.getDataSize());
      } else if(lg1.getUnbalance()) {
        return -1;
      } else if(lg2.getUnbalance()) {
        return 1;
      } else {
        return lg2.getDataSize().compareTo(lg1.getDataSize());
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
