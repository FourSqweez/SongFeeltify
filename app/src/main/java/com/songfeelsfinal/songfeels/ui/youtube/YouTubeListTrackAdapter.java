package com.songfeelsfinal.songfeels.ui.youtube;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class YouTubeListTrackAdapter extends RecyclerView.Adapter<YouTubeListTrackAdapter.ViewHolder> {
    Context mContext;
    ArrayList<CustomTrack> customTracks;
    int positionVideo = -1;
    private OnYouTubeVideoClickListener mOnYouTubeVideoClickListener;


    public YouTubeListTrackAdapter(Context mContext, ArrayList<CustomTrack> customTracks, int positionVideo, OnYouTubeVideoClickListener onYouTubeVideoClickListener) {
            this.mContext = mContext;
            this.customTracks = customTracks;
            this.positionVideo = positionVideo;
            this.mOnYouTubeVideoClickListener = onYouTubeVideoClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_list_track_adapter, parent, false);
            return new ViewHolder(v, mOnYouTubeVideoClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.songName.setText(customTracks.get(position).getTrackName());
            holder.artistName.setText(customTracks.get(position).getArtistsNamesList());
            Picasso.get().load(customTracks.get(position).getAlbumImageSmallUrl()).into(holder.coverImageSong);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positionVideo = position;
                    notifyItemChanged(position);
                    mOnYouTubeVideoClickListener.onYouTubeVideoClick(customTracks, position);
                }
            });

            if (position == positionVideo){
                holder.songName.setTextColor(Color.parseColor("#1BC24A"));
            } else {
                holder.songName.setTextColor(Color.WHITE);
                holder.artistName.setTextColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return customTracks.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView coverImageSong;
            TextView songName, artistName;
            OnYouTubeVideoClickListener onYouTubeVideoClickListener;
            public ViewHolder(@NonNull View itemView, OnYouTubeVideoClickListener onYouTubeVideoClickListener) {
                super(itemView);

                this.coverImageSong = itemView.findViewById(R.id.imvSong);
                this.songName = itemView.findViewById(R.id.tvSongName);
                this.artistName = itemView.findViewById(R.id.tvArtistName);
                this.onYouTubeVideoClickListener = onYouTubeVideoClickListener;
            }
        }

        public interface OnYouTubeVideoClickListener {
            void onYouTubeVideoClick(ArrayList<CustomTrack> customTracks, int position);
        }
    }
