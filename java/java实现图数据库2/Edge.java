public class Edge {
    private String label;
    private Node from;
    private Node to;
    private Map<String, Object> properties;

    public Edge(String label, Node from, Node to) {
        this.label = label;
        this.from = from;
        this.to = to;
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

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }
}
