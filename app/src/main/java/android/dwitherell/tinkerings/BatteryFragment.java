package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import java.util.Arrays;

public class BatteryFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String BATTERYBAR_ANIMATION = "batterybar_animation";
    private static final String BATTERY_COLOR_44 = "battery_color_44";
    private static final String CUSTOM_BATTERYTEXT_COLOR = "custom_batterytext_color";
    private static final String CUSTOM_BATTERY_COLOR = "custom_battery_color";
    private static final String MIUIBATTERY_INDICATOR_STYLE = "miuibattery_indicator_style";
    private static final String STATUSBAR_ACC_BATTERY = "statusbar_acc_battery";
    private static final String STATUSBAR_BATTERY_PERCENT = "statusbar_battery_percent";
    private static final String BATTERY_COLOR = "battery_color";
    private static final String BATTERY_COLOR_AUTO_CHARGING = "battery_color_auto_charging";
    private static final String BATTERY_COLOR_AUTO_LOW = "battery_color_auto_low";
    private static final String BATTERY_COLOR_AUTO_MEDIUM = "battery_color_auto_medium";
    private static final String BATTERY_COLOR_AUTO_REGULAR = "battery_color_auto_regular";
    private static final String BATTERY_TEXT_COLOR = "battery_text_color";
    private static final String MINITLINK = "3minitlink";

    private static final String DISPLAY_BATTERY_PERCENTAGE = "display_battery_percentage";

    Context context;
    private Preference m3minit;
    private ColorPickerPreference mAutoCharging;
    private ColorPickerPreference mAutoLow;
    private ColorPickerPreference mAutoMedium;
    private ColorPickerPreference mAutoRegular;
    private ColorPickerPreference mColor;
    private ListPreference mAccBatt;
    private CheckBoxPreference mBatteryBarAnim;
    private CheckBoxPreference mBatteryPercent;
    private ColorPickerPreference mCustomBColor;
    private ColorPickerPreference mCustomBTextColor;
    private CheckBoxPreference mCustomBattery;
    private ListPreference mMiuiBatteryStyle;
    private CheckBoxPreference mTextColor;

    private boolean checkApp() {
        try {
            getActivity().getPackageManager().getApplicationInfo("com.three.minit.minitbatterysettings", PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }
    }

    private void setListSummary(ListPreference paramListPreference) {
        int i;
        int j;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mAccBatt)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_ACC_BATTERY, 0);
            // Use values then entries when values don't follow index 0, 1, 2,... pattern
            strarray = getResources().getStringArray(R.array.battery_values);
            j = Arrays.asList(strarray).indexOf(String.valueOf(i));
            strarray = getResources().getStringArray(R.array.battery_entries);
            str = strarray[j];
            this.mAccBatt.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mMiuiBatteryStyle)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), MIUIBATTERY_INDICATOR_STYLE, 0);
            strarray = getResources().getStringArray(R.array.batterybar_entries);
            str = strarray[i];
            this.mMiuiBatteryStyle.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref) {
        if ((ColorPref == null) || (ColorPref == this.mAutoCharging)) {
            this.mAutoCharging.setSummary(this.mAutoCharging.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_CHARGING, mAutoCharging.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAutoLow)) {
            this.mAutoLow.setSummary(this.mAutoLow.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_LOW, mAutoLow.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAutoMedium)) {
            this.mAutoMedium.setSummary(this.mAutoMedium.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_MEDIUM, mAutoMedium.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mAutoRegular)) {
            this.mAutoRegular.setSummary(this.mAutoRegular.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_REGULAR, mAutoRegular.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mColor)) {
            this.mColor.setSummary(this.mColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), BATTERY_COLOR, mColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mCustomBColor)) {
            this.mCustomBColor.setSummary(this.mCustomBColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_BATTERY_COLOR, mCustomBColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mCustomBTextColor)) {
            this.mCustomBTextColor.setSummary(this.mCustomBTextColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_BATTERYTEXT_COLOR, mCustomBTextColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

    }

    private void updateBattStylePrefs() {
        // Below, bool1/2 determine if the various color level options are enabled
        // T if some sort of bar is selected
        boolean bool1 = (Settings.Global.getInt(getActivity().getContentResolver(), MIUIBATTERY_INDICATOR_STYLE, 0) != 0);
        // T if batt text % is selected
        boolean bool2 = (Settings.System.getInt(getActivity().getContentResolver(), DISPLAY_BATTERY_PERCENTAGE, 0) != 0);
        boolean bool3 = this.mTextColor.isChecked();

        // Here icon type determines if 3minit link or the battery color options are enabled
        int i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_ACC_BATTERY, 0);
        // T if 3minit selected
        boolean bool4 = (i == 4);
        // T if either 4.4 battery is selected
        boolean bool5 = ((i == 0) || (i == 1));
        boolean bool6 = this.mCustomBattery.isChecked();

        this.mBatteryBarAnim.setEnabled(bool1);

        this.mTextColor.setEnabled(bool1 || bool2);
        this.mAutoCharging.setPreviewDim((bool1 || bool2) && bool3);
        this.mAutoLow.setPreviewDim((bool1 || bool2) && bool3);
        this.mAutoMedium.setPreviewDim((bool1 || bool2) && bool3);
        this.mAutoRegular.setPreviewDim((bool1 || bool2) && bool3);
        this.mColor.setEnabled((bool1 || bool2) && !bool3);
        this.mColor.setPreviewDim((bool1 || bool2) && !bool3);

        this.m3minit.setEnabled(bool4);

        this.mCustomBattery.setEnabled(bool5);
        this.mCustomBColor.setPreviewDim(bool5 && bool6);
        this.mCustomBTextColor.setPreviewDim(bool5 && bool6);
    }


    public BatteryFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.battery_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;

        /* Set up checkbox stuff */
        this.mBatteryBarAnim = ((CheckBoxPreference) localPreferenceScreen.findPreference(BATTERYBAR_ANIMATION));
        if (Settings.Global.getInt(localContentResolver, BATTERYBAR_ANIMATION, 0) != 0) {
            mBatteryBarAnim.setChecked(true);
        } else {
            mBatteryBarAnim.setChecked(false);
        }

        this.mBatteryPercent = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_BATTERY_PERCENT));
        if (Settings.System.getInt(localContentResolver, DISPLAY_BATTERY_PERCENTAGE, 0) != 0) {
            mBatteryPercent.setChecked(true);
        } else {
            mBatteryPercent.setChecked(false);
        }

        this.mCustomBattery = ((CheckBoxPreference) localPreferenceScreen.findPreference(BATTERY_COLOR_44));
        if (Settings.Global.getInt(localContentResolver, BATTERY_COLOR_44, 0) != 0) {
            mCustomBattery.setChecked(true);
        } else {
            mCustomBattery.setChecked(false);
        }

        this.mTextColor = ((CheckBoxPreference) localPreferenceScreen.findPreference(BATTERY_TEXT_COLOR));
        if (Settings.Global.getInt(localContentResolver, BATTERY_TEXT_COLOR, 0) != 0) {
            mTextColor.setChecked(true);
        } else {
            mTextColor.setChecked(false);
        }

        /* Here are for list preferences */
        this.mAccBatt = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_ACC_BATTERY));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_ACC_BATTERY, 0);
        this.mAccBatt.setValue(String.valueOf(i));
        this.mAccBatt.setOnPreferenceChangeListener(this);

        this.mMiuiBatteryStyle = ((ListPreference) localPreferenceScreen.findPreference(MIUIBATTERY_INDICATOR_STYLE));
        i = Settings.Global.getInt(localContentResolver, MIUIBATTERY_INDICATOR_STYLE, 0);
        this.mMiuiBatteryStyle.setValue(String.valueOf(i));
        this.mMiuiBatteryStyle.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to color picker */
        this.mColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(BATTERY_COLOR));
        this.mColor.setOnPreferenceChangeListener(this);

        this.mAutoCharging = ((ColorPickerPreference) localPreferenceScreen.findPreference(BATTERY_COLOR_AUTO_CHARGING));
        this.mAutoCharging.setOnPreferenceChangeListener(this);

        this.mAutoLow = ((ColorPickerPreference) localPreferenceScreen.findPreference(BATTERY_COLOR_AUTO_LOW));
        this.mAutoLow.setOnPreferenceChangeListener(this);

        this.mAutoMedium = ((ColorPickerPreference) localPreferenceScreen.findPreference(BATTERY_COLOR_AUTO_MEDIUM));
        this.mAutoMedium.setOnPreferenceChangeListener(this);

        this.mAutoRegular = ((ColorPickerPreference) localPreferenceScreen.findPreference(BATTERY_COLOR_AUTO_REGULAR));
        this.mAutoRegular.setOnPreferenceChangeListener(this);

        this.mCustomBColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(CUSTOM_BATTERY_COLOR));
        this.mCustomBColor.setOnPreferenceChangeListener(this);

        this.mCustomBTextColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(CUSTOM_BATTERYTEXT_COLOR));
        this.mCustomBTextColor.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to miscellaneous activities */
        this.m3minit = localPreferenceScreen.findPreference(MINITLINK);

        updateBattStylePrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;
        int z;
        String t;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mMiuiBatteryStyle) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), MIUIBATTERY_INDICATOR_STYLE, i);
            setListSummary(this.mMiuiBatteryStyle);
            updateBattStylePrefs();
            return true;
        }

        if (paramPreference == this.mAccBatt) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_ACC_BATTERY, i);
            setListSummary(this.mAccBatt);
            updateBattStylePrefs();
            return true;
        }

        /* For changes in color picker prefs... */
        if (paramPreference == this.mColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), BATTERY_COLOR, (Integer) paramObject);
            t = mColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mColor);
            return true;
        }

        if (paramPreference == this.mAutoCharging) {
            Settings.Global.putInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_CHARGING, (Integer) paramObject);
            t = mAutoCharging.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAutoCharging);
            return true;
        }

        if (paramPreference == this.mAutoLow) {
            Settings.Global.putInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_LOW, (Integer) paramObject);
            t = mAutoLow.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAutoLow);
            return true;
        }

        if (paramPreference == this.mAutoMedium) {
            Settings.Global.putInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_MEDIUM, (Integer) paramObject);
            t = mAutoMedium.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAutoMedium);
            return true;
        }

        if (paramPreference == this.mAutoRegular) {
            Settings.Global.putInt(getActivity().getContentResolver(), BATTERY_COLOR_AUTO_REGULAR, (Integer) paramObject);
            t = mAutoRegular.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mAutoRegular);
            return true;
        }

        if (paramPreference == this.mCustomBColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_BATTERY_COLOR, (Integer) paramObject);
            t = mCustomBColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mCustomBColor);
            return true;
        }

        if (paramPreference == this.mCustomBTextColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_BATTERYTEXT_COLOR, (Integer) paramObject);
            t = mCustomBTextColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                z = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                z *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, z);
            }
            setPrefSummaryColor(mCustomBTextColor);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        Intent localIntent;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mBatteryBarAnim) {
            if (this.mBatteryBarAnim.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, BATTERYBAR_ANIMATION, i);
            return true;
        }

        if (paramPreference == this.mBatteryPercent) {
            if (this.mBatteryPercent.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.System.putInt(localContentResolver, DISPLAY_BATTERY_PERCENTAGE, i);
            updateBattStylePrefs();
            return true;
        }

        if (paramPreference == this.mCustomBattery) {
            if (this.mCustomBattery.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, BATTERY_COLOR_44, i);
            updateBattStylePrefs();
            return true;
        }

        if (paramPreference == this.mTextColor) {
            if (this.mTextColor.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, BATTERY_TEXT_COLOR, i);
            updateBattStylePrefs();
            return true;
        }

        /* For miscellaneous pref things */
        if (paramPreference == this.m3minit) {
            if (checkApp()) {
                localIntent = new Intent();
                localIntent.setClassName("com.three.minit.minitbatterysettings", "com.three.minit.minitbatterysettings.MainActivity");
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(localIntent);
            } else {
                Toast.makeText(getActivity(), "Need to install 3minit app", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBattStylePrefs();
        setListSummary(null);
        setPrefSummaryColor(null);
    }

}