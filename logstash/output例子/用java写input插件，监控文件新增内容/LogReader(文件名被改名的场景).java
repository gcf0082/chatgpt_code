import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
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
    private Tailer tailer;
    private Thread thread;

    public LogReader(String filename) {
        this.filename = filename;
        this.mapper = new ObjectMapper();
        this.events = new ArrayList<>();
        startTailer();
    }

    private void startTailer() {
        tailer = new Tailer(filename, new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                LogEntry entry = parseLogEntry(line);
                Event event = convertToEvent(entry);
                events.add(event);
                emit(event, null);
            }
        });

        thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
    }

    private void stopTailer() {
        tailer.stop();
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

        Path dir = Paths.get(filename).getParent();
        WatchService watchService = dir.getFileSystem().newWatchService();
        dir.register(watchService, StandardWatchEventKinds.ENTRY_RENAME);

        while (true) {
            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                stopTailer();
                thread.interrupt();
                return events;
            }

            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_RENAME) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path oldFilename = ev.context();
                    if (oldFilename.toString().equals(filename)) {
                        stopTailer();
                        startTailer();
                    }
                }
            }

            if (!watchKey.reset()) {
                stopTailer();
                thread.interrupt();
                return events;
            }
        }
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
