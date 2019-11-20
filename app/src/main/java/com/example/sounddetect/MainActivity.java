package com.example.sounddetect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

    Button startBtn;
    Button stopBtn;
    SeekBar seekBar;
    TextView maxVolTv;
    TextView boardLevelBySeekTv;
    RadioButton audioRecButton;
    RadioButton mediaRecorderButton;
    RadioGroup radioGroup;
    final Context context = this;

    private MediaPlayer mp;
    private final int RECORD_REQUEST_CODE = 101;
    GameController gameController;
    private MutableLiveData<GameEvents> liveDataEvent;
    private MutableLiveData<String> maxVolumeLiveData;
    private Observer liveDataObserver = new Observer() {
        @Override
        public void onChanged(Object o) {
            onStopClick();
        }
    };
    private Observer maxVolumeLiveDataObserver = new Observer() {
        @Override
        public void onChanged(Object o) {
            maxVolTv.setText(o.toString());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(
                this,
                RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{(RECORD_AUDIO)},
                    RECORD_REQUEST_CODE);
        }

        startBtn = findViewById(R.id.start_btn);
        stopBtn = findViewById(R.id.stop_btn);
        seekBar = findViewById(R.id.seekBar);
        maxVolTv = findViewById(R.id.maxVol);
        boardLevelBySeekTv = findViewById(R.id.seekBarVal);
        audioRecButton = findViewById(R.id.audioRec);
        mediaRecorderButton = findViewById(R.id.mediaRecorder);
        radioGroup = findViewById(R.id.RadioGroup);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                boardLevelBySeekTv.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar.setProgress(25000);
        boardLevelBySeekTv.setText(String.valueOf(seekBar.getProgress()));

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopClick();
                stopMusic();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxVolTv.setText("max volume");
                enablingViews(false);
                if (audioRecButton.isChecked()) {
                    gameController = new VoiceDetectModule(getApplication());

                }
                if (mediaRecorderButton.isChecked()) {
                    gameController = new VoiceDetectModule2(getApplication());
                }
                startMusic();
                gameController.setMinScreamLimit(seekBar.getProgress());
                liveDataEvent = (MutableLiveData<GameEvents>) gameController.subscribeUpdates();
                maxVolumeLiveData = (MutableLiveData<String>) gameController.getMaxVolumeLiveData();
                liveDataEvent.observeForever(liveDataObserver);
                maxVolumeLiveData.observeForever(maxVolumeLiveDataObserver);
                gameController.startDetection();
            }
        });


//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });


    }


    private void stopDetection() {
        if (gameController != null) {
            gameController.stopDetection();
        }
    }

    private void onStopClick() {
        stopDetection();
        unSubsribeAll();
        enablingViews(true);
        gameController = null;
        liveDataEvent = null;
        maxVolumeLiveData = null;
    }

    private void stopMusic() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private void startMusic() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
        mp = MediaPlayer.create(context, R.raw.audio);
        mp.setLooping(true);
        mp.start();
    }

    private void unSubsribeAll() {
        if (gameController != null) gameController.unSubscribeUpdates();
        if (liveDataEvent != null && liveDataEvent.hasActiveObservers()) {
            liveDataEvent.removeObserver(liveDataObserver);
        }
        if (maxVolumeLiveData != null && maxVolumeLiveData.hasActiveObservers()) {
            maxVolumeLiveData.removeObserver(maxVolumeLiveDataObserver);
        }
    }

    private void enablingViews(boolean enable) {
        audioRecButton.setEnabled(enable);
        mediaRecorderButton.setEnabled(enable);
        seekBar.setEnabled(enable);
        startBtn.setEnabled(enable);
        radioGroup.setClickable(enable);
        seekBar.setClickable(enable);
        startBtn.setClickable(enable);
        audioRecButton.setClickable(enable);
        mediaRecorderButton.setClickable(enable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameController != null) {
            gameController.stopDetection();
        }
    }
}