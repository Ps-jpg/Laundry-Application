package com.example.foodex;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderDetailsFrag extends Fragment {

    public TextView tvOrderedItemName, tvOrderedWorkType, tvuser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SharedViewModel sharedViewModel;
    private String workType;
    private TextView tvOrderedItemPrice;
    private TextView tvOrderedItemMake;
    private TextView tvPlus;
    private TextView tvMinus;
    private TextView tvQuantity;
    private Button btnConfirm;
    private ImageView ivMake;



    public OrderDetailsFrag() {
        // Required empty public constructor
    }

    public static OrderDetailsFrag newInstance(String itemName, String itemPrice, String make, String username, String workType) {
        OrderDetailsFrag fragment = new OrderDetailsFrag();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("itemName", itemName);
        args.putString("itemPrice", itemPrice);
        args.putString("make", make);
        args.putString("workType", (workType != null) ? workType : "Nothing");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        tvOrderedItemName = view.findViewById(R.id.tvItem);
        tvOrderedItemPrice = view.findViewById(R.id.tvPrice);
        tvOrderedItemMake = view.findViewById(R.id.tvOrderedItemMake);
        tvPlus = view.findViewById(R.id.tvPlus);
        tvMinus = view.findViewById(R.id.tvMinus);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        ivMake = view.findViewById(R.id.ivMake);
        tvuser = view.findViewById(R.id.tvuser); // Corrected this line
        // Initialize ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        tvOrderedWorkType = view.findViewById(R.id.work);
        sharedViewModel.getUsernameLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String newUsername) {
                tvuser.setText(newUsername);
                Log.d("OrderDetailsFrag", "Username LiveData changed: " + newUsername);
            }
        });

        sharedViewModel.getWorkTypeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String newWorkType) {
                tvOrderedWorkType.setText(newWorkType);
                workType = newWorkType; // Update the class-level variable if needed
                Log.d("OrderDetailsFrag", "workType in onCreateView: " + workType);
            }
        });

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString("itemName", "");
            String itemPrice = args.getString("itemPrice", "");
            String make = args.getString("make", ""); // Retrieve "make" from arguments


            tvOrderedItemName.setText(itemName);
            tvOrderedItemPrice.setText(itemPrice);
            tvOrderedItemMake.setText(make);
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fetch the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set the user's email or UID to the tvuser TextView
            String username = currentUser.getEmail(); // or currentUser.getUid()
            sharedViewModel.setUsername(username);
            tvuser.setText(username);
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCart();
            }
        });

        return view;
    }

    public void updateWorkType(String workType) {
        if (tvOrderedWorkType != null) {
            tvOrderedWorkType.setText(workType);
        }
    }

    public interface OnAddToCartListener {
        void onAddToCart(String itemName, String itemPrice, String make, int quantity, String username, String workType);

        void onAddToCart(String itemName, String itemPrice, String make, int quantity, String username);
    }

    private void sendToCart() {
        Log.d("OrderDetailsFrag", "sendToCart called");
        String itemName = tvOrderedItemName.getText().toString();
        String itemPrice = tvOrderedItemPrice.getText().toString();
        String make = UUID.randomUUID().toString();

        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        String username = sharedViewModel.getUsername(); // Use the ViewModel to get the username

        Log.d("OrderDetailsFrag", "itemName: " + itemName + ", itemPrice: " + itemPrice + ", make: " + make + ", quantity: " + quantity + ", workType: " + workType + ", username: " + username);

        // Assuming your listener is implemented in the activity, you can call it directly
        if (getActivity() instanceof OnAddToCartListener) {
            OnAddToCartListener listener = (OnAddToCartListener) getActivity();
            listener.onAddToCart(itemName, itemPrice, make, quantity, username, workType);

            // Add the order to Firestore
            addToFirestore(itemName, itemPrice, make, quantity, username, workType);
        }
    }


    private void addToFirestore(String itemName, String itemPrice, String make, int quantity, String username, String workType) {
        // Create a Map to store the data
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("itemName", itemName);
        cartItem.put("itemPrice", itemPrice);
        cartItem.put("make", make);
        cartItem.put("quantity", quantity);
        cartItem.put("username", username);
        cartItem.put("worktype", workType);

        // Create a collection reference for the user's cart
        String userCartCollection = username + "'s cart";

        // Use document() without any argument to generate a random ID
        db.collection(userCartCollection)
                .document() // This line generates a random document ID
                .set(cartItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Log.d("Firestore", "Document added successfully!");
                        showToast("Added to cart successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle error
                        Log.w("Firestore", "Error adding document", e);
                        showToast("Failed to add to cart. Please try again.");
                    }
                });
    }


    // Helper method to show a toast
    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}