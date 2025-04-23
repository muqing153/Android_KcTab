package com.muqing.kctab.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.muqing.AppCompatActivity;
import com.muqing.Dialog.DialogEditText;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ZhouAdapter;
import com.muqing.kctab.LoginApi;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ActivityLoginBinding;
import com.muqing.kctab.zhouDialog;

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
                String encrypt = LoginApi.encrypt(password);
                new Thread(() -> {
                    LoginApi.Token = LoginApi.Login(account, encrypt);
                    gj.sc(LoginApi.Token);
                    if (LoginApi.IsToken(LoginApi.Token)) {
                        EndToken(LoginApi.Token);
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
    }

    private void EndToken(String token) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("token", token);
        resultIntent.putExtra("sync", binding.syncButton.getText().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
