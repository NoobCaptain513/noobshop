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
     * 登录名
     */
    private String username;

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

    /**
     * 是否启用 1-启用 0-禁用
     */
    private Integer isEnable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Byte getUserType() {
        return userType;
    }

    public void setUserType(Byte userType) {
        this.userType = userType;
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public static UserInfoBuilder builder() {
        return new UserInfoBuilder();
    }

    public static class UserInfoBuilder {
        private String id;
        private String username;
        private String nickname;
        private String avatar;
        private String phone;
        private String openid;
        private Byte userType;
        private List<SysRole> sysRoleList;
        private Integer isEnable;

        public UserInfoBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UserInfoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserInfoBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public UserInfoBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public UserInfoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserInfoBuilder openid(String openid) {
            this.openid = openid;
            return this;
        }

        public UserInfoBuilder userType(Byte userType) {
            this.userType = userType;
            return this;
        }

        public UserInfoBuilder sysRoleList(List<SysRole> sysRoleList) {
            this.sysRoleList = sysRoleList;
            return this;
        }

        public UserInfoBuilder isEnable(Integer isEnable) {
            this.isEnable = isEnable;
            return this;
        }

        public UserInfo build() {
            UserInfo userInfo = new UserInfo();
            userInfo.setId(this.id);
            userInfo.setUsername(this.username);
            userInfo.setNickname(this.nickname);
            userInfo.setAvatar(this.avatar);
            userInfo.setPhone(this.phone);
            userInfo.setOpenid(this.openid);
            userInfo.setUserType(this.userType);
            userInfo.setSysRoleList(this.sysRoleList);
            userInfo.setIsEnable(this.isEnable);
            return userInfo;
        }
    }
}
