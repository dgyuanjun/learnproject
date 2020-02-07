package com.yuanjun.learnproject.dao;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class UserSqlProvider {

    public String save(Map<String,Object> params) {
        return new SQL() {
            {
                INSERT_INTO("${tbName}");
                VALUES("user_id", "#{userId}");
                VALUES("user_name", "#{userName}");
                VALUES("password", "#{password}");
            }
        }.toString();
    }
}
