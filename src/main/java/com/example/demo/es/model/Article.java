package com.example.demo.es.model;

import lombok.Data;

/**
 * todo
 */
@Data
public class Article {
    private Long id;//l 文章 id
    private String title;//2: 文章标题
    private String content; //3: 文章内容
    private String author; //4: 文章作者
    private String url; //5 文章 URL 地址
}
