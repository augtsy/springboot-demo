package com.example.demo.controller;

import org.springframework.jms.annotation.JmsListener;

public class Consumer {
    // 接受消息方法

    @JmsListener(destination = "cnn.queue")
    public void readMessage(String text) {
        System.out.println("接受到的消息是：" + text);
    }
}
