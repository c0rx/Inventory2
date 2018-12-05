package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory.data.InventoryContract;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    InventoryAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView inventoryListView = findViewById(R.id.list);

        TextView emptyView = findViewById(R.id.empty_text_view);
        inventoryListView.setEmptyView(emptyView);

        cursorAdapter = new InventoryAdapter(this, null);
        inventoryListView.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    public void productSaleCount(int productID, int productQuantity) {
        productQuantity = productQuantity - 1;
        if (productQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY, productQuantity);
            Uri updateUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, productID);
            int rowsAffected = getContentResolver().update(updateUri, values, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.add_dummy:
                addDummy();
                return true;
            case R.id.add_new:
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                InventoryContract.InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryEntry.CONTENT_URI, null, null);
        Toast.makeText(this, rowsDeleted + " " + getString(R.string.deleted_all), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.question_delete_all);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProducts();
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

    public void addDummy(){
        ContentValues values = new ContentValues();

        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, "random name");
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, "11");
        values.put(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY, "11");
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER, "random supplier");
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, "123");

        Uri nUri = getContentResolver().insert(InventoryContract.InventoryEntry.CONTENT_URI, values);


    }
}



