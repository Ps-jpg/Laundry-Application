package com.example.foodex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
public class CartItemAdapter extends ArrayAdapter<CartActivity.CartItem> {

    private List<CartActivity.CartItem> cartItems;
    private Context context;
    private boolean showRemoveButton;


    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public CartItemAdapter(Context context, int resource, List<CartActivity.CartItem> cartItems,boolean showRemoveButton) {        super(context, resource, cartItems);
        this.context = context;
        this.cartItems = cartItems;
        this.showRemoveButton = showRemoveButton;
    }

    @Override
    public int getCount() {
        // Add 1 to account for the header
        return cartItems.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // Return the view type based on the position
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        // Return the number of view types (header and items)
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {



        // Check the view type and inflate the corresponding layout
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_HEADER) {
            // Header view
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.cart_item_header, parent, false);

                // Set the header titles if needed
                TextView headerItemName = convertView.findViewById(R.id.headerItemName);
                TextView headerItemPrice = convertView.findViewById(R.id.headerItemPrice);
                TextView headerWorkType = convertView.findViewById(R.id.headerWorkType);
                TextView headerQuantity = convertView.findViewById(R.id.headerQuantity);

                // Set titles for other columns if available in the header layout

                // Example titles, you should replace these with your actual titles
                headerItemName.setText("Item Name");
                headerItemPrice.setText("Item Price");
                headerWorkType.setText("Work Type");
                headerQuantity.setText("Quantity");
            }
        } else {
            // Regular item view
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.cart_item_row, parent, false);


                // Initialize the ViewHolder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.itemNameTextView = convertView.findViewById(R.id.tvCartItemName);
                viewHolder.quantityTextView = convertView.findViewById(R.id.quantityTextView);
                viewHolder.itemPriceTextView = convertView.findViewById(R.id.tvCartItemPrice);
                viewHolder.workTypeTextView = convertView.findViewById(R.id.workType);
                viewHolder.btnRemove = convertView.findViewById(R.id.btnRemove);
                if (showRemoveButton) {
                    viewHolder.btnRemove.setVisibility (View.VISIBLE);
                } else {
                    viewHolder.btnRemove.setVisibility(View.GONE);
                }

                // Set the ViewHolder as a tag for the view
                convertView.setTag(viewHolder);
            }

            // Get the current CartItem
            CartActivity.CartItem cartItem = cartItems.get(position - 1); // Adjust position for cart items

            // Get the ViewHolder from the tag
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();

            // Set the values for each TextView
            viewHolder.itemNameTextView.setText(cartItem.getItemName());
            viewHolder.itemPriceTextView.setText(cartItem.getItemPrice());
            viewHolder.workTypeTextView.setText(cartItem.getWorkType());
            viewHolder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));


            // Set the click listener for the remove button
            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the position of the item to be removed
                    int itemPosition = position - 1; // Adjust position for cart items

                    // Trigger the method to remove the item
                    ((CartActivity) context).onRemoveItemClick(itemPosition);

                }

            });
        }



        return convertView;
    }

    // ViewHolder pattern for better performance
    static class ViewHolder {
        TextView itemNameTextView;
        TextView quantityTextView;
        TextView itemPriceTextView;
        TextView workTypeTextView;
        Button btnRemove;
    }
}

