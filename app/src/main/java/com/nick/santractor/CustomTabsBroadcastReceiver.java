package com.nick.santractor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomTabsBroadcastReceiver extends BroadcastReceiver {
    public CustomTabsBroadcastReceiver() {
        // Required empty public constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getDataString();

        if (url != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, url);

            Intent chooserIntent = Intent.createChooser(shareIntent, "Share url");
            chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(chooserIntent);
        }
    }
}
