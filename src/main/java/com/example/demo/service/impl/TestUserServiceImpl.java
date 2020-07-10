package com.example.demo.service.impl;

import com.example.demo.entity.TestUser;
import com.example.demo.mapper.TestUserMapper;
import com.example.demo.service.TestUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "testUserService")
public class TestUserServiceImpl implements TestUserService {

    private static Logger logger = LoggerFactory.getLogger(TestUserServiceImpl.class);

    @Autowired
    private TestUserMapper testUserMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Cacheable(value = "userCache", key = "'user.findAll'")
    public List<TestUser> queryAllUser() throws Exception {
        logger.info("queryAllUser 日志打印");
        System.out.println("从Mysql中查询");
//        List<TestUser> testUsers = redisTemplate.opsForList().range("allUser", 0, -1);
//        if (null == testUsers || testUsers.size() == 0) {
        List<TestUser> testUsers = testUserMapper.queryAllUser();
//            redisTemplate.opsForList().rightPush("allUser", testUsers);
//        }
        return testUsers;
    }

    /**
     * @param testUser
     * @return
     * @author tsy
     */
    @Override
    @Transactional(readOnly = false)
    public Integer saveUser(TestUser testUser) throws Exception {
        List<TestUser> byUserName = testUserMapper.queryByUserName(testUser.getUserName());
        if (byUserName != null && byUserName.size() > 0) {
            throw new RuntimeException("用户已存在");
        }
        Integer integer = testUserMapper.saveUser(testUser);
        return integer;
    }
}
