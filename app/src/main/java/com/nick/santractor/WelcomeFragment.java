package com.nick.santractor;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {
Activity mActivity = getActivity();
static boolean canShowChangelog;
static boolean shouldShowChangelog = true;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Init Shared Preferences
        final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mActivity);

        // Change toolbar title
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        toolbar.setTitle("Saavn Song Extractor");

        Button openDrawerButton = (Button) mView.findViewById(R.id.buttonOpenDrawer);
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Choose Extract to Save songs", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Show changelog
        canShowChangelog =  mPref.getBoolean("show_changelog", true);
        AlertDialog.Builder changeLog = new AlertDialog.Builder(mActivity);
        View mChangelog = View.inflate(mActivity, R.layout.frame_checkbox, null);
        final CheckBox cbDont = (CheckBox) mChangelog.findViewById(R.id.cbDont);
        changeLog.setView(mChangelog);
        changeLog.setTitle("Changelog for ver "+BuildConfig.VERSION_NAME);
        changeLog.setMessage(getResources().getString(R.string.changelog));

        // Show changelog if app is updated
        int versionCheck = mPref.getInt("version_code", BuildConfig.VERSION_CODE);
        if (versionCheck != BuildConfig.VERSION_CODE){
            // Now we set canShowChangelog to true only if it is false
            if (!canShowChangelog){
                canShowChangelog = true;
                // Set checkbox to true state in order to retain the canShowChangelog value
                CheckBox cb = (CheckBox) mChangelog.findViewById(R.id.cbDont);
                cb.setChecked(true);
            }
        }
        // Then we will tell Shared prefs that app is updated by writing the new version code
        mPref.edit().putInt("version_code", BuildConfig.VERSION_CODE).apply();

        changeLog.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean res;
                if (cbDont.isChecked()){
                    res = false;
                }
                else{
                    res = true;
                }
                mPref.edit().putBoolean("show_changelog", res).apply();
                dialogInterface.dismiss();
            }
        });
        if (shouldShowChangelog) {
            if (canShowChangelog) {
                AlertDialog change = changeLog.create();
                change.setCanceledOnTouchOutside(false);
                change.show();
                shouldShowChangelog = false;
            }
        }
        return mView;
    }

}
