package com.example.administrator.discovery_android.Connections;

import com.example.administrator.discovery_android.FinalStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class PostEvent implements Runnable{
    private double x;
    private double y;
    private String content;
    private int id;
    private String type;
    private String title;
    private String file;
    private String path;
    private boolean isSuccessful = false;
    private CountDownLatch c;

    public PostEvent(double x, double y, String content, int id, String type, String title, String file, String path, CountDownLatch c){
        this.x = x;
        this.y = y;
        this.content = content;
        this.id = id;
        this.type = type;
        this.title = title;
        this.file = file;
        this.path = path;
        this.c = c;
    }

    @Override
    public void run(){
        HttpURLConnection connection;
        PrintWriter out;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";

        try {
            URL url = new URL(FinalStrings.HOST + "/events");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

            connection.setConnectTimeout(5000);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject jb = new JSONObject();
            jb.put("positionX", x);
            jb.put("positionY", y);
            jb.put("content", content);
            jb.put("studentId", id);
            jb.put("type", type);
            jb.put("title", title);
            jb.put("picPath", path);
            jb.put("file", file);

            out = new PrintWriter(connection.getOutputStream());
            out.print(jb);
            out.flush();

            if (connection.getResponseCode() == 200){
                isSuccessful = true;
            }
            c.countDown();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}