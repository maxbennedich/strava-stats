package stravastats.activityloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Simple disk-based cache. */
public class DiskCache implements IActivityCache {
    private static final String CACHE_PATH = "activity-db";

    private Path getPath(String key) {
        return Paths.get(CACHE_PATH, key);
    }

    private void write(String key, String value) throws IOException {
        Files.createDirectories(Paths.get(CACHE_PATH));
        Files.write(getPath(key), value.getBytes());
    }

    private String read(String key) throws IOException {
        Path path = getPath(key);

        if (!Files.exists(path))
            return null;

        return new String(Files.readAllBytes(path));
    }

    private static String getActivitiesKey(String accessToken) {
        return "activities-" + accessToken + ".json";
    }

    private static String getActivityKey(String accessToken, long activityId) {
        return "activity-" + accessToken + "-" + activityId + ".json";
    }

    @Override public void putActivities(String accessToken, String data) throws IOException {
        write(getActivitiesKey(accessToken), data);
    }

    @Override public void putActivity(String accessToken, long id, String data) throws IOException {
        write(getActivityKey(accessToken, id), data);
    }

    @Override public String getActivities(String accessToken) throws IOException {
        return read(getActivitiesKey(accessToken));
    }

    @Override public String getActivity(String accessToken, long activityId) throws IOException {
        return read(getActivityKey(accessToken, activityId));
    }
}