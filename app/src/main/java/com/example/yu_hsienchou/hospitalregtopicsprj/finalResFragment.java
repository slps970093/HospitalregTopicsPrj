package com.example.yu_hsienchou.hospitalregtopicsprj;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link finalResFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class finalResFragment extends Fragment {
    private String getID,postID;
    private boolean isDoctor;
    private ArrayList cid = new ArrayList();
    //private ArrayList<String> show_data = new ArrayList();
    private ListView lst;
    private ProgressDialog mDialog;
    private static Calendar mCal = Calendar.getInstance();
    private ResLstClassAdapter mAdapter = null;
    private List<ResLstClass> show_data;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public finalResFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment finalResFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static finalResFragment newInstance(String param1, String param2) {
        finalResFragment fragment = new finalResFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_final_res, container, false);
        Bundle bundle = getArguments();
        getID = bundle.getString("id");
        isDoctor = bundle.getBoolean("isDoctor");
        lst = (ListView)view.findViewById(R.id.lst);
        lst.setOnItemClickListener(lstOnItemClick);
        new ReservationGetTask().execute();
        return view;
    }
    private ListView.OnItemClickListener lstOnItemClick = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String sel =  position+"";
            Log.e("dsize",show_data.size()+"");
            Log.e("csize",cid.size()+"");
            //int tmp = show_data.indexOf(sel);
            postID = cid.get(position).toString();
            new ReservationPostTask().execute();

        }
    };
    //向伺服器發送資料.
    private class ReservationPostTask extends AsyncTask<Void,Void,Void> {
        private boolean isPost = false;
        @Override
        protected Void doInBackground(Void... params) {
            String link = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/reservation/add";
            try{
                Thread.sleep(2000);
                HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
                String cookieStr = getCookie(); //抽取Cookie資訊
                conn.setRequestMethod("POST");  //請求方式 GET/POST
                conn.setReadTimeout(5000);  //設定讀取超時
                conn.setConnectTimeout(5000);   //設定連線超時
                conn.setDoOutput(true); //設定運行輸入
                conn.setDoInput(true);  //設定運行輸入
                conn.setUseCaches(false);   //是否緩存 (POST請輸入false GET請輸入true)
                conn.setRequestProperty("Cookie",cookieStr);
                //請求數據
                String data = "id=" + URLEncoder.encode(postID, "UTF-8");
                Log.e("data=", data);
                OutputStream out = conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                if(conn.getResponseCode()==201){
                    isPost = true;
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
                    Log.e("msg",msg);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setTitle(R.string.pDialog_postData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_postData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.cancel();
            Calendar mCalendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            if(isPost){
                //Toast.makeText(ReservationActivity.this,R.string.data_upload_success,Toast.LENGTH_SHORT).show();
                Thread thread = new Thread(SetAlarm);
                thread.start();
                /*
                                Intent intent = new Intent();
                                intent.setClass(ReservationActivity.this,MainActivity.class);
                                startActivity(intent);
                                */
            }else{
                Toast.makeText(getActivity(),R.string.data_upload_failed,Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }
    //設定自動提醒
    public Runnable SetAlarm = new Runnable() {

        @Override
        public void run() {
            String link = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/scheduling/target/data?id="+postID;
            try {
                Looper.prepare();
                Log.e("link",link);
                Log.e("ss","ss");
                HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                if (conn.getResponseCode() == 200) {
                    //讀取網站資料
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
                    Intent intent = new Intent(getActivity(),SetAlarmActivity.class);
                    //傳值
                    Bundle bundle = new Bundle();
                    bundle.putString("msg",msg);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                Looper.loop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    //向伺服器取得資料 (改這一段)
    private class ReservationGetTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setTitle(R.string.pDialog_getData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_getData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
            show_data = new LinkedList<ResLstClass>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDialog.cancel();
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReservationActivity.this, android.R.layout.simple_list_item_1, show_data);
            mAdapter = new ResLstClassAdapter((LinkedList<ResLstClass>) show_data,getActivity());
            lst.setAdapter(mAdapter);
        }
        @Override
        protected Void doInBackground(Void... params) {
            try{
                Thread.sleep(2000);
                String link;
                if(isDoctor){
                    link = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/scheduling?id="+getID+"&fordoctor="+URLEncoder.encode("ture","UTF-8");
                }else{
                    link = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/scheduling?id="+getID;
                }
                HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
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
                    if(isDoctor){
                        /*
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray sid = jsonObject.getJSONArray("sid");
                        JSONArray date = jsonObject.getJSONArray("date");
                        JSONArray fName = jsonObject.getJSONArray("fName");
                        JSONArray time = jsonObject.getJSONArray("time");
                        for(int i = 0; i<=sid.length()-1;i++){
                            cid.add(sid.get(i));
                            show_data.add("看診日期："+date.get(i)+" 時間："+time.get(i)+" 診："+fName.get(i));
                        }
                        */
                        JSONArray jsonArray = new JSONArray(msg);
                        int tmp = 0;
                        for(int i =0;i<=jsonArray.length()-1;i++){
                            cid.add(jsonArray.getJSONObject(tmp).getInt("sid"));
                            //show_data.add("看診日期："+jsonArray.getJSONObject(tmp).getString("date")+" 時間："+jsonArray.getJSONObject(tmp).getString("time")+" 診："+jsonArray.getJSONObject(tmp).getString("fName"));
                            show_data.add(new ResLstClass("科別："+jsonArray.getJSONObject(tmp).getString("fName")+" 診間："+jsonArray.getJSONObject(tmp).getString("diagnosisName"),"日期:"+jsonArray.getJSONObject(tmp).getString("date")+" 時間："+jsonArray.getJSONObject(tmp).getString("time")));
                            tmp++;
                        }
                    }else{
                        /*
                        JSONObject jsonObject = new JSONObject(msg);
                        JSONArray sid = jsonObject.getJSONArray("sid");
                        JSONArray date = jsonObject.getJSONArray("date");
                        JSONArray pName = jsonObject.getJSONArray("pName");
                        JSONArray time = jsonObject.getJSONArray("time");
                        for(int i = 0; i<=sid.length()-1;i++){
                            cid.add(sid.get(i));
                            show_data.add("看診日期："+date.get(i)+" 時間："+time.get(i)+" 看診醫生："+pName.get(i));
                        }
                        */
                        JSONArray jsonArray = new JSONArray(msg);
                        int tmp = 0;
                        for(int i =0;i<=jsonArray.length()-1;i++){
                            cid.add(jsonArray.getJSONObject(tmp).getInt("sid"));
                            //show_data.add("看診日期："+jsonArray.getJSONObject(tmp).getString("date")+" 時間："+jsonArray.getJSONObject(tmp).getString("time")+" 看診醫生："+jsonArray.getJSONObject(tmp).getString("pName"));
                            show_data.add(new ResLstClass("看診醫生： "+jsonArray.getJSONObject(tmp).getString("pName")+" 診間："+jsonArray.getJSONObject(tmp).getString("diagnosisName"),"日期:"+jsonArray.getJSONObject(tmp).getString("date")+" 時間："+jsonArray.getJSONObject(tmp).getString("time")));
                            tmp++;
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    public String getCookie() throws IOException {
        FileInputStream input = getActivity().openFileInput("session.txt");
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
