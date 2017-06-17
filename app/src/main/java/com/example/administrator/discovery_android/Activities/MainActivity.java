package com.example.administrator.discovery_android.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.example.administrator.discovery_android.Connections.GetEvent;
import com.example.administrator.discovery_android.R;
import com.example.administrator.discovery_android.Utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    private static final int REQUEST = 1;
    private static final LatLng TJU = new LatLng(31.286054, 121.215252);
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);

    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    private Marker locMarker;

    private String path;
    private int eventId = 0;
    private double lat;
    private double lng;
    private Set<String> idSet = new HashSet<>();

    private ExecutorService es = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10));

    private GetEvent getEvent = new GetEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        aMap = mMapView.getMap();
        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                TJU, 16, 30, 30)));

        locationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        ImageButton imageButton = (ImageButton) findViewById(R.id.snap) ;
        final ImageButton freshButton = (ImageButton) findViewById(R.id.fresh) ;
        final ImageButton detailButton = (ImageButton) findViewById(R.id.detail) ;

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 图片存储路径
                path = Environment.getExternalStorageDirectory().getPath()
                        + "/Discovery" + SDF.format(new Date()) + ".png";
                // 打开编辑界面
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(new File(path));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUEST);
            }
        });

        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventId != 0){
                    Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                    intent.putExtra("id", eventId);
                    startActivity(intent);
                }
            }
        });

        freshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fresh();
            }
        });

        startLocation();

        fresh();
    }

    private void fresh(){
        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
        for (Marker i : mapScreenMarkers){
            String tmp = (String)i.getObject();
            if (idSet.contains(tmp)){
                i.remove();
            }
        }
        try{
            if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
                if (!es.isShutdown()) {
                    Future getEventFuture = es.submit(getEvent);
                    JSONObject jb = new JSONObject(getEventFuture.get().toString());
                    JSONArray arr = (JSONArray)jb.get("data");
                    for (int i = 0; i < arr.length(); i++){
                        JSONObject tmp = (JSONObject)arr.get(i);
                        int id = tmp.getInt("eventId");
                        double x = tmp.getDouble("positionX");
                        double y = tmp.getDouble("positionY");
                        String title = tmp.getString("title");
                        String type = tmp.getString("type");
                        LatLng latLng = new LatLng(x, y);
                        MarkerOptions options = new MarkerOptions().position(latLng).snippet(title);

                        switch (type){
                            case "speech":
                                options.title("speech");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.issue)));
                                break;
                            case "new":
                                options.title("new");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.issue)));
                                break;
                            case "performance":
                                options.title("performance");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.issue)));
                                break;
                            case "novelty":
                                options.title("novelty");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fun)));
                                break;
                            case "bargain":
                                options.title("bargain");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fun)));
                                break;
                            default:
                                options.title("others");
                                options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.others)));
                        }
                        Marker marker = aMap.addMarker(options);
                        marker.setObject(id);
                        idSet.add(String.valueOf(id));
                    }
                }
            }else {
                Toast.makeText(MainActivity.this, "网络连接不可用", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // 定义 Marker 点击事件监听
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                eventId = (int) marker.getObject();
                Toast.makeText(MainActivity.this, eventId, Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
    }

    private AMapLocationClientOption getDefaultOption(){
        String strInterval = 5000 + "";
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(Long.valueOf(strInterval));//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                Animation animation = new TranslateAnimation(latLng);

                long duration = 1000L;
                animation.setDuration(duration);
                animation.setInterpolator(new LinearInterpolator());

                if (locMarker != null){
                    locMarker.setAnimation(animation);
                    locMarker.startAnimation();
                }else {
                    locMarker = aMap.addMarker(new MarkerOptions().position(latLng).title("你的位置").snippet("DefaultMarker"));
    //                    locMarker.setObject("loc");
                }
            }
        }
    };

    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回数据
            if (requestCode == REQUEST) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                intent.putExtra("uri", path);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
            }
        }else {
            Toast.makeText(MainActivity.this, "未能读取到图片，请重试", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //后退键询问是否退出
        if(keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Discovery");
            alertDialog.setMessage("Exit Discovery ?");
            alertDialog.setPositiveButton("Cancel",new DialogInterface.OnClickListener() {
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
        return false;
    }
}