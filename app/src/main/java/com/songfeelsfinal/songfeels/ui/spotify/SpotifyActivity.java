package com.songfeelsfinal.songfeels.ui.spotify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songfeelsfinal.songfeels.MainActivity;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.adapters.ListTrackAdapter;
import com.songfeelsfinal.songfeels.models.YoutubeDataModel;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomImage;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomPlaylist;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Recommendations;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SpotifyActivity extends AppCompatActivity implements UserPlayListTrackAdapter.OnUserPlaylistItemPositionClickListener {

    private SpotifyService mSpotifyService;
    private ArrayList<CustomTrack> mTracksList;
    private ArrayList<CustomImage> mImagesTracksList;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewAddToUserPlaylist;
    private EditText mEditTextCreateNewPlaylist;
    private ListTrackAdapter mListTrackAdapter;
    private UserPlayListTrackAdapter mUserPlayListTrackAdapter;
    private String genreSong;
    private String amountSong;
    private Toolbar mToolbar;
    Button btnSkipOrCreate;
    private Button  btnSavePlaylistSpotify;
    int totalPlaylist = 0;
    // for Youtube
    private ArrayList<YoutubeDataModel> mListData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    YoutubeDataModel youtubeObject;

    ArrayList<YoutubeDataModel> mList;
    private Map<String,Object> playlistData;
    private SharedPreferences sharedPreferencesPlaylistTrack;
    private ArrayList<CustomPlaylist> mPlaylistTrack;
    private String userPlaylistName;
    int positionUserPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_recommend_list);

        sharedPreferencesPlaylistTrack = getSharedPreferences("UserPlaylist", MODE_PRIVATE);
        totalPlaylist = sharedPreferencesPlaylistTrack.getInt("PlaylistTotal",0);
        totalPlaylist += 1;

        //tvPlaylistName.setText(playlistName);
        mToolbar = findViewById(R.id.tbRecommendSong);
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        Intent intent = getIntent();
        genreSong = intent.getStringExtra("genre");
        amountSong = intent.getStringExtra("amount");
        Map<String, Object> map = new HashMap<>();

        if(!genreSong.isEmpty() && !amountSong.isEmpty()){
            map.put("limit", amountSong);
            map.put("seed_genres",genreSong);
        }

        mSpotifyService = ((App) SpotifyActivity.this.getApplication()).getSpotifyService();

        mSpotifyService.getRecommendations(map, getRecommendationsCallBack);

        mRecyclerView = findViewById(R.id.recyclerViewSpotifyRecommend);
        btnSavePlaylistSpotify = findViewById(R.id.buttonSavePlaylistSpotify);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        mListData = new ArrayList<>();
        mList = new ArrayList<>();
        playlistData = new HashMap<>();

        addDataToListView();
    }

    private Callback<Recommendations> getRecommendationsCallBack = new Callback<Recommendations>() {
        @Override
        public void success(Recommendations recommendations, Response response) {

            Log.i("Song", "Status" + response.getStatus() + " Body " + response.getBody());


//                setActionBar(recommendations.tracks.size(), mArtistName);
            mTracksList = new ArrayList<>();
            mImagesTracksList = new ArrayList<>();

            for (Track track : recommendations.tracks) {
                CustomTrack customTrack = new CustomTrack();

                customTrack.setId(track.id);
                customTrack.setTrackName(track.name);
                customTrack.setPreviewStreamUrl(track.preview_url);
                customTrack.setExternalUrl(track.external_urls.get("spotify"));
                customTrack.setAlbumImageSmallUrl(track.album.images.get(0).url);

                for (ArtistSimple artistSimple : track.artists) {
                    customTrack.setArtistsNamesList(artistSimple.name);
                }

                mTracksList.add(customTrack);


                Log.i("Song", track.name + " " + track.duration_ms * 0.00001 + "second");
            }

            setRecyclerViewListData(mTracksList);
        }

        @Override
        public void failure(RetrofitError error) {
            Toast.makeText(SpotifyActivity.this, error.getLocalizedMessage() + error.getBody().toString() + error.getResponse().getReason(), Toast.LENGTH_LONG).show();
            Log.e("Spotify Error", ""+error.getMessage() + " l " +  error.getUrl() +" i "+ error.getResponse().getReason());
        }
    };

    private void getUserPrivateID(CallbackGetUserPrivateID callbackGetUserPrivateID) {
        mSpotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                callbackGetUserPrivateID.onSuccess(userPrivate.id);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    private void createPlaylistSpotify(String userId, CallbackGetPlaylistID callbackGetPlaylistID){

        Map<String,Object> objectMapSpotify = new HashMap<>();
        objectMapSpotify.put("name", userPlaylistName);
        objectMapSpotify.put("description", "");
        objectMapSpotify.put("public", false);

       // mSpotifyService = ((App) SpotifyActivity.this.getApplication()).getSpotifyService();


        mSpotifyService.createPlaylist(userId, objectMapSpotify, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                Toast.makeText(SpotifyActivity.this, "Created and Save Playlist" + playlist.id, Toast.LENGTH_SHORT).show();
                callbackGetPlaylistID.onSuccess(userId, playlist.id);
                Log.i("SpotifySavePlaylist", playlist.id);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("SpotifyError", error.getMessage() + " " + error.getBody().toString());

            }
        });
    }

    private void saveTracksToPlaylistSpotify(String userId, String playlistId) {
        String tracks = "";
        for (int i = 0; i < mTracksList.size(); i++) {
            tracks += "spotify:track:" + mTracksList.get(i).getId() + ",";
        }
        Log.i("GenSpotify", tracks.substring(0,tracks.length() - 1));

        Map<String,Object> tracksStringMapQuery = new HashMap<>();
        tracksStringMapQuery.put("uris",tracks);
        tracksStringMapQuery.put("position",0);

        Map<String,Object> tracksStringMapBody = new HashMap<>();
        tracksStringMapBody.put("uris",tracks);
        tracksStringMapBody.put("position",0);

        mSpotifyService = ((App) SpotifyActivity.this.getApplication()).getSpotifyService();

        mSpotifyService.addTracksToPlaylist(userId, playlistId, tracksStringMapQuery, tracksStringMapBody, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                Toast.makeText(SpotifyActivity.this, "All tracks are added",Toast.LENGTH_SHORT).show();
                Log.e("SpotifyLog", ": "+response.getStatus());
                Intent intent = new Intent(SpotifyActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SpotifyLog", "Error: "+error.getMessage());
            }
        });
    }

    private void getUserPrivateIDandPlaylistID(CallbackGetUserPrivateID callbackGetUserPrivateID) {
        getUserPrivateID(callbackGetUserPrivateID);
    }

    private void savePlaylistSpotify(){
        getUserPrivateIDandPlaylistID(new CallbackGetUserPrivateID() {
            @Override
            public void onSuccess(String userId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    if (userPlaylistName != null) {
                            createPlaylistSpotify(userId, new CallbackGetPlaylistID() {
                                @Override
                                public void onSuccess(String userId, String playlistId) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveTracksToPlaylistSpotify(userId, playlistId);
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception ex) {
                                    Log.e("SpotifyErrorActivity", "Exception: " + ex.getMessage());

                                }

                            });
                        } else if (positionUserPlaylist >= 0){
                        Toast.makeText(SpotifyActivity.this, "Added musics to "+ mPlaylistTrack.get(positionUserPlaylist).getName() + " already", Toast.LENGTH_SHORT).show();
                        saveTracksToPlaylistSpotify(userId, mPlaylistTrack.get(positionUserPlaylist).getId());

                    }
                    }
                });
            }

            @Override
            public void onError(Exception ex) {

            }
        });

    }

    @Override
    public void OnUserPlaylistItemPositionClick(ArrayList<CustomPlaylist> mPlaylistTrack, int position) {
        positionUserPlaylist = position;
        Log.i("SpotifyPos", "position" + position + " id : " + mPlaylistTrack.get(position).getId() + " name : " + mPlaylistTrack.get(position).getName());
        savePlaylistSpotify();
    }


    interface CallbackGetUserPrivateID {
        void onSuccess(String response);

        void onError(Exception ex);
    }

    interface CallbackGetPlaylistID {
        void onSuccess(String userId, String playlistId);

        void onError(Exception ex);
    }


    private void setRecyclerViewListData(ArrayList<CustomTrack> customTracks) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListTrackAdapter = new ListTrackAdapter(SpotifyActivity.this, customTracks);
        mRecyclerView.setAdapter(mListTrackAdapter);

    }


    private class RequestYoutubeAPI extends AsyncTask<String, String, List<String>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<String> doInBackground(String... params) {

                List<String> jsonYoutubeList = new ArrayList<>();
                String json;
                String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAK1WK82f0B3iTV2I1KhqtecZlcswZmcXc";

                for (int i = 0; i < mTracksList.size(); i++) {

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + mTracksList.get(i).getTrackName().replace(" ", "%20") + "%20" + mTracksList.get(i).getArtistsNamesList().replace(" ", "%20") + "&regionCode=TH&type=video&videoCategoryId=10&maxResults=1&key=" + GOOGLE_YOUTUBE_API_KEY);

                    try {

                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity httpEntity = response.getEntity();
                        json = EntityUtils.toString(httpEntity);


                        Log.d("HTTP", json);

                        jsonYoutubeList.add(json);

                        Log.i("jsonYoutubeList", jsonYoutubeList.toString());

                    } catch (Exception e) {
                        return new ArrayList<>();
                    }
                }
                return jsonYoutubeList;
            }

            @Override
            protected void onPostExecute(List<String> response) {
                super.onPostExecute(response);
                for (int i = 0; i < response.size(); i++) {
                    if (!response.isEmpty()) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.get(i));
                            Log.e("response", jsonObject.toString());
                            mListData = parseVideoListFromResponse(jsonObject);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (mListData.size() == 0) {
                    Toast.makeText(SpotifyActivity.this, "exceed limit API or No data", Toast.LENGTH_LONG).show();

                }

                // ose it after response

                Log.i("SongFeels", "mList" + mListData);
                Log.i("SongFeels", "mListSize " + mListData.size());
            }
        }

        private ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
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

                                    playlistData.put("songs",mList);

                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("SongFeels", "mList" + mList.size());

            return mList;
        }


    private void addDataToListView() {

        //do action to edittext
        btnSavePlaylistSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.show();
//                openDialog();
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        SpotifyActivity.this, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.layout_bottom_sheet
                        ,(LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );

                mEditTextCreateNewPlaylist = bottomSheetView.findViewById(R.id.etEnterPlaylistName);
                btnSkipOrCreate = bottomSheetView.findViewById(R.id.btnSkipCreatePlaylistName);
                //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                //Date date = new Date();
                mEditTextCreateNewPlaylist.setHint("My playlist #" + totalPlaylist);
                mEditTextCreateNewPlaylist.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        btnSkipOrCreate.setText("SKIP");
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        btnSkipOrCreate.setText("CREATE");

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (!mEditTextCreateNewPlaylist.getText().toString().isEmpty()){
                            btnSkipOrCreate.setText("CREATE");
                        }else{
                            btnSkipOrCreate.setText("SKIP");
                        }
                    }
                });{

                }
                bottomSheetView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                btnSkipOrCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(SpotifyActivity.this, "Created playlist", Toast.LENGTH_SHORT).show();
                        if (mEditTextCreateNewPlaylist.getText().toString().isEmpty()){
                            userPlaylistName = "My playlist #" + totalPlaylist;
                            savePlaylistSpotify();
                            Toast.makeText(SpotifyActivity.this, "Playlist Name"+ userPlaylistName, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        }
                        else if (!mEditTextCreateNewPlaylist.getText().toString().isEmpty()){
                            userPlaylistName = mEditTextCreateNewPlaylist.getText().toString();
                            savePlaylistSpotify();
                            Toast.makeText(SpotifyActivity.this, "Playlist Name"+ userPlaylistName, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        }

                    }
                });

                sharedPreferencesPlaylistTrack = getSharedPreferences("UserPlaylist", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sharedPreferencesPlaylistTrack.getString("PlaylistTrack", null);
                Type type = new TypeToken<ArrayList<CustomPlaylist>>() {}.getType();
                mPlaylistTrack = gson.fromJson(json, type);

                mRecyclerViewAddToUserPlaylist = bottomSheetView.findViewById(R.id.rvAddToUserPlaylist);
                mRecyclerViewAddToUserPlaylist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                mUserPlayListTrackAdapter = new UserPlayListTrackAdapter(getApplicationContext(), mPlaylistTrack, SpotifyActivity.this);
                mRecyclerViewAddToUserPlaylist.setAdapter(mUserPlayListTrackAdapter);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }

}
