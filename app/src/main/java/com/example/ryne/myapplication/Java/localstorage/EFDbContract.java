package com.example.ryne.myapplication.Java.localstorage;

import android.provider.BaseColumns;

/**
 * Created by ryne on 06/09/2018.
 */

public class EFDbContract {
    /**
     * Text data type.
     */
    private static final String TEXT_TYPE = " TEXT";
    /**
     * Integer data type.
     */
    private static final String INTEGER_TYPE = " INTEGER";
    /**
     * Real data type.
     */
    private static final String REAL_TYPE = " REAL";
    /**
     * Comma symbol.
     */
    private static final String COMMA_SEP = ",";
    /**
     * Left bracket symbol.
     */
    private static final String LEFT_BRACKET_SEP = " (";
    /**
     * Right bracket symbol.
     */
    private static final String RIGHT_BRACKET_SEP = " );";
    /**
     * Primary key.
     */
    private static final String PRIMARY_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    /**
     * Create table statement.
     */
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

    /**
     * Create query for PRODUCT table.
     */
    public static final String SQL_CREATE_PRODUCT = new StringBuilder(CREATE_TABLE)
            .append(TBProduct.TABLE_NAME).append(LEFT_BRACKET_SEP)
            .append(TBProduct.COLUMN_PRODUCT_ID)
            .append(COMMA_SEP)
            .append(TBProduct.COLUMN_PRODUCT_NAME).append(TEXT_TYPE)
            .append(COMMA_SEP)
            .append(TBProduct.COLUMN_STATUS).append(TEXT_TYPE)
            .append(RIGHT_BRACKET_SEP)
            .toString();

    /**
     * Drop table statement.
     */
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    /**
     * Drop query for CONTACT table.
     */
    static final String SQL_DELETE_RECIPE = new StringBuilder(DROP_TABLE)
            .append(TBProduct.TABLE_NAME).toString();

    /**
     * Constructor. Prevents the EFDbContract class from being instantiated.
     */
    private EFDbContract() {
    }


    public abstract static class TBProduct implements BaseColumns {
        /**
         * Table name.
         */
        public static final String TABLE_NAME = "product";

        /**
         * Recipe id
         */
        public static final String COLUMN_PRODUCT_ID = "product_id";

        /**
         * product name column.
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";

        /**
         * status column.
         */
        public static final String COLUMN_STATUS = "status";

    }


}
