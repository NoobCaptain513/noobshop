package com.app.noobshop.service.impl;

import com.app.noobshop.mapper.MqConsumerFailedMsgMapper;
import com.app.noobshop.pojo.entity.MqConsumerFailedMsg;
import com.app.noobshop.service.MqConsumerFailedMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MqConsumerFailedMsgServiceImpl extends ServiceImpl<MqConsumerFailedMsgMapper, MqConsumerFailedMsg> implements MqConsumerFailedMsgService {
}
