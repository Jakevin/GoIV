package com.jakevin.pokeshout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jakevin.pokeshout.BuildConfig;
import com.jakevin.pokeshout.updater.AppUpdate;
import com.jakevin.pokeshout.updater.AppUpdateUtil;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        getSupportActionBar().setTitle(getResources().getString(com.jakevin.pokeshout.R.string.settings_page_title));
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog,
                new IntentFilter(MainActivity.ACTION_SHOW_UPDATE_DIALOG));
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        super.onDestroy();
    }

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert BuildConfig.isInternetAvailable;
            AppUpdate update = intent.getParcelableExtra("update");
            if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE) {
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(SettingsActivity.this, update);
                updateDialog.show();
            } else if (update.getStatus() == AppUpdate.UP_TO_DATE) {
                Toast.makeText(SettingsActivity.this, getResources().getString(com.jakevin.pokeshout.R.string.up_to_date), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(SettingsActivity.this, getResources().getString(com.jakevin.pokeshout.R.string.update_check_failed),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(GoIVSettings.PREFS_GO_IV_SETTINGS);
            addPreferencesFromResource(com.jakevin.pokeshout.R.xml.settings);

            if (BuildConfig.isInternetAvailable) {
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");
                checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (!MainActivity.isGoIVBeingUpdated(getActivity())) {
                            Toast.makeText(getActivity(), getResources().getString(com.jakevin.pokeshout.R.string.checking_for_update),
                                    Toast.LENGTH_SHORT).show();
                            MainActivity.shouldShowUpdateDialog = false;
                            AppUpdateUtil.checkForUpdate(getActivity());
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(com.jakevin.pokeshout.R.string.ongoing_update),
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            } else {
                PreferenceScreen preferenceScreen = getPreferenceScreen();
                //Hide update and crash report related settings
                Preference crashReportsPreference = getPreferenceManager().findPreference(
                        GoIVSettings.SEND_CRASH_REPORTS);
                Preference autoUpdatePreference = getPreferenceManager().findPreference(
                        GoIVSettings.AUTO_UPDATE_ENABLED);
                Preference checkForUpdatePreference = getPreferenceManager().findPreference("checkForUpdate");

                preferenceScreen.removePreference(crashReportsPreference);
                preferenceScreen.removePreference(autoUpdatePreference);
                preferenceScreen.removePreference(checkForUpdatePreference);
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
                SwitchPreference manualScreenshotModePreference = (SwitchPreference) getPreferenceManager()
                        .findPreference(GoIVSettings.MANUAL_SCREENSHOT_MODE);
                manualScreenshotModePreference.setDefaultValue(true);
                manualScreenshotModePreference.setChecked(true);
                manualScreenshotModePreference.setEnabled(false);
            }
        }
    }
}