package com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yuanjun
 */
@Service
@Slf4j
public class TableStrategyByDateServiceImpl implements TableStrategyService {

    /**
     *
     * @param tableName
     * @param param
     * @return
     */
    @Override
    public String getTableName(String tableName, String param) {
        log.info("TableStrategyByDate start");
        //todo
        return tableName;
    }
}
