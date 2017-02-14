package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button hospLocBtn;
    private Button authBtn;
    private Button resBtn;
    private Button logoutBtn;
    private Button regDelButton;
    private Button todayregBtn;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isConn = isConnected(); //檢查連線狀態
        //regBtn = (Button) findViewById(R.id.regbtn);
        hospLocBtn = (Button)findViewById(R.id.hospLocBtn);
        authBtn = (Button) findViewById(R.id.authBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        resBtn = (Button)findViewById(R.id.resbtn);
        regDelButton = (Button)findViewById(R.id.resDelete);
        todayregBtn = (Button) findViewById(R.id.todayRegBtn);

        if(isConn){
            new Sysinit().execute();
            resBtn.setOnClickListener(btnOnclick);
            //regBtn.setOnClickListener(btnOnclick);
            authBtn.setOnClickListener(btnOnclick);
            logoutBtn.setOnClickListener(btnOnclick);
            regDelButton.setOnClickListener(btnOnclick);
            hospLocBtn.setOnClickListener(btnOnclick);
            todayregBtn.setOnClickListener(btnOnclick);
        }else{
            //跳出錯誤訊息，並強制關閉，或禁用所有功能
            AlertDialog.Builder NotNetworkmsg = new AlertDialog.Builder(MainActivity.this);
            NotNetworkmsg.setTitle(R.string.Error_Not_Network_title);
            NotNetworkmsg.setMessage(R.string.Error_Not_Network_Msg);
            NotNetworkmsg.setPositiveButton(R.string.default_dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,R.string.Sys_close_App_NotConn,Toast.LENGTH_LONG).show();
                    System.exit(0); //強制關閉
                }
            });
            NotNetworkmsg.setNegativeButton(R.string.default_dialog_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,R.string.Sys_NotConn_close,Toast.LENGTH_SHORT).show();
                    //關閉所有按鈕

                    authBtn.setEnabled(false);
                    logoutBtn.setEnabled(false);
                }
            });
            NotNetworkmsg.show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    private Button.OnClickListener btnOnclick = new Button.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            if(v.getId()==R.id.authBtn){
                intent.setClass(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.logoutBtn){
                logoutBtn.setEnabled(false);
                authBtn.setEnabled(true);
                resBtn.setEnabled(false);
                regDelButton.setEnabled(false);
                new logoutTask().execute();

            }
            if(v.getId()==R.id.resbtn){
                intent.setClass(MainActivity.this,reservationMenuActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.resDelete){
                intent.setClass(MainActivity.this,resDeleteActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.hospLocBtn){
                intent.setClass(MainActivity.this,HospLocActivity.class);
                startActivity(intent);
            }
            if(v.getId() == R.id.todayRegBtn){
                Log.e("hello","world");
                intent.setClass(MainActivity.this,TodayRegActivity.class);
                startActivity(intent);
            }

        }
    };
    /*
         * 檢查連線狀態
         *
         */
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
    /*
         * 程式初始化
         *
         */
    private class Sysinit extends AsyncTask<Void,Void,Void> {
        private boolean isLogin = false;
        @Override
        protected Void doInBackground(Void... params) {
            try{
                Looper.prepare();
                String cookieStr = getCookie(); //抽取Cookie資訊
                Log.e("cook",cookieStr);
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/auth/status";
                URL mUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Cookie",cookieStr);
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                String cde = Integer.toString(conn.getResponseCode());
                Log.e("code",cde);
                if(conn.getResponseCode() == 200){
                    InputStream is = conn.getInputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1){
                        message.write(buffer,0,len);
                    }
                    is.close();
                    message.close();
                    isLogin = true;

                }else{
                    isLogin = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.cancel();
            if(isLogin){
                mDialog.cancel();
                //regBtn.setEnabled(false);
                authBtn.setEnabled(false);
                Toast.makeText(MainActivity.this,R.string.Sys_Auth_Success,Toast.LENGTH_SHORT).show();
            }else{
                mDialog.cancel();
                AlertDialog.Builder AuthAlert = new AlertDialog.Builder(MainActivity.this);
                AuthAlert.setTitle(R.string.AuthAlert_title);
                AuthAlert.setMessage(R.string.AuthAlert_msg);
                AuthAlert.setPositiveButton(R.string.default_dialog_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, R.string.Sys_Auth_Failed, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });
                AuthAlert.setNegativeButton(R.string.default_dialog_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutBtn.setEnabled(false);
                        resBtn.setEnabled(false);
                        Toast.makeText(MainActivity.this, R.string.Sys_Auth_Failed, Toast.LENGTH_SHORT).show();
                    }
                });
                AuthAlert.show();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setTitle(R.string.pDialog_init_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_init_Msg));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
            super.onPreExecute();
        }
    }
    /*
         * 登出執行
         *
         */
    class logoutTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try{
                Looper.prepare();
                String cookieStr = getCookie(); //抽取Cookie資訊
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/auth/logout";
                URL mUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("Cookie",cookieStr);
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                String cde = Integer.toString(conn.getResponseCode());
                if(conn.getResponseCode()==200){
                    InputStream is = conn.getInputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1){
                        message.write(buffer,0,len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("msg",msg);
                    //刪除檔案
                    File f = new File(getFilesDir() + "/" + "session.txt");
                    f.delete();
                    Toast.makeText(MainActivity.this,R.string.logout_success_msg,Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            Looper.loop();
            return null;
        }
    }
    public String getCookie() throws IOException {
        FileInputStream input = openFileInput("session.txt");
        byte[] temp = new byte[1024];
        StringBuilder sb = new StringBuilder("");
        int len = 0;
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        input.close();
        return sb.toString();
    }
}
