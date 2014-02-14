package com.duaandazkar.duaazkar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

/**
 * Created by fzaka on 12/31/13.
 */
public class DuaAdapter {

    public DuaArray S, E, M, D, RB;
    public Context context;
    public boolean wantTrans, wantTranslit;
    public boolean[][] favorites;
    public Menu menu;
    public DuaAdapter(Context c, boolean[][] favorites, Menu menu){
        this.context = c;
        init();
        Log.e("Test", S.toString());
        this.favorites = favorites;
        this.menu = menu;
    }
    public void init(){
        S = new DuaArray(R.array.salah, R.array.salah_count, R.array.salah_trans, R.array.salah_translit, R.array.salah_desc, R.array.salah_ref, context);
        E = new DuaArray(R.array.evening, R.array.evening_count, R.array.evening_trans, R.array.evening_translit, R.array.evening_desc, R.array.evening_ref,  context);
        M = new DuaArray(R.array.morning, R.array.morning_count, R.array.morning_trans, R.array.morning_translit, R.array.morning_desc, R.array.morning_ref, context);
        D = new DuaArray(R.array.daily, R.array.daily_count, R.array.daily_trans, R.array.daily_translit, R.array.daily_desc, R.array.daily_ref, context);
        RB = new DuaArray(R.array.rabbana, R.array.rabbana_count, R.array.rabbana_trans, R.array.rabbana_translit, R.array.rabbana_desc, R.array.rabbana_ref, context );
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        wantTrans = sharedPrefs.getBoolean("prefTrans", false);
        wantTranslit = sharedPrefs.getBoolean("prefTranslit", false);
    }

    public Dua getSalah(int position){
       DuaArray temp = S;
        if(S == null){
            Log.e("S", "null");
        }
        Log.e("position", position + "");
       // Log.e("testing", S.trans.getResourceId(position, -1) + " " + temp.translit.getResourceId(position, -1));
        //if(favorites[0][position] == null){
          //  Log.e("count", "null");
        //}
       Dua tempDua = new Dua(temp.arabic.getResourceId(position, -1), temp.count[position], temp.trans[position], temp.translit[position], temp.desc[position], temp.ref[position], wantTrans, wantTranslit, favorites[0][position]);
        if(tempDua == null){
            Log.e("temp", "duanull");
        }
        return tempDua;
    }
    public Dua getEvening(int position){
        DuaArray temp = E;
        // Log.e("testing", S.trans.getResourceId(position, -1) + " " + temp.translit.getResourceId(position, -1));
        return new Dua(temp.arabic.getResourceId(position, -1), temp.count[position], temp.trans[position], temp.translit[position], temp.desc[position], temp.ref[position], wantTrans, wantTranslit, favorites[1][position]);
    }
    public Dua getMorning(int position){
        DuaArray temp = M;
        // Log.e("testing", S.trans.getResourceId(position, -1) + " " + temp.translit.getResourceId(position, -1));
        return new Dua(temp.arabic.getResourceId(position, -1), temp.count[position], temp.trans[position], temp.translit[position], temp.desc[position], temp.ref[position], wantTrans, wantTranslit, favorites[2][position]);
    }
    public Dua getDaily(int position){
        DuaArray temp = D;
        // Log.e("testing", S.trans.getResourceId(position, -1) + " " + temp.translit.getResourceId(position, -1));
        return new Dua(temp.arabic.getResourceId(position, -1), temp.count[position], temp.trans[position], temp.translit[position], temp.desc[position], temp.ref[position], wantTrans, wantTranslit, favorites[3][position]);

    }
    public Dua getRabbana(int position){
        DuaArray temp = RB;
        // Log.e("testing", S.trans.getResourceId(position, -1) + " " + temp.translit.getResourceId(position, -1));
        return new Dua(temp.arabic.getResourceId(position, -1), temp.count[position], temp.trans[position], temp.translit[position], temp.desc[position], temp.ref[position], wantTrans, wantTranslit, favorites[4][position]);
    }

}
class DuaArray{
    public TypedArray arabic;
    public String[] count, trans, translit, desc, ref;
    public DuaArray(int arabic, int count, int trans, int translit, int desc, int ref, Context context){
        this.arabic = context.getResources().obtainTypedArray(arabic);
        this.count= context.getResources().getStringArray(count);
        this.trans=context.getResources().getStringArray(trans);
        this.translit= context.getResources().getStringArray(translit);
        this.desc = context.getResources().getStringArray(desc);
        this.ref = context.getResources().getStringArray(ref);

    }
}
class Dua{
    public int arabic;
    String count, trans, translit, desc, ref;
    boolean wantTrans, wantTranslit, isFav;
    //Menu menu;
    public Dua(int arabic, String count, String trans, String translit, String desc, String ref, boolean wantTrans, boolean wantTranslit, boolean isFav){
        this.arabic = arabic;
        this.count = count;
        this.trans = trans;
        this.translit = translit;
        this.desc = desc;
        this.ref = ref;
        this.wantTrans = wantTrans;
        this.wantTranslit = wantTranslit;
        this.isFav = isFav;
        //this.menu = menu;
    }
}
