package com.example.demo.utils;

public class JsonResult {

    private Integer status;
    private String message;
    private Object data;

    public JsonResult() {

    }

    public JsonResult(Object data) {
        this.message = "true";
        this.status = 0;
        this.data = data;
    }

    public JsonResult(String message) {
        this.message = "false";
        this.status = 1;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
