package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.TestUser;
import com.example.demo.mapper.TestUserMapper;
import com.example.demo.service.TestUserService;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBinlog {

    @Autowired
    private TestUserMapper testUserService;

    @Test
    public void test() throws IOException {
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "tsy19930815");
        EventDeserializer eventDeserializer = new EventDeserializer();
        //时间反序列化的格式
//        eventDeserializer.setCompatibilityMode(
//                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
//                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
//        );
        client.setEventDeserializer(eventDeserializer);
        client.registerEventListener(event -> {
            EventHeader header = event.getHeader();
            EventType eventType = header.getEventType();
            System.out.println("监听的事件类型:" + eventType);
            if (EventType.isWrite(eventType)) {
                //获取事件体
                WriteRowsEventData data = event.getData();
                log.info(JSON.toJSONString(data));
                String str = JSON.toJSONString(data);
                System.out.println("str = " + str);
                saveRows(str);
            } else if (EventType.isUpdate(eventType)) {
                UpdateRowsEventData data = event.getData();
                log.info(JSON.toJSONString(data));
            } else if (EventType.isDelete(eventType)) {
                DeleteRowsEventData data = event.getData();
                log.info(JSON.toJSONString(data));
            }
        });
        client.connect();
    }

    private void saveRows(String str) {
        JSONObject jsonObject = JSON.parseObject(str);
        JSONArray rows = jsonObject.getJSONArray("rows");
        for (int i = 0; i < rows.size(); i++) {
            JSONArray objects = rows.getJSONArray(i);
            TestUser testUser = new TestUser();
            testUser.setUserId((Integer) objects.get(0));
            testUser.setUserName((String) objects.get(1));
            testUser.setPassword(String.valueOf(objects.get(2)));
            testUser.setEmail((String) objects.get(3));
            testUser.setSex((String) objects.get(4));
            testUser.setInvalid((Integer) objects.get(5));
            try {
                Integer integer = testUserService.saveUserToCopy(testUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}