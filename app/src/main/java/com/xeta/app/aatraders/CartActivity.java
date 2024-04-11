package com.xeta.app.aatraders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Anonymous on 04/23/18.
 */

public class CartActivity extends AppCompatActivity {
    TableLayout productTable, tempTable;
    TextView bill;
    String category;
//    ArrayList<CartItem> items;
    BillingBean billingBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_billing);
        Intent getVal = getIntent();
        category = getVal.getStringExtra("category");
        bill = (TextView) findViewById(R.id.BillTotal);
        bill.setText("0.00");
        productTable = (TableLayout) findViewById(R.id.myTableLayout);
        Log.e("Category ",category);
//        items = new ArrayList<CartItem>();
        billingBean = new BillingBean();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String hsn = data.getStringExtra("hsn");
                String name = data.getStringExtra("name");
                String rate = data.getStringExtra("rate");
                String qty = data.getStringExtra("qty");
                String gst = data.getStringExtra("gst");
                String cgst = data.getStringExtra("cgst");
                String sgst = data.getStringExtra("sgst");
                String type = data.getStringExtra("type");
                String gtotal = data.getStringExtra("gtotal");
                String total = data.getStringExtra("total");

                CartItem tempItem = new CartItem(name, hsn, gst, rate, qty,"0", gtotal, cgst, sgst, total, type);
                if(!checkItemExist(tempItem)){
                    billingBean.setProducts(tempItem);

                    TableRow product = new TableRow(this);

                    TextView tname = new TextView(this);
                    TextView tqty = new TextView(this);
                    TextView trate = new TextView(this);
                    TextView ttotal = new TextView(this);

                    TextView thsn = new TextView(this);
                    TextView tdisc = new TextView(this);
                    TextView tgst = new TextView(this);
                    TextView tcgst = new TextView(this);
                    TextView tsgst = new TextView(this);
                    TextView tgtotal = new TextView(this);


                    ImageView del = new ImageView(this);
                    del.setClickable(true);
                    del.setImageResource(R.drawable.ic_delete_black_18dp);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final TableRow row = (TableRow) view.getParent();
                            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                            builder.setMessage("Do you want to remove the item?")
                                    .setTitle("Warning")
                                    .setCancelable(true)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            TextView tv = (TextView) row.getChildAt(0);
                                            String itm = tv.getText().toString();
                                            removeItemFromList(itm);

                                            productTable.removeView(row);
                                            generateTotal();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            builder.show();
                        }
                    });

                    tname.setText(name);
                    tqty.setText(qty+type);
                    trate.setText(rate);
                    ttotal.setText(total);

                    thsn.setText(hsn);
                    tgst.setText(gst);
                    tcgst.setText(cgst);
                    tsgst.setText(sgst);
                    tgtotal.setText(gtotal);

                    tdisc.setText("0");

                    product.addView(tname);
                    product.addView(trate);
                    product.addView(tqty);
                    product.addView(ttotal);
                    product.addView(del);

                    product.setPadding(0,10,0,10);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.RIGHT;
                    del.setLayoutParams(params);

                    productTable.addView(product);

                    thsn.setVisibility(View.GONE);
                    tgst.setVisibility(View.GONE);
                    tcgst.setVisibility(View.GONE);
                    tsgst.setVisibility(View.GONE);
                    tgtotal.setVisibility(View.GONE);
                    tdisc.setVisibility(View.GONE);
                    generateTotal();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                    builder.setMessage("Item Already exist in cart")
                            .setTitle("Warning")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.show();
                }


            }
        }
    }

    private boolean checkItemExist(CartItem tempItem) {
        for(CartItem eachItem : new ArrayList<CartItem>(billingBean.getProducts())){
            if(eachItem.getName().equals(tempItem.getName()) && eachItem.getHsn().equals(tempItem.getHsn())){
                return true;
            }
        }
        return false;
    }

    private void removeItemFromList(String itm) {
        for(CartItem eachItem : new ArrayList<CartItem>(billingBean.getProducts())){
            if(eachItem.getName().equals(itm)){
                //items.remove(eachItem);
                billingBean.removeProducts(eachItem);
            }
        }
    }


    public void addClickSpecial(View view) {
        if(category.equals("retail")) {
            Intent second = new Intent(this, ScanSpecialActivity.class);
            startActivityForResult(second, 0);
        }
        else {
            Intent second = new Intent(this, ScanSpecialWholeActivity.class);
            startActivityForResult(second, 0);
        }
    }

    public void proceedPay(View view) {
        String amt  = bill.getText().toString();
        float rf = Float.valueOf(amt);
        int r = Math.round(rf);
        float pay = r/(float)1.0;
        if(Float.valueOf(amt)==0.0){
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
            builder.setMessage("Cart is empty")
                    .setTitle("Warning")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            builder.show();
        }
        else {
            Intent proceed = new Intent(this, ProceedBillActivity.class);

            proceed.putExtra("values", billingBean);

            proceed.putExtra("billamnt", amt);
            proceed.putExtra("billamntpay", Float.toString(pay));
            proceed.putExtra("count", productTable.getChildCount());
            startActivity(proceed);
            //finish();
        }
    }
    private void generateTotal(){
        float sum=0;
        for (int i = 1; i < productTable.getChildCount(); i++) {
            View child = productTable.getChildAt(i);
            TableRow row = (TableRow) child;
            TextView temp = (TextView)row.getChildAt(3);
            float t = Float.parseFloat(temp.getText().toString());
            sum+=t;
        }
        bill.setText(String.valueOf(sum));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
