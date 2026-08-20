package com.silk.mapper;

import com.silk.entity.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NotificationMapper {

    // 新增通知
    public int insert(Notification notification);

    // 条件分页查询（按接收人、已读状态过滤，按ID倒序）
    public List<Notification> query(Notification notification);

    // 未读数
    public int unreadCount(@Param("userId") Integer userId);

    // 单条标记已读（带接收人校验，防越权）
    public int markRead(@Param("id") Integer id, @Param("userId") Integer userId);

    // 单条标记未读（带接收人校验，防越权）
    public int markUnread(@Param("id") Integer id, @Param("userId") Integer userId);

    // 全部标记已读
    public int markAllRead(@Param("userId") Integer userId);

    // 删除单条（带接收人校验）
    public int delete(@Param("id") Integer id, @Param("userId") Integer userId);
}
