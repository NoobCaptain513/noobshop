package com.app.noobshop.pojo.vo;

import com.app.noobshop.pojo.entity.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailVO {
    private String id;
    private String nickname;
    private String avatar;
    private String phone;
    private String openid;
    private Byte userType;
    private List<SysRole> sysRoleList;
}
