package com.eye1024.bean;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 菜单返回的json解析类
 * Created by Ray on 2015/6/29.
 */
public class TypeResult extends WebResult{

    private ArrayList<Type> data;

    public static TypeResult parse(String json){
        try {
            return new Gson().fromJson(json, TypeResult.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Type> getData() {
        return data;
    }

    public void setData(ArrayList<Type> data) {
        this.data = data;
    }

    public static class Type{
        private int id;
        private String name;
        private String param1;
        public Type() {
        }

        public Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getParam1() {
            return param1;
        }

        public void setParam1(String param1) {
            this.param1 = param1;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
