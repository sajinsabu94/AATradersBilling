package com.xeta.app.aatraders;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by Anonymous on 04/24/18.
 */

public class ProceedBillActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    ArrayList<CartItem> products;
    //String[] items;
    int n;
    TableView<CartItem> tableView;
    TableLayout tl;
    TextView gt;
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

    BillingBean billDetails;

    int billno;
    Button btnPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_cart);
        shopdb = new ShopDB(this);
        shopdb.getReadableDatabase();

        stringItem = null;
        qtyItem = null;
        billNoInfo = findViewById(R.id.billInfoNo);


        //tl = findViewById(R.id.myTableBillLayout);
        gt = findViewById(R.id.GrandTotal);
        gtp = findViewById(R.id.GrandTotalPayable);
        Intent intent = getIntent();

        tableView = (TableView<CartItem>) findViewById(R.id.ProceedBillTable);
        //tableView.setColumnCount(11);
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
        //

        String[] TABLE_HEADERS = { "Sl.No.", "Name", "HSN Code", "GST%", "Unit price", "Quantity",  "Discount", "Gross Total", "CGST", "SGST", "Total"};
        products = new ArrayList<CartItem>();
        billDetails = (BillingBean)intent.getSerializableExtra("values");
        products = billDetails.getProducts();
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, TABLE_HEADERS));
        tableView.setDataAdapter(new CartItemDataAdapter(this, products));

        //items = intent.getStringArrayExtra("values");
        total = intent.getStringExtra("billamnt");
        totalpayable = intent.getStringExtra("billamntpay");
        gt.setText(total);
        gtp.setText(totalpayable);
        n = intent.getIntExtra("count", 0);
        generateList();

        billno = shopdb.getMaxBillId()+1;
        billDetails.setInvoiceNumber(Integer.toString(shopdb.getMaxBillId()+1));


        billNoInfo.setText("Bill No: "+Integer.toString(billno));

        btnPrint = (Button) this.findViewById(R.id.ButtonCheckOut);
        btnPrint.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        Intent second = new Intent(this, PopUp.class);
        startActivityForResult(second, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String nm = data.getStringExtra("name");
                String ad = data.getStringExtra("addr");
                String ph = data.getStringExtra("ph");
                if(nm!=null && ad!=null && ph!=null){
                    billDetails.setCustomerName(nm);
                    billDetails.setCustomerAddress(ad);
                    billDetails.setCustomerContact(ph);
                    printBill();
                    finalBill();
                }

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void generateList() {

        int i = 1;
        for(CartItem item : products){
            Log.e("products",item.toString());
            System.out.println(item);
            TableRow product = new TableRow(this);
            TextView sl = new TextView(this);
            sl.setText(Integer.toString(i));
            product.addView(sl);
            TextView name = new TextView(this);
            name.setText(item.getName());
            product.addView(name);
            TextView hsn = new TextView(this);
            hsn.setText(item.getHsn());
            product.addView(hsn);
            TextView gst = new TextView(this);
            gst.setText(item.getGst());
            product.addView(gst);
            TextView unit = new TextView(this);
            unit.setText(item.getType());
            product.addView(unit);
            TextView qty = new TextView(this);
            qty.setText(item.getQty()+item.getType());
            product.addView(qty);
            TextView dis = new TextView(this);
            dis.setText(item.getDis());
            product.addView(dis);
            TextView gross = new TextView(this);
            gross.setText(item.getGtotal());
            product.addView(gross);
            TextView cgst = new TextView(this);
            cgst.setText(item.getCgst());
            product.addView(cgst);
            TextView sgst = new TextView(this);
            sgst.setText(item.getSgst());
            product.addView(sgst);
            TextView tot = new TextView(this);
            tot.setText(item.getTotal());
            product.addView(tot);

          //  tl.addView(product);
            i++;
        }

    }

    public void finalBill() {


        String dated = getDateTime() + "\n\n\n\n\n\n";
        boolean res = false;
        Log.e("Products added : ",products.toString());
            for(CartItem eachItem : products){
                res = shopdb.addTransactionToDB(billDetails.getInvoiceNumber(), eachItem.getName(), eachItem.getHsn(), eachItem.getGst(), eachItem.getRate(), eachItem.getQty(), getDateTime(), eachItem.getTotal());
                Log.e("res : ", String.valueOf(res));
                shopdb.removeItemFromStock(eachItem.getHsn(),eachItem.getQty());
                if(shopdb.isDailyExist(eachItem.hsn,getDateTime())){
                    shopdb.insertDaily(eachItem.hsn,eachItem.name,eachItem.qty,getDateTime());
                }
                else {
                    shopdb.updateDaily(eachItem.hsn,eachItem.qty);
                }
            }
        //boolean res = shopdb.addTransactionToDB(String.valueOf(billno), stringItem, stringHSN, stringGST, stringRate, stringQty, getDateTime(), total);

        String pdfFilename = Integer.toString(billno)+".pdf";
        String filepath = "MyFileStorage";
        File myExternalFile;
        myExternalFile = new File(getExternalFilesDir(filepath), pdfFilename);
        Uri uri = Uri.fromFile(myExternalFile);
        if(Build.VERSION.SDK_INT>=24){
            try{
                 uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",myExternalFile);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(uri,"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
        if (res) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Transaction Successful")
                    .setTitle("Message")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent2 = new Intent(ProceedBillActivity.this, MainActivity.class);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent2);
                            ProceedBillActivity.this.finish();

                        }
                    });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Errors occured in transaction")
                    .setTitle("Error")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.show();
        }


    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    protected void printBill() {
        String pdfFilename = Integer.toString(billno)+".pdf";
        String filepath = "MyFileStorage";
        File myExternalFile;

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
        }
        else {
            myExternalFile = new File(getExternalFilesDir(filepath), pdfFilename);
            try {

                OutputStream file = new FileOutputStream(myExternalFile);
                Document document = new Document();
                PdfWriter.getInstance(document, file);

                PdfPTable title = new PdfPTable(1);
                title.setWidthPercentage(100);
                title.addCell(getTitleCell("AA Traders, Mayyanad",PdfPCell.ALIGN_CENTER, 18));
                title.addCell(getTitleCell1("Panchayath Building Complex, Mayyanad",PdfPCell.ALIGN_CENTER, 15));
                title.addCell(getTitleCell1("Mayyanad PO, Kollam -691303",PdfPCell.ALIGN_CENTER, 14));
                title.addCell(getTitleCell2("GSTIN/UIN: 32AIUPA4607K1Z9",PdfPCell.ALIGN_CENTER, 13));
                title.addCell(getTitleCell2("State Name : Kerala, Code : 32",PdfPCell.ALIGN_CENTER, 12));
                title.addCell(getTitleCell2("Contact : 7994325511",PdfPCell.ALIGN_CENTER, 12));
                title.addCell(getTitleCell3("TAX INVOICE",PdfPCell.ALIGN_CENTER, 14));

                PdfPTable fullHead = new PdfPTable(3);
                fullHead.setWidthPercentage(100);


                PdfPTable address = new PdfPTable(1);
                address.setWidthPercentage(100);
                address.addCell(getBillHeadCell("Bill To :")).setPadding(3.0f);
                address.addCell(getBillHeadCell("   "+billDetails.getCustomerName().toUpperCase())).setPadding(3.0f);
                address.addCell(getBillHeadCell("   "+billDetails.getCustomerAddress().toUpperCase())).setPadding(3.0f);
                address.addCell(getBillHeadCell("   "+billDetails.getCustomerContact())).setPadding(3.0f);
                PdfPCell leftHead = new PdfPCell (address);
                leftHead.setPadding(3.0f);
                leftHead.setBorder(PdfPCell.NO_BORDER);
                leftHead.setColspan(2);
                fullHead.addCell(leftHead);

                PdfPTable billInfo = new PdfPTable(1);
                billInfo.setWidthPercentage(100);
                billInfo.addCell(getBillHeadCell("Bill No : "+Integer.toString(billno))).setPadding(3.0f);
                billInfo.addCell(getBillHeadCell("Date    : "+getDateTime())).setPadding(3.0f);
                PdfPCell rightHead = new PdfPCell (billInfo);
                rightHead.setPadding(3.0f);
                rightHead.setBorder(PdfPCell.NO_BORDER);
                fullHead.addCell(rightHead);


                PdfPTable billTable = new PdfPTable(11); //one page contains 15 records
                billTable.setWidthPercentage(100);
                billTable.setWidths(new float[] { 1,5,3,2,3,3,2,2,2,2,3 });
                billTable.setSpacingBefore(30.0f);
                billTable.addCell(getBillHeaderCell("Sl.No."));
                billTable.addCell(getBillHeaderCell("Product"));
                billTable.addCell(getBillHeaderCell("HSN Code"));
                billTable.addCell(getBillHeaderCell("GST%"));
                billTable.addCell(getBillHeaderCell("Unit Price"));
                billTable.addCell(getBillHeaderCell("Quantity"));
                billTable.addCell(getBillHeaderCell("DIS%"));
                billTable.addCell(getBillHeaderCell("Gross Value"));
                billTable.addCell(getBillHeaderCell("CGST Amount"));
                billTable.addCell(getBillHeaderCell("SGST Amount"));
                billTable.addCell(getBillHeaderCell("Total"));

                int qtyt = 0;
                float gt = 0.0f;
                float cgt = 0.0f;
                float sgt = 0.0f;
                float tt = 0.0f;
                int i = 1;
                for(CartItem t : products){
                    billTable.addCell(getBillRowCell(Integer.toString(i)));
                    billTable.addCell(getBillRowCell(t.getName()));
                    billTable.addCell(getBillRowCell(t.getHsn()));
                    billTable.addCell(getBillRowCell(t.getGst()));
                    billTable.addCell(getBillRowCell(t.getRate()));
                    billTable.addCell(getBillRowCell(t.getQty()+t.getType()));
                    billTable.addCell(getBillRowCell(t.getDis()));
                    billTable.addCell(getBillRowCell(t.getGtotal()));
                    billTable.addCell(getBillRowCell(t.getCgst()));
                    billTable.addCell(getBillRowCell(t.getSgst()));
                    billTable.addCell(getBillRowCell(t.getTotal()));
                    qtyt+=Integer.parseInt(t.getQty());
                    gt+=Float.parseFloat(t.getGtotal());
                    cgt+=Float.parseFloat(t.getCgst());
                    sgt+=Float.parseFloat(t.getSgst());
                    tt+=Float.parseFloat(t.getTotal());
                    i++;
                }
                while(i<=30){
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    billTable.addCell(getBillRowCell(" "));
                    i++;
                }
                billTable.completeRow();

                PdfPTable resultTable = new PdfPTable(11);
                resultTable.setWidthPercentage(100);
                resultTable.setWidths(new float[] { 1,5,3,2,3,3,2,2,2,2,3 });


                PdfPTable summaryTotal = new PdfPTable(11);
                summaryTotal.setWidthPercentage(100);
                summaryTotal.setWidths(new float[] { 1,5,3,2,3,3,2,2,2,2,3 });
                summaryTotal.addCell(getBillFooterCell(" "));
                summaryTotal.addCell(getBillFooterCell("Total"));
                summaryTotal.addCell(getBillFooterCell(" "));
                summaryTotal.addCell(getBillFooterCell(" "));
                summaryTotal.addCell(getBillFooterCell(" "));
                summaryTotal.addCell(getBillFooterCell(Integer.toString(qtyt)+" nos"));
                summaryTotal.addCell(getBillFooterCell(" "));
                summaryTotal.addCell(getBillFooterCell(String.format("%.2f",gt)));
                summaryTotal.addCell(getBillFooterCell(String.format("%.2f",cgt)));
                summaryTotal.addCell(getBillFooterCell(String.format("%.2f",sgt)));
                summaryTotal.addCell(getBillFooterCell(String.format("%.2f",tt)));
                PdfPCell cellTotal = new PdfPCell(summaryTotal);
                cellTotal.setColspan(11);
                cellTotal.setBorder(1);
                resultTable.addCell(cellTotal);

                PdfPTable validity = new PdfPTable(1);
                validity.setWidthPercentage(100);
                validity.addCell(getValidityCell(" "));
                validity.addCell(getValidityCell("INR "+convert((int)Math.round(tt)).toUpperCase()+" only"));
                PdfPCell summaryL = new PdfPCell (validity);
                summaryL.setColspan (4);
                summaryL.setPadding (1.0f);
                //billTable.addCell(summaryL);
                resultTable.addCell(summaryL);

                PdfPTable accounts = new PdfPTable(2);
                accounts.setWidthPercentage(100);
                accounts.addCell(getAccountsCell("Subtotal"));
                accounts.addCell(getAccountsCellR(String.format("%.2f",gt)));
                accounts.addCell(getAccountsCell("Tax"));
                accounts.addCell(getAccountsCellR(String.format("%.2f",cgt+sgt)));
                accounts.addCell(getAccountsCell("Amount Payable"));
                accounts.addCell(getAccountsCellR(String.format("%.2f",(float)Math.round(tt))));
                PdfPCell summaryR = new PdfPCell (accounts);
                summaryR.setColspan (7);

                resultTable.addCell(summaryR);

                PdfPTable describer = new PdfPTable(1);
                describer.setWidthPercentage(100);
                describer.addCell(getdescCell(" "));
                describer.addCell(getIRHCell("Authorised Signature",PdfPCell.ALIGN_RIGHT));

                document.open();//PDF document opened........

                document.add(title);
/*                document.add(irhTable);
                document.add(bill);
                document.add(name);
                document.add(contact);
                */
                document.add(fullHead);
//                document.add(address);
                document.add(billTable);
                document.add(resultTable);
                document.add(describer);


                document.close();

                file.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }





    }


    public static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getTitleCell(String text, int alignment, int size) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, size);
        /*	font.setColor(BaseColor.GRAY);*/
        font.setStyle(Font.BOLD);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getTitleCell1(String text, int alignment, int size) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, size);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getTitleCell2(String text, int alignment, int size) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, size);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getTitleCell3(String text, int alignment, int size) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, size);
        /*	font.setColor(BaseColor.GRAY);*/
        font.setStyle(Font.BOLD);
        font.setStyle(Font.UNDERLINE);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(1);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    public static PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.BLACK);
        font.setStyle(Font.BOLD);
        font.setSize(9.0f);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (2.0f);
        return cell;
    }

    public static PdfPCell getBillRowCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.BLACK);
        font.setSize(9.0f);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_LEFT);
        //cell.setPadding (2.0f);
        cell.setPaddingLeft(3.0f);
        cell.setPaddingRight(3.0f);
        cell.setPaddingTop(2.0f);
        cell.setPaddingBottom(2.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getBillFooterCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.BLACK);
        font.setSize(9.0f);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (3.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorder(0);
        return cell;
    }

    public static PdfPCell getBillHeadCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.BLACK);
        font.setStyle(Font.BOLD);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorder(0);
        return cell;
    }

    public static PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding (5.0f);
        return cell;
    }
    public static PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setStyle(Font.BOLD);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
        cell.setPadding (5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    public static PdfPCell getdescCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBorder(0);
        return cell;
    }


    public void run() {

    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }



    private String leftRightAlign(String str1, String str2) {
        String ans = str1+str2;
        if(ans.length() <64){
            int n = (64 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }

    public String convert(int number) {

        if (number == 0) { return "zero"; }

        String prefix = "";

        if (number < 0) {
            number = -number;
            prefix = "negative";
        }

        String current = "";
        int place = 0;

        do {
            int n = number % 1000;
            if (n != 0){
                String s = convertLessThanOneThousand(n);
                current = s + specialNames[place] + current;
            }
            place++;
            number /= 1000;
        } while (number > 0);

        return (prefix + current).trim();
    }

    private static final String[] specialNames = {
            "",
            " thousand",
            " million",
            " billion",
            " trillion",
            " quadrillion",
            " quintillion"
    };

    private static final String[] tensNames = {
            "",
            " ten",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] numNames = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine",
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };

    private String convertLessThanOneThousand(int number) {
        String current;

        if (number % 100 < 20){
            current = numNames[number % 100];
            number /= 100;
        }
        else {
            current = numNames[number % 10];
            number /= 10;

            current = tensNames[number % 10] + current;
            number /= 10;
        }
        if (number == 0) return current;
        return numNames[number] + " hundred" + current;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
