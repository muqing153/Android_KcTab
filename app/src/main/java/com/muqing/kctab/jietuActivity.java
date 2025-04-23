package com.muqing.kctab;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.databinding.ActivityJietuBinding;
import com.muqing.wj;

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
        }else{
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
        MenuItem baocun = menu.add(0, 0, 0, "保存");
        baocun.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        Objects.requireNonNull(baocun.getActionView()).setId(0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {
            //将图片保存到本地
            item.setEnabled(false);
//            new wj.xiangce().saveImageToGallery(this, bitmap);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onOptionsItemSelected(item);
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
}