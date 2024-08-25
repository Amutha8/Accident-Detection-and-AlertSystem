package com.application.accidentdetection;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity2 extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView motionStatusTextView, accelerometerValuesTextView,hello;
    private Button startButton, stopButton;

    private boolean isDetectingMotion = false,stopValue=false;
    private static final float THRESHOLD = 20.0f; // Adjusted threshold for accidental motion
    private static final int NOTIFICATION_ID = 1;
    private static final int PERMISSION_REQUEST_SEND_SMS = 2; // You can use any value you want here

    private static final String CHANNEL_ID = "MotionDetectionChannel";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Button stopAlarmButton;
     public String name,phone,address,emergencyNumber,email,value,userId;
    public Location c_location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


         name = getIntent().getStringExtra("name");
         phone = getIntent().getStringExtra("phone");
         address = getIntent().getStringExtra("address");
         emergencyNumber = getIntent().getStringExtra("emergencyNumber");
         email = getIntent().getStringExtra("email");
         value = getIntent().getStringExtra("value");
         userId = getIntent().getStringExtra("userId");
        // Initialize UI elements
        motionStatusTextView = findViewById(R.id.motionStatusTextView);
        accelerometerValuesTextView = findViewById(R.id.accelerometerValuesTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        stopAlarmButton = findViewById(R.id.stopAlarmButton);
        hello=findViewById(R.id.hello);
        hello.setText("Hello "+name);
        stopAlarmButton.setVisibility(View.GONE);
        // Initialize SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer == null) {
                // Handle the case where accelerometer is not available on the device
            }
        } else {
            // Handle the case where the sensor manager cannot be retrieved
        }

        // Setup button click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMotionDetection();

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMotionDetection();
            }
        });
        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
        // Create notification channel
        createNotificationChannel();
    }

    // Method to start motion detection
    private void startMotionDetection() {
        if (!isDetectingMotion) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            startButton.setVisibility(View.GONE);

            stopButton.setVisibility(View.VISIBLE);
            isDetectingMotion = true;
        }
    }

    // Method to stop motion detection
    private void stopMotionDetection() {
        if (isDetectingMotion) {
            sensorManager.unregisterListener(this);
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
            isDetectingMotion = false;
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            userRef.child("value").setValue("false");

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);

            // Update motion status
            if(!stopValue) {
                if (magnitude > THRESHOLD) {
                    stopValue = true;
                    motionStatusTextView.setText("Accidental motion detected! ");
                    stopAlarmButton.setVisibility(View.VISIBLE);
                    sendNotification("Accidental Motion Detected", "");
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                    userRef.child("value").setValue("true");

                } else {
                    motionStatusTextView.setText("You Are Safe to go Happy Journey !");
                }
            }

            // Format accelerometer values to one decimal place
            String accelerometerValues = String.format("%.1f", magnitude) ;
            // Update accelerometer values TextView
            accelerometerValuesTextView.setText(accelerometerValues);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // Method to create notification channel (required for Android Oreo and above)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Method to send notification
    private void sendNotification(String title, String message) {
        Log.i("NOTIFICATION", ":INVOKED");

        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setSound(Uri.EMPTY);
        // Start countdown
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(stopValue) {
                    int secondsLeft = (int) (millisUntilFinished / 1000);
                    builder.setContentText(message + " Deactivate within " + secondsLeft + " Seconds !");
                    NotificationManagerCompat.from(MainActivity2.this).notify(NOTIFICATION_ID, builder.build());
                }
            }

            @Override
            public void onFinish() {
                if(stopValue) {

                    builder.setContentText(message+"Calling For Emergency");
                    stopAlarmButton.setVisibility(View.GONE);
                    NotificationManagerCompat.from(MainActivity2.this).notify(NOTIFICATION_ID, builder.build());
                    if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity2.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    } else {
                        getLastLocation();
                        stopValue = false;
                    }
                }
            }
        }.start();
    }


    private void stopAlarm() {
        stopValue = false;
        motionStatusTextView.setText("Alarm deactivated.");
        stopAlarmButton.setVisibility(View.GONE);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.child("value").setValue("false");

        // Cancel the notification
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);


    }
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            sendLocationSMS(location);
                        } else {
                            Toast.makeText(MainActivity2.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void sendLocationSMS(Location location) {

        String[] emergencyNumbers = { emergencyNumber};

        String message = name+" had an accident . Click the link to view the location: http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude()+". Address : "+address;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String number : emergencyNumbers) {
                smsManager.sendTextMessage(number, null, message, null, null);
            }
            Toast.makeText(this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send emergency message", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }









}
