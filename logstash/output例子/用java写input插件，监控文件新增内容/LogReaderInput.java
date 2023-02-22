import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.logstash.plugins.Configuration;
import org.logstash.plugins.inputs.AbstractInput;
import org.logstash.plugins.inputs.InputFactory;

import java.io.IOException;
import java.util.Map;

public class LogReaderInput extends AbstractInput {
    private static final Logger logger = LogManager.getLogger(LogReaderInput.class);
    private final String filename;

    public LogReaderInput(String id, Configuration config) {
        super(id, config);
        this.filename = config.getString("filename");
    }

    @Override
    public void start() {
        try {
            LogReader reader = new LogReader(filename);
            for (Event event : reader.read()) {
                this.emit(event, null);
            }
        } catch (IOException e) {
            logger.error("Failed to read log file: {}", e.getMessage());
        }
    }

    public static class Factory implements InputFactory {
        @Override
        public LogReaderInput create(String id, Configuration config, Context context) {
            return new LogReaderInput(id, config);
        }
    }
}
