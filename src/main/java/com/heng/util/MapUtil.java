package com.heng.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class MapUtil {

    private MapUtil(){

    }

    private static ObjectMapper mapper = new ObjectMapper();

    public static Map<String,Object> objectToMap(Object object){

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            String json = mapper.writeValueAsString(object);
            Map<String,Object> map = mapper.readValue(json,Map.class );
            return map;
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
