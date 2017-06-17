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

import com.example.administrator.discovery_android.Connections.Register;
import com.example.administrator.discovery_android.R;
import com.example.administrator.discovery_android.Utils.NetworkUtil;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RegisterActivities extends AppCompatActivity{
    private ExecutorService es = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        final EditText registerAccount = (EditText) findViewById(R.id.registerAccount);
        final EditText registerPassword = (EditText) findViewById(R.id.password1);
        final EditText ensurePassword = (EditText) findViewById(R.id.password2);

        Button ensureRegister = (Button) findViewById(R.id.ensureRegister);
        Button changeLogin = (Button)findViewById(R.id.changeLogin);

        ensureRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = registerAccount.getText().toString();
                String pass1 = registerPassword.getText().toString();
                String pass2 = ensurePassword.getText().toString();

                if (username.equals("")){
                    Toast.makeText(RegisterActivities.this, "账号不能为空", Toast.LENGTH_SHORT).show();
                }else if (pass1.equals("")){
                    Toast.makeText(RegisterActivities.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (pass2.equals("")){
                    Toast.makeText(RegisterActivities.this, "请输入确认密码", Toast.LENGTH_SHORT).show();
                }

                if (!pass1.equals(pass2)){
                    Toast.makeText(RegisterActivities.this, "两次密码不匹配", Toast.LENGTH_SHORT).show();
                }else {
                    if (NetworkUtil.isNetworkAvailable(RegisterActivities.this)){
                        CountDownLatch c = new CountDownLatch(1);
                        Register r = new Register(username, pass1, c);
                        try {
                            Future f = es.submit(r);
                            c.await(2000, TimeUnit.MILLISECONDS);
                            if (r.isSuccessful()){
                                JSONObject jb = new JSONObject(f.get().toString());
                                if (jb.getInt("code") == 200){
                                    es.shutdown();
                                    finish();
                                    Intent intent = new Intent(RegisterActivities.this, LoginActivities.class);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(RegisterActivities.this, "用户名已被注册", Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(RegisterActivities.this, "注册失败，请稍后重试", Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(RegisterActivities.this, "网络连接不可用", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        changeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(RegisterActivities.this, LoginActivities.class);
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
