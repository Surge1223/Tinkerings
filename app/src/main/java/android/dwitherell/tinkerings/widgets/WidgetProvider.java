package android.dwitherell.tinkerings.widgets;

/**
 * Created by devonwitherell on 1/5/2015.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.dwitherell.tinkerings.TinkerActivity;
import android.net.Uri;
import android.widget.RemoteViews;

import android.dwitherell.tinkerings.R;

public class WidgetProvider extends AppWidgetProvider {

    public static final String FRAG_START = "android.dwitherell.tinkerings.ACTION_CUSTOM";
    public static final String EXTRA_STRING = "android.dwitherell.tinkerings.EXTRA_STRING";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(FRAG_START)) {
            final Intent fragintent = new Intent(context, TinkerActivity.class);
            int frag = intent.getExtras().getInt(EXTRA_STRING);
            fragintent.putExtra(TinkerActivity.EXTRA_START_FRAGMENT, frag);
            fragintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fragintent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews mView = initViews(context, appWidgetManager, widgetId);

            // Adding collection list item handler
            final Intent onItemClick = new Intent(context, WidgetProvider.class);
            onItemClick.setAction(FRAG_START);
            onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setPendingIntentTemplate(R.id.widgetCollectionList, onClickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, mView);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews initViews(Context context,
                                  AppWidgetManager widgetManager, int widgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

        return mView;
    }
}