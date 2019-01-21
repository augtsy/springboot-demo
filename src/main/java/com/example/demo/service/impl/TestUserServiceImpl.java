package com.example.demo.service.impl;

import com.example.demo.entity.TestUser;
import com.example.demo.mapper.TestUserMapper;
import com.example.demo.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value="testUserService")
public class TestUserServiceImpl implements TestUserService {

    @Autowired
    private TestUserMapper testUserMapper;

    @Override
    public List<TestUser> queryAllUser(){
        List<TestUser> testUsers = testUserMapper.queryAllUser();
        return testUsers;
    }

    /**
     * @author tsy
     * @param testUser
     * @return
     */
    @Override
    public Integer saveUser(TestUser testUser) throws Exception {
        List<TestUser> byUserName = testUserMapper.queryByUserName(testUser.getUserName());
        if(byUserName != null && byUserName.size() > 0){
            throw new RuntimeException("用户已存在");
        }
        Integer integer = testUserMapper.saveUser(testUser);
        return integer;
    }
}
