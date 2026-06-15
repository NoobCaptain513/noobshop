package com.app.noobshop.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.app.noobshop.pojo.entity.SysRole;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户 ID
     */
    private String id;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像 URL
     */
    private String avatar;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 微信用户唯一标识
     */
    private String openid;

    /**
     * 用户类型：1-系统管理员 2-普通管理员 3-普通买家
     */
    private Byte userType;

    /**
     * 用户角色列表
     */
    private List<SysRole> sysRoleList;



}
