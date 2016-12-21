package com.nick.santractor;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Nick on 12/16/2016.
 */

public class SettingsActivity extends PreferenceActivity {
    // Declare AppCompatDelegate to set content view
    private AppCompatDelegate mDelegate;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.settings_screen);

    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();


    }

    // Inflate menu for the settings activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        switch (i){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.menu_reset:
                // Reset the preferences
                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                mPref.edit().putString("save_loc", "Music").apply();
                mPref.edit().putString("selected_theme", "Black").apply();
                mPref.edit().putBoolean("custom_tabs", true).apply();
                Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
}
