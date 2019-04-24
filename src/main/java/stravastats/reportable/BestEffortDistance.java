package stravastats.reportable;

import stravastats.Analysis;
import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

import java.util.Comparator;

import static stravastats.Config.METERS_PER_MILE;
import static stravastats.utils.Formatting.formatHundredths;
import static stravastats.utils.Formatting.formatSeconds;

/** This reportable calculates the best effort (shortest elapsed time) for a given distance for an activity. */
public class BestEffortDistance extends Reportable {
    public final double distance;

    public BestEffortDistance(double distance, String name) {
        this.name = name;
        this.distance = distance;
    }

    @Override public int hashCode() { return (int)(distance*1000); }

    @Override public boolean equals(Object obj) { return distance == ((BestEffortDistance)obj).distance; }

    @Override
    public ActivityEffort calculateBestEffort(Activity activity) {
        return Analysis.calculateBestEffortForDistance(activity, distance);
    }

    @Override
    public String formatEffort(ActivityEffort effort) {
        return effort == null ? "" : formatSeconds(effort.seconds);
    }

    @Override
    public String result(Activity activity, ActivityEffort effort) {
        return String.format("%s -- %s (%s/km, %s/mile) -- Score: %.0f -- at %.0f - %.0f m",
                activity.startTimeStamp, formatHundredths(effort.seconds),
                formatHundredths(effort.seconds * 1000 / effort.distance),
                formatHundredths(effort.seconds * METERS_PER_MILE / effort.distance),
                Analysis.getScore(effort.distance, effort.seconds),
                effort.distStart, effort.distEnd);
    }

    @Override
    public Comparator<Activity> getActivityComparator() {
        return getActivityComparator(e -> e.seconds, true);
    }
}
