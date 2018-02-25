package com.smartpay.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


import com.smartpay.android.R;
import com.smartpay.android.adapter.ScannedItemsListAdapter;
import com.smartpay.android.model.Item;
import com.smartpay.android.util.Constants;


public class ItemDetailsUpdateDialog extends Dialog {

    private Spinner spinner;

    private ArrayList<Item> items;

    private int position;

    private ScannedItemsListAdapter scannedItemsListAdapter;


    public ItemDetailsUpdateDialog(@NonNull Context context, ArrayList<Item> items, int position, ScannedItemsListAdapter scannedItemsListAdapter) {
        super(context, android.R.style.Theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_item_details);

        this.items = items;
        this.position = position;
        this.scannedItemsListAdapter = scannedItemsListAdapter;
        //this.barcodeView = barcodeView;

        ((TextView) findViewById(R.id.scannedItemNameTextView)).setText(items.get(position).getItemName())
        ;
        ((TextView) findViewById(R.id.scannedItemPrice)).setText(items.get(position).getPrice() + "");


        spinner = (Spinner) findViewById(R.id.qtySpinner);
        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("Select Quantity");
        spinnerArray.add("1");
        spinnerArray.add("2");
        spinnerArray.add("3");
        spinnerArray.add("4");
        spinnerArray.add("5");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(items.get(position).getQty());

        findViewById(R.id.addItemToCartFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        findViewById(R.id.deleteItemFromCartFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        setCancelable(false);

    }

    private void deleteItem() {
        items.remove(position);
        scannedItemsListAdapter.notifyItemRemoved(position);
        getContext().sendBroadcast(new Intent(Constants.DELETE_INTENT));
        dismiss();
    }

    private void addItem() {
        int qty = spinner.getSelectedItemPosition();
        if (qty == 0) qty = 1;
        items.get(position).setQty(qty);
        scannedItemsListAdapter.notifyItemChanged(position);
        dismiss();
    }

}
