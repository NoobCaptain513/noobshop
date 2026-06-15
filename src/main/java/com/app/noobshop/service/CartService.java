package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.CartDTO;
import com.app.noobshop.pojo.dto.CartProductDTO;
import com.app.noobshop.pojo.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CartService extends IService<Cart> {

    Result getCartList();

    Result addProductToCart(CartProductDTO cartProductDTO);

    Result clearCart();

    Result deleteCartProduct(String productIds, String specIds);

    Result mergeCart(CartDTO cartDTO);

    void syncCartToMysql(String userId);
}


