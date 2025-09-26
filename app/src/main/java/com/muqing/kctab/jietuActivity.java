package com.muqing.kctab;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Adapter.TableHAdapter;
import com.muqing.kctab.Adapter.TableTimeAdapter;
import com.muqing.kctab.DataType.TableTimeData;
import com.muqing.kctab.databinding.ActivityJietuBinding;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.databinding.ItemTableHBinding;
import com.muqing.kctab.fragment.kecheng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class jietuActivity extends AppCompatActivity<ActivityJietuBinding> {
    public Bitmap bitmap;
    private int width, height;

    public static void start(Activity activity, Curriculum data) {
// 创建 Intent 并传递 Bitmap 数据
        Intent intent = new Intent(activity, jietuActivity.class);
        intent.putExtra("data", new Gson().toJson(data));
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        Curriculum curriculum = new Gson().fromJson(data, Curriculum.class);
        new Thread(() -> {
            bitmap = recyclerViewToBitmapGrid(curriculum);
            runOnUiThread(() -> binding.imageView.setImageBitmap(bitmap));
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem baocun = menu.add(0, 0, 0, "分享");
        baocun.setIcon(R.drawable.share_24px);
        baocun.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        Objects.requireNonNull(baocun.getActionView()).setId(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {
            shareBitmap(bitmap);
        }
        return super.onOptionsItemSelected(item);
    }

    public void shareBitmap(Bitmap bitmap) {
        try {
            // 1. 创建临时文件
            File cachePath = new File(getCacheDir(), "images");
//            gj.sc("缓存目录：" + cachePath.getPath());
            cachePath.mkdirs(); // 创建目录
            File file = new File(cachePath, "shared_image.png");
            // 2. 保存 Bitmap 到文件
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            // 3. 获取 content:// Uri
            Uri contentUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );
            if (contentUri != null) {
                // 4. 创建分享 Intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 5. 启动分享
                startActivity(Intent.createChooser(shareIntent, "分享图片"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ActivityJietuBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityJietuBinding.inflate(layoutInflater);
    }

    String[] HList = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    public Bitmap recyclerViewToBitmapGrid(Curriculum curriculum) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        // 1. 创建根布局（竖直 LinearLayout）
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        // 2. 添加表头（横向 LinearLayout）
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        {
            ItemTableHBinding tvBinding = ItemTableHBinding.inflate(LayoutInflater.from(this));
            tvBinding.titleRi.setText("Day");
            headerRow.addView(tvBinding.getRoot(), new LinearLayout.LayoutParams(gj.dp2px(this, 35), ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        for (String h : HList) {
            ItemTableHBinding tvBinding = ItemTableHBinding.inflate(LayoutInflater.from(this));
            tvBinding.titleRi.setText(h);
            headerRow.addView(tvBinding.getRoot(), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }
        root.addView(headerRow);
        List<List<List<Curriculum.Course>>> lists = kecheng.GetKcLei(new ArrayList<>(), curriculum);
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        {
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            rowLayout.addView(recyclerView, new LinearLayout.LayoutParams(gj.dp2px(this, 35), ViewGroup.LayoutParams.MATCH_PARENT));
            recyclerView.setAdapter(new TableTimeAdapter(this, Arrays.asList(TableTimeData.tableTimeData)));
        }
        // 3. 添加数据行
        for (List<List<Curriculum.Course>> row : lists) {
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new GridAdapter(this, row));
            rowLayout.addView(recyclerView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }
        root.addView(rowLayout);
        // 4. 手动测量 & 布局
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        root.measure(widthSpec, heightSpec);
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());

        // 5. 创建 Bitmap 并绘制
        Bitmap bitmap = Bitmap.createBitmap(root.getMeasuredWidth(), root.getMeasuredHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //绘制背景色
        canvas.drawColor(gj.getbackgroundColor(this));
        root.draw(canvas);
        return bitmap;
    }

}