package com.smartpay.android.shopping.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.ArrayList;
import java.util.List;


import com.smartpay.android.R;
import com.smartpay.android.shopping.adapter.ScannedItemsListAdapter;
import com.smartpay.android.shopping.model.Item;


public class ItemDetailsDialog extends Dialog {

    private Spinner spinner;

    private ArrayList<Item> items;

    private int position;

    private ScannedItemsListAdapter scannedItemsListAdapter;

    private CompoundBarcodeView barcodeView;



    public ItemDetailsDialog(@NonNull Context context, ArrayList<Item> items, int position, ScannedItemsListAdapter scannedItemsListAdapter, CompoundBarcodeView barcodeView) {
        super(context, android.R.style.Theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop_item_details);

        this.items = items;
        this.position = position;
        this.scannedItemsListAdapter = scannedItemsListAdapter;
        this.barcodeView = barcodeView;

        ((TextView) findViewById(R.id.scannedItemNameTextView)).setText(items.get(position).getItemName())
        ;
        ((TextView) findViewById(R.id.scannedItemPrice)).setText(items.get(position).getPrice() + "");


        spinner = findViewById(R.id.qtySpinner);
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
        dismiss();
        barcodeView.resume();
    }

    private void addItem() {
        int qty = spinner.getSelectedItemPosition();
        if (qty == 0) qty = 1;
        items.get(position).setQty(qty);
        scannedItemsListAdapter.notifyItemInserted(position);
        barcodeView.resume();
        dismiss();
    }

}
