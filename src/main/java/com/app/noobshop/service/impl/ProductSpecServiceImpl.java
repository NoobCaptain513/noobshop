package com.app.noobshop.service.impl;

import com.app.noobshop.pojo.entity.ProductSpec;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.noobshop.service.ProductSpecService;
import com.app.noobshop.mapper.ProductSpecMapper;
import org.springframework.stereotype.Service;

/**
* @author 20589
* @description 针对表【product_spec(商品规格表)】的数据库操作Service实现
* @createDate 2025-12-29 11:00:59
*/
@Service
public class ProductSpecServiceImpl extends ServiceImpl<ProductSpecMapper, ProductSpec>
    implements ProductSpecService{

}




