package ru.krus.bashNews;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class PrefActivity extends AppCompatActivity{

    SharedPreferences sp;
    String listThemelast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Настройки");
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
            ListPreference themeMode = (ListPreference) findPreference("listTheme");
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                themeMode.setEntries(new String[]{"Светлая","Темная"});
                themeMode.setEntryValues(new String[]{"1", "2"});
            }
            Preference preferenceTheme = getPreferenceScreen().getPreference(2);
            preferenceTheme.setSummary("Светлая");
            Preference preference = getPreferenceScreen().getPreference(1);
            preference.setSummary("Новости | Все");
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            Preference preference = getPreferenceScreen().getPreference(1);
            ListPreference dataPref = (ListPreference) findPreference("list");
            if(dataPref.getValue() == null){
                dataPref.setValueIndex(1); //set to index of your deafult value
            }
            updatePreference(preference, "list");
            Preference preferenceTheme = getPreferenceScreen().getPreference(2);
            ListPreference dataPref1 = (ListPreference) findPreference("listTheme");
            if(dataPref1.getValue() == null){
                dataPref1.setValueIndex(3); //set to index of your deafult value
            }
            updatePreference(preferenceTheme, "listTheme");
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key), key);
            if (key.equals("listTheme")) {
                ListPreference themes = (ListPreference) findPreference(key);
                CharSequence value = themes.getEntry();
                if (value.equals("")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                } else if (value.equals("Светлая")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if (value.equals("Темная")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if (value.equals("Системная")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    }
                }
            }

        }

        public void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            if (preference == findPreference("fabHide")) return;
            if (preference == findPreference("bigText")) return;
            if (preference instanceof ListPreference) {
                if (preference != findPreference("listCategory")) {
                    ListPreference listPreference = (ListPreference) preference;
                    listPreference.setSummary(listPreference.getEntry());
                }
                return;
            }
            SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
            if (preference != findPreference("listCategory")) {
                preference.setSummary(sharedPrefs.getString(key, "Default"));
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        String listTheme = sp.getString("listTheme", "1");
            if (listTheme.equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            } else if (listTheme.equals("1")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    View view = getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else if (listTheme.equals("2")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    View view = getWindow().getDecorView();
                    view.setSystemUiVisibility(view.getSystemUiVisibility() & ~(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
                }
            } else if (listTheme.equals("3")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
            }
        super.onResume();
    }
}
