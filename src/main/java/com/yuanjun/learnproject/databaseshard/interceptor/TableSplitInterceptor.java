package com.yuanjun.learnproject.databaseshard.interceptor;


import com.yuanjun.learnproject.databaseshard.annotion.TableShard;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy.TableStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 * @author yuanjun
 */
@Slf4j
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })
//@Component
public class TableSplitInterceptor implements Interceptor {

    @Resource
    private TableStrategyService tableStrategyService;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("进入mybatisSql拦截器：====================");
        System.out.println("进入mybatisSql拦截器：====================");
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler =
                MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
        doSplitTable(metaStatementHandler, parameterObject);
        // 传递给下一个拦截器处理
        return invocation.proceed();
    }

    private void doSplitTable(MetaObject metaStatementHandler, Object param) throws ClassNotFoundException {
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        if (originalSql != null && !originalSql.equals("")) {
            log.info("分表前的SQL：" + originalSql);
            MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            Class<?> classObj = Class.forName(className);
            // 根据配置自动生成分表SQL
            TableShard tableSplit = classObj.getAnnotation(TableShard.class);
            if (tableSplit == null) {
                return;
            }
//            TableSplitRule rule = tableSplit.rules();
//            if (rule != null) {
//
//                String convertedSql = null;
//                // StrategyManager可以使用ContextHelper策略帮助类获取，本次使用注入
//                if (!rule.paramName().isEmpty() && !rule.tableName().isEmpty()) {
//
//                    String paramValue = getParamValue(param, rule.paramName());
//                    //System.err.println("paramValue:"+paramValue);
//                    //获取 参数
//                    String newTableName = strategy.getTableName(rule.tableName(), paramValue);
//                    try {
//                        convertedSql = originalSql.replaceAll(rule.tableName(), newTableName);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                log.info("新sql是：" + convertedSql);
//                metaStatementHandler.setValue("delegate.boundSql.sql", convertedSql);
//            }
        }
    }

    public String getParamValue(Object obj, String paramName) {
        if (obj instanceof Map) {
            return (String) ((Map) obj).get(paramName);
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //System.err.println(field.getName());
            if (field.getName().equalsIgnoreCase(paramName)) {
                try {
                    return (String) field.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }


    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;

    }

    @Override
    public void setProperties(Properties properties) {

    }
}
