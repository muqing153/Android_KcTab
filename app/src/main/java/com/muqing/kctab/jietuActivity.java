package com.muqing.kctab;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muqing.AppCompatActivity;
import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.databinding.ActivityJietuBinding;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.fragment.kecheng;
import com.muqing.wj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class jietuActivity extends AppCompatActivity<ActivityJietuBinding> {
    public static Bitmap bitmap;

    public static void start(Activity activity, Bitmap imageView) {
        bitmap = imageView;
// 创建 Intent 并传递 Bitmap 数据
        Intent intent = new Intent(activity, jietuActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //获取屏幕的宽度
        boolean tablet = gj.isTablet(this);
        getResources().getDisplayMetrics();
        int k;
        if (tablet) {
            k = getResources().getDisplayMetrics().heightPixels;
        } else {
            k = getResources().getDisplayMetrics().widthPixels;
        }
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();

        float scale = (float) k / originalWidth;
        int targetHeight = (int) (originalHeight * scale);
        bitmap = Bitmap.createScaledBitmap(bitmap, k, targetHeight, true);
        ViewGroup.LayoutParams layoutParams = binding.imageView.getLayoutParams();
        layoutParams.height = targetHeight;
        layoutParams.width = k;
//        gj.sc("bitmap:" + bitmap.getWidth() + " " + bitmap.getHeight() + " " + k);
        binding.imageView.setImageBitmap(bitmap);

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
    public void BackPressed() {
        super.BackPressed();
        bitmap = null;
    }

    @Override
    protected ActivityJietuBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityJietuBinding.inflate(layoutInflater);
    }

    public static Bitmap recyclerViewToBitmapGrid(int backcolor,RecyclerView recyclerView) {
        GridAdapter adapter = (GridAdapter) recyclerView.getAdapter();
        if (adapter == null) return null;

        int itemCount = adapter.getItemCount();
        int columnCount = 8; // 多少列
        int rowCount = (int) Math.ceil(itemCount / (float) columnCount);

        List<Bitmap> itemBitmaps = new ArrayList<>();
        List<Integer> rowHeights = new ArrayList<>();

        int totalHeight = 0;
        int itemWidth = recyclerView.getWidth() / columnCount;

        // 收集每个 item 的 Bitmap
        for (int i = 0; i < itemCount; i++) {

            List<Curriculum.Course> item = adapter.dataList.get(i); // 👈 获取数据项
            if (i > 8 && i % 8 != 0 && (item.isEmpty() || !kecheng.IsCourse(item.get(0)))) {
                Bitmap emptyBitmap = Bitmap.createBitmap(itemWidth, 1, Bitmap.Config.ARGB_8888); // 高度先设为1，稍后按行最大高度填充
                itemBitmaps.add(emptyBitmap);
                continue;
            }
            BaseAdapter.ViewHolder<GridItemBinding> holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            Bitmap itemBitmap = Bitmap.createBitmap(
                    holder.itemView.getMeasuredWidth(),
                    holder.itemView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas canvas = new Canvas(itemBitmap);
            holder.itemView.draw(canvas);

            itemBitmaps.add(itemBitmap);
        }

        // 计算每一行的最大高度（网格中，每一行高度由本行中 item 的最大高度决定）
        for (int row = 0; row < rowCount; row++) {
            int maxHeight = 0;
            for (int col = 0; col < columnCount; col++) {
                int index = row * columnCount + col;
                if (index >= itemCount) break;
                Bitmap bmp = itemBitmaps.get(index);
                maxHeight = Math.max(maxHeight, bmp.getHeight());
            }
            rowHeights.add(maxHeight);
            totalHeight += maxHeight;
        }

        // 合成整张 Bitmap
        Bitmap fullBitmap = Bitmap.createBitmap(recyclerView.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fullBitmap);
        canvas.drawColor(backcolor);
        Paint paint = new Paint();

        int y = 0;
        for (int row = 0; row < rowCount; row++) {
            int rowHeight = rowHeights.get(row);
            for (int col = 0; col < columnCount; col++) {
                int index = row * columnCount + col;
                if (index >= itemBitmaps.size()) break;

                Bitmap bmp = itemBitmaps.get(index);
                int x = col * itemWidth;
                canvas.drawBitmap(bmp, x, y, paint);
                bmp.recycle(); // 可选：回收内存
            }
            y += rowHeight;
        }

        return fullBitmap;
    }


}