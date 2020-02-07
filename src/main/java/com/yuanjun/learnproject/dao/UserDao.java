package com.yuanjun.learnproject.dao;

import com.yuanjun.learnproject.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yuanjun
 */
@Mapper
public interface UserDao {

    @Select("select * from tbl_user where user_id = #{userId}")
    List<User> query(@Param("userId") String userId);
}
