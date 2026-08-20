package com.silk.controller;

import com.silk.entity.Notification;
import com.silk.entity.User;
import com.silk.service.NotificationService;
import com.silk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 站内通知
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 安全转换 Map 参数为 Integer（兼容字符串数字）
     */
    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            String s = String.valueOf(v).trim();
            return s.isEmpty() ? null : Integer.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("my")
    public Map<String, Object> my(Notification notification, HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        notification.setUserId(param.getId());
        return Result.ok(notificationService.query(notification));
    }

    @PostMapping("unread_count")
    public Result unreadCount(HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        return Result.ok(notificationService.unreadCount(param.getId()));
    }

    @PostMapping("mark_read")
    public Result markRead(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        Integer id = toInt(params.get("id"));
        if (id == null) {
            return Result.fail("参数不完整");
        }
        int flag = notificationService.markRead(id, param.getId());
        if (flag > 0) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("mark_unread")
    public Result markUnread(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        Integer id = toInt(params.get("id"));
        if (id == null) {
            return Result.fail("参数不完整");
        }
        int flag = notificationService.markUnread(id, param.getId());
        if (flag > 0) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("mark_all_read")
    public Result markAllRead(HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        notificationService.markAllRead(param.getId());
        return Result.ok();
    }

    @GetMapping("delete")
    public Result delete(String ids, HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        int row = 0;
        for (String s : ids.split(",")) {
            if (s != null && !s.trim().isEmpty()) {
                row += notificationService.delete(Integer.parseInt(s.trim()), param.getId());
            }
        }
        if (row > 0) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("publish")
    public Result publish(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        User param = (User) request.getAttribute("user");
        if (param.getUserType() != 2) {
            return Result.fail("无权限操作");
        }

        String target = (String) params.get("target");   // all / student / repairer
        String head = (String) params.get("head");
        String content = (String) params.get("content");
        Integer type = toInt(params.get("type"));

        if (target == null || target.trim().isEmpty()) {
            return Result.fail("请选择接收对象");
        }
        if (head == null || head.trim().isEmpty()) {
            return Result.fail("请填写通知标题");
        }

        switch (target) {
            case "student":
                notificationService.notifyByUserType(0, head, content, type);
                break;
            case "repairer":
                notificationService.notifyByUserType(1, head, content, type);
                break;
            case "all":
                notificationService.notifyByUserType(0, head, content, type);
                notificationService.notifyByUserType(1, head, content, type);
                notificationService.notifyByUserType(2, head, content, type);
                break;
            default:
                return Result.fail("接收对象不合法");
        }
        return Result.ok("发布成功");
    }
}
