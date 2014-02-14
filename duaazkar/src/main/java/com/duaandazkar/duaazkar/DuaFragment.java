package com.duaandazkar.duaazkar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fzaka on 12/24/13.
 */
public class DuaFragment extends Fragment implements AudioStatusBar.AudioBarListener{
    int type;
    int index;
    int count = 0;
    Menu menu;
    public static int pos = 0, top=0;
    DuaAdapter dua = MainActivity.dua;
    public String translation, transliteration;

    public ArrayList<String> titles ;

    public ArrayList<String> descriptions;

    public  ArrayList<Integer> images, typeA, indexA;

    public static int[] icons= {R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5, R.drawable.icon6};
    ListView listView;
    RelativeLayout r;
    List<RowItem> rowItems;
    AudioStatusBar ad;
    public View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // dua = ((DuaAdapter) getActivity().getApplication());


    }

    public interface DuaInterface {

        public void needsHide(int index, int type);
        //public void hideFav();
        //public void hideShare();
    }
    public static Fragment newInstance(int type, int index){
        DuaFragment d = new DuaFragment();
        Bundle b = new Bundle();
        b.putInt("type", type);
        b.putInt("index", index);
        d.setArguments(b);
        return d;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onPlayPressed(){
        Toast.makeText(getActivity().getApplicationContext(), "Play",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPausePressed(){
        Toast.makeText(getActivity().getApplicationContext(), "Pause",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onCancelPressed(boolean stopDownload){
        Toast.makeText(getActivity().getApplicationContext(), "Cancel",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onAcceptPressed(){
        Toast.makeText(getActivity().getApplicationContext(), "Accept",Toast.LENGTH_LONG).show();
    }
    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e("creating menu", "here");
        this.menu = menu;
        menuInflater.inflate(R.menu.activity_main_actions, menu);
        ((DuaInterface)getActivity()).hideFav();
        ((DuaInterface)getActivity()).hideShare();
        super.onCreateOptionsMenu(menu, menuInflater);
    }*/
    public void reload() {

        Intent intent = getActivity().getIntent();
        getActivity().overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getActivity().finish();

        getActivity().overridePendingTransition(0, 0);
        startActivity(intent);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.type = getArguments().getInt("type");
        this.index = getArguments().getInt("index");
        Log.e("Test", type + " " + index );
        if(dua == null){
            reload();
        }
            /*if(MainActivity.favBack == true){
            get.setOnKeyListener( new View.OnKeyListener()
            {
                @Override
                public boolean onKey( View v, int keyCode, KeyEvent event )
                {
                    if( keyCode == KeyEvent.KEYCODE_BACK )
                    {
                        Toast toast = Toast.makeText(dua.context,"back",Toast.LENGTH_SHORT);
                        return true;
                    }
                    return false;
                }
            } );
        }*/

        if(type == 6){
            view = inflater.inflate(R.layout.fragment_favorites, container, false);
            /*view.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.e("back", "pressed");
                        return true;
                    }
                    return false;
                }
            });*/
            // Get ListView object from xml
            titles = new ArrayList<String>();
            descriptions = new ArrayList<String>();
            images = new ArrayList<Integer>();
            typeA = new ArrayList<Integer>();
            indexA = new ArrayList<Integer>();
            rowItems = new ArrayList<RowItem>();
            for(int i = 0; i<6; i++){
                for(int j = 0; j<41; j++){
                    boolean temp = dua.favorites[i][j];
                    if(temp == true){
                        Log.e("isfav", i+" " + j);
                        images.add(icons[i]);
                        titles.add("Dua " + (j));
                        switch(i){
                            case 0:
                                descriptions.add(dua.getSalah(j-1).desc);
                                typeA.add(i);
                                break;
                            case 1:
                                descriptions.add(dua.getEvening(j-1).desc);
                                typeA.add(i);
                                break;
                            case 2:
                                descriptions.add(dua.getMorning(j-1).desc);
                                typeA.add(i);
                                break;
                            case 3:
                                descriptions.add(dua.getDaily(j-1).desc);
                                typeA.add(i);
                                break;
                            case 4:
                                descriptions.add(dua.getRabbana(j-1).desc);
                                typeA.add(i);
                                break;
                                //descriptions.add(dua.getRabbana(j).desc);
                            case 5:
                                //descriptions.add(dua.getSalah(j).desc);
                                break;
                        }
                        indexA.add(j-1);
                    }
                }
            }
            Log.e("he", "hey");
            for (int i = 0; i < titles.size(); i++) {
                RowItem item = new RowItem(images.get(i), titles.get(i), descriptions.get(i), typeA.get(i), indexA.get(i));
                rowItems.add(item);
                Log.e("Row Item", item.getTitle() + " " + item.type + " " + item.index + " " + item.getDesc());
            }
            Log.e("hide fav 2", "here");
            //getActivity().supportInvalidateOptionsMenu();
            //((DuaInterface)getActivity()).hideFav();
            //((DuaInterface)getActivity()).hideShare();

            listView = (ListView) view.findViewById(R.id.favorites);
            CustomListViewAdapter adapter = new CustomListViewAdapter(dua.context,R.layout.favorite_item, rowItems);
            listView.setAdapter(adapter);
            listView.setSelectionFromTop(pos, top);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Log.e("ListClick", " " + position);
                    /*Toast toast = Toast.makeText(dua.context,"Item " + (position + 1) + ": " + rowItems.get(position),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();*/
                    int j = rowItems.get(position).type,i= rowItems.get(position).index;
                    //listView.setVisibility(View.GONE);
                    /*Fragment newFragment = new DuaFragment().newInstance(rowItems.get(position+1).type, rowItems.get(position+1).index);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack
                    transaction.add(R.id.frame_container, newFragment);
                    transaction.addToBackStack(null);

// Commit the transaction
                    transaction.commit();*/
                    pos = listView.getFirstVisiblePosition();
                    View v = listView.getChildAt(0);
                    top = (v == null) ? 0: v.getTop();
                    ((DuaInterface)getActivity()).needsHide(i+1, j);
                }
            });
            return view;
        }
        //DuaAdapter.init();
        try{
            if(index != 0){
                view = inflater.inflate(R.layout.fragment_main, container, false);
                TextView trans = (TextView) view.findViewById(R.id.trans);
                TextView translit = (TextView) view.findViewById(R.id.translit);
                TextView transT = (TextView) view.findViewById(R.id.transText);
                TextView translitT = (TextView) view.findViewById(R.id.translitText);
                TextView ref = (TextView) view.findViewById(R.id.ref);
                r = (RelativeLayout) view.findViewById(R.id.container);
                r.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.e("hi", event.toString());
                        if (event.getAction() == MotionEvent.ACTION_DOWN){
                            Log.v(null, "TOUCH EVENT"); // handle your fragment number here
                            Log.e("count", count + "");
                            if (count % 2 == 0) {
                                //getSupportActionBar().hide();
                                ad.setVisibility(View.INVISIBLE);
                                toggleStatusBar(getActivity(), true);
                            } else {
                                //getSupportActionBar().show();
                                toggleStatusBar(getActivity(), false);
                                ad.setVisibility(View.GONE);

                            }
                            count++;
                            return true;
                        }
                        return false;
                    }
                });
                trans.setTextSize(16);
                transT.setTextSize(12);
                //trans.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pronun_small, null, null, null);
                translit.setTextSize(16);
                translitT.setTextSize(12);
                transT.setText("Translation");
                translitT.setText("Pronunciation");
                ImageView duaImg = (ImageView) view.findViewById(R.id.duaImg);
                ImageView sep1 = (ImageView) view.findViewById(R.id.sep1);
                ImageView sep2 = (ImageView) view.findViewById(R.id.sep2);
                RelativeLayout r = (RelativeLayout) view.findViewById(R.id.container);
                Dua curr = null;
                index--;
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.pager);

                if(type == 0){
                    //vp.setBackgroundResource(R.drawable.ch_1_dua);
                    curr = dua.getSalah(index);
                    duaImg.setImageResource(curr.arabic);
                    sep1.setImageResource(R.drawable.div);
                    trans.setText(curr.trans);
                    sep2.setImageResource(R.drawable.div);
                    translit.setText(curr.translit);
                    ref.setText("[" + curr.ref + "]");
                    translation = curr.trans;
                    transliteration = curr.translit;
                    if(curr.wantTranslit == false){
                        sep1.setVisibility(View.GONE);
                        translit.setVisibility(View.GONE);
                        translitT.setVisibility(View.GONE);
                    }
                    if(curr.wantTrans == false){
                        sep2.setVisibility(View.GONE);
                        trans.setVisibility(View.GONE);
                        transT.setVisibility(View.GONE);
                    }
                    /*ad = (AudioStatusBar) view.findViewById(R.id.audio_area);
                    ad.addButton(R.drawable.ic_action_play);
                    ad.addSeparator();
                    ad.addCount(curr.count);
                    ad.addSeparator();
                    ad.addDesc(curr.desc);
                    */
                }else if(type == 1){
                    //vp.setBackgroundResource(R.drawable.ch_2_dua);
                    curr = dua.getEvening(index);
                    duaImg.setImageResource(curr.arabic);
                    sep1.setImageResource(R.drawable.div);
                    trans.setText(curr.trans);
                    sep2.setImageResource(R.drawable.div);
                    translit.setText(curr.translit);
                    ref.setText("[" + curr.ref + "]");
                    translation = curr.trans;
                    transliteration = curr.translit;
                    if(curr.wantTranslit == false){
                        sep1.setVisibility(View.GONE);
                        translit.setVisibility(View.GONE);
                        translitT.setVisibility(View.GONE);
                    }
                    if(curr.wantTrans == false){
                        sep2.setVisibility(View.GONE);
                        trans.setVisibility(View.GONE);
                        transT.setVisibility(View.GONE);
                    }
                   // r.setBackgroundResource(R.drawable.ch_2_dua);
                }else if(type == 2){
                    //vp.setBackgroundResource(R.drawable.ch_3_dua);
                        curr = dua.getMorning(index);
                    duaImg.setImageResource(curr.arabic);
                    sep1.setImageResource(R.drawable.div);
                    trans.setText(curr.trans);
                    sep2.setImageResource(R.drawable.div);
                    translit.setText(curr.translit);
                    ref.setText("[" + curr.ref + "]");
                    translation = curr.trans;
                    transliteration = curr.translit;
                    if(curr.wantTranslit == false){
                        sep1.setVisibility(View.GONE);
                        translit.setVisibility(View.GONE);
                        translitT.setVisibility(View.GONE);
                    }
                    if(curr.wantTrans == false){
                        sep2.setVisibility(View.GONE);
                        trans.setVisibility(View.GONE);
                        transT.setVisibility(View.GONE);
                    }
                }else if(type == 3){
                    //vp.setBackgroundResource(R.drawable.ch_4_dua);
                    curr = dua.getDaily(index);
                    duaImg.setImageResource(curr.arabic);
                    sep1.setImageResource(R.drawable.div);
                    trans.setText(curr.trans);
                    sep2.setImageResource(R.drawable.div);
                    translit.setText(curr.translit);
                    ref.setText("[" + curr.ref + "]");
                    translation = curr.trans;
                    transliteration = curr.translit;
                    if(curr.wantTranslit == false){
                        sep1.setVisibility(View.GONE);
                        translit.setVisibility(View.GONE);
                        translitT.setVisibility(View.GONE);
                    }
                    if(curr.wantTrans == false){
                        sep2.setVisibility(View.GONE);
                        trans.setVisibility(View.GONE);
                        transT.setVisibility(View.GONE);
                    }
                }else if(type == 4){
                    //vp.setBackgroundResource(R.drawable.ch_5_dua);
                    curr = dua.getRabbana(index);
                    duaImg.setImageResource(curr.arabic);
                    sep1.setImageResource(R.drawable.div);
                    trans.setText(curr.trans);
                    sep2.setImageResource(R.drawable.div);
                    translit.setText(curr.translit);
                    ref.setText("[" + curr.ref + "]");
                    translation = curr.trans;
                    transliteration = curr.translit;
                    if(curr.wantTranslit == false){
                        sep1.setVisibility(View.GONE);
                        translit.setVisibility(View.GONE);
                        translitT.setVisibility(View.GONE);
                    }
                    if(curr.wantTrans == false){
                        sep2.setVisibility(View.GONE);
                        trans.setVisibility(View.GONE);
                        transT.setVisibility(View.GONE);
                    }
                }
                if(curr.isFav == true){
                    //menu.getItem(R.id.action_favorite).setIcon(R.drawable.favorite);
                    Log.e("Favorite", type +  " " + index + " is a favorite");
                }
                //Log.e("currSettings", "Trans: " + curr.wantTrans + " Translit: " + curr.wantTranslit);
                //TextView t = (TextView) view.findViewById(R.id.filler);
                //t.setPadding(0, 0, 0, 160);
                ref.setTextSize(10);
                //ref.setTextColor(Color.GRAY);
                transT.setTextColor(Color.GRAY);
                translitT.setTextColor(Color.GRAY);
                sep1.setVisibility(View.GONE);
                sep2.setVisibility(View.GONE);
                trans.setPadding(0, 10, 0, 10);
                translit.setPadding(0,10,0,10);
            }else{
                view = inflater.inflate(R.layout.fragment_chapter, container, false);
                ImageView bismillah = (ImageView) view.findViewById(R.id.bismillah);
                TextView bismillah_trans = (TextView) view.findViewById(R.id.bismillah_trans);
                TextView title = (TextView) view.findViewById(R.id.title);
                TextView info = (TextView) view.findViewById(R.id.info);
                bismillah.setImageResource(R.drawable.b2);
                bismillah_trans.setText("In the Name of Allah, the Most Gracious, the Most Merciful");
                bismillah_trans.setTextColor(Color.WHITE);
                bismillah_trans.setTextSize(10);

                if(type == 0){
                    view.setBackgroundResource(R.drawable.chapter_1_bg);
                    title.setText("Authentic Du'a after Fard Saalah");
                    info.setText("Some of the very authentic Dua's and Dhikr is to be recited immediately after Fard Saalah and before Du'a (Raising Hand)");
                    title.setTextColor(Color.RED);
                    info.setTextColor(Color.YELLOW);
                }else if(type == 1){
                    view.setBackgroundResource(R.drawable.chapter_2_bg);
                    title.setText("Evening Dhikr and Azkar");
                    info.setText("And glorify the name of your Lord morning and evening \n (Al-Quran 76 : 025) \n  \n  In order that ye (O men) may believe in Allah and His Messenger, that ye may assist and honour Him, and celebrate His praise morning and evening \n (Al-Quran 48 : 009)\nTo be recited between Asr and Magrib (Before Sunset)");
                    title.setTextColor(Color.YELLOW);
                    info.setTextColor(Color.CYAN);
                }else if(type == 2){
                    view.setBackgroundResource(R.drawable.chapter_3_bg);
                    title.setText("Morning Dhikr and Azkar");
                    info.setText("And glorify the name of your Lord morning and evening \n (Al-Qur'an 76 : 025) \n  \n In order that ye (O men) may believe in Allah and His Messenger, that ye may assist and honor Him, and celebrate His praise morning and evening \n (Al-Qur'an 48 : 009)\nTo be recited between Sub-e-Sadik to Fajr (Before Sunrise)");
                    title.setTextColor(Color.RED);
                    info.setTextColor(Color.WHITE);
                }else if(type == 3){
                    view.setBackgroundResource(R.drawable.chapter_4_bg);
                    title.setText("Daily Essential Du'a For All");
                    info.setText("Selection of authentic Dua's form the Qur'an and Sunnah for various occasions. All Dua's in this chapter are simple and easy to memories which can be implemented on our daily lives.");
                    title.setTextColor(Color.RED);
                    info.setTextColor(Color.WHITE);
                }else if(type == 4){
                    view.setBackgroundResource(R.drawable.chapter_5_bg);
                    title.setText("Qur'anic Du'as with 'Rabbana'");
                    info.setText("Qur'an Dua's begins with \"Rabbana\" which is selected from the famous 40 Dua's from the Holy Qur'an. \n You may implement  the Dua's at any time or during Saalah");
                    title.setTextColor(Color.RED);
                    info.setTextColor(Color.WHITE);
                }else if(type == 5){
                    view.setBackgroundResource(R.drawable.ch_6_bg);
                    title.setText("Ruquiya");
                    info.setText("");
                    title.setTextColor(Color.RED);
                    info.setTextColor(Color.CYAN);
                }
                title.setTypeface(null, Typeface.BOLD);
                title.setTextSize(16);
            }
            return view;

        }catch (Exception e){
            reload();
        }
        //ad = (AudioStatusBar) view.findViewById(R.id.audio_area);
        /*view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("hi", event.toString());
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.v(null, "TOUCH EVENT"); // handle your fragment number here
                    Log.e("count", count + "");
                    if (count % 2 == 0) {
                        //getSupportActionBar().hide();
                        ad.setVisibility(View.INVISIBLE);
                        toggleStatusBar(getActivity(), true);
                    } else {
                        //getSupportActionBar().show();
                        toggleStatusBar(getActivity(), false);
                        ad.setVisibility(View.GONE);

                    }
                    count++;
                    return true;
                }
                return false;
            }
        });*/
        //trans.setText("Testing" + index + " " + type + " ");
        return null;
    }

   /* @Override
    public void onDestroy(){
        super.onDestroy();
        ImageView duaImg = (ImageView) view.findViewById(R.id.duaImg);
        duaImg.invalidate();
        duaImg.setImageResource(0);
    }*/
    public static void toggleStatusBar(Activity myActivityReference, boolean boolYesNo) {
        if(boolYesNo == true)
        {
            WindowManager.LayoutParams attrs = myActivityReference.getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            myActivityReference.getWindow().setAttributes(attrs);
        }
        else
        {
            WindowManager.LayoutParams attrs = myActivityReference.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            myActivityReference.getWindow().setAttributes(attrs);
        }
    }
}


class MyAdapter extends FragmentStatePagerAdapter {
    int count;
    DuaAdapter dua;
    public MyAdapter(FragmentManager fm, int count, DuaAdapter d) {
        super(fm);
        this.count = count;
        this.dua = d;
    }

    @Override
    public int getCount() {
        return getDuaCount(count);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Chapter " + (count + 1);
        }
        return "Dua " + (position);
    }

    @Override
    public Fragment getItem(int position) {
        /*DuaFragment d = (DuaFragment) new Fragment();//DuaFragment(1, count, position, dua);
        Bundle b = new Bundle(3);
        b.putInt("type", count);
        b.putInt("index", position);
        d.setArguments(b);
        return d;*/
        //ViewPager v = (ViewPager)
        return DuaFragment.newInstance(count, position);
    }
    public int getDuaCount(int type){
        switch(type){
            case 0:
                return 14;
            case 1:
                return 32;
            case 2:
                return 33;
            case 3:
                return 39;
            case 4:
                return 41;
            case 5:
                return 4;
            case 6:
                return 1;
        }
        return 0;
    }
}
