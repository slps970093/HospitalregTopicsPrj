package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by Yu-Hsien Chou on 2017/2/14.
 */

public class TodayRegLstAdapter extends BaseAdapter {
    private LinkedList<TodayregLst> mData;
    private Context mContext;
    public TodayRegLstAdapter(LinkedList<TodayregLst> mData, Context mContext){
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_today_reg,parent,false);
        TextView txt_Section = (TextView) convertView.findViewById(R.id.Diagnosis);
        TextView txt_info = (TextView) convertView.findViewById(R.id.info);
        txt_info.setText(mData.get(position).getInformation());
        txt_Section.setText(mData.get(position).getSection());
        txt_Section.setTextSize(25);
        txt_info.setTextSize(15);
        return convertView;
    }
}
