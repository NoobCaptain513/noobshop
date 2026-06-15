package com.app.noobshop.aop.aspect.business;


import com.app.noobshop.common.result.Result;
import com.app.noobshop.infrastructure.redis.connect.RedisConnector;
import com.app.noobshop.infrastructure.redis.generator.RedisKeyGenerator;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class RemoveBannerRedisCacheAspect {

    @Pointcut(value = "@annotation(com.app.noobshop.aop.annotation.business.RemoveBannerRedisCacheAnnotation)")
    public void pointcut(){}

    @AfterReturning(pointcut = "pointcut()" ,returning = "result")
    public void afterReturnSuccess(Result<?> result){
        if (Objects.isNull(result)){
            return;
        }
        if (!result.getSuccess()) {
            return;
        }
        RedisConnector.delete(RedisKeyGenerator.banner());

    }
}
