package com.smartpay.android.shopping.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


import com.smartpay.android.R;
import com.smartpay.android.shopping.model.BillItem;

public class BillHistoryItemAdapter extends RecyclerView.Adapter<BillHistoryItemAdapter.ViewHolder> {

    private ArrayList<BillItem> billItems;
    private Context context;

    public BillHistoryItemAdapter(ArrayList<BillItem> billItems, Context context) {
        this.billItems = billItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = android.view.LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.history_bill_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemName.setText(billItems.get(position).getItemName());
        holder.sno.setText(String.valueOf(position+1) + ". ");
        holder.price.setText("MRP: ₹ " +
                String.valueOf(billItems.get(position).getPrice())
                + "/-"
        );
        holder.qty.setText(
                "Quantity: " +
                String.valueOf(billItems.get(position).getQty())
                + " pc"
        );
        holder.amount.setText("₹ " +
                String.valueOf(billItems.get(position).getQty() * billItems.get(position).getPrice())
                + "/-"
        );
    }

    @Override
    public int getItemCount() {
        return billItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,sno,price,amount,qty;
        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name_tv);
            sno = itemView.findViewById(R.id.sl_no_tv);
            price = itemView.findViewById(R.id.item_price_tv);
            amount = itemView.findViewById(R.id.amount_tv);
            qty = itemView.findViewById(R.id.item_qty_tv);
        }
    }
}
