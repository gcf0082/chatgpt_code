import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

// 构造 Bool 查询条件
BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
// 添加 must 条件
boolQuery.must(QueryBuilders.matchQuery("field1", "value1"));
// 添加 must_not 条件
boolQuery.mustNot(QueryBuilders.termQuery("field2", "value2"));
// 添加 should 条件
boolQuery.should(QueryBuilders.rangeQuery("field3").gte(10).lte(20));

// 构造排序条件
JSONObject sort = JSONUtil.createObj()
        .set("field1", "asc")
        .set("field2", "desc");

// 构造查询 DSL
JSONObject query = JSONUtil.createObj()
        .set("query", boolQuery)
        .set("from", 0)
        .set("size", 10)
        .set("sort", sort);

// 输出 DSL
System.out.println(query.toStringPretty());
