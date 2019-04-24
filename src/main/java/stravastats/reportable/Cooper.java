package stravastats.reportable;

import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

/**
 * A {@link BestEffortTime} for 12 minutes that is used to report the Cooper test result and VO2 max.
 * <p/>
 * Cooper test: distance run in 12 minutes<br/>
 * VO2 max: maximal oxygen uptake in ml/kg/min
 */
public class Cooper extends BestEffortTime {
    public Cooper(String name) {
        super(12*60, name);
    }

    private static double vo2Max(double distanceIn12Min) {
        return (distanceIn12Min - 504.9) / 44.73;
    }

    @Override
    public String result(Activity activity, ActivityEffort effort) {
        return super.result(activity, effort) + String.format(" -- VO2 max = %.2f", vo2Max(effort.distance));
    }
}
