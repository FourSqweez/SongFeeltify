package com.songfeelsfinal.songfeels;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.songfeelsfinal.songfeels.ui.fragment.HomeFragment;
import com.songfeelsfinal.songfeels.ui.fragment.ProfileFragment;
import com.songfeelsfinal.songfeels.ui.insertPhoto.InsertPhotoActivity;
import com.songfeelsfinal.songfeels.ui.showEmotion.CustomDialog;
import com.songfeelsfinal.songfeels.ui.spotify.App;
import com.songfeelsfinal.songfeels.ui.spotify.BaseActivity;
import com.songfeelsfinal.songfeels.ui.spotify.CallbackBaseActivityFinished;

import kaaes.spotify.webapi.android.SpotifyService;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, CallbackBaseActivityFinished,
        CustomDialog.CustomGenreDialogListener, CustomDialog.CustomDialogListener,
        CustomDialog.CustomSavePlaylistDialogListener {
    boolean checkLogIn;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    View rootView;
    private ProgressBar loadProgress;



    Fragment fragment = null;

    SpotifyService mSpotifyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.root_view);


        loadProgress = findViewById(R.id.progressBar);

        loadProgress.setVisibility(View.VISIBLE);

        sp = getSharedPreferences("Pref_CheckLogin", Context.MODE_PRIVATE);
        checkLogIn = sp.getBoolean("Check", true);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        mSpotifyService = ((App) this.getApplication()).getSpotifyService();


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            onDestroy();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void openDialog() {
        CustomDialog customDialog = new CustomDialog("Genre");
        customDialog.show(getSupportFragmentManager(), "Genre Selection");
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_camera:
                //fragment = new CameraFragment();
                Intent intent = new Intent(this, InsertPhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void OnBaseActivityLoadFinished(String baseLoadFinished) {
        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (preferences.getBoolean("firstLogin", true)) {
            openDialog();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstLogin", false);
            editor.commit();
            loadFragment(new HomeFragment());
        } else {
            loadFragment(new HomeFragment());
        }
        loadProgress.setVisibility(View.GONE);
    }

    @Override
    public void applyTextsCustomTime(String customTime) {

    }

    @Override
    public void applyTextsListTime(String listTime) {

    }

    @Override
    public void applyTextsListGenre(String listGenre) {

    }

    @Override
    public void applyTextSavePlaylist(String savePlaylistName) {

    }

    @Override
    public void applyTextSavePlaylistUserPlaylistID(String savePlaylistUserPlaylistID, String playlistIdSpotify) {

    }
}
