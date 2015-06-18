package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.util.Arrays;

public class PowerMenuFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String POWER_MENU_AIRPLANE = "power_menu_airplane";
    private static final String POWER_MENU_DATA = "power_menu_data";
    private static final String POWER_MENU_SCREENRECORD = "power_menu_screenrecord";
    private static final String POWER_MENU_SCREENSHOT = "power_menu_screenshot";
    private static final String POWER_MENU_VOLUME = "power_menu_volume";
    private static final String SCREEN_RECORDER_BITRATE = "screen_recorder_bitrate";
    private static final String SCREEN_RECORDER_OUTPUT_DIMENSIONS = "screen_recorder_output_dimensions";
    private static final String SCREEN_RECORDER_RECORD_AUDIO = "screen_recorder_record_audio";
    private CheckBoxPreference mPMAirplane;
    private CheckBoxPreference mPMData;
    private CheckBoxPreference mPMScreenRecord;
    private CheckBoxPreference mPMScreenShot;
    private CheckBoxPreference mPMVolume;
    private CheckBoxPreference mScreenAudio;
    private ListPreference mScreenBit;
    private ListPreference mScreenSize;

    private void setListSummary(ListPreference paramListPreference) {
        int i;
        int j;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mScreenBit)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), SCREEN_RECORDER_BITRATE, 4000000);
            strarray = getResources().getStringArray(R.array.screen_recorder_bitrate_values);
            j = Arrays.asList(strarray).indexOf(String.valueOf(i));
            strarray = getResources().getStringArray(R.array.screen_recorder_bitrate_entries);
            str = strarray[j];
            this.mScreenBit.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mScreenSize)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), SCREEN_RECORDER_OUTPUT_DIMENSIONS, 6);
            strarray = getResources().getStringArray(R.array.screen_recorder_video_sizes);
            str = strarray[i];
            this.mScreenSize.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    public PowerMenuFragment() {
    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.powermenu_fragment);

        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;

        /* Set up checkbox stuff first */
        this.mPMData = ((CheckBoxPreference) localPreferenceScreen.findPreference(POWER_MENU_DATA));
        if (Settings.Global.getInt(localContentResolver, POWER_MENU_DATA, 1) != 0) {
            mPMData.setChecked(true);
        } else {
            mPMData.setChecked(false);
        }

        this.mPMAirplane = ((CheckBoxPreference) localPreferenceScreen.findPreference(POWER_MENU_AIRPLANE));
        if (Settings.Global.getInt(localContentResolver, POWER_MENU_AIRPLANE, 1) != 0) {
            mPMAirplane.setChecked(true);
        } else {
            mPMAirplane.setChecked(false);
        }

        this.mPMScreenShot = ((CheckBoxPreference) localPreferenceScreen.findPreference(POWER_MENU_SCREENSHOT));
        if (Settings.Global.getInt(localContentResolver, POWER_MENU_SCREENSHOT, 1) != 0) {
            mPMScreenShot.setChecked(true);
        } else {
            mPMScreenShot.setChecked(false);
        }

        this.mPMVolume = ((CheckBoxPreference) localPreferenceScreen.findPreference(POWER_MENU_VOLUME));
        if (Settings.Global.getInt(localContentResolver, POWER_MENU_VOLUME, 1) != 0) {
            mPMVolume.setChecked(true);
        } else {
            mPMVolume.setChecked(false);
        }

        this.mPMScreenRecord = ((CheckBoxPreference) localPreferenceScreen.findPreference(POWER_MENU_SCREENRECORD));
        if (Settings.Global.getInt(localContentResolver, POWER_MENU_SCREENRECORD, 1) != 0) {
            mPMScreenRecord.setChecked(true);
        } else {
            mPMScreenRecord.setChecked(false);
        }

        this.mScreenAudio = ((CheckBoxPreference) localPreferenceScreen.findPreference(SCREEN_RECORDER_RECORD_AUDIO));
        if (Settings.Global.getInt(localContentResolver, SCREEN_RECORDER_RECORD_AUDIO, 1) != 0) {
            mScreenAudio.setChecked(true);
        } else {
            mScreenAudio.setChecked(false);
        }

        /* Here are for list preferences */
        this.mScreenSize = ((ListPreference) localPreferenceScreen.findPreference(SCREEN_RECORDER_OUTPUT_DIMENSIONS));
        i = Settings.Global.getInt(localContentResolver, SCREEN_RECORDER_OUTPUT_DIMENSIONS, 6);
        this.mScreenSize.setValue(String.valueOf(i));
        this.mScreenSize.setOnPreferenceChangeListener(this);

        this.mScreenBit = ((ListPreference) localPreferenceScreen.findPreference(SCREEN_RECORDER_BITRATE));
        i = Settings.Global.getInt(localContentResolver, SCREEN_RECORDER_BITRATE, 4000000);
        this.mScreenBit.setValue(String.valueOf(i));
        this.mScreenBit.setOnPreferenceChangeListener(this);

        setListSummary(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mScreenSize) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), SCREEN_RECORDER_OUTPUT_DIMENSIONS, i);
            setListSummary(this.mScreenSize);
            return true;
        }

        if (paramPreference == this.mScreenBit) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), SCREEN_RECORDER_BITRATE, i);
            setListSummary(this.mScreenBit);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mPMData) {
            if (this.mPMData.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, POWER_MENU_DATA, i);
            return true;
        }

        if (paramPreference == this.mPMAirplane) {
            if (this.mPMAirplane.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, POWER_MENU_AIRPLANE, i);
            return true;
        }

        if (paramPreference == this.mPMScreenShot) {
            if (this.mPMScreenShot.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, POWER_MENU_SCREENSHOT, i);
            return true;
        }

        if (paramPreference == this.mPMScreenRecord) {
            if (this.mPMScreenRecord.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, POWER_MENU_SCREENRECORD, i);
            return true;
        }

        if (paramPreference == this.mPMVolume) {
            if (this.mPMVolume.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, POWER_MENU_VOLUME, i);
            return true;
        }

        if (paramPreference == this.mScreenAudio) {
            if (this.mScreenAudio.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SCREEN_RECORDER_RECORD_AUDIO, i);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListSummary(null);
    }
}