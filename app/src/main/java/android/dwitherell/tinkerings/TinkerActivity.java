package android.dwitherell.tinkerings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.res.TypedArray;
import android.dwitherell.tinkerings.utils.NavDrawerItem;
import android.dwitherell.tinkerings.utils.NavDrawerListAdapter;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import eu.chainfire.libsuperuser.Shell;

public class TinkerActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    public static String mPackageName;
    private FragmentManager fragmentManager;
    public static final String EXTRA_START_FRAGMENT = "android.dwitherell.tinkerings.EXTRA_START_FRAGMENT";
    // this allows first entries in stringarray to be skipped from navdrawer/widget
    public static int FRAG_ARRAY_START = 5;

    public static int LAST_STAT_BAR_TAB;
    public static int LAST_TWIZ_APP_TAB;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title/position
    private CharSequence mTitle;
    private int mItemPosition;

    // for backstack tracking
    private Stack<String> fragmentStack;

    // various bools for this or that
    private boolean mBackPress;
    private boolean mIgnoreBack;
    private boolean mFromClick;
    private boolean mIgnore;
    private boolean mMenu;
    private boolean fullyClosed;
    private boolean openingHalf;

    // slide menu items
    private String[] navMenuTitles;
    private String[] navMenuFrags;

    // info for buildprop editor
    public static String mEditName;
    public static String mEditKey;

    // info for app picker
    public static String mPrefKey;
    public static int mTitleArray;
    public static int mIconArray;
    public static int mKeyArray;

    // For handling quick back/About presses
    Handler myHandler = new Handler();

    private class Startup extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;

        public Startup setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // The progress dialog here is so the user will wait until root stuff is figured out.

            dialog = new ProgressDialog(context);
            dialog.setTitle("Hold on a sec");
            dialog.setMessage("Trying to find root...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Let's check to see if SU is there...
            suAvailable = Shell.SU.available();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();

            // output
            if (!suAvailable) {
                String str = "Root not available?!? Some functions may not work as intended!";
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = mDrawerTitle = getTitle();

        mPackageName = getPackageName();
        LAST_STAT_BAR_TAB = 0;
        LAST_TWIZ_APP_TAB = 0;

        mBackPress = false;
        mIgnoreBack = false;
        mFromClick = false;
        mMenu = false;
        fullyClosed = true;
        openingHalf = true;

        fragmentStack = new Stack<String>();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuFrags = getResources().getStringArray(R.array.nav_drawer_fragments);

        // nav drawer icons from resources
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        for (int i=FRAG_ARRAY_START; i < navMenuTitles.length; i++) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            @Override
            public void onDrawerClosed(View view) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mDrawerList);
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                openingHalf = true;
                invalidateOptionsMenu();
                // now that the drawer animation is done - load fragment
                if (mIgnore || !mFromClick ) {
                    mIgnore = false;
                } else {
                    displayView(mItemPosition);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                openingHalf = false;
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                fullyClosed = (slideOffset == 0.0f);
                if (slideOffset < 0.5f && !openingHalf) {
                    openingHalf = true;
                    invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                } else if (slideOffset > 0.5f && openingHalf) {
                    openingHalf = false;
                    invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            }

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        fragmentManager = getFragmentManager();

        // check for root stuff
        (new Startup()).setContext(this).execute();

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(mItemPosition = getIntent().getIntExtra(EXTRA_START_FRAGMENT, 0));
        }
    }

    /* Slide menu item click listener */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // check for change in position, if changed set selected/title remove frag then close drawer
            int origposition = position;
            position += FRAG_ARRAY_START;
            if (mItemPosition == position) {
                mIgnore = true;
            } else {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawerList);
                mFromClick = true;
                mDrawerList.setItemChecked(origposition, true);
                mDrawerList.setSelection(origposition);
                mItemPosition = position;
                setTitle(navMenuTitles[position]);
                removeCurrent();
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /* Displaying fragment view for selected nav drawer list item */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment frags = null;
        String test = navMenuFrags[position];
        try {
            frags = (Fragment)Class.forName(mPackageName + "." + test).newInstance();
            }
        catch (Exception e) {
            frags = null;
            }
        if (frags != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            FragmentTransaction fragtrans = fragmentManager.beginTransaction();
            if (mFromClick || mMenu || mBackPress) {
                fragtrans.setCustomAnimations(R.anim.fadein, R.anim.fadeout, R.anim.fadein, R.anim.fadeout);
            }
            fragtrans.add(R.id.frame_container, frags);
            // add fragment name to custom stack for backstack tracking
            if (!mBackPress && position != 3 && position != 4) { //Avoid adding EditProp or AppPicker fragment to stack
                fragmentStack.push(navMenuFrags[position]);
            }
            fragtrans.commit();

            // update selected item and title, then close the drawer
            if (mFromClick || mBackPress) {
                mFromClick = false;
                mBackPress = false;
            } else {
                if (position < FRAG_ARRAY_START) {
                    mDrawerList.clearChoices();
                    for (int i = 0; i < mDrawerList.getCount(); i++)
                        mDrawerList.setItemChecked(i, false);
                } else {
                    // only stuff that makes it in here stems from widget or back stack
                    mDrawerList.setItemChecked(position - FRAG_ARRAY_START, true);
                    mDrawerList.setSelection(position - FRAG_ARRAY_START);
                }
                setTitle(navMenuTitles[position]);
                if (mMenu) {
                    mMenu = false;
                    mItemPosition = position;
                } else {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
            invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
        } else {
            // error in creating fragment
            Log.e("TinkerActivity", "Error in creating fragment");
        }
    }

    private void removeCurrent() {
        // update the main content by replacing fragments, first by removing the old
        FragmentTransaction fragtrans = fragmentManager.beginTransaction();
        fragtrans.setCustomAnimations(R.anim.fadein,R.anim.fadeout,R.anim.fadein,R.anim.fadeout);
        fragtrans.remove(fragmentManager.findFragmentById(R.id.frame_container));
        fragtrans.commit();
    }

    private void backup() {
        try {
            Shell.SU.run("cp -f /system/build.prop " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak");
            Toast.makeText(getApplicationContext(), "build.prop Backup at " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/build.prop.bak", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void displayBuildPropEditor() {
        myHandler.removeCallbacksAndMessages(null);
        mMenu = true;
        removeCurrent();
        // below replicates the visual delay seen when launching frags from navdrawer
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayView(2);
            }
        }, 400);
    }

    public void displayAppPicker(Preference object, int titles, int icons, int keys) {
        // stuff for apppicker fragment
        mPrefKey = object.getKey();
        mTitleArray = titles;
        mIconArray = icons;
        mKeyArray = keys;

        myHandler.removeCallbacksAndMessages(null);
        mMenu = true;
        removeCurrent();
        // below replicates the visual delay seen when launching frags from navdrawer
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayView(4);
            }
        }, 400);
    }

    public void displayEditProp(String name, String key) {
        //put the name and key strings in here for editprop access
        mEditName = name;
        mEditKey = key;

        myHandler.removeCallbacksAndMessages(null);
        mMenu = true;
        removeCurrent();
        // below replicates the visual delay seen when launching frags from navdrawer
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //edit w/ appropriate number
                displayView(3);
            }
        }, 400);
    }

    public void displayExtra() {
        myHandler.removeCallbacksAndMessages(null);
        mMenu = true;
        removeCurrent();
        // below replicates the visual delay seen when launching frags from navdrawer
        myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayView(1);
                }
            }, 400);
    }

    @Override
    public void onBackPressed() {
        if (!fullyClosed) {
            // backpress closes drawer if open
            mDrawerLayout.closeDrawer(mDrawerList);
        } else if (fragmentStack.size() > 1) {
            if (!mIgnoreBack) {
                mIgnoreBack = true;

                // cancels any pending postdelays just in case
                myHandler.removeCallbacksAndMessages(null);

                // removes latest (current) entry in custom stack
                if (mItemPosition != 3 && mItemPosition != 4) { //but not for editprop or apppicker
                    fragmentStack.pop();
                }
                // uses fragment name to find displayview-relevant position
                final int position = Arrays.asList(navMenuFrags).indexOf(fragmentStack.lastElement());
                // a setup similar to onclickitem
                mDrawerList.setItemChecked(position - FRAG_ARRAY_START, true);
                mDrawerList.setSelection(position - FRAG_ARRAY_START);
                mItemPosition = position;
                setTitle(navMenuTitles[position]);
                removeCurrent();
                // below replicates the visual delay seen when launching frags from navdrawer
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBackPress = true;
                        displayView(position);
                        mIgnoreBack = false;
                    }
                }, 400);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tinker, menu);
        menu.findItem(R.id.action_launchhide).setChecked(!isLauncherIconEnabled());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_about:
                if ( mItemPosition != 0 ) {
                    myHandler.removeCallbacksAndMessages(null);
                    mMenu = true;
                    removeCurrent();
                    // below replicates the visual delay seen when launching frags from navdrawer
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            displayView(0);
                        }
                    }, 400);
                }
                return true;
            case R.id.action_launchhide:
                boolean checked = item.isChecked();
                item.setChecked(!checked);
                setLauncherIconEnabled(checked);
                return true;
            case R.id.action_add:
                displayEditProp(null, null);
                return true;
            case R.id.action_backup:
                backup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called when invalidateOptionsMenu() is triggered */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened/opening, hide the action items
        // add in bits to enable/disable menu items that are fragment specific
        if ( openingHalf ) {
            menu.setGroupVisible(R.id.action_items, true);
            boolean isbuildprop = (mItemPosition == 2);
            boolean iseditprop = (mItemPosition == 3);
            boolean isapppicker = (mItemPosition == 4);
            menu.findItem(R.id.action_add).setVisible(isbuildprop);
            menu.findItem(R.id.action_backup).setVisible(isbuildprop);
            menu.findItem(R.id.action_restore).setVisible(isbuildprop);
            menu.findItem(R.id.action_save).setVisible(iseditprop);
            menu.findItem(R.id.action_discard).setVisible(iseditprop);
            menu.findItem(R.id.action_delete).setVisible(iseditprop);
            menu.findItem(R.id.action_launchhide).setVisible(!(isbuildprop || iseditprop || isapppicker));
            menu.findItem(R.id.action_about).setVisible(!(isbuildprop || iseditprop || isapppicker));
        } else {
            menu.setGroupVisible(R.id.action_items, false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /* When using the mDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged()... */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Methods related to showing/hiding app from app drawer */
    public void setLauncherIconEnabled(boolean enabled) {
        int newState;
        PackageManager packman = getPackageManager();
        if (enabled) {
            newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        } else {
            newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        }
        packman.setComponentEnabledSetting(new ComponentName(this, LauncherActivity.class), newState, PackageManager.DONT_KILL_APP);
    }

    public boolean isLauncherIconEnabled() {
        PackageManager packman = getPackageManager();
        return (packman.getComponentEnabledSetting(new ComponentName(this, LauncherActivity.class)) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }
}