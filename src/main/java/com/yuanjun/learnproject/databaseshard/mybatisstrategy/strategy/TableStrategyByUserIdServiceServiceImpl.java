package com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yuanjun
 */
@Service
@Slf4j
public class TableStrategyByUserIdServiceServiceImpl implements TableStrategyService {

    @Override
    public String getTableName(String tableName, String param) {
        log.info("generateTbName start tableName {},param{}", tableName, param);
        return generateTblName(tableName, param);
    }

    private String generateTblName(String tbName, String userId) {
        int tableIndex = Math.abs(userId.hashCode() % 4);
        return tbName + "_" + tableIndex;
    }
}
