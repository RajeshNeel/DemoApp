package com.gaurav.demoapp.ui.register;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaurav.demoapp.R;
import com.gaurav.demoapp.pojo.Users;
import com.gaurav.demoapp.utils.CommonMethod;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {


    @BindView(R.id.firebase_sign_up_btn) AppCompatButton firebaseRegisterBtn;
    @BindView(R.id.edit_text_user_email) EditText editTextEmail;
    @BindView(R.id.password) EditText editTextPassword;
    @BindView(R.id.user_profile_image) ImageView userImage;
    @BindView(R.id.edit_text_user_full_Name) EditText edit_text_user_full_Name;

    private FirebaseAnalytics firebaseAnalytics;
    private Users user;
    private Uri profileImageUri;
    String emailId,password,fullName;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();


        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    View registerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,databaseRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        registerView = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this,registerView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        return registerView;
    }



    @OnClick({R.id.firebase_sign_up_btn,R.id.user_profile_image})
    public void goToRegisterDestination(View view){
        switch(view.getId()){

            case R.id.firebase_sign_up_btn:

                emailId = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                fullName = edit_text_user_full_Name.getText().toString();
                validateUserRegistrationInput(emailId,password,fullName,profileImageUri.toString());
                break;
            case R.id.user_profile_image:
                openGallery();
                break;


        }
    }





    private void validateUserRegistrationInput(String emailId, String password, String fullName, String photoUri) {

        if(photoUri.isEmpty() || photoUri.length()==0){

            Toast.makeText(getContext(),"Please select profile image", Toast.LENGTH_SHORT).show();
        }

       else  if(fullName.isEmpty()){

            Toast.makeText(getContext(),"Please enter name", Toast.LENGTH_SHORT).show();
            edit_text_user_full_Name.requestFocus();

        }else if(emailId.isEmpty()){
            Toast.makeText(getContext(),"Enter emailId", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();

        }
        else if(!CommonMethod.isValidFirebaseEmailId(emailId)){

            Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();

        }

       else if (password.isEmpty()){
            Toast.makeText(getContext(),"Enter password", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();


        }else{

           if(CommonMethod.haveNetworkConnection(getContext())){

               user = new Users(emailId,password,fullName,profileImageUri.toString());
               CommonMethod.createProgress(getContext(),"Registering user");

               registerUserOnFireBase(emailId,password,user);
           }else {

               Toast.makeText(getContext(),"Internet is missing.", Toast.LENGTH_SHORT).show();

           }


        }


    }

    private void registerUserOnFireBase(String emailId, String password, Users users) {

        user = users;


        firebaseAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                CommonMethod.closeProgress();

                if(task.isSuccessful()){
                    Bundle bundle = new Bundle();
                    bundle.putString("users_signUp", "successful");
                    firebaseAnalytics.logEvent("users_signUp", bundle);
                    uploadUserDataToFirebase(task,user);

                }
                else{
                    Toast.makeText(getContext(),"Registration failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    //   CommonMethod.showToast(getContext()," Registration failed.",Toast.LENGTH_SHORT);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void uploadUserDataToFirebase(Task<AuthResult> task, Users user) {


        FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){

            String userKey = databaseReference.push().getKey();

            databaseReference.child(userKey).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(getContext(),"User Registered Successfully", Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(registerView).navigateUp();

                    }
                    else{
                        Toast.makeText(getContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });
        }


    }

    static int REQUEST_GALLERY = 5;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Pic Image"), REQUEST_GALLERY);
    }

    File   imageFile;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("RegisterFrag","onActivityResult :"+"image uri :"+requestCode+" result code :"+resultCode);

        if (resultCode == RESULT_OK) {

            if (requestCode == 5) {

                profileImageUri = data.getData();

                   imageFile = new File(getRealPathFromURI(profileImageUri));

                   userImage.setImageURI(profileImageUri);

                   Log.v("RegisterFrag","onActivityResult :"+"image uri :"+imageFile);

            }
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}