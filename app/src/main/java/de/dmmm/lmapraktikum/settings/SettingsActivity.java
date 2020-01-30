package de.dmmm.lmapraktikum.settings;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import de.dmmm.lmapraktikum.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String
            KEY_PREF_LIST_RECEIVER = "receiver_switch",
            KEY_PREF_SWITCH_GPS = "switch_gps",
            KEY_PREF_SWITCH_NETWORK = "switch_network",
            KEY_PREF_SWITCH_ACCELERO = "switch_accelero",
            KEY_PREF_SWITCH_GYRO = "switch_gyro",
            KEY_PREF_SWITCH_COMPASS = "switch_compass",
            KEY_PREF_SEEKBAR_GPS = "seekbar_gps",
            KEY_PREF_SEEKBAR_GENERAL = "seekbar_general",
            KEY_PREF_SWITCH_FUESED = "switch_fuesed",
            KEY_PREF_LIST_ACCURACY = "accuracy_switch",
            KEY_PREF_SEEKBAR_FUESED = "seekbar_fuesed"
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
        setSupportActionBar(findViewById(R.id.my_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}