package com.app.noobshop.mapper;

import com.app.noobshop.pojo.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser getSysUserByNameWithRolesAndPermissions(String username);

    SysUser getSysUserByUserIdWithRolesAndPermissions(Long userId);

    SysUser getSysUserByOpenidWithRolesAndPermissions(String openid);

    void insertSysUserConnectSysRole(Long userId, int roleId);

    List<SysUser> listCustomerServiceUsers(@Param("excludeUserId") Long excludeUserId);
}


