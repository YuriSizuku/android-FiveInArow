package com.devseed.fiveinarow.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.ChessIO;

import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;


/**
 * Created by misaki on 2015/11/30.
 */
public class FileDialog extends AlertDialog{//这个不做了，确定返回按钮还要自己做，改用FileDialogView了

    LayoutInflater inflater;
    View rootview;
    View view_filedialog;
    EditText edit_filepath;
    ListView list_filedialog;
    int default_count;

    ArrayList<HashMap<String,Object>> itemList;
    public FileDialog(Context context,int n,String[] item_titles,String[] item_values){//n为默认选项数量
        this(context);
        default_count=n;
        itemList=new ArrayList<HashMap<String,Object>>();
        for(int i=0;i<item_titles.length;i++){
            HashMap<String,Object>  map=new HashMap<String, Object>();
            map.put("item_titles",item_titles[i]);
            map.put("item_values",item_values[i]);
            itemList.add(map);
        }
    }
    protected FileDialog(Context context) {
        super(context);
        iniView();
    }
    public void iniView(){
        inflater=getLayoutInflater();
        SimpleAdapter adapter_filedialog=new SimpleAdapter(getContext(),itemList,
                R.layout.list_filedialog,
                new String[]{"item_titles","item_values"},
                new int[]{R.id.text_title,R.id.text_value});
        View view_filedialog=getLayoutInflater().inflate(R.layout.dialog_file, null);
        ListView list_filedialog=(ListView)view_filedialog.findViewById(R.id.list_filedialog);
        edit_filepath=(EditText)view_filedialog.findViewById(R.id.edit_filepath);
        list_filedialog.setAdapter(adapter_filedialog);
        list_filedialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<default_count) {
                    HashMap<String,Object> map=itemList.get(position);
                    String text= (String) map.get("item_values");
                    edit_filepath.setText(text);
                }
                else if(position==default_count){

                }
            }
        });
    }

}
