package com.jingtao.app.views;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import android.os.Handler;

import com.jingtao.app.data.SoundClip;

import java.io.File;
import java.io.IOException;

public class SoundView extends LinearLayout {
    private boolean isRecording = false;
    private boolean isDoneRecording = false;
    private boolean isPlaying = false;
    private static final String FOLDER_NAME = "QA/SOUND";
    private static final String ERROR_TAG = "SOUND RECORDING ERROR";

    private MediaRecorder recorder;
    private MediaPlayer player;
    private SoundClip currentSoundClip;


    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime;
    private static final int MAX_DURATION = 60; //in seconds
    private static final long TIMER_DELAY = 500; //in milliseconds


    private Button buttonVoice;
    private Button buttonDelete;

    public String getSoundClipPath() {
        if (currentSoundClip == null) {
            return null;
        } else {
            return currentSoundClip.getPath();
        }
    }

    private String getPath(String name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path + "/" + FOLDER_NAME + "/" + name + ".3gp";
    }

    public SoundView(Context context) {
        super(context);
        init(context);
    }
    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sound_view, this);

        //get the button
        buttonVoice = (Button)findViewById(R.id.btn_voice);
        buttonDelete = (Button)findViewById(R.id.btn_delete);

        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRecording) {
                    timerHandler.removeCallbacks(this);
                    return;
                }
                long currentTime = System.currentTimeMillis();
                int lapsed = (int) ((currentTime - startTime) / 1000);
                if (lapsed > MAX_DURATION) {
                    stopRecording();
                    timerHandler.removeCallbacks(this);
                } else {
                    String display = String.valueOf(MAX_DURATION - lapsed);
                    display += " seconds remaining, click to stop";
                    buttonVoice.setText(display);
                    timerHandler.postDelayed(this, TIMER_DELAY);
                }
            }
        };
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //try to delete sound clip
                if (currentSoundClip != null) {
                    File source = new File(currentSoundClip.getPath());
                    if (source.exists()) {
                        source.delete();
                    }
                    //now reset everything
                    isPlaying = false;
                    isRecording = false;
                    isDoneRecording = false;
                    buttonVoice.setText("RECORD");
                    buttonDelete.setVisibility(GONE);
                }
            }
        });
        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording && !isDoneRecording) {
                    //can record
                    boolean result = startRecording();
                    if (result) {
                        isRecording = true;
                    }
                } else if (isRecording) {
                    //stop now
                    boolean result = stopRecording();
                    if (result) {
                        isRecording = false;
                        isDoneRecording = true;
                    }
                } else if (!isPlaying) {
                    //play
                    boolean result = playRecording();
                    if (result) {
                        isPlaying = true;
                    }
                } else if (isPlaying) {
                    //stop playing
                    boolean result = stopPlaying();
                    if (result) {
                        isPlaying = false;
                    }
                }
            }
        });
    }

    private boolean startRecording() {
        //create folder if it doesn't exist
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), FOLDER_NAME);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //try to record
        if (recorder == null) {
            recorder = new MediaRecorder();
        }
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        String fileName = String.valueOf(System.currentTimeMillis());
        String filePath = getPath(fileName);
        currentSoundClip = new SoundClip(fileName, filePath, 0);
        recorder.setOutputFile(filePath);



        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(ERROR_TAG, e.getMessage(), e);
            return false;
        }

        buttonVoice.setText("STOP");
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        return true;
    }

    private boolean stopRecording() {
        try {
            recorder.stop();
        } catch (Exception e) {
            Log.e(ERROR_TAG, e.getMessage(), e);
            return false;
        }

        if (currentSoundClip == null) {
            return false;
        }
        MediaPlayer tempPlayer = MediaPlayer.create(this.getContext(), Uri.parse(currentSoundClip.getPath()));
        currentSoundClip.setLength(tempPlayer.getDuration() / 1000);
        buttonVoice.setText("PLAY");
        buttonDelete.setVisibility(VISIBLE);
        return true;
    }

    private boolean playRecording() {
        if (player == null) {
            player = new MediaPlayer();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    if (isDoneRecording) {
                        buttonVoice.setText("PLAY");
                        buttonDelete.setVisibility(VISIBLE);
                    } else {
                        buttonVoice.setText("RECORD");
                    }
                    isPlaying = false;
                }
            });
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    return true;
                }
            });
        }
        try {
            player.reset();
            player.setDataSource(currentSoundClip.getPath());
            player.prepare();
        } catch (Exception e) {
            Log.e(ERROR_TAG, e.getMessage(), e);
            return false;
        }

        player.seekTo(0);
        player.start();
        buttonVoice.setText("STOP");
        buttonDelete.setVisibility(GONE);
        return true;
    }

    private boolean stopPlaying() {
        if (player == null) {
            return false;
        } else {
            player.stop();
        }

        buttonVoice.setText("PLAY");
        buttonDelete.setVisibility(VISIBLE);
        return true;
    }
}