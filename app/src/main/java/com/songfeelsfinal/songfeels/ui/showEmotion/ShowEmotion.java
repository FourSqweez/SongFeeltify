package com.songfeelsfinal.songfeels.ui.showEmotion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.common.collect.Sets;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.showEmotion.Adapters.FbAdapter;
import com.songfeelsfinal.songfeels.ui.spotify.SpotifyActivity;
import com.songfeelsfinal.songfeels.utils.ColorUtilsHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ShowEmotion extends AppCompatActivity implements CustomDialog.CustomDialogListener, CustomDialog.CustomGenreDialogListener, CustomDialog.CustomSavePlaylistDialogListener {

    private static final String TAG = "EmotionAndGenre";
    SharedPreferences sp;
    private ImageView img;
    private EditText etNumberAlertDialog;
    private TextView showMusicGenre;
    private TextView showColor;
    private Button btnGoToList;
    private String musicGenreFromColor = "";
    private String musicGenreFromEmotion = "";
    private int faceCount = 0;
    private Set<String> musicGenreFromEmotionAndColorUnion = null;
    private Set<String> musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav = null;
    private String finalResultOfGenre = "";
    private String resultOfMusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav = "";
    private String genreUserFav = "";

    private String emotionToGenre = "";

    Toolbar mToolbar;


    //ForAlgorithm
    private Set<String> musicGenreFromUserFavSet = null;
    private Set<String> musicGenreFromColorSet = null;
    private Set<String> musicGenreFromEmotionSet = null;

    //Color Detection Section
    private Feature feature;
    private String[] visionAPI = new String[]{"IMAGE_PROPERTIES"};
    private static final String CLOUD_VISION_API_KEY = "AIzaSyCbfyHaWmxlIpYMV6aTnTlf5m9N27PG1s8";
    private String api = visionAPI[0];

    String colorHelper = "";
    int colorDetection = -1;

    private StorageReference objectStorageReference;
    private FirebaseFirestore objectFirebaseFirestore;

    AlertDialog alertDialog;
    StringBuilder resultPredict;
    StringBuilder resultPredictJustEmotions;

    ArrayList<Bitmap> facesBitmap;

    RecyclerView objectRecyclerView;
    FbAdapter objectFbAdapter;

    ArrayList<String> tasks;
    ArrayList<Object> emotions;
    ArrayList<String> justEmotions;

    Map<String, Object> objectMapBitmap;
    Map<String,Object> emotionMap;

    private String getDataCustomTimeOrListTime = "10";
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_emotion);

        mToolbar = findViewById(R.id.tbShowEmotion);
        mToolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
        });


        alertDialog =  new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Processing...")
                .setCancelable(true)
                .build();
        alertDialog.show();

        objectFirebaseFirestore = FirebaseFirestore.getInstance();
        objectStorageReference =  FirebaseStorage.getInstance().getReference("imageFolder");
        facesBitmap = new ArrayList<>();
        resultPredict = new StringBuilder();
        resultPredictJustEmotions = new StringBuilder();


        emotionMap = new HashMap<>();
        emotions = new ArrayList<>();
        justEmotions = new ArrayList<>();

        objectMapBitmap = new HashMap<>();
        tasks = new ArrayList<>();

        configureHostedModelSource();

        img = findViewById(R.id.imgPhoto);
        etNumberAlertDialog = findViewById(R.id.etNumberAlertDialog);
        showColor = findViewById(R.id.tvShowColor);
        showMusicGenre = findViewById(R.id.tvShowMusicGenre);
        btnGoToList = findViewById(R.id.btnGoToList);
        objectRecyclerView = findViewById(R.id.RV);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Intent intent = getIntent();
//        Bitmap bitmap = intent.getParcelableExtra("photo");
        Uri uriImagePath = intent.getParcelableExtra("uri");

        Uri uriRealImagePath = Uri.fromFile(new File(getRealPathFromURI(getApplicationContext(),uriImagePath)));
        uploadImageToFirebaseStorage(uriRealImagePath);

        Log.e("Uri","Uri : " + uriRealImagePath);
        Log.e("Uri","Uri : " + Uri.fromFile(new File(getRealPathFromURI(getApplicationContext(),uriImagePath))));

        Bitmap bitmap;
        try {

            bitmap = MediaStore.Images.Media.getBitmap(ShowEmotion.this.getContentResolver(), Uri.parse(uriImagePath.toString()));
            img.setImageBitmap(bitmap);


            processFaceDetection(bitmap,uriImagePath);
            processImage(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        addDataToListView();
        requestPermissionsToAccessStorage();


    }


    private void requestPermissionsToAccessStorage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "You have already granted this permission!",Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ShowEmotion.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }
    }

    private void addDataToListView() {

        //do action to edittext
        etNumberAlertDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.show();
                openDialog();
            }
        });

    }

    private void openDialog() {
        CustomDialog customDialog = new CustomDialog("Emotion");
        customDialog.show(getSupportFragmentManager(),"Show Emotion");

    }

    @Override
    public void applyTextsCustomTime(String customTime) {
        etNumberAlertDialog.setText(customTime);
        getDataCustomTimeOrListTime = customTime;
        Log.i("getDataCustomTime", getDataCustomTimeOrListTime);
    }

    @Override
    public void applyTextsListTime(String listTime) {
        etNumberAlertDialog.setText(listTime);
        getDataCustomTimeOrListTime = listTime;
        Log.i("getDataListTime", getDataCustomTimeOrListTime);

    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("getRealPathFromURI", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //Go back
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentforBackButton = NavUtils.getParentActivityIntent(this);
            intentforBackButton.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, intentforBackButton);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Start Color Detection Section
    private void DetectColor(Bitmap bitmap, CallbackMusicGenre musicGenreListener) {
        //Start Color Detection Section
        feature = new Feature();
        feature.setType(visionAPI[0]);
        feature.setMaxResults(1);
        feature.setType(api);
        if (bitmap != null)
            callCloudVision(bitmap, feature, musicGenreListener);

    }

    private void callCloudVision(final Bitmap bitmap, final Feature feature,
                                 final CallbackMusicGenre musicGenreListener) {
        //imageUploadProgress.setVisibility( View.VISIBLE);
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d("ColorDetection", "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d("ColorDetection", "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                Log.i("ResultData", result);
                musicGenreListener.onSuccess(result);
                //imageUploadProgress.setVisibility( View.INVISIBLE);
            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        switch (api) {

            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                getImageProperty(imageProperties);
                break;

        }
        return colorHelper;
    }

    private void getImageProperty(ImageProperties imageProperties) {
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        ColorInfo color = colors.getColors().get(0);

        //Get dominant color from cloud vision api
        int dominantColor = Color.rgb(Math.round(color.getColor().getRed()), Math.round(color.getColor().getGreen()), Math.round(color.getColor().getBlue()));

        Log.i("ColorDominant", " : " + dominantColor);
        colorDetection = dominantColor;

        //Find closest color and map with music genre.
        ColorUtilsHelper colorUtilsHelper = new ColorUtilsHelper();
        String cHelper = colorUtilsHelper.getColorNameFromRgb(Math.round(color.getColor().getRed()), Math.round(color.getColor().getGreen()), Math.round(color.getColor().getBlue()));
        Log.i("Color", " : " + cHelper);
        colorHelper = cHelper;
    }

    Handler handler = new Handler();


    private void processImage(final Bitmap bitmap) {
        if (bitmap != null) {

            DetectColor(bitmap, new CallbackMusicGenre() {
                @Override
                public void onSuccess(final String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.i("Global", " = " + colorDetection);
                            String colorDetectionToString = Integer.toString(colorDetection);

                            Log.i("resultPredict", "Emotion : " + resultPredict);

                            Log.i("Detection", "color " + colorDetectionToString);
                            showColor.setBackgroundColor(Integer.valueOf(colorDetectionToString));
                            musicGenreFromColor = response;

                            MusicGenreFromColor();

                            handler.postDelayed(() -> RandomEmotion(), 600);
                            handler.postDelayed(() -> MappingGenreFromEmotion(), 700);


                        }


                    });
                }

                @Override
                public void onError(Exception ex) {

                }
            });
        }
    }


    private void RandomEmotion(){
        if (justEmotions.size() > 1) {
            ArrayList<String> song = new ArrayList<>();
            for (int i = 0; i < justEmotions.size(); i++) {
                song.add(justEmotions.get(i));
            }
            int size = song.size();
            ArrayList<String> randomSongModel = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int randomNumber = new Random().nextInt(size); //random function
                String document = song.get(randomNumber);
                if (!randomSongModel.contains(document)) {
                    randomSongModel.add(document);
                    if (randomSongModel.size() == 1)
                        break;
                }
            }

            emotionToGenre = randomSongModel.toString().replace("[", "")
                    .replace("]", "").replace(" ", "");

            Log.i(TAG, "Random Emotion = " + emotionToGenre);

        } else {
            emotionToGenre = justEmotions.toString().replace("[", "")
                    .replace("]", "").replace(" ", "");
            Log.i(TAG, "Emotion = " + emotionToGenre);

        }
    }


    private void MappingGenreFromEmotion() {
        if (faceCount == 1) {
                Log.i(TAG, "found " + faceCount + " face");
                EmotionAndGenre();
                MatchingColorAndEmotionAndUserFavoriteGenreOneFace();
            } else if (faceCount > 1) {
                Log.i(TAG, "found " + faceCount + " face");
                EmotionAndGenre();
                MatchingColorAndEmotionAndUserFavoriteGenreOneFace();
            } else {
                Log.i(TAG, "Face Not found");
                MatchingColorAndEmotionAndUserFavoriteGenreOneFace();
            }

    }

    private void EmotionAndGenre() {
        Log.i(TAG, "You are : " + emotionToGenre);
        if (emotionToGenre != null) {
            switch (emotionToGenre) {
                case "sad":
                    musicGenreFromEmotion = "blues,alternative,indie,classical";
                    break;
                case "angry":
                    musicGenreFromEmotion = "metal,rock,electronic,dance";
                    break;
                case "happy":
                case "surprise":
                    musicGenreFromEmotion = "pop,electronic,dance,alternative,wold,country,reggae,r-n-b,jazz";
                    break;
                case "neutral":
                    musicGenreFromEmotion = "";
                    break;
                case "annoy":
                    musicGenreFromEmotion = "metal,rock,electronic,dance,alternative,indie,new-age";
                    break;
                case "sleepy":
                    musicGenreFromEmotion = "folk,wold,new-age,blues,alternative,indie,classical,jazz";
                    break;
            }
        }

    }

    private void MusicGenreFromColor() {
        //convert musicGenreFromColor string to musicGenreFromColorSet
        String[] musicGenreFromColorArr = musicGenreFromColor.split(",");
        musicGenreFromColorSet = new HashSet<>();
        for (int i = 0; i < musicGenreFromColorArr.length; i++) {
            musicGenreFromColorSet.add(musicGenreFromColorArr[i]);

        }
        Log.i(TAG, "Music Genre From Color : " + musicGenreFromColorSet.toString());

    }

    private void MusicGenreFromEmotion() {
        //convert musicGenreFromEmotion string to musicGenreFromEmotionSet
        String[] musicGenreFromEmotionArr = new String[0];
        musicGenreFromEmotionSet = new HashSet<>();

        if (!musicGenreFromEmotion.isEmpty()) {
            musicGenreFromEmotionArr = musicGenreFromEmotion.split(",");
        } else {
            Log.i(TAG, "Haven't Emotion!! ");
        }

        for (int i = 0; i < musicGenreFromEmotionArr.length; i++) {
            musicGenreFromEmotionSet.add(musicGenreFromEmotionArr[i]);

        }
        Log.i(TAG, "Music Genre From Emotion : " + musicGenreFromEmotionSet.toString());
    }

    private void MusicGenreFromUserFav() {
        //Music Genre From User Favorite.
        sp = getSharedPreferences("MyChoice", Context.MODE_PRIVATE);
        genreUserFav = sp.getString("MyChoice", "");
        String[] genreUserFavArr = genreUserFav.split(",");
        musicGenreFromUserFavSet = new HashSet<>();

        for (int i = 0; i < genreUserFavArr.length; i++) {
            musicGenreFromUserFavSet.add(genreUserFavArr[i]);
        }

        Log.i(TAG, "Music Genre From User Favorite : " + musicGenreFromUserFavSet.toString());

        if (genreUserFav.isEmpty()) {
            Log.i(TAG, "Haven't User Favorite Genre");
                musicGenreFromUserFavSet = musicGenreFromEmotionAndColorUnion;
        }
    }

    private void MusicGenreFromEmotionAndColorUnion() {
        musicGenreFromEmotionAndColorUnion = Sets.union(musicGenreFromEmotionSet, musicGenreFromColorSet);
        Log.i(TAG, "Music Genre From Color Union Emotion : " + musicGenreFromEmotionAndColorUnion.toString());
    }

    private void MusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav() {
        if (!genreUserFav.isEmpty()) {
            musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav = Sets.intersection(musicGenreFromEmotionAndColorUnion, musicGenreFromUserFavSet);
            Log.i(TAG, "Music Genre Intersection : " + musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav);

            if (musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav.isEmpty()) {
                Log.i(TAG, "Haven't Music Genre Intersection : " + musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav);
                musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav = Sets.union(musicGenreFromUserFavSet, musicGenreFromEmotionAndColorUnion);
                Log.i(TAG, "Union All" + musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav.toString());
            }

        }

        resultOfMusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav = musicGenreFromEmotionAndColorUnionIntersectionWhitUserFav.toString();
        finalResultOfGenre = resultOfMusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav.substring(1, resultOfMusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav.length() - 1);
        String[] finalResultOfGenreArr = new String[0];
        finalResultOfGenreArr = finalResultOfGenre.split(",");
        if (finalResultOfGenreArr.length > 5) {
            ArrayList<String> song = new ArrayList<>();
            for (int i = 0; i < finalResultOfGenreArr.length; i++) {
                song.add(finalResultOfGenreArr[i]);
            }
            int size = song.size();
            ArrayList<String> randomSongModel = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int randomNumber = new Random().nextInt(size); //random function
                String document = song.get(randomNumber);
                if (!randomSongModel.contains(document)) {
                    randomSongModel.add(document);
                    if (randomSongModel.size() == 5) break;
                }
            }
            finalResultOfGenre = randomSongModel.toString().replace("[", "")
                    .replace("]", "").replace(" ", "");
            Log.i(TAG, "Random Number " + (randomSongModel.size()));
            Log.i(TAG, "All Genre " + randomSongModel.toString());
        }
        Log.i(TAG, "Result of Music Genre : " + finalResultOfGenre);

    }


    private void MatchingColorAndEmotionAndUserFavoriteGenreOneFace() {
        MusicGenreFromEmotion();
        MusicGenreFromUserFav();
        MusicGenreFromEmotionAndColorUnion();

        MusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav();


        alertDialog.dismiss();

        showMusicGenre.setText(finalResultOfGenre);
        btnGoToList = findViewById(R.id.btnGoToList);
        btnGoToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShowEmotion.this, SpotifyActivity.class);
                intent.putExtra("genre", finalResultOfGenre);
                intent.putExtra("amount", getDataCustomTimeOrListTime);
                intent.putExtra("playlistIdSpotify", getDataCustomTimeOrListTime);
                startActivity(intent);
            }
        });

    }

    private void MatchingColorAndEmotionAndUserFavoriteGenreMultiFace() {

        MusicGenreFromEmotion();
        MusicGenreFromUserFav();
        MusicGenreFromEmotionAndColorUnion();

        MusicGenreFromEmotionAndColorUnionIntersectionWhitUserFav();


        alertDialog.dismiss();

        showMusicGenre.setText(finalResultOfGenre);
        btnGoToList = findViewById(R.id.btnGoToList);
        btnGoToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShowEmotion.this, SpotifyActivity.class);
                intent.putExtra("genre", finalResultOfGenre);
                intent.putExtra("amount", getDataCustomTimeOrListTime);
                intent.putExtra("playlistIdSpotify", getDataCustomTimeOrListTime);
                startActivity(intent);
            }
        });

    }

    @Override
    public void applyTextsListGenre(String listGenre) {

    }

    @Override
    public void applyTextSavePlaylist(String savePlaylistName) {

    }

    @Override
    public void applyTextSavePlaylistUserPlaylistID(String savePlaylistUserPlaylistID, String playlistIdSpotify) {

    }


    interface CallbackMusicGenre {
        void onSuccess(String response);

        void onError(Exception ex);
    }


    private void uploadImageToFirebaseStorage(Uri imageLocationPath){
        final String usersName =  UUID.randomUUID().toString();
        try{
            if(imageLocationPath != null){
                String nameOfImage =  UUID.randomUUID() + "." + getMimeType(this,imageLocationPath);
                final StorageReference imageRef = objectStorageReference.child(nameOfImage);

                UploadTask objectUploadTask = imageRef.putFile(imageLocationPath);
                objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Map<String,String> objectMap = new HashMap<>();
                            objectMap.put("uri", task.getResult().toString());
                            objectFirebaseFirestore.collection("usersImageLinks").document(usersName)
                                    .set(objectMap,SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ShowEmotion.this, "Image is uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ShowEmotion.this, "Fails to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if (!task.isSuccessful()){
                            Toast.makeText(ShowEmotion.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this,"Please provide name for image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            Toast.makeText(this,"Error : " +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    private void DetectFaces(Bitmap bitmap, Uri uriImage, CallbackImageLinks callbackImageLinks, CallbackEmotion callbackEmotion) {
        processFaceDetectionFireStore(bitmap, uriImage, callbackImageLinks, callbackEmotion);
    }

    private void processFaceDetectionFireStore(Bitmap bitmap,Uri uriImage, CallbackImageLinks callbackImageLinks, CallbackEmotion callbackEmotion) {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.5f)
                .enableTracking()
                .build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        getFaceResults(firebaseVisionFaces, bitmap, callbackImageLinks, callbackEmotion, uriImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowEmotion.this, "Error : " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }



    //Get faces and draw images and draw rectangle shape
    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces, Bitmap bitmap, CallbackImageLinks callbackImageLinks, CallbackEmotion callbackEmotion, Uri uriImage) {
        int counter = 0;

        UploadTask objectUploadTask;
        StorageReference imageRef;
        for (FirebaseVisionFace faces : firebaseVisionFaces){
            Rect rect = faces.getBoundingBox();
            facesBitmap.add(cropBitmap(bitmap,rect));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cropBitmap(bitmap,rect).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            imageRef = objectStorageReference.child("facesDetection"+ cropBitmap(bitmap,rect) +"." + getMimeType(this,uriImage));
            Log.e("faces", "ArrayList face : " + cropBitmap(bitmap,rect));
            try {
                EmotionDetection(cropBitmap(bitmap,rect),callbackEmotion);
            } catch (FirebaseMLException e) {
                e.printStackTrace();
            }

            objectUploadTask = imageRef.putBytes(data);
            StorageReference finalImageRef = imageRef;
            objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return finalImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    tasks.add(task.getResult().toString());
                    Log.e("Mytask", "task" + tasks);
                    objectMapBitmap.put("bitmaps",tasks);
                    Log.e("MyMap","map"+ objectMapBitmap.toString());
                    callbackImageLinks.onSuccess(objectMapBitmap);

                } else if (!task.isSuccessful()){
                    Toast.makeText(ShowEmotion.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                }

            });



            counter++;
        }


        if(counter == 0){
            faceCount = counter;
            Toast.makeText(ShowEmotion.this, "Found Faces : " + counter,Toast.LENGTH_SHORT).show();
        } else if (counter == 1){
            faceCount = counter;
            Toast.makeText(ShowEmotion.this, "Found Face : " + counter,Toast.LENGTH_SHORT).show();
        } else {
            faceCount = counter;
            Toast.makeText(ShowEmotion.this, "Found Faces : " + counter,Toast.LENGTH_SHORT).show();
        }
//        img.setImageBitmap(combineImageIntoOne(facesBitmap));
        //alertDialog.dismiss();

    }

    private void processFaceDetection(Bitmap bitmap, Uri uriImage){

        DetectFaces(bitmap, uriImage , new CallbackImageLinks() {
            @Override
            public void onSuccess(Map<String, Object> response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("myresponse", "Data is added" + response.values());

                        try {

                            objectFirebaseFirestore.collection("FaceDetection").document("Noom")
                                    .set(response,SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(ShowEmotion.this, "Bitmaps data is added", Toast.LENGTH_SHORT).show();
                                            Log.e("bitmaps", "Bitmaps data is added");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ShowEmotion.this, "Fails to add bitmap", Toast.LENGTH_SHORT).show();
                                    Log.e("bitmaps", "Erorr : " + e.getMessage());
                                }
                            });


                        } catch (Exception e) {
                            Toast.makeText(ShowEmotion.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onError(Exception ex) {

            }
        }, new CallbackEmotion() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            objectFirebaseFirestore.collection("FaceDetection").document("Noom")
                                    .set(response, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(ShowEmotion.this, "Emotion is added", Toast.LENGTH_SHORT).show();
                                            Log.e("emotion", "Emotion is added");                                    }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ShowEmotion.this, "Fails to add bitmap", Toast.LENGTH_SHORT).show();
                                    Log.e("bitmaps", "Erorr : " + e.getMessage());                            }
                            });

                            addBitmapToRV();

                        } catch (Exception e){
                            Toast.makeText(ShowEmotion.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }


            @Override
            public void onError(Exception ex) {

            }
        });

    }

    interface CallbackImageLinks {
        void onSuccess(Map<String,Object> response);

        void onError(Exception ex);
    }

    interface CallbackEmotion {
        void onSuccess(Map<String,Object> response);

        void onError(Exception ex);
    }

    private void addBitmapToRV() {
        Log.i("addToRV", "Hello");

        try {

            objectFbAdapter = new FbAdapter(facesBitmap,emotions, getApplicationContext());

            objectRecyclerView.setHasFixedSize(true);

            objectRecyclerView.setAdapter(objectFbAdapter);

            objectRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        } catch (Exception e){
            Toast.makeText(ShowEmotion.this, "Error : " + e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
        return ret;
    }

    private void configureHostedModelSource() {
        // [START mlkit_cloud_model_source]
        FirebaseCustomRemoteModel remoteModel =
                new FirebaseCustomRemoteModel.Builder("face-emotion-model").build();
        // [END mlkit_cloud_model_source]
        startModelDownloadTask(remoteModel);

    }

    private void startModelDownloadTask(FirebaseCustomRemoteModel remoteModel) {
        // [START mlkit_model_download_task]
        FirebaseModelDownloadConditions.Builder conditionsBuilder =
                new FirebaseModelDownloadConditions.Builder().requireWifi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Enable advanced conditions on Android Nougat and newer.
            conditionsBuilder = conditionsBuilder
                    .requireCharging()
                    .requireDeviceIdle();
        } else {
            conditionsBuilder = conditionsBuilder
                    .requireCharging()
                    .requireDeviceIdle();
        }
        FirebaseModelDownloadConditions conditions = conditionsBuilder.build();

        FirebaseModelManager.getInstance().download(remoteModel, conditions)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Success.
                        Toast.makeText(ShowEmotion.this, "Model has downloaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowEmotion.this, "Model downloaded failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // [END mlkit_model_download_task]
    }

    private FirebaseModelInterpreter createInterpreter(FirebaseCustomLocalModel localModel) throws FirebaseMLException {
        // [START mlkit_create_interpreter]
        FirebaseModelInterpreter interpreter = null;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
        } catch (FirebaseMLException e) {
            // ...
        }
        // [END mlkit_create_interpreter]

        return interpreter;
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
        // [START mlkit_create_io_options]
        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 100, 100, 3})
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 7})
                        .build();
        // [END mlkit_create_io_options]

        return inputOutputOptions;
    }

    private void EmotionDetection(Bitmap bitmap , CallbackEmotion callbackEmotion) throws FirebaseMLException{
        Bitmap bitmapFace = bitmap;

        // [START mlkit_bitmap_input]
        bitmapFace = Bitmap.createScaledBitmap(bitmapFace, 400, 400, true);

        int batchNum = 0;
        float[][][][] input = new float[1][100][100][3];

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {

                int pixel = bitmapFace.getPixel(x, y);
                // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                // model. For example, some models might require values to be normalized
//                // to the range [0.0, 1.0] instead.
//                input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 128.0f;
//                input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 128.0f;
//
//                // values to be normalized to the range [0.0, 1.0]
//                input[batchNum][x][y][0] = pixel / 255.0f;
//                input[batchNum][x][y][1] = pixel / 255.0f;
//                input[batchNum][x][y][2] = pixel / 255.0f;
                input[batchNum][x][y][0] = Color.red(pixel) / 255.0f;
                input[batchNum][x][y][1] = Color.green(pixel) / 255.0f;
                input[batchNum][x][y][2] = Color.blue(pixel) / 255.0f;

//                input[batchNum][x][y][0] = (pixel - 127) / 128.0f;
//                input[batchNum][x][y][1] = (pixel - 127) / 128.0f;
//                input[batchNum][x][y][2] = (pixel - 127) / 128.0f;

                input[batchNum][x][y][0] = (pixel - 127) / 128.0f;
                input[batchNum][x][y][1] = (pixel - 127) / 128.0f;
                input[batchNum][x][y][2] = (pixel - 127) / 128.0f;

            }
        }
        FirebaseCustomRemoteModel remoteModel = new FirebaseCustomRemoteModel.Builder("face-emotion-model").build();
        FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("inceptionv3.tflite")
                .build();
        FirebaseModelManager.getInstance().isModelDownloaded(remoteModel)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isDownloaded) {
                        FirebaseModelInterpreterOptions options;
                        if (isDownloaded) {
                            options = new FirebaseModelInterpreterOptions.Builder(remoteModel).build();
                        } else {
                            options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
                        }
                        try {
                            FirebaseModelInterpreter firebaseInterpreter = FirebaseModelInterpreter.getInstance(options);
                            //
                            FirebaseModelInputOutputOptions inputOutputOptions = createInputOutputOptions();

                            // [START mlkit_run_inference]
                            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                                    .add(input)  // add() as many input arrays as your model requires
                                    .build();
                            firebaseInterpreter.run(inputs, inputOutputOptions)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<FirebaseModelOutputs>() {
                                                @Override
                                                public void onSuccess(FirebaseModelOutputs result) {
                                                    // [START_EXCLUDE]
                                                    // [START mlkit_read_result]
                                                    float[][] output = result.getOutput(0);
                                                    float[] probabilities = output[0];
                                                    // [END mlkit_read_result]
                                                    // [END_EXCLUDE]
                                                    Log.i("output", "predict : "+ Arrays.toString(output[0]) + " " + Arrays.toString(probabilities));

                                                    BufferedReader reader = null;
                                                    try {
                                                        reader = new BufferedReader(
                                                                new InputStreamReader(getAssets().open("retrained_labels.txt")));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    String emo = "";
                                                    String justEmo = "";
                                                    float highestAcc = 0;
                                                    for (int i = 0; i < probabilities.length; i++) {
                                                        String label = null;

                                                        try {
                                                            label = reader.readLine();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                        if(probabilities[i] > highestAcc){
                                                            highestAcc = probabilities[i];
                                                            emo = String.format("%s: %1.2f ", label, (highestAcc * 100.0));
                                                            justEmo = String.format("%s", label);
                                                        }
                                                    }
                                                    resultPredict.append(emo);
                                                    resultPredictJustEmotions.append(justEmo);
                                                    justEmotions.add(justEmo);


                                                    Log.i("MLKit", emo);

                                                    emotions.add(emo);

                                                    emotionMap.put("emotion",emotions);
                                                    callbackEmotion.onSuccess(emotionMap);
                                                    // [END mlkit_use_inference_result]
                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Task failed with an exception
                                                    // ...
                                                    Toast.makeText(ShowEmotion.this, "Model downloaded failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Log.e("MlKit", e.getMessage());

                                                }
                                            });

                        } catch (FirebaseMLException e) {

                        }
                    }
                });


        // [END mlkit_run_inference]
    }


}

