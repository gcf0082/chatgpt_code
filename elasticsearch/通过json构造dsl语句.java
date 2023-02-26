import org.json.JSONArray;
import org.json.JSONObject;

// 构造 bool 查询
JSONObject boolQuery = new JSONObject();
boolQuery.put("bool", new JSONObject());

// 构造 must 条件
JSONArray mustArray = new JSONArray();
mustArray.put(new JSONObject().put("range", new JSONObject()
        .put("@timestamp", new JSONObject()
                .put("gte", "2022-01-01")
                .put("lte", "2022-01-31")
        )));
mustArray.put(new JSONObject().put("match", new JSONObject()
        .put("message", "error")
));
boolQuery.getJSONObject("bool").put("must", mustArray);

// 打印查询语句
System.out.println(boolQuery.toString());
