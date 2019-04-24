package stravastats.activityloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import stravastats.*;
import stravastats.activity.Activity;
import stravastats.activity.StreamPoint;
import stravastats.reportable.Reportable;

/** This class loads and processes activities using a supplied {@link IActivityProvider}. */
public class ActivityLoader {
    private final ActivityType activityType;

    private final IActivityProvider activityProvider;

    public ActivityLoader(ActivityType activityType, IActivityProvider activityProvider) {
        this.activityType = activityType;
        this.activityProvider = activityProvider;
    }

    public List<Activity> loadActivities() throws IOException {
        // first get a list of all activities
        String jsonData = activityProvider.getActivitiesJson();
        JSONArray activitiesJson = new JSONArray(jsonData);

        // now load and process one activity at a time
        List<Activity> activities = new ArrayList<>();
        for (int k = 0; k < activitiesJson.length(); ++k) {
            JSONObject json = activitiesJson.getJSONObject(k);
            try {
                if (!activityType.matches(json.getString("type")))
                    continue;

                long id = json.getLong("id");
                Activity activity = new Activity(id, json.getString("name"), json.getDouble("distance"), json.getDouble("elapsed_time"), json.getString("start_date_local"), getActivityStream(id));
                if (Config.REMOVE_NON_MOVING_SECTIONS)
                    activity.removeNonMoving();
                System.out.println("Adding activity " + activity.name + " (" + activity.startTimeStamp + ")");
                addBestEfforts(activity);
                activities.add(activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return activities;
    }

    private void addBestEfforts(Activity activity) {
        for (Reportable effort : Config.ACTIVITY_TYPE.bestEffortDefinitions.getAllEfforts())
            activity.bestEfforts.put(effort, effort.calculateBestEffort(activity));
    }

    private JSONObject getDataForType(JSONArray streamData, String type) {
        for (int i = 0; i < streamData.length(); ++i) {
            JSONObject data = streamData.getJSONObject(i);
            if (data.getString("type").equals(type))
                return data;
        }
        throw new AssertionError("Missing data type '" + type + "'. JSON: " + streamData);
    }

    private List<StreamPoint> getActivityStream(long id) throws IOException {
        String streamDataJson = activityProvider.getActivityJson(id);

        JSONArray streamData = new JSONArray(streamDataJson);
        assert streamData.length() == 3;

        JSONObject timeData = getDataForType(streamData, "time");
        JSONObject distData = getDataForType(streamData, "distance");
        JSONObject movingData = getDataForType(streamData, "moving");

        JSONArray timeDataArray = timeData.getJSONArray("data");
        JSONArray distDataArray = distData.getJSONArray("data");
        JSONArray movingDataArray = movingData.getJSONArray("data");
        assert timeDataArray.length() == distDataArray.length();
        assert timeDataArray.length() == movingDataArray.length();

        List<StreamPoint> activityStream = new ArrayList<>();
        for (int k = 0; k < timeDataArray.length(); ++k)
            activityStream.add(new StreamPoint(timeDataArray.getInt(k), distDataArray.getDouble(k), movingDataArray.getBoolean(k)));

        return activityStream;
    }
}
