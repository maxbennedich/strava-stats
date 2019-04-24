package stravastats.activityloader;

import stravastats.besteffort.BestEffortsBike;
import stravastats.besteffort.BestEffortsRun;
import stravastats.besteffort.IBestEffortDefinitions;

public enum ActivityType {
    RUN("Run", new BestEffortsRun()),
    BIKE("Ride", new BestEffortsBike()),
    ;

    private final String key;

    public final IBestEffortDefinitions bestEffortDefinitions;

    ActivityType(String key, IBestEffortDefinitions bestEffortDefinitions) {
        this.key = key;
        this.bestEffortDefinitions = bestEffortDefinitions;
    }

    public boolean matches(String activityType) {
        return key.equals(activityType);
    }
}
