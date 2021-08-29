package com.gaurav.demoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gaurav.demoapp.utils.CommonMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    ImageView userImage;
    TextView userName,userEmail;
    private FirebaseAnalytics firebaseAnalytics;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = findViewById(R.id.drawer_layout);



        NavigationView navigationView = findViewById(R.id.nav_view);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(

                R.id.nav_homes, R.id.nav_profile_details)
                .setDrawerLayout(drawer)
                .build();


         navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0);

         userImage  = headerView.findViewById(R.id.imageView);
         userName = (TextView) headerView.findViewById(R.id.userName);
         userEmail = (TextView) headerView.findViewById(R.id.userEmail);









        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               //  Log.v("Constants.TAG", "Logout Called");
                 switch (item.getItemId()) {

                     case R.id.nav_login:

                         Log.v("Constants.TAG", "Login Called");
                         navController.navigate(R.id.nav_homes);

                         if (drawer.isDrawerOpen(GravityCompat.START)) {
                             drawer.closeDrawer(GravityCompat.START);
                         }
                         break;


                     case R.id.nav_logout:



                         CommonMethod.createProgress(MainActivity.this,"Signing Out.");
                         signOut();

                         if (drawer.isDrawerOpen(GravityCompat.START)) {
                             drawer.closeDrawer(GravityCompat.START);
                         }

                         Log.v("Constants.TAG", "Share  List Clicked");


                         break;

                 }
                 return false;
             }
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void signOut() {

        firebaseAuth.signOut();
        Bundle params = new Bundle();
        params.putString("user_logout", "Successful");
        firebaseAnalytics.logEvent("user_logout", params);
        navController.navigate(R.id.nav_homes);

        Toast.makeText(MainActivity.this, "User SignOut Successfully", Toast.LENGTH_SHORT).show();
        CommonMethod.closeProgress();

               /* googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...


                        Bundle params = new Bundle();
                        params.putString("user_logout", "Successful");
                        firebaseAnalytics.logEvent("user_logout", params);
                        navController.navigate(R.id.nav_homes);

                        Toast.makeText(MainActivity.this, "User SignOut Successfully", Toast.LENGTH_SHORT).show();

                    }
                });*/
    }


    @Override
    protected void onResume() {
        super.onResume();

       /* GoogleSignInAccount account = null;
        try {
            account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

            if(account!=null){

                userEmail.setText(account.getEmail());
                userName.setText(account.getDisplayName());
                Log.v("MainActivity :"," image url 2:"+account.getPhotoUrl());

                try {
                    if(account.getPhotoUrl()!=null){

                        Glide.with(MainActivity.this).load(account.getPhotoUrl()).into(userImage);

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }*/

        try {
            FirebaseUser  firebaseUser =  firebaseAuth.getCurrentUser();
            if(firebaseUser!=null){

                userEmail.setText(firebaseUser.getEmail());
                userName.setText(firebaseUser.getDisplayName());
                Log.v("MainActivity "," user image and name :"+firebaseUser.getDisplayName()+" url :"+
                        firebaseUser.getPhotoUrl()+" data :"+firebaseUser.getProviderData().get(0).getDisplayName());
          //      Glide.with(this).load(String.valueOf(firebaseUser.getPhotoUrl())).into(userImage);
            }
            else{


            }





        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        try {
            FirebaseUser  firebaseUser =  firebaseAuth.getCurrentUser();
            if(firebaseUser!=null){

                userEmail.setText(firebaseUser.getEmail());
                userName.setText(firebaseUser.getDisplayName());
             //   Glide.with(this).load(String.valueOf(firebaseUser.getPhotoUrl())).into(userImage);
            }
            else{


            }



        } catch (Exception e) {
            e.printStackTrace();
        }





       /* GoogleSignInAccount account = null;
        try {
            account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

            if(account!=null){
                userEmail.setText(account.getEmail());
                userName.setText(account.getDisplayName());
                Glide.with(this).load(String.valueOf(account.getPhotoUrl())).into(userImage);

            }

        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }*/
    }
}