public class Edge {

    private long sourceId;

    private long targetId;

    private String type;

    public Edge(long sourceId, long targetId, String type) {

        this.sourceId = sourceId;

        this.targetId = targetId;

        this.type = type;

    }

    public long getSourceId() {

        return sourceId;

    }

    public long getTargetId() {

        return targetId;

    }

    public String getType() {

        return type;

    }

    @Override

    public String toString() {

        return sourceId + " --" + type + "--> " + targetId;

    }

}
