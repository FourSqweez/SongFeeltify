package com.songfeelsfinal.songfeels.ui.userplaylist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.adapters.ListTrackAdapter;
import com.songfeelsfinal.songfeels.ui.spotify.App;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.songfeelsfinal.songfeels.ui.youtube.VideoPlayerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserPlaylistActivity extends AppCompatActivity {
    private SpotifyService mSpotifyService;
    private RecyclerView mRecyclerView;
    private ListTrackAdapter mListTrackAdapter;
    private ArrayList<CustomTrack> mCustomTracks;
    String playlistId = "";
    ImageView imageViewMusic;
    TextView tvPlaylistName;
    Toolbar mToolbar;
    String playlistName;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button btnSpotify, btnYoutube;
    String externalUrlFromPlaylist = "";
    String externalUrlFromTrack = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_tracks);

        sharedPreferences = getSharedPreferences("ExternalUrl", Context.MODE_PRIVATE);
        externalUrlFromPlaylist = sharedPreferences.getString("externalUrlFromPlaylist","no data");
        externalUrlFromTrack = sharedPreferences.getString("externalUrlFromTrack","no data");

        Log.i("ExternalUrl","FromPlaylist : " + externalUrlFromPlaylist);
        Log.i("ExternalUrl","FromTrack : " + externalUrlFromTrack);

        btnSpotify = findViewById(R.id.btnSpotify);
        btnYoutube = findViewById(R.id.btnYoutube);
        mRecyclerView  = findViewById(R.id.rvUserPlaylist);
        imageViewMusic = findViewById(R.id.imageViewMusic);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);

        Intent intent = getIntent();
        playlistId = intent.getExtras().getString("mPlaylistId");
        playlistName = intent.getExtras().getString("mPlaylistName");
        String playlistImageURL = intent.getExtras().getString("mPlaylistImageURL");

        mSpotifyService = ((App) UserPlaylistActivity.this.getApplication()).getSpotifyService();
        LoadTracks(playlistId);

       sharedPreferences = getSharedPreferences("Playlist", Context.MODE_PRIVATE);
       editor = sharedPreferences.edit();
       editor.putString("mPlaylistId",playlistId);
       editor.putString("mPlaylistName",playlistName);
       editor.putString("mPlaylistImageURL",playlistImageURL);
       editor.commit();

        //tvPlaylistName.setText(playlistName);
        mToolbar = findViewById(R.id.tbPlaylistName);
        mToolbar.setTitle(playlistName);
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        Picasso.get().load(playlistImageURL).into(imageViewMusic);

        btnSpotify.setOnClickListener(v -> {
            Intent intentToSpotify = new Intent(Intent.ACTION_VIEW);
            Log.i("ExternalUrl",externalUrlFromPlaylist+"&autoplay=true" );
            intentToSpotify.setData(Uri.parse(externalUrlFromPlaylist));
            startActivity(intentToSpotify);

        });

        btnYoutube.setOnClickListener(v -> {
            Intent intentToYoutube = new Intent(UserPlaylistActivity.this, VideoPlayerActivity.class);
            intentToYoutube.putExtra("position_youtube_video", 0);
            intentToYoutube.putExtra("youtube_video_list_tracks", mCustomTracks);
            startActivity(intentToYoutube);
        });
    }

    private void LoadTracks(String playlistId) {

        mSpotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                mSpotifyService.getPlaylistTracks(userPrivate.id, playlistId, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        mCustomTracks = new ArrayList<>();
                        for (PlaylistTrack playlistTrack : playlistTrackPager.items) {
                            CustomTrack customTrack = new CustomTrack();
                            customTrack.setTrackName(playlistTrack.track.name);
                            if (!playlistTrack.track.album.images.isEmpty()){
                                customTrack.setAlbumImageSmallUrl(playlistTrack.track.album.images.get(0).url);
                            }
                            customTrack.setPreviewStreamUrl(playlistTrack.track.preview_url);
                            customTrack.setId(playlistTrack.track.id);
                            customTrack.setExternalUrl(playlistTrack.track.external_urls.get("spotify"));
                            for (ArtistSimple artistSimple : playlistTrack.track.artists){
                                customTrack.setArtistsNamesList(artistSimple.name);
                            }
                            mCustomTracks.add(customTrack);
                        }
                        setAdapter(mCustomTracks);
                    }
                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void setAdapter(ArrayList<CustomTrack> customTracks){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListTrackAdapter = new ListTrackAdapter(UserPlaylistActivity.this, customTracks);
        mRecyclerView.setAdapter(mListTrackAdapter);

    }
}
