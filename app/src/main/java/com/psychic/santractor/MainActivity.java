package com.psychic.santractor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Nick on 12/14/2016.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Declare Variables
    String appVersion = BuildConfig.VERSION_NAME;
    NavigationView navigationView;
    Toolbar toolbar;
    final int requestPermissionCode = 356;
    Utils utils = new Utils();
    boolean shouldShowCustomTabs;
    int colorPrimary;
    int colorPrimaryDark;
    static boolean canShowWelcomeSnackbar=true;
    static String song;

    // Override onRequestPermissionsResult to perform an action when user allows or disallows permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == requestPermissionCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.v("santractor", "User just allowed the permission");
            }
            else{
                // Go to hell. We can't run without permissions.
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Fatal Error");
                b.setMessage("Sorry! We can't perform any action without proper permissions");
                b.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                });
                AlertDialog d = b.create();
                d.setCanceledOnTouchOutside(false);
                d.show();
            }
        }
    }

    // Override theme
    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String selectedTheme = sPref.getString("selected_theme", "Black");
        switch (selectedTheme){
            case "Blue":
                theme.applyStyle(R.style.BlueTheme, true);
                break;
            case "Green":
                theme.applyStyle(R.style.GreenTheme, true);
                break;
            case "Teal":
                theme.applyStyle(R.style.TealTheme, true);
                break;
            case "Pink":
                theme.applyStyle(R.style.PinkTheme, true);
                break;
            case "Red":
                theme.applyStyle(R.style.RedTheme, true);
                break;
            // Black theme will be applied by default according to manifest.
            default:
                break;
        }
        return theme;
    }

    // Override onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start service in case someone killed it :P
        Intent intent=new Intent(getBaseContext(), NotificationListener.class);
        getBaseContext().startService(intent);

        // Check notification access
        if (utils.checkNotificationAccess(getBaseContext())){
            // Continue
            Log.i("santractor", "Notification Access is Enabled");
        }
        else {
            // Take a note
            Log.e("santractor", "Notification Access is Disabled");
            // Ask user to enable access
            // Create a intent for launching settings
            Intent mNotificationSet = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            PendingIntent mSettings = PendingIntent.getActivity(getBaseContext(), 65, mNotificationSet, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create a notification
            int mNotiID = 3478;
            NotificationCompat.Builder mNotification = new NotificationCompat.Builder(getBaseContext());
            mNotification.setSmallIcon(R.drawable.app_logo);
            mNotification.setAutoCancel(true);
            Uri nSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotification.setSound(nSound);
            mNotification.setOngoing(true);
            mNotification.setContentTitle("Enable notification access");
            mNotification.setContentText("Tap to enable notification listener.");
            mNotification.setContentIntent(mSettings);

            // Send notification
            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mgr.notify(mNotiID, mNotification.build());
        }

        // Register Broadcast receiver
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(onNotify, new IntentFilter("sendMsg"));

        // Get theme colors
        getColors();

        // We will be getting the value from settings preferences if user wants to use custom tabs.
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        shouldShowCustomTabs = mPref.getBoolean("custom_tabs", true);

        // If chrome is not installed, then set custom tabs to false
        if (!utils.isAppInstalled(getBaseContext(), "com.android.chrome")){
            shouldShowCustomTabs = false;
            mPref.edit().putBoolean("custom_tabs", false).apply();
        }

        // Disable custom tabs if enabled on Android versions prior to Nougat
        if (shouldShowCustomTabs){
            if (Build.VERSION.SDK_INT < 25){
                shouldShowCustomTabs = false;
                mPref.edit().putBoolean("custom_tabs", false).apply();
            }
        }

        // Request Permissions
        if (utils.isPermissionGranted(getBaseContext())){
            Log.v("santractor", "Permission acquired.");
        }
        else{
            utils.requestPermission(MainActivity.this, requestPermissionCode);
        }

        // Load fragment by reading preferences
        String fragmentToOpen = mPref.getString("open_fragment", "Welcome Fragment");
        switch (fragmentToOpen){
            case "Welcome Fragment":
                WelcomeFragment fragment = new WelcomeFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                break;
            case "Extractor Fragment":
                ExtractorFragment extractorFragment = new ExtractorFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, extractorFragment)
                        .commit();
                break;
            default:
                // Nothing because we are already returning "Welcome Fragment" as fallback value
                // That will fall back to "case: Welcome Fragment" and hence will load Welcome Fragment
                break;
        }


        // Set toolbar, it also sets the title of the app in the top header
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Saavn Song Extractor");
        setSupportActionBar(toolbar);

        // Add drawer toggle and also add a listener to it.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Add Navigation View listener
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set selected fragment in prefs as checked
        String navListSeletion = mPref.getString("open_fragment", "Welcome Fragment");
        switch (navListSeletion) {
            case "Welcome Fragment":
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case "Extractor Fragment":
                navigationView.setCheckedItem(R.id.nav_extract);
                break;
            default:
                break;
        }

        // To change Menu title color (Main)
        Menu menu = navigationView.getMenu();
        MenuItem main = menu.findItem(R.id.nav_title_main);
        SpannableString s = new SpannableString(main.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.customTextStyle), 0, s.length(), 0);
        main.setTitle(s);

        // To change Menu title color (Miscellaneous)
        Menu menu1 = navigationView.getMenu();
        MenuItem misc = menu1.findItem(R.id.nav_title_misc);
        SpannableString s1 = new SpannableString(misc.getTitle());
        s1.setSpan(new TextAppearanceSpan(this, R.style.customTextStyle), 0, s1.length(), 0);
        misc.setTitle(s1);

        // To change Menu title color (Communicate)
        Menu menu2 = navigationView.getMenu();
        MenuItem comm = menu2.findItem(R.id.nav_title_comm);
        SpannableString s2 = new SpannableString(comm.getTitle());
        s2.setSpan(new TextAppearanceSpan(this, R.style.customTextStyle), 0, s2.length(), 0);
        comm.setTitle(s2);

        // Show welcome snackbar only once
        if (canShowWelcomeSnackbar){
            Snackbar.make(findViewById(android.R.id.content), "Welcome to Saavn Extractor.", Snackbar.LENGTH_SHORT).show();
            canShowWelcomeSnackbar = false;
        }
    }


    // Override onNavigationItemSelected
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                WelcomeFragment fragment = new WelcomeFragment();
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
                break;
            case R.id.nav_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/*");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Placeholder Text here.");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(shareIntent,"Share this app using"));
                break;
            case R.id.nav_send:
                String smsApp = Telephony.Sms.getDefaultSmsPackage(this);
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Placeholder for now");
                if (smsApp != null){
                    sendIntent.setPackage(smsApp);
                }
                startActivity(Intent.createChooser(sendIntent, "Send using"));
                break;
            case R.id.nav_extract:
                ExtractorFragment fragmentExtract = new ExtractorFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragmentExtract)
                        .commit();
                break;
            case R.id.nav_update:
                Toast.makeText(getBaseContext(), "You have version "+appVersion+" installed.\n"+"Download new app if app version is greater.", Toast.LENGTH_LONG).show();
                if (shouldShowCustomTabs) {
                    Intent updateIntent = new Intent(getBaseContext(), CustomTabsBroadcastReceiver.class);
                    PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getBaseContext(), 32, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    CustomTabsIntent intent = new CustomTabsIntent.Builder()
                            .setToolbarColor(colorPrimary)
                            .setSecondaryToolbarColor(colorPrimaryDark)
                            .addMenuItem("Share link", pendingUpdateIntent)
                            .build();
                    intent.launchUrl(getBaseContext(), Uri.parse("https://j2java.net"));
                }
                else{
                    Intent updateIntent = new Intent(Intent.ACTION_VIEW);
                    updateIntent.setData(Uri.parse("https://j2java.net"));
                    updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(updateIntent);
                }
                break;
            case R.id.nav_source:
                if (shouldShowCustomTabs) {
                    Intent shareSourceIntent = new Intent(getBaseContext(), CustomTabsBroadcastReceiver.class);
                    PendingIntent pendingSourceIntent = PendingIntent.getBroadcast(getBaseContext(), 74, shareSourceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    CustomTabsIntent sourceIntent = new CustomTabsIntent.Builder()
                            .setToolbarColor(colorPrimary)
                            .setSecondaryToolbarColor(colorPrimaryDark)
                            .addMenuItem("Share source code", pendingSourceIntent)
                            .build();
                    sourceIntent.launchUrl(getBaseContext(), Uri.parse("https://github.com/Psychic-74/Santractor"));
                }
                else{
                    Intent shareSourceIntent = new Intent(Intent.ACTION_VIEW);
                    shareSourceIntent.setData(Uri.parse("https://github.com/Psychic-74/Santractor"));
                    shareSourceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(shareSourceIntent);
                }
                break;
            case R.id.nav_about:
                AboutFragment frag = new AboutFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
                break;
            case R.id.nav_changelog:
                if (shouldShowCustomTabs) {
                    Intent shareSourceIntent = new Intent(getBaseContext(), CustomTabsBroadcastReceiver.class);
                    PendingIntent pendingSourceIntent = PendingIntent.getBroadcast(getBaseContext(), 74, shareSourceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    CustomTabsIntent sourceIntent = new CustomTabsIntent.Builder()
                            .setToolbarColor(colorPrimary)
                            .setSecondaryToolbarColor(colorPrimaryDark)
                            .addMenuItem("Share Changelog", pendingSourceIntent)
                            .build();
                    sourceIntent.launchUrl(getBaseContext(), Uri.parse("https://github.com/Psychic-74/Santractor/commits/master"));
                }
                else{
                    Intent shareSourceIntent = new Intent(Intent.ACTION_VIEW);
                    shareSourceIntent.setData(Uri.parse("https://github.com/Psychic-74/Santractor/commits/master"));
                    shareSourceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(shareSourceIntent);
                }
                break;
            case R.id.nav_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Override onBackPressed
    Boolean canExit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            // Press back again to exit
            if (canExit){
                super.onBackPressed();
            }
            else{
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.press_back), Snackbar.LENGTH_SHORT).show();
                canExit = true;
            }
            mHandler.sendEmptyMessageDelayed(1,2000);
        }
    }

    // Add Handler ro reset back press counter after 2000 millis.
    public Handler mHandler = new Handler(){
      public void handleMessage(android.os.Message msg){
        switch (msg.what){
            case 1:
                canExit = false;
                break;
            default:
                break;
        }
    }
    };

    // Override onCreateOptionsMenu to inflate actionbar menu and show it in action
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    static int op = 0;
    // Override onOptionItemsSelected to perform an action when action bar menu item is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
            case R.id.action_notification:
                // Check if permission is already granted
                if (utils.checkNotificationAccess(getBaseContext())){
                    // Increment on every click.
                    op++;
                    Toast.makeText(getBaseContext(), "No need. Permission is allowed"+"\n\n"+"Tap again to open settings anyway", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent enableIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(enableIntent);
                }

                // Start permission activity if user taps twice
                if (op >= 2){
                    Intent enableIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(enableIntent);
                    // Set OP's value to 0
                    op = 0;
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Override onRestoreInstanceState to retain fragments on orientation change
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
    }

   public void showProfile(View v){
        if (shouldShowCustomTabs){
            Intent shareSourceIntent = new Intent(getBaseContext(), CustomTabsBroadcastReceiver.class);
            PendingIntent pendingProfileIntent = PendingIntent.getBroadcast(getBaseContext(), 74, shareSourceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            CustomTabsIntent profileIntent = new CustomTabsIntent.Builder()
                    .setToolbarColor(colorPrimary)
                    .setSecondaryToolbarColor(colorPrimaryDark)
                    .addMenuItem("Share Url", pendingProfileIntent)
                    .build();
            profileIntent.launchUrl(getBaseContext(),Uri.parse("https://github.com/Psychic-74"));
        }
        else{
            Intent profileIntent = new Intent(Intent.ACTION_VIEW);
            profileIntent.setData(Uri.parse("https://github.com/Psychic-74"));
            profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(profileIntent);
        }

   }

    public void getColors(){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        TypedValue typedValue1 = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue1, true);
        colorPrimaryDark = typedValue1.data;
    }

    private BroadcastReceiver onNotify = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            song = intent.getStringExtra("appTitle");
        }
    };

}
