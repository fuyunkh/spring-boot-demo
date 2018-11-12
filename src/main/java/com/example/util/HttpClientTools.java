package com.example.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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
import java.util.Map;


/**
 * 同时支持Http和Https的客户端工具，
 * <p>
 * 使用非安全方式访问Https网站，信任全部证书。
 * <p>
 * apache HttpClients版本：4.5
 */
public class HttpClientTools {
    protected final static Logger logger = LoggerFactory.getLogger(HttpClientTools.class);
    protected final static int timeout = 10 * 1000;   //默认15秒

    public static Boolean isSuccess(int status) {
        if (status >= 200 && status < 300) {
            return true;
        }
        return false;
    }

    public static String doPost(String url, EntityBuilder builder) {
        return doRequest(url, builder.build(), null, true);
    }

    public static String doPost(String url, HttpEntity entity, Map<String, Object> headers) {
        return doRequest(url, entity, headers, true);
    }

    public static String doGet(String url, Map<String, Object> headers) {
        return doRequest(url, null, headers, false);
    }

    private static String doRequest(String url, HttpEntity entity, Map<String, Object> headers, boolean isPost) {
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(createSSLContext());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpEntity resultEntity;
        StringBuilder sb = new StringBuilder();
        HttpRequestBase requestBase;
        if (isPost) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            requestBase = httpPost;
        } else {
            HttpGet httpGet = new HttpGet(url);
            requestBase = httpGet;
        }
        addHeaders(requestBase, headers);

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
        requestBase.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(requestBase);
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
        } catch (IOException e) {
            logger.error("请求失败,url:{},{}", url, e.getStackTrace());
        }

        return sb.toString();
    }


    private static void addHeaders(HttpRequestBase request, Map<String, Object> headers) {
        if (headers != null && headers.size() > 0) {
            for (String k : headers.keySet()) {
                request.addHeader(k, String.valueOf(headers.get(k)));
            }
        }
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