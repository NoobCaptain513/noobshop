package com.app.noobshop.service.impl;

import com.app.noobshop.common.constant.MessageConstant;
import com.app.noobshop.common.context.BaseContext;
import com.app.noobshop.common.mapstruct.CopyMapper;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.mapper.SysUserMapper;
import com.app.noobshop.pojo.dto.UserDetailDTO;
import com.app.noobshop.pojo.entity.SysUser;
import com.app.noobshop.pojo.vo.UserDetailVO;
import com.app.noobshop.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {
    @Resource
    private CopyMapper copyMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    /**
     * 获取用户详情
     *
     * @return
     */
    @Override
    public Result getUserDetail() {
        String userId = BaseContext.getUserId();
        SysUser user = sysUserMapper.getSysUserByUserIdWithRolesAndPermissions(Long.valueOf(userId));
        UserDetailVO userDetailVO = copyMapper.sysUserToUserDetailVO(user);
        return Result.success(userDetailVO);
    }

    /**
     * 修改用户详情
     *
     * @param userDetailDTO
     * @return
     */
    @Override
    public Result updateUserDetail(UserDetailDTO userDetailDTO) {
        String userId = BaseContext.getUserId();
        if (StringUtils.isBlank(userDetailDTO.getNickname())) {
            return Result.error(MessageConstant.USER_NAME_NOT_NULL);
        }
        boolean isSuccess = lambdaUpdate().eq(SysUser::getId, userId).set(SysUser::getNickname, userDetailDTO.getNickname())
                .set(SysUser::getAvatar, userDetailDTO.getAvatar()).set(SysUser::getPhone, userDetailDTO.getPhone()).update();
        if (!isSuccess) {
            return Result.error(MessageConstant.SQL_MESSAGE_SAVE_ERROR);
        }
        return Result.success();
    }
}
