package com.devseed.fiveinarow.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Config1Fragment extends Fragment {


    public Config1Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String []item_title=new String[]{
                getString(R.string.menu_aiSelect),
                getString(R.string.menu_aiChess),
                getString(R.string.menu_p1name),
                getString(R.string.menu_p2name)};
        final String []item_value=new String[]{
                AppValues.pref.ai,
                AppValues.pref.color_ai+"",
                AppValues.pref.player_1,
                AppValues.pref.player_2};
        item_value[1]=AppValues.pref.color_ai== ChessKernel.white ? getString(R.string.chess_white):getString(R.string.cheess_black);
        ArrayList<HashMap<String,Object>> itemlist=new ArrayList<>();
        for(int i=0;i<item_title.length;i++){
            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("item_title",item_title[i]);
            map.put("item_value",item_value[i]);
            itemlist.add(map);
        }
        final SimpleAdapter adapter_config1=new SimpleAdapter(getActivity(),
                                                        itemlist,R.layout.list_config1,
                                                        new String[]{"item_title","item_value"},new int[]{R.id.text_title,R.id.text_value});
        View rootview=inflater.inflate(R.layout.fragment_config1, container, false);
        final ListView list_config1= (ListView) rootview.findViewById(R.id.list_config1);
        list_config1.setAdapter(adapter_config1);//simpleadapter不能实现初始化改变一行颜色
        list_config1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TextView text_value = (TextView) view.findViewById(R.id.text_value);
                if (position == 1) {//ai color
                    AppValues.ChangeAiColor();
                    String value = AppValues.pref.color_ai == ChessKernel.white ? getString(R.string.chess_white) : getString(R.string.cheess_black);
                    if( AppValues.pref.color_ai == ChessKernel.white) text_value.setTextColor(Color.WHITE);
                    else text_value.setTextColor(Color.BLACK);
                    text_value.setText(value);
                }
                if (position == 2 || position == 3) {//set player name
                    final EditText edit_name = new EditText(getActivity());
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.input_name)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String name = edit_name.getText().toString();
                                    if (name == null || name == "") return;
                                    else {
                                        AppValues.SetPlayerName(position - 2, name);
                                        text_value.setText(name);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create();
                    dialog.setView(edit_name);
                    dialog.show();
                }
            }
        });
        return rootview;
    }


}
