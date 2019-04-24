package stravastats;

import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;
import stravastats.activity.StreamPoint;

import java.util.List;

public class Analysis {
    /**
     * @return An estimated men's world record time for the given distance. Formula developed by applying
     * polynomial regression using actual world record times (as of 2016). Typically within 2 % of the
     * actual world record for distances from 100 meters (with running start) to beyond marathon distance.
     */
    public static double getEstimatedWorldRecordSeconds(double distance) {
        double ld = Math.log(distance);
        return distance / (32.027459 - 6.824489*ld + 0.609759*ld*ld - 0.018826*ld*ld*ld);
    }

    /**
     * @return A performance score from 0 to ~1000 for covering the given distance in the given time,
     * where 1000 equals world record pace, 500 equals 50 % of world record pace, etc. It relies
     * on {@link #getEstimatedWorldRecordSeconds(double)}.
     */
    public static double getScore(double distance, double seconds) {
        return 1000 * getEstimatedWorldRecordSeconds(distance) / seconds;
    }

    /**
     * @return The estimated time in seconds that the given distance can be covered, given the
     * supplied performance score, as defined by {@link #getScore(double, double)}.
     */
    public static double getEstimatedBestTime(double distance, double score) {
        return getEstimatedWorldRecordSeconds(distance) * 1000 / score;
    }

    /** Sliding window best effort for a given distance */
    public static ActivityEffort calculateBestEffortForDistance(Activity activity, double distance) {
        int idxStart = 0, idxEnd = 0;
        double bestTime = Double.MAX_VALUE;
        ActivityEffort bestEffort = null;
        List<StreamPoint> streamData = activity.streamData;

        do {
            double distStart = streamData.get(idxStart).distance;
            double distEnd = streamData.get(idxEnd).distance;
            double totalDistance = distEnd - distStart;
            int timeStart = streamData.get(idxStart).seconds;
            int timeEnd = streamData.get(idxEnd).seconds;
            int totalTime = timeEnd - timeStart;
            if (totalDistance < distance - 0.5) { // i.e. so that 999.6 m would count towards 1 km
                ++idxEnd;
            } else {
                double estimatedTimeForDistance = totalTime / totalDistance * distance;
                if (estimatedTimeForDistance < bestTime)
                    bestEffort = new ActivityEffort(activity, distStart, distEnd, timeStart, timeEnd, distance, bestTime = estimatedTimeForDistance);

                ++idxStart;
            }
        } while (idxEnd < streamData.size());

        return bestEffort;
    }

    /** Sliding window best effort for a given time */
    public static ActivityEffort calculateBestEffortForTime(Activity activity, int seconds) {
        int idxStart = 0, idxEnd = 0;
        double maxDist = 0;
        ActivityEffort bestEffort = null;
        List<StreamPoint> streamData = activity.streamData;

        do {
            double distStart = streamData.get(idxStart).distance;
            double distEnd = streamData.get(idxEnd).distance;
            double totalDistance = distEnd - distStart;
            int timeStart = streamData.get(idxStart).seconds;
            int timeEnd = streamData.get(idxEnd).seconds;
            int totalTime = timeEnd - timeStart;
            if (totalTime < seconds) {
                ++idxEnd;
            } else {
                double estimatedDistanceForTime = totalDistance / totalTime * seconds;
                if (estimatedDistanceForTime > maxDist)
                    bestEffort = new ActivityEffort(activity, distStart, distEnd, timeStart, timeEnd, maxDist = estimatedDistanceForTime, seconds);

                ++idxStart;
            }
        } while (idxEnd < streamData.size());

        return bestEffort;
    }
}
