package com.example.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Zhangkh on 2017/4/27.
 */

public class DataCheck {
    public static int checkdata(int pageno) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://localhost:8080/v1/users?size=500&page=" + pageno)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String resultBody = response.body().string();
            JSONObject object = JSON.parseObject(resultBody);
            int total = object.getIntValue("total");
            if (total <= 0) {
                return 0;
            }
            JSONArray items = object.getJSONArray("items");
            Iterator<Object> it = items.iterator();
            while (it.hasNext()) {
                JSONObject one = (JSONObject) it.next();
                int id = one.getIntValue("id");
                String name = one.getString("name");
                System.out.println("id:" + id + " name:" + name);
            }


        } catch (Exception e) {

        }

        return 1;
    }

    public static void check() {
        int i = 1;
        for (i = 1; i < 100; i++) {
            if (checkdata(i) == 0) {
                break;
            }
        }
        System.out.println(i);
    }

//    public static void main(String[] args) {
//        try {
//            DataCheck.check();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
