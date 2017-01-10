package com.nick.santractor;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExtractorFragment extends Fragment {
    Activity mActivity = getActivity();
    String selectedValue;


    public ExtractorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach (Activity attachedActivity){
        super.onAttach(attachedActivity);
        mActivity = attachedActivity;
    }

    View mView;
    Utils mUtils = new Utils();
    final String appDomain = "com.saavn.android";

    String songExtension = null;
    String sdcardDir = Environment.getExternalStorageDirectory().toString();
    String saavnSongDir = sdcardDir+"/Android/data/"+appDomain+"/songs";
    String songName =null;
    String songsDir =null;
    File outputSong = null;
    MainActivity mc = new MainActivity();

    // File downloaded song should be accessible globally
    File songToSave = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_extractor, container, false);

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        selectedValue = mPref.getString("save_loc", "Music");
        songsDir = sdcardDir+"/"+selectedValue;

        // Change toolbar title
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        toolbar.setTitle("Extract Songs");


        // Check if the app is Installed else tell user to install the app
        if (mUtils.isAppInstalled(mActivity, appDomain)){
            // Take a note
            Log.i("santractor", "App is installed");

            // Now let's find the extension.
            File extensionMp3 = new File(saavnSongDir+"/curr.mp3");
            File extensionMp4 = new File(saavnSongDir+"/curr.mp4");
            if (extensionMp3.exists()){
                songExtension = "mp3";
            }
            else if(extensionMp4.exists()){
                songExtension = "mp4";
            }
            else{
                songExtension = "NON_EXISTENT";
            }

            // Append the extension and also create a file with included extension.
            File downloadedSong = new File(saavnSongDir+"/curr."+songExtension);

            // Cast downloadedSong into songToSave
            songToSave = downloadedSong;

            // Check if the downloaded song exists, else tell user to download.
            if (downloadedSong.exists()){
                // Take a note
                Log.i("santractor", "Downloaded song found");
                EditText songNameText = (EditText) mView.findViewById(R.id.songNameText);
                songNameText.setText(mc.song);
            }
            else{
                // Moron, go and download the song.
                Log.e("santractor", "Downloaded song not found");

                // Ask user if he wants to open Saavn app.
                // We will ask him to open app only if the app is installed.
                if (mUtils.isAppInstalled(mActivity, appDomain)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Open Saavn");
                    builder.setMessage("Saavn app is installed but, you haven't downloaded or played the song. Do you want to open Saavn now?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mUtils.openApp(mActivity, appDomain);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Log.e("santractor", "User refused to Download song.");
                        }
                    });
                    AlertDialog d = builder.create();
                    d.setCanceledOnTouchOutside(false);
                    d.show();
                }
            }


        }
        else{
            // Take a note
            Log.e("santractor", "App not installed.");
            // Ask to open playstore
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Install App");
            builder.setMessage("Official Saavn App not found. Install from Google Play Store?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Create an Intent and open Store
                    Intent storeIntent = new Intent(Intent.ACTION_VIEW);
                    storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    storeIntent.setData(Uri.parse("http://play.google.com/store/apps/details?id="+appDomain));
                    mActivity.startActivity(storeIntent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "This app won't work without official Saavn app.", Snackbar.LENGTH_SHORT).show();
                }
            });
            AlertDialog storeDialog = builder.create();
            storeDialog.setCanceledOnTouchOutside(false);
            storeDialog.show();
        }

        // Fix a issue where song name returned null if notification value was null.
        EditText songNameText = (EditText) mView.findViewById(R.id.songNameText);
        String song = mc.song;
        songNameText.setText(song);

        // Add action to Save Button
        Button saveButton = (Button) mView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Now we will get the value from text field for song name
                EditText songNameText = (EditText) mView.findViewById(R.id.songNameText);
                songName = songNameText.getText().toString();

                // If song name is null then tell user else create a new file for songame.
                // Trim to string to avoid filenames with just a space in their name.
                if (songName.trim().length() == 0){
                    // Take a note
                    Log.e("santractor", "Blank song name in the text field");
                    Snackbar.make(mActivity.findViewById(android.R.id.content), "Song name can\'t be blank", Snackbar.LENGTH_LONG).show();
                }
                else{
                    // Create a new file retaining extension in output directory.
                    outputSong = new File(songsDir+"/"+songName+"."+songExtension);

                    // Take a note
                    Log.i("santractor", "File name found as "+outputSong);

                    // If save directory is non Existent we will create it
                    File saveDir = new File(songsDir);
                    if (saveDir.exists()){
                        //Take a note
                        Log.i("santractor", "Destination directory exists as "+saveDir);
                    }
                    else{
                        Log.e("santractor", "Destination directory not found");
                        saveDir.mkdirs();
                        Log.i("santractor", "Destination directory created as "+saveDir);
                    }

                    // Save the song, we'll check return boolean to see if song was saved successfully.
                    Log.i("santractor", "Song to save is "+songToSave);
                    mUtils.saveSong(songToSave, outputSong);

                    // Check if saving succeeded
                    if (mUtils.isSongSaved){
                        // Take a note
                        Log.i("santractor", "Song has been saved successfully.");

                        // Tell user with a long toast messsage
                        Toast.makeText(mActivity, "Song saved as:-\n"+outputSong, Toast.LENGTH_LONG).show();

                        // Notify Media Storage of the new file.
                        mUtils.notifyMediaStorage(mActivity, outputSong);
                    }
                    else {
                        // Take a note
                        Log.e("santractor", "Unable to save song. Some error occoured.");

                        // Tell user with a long toast messsage.
                        Toast.makeText(mActivity, "Error while saving the song.", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        // Add action to reset Button
        Button resetButton = (Button) mView.findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExtractorFragment fragmentExtract = new ExtractorFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragmentExtract)
                        .commit();
                EditText et = (EditText) mView.findViewById(R.id.songNameText);
                et.setText(null);
            }
        });

        return mView;
    }
}
