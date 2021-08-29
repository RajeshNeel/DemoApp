package com.gaurav.demoapp.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.gaurav.demoapp.MainActivity;
import com.gaurav.demoapp.R;
import com.gaurav.demoapp.utils.CommonMethod;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private GoogleSignInClient googleSignInClient;
    private int  RC_SIGN_IN = 100;
    @BindView(R.id.sign_in_button) SignInButton googleSignInBtn;
    @BindView(R.id.firebase_sign_in_btn) Button firebase_sign_in_btn;
    @BindView(R.id.firebase_sign_up_btn) Button firebase_sign_up_btn;

   /* @BindView(R.id.edit_text_user_email) EditText edit_text_user_email;
    @BindView(R.id.password) EditText edit_text_password;*/




    Button firebaseLoginBtn,firebaseSignUpBtn;
    EditText edit_text_user_email,edit_text_password;
    TextView textForgotPassword;

    private NavController navController;
    private FirebaseAnalytics firebaseAnalytics;
    View root;
    ImageView userImage;
    TextView userName,userEmail;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

         root = inflater.inflate(R.layout.fragment_home, container, false);
         ButterKnife.bind(getActivity(),root);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();



        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        auth = FirebaseAuth.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        SignInButton signInButton = root.findViewById(R.id.sign_in_button);

        firebaseLoginBtn = root.findViewById(R.id.firebase_sign_in_btn);
        firebaseSignUpBtn = root.findViewById(R.id.firebase_sign_up_btn);
        textForgotPassword = root.findViewById(R.id.text_forgot_password);

        edit_text_user_email = root.findViewById(R.id.edit_text_user_email);
        edit_text_password = root.findViewById(R.id.password);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());


        signInButton.setSize(SignInButton.SIZE_WIDE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });


        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailId = edit_text_user_email.getText().toString();

                if(TextUtils.isEmpty(emailId)){
                    Toast.makeText(getContext(),"Please enter email id",Toast.LENGTH_SHORT).show();
                    return;
                }

                CommonMethod.createProgress(getContext(),"Sending forgot password reset link");

                auth.sendPasswordResetEmail(emailId).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        CommonMethod.closeProgress();

                        if(task.isSuccessful()){

                            Toast.makeText(getContext()," password reset link has been sent to your email.",Toast.LENGTH_SHORT).show();

                        }else{

                            Toast.makeText(getContext()," failed to sent reset email.",Toast.LENGTH_SHORT).show();

                        }

                    }
                });

            }
        });

        firebaseSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(root).navigate(R.id.action_nav_homes_to_nav_register);

           //     navController.navigate(R.id.action_nav_homes_to_nav_register);
            }
        });

        firebaseLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edit_text_user_email.getText().toString().trim();

                String password = edit_text_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                CommonMethod.createProgress(getContext(),"Signing..");


                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){


                            FirebaseUser firebaseUser = auth.getCurrentUser();


                            Bundle bundle = new Bundle();
                            bundle.putString("users_login", "successful");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                            Toast.makeText(getContext(),"successfully login",Toast.LENGTH_SHORT).show();

                            CommonMethod.closeProgress();
                            bundle.putString("userEmail",firebaseUser.getEmail());
                            bundle.putString("UserName",firebaseUser.getDisplayName());
                            bundle.putString("userPhoto",String.valueOf(firebaseUser.getPhotoUrl()));

                            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_gallery,bundle);
                        }

                    }
                });

            }
        });




        return root;
    }



    private void signIn() {

        CommonMethod.createProgress(getContext(),"Signing...");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            CommonMethod.closeProgress();

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            if (acct != null) {

                String personName = acct.getDisplayName();
                String userGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
            }

            Toast.makeText(getContext(),"SignedIn Successfully."+acct.getDisplayName()+" person email :"+acct.getEmail(),Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
            Bundle profileInfo = new Bundle();
            profileInfo.putString("UserName", account.getDisplayName());
            profileInfo.putString("userGivenName", account.getGivenName());
            profileInfo.putString("userFamilyName", account.getFamilyName());
            profileInfo.putString("userEmail", account.getEmail());
            profileInfo.putString("userPhoto", String.valueOf(account.getPhotoUrl()));


            Navigation.findNavController(root).navigate(R.id.action_nav_home_to_nav_gallery,profileInfo);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Home Frag", "signInResult:failed code=" + e.getStatusCode());
         //   updateUI(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

    }
}