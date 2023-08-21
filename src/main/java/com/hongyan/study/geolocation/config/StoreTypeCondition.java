package com.hongyan.study.geolocation.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author zy
 * @version 1.0
 * @date Created in 2023/8/17 11:19 AM
 * @description 自定义的条件判断类，解决枚举类型无法使用到注解上的问题
 */
public class StoreTypeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return StoreTypeEnum.isExist(context.getEnvironment().getProperty("paraview.geo.storetype"));
    }
}
