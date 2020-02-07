package com.yuanjun.learnproject.databaseshard.annotion;

import com.yuanjun.learnproject.constant.StrategyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuanjun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableShard {

    String tableName();

    //暂时只支持单参数
    String paramName();

    StrategyType tableStrategyType();

}
