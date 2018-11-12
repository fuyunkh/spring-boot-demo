package com.example.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.User;
import com.example.entity.UserA;
import com.example.mappers.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhangkh on 2017/3/7.
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public List<User> get(String userName) {
        return userMapper.get(userName);
    }

    public void getData(int grade) {
        List<Map> result = userMapper.getData();
        for (int i = 0; i < result.size(); i++) {
            Map m = result.get(i);
            Set<Object> keys = m.keySet();
            for (Object k : keys) {
                System.out.println("field:" + k.toString() + " value:" + m.get(k).toString());
            }
        }
    }

//    public int checkdata(int pageno) {
//        try {
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("http://localhost:8080/v1/users/convertlist?size=50&page=" + pageno)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            String resultBody = response.body().string();
//            JSONObject object = JSON.parseObject(resultBody);
//            int total = object.getIntValue("total");
//            if (total <= 0) {
//                return 0;
//            }
//            JSONArray items = object.getJSONArray("items");
//            Iterator<Object> it = items.iterator();
//            while (it.hasNext()) {
//                JSONObject one = (JSONObject) it.next();
//                int id = one.getIntValue("id");
//                String name = one.getString("convertId");
//                UserA ua = new UserA();
//                ua.setId(id);
//                ua.setName(name);
//                userMapper.insert(ua);
//               // System.out.println("id:" + id + " name:" + name);
//            }
//
//
//        } catch (Exception e) {
//
//        }
//
//        return 1;
//    }


}


