package info.mores.weedon.Model;

/**
 * Created by Skipper on 25-03-2018.
 */

public class SeenPojo {
    private static SeenPojo seenPojo = null;

    public  boolean seen;
    public long timestamp;

    public SeenPojo() {
    }

    public SeenPojo(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public static SeenPojo getInstance()
    {
        if (seenPojo == null) {
            seenPojo = new SeenPojo();
        }
    return seenPojo;
    }

    public static SeenPojo getInstance(boolean seen, long timestamp)
    {
        if (seenPojo == null) {
            seenPojo = new SeenPojo(seen,timestamp);
        }
        return seenPojo;
    }

    public  boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
