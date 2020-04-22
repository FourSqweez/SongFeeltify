package com.songfeelsfinal.songfeels.ui.spotify;

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
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomPlaylist;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomTrack;
import com.songfeelsfinal.songfeels.ui.youtube.YouTubeListTrackAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPlayListTrackAdapter extends RecyclerView.Adapter<UserPlayListTrackAdapter.ViewHolder> {
    Context mContext;
    private ArrayList<CustomPlaylist> mPlaylistTrack;
    private OnUserPlaylistItemPositionClickListener mOnUserPlaylistItemPositionClickListener;


    public UserPlayListTrackAdapter(Context mContext, ArrayList<CustomPlaylist> mPlaylistTrack, OnUserPlaylistItemPositionClickListener mOnUserPlaylistItemPositionClickListener) {
            this.mContext = mContext;
            this.mPlaylistTrack = mPlaylistTrack;
            this.mOnUserPlaylistItemPositionClickListener = mOnUserPlaylistItemPositionClickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_user_playlist_adapter, parent, false);
            return new ViewHolder(v, mOnUserPlaylistItemPositionClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.playlistName.setText(String.valueOf(mPlaylistTrack.get(position).getName()));
            holder.totalSongInPlaylist.setText(String.valueOf(mPlaylistTrack.get(position).getTotalSong()));
            Picasso.get().load(mPlaylistTrack.get(position).getImageUrl()).into(holder.coverImageSong);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnUserPlaylistItemPositionClickListener.OnUserPlaylistItemPositionClick(mPlaylistTrack, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPlaylistTrack.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageView coverImageSong;
            TextView playlistName, totalSongInPlaylist;
            OnUserPlaylistItemPositionClickListener onUserPlaylistItemPositionClickListener;
            public ViewHolder(@NonNull View itemView, OnUserPlaylistItemPositionClickListener onUserPlaylistItemPositionClickListener) {
                super(itemView);

                this.coverImageSong = itemView.findViewById(R.id.imvSongPlaylist);
                this.playlistName = itemView.findViewById(R.id.tvPlaylistName);
                this.totalSongInPlaylist = itemView.findViewById(R.id.tvTotalSongInPlaylist);
                this.onUserPlaylistItemPositionClickListener = onUserPlaylistItemPositionClickListener;

            }
        }

    public interface OnUserPlaylistItemPositionClickListener {
        void OnUserPlaylistItemPositionClick( ArrayList<CustomPlaylist> mPlaylistTrack, int position);
    }

    }
