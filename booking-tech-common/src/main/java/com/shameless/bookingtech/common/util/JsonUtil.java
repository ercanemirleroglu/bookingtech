package com.shameless.bookingtech.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class JsonUtil<T> {
    private static JsonUtil INSTANCE;
    private ObjectMapper objectMapper;
    private Gson gson;

    public synchronized static JsonUtil getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new JsonUtil<>();
            INSTANCE.gson = new Gson();
            INSTANCE.objectMapper = new ObjectMapper();
            INSTANCE.objectMapper.registerModule(new JavaTimeModule());
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

    public Object readFile(Class clazz, String path, String fileName) {
        try {
            InputStream inputStream = new FileInputStream(path);
            return objectMapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(fileName + " file could not read! Detailed message: " + e.getMessage());
        }
    }

    public Object deepClone(T t) {
        if (INSTANCE == null) {
            INSTANCE = getInstance();
        }
        return gson.fromJson(gson.toJson(t), t.getClass());
    }
}
