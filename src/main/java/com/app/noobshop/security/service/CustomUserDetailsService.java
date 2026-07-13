package com.app.noobshop.security.service;

import com.app.noobshop.common.result.UserInfo;
import com.app.noobshop.infrastructure.redis.connect.RedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import com.app.noobshop.mapper.SysUserMapper;
import com.app.noobshop.pojo.entity.SysPermission;
import com.app.noobshop.pojo.entity.SysRole;
import com.app.noobshop.pojo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 UserDetailsService - 替代 Shiro 的 CustomRealm
 * 用于加载用户认证和授权信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    /**
     * 根据用户ID加载用户信息
     * Spring Security 调用此方法进行认证
     *
     * @param userId 用户ID（username 参数实际存储的是 userId）
     * @return UserDetails
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.debug("加载用户信息，userId: {}", userId);

        // 1. 尝试从 Redis 获取用户信息
        SysUser user = loadUserFromRedis(userId);

        // 2. Redis 不存在，从数据库加载
        if (user == null) {
            user = loadUserFromDatabase(userId);
        }

        // 3. 用户不存在
        if (user == null) {
            log.warn("用户不存在，userId: {}", userId);
            throw new UsernameNotFoundException("用户不存在: " + userId);
        }

        // 4. 检查用户是否启用
        if (user.getIsEnable() != null && user.getIsEnable().getNumber() == 0) {
            log.warn("用户已被禁用，userId: {}", userId);
            throw new UsernameNotFoundException("用户已被禁用: " + userId);
        }

        // 5. 构建权限列表
        List<GrantedAuthority> authorities = buildAuthorities(user);

        // 6. 构建 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
                userId,
                user.getPassword() != null ? user.getPassword() : "",
                user.getIsEnable() == null || user.getIsEnable().getNumber() != 0,
                true, true, true,
                authorities
        );
    }

    /**
     * 从 Redis 加载用户信息
     */
    private SysUser loadUserFromRedis(String userId) {
        try {
            String userKey = RedisKeyGenerator.loginUser(Long.parseLong(userId));
            UserInfo userInfo = RedisConnector.getHashField(userKey, SysUser.Fields.userInfo, UserInfo.class);

            if (userInfo != null) {
                return convertToSysUser(userInfo);
            }
        } catch (Exception e) {
            log.warn("从 Redis 加载用户信息失败，userId: {}", userId, e);
        }
        return null;
    }

    /**
     * 从数据库加载用户信息
     */
    private SysUser loadUserFromDatabase(String userId) {
        try {
            // 使用已存在的方法加载用户及其角色权限
            SysUser user = sysUserMapper.getSysUserByUserIdWithRolesAndPermissions(Long.parseLong(userId));
            if (user != null) {
                // 缓存到 Redis
                cacheUserToRedis(user);
            }
            return user;
        } catch (Exception e) {
            log.error("从数据库加载用户信息失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 将用户信息缓存到 Redis
     */
    private void cacheUserToRedis(SysUser user) {
        try {
            String userKey = RedisKeyGenerator.loginUser(user.getId());
            UserInfo userInfo = buildUserInfo(user);
            RedisConnector.opsForHash().put(userKey, SysUser.Fields.userInfo, userInfo);
        } catch (Exception e) {
            log.warn("缓存用户信息到 Redis 失败，userId: {}", user.getId(), e);
        }
    }

    /**
     * 构建 UserInfo
     */
    private UserInfo buildUserInfo(SysUser user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId().toString());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setOpenid(user.getOpenid());
        userInfo.setPhone(user.getPhone());
        userInfo.setUserType(user.getUserType());
        userInfo.setIsEnable(user.getIsEnable() != null ? user.getIsEnable().getNumber() : 1);
        return userInfo;
    }

    /**
     * 将 UserInfo 转换为 SysUser
     */
    private SysUser convertToSysUser(UserInfo userInfo) {
        SysUser user = new SysUser();
        user.setId(Long.parseLong(userInfo.getId()));
        user.setUsername(userInfo.getUsername());
        user.setNickname(userInfo.getNickname());
        user.setAvatar(userInfo.getAvatar());
        user.setOpenid(userInfo.getOpenid());
        user.setPhone(userInfo.getPhone());
        user.setUserType(userInfo.getUserType());
        return user;
    }

    /**
     * 构建权限列表
     */
    private List<GrantedAuthority> buildAuthorities(SysUser user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限
        if (user.getSysRoleList() != null) {
            for (SysRole role : user.getSysRoleList()) {
                if (role.getRoleCode() != null) {
                    // Spring Security 角色需要以 ROLE_ 开头
                    String roleCode = role.getRoleCode();
                    if (!roleCode.startsWith("ROLE_")) {
                        roleCode = "ROLE_" + roleCode;
                    }
                    authorities.add(new SimpleGrantedAuthority(roleCode));
                    log.debug("添加角色权限: {}", roleCode);
                }
            }
        }

        // 添加操作权限
        if (user.getSysPermissionList() != null) {
            for (SysPermission permission : user.getSysPermissionList()) {
                if (permission.getPermCode() != null) {
                    authorities.add(new SimpleGrantedAuthority(permission.getPermCode()));
                    log.debug("添加操作权限: {}", permission.getPermCode());
                }
            }
        }

        return authorities;
    }

    /**
     * 根据 Token 加载用户信息（供过滤器使用）
     */
    public UserDetails loadUserByToken(String userId, String token) {
        return loadUserByUsername(userId);
    }
}
