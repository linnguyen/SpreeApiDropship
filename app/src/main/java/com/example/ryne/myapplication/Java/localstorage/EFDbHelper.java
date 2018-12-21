package com.example.ryne.myapplication.Java.localstorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ryne on 06/09/2018.
 */

public class EFDbHelper extends SQLiteOpenHelper {
    /**
     * Database name.
     */
    public static final String DATABASE_NAME = "dropshipApp.db";
    /**
     * Database version. If you change the database schema, you must increase the database version.
     */
    private static final int DATABASE_VERSION = 2;
    /**
     * EFDbHelper instance.
     */
    private static EFDbHelper helper;

    /**
     * Constructor.
     *
     * @param contextApp The application context.
     */
    private EFDbHelper(final Context contextApp) {
        super(contextApp, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Get instance.
     *
     * @param context Application context
     * @return instance of EFDbHelper
     */
    public static synchronized EFDbHelper getInstance(final Context context) {
        if (helper == null) {
            helper = new EFDbHelper(context);
        }
        return helper;
    }

    /**
     * This function is used to reset the helper instance.
     */
    public static void resetHelper() {
        if (helper != null) {
            helper = null;
        }
    }

    /**
     * onCreate method. This method is used to create tables
     *
     * @param db The SQLiteDatabase.
     */
    public final void onCreate(final SQLiteDatabase db) {
        db.execSQL(EFDbContract.SQL_CREATE_PRODUCT);
    }

    /**
     * onUpgrade method. This method is used to upgrade the database scheme
     *
     * @param db         The SQLiteDatabase.
     * @param oldVersion The old version.
     * @param newVersion The new version.
     */
    public final void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                                final int newVersion) {
        if (oldVersion > newVersion) { // Begin to alter db from version 45.
            deleteDatabase(db);
        } else {
            int oVersion = oldVersion;
            while (oVersion < newVersion) {
                switch (oVersion) {
                    case 1:
                        break;
                    default:
                        deleteDatabase(db);
                        break;
                }
                oVersion++;
            }
        }
    }

    /**
     * onDowngrade method. This method is used to downgrade the database scheme
     *
     * @param db         The SQLiteDatabase.
     * @param oldVersion The old version.
     * @param newVersion The new version.
     */
    public final void onDowngrade(final SQLiteDatabase db, final int oldVersion,
                                  final int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Delete all data.
     *
     * @param db Database
     */
    public final void deleteDatabase(final SQLiteDatabase db) {
        db.execSQL(EFDbContract.SQL_DELETE_RECIPE);
        // Create again
        onCreate(db);
    }
}
