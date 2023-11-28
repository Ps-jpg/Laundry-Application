package com.example.foodex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderHistoryAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orderList.get(position);
        holder.bind(order);
        holder.btnSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Save PDF" button click
                onSavePDFButtonClick(order.getOrderId(), orderList.get(position));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click to show cart items
                Log.d("OrderHistoryAdapter", "Order ID clicked: " + order.getOrderId());
                showCartItems(order.getOrderId(), order.getCartItems());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView;
        private TextView deliveryAddressTextView;
        private TextView pickupTimeTextView;
        private TextView dropTimeTextView;
        private TextView totalAmountTextView;
        private TextView statusTextView;
        private Button btnSavePDF;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tvOrderId);
            deliveryAddressTextView = itemView.findViewById(R.id.tvDeliveryAddress);
            pickupTimeTextView = itemView.findViewById(R.id.tvPickupTime);
            dropTimeTextView = itemView.findViewById(R.id.tvDropTime);
            totalAmountTextView = itemView.findViewById(R.id.tvTotalAmount);
            statusTextView = itemView.findViewById(R.id.tvOrderStatus);
            btnSavePDF = itemView.findViewById(R.id.btnSavePDF);
        }

        public void bind(Order order) {
            orderIdTextView.setText("Order ID: " + order.getOrderId());
            deliveryAddressTextView.setText("Delivery Address: " + order.getDeliveryAddress());
            pickupTimeTextView.setText("Pickup Time: " + order.getPickupTime());
            dropTimeTextView.setText("Drop Time: " + order.getDropTime());
            totalAmountTextView.setText("Total Amount: " + order.getTotalAmount());
            statusTextView.setText("Status: " + order.getStatus());
        }
    }

    private void showCartItems(String orderId, List<CartActivity.CartItem> cartItems) {
        Intent intent = new Intent(context, CartItemsActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("cartItems", (Serializable) cartItems);
        context.startActivity(intent);
    }

    private void onSavePDFButtonClick(String orderId, Order order) {
        // Fetch order details from Firestore based on orderId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ordersRef = db.collection("Orders");

        ordersRef.whereEqualTo("orderId", orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Get order details
                    String deliveryAddress = document.getString("deliveryAddress");
                    String pickupTime = document.getString("pickupTime");
                    String dropTime = document.getString("dropTime");
                    String totalAmount = document.getString("totalAmount");
                    String status = document.getString("status");

                    // Generate and save PDF
                    generateAndSavePDF(orderId, deliveryAddress, pickupTime, dropTime, totalAmount, status, order.getCartItems());
                }
            } else {
                Log.e("OrderHistoryAdapter", "Error getting order details.", task.getException());
                // Handle the error
            }
        });
    }


    private void generateAndSavePDF(String orderId, String deliveryAddress, String pickupTime,
                                    String dropTime, String totalAmount, String status,
                                    List<CartActivity.CartItem> cartItems) {
        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        // Create a PageInfo
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();

        // Start a new page
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Get the Canvas for rendering into the page
        Canvas canvas = page.getCanvas();

        // Create a Paint object for styling
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);

        // Set position for text
        int x = 10, y = 25;

        // Draw a border around the page
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(0, 0, 300, 600, paint);

        // Draw text content on the page
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        paint.setTypeface( Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        canvas.drawText("Order ID: " + orderId, x, y, paint);
        y += 20;
        canvas.drawText("Delivery Address: " + deliveryAddress, x, y, paint);
        y += 20;
        canvas.drawText("Pickup Time: " + pickupTime, x, y, paint);
        y += 20;
        canvas.drawText("Drop Time: " + dropTime, x, y, paint);
        y += 20;
        canvas.drawText("Total Amount: " + totalAmount, x, y, paint);
        y += 20;
        canvas.drawText("Status: " + status, x, y, paint);
        y += 20;

        // Draw the cart items
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        for (CartActivity.CartItem cartItem : cartItems) {
            canvas.drawText("Item: " + cartItem.getItemName(), x, y, paint);
            y += 15;
            canvas.drawText("Price: " + cartItem.getItemPrice(), x, y, paint);
            y += 15;
            canvas.drawText("Quantity: " + cartItem.getQuantity(), x, y, paint);
            y += 20;
        }

        // Finish the page
        pdfDocument.finishPage(page);

        // Specify the file path for the PDF
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Order_" + orderId + ".pdf";

        // Create a file output stream
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            // Write the document content to the file
            pdfDocument.writeTo(outputStream);
            // Show a toast indicating successful PDF creation
            showToast("PDF created successfully. Location: " + filePath);
        } catch (IOException e) {
            Log.e("OrderHistoryAdapter", "Error writing PDF.", e);
            // Handle the error
        }

        // Close the document
        pdfDocument.close();
    }



    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
