package stravastats.besteffort;

import stravastats.reportable.BestEffortDistance;
import stravastats.reportable.BestEffortTime;

public class BestEffortUtils {
    static BestEffortDistance bed(double distance, String name) {
        return new BestEffortDistance(distance, name);
    }

    static BestEffortTime bet(int seconds, String name) {
        return new BestEffortTime(seconds, name);
    }
}
