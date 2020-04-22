package com.songfeelsfinal.songfeels.ui.insertPhoto;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.songfeelsfinal.songfeels.R;


public class InsertPhotoActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_photo);

        navView = findViewById(R.id.nav_view_insert_photo);
        navView.setOnNavigationItemSelectedListener(this);

        loadFragment(new CameraFragment());

    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Fragment fragment = null;
        fragment = new CameraFragment();
        navView.setSelectedItemId(R.id.navigation_camera);
        loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_insert_photo, fragment)
                    .commit();

            return true;

        }
        return false;

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_camera:
                fragment = new CameraFragment();
                break;

            case R.id.navigation_gallery:
                fragment = new GalleryFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
