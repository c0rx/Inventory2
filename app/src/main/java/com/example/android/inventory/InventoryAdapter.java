package com.example.android.inventory;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventory.data.InventoryContract;

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        TextView Name = view.findViewById(R.id.product_name);
        TextView Price = view.findViewById(R.id.product_price);
        TextView Quantity = view.findViewById(R.id.product_quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        final int id = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
        int productNameI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
        int productPriceI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
        int productQuantityI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY);

        final String productID = cursor.getString(id);
        String productName = cursor.getString(productNameI);
        String productPrice = cursor.getString(productPriceI);
        final String productQuantity = cursor.getString(productQuantityI);

        Name.setText(productName);
        Price.setText(context.getString(R.string.product_price) + " : " + productPrice);
        Quantity.setText(context.getString(R.string.product_quantity) + " : " + productQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity Activity = (MainActivity) context;
                Activity.productSaleCount(Integer.valueOf(productID), Integer.valueOf(productQuantity));
            }
        });

        Button productViewButton = view.findViewById(R.id.view_button);
        productViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), ProductActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, Long.parseLong(productID));
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });

        Button productEditButton = view.findViewById(R.id.edit_button);
        productEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, Long.parseLong(productID));
                intent.setData(currentProductUri);
                context.startActivity(intent);
            }
        });
    }
}