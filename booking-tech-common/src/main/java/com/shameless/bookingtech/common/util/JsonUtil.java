package com.shameless.bookingtech.common.util;

import com.google.gson.Gson;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class JsonUtil<T> {
    private static JsonUtil INSTANCE;
    private Gson gson;

    public synchronized static JsonUtil getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new JsonUtil<>();
            INSTANCE.gson = new Gson();
        }

        return INSTANCE;
    }

    public String toJson(List<T> tList) {
        if (Objects.isNull(tList))
            return "";
         if (INSTANCE == null) {
             INSTANCE = getInstance();
         }
         return INSTANCE.gson.toJson(tList);
    }

    public String toJson(T t) {
        if (Objects.isNull(t))
            return "";
        if (INSTANCE == null) {
            INSTANCE = getInstance();
        }
        return INSTANCE.gson.toJson(t);
    }

    public Object deepClone(T t) {
        if (INSTANCE == null) {
            INSTANCE = getInstance();
        }
        return gson.fromJson(gson.toJson(t), t.getClass());
    }
}
