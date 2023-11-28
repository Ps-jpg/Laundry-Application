package com.example.foodex;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartItemsActivity extends AppCompatActivity {

    private ListView listViewCartItems;
    private CartItemAdapter cartItemAdapter;
    private List<CartActivity.CartItem> cartItems;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_items);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listViewCartItems = findViewById(R.id.listViewCartItems);

        // Fetch and display cart items
        fetchCartItems();
    }

    private void fetchCartItems() {
        // Replace this with your logic to get order ID from the intent
        String orderId = getIntent().getStringExtra("orderId");

        // Check if orderId is not null or empty
        if (orderId != null && !orderId.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String username = currentUser.getEmail();

                // Reference to the "Orders" collection in Firestore
                CollectionReference ordersRef = db.collection("Orders");

                // Query to find the document with the specified order ID
                Query query = ordersRef.whereEqualTo("orderId", orderId)
                        .whereEqualTo("username", username);

                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the cart items from the document
                            List<HashMap<String, Object>> fetchedCartItems = (List<HashMap<String, Object>>) document.get("cartItems");

                            if (fetchedCartItems != null) {
                                // Convert HashMap to CartItem
                                List<CartActivity.CartItem> cartItems = new ArrayList<>();
                                for (HashMap<String, Object> itemMap : fetchedCartItems) {
                                    String itemName = (String) itemMap.get("itemName");
                                    String make = (String) itemMap.get("make");
                                    String workType = (String) itemMap.get("workType");
                                    String itemPrice = (String) itemMap.get("itemPrice");
                                    long quantity = (long) itemMap.get("quantity");
                                    String price = (String) itemMap.get("price");

                                    CartActivity.CartItem cartItem = new CartActivity.CartItem(itemName, itemPrice, make, workType, username, quantity);
                                    cartItems.add(cartItem);
                                }

                                // Initialize the list and adapter
                                this.cartItems = cartItems;
                                cartItemAdapter = new CartItemAdapter(this, R.layout.cart_item_row, cartItems, false); // Set showRemoveButton to true
                                listViewCartItems.setAdapter(cartItemAdapter);

                                // Notify the adapter that the data set has changed
                                cartItemAdapter.notifyDataSetChanged();
                            } else {
                                showToast("No cart items found for the selected order");
                            }
                        }
                    } else {
                        Log.e("CartItemsActivity", "Error getting documents.", task.getException());
                        showToast("Error fetching cart items");
                    }
                });
            }
        } else {
            showToast("Invalid order ID");
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
