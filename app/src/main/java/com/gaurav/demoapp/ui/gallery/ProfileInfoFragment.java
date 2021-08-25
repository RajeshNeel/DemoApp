package com.gaurav.demoapp.ui.gallery;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gaurav.demoapp.R;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;

public class ProfileInfoFragment extends Fragment implements LocationListener {


    @BindView(R.id.imageViewUser)
    ImageView imageViewUser;
    @BindView(R.id.userNameText)
    TextView userNameText;

    TextView userNameTexts, userEmailTexts;
    ImageView imageViewUsers;
    DatabaseReference databaseReference;
    LocationManager locationManager;

    private final long MIN_TIME = 1000;
    private final long MAX_TIME = 5;
    String latitude, longitude, speed, altitude, accuracy;


    @BindView(R.id.userEmailText)
    TextView userEmailText;

  /*
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;


    ImageView imgFb,imgLinkDn,imgTw,imgInstGram;
    CardView cardWtdraw,cardTransfer,cardVoucher;*/


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_profile_information, container, false);
        ButterKnife.bind(root);

        userEmailTexts = root.findViewById(R.id.userEmailText);
        userNameTexts = root.findViewById(R.id.userNameText);
        imageViewUsers = root.findViewById(R.id.imageViewUser);


        if (getArguments() != null) {

            userNameTexts.setText(getArguments().getString("UserName"));
            userEmailTexts.setText(getArguments().getString("userEmail"));
            Glide.with(getContext()).load(getArguments().getString("userPhoto")).into(imageViewUsers);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        databaseReference.child("Latitude").push().setValue(latitude);
        databaseReference.child("Longitude").push().setValue(longitude);
        databaseReference.child("Speed").push().setValue(speed);
        databaseReference.child("Accuracy").push().setValue(accuracy);
        databaseReference.child("Altitude").push().setValue(altitude);

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    Log.v("ProfileInfo","onData Change :"+snapshot.child("Latitude").getValue().toString()+" longitude :"+snapshot.
                            child("Longitude").getValue().toString()+" Accuracy :"+snapshot.child("Accuracy").getValue().toString()+"" +
                            "Altitude :"+snapshot.child("Altitude").getValue()+" speed "+snapshot.child("Speed").getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        updateDatabase(latitude,longitude,speed,accuracy,altitude);


        return root;
    }

    private void updateDatabase(String latitude, String longitude, String speed, String accuracy, String altitude) {



        databaseReference.child("Latitude").push().setValue("20");
        databaseReference.child("Longitude").push().setValue("30");
        databaseReference.child("Speed").push().setValue("2.9");
        databaseReference.child("Accuracy").push().setValue("30%");
        databaseReference.child("Altitude").push().setValue("2");



    }

    @Override
    public void onLocationChanged(@NonNull Location location) {


        try {
            Log.v("ProfileInfoFrag"," onLocation Changed :"+location.describeContents());
            updateDatabase(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),
                    String.valueOf(location.getSpeed()),String.valueOf(location.getAccuracy()),String.valueOf(location.getAltitude()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

      /*  if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }*/

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}