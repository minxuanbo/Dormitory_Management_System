package com.silk.controller;

import com.github.pagehelper.PageInfo;
import com.silk.entity.Building;
import com.silk.service.BuildingService;
import com.silk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 楼栋管理接口（对接前端 page/building/list.html、page/room/list.html）
 */
@RestController
@RequestMapping("/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @PostMapping("create")
    public Result create(@RequestBody Building building) {
        if (buildingService.detail(building.getId()) != null) {
            return Result.fail("该楼栋编号已存在");
        }
        int flag = buildingService.create(building);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    @PostMapping("update")
    public Result update(@RequestBody Building building) {
        int flag = buildingService.updateSelective(building);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    @GetMapping("delete")
    public Result delete(String ids) {
        int flag = buildingService.delete(ids);
        return flag > 0 ? Result.ok() : Result.fail();
    }

    @PostMapping("query")
    public Map<String, Object> query(Building building) {            // layui 表格等以表单参数请求
        PageInfo<Building> pageInfo = buildingService.query(building);
        return Result.ok(pageInfo);
    }

    // 查询楼栋总层数（房间管理页加载楼层使用）
    @GetMapping("query_floor_num")
    public Result queryFloorNum(Integer id) {
        Integer floorNum = buildingService.queryFloorNum(id);
        Map<String, Object> map = new HashMap<>();
        map.put("floorNum", floorNum == null ? 0 : floorNum);
        return Result.ok(map);
    }

    // 楼栋入住率与性别（房间管理页图表使用），返回 [入住率(%), 性别]
    @GetMapping("occupancy_rate_and_gender")
    public Result occupancyRateAndGender(Integer buildingId) {
        return Result.ok(buildingService.occupancyRateAndGender(buildingId));
    }
}
