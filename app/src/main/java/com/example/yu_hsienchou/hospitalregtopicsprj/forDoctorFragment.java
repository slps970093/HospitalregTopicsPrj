package com.example.yu_hsienchou.hospitalregtopicsprj;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link forDoctorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class forDoctorFragment extends Fragment {
    private ProgressDialog mDialog;
    private ArrayList pID = new ArrayList();
    private ArrayList<String> pNAME = new ArrayList();
    private ListView lst;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public forDoctorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment forDoctorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static forDoctorFragment newInstance(String param1, String param2) {
        forDoctorFragment fragment = new forDoctorFragment();
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
        View view =  inflater.inflate(R.layout.fragment_for_doctor, container, false);
        lst = (ListView)view.findViewById(R.id.lst);
        new getDoctorTask().execute();
        lst.setOnItemClickListener(lstOnItemClick);
        return view;
    }
    private ListView.OnItemClickListener lstOnItemClick = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String sel =  parent.getItemAtPosition(position).toString();
            int tmp = pNAME.indexOf(sel);
            //傳值
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isDoctor",true);
            bundle.putString("id",pID.get(tmp).toString());
            finalResFragment target = new finalResFragment();
            target.setArguments(bundle);
            transaction.replace(R.id.UI,target);
            transaction.commit();
        }
    };
    class getDoctorTask extends AsyncTask<Void, Void, Void> {
        private String UrlString = "http://kevin.hwai.edu.tw/~kevin/chou/callsystem/index.php/api/physician";
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
            mDialog.cancel();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, pNAME);
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
