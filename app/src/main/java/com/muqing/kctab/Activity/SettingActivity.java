package com.muqing.kctab.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ThemeAdapter;
import com.muqing.kctab.GXThread;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivitySettingBinding;
import com.muqing.kctab.main;
import com.muqing.wj;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

public class SettingActivity extends AppCompatActivity<ActivitySettingBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
        binding.recyclerTheme.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<Drawable> objects = new ArrayList<>();
        objects.add(new ColorDrawable(Color.WHITE));
        objects.add(new ColorDrawable(Color.BLACK));
        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        ThemeAdapter themeAdapter = new ThemeAdapter(this, objects) {
            @Override
            public void OnClick(int position) {
//                main.setThemeMode(position);
            }
        };
        int code = sharedPreferences.getInt("mods", 0);
        themeAdapter.mode = code;
        binding.recyclerTheme.setAdapter(themeAdapter);

        String[] theme = new String[]{"跟随系统", "浅色", "深色"};
        binding.themeSystem.setTitle(theme[code]);
        binding.themeSystem.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.setGravity(Gravity.CENTER);
            popupMenu.getMenuInflater().inflate(R.menu.theme_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                int position = 0;
                if (itemId == R.id.menu_light) {
                    position = 1;
                } else if (itemId == R.id.menu_dark) {
                    position = 2;
                }
                binding.themeSystem.setTitle(theme[position]);
                main.setThemeMode(position);
                sharedPreferences.edit().putInt("mods", position).apply();
                return true;
            });
            popupMenu.show();
        });

        //动态颜色Debug
        if (DynamicColors.isDynamicColorAvailable()) {
            binding.themeDynamic.setEnabled(true);
            binding.themeDynamic.setChecked(sharedPreferences.getBoolean("dynamic", false));
            binding.themeDynamic.setOnCheckedChangeListener((compoundButton, b) -> sharedPreferences.edit().putBoolean("dynamic", b).apply());
        } else {
            binding.themeDynamic.setEnabled(false);
            binding.themeDynamic.setChecked(false);
            binding.themeDynamic.setMessage("你目前的系统不支持此选项");
        }

        binding.kbDaochu.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/kczip");
            intent.putExtra(Intent.EXTRA_TITLE, "TabList.kczip");
            createZipLauncher.launch(intent);
        });
        {
            File file = new File(wj.data, "TabList");
            String[] list = file.list();
            if (list != null) {
                Arrays.sort(list);
                SharedPreferences kebiao = getSharedPreferences("kebiao", MODE_PRIVATE);
                binding.kbXuenian.setMessage(kebiao.getString("xuenian", "null"));
                binding.kbXuenian.setOnClickListener(view -> new MaterialAlertDialogBuilder(SettingActivity.this)
                        .setTitle("选择学年")
                        .setItems(list, (dialogInterface, i) -> {
                            SharedPreferences kebiao1 = getSharedPreferences("kebiao", MODE_PRIVATE);
                            kebiao1.edit().putString("xuenian", list[i]).apply();
                            binding.kbXuenian.setMessage(list[i]);
                        })
                        .show());
            }
        }
        binding.kbDelete.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(SettingActivity.this)
                    .setTitle("格式化")
                    .setMessage("此操作将删除所有课表数据，是否继续？")
                    .setPositiveButton("确定", (dialogInterface, i) -> {
                        wj.sc(new File(wj.data, "TabList"));
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    })
                    .setNegativeButton("取消", null)
                    .show();

        });


        try {
            binding.qtJcgx.setMessage("当前版本：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            gj.sc(e);
            binding.qtJcgx.setMessage("获取版本失败");
        }
        binding.qtJcgx.setOnClickListener(view -> {
            view.setEnabled(false);
            new GXThread(SettingActivity.this, () -> Toast.makeText(SettingActivity.this, "你已经是最新版本！", Toast.LENGTH_SHORT).show()) {
                @Override
                public void run() {
                    super.run();
                    runOnUiThread(() -> view.setEnabled(true));
                }

                @Override
                public void error(String msg) {
                    runOnUiThread(() -> Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show());
                }
            };
        });

        binding.qtAbout.setOnClickListener(view -> startActivity(new Intent(SettingActivity.this, activity_about_software.class)));

        binding.kbZhengli.setOnClickListener(view -> startActivity(new Intent(SettingActivity.this, CourseOverviewActivity.class)));


        SharedPreferences SpSetting = getSharedPreferences("setting", MODE_PRIVATE);
        binding.qtJcgxSwitch.setChecked(SpSetting.getBoolean("jcgx", true));
        binding.qtJcgxSwitch.setOnCheckedChangeListener((compoundButton, b) -> SpSetting.edit().putBoolean("jcgx", b).apply());
    }

    private final ActivityResultLauncher<Intent> createZipLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    File sourceFolder = new File(wj.data, "TabList");
                    if (uri != null) {
                        try (OutputStream os = getContentResolver().openOutputStream(uri);
                             ZipOutputStream zos = new ZipOutputStream(os)) {
                            wj.zipFiles(sourceFolder, zos);
                            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
//        binding.toolbar.setPadding(0, systemBars.top, 0, 0);
//        super.setOnApplyWindowInsetsListener(systemBars, v);
    }

    @Override
    protected ActivitySettingBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivitySettingBinding.inflate(layoutInflater);
    }
}