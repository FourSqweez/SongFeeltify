package com.songfeelsfinal.songfeels.ui.insertPhoto;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.showEmotion.ShowEmotion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class GalleryFragment extends Fragment implements View.OnClickListener{

    private static final int RC_PHOTO_PICKER = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        uploadImage();

        return view;
    }

    /* Select image from gallery*/
    private void uploadImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
        startActivityForResult(Intent.createChooser(photoPickerIntent, "Complete Action Using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            Bitmap bitmap = null;
            try {
                /* Convert Uri to Bitmap*/
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert bitmap != null;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
            processImage(bitmap, Uri.parse(path));

        }
    }

    private void processImage(final Bitmap bitmap, final Uri uriImagePath) {
        if (bitmap != null) {
            Intent intent = new Intent(getActivity(), ShowEmotion.class);
//            intent.putExtra("photo", bitmap);
            intent.putExtra("uri", uriImagePath);

            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
