package com.songfeelsfinal.songfeels.ui.youtube;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.adapters.SelectSongAdapter;
import com.songfeelsfinal.songfeels.models.YoutubeDataModel;

import java.util.ArrayList;

public class SelectSongToPlaylist extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SelectSongAdapter selectSongAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView.setLayoutManager(new LinearLayoutManager(SelectSongToPlaylist.this));
        selectSongAdapter = new SelectSongAdapter(this,(ArrayList<YoutubeDataModel>) getIntent().getSerializableExtra("dataSet")); //send to VideoPostAdapter
        recyclerView.setAdapter(selectSongAdapter);
    }
}