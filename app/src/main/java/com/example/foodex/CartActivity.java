package com.example.foodex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    public List<CartItem> cartItems;
    private FirebaseFirestore db;
    private Button btnBack, btnCheck;
    public CartItemAdapter cartItemAdapter;// Declare the adapter as a class-level variable
    private TextView userNameTextView;
    // Declare TextView for UserName

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize cartItems list
        cartItems = new ArrayList<>();

        // Initialize ListView
        ListView listView = findViewById(R.id.tableLayoutCart);

        // Initialize and set up a custom adapter for your ListView

        cartItemAdapter = new CartItemAdapter(this, R.layout.cart_item_row, cartItems,true);
        listView.setAdapter(cartItemAdapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click, e.g., navigate to the HomeActivity
                navigateToHome();
            }
        });
        // Fetch the reference of the TextView with id UserName
        userNameTextView = findViewById(R.id.UserName);
        // Fetch UserData
        fetchUserData();
        // Fetch cart items from Firestore
        fetchCartItems();
        // Assuming you have a button with the id btnCheck in your layout
        btnCheck = findViewById(R.id.btnCheckout);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger the method to navigate to CheckoutActivity
                navigateToCheckout();
            }
        });
    }

    private void navigateToCheckout() {
        // Calculate total amount
        double totalAmount = calculateTotalAmount();

        // Show a toast indicating that we are proceeding to the next stage
        Toast.makeText(this, "Proceeding to the next stage...", Toast.LENGTH_SHORT).show();

        // Create an Intent to navigate to CheckoutActivity
        Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);

        // Pass relevant information to CheckoutActivity
        checkoutIntent.putExtra("username", mAuth.getCurrentUser().getDisplayName()); // Replace with the actual username
        checkoutIntent.putExtra("cartItems", new ArrayList<>(cartItems)); // Pass the cart items as an ArrayList

        checkoutIntent.putExtra("totalAmount", totalAmount);

        // Start CheckoutActivity
        startActivity(checkoutIntent);
    }


    // Calculate total amount based on the items in the cart
    private double calculateTotalAmount() {
        double totalAmount = 0.0;

        // Iterate through cartItems and sum up the item prices
        for (CartItem cartItem : cartItems) {
            // Assuming the item price is a string, you may need to parse it to a numeric value
            try {
                double itemPrice = Double.parseDouble(cartItem.getItemPrice());
                totalAmount += itemPrice;
            } catch (NumberFormatException e) {
                // Handle the exception if parsing fails
                e.printStackTrace();
            }
        }
        TextView totalAmountTextView = findViewById(R.id.totalbill);
        Toast.makeText(this, "TotalAmount =" + totalAmount, Toast.LENGTH_LONG).show();
        totalAmountTextView.setText("Pay₹" + totalAmount);
        return totalAmount;
    }

    private void fetchUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userNameTextView.setText(currentUser.getDisplayName());
        }
    }

    // Fetch cart items from Firestore
   public void fetchCartItems() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getEmail(); // Assuming the email is the username
            String userCartCollection = username + "'s cart";

            db.collection(userCartCollection)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            cartItems.clear(); // Clear existing items before adding new ones
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Create a new CartItem instance using the document data
                                CartItem cartItem = new CartItem(
                                        document.getString("itemName"),
                                        document.getString("itemPrice"),
                                        document.getString("make"),
                                        document.getString("worktype"),
                                        document.getString("username"),
                                        document.getLong("quantity").intValue()
                                );
                                // Add the cart item to the list
                                cartItems.add(cartItem);
                            }
                            Log.d("CartActivity", "Fetched cartItems. Size: " + cartItems.size());

                            // Notify the adapter that the data set has changed
                            cartItemAdapter.notifyDataSetChanged();

                            // Check if cart is empty and show a message
                            if (cartItems.isEmpty()) {
                                showNoItemsMessage();
                            } else {
                                // Calculate total amount and update the UI
                                calculateAndShowTotalAmount();
                            }

                        } else {
                            // Log the error if fetching fails
                            Log.w("CartActivity", "Error getting documents.", task.getException());
                        }
                    });

        }
    }
    public void onRemoveItemClick(int position) {
        if (position >= 0 && position < cartItems.size()) {
            CartActivity.CartItem removedItem = cartItems.get(position);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userCartCollection = removedItem.getUsername() + "'s cart";
            Log.d("CartActivity", "Attempting to delete document with make: " + removedItem.getMake() + " from collection: " + userCartCollection);

            // Use a query to find the document with the specified 'make' field
            CollectionReference collectionReference = db.collection(userCartCollection);
            Query query = collectionReference.whereEqualTo("make", removedItem.getMake());

            // Execute the query and delete the document
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete().addOnSuccessListener(aVoid -> {
                            Log.d("CartActivity", "Document successfully deleted from Firestore");
                            // Remove the item from the local cartItems list
                            cartItems.remove(position);
                            cartItemAdapter.notifyDataSetChanged();
                            calculateAndShowTotalAmount();
                            Toast.makeText(CartActivity.this, "Item removed from the cart", Toast.LENGTH_SHORT).show();
                            // If the cart is empty, show a message
                            if (cartItems.isEmpty()) {
                                showNoItemsMessage();
                            }
                        }).addOnFailureListener(e -> {
                            // Handle the failure to delete from Firestore
                            Toast.makeText(CartActivity.this, "Failed to remove item from Firestore", Toast.LENGTH_SHORT).show();
                            Log.e("CartActivity", "Failed to delete document from Firestore", e);
                            // Log the specific error message
                            Log.e("CartActivity", "Error message: " + e.getMessage());
                        });
                    }
                } else {
                    // Handle the error
                    Toast.makeText(CartActivity.this, "Failed to fetch document from Firestore", Toast.LENGTH_SHORT).show();
                    Log.e("CartActivity", "Error getting documents: ", task.getException());
                }
            });
        }
    }





    // Calculate total amount based on the items in the cart and update the UI
    private void calculateAndShowTotalAmount() {
        double totalAmount = calculateTotalAmount();

        TextView totalAmountTextView = findViewById(R.id.totalbill);
        Toast.makeText(this, "TotalAmount =" + totalAmount, Toast.LENGTH_LONG).show();
        totalAmountTextView.setText("Pay₹" + totalAmount);
    }

    private void showNoItemsMessage() {
        // Implement the logic to show a message when there are no items in the cart
        Toast.makeText(this, "No items in the cart", Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        // You can change HomeActivity.class to the actual class you want to navigate to
        startActivity(new Intent(CartActivity.this, Home.class));
        // Finish the current activity to remove it from the stack
        finish();
    }

    public static class CartItem implements Serializable {
        private String itemName;
        private String itemPrice;
        private String make;
        private String workType;
        private String username;
        private long quantity;

        // Default, no-argument constructor required for Firestore deserialization
        public CartItem() {
            // Default constructor is needed for Firestore to deserialize objects
        }

        // Constructor with parameters
        public CartItem(String itemName, String itemPrice, String make, String workType, String username, long quantity) {
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.make = make;
            this.workType = workType;
            this.username = username;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        // Getter method for itemPrice
        public String getItemPrice() {
            return itemPrice;
        }

        // Getter method for make
        public String getMake() {
            return make;
        }

        // Getter method for workType
        public String getWorkType() {
            return workType;
        }

        // Getter method for username
        public String getUsername() {
            return username;
        }

        public long getQuantity() {
            return quantity;
        }

    }

}