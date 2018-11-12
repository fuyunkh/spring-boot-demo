package com.example.es;

import com.example.util.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.*;

/**
 * Created by Zhangkh on 2018/5/25.
 */
public class ElasticTest {
    public static class FileData {
        public String filename;
        public byte[] data;

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }
    }

    public static void send(String fileNames) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("filename", fileNames);
            data.put("data", FileUtils.readFileToByteArray(new File(fileNames)));


            httpClient = HttpClients.createDefault();
            String targetUrl = "http://10.29.37.123:9200/index4/type" + "?pipeline=single_attachment";
//            HttpPut method = new HttpPut(targetUrl);
            //不指定id需要用post操作
            HttpPost method = new HttpPost(targetUrl);
            StringEntity entity = new StringEntity(JsonUtil.toJsonString(data), "UTF-8");
//            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60 * 1000)
                    .setConnectionRequestTimeout(60 * 1000)
                    .setSocketTimeout(60 * 1000).build();
            method.setEntity(entity);
            method.addHeader("Content-Type", "application/json");
            method.setConfig(requestConfig);
            httpResponse = httpClient.execute(method);
            int statusCode = httpResponse.getStatusLine().getStatusCode();

//            HttpEntity responseEntity = httpResponse.getEntity();
//            String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
            if (statusCode >= 200 && statusCode < 300) {
//                System.out.println(responseContent);
            } else {
                System.out.println(fileNames + " failed");
            }
        } catch (Exception e) {

        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {

            }
        }
    }

    public static void upload(String pathName) {
        File path = new File(pathName);
        int idx = 5;
        if (path.isDirectory())//判断file是否是目录
        {
            File[] files = path.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getAbsolutePath();
                    System.out.println(fileName);
                    send(fileName);
                    idx++;
                }
            }
        }
    }

    public static void main(String[] argc) {
//        String[] files = {"tomcatServerStartup.pdf", "蜂巢微信版建设方案.docx"};


        long begin = System.currentTimeMillis();
        String path = "/home/zhangkh/testfiles";
        upload(path);
        long take = (System.currentTimeMillis() - begin);
        System.out.println("用时:" + take);
    }
}
