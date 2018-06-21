package com.example.user.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class InputPriority extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String value = intent.getStringExtra("ghhh");
        Toast.makeText(getApplicationContext(), "YOU ARE IN NEW ACTIVITY", Toast.LENGTH_SHORT).show();
    }
}
