package com.app.noobshop.controller.admin;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.CouponCreateDTO;
import com.app.noobshop.service.CouponService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.app.noobshop.security.constant.SecurityExpressions.COUPON_RELEASE;

@RestController
@RequestMapping("/api")
@Tag(name = "优惠劵管理")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;


    @PostMapping("/admin/coupon/release")
    @PreAuthorize(COUPON_RELEASE)
    public Result<?> saveCouponAdmin(@RequestBody @Valid @NotNull CouponCreateDTO couponCreateDTO) {
        return couponService.saveCouponAdmin(couponCreateDTO);
    }


}
