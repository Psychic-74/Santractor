package com.psychic.santractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Nick on 12/16/2016.
 */

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar mBar = getSupportActionBar();
        mBar.setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.settings_screen);

        Preference appVer = findPreference("pref_version");
        appVer.setSummary(BuildConfig.VERSION_NAME);

        Preference sourceCode = findPreference("pref_source");
        sourceCode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                boolean showCustomTabs = mPref.getBoolean("custom_tabs", true);

                if (showCustomTabs){

                    CustomTabsIntent sourceIntent = new CustomTabsIntent.Builder()
                            .build();
                    sourceIntent.launchUrl(SettingsActivity.this, Uri.parse("https://github.com/Psychic-74/Santractor"));
                }

                else {
                    Intent source = new Intent(Intent.ACTION_VIEW);
                    source.setData(Uri.parse("https://github.com/Psychic-74/Santractor"));
                    source.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(source);
                }

                return false;

            }
        });
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
                mPref.edit().putString("open_fragment", "Welcome Fragment").apply();
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
