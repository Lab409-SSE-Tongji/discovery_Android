package com.example.administrator.discovery_android.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.discovery_android.Connections.PostEvent;
import com.example.administrator.discovery_android.R;
import com.example.administrator.discovery_android.Utils.NetworkUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageActivity extends AppCompatActivity{
    private final CountDownLatch c = new CountDownLatch(1);
    private final ExecutorService es = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    private EditText contentField;
    private EditText titleField;
    private Bitmap bitmap;
    private PostEvent postEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_main);

        ImageView imageView = (ImageView) findViewById(R.id.showPic);
        ImageButton yes = (ImageButton) findViewById(R.id.yes);
        ImageButton no = (ImageButton) findViewById(R.id.no);
        contentField = (EditText) findViewById(R.id.content);
        titleField = (EditText) findViewById(R.id.eTitle);

        // 获取绑定的uri数据
        Bundle bundle = MessageActivity.this.getIntent().getExtras();
        String path = bundle.getString("uri");
        final double lat = bundle.getDouble("lat");
        final double lng = bundle.getDouble("lng");

        //如果为空则返回
        if (path == null || Double.isNaN(lat) || Double.isNaN(lng)){
            startActivity(new Intent(MessageActivity.this, MainActivity.class));
            return;
        }

        try (FileInputStream fis = new FileInputStream(path)){
            bitmap = BitmapFactory.decodeStream(fis);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                final String content = contentField.getText().toString();
                final String title = titleField.getText().toString();

                postEvent = new PostEvent(lat, lng, content, 21, title, "wa", c);
                try {
                    if (NetworkUtil.isNetworkAvailable(MessageActivity.this)){
                        if (!es.isShutdown()){
                            es.execute(postEvent);
                            System.out.println(postEvent);
                            c.await(2000, TimeUnit.MILLISECONDS);
                            if (postEvent.isSuccessful()){
                                Toast.makeText(MessageActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                es.shutdown();
                                finish();
                            }else {
                                Toast.makeText(MessageActivity.this, "上传失败，请稍后再试", Toast.LENGTH_LONG).show();
                            }
                        }
                    }else {
                        Toast.makeText(MessageActivity.this, "网络连接不可用", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ensureQuit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //后退键询问是否退出
        if(keyCode==KeyEvent.KEYCODE_BACK){
            ensureQuit();
        }
        return false;
    }

    private void ensureQuit(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageActivity.this);
        alertDialog.setTitle("Discovery");
        alertDialog.setMessage("Cancel Editing ?");
        alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        alertDialog.show();
    }
}
