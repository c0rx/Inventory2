package com.example.android.inventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    private Uri currentProductUri;

    private EditText productName;
    private EditText productPrice;
    private EditText productQuantity;
    private EditText supplierName;
    private EditText supplierPhone;


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
                 return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(getString(R.string.add_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productQuantity = findViewById(R.id.theproduct_quantity);
        supplierName = findViewById(R.id.supplier_name);
        supplierPhone = findViewById(R.id.supplier_phone);

        productName.setOnTouchListener(touchListener);
        productPrice.setOnTouchListener(touchListener);
        productQuantity.setOnTouchListener(touchListener);
        supplierName.setOnTouchListener(touchListener);
        supplierPhone.setOnTouchListener(touchListener);

    }

    private void saveProduct() {
        String productNameString = productName.getText().toString().trim();
        String productPriceString = productPrice.getText().toString().trim();
        String productQuantityString = productQuantity.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhone.getText().toString().trim();
        String supplierNameString = supplierName.getText().toString().trim();
        if (currentProductUri == null) {
            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.provide_price), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierNameString)) {
                Toast.makeText(this, getString(R.string.provide_supplier), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.provide_quantity), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.provide_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, productNameString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, productPriceString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY, productQuantityString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER, supplierNameString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, supplierPhoneNumberString);

            Uri newUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {

            if (TextUtils.isEmpty(productNameString)) {
                Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productQuantityString)) {
                Toast.makeText(this, getString(R.string.provide_quantity), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(productPriceString)) {
                Toast.makeText(this, getString(R.string.provide_price), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierNameString)) {
                Toast.makeText(this, getString(R.string.provide_supplier), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierPhoneNumberString)) {
                Toast.makeText(this, getString(R.string.provide_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, productNameString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, productPriceString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY, productQuantityString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER, supplierNameString);
            values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, supplierPhoneNumberString);

            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveProduct();
                return true;
            case android.R.id.home:
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
            return;
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);

            String currentName = cursor.getString(nameColumnIndex);
            int currentPrice = cursor.getInt(priceColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            productName.setText(currentName);
            productPrice.setText(Integer.toString(currentPrice));
            productQuantity.setText(Integer.toString(currentQuantity));
            supplierName.setText(currentSupplierName);
            supplierPhone.setText(Integer.toString(currentSupplierPhone));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.getText().clear();
        productPrice.getText().clear();
        productQuantity.getText().clear();
        supplierName.getText().clear();
        supplierPhone.getText().clear();

    }
}
