package com.muqing.kctab;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.Activity.LoginActivity;
import com.muqing.kctab.Activity.SettingActivity;
import com.muqing.kctab.Adapter.KeChengPageAdapter;
import com.muqing.kctab.databinding.ActivityMainBinding;
import com.muqing.kctab.fragment.kecheng;
import com.muqing.wj;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity<ActivityMainBinding> {
    public static String Time;//当前时间xx:xx 当前日期 1234567
    public static int Week;
    public static int benzhou = 0;
    public static List<String> TabList = new ArrayList<>();

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

    final File fileTabList = new File(wj.data, "TabList");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!fileTabList.isDirectory()) {
            Login();
            return;
        }
        new GXThread(this);
        LoadUI();
    }

    private void LoadUI() {
        pageAdapter = null;
        pageAdapter = new KeChengPageAdapter(this);
        File[] list = fileTabList.listFiles();
        TabList.clear();
        if (list != null) {
            for (File s : list) {
                TabList.add(s.getAbsolutePath());
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        TabList.sort((f1, f2) -> {
            LocalDate date1 = extractDate(f1, pattern, formatter);
            LocalDate date2 = extractDate(f2, pattern, formatter);
            if (date1 == null || date2 == null) return 0;
            return date1.compareTo(date2);
        });
        int i = 1;
        for (String s : TabList) {
            pageAdapter.addPage(kecheng.newInstance(s, i));
            i++;
        }
        UI();

    }

    @Nullable
    private static LocalDate extractDate(String text, Pattern pattern, DateTimeFormatter formatter) {
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

    public void UI() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                gj.sc("每秒执行一次任务");
                LocalTime now = LocalTime.now();
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                Time = now.format(formatter);
                // 获取当前星期几（1=星期一，7=星期日）
                Week = today.getDayOfWeek().getValue();
                // 在主线程更新 UI
                runOnUiThread(() -> {
                    try {
                        int currentItem = binding.viewpage.getCurrentItem();
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + currentItem);
                        if (fragment instanceof kecheng) {
                            kecheng k = (kecheng) fragment;
                            k.adapter.Load(k.binding.recyclerview);
                        }
                    } catch (Exception e) {
                        gj.sc("TimerTask " + e);
                    }
                });
            }
        };
        if (binding == null) {
            setContentView();
            setSupportActionBar(binding.toolbar);
        }
        binding.viewpage.setAdapter(pageAdapter);
        int week = KcApi.getWeek();
        MainActivity.benzhou = week;
        binding.viewpage.setCurrentItem(week - 1, false);
        binding.menuZhou.setText(String.format("第%s周", week));
        binding.viewpage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.menuZhou.setText(String.format("第%s周", position + 1));
            }
        });
        timer.schedule(task, 0, 1000); // 立即开始，每隔1秒执行
        //获取yyyy-MM-dd
        binding.time.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        binding.menuZhou.setOnClickListener(view -> {
            zhouDialog zhouDialog = new zhouDialog(MainActivity.this) {
                @Override
                public void click(int position) {
                    MainActivity.this.binding.viewpage.setCurrentItem(position, true);
                }
            };
            zhouDialog.zhouAdapter.week = MainActivity.this.binding.viewpage.getCurrentItem() + 1;
        });
    }


    public static class ScheduleItem {
        public String session;
        public String time1;
        public String time2;

        public ScheduleItem(String session, String time1, String time2) {
            this.session = session;
            this.time1 = time1;
            this.time2 = time2;
        }
    }

    public static final ScheduleItem[] schedule = {new ScheduleItem("第 1 节", "08:20-09:05", "09:15-10:00"), new ScheduleItem("第 2 节", "10:10-11:40", "10:30-12:00"), new ScheduleItem("第 3 节", "13:30-14:15", "14:25-15:10"), new ScheduleItem("第 4 节", "15:20-16:05", "16:15-17:00"), new ScheduleItem("第 5 节", "18:30-19:15", "19:25-20:10")};

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
            kecheng kecheng = pageAdapter.data.get(currentItem);
            kecheng.adapter.isjt = true;
            RecyclerView recyclerView = kecheng.binding.recyclerview;
            try {
                kecheng.adapter.Load(kecheng.binding.recyclerview);
            } catch (Exception e) {
                gj.sc(e);
            }
            viewWidth = recyclerView.getMeasuredWidth();
            viewHeight = recyclerView.getMeasuredHeight();
            Bitmap fullRecyclerViewBitmap = getFullRecyclerViewBitmap(recyclerView, null);
            gj.sc(fullRecyclerViewBitmap);
            jietuActivity.start(this, fullRecyclerViewBitmap);
            kecheng.adapter.isjt = false;
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.sync) {
            Intent intent = new Intent(this, LoginActivity.class);
            int currentItem = binding.viewpage.getCurrentItem();
            kecheng kecheng = pageAdapter.data.get(currentItem);
            intent.putExtra("sync", kecheng.curriculum == null ? "ALL" : String.valueOf(kecheng.curriculum.data.get(0).week));
            SyncKc.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> SyncKc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                new LoadKc(data) {
                    @Override
                    public void error() {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "同步更新失败", Toast.LENGTH_SHORT).show());
                    }
                };
            }
        }
    });
    ActivityResultLauncher<Intent> LoginStart = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                new LoadKc(data) {
                    @Override
                    public void error() {
                        Login();
                    }
                };
                return;
            }
        }
        finish();
    });

    public abstract class LoadKc extends Thread {
        public Intent data;
        AlertDialog alertDialog;

        public LoadKc(Intent data) {
            this.data = data;
            alertDialog = main.LoadIng(MainActivity.this);
            start();
        }

        @Override
        public void run() {
            String sycn = data.getStringExtra("sync");
            String token = data.getStringExtra("token");
            boolean load = false;
            if (sycn == null) {
                return;
            }
            if (sycn.equals("ALL")) {
                load = KcApi.Load(token);
            } else if (sycn.equals("kczip")) {
                load = true;
            } else {
                try {
                    File file = new File(wj.data, "TabList");
                    String s = KcApi.GetCurriculum(sycn, "");
                    Curriculum curriculum = new Gson().fromJson(s, Curriculum.class);
                    curriculum.data.get(0).week = Integer.parseInt(sycn);
                    int length = curriculum.data.get(0).date.size();
                    String zc = curriculum.data.get(0).date.get(length - 1).mxrq;
                    wj.xrwb(new File(file, zc + ".txt"), new Gson().toJson(curriculum));
                    load = true;
                } catch (Exception e) {
                    gj.sc(e);
                }
            }
            if (load) {
                runOnUiThread(MainActivity.this::LoadUI);
            } else {
                error();
            }
            runOnUiThread(() -> alertDialog.dismiss());
        }

        public abstract void error();
    }

    private int viewWidth, viewHeight;

    public Bitmap getFullRecyclerViewBitmap(View view, Bitmap background) {
        if (viewWidth == 0 || viewHeight == 0) {
            gj.sc("避免创建空 Bitmap");
            return null; // 避免创建空 Bitmap
        }
        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground, android.R.attr.textColorPrimary,});
        int backgroundColor = array.getColor(0, 0xFFF5F5F5);
        canvas.drawColor(backgroundColor);
        view.draw(canvas);
        // **绘制背景图片**
        if (background != null) {
            int bgWidth = background.getWidth();
            int bgHeight = background.getHeight();

            // 计算比例，确保背景图充满整个 View
            float scaleX = (float) viewWidth / bgWidth;
            float scaleY = (float) viewHeight / bgHeight;
            float scale = Math.max(scaleX, scaleY);

            // 计算裁剪区域
            int cropWidth = (int) (viewWidth / scale);
            int cropHeight = (int) (viewHeight / scale);

            int cropX = (bgWidth - cropWidth) / 2;
            int cropY = (bgHeight - cropHeight) / 2;

            Rect src = new Rect(cropX, cropY, cropX + cropWidth, cropY + cropHeight);
            Rect dst = new Rect(0, 0, viewWidth, viewHeight);

            Paint path = new Paint();
            path.setAlpha(30);

            // 绘制背景图
            canvas.drawBitmap(background, src, dst, path);
        }
        array.recycle();
        return bitmap;
    }
}