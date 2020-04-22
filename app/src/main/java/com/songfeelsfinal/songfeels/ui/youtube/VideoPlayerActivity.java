package com.songfeelsfinal.songfeels.ui.youtube;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.interfacehelper.RequestInterface;
import com.songfeelsfinal.songfeels.models.YoutubeDataModel;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class VideoPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubeListTrackAdapter.OnYouTubeVideoClickListener {
    public static final String GOOGLE_API_KEY = "AIzaSyCbfyHaWmxlIpYMV6aTnTlf5m9N27PG1s8";
    public int position_video;
    YouTubePlayerView mYouTubePlayerView = null;
    YouTubePlayer mYouTubePlayer;
    Toolbar mToolbar;
    private ArrayList<CustomTrack> customTracks;
    private ArrayList<YoutubeDataModel> mList;
    private ArrayList<YoutubeDataModel> mListData;
    private YoutubeDataModel youtubeObject;
    private Button btnNext, btnPrev, btnPlay;
    private RecyclerView mRecyclerView;
    private YouTubeListTrackAdapter mYouTubeListTrackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnPlay = findViewById(R.id.btnPlay);
        mRecyclerView = findViewById(R.id.rvYoutubePlaylist);

        mToolbar = findViewById(R.id.tbMediaPlayer);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        mYouTubePlayerView = findViewById(R.id.youtube_player_id);
        mYouTubePlayerView.initialize(GOOGLE_API_KEY, this);
        mListData = new ArrayList<>();
        Intent intent = this.getIntent();
        position_video = intent.getExtras().getInt("position_youtube_video", 0);
        Log.d(TAG, "VideoId Player : " + position_video);

        customTracks = (ArrayList<CustomTrack>) getIntent().getSerializableExtra("youtube_video_list_tracks");
        getTrackFromYoutube(customTracks, position_video);

        btnNext.setOnClickListener(v -> {
            if (position_video < customTracks.size() - 1) {
                // check if the current position of the song in the list is less than the total song present in the list
                // increase the position by one to play next song in the list

                position_video++;
            } else {
                // if the position is greater than or equal to the number of songs on the list
                // set the position to zero
                position_video = 0;
            }

            // play the song in the list with position
            getTrackFromYoutube(customTracks, position_video);
        });

        btnPrev.setOnClickListener(v -> {
            if (position_video <= 0) {
                // if the position of the song on the list is less or equal to zero
                position_video = customTracks.size() - 1;
            } else {
                position_video--;
            }
            getTrackFromYoutube(customTracks, position_video);
        });

    }

    private void getTrackFromYoutube(ArrayList<CustomTrack> customTracks, int position_video) {

        setAdapter(customTracks, position_video);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterfaceApi = retrofit.create(RequestInterface.class);

        String musicAndArtistName = customTracks.get(position_video).getTrackName() + " "
                + customTracks.get(position_video).getArtistsNamesList();
        String apiKey = "AIzaSyCbfyHaWmxlIpYMV6aTnTlf5m9N27PG1s8";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("part","snippet");
        parameters.put("q", musicAndArtistName.replace(".", ""));
        parameters.put("regionCode","TH");
        parameters.put("type","video");
        parameters.put("videoCategoryId",10);
        parameters.put("maxResults",1);
        parameters.put("key",apiKey);

        Call<JsonObject> call = requestInterfaceApi.getSong(parameters);
        Log.i("jsonData", customTracks.get(position_video).getTrackName() + " "
                + customTracks.get(position_video).getArtistsNamesList());

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.i("jsonData55", ""+response.code() + " " + response.body() +" " +response.message() + " "+ response.errorBody());
                if (!response.isSuccessful()){
                    Log.i("jsonDataError", "Code : " + response.code() +" " +response.message() + " "+ response.errorBody());
                    return;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(response.body()));
                        mListData = getSongFromYouTube(jsonObject);
                        mYouTubePlayer.loadVideo(mList.get(0).getVideo_id());
                        for (YoutubeDataModel dataModel: mListData){
                            Log.i("jsonDataMlist", ""+ dataModel.getVideo_id());
                        }
                        Log.i("JsonDataError1", ""+mList.get(0).getVideo_id());
                    } catch (JSONException e) {

                        Log.i("JsonDataError1", ""+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i("JsonDataError2", ""+t.getMessage());
            }
        });
    }

    private ArrayList<YoutubeDataModel> getSongFromYouTube(JSONObject jsonObject) {
        mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                //get the data from snippet
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");

                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject = new YoutubeDataModel();
                                youtubeObject.setTitle(StringEscapeUtils.unescapeHtml4(title));
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);

                                mList.add(youtubeObject);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i("SongFeels", "mList" + mList.size());

        return mList;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("SongFeels", "onInitializationFail");
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestore) {
        Log.d("SongFeels", "onInitializationSuccess");

        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        if (!wasRestore) {
            mYouTubePlayer = player;

            // setup play/pause button
            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player.isPlaying()){
                        player.pause();
                        btnPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                    } else {
                        player.play();
                        btnPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    }
                }
            });
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
            Log.d("SongFeels", "onBuffering");
        }

        @Override
        public void onPaused() {
            btnPlay.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            Log.d("SongFeels", "onPaused");
        }

        @Override
        public void onPlaying() {
            btnPlay.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            Log.d("SongFeels", "onPlaying");
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            Log.d("SongFeels", "onError " + arg0);
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {

                // lets repeat the songs when it finished playing automatically
                if (position_video < customTracks.size() - 1) {
                    position_video++;
                } else {
                    position_video = 0;
                }

                getTrackFromYoutube(customTracks, position_video);
        }

        @Override
        public void onVideoStarted() {
            Log.d("SongFeels", "onVideoStarted");
        }
    };

    private void setAdapter(ArrayList<CustomTrack> customTracks, int videoPosition){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(videoPosition,0);
        mYouTubeListTrackAdapter = new YouTubeListTrackAdapter(VideoPlayerActivity.this, customTracks, videoPosition, this);
        mRecyclerView.setAdapter(mYouTubeListTrackAdapter);
    }

    @Override
    public void onYouTubeVideoClick(ArrayList<CustomTrack> customTracks, int position) {
        position_video = position;
        getTrackFromYoutube(customTracks, position_video);
    }
}
