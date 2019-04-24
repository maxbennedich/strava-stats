package stravastats.activityloader;

import java.io.IOException;

interface IActivityProvider {
    /** @return Overview list of all activities (JSON). */
    String getActivitiesJson() throws IOException;

    /** @return Stream data of activity for given id (JSON). */
    String getActivityJson(long id) throws IOException;
}
