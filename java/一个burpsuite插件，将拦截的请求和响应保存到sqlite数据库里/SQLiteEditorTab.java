public class SQLiteEditorTab implements IMessageEditorTab {

    private IMessageEditorController controller;
    private JTextArea textArea;
    private byte[] currentMessage;
    private SQLiteDB sqliteDB;

    public SQLiteEditorTab(SQLiteDB sqliteDB, IMessageEditorController controller, boolean editable) {
        this.sqliteDB = sqliteDB;
        this.controller = controller;

        textArea = new JTextArea();
        textArea.setEditable(editable);
    }

    @Override
    public String getTabCaption() {
        return "SQLite DB";
    }

    @Override
    public Component getUiComponent() {
        return textArea;
    }

    @Override
    public boolean isEnabled(byte[] content, boolean isRequest) {
        return true;
    }

    @Override
    public void setMessage(byte[] content, boolean isRequest) {
        if (content == null) {
            textArea.setText("");
            return;
        }

        currentMessage = content;
        textArea.setText(new String(content));
    }

    @Override
    public byte[] getMessage() {
        return currentMessage;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public byte[] getSelectedData() {
        return textArea.getSelectedText().getBytes();
    }

    @Override
    public IMessageEditorTab cloneTab() {
        return new SQLiteEditorTab(sqliteDB, controller, true);
    }

    public void updateDatabase() {
        if (currentMessage == null) {
            return;
        }

        boolean isRequest = controller.isRequestEditor();
        IHttpRequestResponse message = callbacks.saveBuffersToTempFiles(new IHttpRequestResponse[] { controller.getHttpService(), new CustomHttpRequestResponse(currentMessage, isRequest) })[1];
        sqliteDB.doPassiveScan(message);
    }

    private class CustomHttpRequestResponse implements IHttpRequestResponse {
        private final byte[] message;
        private final boolean isRequest;

        public CustomHttpRequestResponse(byte[] message, boolean isRequest) {
            this.message = message;
            this.isRequest = isRequest;
        }

        @Override
        public byte[] getRequest() {
            return isRequest ? message : null;
        }

        @Override
        public void setRequest(byte[] message) {}

        @Override
        public byte[] getResponse() {
            return !isRequest ? message : null;
        }

        @Override
        public void setResponse(byte[] message) {}

        @Override
        public String getComment() {
            return null;
        }

        @Override
        public void setComment(String comment) {}

        @Override
        public String getHighlight() {
            return null;
        }

        @Override
        public void setHighlight(String color) {}
    }
}
