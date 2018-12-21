package com.example.ryne.myapplication.Java.localstorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ryne.myapplication.Java.entity.response.ProductResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryne on 06/09/2018.
 */

public class DAProduct {
    private ContentValues getContentValues(final ProductResponse product, Context context) {
        ContentValues values = new ContentValues();
        values.put(EFDbContract.TBProduct.COLUMN_PRODUCT_ID, product.getId());
        values.put(EFDbContract.TBProduct.COLUMN_PRODUCT_NAME, product.getName());
        values.put(EFDbContract.TBProduct.COLUMN_STATUS, product.getStatus());
        return values;
    }

    private ProductResponse getFromCursor(final Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(EFDbContract.TBProduct.COLUMN_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndex(EFDbContract.TBProduct.COLUMN_PRODUCT_NAME));
        String status = cursor.getString(cursor.getColumnIndex(EFDbContract.TBProduct.COLUMN_STATUS));
        return new ProductResponse(id, name, status);
    }

    public long add(ProductResponse product, Context context) {
        long id = 0;
        EFDbHelper dbHelper = EFDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = getContentValues(product, context);
        if (db != null && db.isOpen()) {
            id = db.insert(EFDbContract.TBProduct.TABLE_NAME, null, values);
        }
        return id;
    }

    public List<ProductResponse> getAll(Context context) {
        List<ProductResponse> recipes = new ArrayList<>();
        EFDbHelper dbHelper = EFDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null && db.isOpen()) {
            Cursor cursor = db.query(EFDbContract.TBProduct.TABLE_NAME, getProjection(),
                    null, null, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                ProductResponse product;
                do {
                    product = getFromCursor(cursor);
                    recipes.add(product);
                } while (cursor.moveToNext());
            }
            // Close the cursor
            cursor.close();
        }
        return recipes;
    }


    private String[] getProjection() {
        String[] projection = {
                EFDbContract.TBProduct.COLUMN_PRODUCT_ID,
                EFDbContract.TBProduct.COLUMN_PRODUCT_NAME,
                EFDbContract.TBProduct.COLUMN_STATUS,
        };
        return projection;
    }

    public boolean deleteAll(Context context) {
        EFDbHelper dbHelper = EFDbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db != null && db.isOpen()
                && db.delete(EFDbContract.TBProduct.TABLE_NAME, null, null) > 0;
    }

}
