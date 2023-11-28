package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class Menu extends AppCompatActivity implements ItemListAdapter.ItemClicked, OrderDetailsFrag.OnAddToCartListener {
    Button btnHome, btnOrder, btnConfirm, btnBack;
    TextView tvItem, tvPrice,setwork;
    TextView tvPlus, tvMinus, tvQuantity;
    private SharedViewModel sharedViewModel;

    ImageView ivMake, btnCart;
    FragmentManager fragmentManager;
    Fragment buttonFrag, listFrag, OrderDetailFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_menu );

//        setwork = findViewById ( R.id.work );
//        btnCart = findViewById ( R.id.btnCart );
//        btnHome = findViewById ( R.id.btnHome );
//        btnOrder = findViewById ( R.id.btnOrder );
//        tvItem = findViewById ( R.id.tvItem );
//        tvPrice = findViewById ( R.id.tvPrice );
//        ivMake = findViewById ( R.id.ivMake );
//        tvPlus = findViewById ( R.id.tvPlus );
//        tvMinus = findViewById ( R.id.tvMinus );
//        tvQuantity = findViewById ( R.id.tvQuantity );
//        btnBack = findViewById ( R.id.btnBack );

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        tvPlus.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String number = tvQuantity.getText ().toString ().trim ();
                String price = tvPrice.getText ().toString ().trim ();
                int quntity = Integer.parseInt ( number );
                int Amount = Integer.parseInt ( price );
                int perAmount = Amount / quntity;
                quntity = quntity + 1;
                Amount = perAmount * quntity;
                tvQuantity.setText ( String.valueOf ( quntity ) );
                tvPrice.setText ( String.valueOf ( Amount ) );
            }


        } );
        tvMinus.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                String number = tvQuantity.getText ().toString ().trim ();
                String price = tvPrice.getText ().toString ().trim ();
                int quntity = Integer.parseInt ( number );
                int Amount = Integer.parseInt ( price );
                int perAmount = Amount / quntity;
                if (quntity < 2) {
                    Toast.makeText ( Menu.this, "Wrong Attempt", Toast.LENGTH_SHORT ).show ();
                } else {
                    quntity = quntity - 1;
                    Amount = Amount - perAmount;
                    tvPrice.setText ( String.valueOf ( Amount ) );
                    tvQuantity.setText ( String.valueOf ( quntity ) );


                }

            }
        } );
        ActionBar actionBar = getSupportActionBar ();
        // actionBar.setSubtitle("Welcome to our App");
        actionBar.setTitle ( "Smart Laundry Application    " );
        actionBar.setDisplayUseLogoEnabled ( true );
        actionBar.setDisplayShowHomeEnabled ( true );


        fragmentManager = getSupportFragmentManager ();
        listFrag = fragmentManager.findFragmentById ( R.id.listFrag );
        buttonFrag = fragmentManager.findFragmentById ( R.id.buttonFrag );
        OrderDetailFrag = fragmentManager.findFragmentById ( R.id.OrderDetailFrag );
        fragmentManager.beginTransaction ()
                .show ( listFrag )
                .hide ( OrderDetailFrag )
                .show ( buttonFrag )
                .commit ();
        btnHome.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( Menu.this, Home.class );
                startActivity ( intent );
            }
        } );


        btnOrder.setOnClickListener ( new View.OnClickListener () {

            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction ()
                        .hide ( listFrag )
                        .show ( OrderDetailFrag )
                        .hide ( buttonFrag )
                        .commit ();


            }
        } );
        btnBack.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction ()
                        .show ( listFrag )
                        .hide ( OrderDetailFrag )
                        .show ( buttonFrag )
                        .commit ();

            }
        } );
        btnCart.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // When btnCart is clicked, start the CartActivity
                Intent intent = new Intent ( Menu.this, CartActivity.class );
                startActivity ( intent );
            }
        } );
        onItemClicked ( 0 );
    }


    @Override
    public void onItemClicked(int index) {
        tvPrice.setText ( ApplicationClass.item.get ( index ).getItemPrice () );
        String itemName = ApplicationClass.item.get(index).getItemName();
        String itemPrice = ApplicationClass.item.get(index).getItemPrice();
        String make = ApplicationClass.item.get(index).getMake();
        tvItem.setText ( ApplicationClass.item.get ( index ).getItemName () );
        String workType = String.valueOf ( sharedViewModel.getWorkTypeLiveData ()); // Implement determineWorkType() based on your logic
        OrderDetailsFrag orderDetailsFrag = OrderDetailsFrag.newInstance(itemName, itemPrice, make, getUsername(),workType);
        if (ApplicationClass.item.get ( index ).getMake ().equals ( "dress" )) {
            ivMake.setImageResource ( R.drawable.dress );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "hoodie" )) {
            ivMake.setImageResource ( R.drawable.hoodie );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "skirt" )) {
            ivMake.setImageResource ( R.drawable.skirt );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "traditional" )) {
            ivMake.setImageResource ( R.drawable.traditional );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "westernwear" )) {
            ivMake.setImageResource ( R.drawable.westernwear );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "sportwear" )) {
            ivMake.setImageResource ( R.drawable.sportwear );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "jumpsuit" )) {
            ivMake.setImageResource ( R.drawable.jumpsuit );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "jeans" )) {
            ivMake.setImageResource ( R.drawable.jeans );
        } else if (ApplicationClass.item.get ( index ).getMake ().equals ( "formal" )) {
            ivMake.setImageResource ( R.drawable.formal );
        } else {
            ivMake.setImageResource ( R.drawable.tshirt );
        }

    }


    private String getUsername() {
        return "default_username";
    }

    @Override
    public void onAddToCart(String itemName, String itemPrice, String make, int quantity) {
        // Not adding to cart here, just navigating to CartActivity
        Intent intent = new Intent(Menu.this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAddToCart(String itemName, String itemPrice, String make, int quantity, String username, String workType) {

    }

    @Override
    public void onAddToCart(String itemName, String itemPrice, String make, int quantity, String username) {

    }
}




