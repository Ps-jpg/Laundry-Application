package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {
    Button btnWash, btnLocation, btnDry, btnIron, btnCart, btnSettings;
    ImageView btnAbout;
    FirebaseAuth mFirebaseAuth;
    private SharedViewModel sharedViewModel;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String workType = "Nothing";
        setContentView(R.layout.activity_home);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        btnWash = findViewById(R.id.btnWash);
        btnLocation = findViewById(R.id.btnLocation);
        btnIron = findViewById(R.id.btnIron);
        btnDry = findViewById(R.id.btnDry);
        btnAbout = findViewById(R.id.btnAbout);
        btnCart = findViewById(R.id.btnCart);
        btnSettings = findViewById(R.id.btnSettingsText);
        // Initialize AuthStateListener to update username
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    String username = user.getDisplayName();
                    sharedViewModel.setUsername(username);
                }
            }
        };

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, About.class);
                startActivity(intent);
            }
        });


        btnIron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.updateWorkType("IronButton");
                Intent intent = new Intent(Home.this, IronAnimation.class);
                startActivity(intent);
            }
        });

        btnDry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.updateWorkType("DryCleanButton");
                Intent intent = new Intent(Home.this, DryAnimation.class);
                startActivity(intent);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LocationAnimation.class);
                startActivity(intent);
            }
        });

        btnWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedViewModel.updateWorkType("WashButton");
                Intent intent = new Intent(Home.this, WashAnimation.class);
                startActivity(intent);

            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CartActivity.class);
                startActivity(intent);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,OrderHistory.class);
                startActivity(intent);
            }

        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home");
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(Home.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
