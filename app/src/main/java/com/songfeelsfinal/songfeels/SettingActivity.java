package com.songfeelsfinal.songfeels;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.songfeelsfinal.songfeels.ui.showEmotion.CustomDialog;

public class SettingActivity extends AppCompatActivity implements CustomDialog.CustomGenreDialogListener,
        CustomDialog.CustomDialogListener, CustomDialog.CustomSavePlaylistDialogListener {

    private View btGenre;
    private EditText mName;
    private TextView mGenre;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView listGenre;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //tvPlaylistName.setText(playlistName);
        mToolbar = findViewById(R.id.tbSetting);
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        btGenre = findViewById(R.id.loGenreId);
        mGenre = findViewById(R.id.tvGenreId);

        sharedPreferences = getSharedPreferences("MyChoice", Context.MODE_PRIVATE);
        String genre = sharedPreferences.getString("MyChoice", "");
        mGenre.setText(genre);


        btGenre.setOnClickListener(view -> addDataToListView());
    }

    private void addDataToListView() {
        openDialog();
    }

    private void openDialog() {
        CustomDialog customDialog = new CustomDialog("Genre");
        customDialog.show(getSupportFragmentManager(), "Genre Selection");
    }

    @Override
    public void applyTextsListGenre(String listGenre) {
        mGenre.setText(listGenre);
    }

    @Override
    public void applyTextsCustomTime(String customTime) {

    }

    @Override
    public void applyTextsListTime(String listTime) {

    }

    @Override
    public void applyTextSavePlaylist(String savePlaylistName) {

    }

    @Override
    public void applyTextSavePlaylistUserPlaylistID(String savePlaylistUserPlaylistID, String playlistIdSpotify) {

    }
}
