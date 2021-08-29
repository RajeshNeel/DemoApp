package com.gaurav.demoapp.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gaurav.demoapp.R;
import com.gaurav.demoapp.services.LocationUpdateService;
import com.gaurav.demoapp.utils.CommonMethod;
import com.gaurav.demoapp.utils.DemoAppConstants;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;

public class ProfileInfoFragment extends Fragment {


    @BindView(R.id.imageViewUser) ImageView imageViewUser;
    @BindView(R.id.userNameText) TextView userNameText;

    TextView userNameTexts, userEmailTexts,text_latitude,text_longitude,text_speed,text_altitude,text_accuracy;
    ImageView imageViewUsers;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    LocationManager locationManager;

    private final long MIN_TIME = 1000;
    private final long MAX_TIME = 5;
    String latitude, longitude, speed, altitude, accuracy;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    @BindView(R.id.userEmailText)
    TextView userEmailText;

  /*
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;


    ImageView imgFb,imgLinkDn,imgTw,imgInstGram;
    CardView cardWtdraw,cardTransfer,cardVoucher;*/


    String emailId;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_profile_information, container, false);
        ButterKnife.bind(root);

        userEmailTexts = root.findViewById(R.id.userEmailText);
        userNameTexts = root.findViewById(R.id.userNameText);
        imageViewUsers = root.findViewById(R.id.imageViewUser);
        text_latitude = root.findViewById(R.id.text_latitude);
        text_longitude = root.findViewById(R.id.text_longitude);
        text_speed = root.findViewById(R.id.text_speed);
        text_altitude = root.findViewById(R.id.text_altitude);
        text_accuracy = root.findViewById(R.id.text_accuracy);


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        if (getArguments() != null) {


            emailId = getArguments().getString("userEmail");

            if(emailId!=null){
                if(CommonMethod.haveNetworkConnection(getContext())){
                    CommonMethod.createProgress(getContext(),"Loading profile.");

                    updateUserProfile(emailId);
                }else{
                    Toast.makeText(getContext(), "Internet connection is missing.", Toast.LENGTH_SHORT).show();
                }

            }
        }

        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){



            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        }
        return root;
    }

    private void updateUserProfile(String emailId) {


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                CommonMethod.closeProgress();

                for(DataSnapshot snapshot1: snapshot.getChildren()){

                    if(snapshot1.child("userEmail").getValue().equals(ProfileInfoFragment.this.emailId)){

                        Log.v("Profile update :","snapshot data :"+snapshot1.child("userEmail").getValue(String.class)+" user name"+

                                snapshot1.child("fullName").getValue(String.class)+" photo uri"+snapshot1.child("photoUri").getValue(String.class));

                        userEmailTexts.setText(snapshot1.child("userEmail").getValue(String.class));
                        userNameTexts.setText(snapshot1.child("fullName").getValue(String.class));
                        Glide.with(getContext()).load(Uri.parse(snapshot1.child("photoUri").getValue(String.class))).into(imageViewUsers);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.v("ProfileFrag :","onRequest permission :"+requestCode);


        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){

            if(grantResults[0] != PackageManager.PERMISSION_DENIED){
                startLocationUpdateService();

            }else{
                Toast.makeText(getContext(),"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdateService(){
            Intent intent = new Intent(getContext(), LocationUpdateService.class);
            intent.setAction(DemoAppConstants.ACTION_START_LOCATION_SERVICE);
            getContext().startService(intent);
            Toast.makeText(getContext(),"Location service has been started.",Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdateService(){
            Intent intent = new Intent(getContext(),LocationUpdateService.class);
            intent.setAction(DemoAppConstants.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent);
            Toast.makeText(getContext(),"Location service has been stopped.",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdateService();

    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("location_updated");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v("Profile Frag :"," received data :"+intent.getData());

            LocationResult locationResult = intent.getParcelableExtra("locationUpdateResult");

            if(locationResult!=null){
                text_latitude.setText(String.format("%.2f",locationResult.getLastLocation().getLatitude()));
                text_longitude.setText(String.format("%.2f",locationResult.getLastLocation().getLongitude()));
                text_speed.setText(String.format("%.2f",locationResult.getLastLocation().getSpeed()));
                text_accuracy.setText(String.format("%.2f",locationResult.getLastLocation().getAccuracy()));
                text_altitude.setText(String.format("%.2f",locationResult.getLastLocation().getAltitude()));

            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
    }
}