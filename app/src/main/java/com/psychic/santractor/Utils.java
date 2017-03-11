package com.psychic.santractor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

/**
 * Created by Nick on 12/15/2016.
 */

public class Utils extends AppCompatActivity{

    // This file contains all the methods used in this program.

    // Method to check is an app is installed.
    public boolean isAppInstalled(Context context, String appName){
        PackageManager mgr = context.getPackageManager();
        try{
            mgr.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }

    // Method to open an installed App.
    public boolean openApp(Context context, String appName){
        PackageManager manager = context.getPackageManager();
        try{
            Intent i = manager.getLaunchIntentForPackage(appName);
            if (i == null){
                throw new PackageManager.NameNotFoundException();
            }
            else {
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                return true;
            }
        }
        catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    // Method to notify MediaStorage of a file
    public void notifyMediaStorage(Context context, File file){
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                // Okay. Taking a note in log
                Log.i("santractor", "Scanner Notified");
            }
        });
    }

    // Method to check if the permission is granted.
    public boolean isPermissionGranted(Context context){
        int a = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (a == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    // Method to ask for permission
    public void requestPermission(final Activity activity, final int requestCode){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
           // Tell user why that permission is needed.
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Allow the permission?");
            builder.setMessage("We need the Storage permission to save songs for you.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                }
            });
            AlertDialog d = builder.create();
            d.setCanceledOnTouchOutside(false);
            d.show();
        }
        else{
            // Request Permission on App Start
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        }
    }

    // Method to save the song
    boolean isSongSaved = false;
    public void saveSong(File songLocation, File songDestination){
        try{
            if (songLocation.renameTo(songDestination)){
                Log.i("santractor", "Song Saved");
                isSongSaved = true;
            }
        }
        catch (Exception e){
            Log.e("santractor", e.getMessage());
            isSongSaved = false;
        }
    }

    // Method to check if notification access service is enabled or not
    public static boolean checkNotificationAccess(Context context) {
        try{
            if(Settings.Secure.getString(context.getContentResolver(),
                    "enabled_notification_listeners").contains(context.getPackageName()))
            {
                return true;
            } else {
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
