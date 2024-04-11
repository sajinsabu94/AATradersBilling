package com.xeta.app.aatraders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.zj.usbsdk.UsbController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by Anonymous on 04/25/18.
 */

public class BillInfoActivity extends AppCompatActivity implements Runnable{
    ArrayList<CartItem> products;
    //String[] items;
    int n;
    TextView dt;
    TableView<CartItem> tableView;
    TextView gtp;
    ShopDB shopdb;
    String stringItem;
    String stringHSN;
    String stringGST;
    String stringRate;
    String stringQty;
    String qtyItem;
    String rateItem;
    String total;
    String totalpayable;
    TextView billNoInfo;
    String ext;

    BillingBean billDetails;

    int billno;
    Button btnPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_print_again);
        Intent get = getIntent();
        ext = get.getStringExtra("billno");

        shopdb = new ShopDB(this);
    //    shopdb.getReadableDatabase();

        stringItem = null;
        qtyItem = null;

        //Toast.makeText(this, ext+" "+ext.length(), Toast.LENGTH_SHORT).show();

        billNoInfo = findViewById(R.id.billInfoNoprintShow);
        billNoInfo.setText(ext);
 //       shopdb = new ShopDB(this);

        tableView = findViewById(R.id.myTableBillLayoutprint);

        billNoInfo = findViewById(R.id.GrandTotalPayableprint);
        dt = findViewById(R.id.billInfoDateprintShow);
        gtp = findViewById(R.id.GrandTotalPayableprint);

        //billnumber = billNoInfo.getText().toString();
        Cursor cursor = shopdb.getBillInfoNew(ext);

        Log.e("Number : ", Integer.toString(cursor.getCount()));
        //Toast.makeText(this, cursor2.getCount(), Toast.LENGTH_SHORT).show();


        if(cursor != null && (cursor.getCount()> 0)){

            int billnoCol = cursor.getColumnIndex("billno");
            int itemsCol = cursor.getColumnIndex("items");
            int hsnCol = cursor.getColumnIndex("hsn");
            int gstCol = cursor.getColumnIndex("gst");
            int rateCol = cursor.getColumnIndex("rate");
            int qtyCol = cursor.getColumnIndex("qty");
            int dattimeCol = cursor.getColumnIndex("dattime");
            int amountCol = cursor.getColumnIndex("amount");
            cursor.moveToFirst();
            //Toast.makeText(this, cursor2.getColumnCount() + " "+ cursor2.getCount(), Toast.LENGTH_SHORT).show();
            String billno = cursor.getString(billnoCol);
            BillingBean billingBean = new BillingBean();
            billingBean.setInvoiceNumber(billno);
            String dattime = cursor.getString(dattimeCol);
            dt.setText(dattime);
            cursor.moveToFirst();
            float total = 0.0f;

            do{
                CartItem cartItem = new CartItem();
                String items = cursor.getString(itemsCol);
                String hsn = cursor.getString(hsnCol);
                String gst = cursor.getString(gstCol);
                String rate = cursor.getString(rateCol);
                String qty = cursor.getString(qtyCol);
                String amount = cursor.getString(amountCol);

                float temp = Float.parseFloat(amount);
                total+=temp;

                cartItem.setHsn(hsn);
                cartItem.setName(items);
                cartItem.setRate(rate);
                cartItem.setQty(qty);
                cartItem.setDis("0");
                cartItem.setGst(gst);
                cartItem.calculateGTotal();
                cartItem.calculateCSGST();
                cartItem.setType("nos");
                cartItem.setTotal(amount);
                billingBean.setProducts(cartItem);
                Log.e("pdt : ",cartItem.toString());
            }while(cursor.moveToNext());

            TableColumnWeightModel columnModel = new TableColumnWeightModel(11);
            columnModel.setColumnWeight(0, 1);
            columnModel.setColumnWeight(1, 2);
            columnModel.setColumnWeight(2, 2);
            columnModel.setColumnWeight(3, 1);
            columnModel.setColumnWeight(4, 1);
            columnModel.setColumnWeight(5, 1);
            columnModel.setColumnWeight(6, 1);
            columnModel.setColumnWeight(7, 1);
            columnModel.setColumnWeight(8, 1);
            columnModel.setColumnWeight(9, 1);
            columnModel.setColumnWeight(10, 1);
            tableView.setColumnModel(columnModel);

            String[] TABLE_HEADERS = { "Sl.No.", "Name", "HSN Code", "GST%", "Unit price", "Quantity",  "Discount", "Gross Total", "CGST", "SGST", "Total"};
            products = new ArrayList<CartItem>();

            products = billingBean.getProducts();



            tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
            tableView.setDataAdapter(new CartItemDataAdapter(this, products));

            gtp.setText(String.format("%.2f",total));

        }

    }



    public void exitBillInfo(View view) {
        finish();
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    public void run() {

    }



    public void goBack(View view) {

    }
}
