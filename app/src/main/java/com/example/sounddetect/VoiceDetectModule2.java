package com.example.sounddetect;

import android.app.Application;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sounddetect.GameController;
import com.example.sounddetect.GameEvents;

import org.jetbrains.annotations.NotNull;

public class VoiceDetectModule2 extends GameController{

    private int MIN_SCREAM_LIMIT = 27000;
    private MutableLiveData<GameEvents> liveData = new MutableLiveData<>();

    public int getMinScreamLimit() {
        return MIN_SCREAM_LIMIT;
    }

    public void setMinScreamLimit(int minScreamLimit) {
        this.MIN_SCREAM_LIMIT = minScreamLimit;
    }

    //////////////
    private MutableLiveData<String> maxVolumeLiveData = new MutableLiveData<>();
    ///////////////


    private Application context;

    public VoiceDetectModule2(Application application) {
        super(application);
        this.context = application;
    }


    @NotNull
    @Override
    public LiveData<GameEvents> subscribeUpdates() {
        Log.e("_cryy", "subscribeUpdates " + this.getClass().toString());
        return liveData;
    }

    public LiveData<String> getMaxVolumeLiveData() {
        return maxVolumeLiveData;
    }

    @Override
    public void unSubscribeUpdates() {
        stopDetection();
    }

    private MediaRecorder mRecorder = null;

    public void startDetection() {
        try {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                mRecorder.prepare();
                mRecorder.start();
            }
        } catch (Exception e) {
            stopDetection();
        }
    }

    public void stopDetection() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public int getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;

    }

}
