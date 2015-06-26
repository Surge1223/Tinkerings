package android.dwitherell.tinkerings.utils;

/**
 * Created by devonwitherell on 6/11/2015.
 * Heavily borrowed from terkinarslan material sample
 */

import android.app.FragmentManager;
import android.app.Fragment;
import android.dwitherell.tinkerings.TinkerActivity;
import android.support.v13.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String titles[] ;
    private String frags[] ;
    private String mPackageName;

    public ViewPagerAdapter(FragmentManager fm, String[] titles2, String[] frags2) {
        super(fm);
        titles=titles2;
        frags=frags2;
        mPackageName = TinkerActivity.mPackageName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        String test = frags[position];
        try {
            frag = (Fragment)Class.forName(mPackageName + "." + test).newInstance();
        }
        catch (Exception e) {
            frag = null;
        }

        return frag;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return frags.length;
    }

}