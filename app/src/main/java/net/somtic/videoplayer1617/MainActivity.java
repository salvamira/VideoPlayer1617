package net.somtic.videoplayer1617;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        SurfaceHolder.Callback, View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private EditText editText;
    private ImageButton bPlay, bPause, bStop, bLog;
    private TextView logTextView;
    private boolean pause, stop;
    private String path;
    private int savePos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        // obsoleto, pero neceario para versiones de Android
        // anteriores a la 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        editText = (EditText) findViewById(R.id.path);
        editText.setText("http://campus.somtic.net/android/video1617.mp4");
        logTextView = (TextView) findViewById(R.id.Log);
        bPlay = (ImageButton) findViewById(R.id.play);
        bPlay.setOnClickListener(this);
        bPause = (ImageButton) findViewById(R.id.pause);
        bPause.setOnClickListener(this);
        bStop = (ImageButton) findViewById(R.id.stop);
        bStop.setOnClickListener(this);
        bLog = (ImageButton) findViewById(R.id.logButton);
        bLog.setOnClickListener(this);
        log("");
    }

    private void log(String s) {
        logTextView.append(s + "\n");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                if (mediaPlayer != null) {
                    if (pause) {
                        mediaPlayer.start();
                    } else {
                        playVideo();
                    }
                }
                break;
            case R.id.pause:
                if (mediaPlayer != null) {
                    pause = true;
                    mediaPlayer.pause();
                }
                break;

            case R.id.stop:
                if (mediaPlayer != null) {
                    pause = false;
                    mediaPlayer.stop();
                    savePos = 0;
                    stop = true;
                }
                break;
            case R.id.logButton:
                if (logTextView.getVisibility()==TextView.VISIBLE) {
                    logTextView.setVisibility(TextView.INVISIBLE);
                } else {
                    logTextView.setVisibility(TextView.VISIBLE);
                }
                break;
        }
    }

    private void playVideo() {
        try {
            pause = false;
            path = editText.getText().toString();
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();
            if (stop)
                mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepare();
            // mMediaPlayer.prepareAsync(); Para streaming
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.seekTo(savePos);
            stop = false;
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        log("onBufferingUpdate porcentaje:" + percent);
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        log("llamada a onCompletion");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log("llamada a onPrepared");
        int mVideoWidth = mediaPlayer.getVideoWidth();
        int mVideoHeight = mediaPlayer.getVideoHeight();
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
            mediaPlayer.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log("llamada a surfaceCreated");
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        log("llamada a surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        log("llamada a surfaceDestroyed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && !pause) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaPlayer != null && !pause) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        super.onSaveInstanceState(estado);
        if (mediaPlayer != null) {
            int pos = mediaPlayer.getCurrentPosition();
            estado.putString("ruta", path);
            estado.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle estado) {
        super.onRestoreInstanceState(estado);
        if (estado != null) {
            path = estado.getString("ruta");
            savePos = estado.getInt("posicion");
        }
    }

}
