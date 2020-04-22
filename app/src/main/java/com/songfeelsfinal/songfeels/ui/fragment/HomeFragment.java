package com.songfeelsfinal.songfeels.ui.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.adapters.HomeAdapter;
import com.songfeelsfinal.songfeels.ui.spotify.App;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomPlaylist;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import retrofit.RetrofitError;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {
    private ArrayList<CustomPlaylist> mPlaylistTrack;
    private String TAG = "Playlist";
    private SpotifyService mSpotifyService;
    private RecyclerView mRecyclerView;
    Toolbar mToolbar;
    TextView mHome;
    TextView mNoPlaylist;
    int invisible = 1;
    int totalPlaylist = 0;
    private SharedPreferences sharedPreferencesPlaylistTrack;
    private SharedPreferences.Editor editorPlaylistTrack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.rvPlaylist);



        mToolbar = view.findViewById(R.id.tbPlaylist);
        mHome = view.findViewById(R.id.tvHome);
        mNoPlaylist = view.findViewById(R.id.tvNoPlaylist);
        mRecyclerView.setHasFixedSize(true);

        mSpotifyService = ((App) getActivity().getApplication()).getSpotifyService();

        LoadPlaylists();
        initViews();


        if (invisible == 1){
            mToolbar.setVisibility(View.INVISIBLE);
        }

        return view;
    }


    private void initViews() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }


    private void LoadPlaylists() {
        //Load from Spotify API
        mSpotifyService.getMyPlaylists(new retrofit.Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playlistSimplePager, retrofit.client.Response response) {

                if (!playlistSimplePager.items.isEmpty()) {
                    mHome.setVisibility(View.INVISIBLE);
                    mNoPlaylist.setVisibility(View.INVISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    initViews();
                } else {
                    loadText();
                }

                mPlaylistTrack = new ArrayList<>();
                totalPlaylist = playlistSimplePager.total;
                Log.i(TAG, "Total : " + totalPlaylist);

                for (PlaylistSimple pl : playlistSimplePager.items) {
                    CustomPlaylist customPlaylist  = new CustomPlaylist();
                    customPlaylist.setName(pl.name);
                    if (!pl.images.isEmpty()) {
                        customPlaylist.setImageUrl(pl.images.get(0).url);

                    }
                    customPlaylist.setId(pl.id);
                    customPlaylist.setTotalSong(pl.tracks.total);
                    customPlaylist.setExternalUrl(pl.external_urls.get("spotify"));

                    mPlaylistTrack.add(customPlaylist);
                }

                setAdapter();
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("Error", error.getMessage());

            }
        });


    }

    void loadText() {
        mNoPlaylist.setText("You don't have any playlists \n ");
        mHome.setText("Please Click + button to create a new playlist");
    }

    private void setAdapter() {
        try {
            if (!mPlaylistTrack.isEmpty()){
                sharedPreferencesPlaylistTrack = getActivity().getSharedPreferences("UserPlaylist", MODE_PRIVATE);
                editorPlaylistTrack = sharedPreferencesPlaylistTrack.edit();
                editorPlaylistTrack.putInt("PlaylistTotal",totalPlaylist);
                Gson gson = new Gson();
                String json = gson.toJson(mPlaylistTrack);
                editorPlaylistTrack.putString("PlaylistTrack", json);
                editorPlaylistTrack.apply();
                RecyclerView.Adapter adt = new HomeAdapter(getActivity(),mPlaylistTrack);
                mRecyclerView.setAdapter(adt);
            }

        }catch (Exception e){
            Log.e(TAG, ": " + e.getMessage());


        }
    }
}
