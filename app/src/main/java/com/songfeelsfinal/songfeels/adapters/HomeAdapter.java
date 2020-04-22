package com.songfeelsfinal.songfeels.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.spotify.App;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomPlaylist;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.songfeelsfinal.songfeels.ui.userplaylist.UserPlaylistActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.PlaylistsHolder> {
    private Context mContext;
    private ArrayList<CustomPlaylist> mPlaylistTrack;
    private String playlistId = "";
    private SpotifyService mSpotifyService;
    private ArrayList<CustomTrack> mTrack;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public HomeAdapter(Context context, ArrayList<CustomPlaylist> mPlaylistTrack) {
        this.mContext = context;
        this.mPlaylistTrack = mPlaylistTrack;

    }


    @NonNull
    @Override
    public PlaylistsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_adapter, parent, false);
        LoadTracks();
        return new PlaylistsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistsHolder holder, int position) {


        holder.playListName.setText(mPlaylistTrack.get(position).getName());
        holder.totalSong.setText(mPlaylistTrack.get(position).getTotalSong() + " songs");
        Picasso.get().load(mPlaylistTrack.get(position).getImageUrl()).into(holder.coverImagePlaylist);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,UserPlaylistActivity.class);
                intent.putExtra("mPlaylistId",mPlaylistTrack.get(position).getId());
                intent.putExtra("mPlaylistName",mPlaylistTrack.get(position).getName());
                intent.putExtra("mPlaylistImageURL",mPlaylistTrack.get(position).getImageUrl());
                intent.putExtra("mTotalSong",mPlaylistTrack.get(position).getTotalSong());


                sharedPreferences = mContext.getSharedPreferences("ExternalUrl", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("externalUrlFromPlaylist",mPlaylistTrack.get(position).getExternalUrl());
                editor.commit();

                mContext.startActivity(intent);
            }
        });

    }


    private void LoadTracks() {
        mSpotifyService = ((App) mContext.getApplicationContext()).getSpotifyService();


        mSpotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                mSpotifyService.getPlaylistTracks(userPrivate.id, playlistId, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        mTrack = new ArrayList<>();
                        for (PlaylistTrack trackSimple : playlistTrackPager.items) {
                            CustomTrack customTrack = new CustomTrack();
                            customTrack.setTrackName(trackSimple.track.name);
                            customTrack.setAlbumImageSmallUrl(trackSimple.track.album.images.get(0).url);
                            customTrack.setArtistsNamesList(trackSimple.track.artists.toString());
                            customTrack.setId(trackSimple.track.id);

                            mTrack.add(customTrack);

                        }

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

    @Override
    public int getItemCount() {
        return mPlaylistTrack.size();
    }


    public class PlaylistsHolder extends RecyclerView.ViewHolder {


        RecyclerView recyclerView;
        ImageView coverImagePlaylist;
        TextView playListName, totalSong;
        View click;

        public PlaylistsHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerView = itemView.findViewById(R.id.rvPlaylist);
            this.coverImagePlaylist = itemView.findViewById(R.id.imvPlaylist);
            this.playListName = itemView.findViewById(R.id.tvPlaylistName);
            this.totalSong = itemView.findViewById(R.id.tvTotalSong);
            this.click = itemView.findViewById(R.id.layoutId);
        }
    }
}
