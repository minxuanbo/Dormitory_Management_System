package com.silk.mapper;

import com.silk.entity.Room;
import com.silk.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {

    // 条件查询（按楼栋+楼层，或按ID）
    List<Room> query(Room room);

    // 查询房间入住人数
    Integer queryLiverAmount(@Param("id") Integer id);

    // 查询房间已入住的学生列表（床位顺序：按学号排序）
    List<User> queryLivers(@Param("roomId") Integer roomId);

    // 新增
    int insert(Room room);

    // 按需更新
    int updateSelective(Room room);

    // 批量删除（ids 逗号分隔）
    int deleteByIds(@Param("ids") String ids);

    // 按楼栋批量删除（删除楼栋时连带删除其下房间）
    int deleteByBuildingIds(@Param("ids") String ids);

    // 详情
    Room detail(@Param("id") Integer id);

    // 床位+1
    int capacityPlusOne(@Param("id") Integer id);

    // 床位-1
    int capacityMinusOne(@Param("id") Integer id);
}
