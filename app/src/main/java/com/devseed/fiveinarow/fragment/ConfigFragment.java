package com.devseed.fiveinarow.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.devseed.fiveinarow.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigFragment extends Fragment {

    View rootview;
    ListView config_list;
    ArrayAdapter<String> config_adapter;
    public ConfigFragment() {
        // Required empty public constructor

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_config, container, false);

        String []configName= new String[]{//先加载布局在加载资源，所以不要放外面
                getString(R.string.setting_basic),
                getString(R.string.setting_display),
                getString(R.string.setting_advance)};
        //config_adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_expandable_list_item_1);
        config_adapter=new ArrayAdapter<String>(this.getActivity(),R.layout.list_config,R.id.text);
        config_adapter.addAll(configName);
        config_list =(ListView) (rootview.findViewById(R.id.list_config));
        config_list.setAdapter(config_adapter);
        config_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Config0ClickListener)getActivity()).onConfig0click(position+1);//通过接口回调主函数
            }
        });
        return rootview;
    }
    public interface Config0ClickListener{
        public void onConfig0click(int i);
    }
}
