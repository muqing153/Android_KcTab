package com.muqing.kctab.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.muqing.kctab.databinding.ItemZhouBoxBinding;
import com.muqing.kctab.databinding.ZhouDialogBinding;
import com.muqing.kctab.zhouDialog;
import com.muqing.wj;

import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
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
                gj.sc("Token:" + token);
                if (LoginApi.IsToken(token)) {
                    EndToken(token);
                }
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
            zhouList.add(MainActivity.benzhou - 1);
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
            zhouList.add(MainActivity.benzhou - 1);
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
    }

    /**
     * kczip导入
     */
    ActivityResultLauncher<String[]> fhkczip = registerForActivityResult(new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        gj.sc("成功获取文件流"); // 你的回调方法
                        File outputDir = new File(wj.data, "kchuancun");
                        if (!outputDir.exists()) outputDir.mkdirs();
                        boolean b = YourKczipOpenActivity.unzipFromUri(LoginActivity.this, inputStream, outputDir);
                        if (b) {
                            File[] files = outputDir.listFiles();
                            if (files != null && files.length > 0) {
                                Gson gson = new Gson();
                                for (File file : files){
                                    Curriculum curriculum = gson.fromJson(wj.dqwb(file), Curriculum.class);
                                    KcApi.putjsonkc(curriculum,gson);
                                }
                            }
                            wj.sc(outputDir);
                            binding.syncButton.setText("kczip");
                            EndToken(null);
                        }
                    } catch (Exception e) {
                        gj.sc("文件读取失败: " + e.getMessage());
                    }
                }
            });

    private void EndToken(String token) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("token", token);
        if (zhouList.isEmpty()) {
            resultIntent.putExtra("sync", binding.syncButton.getText().toString());
        }else{
            resultIntent.putExtra("sync", new Gson().toJson(zhouList));
        }
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

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
