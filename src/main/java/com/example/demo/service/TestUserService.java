package com.example.demo.service;

import com.example.demo.entity.TestUser;

import java.util.List;

public interface TestUserService {

    List<TestUser> queryAllUser();

    Integer saveUser(TestUser testUser) throws Exception;

}
