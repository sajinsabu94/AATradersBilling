package com.xeta.app.aatraders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class CartItemDataAdapter extends TableDataAdapter<CartItem> {

    public CartItemDataAdapter(Context context, ArrayList<CartItem> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        CartItem cartItem = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderIndex(rowIndex+1);
                break;
            case 1:
                renderedView = renderTextView(cartItem.getName());
                break;
            case 2:
                renderedView = renderTextView(cartItem.getHsn());
                break;
            case 3:
                renderedView = renderTextView(cartItem.getGst());
                break;
            case 4:
                renderedView = renderTextView(cartItem.getRate());
                break;
            case 5:
                renderedView = renderTextView(cartItem.getQty()+cartItem.getType());
                break;
            case 6:
                renderedView = renderTextView(cartItem.getDis());
                break;
            case 7:
                renderedView = renderTextView(cartItem.getGtotal());
                break;
            case 8:
                renderedView = renderTextView(cartItem.getCgst());
                break;
            case 9:
                renderedView = renderTextView(cartItem.getSgst());
                break;
            case 10:
                renderedView = renderTextView(cartItem.getTotal());
                break;
        }

        return renderedView;
    }

    private View renderTextView(String name) {
        TextView temp = new TextView(getContext());
        temp.setText(name);
        return temp;
    }

    private View renderIndex(int i) {
        TextView temp = new TextView(getContext());
        temp.setText(Integer.toString(i));
        return temp;
    }
}
