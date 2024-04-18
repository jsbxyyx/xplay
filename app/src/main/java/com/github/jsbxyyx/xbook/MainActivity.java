package com.github.jsbxyyx.xbook;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.jsbxyyx.xbook.common.Common;
import com.github.jsbxyyx.xbook.common.LogUtil;
import com.github.jsbxyyx.xbook.common.SPUtils;
import com.github.jsbxyyx.xbook.common.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @author jsbxyyx
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    private String TAG = "xbook";

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private HomeFragment homeFragment;

    private ListFragment listFragment;
    private VideoListFragment videoListFragment;
    private ProfileFragment profileFragment;
    private int position = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        fm = getSupportFragmentManager();
        showFragment(1);

        LogUtil.d(TAG, "login in : %s", SPUtils.getData(this, Common.login_key));
        SessionManager.setSession(SPUtils.getData(this, Common.login_key));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    position = 1;
                    break;
                case R.id.nav_list:
                    position = 2;
                    break;
                case R.id.nav_video_list:
                    position = 3;
                    break;
                case R.id.nav_profile:
                    position = 4;
                    break;
            }
            showFragment(position);
            return true;
        });
    }

    private void showFragment(int index) {
        FragmentTransaction ft = fm.beginTransaction();
        if (homeFragment != null) {
            ft.hide(homeFragment);
        }
        if (listFragment != null) {
            ft.hide(listFragment);
        }
        if (videoListFragment != null) {
            ft.hide(videoListFragment);
        }
        if (profileFragment != null) {
            ft.hide(profileFragment);
        }
        switch (index) {
            case 1:
                if (homeFragment != null) {
                    ft.show(homeFragment);
                    homeFragment.onResume();
                } else {
                    homeFragment = new HomeFragment();
                    ft.add(R.id.fragment_container, homeFragment);
                }
                break;
            case 2:
                if (listFragment != null) {
                    ft.show(listFragment);
                    listFragment.onResume();
                } else {
                    listFragment = new ListFragment();
                    ft.add(R.id.fragment_container, listFragment);
                }
                break;
            case 3:
                if (videoListFragment != null) {
                    ft.show(videoListFragment);
                } else {
                    videoListFragment = new VideoListFragment();
                    ft.add(R.id.fragment_container, videoListFragment);
                }
                break;
            case 4:
                if (profileFragment != null) {
                    ft.show(profileFragment);
                    profileFragment.onResume();
                } else {
                    profileFragment = new ProfileFragment();
                    ft.add(R.id.fragment_container, profileFragment);
                }
                break;
        }
        ft.commit();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("position");
        showFragment(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.LOCATION_HARDWARE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_SETTINGS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.INTERNET,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_PRIVILEGED,
                                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                                Manifest.permission.POST_NOTIFICATIONS
                        }, 0x0010);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}