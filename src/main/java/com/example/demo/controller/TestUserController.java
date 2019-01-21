package com.example.demo.controller;

import com.example.demo.entity.TestUser;
import com.example.demo.service.TestUserService;
import com.example.demo.utils.JsonResult;
import com.example.demo.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestUserController {

    @Autowired
    private TestUserService testUserService;

    @RequestMapping("/all")
    public JsonResult queryAllUser() {
        List<TestUser> testUsers = testUserService.queryAllUser();
        return new JsonResult(testUsers);
    }

    @RequestMapping("/save")
    public Result saveUser(TestUser testUser) {
        Integer mun;
        try {
            mun = testUserService.saveUser(testUser);
            if (mun > 0) {
                return new Result(true, mun);
            }
        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }
        return new Result(false, "保存异常");
    }
}
