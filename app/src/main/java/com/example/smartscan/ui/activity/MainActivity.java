package com.example.smartscan.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartscan.R;
import com.example.smartscan.ui.fragment.HomeFragment;
import com.example.smartscan.ui.fragment.KnowledgeFragment;
import com.example.smartscan.ui.fragment.ProfileFragment;
import com.example.smartscan.ui.fragment.WrongBookFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 应用主入口，使用 BottomNavigationView 管理首页、知识库、错题本和我的页面。
 */
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setupBottomNavigation();
        if (savedInstanceState == null) {
            switchFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_knowledge) {
                switchFragment(new KnowledgeFragment());
                return true;
            } else if (itemId == R.id.nav_scan) {
                startActivity(new Intent(this, ScanActivity.class));
                return false;
            } else if (itemId == R.id.nav_wrong) {
                switchFragment(new WrongBookFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
