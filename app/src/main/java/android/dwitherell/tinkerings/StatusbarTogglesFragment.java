package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.dwitherell.tinkerings.utils.SeekBarPreference;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class StatusbarTogglesFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String CUSTOM_LONG_TSM = "custom_long_tsm";
    private static final String CUSTOM_LONG_TSM_TARGET = "custom_long_tsm_target";
    private static final String CUSTOM_TOGGLE_NUMBER = "custom_toggle_number";
    private static final String CUSTOM_TOGGLE_NUMBER_LAND = "custom_toggle_number_land";
    private static final String CUSTOM_TOGGLE_NUMBER_PORT = "custom_toggle_number_port";
    private static final String STATUSBAR_HIDE_BRIGHT = "statusbar_hide_bright";
    private static final String STATUSBAR_PANEL_NUMBER = "statusbar_panel_number";
    private static final String STATUSBAR_TOGGLETEXT_COLOR = "statusbar_toggletext_color";
    private static final String STATUSBAR_TOGGLE_COLOR = "statusbar_toggle_color";
    private static final String STATUSBAR_TOGGLE_LED_COLOR = "statusbar_toggle_led_color";
    private static final String STATUSBAR_TOGGLE_OPTIONS = "statusbar_toggle_options";
    private static final String STATUSBAR_TOGGLE_RESET = "statusbar_toggle_reset";
    private static final String STATUSBAR_TOGGLE_TYPE = "statusbar_toggle_type";
    private static final String TOGGLE_CUSTOM_COLOR = "toggle_custom_color";
    private static final String TOGGLE_CUSTOM_TEXTCOLOR = "toggle_custom_textcolor";
    private static final String TOGGLE_HIDE_TEXT = "toggle_hide_text";
    private static final String VOLUME_NOTIPANEL_ADJUSTMENT = "volume_notipanel_adjustment";
    private static final String CUSTOM_DONOTHING_INTENT = "donothingintent";
    private static final String CUSTOM_POWERMENU_INTENT = "powermenuintent";
    private static final String CUSTOM_SCREENOFF_INTENT = "screenoffintent";

    private static final String CSC_FLAG = "TMB";
    private static final String NOTIFICATION_PANEL_BRIGHTNESS_ADJUSTMENT = "notification_panel_brightness_adjustment";
    private static final String NOTIFICATION_PANEL_ACTIVE_APP_LIST = "notification_panel_active_app_list";
    private static final String TOGGLE_LIGHT = "toggle_light";
    private static final String LOCK_LIGHT = "lock_light";

    Context context;

    private CheckBoxPreference mCustomTSMLong;
    private Preference mCustomTSMTarget;
    private CheckBoxPreference mHideBright;
    private CheckBoxPreference mHideToggleText;
    private CheckBoxPreference mStatusbarPanNum;
    private ColorPickerPreference mToggleColor;
    private CheckBoxPreference mToggleColorAllow;
    private ColorPickerPreference mToggleLEDColor;
    private SeekBarPreference mToggleNum;
    private SeekBarPreference mToggleNumLand;
    private SeekBarPreference mToggleNumPort;
    private Preference mToggleOptions;
    private Preference mToggleReset;
    private ColorPickerPreference mToggleTextColor;
    private CheckBoxPreference mToggleTextColorAllow;
    private ListPreference mToggleType;
    private CheckBoxPreference mVolSlider;


    private String getFriendlyActivityName(Intent activityIntent, boolean labelOnly) {
        PackageManager localPackageManager = getActivity().getPackageManager();
        ActivityInfo localActivityInfo = activityIntent.resolveActivityInfo(localPackageManager, 1);
        String str = null;
        if (localActivityInfo != null) {
            str = localActivityInfo.loadLabel(localPackageManager).toString();
            if (TextUtils.isEmpty(str) && (!labelOnly)) {
                str = localActivityInfo.name;
            }
        }
        if (!TextUtils.isEmpty(str) || (labelOnly)) {
            return str;
        }
        return activityIntent.toUri(0);
    }

    private String getFriendlyShortcutName(Intent shortIntent) {
        String str1 = getFriendlyActivityName(shortIntent, true);
        String str2 = shortIntent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
        if (!TextUtils.isEmpty(str1) && !TextUtils.isEmpty(str2)) {
            return str1 + ": " + str2;
        }
        if (!TextUtils.isEmpty(str2)) {
            return str2;
        }
        return shortIntent.toUri(0);
    }

    private void setCustomAppSummary(Preference appPickPref) {
        String str1;
        String str2;

        if ((appPickPref == null) || (appPickPref == this.mCustomTSMTarget)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_LONG_TSM_TARGET);
            if (TextUtils.isEmpty(str1)) {
                str2 = getResources().getString(R.string.picker_summary);
            } else {
                switch (str1) {
                    case CUSTOM_SCREENOFF_INTENT:
                        str2 = getString(R.string.screen_off);
                        break;
                    case CUSTOM_POWERMENU_INTENT:
                        str2 = getString(R.string.powermenu);
                        break;
                    case CUSTOM_DONOTHING_INTENT:
                        str2 = getString(R.string.donothing);
                        break;
                    default:
                        str2 = getFriendlyNameForUri(str1);
                }
            }
            this.mCustomTSMTarget.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }
    }


    private void setListSummary(ListPreference paramListPreference)
    {
        int i;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mToggleType)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_TYPE, 0);
            strarray = getResources().getStringArray(R.array.toggle_entries);
            str = strarray[i];
            this.mToggleType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref)
    {
        if ((ColorPref == null) || (ColorPref == this.mToggleColor))
        {
            this.mToggleColor.setSummary(this.mToggleColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_COLOR, mToggleColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mToggleLEDColor))
        {
            this.mToggleLEDColor.setSummary(this.mToggleLEDColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_LED_COLOR, mToggleLEDColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mToggleTextColor))
        {
            this.mToggleTextColor.setSummary(this.mToggleTextColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_TOGGLETEXT_COLOR, mToggleTextColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateColorPrefs() {
        // These are set up only for those that might possibly become enabled/disabled
        this.mToggleColor.setPreviewDim(this.mToggleColor.isEnabled());
        this.mToggleLEDColor.setPreviewDim(this.mToggleLEDColor.isEnabled());
        this.mToggleTextColor.setPreviewDim(this.mToggleTextColor.isEnabled());
    }

    private void updateTogglePrefs() {
        boolean bool1 = (Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_TYPE, 0) == 2);
        boolean bool2 = (Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_PANEL_NUMBER, 0) == 0);

        this.mToggleNum.setEnabled(!(bool1 && bool2));
        this.mToggleNumLand.setEnabled(!bool1);
        this.mToggleNumPort.setEnabled(!bool1);
    }

    public String getFriendlyNameForUri(String paramUri) {
        if (TextUtils.isEmpty(paramUri)) {
            return null;
        }
        try
        {
            Intent localIntent = Intent.parseUri(paramUri, 0);
            if (Intent.ACTION_MAIN.equals(localIntent.getAction())) {
                return getFriendlyActivityName(localIntent, false);
            }
            if (-1 != localIntent.getExtras().getInt(TinkerActivity.EXTRA_START_FRAGMENT, -1)) {
                return getString(R.string.app_name);
            }
            return getFriendlyShortcutName(localIntent);
        }
        catch (URISyntaxException localURISyntaxException) {}
        return paramUri;
    }

    public StatusbarTogglesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.statusbar_toggles_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;
        BigDecimal k;
        BigDecimal l;

        /* Set up checkbox stuff */
        this.mHideBright = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_BRIGHT));
        if (Settings.System.getInt(localContentResolver, NOTIFICATION_PANEL_BRIGHTNESS_ADJUSTMENT, 0) != 0) {
            mHideBright.setChecked(true);
        } else {
            mHideBright.setChecked(false);
        }

        this.mToggleTextColorAllow = ((CheckBoxPreference) localPreferenceScreen.findPreference(TOGGLE_CUSTOM_TEXTCOLOR));
        if (Settings.Global.getInt(localContentResolver, TOGGLE_CUSTOM_TEXTCOLOR, 0) != 0) {
            mToggleTextColorAllow.setChecked(true);
        } else {
            mToggleTextColorAllow.setChecked(false);
        }

        this.mToggleColorAllow = ((CheckBoxPreference) localPreferenceScreen.findPreference(TOGGLE_CUSTOM_COLOR));
        if (Settings.Global.getInt(localContentResolver, TOGGLE_CUSTOM_COLOR, 0) != 0) {
            mToggleColorAllow.setChecked(true);
        } else {
            mToggleColorAllow.setChecked(false);
        }

        this.mStatusbarPanNum = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_PANEL_NUMBER));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_PANEL_NUMBER, 0) != 0) {
            mStatusbarPanNum.setChecked(true);
        } else {
            mStatusbarPanNum.setChecked(false);
        }

        this.mHideToggleText = ((CheckBoxPreference) localPreferenceScreen.findPreference(TOGGLE_HIDE_TEXT));
        if (Settings.Global.getInt(localContentResolver, TOGGLE_HIDE_TEXT, 0) != 0) {
            mHideToggleText.setChecked(true);
        } else {
            mHideToggleText.setChecked(false);
        }

        this.mVolSlider = ((CheckBoxPreference) localPreferenceScreen.findPreference(VOLUME_NOTIPANEL_ADJUSTMENT));
        if (Settings.Global.getInt(localContentResolver, VOLUME_NOTIPANEL_ADJUSTMENT, 0) != 0) {
            mVolSlider.setChecked(true);
        } else {
            mVolSlider.setChecked(false);
        }

        this.mCustomTSMLong = ((CheckBoxPreference) localPreferenceScreen.findPreference(CUSTOM_LONG_TSM));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_LONG_TSM, 0) != 0) {
            mCustomTSMLong.setChecked(true);
        } else {
            mCustomTSMLong.setChecked(false);
        }

        /* Here are for list preferences */
        this.mToggleType = ((ListPreference) localPreferenceScreen.findPreference(STATUSBAR_TOGGLE_TYPE));
        i = Settings.Global.getInt(localContentResolver, STATUSBAR_TOGGLE_TYPE, 0);
        this.mToggleType.setValue(String.valueOf(i));
        this.mToggleType.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to color picker */
        this.mToggleColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_TOGGLE_COLOR));
        this.mToggleColor.setOnPreferenceChangeListener(this);

        this.mToggleLEDColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_TOGGLE_LED_COLOR));
        this.mToggleLEDColor.setOnPreferenceChangeListener(this);

        this.mToggleTextColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_TOGGLETEXT_COLOR));
        this.mToggleTextColor.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to number picker */
        this.mToggleNum = ((SeekBarPreference) localPreferenceScreen.findPreference(CUSTOM_TOGGLE_NUMBER));
        i = Settings.Global.getInt(localContentResolver, CUSTOM_TOGGLE_NUMBER, this.mToggleNum.getDefault() * this.mToggleNum.getFactor());
        k = new BigDecimal(i);
        if (this.mToggleNum.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.mToggleNum.getPercent() ? new BigDecimal(this.mToggleNum.getMax() - this.mToggleNum.getMin()) : new BigDecimal(this.mToggleNum.getFactor() * Math.pow(10, (double)this.mToggleNum.getDecimals()));
        k = k.divide(l, this.mToggleNum.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.mToggleNum.setSummary(k + " " + this.mToggleNum.getUnits());
        this.mToggleNum.setOnPreferenceChangeListener(this);

        this.mToggleNumPort = ((SeekBarPreference) localPreferenceScreen.findPreference(CUSTOM_TOGGLE_NUMBER_PORT));
        i = Settings.Global.getInt(localContentResolver, CUSTOM_TOGGLE_NUMBER_PORT, this.mToggleNumPort.getDefault() * this.mToggleNumPort.getFactor());
        k = new BigDecimal(i);
        if (this.mToggleNumPort.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.mToggleNumPort.getPercent() ? new BigDecimal(this.mToggleNumPort.getMax() - this.mToggleNumPort.getMin()) : new BigDecimal(this.mToggleNumPort.getFactor() * Math.pow(10, (double)this.mToggleNumPort.getDecimals()));
        k = k.divide(l, this.mToggleNumPort.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.mToggleNumPort.setSummary(k + " " + this.mToggleNumPort.getUnits());
        this.mToggleNumPort.setOnPreferenceChangeListener(this);

        this.mToggleNumLand = ((SeekBarPreference) localPreferenceScreen.findPreference(CUSTOM_TOGGLE_NUMBER_LAND));
        i = Settings.Global.getInt(localContentResolver, CUSTOM_TOGGLE_NUMBER_LAND, this.mToggleNumLand.getDefault() * this.mToggleNumLand.getFactor());
        k = new BigDecimal(i);
        if (this.mToggleNumLand.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.mToggleNumLand.getPercent() ? new BigDecimal(this.mToggleNumLand.getMax() - this.mToggleNumLand.getMin()) : new BigDecimal(this.mToggleNumLand.getFactor() * Math.pow(10, (double)this.mToggleNumLand.getDecimals()));
        k = k.divide(l, this.mToggleNumLand.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.mToggleNumLand.setSummary(k + " " + this.mToggleNumLand.getUnits());
        this.mToggleNumLand.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to app picking */
        this.mCustomTSMTarget = localPreferenceScreen.findPreference(CUSTOM_LONG_TSM_TARGET);

        /* Here are for preferences related to miscellaneous activities */
        this.mToggleOptions = localPreferenceScreen.findPreference(STATUSBAR_TOGGLE_OPTIONS);
        this.mToggleReset = localPreferenceScreen.findPreference(STATUSBAR_TOGGLE_RESET);

        /* Get preference enabled/summary things all set up */
        updateColorPrefs();
        updateTogglePrefs();
        setListSummary(null);
        setCustomAppSummary(null);
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;
        int m;
        String t;
        BigDecimal k;
        BigDecimal l;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mToggleType) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_TYPE, i);
            setListSummary(this.mToggleType);
            updateTogglePrefs();
            return true;
        }

        /* Process changes made in seekbar preferences */
        if (paramPreference == this.mToggleNum) {
            i = (this.mToggleNum.getMin() + (Integer) paramObject * this.mToggleNum.getStep()) * this.mToggleNum.getFactor();
            k = new BigDecimal(i);
            if (this.mToggleNum.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.mToggleNum.getPercent() ? new BigDecimal(this.mToggleNum.getMax() - this.mToggleNum.getMin()) : new BigDecimal(this.mToggleNum.getFactor() * Math.pow(10, (double)this.mToggleNum.getDecimals()));
            k = k.divide(l, this.mToggleNum.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER, i);
            this.mToggleNum.setSummary(k + " " + mToggleNum.getUnits());

            // Check to see if new max is lower than land/port settings, and force em down if so remembering to take away min() so things set correctly
            if (i < Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER_PORT, i)) {
                onPreferenceChange(this.mToggleNumPort, i - this.mToggleNumPort.getMin());
            }
            if (i < Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER_LAND, i)) {
                onPreferenceChange(this.mToggleNumLand, i - this.mToggleNumLand.getMin());
            }
            return true;
        }

        if (paramPreference == this.mToggleNumPort) {
            i = (this.mToggleNumPort.getMin() + (Integer) paramObject * this.mToggleNumPort.getStep()) * this.mToggleNumPort.getFactor();
            // These next two lines are specific to the land/port/max toggle relationship
            m = Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER, i);
            i = (i > m) ? m : i;
            k = new BigDecimal(i);
            if (this.mToggleNumPort.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.mToggleNumPort.getPercent() ? new BigDecimal(this.mToggleNumPort.getMax() - this.mToggleNumPort.getMin()) : new BigDecimal(this.mToggleNumPort.getFactor() * Math.pow(10, (double)this.mToggleNumPort.getDecimals()));
            k = k.divide(l, this.mToggleNumPort.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER_PORT, i);
            this.mToggleNumPort.setSummary(k + " " + mToggleNumPort.getUnits());
            return true;
        }

        if (paramPreference == this.mToggleNumLand) {
            i = (this.mToggleNumLand.getMin() + (Integer) paramObject * this.mToggleNumLand.getStep()) * this.mToggleNumLand.getFactor();
            // These next two lines are specific to the land/port/max toggle relationship
            m = Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER, i);
            i = (i > m) ? m : i;
            k = new BigDecimal(i);
            if (this.mToggleNumLand.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.mToggleNumLand.getPercent() ? new BigDecimal(this.mToggleNumLand.getMax() - this.mToggleNumLand.getMin()) : new BigDecimal(this.mToggleNumLand.getFactor() * Math.pow(10, (double)this.mToggleNumLand.getDecimals()));
            k = k.divide(l, this.mToggleNumLand.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_TOGGLE_NUMBER_LAND, i);
            this.mToggleNumLand.setSummary(k + " " + mToggleNumLand.getUnits());
            return true;
        }

        /* For changes in color picker prefs... */
        if (paramPreference == this.mToggleColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_COLOR, (Integer) paramObject);
            t = mToggleColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mToggleColor);
            return true;
        }

        if (paramPreference == this.mToggleLEDColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_TOGGLE_LED_COLOR, (Integer) paramObject);
            t = mToggleLEDColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mToggleLEDColor);
            return true;
        }

        if (paramPreference == this.mToggleTextColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_TOGGLETEXT_COLOR, (Integer) paramObject);
            t = mToggleTextColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mToggleTextColor);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        String str;
        Intent localIntent;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mHideBright) {
            if (this.mHideBright.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.System.putInt(localContentResolver, NOTIFICATION_PANEL_BRIGHTNESS_ADJUSTMENT, i);
            return true;
        }

        if (paramPreference == this.mStatusbarPanNum) {
            if (this.mStatusbarPanNum.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_PANEL_NUMBER, i);
            updateTogglePrefs();
            return true;
        }

        if (paramPreference == this.mToggleTextColorAllow) {
            if (this.mToggleTextColorAllow.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, TOGGLE_CUSTOM_TEXTCOLOR, i);
            updateColorPrefs();
            return true;
        }

        if (paramPreference == this.mToggleColorAllow) {
            if (this.mToggleColorAllow.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, TOGGLE_CUSTOM_COLOR, i);
            updateColorPrefs();
            return true;
        }

        if (paramPreference == this.mHideToggleText) {
            if (this.mHideToggleText.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, TOGGLE_HIDE_TEXT, i);
            updateColorPrefs();
            return true;
        }

        if (paramPreference == this.mVolSlider) {
            if (this.mVolSlider.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, VOLUME_NOTIPANEL_ADJUSTMENT, i);
            return true;
        }

        if (paramPreference == this.mCustomTSMLong) {
            if (this.mCustomTSMLong.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_LONG_TSM, i);
            return true;
        }

        /* For app picker preferences */
        if (paramPreference == this.mCustomTSMTarget) {
            ((TinkerActivity)getActivity()).displayAppPicker(paramPreference, R.array.apppicker_2_extras, R.array.apppicker_2_icons_extras, R.array.apppicker_2_keys_extras);
            return true;
        }

        /* For miscellaneous pref things */
        if (paramPreference == this.mToggleOptions)
        {
            localIntent = new Intent();
            localIntent.setClassName("com.android.settings", "com.android.settings.Settings$NotificationPanelMenuActivity");
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(localIntent);
            return true;
        }

        if (paramPreference == this.mToggleReset)
        {
            if ((SystemProperties.get("ro.csc.sales_code")).equals(CSC_FLAG)) {
                str = "Wifi;MobileData;Location;SilentMode;Bluetooth;Flashlight;TSM;AutoRotate;WifiCall;NetworkBooster;Ebook;DormantMode;PowerSaving;MultiWindow;AllShareCast;WiFiHotspot;SBeam;Nfc;AirView;AirGesture;DrivingMode;SmartStay;SmartPause;SmartScroll;Sync;AirplaneMode;ToddlerMode;SmartNetwork";
            } else {
                str = "Wifi;MobileData;Location;SilentMode;Bluetooth;Flashlight;TSM;AutoRotate;NetworkBooster;Ebook;DormantMode;PowerSaving;MultiWindow;AllShareCast;WiFiHotspot;SBeam;Nfc;AirView;AirGesture;DrivingMode;SmartStay;SmartPause;SmartScroll;Sync;AirplaneMode;ToddlerMode;SmartNetwork";
            }
            Settings.System.putString(localContentResolver, NOTIFICATION_PANEL_ACTIVE_APP_LIST, str);
            // Below is to help "reset" some things that might cause the flashlight toggle to be stuck as on or in process
            Settings.System.putInt(localContentResolver, TOGGLE_LIGHT, 0);
            Settings.System.putInt(localContentResolver, LOCK_LIGHT, 0);
            localIntent = new Intent("com.sec.samsung.torchwidget.torch_off");
            context.sendBroadcast(localIntent);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateColorPrefs();
        updateTogglePrefs();
        setListSummary(null);
        setCustomAppSummary(null);
        setPrefSummaryColor(null);
    }
}
