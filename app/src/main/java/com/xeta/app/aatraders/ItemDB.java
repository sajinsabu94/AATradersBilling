package com.xeta.app.aatraders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ItemDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "store";

    public ItemDB(Context context) {
        super(context, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    public void deleteSpecialItem(String hsn){
        getWritableDatabase().execSQL("delete from items_special where hsn='"+hsn+"'");
        getWritableDatabase().execSQL("delete from stock where hsn='"+hsn+"'");
    }


    public boolean addSpecialItem(String hsn, String name, String rate, String ratewhole, String rateretail, String gst, String qty, String type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hsn", hsn);
        values.put("name", name);
        values.put("rate", rate);
        values.put("ratewhole", ratewhole);
        values.put("rateretail", rateretail);
        values.put("gst", gst);
        values.put("type", type);
        long insert = db.insert("items_special", null, values);
        if(insert>0) {
            ContentValues valuesStock = new ContentValues();
            valuesStock.put("hsn", hsn);
            valuesStock.put("qty", qty);
            long insertStock = db.insert("stock", null, valuesStock);
            return insertStock>0;
        }
        return false;
    }
    public boolean updateSpecialItem(String original, String hsn, String name, String rate, String ratewhole, String rateretail, String gst, String qty, String type){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hsn", hsn);
        values.put("name", name);
        values.put("rate", rate);
        values.put("ratewhole", ratewhole);
        values.put("rateretail", rateretail);
        values.put("gst", gst);
        values.put("type", type);
        long insert = db.update("items_special",values,"hsn= ?", new String[]{original});
        if(insert>0) {
            db.execSQL("UPDATE stock SET qty = qty + "+Integer.parseInt(qty)+" WHERE hsn = '"+hsn+"'");
            return true;
        }
        return false;
    }

    public Cursor getStockInfo(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT a.name as name, a.hsn as hsn, b.qty as qty FROM items_special a, stock b WHERE a.hsn = b.hsn", null);
        return cur;

    }

    public Cursor getItemStockInfoHSN(String hsn){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT qty FROM stock where hsn = '"+hsn+"'", null);
        return cur;

    }

    public Cursor getItemStockInfo(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT b.name as name, a.hsn as hsn, a.qty as qty FROM stock a, items_special b WHERE b.name = '"+name+"' and a.hsn = b.hsn", null);
        return cur;

    }

    public Cursor getSpecialItemDetailsWithHSN(String hsn){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM items_special WHERE hsn = '"+hsn+"'", null);
        return cur;
    }

    public Cursor getSpecialItemHSN(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT hsn FROM items_special", null);
        return cur;
    }


    public Cursor getSpecialItemName(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT name FROM items_special", null);
        return cur;
    }


    public Cursor getSpecialItemDetails(String name){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM items_special WHERE name = '"+name+"'", null);
        return cur;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
