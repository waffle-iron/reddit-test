package com.prueba.rappi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reddit.db";

    private static final String TIPO_TEXTO = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PostEntry.TABLE_NAME + " (" +
                    PostEntry._ID + " INTEGER PRIMARY KEY," +
                    PostEntry.COLUMN_NAME_TITULO + TIPO_TEXTO + COMMA_SEP +
                    PostEntry.COLUMN_NAME_CORTA + TIPO_TEXTO + COMMA_SEP +
                    PostEntry.COLUMN_NAME_LARGA + TIPO_TEXTO + COMMA_SEP +
                    PostEntry.COLUMN_NAME_IMAGEN + TIPO_TEXTO + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PostEntry.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_TITULO = "titulo";
        public static final String COLUMN_NAME_CORTA = "desc_corta";
        public static final String COLUMN_NAME_LARGA = "desc_larga";
        public static final String COLUMN_NAME_IMAGEN = "imagen";
    }
}
