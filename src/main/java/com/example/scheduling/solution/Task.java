package com.example.scheduling.solution;

import com.example.scheduling.Lane;
import com.example.scheduling.LibraryGroup;
import com.example.scheduling.util.CommonComponent;
import com.example.scheduling.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class Task implements Callable<CommonComponent.ScheduledResult> {
  private volatile boolean isCancelled;
  private final Map<String, LibraryGroup> libraryGroupMap;
  private final List<Lane> laneList;

  public Task(Map<String, LibraryGroup> libraryGroupMap, List<Lane> laneList) {
    this.libraryGroupMap = libraryGroupMap;
    this.laneList = laneList;
  }

  /**
   * 获取排单结果
   * @return 排单结果
   */
  @Override
  public CommonComponent.ScheduledResult call() {
    CommonComponent.ScheduledResult sr = new CommonComponent.ScheduledResult();
    // 将map转换为list，并排序
    List<LibraryGroup> libraryGroupList = libraryGroupMap.values().stream().sorted().collect(Collectors.toList());
    // 根据lane列表获取indextype列表
    List<CommonComponent.IndexType> itList = laneList.stream().map(Lane::getIndexType).collect(Collectors.toList());
    // 最后一个放到lane中的文库组的编号，初始是0，说明还没有放
    int lastNumber = 0;
    while (!isCancelled) {
      Map<String, LibraryGroup> unscheduledMap = new HashMap<>();

      Utils.setDynamicHammingDistantLimitCodeMap(libraryGroupMap, laneList, unscheduledMap);
      Utils.moveToUnscheduledMapAccordingHammingDistance(laneList, libraryGroupMap, unscheduledMap);
//      System.out.println("unscheduledMap:");
//      Float unscheduledMapSize = 0f;
//      for(Map.Entry<String, LibraryGroup> entry: unscheduledMap.entrySet()) {
//        unscheduledMapSize += entry.getValue().getDataSize();
//        System.out.println(entry.getKey() + ": " + entry.getValue().getNumber());
//      }
//      System.out.println("unscheduledMapSize: " + unscheduledMapSize);

      lastNumber = Utils.traversalMemory(libraryGroupList, laneList, unscheduledMap, lastNumber);
      // 找到最新的待排单的文库组，如果是非加急的，就看看已经排好的是否符合要求，
      // 如果符合要求，就可以快速结束排单了
//      if(lastNumber!=0 && lastNumber!=libraryGroupList.size()) {
//        LibraryGroup lg = libraryGroupList.get(lastNumber + 1);
//        if (!lg.getUrgent()) {
//          sr.setLaneList(laneList);
//          for (LibraryGroup tmp : libraryGroupList) {
//            if (tmp.getNumber() > lastNumber) {
//              Utils.addLibraryGroupToUnscheduledMap(tmp, unscheduledMap);
//            }
//          }
//          sr.setUnscheduledLibraryGroupMap(unscheduledMap);
//          Utils.setScheduledResultInfo(sr);
//          if (!sr.getSuccess()) continue;
//          System.out.println(itList + "======== success--unscheduled ======");
//          break;
//        }
//      }
      // 找到在libraryGroupList中，但不在unscheduledMap中的最大的number
      int largestNumber = Utils.getLargestNumber(libraryGroupList, unscheduledMap);
      // 全部文库组都排到lane中了
      if(lastNumber==largestNumber) {
        sr.setLaneList(laneList);
        sr.setUnscheduledLibraryGroupMap(unscheduledMap);
        Utils.setScheduledResultInfo(sr);
//        if(!sr.getSuccess()) continue;
        System.out.println(itList + "======== success ======");
        break;
      }
      // 全部遍历了，无法排出来
      // lane中数据为空，说明全部都试了，无法排出来
      if(lastNumber == 0) {
        System.out.println(itList + "======== failed ======");
        break;
      }
    }
    return sr;
  }
  public void cancel() {
    isCancelled = true;
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public void setCancelled(boolean cancelled) {
    isCancelled = cancelled;
  }

  public Map<String, LibraryGroup> getLibraryGroupMap() {
    return libraryGroupMap;
  }

  public List<Lane> getLaneList() {
    return laneList;
  }
}
