package stravastats.reportable;

import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;

import java.util.Comparator;
import java.util.function.Function;

/**
 * A reportable turns an {@link Activity} into an {@link ActivityEffort} (i.e. selects a
 * segment out of the activity) according to some logic, and can also sort and display activities
 * based on that logic.
 * <p/>
 * Example reportables are to calculate and report the fastest consecutive 400 meters
 * within an activity, or the Cooper test score for an activity.
 */
public abstract class Reportable {
    public String name;

    public abstract ActivityEffort calculateBestEffort(Activity activity);
    public abstract String formatEffort(ActivityEffort effort);
    public abstract String result(Activity activity, ActivityEffort effort);
    public abstract Comparator<Activity> getActivityComparator();

    Comparator<Activity> getActivityComparator(Function<ActivityEffort, Double> func, boolean ascending) {
        return (Activity a, Activity b) -> {
            ActivityEffort e1 = a.bestEfforts.get(this);
            ActivityEffort e2 = b.bestEfforts.get(this);
            double t1 = e1 == null ? ascending ? Double.MAX_VALUE : Double.MIN_VALUE : func.apply(e1);
            double t2 = e2 == null ? ascending ? Double.MAX_VALUE : Double.MIN_VALUE : func.apply(e2);
            return ascending ? Double.compare(t1, t2) : Double.compare(t2, t1);
        };
    }

    @Override public String toString() { return name; }
}

