package com.nick.santractor;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {
Activity mActivity = getActivity();

    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    View mView;
    static boolean canShowWelcomeSnackbar=true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Change toolbar title
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        toolbar.setTitle("Saavn Song Extractor");

        // Show welcome snackbar only once
        if (canShowWelcomeSnackbar){
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Welcome to Saavn Extractor.", Snackbar.LENGTH_SHORT).show();
            canShowWelcomeSnackbar = false;
        }
        Button openDrawerButton = (Button) mView.findViewById(R.id.buttonOpenDrawer);
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Choose Extract to Save songs", Toast.LENGTH_SHORT).show();
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        return mView;
    }

}
