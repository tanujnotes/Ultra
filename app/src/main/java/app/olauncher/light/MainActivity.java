package app.olauncher.light;


import android.app.Activity;
import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class MainActivity extends Activity {
    private List<AppModel> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(FLAG_LAYOUT_NO_LIMITS);

        AppAdapter appAdapter = new AppAdapter(this, appList);
        ListView appListView = findViewById(R.id.app_list_view);
        appListView.setAdapter(appAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAppsList();
    }


    private void getAppsList() {
        appList.clear();
        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        for (UserHandle profile : userManager.getUserProfiles()) {
            for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile)) {
                appList.add(new AppModel(
                        activityInfo.getLabel().toString(),
                        activityInfo.getApplicationInfo().packageName,
                        profile));
            }
        }
    }

    static class AppModel {
        String appLabel;
        String appPackage;
        UserHandle userHandle;

        public AppModel(String appLabel, String appPackage, UserHandle userHandle) {
            this.appLabel = appLabel;
            this.appPackage = appPackage;
            this.userHandle = userHandle;
        }
    }

    public static class AppAdapter extends ArrayAdapter<AppModel> {

        private static class ViewHolder {
            TextView appName;
            View indicator;
        }

        public AppAdapter(Context context, List<AppModel> apps) {
            super(context, 0, apps);
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            AppModel appModel = getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.adapter_app, parent, false);
                viewHolder.appName = convertView.findViewById(R.id.app_name);
                viewHolder.indicator = convertView.findViewById(R.id.other_profile_indicator);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.appName.setText(appModel.appLabel);
            if (appModel.userHandle == android.os.Process.myUserHandle())
                viewHolder.indicator.setVisibility(View.GONE);
            else viewHolder.indicator.setVisibility(View.VISIBLE);
            return convertView;
        }
    }
}