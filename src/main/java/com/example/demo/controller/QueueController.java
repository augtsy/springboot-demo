package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.Queue;

@RestController
@RequestMapping("queue")
public class QueueController {
    //注入发送消息的对象
    @Autowired
    private JmsTemplate jmsTemplate;
    //注入消息队列
    @Autowired
    private Queue queue;

    //编写发送消息的方法
    @RequestMapping("send/{message}")
    public String send(@PathVariable String message) {
        this.jmsTemplate.convertAndSend(queue, message);
        return "消息发送成功!消息内容：" + message;
    }

}
