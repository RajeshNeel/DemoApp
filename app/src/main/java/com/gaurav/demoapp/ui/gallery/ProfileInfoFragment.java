package com.gaurav.demoapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.gaurav.demoapp.R;
import com.google.android.gms.common.SignInButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileInfoFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    @BindView(R.id.imageViewUser) ImageView imageViewUser;
    @BindView(R.id.userNameText) TextView userNameText;

    TextView userNameTexts,userEmailTexts;
    ImageView imageViewUsers;


    @BindView(R.id.userEmailText) TextView userEmailText;

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

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        if(getArguments()!=null){

            userNameTexts.setText(getArguments().getString("UserName"));
            userEmailTexts.setText(getArguments().getString("userEmail"));
            Glide.with(getContext()).load(getArguments().getString("userPhoto")).into(imageViewUsers);
        }


        return root;
    }
}