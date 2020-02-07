package com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy;

/**
 * @author yuanjun
 */
public interface TableStrategyService {
    /**
     *
     * @param tableName
     * @param splitTableParam
     * @return
     */
    String getTableName(String tableName, String splitTableParam);
}
