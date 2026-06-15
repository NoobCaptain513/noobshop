package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.ChatMessageMapper;
import com.app.noobshop.mapper.ChatSessionMapper;
import com.app.noobshop.mapper.SysUserMapper;
import com.app.noobshop.infrastructure.netty.manager.UserChannelManager;
import com.app.noobshop.pojo.entity.ChatMessage;
import com.app.noobshop.pojo.entity.ChatSession;
import com.app.noobshop.pojo.entity.SysUser;
import com.app.noobshop.pojo.vo.CustomerServiceVO;
import com.app.noobshop.pojo.vo.ChatSessionVO;
import com.app.noobshop.service.ChatService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天服务实现类
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    private final ChatSessionMapper chatSessionMapper;
    private final SysUserMapper sysUserMapper;
    private final UserChannelManager userChannelManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatMessage saveAndGetMessage(Long fromUserId, Long toUserId, String content, Integer msgType, Long productId) {
        // 1. 保存消息
        ChatMessage message = ChatMessage.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .content(content)
                .msgType(msgType)
                .productId(productId)
                .isRead(0)
                .createTime(LocalDateTime.now())
                .build();
        this.save(message);

        // 2. 更新或创建会话
        Long userId = Math.min(fromUserId, toUserId);
        Long contactId = Math.max(fromUserId, toUserId);

        ChatSession session = chatSessionMapper.selectOne(new LambdaQueryWrapper<ChatSession>()
                .eq(ChatSession::getUserId, userId)
                .eq(ChatSession::getContactId, contactId));

        if (session == null) {
            session = ChatSession.builder()
                    .userId(userId)
                    .contactId(contactId)
                    .lastMsgContent(content)
                    .lastMsgTime(LocalDateTime.now())
                    .unreadCountA(fromUserId.equals(contactId) ? 1 : 0) // 如果发送者是较大ID，则较小ID(A)未读+1
                    .unreadCountB(fromUserId.equals(userId) ? 1 : 0)    // 如果发送者是较小ID，则较大ID(B)未读+1
                    .build();
            chatSessionMapper.insert(session);
        } else {
            LambdaUpdateWrapper<ChatSession> updateWrapper = new LambdaUpdateWrapper<ChatSession>()
                    .eq(ChatSession::getId, session.getId())
                    .set(ChatSession::getLastMsgContent, content)
                    .set(ChatSession::getLastMsgTime, LocalDateTime.now());

            if (fromUserId.equals(userId)) {
                // 发送者是 A，则 B 未读数 + 1
                updateWrapper.setSql("unread_count_b = unread_count_b + 1");
            } else {
                // 发送者是 B，则 A 未读数 + 1
                updateWrapper.setSql("unread_count_a = unread_count_a + 1");
            }
            chatSessionMapper.update(null, updateWrapper);
        }

        return message;
    }

    @Override
    public List<ChatSessionVO> getSessionList(Long userId) {
        return chatSessionMapper.selectSessionList(userId);
    }

    @Override
    public Page<ChatMessage> getChatHistory(Long userId, Long contactId, Integer page, Integer size) {
        Page<ChatMessage> chatPage = new Page<>(page, size);
        return this.page(chatPage, new LambdaQueryWrapper<ChatMessage>()
                .and(wrapper -> wrapper
                        .eq(ChatMessage::getFromUserId, userId).eq(ChatMessage::getToUserId, contactId)
                        .or()
                        .eq(ChatMessage::getFromUserId, contactId).eq(ChatMessage::getToUserId, userId))
                .orderByDesc(ChatMessage::getCreateTime));
    }

    @Override
    public void clearUnread(Long userId, Long contactId) {
        Long uid = Math.min(userId, contactId);
        Long cid = Math.max(userId, contactId);

        LambdaUpdateWrapper<ChatSession> updateWrapper = new LambdaUpdateWrapper<ChatSession>()
                .eq(ChatSession::getUserId, uid)
                .eq(ChatSession::getContactId, cid);

        if (userId.equals(uid)) {
            updateWrapper.set(ChatSession::getUnreadCountA, 0);
        } else {
            updateWrapper.set(ChatSession::getUnreadCountB, 0);
        }
        chatSessionMapper.update(null, updateWrapper);
    }

    @Override
    public CustomerServiceVO assignCustomerService(Long currentUserId) {
        List<SysUser> customerServiceUsers = sysUserMapper.listCustomerServiceUsers(currentUserId);
        if (customerServiceUsers.isEmpty()) {
            return null;
        }

        SysUser selected = customerServiceUsers.stream()
                .filter(user -> userChannelManager.isOnline(user.getId()))
                .findFirst()
                .orElse(customerServiceUsers.get(0));

        return CustomerServiceVO.builder()
                .id(selected.getId())
                .nickname(selected.getNickname())
                .avatar(selected.getAvatar())
                .online(userChannelManager.isOnline(selected.getId()))
                .build();
    }
}
