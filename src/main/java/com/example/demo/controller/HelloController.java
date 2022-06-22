package com.example.demo.controller;

import com.example.demo.entity.TestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    final private ApplicationEventPublisher applicationEventPublisher;

    @RequestMapping("/hello")
    public String index() {
        TestUser testUser = new TestUser();
        testUser.setUserId(12);
        testUser.setUserName("tsy");
        applicationEventPublisher.publishEvent(testUser);
        System.out.println("Hello World tsy  nihao");
        return "Hello World tsy  nihao ";
    }

    @RequestMapping("/test")
    public String test() {
        return "Hello World tsy nihao";
    }

    @Async("lazyTraceExecutor")
    @EventListener(TestUser.class)
    public void testPublishEvent(TestUser testUser) throws InterruptedException {
        System.out.println("testUser = " + testUser);
    }

}

