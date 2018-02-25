package com.smartpay.android.shopping.adapter;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.smartpay.android.R;
import com.smartpay.android.shopping.dialog.ItemDetailsUpdateDialog;
import com.smartpay.android.shopping.model.Item;

public class ScannedItemsListAdapter extends RecyclerView.Adapter<ScannedItemsListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Item> items;

    public ScannedItemsListAdapter(Context context, ArrayList<Item> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = android.view.LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.scanned_list_item,parent,false);

        return new ScannedItemsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("MRP: ₹ " + item.getPrice() + "/-");
        holder.sno.setText(String.format("%d.", position + 1));
        holder.qty.setText("Quantity: " + item.getQty() + " pc");
        holder.amount.setText("₹ " + (item.getPrice() * item.getQty()) + "/-");
        holder.offer.setImageDrawable(item.hasOffer() ?
                ContextCompat.getDrawable(context,R.drawable.offer_tag)
        : new ColorDrawable(ContextCompat.getColor(context,R.color.colorTransparent)));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemDetailsUpdateDialog(context,items,holder.getAdapterPosition(),ScannedItemsListAdapter.this).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,sno,price,amount,qty;
        ImageView offer;
        int position;
        View rootView;
        ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            itemName = itemView.findViewById(R.id.item_name_tv);
            sno = itemView.findViewById(R.id.sl_no_tv);
            price = itemView.findViewById(R.id.item_price_tv);
            amount = itemView.findViewById(R.id.amount_tv);
            qty = itemView.findViewById(R.id.item_qty_tv);
            offer = itemView.findViewById(R.id.offer_tag_icon);
            /*position = Integer.parseInt(sno.getText().charAt(0)+"");
            position--;
            itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemDetailsUpdateDialog(context,items,position,ScannedItemsListAdapter.this).show();
                }
            });*/
        }
    }
}
