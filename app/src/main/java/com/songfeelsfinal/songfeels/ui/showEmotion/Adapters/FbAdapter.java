package com.songfeelsfinal.songfeels.ui.showEmotion.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.songfeelsfinal.songfeels.R;

import java.util.List;

public class FbAdapter extends RecyclerView.Adapter<FbAdapter.FbViewHolder> {

    private List<Object> emotions;
    private List<Bitmap> bitmaps;
    private Context context;

    public FbAdapter(List<Bitmap> bitmaps, List<Object> emotions, Context context) {
        this.bitmaps = bitmaps;
        this.emotions = emotions;
        this.context = context;
    }

    @NonNull
    @Override
    public FbViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        viewGroup.getContext();
        return new FbViewHolder(LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.single_row_emotion, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FbViewHolder holder, int position) {

        Bitmap bitmap = bitmaps.get(position);
        holder.bitmapsView.setImageBitmap(bitmap);
        Log.i("FbAdapter", bitmap.toString());

        String emotion = emotions.get(position).toString();
        holder.emotion.setText(emotion);
        Log.i("FbAdapter", emotion);

    }

    @Override
    public int getItemCount() {
        return emotions.size();
    }

    public class FbViewHolder extends RecyclerView.ViewHolder{

        ImageView bitmapsView;
        TextView emotion;

        public FbViewHolder(@NonNull View singleRowEmotion) {
            super(singleRowEmotion);

            bitmapsView = singleRowEmotion.findViewById(R.id.bitmapsView);
            emotion = singleRowEmotion.findViewById(R.id.listEmotion);

        }
    }
}
