package com.songfeelsfinal.songfeels.ui.showEmotion;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songfeelsfinal.songfeels.R;
import com.songfeelsfinal.songfeels.ui.spotify.models.CustomPlaylist;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CustomDialog extends AppCompatDialogFragment {

    private EditText customTime;
    private TextView customTitle;
    private EditText customSavePlaylistName;
    private ListView listTime;
    private ListView listGenre;
    private ListView listSavePlaylist;
    private CustomDialogListener listener;
    CustomGenreDialogListener listenerGenre;
    private CustomSavePlaylistDialogListener listenerSavePlaylistDialog;
    ArrayList<CustomPlaylist> customPlaylists;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> arrayPlaylistArrayAdapter;
    private SharedPreferences sharedPreferencesPlaylistTrack;
    SharedPreferences sharedPreferences;


    private List<String> data;
    private List<String> genre;
    private List<String> savePlaylistName;
    private String mPage;
    private Button getChoice, clearAll;
    private SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyChoice";
    public static final String MyPosition = "MyPosition";

    private ArrayList<String> selectedItems = new ArrayList<String>();


    public CustomDialog(String Page) {
        this.mPage = Page;


    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));


        if (mPage.equals("Emotion")) {
            // add data to array list
            data = new ArrayList<>();
            data.add("10");
            data.add("15");
            data.add("20");
            data.add("25");
            data.add("30");
            data.add("35");
            data.add("40");
            data.add("45");
            data.add("50");
            //Create array list adapter
            arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);


            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_custom_dialog, null);

            builder.setView(view)
                    .setTitle("Choose amount")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String getCustomTime = customTime.getText().toString();
                            listener.applyTextsCustomTime(getCustomTime);

                        }
                    });


            customTime = view.findViewById(R.id.customTime);
            customTitle = view.findViewById(R.id.textCustomTitle);
            customTitle.setText("Enter amount you want");
            listTime = view.findViewById(R.id.listId);

            listTime.setAdapter(arrayAdapter);


            listTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listener.applyTextsListTime(arrayAdapter.getItem(position));
                    getDialog().dismiss();
                }
            });

        } else if (mPage.equals("Genre")) {
            genre = new ArrayList<>();
            genre.add("classical");
            genre.add("metal");
            genre.add("rock");
            genre.add("pop");
            genre.add("jazz");
            genre.add("blues");
            genre.add("alternative");
            genre.add("electronic");
            genre.add("latin");
            genre.add("dance");
            genre.add("gospel");
            genre.add("soul");
            genre.add("new-age");
            genre.add("country");
            genre.add("folk");
            genre.add("reggae");
            genre.add("hip-hop");
            genre.add("indie");
            genre.add("r-n-b");
            genre.add("funk");
            genre.add("country");

            arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, genre);


            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_custom_dialog, null);

            builder.setView(view)
                    .setTitle("Choose Favorite Genre")
                    .setNegativeButton("cancel", (dialog, which) -> {

                    })
                    .setPositiveButton("OK", (dialog, which) -> {

                    })
                    .setNeutralButton("Clear all", (dialog, which) -> ClearSelections());


            listGenre = view.findViewById(R.id.listId);
            customTime = view.findViewById(R.id.customTime);
            customTime.setVisibility(View.INVISIBLE);
            customTitle = view.findViewById(R.id.textCustomTitle);
            customTitle.setVisibility(View.INVISIBLE);
            listGenre.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listGenre.setAdapter(arrayAdapter);

            sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if (sharedpreferences.contains(MyPREFERENCES)) {
                LoadSelections();
            }


            listGenre.setOnItemClickListener((parent, view1, position, id) -> {
                String selected = "";
                int cntChoice = listGenre.getCount();

                SparseBooleanArray sparseBooleanArray = listGenre.getCheckedItemPositions();
                for (int i = 0; i < cntChoice; i++) {
                    if (sparseBooleanArray.get(i)) {
                        selected += listGenre.getItemAtPosition(i).toString() + "\n";
                        System.out.println("Checking list while adding:" + listGenre.getItemAtPosition(i).toString());
                        SaveSelections();
                    }

                }

                Toast.makeText(getActivity(), selected, Toast.LENGTH_LONG).show();
            });

        } else if (mPage.equals("SavePlaylist")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            //Create array list adapter
            savePlaylistName = new ArrayList<>();

            sharedPreferencesPlaylistTrack = getActivity().getSharedPreferences("UserPlaylist", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferencesPlaylistTrack.getString("PlaylistTrack", null);
            Type type = new TypeToken<ArrayList<CustomPlaylist>>() {}.getType();
            customPlaylists = gson.fromJson(json, type);

            if (customPlaylists == null){
                customPlaylists = new ArrayList<>();
            }

            for (CustomPlaylist customPlaylist : customPlaylists){
                savePlaylistName.add(customPlaylist.getName());
                Log.i("Spotify", ""+savePlaylistName + " size : " + customPlaylists.size() );

            }
            arrayPlaylistArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, savePlaylistName);

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_custom_dialog, null);

            builder.setView(view)
                    .setTitle("Choose Your Playlist To Save")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        String getCustomSavePlaylistName = customSavePlaylistName.getText().toString();
                        if (!customSavePlaylistName.getText().toString().isEmpty()) {
                            listenerSavePlaylistDialog.applyTextSavePlaylist(getCustomSavePlaylistName);
                        } else {
                            listenerSavePlaylistDialog.applyTextSavePlaylist(formatter.format(date));
                        }
                    });


            customSavePlaylistName = view.findViewById(R.id.customTime);
            customSavePlaylistName.setInputType(InputType.TYPE_CLASS_TEXT);
            customSavePlaylistName.setHint(formatter.format(date));
            customTitle = view.findViewById(R.id.textCustomTitle);
            customTitle.setText("Create New Playlist");
            listSavePlaylist = view.findViewById(R.id.listId);

            listSavePlaylist.setAdapter(arrayPlaylistArrayAdapter);

            listSavePlaylist.setOnItemClickListener((parent, view12, position, id) -> {
                listenerSavePlaylistDialog.applyTextSavePlaylistUserPlaylistID(arrayPlaylistArrayAdapter.getItem(position), customPlaylists.get(position).getId());
                getDialog().dismiss();
            });

        }


        return builder.create();
    }

    private void LoadSelections() {
// if the selections were previously saved load them

        String currentItem = "";
        if (sharedpreferences.contains(MyPREFERENCES)) {

            String savedItems = sharedpreferences.getString(MyPREFERENCES, "");
            selectedItems.addAll(Arrays.asList(savedItems.split(",")));

            int count = listGenre.getAdapter().getCount();

            for (int i = 0; i < count; i++) {
                currentItem = (String) listGenre.getAdapter()
                        .getItem(i);
                if (selectedItems.contains(currentItem)) {
                    listGenre.setItemChecked(i, true);

                } else {
                    listGenre.setItemChecked(i, false);
                }

            }

            Toast.makeText(getActivity(),
                    "Curren Item: " + savedItems ,
                    Toast.LENGTH_LONG).show();

        }
    }

    private void ClearSelections() {
// user has clicked clear button so uncheck all the items
        int count = this.listGenre.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            this.listGenre.setItemChecked(i, false);
        }
// also clear the saved selections
        SaveSelections();
    }

    private void SaveSelections() {
// save the selections in the shared preference in private mode for the user

        SharedPreferences.Editor prefEditor = sharedpreferences.edit();
        String savedItems = getSavedItems();
        prefEditor.putString(MyPREFERENCES, savedItems);
        prefEditor.commit();

        listenerGenre.applyTextsListGenre(savedItems);

    }

    private String getSavedItems() {
        String savedItems = "";
        int count = listGenre.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            if (listGenre.isItemChecked(i)) {
                if (savedItems.length() > 0) {
                    savedItems += "," + listGenre.getItemAtPosition(i);
                } else {
                    savedItems += listGenre.getItemAtPosition(i);
                }
            }
        }
        return savedItems;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CustomDialogListener) context;
            listenerGenre = (CustomGenreDialogListener) context;
            listenerSavePlaylistDialog = (CustomSavePlaylistDialogListener) context;
        } catch (ClassCastException e){
            throw  new ClassCastException(context.toString() +
                    "must implement CustomDialogListener");
        }
    }


    public interface CustomDialogListener{
        void applyTextsCustomTime(String customTime);
        void applyTextsListTime(String listTime);
    }

    public interface CustomGenreDialogListener {
        void applyTextsListGenre(String listGenre);
    }

    public interface CustomSavePlaylistDialogListener {
        void applyTextSavePlaylist(String savePlaylistName);
        void applyTextSavePlaylistUserPlaylistID(String savePlaylistUserPlaylistID, String playlistIdSpotify);
    }

}
