package com.smartpay.android.shopping.util.name_generator;


import android.content.Context;

public class RandomNameGenerator {
    private int pos;
    private Context context;

    private RandomNameGenerator(Context context, int seed) {
        this.pos = seed;
        this.context = context;
    }

    public RandomNameGenerator(Context context) {
        this(context, (int) System.currentTimeMillis());
    }
    public synchronized String next() {
        Dictionary d = Dictionary.getInstance(context);
        pos = Math.abs(pos+d.getPrime()) % d.size();
        return d.word(pos);
    }
}
