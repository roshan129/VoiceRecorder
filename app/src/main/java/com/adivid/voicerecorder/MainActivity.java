package com.adivid.voicerecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.adivid.voicerecorder.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private boolean isRecording = false;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSIOn_CODE = 21;

    private MediaRecorder mediaRecorder;
    private String recordFile, savedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
                binding.textView.setText("Recording Stopped");
                isRecording = false;

            } else {
                if (checkPermissions()) {
                    startRecording();
                    binding.textView.setText("Recording Started");
                    isRecording = true;
                }
            }
        });

    }

    private void startRecording() {
        binding.chronometer.setBase(SystemClock.elapsedRealtime());
        binding.chronometer.start();

        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss",
                Locale.CANADA);
        Date now = new Date();
        //recordFile = System.currentTimeMillis() + ".mp3";
        recordFile = "Recording_" + formatter.format(now) + ".mp3";

        savedPath = recordPath + recordFile;

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    private void stopRecording() {

        binding.chronometer.stop();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        Uri uri = Uri.fromFile(new File(savedPath));
        File file = new File(savedPath);
        Log.d("TAG", "stopRecording: Uri : "+ uri);
        Log.d("TAG", "stopRecording: File : "+ file);

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, recordPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission},
                    PERMISSIOn_CODE);
            return false;
        }

    }
}