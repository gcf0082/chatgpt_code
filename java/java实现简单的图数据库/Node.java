public class Node {
    private long id;
    private String label;
    private long parentId;

    public Node(long id, String label, long parentId) {
        this.id = id;
        this.label = label;
        this.parentId = parentId;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return id + ":" + label;
    }
}
