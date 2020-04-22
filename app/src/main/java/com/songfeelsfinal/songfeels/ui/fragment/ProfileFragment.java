package com.songfeelsfinal.songfeels.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.songfeelsfinal.songfeels.MainActivity;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.SettingActivity;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private TextView mUserName;
    private TextView mEmail;
    private ImageView mImageView;
    private TextView mSetting;
    private View mSignOutLayout;
    private View mSettingLayout;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Context context;
    Toolbar mToolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mUserName = view.findViewById(R.id.tvUserName);
        mEmail = view.findViewById(R.id.tvEmail);
        mImageView = view.findViewById(R.id.profile_image);
        mSetting = view.findViewById(R.id.tvSettingId);
        mSettingLayout = view.findViewById(R.id.settingLayoutId);
        mSignOutLayout = view.findViewById(R.id.signOutLayoutId);

        setProfile();
        context = container.getContext();


        mSettingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            startActivity(intent);
        });

        mSignOutLayout.setOnClickListener(v -> {
            sp = getActivity().getSharedPreferences("Pref_CheckLogin", MODE_PRIVATE);
            editor = sp.edit();
            editor.putBoolean("Check",false);
            editor.putBoolean("checkBase",false);
            editor.apply();
            startActivity(new Intent(getActivity(),MainActivity.class));
            AuthorizationClient.clearCookies(context);
        });



        return view;
    }



    private void setProfile() {
        //Get Data From Share Preference
        sp = getActivity().getSharedPreferences("PREF_PROFILE", MODE_PRIVATE);
        //Set Profile
        Picasso.get().load(sp.getString("ProfileImageUrl", "not found")).into(mImageView);
        mUserName.setText(sp.getString("UserName", "Not found"));
        mEmail.setText(sp.getString("Email", " "));
    }


}