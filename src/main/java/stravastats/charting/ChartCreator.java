package stravastats.charting;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.LogFormat;
import org.jfree.data.xy.DefaultXYDataset;
import stravastats.activity.Activity;
import stravastats.activity.ActivityEffort;
import stravastats.utils.Pair;

/** This class is responsible for generating the performance charts. It uses JFreeChart. */
public class ChartCreator {
    private static final int CHART_WIDTH = 1920;
    private static final int CHART_HEIGHT = 1080;

    private static final int GRAPH_COLOR = 0x0077CC; // blue: 3072F3 orange: ff9900

    private static final int GRAPH_POINTS = 1000;

    private static final int[] SERIES_COLOR = {0xB0171F, 0xFFAEB9, 0xCD1076, 0xBA55D3, 0x7A67EE, 0x3A5FCD, 0x00B2EE, 0x00CED1,
            0x00CD66, 0xA2CD5A, 0xCDCD00, 0xEEB422, 0xEE7600, 0xEE4000, 0x1E1E1E, 0x848484, 0xB9D3EE, 0x00C78C};

    private static double[][] getEffortData(List<ActivityEffort> stats, int minChartScore) {
        double minDist = stats.get(0).distance;
        double maxDist = stats.get(stats.size() - 1).distance;
        List<Pair<Double, Double>> data = new ArrayList<>();
        for (int x = 0, statsIdx = 0; x < GRAPH_POINTS; ++x) {
            double dist = minDist + (x+1) * (maxDist - minDist) / GRAPH_POINTS;
            int count = 0;
            double sumScore = 0;
            double d0 = stats.get(statsIdx).distance;
            while (statsIdx < stats.size() && stats.get(statsIdx).distance <= dist) {
                ++count;
                sumScore += stats.get(statsIdx).score;
                ++statsIdx;
            }

            if (count > 0) {
                double d1 = stats.get(statsIdx-1).distance;
                double score = sumScore / count;
                if (score >= minChartScore)
                    data.add(new Pair<>((d0+d1)/2, score));
            }
        }

        double[][] dataArray = new double[2][data.size()];
        for (int n = 0; n < data.size(); ++n) {
            dataArray[0][n] = data.get(n).p;
            dataArray[1][n] = data.get(n).q;
        }
        return dataArray;
    }

    private static void saveChart(JFreeChart chart, String name) {
        try {
            ChartUtilities.saveChartAsPNG(new File(name), chart, CHART_WIDTH, CHART_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return A mapping from activity to a unique name. This is accomplished by prepending the activity date or timestamp as needed to
     * achieve a set of unique names. This is needed since activities are frequently given the same name (such as "Morning Run").
     */
    private static Map<Activity, String> getUniqueNameMapping(Set<Activity> activities) {
        Set<String> names = new HashSet<>();
        Set<String> namesAndDates = new HashSet<>();
        Set<String> duplicateNames = new HashSet<>();
        Set<String> duplicateNameAndDates = new HashSet<>();

        for (Activity activity : activities) {
            if (!names.add(activity.name))
                duplicateNames.add(activity.name);
            if (!namesAndDates.add(activity.name + " " + activity.date))
                duplicateNameAndDates.add(activity.name);
        }

        duplicateNames.removeAll(duplicateNameAndDates);

        Map<Activity, String> uniqueNameMapping = new HashMap<>();
        for (Activity activity : activities) {
            if (duplicateNameAndDates.contains(activity.name))
                uniqueNameMapping.put(activity, activity.name + " " + activity.startTimeStamp);
            else if (duplicateNames.contains(activity.name))
                uniqueNameMapping.put(activity, activity.name + " " + activity.date);
            else
                uniqueNameMapping.put(activity, activity.name);
        }

        return uniqueNameMapping;
    }

    /** Create a chart of the top performances per meter. */
    public static void createChart(Map<Activity, List<ActivityEffort>> bestEffortsByActivity, int minChartScore, int nrTopActivities) {
        Set<Activity> activitiesToInclude = new HashSet<>();

        int maxIdx = 0;
        for (List<ActivityEffort> e : bestEffortsByActivity.values())
            maxIdx = Math.max(maxIdx, e.size());

        for (int idx = 0; idx < maxIdx; ++idx) {
            List<Pair<Activity, Double>> activities = new ArrayList<>();

            for (Entry<Activity, List<ActivityEffort>> entry : bestEffortsByActivity.entrySet())
                if (entry.getValue().size() > idx)
                    activities.add(new Pair<>(entry.getKey(), entry.getValue().get(idx).score));

            Collections.sort(activities, (e1, e2) -> e2.q.compareTo(e1.q));
            for (int n = 0; n < Math.min(activities.size(), nrTopActivities); ++n)
                activitiesToInclude.add(activities.get(n).p);
        }

        Map<Activity, String> uniqueNameMapping = getUniqueNameMapping(activitiesToInclude);

        DefaultXYDataset dataset = new DefaultXYDataset();
        for (Activity activity : activitiesToInclude)
            dataset.addSeries(uniqueNameMapping.get(activity), getEffortData(bestEffortsByActivity.get(activity), minChartScore));

        JFreeChart chart = ChartFactory.createXYLineChart("Performance Score", "Distance (m)", "Score", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYItemRenderer r = plot.getRenderer();
        for (int s = 0; s < dataset.getSeriesCount(); ++s) {
            r.setSeriesPaint(s, new Color(SERIES_COLOR[s % SERIES_COLOR.length]));
            r.setSeriesStroke(s, new BasicStroke(2));
        }

        NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);

        saveChart(chart, "chart-series.png");

        // Create same chart with log axis. More useful for short distances.
        LogAxis logAxis = new LogAxis("Log Dist");
        logAxis.setBase(10);
        logAxis.setNumberFormatOverride(new MyFormat(logAxis.getBase()));
        plot.setDomainAxis(logAxis);

        saveChart(chart, "chart-series-log.png");
    }

    @SuppressWarnings("serial")
    static class MyFormat extends LogFormat {
        MyFormat(double base) {
            super(base, "", "", false);
        }

        @Override
        public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
            toAppendTo.append((int)(number+0.5));
            return toAppendTo;
        }
    }

    /** Create a chart of best performance score per meter. */
    public static void createChart(List<ActivityEffort> stats, int minChartScore) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        dataset.addSeries("run0", getEffortData(stats, minChartScore));

        JFreeChart chart = ChartFactory.createXYLineChart("Performance Score", "Distance (m)", "Score", dataset, PlotOrientation.VERTICAL, false, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        XYItemRenderer r = plot.getRenderer();
        r.setSeriesPaint(0, new Color(GRAPH_COLOR));
        r.setSeriesStroke(0, new BasicStroke(2));

        NumberAxis axis = (NumberAxis)plot.getRangeAxis();
        axis.setAutoRangeIncludesZero(false);

        saveChart(chart, "chart.png");
    }
}
