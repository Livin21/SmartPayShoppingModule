package com.smartpay.android.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


import com.smartpay.android.R;
import com.smartpay.android.activity.BillHistoryDetails;
import com.smartpay.android.model.BillHistory;

public class BillHistoryAdapter extends RecyclerView.Adapter<BillHistoryAdapter.ViewHolder>{

    private Context context;
    private ArrayList<BillHistory> billItems;

    public BillHistoryAdapter(Context context, ArrayList<BillHistory> billItems){
        this.billItems = billItems;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = android.view.LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.bill_history_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.shopNameTV.setText(billItems.get(position).getShopName());
        holder.dateTV.setText(billItems.get(position).getDate());
        holder.amountTV.setText(String.valueOf(billItems.get(position).getAmount()));
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BillHistoryDetails.class);
                intent.putExtra("ITEMS",billItems.get(holder.getAdapterPosition()).getItems());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return billItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView shopNameTV, dateTV, amountTV;
        View root;
        ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            shopNameTV = (TextView) itemView.findViewById(R.id.shopNameTV);
            dateTV = (TextView) itemView.findViewById(R.id.billDateTV);
            amountTV = (TextView) itemView.findViewById(R.id.billAmountTV);
        }
    }
}
