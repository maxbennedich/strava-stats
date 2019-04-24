package stravastats.activity;

import stravastats.Analysis;

import static stravastats.Config.METERS_PER_MILE;
import static stravastats.utils.Formatting.formatHundredths;
import static stravastats.utils.Formatting.formatSeconds;

/** A section (effort) within an activity. */
public class ActivityEffort {
    public final Activity activity;
    public final double distStart;
    public final double distEnd;
    public final int timeStart;
    public final int timeEnd;
    public final double distance;
    public final double seconds;
    public final double score;

    public ActivityEffort(Activity activity, double distStart, double distEnd, int timeStart, int timeEnd, double distance, double seconds) {
        this.activity = activity;
        this.distStart = distStart;
        this.distEnd = distEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.distance = distance;
        this.seconds = seconds;
        this.score = Analysis.getScore(distance, seconds);
    }

    @Override public String toString() {
        return String.format("%s -- %.1f -- %.0f m, %s (%s/km, %s/mile) -- %.0f - %.0f m at %s - %s",
                activity.name, score,
                distance, formatSeconds(seconds),
                formatHundredths(seconds * 1000 / distance),
                formatHundredths(seconds * METERS_PER_MILE / distance),
                distStart, distEnd, formatSeconds(timeStart), formatSeconds(timeEnd));
    }
}
