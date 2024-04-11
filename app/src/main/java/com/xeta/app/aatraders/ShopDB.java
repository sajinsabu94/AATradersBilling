package com.xeta.app.aatraders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Anonymous on 04/24/18.
 */

public class ShopDB extends SQLiteOpenHelper {
    final static String DB_NAME = "store";
    SQLiteDatabase db;
    public ShopDB(Context context) {
        super(context, DB_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public boolean addTransactionToDB(String billno, String items, String hsn, String gst, String rate, String qty, String datetime, String total){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("billno", billno);
        values.put("items", items);
        values.put("hsn", hsn);
        values.put("gst", gst);
        values.put("rate", rate);
        values.put("qty", qty);
        values.put("dattime", datetime);
        values.put("amount", total);
        long insert = db.insert("shop", null, values);

        //getWritableDatabase().execSQL("delete from shop where id not in (SELECT MIN(id) FROM shop GROUP BY billno)");
        return insert>0;
    }



    public int getMaxBillId(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT billno FROM shop WHERE id=(SELECT MAX(id) from shop)", null);
        int billCol = cursor.getColumnIndex("billno");
        int no = 1000;
        if(cursor != null && cursor.moveToFirst()) {
            String bill = cursor.getString(billCol);
            no = Integer.parseInt(bill);
        }
        cursor.close();
        return no;
    }

    public Cursor getBillInfoNew(String billno){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM shop WHERE billno = ?",new String[]{billno});
        return c;
    }

    public Cursor getBillInfoByDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(billno) as n1,sum(amount) as n2 FROM shop WHERE dattime = '"+date+"'",null);
        return c;
    }

    public Cursor getFullBillInfoByDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT distinct(billno) FROM shop WHERE dattime = '"+date+"'",null);
        return c;
    }

    public Cursor getBillInfoByMonth(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT count(billno) as n1,sum(amount) as n2 FROM shop WHERE dattime LIKE '___"+date+"'",null);
        return c;
    }

    public Cursor getFullBillInfoByMonth(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT distinct(billno) FROM shop WHERE dattime LIKE '___"+date+"'",null);
        return c;
    }

    public Cursor getproductInfo(String hsn){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM items_special WHERE hsn = '"+hsn+"'", null);
        return cur;

    }


    public boolean removeItemFromStock(String hsn, String qty){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE stock SET qty = qty - "+Integer.parseInt(qty)+" WHERE hsn = '"+hsn+"'");
        return true;
    }



    public Cursor searchDaily(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM daily", null);
        return cur;
    }

    public Cursor searchDailyDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM daily WHERE dattime = '"+date+"'", null);
        return cur;
    }

    public boolean isDailyExist(String hsn, String date){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM daily WHERE dattime = '"+date+"' AND hsn = '"+hsn+"'", null);
        cur.moveToFirst();
        if(cur.getCount()>0){
            return true;
        }
        return false;
    }

    public boolean updateDaily(String hsn, String a) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("qty", a);
        long insert = db.update("daily",values,"hsn = ?", new String[]{hsn});
        return insert>0;
    }

    public boolean insertDaily(String hsn, String name, String qty, String date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hsn", hsn);
        values.put("name", name);
        values.put("qty", qty);
        values.put("date", date);
        long insert = db.insert("daily", null, values);
        return insert>0;
    }

}
