package com.app.noobshop.service;

import com.app.noobshop.common.result.Result;
import com.app.noobshop.pojo.entity.FactoryInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FactoryInfoService extends IService<FactoryInfo> {

    Result<Object> getFactoryInfo();

}
