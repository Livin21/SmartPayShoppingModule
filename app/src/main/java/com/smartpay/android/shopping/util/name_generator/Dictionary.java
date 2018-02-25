package com.smartpay.android.shopping.util.name_generator;

import android.content.Context;

import com.smartpay.android.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


class Dictionary {
    private List<String> nouns;
    private List<String> adjectives;

    private final int prime;

    private static Dictionary INSTANCE;

    private Dictionary(Context context) {
        nouns = new ArrayList<>();
        adjectives = new ArrayList<>();
        try {
            load(context, "a.txt", adjectives);
            load(context, "n.txt", nouns);
        } catch (IOException e) {
            throw new Error(e);
        }

        int combo = size();

        int primeCombo = 2;
        while (primeCombo<=combo) {
            int nextPrime = primeCombo+1;
            primeCombo *= nextPrime;
        }
        prime = primeCombo+1;
    }

    /**
     * Total size of the combined words.
     */
    public int size() {
        return nouns.size()*adjectives.size();
    }

    /**
     * Sufficiently big prime that's bigger than {@link #size()}
     */
    int getPrime() {
        return prime;
    }

    String word(int i) {
        int a = i%adjectives.size();
        int n = i/adjectives.size();

        return adjectives.get(a)+"_"+nouns.get(n);
    }

    private void load(Context context, String name, List<String> col) throws IOException {

        int fileId = 0;

        if (name.equals("a.txt")){
            fileId = R.raw.a;
        }else if (name.equals("n.txt")){
            fileId = R.raw.n;
        }

        InputStream inputStream = context.getResources().openRawResource(fileId);

        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line=r.readLine())!=null)
                col.add(line);
        } finally {
            r.close();
        }
    }

    static Dictionary getInstance(Context context){
        if (INSTANCE == null)
            INSTANCE = new Dictionary(context);
        return INSTANCE;
    }
}
