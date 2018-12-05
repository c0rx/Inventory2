package com.example.android.inventory;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.android.inventory.data.InventoryContract;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri currentProductUri;

    private TextView productName;
    private TextView productPrice;
    private TextView productQuantity;
    private TextView supplierName;
    private TextView supplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productQuantity = findViewById(R.id.product_quantity);
        supplierName = findViewById(R.id.supplier_name);
        supplierPhone = findViewById(R.id.supplier_phone);

        Intent intent = getIntent();
        currentProductUri = intent.getData();
        if (currentProductUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.PRODUCT_NAME,
                InventoryContract.InventoryEntry.PRODUCT_PRICE,
                InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            final int idColumnI = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int priceColumnI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int quantityColumnI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY);
            int supplierNameColumnI = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER);
            int supplierPhoneColumnI = cursor.getColumnIndex( InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);

            String currentName = cursor.getString(nameColumnI);
            final int currentPrice = cursor.getInt(priceColumnI);
            final int currentQuantity = cursor.getInt(quantityColumnI);
            String currentSupplierName = cursor.getString(supplierNameColumnI);
            final int currentSupplierPhone = cursor.getInt(supplierPhoneColumnI);

            productName.setText(currentName);
            productPrice.setText(Integer.toString(currentPrice));
            productQuantity.setText(Integer.toString(currentQuantity));
            supplierName.setText(currentSupplierName);
            supplierPhone.setText(Integer.toString(currentSupplierPhone));

            Button productDecreaseButton = findViewById(R.id.minus_button);
            productDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseCount(idColumnI, currentQuantity);
                }
            });

            Button productIncreaseButton = findViewById(R.id.plus_button);
            productIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseCount(idColumnI, currentQuantity);
                }
            });

            Button productDeleteButton = findViewById(R.id.delete_button);
            productDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            Button phoneButton = findViewById(R.id.phone_button);
            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = String.valueOf(currentSupplierPhone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void decreaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
           }
    }

    public void increaseCount(int productID, int productQuantity) {
        productQuantity = productQuantity + 1;
        if (productQuantity >= 0) {
            updateProduct(productQuantity);
        }
    }

    private void updateProduct(int productQuantity) {

        if (currentProductUri == null) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY, productQuantity);

        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
            }
        }
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

        }
        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this product?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
