package com.xeta.app.aatraders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ScanSpecialWholeActivity extends AppCompatActivity {
    Spinner spinner;
    AutoCompleteTextView name, hsn;
    EditText rate,qty, ratew;
    TextView total, gtotal, gst, sgst, cgst;
    String valGST;
    ItemDB itemdb;
    int itemQty;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_special_wholesale);
        itemdb = new ItemDB(this);
        itemQty = 0;

        hsn = findViewById(R.id.IdSSw);
        name = findViewById(R.id.nameSSw);
        qty = (EditText) findViewById(R.id.qtySSw);
        qty.addTextChangedListener(qtyWatcher);

        rate = (EditText) findViewById(R.id.rateSSw);
        rate.setEnabled(false);
        ratew = (EditText) findViewById(R.id.rateSSWholew);
        //ratew.setEnabled(false);
        total = (TextView) findViewById(R.id.totalSSw);
        gtotal = (TextView) findViewById(R.id.grossSSw);
        gst = (TextView) findViewById(R.id.gstSSw);
        cgst = (TextView) findViewById(R.id.cgstSSw);
        sgst = (TextView) findViewById(R.id.sgstSSw);
        hsn.setEnabled(false);

        String[] arrayQuantity = new String[] {
                "nos", "m", "mm", "cm"
        };
        spinner = (Spinner) findViewById(R.id.spinnerQtyw);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayQuantity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> itemadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items());
        name.setAdapter(itemadapter);
        name.addTextChangedListener(nameWatcher);
/*
        ArrayAdapter<String> hsnadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items_hsn());
        hsn.setAdapter(hsnadapter);
        hsn.addTextChangedListener(hsnWatcher);
*/
    }

    private  String[] special_items () {

        int i=0;
        Cursor cursor = itemdb.getSpecialItemName();
        int nameCol = cursor.getColumnIndex("name");
        cursor.moveToFirst();
        String[] items = new String[cursor.getCount()];
        if(cursor != null && (cursor.getCount()> 0)){
            do {
                String id = cursor.getString(nameCol);
                items[i++] = id;
            }while (cursor.moveToNext());
        }
        return items;
    }

    private  String[] special_items_hsn() {

        int i=0;
        Cursor cursor = itemdb.getSpecialItemHSN();
        int nameCol = cursor.getColumnIndex("hsn");
        cursor.moveToFirst();
        String[] items = new String[cursor.getCount()];
        if(cursor != null && (cursor.getCount()> 0)){
            do {
                String id = cursor.getString(nameCol);
                items[i++] = id;
            }while (cursor.moveToNext());
        }
        return  items;
    }

    public String getRemainingItem(String hsn){
        Cursor cursor = itemdb.getItemStockInfoHSN(hsn);
        String valQty = "0";
        int qtyCol = cursor.getColumnIndex("qty");
        cursor.moveToFirst();
        if(cursor != null && (cursor.getCount()> 0)){
            valQty = cursor.getString(qtyCol);
        }
        return valQty;
    }

    private final TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String itemname = name.getText().toString();

            Cursor cursor = itemdb.getSpecialItemDetails(itemname);

            int hsnCol = cursor.getColumnIndex("hsn");
            int rateCol = cursor.getColumnIndex("rate");
            int rateColR = cursor.getColumnIndex("ratewhole");
            int typeCol = cursor.getColumnIndex("type");
            int gstCol = cursor.getColumnIndex("gst");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String hsnd = cursor.getString(hsnCol);
                String srate = cursor.getString(rateCol);
                String sratew = cursor.getString(rateColR);
                String stype = cursor.getString(typeCol);
                valGST = cursor.getString(gstCol);
                hsn.setText(hsnd);
                rate.setText(srate);
                ratew.setText(sratew);
                gst.setText(valGST+"%");

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                name.setEnabled(false);
                hsn.setEnabled(false);
                name.setFocusable(false);
                name.clearFocus();
                qty.setFocusable(true);

                itemQty = Integer.parseInt(getRemainingItem(hsnd));
                if(itemQty==0){
                    qty.setEnabled(false);
                }
                qty.setFilters(new InputFilter[]{ new InputFilterMinMax("1", Integer.toString(itemQty))});
                Toast.makeText(ScanSpecialWholeActivity.this, "No. of item remaining : "+Integer.toString(itemQty), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private final TextWatcher qtyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!name.getText().toString().isEmpty() && !rate.getText().toString().isEmpty() && !qty.getText().toString().isEmpty()) {
                String sratew = ratew.getText().toString();
                String sqnty = qty.getText().toString();
                String itemtype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                if(!sqnty.substring(sqnty.length()-1).equals('.')) {
                    if (sratew.matches("\\d+(?:\\.\\d+)?") && sqnty.matches("\\d+(?:\\.\\d+)?")) {
                        float qt = Float.parseFloat(sqnty);
                        float rt = Float.parseFloat(sratew);
                    /*
                    float total = (float) qt*rt;
                    textTotal.setText(String.valueOf(total));
                    */
                        float crate = (float) (qt * rt);
                        gtotal.setText(String.valueOf(crate));
                        float vgst = Float.parseFloat(valGST);
                        float prctg = (crate*vgst)/100;
                        String temp = String.format("%.2f",prctg);
                        float cval = Float.valueOf(temp);
                        sgst.setText(temp);
                        cgst.setText(temp);

                        float total_t = crate+(cval*2);
                        temp = String.format("%.2f",total_t);
                        total.setText(temp);
                    }
                }
            }
        }
    };

    public void addSpecialItem(View view) {
        String shsn = hsn.getText().toString();
        String sname = name.getText().toString();
        String srate = ratew.getText().toString();
        String test = rate.getText().toString();
        String sqty = qty.getText().toString();
        String gstd = gst.getText().toString();
        String cgstd = cgst.getText().toString();
        String sgstd = sgst.getText().toString();
        String stype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        String sgtotal = gtotal.getText().toString();
        String stotal = total.getText().toString();

        if(!srate.isEmpty()&& !test.isEmpty() && !sqty.equals("")) {
            Intent back = new Intent();
            back.putExtra("hsn", shsn);
            back.putExtra("name", sname);
            back.putExtra("rate", srate);
            back.putExtra("qty", sqty);
            back.putExtra("gst", gstd);
            back.putExtra("cgst", cgstd);
            back.putExtra("sgst", sgstd);
            back.putExtra("type", stype);
            back.putExtra("gtotal", sgtotal);
            back.putExtra("total", stotal);
            setResult(RESULT_OK, back);
            finish();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Item not available or Invald entry")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    public void cancelScanSpecial(View view) {
        finish();
    }
}

class InputFilterMinMax implements InputFilter {

    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}