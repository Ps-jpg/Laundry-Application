package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class IronAnimation extends AppCompatActivity {
    Button btnIronNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iron_animation);

        btnIronNext=findViewById(R.id.btnDryNext);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Smart Laundry Application    ");
        String workType = "For Ironing"; // You can set it dynamically based on your requirement

        btnIronNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(IronAnimation.this, Menu.class);
                intent.putExtra("workType", workType);
                startActivity(intent);
            }
        });
    }
}