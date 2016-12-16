package com.nick.santractor;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {
    View mView;
    Activity mActivity = getActivity();
    String appVersion = BuildConfig.VERSION_NAME;


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_about, container, false);

        // Change toolbar title
        Toolbar toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        toolbar.setTitle("About");

        // Set app version
        TextView verInfo = (TextView) mView.findViewById(R.id.appTitileView);
        verInfo.setText("Santractor v "+appVersion);

        return mView;
    }

}
