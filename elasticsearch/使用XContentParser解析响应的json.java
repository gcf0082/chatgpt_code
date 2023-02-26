import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.search.SearchHit;

// 假设 searchHits 是 Elasticsearch 查询的响应结果
SearchHit[] searchHits = ...;

// 遍历每个搜索结果
for (SearchHit hit : searchHits) {
    // 获取 JSON 格式的源数据
    String source = hit.getSourceAsString();
    if (source != null) {
        // 使用 XContentParser 解析 JSON 数据
        try (XContentParser parser = XContentFactory.jsonParser(source)) {
            // 遍历 JSON 数据的每个字段
            while (parser.nextToken() != null) {
                String fieldName = parser.currentName();
                parser.nextToken();
                Object fieldValue = parser.objectText();
                // TODO: 对每个字段进行相应的处理
                System.out.println(fieldName + ": " + fieldValue);
            }
        }
    }
}
