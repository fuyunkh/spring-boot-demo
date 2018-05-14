package com.example.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zhangkh on 2017/11/3.
 */
public class LBMsgHelper {
    protected static Logger logger = LoggerFactory.getLogger(LBMsgHelper.class);

    static String success = "1";
    //soap1.2 http://schemas.xmlsoap.org/soap/envelope/
    // soap1.1 http://www.w3.org/2003/05/soap-envelope
    private static String soapHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
            " xmlns:ws=\"http://ws.livebos.apex.com/\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>";
    private static String soapTail = "</soapenv:Body>" + "</soapenv:Envelope>";

    public static String buildLoginMsg(Map<String, String> params, String methodName) {
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append(soapHeader);
        soapRequestData.append("<ws:" + methodName + ">");
        Set<String> nameSet = params.keySet();
        for (String name : nameSet) {
            soapRequestData.append("<" + name + ">" +
                    params.get(name) +
                    "</" + name + ">");
        }

        soapRequestData.append("</ws:" + methodName + ">");
        soapRequestData.append(soapTail);
        return soapRequestData.toString();
    }

    public static String buildQueryMsg(String methodName, String sessionId, String objName, String userId) {
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append(soapHeader);
        soapRequestData.append("<ws:" + methodName + ">");

        soapRequestData.append("<sessionId>" + sessionId + "</sessionId>");
        soapRequestData.append("<objectName>" + objName + "</objectName>");
        soapRequestData.append(" <params>");
        soapRequestData.append("<name>userid</name>");
        soapRequestData.append("<value>" + userId + "</value>");
        soapRequestData.append("</params>");
        soapRequestData.append(" <condition></condition>");
        soapRequestData.append("<queryOption>");
        soapRequestData.append("  <batchNo>1</batchNo>");
        soapRequestData.append("  <batchSize>10000000</batchSize>");
        soapRequestData.append("    <queryCount>true</queryCount>");
        soapRequestData.append("   <valueOption>VALUE</valueOption>");
        soapRequestData.append("</queryOption>");

        soapRequestData.append("</ws:" + methodName + ">");
        soapRequestData.append(soapTail);
        return soapRequestData.toString();
    }

    public static String buildRegistryMsg(String sessionId, String userId, String targetUrl) {
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append(soapHeader);
        soapRequestData.append("<ws:registry>");

        soapRequestData.append("<sessionId>" + sessionId + "</sessionId>");
        soapRequestData.append("<flag>1</flag>");
        soapRequestData.append("<userid>" + userId + "</userid>");
        soapRequestData.append("<target>" + URLEncoder.encode(targetUrl) + "</target>");

        soapRequestData.append("</ws:registry>");
        soapRequestData.append(soapTail);
        return soapRequestData.toString();
    }

    public static String getLoginSoapMsg(Map<String, String> paras) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("userid", paras.get("adminname"));
        parameterMap.put("password", paras.get("adminpassword"));
        parameterMap.put("scheme", paras.get("scheme"));
        parameterMap.put("algorithm", paras.get("algorithm"));
        parameterMap.put("securityCode", paras.get("securityCode"));
        String soapRequestData = buildLoginMsg(parameterMap, "login");
        return soapRequestData;
    }

    public static String getQuerySoapMsg(String sessionId, String objName, String userId) {
        String soapRequestData = buildQueryMsg("query", sessionId, objName, userId);
        return soapRequestData;
    }


    public static String buildUploadFileMsg(String sessionId, String filename,
                                            long length, byte[] contents,
                                            String objectName, String columnName) {

        String[] nameAndId = objectName.split("\\.");
        StringBuffer soapRequestData = new StringBuffer();
        soapRequestData.append(soapHeader);
        soapRequestData.append("<ws:putDocument>");

        soapRequestData.append("<sessionId>" + sessionId + "</sessionId>");
        soapRequestData.append("<document>");
        soapRequestData.append("<filename>" + filename + "</filename>");
        soapRequestData.append("<length>" + length + "</length>");
        soapRequestData.append("<documentData>" + contents + "</documentData>");
        soapRequestData.append("</document>");


        soapRequestData.append("<objectName>" + nameAndId[0] + "</objectName>");
        soapRequestData.append("<columnName>" + columnName + "</columnName>");
        soapRequestData.append("<id>" + nameAndId[1] + "</id>");

        soapRequestData.append("</ws:putDocument>");
        soapRequestData.append(soapTail);
        return soapRequestData.toString();
    }
}
