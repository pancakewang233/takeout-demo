package com.itheima.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/*
* 通用返回结果
* */
@Data
public class R<T> {
    private Integer code; // 1是成功，其他失败
    private String msg;
    private T data;
    private Map map = new HashMap();  //动态数据

    public static <T> R<T> success(T object){
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value){
        this.map.put(key, value);
        return this;
    }
}
