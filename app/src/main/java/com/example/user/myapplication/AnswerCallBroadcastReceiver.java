package com.example.user.myapplication;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import static com.example.user.myapplication.Contacts.contactData;

public class AnswerCallBroadcastReceiver extends BroadcastReceiver {

    Context context;
    public AnswerCallBroadcastReceiver(Context con){
        this.context = con;
    }

    String inComingNumber = "";
    static public int[] numberOfCalls = {0,0,0,0,0,0,0,0,0,0};
    public void setIncommingNumber(String str){
        inComingNumber = str;
    }
    public String getIncomingNumber(){
        return inComingNumber;
    }
    String TAG = "TAG";


    private void request_mute_permissions(Context cont) {
        try {
            if (Build.VERSION.SDK_INT < 23) {
                AudioManager audioManager = (AudioManager)cont.getSystemService(Context.AUDIO_SERVICE);
                assert audioManager != null;
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else {
                this.request_for_no_disturb_permission(context);
            }
        } catch ( SecurityException e ) {
            Log.e("TAG", "Error occurred");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void request_for_no_disturb_permission(Context cont) {
        AudioManager audioManager = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public void silentRingtone(Context cont){

        request_mute_permissions(cont);
        AudioManager am  = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
        MediaPlayer catSoundMediaPlayer = new MediaPlayer();
        assert am != null;
        am.setRingerMode(0x00000000);
        try {
            if (catSoundMediaPlayer.isPlaying()) {
                catSoundMediaPlayer.stop();
                catSoundMediaPlayer.release();
                catSoundMediaPlayer = MediaPlayer.create(context, R.raw.beep);
            }
            catSoundMediaPlayer.start();
        } catch(Exception e) { e.printStackTrace(); }

    }

    public void handlingIncomingCall(Context cont){
        String num = getIncomingNumber();
        for(int i=0; i<contactData.size(); i++){
          //  Log.e(TAG, contactData.get(i).number);
            if(contactData.get(i).number.equals(num)){
                numberOfCalls[i]++;
                if(numberOfCalls[i] >= 3){
                    Log.e(TAG, "Called 3 times ");
                    //  silentRingtone(cont);
                    // am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    //catSoundMediaPlayer.start();
                }
            }
        }
    }

    @Override
    public void onReceive(Context cont, Intent intent) {

        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        }
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            Log.d(TAG, "EXTRA_STATE_OFF_HOOK");
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            setIncommingNumber(number);
            Toast.makeText(cont, "CALL FROM"+number, Toast.LENGTH_LONG).show();
            Log.e(TAG, "OUTGOING NUMBER : " + number);
        }

        else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            setIncommingNumber(number);
            Toast.makeText(cont, "CALL FROM: "+number, Toast.LENGTH_LONG).show();
            setIncommingNumber(number);
            handlingIncomingCall(cont);
            Log.e(TAG, "INCOMING NUMBER : " + number);
        }
        else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            Log.d(TAG, "EXTRA_STATE_IDLE");
        }
    }
}