package com.yuanjun.learnproject.databaseshard.interceptor;

import com.yuanjun.learnproject.databaseshard.annotion.TableShard;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.StrategyManagerFactory;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy.TableStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 * 拦截SQL语句
 * <p>
 * 自定义组装SQL语句
 *
 * @author yuanjun
 */
@Slf4j
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("mybatis intercept sql start");
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

        // rebuild sql
        rebuildSql(metaObject);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private void rebuildSql(MetaObject metaStatementHandler) throws ClassNotFoundException {
        log.info("mybatis rebuild sql start");
        TableShard tableShard = getTableShardAnnotation(metaStatementHandler);
        String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
        log.info("splitTable original sql {}", originalSql);
        if (!StringUtils.isEmpty(originalSql) && tableShard != null) {
            String tableName = tableShard.tableName();
            String paramName = tableShard.paramName();
            TableStrategyService tableStrategyService = StrategyManagerFactory.getBean(tableShard.tableStrategyType());
            if (!StringUtils.isEmpty(tableName) && !StringUtils.isEmpty(paramName)) {
                Object parameterObject = metaStatementHandler.getValue("delegate.boundSql.parameterObject");
                String param = getParams(parameterObject, paramName);
                String newTableName = tableStrategyService.getTableName(tableName, param);

                String newSql = originalSql.replaceAll(tableName, newTableName);
                log.info("splitTable new sql {}", newSql);
                metaStatementHandler.setValue("delegate.boundSql.sql", newSql);
            }
        }


    }

    private TableShard getTableShardAnnotation(MetaObject metaStatementHandler) throws ClassNotFoundException {
        // get sql xml info
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        // get sql namespace
        String id = mappedStatement.getId();
        // get dao class
        Class<?> targetClass = Class.forName(id.substring(0, id.lastIndexOf('.')));
        // judge contain tableShard annotation
        return targetClass.getAnnotation(TableShard.class);
    }


    private String getParams(Object parameterObject, String paramName) {
        if (parameterObject instanceof Map) {
            return (String) ((Map) parameterObject).get(paramName);
        }
        Field[] declaredFields = parameterObject.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(paramName)) {
                try {
                    return String.valueOf(field.get(parameterObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
