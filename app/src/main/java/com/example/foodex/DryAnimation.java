package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class DryAnimation extends AppCompatActivity {
    Button btnDryNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dry_animation);

        btnDryNext=findViewById(R.id.btnDryNext);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Smart Laundry Application    ");
        String workType = "For Dry Cleaning"; // You can set it dynamically based on your requirement

        btnDryNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DryAnimation.this, Menu.class);
                intent.putExtra("workType", workType);
                startActivity(intent);
            }
        });
    }
}