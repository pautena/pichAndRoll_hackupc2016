package hackupc.wallecarboard;

import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity implements SensorEventListener{
    private final static String TAG="VideoActivity";
    private static final String VIDEO_URL = "http://10.192.114.44:8000/video3gp.3gp";

    private VideoView videoViewLeft;
    private VideoView videoViewRight;

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

    private void configureSensors(){
        //TODO: Configure sensors
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
            videoViewLeft.requestFocus();

            videoViewLeft.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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

            });

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
                    return false;
                }

            });

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
        //TODO: Work with sensors
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO:Work with sensors
    }
}
