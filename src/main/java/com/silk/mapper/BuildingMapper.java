package com.silk.mapper;

import com.silk.entity.Building;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BuildingMapper {

    // 条件分页查询（楼栋列表）
    List<Building> query(Building building);

    // 查询楼栋总层数
    Integer queryFloorNum(@Param("id") Integer id);

    // 查询指定楼栋已入住人数
    Integer queryLiverCount(@Param("buildingId") Integer buildingId);

    // 查询指定楼栋总床位
    Integer queryTotalCapacity(@Param("buildingId") Integer buildingId);

    // 新增
    int insert(Building building);

    // 按需更新
    int updateSelective(Building building);

    // 批量删除（ids 逗号分隔）
    int deleteByIds(@Param("ids") String ids);

    // 详情
    Building detail(@Param("id") Integer id);
}
