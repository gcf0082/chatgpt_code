public class Node {
    private String label;
    private Map<String, Object> properties;

    public Node(String label) {
        this.label = label;
        this.properties = new HashMap<>();
    }

    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    public Object getProperty(String key) {
        return this.properties.get(key);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
