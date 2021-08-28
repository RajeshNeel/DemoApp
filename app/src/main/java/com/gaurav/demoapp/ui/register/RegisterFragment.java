package com.gaurav.demoapp.ui.register;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gaurav.demoapp.R;
import com.gaurav.demoapp.Users;
import com.gaurav.demoapp.utils.CommonMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.os.Build.USER;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    Button firebaseRegisterBtn;
    EditText editTextEmail,editTextPassword;
    private FirebaseAnalytics firebaseAnalytics;
    private Users users;


    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    View registerView;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         registerView = inflater.inflate(R.layout.fragment_register, container, false);

        firebaseRegisterBtn = registerView.findViewById(R.id.firebase_sign_up_btn);
        editTextEmail = registerView.findViewById(R.id.edit_text_user_email);
        editTextPassword = registerView.findViewById(R.id.password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(USER);


        firebaseRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailId = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String fullName =

                if(TextUtils.isEmpty(emailId) || TextUtils.isEmpty(password)){
                    Toast.makeText(getContext(),"Please enter email and password", Toast.LENGTH_SHORT).show();
                 //   CommonMethod.showToast(getContext(),"Please enter email and password", Toast.LENGTH_SHORT);
                    return;
                }else{
                    registerUserOnFireBase(emailId,password);
                }

            }
        });


        return registerView;
    }

    private void registerUserOnFireBase(String emailId, String password) {

        firebaseAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Successfully Registered", Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();

                    bundle.putString("users_signUp", "successful");
                    firebaseAnalytics.logEvent("users_signUp", bundle);


                    FirebaseUser firebaseUser =  firebaseAuth.getCurrentUser();

                    if(firebaseUser!=null){
                        String userId = databaseReference.push().getKey();
                        databaseReference.child(userId).setValue(user);
                    }

                 /*   AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Are you sure want to Choose Profile image?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    openGallery();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(task.getResult().getAdditionalUserInfo().getUsername())
                            .setPhotoUri(Uri.parse(String.valueOf(imageFile)))
                            .build();


                         user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated."+profileUpdates);

                                        Navigation.findNavController(registerView).navigateUp();
                                        CommonMethod.showToast(getContext(),"Successfully Registered",Toast.LENGTH_SHORT);

                                    }
                                }
                            });*/



                  //

                }else{
                    Toast.makeText(getContext(),"Registration failed", Toast.LENGTH_SHORT).show();

                 //   CommonMethod.showToast(getContext()," Registration failed.",Toast.LENGTH_SHORT);

                }

            }
        });


    }

    static int REQUEST_GALLERY = 5;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    File   imageFile;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageURI = data.getData();
                   imageFile = new File(getRealPathFromURI(selectedImageURI));

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