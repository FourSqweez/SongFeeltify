package com.songfeelsfinal.songfeels.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.models.YoutubeDataModel;
import com.songfeelsfinal.songfeels.ui.youtube.VideoPlayerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SelectSongAdapter extends RecyclerView.Adapter<SelectSongAdapter.SelectSongHolder> {

    private ArrayList<YoutubeDataModel> dataSet;
    private Context mContext;

    public SelectSongAdapter(Context context, ArrayList<YoutubeDataModel> youtubeDataModels) {
        this.mContext = context;
        this.dataSet = youtubeDataModels;

    }


    @NonNull
    @Override
    public SelectSongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_select_song_adapter,parent,false);
        final  SelectSongHolder selectSongAdapter = new SelectSongHolder(view);
        return selectSongAdapter;
    }



    public static class SelectSongHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDes;
        ImageView ImageThumb;
        View layout;
        Button menuButton;

        public SelectSongHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.textViewDes = itemView.findViewById(R.id.textViewDes);
            this.ImageThumb = itemView.findViewById(R.id.ImageThumb);
            this.layout = itemView.findViewById(R.id.layoutId);
            this.menuButton = itemView.findViewById(R.id.menuId);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull SelectSongAdapter.SelectSongHolder holder, int position) {
        //set the views here
        final TextView textViewTitle = holder.textViewTitle;
        TextView textViewDes = holder.textViewDes;
        ImageView ImageThumb = holder.ImageThumb;

        YoutubeDataModel object = dataSet.get(position);

        textViewTitle.setText(object.getTitle());
        textViewDes.setText(object.getDescription());

        //TODO : image will be download from url
        Picasso.get().load(object.getThumbnail()).into(ImageThumb);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String videoId = dataSet.get(position).getVideo_id();
                Log.d(TAG, "VideoID  : " + videoId);
                DetailActivityWithData(videoId);
            }

            private void DetailActivityWithData(String videoId) {
                Intent it = new Intent(mContext, VideoPlayerActivity.class);
                it.putExtra("videoId", videoId);
                mContext.startActivity(it);
            }

        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
