package com.example.foodex;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private List<Order> orderList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orderList, this);

        recyclerView.setAdapter(orderHistoryAdapter);


        // Fetch and display order history
        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Assuming the email is the username
            String username = currentUser.getEmail();
            CollectionReference ordersRef = db.collection("Orders");

            // Query orders for the current user
            ordersRef.whereEqualTo("username", username)
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp, latest first
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            orderList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Create a new Order instance using the document data
                                Order order = document.toObject(Order.class);
                                orderList.add(order);
                            }

                            // Notify the adapter that the data set has changed
                            orderHistoryAdapter.notifyDataSetChanged();

                            // Check if there are no past orders
                            if (orderList.isEmpty()) {
                                showToast("No past orders found");
                            }

                        } else {
                            // Log the error if fetching fails
                            showToast("Error fetching order history");
                            task.getException().printStackTrace();
                        }

                    });
        }
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
