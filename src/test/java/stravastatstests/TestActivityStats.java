package stravastatstests;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;
import stravastats.activity.Activity;
import stravastats.activityloader.ActivityLoader;
import stravastats.activityloader.ActivityType;
import stravastats.activityloader.CacheOnlyActivityProvider;
import stravastats.reportable.*;

import java.io.IOException;
import java.util.List;

/** Load a few sample activities (JSON files) and test various reportables. */
public class TestActivityStats {
    private static List<Activity> loadActivities() {
        try {
            ActivityLoader activityLoader = new ActivityLoader(
                    ActivityType.RUN,
                    new CacheOnlyActivityProvider(new MockCache(), null));
            return activityLoader.loadActivities();
        } catch (IOException ioe) {
            throw new AssertionError(ioe);
        }
    }

    private static final int A = 2743;
    private static final int B = 4749;
    private static final int C = 10233;

    private static final List<Activity> ACTIVITIES = loadActivities();

    private static void assertOrder(Reportable reportable, int... order) {
        ACTIVITIES.sort(reportable.getActivityComparator());
        assertArrayEquals(order, ACTIVITIES.stream().mapToInt(a -> (int)Math.round(a.distance)).toArray());
    }

    private static void assertDistanceOrder(int distance, int... order) {
        assertOrder(new BestEffortDistance(distance, "test"), order);
    }

    private static void assertTimeOrder(int seconds, int... order) {
        assertOrder(new BestEffortTime(seconds, "test"), order);
    }

    @Test
    public void testDistanceOrder() {
        assertDistanceOrder(100, C, B, A);
        assertDistanceOrder(500, B, A, C);
        assertDistanceOrder(1000, A, B, C);
        assertDistanceOrder(2500, A, B, C);
        assertDistanceOrder(3000, B, C, A); // A is last since it's shorter than 3000 m
        assertDistanceOrder(5000, C, B, A); // C is first since it's the only activity > 5000 m
    }

    @Test
    public void testTimeOrder() {
        assertTimeOrder(20, C, B, A);
        assertTimeOrder(2*60, B, A, C);
        assertTimeOrder(5*60, A, B, C);
        assertTimeOrder(10*60, A, B, C);
        assertTimeOrder(12*60, B, C, A); // A is last since it's less than 12 minutes
        assertTimeOrder(30*60, C, B, A); // C is first since it's the only activity > 30 minutes
    }

    @Test
    public void testPerformanceOrder() {
        assertOrder(new PerformanceScore("test"), B, A, C);
    }

    @Test
    public void testCooperOrder() {
        assertOrder(new Cooper("test"), B, C, A); // A is last since it's less than 12 minutes
    }

    @Test
    public void testVVO2maxOrder() {
        assertOrder(new VVO2max("test"), A, B, C);
    }
}

