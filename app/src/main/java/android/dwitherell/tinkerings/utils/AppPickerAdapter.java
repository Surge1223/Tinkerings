package android.dwitherell.tinkerings.utils;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.dwitherell.tinkerings.R;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by devonwitherell on 6/23/2015.
 */
public class AppPickerAdapter extends BaseAdapter {

    List<ApplicationInfo> packageList;
    List<ResolveInfo> shortcutList;
    Activity context;
    PackageManager packageManager;
    int extratitle;
    int extraicon;
    int xtracount;

    public AppPickerAdapter(Activity context, PackageManager packageManager, List<ApplicationInfo> packageList, List<ResolveInfo> shortcutList, int titles, int icons) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.shortcutList = shortcutList;
        this.packageManager = packageManager;
        if (titles != 0 && icons != 0) {
            extratitle = titles;
            extraicon = icons;
            TypedArray a = context.getResources().obtainTypedArray(titles);
            this.xtracount = a.length();
            a.recycle();
        } else {
            this.extratitle = 0;
            this.extraicon = 0;
            this.xtracount = 0;
        }
    }

    private class ViewHolder {
        TextView apkName;
    }

    public int getCount() {
        if (packageList != null) {
            return packageList.size();
        } else if (shortcutList != null) {
            return shortcutList.size();
        } else {
            return xtracount;
        }
    }

    public Object getItem(int position) {
        if (packageList != null) {
            return packageList.get(position);
        } else if (shortcutList != null) {
            return shortcutList.get(position);
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Drawable appIcon = null;
        String appName = null;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_app_item, parent, false);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView.findViewById(R.id.appname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (packageList != null) {
            ApplicationInfo appInfo = (ApplicationInfo) getItem(position);
            appIcon = packageManager.getApplicationIcon(appInfo);
            appName = packageManager.getApplicationLabel(appInfo).toString();
        } else if (shortcutList != null) {
            ResolveInfo shortInfo = (ResolveInfo) getItem(position);
            appIcon = shortInfo.loadIcon(packageManager);
            appName = shortInfo.loadLabel(packageManager).toString();
        } else if (xtracount > 0) {
            TypedArray title = context.getResources().obtainTypedArray(extratitle);
            TypedArray icon = context.getResources().obtainTypedArray(extraicon);

            appName = context.getString(title.getResourceId(position, -1));
            appIcon = context.getDrawable(icon.getResourceId(position, -1));

            icon.recycle();
            title.recycle();
        }

        if (appIcon != null) {
            int bound = (int)(context.getResources().getDimension(R.dimen.apppicker_listview_height) - (5 * context.getResources().getDisplayMetrics().density));
            appIcon.setBounds(0, 0, bound, bound); //try to shave a bit off the bound to appear padded
        }
        holder.apkName.setCompoundDrawables(appIcon, null, null, null);
        holder.apkName.setCompoundDrawablePadding((int)(10 * context.getResources().getDisplayMetrics().density));
        holder.apkName.setText(appName);

        return convertView;
    }
}

