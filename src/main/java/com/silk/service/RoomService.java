package com.silk.service;

import com.silk.entity.Room;
import com.silk.entity.User;
import com.silk.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;

    // 条件查询（楼栋+楼层，或按ID）
    public List<Room> query(Room room) {
        return roomMapper.query(room);
    }

    // 房间入住人数
    public Integer queryLiverAmount(Integer id) {
        Integer amount = roomMapper.queryLiverAmount(id);
        return amount == null ? 0 : amount;
    }

    // 房间已入住的学生列表
    public List<User> queryLivers(Integer roomId) {
        return roomMapper.queryLivers(roomId);
    }

    public int create(Room room) {
        return roomMapper.insert(room);
    }

    public int updateSelective(Room room) {
        return roomMapper.updateSelective(room);
    }

    public int delete(String ids) {
        return roomMapper.deleteByIds(ids);
    }

    public Room detail(Integer id) {
        return roomMapper.detail(id);
    }

    public int capacityPlusOne(Integer id) {
        return roomMapper.capacityPlusOne(id);
    }

    public int capacityMinusOne(Integer id) {
        return roomMapper.capacityMinusOne(id);
    }
}
