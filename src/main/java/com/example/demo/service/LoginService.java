package com.example.demo.service;

import com.example.demo.entity.User;

public interface LoginService {

    User getUserByName(String getMapByName);

    User getMapByName(String userName);
}
