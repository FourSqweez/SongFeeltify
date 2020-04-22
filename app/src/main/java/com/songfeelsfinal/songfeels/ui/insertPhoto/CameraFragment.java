package com.songfeelsfinal.songfeels.ui.insertPhoto;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.showEmotion.ShowEmotion;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment implements View.OnClickListener {
    public CameraFragment() {

    }

    private int STORAGE_PERMISSION_CODE = 1;
    private static final int RC_PHOTO_PICKER = 3;
    private Button btnDetectObject;
    private Button btnToggleCamera;
    private CameraView cameraView;
    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraView = view.findViewById(R.id.cameraView);
        btnToggleCamera = view.findViewById(R.id.btnToggleCamera);
        btnDetectObject = view.findViewById(R.id.btnDetectObject);

        requestPermissionsToAccessStorage();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        //Set title on Tool Bar
        mToolbar = view.findViewById(R.id.tbInsertPhoto);
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        mToolbar.setNavigationOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Title", null);
                processImage(bitmap, Uri.parse(path));

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
        btnToggleCamera.setOnClickListener(this);
        btnDetectObject.setOnClickListener(this);

        return view;

    }

    private void requestPermissionsToAccessStorage() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
        } else {
            requestStoragePermission();
        }
    }


    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    }).setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnToggleCamera:
                cameraView.toggleFacing();
                break;
            case R.id.btnDetectObject:
                cameraView.captureImage();
                break;
        }
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

    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    private void processImage(final Bitmap bitmap, final Uri uriImagePath) {
        if (bitmap != null) {
            Intent intent = new Intent(getActivity(), ShowEmotion.class);
//            intent.putExtra("photo", bitmap);
            intent.putExtra("uri", uriImagePath);

            startActivity(intent);
        }
    }

}
