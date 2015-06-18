package android.dwitherell.tinkerings;
/**
 * Created by devonwitherell on 12/31/2014.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;
import java.net.URISyntaxException;
import java.util.ArrayList;
import android.support.annotation.NonNull;

public class HardKeysFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    private static final String CUSTOM_DUBHOME_ACTIVITY = "custom_dubhome_activity";
    private static final String CUSTOM_DUBHOME_INTENT = "custom_dubhome_intent";
    private static final String CUSTOM_LONGBACK_ACTIVITY = "custom_longback_activity";
    private static final String CUSTOM_LONGBACK_INTENT = "custom_longback_intent";
    private static final String CUSTOM_LONGHOME_ACTIVITY = "custom_longhome_activity";
    private static final String CUSTOM_LONGHOME_INTENT = "custom_longhome_intent";
    private static final String CUSTOM_LONGMENU_ACTIVITY = "custom_longmenu_activity";
    private static final String CUSTOM_LONGMENU_INTENT = "custom_longmenu_intent";
    private static final String CUSTOM_DONOTHING_INTENT = "donothingintent";
    private static final String CUSTOM_POWERMENU_INTENT = "powermenuintent";
    private static final String CUSTOM_SCREENOFF_INTENT = "screenoffintent";
    private static final String LONGPRESSTYPE = "longpresstype";
    private static final String LONG_PRESS_SKIP = "long_press_skip";
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_PICK_SHORTCUT = 1;
    private static final String STOP_DOUBLE_TAP = "stop_double_tap";
    private static final String VOL_CURSOR_TYPE = "vol_cursor_type";
    private static final String VOL_DOWN_TYPE = "vol_down_type";
    private static final String VOL_UP_TYPE = "vol_up_type";

    private Preference mDoubleHomeAct;
    private CheckBoxPreference mDoubleHomeIntent;
    private Preference mLongBackAct;
    private CheckBoxPreference mLongBackIntent;
    private Preference mLongHomeAct;
    private CheckBoxPreference mLongHomeIntent;
    private Preference mLongMenuAct;
    private CheckBoxPreference mLongMenuIntent;
    private CheckBoxPreference mLongPressSkip;
    private ListPreference mLongPressType;
    private CheckBoxPreference mStopDoubleHome;
    private int mTargetPref = 0;
    private ListPreference mVolCursType;
    private ListPreference mVolDownType;
    private ListPreference mVolUpType;

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
            final Intent fragintent = new Intent(getActivity(), TinkerActivity.class);
            fragintent.putExtra(TinkerActivity.EXTRA_START_FRAGMENT, 0);
            fragintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);

            shortcutPicked(fragintent.toUri(0), getString(R.string.app_name), true);
        }
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

    private void pickShort(Preference paramPreference) {
        Bundle localBundle = new Bundle();
        Context localContext = getActivity();
        ArrayList<String> localArrayList1 = new ArrayList<>();
        localArrayList1.add(getString(R.string.group_applications));
        localArrayList1.add(getString(R.string.app_name));
        localArrayList1.add(getString(R.string.screen_off));
        localArrayList1.add(getString(R.string.powermenu));
        localArrayList1.add(getString(R.string.donothing));

        localBundle.putStringArrayList(Intent.EXTRA_SHORTCUT_NAME, localArrayList1);

        ArrayList<Intent.ShortcutIconResource> localArrayList2 = new ArrayList<>();
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_apps));
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.app_icon));
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_screenoff));
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_tsm_powermenu));
        localArrayList2.add(Intent.ShortcutIconResource.fromContext(localContext, R.drawable.ic_launcher_donothing));

        localBundle.putParcelableArrayList(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, localArrayList2);

        Intent localIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        localIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        localIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.select_custom_short_title));
        localIntent.putExtras(localBundle);

        mTargetPref = 0;
        if (paramPreference == this.mDoubleHomeAct) {
            mTargetPref = 1;
        } else if (paramPreference == this.mLongHomeAct) {
            mTargetPref = 2;
        } else if (paramPreference == this.mLongBackAct) {
            mTargetPref = 3;
        } else if (paramPreference == this.mLongMenuAct) {
            mTargetPref = 4;
        }
        startActivityForResult(localIntent, REQUEST_PICK_SHORTCUT);
    }

    private void setCustomAppSummary(Preference appPickPref) {
        String str1;
        String str2;

        if ((appPickPref == null) || (appPickPref == this.mDoubleHomeAct)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_DUBHOME_ACTIVITY);
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
            this.mDoubleHomeAct.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }

        if ((appPickPref == null) || (appPickPref == this.mLongHomeAct)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_LONGHOME_ACTIVITY);
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
            this.mLongHomeAct.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }

        if ((appPickPref == null) || (appPickPref == this.mLongBackAct)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_LONGBACK_ACTIVITY);
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
            this.mLongBackAct.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }

        if ((appPickPref == null) || (appPickPref == this.mLongMenuAct)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_LONGMENU_ACTIVITY);
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
            this.mLongMenuAct.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }
    }

    private void setListSummary(ListPreference paramListPreference) {
        int i;
        String str;
        String[] strarray;

        if ((paramListPreference == null) || (paramListPreference == this.mLongPressType))
        {
            i = Settings.Global.getInt(getActivity().getContentResolver(), LONGPRESSTYPE, 0);
            strarray = getResources().getStringArray(R.array.longpress_entries);
            str = strarray[i];
            this.mLongPressType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mVolCursType))
        {
            i = Settings.Global.getInt(getActivity().getContentResolver(), VOL_CURSOR_TYPE, 0);
            strarray = getResources().getStringArray(R.array.volcursor_entries);
            str = strarray[i];
            this.mVolCursType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mVolUpType))
        {
            i = Settings.Global.getInt(getActivity().getContentResolver(), VOL_UP_TYPE, 0);
            strarray = getResources().getStringArray(R.array.volup_entries);
            str = strarray[i];
            this.mVolUpType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }

        if ((paramListPreference == null) || (paramListPreference == this.mVolDownType))
        {
            i = Settings.Global.getInt(getActivity().getContentResolver(), VOL_DOWN_TYPE, 1);
            strarray = getResources().getStringArray(R.array.volup_entries);
            str = strarray[i];
            this.mVolDownType.setSummary(str);
            if (paramListPreference != null) {
                return;
            }
        }
    }

    public HardKeysFragment(){}

    public String getFriendlyNameForUri(String paramUri) {
        if (TextUtils.isEmpty(paramUri)) {
            return null;
        }
        try {
            Intent localIntent = Intent.parseUri(paramUri, 0);
            if (Intent.ACTION_MAIN.equals(localIntent.getAction())) {
                return getFriendlyActivityName(localIntent, false);
            }
            if (-1 != localIntent.getExtras().getInt(TinkerActivity.EXTRA_START_FRAGMENT, -1)) {
                return getString(R.string.app_name);
            }
            return getFriendlyShortcutName(localIntent);
        } catch (URISyntaxException localURISyntaxException) {}
        return paramUri;
    }

    @Override
    public void onActivityResult(int paramRequest, int paramResult, Intent paramData) {
        super.onActivityResult(paramRequest, paramResult, paramData);
        if (paramResult != -1) {
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
            default:
        }

    }

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.hardkeys_fragment);

        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();
        int i;

        /* Set up checkbox stuff first */
        this.mDoubleHomeIntent = ((CheckBoxPreference)localPreferenceScreen.findPreference(CUSTOM_DUBHOME_INTENT));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_DUBHOME_INTENT, 0) != 0) {
            mDoubleHomeIntent.setChecked(true);
        } else {
            mDoubleHomeIntent.setChecked(false);
        }

        this.mLongHomeIntent = ((CheckBoxPreference)localPreferenceScreen.findPreference(CUSTOM_LONGHOME_INTENT));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_LONGHOME_INTENT, 0) != 0) {
            mLongHomeIntent.setChecked(true);
        } else {
            mLongHomeIntent.setChecked(false);
        }

        this.mLongPressSkip = ((CheckBoxPreference)localPreferenceScreen.findPreference(LONG_PRESS_SKIP));
        if (Settings.Global.getInt(localContentResolver, LONG_PRESS_SKIP, 0) != 0) {
            mLongPressSkip.setChecked(true);
        } else {
            mLongPressSkip.setChecked(false);
        }

        this.mLongBackIntent = ((CheckBoxPreference)localPreferenceScreen.findPreference(CUSTOM_LONGBACK_INTENT));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_LONGBACK_INTENT, 0) != 0) {
            mLongBackIntent.setChecked(true);
        } else {
            mLongBackIntent.setChecked(false);
        }

        this.mLongMenuIntent = ((CheckBoxPreference)localPreferenceScreen.findPreference(CUSTOM_LONGMENU_INTENT));
        if (Settings.Global.getInt(localContentResolver, CUSTOM_LONGMENU_INTENT, 0) != 0) {
            mLongMenuIntent.setChecked(true);
        } else {
            mLongMenuIntent.setChecked(false);
        }

        this.mStopDoubleHome = ((CheckBoxPreference)localPreferenceScreen.findPreference(STOP_DOUBLE_TAP));
        if (Settings.Global.getInt(localContentResolver, STOP_DOUBLE_TAP, 0) != 0) {
            mStopDoubleHome.setChecked(true);
        } else {
            mStopDoubleHome.setChecked(false);
        }

        /* Here are for list preferences */
        this.mLongPressType = ((ListPreference)localPreferenceScreen.findPreference(LONGPRESSTYPE));
        i = Settings.Global.getInt(localContentResolver, LONGPRESSTYPE, 0);
        this.mLongPressType.setValue(String.valueOf(i));
        this.mLongPressType.setOnPreferenceChangeListener(this);

        this.mVolCursType = ((ListPreference)localPreferenceScreen.findPreference(VOL_CURSOR_TYPE));
        i = Settings.Global.getInt(localContentResolver, VOL_CURSOR_TYPE, 0);
        this.mVolCursType.setValue(String.valueOf(i));
        this.mVolCursType.setOnPreferenceChangeListener(this);

        this.mVolUpType = ((ListPreference)localPreferenceScreen.findPreference(VOL_UP_TYPE));
        i = Settings.Global.getInt(localContentResolver, VOL_UP_TYPE, 0);
        this.mVolUpType.setValue(String.valueOf(i));
        this.mVolUpType.setOnPreferenceChangeListener(this);

        this.mVolDownType = ((ListPreference)localPreferenceScreen.findPreference(VOL_DOWN_TYPE));
        i = Settings.Global.getInt(localContentResolver, VOL_DOWN_TYPE, 1);
        this.mVolDownType.setValue(String.valueOf(i));
        this.mVolDownType.setOnPreferenceChangeListener(this);

        /* Here are for preferences related to app picking */
        this.mDoubleHomeAct = (localPreferenceScreen.findPreference(CUSTOM_DUBHOME_ACTIVITY));
        this.mLongHomeAct = (localPreferenceScreen.findPreference(CUSTOM_LONGHOME_ACTIVITY));
        this.mLongBackAct = (localPreferenceScreen.findPreference(CUSTOM_LONGBACK_ACTIVITY));
        this.mLongMenuAct = (localPreferenceScreen.findPreference(CUSTOM_LONGMENU_ACTIVITY));

        setListSummary(null);
        setCustomAppSummary(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        int i;

        /* Put stuff for list preferences here */
        if (paramPreference == this.mLongPressType)
        {
            i = Integer.valueOf((String)paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), LONGPRESSTYPE, i);
            setListSummary(this.mLongPressType);
            return true;
        }

        if (paramPreference == this.mVolDownType)
        {
            i = Integer.valueOf((String)paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), VOL_DOWN_TYPE, i);
            setListSummary(this.mVolDownType);
            return true;
        }

        if (paramPreference == this.mVolUpType)
        {
            i = Integer.valueOf((String)paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), VOL_UP_TYPE, i);
            setListSummary(this.mVolUpType);
            return true;
        }

        if (paramPreference == this.mVolCursType)
        {
            i = Integer.valueOf((String)paramObject);
            Settings.Global.putInt(getActivity().getContentResolver(), VOL_CURSOR_TYPE, i);
            setListSummary(this.mVolCursType);
            return true;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mDoubleHomeIntent)
        {
            if (this.mDoubleHomeIntent.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_DUBHOME_INTENT, i);
            return true;
        }

        if (paramPreference == this.mLongHomeIntent)
        {
            if (this.mLongHomeIntent.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_LONGHOME_INTENT, i);
            return true;
        }

        if (paramPreference == this.mLongPressSkip)
        {
            if (this.mLongPressSkip.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, LONG_PRESS_SKIP, i);
            return true;
        }

        if (paramPreference == this.mLongBackIntent)
        {
            if (this.mLongBackIntent.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_LONGBACK_INTENT, i);
            return true;
        }

        if (paramPreference == this.mLongMenuIntent)
        {
            if (this.mLongMenuIntent.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CUSTOM_LONGMENU_INTENT, i);
            return true;
        }

        if (paramPreference == this.mStopDoubleHome)
        {
            if (this.mStopDoubleHome.isChecked())
            {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STOP_DOUBLE_TAP, i);
            return true;
        }

        /* For app picker preferences */
        if (paramPreference == this.mDoubleHomeAct)
        {
            pickShort(paramPreference);
            return true;
        }

        if (paramPreference == this.mLongHomeAct)
        {
            pickShort(paramPreference);
            return true;
        }

        if (paramPreference == this.mLongBackAct)
        {
            pickShort(paramPreference);
            return true;
        }

        if (paramPreference == this.mLongMenuAct)
        {
            pickShort(paramPreference);
            return true;
        }

        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setListSummary(null);
        setCustomAppSummary(null);
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
        switch (this.mTargetPref) {
            case 1:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_DUBHOME_ACTIVITY, paramURI)) {
                    this.mDoubleHomeAct.setSummary(paramFriendlyName);
                }
                break;

            case 2:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_LONGHOME_ACTIVITY, paramURI)) {
                    this.mLongHomeAct.setSummary(paramFriendlyName);
                }
                break;

            case 3:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_LONGBACK_ACTIVITY, paramURI)) {
                    this.mLongBackAct.setSummary(paramFriendlyName);
                }
                break;

            case 4:
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_LONGMENU_ACTIVITY, paramURI)) {
                    this.mLongMenuAct.setSummary(paramFriendlyName);
                }
                break;
            default:
                Toast.makeText(getActivity(), "Error... lost initiating preference...", Toast.LENGTH_SHORT).show();
                break;
        }
        this.mTargetPref = 0;
    }
}
