package com.example.user.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class SetContactPriority extends MainActivity {

    Contacts contacts;
    LinearLayout ll;
    ScrollView scrollView;
    final String nameOfFile = "mobile_final4.txt";

    private void prioritize(final Context context){
        contacts.getContactList(context);
       ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(ll);
        contacts.readFromFile(ll, nameOfFile, context);
        setContentView(scrollView);


        for(int i=0; i<contacts.arrayButton.size(); i++){
            contacts.arrayButton.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    final AlertDialog mBuilder = new AlertDialog.Builder(context).create();
                    View mView = getLayoutInflater().inflate(R.layout.dummy_input, null);
                    final EditText mEmail = mView.findViewById(R.id.etEmail);
                    Button mLogin = mView.findViewById(R.id.btnLogin);
                    mBuilder.setView(mView);
                    mBuilder.show();
                    mLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!mEmail.getText().toString().isEmpty() && Integer.valueOf(mEmail.getText().toString()) > -1 && Integer.valueOf(mEmail.getText().toString()) < 101){
                                Toast.makeText(context, "Threshold have been set",
                                        Toast.LENGTH_SHORT).show();
                                mBuilder.dismiss();
                            }else{
                                Toast.makeText(context,"Enter threshold again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }

    }

   // @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_set_contact_priority);

        contacts = new Contacts(SetContactPriority.this);
        scrollView = new ScrollView(SetContactPriority.this);
        final Context context = SetContactPriority.this;
        prioritize(context);
    }
}
