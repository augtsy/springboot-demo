package com.example.demo.entity;

import lombok.Data;

import java.util.List;

@Data
public class Dept {

    private String id;
    private String name;
    private String num;

    private List<TestUser> testUsers;
}
