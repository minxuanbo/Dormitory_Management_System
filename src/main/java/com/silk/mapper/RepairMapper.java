package com.silk.mapper;

import java.util.List;
import java.util.Map;

import com.silk.entity.Repair;
import org.apache.ibatis.annotations.Param;


public interface RepairMapper {

	public int create(Repair repair);

	public int delete(Integer id);

	public int update(Repair repair);

	public int updateSelective(Repair repair);

	// 维修人员拒绝工单：清空指派信息，状态回到待指派(0)
	public int reject(Integer id);

	public List<Repair> query(Repair repair);

	public Repair detail(Integer id);

	public int count(Repair repair);

	public int countByStatus(@Param("repStatus") Integer repStatus);

	public int countByUrgency(@Param("urgency") Integer urgency);

	public int countByRepairer(@Param("repairerId") Integer repairerId);

	public double avgRating();

	public int countByRating(@Param("rating") Integer rating);

	public List<Map<String, Object>> countTrendNew();

	public List<Map<String, Object>> countTrendDone();

	// 按报修类型统计（饼图）
	public List<Map<String, Object>> countByType();

	// 维修人员绩效（工单量/完成量/平均评分/平均耗时）
	public List<Map<String, Object>> repairerPerformance();

}