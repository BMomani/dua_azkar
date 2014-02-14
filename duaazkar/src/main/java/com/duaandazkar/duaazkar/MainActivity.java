package com.duaandazkar.duaazkar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener, DuaFragment.DuaInterface{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    private MyAdapter mAdapter;
    private ViewPager mPager;
    private PagerTabStrip mPagerTitle;
    public static DuaAdapter dua;
    private static final int SETTINGS_RESULT = 1;
    private AudioStatusBar ad;
    private int type;
    private SharedPreferences sharedPrefs;
    private boolean changed = false;
    int count= 0;
    private boolean[][] favorites;
    protected PowerManager.WakeLock mWakeLock;
    public Menu menu;
    public MenuItem fav;
    public static boolean favBack = false;
    public ProgressDialog mProgressDialog;
    private static String file_url = "http://www.duaandazkar.com/audio_new/";
    public MediaPlayer mp;
    public int mpLength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("oncreate", "here");
       /*
        boolean imgTemp= checkImages();

        if(imgTemp == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Download");
            builder.setMessage("Download Dua images?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    //ad.downloadFile(mPager.getCurrentItem(), type);
                    new DownloadFileFromURL().execute(file_url);

                    //dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            builder.setIcon(R.drawable.ic_action_download);

            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
*/

        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        JSONArray jsonArray = null;
        //try{
        try{
            jsonArray = new JSONArray(sharedPrefs.getString("key", "[]"));
        }catch (Exception e){

        }
        favorites = new boolean[6][41];
        Log.e("yesjson", "here1");
        for (int i = 0; i < jsonArray.length(); i++) {
            String temp = null;
            try{
                temp = jsonArray.getString(i);
            }catch (Exception e){

            }
            String[] parts = temp.split(",");
            int tempType = Integer.parseInt(parts[0]);
            int tempIndex = Integer.parseInt(parts[1]);
            favorites[tempType][tempIndex] = true;
            Log.e("favorite", tempType + ", " + tempIndex);
        }
        //}catch(Exception e){
         //   Log.e("nojson", "here");
        //}
        //Log.e("favs", favorites.toString());
        dua = new DuaAdapter(getApplicationContext(), favorites, menu);
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1), true, "50+"));
        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setIcon(R.drawable.icon1);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mp = new MediaPlayer();
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            ad = (AudioStatusBar) findViewById(R.id.audio_area);
            ad.setAudioBarListener(new AudioStatusBar.AudioBarListener() {
                @Override
                public void onPlayPressed(){
                    Log.e("Play", "here");
                    Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_LONG).show();
                    //ad.showPromptForDownloadMode();
                    ad.setPausedButton();
                    boolean temp = ad.checkFileExists(mPager.getCurrentItem(), type);
                    if(ad.mCurrentMode == ad.PAUSED_MODE){
                        mp.seekTo(mpLength);
                        mp.start();
                    }else{
                        if(temp == true){
                            String packageName = getApplicationContext().getPackageName();
                            File externalPath = Environment.getExternalStorageDirectory();
                            File dirPath = new File(externalPath.getAbsolutePath() +
                                    "/Android/data/" + packageName + "/files");
                            String filePath = dirPath.toString() + File.separator + "CH " + (type + 1) + " DUA " + mPager.getCurrentItem() + ".mp3";

                            try {
                                mp.setDataSource(filePath);
                                mp.prepare();
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        ad.resetButton();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            builder.setTitle("Download");
                            builder.setMessage("Download Dua " + mPager.getCurrentItem() + " audio?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing but close the dialog
                                    //ad.downloadFile(mPager.getCurrentItem(), type);
                                    new DownloadFileFromURL().execute(file_url);

                                    //dialog.dismiss();
                                }

                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });
                            builder.setIcon(R.drawable.ic_action_download);

                            AlertDialog alert = builder.create();
                            alert.setCanceledOnTouchOutside(false);
                            alert.show();
                        }
                    }

                    //DownloadFile downloadFile = new DownloadFile();
                    //downloadFile.execute("http://google.com");
                }
                @Override
                public void onPausePressed(){
                    mp.pause();
                    mpLength = mp.getCurrentPosition();
                    ad.setPlayButton();
                }
                @Override
                public void onCancelPressed(boolean stopDownload){
                    Toast.makeText(getApplicationContext(), "Cancel",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onAcceptPressed(){
                    Toast.makeText(getApplicationContext(), "Accept",Toast.LENGTH_LONG).show();
                }
            });
            ad.setVisibility(View.GONE);
            displayView(0);
        }

        type = 0;
        mAdapter = new MyAdapter(getSupportFragmentManager(),0, dua );
        //supportInvalidateOptionsMenu();
        Log.e("invalid menu", "here");
        if(menu == null){
            //reload();
        }
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        /*mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                //supportInvalidateOptionsMenu();
                Log.e("viewpager listerner", "current item:" + mPager.getCurrentItem());
                //try{
                if(favorites[type][mPager.getCurrentItem()] == true){
                    //menu.getItem(R.id.action_favorite).setIcon(R.drawable.favorite);
                    Log.e("pageselected", type+"" + mPager.getCurrentItem());
                    if(fav == null){
                        //supportInvalidateOptionsMenu();
                        Log.e("menu item ", "null");
                        reload();

                    }else{
                        fav.setIcon(R.drawable.favorite);
                    }
                }else{
                    if(fav == null){
                        //supportInvalidateOptionsMenu();
                        Log.e("menu item ", "null");
                        reload();

                    }else{
                        fav.setIcon(R.drawable.not_favorite);
                    }
                }
                //}catch (Exception e){
                //reload();
                //}

            }
        });*/
       // mPager.setBackgroundResource(R.drawable.ch_1_dua);

        /*mPager.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                switch(e.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        mPager.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_DOWN:
                        //mPager.requestDisallowInterceptTouchEvent(true);
                        if (count % 2 == 0) {
                            //getSupportActionBar().hide();
                            ad.setVisibility(View.INVISIBLE);
                        } else {
                            //getSupportActionBar().show();
                            ad.setVisibility(View.VISIBLE);

                        }
                        count++;
                        break;
                }
                return false;
            }
        });
                */
        /*mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                Log.e("hi", (event.getAction() == MotionEvent.ACTION_DOWN) + " " + count);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e(null, "TOUCH EVENT"); // handle your fragment number here

                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.e("move", "here");
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.e("up", "here");
                }
                return false;
            }
        });
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
*/
        /*setOnClickListener(new FrameLayout.OnClickListener() {


            public void onClick(View v) {
                if (count % 2 == 0) {
                    getSupportActionBar().hide();
                } else {
                    getSupportActionBar().show();
                }
                count++;

            }
        });*/
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

    public boolean checkImages(){
        String packageName = getApplicationContext().getPackageName();
        File externalPath = Environment.getExternalStorageDirectory();
        File dirPath = new File(externalPath.getAbsolutePath() +
                "/Android/data/" + packageName + "/images");
        for(int i = 0; i<6; i++){
            for(int j = 0; j< getDuaCount(i); j++){
                if(i==0){

                    String filePath = dirPath.toString() + File.separator + "CH " + (type + 1) + " DUA " + mPager.getCurrentItem() + ".mp3";
                }
            }
        }
        return true;
    }
    public void createDialog(){
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

    }

    public void hideDialog(){
        mProgressDialog.dismiss();
    }

    public void setPage(int page){
        mPager.setCurrentItem(page);
    }
    /* @Override
        public boolean onTouchEvent(View v, MotionEvent e){
            switch(e.getAction()){
                case MotionEvent.ACTION_MOVE:
                    mPager.requestDisallowInterceptTouchEvent(true);
                    return false;
                case MotionEvent.ACTION_DOWN:
                    mPager.requestDisallowInterceptTouchEvent(false);
                    if (count % 2 == 0) {
                        //getSupportActionBar().hide();
                        ad.setVisibility(View.INVISIBLE);
                    } else {
                        //getSupportActionBar().show();
                        ad.setVisibility(View.VISIBLE);

                    }
                    count++;
                    return true;
            }
            return false;
        }*/
    public void needsHide(int index, int type){
        favBack = true;
        displayView(type, index);
    }

   /* @Override
    public void onBackPressed() {
        Log.e("back", "pressed");
        if(favBack == true){
            Log.e("favBack", "true");
            displayView(6);
        }
        favBack = false;
        super.onBackPressed();
    }*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            Log.e("back", "pressed2");
            if(favBack == true){
                Log.e("favBack", "true2");
                displayView(6);
                favBack = false;
            }else{
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void reload() {

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("type", type);
        savedInstanceState.putInt("index", mPager.getCurrentItem());
        Log.e("SaveInstance", type + " " + mPager.getCurrentItem());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        //supportInvalidateOptionsMenu();
        Log.e("restoreinstance", "here");
        DuaFragment.pos = 0;
        DuaFragment.top = 0;
        displayView(savedInstanceState.getInt("type"), savedInstanceState.getInt("index"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        dua.wantTrans = sharedPrefs.getBoolean("prefTrans", false);
        dua.wantTranslit = sharedPrefs.getBoolean("prefTranslit", false);
        changed = true;
    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.e("restart", "here");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e("stop", "here");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume", "here");
        //supportInvalidateOptionsMenu();
        if(changed == true){
            int temp;
            try{
                temp = mPager.getCurrentItem();
            }catch (Exception e){
                temp = 0;
            }
            DuaFragment.pos = 0;
            DuaFragment.top = 0;

            displayView(type);
            changed = false;
            mPager.setCurrentItem(temp);

        }
    }


    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            if(position == 6){
                Log.e("favs", "favs");
            }
            DuaFragment.pos = 0;
            DuaFragment.top = 0;

            displayView(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e("creating menu", "here");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.activity_main_actions, menu);
        fav = menu.findItem(R.id.action_favorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_RESULT);
                return true;
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                Dua temp = null;
                int curr = mPager.getCurrentItem();
                if(curr == 0){
                    return true;
                }
                curr--;
                switch (type){
                    case 0:
                        temp = dua.getSalah(curr);
                        break;
                    case 1:
                        temp = dua.getEvening(curr);
                        break;
                    case 2:
                        temp = dua.getMorning(curr);
                        break;
                    case 3:
                        temp = dua.getDaily(curr);
                        break;
                    case 4:
                        temp = dua.getRabbana(curr);
                        break;
                    case 5:
                        ///temp = dua.getR(curr);
                        break;
                }
                String shareBody = "\nPronunciation\n"+ temp.translit + "\n\nTranslation\n" + temp.trans+ "\n";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Dua: " + temp.desc);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;
            case R.id.action_favorite:
                //if(favorites[type][mPager.getCurrentItem()] == true){
                //    return true;
                //}
                if(mPager.getCurrentItem() == 0){
                    return true;
                }
                JSONArray jsonArray;
                jsonArray = new JSONArray();

                Log.e("json", jsonArray.toString());
                if(favorites[type][mPager.getCurrentItem()] == true){
                    favorites[type][mPager.getCurrentItem()] = false;
                    fav.setIcon(R.drawable.not_favorite);
                }else{
                    favorites[type][mPager.getCurrentItem()] = true;
                    fav.setIcon(R.drawable.favorite);
                }
                for(int i = 0; i<6; i++){
                    for(int j = 0; j<41; j++){
                        if(favorites[i][j] == true){
                            jsonArray.put(i + "," + j);
                        }
                    }
                }
                //jsonArray.put(type + "," + mPager.getCurrentItem() + "");
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("key", jsonArray.toString());
                Log.e("favorite", jsonArray.toString());
                editor.commit();


                return true;
            case R.id.action_jump:
                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Go To Dua");
                builder.setMessage("Select Dua: ");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.select_dialog_singlechoice);
                int tempcount = getDuaCount(type) -1 ;
                for(int i =1; i<= tempcount;i++){
                    arrayAdapter.add("Dua " + i);
                }
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        //ad.downloadFile(mPager.getCurrentItem(), type);
                        dialog.dismiss();

                        //dialog.dismiss();
                    }

                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                builder.setIcon(R.drawable.ic_action_download);
                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        String strName = arrayAdapter.getItem(item);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                MainActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();*/
                FragmentManager fm = getSupportFragmentManager();
                JumpFragment jumpDialog = JumpFragment.newInstance(type);
                jumpDialog.show(fm, JumpFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideFav(){
        menu.findItem(R.id.action_favorite).setVisible(false);
    }
    public void hideShare(){
        menu.findItem(R.id.action_share).setVisible(false);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        super.onPrepareOptionsMenu(menu);
        Log.e("prepare opions menu", "here");
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        fav = (MenuItem) menu.findItem(R.id.action_favorite);

        try{
            mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
                public void onPageScrollStateChanged(int state) {}
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                public void onPageSelected(int position) {
                    // Check if this is the page you want.
                    //supportInvalidateOptionsMenu();
                    Log.e("viewpager listerner", "current item:" + mPager.getCurrentItem());
                    //try{

                    ad.setVisibility(View.VISIBLE);
                    Dua temp = null;
                    int curr = mPager.getCurrentItem();
                    if(curr > 0){
                        curr--;

                        switch (type){
                            case 0:
                                temp = dua.getSalah(curr);
                                break;
                            case 1:
                                temp = dua.getEvening(curr);
                                break;
                            case 2:
                                temp = dua.getMorning(curr);
                                break;
                            case 3:
                                temp = dua.getDaily(curr);
                                break;
                            case 4:
                                temp = dua.getRabbana(curr);
                                break;
                            case 5:
                                ///temp = dua.getR(curr);
                                break;
                        }
                        ad.setCount(temp.count);
                        ad.setDesc(temp.desc);
                    }else{
                        ad.setVisibility(View.GONE);
                    }
                    if(favorites[type][mPager.getCurrentItem()] == true){
                        //menu.getItem(R.id.action_favorite).setIcon(R.drawable.favorite);
                        Log.e("pageselected", type+"" + mPager.getCurrentItem());
                        if(fav == null){
                            //supportInvalidateOptionsMenu();
                            Log.e("menu item ", "null");
                            reload();

                        }else{
                            fav.setIcon(R.drawable.favorite);
                        }
                    }else{
                        if(fav == null){
                            //supportInvalidateOptionsMenu();
                            Log.e("menu item ", "null");
                            reload();

                        }else{
                            fav.setIcon(R.drawable.not_favorite);
                        }
                    }

                    //}catch (Exception e){
                    //reload();
                    //}

                }
            });
        }catch (Exception e){
            reload();
        }
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULT)
        {
            displayUserSettings();
        }

    }


    private void displayUserSettings()
    {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String  settings = "";
        settings=settings+"\nTrans:"+ sharedPrefs.getBoolean("prefTrans", false);
        settings=settings+"\nTranslit:"+ sharedPrefs.getBoolean("prefTranslit", false);
        Log.e("Settings",settings);
    }
    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
     private void displayView(int position) {
         displayView(position, 0);
     }
    private static final String TAG_LIST = "list";


    public void displayView(int position, int index) {
        // update the main content by replacing fragments
        //Fragment fragment = null;
        ad = (AudioStatusBar) findViewById(R.id.audio_area);


        switch (position) {
            case 0:
                //fragment = new DuaFragment(1, 1, "hi1");
                mAdapter = new MyAdapter(getSupportFragmentManager(),0, dua);

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_1_dua);
                Log.e("here dua1", "dua1");
                type = 0;
                break;
            case 1:
                mAdapter = new MyAdapter(getSupportFragmentManager(),1 , dua);

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_2_dua);
                Log.e("here dua2", "dua2");

                type = 1;
                //fragment = new DuaFragment(1, 1, "hi2");
                break;
            case 2:
                mAdapter = new MyAdapter(getSupportFragmentManager(),2, dua );

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_3_dua);
                Log.e("here dua3", "dua3");

                type = 2;
                //fragment = new DuaFragment(1, 1, "hi3");
                break;
            case 3:
                mAdapter = new MyAdapter(getSupportFragmentManager(),3, dua );

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_4_dua);
                Log.e("here dua4", "dua4");

                type = 3;
                //fragment = new DuaFragment(1, 1, "hi4");
                break;
            case 4:
                mAdapter = new MyAdapter(getSupportFragmentManager(),4, dua );

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_5_dua);
                Log.e("here dua5", "dua5");

                type = 4;
                //fragment = new DuaFragment(1, 1, "hi5");
                break;
            case 5:
                mAdapter = new MyAdapter(getSupportFragmentManager(),5, dua );

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.VISIBLE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_6_dua_part_1);
                Log.e("here dua6", "dua6");

                type = 5;
                //fragment = new DuaFragment(1, 1, "hi6");
                break;
            case 6:
                mPager.setBackgroundResource(0);

                mAdapter = new MyAdapter(getSupportFragmentManager(),6, dua );

                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerTitle = (PagerTabStrip) findViewById(R.id.pager_title_strip);
                mPagerTitle.setVisibility(View.GONE);
                //mPagerTitle.setDrawFullUnderline(true);
                //mPagerTitle.setTabIndicatorColor(Color.BLUE);
                //menu.findItem(R.id.action_share).setVisible(false);
                //menu.findItem(R.id.action_favorite).setVisible(false);
                Log.e("hiding menu item", "here");
                mPager.setAdapter(mAdapter);
                //mPager.setBackgroundResource(R.drawable.ch_6_dua_part_1);
                /*
                setContentView(R.layout.fragment_favorites);

                // Get ListView object from xml
                String[] values = new String[] { "Android List View",
                        "Adapter implementation",
                        "Simple List View In Android",
                        "Create List View Android",
                        "Android Example",
                        "List View Source Code",
                        "List View Array Adapter",
                        "Android Example List View"
                };
                ListView listView = (ListView) findViewById(R.id.favorites);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);


                // Assign adapter to ListView
                listView.setAdapter(adapter);
                /*
                mPager.setVisibility(View.GONE);
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_LIST);
                if (fragment == null) {
                    fragment = SimpleListFragment.newInstance();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(android.R.id.content, fragment);
                    ft.commit();
                }*/
                break;
            default:
                break;
        }
        //ad.setVisibility(View.GONE);
        mPager.setCurrentItem(index);
        if(index > 0){
            ad.setVisibility(View.VISIBLE);
            Dua temp = null;
            int curr = mPager.getCurrentItem();
            curr--;

            switch (type){
                case 0:
                    temp = dua.getSalah(curr);
                    break;
                case 1:
                    temp = dua.getEvening(curr);
                    break;
                case 2:
                    temp = dua.getMorning(curr);
                    break;
                case 3:
                    temp = dua.getDaily(curr);
                    break;
                case 4:
                    temp = dua.getRabbana(curr);
                    break;
                case 5:
                    ///temp = dua.getR(curr);
                    break;
            }
            ad.setCount(temp.count);
            ad.setDesc(temp.desc);

        }else{
            ad.setVisibility(View.GONE);
        }
                //if (fragment != null) {
            //FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        //} else {
            // error in creating fragment
           // Log.e("MainActivity", "Error in creating fragment");
        //}
    }

    @Override
    public void setTitle(int i) {
        mTitle = navMenuTitles[i];
        getSupportActionBar().setTitle(mTitle);
        switch (i){
            case 0:
                getSupportActionBar().setIcon(R.drawable.icon1);
                break;
            case 1:
                getSupportActionBar().setIcon(R.drawable.icon2);
                break;
            case 2:
                getSupportActionBar().setIcon(R.drawable.icon3);
                break;
            case 3:
                getSupportActionBar().setIcon(R.drawable.icon4);
                break;
            case 4:
                getSupportActionBar().setIcon(R.drawable.icon5);
                break;
            case 5:
                getSupportActionBar().setIcon(R.drawable.icon6);
                break;
            case 6:
                getSupportActionBar().setIcon(R.drawable.star_hdpi);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    public class Pair<L,R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() { return left; }
        public R getRight() { return right; }

        @Override
        public int hashCode() { return left.hashCode() ^ right.hashCode(); }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof Pair)) return false;
            Pair pairo = (Pair) o;
            return this.left.equals(pairo.getLeft()) &&
                    this.right.equals(pairo.getRight());
        }

    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            createDialog();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0] + "CH%20" + (type + 1) + "%20DUA%20" + mPager.getCurrentItem() + ".mp3");
                Log.e("url", url.toString());
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                String packageName = getApplicationContext().getPackageName();
                File externalPath = Environment.getExternalStorageDirectory();
                File dirPath = new File(externalPath.getAbsolutePath() +
                        "/Android/data/" + packageName + "/files");
                String filePath = dirPath.toString() + File.separator + "CH " + (type + 1) + " DUA " + mPager.getCurrentItem() + ".mp3";
                OutputStream output = new FileOutputStream(filePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            hideDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String packageName = getApplicationContext().getPackageName();
            File externalPath = Environment.getExternalStorageDirectory();
            File dirPath = new File(externalPath.getAbsolutePath() +
                    "/Android/data/" + packageName + "/files");
            String filePath = dirPath.toString() + File.separator + "CH " + (type + 1) + " DUA " + mPager.getCurrentItem() + ".mp3";
            MediaPlayer mp = new MediaPlayer();

            try {
                mp.setDataSource(filePath);
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // setting downloaded into image view
        }

    }
}


