package com.silk.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silk.entity.Notification;
import com.silk.mapper.NotificationMapper;
import com.silk.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 给单个用户发送通知（系统通知 senderId 为 null）
     */
    public void notify(Integer userId, String head, String content, Integer type, Integer relId) {
        if (userId == null) {
            return;
        }
        Notification n = new Notification();
        n.setSenderId(null);
        n.setUserId(userId);
        n.setHead(head);
        n.setContent(content);
        n.setType(type == null ? 0 : type);
        n.setIsRead(0);
        n.setCreateTime(new Date());
        n.setRelId(relId);
        notificationMapper.insert(n);
    }

    /**
     * 按角色给某类用户群发通知（userType：学生=0；维修人员=1；管理员=2）
     */
    public void notifyByUserType(Integer userType, String head, String content, Integer type) {
        if (userType == null) {
            return;
        }
        List<Integer> ids = userMapper.queryIdsByType(userType);
        for (Integer id : ids) {
            notify(id, head, content, type, null);
        }
    }

    public PageInfo<Notification> query(Notification notification) {
        if (notification != null && notification.getPage() != null) {
            PageHelper.startPage(notification.getPage(), notification.getLimit());
        }
        return new PageInfo<>(notificationMapper.query(notification));
    }

    public int unreadCount(Integer userId) {
        return notificationMapper.unreadCount(userId);
    }

    public int markRead(Integer id, Integer userId) {
        return notificationMapper.markRead(id, userId);
    }

    public int markUnread(Integer id, Integer userId) {
        return notificationMapper.markUnread(id, userId);
    }

    public int markAllRead(Integer userId) {
        return notificationMapper.markAllRead(userId);
    }

    public int delete(Integer id, Integer userId) {
        return notificationMapper.delete(id, userId);
    }
}
