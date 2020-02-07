# learnproject
<h2>mysql自动分表实现</h2>
一、实现原理
   通过mybatis的插件拦截执行的sql语句，根据策略得到具体的表名，替换sql里的表名，从而达到自动分表的效果，在次过程中对Dao无侵入性，不需要关注
表名的逻辑处理。
二、手动分表的实现(背景：根据userId进行分表)
1.以查询为例(dao层传入表名和查询参数)
mybatis #{} ->参数占位符，对数据加引号处理，会对参数进行检查，可以防止sql注入
        ${} ->替换sql里的参数，简单的字符串替换，需要手动防止sql注入，在这里表名需要用${}处理
 ``` sql
@Select("select * from ${tbName} where user_id=#{userId}")
List<User> query(@Param("tbName") String tbName, @Param("userId") String userId);
 ```
2.问题，表名处理繁琐特别对于插入操作，使用注解替代xml实现不好写，而且增加dao的逻辑
3.能否自动处理表名
三、自动分表的实现原理
采用注解+mybatis插件实现
四、实现步骤
1.注解（灵活使用，需要分表的dao添加注解）

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableShard {
    String tableName();

    //暂时只支持单参数
    String paramName();

    StrategyType tableStrategyType();
}
2.mybatis插件

1）实现Interceptor，并添加插件的注解，添加拦截点
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})

2）获取StatementHandler，并转化成MetaObject，便于获取属性

StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        
3）MappedStatement-》获取命名空间-》找到dao类-》获取注解

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
4）metaObject-》获取sql + 执行参数 -》根据注解的参数+策略 生成新的表名 -》替换表名
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
5）参数值的获取，map key+ 对象字段名匹配
