package com.example.laboratornr1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private WifiManager wifiManager;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final int NOTIFY_ID = 1;
    private Button bPushNotification;
    private Button bSearch;
    private EditText editText;
    private Button bCamera;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int cameraId;
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private Handler mHandler = new Handler();
    private Button bWifiOff;
    private ToggleButton toggleButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bPushNotification = findViewById(R.id.bPushNotification);
        bSearch = findViewById(R.id.bSearch);
        editText = findViewById(R.id.et_search);
        radioGroup = findViewById(R.id.groupId);
        bCamera = findViewById(R.id.b_camera);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        textView = (TextView) findViewById(R.id.textView);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ||
                (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        )
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + editText.getText())));
            }
        };
        bSearch.setOnClickListener(onClickListener);

        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioId);
                Log.d("asdas",radioId.toString());
                if (radioId == R.id.r_front){
                    openActivityFront();
                }
                else if(radioId == R.id.r_back) {
                    openActivityBack();
                }
            }

        });

        toggleButton = findViewById(R.id.toggleButton);
        textView = findViewById(R.id.textView);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    textView.setText("Bluetooth is ON");
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    adapter.enable();
                } else {
                    textView.setText("Bluetooth is OFF");
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    adapter.disable();
                }
            }
        });
        // For initial setting
        if (toggleButton.isChecked()) {
            textView.setText("Bluetooth is ON");
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.enable();
        } else {
            textView.setText("Bluetooth is OFF");
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.disable();
        }


    }
    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    public void displayNotification(View view) {
        mHandler.postDelayed(mNoteRunnable, 10000);
    }

    private Runnable mNoteRunnable = new Runnable() {
        @Override
        public void run() {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setAutoCancel(false)
                            .setSmallIcon(R.drawable.ic_notifications)
                            .setWhen(System.currentTimeMillis())
                            .setContentIntent(pendingIntent)
                            .setContentTitle("Notification Alert!")
                            .setContentText("O notificare de la Theo")
                            .setPriority(PRIORITY_HIGH);
            createChannelIfNeeded(notificationManager);
            notificationManager.notify(NOTIFY_ID, notificationBuilder.build());

        }
    };

    public void openActivityFront() {
        cameraId = 1;
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        intent.putExtra(EXTRA_TEXT,cameraId);
        startActivity(intent);
    }
    public void openActivityBack() {
        cameraId = 0;
        Intent intent = new Intent(MainActivity.this, Activity2.class);
        intent.putExtra(EXTRA_TEXT,cameraId);
        startActivity(intent);
    }


}