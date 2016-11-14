package com.github.windsekirun.itinerary_builder.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.github.windsekirun.itinerary_builder.R;

/**
 * InfoSettingFragment
 * Created by Pyxis on 2016. 11. 14..
 */
public class InfoSettingFragment extends PreferenceFragment {
    Preference openSource;
    Preference madeBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_info);

        openSource = findPreference("open_source");
        madeBy = findPreference("made_by");

        openSource.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https://gist.github.com/WindSekirun/7edda64a5cbc0dc2381ac5fb218786e9";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });

        madeBy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String url = "https:/github.com/windsekirun";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            }
        });
    }
}
