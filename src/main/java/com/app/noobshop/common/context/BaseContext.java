package com.app.noobshop.common.context;

import com.app.noobshop.common.exception.BusinessException;
import com.app.noobshop.common.result.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 线程上下文工具类
 * 用于存储当前登录用户信息
 */
public class BaseContext {
    public static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();

    public static void setUserInfo(UserInfo userInfo) {
        threadLocal.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return threadLocal.get();
    }

    public static void removeUserInfo() {
        threadLocal.remove();
    }

    public static String getUserId() {
        UserInfo userInfo = threadLocal.get();
        if (Objects.isNull(userInfo)) {
            throw new BusinessException(com.app.noobshop.common.constant.MessageConstant.USER_NOT_LOGIN);
        }
        String userId = userInfo.getId();
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException(com.app.noobshop.common.constant.MessageConstant.USER_NOT_LOGIN);
        }
        return userId;
    }
}
