package stravastats.activityloader;

import java.io.IOException;

/** Load existing activities from cache, download new and missing ones via Strava API. */
public class CachedStravaAPIActivityProvider implements IActivityProvider {
    private final CacheOnlyActivityProvider cachedProvider;
    private final StravaAPIActivityProvider apiProvider;

    public CachedStravaAPIActivityProvider(IActivityCache cache, String accessToken) {
        cachedProvider = new CacheOnlyActivityProvider(cache, accessToken);
        apiProvider = new StravaAPIActivityProvider(cache, accessToken);
    }

    @Override public String getActivitiesJson() throws IOException {
        // always go through API to see if there are any new activities, or any activity renames
        return apiProvider.getActivitiesJson();
    }

    @Override public String getActivityJson(long id) throws IOException {
        String activityJson = cachedProvider.getActivityJsonOrNull(id);
        return activityJson != null ? activityJson : apiProvider.getActivityJson(id);
    }
}
