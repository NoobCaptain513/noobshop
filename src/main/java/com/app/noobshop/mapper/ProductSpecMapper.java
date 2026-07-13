package com.app.noobshop.mapper;

import com.app.noobshop.pojo.entity.ProductSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 20589
* @description 针对表【product_spec(商品规格表)】的数据库操作Mapper
* @createDate 2025-12-29 11:00:59
* @Entity com.app.noobshop.ProductSpec
*/
public interface ProductSpecMapper extends BaseMapper<ProductSpec> {

    int updateStockByDelta(Long specId, Integer delta);
}




