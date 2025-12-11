package druyaned.aston.intensive.userevents;

/**
 * Prep#03: provides properties of {@code kafka.topics.user-events}.
 *
 * @author druyaned
 */
public class UserEventsProperties {

    private String name;
    private int partitionsCount;
    private int replicasCount;

    public UserEventsProperties() {}

    public UserEventsProperties(String name, int partitionsCount, int replicasCount) {
        this.name = name;
        this.partitionsCount = partitionsCount;
        this.replicasCount = replicasCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPartitionsCount() {
        return partitionsCount;
    }

    public void setPartitionsCount(int partitionsCount) {
        this.partitionsCount = partitionsCount;
    }

    public int getReplicasCount() {
        return replicasCount;
    }

    public void setReplicasCount(int replicasCount) {
        this.replicasCount = replicasCount;
    }

    @Override
    public String toString() {
        return "UserEventsProperties{" + "name=" + name + ", partitionsCount=" + partitionsCount
                + ", replicasCount=" + replicasCount + '}';
    }
}
