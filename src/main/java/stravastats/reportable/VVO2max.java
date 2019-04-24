package stravastats.reportable;

import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

/**
 * A {@link BestEffortTime} for 6 minutes that also reports vVO2max.
 * <p/>
 * vVO2max: Velocity at maximal oxygen uptake. Exercising faster than this requires pure anaerobic power.
 * Research suggests that vVO2max can be maintained for around 6 minutes.
 * Ref: Billat, Veronique (1999) Interval training at VO2max: Effects on Aerobic Performance and overtraining markers.
 * Medicine and Science in Sports and Exercise, 31 (1), pp. 156-163
 */
public class VVO2max extends BestEffortTime {
    public VVO2max(String name) {
        super(6*60, name);
    }

    @Override
    public String result(Activity activity, ActivityEffort effort) {
        return super.result(activity, effort) + String.format(" -- vVO2max = %.2f m/s (%.0f m)", effort.distance / effort.seconds, effort.distance / 2);
    }
}
