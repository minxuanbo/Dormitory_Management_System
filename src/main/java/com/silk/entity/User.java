package com.silk.entity;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import com.silk.utils.Entity;
import java.util.Date;


/**
 * @author LindaSilk
 * @date 2021年3月07日, 周日
 * @description 用户信息
 */
public class User extends Entity{

	private Integer id;
	private String userPwd;
	private String userName;
	private Integer gender;			// 用户性别：女=0；男=1
	private String phone;			// 联系电话
	private String email;
	private Integer roomId;			// 房间ID（关联 tb_room.id）
	private Integer userType;		// 角色类型：学生=0；维修人员=1；管理员=2

	// 宿舍信息（查询时JOIN展示，不参与存储）
	private String buildingName;	// 楼栋名称
	private String roomBrand;		// 房间门牌号（如 0701）
	private Integer roomFloor;		// 楼层

	// 负责楼栋（维修人员：由 tb_building.manager_id 反查，多栋以"、"拼接，不参与存储）
	private String managerBuildings;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@JsonIgnore						// 返回Json时不显示密码
	public String getUserPwd() {
		return userPwd;
	}

	@JsonProperty					// 提交时能够使用
	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getRoomBrand() {
		return roomBrand;
	}

	public void setRoomBrand(String roomBrand) {
		this.roomBrand = roomBrand;
	}

	public Integer getRoomFloor() {
		return roomFloor;
	}

	public void setRoomFloor(Integer roomFloor) {
		this.roomFloor = roomFloor;
	}

	public String getManagerBuildings() {
		return managerBuildings;
	}

	public void setManagerBuildings(String managerBuildings) {
		this.managerBuildings = managerBuildings;
	}

	public User() {
	}

	public User(Integer id, String userPwd, String userName, Integer gender, String email, Integer roomId, Integer userType) {
		this.id = id;
		this.userPwd = userPwd;
		this.userName = userName;
		this.gender = gender;
		this.email = email;
		this.roomId = roomId;
		this.userType = userType;
	}
}
