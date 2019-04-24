package stravastats.reportable;

import stravastats.Analysis;
import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

import java.util.Comparator;

import static stravastats.Config.METERS_PER_MILE;
import static stravastats.utils.Formatting.formatHundredths;
import static stravastats.utils.Formatting.formatSeconds;

/** This reportable calculates the best effort (longest distance) for a given time duration for an activity. */
public class BestEffortTime extends Reportable {
    public final int seconds;

    public BestEffortTime(int seconds, String name) {
        this.name = name;
        this.seconds = seconds;
    }

    @Override public int hashCode() { return seconds; }

    @Override public boolean equals(Object obj) { return seconds == ((BestEffortTime)obj).seconds; }

    @Override
    public ActivityEffort calculateBestEffort(Activity activity) {
        return Analysis.calculateBestEffortForTime(activity, seconds);
    }

    @Override
    public String formatEffort(ActivityEffort effort) {
        return effort == null ? "" : String.format("%.0f", effort.distance);
    }

    @Override
    public String result(Activity activity, ActivityEffort effort) {
        return String.format("%s -- %.0f m (%s/km, %s/mile) -- Score: %.0f -- at %s - %s",
                activity.startTimeStamp, effort.distance,
                formatHundredths(effort.seconds * 1000 / effort.distance),
                formatHundredths(effort.seconds * METERS_PER_MILE / effort.distance),
                Analysis.getScore(effort.distance, effort.seconds),
                formatSeconds(effort.timeStart), formatSeconds(effort.timeEnd));
    }

    @Override
    public Comparator<Activity> getActivityComparator() {
        return getActivityComparator(e -> e.distance, false);
    }
}
