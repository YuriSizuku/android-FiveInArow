package com.devseed.fiveinarow.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.data.ChessIO;
import com.devseed.fiveinarow.dialog.FileDialogView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.bit.cs.myfileexplorer.FileExplorerFragmentContants;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class Config3Fragment extends Fragment {

    FileDialogView filedialog=null;
    public Config3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final String []item_title=new String[]{
                getString(R.string.menu_workpath),
                "......",
                "......",
                "......",
                getString(R.string.menu_reset)};
        final String []item_value=new String[]{
                AppValues.pref.workpath,
                "",
                "",
                "",
                getString(R.string.menu_reset_text),
                };
        ArrayList<HashMap<String,Object>> itemlist=new ArrayList<>();
        for(int i=0;i<item_title.length;i++){
            HashMap<String,Object> map=new HashMap<String, Object>();
            map.put("item_title",item_title[i]);
            map.put("item_value",item_value[i]);
            itemlist.add(map);
        }
        final SimpleAdapter adapter_config3=new SimpleAdapter(getActivity(),
                itemlist,R.layout.list_config1,
                new String[]{"item_title","item_value"},new int[]{R.id.text_title,R.id.text_value});
        View rootview=inflater.inflate(R.layout.fragment_config3, container, false);
        final ListView list_config3= (ListView) rootview.findViewById(R.id.list_config3);
        list_config3.setAdapter(adapter_config3);
        list_config3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TextView text_value = (TextView) view.findViewById(R.id.text_value);
                if (position == 0) {//workplace
                    final String[] item_titles = new String[]{
                            getString(R.string.default_path) ,
                            getString(R.string.sdcard_path),
                            getString(R.string.extsdcard_path),
                            getString(R.string.custom_path),};
                    final String[] item_values = new String[]{
                            getActivity().getExternalFilesDir(null).getPath()+"/",
                            Environment.getExternalStorageDirectory().toString() + "/"+getString(R.string.appinner_name)+"/",
                            "mnt/media_rw/extSdCard/"+getString(R.string.appinner_name)+"/",
                            "..."};
                    filedialog = new FileDialogView(getActivity(), 3, item_titles, item_values)
                            .setOnCustomPathListener(new FileDialogView.OnCustomPathListener() {
                                @Override
                                public void onClick() {
                                    Intent intent = ChessIO.MakeFileExplorerIntent(getActivity(), ExplorerMode.CHOOSE_DIRECTORY_SINGLE);
                                    startActivityForResult(intent, 3);
                                }
                            });
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String path = filedialog.getPath();
                                    int res = ChessIO.CheckStepLogFile(path);
                                    if (res != 4) {//4 dictionary
                                        if(res!=1){
                                            File file=new File(path);
                                            if(file.mkdirs()){
                                                AppValues.SetWorkpath(path);
                                                text_value.setText(path);
                                                return;
                                            }
                                        }
                                        Toast.makeText(getActivity(), R.string.error_invalid_path, Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        AppValues.SetWorkpath(path);
                                        text_value.setText(path);
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .setTitle(getString(R.string.select_workpath))
                            .create();
                    dialog.setView(filedialog.getView());
                    dialog.show();
                } else if (position == item_title.length - 1) {//reset
                    new AlertDialog.Builder(getActivity())
                            .setMessage(R.string.warning_reset)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AppValues.reset_prefs();
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), R.string.menu_reset_text, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            )
                            .setTitle(R.string.warning).show();
                }
            }
        });
        return rootview;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 3://load
                if(data!=null) {
                    String path=data.getStringExtra(FileExplorerFragmentContants.SELECTED_PATH);
                    filedialog.setPath(path);
                }
                break;
        }
    }
}
