import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static java.util.Collections.singletonList;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.fetch.subphase.highlight.Highlighter;
import org.elasticsearch.search.fetch.subphase.highlight.HighlighterBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlighterParser;
import org.elasticsearch.search.internal.SearchContext;

public class MatchValuePlugin extends Plugin implements SearchPlugin {

  @Override
  public Map<String, SearchModule> getSearchModules() {
    return Map.of(
      MatchValueQuery.NAME, new MatchValueQuery.SearchModule()
    );
  }

  public static class MatchValueQuery implements Supplier<MatchValueQuery> {

    public static final String NAME = "match_value";

    public MatchValueQuery() {}

    public static SearchModule getSearchModule() {
      return new SearchModule()
          .addQuery(NAME, MatchValueQuery::new)
          .addParser(new MatchValueParser());
    }

    public static class SearchModule implements org.elasticsearch.plugins.SearchPlugin.SearchModule {
      @Override
      public List<QuerySpec<?>> getQueries() {
        return singletonList(
            new QuerySpec<>(MatchValueQuery.NAME, MatchValueQuery::new, MatchValueQuery::fromXContent)
        );
      }
    }

    public static class MatchValueParser implements HighlighterParser {
      @Override
      public HighlighterBuilder parse(SearchContext searchContext) {
        return new MatchValueHighlighterBuilder();
      }
    }

    public static class MatchValueHighlighterBuilder implements HighlighterBuilder {
      @Override
      public Highlighter build() {
        return HighlighterBuilders.match();
      }
    }

    public static MatchValueQuery fromXContent(QueryParseContext parseContext) {
      return new MatchValueQuery();
    }
  }
}
