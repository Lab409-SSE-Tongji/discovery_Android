package com.example.administrator.discovery_android;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/6/11.
 */

class PostEvent implements Runnable{
    private double x;
    private double y;
    private String content;
    private int id;
    private boolean isSuccessful = false;
    private CountDownLatch c;

    PostEvent(double x, double y, String content, int id, CountDownLatch c){
        this.x = x;
        this.y = y;
        this.content = content;
        this.id = id;
        this.c = c;
    }

    @Override
    public void run(){
        HttpURLConnection connection;
        PrintWriter out;
        BufferedReader in;

        try {
            URL url = new URL("http://192.168.1.102:8080/events");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject jb = new JSONObject();
            jb.put("positionX", x);
            jb.put("positionY", y);
            jb.put("content", content);
            jb.put("studentId", id);

            out = new PrintWriter(connection.getOutputStream());
            out.print(jb);
            out.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            //返回成功码200则解析
            if(connection.getResponseCode() == 200){
                while ((line = in.readLine()) != null){
                    System.out.println(line);
                    isSuccessful = true;
                }
            }

            c.countDown();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
    }

    boolean isSuccessful() {
        return isSuccessful;
    }
}