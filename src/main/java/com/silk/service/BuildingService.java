package com.silk.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silk.entity.Building;
import com.silk.mapper.BuildingMapper;
import com.silk.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    @Autowired
    private BuildingMapper buildingMapper;

    @Autowired
    private RoomMapper roomMapper;

    // 条件分页查询
    public PageInfo<Building> query(Building building) {
        if (building != null && building.getPage() != null) {
            PageHelper.startPage(building.getPage(), building.getLimit());
        }
        List<Building> list = buildingMapper.query(building);
        return new PageInfo<>(list);
    }

    // 查询楼栋总层数
    public Integer queryFloorNum(Integer id) {
        return buildingMapper.queryFloorNum(id);
    }

    // 入住率(%)与性别，返回 [入住率, 性别]
    public Object[] occupancyRateAndGender(Integer buildingId) {
        Integer liverCount = buildingMapper.queryLiverCount(buildingId);
        Integer totalCapacity = buildingMapper.queryTotalCapacity(buildingId);
        if (liverCount == null) liverCount = 0;
        if (totalCapacity == null) totalCapacity = 0;
        double rate = totalCapacity == 0 ? 0 : Math.round(liverCount * 10000.0 / totalCapacity) / 100.0;
        Integer gender = null;
        Building b = buildingMapper.detail(buildingId);
        if (b != null) {
            gender = b.getLiverGender();
        }
        return new Object[]{rate, gender};
    }

    public int create(Building building) {
        return buildingMapper.insert(building);
    }

    public int updateSelective(Building building) {
        return buildingMapper.updateSelective(building);
    }

    // 删除楼栋（同时删除其下房间）
    public int delete(String ids) {
        roomMapper.deleteByBuildingIds(ids);
        return buildingMapper.deleteByIds(ids);
    }

    public Building detail(Integer id) {
        return buildingMapper.detail(id);
    }
}
