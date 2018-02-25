package com.smartpay.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import com.smartpay.android.R;
import com.smartpay.android.adapter.BillHistoryItemAdapter;
import com.smartpay.android.model.BillItem;

public class BillHistoryDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_history_details);

        ArrayList<BillItem> items = (ArrayList<BillItem>) getIntent().getSerializableExtra("ITEMS");

        RecyclerView recyclerView = findViewById(R.id.billItemsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        BillHistoryItemAdapter adapter = new BillHistoryItemAdapter(items, this);
        recyclerView.setAdapter(adapter);

    }
}
