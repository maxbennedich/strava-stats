package stravastats.besteffort;

import stravastats.reportable.*;

/**
 * TODO: This interface could be extended to contain more than just best effort
 * definitions, such as performance score formula, and stats/chart configuration.
 */
public interface IBestEffortDefinitions {
    /** Best efforts that will be included as columns in the list of all activities. */
    Reportable[] getActivityListEfforts();

    /** Each of the following best efforts will be reported as a list of ranked activities. */
    Reportable[] getAllEfforts();

    /** Distances for which estimated best times will be reported. */
    BestEffortDistance[] getEstimateBestTimeEfforts();
}
