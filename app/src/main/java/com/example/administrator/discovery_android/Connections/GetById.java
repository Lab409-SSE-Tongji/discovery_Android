package com.example.administrator.discovery_android.Connections;

import com.example.administrator.discovery_android.FinalStrings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class GetById implements Callable{
    private boolean isSuccessful = false;
    private int id;

    public GetById(int id){
        this.id = id;
    }

    @Override
    public Object call(){
        HttpURLConnection connection;
        BufferedReader in;
        Object o = new Object();
        try {
            URL url = new URL(FinalStrings.HOST + "/events/" + id);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            //返回成功码200则解析
            if(connection.getResponseCode() == 200){
                while ((line = in.readLine()) != null){
                    System.out.println(line);
                    o = map;
                    isSuccessful = true;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return o;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
