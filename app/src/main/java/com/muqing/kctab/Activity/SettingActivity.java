package com.muqing.kctab.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.muqing.AppCompatActivity;
import com.muqing.kctab.Adapter.ThemeAdapter;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivitySettingBinding;
import com.muqing.kctab.main;

import java.sql.Array;
import java.util.ArrayList;

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
        binding.themeSystem.setText(theme[code]);
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
                main.setThemeMode(position);
                sharedPreferences.edit().putInt("mods", position).apply();
                return true;
            });
            popupMenu.show();
        });
    }

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