package com.example.android.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.inventory.R;

public class InventoryProvider extends ContentProvider{

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher UriMatch = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        UriMatch.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, PRODUCTS);

        UriMatch.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PRODUCT_ID);
    }

    private InventoryDbHelper DbHelper;

    @Override
    public boolean onCreate() {
        DbHelper = new InventoryDbHelper((getContext()));
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = DbHelper.getReadableDatabase();

        Cursor cursor;

        int match = UriMatch.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException(uri + "is not supported");
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("unknown URI and match" + uri + match);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String nameProduct = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
        if (nameProduct == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.provide_name));
        }

        Integer priceProduct = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
        if (priceProduct != null && priceProduct < 0) {
            throw new IllegalArgumentException("Provide Product price");
        }

        Integer quantityProduct = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY);
        if (quantityProduct != null && quantityProduct < 0) {
            throw new IllegalArgumentException("Provide Product quantity");
        }

        String supplierName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER);
        if (supplierName == null) {
            throw new IllegalArgumentException("Provide Supplier name");
        }

        Integer supplierPhone = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
        if (supplierPhone != null && supplierPhone < 0) {
            throw new IllegalArgumentException("Provide Supplier phone number");
        }

        SQLiteDatabase database = DbHelper.getWritableDatabase();
        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[]
            selectionArgs) {
        final int match = UriMatch.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(uri + " cannot be updated");
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = DbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = UriMatch.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(uri + " cannot be deleted");
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_NAME)) {
            String nameProduct = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_NAME);
            if (nameProduct == null) {
                throw new IllegalArgumentException("Provide Product name");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_PRICE)) {
            Integer priceProduct = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            if (priceProduct != null && priceProduct < 0) {
                throw new
                        IllegalArgumentException("Provide Product price");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY)) {
            Integer quantityProduct = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_STOCK_QUANTITY);
            if (quantityProduct != null && quantityProduct < 0) {
                throw new
                        IllegalArgumentException("Provide product quantity");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER)) {
            String supplierName = values.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER);
            if (supplierName == null) {
                throw new IllegalArgumentException("Provide Supplier name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE)) {
            Integer supplierPhone = values.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE);
            if (supplierPhone != null && supplierPhone < 0) {
                throw new
                        IllegalArgumentException("Provide Supplier Phone");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = DbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}

