package com.gaurav.demoapp.ui.register;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gaurav.demoapp.R;
import com.gaurav.demoapp.utils.CommonMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    Button firebaseRegisterBtn;
    EditText editTextEmail,editTextPassword;
    FirebaseAnalytics firebaseAnalytics;


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
    FirebaseAuth firebaseAuth;
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


        firebaseRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailId = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(TextUtils.isEmpty(emailId) || TextUtils.isEmpty(password)){
                    Toast.makeText(getContext(),"Please enter email and password", Toast.LENGTH_SHORT).show();
                 //   CommonMethod.showToast(getContext(),"Please enter email and password", Toast.LENGTH_SHORT);
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

                    Navigation.findNavController(registerView).navigateUp();

                   // CommonMethod.showToast(getContext(),"Successfully Registered",Toast.LENGTH_SHORT);
                }else{
                    Toast.makeText(getContext(),"Registration failed", Toast.LENGTH_SHORT).show();

                 //   CommonMethod.showToast(getContext()," Registration failed.",Toast.LENGTH_SHORT);

                }

            }
        });


    }
}