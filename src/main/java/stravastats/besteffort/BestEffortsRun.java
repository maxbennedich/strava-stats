package stravastats.besteffort;

import stravastats.reportable.*;

import static stravastats.besteffort.BestEffortUtils.bed;
import static stravastats.besteffort.BestEffortUtils.bet;

public class BestEffortsRun implements IBestEffortDefinitions {
    @Override public Reportable[] getActivityListEfforts() { return ACTIVITY_LIST; }

    @Override public Reportable[] getAllEfforts() { return ALL; }

    @Override public BestEffortDistance[] getEstimateBestTimeEfforts() { return ESTIMATE_BEST_TIME; }

    private static final Reportable[] ACTIVITY_LIST = {
            new PerformanceScore("Score"),

            new Cooper("Cooper"),
            bet(20*60, "20 min"),

            bed(400, "400 m"),
            bed(804.67, "½ mile"),
            bed(1000, "1 km"),
            bed(1609.34, "1 mile"),
            bed(2500, "2.5 km"),
            bed(5000, "5 km"),
            bed(7500, "7.5 km"),
            bed(10000, "10 km"),
    };

    private static final Reportable[] ALL = {
            new PerformanceScore("Top performances"),

            bet(20, "20 s"),
            bet(30, "30 s"),
            bet(1*60, "1 min"),
            bet(2*60, "2 min"),
            bet(3*60, "3 min"),
            bet(4*60, "4 min"),
            bet(5*60, "5 min"),
            new VVO2max("6 min (vVO2max)"),
            bet(10*60, "10 min"),
            new Cooper("12 min (Cooper)"),
            bet(20*60, "20 min"),
            bet(30*60, "30 min"),
            bet(45*60, "45 min"),
            bet(60*60, "60 min"),

            bed(100, "100 m"),
            bed(200, "200 m"),
            bed(400, "400 m"),
            bed(500, "500 m"),
            bed(800, "800 m"),
            bed(804.67, "1/2 mile"),
            bed(1000, "1 km"),
            bed(1500, "1.5 km"),
            bed(1609.34, "1 mile"),
            bed(2000, "2 km"),
            bed(2500, "2.5 km"),
            bed(3000, "3 km"),
            bed(3218.69, "2 miles"),
            bed(4000, "4 km"),
            bed(4828.03, "3 miles"),
            bed(5000, "5 km"),
            bed(6000, "6 km"),
            bed(6437.38, "4 miles"),
            bed(7000, "7 km"),
            bed(7500, "7.5 km"),
            bed(8000, "8 km"),
            bed(8046.72, "5 miles"),
            bed(9000, "9 km"),
            bed(9656.06, "6 miles"),
            bed(10000, "10 km"),
    };

    private static final BestEffortDistance[] ESTIMATE_BEST_TIME = {
            bed(100, "100 m"),
            bed(200, "200 m"),
            bed(400, "400 m"),
            bed(800, "800 m"),
            bed(1000, "1 km"),
            bed(1500, "1.5 km"),
            bed(1609.34, "1 mile"),
            bed(2000, "2 km"),
            bed(2500, "2.5 km"),
            bed(5000, "5 km"),
            bed(10000, "10 km"),
            bed(21097, "Half mar"),
            bed(42195, "Marathon"),
    };
}
