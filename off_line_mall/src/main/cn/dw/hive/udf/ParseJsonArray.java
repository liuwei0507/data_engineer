package cn.dw.hive.udf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.internal.joptsimple.internal.Strings;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.junit.Test;

import java.util.ArrayList;

public class ParseJsonArray extends UDF {
    public ArrayList<String> evaluate(String jsonStr, String key) {
        if (Strings.isNullOrEmpty(jsonStr)) {
            return null;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            ArrayList<String> list = new ArrayList<>();
            for (Object o : jsonArray) {
                list.add(o.toString());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testParseJSonArray() {

    }
}
