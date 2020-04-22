package com.songfeelsfinal.songfeels.ui.spotify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.songfeelsfinal.songfeels.R;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.spotify.sdk.android.auth.AuthorizationResponse.Type.TOKEN;

public abstract class BaseActivity extends AppCompatActivity{
    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.songfeelsfinal.songfeels.ui.spotify://callback";
    public static final String CLIENT_ID = "d7e596c487e34607b95afd8a30531c9f";

    private SpotifyService mSpotifyService;
    private FirebaseFirestore objectFirebaseFirestore;
    private DocumentReference objectDocumentReference;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    CallbackBaseActivityFinished callbackBaseActivityFinished;
    boolean checkLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View rootView = findViewById(R.id.root_view);

        sp = getSharedPreferences("Pref_CheckLogin", Context.MODE_PRIVATE);
        checkLogIn = sp.getBoolean("Check", true);

        callbackBaseActivityFinished = (CallbackBaseActivityFinished) this;

        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        ConnectivityManager cm =
                (ConnectivityManager) BaseActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) //returns true if internet available
        {

            if (checkLogIn) {
                requestSpotifyApiToken();
            } else {
                Snackbar.make(rootView, "No login! Please login app again",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("Got it", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                editor = sp.edit();
                                editor.putBoolean("Check", true);
                                editor.apply();
                                requestSpotifyApiToken();
                            }
                        })
                        .setActionTextColor(Color.parseColor("#1BC24A"))
                        .show();
            }

        } else {
            Snackbar.make(rootView, "No internet connection!\nPlease check and try again.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", v -> finish())
                    .setActionTextColor(Color.parseColor("#1BC24A"))
                    .show();
        }

    }

    public void requestSpotifyApiToken() {

        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI);

        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        App app = ((App) getApplication());
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Toast.makeText(this, "Authorization Success " + response.getCode(),Toast.LENGTH_SHORT).show();
                    Log.i("Spotify Token", response.getAccessToken());
                    app.setSpotifyApiAccessToken(response.getAccessToken());
                    app.setIsAuthorized(true);
                    if(app.getIsAuthorized()){
                        mSpotifyService = app.getSpotifyService();
                        mSpotifyService.getMe(new Callback<UserPrivate>() {
                            @Override
                            public void success(UserPrivate userPrivate, Response response) {

                                //Save Data To Share Preference
                                sp = getSharedPreferences("PREF_PROFILE", Context.MODE_PRIVATE);
                                editor = sp.edit();
                                editor.putString("Id", userPrivate.id);
                                editor.putString("UserName", userPrivate.display_name);
                                editor.putString("Email", userPrivate.email);
                                editor.putString("ProfileImageUrl", userPrivate.images.get(0).url);
                                editor.commit();

                                objectDocumentReference = objectFirebaseFirestore.collection("Profile").document(userPrivate.id);
                                objectDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            Log.i("docuFire", documentSnapshot.getId() +" and " + userPrivate.id + " "+documentSnapshot.getString("SpotifyId"));
                                            if(documentSnapshot.exists()){
                                                // If email exists then go to MainActivity
                                                callbackBaseActivityFinished.OnBaseActivityLoadFinished("finished");
                                                Toast.makeText(BaseActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                                            } else {
                                                // If email not exists then add email to Firestore if added then go to MainActivity
                                                Map<String,Object> dataObjectMap = new HashMap<>();
                                                dataObjectMap.put("UserName", userPrivate.display_name);
                                                dataObjectMap.put("Email", userPrivate.email);
                                                dataObjectMap.put("ProfileImage", userPrivate.images);
                                                dataObjectMap.put("Timestamp", new Timestamp(new Date()));
                                                dataObjectMap.put("SpotifyId", userPrivate.id);
                                                dataObjectMap.put("UserPlaylist", "");
                                                dataObjectMap.put("UserDataImageDetection", "");
                                                dataObjectMap.put("FavoriteGenre", "");
                                                dataObjectMap.put("DurationListeningMusic", "");
                                                objectFirebaseFirestore.collection("Profile").document(userPrivate.id).set(dataObjectMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                callbackBaseActivityFinished.OnBaseActivityLoadFinished("finished");
                                                                Toast.makeText(BaseActivity.this, "Register Success", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            task.getException();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    }
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Toast.makeText(this, "Authorization error! Detail:" + response.getError(),Toast.LENGTH_SHORT).show();
                    Log.e("Spotify Error", response.getError());
                    app.setIsAuthorized(false);
                    //startActivity(new Intent(LoginSpotifyActivity.this, MainActivity.class));
                    requestSpotifyApiToken();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    app.setIsAuthorized(false);
                    requestSpotifyApiToken();

            }
        }
    }
}
