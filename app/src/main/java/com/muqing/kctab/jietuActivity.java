package com.muqing.kctab;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.muqing.wj;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class jietuActivity extends AppCompatActivity<ActivityJietuBinding> {
    public Bitmap bitmap;

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
        bitmap = recyclerViewToBitmapGrid(curriculum);
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
    String[] HList = new String[]{"日期", "一", "二", "三", "四", "五", "六", "日"};
    public Bitmap recyclerViewToBitmapGrid(Curriculum curriculum) {
        Paint paint = new Paint();

        // 渲染 HAdapter (星期标题)
        TableHAdapter hAdapter = new TableHAdapter(jietuActivity.this, Arrays.asList(HList));
        Bitmap hBitmap = renderAdapterToBitmapHorizontal(hAdapter);

        // 渲染 TimeAdapter (时间列)
        TableTimeAdapter timeAdapter = new TableTimeAdapter(jietuActivity.this, Arrays.asList(TableTimeData.tableTimeData));
        Bitmap timeBitmap = renderAdapterToBitmapVertical(timeAdapter);

        // 渲染每列 GridAdapter（课程格）
        List<List<List<Curriculum.Course>>> lists = kecheng.GetKcLei(new ArrayList<>(), curriculum);
        List<Bitmap> gridBitmaps = new ArrayList<>();
        int gridWidth = hBitmap.getWidth();
        int gridHeight = timeBitmap.getHeight() + 200;
        for (List<List<Curriculum.Course>> list : lists) {
            GridAdapter gridAdapter = new GridAdapter(jietuActivity.this, list);
            Bitmap bmp = GridTableToBitmap(gridAdapter);
            gridBitmaps.add(bmp);
        }
        Bitmap bigBitmap = Bitmap.createBitmap(gridWidth, gridHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bigBitmap);
        //设置背景色 获取系统背景色
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground});
        int backgroundColor = array.getColor(0, 0xFFF5F5F5);
        canvas.drawColor(backgroundColor);
        array.recycle();
        // 1. 绘制 HAdapter（横向标题），在时间列右侧
        canvas.drawBitmap(hBitmap, 0f, 0f, paint);

        // 2. 绘制时间列（左侧竖向）
        canvas.drawBitmap(timeBitmap, 0f, hBitmap.getHeight(), paint);
        // 3. 绘制 GridAdapter（课程格），右侧并排
        int xOffset = timeBitmap.getWidth();
        for (Bitmap bmp : gridBitmaps) {
            canvas.drawBitmap(bmp, xOffset, hBitmap.getHeight(), paint);
            xOffset += bmp.getWidth();
            bmp.recycle();
        }

        // 回收 HAdapter 和 TimeAdapter
        hBitmap.recycle();
        timeBitmap.recycle();
        return bigBitmap;
    }
    private Bitmap renderAdapterToBitmapHorizontal(RecyclerView.Adapter adapter) {
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return null;

        List<Bitmap> itemBitmaps = new ArrayList<>();
        int totalWidth = 0;
        int maxHeight = 0;

        // 逐个渲染 item
        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(new FrameLayout(jietuActivity.this), adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);

            // 使用 getRoot() 默认宽度
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(holder.itemView.getLayoutParams().width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());

            Bitmap bmp = Bitmap.createBitmap(holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            holder.itemView.draw(canvas);

            itemBitmaps.add(bmp);
            totalWidth += holder.itemView.getMeasuredWidth();
            maxHeight = Math.max(maxHeight, holder.itemView.getMeasuredHeight());
        }

        // 横向拼接
        Bitmap result = Bitmap.createBitmap(totalWidth, maxHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        int xOffset = 0;
        for (Bitmap bmp : itemBitmaps) {
            canvas.drawBitmap(bmp, xOffset, 0f, null);
            xOffset += bmp.getWidth();
            bmp.recycle();
        }

        return result;
    }
    private Bitmap renderAdapterToBitmapVertical(RecyclerView.Adapter adapter) {
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return null;

        List<Bitmap> itemBitmaps = new ArrayList<>();
        int totalHeight = 0;
        int columnWidth = 0;

        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(new FrameLayout(jietuActivity.this), adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);

            // 使用 getRoot() 默认宽度
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(holder.itemView.getLayoutParams().width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(holder.itemView.getLayoutParams().height, View.MeasureSpec.EXACTLY)
            );
            holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());

            Bitmap bmp = Bitmap.createBitmap(holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            holder.itemView.draw(canvas);

            itemBitmaps.add(bmp);
            columnWidth = holder.itemView.getMeasuredWidth();
            totalHeight += holder.itemView.getMeasuredHeight();
        }

        Bitmap result = Bitmap.createBitmap(columnWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        int yOffset = 0;
        for (Bitmap bmp : itemBitmaps) {
            canvas.drawBitmap(bmp, 0f, yOffset, null);
            yOffset += bmp.getHeight();
            bmp.recycle();
        }


        return result;
    }
    private Bitmap GridTableToBitmap(GridAdapter adapter) {
        int itemCount = adapter.getItemCount();
        if (itemCount == 0) return null;

        List<Bitmap> itemBitmaps = new ArrayList<>();
        int totalHeight = 0;
        int columnWidth = 0;
        for (int i = 0; i < itemCount; i++) {
            BaseAdapter.ViewHolder<GridItemBinding> holder = adapter.createViewHolder(new FrameLayout(jietuActivity.this), adapter.getItemViewType(i));
            adapter.onBindViewHolder(holder, i);
            holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(holder.itemView.getLayoutParams().width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(holder.itemView.getLayoutParams().height * adapter.dataList.get(i).get(0).height, View.MeasureSpec.EXACTLY)
            );
            Bitmap bmp = Bitmap.createBitmap(holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            if (!kecheng.IsCourse(adapter.dataList.get(i).get(0))) {
                bmp = Bitmap.createBitmap(holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(bmp);
            }else{

                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
            }
            holder.itemView.draw(canvas);
            itemBitmaps.add(bmp);
            columnWidth = holder.itemView.getMeasuredWidth();
            totalHeight += holder.itemView.getMeasuredHeight();
        }

        Bitmap result = Bitmap.createBitmap(columnWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        int yOffset = 0;
        for (Bitmap bmp : itemBitmaps) {
            canvas.drawBitmap(bmp, 0f, yOffset, null);
            yOffset += bmp.getHeight();
            bmp.recycle();
        }

        return result;
    }
}