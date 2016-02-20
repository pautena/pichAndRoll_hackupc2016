package hackupc.wallecarboard;

import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.UnsupportedEncodingException;


public class VideoActivity extends AppCompatActivity implements SensorEventListener{
    private final static String TAG="VideoActivity";
    //private static final String VIDEO_URL = "http://10.192.114.44:8000/video3gp.3gp";
    //private static final String VIDEO_URL = "http://192.168.43.29:8000/video3gp.3gp";
    private static final String VIDEO_URL = "http://192.168.43.107:8090/";
    private VideoView videoViewLeft;
    private VideoView videoViewRight;

    private SensorManager sensorManager;
    float RotMat[]=null;
    float I[]=null;
    float sensorAcc[]=new float[3];
    float sensorMag[]=new float[3];
    float[] values = new float[3];
    float pitch;
    float roll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Log.d(TAG, "onCreate");
        hideSystemUI();
        configureSensors();
        configureVideo();

        playVideoLeft();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void configureSensors(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    private void configureVideo(){
        videoViewLeft = (VideoView) findViewById(R.id.video_view_left);
        videoViewRight = (VideoView) findViewById(R.id.video_view_right);
    }

    private void playVideoLeft(){
        Log.d(TAG, "playVideoLeft: " + videoViewLeft.getId());
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoViewLeft);

        Uri video = Uri.parse(VIDEO_URL);
        videoViewLeft.setMediaController(mediaController);
        videoViewLeft.setVideoURI(video);
        videoViewLeft.requestFocus();


        videoViewLeft.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "video left prepared");
                playVideoRight();
            }
        });

        videoViewLeft.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG,"video error: "+videoViewLeft.getId()+", what: "+what+", extra: "+extra);
                if(what==100) {
                    videoViewLeft.stopPlayback();
                    playVideoLeft();
                }
                return false;
            }
        });
    }

    private void playVideoRight(){
        Log.d(TAG, "playVideoRight: " + videoViewRight.getId());
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoViewRight);

        Uri video = Uri.parse(VIDEO_URL);
        videoViewRight.setMediaController(mediaController);
        videoViewRight.setVideoURI(video);
        videoViewRight.requestFocus();

        videoViewRight.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG,"video right prepared");
                videoViewLeft.start();
                videoViewRight.start();
            }
        });
        videoViewRight.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG,"video error: "+videoViewRight.getId()+", what: "+what+", extra: "+extra);
                if(what==100) {
                    videoViewRight.stopPlayback();
                    playVideoRight();
                }
                return false;
            }

        });
    }

    private void hideSystemUI() {
        int mUIFlag =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorMag = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorAcc = event.values.clone();
                break;
        }
        if (sensorMag != null && sensorAcc != null) {
            RotMat = new float[9];
            I = new float[9];
            SensorManager.getRotationMatrix(RotMat, I, sensorAcc, sensorMag);
            /*Correction for landscape */
            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(RotMat, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            /*  getOrientation the array values is filled with the result:
                values[0]: azimuth, rotation around the -Z axis, i.e. the opposite direction of Z axis.
                values[1]: pitch, rotation around the -X axis, i.e the opposite direction of X axis.
                values[2]: roll, rotation around the Y axis. */
            SensorManager.getOrientation(outR, values);
            /* 57,295 = 1  rad */
            pitch = values[1] * 57.2957795f;
            roll = values[2] * 57.2957795f;
            sensorMag = null; //ensure that is null to next iteration
            sensorAcc = null;
            TextView getX = (TextView)findViewById(R.id.getX);
            TextView getY = (TextView)findViewById(R.id.getY);
            getX.setText("PITCH:"+String.valueOf(pitch));
            getY.setText("ROLL:"+String.valueOf(roll));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
