package com.example.util;


import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * 同时支持Http和Https的客户端工具，
 * <p>
 * 使用非安全方式访问Https网站，信任全部证书。
 * <p>
 * apache HttpClients版本：4.5
 */
public class HttpClientTools {
    protected static final  Logger logger = LoggerFactory.getLogger(HttpClientTools.class);
    protected static final  int timeout = 15 * 1000;   //默认15秒


    public static Boolean isSuccess(int status) {
        if (status >= 200 && status < 300) {
            return true;
        }
        return false;
    }

    public static String sendRequest(String url, EntityBuilder entity) throws Exception {
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(createSSLContext());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpEntity resultEntity;
        StringBuilder sb = new StringBuilder();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity.build());
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpPost);

        resultEntity = response.getEntity();
        int status = response.getStatusLine().getStatusCode();
        if (isSuccess(status)) {
            sb.append(EntityUtils.toString(resultEntity, "UTF-8"));
        } else {
            //失败
            logger.error("请求失败({}),status:{},msg:{}", url, status, EntityUtils.toString(resultEntity, "UTF-8"));
        }
        response.close();
        httpClient.close();

        return sb.toString();
    }

    public static void getRemoteFile(String url, OutputStream outstream) {
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(createSSLContext());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpEntity resultEntity;
        HttpGet httpMethod = new HttpGet(url);

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        httpMethod.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpMethod);
            resultEntity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (isSuccess(status)) {
                resultEntity.writeTo(outstream);
            } else {
                //失败
                logger.error("请求失败({}),status:{},msg:{}", url, status, EntityUtils.toString(resultEntity, "UTF-8"));
            }
            response.close();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SSLContext createSSLContext() {
        SSLContext sslcontext = null;
        try {
            sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
        } catch (Exception e) {
            logger.error("createSSLContext, {}", e.getStackTrace());
        }
        return sslcontext;
    }

    // 自定义私有类
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}