package com.example.foodex;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

        public class CheckoutActivity extends AppCompatActivity {

            private TextView TotalAmount, Note;
            private Button btnNextStep, btnDate, btnpickup, btndrop, btnback;
            private EditText editTextDeliveryAddress, editTextPickupTime, editTextDropTime, OrderDate;
            private Calendar pickupTimeCalendar, dropTimeCalendar;
            private SimpleDateFormat dateFormatter;
            private FirebaseAuth mAuth;
            private FirebaseFirestore db;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate ( savedInstanceState );
                setContentView ( R.layout.activity_checkout );
                mAuth = FirebaseAuth.getInstance ();
                db = FirebaseFirestore.getInstance ();
                TotalAmount = findViewById ( R.id.TotalAmount );
                double totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
                String payableAmountText = "Total Amt:₹" + String.format(Locale.getDefault(), "%.2f", totalAmount);
                TotalAmount.setText(payableAmountText);
                editTextDeliveryAddress = findViewById ( R.id.Delivery );
                editTextPickupTime = findViewById ( R.id.Pickuptime );
                editTextDropTime = findViewById ( R.id.droptime );
                Note = findViewById ( R.id.Note );
                btnNextStep = findViewById ( R.id.btnNextStep );
                btnback = findViewById ( R.id.btnback );
                btnDate = findViewById ( R.id.btnDate );
                OrderDate = findViewById ( R.id.OrderDate );
                btnpickup = findViewById ( R.id.btnpickup );
                btndrop = findViewById ( R.id.btndrop );

                pickupTimeCalendar = Calendar.getInstance ();
                dropTimeCalendar = Calendar.getInstance ();
                dateFormatter = new SimpleDateFormat ( "MM/dd/yyyy", Locale.US );
                List<CartActivity.CartItem> cartItems = (List<CartActivity.CartItem>) getIntent().getSerializableExtra("cartItems");

                CartActivity cartActivity = new CartActivity();
                // Fetch total amount from CartActivity
                // Set onClickListener for the "Next Step" button
                btnNextStep.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        completeCheckout(cartItems);
                        }
                    } );


                // Set onClickListener for the pick-up time Button
                btnpickup.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog ( true );
                    }
                } );

                // Set onClickListener for the drop time Button
                btndrop.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog ( false );
                    }
                } );

                // Set onClickListener for the date Button
                btnDate.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog ();
                    }
                } );

                // Set onClickListener for the back Button
                btnback.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        // Handle the back button click, you can implement specific behavior
                        // For now, just finish the activity
                        finish ();
                    }
                } );
            }
            // Fetch delivery address from Firestore (replace this with your actual logic)
            private void fetchDeliveryAddress() {
                // Dummy logic, replace this with actual Firestore retrieval
                String deliveryAddress = "Dummy Delivery Address";

                // Check if delivery address is not null
                if (!TextUtils.isEmpty(deliveryAddress)) {



                } else {
                    // Display a Toast if no delivery address is detected
                    showToast("No delivery address detected. Please enter a delivery address.");
                }
            }

            // Validate pickup time
            private boolean validatePickupTime() {

                // Check if pickup time is within the allowed range (6 am to 9 am)
                if (!isTimeInRange(editTextPickupTime.getText().toString(), "06:00", "21:00")) {
                    showToast("Pickup time must be between 6 am and 9 pm");
                    return false;
                }

                // If all validation checks pass, return true
                return true;
            }

            // Validate drop time
            private boolean validateDropTime() {
                // ... (your existing validation logic)

                // Check if drop time is within the allowed range (6 am to 9 am)
                if (!isTimeInRange(editTextDropTime.getText().toString(), "06:00", "21:00")) {
                    showToast("Drop time must be between 6 am and 9 pm");
                    return false;
                }

                // If all validation checks pass, return true
                return true;
            }

            // Check if the given time is within the specified range
            private boolean isTimeInRange(String time, String startTime, String endTime) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
                    Date timeDate = sdf.parse(time);
                    Date startDate = sdf.parse(startTime);
                    Date endDate = sdf.parse(endTime);

                    return timeDate.after(startDate) && timeDate.before(endDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            // Validate date and time range (no past date, at least 3 hours gap)
            private boolean validateDateTimeRange(Calendar selectedTime) {
                Calendar currentTime = Calendar.getInstance();

                // Check if the selected date is the same day or a future day
                if (selectedTime.get(Calendar.DAY_OF_YEAR) != currentTime.get(Calendar.DAY_OF_YEAR)) {
                    showToast("Please select a future date");
                    return false;
                }

                return true;
            }

            // Complete the checkout process (e.g., proceed to payment)
            private void completeCheckout(List<CartActivity.CartItem> cartItems) {
                if (!validatePickupTime() || !validateDropTime() || !validateDateTimeRange(dropTimeCalendar)) {
                    Toast.makeText(this, "Correct", Toast.LENGTH_LONG).show();
                    return;
                }

                // Save order details to Firestore
                saveOrderDetailsToFirestore(cartItems);

                showToast("Processing to payment gateway");

                // Get relevant data to pass to PaymentActivity
                String orderId = generateOrderId();
                String totalAmount = TotalAmount.getText().toString().trim();
                String username = mAuth.getCurrentUser().getEmail();

                // Create an Intent to start PaymentActivity
                Intent paymentIntent = new Intent(CheckoutActivity.this, PaymentActivity.class);
                paymentIntent.putExtra("orderId", orderId);
                paymentIntent.putExtra("totalAmount", totalAmount);
                paymentIntent.putExtra("username", username);

                // Start PaymentActivity
                startActivity(paymentIntent);
            }
            private void saveOrderDetailsToFirestore(List<CartActivity.CartItem> cartItems) {
                String uid = mAuth.getCurrentUser().getUid();
                String username = mAuth.getCurrentUser().getEmail(); // Assuming email is the username

                // Create a new order document in Firestore
                String orderId = generateOrderId(); // Implement this method to generate a unique order ID

                // Get other details from the UI
                String deliveryAddress = editTextDeliveryAddress.getText().toString().trim();
                String pickupTime = editTextPickupTime.getText().toString().trim();
                String dropTime = editTextDropTime.getText().toString().trim();
                String specialInstructions = Note.getText().toString().trim();
                String totalAmount = TotalAmount.getText().toString().trim();

                // Create a new order object
                Order order = new Order(orderId, username, deliveryAddress, pickupTime, dropTime, specialInstructions, totalAmount, "Order Initiated", cartItems);

                // Create a reference to the "Orders" collection in Firestore
                CollectionReference ordersRef = db.collection("Orders");

                // Add the order details to Firestore
                ordersRef.document(orderId)
                        .set(order)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast("Order details saved successfully");
                                // Navigate to payment activity or perform other actions
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showToast("Error saving order details to Firestore");
                                e.printStackTrace();
                            }
                        });
            }

            private String generateOrderId() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
                String timestamp = dateFormat.format(new java.util.Date());

                // Combine the timestamp with a random number to ensure uniqueness
                int random = (int) (Math.random() * 1000);
                return timestamp + random;

            }

            // Show a TimePickerDialog for pick-up and drop times
            private void showTimePickerDialog(final boolean isPickupTime) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update the Calendar object with the selected time
                        Calendar selectedTime = isPickupTime ? pickupTimeCalendar : dropTimeCalendar;
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

                        // Update the corresponding EditText with the selected time
                        EditText editTextTime = isPickupTime ? editTextPickupTime : editTextDropTime;
                        editTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                    }
                };

                // Create and show the TimePickerDialog
                Calendar initialTime = isPickupTime ? pickupTimeCalendar : dropTimeCalendar;
                int initialHour = initialTime.get(Calendar.HOUR_OF_DAY);
                int initialMinute = initialTime.get(Calendar.MINUTE);

                new TimePickerDialog(
                        this,
                        timeSetListener,
                        initialHour,
                        initialMinute,
                        false
                ).show();
            }

            // Show a DatePickerDialog for the date
            private void showDatePickerDialog() {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the Calendar object with the selected date
                        pickupTimeCalendar.set(Calendar.YEAR, year);
                        pickupTimeCalendar.set(Calendar.MONTH, month);
                        pickupTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Update the corresponding EditText with the selected date
                        OrderDate.setText(dateFormatter.format(pickupTimeCalendar.getTime()));
                    }
                };

                // Create and show the DatePickerDialog
                Calendar currentDate = Calendar.getInstance();
                int currentYear = currentDate.get(Calendar.YEAR);
                int currentMonth = currentDate.get(Calendar.MONTH);
                int currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(
                        this,
                        dateSetListener,
                        currentYear,
                        currentMonth,
                        currentDayOfMonth
                ).show();
            }

            // Show a Toast message
            private void showToast(String message) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
