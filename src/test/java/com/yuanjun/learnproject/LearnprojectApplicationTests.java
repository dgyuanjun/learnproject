package com.yuanjun.learnproject;

import com.yuanjun.learnproject.bean.User;
import com.yuanjun.learnproject.dao.UserDao;
import com.yuanjun.learnproject.dao.UserMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@MapperScan("com.yuanjun.learnproject.dao")
public class LearnprojectApplicationTests {
    private User user;
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserDao userDao;

    @Before
    public void init() {
        user = new User();
        user.setUserId("M12348");
        user.setUserName("yuanjun");
        user.setPassword("123456");
    }

    @Test
    public void save() {
//        String tbName = TblNameUtil.generateTblName(user.getUserId());
        userMapper.saveUser(user);
//        Map<String,Object> params = new HashMap<>();
//        params.put("tbName",tbName);
//        params.put("userId",user.getUserId());
//        params.put("userName",user.getUserName());
//        params.put("password",user.getPassword());
//        userMapper.saveUser(params);
    }

    @Test
    public void testQuery() {
//        String tblName = TblNameUtil.generateTblName(user.getUserId());
//        List<User> users = userMapper.query(tblName, "M12356");
        List<User> users = userMapper.query(user.getUserId());
        users.forEach(System.out::println);
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void testWithoutSpiltTableQuery(){
        List<User> users = userDao.query("M20765");
        users.forEach(System.out::println);
        Assert.assertEquals(1, users.size());
    }

}
