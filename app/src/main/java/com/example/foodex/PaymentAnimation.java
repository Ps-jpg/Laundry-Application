package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentAnimation extends AppCompatActivity {
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_animation);
        btnNext=findViewById(R.id.btnNext);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Smart Laundry Application    ");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PaymentAnimation.this, Confirm.class);
                startActivity(intent);
            }
        });



    }
}