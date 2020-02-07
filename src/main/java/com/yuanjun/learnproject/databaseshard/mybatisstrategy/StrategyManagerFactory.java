package com.yuanjun.learnproject.databaseshard.mybatisstrategy;


import com.yuanjun.learnproject.constant.StrategyType;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy.TableStrategyByDateServiceImpl;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy.TableStrategyByUserIdServiceServiceImpl;
import com.yuanjun.learnproject.databaseshard.mybatisstrategy.strategy.TableStrategyService;

/**
 * @author yuanjun
 */
public class StrategyManagerFactory {


    public static TableStrategyService getBean(StrategyType strategyType) {
        if (StrategyType.USER_ID == strategyType) {
            return new TableStrategyByUserIdServiceServiceImpl();
        } else {
            return new TableStrategyByDateServiceImpl();
        }
    }

}
