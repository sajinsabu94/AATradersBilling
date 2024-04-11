package com.xeta.app.aatraders;

import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class StockActivity extends AppCompatActivity {
    TableView tableStock;
    AutoCompleteTextView tableStockText;
    ItemDB itemDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stock);
        tableStock = (TableView) findViewById(R.id.tableStock);
        tableStockText = (AutoCompleteTextView) findViewById(R.id.tableStockText);
        itemDB = new ItemDB(this);

        String[] TABLE_HEADERS = { "Sl.No.", "Name", "HSN Code", "Qty"};
        tableStock.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        tableStock.setDataAdapter(new SimpleTableDataAdapter(this, getAllStock()));

        ArrayAdapter<String> itemadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, special_items());
        tableStockText.setAdapter(itemadapter);
        tableStockText.addTextChangedListener(nameWatcher);
    }

    private  String[] special_items () {
        int i=0;
        Cursor cursor = itemDB.getSpecialItemName();
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

    public List<String[]> getAllStock(){
        List<String[]> itemList = new ArrayList<>();
        Cursor items = itemDB.getStockInfo();
        int hsnCol = items.getColumnIndex("hsn");
        int nameCol = items.getColumnIndex("name");
        int qtyCol = items.getColumnIndex("qty");
        items.moveToFirst();
        if(items!=null && items.getCount()>0){
            int ct = 0;
            do{
                ct++;
                String tempStr[] = new String[4];
                tempStr[0] = Integer.toString(ct);
                tempStr[1] = items.getString(hsnCol);
                tempStr[2] = items.getString(nameCol);
                tempStr[3] = items.getString(qtyCol);
                itemList.add(tempStr);
            }while (items.moveToNext());
        }
        return itemList;
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

            String itemname = tableStockText.getText().toString();

            List<String[]> itemList = new ArrayList<>();
            Cursor items = itemDB.getItemStockInfo(itemname);
            int hsnCol = items.getColumnIndex("hsn");
            int nameCol = items.getColumnIndex("name");
            int qtyCol = items.getColumnIndex("qty");
            items.moveToFirst();

            Log.e("StockError",Integer.toString(items.getCount()));

            if(items!=null && items.getCount()>0){
                int ct = 0;
                do{
                    Log.e("StockError",Integer.toString(items.getColumnCount()));
                    ct++;
                    String tempStr[] = new String[4];
                    tempStr[0] = Integer.toString(ct);
                    tempStr[1] = items.getString(hsnCol);
                    tempStr[2] = items.getString(nameCol);
                    tempStr[3] = items.getString(qtyCol);
                    itemList.add(tempStr);
                }while (items.moveToNext());
            }

            Log.e("StockError","no work");

         //   tableStock.removeAllViews();
         //   String[] TABLE_HEADERS = { "Sl.No.", "Name", "HSN Code", "Qty"};
         //   tableStock.setHeaderAdapter(new SimpleTableHeaderAdapter(StockActivity.this, TABLE_HEADERS));
            tableStock.setDataAdapter(new SimpleTableDataAdapter(StockActivity.this, itemList));
        }
    };
}
