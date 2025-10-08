package com.muqing.kctab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.Activity.AutoTableActivity;
import com.muqing.kctab.Activity.LoginActivity;
import com.muqing.kctab.Activity.SettingActivity;
import com.muqing.kctab.Adapter.KeChengPageAdapter;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.databinding.ActivityMainBinding;
import com.muqing.kctab.fragment.kecheng;
import com.muqing.wj;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity<ActivityMainBinding> {
    public static int benzhou = 1,MaxZhou = 20,ValueZhou = 1;
    public static Curriculum curriculum;
    @Override
    protected ActivityMainBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }
    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
//        binding.appbar.setPadding(0, systemBars.top, 0, 0);
    }
    private void Login() {
        Intent intent = new Intent(this, LoginActivity.class);
        LoginStart.launch(intent);
    }

    public static File fileTabList = new File(wj.data);


    public static int ThisColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.ThisColor = gj.getThemeColor(this, com.google.android.material.R.attr.colorPrimaryFixedDim);
        SharedPreferences kebiao = getSharedPreferences("kebiao", MODE_PRIVATE);
        fileTabList = new File(wj.data, "debug.json");
        MainActivity.curriculum = new Gson().fromJson(wj.dqwb(fileTabList), Curriculum.class);
//        gj.sc(new Gson().toJson(MainActivity.curriculum));

//        String schoolYearTerm = main.getSchoolYearTerm(LocalDate.now());
//        String xuenian = kebiao.getString("xuenian", null);
//        if (xuenian == null) {
//            kebiao.edit().putString("xuenian", schoolYearTerm).apply();
//            fileTabList = new File(fileTabList, schoolYearTerm);
//        } else {
//            fileTabList = new File(fileTabList, xuenian);
//        }
//        {
//            if (!fileTabList.isDirectory() || Objects.requireNonNull(fileTabList.list()).length == 0) {
//                //noinspection ResultOfMethodCallIgnored
//                fileTabList.mkdirs();
//                Login();
//                return;
//            }
//        }
        new GXThread(this);
        LoadUI();
    }

    private void LoadUI() {
        UI();
    }
    @Nullable
    public static LocalDate extractDate(String text, Pattern pattern, DateTimeFormatter formatter) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(), formatter);
        }
        return null;
    }

    private Timer timer = new Timer();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();      // 停止任务队列
            timer.purge();       // 清除已取消的任务
            timer = null;
        }
    }

    public KeChengPageAdapter pageAdapter;
    public static TableStyleData TableStyle;

    public void UI() {
        if (binding == null) {
            setContentView();
            setSupportActionBar(binding.toolbar);
        }
        Gson gson = new Gson();
        SharedPreferences a = getSharedPreferences("tablestyle", Context.MODE_PRIVATE);
        TableStyle = gson.fromJson(a.getString("tablestyle", gson.toJson(null)), TableStyleData.class);
        pageAdapter = new KeChengPageAdapter(getSupportFragmentManager(), getLifecycle());
        for (int i = 1; i <= MaxZhou; i++) {
            pageAdapter.addPage(kecheng.newInstance(i));
        }
//        binding.viewpage.setSaveEnabled(false);
        binding.viewpage.setAdapter(pageAdapter);
        int week = KcApi.getWeek("2025-09-01", "2025-10-08");
        MainActivity.benzhou = week;
        MainActivity.ValueZhou = week;
        binding.viewpage.setCurrentItem(week - 1, false);
        binding.menuZhou.setText(String.format("第%s周", week));
        binding.viewpage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                MainActivity.ValueZhou = position + 1;
                binding.menuZhou.setText(String.format("第%s周", MainActivity.ValueZhou));
                String weekDate = Curriculum.getWeekDates("2025-09-01", MainActivity.ValueZhou)[0];
                binding.toolbarTime.setText(weekDate);
            }
        });
        String weekDate = Curriculum.getWeekDates("2025-09-01", MainActivity.ValueZhou)[0];
        binding.toolbarTime.setText(weekDate);
        binding.title.setOnClickListener(view -> {
            //获取当前viewpage展示的页面位置
            int position = MainActivity.this.binding.viewpage.getCurrentItem();
            new zhouDialog(MainActivity.this, position + 1) {
                @Override
                public void click(int position) {
                    MainActivity.this.binding.viewpage.setCurrentItem(position, false);
                }
            };
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            int currentItem = binding.viewpage.getCurrentItem();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentItem);
            if (fragment instanceof kecheng) {
                kecheng k = (kecheng) fragment;
//                jietuActivity.start(MainActivity.this, k.curriculum);
            }
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.sync) {
            Intent intent = new Intent(this, LoginActivity.class);
            int currentItem = binding.viewpage.getCurrentItem();
            kecheng kecheng = pageAdapter.data.get(currentItem);
//            intent.putExtra("sync", kecheng.curriculum == null ? "ALL" : String.valueOf(kecheng.curriculum.data.get(0).week));
            SyncKc.launch(intent);
        } else {
            startActivity(new Intent(this, AutoTableActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> SyncKc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            //                LoadUI();
        }
    });
    ActivityResultLauncher<Intent> LoginStart = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                boolean kc = data.getBooleanExtra("kc", false);
                if (kc) {
                    LoadUI();
                    return;
                }
            }
        }
        finish();
    });
}