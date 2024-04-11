package com.xeta.app.aatraders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class BillingBean implements Serializable {

    private String invoiceNumber;
    private String customerName;
    private String customerContact;
    private String customerAddress;
    private String count;
    private String grossTotal;
    private String cgstTotal;
    private String sgstTotal;
    private String totalPrice;
    private ArrayList<CartItem> products;

    public BillingBean() {
        products = new ArrayList<CartItem>();
    }


    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(String grossTotal) {
        this.grossTotal = grossTotal;
    }

    public String getCgstTotal() {
        return cgstTotal;
    }

    public void setCgstTotal(String cgstTotal) {
        this.cgstTotal = cgstTotal;
    }

    public String getSgstTotal() {
        return sgstTotal;
    }

    public void setSgstTotal(String sgstTotal) {
        this.sgstTotal = sgstTotal;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<CartItem> getProducts() {
        return products;
    }
    public void setProducts(CartItem products) {
        this.products.add(products);
    }
    public void removeProducts(CartItem products) {
        this.products.remove(products);
    }

    @Override
    public String toString() {
        return "BillingBean{" +
                "invoiceNumber='" + invoiceNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerContact='" + customerContact + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", count='" + count + '\'' +
                ", grossTotal='" + grossTotal + '\'' +
                ", cgstTotal='" + cgstTotal + '\'' +
                ", sgstTotal='" + sgstTotal + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", products=" + products +
                '}';
    }
}
