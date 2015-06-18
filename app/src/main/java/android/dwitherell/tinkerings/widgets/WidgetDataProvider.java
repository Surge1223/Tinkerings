package android.dwitherell.tinkerings.widgets;

/**
 * Created by devonwitherell on 1/5/2015.
 */

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.dwitherell.tinkerings.R;
import android.dwitherell.tinkerings.TinkerActivity;
import android.dwitherell.tinkerings.utils.NavDrawerItem;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class WidgetDataProvider implements RemoteViewsFactory {
    ArrayList<NavDrawerItem> mCollections = new ArrayList<>();
    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        mView.setTextViewText(R.id.title, mCollections.get(position).getTitle());
        mView.setImageViewResource(R.id.icon, mCollections.get(position).getIcon());

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(WidgetProvider.FRAG_START);

        int temp = position + TinkerActivity.FRAG_ARRAY_START;
        //position += TinkerActivity.FRAG_ARRAY_START;

        final Bundle bundle = new Bundle();
        bundle.putInt(WidgetProvider.EXTRA_STRING, temp);
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(R.id.backgroundImage, fillInIntent);

        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    private void initData() {
        mCollections.clear();

        String[] navMenuTitles = mContext.getResources().getStringArray(R.array.nav_drawer_items);
//        String[] navMenuFrags = mContext.getResources().getStringArray(R.array.nav_drawer_fragments);

        // nav drawer icons from resources
        TypedArray navMenuIcons = mContext.getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        // adding nav drawer items to array
        // start at i=FRAG_ARRAY_START to skip items not in the navdrawer
        for (int i= TinkerActivity.FRAG_ARRAY_START; i < navMenuTitles.length; i++) {
            mCollections.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        navMenuIcons.recycle();
    }

    @Override
    public void onDestroy() {
    }
}
