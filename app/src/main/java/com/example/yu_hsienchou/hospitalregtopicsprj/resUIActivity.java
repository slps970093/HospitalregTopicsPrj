package com.example.yu_hsienchou.hospitalregtopicsprj;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class resUIActivity extends AppCompatActivity {
    private FragmentManager manager = getSupportFragmentManager();
    private FragmentTransaction transaction = manager.beginTransaction();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_ui);

        transaction.replace(R.id.UI,new forFamilyFragment());
        transaction.replace(R.id.footer,new resFooterFragment());
        transaction.commit();

    }

}
