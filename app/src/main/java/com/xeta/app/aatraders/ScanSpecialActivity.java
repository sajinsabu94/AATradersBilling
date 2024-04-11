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
import android.util.Log;
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

public class ScanSpecialActivity extends AppCompatActivity {
    Spinner spinner;
    AutoCompleteTextView name, hsn;
    EditText rate,qty, rater;
    TextView total, gtotal, gst, sgst, cgst;
    String valGST;
    ItemDB itemdb;
    int itemQty;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_special);
        itemdb = new ItemDB(this);
        itemQty = 0;

        hsn = findViewById(R.id.IdSS);
        name = findViewById(R.id.nameSS);
        qty = (EditText) findViewById(R.id.qtySS);
        qty.addTextChangedListener(qtyWatcher);

        rate = (EditText) findViewById(R.id.rateSS);
        rate.setEnabled(false);
        rater = (EditText) findViewById(R.id.rateSSRetail);
        //rater.setEnabled(false);
        total = (TextView) findViewById(R.id.totalSS);
        gtotal = (TextView) findViewById(R.id.grossSS);
        gst = (TextView) findViewById(R.id.gstSS);
        cgst = (TextView) findViewById(R.id.cgstSS);
        sgst = (TextView) findViewById(R.id.sgstSS);

        String[] arrayQuantity = new String[] {
                "nos", "m", "mm", "cm"
        };
        spinner = (Spinner) findViewById(R.id.spinnerQty);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayQuantity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> itemadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items());
        name.setAdapter(itemadapter);
        name.addTextChangedListener(nameWatcher);
        hsn.setEnabled(false);
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



    public String getRemainingItem(String hsn){
        Cursor cursor = itemdb.getItemStockInfoHSN(hsn);
        String valQty = "0";
        int qtyCol = cursor.getColumnIndex("qty");
        cursor.moveToFirst();
        if(cursor != null && (cursor.getCount()> 0)){
            valQty = cursor.getString(qtyCol);
            //Log.e("Item Rem: ",valQty);
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
            int rateColR = cursor.getColumnIndex("rateretail");
            int typeCol = cursor.getColumnIndex("type");
            int gstCol = cursor.getColumnIndex("gst");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String hsnd = cursor.getString(hsnCol);
                String srate = cursor.getString(rateCol);
                String srater = cursor.getString(rateColR);
                String stype = cursor.getString(typeCol);
                valGST = cursor.getString(gstCol);
                hsn.setText(hsnd);
                rate.setText(srate);
                rater.setText(srater);
                gst.setText(valGST+"%");

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                name.setEnabled(false);
                hsn.setEnabled(false);
                name.setFocusable(false);
                name.clearFocus();
                qty.setFocusable(true);

                Log.e("Item Rem: ",getRemainingItem(hsnd));

                itemQty = Integer.parseInt(getRemainingItem(hsnd));
                if(itemQty==0){
                    qty.setEnabled(false);
                }
                qty.setFilters(new InputFilter[]{ new InputFilterMinMax("1", Integer.toString(itemQty))});
                Toast.makeText(ScanSpecialActivity.this, "No. of item remaining : "+Integer.toString(itemQty), Toast.LENGTH_SHORT).show();
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
                String srater = rater.getText().toString();
                String sqnty = qty.getText().toString();
                String itemtype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                if(!sqnty.substring(sqnty.length()-1).equals('.')) {
                    if (srater.matches("\\d+(?:\\.\\d+)?") && sqnty.matches("\\d+(?:\\.\\d+)?")) {
                        float qt = Float.parseFloat(sqnty);
                        float rt = Float.parseFloat(srater);
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
        String srate = rater.getText().toString();
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
