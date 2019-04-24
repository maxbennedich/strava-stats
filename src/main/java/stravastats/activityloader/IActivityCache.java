package stravastats.activityloader;

import java.io.IOException;

/** Cache for storing activity overviews and activities. */
public interface IActivityCache {
    /** Add or update activity overview in cache. */
    void putActivities(String accessToken, String data) throws IOException;

    /** Add or update activity overview in cache. */
    void putActivity(String accessToken, long id, String data) throws IOException;

    /** @return Activity overview for the given athlete, or null if not present in the cache. */
    String getActivities(String accessToken) throws IOException;

    /** @return Activity for the given athlete and activity id, or null if not present in the cache. */
    String getActivity(String accessToken, long activityId) throws IOException;
}
