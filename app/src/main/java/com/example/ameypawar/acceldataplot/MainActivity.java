package com.example.ameypawar.acceldataplot;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText;
    private int i = 0;
    private Button btnStart, btnStop, btnUpload;
    private LinearLayout layout;
    private SensorManager sensorManager;
    private final String TAG = "GraphSensors";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private GraphView mGraphAccel;
    private GraphView line_graph;
    private LineGraphSeries<DataPoint>  mSeriesAccelX, mSeriesAccelY, mSeriesAccelZ, mSeriesAccelT, mSeriesAccelF;
    private double graphLastAccelXValue = 10d;
    private Double totalaccel;
    private ArrayList<Float> datar = new ArrayList<Float>();
    private float[] data;
    private int nl = 20;
    private int nr = 20;
    private double[] coeffs ;
    private  SGFilter sgFilter = new SGFilter(nl, nr);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);


        mSeriesAccelX = initSeries(Color.BLUE, "X");
        mSeriesAccelY = initSeries(Color.RED, "Y");
        mSeriesAccelZ = initSeries(Color.GREEN, "Z");

        mGraphAccel = initGraph(R.id.xyzgraph, "X, Y, Z direction Acceleration");

        mGraphAccel.addSeries(mSeriesAccelX);
        mGraphAccel.addSeries(mSeriesAccelY);
        mGraphAccel.addSeries(mSeriesAccelZ);

        mSeriesAccelT = initSeries(Color.BLUE, "Taccel");
        mSeriesAccelF = initSeries(Color.BLACK, "Faccel");

        mGraphAccel = initGraph(R.id.accelgraph, "Total and Filtered Acceleration");
        mGraphAccel.addSeries(mSeriesAccelT);
        mGraphAccel.addSeries(mSeriesAccelF);

        coeffs = SGFilter.computeSGCoefficients(nl, nr, 6);
        startAccel();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x,y,z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        xText.setText("X: " + x);
        yText.setText("Y: " + y);
        zText.setText("Z: " + z);

        graphLastAccelXValue += 0.05d;

        mSeriesAccelX.appendData(new DataPoint(graphLastAccelXValue, x), true, 100);
        mSeriesAccelY.appendData(new DataPoint(graphLastAccelXValue, y), true, 100);
        mSeriesAccelZ.appendData(new DataPoint(graphLastAccelXValue, z), true, 100);

        totalaccel = Math.sqrt(x * x + y * y + z * z);

        float[] smooth;
        Float obj = new Float(totalaccel);
        datar.add(obj);
        data=new float[datar.size()];
        for(int j=0;j<datar.size();j++){
            data[j]=(float)datar.get(j);}



        if(datar.size()>=(nl + nr + 1)) {
            smooth = sgFilter.smooth(data, coeffs);
            datar.remove(0);

            mSeriesAccelT.appendData(new DataPoint(graphLastAccelXValue, data[(int)(nl+nr)/2]), true, 100);
            mSeriesAccelF.appendData(new DataPoint(graphLastAccelXValue, smooth[(int)(nl+nr)/2]), true,100);
            String dataString = String.valueOf(event.accuracy) + "," + String.valueOf(event.timestamp) + "," + String.valueOf(event.sensor.getType()) + "\n";
            Log.d(TAG, "Data received: " + dataString);
        }
    }
    public GraphView initGraph(int id, String title) {
        GraphView graph = (GraphView) findViewById(id);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getGridLabelRenderer().setLabelVerticalWidth(100);
        graph.setTitle(title);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        return graph;
    }

    public void startAccel(){
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use
    }

    public LineGraphSeries<DataPoint> initSeries(int color, String title) {
        LineGraphSeries<DataPoint> series;
        series = new LineGraphSeries<>();
        series.setDrawDataPoints(true);
        series.setDrawBackground(false);
        series.setColor(color);
        series.setTitle(title);
        return series;
    }


}