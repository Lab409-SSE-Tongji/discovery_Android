package com.example.administrator.discovery_android.Connections;

import com.example.administrator.discovery_android.FinalStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class Register implements Callable{
    private boolean isSuccessful = false;
    private String username;
    private String password;
    private CountDownLatch c;

    public Register(String username, String password, CountDownLatch c){
        this.username = username;
        this.password = password;
        this.c = c;
    }

    @Override
    public Object call() throws Exception {
        HttpURLConnection connection;
        PrintWriter out;
        BufferedReader in;
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(FinalStrings.HOST + "/accounts");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

            connection.setConnectTimeout(5000);

            connection.setDoInput(true);
            connection.setDoOutput(true);

            JSONObject jb = new JSONObject();
            jb.put("name", username);
            jb.put("pwd", password);

            out = new PrintWriter(connection.getOutputStream());
            out.print(jb);
            out.flush();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            //返回成功码200则解析
            if(connection.getResponseCode() == 200){
                while ((line = in.readLine()) != null){
                    sb.append(line);
                    isSuccessful = true;
                }
            }

            c.countDown();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return sb;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
