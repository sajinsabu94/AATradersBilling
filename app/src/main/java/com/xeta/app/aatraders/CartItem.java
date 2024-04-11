package com.xeta.app.aatraders;

import java.io.Serializable;

public class CartItem implements Serializable {

    String name;
    String hsn;
    String gst;
    String rate;
    String qty;
    String dis;
    String gtotal;
    String cgst;
    String sgst;
    String total;
    String type;

    public String getHsn() {
        return hsn;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGtotal() {
        return gtotal;
    }

    public void setGtotal(String gtotal) {
        this.gtotal = gtotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }

    public CartItem(String name, String hsn, String gst, String rate, String qty, String dis, String gtotal, String cgst, String sgst, String total, String type) {
        this.name = name;
        this.hsn = hsn;
        this.gst = gst;
        this.rate = rate;
        this.qty = qty;
        this.dis = dis;
        this.gtotal = gtotal;
        this.cgst = cgst;
        this.sgst = sgst;
        this.total = total;
        this.type = type;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "hsn='" + hsn + '\'' +
                ", name='" + name + '\'' +
                ", rate='" + rate + '\'' +
                ", qty='" + qty + '\'' +
                ", dis='" + dis + '\'' +
                ", gst='" + gst + '\'' +
                ", cgst='" + cgst + '\'' +
                ", sgst='" + sgst + '\'' +
                ", type='" + type + '\'' +
                ", gtotal='" + gtotal + '\'' +
                ", total='" + total + '\'' +
                '}';
    }

    public void calculateGTotal(){
        float gt = Float.parseFloat(this.rate)*Integer.parseInt(this.qty);
        this.gtotal = Float.toString(gt);
    }

    public void calculateCSGST(){
        float gst = (Float.parseFloat(this.gtotal))*(Integer.parseInt(this.gst.replaceAll("[\\D]", ""))/100);
        this.cgst = Float.toString(gst);
        this.sgst = Float.toString(gst);
    }

    public void calculateTotal(){
        float gt = Float.parseFloat(this.gtotal)+(Float.parseFloat(this.cgst)*2);
        this.total = Float.toString(gt);
    }

    public CartItem() {
    }
}
