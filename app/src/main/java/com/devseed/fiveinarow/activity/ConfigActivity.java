package com.devseed.fiveinarow.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.fragment.Config1Fragment;
import com.devseed.fiveinarow.fragment.Config2Fragment;
import com.devseed.fiveinarow.fragment.Config3Fragment;
import com.devseed.fiveinarow.fragment.ConfigFragment;

import java.nio.LongBuffer;

public class ConfigActivity extends Activity implements ConfigFragment.Config0ClickListener{

    Button button_back;
    TextView text_title;
    Fragment[] fragments;
    String[] titles;
    int config_state=0;//标记当前fragment在什么位置
    public void SetTitle(){
        SetTitle(config_state);
    }
    public void SetTitle(int i){
        String config_name="";
        if(i!=0) config_name=titles[i];
        text_title.setText(titles[0] + "/" + config_name);
    }
    void iniFragment(){
        addFragment(0);
    }
    public void changeFragment(int i){
        config_state=i;
        SetTitle();
        getFragmentManager().beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .addToBackStack(null)
        .replace(R.id.frame_config, fragments[i]).commit();
        if(i==0) {
            getFragmentManager().popBackStack();//(压栈的是引用，弹栈弹出去后，视图不会显示数据)
        }
    }
    public void addFragment(int i){
        config_state=i;
        SetTitle();
        getFragmentManager().beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .add(R.id.frame_config, fragments[i])
        .addToBackStack(null)
        .commit();
    }
    public void removeFragment(int i){
        config_state=0;
        SetTitle();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .remove(fragments[i])
                .commit();
    }
    public void onConfig0click(int i){//
        changeFragment(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        text_title=(TextView)findViewById(R.id.text_title);
        button_back=(Button)findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onBackPressed();
            }
        });
       fragments= new Fragment[]{
               new ConfigFragment(),
               new Config1Fragment(),
               new Config2Fragment(),
               new Config3Fragment()};
        titles=new String[]{
                getString(R.string.config),
                getString(R.string.setting_basic),
                getString(R.string.setting_display),
                getString(R.string.setting_advance)};
      iniFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_config, menu);
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
    @Override
    protected void onPause(){
        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);
        super.onPause();
    }
    @Override
    public void onBackPressed(){
        changeFragment(0);//此处设计就2级菜单
        if(getFragmentManager().getBackStackEntryCount()==1)
            getFragmentManager().popBackStack();//回到第一个时候，栈中数是1，只有栈为0是在按才是退出
        super.onBackPressed();//不调用父类就无法返回
    }

    @Override
    protected void onDestroy() {
        AppValues.save_prefs();//结束时才保存配置信息更改
        super.onDestroy();
    }
}
