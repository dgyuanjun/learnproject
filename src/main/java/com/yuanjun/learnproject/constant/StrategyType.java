package com.yuanjun.learnproject.constant;

import lombok.Getter;

/**
 * tableShard strategyType
 *
 * @author yuanjun
 */
@Getter
public enum StrategyType {
    USER_ID(1),

    BY_DATE(2);

    private int code;

    StrategyType(int code) {
        this.code = code;
    }

}
