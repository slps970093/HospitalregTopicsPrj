package com.example.yu_hsienchou.hospitalregtopicsprj;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link resDellstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class resDellstFragment extends Fragment {
    private ProgressDialog mDialog;
    private ArrayList r_id = new ArrayList();
    private ResDelAdapter mAdapter = null;
    private List<ResLstClass> show_data;
    private ListView viewLst;
    private int target_id;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public resDellstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment resDellstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static resDellstFragment newInstance(String param1, String param2) {
        resDellstFragment fragment = new resDellstFragment();
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
        View view = inflater.inflate(R.layout.fragment_res_dellst, container, false);
        new resgetData().execute();
        viewLst = (ListView)view.findViewById(R.id.lst);
        viewLst.setOnItemClickListener(lstOnclick);
        return view;
    }
    private ListView.OnItemClickListener lstOnclick = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String sel =  parent.getItemAtPosition(position).toString();
            //int tmp = show_data.indexOf(sel);
            target_id = Integer.parseInt(r_id.get(position).toString());
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
            alertdialog.setTitle(R.string.usr_permit_title);
            alertdialog.setMessage(R.string.usr_permit_content);
            alertdialog.setPositiveButton(R.string.default_dialog_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    new resDeldata().execute();
                }
            });
            alertdialog.setNegativeButton(R.string.default_dialog_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertdialog.show();
        }
    };
    private class resDeldata extends AsyncTask<Void,Void,Void> {
        private boolean isTure;
        @Override
        protected Void doInBackground(Void... params) {
            try{
                Thread.sleep(1500);
                Log.e("eee","eee");
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/res/delete";
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");  //請求方式 GET/POST
                conn.setReadTimeout(5000);  //設定讀取超時
                conn.setConnectTimeout(5000);   //設定連線超時
                conn.setDoOutput(true); //設定運行輸入
                conn.setDoInput(true);  //設定運行輸入
                String cookieStr = getCookie(); //抽取Cookie資訊
                conn.setRequestProperty("Cookie",cookieStr);
                conn.setUseCaches(false);   //是否緩存 (POST請輸入false GET請輸入true)
                //請求數據
                String data = "rid="+target_id;
                Log.e("data=", data);
                OutputStream out = conn.getOutputStream();
                out.write(data.getBytes());
                out.flush();
                String cde = Integer.toString(conn.getResponseCode());
                Log.e("code",cde);
                if(conn.getResponseCode() == 201){  //Http ResponseCode
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
                    Log.e("MSG",msg);
                    isTure = true;

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setTitle(R.string.pDialog_getData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_getData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isTure){
                mDialog.cancel();
                Toast.makeText(getActivity(),R.string.usr_permit_show_success,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(),MainActivity.class);
                startActivity(intent);
            }else{
                mDialog.cancel();
            }
        }
    }
    //抽取資料
    private class resgetData extends AsyncTask<Void,Void,Void>{
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
            String s = String.valueOf(show_data.size());
            Log.e("size",s);
            mAdapter = new ResDelAdapter((LinkedList<ResLstClass>)show_data,getActivity());
            viewLst.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Thread.sleep(1500);
                Log.e("eee","eee");
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/res";
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                String cookieStr = getCookie(); //抽取Cookie資訊
                conn.setRequestProperty("Cookie",cookieStr);
                String cde = String.valueOf(conn.getResponseCode());
                Log.e("cde",cde);
                if(conn.getResponseCode() == 201){
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
                    Log.e("str",msg);
                    /*
                    JSONObject jsonObject = new JSONObject(msg);
                    JSONArray rid = jsonObject.getJSONArray("id");
                    JSONArray addDate  = jsonObject.getJSONArray("addDate");
                    JSONArray addTime  = jsonObject.getJSONArray("addTime");
                    JSONArray Name = jsonObject.getJSONArray("Name");
                    JSONArray startDay = jsonObject.getJSONArray("startDay");
                    JSONArray startTime = jsonObject.getJSONArray("startTime");
                    for(int i =0; i<startDay.length();i++){
                        Log.e("hello","hello");
                        r_id.add(rid.getInt(i));
                        show_data.add(" 診:"+Name.get(i)+" 開始看診日:"+startDay.get(i)+" 開始看診時間:"+startTime.get(i)+" 資料新增時間:"+addDate.get(i));
                    }
                    */
                    JSONArray jsonArray = new JSONArray(msg);
                    short tmp = 0;
                    for(int i=0;i<jsonArray.length();i++){
                        r_id.add(jsonArray.getJSONObject(tmp).getInt("id"));
                        show_data.add(new ResLstClass("診:"+jsonArray.getJSONObject(tmp).getString("Name")+" 開始看診日:"+jsonArray.getJSONObject(tmp).getString("startDay")," 開始看診時間:"+jsonArray.getJSONObject(tmp).getString("startTime")+" 資料新增時間:"+jsonArray.getJSONObject(tmp).getString("addDate")));
                        tmp++;
                    }
                }else{
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
