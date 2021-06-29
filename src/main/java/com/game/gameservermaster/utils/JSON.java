package com.game.gameservermaster.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.gameservermaster.exception.MalformedJsonException;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JSON {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> map;

    public JSON() {
        map = new HashMap<>();
    }

    public JSON(Map<String, Object> map) {
        this.map = map;
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public void put(String key, JSON value) {
        map.put(key, value.map);
    }

    public void put(String key, List<JSON> value) {
        map.put(key, value.stream().map(j -> j.map).collect(Collectors.toList()));
    }

    public <T extends Object> T get(String key) throws MalformedJsonException {
        try{
            return (T)map.get(key);
        } catch (ClassCastException e) {
            log.error("json could not cast key {} to {}", key,
                ((Class<T>)((ParameterizedType)getClass().getGenericSuperclass())
                .getActualTypeArguments()[0]).getTypeName());
            throw new MalformedJsonException(e);
        }

    }

    public boolean has(String key) {
        return map.containsKey(key);
    }

    public String toJsonString() {
        try{
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("unable to convert map to json", e);
            return null;
        }
    }

    public static JSON parse(String jsonString) throws MalformedJsonException {
        try {
            return new JSON(objectMapper.readValue(jsonString, HashMap.class));
        } catch(JsonProcessingException e) {
            log.error("error parsing JSON", e.getMessage());
            throw new MalformedJsonException(e);
        }
    }

}
