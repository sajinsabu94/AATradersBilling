package com.xeta.app.aatraders;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Anonymous on 04/23/18.
 */

public class ItemAddSpecialActivity extends AppCompatActivity {
    Spinner spinner, spinnerGST;
    AutoCompleteTextView name, hsn;
    EditText qty, rate, ratewhole, rateretail;
    ItemDB itemdb;
    String updatename;
    String updatehsn;
    boolean isUpdating = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_special_item);
        hsn = (AutoCompleteTextView) findViewById(R.id.editTextSpecialHSN);
        name = (AutoCompleteTextView) findViewById(R.id.editTextSpecialName);
        qty = (EditText) findViewById(R.id.editTextSpecialQty);
        rate = (EditText) findViewById(R.id.editTextSpecialRate);
        ratewhole = (EditText) findViewById(R.id.editTextSpecialRateWholesale);
        rateretail = (EditText) findViewById(R.id.editTextSpecialRateRetail);
        itemdb = new ItemDB(this);
        hsn.setText("");
        name.setText("");
        qty.setText("0");

        String[] arrayQuantity = new String[] {
                "nos", "m", "mm", "cm"
        };

        String[] arrayGST = new String[] {
                "12", "18"
        };

        //Spinner Start
        spinner = (Spinner) findViewById(R.id.spinnerSpecialQty);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayQuantity);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerGST = (Spinner) findViewById(R.id.spinnerSpecialGST);
        ArrayAdapter<String> adapterGST = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayGST);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGST.setAdapter(adapterGST);

        //Spinner End

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

    public void addSpecialItemDB(View view) {
        boolean res;

        String itemhsn = hsn.getText().toString();
        String item = name.getText().toString();
        String itemqty = qty.getText().toString();
        String itemrate = rate.getText().toString();
        String itemratewhole = ratewhole.getText().toString();
        String itemrateretail = rateretail.getText().toString();
        String itemtype = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        String itemgst = spinnerGST.getItemAtPosition(spinnerGST.getSelectedItemPosition()).toString();

        if (!item.trim().isEmpty() && !itemqty.trim().isEmpty() && !itemrate.trim().isEmpty() && !itemratewhole.trim().isEmpty() && !itemrateretail.trim().isEmpty()) {
            item = capitalizeWord(item);
            if(!isUpdating) {
                res = itemdb.addSpecialItem(itemhsn, item, itemrate, itemratewhole, itemrateretail, itemgst, itemqty, itemtype);
                if (res) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Item Added Successfully")
                            .setTitle("Message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Error Adding Item")
                            .setTitle("Message")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                    builder.show();
                }
                clearFields();
            } else {
                updateSpecial(itemhsn, item, itemrate, itemratewhole, itemrateretail, itemgst, itemqty, itemtype);
            }
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Incomplete Fields")
                    .setTitle("Error")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
    }

    private void updateSpecial(String itemhsn, String item, String itemrate, String itemratewhole, String itemrateretail, String itemgst, String itemqty, String itemtype) {
        boolean res;
        res = itemdb.updateSpecialItem(updatehsn, itemhsn, item, itemrate, itemratewhole, itemrateretail, itemgst, itemqty, itemtype);
        if (res) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Item Updated Successfully")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error Updating Item")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
            builder.show();
        }
        clearFields();

    }

    private  String[] special_items() {

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
        return  items;
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
            int rateWholeCol = cursor.getColumnIndex("ratewhole");
            int rateRetailCol = cursor.getColumnIndex("rateretail");
            int typeCol = cursor.getColumnIndex("type");
            int gstCol = cursor.getColumnIndex("gst");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String shsn = cursor.getString(hsnCol);
                String srate = cursor.getString(rateCol);
                String swrate = cursor.getString(rateWholeCol);
                String srrate = cursor.getString(rateRetailCol);
                String stype = cursor.getString(typeCol);
                String sgst = cursor.getString(gstCol);
                rate.setText(srate);
                ratewhole.setText(swrate);
                rateretail.setText(srrate);

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                ArrayAdapter myAdapGST = (ArrayAdapter) spinnerGST.getAdapter();
                spinnerPosition = myAdapGST.getPosition(sgst);
                spinnerGST.setSelection(spinnerPosition);

                hsn.setText(shsn);
                updatehsn = shsn;
                isUpdating = true;
            }
        }
    };

    private final TextWatcher hsnWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            String itemhsn = hsn.getText().toString();
            Cursor cursor = itemdb.getSpecialItemDetailsWithHSN(itemhsn);
            int nameCol = cursor.getColumnIndex("name");
            int rateCol = cursor.getColumnIndex("rate");
            int rateWholeCol = cursor.getColumnIndex("ratewhole");
            int rateRetailCol = cursor.getColumnIndex("rateretail");
            int typeCol = cursor.getColumnIndex("type");
            int gstCol = cursor.getColumnIndex("gst");

            cursor.moveToFirst();

            if(cursor != null && (cursor.getCount()> 0)){
                String sname = cursor.getString(nameCol);
                String srate = cursor.getString(rateCol);
                String swrate = cursor.getString(rateWholeCol);
                String srrate = cursor.getString(rateRetailCol);
                String stype = cursor.getString(typeCol);
                String sgst = cursor.getString(gstCol);
                rate.setText(srate);
                ratewhole.setText(swrate);
                rateretail.setText(srrate);

                ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
                int spinnerPosition = myAdap.getPosition(stype);
                spinner.setSelection(spinnerPosition);
                ArrayAdapter myAdapGST = (ArrayAdapter) spinnerGST.getAdapter();
                spinnerPosition = myAdapGST.getPosition(sgst);
                spinnerGST.setSelection(spinnerPosition);

                name.setText(sname);
                updatehsn = itemhsn;
                isUpdating = true;

            }
            else{
                isUpdating = false;
            }
        }
    };

    private void clearFields() {
        isUpdating = false;
        hsn.setText("");
        name.setText("");
        updatename = "";
        updatehsn = "";
        rate.setText("0.00");
        qty.setText("0");
    }
    public static String capitalizeWord(String str){
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    public void removeSpecialItemDB(View view) {
        final String ids = hsn.getText().toString();
        if(!ids.equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Confirm Deletion")
                    .setTitle("Message")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            itemdb.deleteSpecialItem(ids);
                            Toast.makeText(ItemAddSpecialActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                            clearFields();
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }
        else {
            Toast.makeText(this, "No Item", Toast.LENGTH_SHORT).show();
        }
    }
}
