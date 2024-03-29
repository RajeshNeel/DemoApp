package com.gaurav.demoapp.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.gaurav.demoapp.R;
import com.gaurav.demoapp.pojo.Location;
import com.gaurav.demoapp.utils.DemoAppConstants;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by Gaurav Sharma on 27-08-2021 on 14:50 .
 */
public class LocationUpdateService extends Service {

   private DatabaseReference databaseReference;


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {

                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                Log.e("LocationUpdateService ", "latitude :" + latitude + " :" + "longitude :" + longitude);
                try {
                    Log.v("ProfileInfo Frag", " onLocation Changed :" + locationResult.getLastLocation().describeContents());

                    updateDatabase(String.valueOf(locationResult.getLastLocation().getLatitude()), String.valueOf(locationResult.getLastLocation().getLongitude()),
                            String.valueOf(locationResult.getLastLocation().getSpeed()), String.valueOf(locationResult.getLastLocation().getAccuracy()),
                            String.valueOf(locationResult.getLastLocation().getAltitude()));

                    Intent intent = new Intent();
                    intent.putExtra("locationUpdateResult", locationResult);
                    intent.setAction("location_updated");
                    sendBroadcast(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void startLocationUpdate() {

        String CHANNEL_ID = "gaurav_location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent locationIntent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Update Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        builder.setContentText("Demo App is Running");

        builder.setContentIntent(pendingIntent);

        builder.setAutoCancel(false);

        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Location_service", NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription("This Channel is Using By Location Update Service");
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }else{
            Log.e("LocationUpdate Service ","channel is not created gaurav");
        }


        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
                 {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        startForeground(DemoAppConstants.LOCATION_SERVICE_ID,builder.build());

    }


    private void stopLocationUpdate() {

        Log.v("LocationUpdate service "," stoppedLocationUpdate called from Profile fragment ");

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();

    }


    @Override
    public void onDestroy() {
        Log.v("LocationUpdate service ","service stopped ");

        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      //  startLocationService();
        Log.v("LocationUpdateService:","starting service"+intent.getAction());

        if(intent!=null){

            String action = intent.getAction();

            if(action!=null){

                if(action.equalsIgnoreCase(DemoAppConstants.ACTION_START_LOCATION_SERVICE)){

                    startLocationUpdate();

                }
                else if (action.equalsIgnoreCase(DemoAppConstants.ACTION_STOP_LOCATION_SERVICE)){

                    stopLocationUpdate();
                }
            }
        }



        return super.onStartCommand(intent, flags, startId);
    }



    private void updateDatabase(String latitude, String longitude, String speed, String accuracy, String altitude) {

        Location location = new Location(latitude,longitude,speed,accuracy,altitude);

        String userKey = databaseReference.push().getKey();

        Log.v("LocationUpdate service ","user key "+userKey);

        databaseReference.child(userKey).setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Firebase database updated successfully",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(LocationUpdateService.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LocationUpdateService.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}
