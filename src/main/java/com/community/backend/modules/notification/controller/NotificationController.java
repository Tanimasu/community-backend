package com.community.backend.modules.notification.controller;

import com.community.backend.common.ApiResponse;
import com.community.backend.common.PageQuery;
import com.community.backend.common.PageResult;
import com.community.backend.modules.notification.dto.NotificationResponse;
import com.community.backend.modules.notification.service.NotificationService;
import com.community.backend.security.LoginUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<PageResult<NotificationResponse>> listNotifications(
            @AuthenticationPrincipal LoginUser loginUser,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        return ApiResponse.success(
                notificationService.listNotifications(loginUser.id(), new PageQuery(page, pageSize), unreadOnly));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnread(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.success(notificationService.countUnread(loginUser.id()));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@AuthenticationPrincipal LoginUser loginUser, @PathVariable Long id) {
        notificationService.markAsRead(loginUser.id(), id);
        return ApiResponse.success(null);
    }
}
