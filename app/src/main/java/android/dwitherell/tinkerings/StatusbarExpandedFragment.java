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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;
import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class StatusbarExpandedFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String CUSTOM_DATE_VIEW = "custom_date_view";
    private static final String CUSTOM_PIC_ALPHA = "custom_pic_alpha";
    private static final String CUSTOM_PIC_BG = "custom_pic_bg";
    private static final String CUSTOM_PIC_SWITCH = "custom_pic_switch";
    private static final String DATECLOCK_COLOR = "dateclock_color";
    private static final String NOTIFICATION_BACKGROUND_COLOR = "notification_background_color";
    private static final String NOTIFICATION_COLOR_CUSTOM = "notification_color_custom";
    private static final String NOTIFICATION_IMAGE = "notification_image";
    private static final String STATUSBAR_AOSP_CLEAR = "statusbar_aosp_clear";
    private static final String STATUSBAR_HANDLE_ANIM = "statusbar_handle_anim";
    private static final String STATUSBAR_HIDE_LATEST = "statusbar_hide_latest";
    private static final String STATUSBAR_HIDE_NONOTI = "statusbar_hide_nonoti";
    private static final String STATUSBAR_ONGOING_BLOCK = "statusbar_ongoing_block";
    private static final String USE_CUSTOM_DATE = "use_custom_date";
    private static final String CUSTOM_DONOTHING_INTENT = "donothingintent";
    private static final String CUSTOM_POWERMENU_INTENT = "powermenuintent";
    private static final String CUSTOM_SCREENOFF_INTENT = "screenoffintent";
    private static final int SELECT_PHOTO = 4;
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_PICK_SHORTCUT = 1;

    private static final String picdirpath = "Tweaked/res/";
    private static final String notibgfile = "background.png";

    Context context;
    private int mTargetPref = 0;

    private CheckBoxPreference mCustomDateViewApp;
    private ColorPickerPreference mDateClockColor;
    private Preference mDateViewApp;
    private CheckBoxPreference mHideLatest;
    private CheckBoxPreference mHideNoNoti;
    private CheckBoxPreference mHideOngoing;
    private CheckBoxPreference mMoveClear;
    private CheckBoxPreference mNotiAnimHandle;
    private ListPreference mNotiCustom;
    private ColorPickerPreference mNotificationColor;
    private CheckBoxPreference mNotiPic;
    private SeekBarPreference mNotiPicAlpha;
    private Preference mNotiPicPicker;

    private void completeSetCustom(String customkey) {
        if (customkey.equals(getString(R.string.screen_off))) {
            shortcutPicked(CUSTOM_SCREENOFF_INTENT, getString(R.string.screen_off), true);
        }

        if (customkey.equals(getString(R.string.powermenu))) {
            shortcutPicked(CUSTOM_POWERMENU_INTENT, getString(R.string.powermenu), true);
        }

        if (customkey.equals(getString(R.string.donothing))) {
            shortcutPicked(CUSTOM_DONOTHING_INTENT, getString(R.string.donothing), true);
        }

        if (customkey.equals(getString(R.string.app_name))) {
            final Intent fragintent = new Intent(context, TinkerActivity.class);
            fragintent.putExtra(TinkerActivity.EXTRA_START_FRAGMENT, 0);
            fragintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);

            shortcutPicked(fragintent.toUri(0), getString(R.string.app_name), true);
        }

    }

    private void completePicPicker(Intent customIntent) {
        // Show path to file... perhaps clean this up later...?
        // Maybe have summary show dimens? Is that possible?
        final Uri imageUri = (Uri)customIntent.getExtra(MediaStore.EXTRA_OUTPUT);
        String str = imageUri.getPath();

        shortcutPicked(str, str, true);
    }

    private void completeSetCustomApp(Intent customIntent) {
        shortcutPicked(customIntent.toUri(0), getFriendlyActivityName(customIntent, false), true);
    }

    private void completeSetCustomShortcut(Intent shortIntent) {
        Intent localIntent = shortIntent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
        localIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortIntent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME));
        shortcutPicked(localIntent.toUri(0).replaceAll("com.android.contacts.action.QUICK_CONTACT", Intent.ACTION_VIEW), getFriendlyShortcutName(localIntent), false);
    }

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

    private void pickPic(Preference paramPreference) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mOutX = dm.widthPixels;
            int mOutY = dm.heightPixels;

            File f = new File(Environment.getExternalStorageDirectory(), picdirpath + notibgfile);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            Uri tempUri = Uri.fromFile(f);
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.putExtra("crop", "true");
            photoPickerIntent.putExtra("scale", true);
            photoPickerIntent.putExtra("aspectX", mOutX);
            photoPickerIntent.putExtra("aspectY", mOutY);
            photoPickerIntent.putExtra("outputX", mOutX);
            photoPickerIntent.putExtra("outputY", mOutY);
            photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
            photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

            mTargetPref = 0;
            if (paramPreference == mNotiPicPicker) {
                mTargetPref = 2;
            }
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }

    }

    private void pickShort(Preference paramPreference) {
        Bundle localBundle = new Bundle();
        Context localContext = getActivity();
        ArrayList<String> localArrayList1 = new ArrayList<>();
        localArrayList1.add(getString(R.string.group_applications));
        localArrayList1.add(getString(R.string.app_name));
        //localArrayList1.add(getString(R.string.screen_off));
        //localArrayList1.add(getString(R.string.powermenu));
        //localArrayList1.add(getString(R.string.donothing));

        localBundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, localArrayList1);

        ArrayList<Intent.ShortcutIconResource> localArrayList2 = new ArrayList<>();
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_apps));
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.app_icon));
        //localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_screenoff));
        //localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_tsm_powermenu));
        //localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_donothing));

        localBundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, localArrayList2);

        Intent localIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        localIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        localIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.select_custom_short_title));
        localIntent.putExtras(localBundle);

        mTargetPref = 0;
        if (paramPreference == this.mDateViewApp) {
            mTargetPref = 1;
        }
        startActivityForResult(localIntent, REQUEST_PICK_SHORTCUT);
    }

    private void setCustomAppSummary(Preference appPickPref) {
        String str1;
        String str2;

        if ((appPickPref == null) || (appPickPref == this.mDateViewApp)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_DATE_VIEW);
            if (TextUtils.isEmpty(str1)) {
                str2 = getString(R.string.picker_summary);
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
            this.mDateViewApp.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }

        // Slightly different handling for image pick preferences...
        if ((appPickPref == null) || (appPickPref == this.mNotiPicPicker)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_PIC_BG);
            if (TextUtils.isEmpty(str1)) {
                str2 = getString(R.string.picker_summary);
            } else {
                str2 = str1;
            }
            this.mNotiPicPicker.setSummary(str2);
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

        if ((paramListPreference == null) || (paramListPreference == this.mNotiCustom)) {
            i = Settings.Global.getInt(getActivity().getContentResolver(), NOTIFICATION_COLOR_CUSTOM, 0);
            strarray = getResources().getStringArray(R.array.notiback_entries);
            str = strarray[i];
            this.mNotiCustom.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    private void setPrefSummaryColor(ColorPickerPreference ColorPref)
    {
        if ((ColorPref == null) || (ColorPref == this.mDateClockColor))
        {
            this.mDateClockColor.setSummary(this.mDateClockColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), DATECLOCK_COLOR, mDateClockColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }

        if ((ColorPref == null) || (ColorPref == this.mNotificationColor))
        {
            this.mNotificationColor.setSummary(this.mNotificationColor.getSummaryText() + ColorPickerPreference.convertToARGB(Settings.Global.getInt(getActivity().getContentResolver(), NOTIFICATION_BACKGROUND_COLOR, mNotificationColor.getPrefDefault())));
            if (ColorPref != null) {
                return;
            }
        }
    }

    private void updateNotiBackPrefs() {
        // Stuck the color preview dim option in this one as opposed to the other...
        int i = Settings.Global.getInt(getActivity().getContentResolver(), NOTIFICATION_COLOR_CUSTOM, 0);
        boolean bool1 = (i == 2);
        boolean bool2 = (i == 0);

        this.mNotificationColor.setEnabled(bool1);
        this.mNotificationColor.setPreviewDim(bool1);

        this.mNotiPic.setEnabled(bool2);
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

    public void onActivityResult(int paramRequest, int paramResult, Intent paramData) {
        super.onActivityResult(paramRequest, paramResult, paramData);
        if (paramResult != -1 || paramData == null) {
            return;
        }
        switch (paramRequest)
        {
            case REQUEST_PICK_SHORTCUT:
                processShortcut(paramData, REQUEST_PICK_APPLICATION, REQUEST_CREATE_SHORTCUT);
                return;
            case REQUEST_PICK_APPLICATION:
                completeSetCustomApp(paramData);
                return;
            case REQUEST_CREATE_SHORTCUT:
                completeSetCustomShortcut(paramData);
                return;
            case SELECT_PHOTO:
                completePicPicker(paramData);
                return;
            default:
        }
    }

    public StatusbarExpandedFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.statusbar_expanded_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;
        BigDecimal k;
        BigDecimal l;

        /* Set up checkbox stuff */
        this.mHideNoNoti = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_NONOTI));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_NONOTI, 0) != 0) {
            mHideNoNoti.setChecked(true);
        } else {
            mHideNoNoti.setChecked(false);
        }

        this.mHideOngoing = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_ONGOING_BLOCK));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_ONGOING_BLOCK, 0) != 0) {
            mHideOngoing.setChecked(true);
        } else {
            mHideOngoing.setChecked(false);
        }

        this.mMoveClear = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_AOSP_CLEAR));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_AOSP_CLEAR, 0) != 0) {
            mMoveClear.setChecked(true);
        } else {
            mMoveClear.setChecked(false);
        }

        this.mHideLatest = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HIDE_LATEST));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HIDE_LATEST, 0) != 0) {
            mHideLatest.setChecked(true);
        } else {
            mHideLatest.setChecked(false);
        }

        this.mNotiAnimHandle = ((CheckBoxPreference) localPreferenceScreen.findPreference(STATUSBAR_HANDLE_ANIM));
        if (Settings.Global.getInt(localContentResolver, STATUSBAR_HANDLE_ANIM, 0) != 0) {
            mNotiAnimHandle.setChecked(true);
        } else {
            mNotiAnimHandle.setChecked(false);
        }

        this.mCustomDateViewApp = ((CheckBoxPreference) localPreferenceScreen.findPreference(USE_CUSTOM_DATE));
        if (Settings.Global.getInt(localContentResolver, USE_CUSTOM_DATE, 0) != 0) {
            mCustomDateViewApp.setChecked(true);
        } else {
            mCustomDateViewApp.setChecked(false);
        }

        this.mNotiPic = ((CheckBoxPreference) localPreferenceScreen.findPreference(NOTIFICATION_IMAGE));
        if (Settings.Global.getInt(localContentResolver, NOTIFICATION_IMAGE, 0) != 0) {
            mNotiPic.setChecked(true);
        } else {
            mNotiPic.setChecked(false);
        }

        /* Here are for list preferences */
        this.mNotiCustom = ((ListPreference) localPreferenceScreen.findPreference(NOTIFICATION_COLOR_CUSTOM));
        i = Settings.Global.getInt(localContentResolver, NOTIFICATION_COLOR_CUSTOM, 0);
        this.mNotiCustom.setValue(String.valueOf(i));
        this.mNotiCustom.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to color picker */
        this.mNotificationColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(NOTIFICATION_BACKGROUND_COLOR));
        this.mNotificationColor.setOnPreferenceChangeListener(this);

        this.mDateClockColor = ((ColorPickerPreference) localPreferenceScreen.findPreference(DATECLOCK_COLOR));
        this.mDateClockColor.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to number picker */
        this.mNotiPicAlpha = ((SeekBarPreference) localPreferenceScreen.findPreference(CUSTOM_PIC_ALPHA));
        i = Settings.Global.getInt(localContentResolver, CUSTOM_PIC_ALPHA, this.mNotiPicAlpha.getDefault() * this.mNotiPicAlpha.getFactor());
        k = new BigDecimal(i);
        if (this.mNotiPicAlpha.getPercent()) {
            l = new BigDecimal(100);
            k = k.multiply(l);
        }
        l = this.mNotiPicAlpha.getPercent() ? new BigDecimal(this.mNotiPicAlpha.getMax() - this.mNotiPicAlpha.getMin()) : new BigDecimal(this.mNotiPicAlpha.getFactor() * Math.pow(10, (double)this.mNotiPicAlpha.getDecimals()));
        k = k.divide(l, this.mNotiPicAlpha.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
        this.mNotiPicAlpha.setSummary(k + " " + this.mNotiPicAlpha.getUnits());
        this.mNotiPicAlpha.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to app picking */
        this.mDateViewApp = localPreferenceScreen.findPreference(CUSTOM_DATE_VIEW);

        /* Here are for preferences related to miscellaneous activities */
        this.mNotiPicPicker = localPreferenceScreen.findPreference(CUSTOM_PIC_BG);

        /* Get preference enabled/summary things all set up */
        updateNotiBackPrefs();
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
        if (paramPreference == this.mNotiCustom) {
            i = Integer.valueOf((String) paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), NOTIFICATION_COLOR_CUSTOM, i);
            setListSummary(this.mNotiCustom);
            updateNotiBackPrefs();
            return true;
        }

        /* Process changes made in seekbar preferences */
        // This is slightly different due to being a percent scale... should add that in...
        if (paramPreference == this.mNotiPicAlpha) {
            i = (this.mNotiPicAlpha.getMin() + (Integer) paramObject * this.mNotiPicAlpha.getStep()) * this.mNotiPicAlpha.getFactor();
            k = new BigDecimal(i);
            if (this.mNotiPicAlpha.getPercent()) {
                l = new BigDecimal(100);
                k = k.multiply(l);
            }
            l = this.mNotiPicAlpha.getPercent() ? new BigDecimal(this.mNotiPicAlpha.getMax() - this.mNotiPicAlpha.getMin()) : new BigDecimal(this.mNotiPicAlpha.getFactor() * Math.pow(10, (double)this.mNotiPicAlpha.getDecimals()));
            k = k.divide(l, this.mNotiPicAlpha.getDecimals(), BigDecimal.ROUND_HALF_EVEN);
            Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_PIC_ALPHA, i);
            this.mNotiPicAlpha.setSummary(k + " " + mNotiPicAlpha.getUnits());
            return true;
        }

        /* For changes in color picker prefs... */
        if (paramPreference == this.mNotificationColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), NOTIFICATION_BACKGROUND_COLOR, (Integer) paramObject);
            t = mNotificationColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mNotificationColor);
            return true;
        }

        if (paramPreference == this.mDateClockColor) {
            Settings.Global.putInt(getActivity().getContentResolver(), DATECLOCK_COLOR, (Integer) paramObject);
            t = mDateClockColor.getPrefFlag();
            if (!TextUtils.isEmpty(t)) {
                m = Settings.Global.getInt(getActivity().getContentResolver(), t, 1);
                m *= -1;
                Settings.Global.putInt(getActivity().getContentResolver(), t, m);
            }
            setPrefSummaryColor(mDateClockColor);
            return true;
        }
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mHideNoNoti) {
            if (this.mHideNoNoti.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_NONOTI, i);
            return true;
        }

        if (paramPreference == this.mHideOngoing) {
            if (this.mHideOngoing.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_ONGOING_BLOCK, i);
            return true;
        }

        if (paramPreference == this.mMoveClear) {
            if (this.mMoveClear.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_AOSP_CLEAR, i);
            return true;
        }

        if (paramPreference == this.mHideLatest) {
            if (this.mHideLatest.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HIDE_LATEST, i);
            return true;
        }

        if (paramPreference == this.mNotiAnimHandle) {
            if (this.mNotiAnimHandle.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STATUSBAR_HANDLE_ANIM, i);
            return true;
        }

        if (paramPreference == this.mCustomDateViewApp) {
            if (this.mCustomDateViewApp.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, USE_CUSTOM_DATE, i);
            return true;
        }

        if (paramPreference == this.mNotiPic) {
            if (this.mNotiPic.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, NOTIFICATION_IMAGE, i);
            return true;
        }

        /* For app picker preferences */
        if (paramPreference == this.mDateViewApp) {
            pickShort(mDateViewApp);
            return true;
        }

        if (paramPreference == this.mNotiPicPicker) {
            pickPic(mNotiPicPicker);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotiBackPrefs();
        setListSummary(null);
        setCustomAppSummary(null);
        setPrefSummaryColor(null);
    }

    void processShortcut(Intent paramIntent, int appInt, int shortcutInt) {
        String str = paramIntent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

        if (!TextUtils.isEmpty(str)) {
            if (str.equals(getString(R.string.group_applications))) {
                Intent localIntent1 = new Intent(Intent.ACTION_MAIN, null);
                localIntent1.addCategory(Intent.CATEGORY_LAUNCHER);
                Intent localIntent2 = new Intent(Intent.ACTION_PICK_ACTIVITY);
                localIntent2.putExtra(Intent.EXTRA_INTENT, localIntent1);
                localIntent2.putExtra(Intent.EXTRA_TITLE, getString(R.string.select_custom_apps_title));
                startActivityForResult(localIntent2, appInt);
                return;
            }

            if (str.equals(getString(R.string.app_name)) || str.equals(getString(R.string.screen_off)) || str.equals(getString(R.string.powermenu)) || str.equals(getString(R.string.donothing))) {
                completeSetCustom(str);
                return;
            }
        }

        startActivityForResult(paramIntent, shortcutInt);
    }

    public void shortcutPicked(String paramURI, String paramFriendlyName, boolean paramIsApp)
    {
        int i;
        switch (this.mTargetPref) {
            case 1:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_DATE_VIEW, paramURI)) {
                    this.mDateViewApp.setSummary(paramFriendlyName);
                }
                break;

            case 2:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_PIC_BG, paramURI)) {
                    this.mNotiPicPicker.setSummary(paramFriendlyName);
                    i = Settings.Global.getInt(getActivity().getContentResolver(), CUSTOM_PIC_SWITCH, 1);
                    i *= -1;
                    Settings.Global.putInt(getActivity().getContentResolver(), CUSTOM_PIC_SWITCH, i);
                }
                break;

            default:
                Toast.makeText(getActivity(), "Error... lost initiating preference...", Toast.LENGTH_SHORT).show();
                break;
        }
        this.mTargetPref = 0;
    }

}
