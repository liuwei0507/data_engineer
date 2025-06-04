package cn.dw.hive.udf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.junit.Test;

import java.util.ArrayList;

public class ParseJsonArray extends UDF {
    public ArrayList<String> evaluate(String jsonStr) {
        if (Strings.isNullOrEmpty(jsonStr)) {
            return null;
        }

        try {
            // 获取jsonArray
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            ArrayList<String> lst = new ArrayList<>();
            for (Object o : jsonArray) {
                lst.add(o.toString());
            }
            return lst;
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testParseJSonArray() {
        String jsonStr = "[{\"name\":\"goods_detail_loading\",\"json\":{\"entry\":\"1\",\"goodsid\":\"0\",\"loading_time\":\"93\",\"action\":\"3\",\"staytime\":\"56\",\"showtype\":\"2\"},\"time\":1596343881690},{\"name\":\"loading\",\"json\":{\"loading_time\":\"15\",\"action\":\"3\",\"loading_type\":\"3\",\"type\":\"1\"},\"time\":1596356988428},{\"name\":\"notification\",\"json\":{\"action\":\"1\",\"type\":\"2\"},\"time\":1596374167278},{\"name\":\"favorites\",\"json\":{\"course_id\":1,\"id\":0,\"userid\":0},\"time\":1596350933962}]";
        ArrayList<String> result = evaluate(jsonStr);
        System.out.println(result.size());
        System.out.println(JSON.toJSONString(result));
    }
}
