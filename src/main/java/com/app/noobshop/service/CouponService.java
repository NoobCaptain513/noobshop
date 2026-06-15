package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.CouponCreateDTO;
import com.app.noobshop.pojo.entity.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface CouponService extends IService<Coupon> {

    Result<?> saveCouponAdmin(@Valid @NotNull CouponCreateDTO couponCreateDTO);

    void updateCouponRedisCache();
}
