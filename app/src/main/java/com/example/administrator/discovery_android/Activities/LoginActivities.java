package com.example.administrator.discovery_android.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.discovery_android.Connections.Login;
import com.example.administrator.discovery_android.R;
import com.example.administrator.discovery_android.Utils.NetworkUtil;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LoginActivities extends AppCompatActivity{
    private final ExecutorService es = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    private EditText account;
    private EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);

        final Button login = (Button) findViewById(R.id.login);
        final Button register = (Button) findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String acc = account.getText().toString();
                final String pwd = password.getText().toString();

                if (acc.equals("")){
                    Toast.makeText(LoginActivities.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                }else if (pwd.equals("")){
                    Toast.makeText(LoginActivities.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else {
                    Login login1 = new Login(acc, pwd);
                    if (NetworkUtil.isNetworkAvailable(LoginActivities.this)){
                        try {
                            Future f = es.submit(login1);
                            JSONObject jb = new JSONObject(f.get().toString());
                            if (login1.isSuccessful()){
                                if (jb.getInt("code") == 200){
                                    es.shutdown();
                                    Toast.makeText(LoginActivities.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                    Intent intent = new Intent(LoginActivities.this, MainActivity.class);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(LoginActivities.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(LoginActivities.this, "登陆失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(LoginActivities.this, RegisterActivities.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
