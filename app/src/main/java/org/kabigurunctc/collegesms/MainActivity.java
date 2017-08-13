package org.kabigurunctc.collegesms;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnsendSMS;
    EditText textMessage;
    final public static int ALL_PERMISSIONS = 101;
    final public static String[] permissions = {Manifest.permission.SEND_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkOrObtainPermissions();

        btnsendSMS = (Button) findViewById(R.id.button);
        textMessage = (EditText) findViewById(R.id.editText);

        btnsendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File file = getAppPublicExternalStorageDir();
                String message = textMessage.getText().toString();
                if (message.length()>0 && checkPermissions()) {
                    for(String phone: getPhoneNumbers()) {
                        sendSMS(phone, message);
                    }
                }
            }
        });


    }

    private void sendSMS(String phone, String message)
    {
//        String phoneNumber = "9046379123";
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, message, pi, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkOrObtainPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions,
                        ALL_PERMISSIONS);
            }
        }
    }

    public boolean checkPermissions(){
        if (Build.VERSION.SDK_INT >= 23) {
            for (String permission: permissions) {
                int checkSMSPermission = ContextCompat.checkSelfPermission(MainActivity.this, permission);
                if (checkSMSPermission == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "SEND_SMS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Storage Access Denied", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    getAppPublicExternalStorageDir();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public  File getAppPublicExternalStorageDir() {
        File appDir = null;

        try {
            appDir = new File(Environment.getExternalStorageDirectory(), "/CollegeSMS");
            if(!appDir.exists()) {
                appDir.mkdirs();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return appDir;
    }

    public List<String> getPhoneNumbers(){
        List<String> phoneNumbers = new ArrayList();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/CollegeSMS/data");
            if (!file.canRead()) {
                System.out.println("NOT readable");
            } else
                System.out.println("++++++++++++++ readable");
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 10)
                    phoneNumbers.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return phoneNumbers;
    }

}
