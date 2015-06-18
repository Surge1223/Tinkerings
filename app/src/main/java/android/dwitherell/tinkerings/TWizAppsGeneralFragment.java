package android.dwitherell.tinkerings;

/**
 * Created by devonwitherell on 6/13/2015.
 */

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import java.net.URISyntaxException;
import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

public class TWizAppsGeneralFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String CALLRECORDSHOW = "callrecordshow";
    private static final String CONTACTS_CALL_ICON = "contacts_call_icon";
    private static final String CUSTOM_UPSWIPE_APP = "custom_upswipe_app";
    private static final String DISCONNECTSCREEN = "disconnectscreen";
    private static final String GRID_LAYOUT_UI = "grid_layout_ui";
    private static final String SECURITY_EXCHANGE_SKIP = "security_exchange_skip";
    private static final String STEADYRING = "steadyring";
    private static final String STOCK_LAUNCHER_LOOP = "stock_launcher_loop";
    private static final String STOCK_LAUNCHER_SCROLL = "stock_launcher_scroll";
    private static final String STOCK_LAUNCHER_UPSWIPE = "stock_launcher_upswipe";
    private static final String CUSTOM_DONOTHING_INTENT = "donothingintent";
    private static final String CUSTOM_POWERMENU_INTENT = "powermenuintent";
    private static final String CUSTOM_SCREENOFF_INTENT = "screenoffintent";
    private static final int REQUEST_CREATE_SHORTCUT = 3;
    private static final int REQUEST_PICK_APPLICATION = 2;
    private static final int REQUEST_PICK_SHORTCUT = 1;

    Context context;
    private CheckBoxPreference mCallRecord;
    private CheckBoxPreference mContactCall;
    private CheckBoxPreference mDisconnectScreen;
    private CheckBoxPreference mGridSettingsLayout;
    private CheckBoxPreference mHomeUpswipe;
    private CheckBoxPreference mScrollLoop;
    private CheckBoxPreference mScrollWall;
    private CheckBoxPreference mSecExchangeSkip;
    private CheckBoxPreference mSteadyRing;
    private Preference mUpswipeApp;
    private int mTargetPref = 0;

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
        if (paramPreference == this.mUpswipeApp) {
            mTargetPref = 1;
        }
        startActivityForResult(localIntent, REQUEST_PICK_SHORTCUT);
    }

    private void setCustomAppSummary(Preference appPickPref) {
        String str1;
        String str2;

        if ((appPickPref == null) || (appPickPref == this.mUpswipeApp)) {
            str1 = Settings.Global.getString(getActivity().getContentResolver(), CUSTOM_UPSWIPE_APP);
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
            this.mUpswipeApp.setSummary(str2);
            if (appPickPref != null) {
                return;
            }
        }

    }

    private void resetStuff(String paramPackageString) {
        String str = "pkill -TERM -f " + paramPackageString;
        Shell.SU.run(str);
    }

    private void switchEnabledPackages(String paramClassOn, String paramClassOff)
    {
        PackageManager localPackageManager = getActivity().getPackageManager();
        ComponentName localComponentName1 = new ComponentName("com.android.settings", paramClassOn);
        ComponentName localComponentName2 = new ComponentName("com.android.settings", paramClassOff);
        localPackageManager.setComponentEnabledSetting(localComponentName1, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
        localPackageManager.setComponentEnabledSetting(localComponentName2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);

        // Starts up the one that was recently enabled...
        Intent localIntent = new Intent(Intent.ACTION_VIEW);
        localIntent.setComponent(localComponentName1);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getActivity().startActivity(localIntent);
    }

    public TWizAppsGeneralFragment(){}

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
            case 2:
                completeSetCustomApp(paramData);
                return;
            case 3:
                completeSetCustomShortcut(paramData);
                return;
            default:
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Load the preferences from an XML resource */
        addPreferencesFromResource(R.xml.twizapps_general_fragment);
        this.context = getActivity().getApplicationContext();
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Set up checkbox stuff */
        this.mCallRecord = ((CheckBoxPreference) localPreferenceScreen.findPreference(CALLRECORDSHOW));
        if (Settings.Global.getInt(localContentResolver, CALLRECORDSHOW, 0) != 0) {
            mCallRecord.setChecked(true);
        } else {
            mCallRecord.setChecked(false);
        }

        this.mContactCall = ((CheckBoxPreference) localPreferenceScreen.findPreference(CONTACTS_CALL_ICON));
        if (Settings.Global.getInt(localContentResolver, CONTACTS_CALL_ICON, 0) != 0) {
            mContactCall.setChecked(true);
        } else {
            mContactCall.setChecked(false);
        }

        this.mDisconnectScreen = ((CheckBoxPreference) localPreferenceScreen.findPreference(DISCONNECTSCREEN));
        if (Settings.Global.getInt(localContentResolver, DISCONNECTSCREEN, 0) != 0) {
            mDisconnectScreen.setChecked(true);
        } else {
            mDisconnectScreen.setChecked(false);
        }

        this.mGridSettingsLayout = ((CheckBoxPreference) localPreferenceScreen.findPreference(GRID_LAYOUT_UI));
        if (Settings.Global.getInt(localContentResolver, GRID_LAYOUT_UI, 0) != 0) {
            mGridSettingsLayout.setChecked(true);
        } else {
            mGridSettingsLayout.setChecked(false);
        }

        this.mHomeUpswipe = ((CheckBoxPreference) localPreferenceScreen.findPreference(STOCK_LAUNCHER_UPSWIPE));
        if (Settings.Global.getInt(localContentResolver, STOCK_LAUNCHER_UPSWIPE, 0) != 0) {
            mHomeUpswipe.setChecked(true);
        } else {
            mHomeUpswipe.setChecked(false);
        }

        this.mScrollLoop = ((CheckBoxPreference) localPreferenceScreen.findPreference(STOCK_LAUNCHER_LOOP));
        if (Settings.Global.getInt(localContentResolver, STOCK_LAUNCHER_LOOP, 1) != 0) {
            mScrollLoop.setChecked(true);
        } else {
            mScrollLoop.setChecked(false);
        }

        this.mScrollWall = ((CheckBoxPreference) localPreferenceScreen.findPreference(STOCK_LAUNCHER_SCROLL));
        if (Settings.Global.getInt(localContentResolver, STOCK_LAUNCHER_SCROLL, 0) != 0) {
            mScrollWall.setChecked(true);
        } else {
            mScrollWall.setChecked(false);
        }

        this.mSecExchangeSkip = ((CheckBoxPreference) localPreferenceScreen.findPreference(SECURITY_EXCHANGE_SKIP));
        if (Settings.Global.getInt(localContentResolver, SECURITY_EXCHANGE_SKIP, 0) != 0) {
            mSecExchangeSkip.setChecked(true);
        } else {
            mSecExchangeSkip.setChecked(false);
        }

        this.mSteadyRing = ((CheckBoxPreference) localPreferenceScreen.findPreference(STEADYRING));
        if (Settings.Global.getInt(localContentResolver, STEADYRING, 0) != 0) {
            mSteadyRing.setChecked(true);
        } else {
            mSteadyRing.setChecked(false);
        }

        /* Here are for preferences related to app picking */
        this.mUpswipeApp = (localPreferenceScreen.findPreference(CUSTOM_UPSWIPE_APP));

        setCustomAppSummary(null);
    }

    @Override
    public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, @NonNull Preference paramPreference) {
        int i;
        String str1;
        String str2;
        ContentResolver localContentResolver = getActivity().getContentResolver();

        /* Deal with checkbox stuff first */
        if (paramPreference == this.mCallRecord) {
            if (this.mCallRecord.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CALLRECORDSHOW, i);
            resetStuff("com.android.incallui");
            return true;
        }

        if (paramPreference == this.mContactCall) {
            if (this.mContactCall.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, CONTACTS_CALL_ICON, i);
            resetStuff("com.android.contacts");
            return true;
        }

        if (paramPreference == this.mDisconnectScreen) {
            if (this.mDisconnectScreen.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, DISCONNECTSCREEN, i);
            return true;
        }

        if (paramPreference == this.mGridSettingsLayout) {
            if (this.mGridSettingsLayout.isChecked()) {
                i = 1;
                str1 = "com.android.settings.GridSettings";
                str2 = "com.android.settings.Settings";
            } else {
                i = 0;
                str1 = "com.android.settings.Settings";
                str2 = "com.android.settings.GridSettings";
            }
            Settings.Global.putInt(localContentResolver, GRID_LAYOUT_UI, i);
            switchEnabledPackages(str1, str2);
            return true;
        }

        if (paramPreference == this.mHomeUpswipe) {
            if (this.mHomeUpswipe.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STOCK_LAUNCHER_UPSWIPE, i);
            return true;
        }

        if (paramPreference == this.mScrollLoop) {
            if (this.mScrollLoop.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STOCK_LAUNCHER_LOOP, i);
            return true;
        }

        if (paramPreference == this.mScrollWall) {
            if (this.mScrollWall.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STOCK_LAUNCHER_SCROLL, i);
            return true;
        }

        if (paramPreference == this.mSecExchangeSkip) {
            if (this.mSecExchangeSkip.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, SECURITY_EXCHANGE_SKIP, i);
            resetStuff("com.android.email");
            return true;
        }

        if (paramPreference == this.mSteadyRing) {
            if (this.mSteadyRing.isChecked()) {
                i = 1;
            } else {
                i = 0;
            }
            Settings.Global.putInt(localContentResolver, STEADYRING, i);
            return true;
        }

        /* For app picker preferences */
        if (paramPreference == this.mUpswipeApp)
        {
            pickShort(paramPreference);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
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
                if (Settings.Global.putString(getActivity().getContentResolver(), CUSTOM_UPSWIPE_APP, paramURI)) {
                    this.mUpswipeApp.setSummary(paramFriendlyName);
                }
                break;

            default:
                Toast.makeText(getActivity(), "Error... lost initiating preference...", Toast.LENGTH_SHORT).show();
                break;
        }
        this.mTargetPref = 0;
    }

}
