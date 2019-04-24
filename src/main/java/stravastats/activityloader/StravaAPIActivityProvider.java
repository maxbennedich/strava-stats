package stravastats.activityloader;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/** Download all activities via Strava API and overwrite cache. */
public class StravaAPIActivityProvider implements IActivityProvider {
    /** For more than this, paging needs to be implemented (which isn't very difficult, it just hasn't been needed). */
    private static final int MAX_SUPPORTED_ACTIVITIES = 199;

    private final IActivityCache cache;

    private final String accessToken;

    public StravaAPIActivityProvider(IActivityCache cache, String accessToken) {
        this.cache = cache;
        this.accessToken = accessToken;
    }

    @Override public String getActivitiesJson() throws IOException {
        // TODO: paging to support more activities
        String activitiesJson = getAPIResponse("https://www.strava.com/api/v3/athlete/activities?per_page=" + (MAX_SUPPORTED_ACTIVITIES+1));
        cache.putActivities(accessToken, activitiesJson);

        int nrActivities = new JSONArray(activitiesJson).length();
        if (nrActivities > MAX_SUPPORTED_ACTIVITIES)
            throw new IllegalStateException("Too many activities! (>" + MAX_SUPPORTED_ACTIVITIES + ")");

        return activitiesJson;
    }

    @Override public String getActivityJson(long id) throws IOException {
        String activityJson = getAPIResponse("https://www.strava.com/api/v3/activities/" + id + "/streams/time,distance,moving");
        cache.putActivity(accessToken, id, activityJson);
        return activityJson;
    }

    private String getAPIResponse(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        return response.toString();
    }
}
