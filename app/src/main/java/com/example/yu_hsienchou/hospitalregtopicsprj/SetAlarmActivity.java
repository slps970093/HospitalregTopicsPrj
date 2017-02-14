package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

public class SetAlarmActivity extends AppCompatActivity {
    private static Calendar mCal = Calendar.getInstance();
    private static AlarmManager am;
    long Set_datetime;
    PendingIntent pi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String message = bundle.getString("msg");
        try{
            //解析JSON文件
            JSONArray jsonArray = new JSONArray(message);
            //切割
            String date[] = jsonArray.getJSONObject(0).getString("date").split("-");
            //設定日期與轉換為數值資料
            Log.e("Y",Integer.parseInt(date[0])+"");
            Log.e("M",Integer.parseInt(date[1])+"");
            Log.e("D",Integer.parseInt(date[2])+"");

            mCal.set(Calendar.YEAR,Integer.parseInt(date[0]));
            //設定月份
            switch (Integer.parseInt(date[1])){
                case 1:
                    mCal.set(Calendar.MONTH,Calendar.JANUARY);
                    break;
                case 2:
                    mCal.set(Calendar.MONTH,Calendar.FEBRUARY);
                    break;
                case 3:
                    mCal.set(Calendar.MONTH,Calendar.MARCH);
                    break;
                case 4:
                    mCal.set(Calendar.MONTH,Calendar.APRIL);
                    break;
                case 5:
                    mCal.set(Calendar.MONTH,Calendar.MAY);
                    break;
                case 6:
                    mCal.set(Calendar.MONTH,Calendar.JUNE);
                    break;
                case 7:
                    mCal.set(Calendar.MONTH,Calendar.JULY);
                    break;
                case 8:
                    mCal.set(Calendar.MONTH,Calendar.AUGUST);
                    break;
                case 9:
                    mCal.set(Calendar.MONTH,Calendar.SEPTEMBER);
                    break;
                case 10:
                    mCal.set(Calendar.MONTH,Calendar.OCTOBER);
                    break;
                case 11:
                    mCal.set(Calendar.MONTH,Calendar.NOVEMBER);
                    break;
                default:
                    mCal.set(Calendar.MONTH,Calendar.DECEMBER);
                    break;
            }
            mCal.set(Calendar.DATE,Integer.parseInt(date[2]));
            Log.e("Y",mCal.getTime()+"");

            //long lSysTime1 = tmp.getTime() / 1000;
            Date set_date = mCal.getTime();
            Set_datetime = set_date.getTime();
            Intent BroadCastintent = new Intent();
            BroadCastintent.setClass(SetAlarmActivity.this,MyRegBroadcast.class);
            //傳值
            Bundle BroadCastbundle = new Bundle();
            BroadCastbundle.putString("doctor",jsonArray.getJSONObject(0).getString("time"));
            BroadCastbundle.putString("datetime",jsonArray.getJSONObject(0).getString("pName"));
            BroadCastbundle.putString("message",jsonArray.getJSONObject(0).getString("fName")+"("+jsonArray.getJSONObject(0).getString("diagnosis")+")");
            BroadCastbundle.putString("msg","play_hskey");
            BroadCastintent.putExtras(bundle);
            pi = PendingIntent.getBroadcast(SetAlarmActivity.this, 0, BroadCastintent,0);
            //開啟時間對話框
            TimePickerDialog tpd = new TimePickerDialog(SetAlarmActivity.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Log.e("alarm","aaa");
                    //根據User 選擇的時間來設定Calendar
                    mCal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                    mCal.set(Calendar.MINUTE,minute);
                    //Log.e("time",set_date+"");
                    Log.e("mcal",mCal.getTimeInMillis()+"");
                    Log.e("Y",mCal.getTime()+"");
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, mCal.getTimeInMillis(), pi);
                    Toast.makeText(SetAlarmActivity.this,R.string.data_upload_success,Toast.LENGTH_SHORT).show();
                    Intent goto_intent = new Intent();
                    goto_intent.setClass(SetAlarmActivity.this,MainActivity.class);
                    startActivity(goto_intent);
                }
            },mCal.get(Calendar.HOUR_OF_DAY), mCal.get(Calendar.MINUTE), false);
            tpd.show();

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
