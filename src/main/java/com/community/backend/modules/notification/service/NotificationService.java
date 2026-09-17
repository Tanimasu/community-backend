package com.community.backend.modules.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.common.exception.BusinessException;
import com.community.backend.modules.notification.dto.NotificationResponse;
import com.community.backend.modules.notification.entity.Notification;
import com.community.backend.modules.notification.mapper.NotificationMapper;
import com.community.backend.modules.notification.mq.NotificationEvent;
import com.community.backend.modules.user.dto.UserBriefResponse;
import com.community.backend.modules.user.entity.User;
import com.community.backend.modules.user.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserService userService;

    public NotificationService(NotificationMapper notificationMapper, UserService userService) {
        this.notificationMapper = notificationMapper;
        this.userService = userService;
    }

    // 由 RabbitMQ 消费者调用
    public void createNotification(NotificationEvent event) {
        Notification notification = new Notification();
        notification.setReceiverId(event.receiverId());
        notification.setSenderId(event.senderId());
        notification.setType(event.type());
        notification.setTargetType(event.targetType());
        notification.setTargetId(event.targetId());
        notification.setContent(event.content());
        notification.setIsRead(false);
        notificationMapper.insert(notification);
    }

    public PageResult<NotificationResponse> listNotifications(Long userId, PageQuery pageQuery, boolean unreadOnly) {
        Page<Notification> page = notificationMapper.selectPage(pageQuery.toPage(),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .eq(unreadOnly, Notification::getIsRead, false)
                        .orderByDesc(Notification::getId));

        List<Notification> notifications = page.getRecords();
        Map<Long, User> senders = userService.getUserMap(
                notifications.stream().map(Notification::getSenderId).toList());
        List<NotificationResponse> list = notifications.stream()
                .map(notification -> NotificationResponse.from(
                        notification,
                        UserBriefResponse.from(senders.get(notification.getSenderId()))))
                .toList();
        return PageResult.of(page, list);
    }

    public long countUnread(Long userId) {
        return notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getReceiverId, userId)
                .eq(Notification::getIsRead, false));
    }

    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "通知不存在");
        }
        // 只能操作自己的通知
        if (!notification.getReceiverId().equals(userId)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "没有访问权限");
        }
        notificationMapper.update(new LambdaUpdateWrapper<Notification>()
                .set(Notification::getIsRead, true)
                .eq(Notification::getId, notificationId));
    }
}
