package com.gaurav.demoapp.ui.gallery;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gaurav.demoapp.R;
import com.gaurav.demoapp.services.LocationUpdateService;
import com.gaurav.demoapp.utils.DemoAppConstants;
import com.google.android.gms.common.SignInButton;
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


    @BindView(R.id.imageViewUser)
    ImageView imageViewUser;
    @BindView(R.id.userNameText)
    TextView userNameText;

    TextView userNameTexts, userEmailTexts;
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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Location");

        databaseReference.child("Latitude").push().setValue(latitude);
        databaseReference.child("Longitude").push().setValue(longitude);
        databaseReference.child("Speed").push().setValue(speed);
        databaseReference.child("Accuracy").push().setValue(accuracy);
        databaseReference.child("Altitude").push().setValue(altitude);

        firebaseAuth = FirebaseAuth.getInstance();

        String userId = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        if(userId!=null){

            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                  //  userNameTexts.setText(snapshot.getValue().;


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


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

        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED){



            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
        }

            startLocationUpdateService();





        return root;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){

            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                startLocationUpdateService();
            }else{
                Toast.makeText(getContext(),"Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationUpdateServiceRunning() {

        ActivityManager activityManager = (ActivityManager)getActivity(). getSystemService(Context.ACTIVITY_SERVICE);

        if(activityManager!=null){

            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {


                if (LocationServices.class.getName().equals(service.service.getClassName())) {

                    if(service.foreground){
                        Log.i("Service already","running");

                        return true;
                    }
                }
            }
            Log.i("Service not","running");

            return false;

        }

        return false;
    }


    private void startLocationUpdateService(){

   //     if(!isLocationUpdateServiceRunning()){

            Intent intent = new Intent(getContext(), LocationUpdateService.class);
            intent.setAction(DemoAppConstants.ACTION_START_LOCATION_SERVICE);
            getContext().startService(intent);

            Toast.makeText(getContext(),"Location Update service has been started.",Toast.LENGTH_SHORT).show();
     //   }
    }

    private void stopLocationUpdateService(){

   //     if(isLocationUpdateServiceRunning()){

            Intent intent = new Intent(getContext(),LocationUpdateService.class);
            intent.setAction(DemoAppConstants.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent);

            Toast.makeText(getContext(),"Location Update service has been stopped.",Toast.LENGTH_SHORT).show();

   //     }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdateService();

    }

    @Override
    public void onResume() {
        super.onResume();



     //   startLocationUpdateService();
    }
}