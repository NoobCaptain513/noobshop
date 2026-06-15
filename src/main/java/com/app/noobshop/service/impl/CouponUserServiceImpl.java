package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.CouponUserMapper;
import com.app.noobshop.pojo.entity.CouponUser;
import com.app.noobshop.service.CouponUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CouponUserServiceImpl extends ServiceImpl<CouponUserMapper, CouponUser> implements CouponUserService {
}
