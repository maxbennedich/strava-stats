package stravastats;

import java.util.*;
import java.util.Map.Entry;

import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;
import stravastats.charting.ChartCreator;
import stravastats.reportable.BestEffortDistance;
import stravastats.reportable.Reportable;

import static stravastats.Config.METERS_PER_MILE;
import static stravastats.utils.Formatting.formatSeconds;
import static stravastats.utils.Formatting.formatVariablePrecision;

/** Main class. */
public class StravaStats {
    public static void main(String ... args) throws Exception {
        StravaStats stats = new StravaStats();
        stats.analyze();
    }

    private void analyze() throws Exception {
        List<Activity> activities = Config.ACTIVITY_LOADER.loadActivities();

        // list all activities
        int maxActivityNameLength = 0;
        for (Activity a : activities)
            maxActivityNameLength = Math.max(maxActivityNameLength, a.name.length());

        System.out.println();
        System.out.printf("Date              %-" + maxActivityNameLength + "s  Dist     Time   /km   /mile", "Description");

        for (Reportable effort : Config.ACTIVITY_TYPE.bestEffortDefinitions.getActivityListEfforts())
            System.out.printf("  %-6s", effort.name);

        System.out.println();

        for (Activity a : activities) {
            System.out.printf("%s  %-" + maxActivityNameLength + "s  %-7s  %5s  %4s  %4s  ",
                    a.startTimeStamp, a.name, (int)(a.distance + 0.5) + " m",
                    formatSeconds(a.seconds),
                    formatSeconds(a.seconds * 1000 / a.distance),
                    formatSeconds(a.seconds * METERS_PER_MILE / a.distance));

            for (Reportable effort : Config.ACTIVITY_TYPE.bestEffortDefinitions.getActivityListEfforts())
                System.out.printf(" %-5s  ", effort.formatEffort(a.bestEfforts.get(effort)));

            System.out.println();
        }

        // calculate best score per distance over all activities (the below can be optimized if needed)
        double bestScoreGlobally = Double.MIN_VALUE;

        List<ActivityEffort> overallBestEfforts = new ArrayList<>();
        Map<Activity, List<ActivityEffort>> bestEffortsByActivity = new HashMap<>();
        double maxDist = 0;
        for (Activity a : activities)
            maxDist = Math.max(maxDist,  a.distance);

        for (int distance = Config.MIN_EFFORT_DISTANCE; distance <= maxDist; ++distance) {
            double bestScoreAcrossActivities = 0;
            ActivityEffort bestEffortAcrossActivities = null;
            for (Activity a : activities) {
                ActivityEffort effort = Analysis.calculateBestEffortForDistance(a, distance);
                if (effort != null) {
                    List<ActivityEffort> bestEffortForActivity = bestEffortsByActivity.get(a);
                    if (bestEffortForActivity == null)
                        bestEffortsByActivity.put(a, bestEffortForActivity = new ArrayList<>());
                    bestEffortForActivity.add(effort);

                    if (effort.score > bestScoreAcrossActivities) {
                        bestScoreAcrossActivities = effort.score;
                        bestEffortAcrossActivities = effort;
                    }
                }
            }
            overallBestEfforts.add(bestEffortAcrossActivities);
            bestScoreGlobally = Math.max(bestScoreGlobally, bestScoreAcrossActivities);
        }

        // sanity check that data is valid, and that we have a score for every single meter
        for (Entry<Activity, List<ActivityEffort>> entry : bestEffortsByActivity.entrySet()) {
            for (int idx = 0, distance = Config.MIN_EFFORT_DISTANCE; idx < entry.getValue().size(); ++idx, ++distance) {
                double effortDistance = entry.getValue().get(idx).distance;
                if (effortDistance != distance)
                    throw new IllegalStateException("Invalid distance: " + distance + " != " + effortDistance + " for activity " + entry.getKey().name);
            }
        }

        // calculate and print ranges of best performances
        System.out.println("\nDist range        Score       Best performance for range");
        int curActivityIdx = 0;
        double minScore = Double.MAX_VALUE, maxScore = Double.MIN_VALUE;
        for (int n = 0; n < overallBestEfforts.size(); ++n) {
            ActivityEffort e = overallBestEfforts.get(n);
            minScore = Math.min(minScore, e.score);
            maxScore = Math.max(maxScore, e.score);

            Activity a = e.activity;
            if (n == overallBestEfforts.size() - 1 || a != overallBestEfforts.get(n+1).activity) {
                System.out.printf("%5.0f - %-7s   %3.0f - %3.0f   %s, %.0f m, %s%n", overallBestEfforts.get(curActivityIdx).distance, (int)(e.distance + 0.5) + " m", minScore, maxScore, a.name, a.distance, formatSeconds(a.seconds));
                curActivityIdx = n + 1;
                minScore = Double.MAX_VALUE;
                maxScore = Double.MIN_VALUE;
            }
        }

        // print estimated best time per distance (based on the global top score)
        System.out.println("\nDistance  Est. best   Actual     Diff     Activity");
        for (BestEffortDistance effort : Config.ACTIVITY_TYPE.bestEffortDefinitions.getEstimateBestTimeEfforts()) {
            double estSeconds = Analysis.getEstimatedBestTime(effort.distance, bestScoreGlobally);
            double actualSeconds = Double.MAX_VALUE;
            Activity bestActivity = null;
            for (Activity a : activities) {
                ActivityEffort bestEffort = effort.calculateBestEffort(a);
                if (bestEffort != null) {
                    if (bestEffort.seconds < actualSeconds) {
                        actualSeconds = bestEffort.seconds;
                        bestActivity = a;
                    }
                }
            }

            System.out.printf("%-10s%-8s", effort.name, formatVariablePrecision(estSeconds));
            if (bestActivity != null)
                System.out.printf("    %-8s   %-7s  %s, %.0f m, %s", formatVariablePrecision(actualSeconds), String.format("%.1f", 100 * (actualSeconds / estSeconds - 1)) + " %",
                        bestActivity.name, bestActivity.distance, formatSeconds(bestActivity.seconds));
            System.out.println();
        }

        // print overall top best efforts
        for (Reportable effort : Config.ACTIVITY_TYPE.bestEffortDefinitions.getAllEfforts()) {
            activities.sort(effort.getActivityComparator());
            if (activities.get(0).bestEfforts.get(effort) != null) {
                System.out.println("\n" + effort.name);
                int order = 1;
                for (Activity a : activities.subList(0, Math.min(Config.ACTIVITIES_PER_EFFORT, activities.size()))) {
                    ActivityEffort e = a.bestEfforts.get(effort);
                    if (e != null)
                        System.out.printf("%2d. %s -- %s, %.0f m, %s%n",
                                order++, effort.result(a, e),
                                a.name, a.distance, formatSeconds(a.seconds));
                }
            }
        }

        // create charts and store to disk
        ChartCreator.createChart(overallBestEfforts, 0);
        ChartCreator.createChart(bestEffortsByActivity, Config.MIN_CHART_SCORE, Config.NR_TOP_ACTIVITIES_IN_CHART);
    }
}
