package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    private TextView rocid_Txt;
    private Button submitbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rocid_Txt = (TextView) findViewById(R.id.rocidTxt);
        submitbtn = (Button) findViewById(R.id.okbtn);
        submitbtn.setOnClickListener(btnOnclick);
    }
    private Button.OnClickListener btnOnclick = new Button.OnClickListener(){
        public void onClick(View v){
            Log.e("btn","OK");
            Thread thread = new Thread(login);
            thread.start();
        }
    };
    public Runnable login = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            Log.e("thread","OK");
            String rocid = rocid_Txt.getText().toString();
            String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/auth/login";
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                String data = "cRocid="+ URLEncoder.encode(rocid,"UTF-8");
                Log.e("data=",data);
                OutputStream out = conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                String cde = Integer.toString(conn.getResponseCode());
                Log.e("code",cde);
                String cookieStr =conn.getHeaderField("Set-Cookie");
                if(conn.getResponseCode()==200){
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while((len = is.read(buffer))!=-1){
                        message.write(buffer,0,len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("msg",msg);
                    JSONObject jsonObject = new JSONObject(msg);
                    boolean status = jsonObject.getBoolean("status");
                    if(status){
                        //儲存SESSION
                        try{
                            FileOutputStream fos = openFileOutput("session.txt",MODE_PRIVATE);
                            fos.write(cookieStr.getBytes());
                            fos.close();
                            Intent intent = new Intent();
                            intent.setClass(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    Toast.makeText(LoginActivity.this,R.string.Sys_Auth_Failed,Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
}
