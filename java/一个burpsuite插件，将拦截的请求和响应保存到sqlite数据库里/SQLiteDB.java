public class SQLiteDB implements IMessageEditorTabFactory, IScannerCheck {

    private final String DB_FILE = "burp.sqlite";
    private final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS requests (" +
        "id INTEGER PRIMARY KEY," +
        "url TEXT," +
        "method TEXT," +
        "request TEXT," +
        "response TEXT," +
        "time_received TEXT," +
        "time_sent TEXT" +
        ")";
    private Connection conn;

    public SQLiteDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB_FILE);
            Statement stmt = conn.createStatement();
            stmt.execute(CREATE_TABLE_SQL);
        } catch (ClassNotFoundException | SQLException e) {
            callbacks.printError(e.getMessage());
        }
    }

    @Override
    public IMessageEditorTab createNewInstance(IMessageEditorController controller, boolean editable) {
        return new SQLiteEditorTab(this, controller, editable);
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        addRequestResponseToDatabase(baseRequestResponse);
        return null;
    }

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        addRequestResponseToDatabase(baseRequestResponse);
        return null;
    }

    private void addRequestResponseToDatabase(IHttpRequestResponse requestResponse) {
        String url = callbacks.getHelpers().analyzeRequest(requestResponse).getUrl().toString();
        String method = callbacks.getHelpers().analyzeRequest(requestResponse).getMethod();
        String request = new String(requestResponse.getRequest());
        String response = new String(requestResponse.getResponse());
        String timeReceived = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(requestResponse.getTimeStamp()));
        String timeSent = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO requests(url, method, request, response, time_received, time_sent) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, url);
            stmt.setString(2, method);
            stmt.setString(3, request);
            stmt.setString(4, response);
            stmt.setString(5, timeReceived);
            stmt.setString(6, timeSent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            callbacks.printError(e.getMessage());
        }
    }
}
