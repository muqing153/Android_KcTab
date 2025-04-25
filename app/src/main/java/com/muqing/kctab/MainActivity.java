package com.muqing.kctab;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.Activity.LoginActivity;
import com.muqing.kctab.Activity.SettingActivity;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.databinding.ActivityMainBinding;
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
    public static Curriculum curriculum = null;
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
        intent.putExtra("login", true);
        SyncKc.launch(intent);
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
        File[] list = fileTabList.listFiles();
        TabList.clear();
        if (list != null) {
            for (File s :
                    list) {
                TabList.add(s.getAbsolutePath());
            }
        }
        new LoadToken().start();

    }

    private class LoadToken extends Thread {

        @Override
        public void run() {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
                TabList.sort((f1, f2) -> {
                    LocalDate date1 = extractDate(f1, pattern, formatter);
                    LocalDate date2 = extractDate(f2, pattern, formatter);
                    if (date1 == null || date2 == null) return 0;
                    return date1.compareTo(date2);
                });
                curriculum = KcApi.GetCurriculum();
                if (curriculum == null) {
                    throw new Exception("获取课表失败");
                }
                runOnUiThread(MainActivity.this::UI);
            } catch (Exception e) {
                gj.sc(e);
            }
        }
    }

    @Nullable
    private static LocalDate extractDate(String text, Pattern pattern, DateTimeFormatter formatter) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return LocalDate.parse(matcher.group(), formatter);
        }
        return null;
    }


    GridAdapter adapter;

    Timer timer = new Timer();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();      // 停止任务队列
            timer.purge();       // 清除已取消的任务
            timer = null;
        }
    }

    public void UI() {
        adapter = new GridAdapter(this, new ArrayList<>());
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
                        adapter.Load(binding.recyclerview);
                    } catch (Exception e) {
                        gj.sc(e);
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000); // 立即开始，每隔1秒执行

        if (binding == null) {
            setContentView();
            setSupportActionBar(binding.toolbar);
        }
        Log.i(TAG, "UI: 执行UI构建：" + curriculum);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 8); // 列
        binding.recyclerview.setLayoutManager(layoutManager);

        if (curriculum != null) {
            LoadTab();
        }
        //获取yyyy-MM-dd
        binding.time.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        binding.menuZhou.setOnClickListener(view -> {
            new zhouDialog(MainActivity.this) {
                @Override
                public void click(int position) {
                    MainActivity.curriculum = KcApi.GetCurriculumFile(TabList.get(position));
                    LoadTab();
                }
            };
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

    final ScheduleItem[] schedule = {new ScheduleItem("第 1 节", "08:20-09:05", "09:15-10:00"),
            new ScheduleItem("第 2 节", "10:10-11:40", "10:30-12:00"),
            new ScheduleItem("第 3 节", "13:30-14:15", "14:25-15:10"),
            new ScheduleItem("第 4 节", "15:20-16:05", "16:15-17:00"),
            new ScheduleItem("第 5 节", "18:30-19:15", "19:25-20:10")
    };

    public void LoadTab() {
        adapter.dataList.clear();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                if (row == 0 && col == 0) {
                    adapter.dataList.add(new KcLei("节/日期"));
                    continue;
                }
                adapter.dataList.add(new KcLei("R" + (row + 1) + " C" + (col + 1)));
            }
        }
        Curriculum.DataItem dataItem = curriculum.data.get(0);
        for (int i = 0; i < dataItem.date.size(); i++) {
            Curriculum.DateInfo dateInfo = dataItem.date.get(i);
            adapter.dataList.set(i + 1, new KcLei(String.format("%s(%s)", dateInfo.xqmc, dateInfo.rq)));
        }
        for (int i = 0, j = 8; i < schedule.length; i++, j += 8) {
            KcLei kcLei = new KcLei(schedule[i].session);
            kcLei.message = schedule[i].time1 + "\n" + schedule[i].time2;
            adapter.dataList.set(j, kcLei);
        }
        // 1. 遍历每个节次，创建行数据
        String[] ric = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        int ric_i = 0;
        for (int i = 0, j = 9; i < 5; i++) {
            for (int k = 0; k < 7; k++, j++) {
                int finalRic_i = ric_i;
                int finalI = k + 1;
                Curriculum.Course result = dataItem.courses.stream()
                        .filter(c -> c.classTime.endsWith(String.format("%s%s", ric[finalRic_i], ric[finalRic_i + 1])) &&
                                c.weekDay == finalI)
                        .findFirst()
                        .orElse(null);
//                gj.sc(result);
                if (result == null) {
                    result = new Curriculum.Course();
                    result.startTime = schedule[i].time1.split("-")[0];
                    result.endTime = schedule[i].time2.split("-")[1];
                    result.weekDay = k + 1;

                }
                adapter.dataList.set(j, new KcLei(result));
            }
            j++;
            ric_i += 2;
        }
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.post(() -> {
            gj.sc("adapter.Load(binding.recyclerview)");
            try {
                adapter.Load(binding.recyclerview);
                gj.sc(ItemXY[0] + " " + ItemXY[1]);
                binding.horizontal.scrollTo(ItemXY[0], ItemXY[1]);
            } catch (Exception e) {
                gj.sc(e);
            }
        });

        if (curriculum != null) {
            binding.menuZhou.setText(String.format("第 %s 周", curriculum.data.get(0).week));
        }
    }

    //    public static final String[] weeks = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public static int[] ItemXY = new int[]{0, 0};

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            adapter.isjt = true;
            try {
                adapter.Load(binding.recyclerview);
            } catch (Exception e) {
                gj.sc(e);
            }
            viewWidth = binding.recyclerview.getMeasuredWidth();
            viewHeight = binding.recyclerview.getMeasuredHeight();
            Bitmap fullRecyclerViewBitmap = getFullRecyclerViewBitmap(binding.recyclerview, null);
            gj.sc(fullRecyclerViewBitmap);
            jietuActivity.start(this, fullRecyclerViewBitmap);
            adapter.isjt = false;
        } else if (id == R.id.settings) {
            startActivity(new Intent(this, SettingActivity.class));
        } else if (id == R.id.sync) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("sync", benzhou == 0 ? "ALL" : String.valueOf(benzhou));
            SyncKc.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    ActivityResultLauncher<Intent> SyncKc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                if (data.getBooleanExtra("login", false)) {
                    finish();
                    return;
                }

                String sycn = data.getStringExtra("sync");
                String token = data.getStringExtra("token");
                AlertDialog alertDialog = LoadIng();
                new Thread(() -> {
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
                        runOnUiThread(() -> {
                            LoadUI();
                            alertDialog.dismiss();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "同步更新失败", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        }
    });

    private AlertDialog LoadIng() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
        dialog.setView(R.layout.load_dialog);
        AlertDialog show = dialog.show();
        show.setCanceledOnTouchOutside(false);
        show.setCancelable(false);
        if (show.getWindow() != null) {
            show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return show;
    }

    int viewWidth, viewHeight;

    public Bitmap getFullRecyclerViewBitmap(View view, Bitmap background) {
        if (viewWidth == 0 || viewHeight == 0) {
            gj.sc("避免创建空 Bitmap");
            return null; // 避免创建空 Bitmap
        }
        Bitmap bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
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

    public static final String TAG = "打印";
}