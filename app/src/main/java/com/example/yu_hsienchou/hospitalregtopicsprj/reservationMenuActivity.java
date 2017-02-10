package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class reservationMenuActivity extends AppCompatActivity {
    private Button btn_Family;
    private Button btn_Doctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_menu);
        btn_Doctor = (Button) findViewById(R.id.btnfordoctor);
        btn_Family = (Button) findViewById(R.id.btnforclass);
        btn_Doctor.setOnClickListener(btnOnclick);
        btn_Family.setOnClickListener(btnOnclick);
    }
    private Button.OnClickListener btnOnclick = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            if(v.getId()==R.id.btnforclass){
                intent.setClass(reservationMenuActivity.this,FamilyActivity.class);
                startActivity(intent);
            }
            if(v.getId()==R.id.btnfordoctor){
                intent.setClass(reservationMenuActivity.this,doctorlstActivity.class);
                startActivity(intent);
            }
        }
    };
}
