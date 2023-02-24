import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.druid.client.DruidServer;
import io.druid.data.input.MapBasedInputRow;
import io.druid.java.util.common.logger.Logger;
import io.druid.java.util.common.logger.LoggerFactory;
import io.druid.java.util.common.StringUtils;
import io.druid.java.util.common.guava.Functions;
import io.druid.java.util.common.guava.Sequence;
import io.druid.java.util.common.guava.Sequences;
import io.druid.java.util.common.io.smoosh.Smoosh;
import io.druid.java.util.common.io.smoosh.SmooshedWriter;
import io.druid.query.DruidProcessingConfig;
import io.druid.segment.IndexIO;
import io.druid.segment.IndexMerger;
import io.druid.segment.QueryableIndex;
import io.druid.segment.QueryableIndexStorageAdapter;
import io.druid.segment.loading.DataSegmentPusher;
import io.druid.segment.loading.LocalDataSegmentPusher;
import io.druid.segment.realtime.FireDepartment;
import io.druid.segment.realtime.FireHydrant;
import io.druid.segment.realtime.plumber.Plumber;
import io.druid.segment.realtime.plumber.Sink;
import io.druid.segment.realtime.plumber.SinkTap;
import io.druid.segment.realtime.plumber.SinkTapConfig;
import io.druid.segment.realtime.plumber.Sinks;
import io.druid.segment.realtime.plumber.SinksConfig;
import io.druid.timeline.DataSegment;
import io.druid.timeline.partition.NumberedShardSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DruidWriteExample {

  private static final Logger LOG = LoggerFactory.getLogger(DruidWriteExample.class);

  public static void main(String[] args) throws Exception {

    // Create a segment identifier
    final String segmentIdentifier = "test_segment_" + UUID.randomUUID().toString();

    // Define the fire department
    final FireDepartment fireDepartment = new FireDepartment(
        new DataSegmentPusher() {
          @Override
          public String getPathForHadoop(String dataSource) {
            throw new UnsupportedOperationException("Not implemented yet.");
          }

          @Override
          public DataSegment push(File file, DataSegment segment, boolean replaceExisting) throws IOException {
            LOG.info("Pushing segment[%s]", segment.getIdentifier());

            return segment;
          }

          @Override
          public Map<String, Object> makeLoadSpec(File file) {
            throw new UnsupportedOperationException("Not implemented yet.");
          }
        },
        new LocalDataSegmentPusher(new File("/tmp")),
        new IndexMerger(IndexIO.getDefaultIndexIO(),
                        MapBasedInputRow::new,
                        new DruidProcessingConfig())
    );

    // Define the sinks
    final List<Sink> sinks = new ArrayList<>();
    final Map<String, Object> sinkProperties = new HashMap<>();
    sinkProperties.put("type", "local");
    sinkProperties.put("path", "/tmp");
    sinks.add(
        Sinks.create(
            "sink1",
            new SinkTapConfig(
                fireDepartment,
                new SinkTap(
                    "sink1",
                    new QueryableIndexStorageAdapter(
                        QueryableIndex.toQueryableIndex(
                            new File("/tmp/index"),
                            IndexIO.getDefaultIndexIO(),
                            new IndexMerger(IndexIO.getDefaultIndexIO(),
                                            MapBasedInputRow::new,
                                            new DruidProcessingConfig()
)
)
)
),
sinkProperties
)
);
      // Define the plumber
final Plumber plumber = new Plumber(
    SinksConfig.fromList(sinks),
    fireDepartment
);

// Start the plumber
plumber.start();

// Create an input row
final Map<String, Object> row = new HashMap<>();
row.put("timestamp", System.currentTimeMillis());
row.put("dimension1", "value1");
row.put("metric1", 42);

// Convert the input row to JSON
final String json = JSON.toJSONString(row, SerializerFeature.WriteDateUseDateFormat);

// Create a sink tap and write the JSON string to it
final SinkTap sinkTap = new SinkTap("sink1", null);
try (final Smoosh smoosh = Smoosh.forConfig(SinkTapConfig.DEFAULT_SMOOSH_CONFIG)) {
  final SmooshedWriter writer = smoosh.getWriter(sinkTap.getIdentifier());

  writer.writeLine(StringUtils.toUtf8(json));
}

// Stop the plumber
plumber.stop();

// Create a data segment for the sink
final DataSegment dataSegment = new DataSegment(
    "test_datasource",
    fireDepartment.getInterval(),
    "version_" + UUID.randomUUID(),
    Collections.singletonList(new NumberedShardSpec(0, 0)),
    fireDepartment.getDimensions(),
    fireDepartment.getMetrics(),
    fireDepartment.getAggregators(),
    null,
    0,
    1
);

// Push the data segment to Druid
final List<DataSegment> pushedSegments = fireDepartment.getSegmentPusher()
                                                       .push(
                                                           new File("/tmp/index"),
                                                           dataSegment,
                                                           true
                                                       );

LOG.info("Pushed segments: %s", pushedSegments);
}
}
