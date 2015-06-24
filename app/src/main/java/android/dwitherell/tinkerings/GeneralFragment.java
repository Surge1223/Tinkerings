package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.dwitherell.tinkerings.utils.ColorPickerPreference;
import android.dwitherell.tinkerings.utils.SeekBarPreference;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.IWindowManager;

import eu.chainfire.libsuperuser.Shell;

public class GeneralFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
    /*Keys from xml go here*/
    private static final String AIRCOMMAND_TEXT_COLOR = "aircommand_text_color";
    private static final String ANIM_DUR_CUSTOM = "anim_dur_custom";
    private static final int ANIMATION_WINDOW = 0;
    private static final int ANIMATION_TRANSITION = 1;
    private static final int ANIMATION_DURATION = 2;
    private static final String ENABLE_EAR_PROTECT = "enable_ear_protect";
    private static final String HEADS_UP_ENABLED = "heads_up_enabled";
    private static final String HEADS_UP_TIMEOUT = "heads_up_timeout";
    private static final String HIDEFULLBATT = "hidefullbatt";
    private static final String HIDELOWLBATT = "hidelowbatt";
    private static final String HIDERECCAPPS = "hidereccapps";
    private static final String NFC_TYPE_KEY = "nfc_type_key";
    private static final String PLUGSOUND = "plugsound";
    private static final String SCREEN_TOUCH_CAPBLOCK = "screen_touch_capblock";
    private static final String SKIPFLAGSECURE = "skipflagsecure";
    private static final String STAYLITSCREEN = "staylitscreen";
    private static final String TRANS_ANIM_CUSTOM = "trans_anim_custom";
    private static final String USBPLUGWAKE = "usbplugwake";
    private static final String VOL_PANEL_EXPAND_DEF = "vol_panel_expand_def";
    private static final String WINDOW_ANIM_CUSTOM = "window_anim_custom";
    Context context;
    private ColorPickerPreference mAirComColor;
    private SeekBarPreference mCustomAnimDur;
    private SeekBarPreference mCustomTransAnim;
    private SeekBarPreference mCustomWindowAnim;
    private CheckBoxPreference mEarProtect;
    private CheckBoxPreference mHeadsUp;
    private SeekBarPreference mHeadsUpTimeout;
    private CheckBoxPreference mHideFullB;
    private CheckBoxPreference mHideLowB;
    private CheckBoxPreference mHideReccApps;
    private ListPreference mNfcType;
    private CheckBoxPreference mPlugSound;
    private CheckBoxPreference mScreenTouchCap;
    private CheckBoxPreference mSkipFlagSecure;
    private CheckBoxPreference mStayLit;
    private CheckBoxPreference mUSBPlugWake;
    private CheckBoxPreference mVolPanExp;
    private IWindowManager mWindowManager;


/*
    private int invertBackGroundSpan(int paramTextColor)
    {
        double d1 = 0.299D * Color.red(paramTextColor);
        double d2 = 0.587D * Color.green(paramTextColor);
        if (1.0D - (0.114D * Color.blue(paramTextColor) + (d1 + d2)) / 255.0D >= 0.6D) {
            return Color.parseColor("#ccffffff");
        }
        return Color.parseColor("#cc000000");
    }
*/

    private void resetStuff(String paramPackageString) {
        String str = "pkill -TERM -f " + paramPackageString;
        Shell.SU.run(str);
    }

    private void restartStuff(String paramPackageString)
    {
        String str = "am startservice -n " + paramPackageString;
        Shell.SU.run(str);
    }

    private void setListSummary(ListPreference paramListPreference)
    {
        int i;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mNfcType))
        {
            i = Settings.Global.getInt(getActivity().getContentResolver(), NFC_TYPE_KEY, 0);
            strarray = getResources().getStringArray(R.array.nfc_entries);
            str = strarray[i];
            this.mNfcType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref)
    {
        if ((ColorPref == null) || (ColorPref == this.mAirComColor))
        {
            this.mAirComColor.setSummary(this.mAirComColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), AIRCOMMAND_TEXT_COLOR, mAirComColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    public GeneralFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.general_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        float f;
        BigDecimal k;
        BigDecimal l;
        int i;
        String str;

        /* Set up checkbox stuff first */
        this.mSkipFlagSecure = ((CheckBoxPreference)localPreferenceScreen.findPreference(SKIPFLAGSECURE));
        if (Settings.Global.getInt(localContentResolver, SKIPFLAGSECURE, 0) != 0) {
            mSkipFlagSecure.setChecked(true);
        } else {
            mSkipFlagSecure.setChecked(false);
        }

        this.mHeadsUp = ((CheckBoxPreference)localPreferenceScreen.findPreference(HEADS_UP_ENABLED));
        if (Settings.Global.getInt(localContentResolver, HEADS_UP_ENABLED, 0) != 0) {
            mHeadsUp.setChecked(true);
        } else {
            mHeadsUp.setChecked(false);
        }

        this.mScreenTouchCap = ((CheckBoxPreference)localPreferenceScreen.findPreference(SCREEN_TOUCH_CAPBLOCK));
        if (Settings.Global.getInt(localContentResolver, SCREEN_TOUCH_CAPBLOCK, 0) != 0) {
            mScreenTouchCap.setChecked(true);
        } else {
            mScreenTouchCap.setChecked(false);
        }

        this.mUSBPlugWake = ((CheckBoxPreference)localPreferenceScreen.findPreference(USBPLUGWAKE));
        if (Settings.Global.getInt(localContentResolver, USBPLUGWAKE, 0) != 0) {
            mUSBPlugWake.setChecked(true);
        } else {
            mUSBPlugWake.setChecked(false);
        }

        this.mPlugSound = ((CheckBoxPreference)localPreferenceScreen.findPreference(PLUGSOUND));
        if (Settings.Global.getInt(localContentResolver, PLUGSOUND, 0) != 0) {
            mPlugSound.setChecked(true);
        } else {
            mPlugSound.setChecked(false);
        }

        this.mStayLit = ((CheckBoxPreference)localPreferenceScreen.findPreference(STAYLITSCREEN));
        if (Settings.Global.getInt(localContentResolver, STAYLITSCREEN, 0) != 0) {
            mStayLit.setChecked(true);
        } else {
            mStayLit.setChecked(false);
        }

        this.mHideReccApps = ((CheckBoxPreference)localPreferenceScreen.findPreference(HIDERECCAPPS));
        if (Settings.Global.getInt(localContentResolver, HIDERECCAPPS, 0) != 0) {
            mHideReccApps.setChecked(true);
        } else {
            mHideReccApps.setChecked(false);
        }

        this.mHideLowB = ((CheckBoxPreference)localPreferenceScreen.findPreference(HIDELOWLBATT));
        if (Settings.Global.getInt(localContentResolver, HIDELOWLBATT, 0) != 0) {
            mHideLowB.setChecked(true);
        } else {
            mHideLowB.setChecked(false);
        }

        this.mHideFullB = ((CheckBoxPreference)localPreferenceScreen.findPreference(HIDEFULLBATT));
        if (Settings.Global.getInt(localContentResolver, HIDEFULLBATT, 0) != 0) {
            mHideFullB.setChecked(true);
        } else {
            mHideFullB.setChecked(false);
        }

        this.mVolPanExp = ((CheckBoxPreference)localPreferenceScreen.findPreference(VOL_PANEL_EXPAND_DEF));
        if (Settings.Global.getInt(localContentResolver, VOL_PANEL_EXPAND_DEF, 0) != 0) {
            mVolPanExp.setChecked(true);
        } else {
            mVolPanExp.setChecked(false);
        }

        this.mEarProtect= ((CheckBoxPreference)localPreferenceScreen.findPreference(ENABLE_EAR_PROTECT));
        if (Settings.Global.getInt(localContentResolver, ENABLE_EAR_PROTECT, 0) != 0) {
            mEarProtect.setChecked(true);
        } else {
            mEarProtect.setChecked(false);
        }

        /* Here are for preferences related to color picker */
        this.mAirComColor = ((ColorPickerPreference)localPreferenceScreen.findPreference(AIRCOMMAND_TEXT_COLOR));
        this.mAirComColor.setOnPreferenceChangeListener(this);

        /* Here are for list preferences */
        this.mNfcType = ((ListPreference)localPreferenceScreen.findPreference(NFC_TYPE_KEY));
        i = Settings.Global.getInt(localContentResolver, NFC_TYPE_KEY, 0);
        this.mNfcType.setValue(String.valueOf(i));
        this.mNfcType.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to number picker */
        this.mHeadsUpTimeout = ((SeekBarPreference) localPreferenceScreen.findPreference(HEADS_UP_TIMEOUT));
        i = Settings.Global.getInt(localContentResolver, HEADS_UP_TIMEOUT, this.mHeadsUpTimeout.getDefault() * this.mHeadsUpTimeout.getFactor());
        k = new BigDecimal(i);
        if (this.mHeadsUpTimeout.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.mHeadsUpTimeout.getPercent() ? new BigDecimal(this.mHeadsUpTimeout.getMax() - this.mHeadsUpTimeout.getMin()) : new BigDecimal(this.mHeadsUpTimeout.getFactor() * Math.pow(10, (double)this.mHeadsUpTimeout.getDecimals()));
        k = k.divide(l, this.mHeadsUpTimeout.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.mHeadsUpTimeout.setSummary(k + " " + this.mHeadsUpTimeout.getUnits());
        this.mHeadsUpTimeout.setOnPreferenceChangeListener(this);


        /* These are for number picker preferences as well but are special as they are animation related */
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));

        /* Window Animation */
        this.mCustomWindowAnim = ((SeekBarPreference) localPreferenceScreen.findPreference(WINDOW_ANIM_CUSTOM));
        try {
            f = this.mWindowManager.getAnimationScale(ANIMATION_WINDOW);
        } catch (RemoteException e) {
            f = 0.0F;
        }
        k = new BigDecimal(f).setScale(mCustomWindowAnim.getDecimals(),BigDecimal.ROUND_HALF_EVEN);
        f = k.floatValue();

        if (f <= 0.0F) {
            str = " off";
            this.mCustomWindowAnim.setSummary("Animation scale " + str);
        } else {
            str = getString(R.string.winaniscale_units);
            this.mCustomWindowAnim.setSummary("Animation scale " + "" + f + str);
        }

        this.mCustomWindowAnim.setOnPreferenceChangeListener(this);

        /* Transition animation */
        this.mCustomTransAnim = ((SeekBarPreference) localPreferenceScreen.findPreference(TRANS_ANIM_CUSTOM));
        try {
            f = this.mWindowManager.getAnimationScale(ANIMATION_TRANSITION);
        } catch (RemoteException e) {
            f = 0.0F;
        }
        k = new BigDecimal(f).setScale(mCustomTransAnim.getDecimals(),BigDecimal.ROUND_HALF_EVEN);
        f = k.floatValue();

        if (f <= 0.0F) {
            str = " off";
            this.mCustomTransAnim.setSummary("Animation scale " + str);
        } else {
            str = getString(R.string.trananiscale_units);
            this.mCustomTransAnim.setSummary("Animation scale " + "" + f + str);
        }

        this.mCustomTransAnim.setOnPreferenceChangeListener(this);

        /* Animation duration */
        this.mCustomAnimDur = ((SeekBarPreference) localPreferenceScreen.findPreference(ANIM_DUR_CUSTOM));
        try {
            f = this.mWindowManager.getAnimationScale(ANIMATION_DURATION);
        } catch (RemoteException e) {
            f = 0.0F;
        }
        k = new BigDecimal(f).setScale(mCustomAnimDur.getDecimals(),BigDecimal.ROUND_HALF_EVEN);
        f = k.floatValue();

        if (f <= 0.0F) {
            str = " off";
            this.mCustomAnimDur.setSummary("Animation scale " + str);
        } else {
            str = getString(R.string.anidurscale_units);
            this.mCustomAnimDur.setSummary("Animation scale " + "" + f + str);
        }

        this.mCustomAnimDur.setOnPreferenceChangeListener(this);

        setListSummary(null);
        setPrefSummaryColor(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
    {
        int i;
        float f;
        BigDecimal k;
        BigDecimal l;
        String t;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mNfcType)
        {
            i = Integer.valueOf((String)paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), NFC_TYPE_KEY, i);
            setListSummary(this.mNfcType);
            return true;
        }

        /* Process changes made in seekbar preferences */
        if (paramPreference == this.mHeadsUpTimeout)
        {
            i = (this.mHeadsUpTimeout.getMin() + (Integer) paramObject * this.mHeadsUpTimeout.getStep()) * this.mHeadsUpTimeout.getFactor();
            k = new BigDecimal(i);
            if (this.mHeadsUpTimeout.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.mHeadsUpTimeout.getPercent() ? new BigDecimal(this.mHeadsUpTimeout.getMax() - this.mHeadsUpTimeout.getMin()) : new BigDecimal(this.mHeadsUpTimeout.getFactor() * Math.pow(10, (double)this.mHeadsUpTimeout.getDecimals()));
            k = k.divide(l, this.mHeadsUpTimeout.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), HEADS_UP_TIMEOUT, i);
            this.mHeadsUpTimeout.setSummary(k + " " + mHeadsUpTimeout.getUnits());
            return true;
        }

        /* These are also seekbar preferences, but special animation cases */
        if (paramPreference == this.mCustomWindowAnim) {
            k = new BigDecimal((Integer)paramObject);
            l = new BigDecimal(this.mCustomWindowAnim.getFactor() * Math.pow(10, (double)this.mCustomWindowAnim.getDecimals())).setScale(0, BigDecimal.ROUND_HALF_EVEN);
            k = k.divide(l, this.mCustomWindowAnim.getDecimals(), BigDecimal.ROUND_HALF_EVEN);

            f = k.floatValue();
            try {
                this.mWindowManager.setAnimationScale(ANIMATION_WINDOW, f);
            } catch (RemoteException e) { }
            if (f <= 0.0F) {
                t = " off";
                this.mCustomWindowAnim.setSummary("Animation scale " + t);
            } else {
                t = getString(R.string.winaniscale_units);
                this.mCustomWindowAnim.setSummary("Animation scale " + "" + f + t);
            }

        }

        if (paramPreference == this.mCustomTransAnim) {
            k = new BigDecimal((Integer)paramObject);
            l = new BigDecimal(this.mCustomTransAnim.getFactor() * Math.pow(10, (double)this.mCustomTransAnim.getDecimals())).setScale(0, BigDecimal.ROUND_HALF_EVEN);
            k = k.divide(l, this.mCustomTransAnim.getDecimals(), BigDecimal.ROUND_HALF_EVEN);

            f = k.floatValue();
            try {
                this.mWindowManager.setAnimationScale(ANIMATION_TRANSITION, f);
            } catch (RemoteException e) { }
            if (f <= 0.0F) {
                t = " off";
                this.mCustomTransAnim.setSummary("Animation scale " + t);
            } else {
                t = getString(R.string.trananiscale_units);
                this.mCustomTransAnim.setSummary("Animation scale " + "" + f + t);
            }

        }

        if (paramPreference == this.mCustomAnimDur) {
            k = new BigDecimal((Integer)paramObject);
            l = new BigDecimal(this.mCustomAnimDur.getFactor() * Math.pow(10, (double)this.mCustomAnimDur.getDecimals())).setScale(0, BigDecimal.ROUND_HALF_EVEN);
            k = k.divide(l, this.mCustomAnimDur.getDecimals(), BigDecimal.ROUND_HALF_EVEN);

            f = k.floatValue();
            try {
                this.mWindowManager.setAnimationScale(ANIMATION_DURATION, f);
            } catch (RemoteException e) { }
            if (f <= 0.0F) {
                t = " off";
                this.mCustomAnimDur.setSummary("Animation scale " + t);
            } else {
                t = getString(R.string.anidurscale_units);
                this.mCustomAnimDur.setSummary("Animation scale " + "" + f + t);
            }

        }

        /* For color picker... */
        if (paramPreference == this.mAirComColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), AIRCOMMAND_TEXT_COLOR, (Integer) paramObject);
            t = mAirComColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                i = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                i *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, i);
            }
            setPrefSummaryColor(mAirComColor);
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference)
    {
        int i;
        int j;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mSkipFlagSecure)
        {
            if (this.mSkipFlagSecure.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SKIPFLAGSECURE, i);
            return true;
        }

        if (paramPreference == this.mHeadsUp)
        {
            if (this.mHeadsUp.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, HEADS_UP_ENABLED, i);
            return true;
        }

        if (paramPreference == this.mScreenTouchCap)
        {
            if (this.mScreenTouchCap.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SCREEN_TOUCH_CAPBLOCK, i);
            return true;
        }

        if (paramPreference == this.mUSBPlugWake)
        {
            if (this.mUSBPlugWake.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, USBPLUGWAKE, i);
            return true;
        }

        if (paramPreference == this.mPlugSound)
        {
            if (this.mPlugSound.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, PLUGSOUND, i);
            return true;
        }

        if (paramPreference == this.mStayLit)
        {
            if (this.mStayLit.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STAYLITSCREEN, i);
            return true;
        }

        if (paramPreference == this.mHideReccApps)
        {
            if (this.mHideReccApps.isChecked())
            {
                i = 1;
                j = 0;
            } else {
                i = 0;
                j = 1;
            }
            Settings.Global.putInt(localContentResolver, HIDERECCAPPS, i);
            Settings.System.putInt(localContentResolver, "recommended_apps_setting", j);
            Settings.System.putInt(localContentResolver, "recommended_apps_automatically_dockings", j);
            Settings.System.putInt(localContentResolver, "recommended_apps_automatically_roaming", j);
            return true;
        }

        if (paramPreference == this.mHideLowB)
        {
            if (this.mHideLowB.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, HIDELOWLBATT, i);
            return true;
        }

        if (paramPreference == this.mHideFullB)
        {
            if (this.mHideFullB.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, HIDEFULLBATT, i);
            return true;
        }

        if (paramPreference == this.mVolPanExp)
        {
            if (this.mVolPanExp.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, VOL_PANEL_EXPAND_DEF, i);
            return true;
        }

        if (paramPreference == this.mEarProtect)
        {
            if (this.mEarProtect.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, ENABLE_EAR_PROTECT, i);
            return true;
        }

        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListSummary(null);
        setPrefSummaryColor(null);
    }
}
