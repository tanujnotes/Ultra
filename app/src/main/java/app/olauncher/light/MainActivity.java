package app.olauncher.light;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

public class MainActivity extends Activity {
    private final List<AppModel> appList = new ArrayList<>();
    private LinearLayout homeAppsLayout;
    private EditText search;
    private View appDrawer;

    public interface AppClickListener {
        void appClicked(AppModel appModel);
    }

    @Override
    public void onBackPressed() {
        if (appDrawer.getVisibility() == View.VISIBLE) {
            appDrawer.setVisibility(View.GONE);
            homeAppsLayout.setVisibility(View.VISIBLE);
        } else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(FLAG_LAYOUT_NO_LIMITS);
        findViewById(R.id.layout_main).setOnTouchListener(getSwipeGestureListener(this));

        search = findViewById(R.id.search);
        homeAppsLayout = findViewById(R.id.home_apps_layout);
        appDrawer = findViewById(R.id.app_drawer_layout);

        AppAdapter appAdapter = new AppAdapter(this, appList, this::launchApp);
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

    private void showAppList() {
        search.setText("");
        homeAppsLayout.setVisibility(View.GONE);
        appDrawer.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"WrongConstant", "PrivateApi"})
    private void expandNotificationDrawer() {
        try {
            Object statusBarService = getSystemService("statusbar");
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method method = statusBarManager.getMethod("expandNotificationsPanel");
            method.invoke(statusBarService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchApp(AppModel appModel) {
        LauncherApps launcher = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        List<LauncherActivityInfo> appLaunchActivityList = launcher.getActivityList(appModel.appPackage, appModel.userHandle);
        ComponentName componentName;

        switch (appLaunchActivityList.size()) {
            case 0:
                Toast.makeText(this, "App not found", Toast.LENGTH_SHORT).show();
                return;
            case 1:
                componentName = new ComponentName(appModel.appPackage, appLaunchActivityList.get(0).getName());
                break;
            default:
                componentName = new ComponentName(
                        appModel.appPackage, appLaunchActivityList.get(appLaunchActivityList.size() - 1).getName());
                break;
        }

        try {
            launcher.startMainActivity(componentName, appModel.userHandle, null, null);
        } catch (SecurityException securityException) {
            launcher.startMainActivity(componentName, android.os.Process.myUserHandle(), null, null);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to launch app", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnTouchListener getSwipeGestureListener(Context context) {
        return new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(intent);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                showAppList();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                expandNotificationDrawer();
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onLongClick() {
                if (homeAppsLayout.getGravity() == Gravity.CENTER)
                    homeAppsLayout.setGravity(Gravity.START);
                else homeAppsLayout.setGravity(Gravity.CENTER);
                super.onLongClick();
            }

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
            }

            @Override
            public void onTripleClick() {
                super.onTripleClick();
            }
        };
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

    static class AppAdapter extends ArrayAdapter<AppModel> {

        private final AppClickListener appClickListener;

        private static class ViewHolder {
            TextView appName;
            View indicator;
        }

        public AppAdapter(Context context, List<AppModel> apps, AppClickListener appClickListener) {
            super(context, 0, apps);
            this.appClickListener = appClickListener;
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

            viewHolder.appName.setTag(appModel);
            viewHolder.appName.setText(appModel.appLabel);
            viewHolder.appName.setOnClickListener(view -> {
                AppModel clickedAppModel = (AppModel) viewHolder.appName.getTag();
                appClickListener.appClicked(clickedAppModel);
            });
            if (appModel.userHandle == android.os.Process.myUserHandle())
                viewHolder.indicator.setVisibility(View.GONE);
            else viewHolder.indicator.setVisibility(View.VISIBLE);
            return convertView;
        }
    }
}