package com.silk.entity;

import com.silk.utils.Entity;

/**
 * 楼栋实体（如：国光公寓13栋）
 */
public class Building extends Entity {

    private Integer id;                 // 楼栋编号
    private String buildingName;        // 楼栋名称
    private Integer floorNum;           // 总层数
    private Integer liverGender;        // 入住学生性别：女=0；男=1
    private Integer managerId;          // 维修人员ID（该维修人员负责本楼栋）

    private User user;                  // 维修人员信息（关联 tb_user）

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Integer getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(Integer floorNum) {
        this.floorNum = floorNum;
    }

    public Integer getLiverGender() {
        return liverGender;
    }

    public void setLiverGender(Integer liverGender) {
        this.liverGender = liverGender;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
