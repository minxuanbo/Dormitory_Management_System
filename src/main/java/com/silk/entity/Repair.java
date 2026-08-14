package com.silk.entity;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.silk.utils.Entity;
import java.util.Date;


/**
 * @author LindaSilk
 * @date 2021年3月13日, 周六
 * @description 报修工单
 */
public class Repair extends Entity{

	private Integer id;

	private String repItem;

	private String description;

	private Date repDate;

	private Integer stuId;

	private Integer buildingId;

	private Integer roomId;

	private Integer repStatus;		// 维修状态：待指派=0；待接单=1；维修中=2；已完成=3；已评价=4

	private String repMan;

	// === 新字段 ===
	private Integer urgency;		// 紧急程度：普通=0；紧急=1；非常紧急=2
	private Integer repairerId;		// 维修人员ID
	private String images;			// 报修图片路径，逗号分隔
	private String repairRecord;	// 维修记录
	private Integer rating;			// 评分 1-5
	private String feedback;		// 评价内容
	private Date assignedTime;		// 派单时间
	private Date acceptedTime;		// 接单时间
	private Date completedTime;		// 完成时间

	/**
	 * 报修学生信息（用于前端展示）
	 */
	private User user;

	// 维修人员信息（用于前端展示）
	private User repairer;

	// 楼栋信息（用于前端展示楼栋名称）
	private Building building;

	// 房间信息（用于前端展示楼层/门牌号）
	private Room room;

	// === 查询筛选字段（不参与实体存储） ===
	private String keyword;			// 关键字（匹配报修项目/故障描述）
	private String repStatuses;		// 多状态查询，逗号分隔，如 "3,4"
	private String startTime;		// 开始日期（yyyy-MM-dd）
	private String endTime;			// 结束日期（yyyy-MM-dd）


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getRepItem() {
		return repItem;
	}

	public void setRepItem(String repItem) {
		this.repItem = repItem;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getRepDate() {
		return repDate;
	}

	public void setRepDate(Date repDate) {
		this.repDate = repDate;
	}

	public Integer getStuId() {
		return stuId;
	}

	public void setStuId(Integer stuId) {
		this.stuId = stuId;
	}

	public Integer getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(Integer buildingId) {
		this.buildingId = buildingId;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public Integer getRepStatus() {
		return repStatus;
	}

	public void setRepStatus(Integer repStatus) {
		this.repStatus = repStatus;
	}

	public String getRepMan() {
		return repMan;
	}

	public void setRepMan(String repMan) {
		this.repMan = repMan;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// === 新字段 getter/setter ===

	public Integer getUrgency() {
		return urgency;
	}

	public void setUrgency(Integer urgency) {
		this.urgency = urgency;
	}

	public Integer getRepairerId() {
		return repairerId;
	}

	public void setRepairerId(Integer repairerId) {
		this.repairerId = repairerId;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getRepairRecord() {
		return repairRecord;
	}

	public void setRepairRecord(String repairRecord) {
		this.repairRecord = repairRecord;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Date getAssignedTime() {
		return assignedTime;
	}

	public void setAssignedTime(Date assignedTime) {
		this.assignedTime = assignedTime;
	}

	public Date getAcceptedTime() {
		return acceptedTime;
	}

	public void setAcceptedTime(Date acceptedTime) {
		this.acceptedTime = acceptedTime;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public User getRepairer() {
		return repairer;
	}

	public void setRepairer(User repairer) {
		this.repairer = repairer;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRepStatuses() {
		return repStatuses;
	}

	public void setRepStatuses(String repStatuses) {
		this.repStatuses = repStatuses;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}