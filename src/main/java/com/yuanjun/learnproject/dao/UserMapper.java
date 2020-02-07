package com.yuanjun.learnproject.dao;

import com.yuanjun.learnproject.bean.User;
import com.yuanjun.learnproject.constant.StrategyType;
import com.yuanjun.learnproject.databaseshard.annotion.TableShard;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author yuanjun
 */
@Mapper
@TableShard(tableName = "tbl_user", paramName = "userId", tableStrategyType = StrategyType.USER_ID)
public interface UserMapper {

    @Insert("insert into tbl_user (user_id,user_name,password) values(#{userId},#{userName},#{password})")
    void saveUser(User user);


    @Select("select * from tbl_user where user_id=#{userId}")
    List<User> query(@Param("userId") String userId);

    /** @SelectProvider(type = UserSqlProvider.class, method = "save")
     *   void saveUser(Map<String,Object> param);
     */

    /**
     * @Select("select * from ${tbName} where user_id=#{userId}")
     * List<User> query(@Param("tbName") String tbName, @Param("userId") String userId);
     */
}
