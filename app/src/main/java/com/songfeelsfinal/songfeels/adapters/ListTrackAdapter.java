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
import com.songfeelsfinal.songfeels.ui.mediaplayer.MediaPlayerActivity;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListTrackAdapter extends RecyclerView.Adapter<ListTrackAdapter.ViewHolder> {
    Context mContext;
    ArrayList<CustomTrack> customTracks;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public ListTrackAdapter(Context mContext, ArrayList<CustomTrack> customTracks) {
        this.mContext = mContext;
        this.customTracks = customTracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_list_track_adapter, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songName.setText(customTracks.get(position).getTrackName());
        holder.artistName.setText(customTracks.get(position).getArtistsNamesList());
        Picasso.get().load(customTracks.get(position).getAlbumImageSmallUrl()).into(holder.coverImageSong);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MediaPlayerActivity.class);
            intent.putExtra("positionSongList", position);
            intent.putExtra("customTracksList", customTracks);

            sharedPreferences = mContext.getSharedPreferences("ExternalUrl", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("externalUrlFromTrack", customTracks.get(position).getExternalUrl());
            editor.commit();
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return customTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImageSong;
        TextView songName, artistName;
        View click;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.coverImageSong = itemView.findViewById(R.id.imvSong);
            this.songName = itemView.findViewById(R.id.tvSongName);
            this.artistName = itemView.findViewById(R.id.tvArtistName);
            this.click = itemView.findViewById(R.id.layoutId);
        }
    }
}
