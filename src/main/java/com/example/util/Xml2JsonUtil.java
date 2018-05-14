package com.example.util;


import static org.apache.coyote.http11.Constants.a;

/**
 * Created by Zhangkh on 2017/12/9.
 */
public class Xml2JsonUtil {
    //    private static final TerseJson.WhitespaceBehaviour COMPACT_WHITE_SPACE = TerseJson.WhitespaceBehaviour.Compact;
    public static void main(String[] args) throws Exception {

//        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><AuditRecords><Record><State>0</State><Msg>此项待办任务已完成、终止或暂停，不能被执行。</Msg></Record></AuditRecords>";
//        JSONObject a = XML.toJSONObject(str);
        System.out.println(JsonUtil.toJsonString(a));
    }

    public static void test() {
        String xmlString;  // some XML String previously created
//        XmlToJson xmlToJson = new XmlToJson.Builder(xmlString).build();
    }

    /**
     * JSON(数组)字符串<STRONG>转换</STRONG>成XML字符串
     *
     * @param jsonString
     * @return
     */
//    public static String json2xml(String jsonString) {
//        XMLSerializer xmlSerializer = new XMLSerializer();
//        return xmlSerializer.write(JSONSerializer.toJSON(jsonString));
//        // return xmlSerializer.write(JSONArray.fromObject(jsonString));//这种方式只支持JSON数组
//    }

}
