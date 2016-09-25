package com.devseed.fiveinarow.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.devseed.fiveinarow.R;

public class AboutActivity extends Activity {

    EditText text_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        text_about= (EditText) findViewById(R.id.text_about);
        String text=getString(R.string.app_name)+"  ver"+getString(R.string.app_ver)+"\n\n"
                +getString(R.string.app_about)+"\n\n\n\n"+getString(R.string.app_info);
        text_about.setText(text);
        text_about.setKeyListener(null);
        text_about.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动条
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
}
