package stravastats.activity;

/** Single data point within an activity. */
public class StreamPoint {
    public final int seconds;
    public final double distance;
    public final boolean moving;

    public StreamPoint(int seconds, double distance, boolean moving) {
        this.seconds = seconds;
        this.distance = distance;
        this.moving = moving;
    }
}
