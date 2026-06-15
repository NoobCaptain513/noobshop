package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.UserDetailDTO;
import com.app.noobshop.pojo.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<SysUser> {
    Result getUserDetail();

    Result updateUserDetail(UserDetailDTO userDetailDTO);

}
