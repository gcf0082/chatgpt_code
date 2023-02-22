import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.logstash.Event;

public class LogReader {
    private final String filename;
    private final ObjectMapper mapper;
    private final List<Event> events;

    public LogReader(String filename) {
        this.filename = filename;
        this.mapper = new ObjectMapper();
        this.events = new ArrayList<>();
    }

    public List<Event> read() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = parseLogEntry(line);
                Event event = convertToEvent(entry);
                events.add(event);
            }
        }

        // Tail the file to read new lines
        Tailer tailer = new Tailer(filename, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                LogEntry entry = parseLogEntry(line);
                Event event = convertToEvent(entry);
                events.add(event);
                emit(event, null);
            }
        });
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();

        return events;
    }

    private LogEntry parseLogEntry(String line) {
        LogEntry entry = new LogEntry();
        String[] parts = line.split("\\|");
        LocalDateTime timestamp = LocalDateTime.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        entry.setTimestamp(timestamp);
        entry.setMessage(parts[1]);
        return entry;
    }

    private Event convertToEvent(LogEntry entry) {
        Event event = new Event();
        event.setField("@timestamp", entry.getTimestamp());
        event.setField("message", entry.getMessage());
        return event;
    }
}
