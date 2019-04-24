package stravastats.activity;

import stravastats.reportable.Reportable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static stravastats.utils.Formatting.formatSeconds;

/** This class represents an activity, such as a run or bike ride. */
public class Activity {
    public final long id;
    public final String name;
    public final String startTimeStamp;
    public final String date;

    public double distance;
    public double seconds;
    public List<StreamPoint> streamData;

    public final Map<Reportable, ActivityEffort> bestEfforts = new HashMap<>();

    public Activity(long id, String name, double distance, double seconds, String startDate, List<StreamPoint> streamData) {
        this.id = id;
        this.name = name;
        this.distance = distance;
        this.seconds = seconds;
        this.startTimeStamp = startDate
                .replaceFirst("(\\d)T(\\d)", "$1 $2")
                .replaceFirst("\\:\\d\\dZ", "");
        this.date = startDate.replaceFirst("(.+)T(.+)", "$1");
        this.streamData = streamData;
    }

    @Override public int hashCode() { return Long.hashCode(id); }

    @Override public boolean equals(Object obj) { return id == ((Activity)obj).id; }

    @Override public String toString() {
        return String.format("%s, %.0f m, %s", name, distance, formatSeconds(seconds));
    }

    /** Remove non-moving sections of the activity. See {@link stravastats.Config#REMOVE_NON_MOVING_SECTIONS}. */
    public void removeNonMoving() {
        double totDistance = 0;
        int totSeconds = 0;
        List<StreamPoint> nonMovingData = new ArrayList<>();
        nonMovingData.add(new StreamPoint(0, 0, true));

        for (int k = 0; k < streamData.size(); ++k) {
            StreamPoint sp = streamData.get(k);
            if (sp.moving) {
                double prevDist = k == 0 ? 0 : streamData.get(k-1).distance;
                int prevSeconds = k == 0 ? 0 : streamData.get(k-1).seconds;
                totDistance += sp.distance - prevDist;
                totSeconds += sp.seconds - prevSeconds;
                nonMovingData.add(new StreamPoint(totSeconds, totDistance, true));
            }
        }

        distance = totDistance;
        seconds = totSeconds;
        streamData = nonMovingData;
    }
}