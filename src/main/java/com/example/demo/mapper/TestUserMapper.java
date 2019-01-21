package com.example.demo.mapper;

import com.example.demo.entity.TestUser;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TestUserMapper {

    List<TestUser> queryAllUser();

    Integer saveUser(TestUser testUser);

    List<TestUser> queryByUserName(String userName);


}
