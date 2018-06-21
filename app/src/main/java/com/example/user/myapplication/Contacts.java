package com.example.user.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Contacts extends MainActivity {

    private static final String TAG = "TAG";
    static public ArrayList<ContactData> contactData;
    public ArrayList<Button> arrayButton;

    InputPriority inputPriority;
    File file;
    public Contacts(){}

    public class ContactData{
        public String name;
        public String number;
        ContactData(String n, String num){
            if(n != null && num != null){
                name = n;
                number = num;
            } else {
                name = "...empty...";
                number = "...empty...";
            }
        }
    }

    Contacts(Context context){
        contactData = new ArrayList<>();
        arrayButton = new ArrayList<>();
        inputPriority = new InputPriority();

    }

    public void getDetailOfCalls(LinearLayout linearLayout, final Context context){
        StringBuilder sb = new StringBuilder();
        TextView tv = new TextView(context);
        sb.append("\nCall Details: \n");
        sb.append( "\nPhone Number:--- "+" 01029801701 " +" \nCall Type:--- "+"INCOMING"+" \nCall Date:--- "+"Thu May 29"+" \nCall duration in sec :--- "+" 34" );
        sb.append( "\nPhone Number:--- "+" 01030158645 " +" \nCall Type:--- "+"OUTGOING"+" \nCall Date:--- "+"Thu May 29"+" \nCall duration in sec :--- "+" 26" );
        sb.append( "\nPhone Number:--- "+" 01013156635 " +" \nCall Type:--- "+"INCOMING"+" \nCall Date:--- "+"Thu May 29"+" \nCall duration in sec :--- "+" 0" );
        sb.append( "\nPhone Number:--- "+" 01011235648 " +" \nCall Type:--- "+"INCOMING"+" \nCall Date:--- "+"Thu May 29"+" \nCall duration in sec :--- "+" 56" );
        tv.setText(sb);
        linearLayout.addView(tv);
    }

    public void readFromFile(LinearLayout linearLayout, String filename, final Context context){

        if(isExternalStorageReadable()){
            StringBuilder sb = new StringBuilder();
            try{
                File textFile = new File(Environment.getExternalStorageDirectory(), filename);
                FileInputStream fis = new FileInputStream(textFile);

                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader buff = new BufferedReader(isr);

                String line = null;

                while((line = buff.readLine()) != null){
                   // sb.append(line).append("\n");

                    Button tv1 = new Button(context);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
                    gd.setCornerRadius(5);
                    gd.setStroke(3, 0xFF000000);
                    tv1.setBackground(gd);

                    arrayButton.add(tv1);
                    tv1.setText(line);

                    linearLayout.addView(tv1);

                }
                // Debug purpose
                String[] contacts = {"Emanuel Safron", "01055641548", "Volya Stasov", "01011542598", "Stas Voronej", "01065481549", "Uncle Joe", "01065481126",
                        "grandma", "01066541515",   "grandpa", "01016524518",  "Sonya", "01056112245", "Bill  Gates", "01065569848", "Napoleon Hill", "01056684515", "Borodach", "01026554515"};
                for(int i = 0; i< 10; i+=2){
                    Button tv1 = new Button(context);
                    GradientDrawable gd = new GradientDrawable();
                    gd.setColor(0xFF00FF00);
                    gd.setCornerRadius(5);
                    gd.setStroke(3, 0xFF000000);
                    tv1.setBackground(gd);

                    arrayButton.add(tv1);
                    int k = i;
                    k++;
                    String s = contacts[i] + " ^ " + contacts[k];
                    tv1.setText(s);
                    linearLayout.addView(tv1);
                }
                Toast.makeText(context, String.valueOf(arrayButton.size()), Toast.LENGTH_LONG).show();
                fis.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Cannot Read from External Storage.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isExternalStorageReadable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            Log.i("State", "Yes,  we can read data!");
            return true;
        } else {
            return false;
        }
    }

    public void writeToFile(File directory, String filename, String data ) throws IOException {
        File out;
        OutputStreamWriter outStreamWriter = null;
        FileOutputStream outStream = null;

        out = new File(new File(String.valueOf(directory)), filename);

        Log.e(TAG, "SAVED INTO :  " + out);

        if (!out.exists()){
            out.createNewFile();
        }

        outStream = new FileOutputStream(out, true) ;
        outStreamWriter = new OutputStreamWriter(outStream);

        outStreamWriter.append(data);
        outStreamWriter.flush();
    }

    public void saveInFile(Context context, String filename){
        try {
            for (int i = 0; i < contactData.size(); i++) {
                if(contactData.get(i) != null){
                    String entry = contactData.get(i).name + " ^ " +  contactData.get(i).number + '\n';
                    writeToFile(Environment.getExternalStorageDirectory(), filename, entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContactList(Context con) {
        ContentResolver cr = con.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    StringBuilder stringBuilder = new StringBuilder();
                    while (pCur != null && pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactData.add(new ContactData(name, phoneNo));
                        Log.e(TAG, "Name: " + name);
                        Log.e(TAG, "Phone Number: " + phoneNo);

                    }
                    // DEBUG START

                    // DEBUG END

                 //   TextView tv = findViewById(R.id.textView);
                  //  tv.setText(stringBuilder.toString());
                    //Toast.makeText(con.getApplicationContext(), stringBuilder,Toast.LENGTH_LONG).show();
                    assert pCur != null;
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }
}
