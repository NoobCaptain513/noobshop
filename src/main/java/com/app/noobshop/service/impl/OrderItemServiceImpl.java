package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.OrderItemMapper;
import com.app.noobshop.pojo.entity.OrderItem;
import com.app.noobshop.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

}
