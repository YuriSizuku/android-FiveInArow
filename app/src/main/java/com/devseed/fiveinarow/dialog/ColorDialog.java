package com.devseed.fiveinarow.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devseed.fiveinarow.R;

import java.util.zip.Inflater;

import cn.edu.bit.cs.utils.StringUtils;

/**
 * Created by misaki on 2015/12/2.
 */
public class ColorDialog extends AlertDialog{
    LayoutInflater inflater;
    LinearLayout rootview;
    TextView text_colargb;
    EditText []edit_argb=new EditText[4];
    SeekBar []seek_argb=new SeekBar[4];
    int []id_edit=new int[]{R.id.edit_a,R.id.edit_r,R.id.edit_g,R.id.edit_b};
    int []id_seek=new int[]{R.id.seek_a,R.id.seek_r,R.id.seek_g,R.id.seek_b};
    ImageView img_col;
    Button button_confirm;
    Button button_cancel;
    OnClickListener listener_confirm;
    int color=0;//java没有unsigned int 否则0xffffff则为-1
    int[] color_argb=new int[]{0,0,0,0};
    final int MAX_VALUE=255;
    final int MIN_VALUE=0;
    private void col2argb(){
        String str=String.format("%08x",color);//直接弄有负号问题，干脆直接处理字符串吧，或者<0时候+0xff
        for(int i=0;i<4;i++){
            color_argb[i]=Integer.valueOf(str.substring(2*i,2*i+2),16);
        }
    }
    private void argb2col(){
        color=Color.argb(color_argb[0],color_argb[1],color_argb[2],color_argb[3]);
    }
    public ColorDialog(Context context,int color) {//最后参数传递结束按钮监听器
        this(context);
        this.color=color;
        this.listener_confirm=listener_confirm;
        col2argb();
        iniView();
    }
    protected ColorDialog(Context context) {
        super(context);//super必须在第一句
        iniView();
    }
    public void iniView(){
        //各控件获取
        inflater= LayoutInflater.from(getContext());
        rootview= (LinearLayout) inflater.inflate(R.layout.dialog_color, null);
        text_colargb= (TextView) rootview.findViewById(R.id.text_colorargb);
        img_col= (ImageView) rootview.findViewById(R.id.img_col);
        button_cancel= (Button) rootview.findViewById(R.id.button_cancel);
        button_confirm= (Button) rootview.findViewById(R.id.button_confirm);
        for(int i=0;i<id_edit.length;i++){
            edit_argb[i]=(EditText) rootview.findViewById(id_edit[i]);
            edit_argb[i].setText(String.valueOf(color_argb[i]));
            seek_argb[i]= (SeekBar) rootview.findViewById(id_seek[i]);
            seek_argb[i].setMax(MAX_VALUE);
            seek_argb[i].setProgress(color_argb[i]);
            img_col.setBackgroundColor(color);
        }

        //各控件适配
        for(int i=0;i<id_seek.length;i++){
            final int finalI = i;
            edit_argb[i].addTextChangedListener(new TextWatcher() {
                int number = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {//before,after是光标前后字符数，删除字符count=1，否则为0
                    //Log.d("before",start+",count="+count+",after="+after);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        number = Integer.parseInt(s.toString());
                        if (number > MAX_VALUE) {
                            s.clear();
                            s.append(String.valueOf(MAX_VALUE));
                            number = MAX_VALUE;
                        } else if (number < MIN_VALUE) {
                            s.clear();
                            s.append(String.valueOf(MIN_VALUE));
                            number = MIN_VALUE;
                        }
                    } else {
                        number=0;
                    }
                    seek_argb[finalI].setProgress(number);
                    color_argb[finalI]=number;
                    argb2col();
                    text_colargb.setText(String.format("#%08x", color));
                    img_col.setBackgroundColor(color);
                }
            });
            seek_argb[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    edit_argb[finalI].setText(String.valueOf(progress));
                    color_argb[finalI]=progress;
                    argb2col();
                    text_colargb.setText(String.format("#%08x", color));
                    img_col.setBackgroundColor(color);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        text_colargb.setText(String.format("#%08x",color));
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickinstance!=null) clickinstance.onConfirmClick();//调用接口实例
                dismiss();
            }
        });
        setView(rootview);
    }
    public void setColor(int color){
            iniView();
    }
    public int getColor(){
        return color;
    }
    public interface ClickListenerInterface{//提供给调用者的接口
        public void onConfirmClick();
    }
    public ColorDialog setClickListener(ClickListenerInterface clickinstance){
        this.clickinstance=clickinstance;
        return this;
    }
    private ClickListenerInterface clickinstance;
}
