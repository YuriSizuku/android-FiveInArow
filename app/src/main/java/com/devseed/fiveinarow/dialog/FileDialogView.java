package com.devseed.fiveinarow.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.activity.MainActivity;
import com.devseed.fiveinarow.data.ChessIO;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by misaki on 2015/12/3.
 */
public class FileDialogView extends Activity{

    Context context;
    LayoutInflater inflater;
    View view_filedialog;
    EditText edit_filepath;
    ListView list_filedialog;
    SimpleAdapter adapter_filedialog;
    int default_count;
    private OnCustomPathListener listener_custompath=null;

    ArrayList<HashMap<String,Object>> itemList;

    /**
     * param  n 为提供几个默认选项
     * item_titles 默认选项名称
     * item_valus 默认选项值
     *之后可以用setOnCustomPathListener来监听按自定义路径时候的结果（若通过新的activity获取，则在result中要调用setPath更新视图）
     * 只是简单封装了一下view，必须用fromview获得视图
     */
    public FileDialogView(Context context,int n,String[] item_titles,String[] item_values){//n为默认选项数量
        this(context);
        default_count=n;
        itemList=new ArrayList<HashMap<String,Object>>();
        for(int i=0;i<item_titles.length;i++){
            HashMap<String,Object>  map=new HashMap<String, Object>();
            map.put("item_titles",item_titles[i]);
            map.put("item_values",item_values[i]);
            itemList.add(map);
        }
        iniView();
    }
    public FileDialogView(Context context) {
        super();
        this.context=context;
    }
    private View iniView(){
        inflater=LayoutInflater.from(context);
        adapter_filedialog=new SimpleAdapter(context,itemList,
                R.layout.list_filedialog,
                new String[]{"item_titles","item_values"},
                new int[]{R.id.text_title,R.id.text_value});
        view_filedialog=inflater.inflate(R.layout.dialog_file, null);
        list_filedialog=(ListView)view_filedialog.findViewById(R.id.list_filedialog);
        edit_filepath=(EditText)view_filedialog.findViewById(R.id.edit_filepath);
        list_filedialog.setAdapter(adapter_filedialog);
        list_filedialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < default_count) {
                    HashMap<String, Object> map = itemList.get(position);
                    String text = (String) map.get("item_values");
                    edit_filepath.setText(text);
                } else if (position == default_count) {
                    if (listener_custompath != null) {//此处不能intent到其他activity
                        listener_custompath.onClick();
                    }
                }
            }
        });
        return view_filedialog;
    }
    public View formView(){
        if(view_filedialog==null) return iniView();
        else return view_filedialog;
    }
    public String getPath(){return edit_filepath.getText().toString();}
    public void setPath(String path){edit_filepath.setText(path);}
    public View getView(){return view_filedialog;}
    public FileDialogView setOnCustomPathListener(OnCustomPathListener listener_custompath){
        this.listener_custompath=listener_custompath;
        return this;
    }
    public interface OnCustomPathListener{
        void onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);//无用
    }
}
