package com.app.noobshop.mapper;

import com.app.noobshop.pojo.entity.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
    List<Coupon> selectCouponWithMutexCroupAndScopeDetail();
}
