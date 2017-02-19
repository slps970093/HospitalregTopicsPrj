package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.LinkedList;

/**
 * Created by Yu-Hsien Chou on 2017/2/16.
 */

public class ResLstClassAdapter extends BaseAdapter {

    private LinkedList<ResLstClass> mData;
    private Context mContext;

    public ResLstClassAdapter(LinkedList<ResLstClass> mData,Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_final_res,parent,false);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.name);
        TextView txt_aInfo = (TextView) convertView.findViewById(R.id.info);
        txt_aName.setText(mData.get(position).getaName());
        txt_aInfo.setText(mData.get(position).getaInfo());
        txt_aName.setTextSize(17);
        txt_aInfo.setTextSize(14);
        return convertView;
    }
}
