package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

        @RequestMapping("/hello")
        public String index() {
            return "Hello World tsy   ";
        }

        @RequestMapping("/test")
        public String test() {
            return "Hello World tsy nihao";
        }
}

