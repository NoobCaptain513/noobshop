package com.app.noobshop.controller.user;

import com.app.noobshop.aop.annotation.business.SaveCartRedisCacheToMysqlAnnotation;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.CartDTO;
import com.app.noobshop.pojo.dto.CartProductDTO;
import com.app.noobshop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.app.noobshop.security.constant.SecurityExpressions.CART_READ;
import static com.app.noobshop.security.constant.SecurityExpressions.CART_WRITE;

@RequestMapping("/api/cart")
@RestController
@Tag(name = "购物车管理")
public class CartController {
    @Resource
    private CartService cartService;

    /**
     * 获取购物车列表
     *
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize(CART_READ)
    @Operation(summary = "查询购物车列表", description = "获取当前用户的购物车商品列表")
    public Result getCartList() {
        return cartService.getCartList();
    }

    /**
     * 添加商品到购物车
     *
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize(CART_WRITE)
    @Operation(summary = "新增购物车商品", description = "将商品添加到当前用户的购物车")
    @SaveCartRedisCacheToMysqlAnnotation
    public Result addProductToCart(@RequestBody CartProductDTO cartProductDTO) {
        return cartService.addProductToCart(cartProductDTO);
    }

    /**
     * 清空购物车
     *冗余接口(暂定)
     * @return
     */
    @DeleteMapping("/clear")
    @PreAuthorize(CART_WRITE)
    @Operation(summary = "清空购物车", description = "清空当前用户的购物车所有商品（冗余接口，暂定）")
    @SaveCartRedisCacheToMysqlAnnotation
    public Result clearCart() {
        return cartService.clearCart();
    }

    /**
     * 批量删除购物车商品(单个+批量)
     * 冗余接口(暂定)
     *
     * @return
     */
    @DeleteMapping("/products")
    @PreAuthorize(CART_WRITE)
    @Operation(summary = "删除购物车商品", description = "支持单个或批量删除购物车商品（冗余接口，暂定）")
    @SaveCartRedisCacheToMysqlAnnotation
    public Result deleteCartProduct(@RequestParam("productIds") String productIds, @RequestParam("specIds") String specIds) {
        return cartService.deleteCartProduct(productIds, specIds);
    }

    /**
     * 将前端的购物车数据(List)更新到数据库
     *
     * @param cartDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize(CART_WRITE)
    @Operation(summary = "更新购物车数据", description = "将前端购物车数据列表同步更新到数据库")
    @SaveCartRedisCacheToMysqlAnnotation
    public Result mergeCart(@RequestBody CartDTO cartDTO) {
        return cartService.mergeCart(cartDTO);
    }
}
