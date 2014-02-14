package com.duaandazkar.duaazkar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class JumpFragment extends DialogFragment{
    public static final String TAG = "JumpFragment";
    public int type;
    int pos;
    public JumpFragment(){
    }
    /*public interface JumpListener{
        public void setPage(int type);
    }*/
    static JumpFragment newInstance(int num) {
        JumpFragment f = new JumpFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
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
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        type = getArguments().getInt("num");
        FragmentActivity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.jump_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Jump");
        builder.setIcon(R.drawable.ic_action_forward);
        final Spinner duaSpinner = (Spinner)layout.findViewById(R.id.dua_spinner);
        String[] duas = new String[getDuaCount(type)];
        for (int i=0; i<duas.length; i++){
            duas[i] = "Dua " + (i+1);
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(activity, android.R.layout.simple_spinner_item, duas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        duaSpinner.setAdapter(adapter);

        duaSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long rowId) {
                        Log.e("selected", position + "");
                        pos = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });


        builder.setView(layout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            ((MainActivity) getActivity()).setPage(pos+1);
                            dialog.dismiss();
                            /*String text = input.getText().toString();
                            if (TextUtils.isEmpty(text)){
                                text = input.getHint().toString();
                            }

                            int page = Integer.parseInt(text);
                            if (page >= Constants.PAGES_FIRST && page
                                    <= Constants.PAGES_LAST) {
                                Activity activity = getActivity();
                                if (activity instanceof QuranActivity) {
                                    ((QuranActivity) activity).jumpTo(page);
                                }
                                else if (activity instanceof PagerActivity) {
                                    ((PagerActivity) activity).jumpTo(page);
                                }
                            }*/
                        } catch (Exception e) {
                        }
                    }
                });

        return builder.create();
    }
}