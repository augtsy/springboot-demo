package com.example.demo.utils;

public class Result {

    private boolean success;

    private Object  data;

    private String  error;


    public Result(boolean success, Object data) {

        this.success = success;
        this.data = data;
    }


    public Result(boolean success, String error) {

        this.success = success;
        this.error = error;
    }


    public boolean isSuccess() {

        return success;
    }


    public void setSuccess(boolean success) {

        this.success = success;
    }


    public Object getData() {

        return data;
    }


    public void setData(Object data) {

        this.data = data;
    }


    public String getError() {

        return error;
    }


    public void setError(String error) {

        this.error = error;
    }

}
