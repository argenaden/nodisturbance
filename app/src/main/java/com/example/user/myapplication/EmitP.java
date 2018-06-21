package com.example.user.myapplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class EmitP extends AppCompatActivity {

    File sound_data;
    boolean is_recording = false;
    private MediaRecorder listen = null;
    private MediaPlayer play_back = null;
    final String audio_file = "MobileProjectAudio_DeleteMe";
    public byte records[];
    public AnalyzerRoutin.FFT analysis;
    public boolean det= false;

    private static File create_audio_file(Context context, String audioName) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);

        File aud = File.createTempFile(audioName, ".3gp", storageDir);
        return aud;
    }

    public void startRecording(View view) {
        request_audio_permissions();
    }

    private void record_audio_file() {
        if (listen == null) {
            listen = new MediaRecorder();
            listen.setAudioSource(MediaRecorder.AudioSource.MIC);
            listen.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            listen.setOutputFile(sound_data.getAbsolutePath());
            listen.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        if (!is_recording) {
            try {
                listen.prepare();
                listen.start();
                is_recording = true;

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                ParcelFileDescriptor[] descriptors = ParcelFileDescriptor.createPipe();
                ParcelFileDescriptor parcelRead = new ParcelFileDescriptor(descriptors[0]);
                ParcelFileDescriptor parcelWrite = new ParcelFileDescriptor(descriptors[1]);

                InputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelRead);
                int read;
                byte[] data = new byte[16384];

                while ((read = inputStream.read(data, 0, data.length)) != -1) {
                    byteArrayOutputStream.write(data, 0, read);
                }
                byteArrayOutputStream.flush();
                records = byteArrayOutputStream.toByteArray();

            } catch (IOException e) {

            }
        } else if (is_recording) {
            is_recording = false;
            stopRecording();
        }
    }

    public void convertion(double[] Doubles, byte[] ByteVec){
        for(int i=0, j=0; i!= Doubles.length; ++i, j+=3){
            Doubles[i] = (double) ( (ByteVec[j] & 0xFF) | ((ByteVec[j+1] & 0xFF) * 256) | (ByteVec[j+2] * 65536) );
        }
    }

    public boolean ItirateBytes(){
        boolean success = false;
        for(byte rec : records){
            Log.e("TAG", "record " + rec);
        }
        double[] data = new double[16384];
        double[] fft = new double[16384];

        convertion(data, records);
        analysis.fft(data, fft);
        return det;
    }

    public void stopRecording() {
        if (listen != null) {
            listen.stop(); listen.reset(); listen.release(); listen = null;
        }
    }

    public void stopPlaying() {
        if (play_back != null) {
            play_back.release(); play_back = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emit_p);

      //  analysis = new AnalyzerRoutin();
        try {
            sound_data = create_audio_file(this, audio_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        det = ItirateBytes();
    }

    public void startPlaying(View view) {
        play_back = new MediaPlayer();
        try {
            play_back.setDataSource(sound_data.getAbsolutePath()); play_back.prepare(); play_back.start();
        } catch (IOException e) {
            Log.e("Engine sounds", " failed to record");
        }
    }

    @Override
    public void onDestroy() {
        if (play_back != null) {
            stopPlaying();
        }
        if (listen != null) {
            stopRecording();
        }
        super.onDestroy();
    }


    private void request_audio_permissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_RECORD_AUDIO = 1;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            record_audio_file();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String percmissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            record_audio_file();
        } else {
            Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_SHORT).show();
        }
    }


}
