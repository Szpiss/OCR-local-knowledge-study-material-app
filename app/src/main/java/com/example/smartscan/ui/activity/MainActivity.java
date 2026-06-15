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
 *
 * 答辩重点：
 * 1. MainActivity 只负责主导航，不堆业务逻辑。
 * 2. 首页、知识库、错题本、我的使用 Fragment 切换。
 * 3. 扫描页功能较复杂，因此单独打开 ScanActivity。
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
        // BottomNavigationView 是底部导航栏，点击不同菜单切换不同 Fragment。
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_knowledge) {
                switchFragment(new KnowledgeFragment());
                return true;
            } else if (itemId == R.id.nav_scan) {
                // 扫描识别包含选图、OCR、表单保存，页面较复杂，所以使用独立 Activity。
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
        // Fragment 切换统一走这里，保证主页面只有一个 fragment_container。
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
