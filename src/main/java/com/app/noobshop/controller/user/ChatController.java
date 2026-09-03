package com.app.noobshop.controller.user;

import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.common.result.PageResult;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.entity.ChatMessage;
import com.app.noobshop.pojo.vo.CustomerServiceVO;
import com.app.noobshop.pojo.vo.ChatSessionVO;
import com.app.noobshop.service.ChatService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.noobshop.security.constant.SecurityExpressions.CHAT_ACCESS;

/**
 * 实时聊天相关接口
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "聊天接口", description = "实时聊天、会话列表相关接口")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/sessions")
    @PreAuthorize(CHAT_ACCESS)
    @Operation(summary = "获取当前用户的会话列表")
    public Result<List<ChatSessionVO>> getSessionList() {
        Long userId = Long.valueOf(BaseContext.getUserId());
        List<ChatSessionVO> list = chatService.getSessionList(userId);
        return Result.success(list);
    }

    @GetMapping("/customer-service/assign")
    @PreAuthorize(CHAT_ACCESS)
    @Operation(summary = "分配客服", description = "优先分配在线客服，无在线客服时返回一个启用客服作为兜底")
    public Result<CustomerServiceVO> assignCustomerService() {
        Long userId = Long.valueOf(BaseContext.getUserId());
        CustomerServiceVO customerService = chatService.assignCustomerService(userId);
        if (customerService == null) {
            return Result.error("暂无可用客服");
        }
        return Result.success(customerService);
    }

    @GetMapping("/history/{contactId}")
    @PreAuthorize(CHAT_ACCESS)
    @Operation(summary = "分页获取与某人的聊天历史")
    public Result<PageResult> getChatHistory(
            @PathVariable Long contactId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = Long.valueOf(BaseContext.getUserId());
        Page<ChatMessage> chatPage = chatService.getChatHistory(userId, contactId, page, size);
        
        PageResult result = PageResult.builder()
                .list(chatPage.getRecords())
                .total(chatPage.getTotal())
                .pageNum(page)
                .pageSize(size)
                .build();
        
        return Result.success(result);
    }

    @PostMapping("/clearUnread/{contactId}")
    @PreAuthorize(CHAT_ACCESS)
    @Operation(summary = "手动清除某会话的未读数")
    public Result<Void> clearUnread(@PathVariable Long contactId) {
        Long userId = Long.valueOf(BaseContext.getUserId());
        chatService.clearUnread(userId, contactId);
        return Result.success();
    }
}
