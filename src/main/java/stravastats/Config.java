package stravastats;

import stravastats.activityloader.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    /**
     * Minimum distance to collect stats for. Shorter distances are increasingly susceptible to GPS errors.
     * For data with poor GPS accuracy, this might have to be increased. (It would be obvious from having
     * volatile/unrealistic data at short distances.)
     */
    public static final int MIN_EFFORT_DISTANCE = 100;

    /** Start of Y axis in the top-N activities chart. Adjust as needed to create a pleasing chart. */
    public static final int MIN_CHART_SCORE = 450;

    /** Only include the top activities per distance, to not clutter the graph. */
    public static final int NR_TOP_ACTIVITIES_IN_CHART = 3;

    /** How many activities to display per effort (e.g. best 1 km or 10 minutes run). */
    public static final int ACTIVITIES_PER_EFFORT = 10;

    /** Only activities of this type will be loaded and analyzed. */
    public static final ActivityType ACTIVITY_TYPE = ActivityType.RUN;

    /** Set to true to only use cache, not the Strava API at all. Useful during development. */
    private static final boolean OFFLINE_MODE = false;

    /** Specify activity type and activity provider here. */
    public static final ActivityLoader ACTIVITY_LOADER = new ActivityLoader(
            ACTIVITY_TYPE,
            OFFLINE_MODE ?
                    new CacheOnlyActivityProvider(new DiskCache(), getAthleteAccessToken()) :
                    new CachedStravaAPIActivityProvider(new DiskCache(), getAthleteAccessToken()));

    /**
     * Set this flag to true to remove sections of activities where no movement was reported, such as when
     * standing still at a red light or stopping to look at a map.
     * <p/>
     * Removing non-moving sections often creates more accurate calculations of average pace, however it
     * should be used with caution since it can result in inflated scores, and makes it possible to cheat.
     * For example, to create a really fast 5k run, one could do 25x200 meter sprints, with a minute or two
     * of rest in between each one to recover.
     * <p/>
     * Note that non-moving sections are removed by default in Strava, except for activities with type "Race".
     * <p/>
     * TODO: It probably makes sense to allow configuring this per activity, and/or always display the
     * raw pace in addition to the moving pace.
     */
    public static final boolean REMOVE_NON_MOVING_SECTIONS = true;

    public static final double METERS_PER_MILE = 1609.34;

    private static String getAthleteAccessToken() {
        try {
            Properties athleteProperties = new Properties();
            athleteProperties.load(new FileInputStream("athlete.properties"));
            return athleteProperties.getProperty("accesstoken");
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to read 'athlete.properties'. Make sure the file exists in the project root " +
                    "directory, and contains the property 'accesstoken', which should be a 40 character " +
                    "hexadecimal Strava API access token for the athlete to be analyzed.", e);
        }
    }
}
