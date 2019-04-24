package stravastats.reportable;

import stravastats.Analysis;
import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

import java.util.Comparator;

import static stravastats.Config.METERS_PER_MILE;
import static stravastats.Config.MIN_EFFORT_DISTANCE;
import static stravastats.utils.Formatting.formatHundredths;
import static stravastats.utils.Formatting.formatSeconds;

/** This reportable calculates the highest score among all possible distances within an activity. */
public class PerformanceScore extends Reportable {
    public PerformanceScore(String name) {
        this.name = name;
    }

    @Override public int hashCode() {
        // static hash code since all instances are considered equal
        return 0;
    }

    @Override public boolean equals(Object obj) {
        // consider all instances equal (there may be multiple instances of this reportable with different titles,
        // and they should be considered equal when used in collections such as hash sets)
        return obj instanceof PerformanceScore;
    }

    @Override
    public ActivityEffort calculateBestEffort(Activity activity) {
        ActivityEffort bestEffort = null;
        double bestScore = 0;
        // the below can be optimized if needed
        for (int distance = MIN_EFFORT_DISTANCE; distance <= activity.streamData.get(activity.streamData.size()-1).distance; ++distance) {
            ActivityEffort effort = Analysis.calculateBestEffortForDistance(activity, distance);
            if (effort.score > bestScore) {
                bestScore = effort.score;
                bestEffort = effort;
            }
        }
        return bestEffort;
    }

    @Override
    public String formatEffort(ActivityEffort effort) {
        return effort == null ? "" : String.format("%.0f", effort.score);
    }

    @Override
    public String result(Activity activity, ActivityEffort effort) {
        return String.format("%s -- %.1f -- %.0f m, %s (%s/km, %s/mile) -- %.0f - %.0f m at %s - %s",
                activity.startTimeStamp, effort.score,
                effort.distance, formatSeconds(effort.seconds),
                formatHundredths(effort.seconds * 1000 / effort.distance),
                formatHundredths(effort.seconds * METERS_PER_MILE / effort.distance),
                effort.distStart, effort.distEnd, formatSeconds(effort.timeStart), formatSeconds(effort.timeEnd));
    }

    @Override
    public Comparator<Activity> getActivityComparator() {
        return getActivityComparator(e -> e.score, false);
    }
}
