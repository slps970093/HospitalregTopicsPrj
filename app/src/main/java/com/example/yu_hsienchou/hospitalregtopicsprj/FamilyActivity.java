package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FamilyActivity extends AppCompatActivity {
    public ListView lst;
    public ArrayList<String> showData = new ArrayList();
    public ArrayList cid = new ArrayList();
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        lst = (ListView) findViewById(R.id.lst);
        new Familytesk().execute();
        lst.setOnItemClickListener(lstPreferListener);
    }
    private ListView.OnItemClickListener lstPreferListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String sel =  parent.getItemAtPosition(position).toString();
            int i = showData.indexOf(sel);
            Log.e("sel",cid.get(i).toString());
            Bundle bundle = new Bundle();
            bundle.putString("id",cid.get(i).toString());
            bundle.putBoolean("isDoctor",false);
            Intent intent = new Intent();
            intent.setClass(FamilyActivity.this,ReservationActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    class Familytesk extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(FamilyActivity.this);
            mDialog.setTitle(R.string.pDialog_getData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_getData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.cancel();
            String str = String.valueOf(showData.size());
            Log.e("size", str);
            Log.e("1", showData.get(1).toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FamilyActivity.this, android.R.layout.simple_list_item_1, showData);
            lst.setAdapter(adapter);
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(2000);
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/family";
                URL mUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() == 200) {
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                    Log.e("Msg", msg);
                    /*
                                        JSONObject jsonObject = new JSONObject(msg);
                                        JSONArray name = jsonObject.getJSONArray("name");
                                        JSONArray id = jsonObject.getJSONArray("id");
                                        for (int i = 0; i <= name.length() - 1; i++) {
                                            cid.add(id.get(i));
                                            showData.add(name.getString(i).toString());
                                        }
                                        */
                    JSONArray jsonArray = new JSONArray(msg);
                    int tmp = 0;
                    for(int i = 0; i<=jsonArray.length()-1;i++){
                        cid.add(jsonArray.getJSONObject(tmp).getInt("id"));
                        showData.add(jsonArray.getJSONObject(tmp).getString("name"));
                        tmp++;
                    }
                } else {
                    InputStream is = conn.getErrorStream();
                    ByteArrayOutputStream message = new ByteArrayOutputStream();
                    int len = 0;
                    byte buffer[] = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        message.write(buffer, 0, len);
                    }
                    is.close();
                    message.close();
                    String msg = new String(message.toByteArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
