package com.example.yu_hsienchou.hospitalregtopicsprj;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link forFamilyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class forFamilyFragment extends Fragment {
    public ListView lst;
    public ArrayList<String> showData = new ArrayList();
    public ArrayList cid = new ArrayList();
    private ProgressDialog mDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public forFamilyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment forFamilyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static forFamilyFragment newInstance(String param1, String param2) {
        forFamilyFragment fragment = new forFamilyFragment();
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
        View view = inflater.inflate(R.layout.fragment_for_family, container, false);
        lst = (ListView)view.findViewById(R.id.lst);
        new Familytesk().execute();
        lst.setOnItemClickListener(lstPreferListener);
        return view;
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
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            finalResFragment target = new finalResFragment();
            target.setArguments(bundle);
            transaction.replace(R.id.UI,target);
            transaction.commit();
        }
    };
    class Familytesk extends AsyncTask<String, Void, String> {
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mDialog.cancel();
            String str = String.valueOf(showData.size());
            Log.e("size", str);
            Log.e("1", showData.get(1).toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, showData);
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
