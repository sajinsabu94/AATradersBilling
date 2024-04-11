package com.xeta.app.aatraders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;


public class PopUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.50f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        TextView SubmitAdditional = (TextView) findViewById(R.id.SubmitAdditional);
        final EditText product = (EditText) findViewById(R.id.adittionalTextEdit);
        final EditText qty = (EditText) findViewById(R.id.additionalQuantityTE);
        final EditText price = (EditText) findViewById(R.id.adittionalPriceTE);
        final Intent intent = getIntent();
        final Context context = this;
        SubmitAdditional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String additionalProduct = product.getText().toString();
                String additionalQuantity = qty.getText().toString();
                String additionalPrice = price.getText().toString();
                if (additionalProduct != null && additionalPrice.isEmpty() == false && additionalQuantity.isEmpty() == false) {
                    intent.putExtra("addr", additionalQuantity);
                    intent.putExtra("ph", additionalPrice);
                    intent.putExtra("name", additionalProduct);
                    setResult(RESULT_OK, intent);
                    finish();

                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Missing Details");
                    alert.setMessage("Some of the required fields are missing. please try again");
                    alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                }

            }
        });
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
    }

    public void doNothing(View view) {
        finish();
    }
}