package com.muqing.kctab.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.Dialog.DialogEditText;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ZhouBoxAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.KcApi;
import com.muqing.kctab.LoginApi;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivityLoginBinding;
import com.muqing.kctab.databinding.DialogZhouBoxBinding;
import com.muqing.wj;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
                new LoadKc(token) {
                    @Override
                    public void yes() {
                        EndToken();
                    }

                    @Override
                    public void error(String error) {
                        runOnUiThread(() -> {
                            gj.ts(LoginActivity.this, error);
                        });
                    }
                };
            }
        }
    });

    public List<Integer> zhouList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
//        String XYIP = getLocalIpAddress();
//        gj.sc(XYIP);
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        binding.account.setText(sharedPreferences.getString("account", ""));
        binding.password.setText(sharedPreferences.getString("password", ""));
//        gj.sc(zhouList.size());
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
                String encrypt = LoginApi.encrypt(password);
                new Thread(() -> {
                    LoginApi.Token = LoginApi.Login(account, encrypt);
                    if (LoginApi.IsToken(LoginApi.Token)) {
                        new LoadKc(LoginApi.Token) {
                            @Override
                            public void yes() {
                                EndToken();
                            }

                            @Override
                            public void error(String error) {
                                runOnUiThread(() -> {
                                    gj.ts(LoginActivity.this, error);
                                });
                            }
                        };
                    } else {
                        runOnUiThread(() -> gj.ts(LoginActivity.this, "登陆失败"));
                    }
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
                    new LoadKc(LoginApi.Token) {
                        @Override
                        public void yes() {
                            EndToken();
                        }

                        @Override
                        public void error(String error) {
                            runOnUiThread(() -> {
                                gj.ts(LoginActivity.this, error);
                            });
                        }
                    };
                    return true;
                }
                return false;
            }
        }.setMessage("输入Token(浏览器F12课程表处获取)"));

        binding.syncButton.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this);
            DialogZhouBoxBinding zhouDialogBinding = DialogZhouBoxBinding.inflate(LayoutInflater.from(LoginActivity.this));
//            zhouDialogBinding.fh.setText("同步课程表");
            bottomSheetDialog.setContentView(zhouDialogBinding.getRoot());

            ArrayList<String> objects = new ArrayList<>();
            for (int i = 0; i < MainActivity.TabList.size(); i++) {
                objects.add(String.valueOf(i + 1));
            }
            int itemWidthDp = 100;
            float density = getResources().getDisplayMetrics().density;
            int itemWidthPx = (int) (itemWidthDp * density + 0.5f);
            zhouList.clear();
            zhouList.add(MainActivity.benzhou);
            zhouDialogBinding.getRoot().post(() -> {
                int width = zhouDialogBinding.getRoot().getWidth();
                int spanCount = Math.max(5, width / itemWidthPx);
                zhouDialogBinding.recyclerview.setLayoutManager(new GridLayoutManager(LoginActivity.this, spanCount));
                zhouDialogBinding.recyclerview.setAdapter(new ZhouBoxAdapter(LoginActivity.this, objects, zhouDialogBinding, zhouList));
            });
            bottomSheetDialog.show();
        });
        Intent intent = getIntent();
        if (intent.getStringExtra("sync") != null) {
            binding.syncButton.setEnabled(true);
            zhouList.clear();
            zhouList.add(MainActivity.benzhou);
            binding.syncButton.setText(intent.getStringExtra("sync"));
        }

        binding.other4.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent1.addCategory(Intent.CATEGORY_OPENABLE);
            intent1.setType("*/*");

            // 可选：限制选择单个文件
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            fhkczip.launch(new String[]{"*/*"});
        });

        String text = "使用登陆功能请同意 <a href='https://yourdomain.com/user'>《用户协议》</a> 与 <a href='https://yourdomain.com/privacy'>《隐私政策》</a>";
        binding.checkbox.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        binding.checkbox.setMovementMethod(LinkMovementMethod.getInstance());
        boolean xieyi = sharedPreferences.getBoolean("xieyi", false);
        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("xieyi", isChecked);
            edit.apply();
            setXieyi(isChecked);
        });
        binding.checkbox.setChecked(xieyi);
        setXieyi(xieyi);
    }

    /**
     * kczip导入
     */
    ActivityResultLauncher<String[]> fhkczip = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                gj.sc("成功获取文件流"); // 你的回调方法
                File outputDir = new File(wj.data, "kchuancun");
                if (!outputDir.exists()) outputDir.mkdirs();
                boolean b = YourKczipOpenActivity.unzipFromUri(LoginActivity.this, inputStream, outputDir);
                if (b) {
                    List<File> allFiles = wj.getAllFiles(outputDir);
                    Gson gson = new Gson();
                    for (File file : allFiles) {
                        try {
                            Curriculum curriculum = gson.fromJson(wj.dqwb(file), Curriculum.class);
                            KcApi.putjsonkc(curriculum, gson);
                        } catch (Exception e) {
                            gj.sc("这个文件不是正确的数据：" + e);
                        }
                    }
                    wj.sc(outputDir);
//                            binding.syncButton.setText("kczip");
                    EndToken();
                }
            } catch (Exception e) {
                gj.sc("文件读取失败: " + e.getMessage());
            }
        }
    });


    private abstract class LoadKc extends Thread {

        public AlertDialog alertDialog;

        public LoadKc(String data) {
            alertDialog = LoadIng();
            LoginApi.Token = data;
            start();
        }

        @Override
        public void run() {
            if (zhouList.isEmpty()) {
                boolean load = KcApi.Load(LoginApi.Token);
                if (!load) {
                    error("加载失败");
                } else {
                    yes();
                }
            }
            try {
                gj.sc(zhouList.size());
                boolean load = false;
                for (int i = 0; i < zhouList.size(); i++) {
//                    gj.sc("开始同步第" + (i + 1) + "周");
                     load = KcApi.Load(zhouList.get(0));
                    if (!load) {
                        error(zhouList.get(0) + "加载失败");
                    }
                }
                if (load) {
                    yes();
                }
            } catch (Exception e) {
                gj.sc("同步失败:" + e);
            }
//            yes();
            runOnUiThread(alertDialog::dismiss);
        }

        public abstract void yes();

        public abstract void error(String error);
    }

    private void EndToken() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("kc", true);
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

    private void setXieyi(boolean xieyi) {
        binding.other1.setEnabled(xieyi);
        binding.other2.setEnabled(xieyi);
        binding.other3.setEnabled(xieyi);
        binding.loginButton.setEnabled(xieyi);
    }
}
