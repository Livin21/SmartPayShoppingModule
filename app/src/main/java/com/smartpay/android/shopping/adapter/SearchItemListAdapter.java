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
import java.util.HashMap;


import com.smartpay.android.R;
import com.smartpay.android.shopping.model.Item;


public class SearchItemListAdapter extends RecyclerView.Adapter<SearchItemListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> items;
    private HashMap<String,String> shops;

    public SearchItemListAdapter(Context context, ArrayList<Item> items, HashMap<String,String> shops){
        this.context = context;
        this.items = items;
        this.shops = shops;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = android.view.LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_item_list_item,parent,false);

        return new SearchItemListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText("Price: ₹ " + item.getPrice() + "/-");
        holder.shopName.setText(shops.get(item.getShopID()));
        holder.offer.setImageDrawable(item.hasOffer() ?
                ContextCompat.getDrawable(context,R.drawable.offer_tag)
                : new ColorDrawable(ContextCompat.getColor(context,R.color.colorTransparent)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,price,shopName;
        ImageView offer;
        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name_tv);
            price = itemView.findViewById(R.id.item_price_tv);
            shopName = itemView.findViewById(R.id.item_shop_tv);
            offer = itemView.findViewById(R.id.offer_tag_icon);
        }
    }
}
