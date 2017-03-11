package com.psychic.santractor;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


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
        MaterialDialog.Builder changeLog = new MaterialDialog.Builder(mActivity);
        changeLog.title("Changelog for ver "+BuildConfig.VERSION_NAME);
        changeLog.content(getResources().getString(R.string.changelog));

        // Show changelog if app is updated
        int versionCheck = mPref.getInt("version_code", BuildConfig.VERSION_CODE);
        if (versionCheck != BuildConfig.VERSION_CODE){
            // Now we set canShowChangelog to true only if it is false
            if (!canShowChangelog){
                canShowChangelog = true;
            }
        }
        // Then we will tell Shared prefs that app is updated by writing the new version code
        mPref.edit().putInt("version_code", BuildConfig.VERSION_CODE).apply();

        changeLog.positiveText("Dismiss");
        changeLog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                mPref.edit().putBoolean("show_changelog", false).apply();
            }
        });
        changeLog.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPref.edit().putBoolean("show_changelog", false).apply();
            }
        });

        if (shouldShowChangelog) {
            if (canShowChangelog) {
                MaterialDialog change = changeLog.build();
                change.setCanceledOnTouchOutside(false);
                change.show();
                shouldShowChangelog = false;
            }
        }
        return mView;
    }

}
