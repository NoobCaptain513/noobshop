package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.dto.OrderDTO;
import com.app.noobshop.pojo.dto.ScrollQueryDTO;
import com.app.noobshop.pojo.emums.OrderStatusEnum;
import com.app.noobshop.pojo.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface OrderService extends IService<Order> {

    Result insertOrder(@NotNull  OrderDTO orderDTO);

    Result getOrderList(Integer pageNum, Integer pageSize, String status);

    Result getOrderDesc(@NotNull String orderNo);

    Result cancelOrder(@NotBlank String orderNo,String cancelReason);

    Result confirmOrderReceipt(@NotBlank String orderNo);

    Result deleteOrder(@NotBlank String orderNo);

    Result getOrderFreight(@NotBlank String productIds, @NotBlank String addressId);

    Result getOrderLogistics(@NotBlank String orderNo);

    Result getOrderByScrollQuery(ScrollQueryDTO scrollQueryDTO);

    Result searchOrderByCondition(@NotBlank String SearchCondition);

    Result getOrderListByPage(@NotBlank String pageName);

    Result paySuccessOrder(@NotBlank String orderNo);

    Boolean updateOrderStatus(Long orderId, String orderNo, @NotNull OrderStatusEnum orderStatusEnum);
}
