package com.app.noobshop.service.impl;

import com.app.noobshop.common.constant.DataConstant;
import com.app.noobshop.common.constant.MessageConstant;
import com.app.noobshop.common.mapstruct.CopyMapper;
import com.app.noobshop.common.result.Result;
import com.app.noobshop.mapper.FactoryInfoMapper;
import com.app.noobshop.pojo.entity.FactoryInfo;
import com.app.noobshop.pojo.vo.FactoryInfoVO;
import com.app.noobshop.service.FactoryInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class FactoryInfoServiceImpl extends ServiceImpl<FactoryInfoMapper, FactoryInfo> implements FactoryInfoService {

    @Resource
    private CopyMapper copyMapper;


    @Override
    public Result<Object> getFactoryInfo() {
        FactoryInfo factoryInfo = query().last("LIMIT " + DataConstant.ONE_INT).one();
        if (Objects.isNull(factoryInfo)){
            return Result.error(MessageConstant.TOM_CAT_ERROR);

        }
        FactoryInfoVO factoryInfoVO = copyMapper.factoryInfoToFactoryInfoVO(factoryInfo);
        return Result.success(factoryInfoVO);
    }
}
