package android.dwitherell.tinkerings;

import android.dwitherell.tinkerings.utils.SlidingTabLayout;
import android.dwitherell.tinkerings.utils.ViewPagerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;

/**
 * Created by devonwitherell on 6/11/2015.
 */
public class TWizAppsFragment extends PreferenceFragment {

    ViewPager pager;
    SlidingTabLayout slidingTabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.slidetab, container, false);

        // slide menu items
        String[] tabTitles = getResources().getStringArray(R.array.twizapps_tab_items);
        String[] tabFrags = getResources().getStringArray(R.array.twizapps_tab_fragments);

        pager = (ViewPager) v.findViewById(R.id.viewpager);
        slidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setInitFrag(this);

        pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), tabTitles, tabFrags));

        slidingTabLayout.setViewPager(pager);

        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setDistributeEvenly(false);

        // go to last tab that was opened
        pager.setCurrentItem(TinkerActivity.LAST_TWIZ_APP_TAB);

        return v;
    }
}
