package stravastats.besteffort;

import stravastats.reportable.*;

import static stravastats.besteffort.BestEffortUtils.bed;
import static stravastats.besteffort.BestEffortUtils.bet;

public class BestEffortsBike implements IBestEffortDefinitions {
    @Override public Reportable[] getActivityListEfforts() { return ACTIVITY_LIST; }

    @Override public Reportable[] getAllEfforts() { return ALL; }

    @Override public BestEffortDistance[] getEstimateBestTimeEfforts() { return ESTIMATE_BEST_TIME; }

    private static final Reportable[] ACTIVITY_LIST = {
            new PerformanceScore("Score"),

            bet(30*60, "30 min"),
            bet(60*60, "1 h"),

            bed(400,   "400 m"),
            bed(1000,  "1 km"),
            bed(5000,  "5 km"),
            bed(10000, "10 km"),
            bed(20000, "20 km"),
            bed(50000, "50 km"),
    };

    private static final Reportable[] ALL = {
            new PerformanceScore("Top performances"),

            bet(1*60, "1 min"),
            bet(2*60, "2 min"),
            bet(5*60, "5 min"),
            bet(10*60, "10 min"),
            bet(20*60, "20 min"),
            bet(30*60, "30 min"),
            bet(60*60, "1 h"),
            bet(2*60*60, "2 h"),
            bet(3*60*60, "3 h"),

            bed(400, "400 m"),
            bed(500, "500 m"),
            bed(800, "800 m"),
            bed(1000, "1 km"),
            bed(1609.34, "1 mile"),
            bed(2000, "2 km"),
            bed(2500, "2.5 km"),
            bed(3218.69, "2 miles"),
            bed(5000, "5 km"),
            bed(7500, "7.5 km"),
            bed(8046.72, "5 miles"),
            bed(10000, "10 km"),
            bed(16093.4, "10 miles"),
            bed(20000, "20 km"),
            bed(25000, "25 km"),
            bed(32186.9, "20 miles"),
            bed(50000, "50 km"),
            bed(75000, "75 km"),
            bed(80467.2, "50 miles"),
    };

    private static final BestEffortDistance[] ESTIMATE_BEST_TIME = {
            bed(400, "400 m"),
            bed(500, "500 m"),
            bed(1000, "1 km"),
            bed(1609.34, "1 mile"),
            bed(5000, "5 km"),
            bed(10000, "10 km"),
            bed(16093.4, "10 miles"),
            bed(20000, "20 km"),
            bed(50000, "50 km"),
            bed(100000, "100 km"),
    };
}
