package com.devseed.fiveinarow.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.data.ChessIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import cn.edu.bit.cs.myfileexplorer.FileExplorerFragmentContants;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;


public class MainActivity extends Activity {

    public Bundle s;
    EditText edit_filepath=null;
    public void iniLanguage(){
        Configuration config = getResources().getConfiguration();//获取系统的配置
        switch (AppValues.pref.language){
            case 0://system
                config.locale=Locale.getDefault();
                break;
            case 1://english
                config.locale=Locale.ENGLISH;
                break;
            case 2://janpaness
                config.locale=Locale.JAPANESE;
                break;
            case 3://chinese
                config.locale = Locale.CHINA;
                break;
        }
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());//更新配置
    }
    public void LoadStep_asci(String path){
        if(ChessIO.CheckStepLogFile(path)!=0){
            Toast.makeText(this,getString(R.string.error_invalid_path),Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=new Intent();
        intent.putExtra("isLoad", 1);
        intent.putExtra("fpath", path);
        intent.setClass(this, ChessGameActivity.class);
        startActivityForResult(intent,3);
    }
    public void onStartGame(int mode){//0与人，1与ai
        Intent i=new Intent();
        //只是传最基本的信息，其余附加信息在AppValue传
        if(mode==0)
            i.putExtra("color_ai", ChessKernel.empty);
        else
            i.putExtra("color_ai", AppValues.pref.color_ai);
        i.putExtra("color_player", AppValues.pref.color_player);
        i.putExtra("row",AppValues.pref.row);
        i.putExtra("column",AppValues.pref.column);
        i.putExtra("isLoad", 0);
        i.putExtra("isShowStepNum",AppValues.pref.isShowStepNum);
        i.setClass(MainActivity.this, ChessGameActivity.class);
        startActivityForResult(i, mode);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void onStartNetWork(){
        Intent intent=new Intent(MainActivity.this,NetworkActivity.class);
        startActivityForResult(intent,2);
    }
    public void onChangeLanguage(){
        String [] array_language={getString(R.string.follow_system),"English","日本語","简体中文"};
       new AlertDialog.Builder(this)
               .setTitle(getResources().getString(R.string.set_language))
               .setSingleChoiceItems(array_language, AppValues.pref.language, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       AppValues.SetLanguage(which);
                       iniLanguage();
                       dialog.dismiss();
                       onCreate(s);
                   }
               })
               .show();
    }
    public void onSetConfig(){
        Intent i=new Intent();
        i.setClass(MainActivity.this, ConfigActivity.class);
        startActivityForResult(i, 4);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    public void onLoad(){
        final String[] item_title=new String[]{getString(R.string.default_path),
                                                getString(R.string.last_steplog),
                                                getString(R.string.autosave_path),
                                                getString(R.string.custom_path),};
        final String[] item_value=new String[]{Environment.getExternalStorageDirectory().toString()+"/"+"steplog.cbs",
                                                AppValues.pref.last_steplogpath,
                                                AppValues.pref.workpath+"/"+AppValues.res.syslog_path+"/"+AppValues.res.autosave_name+AppValues.res.steplog_ext,
                                                "..."};
        ArrayList<HashMap<String,Object>> itemList=new ArrayList<HashMap<String,Object>>();
        for(int i=0;i<item_title.length;i++){
           HashMap<String,Object>  map=new HashMap<String, Object>();
            map.put("item_title",item_title[i]);
            map.put("item_value",item_value[i]);
            itemList.add(map);
        }
        SimpleAdapter adapter_filedialog=new SimpleAdapter(this,itemList,
                                                            R.layout.list_filedialog,
                                                            new String[]{"item_title","item_value"},
                                                            new int[]{R.id.text_title,R.id.text_value});
        View view_filedialog=getLayoutInflater().inflate(R.layout.dialog_file, null);
        ListView list_filedialog=(ListView)view_filedialog.findViewById(R.id.list_filedialog);
        edit_filepath=(EditText)view_filedialog.findViewById(R.id.edit_filepath);
        list_filedialog.setAdapter(adapter_filedialog);
        list_filedialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<3) edit_filepath.setText(item_value[position]);
                else if(position==3){
                    Intent intent=ChessIO.MakeFileExplorerIntent(MainActivity.this, ExplorerMode.CHOOSE_FILE_SINGLE);
                    startActivityForResult(intent, 3);
                }
            }
        });
        AlertDialog file_dialog= new AlertDialog.Builder(this)//由于项目不定，最终还是不打算重写类了
                .setTitle(getString(R.string.select_steplog))
                .setNegativeButton(getString(R.string.cancel), null)//必须提前声明
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = edit_filepath.getText().toString();
                        LoadStep_asci(path);
                        edit_filepath=null;
                    }
                })
                .create();
        file_dialog.setCanceledOnTouchOutside(false);//点击对话框外不退出
        file_dialog.setView(view_filedialog);
        file_dialog.show();
    }
    public void onViewlog(){
        Intent intent=new Intent(MainActivity.this,DbViewActivity.class);
        startActivityForResult(intent,6);
    }
    public void onAbout(){
        Intent intent=new Intent(MainActivity.this,AboutActivity.class);
        startActivityForResult(intent,7);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s=savedInstanceState;
        new AppValues(this.getApplicationContext());
        iniLanguage();
        setContentView(R.layout.activity_main);
        //下方copyright生成,version
        TextView text_copyright=(TextView)findViewById(R.id.text_copyright);
        text_copyright.setText(String.format(getResources().getString(R.string.copyright),AppValues.res.version));
        TextView text_version=(TextView)findViewById(R.id.text_version);
        text_version.setText("---ver"+AppValues.res.version);
        //菜单gridview生成
        String[] menuName={
                getString(R.string.start_player),getString(R.string.start_ai),getString(R.string.network),getString(R.string.load_asci),
                getString(R.string.config),getString(R.string.language),getString(R.string.viewlog),getString(R.string.about),
        };
        int [] menuImage={
                R.mipmap.p1,R.mipmap.p2,R.mipmap.p3, R.mipmap.p4,
                R.mipmap.p5, R.mipmap.p6, R.mipmap.p7, R.mipmap.p8
        };
        ArrayList<HashMap<String,Object>> menuList=new  ArrayList<HashMap<String,Object>>();
        for(int i=0;i<8;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("menuImage",menuImage[i]);
            map.put("menuName",menuName[i]);
            menuList.add(map);
        }
        SimpleAdapter adapter=new SimpleAdapter(this,menuList,
                R.layout.grid_mainmenu,
                new String[]{"menuImage","menuName"},
                new int[]{R.id.menu_image,R.id.menu_text});
        GridView gridView=(GridView)findViewById(R.id.grid_mainMenu);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0://player
                        onStartGame(0);
                        break;
                    case 1://ai
                        onStartGame(1);
                        break;
                    case 2://network
                        onStartNetWork();
                        break;
                    case 3://load
                        onLoad();
                        break;
                    case 4://config
                        onSetConfig();
                        break;
                    case 5://language
                        onChangeLanguage();
                        break;
                    case 6://view
                        onViewlog();
                        break;
                    case 7://about
                        onAbout();
                        break;
                }
            }
        });
        Intent intent=getIntent();
        String action=intent.getAction();
        if(intent.ACTION_VIEW.equals(action)){//若是直接主函数，则是action.main
            Log.d("intent.getDataString()", intent.getDataString());
            String fpath=intent.getDataString().replace("file://", "");
            LoadStep_asci(fpath);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // 第一个参数为请求码，即调用startActivityForResult()传递过去的值
    // 第二个参数为结果码，结果码用于标识返回数据来自哪个新Activity
        //Log.d("onaresult----", "requestCode=" + requestCode + "*********result=" + resultCode);
       switch (requestCode){
           case 3://load
               if(data!=null) {
                   String path=data.getStringExtra(FileExplorerFragmentContants.SELECTED_PATH);
                   if(edit_filepath!=null){
                       edit_filepath.setText(path);
                   }
               }
               break;
       }
    }
}
