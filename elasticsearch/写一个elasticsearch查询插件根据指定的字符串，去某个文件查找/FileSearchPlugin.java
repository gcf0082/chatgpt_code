import org.apache.lucene.search.Query;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;
import org.elasticsearch.search.internal.SearchContext;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentParserUtils.ensureFieldName;
import static org.elasticsearch.index.query.QueryShardContext.DEFAULT_TYPE;

public class FileSearchPlugin extends Plugin implements SearchPlugin {
    
    @Override
    public QueryBuilder getQueryBuilder(QueryShardContext context) {
        return new FileSearchQueryBuilder();
    }

    @Override
    public void onIndexModule(IndexModule indexModule) {
        indexModule.addIndexEventListener(new FileSearchIndexEventListener());
    }

    private static class FileSearchQueryBuilder extends QueryBuilder {
        private String filePath;
        private String searchString;

        public FileSearchQueryBuilder() {
        }

        public FileSearchQueryBuilder(String filePath, String searchString) {
            this.filePath = filePath;
            this.searchString = searchString;
        }

        @Override
        protected void doXContent(XContentParser parser, QueryParseContext context) throws IOException {
            QueryShardContext shardContext = context.getQueryShardContext();
            String currentFieldName = null;
            ensureFieldName(parser.nextToken(), parser::getCurrentName); // Move to the field name
            while ((parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                currentFieldName = parser.currentName();
                if ("file_path".equals(currentFieldName)) {
                    filePath = parser.text();
                } else if ("search_string".equals(currentFieldName)) {
                    searchString = parser.text();
                } else {
                    throw new IllegalArgumentException("Unknown parameter name [" + currentFieldName + "] in file search query.");
                }
            }
            if (filePath == null || searchString == null) {
                throw new IllegalArgumentException("file_path and search_string parameters are required for file search query.");
            }
        }

        @Override
        protected Query doToQuery(SearchExecutionContext context) throws IOException {
            String result = FileSearcher.search(filePath, searchString);
            return QueryBuilders.queryStringQuery(result);
        }

        @Override
        public String getName() {
            return "file_search";
        }

        @Override
        public String getType() {
            return DEFAULT_TYPE
        }
    }
