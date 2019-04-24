package stravastats.activityloader;

import java.io.IOException;

/** Load whatever activities exist in cache. Don't use Strava API at all. Fully offline. */
public class CacheOnlyActivityProvider implements IActivityProvider {
    private final IActivityCache cache;

    private final String accessToken;

    public CacheOnlyActivityProvider(IActivityCache cache, String accessToken) {
        this.cache = cache;
        this.accessToken = accessToken;
    }

    @Override public String getActivitiesJson() throws IOException {
        String activitiesJson = cache.getActivities(accessToken);

        if (activitiesJson == null)
            throw new IOException("Could not find any activities in cache for athlete = " + accessToken);

        return activitiesJson;
    }

    String getActivityJsonOrNull(long id) throws IOException {
        return cache.getActivity(accessToken, id);
    }

    @Override public String getActivityJson(long id) throws IOException {
        String activityJson = getActivityJsonOrNull(id);

        if (activityJson == null)
            throw new IOException("Could not find activity in cache for athlete = " + accessToken + ", id = " + id);

        return activityJson;
    }
}
