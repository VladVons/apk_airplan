package ua.com.vando.apk_airplan;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;

public class ActivityPreferences extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            CharSequence summary = index >= 0 ? listPreference.getEntries()[index] : null;
            preference.setSummary(summary);
        } else {
            preference.setSummary(stringValue);
        }

        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's current value.
        onPreferenceChange(preference,
              PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            // im a static class, so make an instance to parent
            ActivityPreferences activityPreferences = new ActivityPreferences();
            activityPreferences.bindPreferenceSummaryToValue(findPreference("pref_server_address"));
            activityPreferences.bindPreferenceSummaryToValue(findPreference("pref_server_port"));
            activityPreferences.bindPreferenceSummaryToValue(findPreference("pref_motor_min"));
            activityPreferences.bindPreferenceSummaryToValue(findPreference("pref_motor_max"));
        }
    }
}
