package app.olauncher.ultra;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    private final int FLAG_LAUNCH_APP = 0;
    private final List<AppModel> appList = new ArrayList<>();

    private Prefs prefs;
    private View appDrawer;
    private EditText search;
    private ListView appListView;
    private AppAdapter appAdapter;
    private LinearLayout homeAppsLayout;
    private TextView homeApp1, homeApp2, homeApp3, homeApp4, homeApp5, homeApp6, setDefaultLauncher;

    public interface AppClickListener {
        void appClicked(AppModel appModel, int flag);

        void appLongPress(AppModel appModel);
    }

    @Override
    public void onBackPressed() {
        if (appDrawer.getVisibility() == View.VISIBLE) backToHome();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        findViewById(R.id.layout_main).setOnTouchListener(getSwipeGestureListener(this));
        initClickListeners();

        prefs = new Prefs(this);
        search = findViewById(R.id.search);
        homeAppsLayout = findViewById(R.id.home_apps_layout);
        appDrawer = findViewById(R.id.app_drawer_layout);

        appAdapter = new AppAdapter(this, appList, getAppClickListener());
        appListView = findViewById(R.id.app_list_view);
        appListView.setAdapter(appAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                appAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        appListView.setOnScrollListener(getScrollListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        backToHome();
        populateHomeApps();
        refreshAppsList();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.set_as_default_launcher) {
            resetDefaultLauncher();
        } else if (viewId == R.id.clock) {
            startActivity(new Intent(new Intent(AlarmClock.ACTION_SHOW_ALARMS)));
        } else if (viewId == R.id.date) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR);
            startActivity(intent);
        } else {
            try {
                int location = Integer.parseInt(view.getTag().toString());
                homeAppClicked(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        try {
            int location = Integer.parseInt(view.getTag().toString());
            showAppList(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void initClickListeners() {
        setDefaultLauncher = findViewById(R.id.set_as_default_launcher);
        setDefaultLauncher.setOnClickListener(this);

        findViewById(R.id.clock).setOnClickListener(this);
        findViewById(R.id.date).setOnClickListener(this);

        homeApp1 = findViewById(R.id.home_app_1);
        homeApp2 = findViewById(R.id.home_app_2);
        homeApp3 = findViewById(R.id.home_app_3);
        homeApp4 = findViewById(R.id.home_app_4);
        homeApp5 = findViewById(R.id.home_app_5);
        homeApp6 = findViewById(R.id.home_app_6);

        homeApp1.setOnClickListener(this);
        homeApp2.setOnClickListener(this);
        homeApp3.setOnClickListener(this);
        homeApp4.setOnClickListener(this);
        homeApp5.setOnClickListener(this);
        homeApp6.setOnClickListener(this);

        homeApp1.setOnLongClickListener(this);
        homeApp2.setOnLongClickListener(this);
        homeApp3.setOnLongClickListener(this);
        homeApp4.setOnLongClickListener(this);
        homeApp5.setOnLongClickListener(this);
        homeApp6.setOnLongClickListener(this);
    }

    private void populateHomeApps() {
        homeApp1.setText(prefs.getAppName(1));
        homeApp2.setText(prefs.getAppName(2));
        homeApp3.setText(prefs.getAppName(3));
        homeApp4.setText(prefs.getAppName(4));
        homeApp5.setText(prefs.getAppName(5));
        homeApp6.setText(prefs.getAppName(6));
    }

    private void showLongPressToast() {
        Toast.makeText(this, "Long press to select app", Toast.LENGTH_SHORT).show();
    }

    private void backToHome() {
        appDrawer.setVisibility(View.GONE);
        homeAppsLayout.setVisibility(View.VISIBLE);
        appAdapter.setFlag(FLAG_LAUNCH_APP);
        hideKeyboard();
        appListView.setSelectionAfterHeaderView();
        checkForDefaultLauncher();
    }

    private void refreshAppsList() {
        new Thread(() -> {
            try {
                List<AppModel> apps = new ArrayList<>();
                UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
                LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
                for (UserHandle profile : userManager.getUserProfiles()) {
                    for (LauncherActivityInfo activityInfo : launcherApps.getActivityList(null, profile))
                        if (!activityInfo.getApplicationInfo().packageName.equals(BuildConfig.APPLICATION_ID))
                            apps.add(new AppModel(
                                    activityInfo.getLabel().toString(),
                                    activityInfo.getApplicationInfo().packageName,
                                    profile));
                }
                Collections.sort(apps, (app1, app2) -> app1.appLabel.compareToIgnoreCase(app2.appLabel));
                appList.clear();
                appList.addAll(apps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAppList(int flag) {
        setDefaultLauncher.setVisibility(View.GONE);
        showKeyboard();
        search.setText("");
        appAdapter.setFlag(flag);
        homeAppsLayout.setVisibility(View.GONE);
        appDrawer.setVisibility(View.VISIBLE);
    }

    private void showKeyboard() {
        search.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyboard() {
        search.clearFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0);
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

    private void prepareToLaunchApp(AppModel appModel) {
        hideKeyboard();
        launchApp(appModel);
        backToHome();
        search.setText("");
    }

    private void homeAppClicked(int location) {
        if (prefs.getAppPackage(location).isEmpty()) showLongPressToast();
        else launchApp(getAppModel(
                prefs.getAppName(location),
                prefs.getAppPackage(location),
                prefs.getAppUserHandle(location)));
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

    private void openAppInfo(AppModel appModel) {
        LauncherApps launcher = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        Intent intent = getPackageManager().getLaunchIntentForPackage(appModel.appPackage);
        if (intent == null || intent.getComponent() == null) return;
        launcher.startAppDetailsActivity(intent.getComponent(), appModel.userHandle, null, null);
    }

    private void setHomeApp(AppModel appModel, int flag) {
        prefs.setHomeApp(appModel, flag);
        backToHome();
        populateHomeApps();
    }

    private void checkForDefaultLauncher() {
        if (BuildConfig.APPLICATION_ID.equals(getDefaultLauncherPackage()))
            setDefaultLauncher.setVisibility(View.GONE);
        else setDefaultLauncher.setVisibility(View.VISIBLE);
    }

    private String getDefaultLauncherPackage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo result = getPackageManager().resolveActivity(intent, 0);
        if (result == null || result.activityInfo == null)
            return "android";
        return result.activityInfo.packageName;
    }

    private void resetDefaultLauncher() {
        try {
            PackageManager packageManager = getPackageManager();
            ComponentName componentName = new ComponentName(this, FakeHomeActivity.class);
            packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            );
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getDefaultLauncherPackage().contains("."))
            openLauncherPhoneSettings();
    }

    private void openLauncherPhoneSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Toast.makeText(this, "Set Ultra as default launcher", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
        } else {
            Toast.makeText(this, "Search for launcher or home apps", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void openEditSettingsPermission() {
        Toast.makeText(this, "Please grant this permission", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        startActivity(intent);
    }

    private AppClickListener getAppClickListener() {
        return new AppClickListener() {
            @Override
            public void appClicked(AppModel appModel, int flag) {
                if (flag == FLAG_LAUNCH_APP) prepareToLaunchApp(appModel);
                else setHomeApp(appModel, flag);
            }

            @Override
            public void appLongPress(AppModel appModel) {
                hideKeyboard();
                openAppInfo(appModel);
            }
        };
    }

    private AppModel getAppModel(String appLabel, String appPackage, String appUserHandle) {
        return new AppModel(appLabel, appPackage, getUserHandleFromString(appUserHandle));
    }

    private UserHandle getUserHandleFromString(String appUserHandleString) {
        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);
        for (UserHandle userHandle : userManager.getUserProfiles())
            if (userHandle.toString().equals(appUserHandleString))
                return userHandle;
        return android.os.Process.myUserHandle();
    }

    private AbsListView.OnScrollListener getScrollListener() {
        return new AbsListView.OnScrollListener() {

            boolean onTop = false;

            @Override
            public void onScrollStateChanged(AbsListView listView, int state) {
                if (state == 1) { // dragging
                    onTop = !listView.canScrollVertically(-1);
                    if (onTop) hideKeyboard();

                } else if (state == 0) { // stopped
                    if (!listView.canScrollVertically(1)) hideKeyboard();
                    else if (!listView.canScrollVertically(-1)) {
                        if (onTop) backToHome();
                        else showKeyboard();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        };
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
                showAppList(FLAG_LAUNCH_APP);
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                expandNotificationDrawer();
            }

            @Override
            public void onLongClick() {
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

    static class AppAdapter extends BaseAdapter implements Filterable {

        private final Context context;
        private final AppClickListener appClickListener;
        private List<AppModel> filteredAppsList;
        private final List<AppModel> allAppsList;
        private int flag = 0;

        private static class ViewHolder {
            TextView appName;
            View indicator;
        }

        public AppAdapter(Context context, List<AppModel> apps, AppClickListener appClickListener) {
            this.context = context;
            this.appClickListener = appClickListener;
            this.filteredAppsList = apps;
            this.allAppsList = apps;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        @Override
        public int getCount() {
            return filteredAppsList.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredAppsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppModel appModel = (AppModel) getItem(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
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
                appClickListener.appClicked(clickedAppModel, flag);
            });
            viewHolder.appName.setOnLongClickListener(view -> {
                AppModel clickedAppModel = (AppModel) viewHolder.appName.getTag();
                appClickListener.appLongPress(clickedAppModel);
                return true;
            });
            if (appModel.userHandle == android.os.Process.myUserHandle())
                viewHolder.indicator.setVisibility(View.GONE);
            else viewHolder.indicator.setVisibility(View.VISIBLE);

            if (flag == 0 && getCount() == 1) appClickListener.appClicked(appModel, flag);

            return convertView;
        }

        @Override
        public Filter getFilter() {

            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredAppsList = (List<AppModel>) results.values;
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<AppModel> filteredApps = new ArrayList<>();

                    if (constraint.toString().isEmpty())
                        filteredApps = allAppsList;
                    else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < allAppsList.size(); i++) {
                            AppModel app = allAppsList.get(i);
                            if (app.appLabel.toLowerCase().contains(constraint))
                                filteredApps.add(app);
                        }
                    }

                    results.count = filteredApps.size();
                    results.values = filteredApps;
                    return results;
                }
            };
        }
    }
}