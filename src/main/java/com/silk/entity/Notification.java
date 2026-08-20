package com.silk.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.silk.utils.Entity;

import java.util.Date;

/**
 * 站内通知
 */
public class Notification extends Entity {

    private Integer id;
    private Integer senderId;   // 发送人ID（系统通知为NULL）
    private Integer userId;     // 接收人ID
    private String head;        // 通知标题
    private String content;     // 通知内容
    private Integer type;       // 通知类型：一般=0；重要=1
    private Integer isRead;     // 已读状态：未读=0；已读=1
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;    // 创建时间
    private Integer relId;      // 关联工单ID（可为空）

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getRelId() {
        return relId;
    }

    public void setRelId(Integer relId) {
        this.relId = relId;
    }
}
