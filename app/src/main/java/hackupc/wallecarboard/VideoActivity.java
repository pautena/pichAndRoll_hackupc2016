package hackupc.wallecarboard;

import android.content.Intent;
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
import android.widget.TextView;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoActivity extends AppCompatActivity implements SensorEventListener{
    private final static String TAG="VideoActivity";
    private static final String VIDEO_URL = "rtsp://10.4.180.187:8554/";

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
        try{
            Log.d(TAG, "playVideoLeft: " + videoViewLeft.getId());
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoViewLeft);

            Uri video = Uri.parse(VIDEO_URL);
            videoViewLeft.setMediaController(mediaController);
            videoViewLeft.setVideoURI(video);
            videoViewLeft.setBufferSize(2048);
            videoViewLeft.setVideoQuality(16);
            videoViewLeft.requestFocus();

            videoViewLeft.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(io.vov.vitamio.MediaPlayer mp) {
                    Log.d(TAG, "video left prepared");
                    playVideoRight();
                }
            });

            videoViewLeft.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                    Log.d(TAG, "video error: " + videoViewLeft.getId() + ", what: " + what + ", extra: " + extra);
                    return false;
                }
            });

            /*videoViewLeft.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG,"video left prepared");
                    playVideoRight();
                }
            });
            videoViewLeft.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d(TAG,"video error: "+videoViewLeft.getId()+", what: "+what+", extra: "+extra);
                    return false;
                }

            });*/



        }catch(Exception e){
            Log.d(TAG,"Error play video. message: "+e.getMessage());
            finish();
        }
    }

    private void playVideoRight(){
        try{
            Log.d(TAG, "playVideoRight: " + videoViewRight.getId());
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoViewRight);

            Uri video = Uri.parse(VIDEO_URL);
            videoViewRight.setMediaController(mediaController);
            videoViewRight.setVideoURI(video);
            videoViewRight.setBufferSize(2048);
            videoViewRight.setVideoQuality(16);
            videoViewRight.requestFocus();

            videoViewRight.setOnPreparedListener(new io.vov.vitamio.MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(io.vov.vitamio.MediaPlayer mp) {
                    Log.d(TAG, "video right prepared");
                    videoViewLeft.start();
                    videoViewRight.start();
                }
            });

            videoViewRight.setOnErrorListener(new io.vov.vitamio.MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(io.vov.vitamio.MediaPlayer mp, int what, int extra) {
                    Log.d(TAG,"video error: "+videoViewRight.getId()+", what: "+what+", extra: "+extra);
                    return false;
                }
            });

            /*videoViewRight.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
                    return false;
                }

            });*/

        }catch(Exception e){
            Log.d(TAG,"Error play video. message: "+e.getMessage());
            finish();
        }
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
