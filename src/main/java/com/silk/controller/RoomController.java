package com.silk.controller;

import com.silk.entity.Room;
import com.silk.service.RoomService;
import com.silk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间管理接口（对接前端 page/room/*）
 */
@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("create")
    public Result create(@RequestBody Room room) {
        // 前端未传 id 时，按编码规则自动生成：楼栋ID*10000 + 门牌号（如 13栋 0701 -> 130701）
        if (room.getId() == null && room.getBuildingId() != null && room.getBrand() != null) {
            try {
                room.setId(room.getBuildingId() * 10000 + Integer.parseInt(room.getBrand().trim()));
            } catch (NumberFormatException e) {
                return Result.fail("门牌号格式不正确，应为数字（如0701）");
            }
        }
        if (roomService.detail(room.getId()) != null) {
            return Result.fail("该房间ID已存在");
        }
        int flag = roomService.create(room);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    @PostMapping("update")
    public Result update(@RequestBody Room room) {
        int flag = roomService.updateSelective(room);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    @GetMapping("delete")
    public Result delete(String ids) {
        int flag = roomService.delete(ids);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    // 按楼栋+楼层（或按ID）查询房间列表，返回数组
    @PostMapping("query")
    public Result query(@RequestBody Room room) {
        List<Room> list = roomService.query(room);
        return Result.ok(list);
    }

    // 房间入住人数
    @GetMapping("query_liver_amount")
    public Result queryLiverAmount(Integer id) {
        return Result.ok(roomService.queryLiverAmount(id));
    }

    // 房间已入住的学生列表（床位点击查看学生信息用）
    @GetMapping("query_livers")
    public Result queryLivers(Integer id) {
        return Result.ok(roomService.queryLivers(id));
    }

    // 床位+1
    @GetMapping("capacity_plus_one")
    public Result capacityPlusOne(Integer id) {
        roomService.capacityPlusOne(id);
        return Result.ok();
    }

    // 床位-1
    @GetMapping("capacity_minus_one")
    public Result capacityMinusOne(Integer id) {
        roomService.capacityMinusOne(id);
        return Result.ok();
    }
}
