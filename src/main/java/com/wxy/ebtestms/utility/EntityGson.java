package com.wxy.ebtestms.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class EntityGson <T>{
    public String type;
    public T data;

    public EntityGson(String type, T data) {
        this.type = type;
        this.data = data;
    }

    public EntityGson() {
    }

    public String  getGsonString()
    {

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new LongDateTypeAdapter()).create();
        String strRpt=  gson.toJson(this);
        return strRpt;
    }

    static public String getType(String msg)
    {
        JsonElement je = new JsonParser().parse(msg);
        String retype  = je.getAsJsonObject().get("type").getAsString();
        return retype;
    }


    static  public <T> T getData(String type, String msg)
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new LongDateTypeAdapter()).create();
        switch(type)
        {
            case "cfgAddr": {
                Type userType = new TypeToken<EntityGson<List<Long>>>() {}.getType();
                EntityGson<List<Long>> userResult = gson.fromJson(msg, userType);
                return  (T)userResult.data;
            }

            default:
                break;
        }
        return null;
    }

}