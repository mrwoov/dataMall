package com.dataMall.userCenter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@SuppressWarnings("ALL")
public class JSONUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> jsonToMap(String jsonStr) throws JsonProcessingException {
        Map<String, Object> map = mapper.readValue(jsonStr, Map.class);
        return map;
    }
}
