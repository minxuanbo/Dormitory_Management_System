package com.silk.entity;

import com.silk.utils.Entity;

/**
 * 房间实体
 * 房间ID编码规则：楼栋ID*10000 + 楼层*100 + 房号
 * 例：13栋7楼01房 = 13*10000 + 7*100 + 1 = 130701，门牌号(brand)为 0701
 */
public class Room extends Entity {

    private Integer id;                 // 房间ID编码
    private Integer buildingId;         // 楼栋ID
    private Integer floor;              // 楼层
    private String brand;               // 门牌号（如 0701）
    private Integer roomCapacity;       // 房间容量（床位数）
    private Integer roomType;           // 0=学生房间 1=宿管 2=后勤办公室 3=招待所 4=小卖部 5=杂物间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Integer buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public Integer getRoomType() {
        return roomType;
    }

    public void setRoomType(Integer roomType) {
        this.roomType = roomType;
    }
}
