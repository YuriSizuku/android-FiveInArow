package com.devseed.fiveinarow.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.adapter.Config2Adapter;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.data.ChessIO;
import com.devseed.fiveinarow.dialog.ColorDialog;
import com.devseed.fiveinarow.dialog.FileDialog;
import com.devseed.fiveinarow.dialog.FileDialogView;

import org.w3c.dom.Text;

import cn.edu.bit.cs.myfileexplorer.FileExplorerFragmentContants;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;

/**
 * A simple {@link Fragment} subclass.
 */
public class Config2Fragment extends Fragment {

    Config2Adapter adapter_config2;
    FileDialogView filedialog=null;
    public Config2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adapter_config2=new Config2Adapter(getActivity());
        
        View rootview=inflater.inflate(R.layout.fragment_config2, container, false);
        ListView list_config2= (ListView) rootview.findViewById(R.id.list_config2);
        list_config2.setAdapter(adapter_config2);
        list_config2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    adapter_config2.changeShowNum();
                } else if (position == 1) {
                    final String[] items = new String[]{
                            getString(R.string.pure_color),
                            getString(R.string.inner_picture),
                            getString(R.string.outer_picture),};
                    int checked = AppValues.pref.bgmode;
                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).setSingleChoiceItems(items, checked, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                final ColorDialog dialog2 = new ColorDialog(getActivity(), AppValues.pref.bgcol);
                                dialog2.setClickListener(new ColorDialog.ClickListenerInterface() {
                                    @Override
                                    public void onConfirmClick() {
                                        adapter_config2.setBg(0, dialog2.getColor());
                                    }
                                }).show();
                            } else if (which == 1) {
                                final String[] items = new String[]{R.mipmap.bg1+"", R.mipmap.bg2+"", R.mipmap.bg3+""};
                                int checked = 0;
                                for (int i = 0; i < items.length; i++) {
                                    if (Integer.parseInt(items[i]) == AppValues.pref.bgres) {
                                        checked = i;
                                        break;
                                    }
                                }
                                new AlertDialog.Builder(getActivity()).setSingleChoiceItems(items, checked, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        adapter_config2.setBg(1,Integer.parseInt(items[which]));
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else if (which == 2) {
                                final String[] item_titles=new String[]{getString(R.string.default_path)+"1",
                                        getString(R.string.default_path)+"2",
                                        getString(R.string.custom_path),};
                                final String[] item_values=new String[]{AppValues.pref.workpath+"/"+AppValues.res.res_path+"/"+"fiarbg.jpg",
                                        Environment.getExternalStorageDirectory().toString()+"/"+"fiarbg.jpg",
                                        "..."};
                              filedialog=new FileDialogView(getActivity(),2,item_titles,item_values)
                                        .setOnCustomPathListener(new FileDialogView.OnCustomPathListener() {
                                            @Override
                                            public void onClick() {
                                                Intent intent= ChessIO.MakeFileExplorerIntent(getActivity(), ExplorerMode.CHOOSE_FILE_SINGLE,getString(R.string.select_bgpic));
                                                startActivityForResult(intent, 3);
                                            }
                                        });
                                AlertDialog dialog2=new AlertDialog.Builder(getActivity())
                                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                String path=filedialog.getPath();
                                                int res=ChessIO.CheckStepLogFile(path);
                                                if(res!=0 && res!=3) {
                                                    Toast.makeText(getActivity(), R.string.error_invalid_path, Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    adapter_config2.setBg(2,path);
                                                }
                                            }
                                        })
                                        .setNegativeButton(R.string.cancel,null)
                                        .setTitle(getString(R.string.select_bgpic))
                                        .create();
                                dialog2.setView(filedialog.getView());
                                dialog2.show();
                            }
                            dialog.dismiss();
                        }
                    }).show();
                } else if (position == 2) {
                    String[] items = new String[]{getString(R.string.zoom_center), getString(R.string.zoom_stretching)};
                    int checked = AppValues.pref.bgscale;
                    new AlertDialog.Builder(getActivity()).setSingleChoiceItems(items, checked, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            adapter_config2.setBgScale(which);
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    final ColorDialog dialog = new ColorDialog(getActivity(), adapter_config2.getColor(position - 3));
                    dialog.setClickListener(new ColorDialog.ClickListenerInterface() {
                        @Override
                        public void onConfirmClick() {
                            int color = dialog.getColor();
                            AppValues.SetColor(position - 3, color);
                            adapter_config2.setColor(position - 3, color);
                        }
                    }).show();
                    //dialog.setCanceledOnTouchOutside(false);
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
