package com.example.administrator.discovery_android.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.discovery_android.Connections.GetById;
import com.example.administrator.discovery_android.R;
import com.example.administrator.discovery_android.Utils.ImageCodingUtil;

import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventDetailActivity extends AppCompatActivity{
    private final ExecutorService es = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_main);

        // 获取绑定的uri数据
        Bundle bundle = EventDetailActivity.this.getIntent().getExtras();
        int id = bundle.getInt("id");

        ImageView imageView = (ImageView) findViewById(R.id.detailPic);
        TextView contentText = (TextView) findViewById(R.id.content);
        TextView timeText = (TextView) findViewById(R.id.time);

        try {
            GetById getById = new GetById(id);
            Future f = es.submit(getById);
            JSONObject jb = new JSONObject(f.get().toString());
            if (getById.isSuccessful()){
                if (jb.getInt("code") == 200){
                    JSONObject data = jb.getJSONObject("data");
                    String str = data.getString("file");
                    String date = data.getString("time");
                    String content = data.getString("content");
                    contentText.setText(content);
                    timeText.setText(date);
                    imageView.setImageBitmap(ImageCodingUtil.getBitmapFromString(str));
                }else {
                    Toast.makeText(EventDetailActivity.this, "未能获取该事件信息", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(EventDetailActivity.this, "服务器出错啦", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

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
