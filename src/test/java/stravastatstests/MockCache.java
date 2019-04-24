package stravastatstests;

import stravastats.activityloader.IActivityCache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class MockCache implements IActivityCache {
    @Override
    public void putActivities(String accessToken, String data) { }

    @Override
    public void putActivity(String accessToken, long id, String data) { }

    @Override
    public String getActivities(String accessToken) throws IOException {
        return Files.readString(Path.of(TestActivityStats.class.getResource("activities.json").getFile()));
    }

    @Override
    public String getActivity(String accessToken, long activityId) throws IOException {
        return Files.readString(Path.of(TestActivityStats.class.getResource("activity-" + activityId + "m.json").getFile()));
    }
}
