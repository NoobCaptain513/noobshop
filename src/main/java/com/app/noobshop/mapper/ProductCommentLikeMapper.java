package com.app.noobshop.mapper;

import com.app.noobshop.pojo.entity.ProductCommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductCommentLikeMapper  extends BaseMapper<ProductCommentLike> {
    int batchUpdate (@Param("list") List<ProductCommentLike> productCommentLikeList);
}
