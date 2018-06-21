package com.example.user.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    public static final int PERMISSION_REQUEST_CODE = 1;
    boolean permission_to_write = isStoragePermissionGranted();
    boolean permission_to_read = checkPermissionForReadExtertalStorage();
    AnswerCallBroadcastReceiver call;
    EmitP emit;
    byte[] record;
    double ball = 0, thre = 0;
    int[] threshold = {30,30,80,50,70};
    int[] durationOfCallMinWeek = {20, 5, 120, 20, 17};
    int[] frequenceOfCallMinWeek = {3, 6, 10, 2, 4};
    static String[] numbers = new String[5];
    public boolean detected = false;
    Boolean isClickedOnSave = false;
    final String nameOfFile = "mobile_final4.txt";
    final String setPassengerSuccess = "PASSENGER is DETECTED ...";
    final String driverDetected = "DRIVER is DETECTED";

    Button getContactList;
    Button settings;
    TextView text;
    ScrollView scrollView;
    ScrollView contactView;
    LinearLayout linLay;
    final String TAG = "TAG";
    Contacts contacts;

    private void getCallDetails() {

        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery( CallLog.Calls.CONTENT_URI,null, null,null, null);
        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        sb.append( "Details of call :");
        while ( managedCursor.moveToNext() ) {
            String phNumber = managedCursor.getString( number );
            String callType = managedCursor.getString( type );
            String callDate = managedCursor.getString( date );
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString( duration );
            String dir = null;
            int dircode = Integer.parseInt( callType );
            switch( dircode ) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
            }
            sb.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(callDayTime).append(" \nCall duration in sec :--- ").append(callDuration);
        }
        managedCursor.close();

        TextView tv1 = new TextView(getApplicationContext());
        tv1.setText(sb);
        linLay.addView(tv1);
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void request_permission_to_read_external_storage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = new Contacts(getApplicationContext());
        text = findViewById(R.id.textView);
        scrollView = new ScrollView(this);
        getContactList = findViewById(R.id.getContacts);
        settings = findViewById(R.id.settings);

        call = new AnswerCallBroadcastReceiver(MainActivity.this);
        emit = new EmitP();
        record = new byte[16256];

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED) {

                Log.d("TAG", "Requesting permission");
                String[] permissions = {Manifest.permission.WRITE_CALL_LOG};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
                Log.v(TAG,"No grant for permission");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {


            if(!permission_to_read){
                try {
                    request_permission_to_read_external_storage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(permission_to_write){

                settings.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        startActivity(new Intent(getApplicationContext(), EmitP.class));

                        linLay = new LinearLayout(getApplicationContext());
                        linLay.setOrientation(LinearLayout.VERTICAL);
                        contactView.addView(linLay);
                        contacts.getDetailOfCalls(linLay, getApplicationContext());
                        getCallDetails();
                        setContentView(contactView);

                        if(detected){
                            SpannableString spannableString = new SpannableString(setPassengerSuccess);
                            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),0, spannableString.length(),0);
                            Toast.makeText(getApplicationContext(), spannableString, Toast.LENGTH_LONG).show();
                        } else if(!detected && ball > thre){
                            call.silentRingtone(getApplicationContext());
                            SpannableString spannableString = new SpannableString(driverDetected);
                            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),0, spannableString.length(),0);
                            Toast.makeText(getApplicationContext(), spannableString, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                getContactList.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        startActivity(new Intent(getApplicationContext(), SetContactPriority.class));
                    }
                });

            }

        }

    }
}
