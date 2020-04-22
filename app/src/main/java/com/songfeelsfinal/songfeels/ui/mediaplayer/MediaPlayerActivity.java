package com.songfeelsfinal.songfeels.ui.mediaplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.spotify.App;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MediaPlayerActivity extends AppCompatActivity {

    Button playBtn, nextBtn, prevBtn;
    SeekBar positionBar;
    TextView currentTime;
    TextView totalTime;
    TextView trackName;
    TextView artistName;
    Toolbar mToolbar;
    static MediaPlayer mMediaPlayer;
    ArrayList<CustomTrack> customTracks;

    int position;
    Bundle songExtraData;
    ImageView imageViewMusic;

    private SpotifyService mSpotifyService;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        currentTime = findViewById(R.id.currentTimer);
        totalTime = findViewById(R.id.totalTimer);
        positionBar = findViewById(R.id.positionBar);
        imageViewMusic = findViewById(R.id.imageViewMusic);
        trackName = findViewById(R.id.tvTrackName);
        artistName = findViewById(R.id.tvArtistName);

        mSpotifyService = ((App) MediaPlayerActivity.this.getApplication()).getSpotifyService();

        Intent songData = getIntent();
        songExtraData = songData.getExtras();
        customTracks = songExtraData.getParcelableArrayList("customTracksList");

        position = songExtraData.getInt("positionSongList", 0);

        //Set title on Tool Bar
        mToolbar = findViewById(R.id.tbMediaPlayer);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });

        // check if music is playing then close it for playing the next
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        mMediaPlayer = new MediaPlayer();
        positionBar.setMax(100);

        // setup play/pause button
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mMediaPlayer.pause();
                    playBtn.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                } else {
                    mMediaPlayer.start();
                    playBtn.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    updateSeekBar();
                }
            }
        });

        prepareMediaPlayer(position); // start media player

        // position bar
        positionBar.setOnTouchListener((v, event) -> {
            SeekBar seekBar = (SeekBar) v;
            int playPosition = (mMediaPlayer.getDuration() / 100) * seekBar.getProgress();
            mMediaPlayer.seekTo(playPosition);
            if (!mMediaPlayer.isPlaying()) {
                currentTime.setText("0:00");
            } else {
                currentTime.setText(milliSecondsToTimer(mMediaPlayer.getCurrentPosition()));
            }
            return false;
        });

        mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> positionBar.setSecondaryProgress(percent));


        nextBtn.setOnClickListener(v -> {
            if (position < customTracks.size() - 1) {
                // check if the current position of the song in the list is less than the total song present in the list
                // increase the position by one to play next song in the list

                position++;
            } else {
                // if the position is greater than or equal to the number of songs on the list
                // set the position to zero

                position = 0;
            }

            // play the song in the list with position
            mMediaPlayer.reset();
            prepareMediaPlayer(position);
        });

        prevBtn.setOnClickListener(v -> {
            if (position <= 0) {
                // if the position of the song on the list is less or equal to zero
                position = customTracks.size() - 1;
            } else {
                position--;
            }
            mMediaPlayer.reset();
            prepareMediaPlayer(position);
        });


        // Volume Bar
        mMediaPlayer.setVolume(0.5f, 0.5f);

    }

    private void prepareMediaPlayer(final int position) {
        try {
            Picasso.get().load(customTracks.get(position).getAlbumImageSmallUrl()).into(imageViewMusic);
            trackName.setText(customTracks.get(position).getTrackName());
            artistName.setText(customTracks.get(position).getArtistsNamesList());


            // get song from Spotify preview url to Media Player
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mSpotifyService.getTrack(customTracks.get(position).getId(), new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    if (track.preview_url != null) {
                        try {
                            mMediaPlayer.setDataSource(track.preview_url);
                            mMediaPlayer.prepare();
                            totalTime.setText(milliSecondsToTimer(mMediaPlayer.getDuration()));
                            playBtn.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                            mMediaPlayer.start();
                            updateSeekBar();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(MediaPlayerActivity.this, "This song can't play preview", Toast.LENGTH_SHORT).show();
                        positionBar.setProgress(0);
                        playBtn.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
                        currentTime.setText("0:00");
                        totalTime.setText("0:00");


                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });


        } catch (Exception e) {
            Toast.makeText(MediaPlayerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mMediaPlayer.getCurrentPosition();
            if (!mMediaPlayer.isPlaying()) {
                currentTime.setText("0:00");
            } else {
                currentTime.setText(milliSecondsToTimer(currentDuration));
            }
        }
    };

    private void updateSeekBar() {
        if (mMediaPlayer.isPlaying()) {
            positionBar.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";
        String secondsString;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60) / (1000 * 60));
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;

        return timerString;
    }

    ;

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

}

