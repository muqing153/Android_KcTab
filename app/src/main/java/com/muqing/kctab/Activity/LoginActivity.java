package com.muqing.kctab.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.AppCompatActivity;
import com.muqing.Dialog.DialogEditText;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ZhouAdapter;
import com.muqing.kctab.LoginApi;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivityLoginBinding;
import com.muqing.kctab.zhouDialog;
import com.muqing.wj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity<ActivityLoginBinding> {
    @Override
    protected ActivityLoginBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityLoginBinding.inflate(layoutInflater);
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                String token = data.getStringExtra("token");
                gj.sc("Token:" + token);
                if (LoginApi.IsToken(token)) {
                    EndToken(token);
                }
            }
        }
    });


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setToolsBar(binding.toolbar);
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        binding.account.setText(sharedPreferences.getString("account", ""));
        binding.password.setText(sharedPreferences.getString("password", ""));

        binding.loginButton.setOnClickListener(v -> {
            String account = Objects.requireNonNull(binding.account.getText()).toString();
            String password = Objects.requireNonNull(binding.password.getText()).toString();
            if (account.isEmpty()) {
                binding.account.setError("账号不能为空");
            } else if (password.isEmpty()) {
                binding.password.setError("密码不能为空");
            } else if (binding.isApbox.isChecked()) {
                binding.account.setError(null);
                binding.password.setError(null);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("account", account);
                edit.putString("password", password);
                edit.apply();
            }
            try {
                AlertDialog alertDialog = LoadIng();
                String encrypt = LoginApi.encrypt(password);
                new Thread(() -> {
                    LoginApi.Token = LoginApi.Login(account, encrypt);
                    gj.sc(LoginApi.Token);
                    if (LoginApi.IsToken(LoginApi.Token)) {
                        EndToken(LoginApi.Token);
                    } else {
                        runOnUiThread(() -> gj.ts(LoginActivity.this, "登陆失败"));
                    }
                    runOnUiThread(alertDialog::dismiss);
                }).start();
            } catch (Exception e) {
                gj.sc(e);
            }
        });

        binding.other1.setOnClickListener(view -> {
            String url = "http://protal.qdpec.edu.cn:19580/tp_wp/h5?act=wp/wxH5/appSquare/383304265056256#act=wp/wxH5/appSquare/383304265056256";
            Intent intent = new Intent(LoginActivity.this, WebActivity.class);
            intent.putExtra("url", url);
//            startActivity(intent);
            resultLauncher.launch(intent);
        });

        binding.other2.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, WebActivity.class);
            intent.putExtra("url", "http://jw.qdpec.edu.cn:8088");
            resultLauncher.launch(intent);
        });

        binding.other3.setOnClickListener(view -> new DialogEditText(LoginActivity.this, "请输入token") {
            @Override
            public boolean setNegative(View view) {
                return true;
            }

            @Override
            public boolean setPositive(View view) {
                if (LoginApi.IsToken(getEditText())) {
                    EndToken(getEditText());
                    return true;
                }
                return false;
            }
        }.setMessage("输入Token(浏览器F12课程表处获取)"));


        binding.syncButton.setOnClickListener(view -> {
            zhouDialog zhouDialog = new zhouDialog(LoginActivity.this) {
                @Override
                public void click(int position) {
                    LoginActivity.this.binding.syncButton.setText(zhouAdapter.dataList.get(position));
                }
            };
            zhouDialog.zhouAdapter.gaoliang = false;
            zhouDialog.binding.fh.setVisibility(View.GONE);
            zhouDialog.zhouAdapter.dataList.add(0, "ALL");
        });
        Intent intent = getIntent();
        if (intent.getStringExtra("sync") != null) {
            binding.syncButton.setEnabled(true);
        }

        binding.other4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");

                // 可选：限制选择单个文件
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                fhkczip.launch(new String[]{"*/*"});
            }
        });
    }

    ActivityResultLauncher<String[]> fhkczip = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
//                    // 获取文件路径或内容
//                    String filePath = null;
//                    String[] projection = {MediaStore.Files.FileColumns.DATA};
//                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//                    if (cursor != null && cursor.moveToFirst()) {
//                        int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
//                        filePath = cursor.getString(columnIndex);
//                        cursor.close();
//                    }
//                    gj.sc(filePath);
                    // 或者直接使用ContentResolver打开输入流
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        gj.sc("成功获取文件流"); // 你的回调方法
                        File outputDir = new File(wj.data, "TabList");
                        if (!outputDir.exists()) outputDir.mkdirs();
                        boolean b = YourKczipOpenActivity.unzipFromUri(LoginActivity.this, inputStream, outputDir);
                        if (b) {
                            finish();
                        }
                        // 处理输入流...
                    } catch (Exception e) {
                        gj.sc("文件读取失败: " + e.getMessage());
                    }
                }
            });

    private void EndToken(String token) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("token", token);
        resultIntent.putExtra("sync", binding.syncButton.getText().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

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
}
