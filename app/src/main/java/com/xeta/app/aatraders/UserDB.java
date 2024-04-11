package com.xeta.app.aatraders;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anonymous on 04/26/18.
 */

public class UserDB extends SQLiteOpenHelper{
    private static final String DB_NAME = "store";
    SQLiteDatabase db;


    public UserDB(Context context) {
        super(context, DB_NAME, null, 2);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createSpecialItem = "CREATE TABLE items_special(hsn INTEGER PRIMARY KEY, name VARCHAR(100), rate VARCHAR(100), ratewhole VARCHAR(100), rateretail VARCHAR(100), gst VARCHAR(10), type VARCHAR(25))";
        sqLiteDatabase.execSQL(createSpecialItem);

        String createShop = "CREATE TABLE shop(id INTEGER PRIMARY KEY AUTOINCREMENT, billno VARCHAR(50), items VARCHAR(1000), hsn VARCHAR(1000), gst VARCHAR(1000), rate VARCHAR(1000), qty VARCHAR(1000), dattime varchar(50), amount VARCHAR(20))";
        sqLiteDatabase.execSQL(createShop);

        String createUser = "CREATE TABLE user(id VARCHAR(50), pass VARCHAR(50))";
        sqLiteDatabase.execSQL(createUser);

        String createStock = "CREATE TABLE stock(hsn VARCHAR(50), qty VARCHAR(50))";
        sqLiteDatabase.execSQL(createStock);

        String createDaily = "CREATE TABLE daily(hsn VARCHAR(100), name VARCHAR(50), qty VARCHAR(100), dattime DATE)";
        sqLiteDatabase.execSQL(createDaily);

        ContentValues values = new ContentValues();
        values.put("id", "admin");
        values.put("pass", "123456");
        long insert = sqLiteDatabase.insert("user", null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor readLogin(String pass){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT pass FROM user WHERE pass= '"+pass+"' ", null);
        return cur;
    }
    public boolean updateUser(String original,  String pass){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("pass", pass);
        long insert = db.update("user",values,"id = ?", new String[]{original});
        return insert>0;
    }

}
