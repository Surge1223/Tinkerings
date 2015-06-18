package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import eu.chainfire.libsuperuser.Shell;

public class StatusbarGeneralFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String S5_SYSTEMUI_ENABLE = "s5_systemui_enable";
    private static final String STATUSBAR_BACKGROUND_COLOR = "statusbar_background_color";
    private static final String STATUSBAR_BRIGHT_CONTROL = "statusbar_bright_control";
    private static final String STATUSBAR_COLOR_CUSTOM = "statusbar_color_custom";
    private static final String STATUSBAR_DIM_BLOCK = "statusbar_dim_block";
    private static final String STATUSBAR_DOUBLETAP_SLEEP = "statusbar_doubletap_sleep";

    private static final String NOTIFICATION_PANEL_BRIGHTNESS_ADJUSTMENT = "notification_panel_brightness_adjustment";

    Context context;

    private CheckBoxPreference mDimBlock;
    private CheckBoxPreference mDoubleTapSleep;
    private CheckBoxPreference mS5SysUI;
    private CheckBoxPreference mStatusbarBrightControl;
    private ColorPickerPreference mStatusbarColor;
    private CheckBoxPreference mStatusbarCustom;

    private void resetStuff(String paramPackageString) {
        String str = "pkill -TERM -f " + paramPackageString;
        Shell.SU.run(str);
    }

    private void restartStuff(String paramPackageString)
    {
        String str = "am startservice -n " + paramPackageString;
        Shell.SU.run(str);
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref)
    {
        if ((ColorPref == null) || (ColorPref == this.mStatusbarColor))
        {
            this.mStatusbarColor.setSummary(this.mStatusbarColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), STATUSBAR_BACKGROUND_COLOR, mStatusbarColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateColorPrefs() {
        // These are set up only for those that might possibly become enabled/disabled
        this.mStatusbarColor.setPreviewDim(this.mStatusbarColor.isEnabled());
    }

    public StatusbarGeneralFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.statusbar_general_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Set up checkbox stuff */
        this.mS5SysUI = ((CheckBoxPreference) localPreferenceScreen.findPreference(S5_SYSTEMUI_ENABLE));
        if (Settings.Global.getInt(localContentResolver, S5_SYSTEMUI_ENABLE, 0) != 0) {
            mS5SysUI.setChecked(true);
        } else {
            mS5SysUI.setChecked(false);
        }

        this.mStatusbarCustom = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_COLOR_CUSTOM));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_COLOR_CUSTOM, 0) != 0) {
            mStatusbarCustom.setChecked(true);
        } else {
            mStatusbarCustom.setChecked(false);
        }

        this.mStatusbarBrightControl = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_BRIGHT_CONTROL));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_BRIGHT_CONTROL, 0) != 0) {
            mStatusbarBrightControl.setChecked(true);
        } else {
            mStatusbarBrightControl.setChecked(false);
        }

        this.mDoubleTapSleep = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_DOUBLETAP_SLEEP));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_DOUBLETAP_SLEEP, 0) != 0) {
            mDoubleTapSleep.setChecked(true);
        } else {
            mDoubleTapSleep.setChecked(false);
        }

        this.mDimBlock = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_DIM_BLOCK));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_DIM_BLOCK, 0) != 0) {
            mDimBlock.setChecked(true);
        } else {
            mDimBlock.setChecked(false);
        }

        /* Here are for preferences related to color picker */
        this.mStatusbarColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(STATUSBAR_BACKGROUND_COLOR));
        this.mStatusbarColor.setOnPreferenceChangeListener(this);

        /* Get preference enabled/summary things all set up */
        updateColorPrefs();
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int m;
        String t;

        /* For changes in color picker prefs... */
        if (paramPreference == this.mStatusbarColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), STATUSBAR_BACKGROUND_COLOR, (Integer) paramObject);
            t = mStatusbarColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mStatusbarColor);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mStatusbarCustom) {
            if (this.mStatusbarCustom.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_COLOR_CUSTOM, i);
            updateColorPrefs();
            return true;
        }

        if (paramPreference == this.mStatusbarBrightControl) {
            if (this.mStatusbarBrightControl.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_BRIGHT_CONTROL, i);
            return true;
        }

        if (paramPreference == this.mDoubleTapSleep) {
            if (this.mDoubleTapSleep.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_DOUBLETAP_SLEEP, i);
            return true;
        }

        if (paramPreference == this.mS5SysUI) {
            if (this.mS5SysUI.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, S5_SYSTEMUI_ENABLE, i);
            resetStuff("com.android.systemui");
            restartStuff("com.android.systemui/.SystemUIService");
            return true;
        }

        if (paramPreference == this.mDimBlock) {
            if (this.mDimBlock.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_DIM_BLOCK, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateColorPrefs();
        setPrefSummaryColor(null);
    }
}
