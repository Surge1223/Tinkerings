package android.dwitherell.tinkerings;

import android.app.Fragment;
import android.dwitherell.tinkerings.utils.SlidingTabLayout;
import android.dwitherell.tinkerings.utils.ViewPagerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;

/**
 * Created by devonwitherell on 6/11/2015.
 */
public class AppPickerFragment extends Fragment {

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
        String[] tabTitles = getResources().getStringArray(R.array.app_pick_items);
        String[] tabFrags = getResources().getStringArray(R.array.apppick_tab_fragments);

        pager = (ViewPager) v.findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);

        slidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.sliding_tabs);
        slidingTabLayout.setInitFrag(this);

        pager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), tabTitles, tabFrags));

        slidingTabLayout.setViewPager(pager);

        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        slidingTabLayout.setDistributeEvenly(false);

        return v;
    }
}
