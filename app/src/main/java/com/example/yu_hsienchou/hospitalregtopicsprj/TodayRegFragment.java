package com.example.yu_hsienchou.hospitalregtopicsprj;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayRegFragment extends Fragment {
    private ListView datalst;
    private List<TodayregLst> mData=null;
    private TodayRegLstAdapter mAdapter = null;
    private ProgressDialog mDialog;

    public TodayRegFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_today_reg, container, false);
        datalst = (ListView) view.findViewById(R.id.lst);
        mData = new LinkedList<TodayregLst>();
        new load_TodayReg().execute();
        return view;
    }

    class load_TodayReg extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setTitle(R.string.pDialog_postData_title);
            mDialog.setMessage(getResources().getString(R.string.pDialog_postData_Content));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDialog.cancel();
            mAdapter = new TodayRegLstAdapter((LinkedList<TodayregLst>)mData,getActivity());
            datalst.setAdapter(mAdapter);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Log.e("eee","eee");
                String url = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/res/nowday/data";
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                String cookieStr = getCookie(); //抽取Cookie資訊
                conn.setRequestProperty("Cookie",cookieStr);
                String cde = String.valueOf(conn.getResponseCode());
                Log.e("cde",cde);
                if(conn.getResponseCode() == 200){
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
                    JSONArray jsonArray = new JSONArray(msg);
                    short tmp = 0;
                    for(int i=0;i<jsonArray.length();i++){
                        String[] str = new String[2];
                        str[0] = jsonArray.getJSONObject(tmp).getString("fName")+"(診間: "+jsonArray.getJSONObject(tmp).getString("diagnosisName")+")";
                        str[1] = "開始看診日："+jsonArray.getJSONObject(tmp).getString("startDay")+" 時間："+jsonArray.getJSONObject(tmp).getString("startTime");
                        mData.add(new TodayregLst(str[0],str[1]));
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
