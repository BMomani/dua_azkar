package com.duaandazkar.duaazkar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;


public class AudioStatusBar extends RelativeLayout {

    public static final int STOPPED_MODE = 1;
    public static final int DOWNLOADING_MODE = 2;
    public static final int PLAYING_MODE = 3;
    public static final int PAUSED_MODE = 4;
    public static final int PROMPT_DOWNLOAD_MODE = 5;

    private Context mContext;
    public int mCurrentMode;
    private int mButtonWidth;
    private int mSeparatorWidth;
    private int mSeparatorSpacing;
    private int mTextFontSize;
    private int mTextFullFontSize;

    private int mCurrentQari;
    private int mCurrentRepeat = 0;
    private boolean mHaveCriticalError = false;
    private SharedPreferences mSharedPreferences;

    private TextView mProgressText;
    private ProgressBar mProgressBar;
    private TextView mRepeatButton;
    private AudioBarListener mAudioBarListener;
    private TextView mPromptTextCount;
    private TextView mPromptTextDesc, mPromptText;
    private ImageView button, button2, separator1, separator2;
    private int[] mRepeatValues = { 0, 1, 2, -1 };

    public interface AudioBarListener {
        public void onPlayPressed();
        public void onPausePressed();
        public void onCancelPressed(boolean stopDownload);
        public void onAcceptPressed();

    }

    public AudioStatusBar(Context context) {
        super(context);
        init(context);
    }

    public AudioStatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioStatusBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        Resources resources = getResources();
        mButtonWidth = resources.getDimensionPixelSize(R.dimen.audiobar_button_width);
        mSeparatorWidth = resources.getDimensionPixelSize(
                R.dimen.audiobar_separator_width);
        mSeparatorSpacing = resources.getDimensionPixelSize(
                R.dimen.audiobar_separator_padding);
        mTextFontSize = resources.getDimensionPixelSize(
                R.dimen.audiobar_text_font_size);
        mTextFullFontSize = resources.getDimensionPixelSize(
                R.dimen.audiobar_text_full_font_size);
        //setOrientation(LinearLayout.HORIZONTAL);

        addCount();
        addSeparator2();
        addDesc();
        addSeparator1();
        addButton(R.drawable.ic_action_play);

    }

    public void addCount(){
        mPromptTextCount = new TextView(mContext);
        mPromptTextCount.setTextColor(Color.WHITE);
        mPromptTextCount.setGravity(Gravity.CENTER);
        mPromptTextCount.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mTextFullFontSize);
        mPromptTextCount.setId(1);
        mPromptTextCount.setText("hey");
        //mPromptTextCount.setText(count);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //params.weight = 1;
        //params.addRule(RelativeLayout.RIGHT_OF, separator1.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL, mPromptTextCount.getId());
        addView(mPromptTextCount, params);
    }
    public void setCount(String text){
        mPromptTextCount.setText(text);
    }
    public void addDesc(){
        mPromptTextDesc = new TextView(mContext);
        mPromptTextDesc.setTextColor(Color.WHITE);
        mPromptTextDesc.setGravity(Gravity.CENTER);
        mPromptTextDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextFontSize);
        mPromptTextDesc.setPadding(0, 0, mSeparatorSpacing, 0);
        mPromptTextCount.setText("hey");
        mPromptTextDesc.setId(3);
        //mPromptTextDesc.setText(count);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //params.weight = 1;
        params.addRule(RelativeLayout.RIGHT_OF, separator2.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL, mPromptTextDesc.getId());
        addView(mPromptTextDesc, params);
    }
    public void setDesc(String text){
        mPromptTextDesc.setText(text);
    }
    public void showPromptForDownloadMode(){
        mCurrentMode = PROMPT_DOWNLOAD_MODE;

        removeAllViews();
        addButton(R.drawable.ic_action_cancel);
        addSeparator1();

        mPromptText = new TextView(mContext);
        mPromptText.setTextColor(Color.WHITE);
        mPromptText.setGravity(Gravity.CENTER_VERTICAL);
        mPromptText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mTextFontSize);
        mPromptText.setId(6);
        mPromptText.setText("Are you sure you want to download?");
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, separator1.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL, mPromptText.getId());
        //params.weight = 1;
        addView(mPromptText, params);
        addSeparator3();
        addButton2(R.drawable.ic_accept);
    }
    public boolean checkFileExists(int index, int type){
        String packageName = mContext.getPackageName();
        File externalPath = Environment.getExternalStorageDirectory();
        File dirPath = new File(externalPath.getAbsolutePath() +
                "/Android/data/" + packageName);
        File projDir = new File(dirPath.toString());
        if(!projDir.exists()){
            Log.e("making dir1", "here");
            projDir.mkdir();
        }
        dirPath = new File(externalPath.getAbsolutePath() +
                "/Android/data/" + packageName + "/files");
        projDir = new File(dirPath.toString());
        if(!projDir.exists()){
            Log.e("making dir2", "here");
            projDir.mkdir();
        }
        String filePath = dirPath.toString() + File.separator + "CH " + (type + 1) + " DUA " + index + ".mp3";
        Log.e("Filepath", filePath);
        File file = new File(filePath);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }

    public boolean downloadFile(int index, int type){
        Toast.makeText(mContext, "downloading", Toast.LENGTH_LONG).show();
        String dirPath = mContext.getFilesDir().getAbsolutePath();
        String filePath = dirPath + File.separator + "CH " + index + " DUA " + type + ".mp3";
        boolean test = isDownloadManagerAvailable();
        if(test == false){
            Toast.makeText(mContext, "Error in download", Toast.LENGTH_LONG).show();
        }
        return true;
    }


    public boolean isDownloadManagerAvailable() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void setPausedButton(){
        button.setImageResource(R.drawable.ic_action_pause);
        mCurrentMode = PLAYING_MODE;
    }

    public void resetButton(){
        button.setImageResource(R.drawable.ic_action_play);
        mCurrentMode = STOPPED_MODE;
    }

    public void setPlayButton(){
        button.setImageResource(R.drawable.ic_action_play);
        mCurrentMode = PAUSED_MODE;
    }
    private void showPlayingMode(boolean isPaused) {
        removeAllViews();
        int button;
        if (isPaused){
            button = R.drawable.ic_action_play;
            mCurrentMode = PAUSED_MODE;
        }
        else {
            button = R.drawable.ic_action_pause;
            mCurrentMode = PLAYING_MODE;
        }

        addButton(R.drawable.ic_action_stop);
        addButton(R.drawable.ic_action_previous);
        addButton(button);
        addButton(R.drawable.ic_action_next);

        mCurrentRepeat = 0;
        mRepeatButton = new TextView(mContext);
        mRepeatButton.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_action_repeat, 0, 0, 0);
        mRepeatButton.setBackgroundResource(
                R.drawable.abc_item_background_holo_dark);
        mRepeatButton.setTag(R.drawable.ic_action_repeat);
        mRepeatButton.setOnClickListener(mOnClickListener);
        addView(mRepeatButton, LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
    }

    public void addButton(int imageId){
        button = new ImageView(mContext);
        button.setImageResource(imageId);
        button.setScaleType(ImageView.ScaleType.CENTER);
        button.setOnClickListener(mOnClickListener);
        button.setTag(imageId);
        button.setId(5);
        LayoutParams paddingParams = new LayoutParams(mButtonWidth, LayoutParams.MATCH_PARENT);
        paddingParams.addRule(RelativeLayout.RIGHT_OF, separator1.getId());
        addView(button, paddingParams);
    }
    public void addButton2(int imageId){
        button2 = new ImageView(mContext);
        button2.setImageResource(imageId);
        button2.setScaleType(ImageView.ScaleType.CENTER);
        button2.setOnClickListener(mOnClickListener);
        button2.setTag(imageId);
        button2.setId(7);
        button2.setBackgroundResource(R.drawable.abc_item_background_holo_dark);
        LayoutParams paddingParams = new LayoutParams(mButtonWidth, LayoutParams.MATCH_PARENT);
        paddingParams.addRule(RelativeLayout.RIGHT_OF, separator2.getId());
        addView(button2, paddingParams);


    }
    public void addSeparator1(){
        separator1 = new ImageView(mContext);
        separator1.setBackgroundColor(Color.WHITE);
        separator1.setPadding(0, mSeparatorSpacing, 0, mSeparatorSpacing);
        separator1.setId(4);
        LayoutParams paddingParams = new LayoutParams(mSeparatorWidth, LayoutParams.MATCH_PARENT);
        paddingParams.setMargins(0, 0, mSeparatorSpacing, 0);
        paddingParams.addRule(RelativeLayout.RIGHT_OF, mPromptTextDesc.getId());
        addView(separator1, paddingParams);
    }
    public void addSeparator2(){
        separator2 = new ImageView(mContext);
        separator2.setBackgroundColor(Color.WHITE);
        separator2.setPadding(0, mSeparatorSpacing, 0, mSeparatorSpacing);
        separator2.setId(2);
        LayoutParams paddingParams =
                new LayoutParams(mSeparatorWidth, LayoutParams.MATCH_PARENT);
        paddingParams.setMargins(mSeparatorSpacing, 0, mSeparatorSpacing, 0);
        paddingParams.addRule(RelativeLayout.RIGHT_OF, mPromptTextCount.getId());
        addView(separator2, paddingParams);
    }
    public void addSeparator3(){
        separator2 = new ImageView(mContext);
        separator2.setBackgroundColor(Color.WHITE);
        separator2.setPadding(0, mSeparatorSpacing, 0, mSeparatorSpacing);
        separator2.setId(4);
        LayoutParams paddingParams =
                new LayoutParams(mSeparatorWidth, LayoutParams.MATCH_PARENT);
        paddingParams.setMargins(mSeparatorSpacing, 0, mSeparatorSpacing, 0);
        paddingParams.addRule(RelativeLayout.RIGHT_OF, mPromptText.getId());
        addView(separator2, paddingParams);
    }

    private void incrementRepeat(){
        mCurrentRepeat++;
        if (mCurrentRepeat == mRepeatValues.length){ mCurrentRepeat = 0; }
        String str = null;
        int value = mRepeatValues[mCurrentRepeat];
        if (value == 0){ str = ""; }
        else if (value > 0){
            str = mRepeatValues[mCurrentRepeat] + "";
        }
        else { str = mContext.getString(R.string.infinity); }
        mRepeatButton.setText(str);
    }

    public void setAudioBarListener(AudioBarListener listener){
        mAudioBarListener = listener;
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mAudioBarListener != null){
                int tag = (Integer)view.getTag();
                switch (tag){
                    case R.drawable.ic_action_play:
                        Log.e("Play", "herE");
                        mAudioBarListener.onPlayPressed();
                        break;
                    case R.drawable.ic_action_pause:
                        mAudioBarListener.onPausePressed();
                        break;
                    case R.drawable.ic_action_cancel:
                        /*if (mHaveCriticalError){
                            mHaveCriticalError = false;
                        }else {
                            mAudioBarListener.onCancelPressed(
                                    mCurrentMode != PROMPT_DOWNLOAD_MODE);
                        }
                        break;*/
                        removeAllViews();
                        init(mContext);
                        setCount(mPromptTextCount.getText().toString());
                        setDesc(mPromptTextDesc.getText().toString());
                    case R.drawable.ic_action_accept:
                        mAudioBarListener.onAcceptPressed();
                        break;
                }
            }
        }
    };
}
