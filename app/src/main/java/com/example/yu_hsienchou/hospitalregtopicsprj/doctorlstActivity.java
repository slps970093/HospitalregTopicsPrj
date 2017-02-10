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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class doctorlstActivity extends AppCompatActivity {
    private ProgressDialog mDialog;
    private ArrayList pID = new ArrayList();
    private ArrayList<String> pNAME = new ArrayList();
    private ListView lst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctorlst);
        lst = (ListView) findViewById(R.id.doctorLst);
        lst.setOnItemClickListener(lstOnItemClick);
        new getDoctorTask().execute();
    }
    private ListView.OnItemClickListener lstOnItemClick = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String sel =  parent.getItemAtPosition(position).toString();
            int tmp = pNAME.indexOf(sel);
            //傳值
            Intent intent = new Intent();
            intent.setClass(doctorlstActivity.this,ReservationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isDoctor",true);
            bundle.putString("id",pID.get(tmp).toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    class getDoctorTask extends AsyncTask<Void, Void, Void> {
        private String UrlString = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/physician";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(doctorlstActivity.this);
            mDialog.setTitle(R.string.pDialog_getData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_getData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDialog.cancel();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(doctorlstActivity.this, android.R.layout.simple_list_item_1, pNAME);
            lst.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                HttpURLConnection conn = (HttpURLConnection) new URL(UrlString).openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                if(conn.getResponseCode()==200){
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
                    Log.e("Msg",msg);
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONArray pid = jsonObject.getJSONArray("pid");
                    JSONArray pName = jsonObject.getJSONArray("pName");
                    for(int i=0;i<=pid.length()-1;i++){
                        pID.add(pid.getInt(i));
                        pNAME.add(pName.getString(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
