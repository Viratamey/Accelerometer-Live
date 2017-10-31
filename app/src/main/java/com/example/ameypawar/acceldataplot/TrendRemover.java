package com.example.ameypawar.acceldataplot;


/**
 * De-trends data by setting straight line between the first and the last point
 * and subtracting it from data. Having applied filters to data you should
 * reverse detrending by using {@link TrendRemover#retrend(double[], double[])}
 *
 * @author Marcin Rzeźnicki
 *
 */
public class TrendRemover implements Preprocessor {

    @Override
    public void apply(double[] data) {
        // de-trend data so to avoid boundary distortion
        // we will achieve this by setting straight line from end to beginning
        // and subtracting it from the trend
        int n = data.length;
        if (n <= 2)
            return;
        double y0 = data[0];
        double slope = (data[n - 1] - y0) / (n - 1);
        for (int x = 0; x < n; x++) {
            data[x] -= (slope * x + y0);
        }
    }

    /**
     * Reverses the effect of {@link #apply(double[])} by modifying {@code
     * newData}
     *
     * @param newData
     *            processed data
     * @param data
     *            original data
     */
    public void retrend(double[] newData, double[] data) {
        int n = data.length;
        double y0 = data[0];
        double slope = (data[n - 1] - y0) / (n - 1);
        for (int x = 0; x < n; x++) {
            newData[x] += slope * x + y0;
        }
    }

}