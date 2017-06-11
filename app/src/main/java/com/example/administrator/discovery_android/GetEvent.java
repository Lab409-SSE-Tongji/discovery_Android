package com.example.administrator.discovery_android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/11.
 */

public class GetEvent implements Runnable{
    private boolean isSuccessful = false;

    @Override
    public void run(){
        HttpURLConnection connection;
        BufferedReader in;
        try {
            URL url = new URL("http://192.168.1.102:8080/events?eventId=1");
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
                    isSuccessful = true;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}