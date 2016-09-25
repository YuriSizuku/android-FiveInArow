package com.devseed.fiveinarow.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.dialog.FileDialogView;

import java.io.File;

/**
 * Created by misaki on 2015/12/1.
 */
public class Config2Adapter extends BaseAdapter{
    private LayoutInflater inflater;

    Context context;
    String itemShownum_title;
    String itemShownum_value;
    String zoommode_title;
    String zoommode_value;
    String itembg_title;
    int itembg_mode;
    String itembg_value;
    String[] itemcol_title=null;
    int[] itemcol_value=null;
    TextView item_title=null;
    TextView item_value=null;
    ImageView item_image=null;
    Bitmap item_bitmap=null;

    public int getColor(int i){
        return itemcol_value[i];
    }
    public void changeShowNum(){
        AppValues.ChangeShowNum();
        itemShownum_value=AppValues.pref.isShowStepNum==1 ? context.getString(R.string.yes) :context.getString(R.string.no);
        notifyDataSetChanged();
    }
    public void setColor(int index,int color){
        AppValues.SetColor(index, color);
        itemcol_value[index]=color;
        notifyDataSetChanged();
    }
    public <T> void setBg(int mode,T value){
        itembg_mode=mode;
        if(mode==2){
            itembg_value=(String)value;//检测可行之后再更新
            item_bitmap=null;
            item_bitmap=BitmapFactory.decodeFile(itembg_value);
        }
        else{
            AppValues.SetBg(mode,value);
        }
        notifyDataSetChanged();
    }
    public void setBgScale(int mode){
        AppValues.pref.bgscale=mode;
        if(mode==0) zoommode_value=context.getString(R.string.zoom_center);
        else zoommode_value=context.getString(R.string.zoom_stretching);
        notifyDataSetChanged();
    }
    public Config2Adapter(Context context){
        this.context=context;
        itemShownum_title=context.getString(R.string.showstep);
        itemShownum_value=AppValues.pref.isShowStepNum==1 ? context.getString(R.string.yes) :context.getString(R.string.no);
        itembg_title=context.getString(R.string.chessboard_bg);
        itembg_mode=AppValues.pref.bgmode;
        zoommode_title=context.getString(R.string.zoom_mode);
        setBgScale(AppValues.pref.bgscale);
        if(itembg_mode==2){
            itembg_value=AppValues.pref.bgsrc;
            if(item_bitmap==null)
                item_bitmap= BitmapFactory.decodeFile(itembg_value);
        }

        itemcol_title=new String[]{
                context.getString(R.string.col_chessw),//白棋子颜色
                context.getString(R.string.col_chessb),//黑棋子颜色
                context.getString(R.string.col_chesswg),//灰色白棋子颜色
                context.getString(R.string.col_chessbg),//灰色黑棋子颜色
                context.getString(R.string.col_bline),//棋盘线颜色
                context.getString(R.string.col_eline),//棋盘边界线颜色666600
                context.getString(R.string.col_winline),//五连珠时候绘图的颜色
                context.getString(R.string.col_str),//文字颜色
                context.getString(R.string.col_curflag ),//当前棋子标记的颜色
                context.getString(R.string.col_numw),//白棋子上数字的颜色
                context.getString(R.string.col_numb)//黑棋子上数字颜色
        };
        itemcol_value=new int[]{
                //棋盘线与棋子
                AppValues.pref.col_chessw,//白棋子颜色
                AppValues.pref.col_chessb,//黑棋子颜色
                AppValues.pref.col_chesswg,//灰色白棋子颜色
                AppValues.pref.col_chessbg,//灰色黑棋子颜色
                AppValues.pref.col_bline,//棋盘线颜色
                AppValues.pref.col_eline,//棋盘边界线颜色666600
                AppValues.pref.col_winline,//五连珠时候绘图的颜色
                AppValues.pref.col_str,//文字颜色
                AppValues.pref.col_curflag ,//当前棋子标记的颜色
                AppValues.pref.col_numw,//白棋子上数字的颜色
                AppValues.pref.col_numb//黑棋子上数字颜色
        };
        context.getString(R.string.cheess_black);
    }
    @Override
    public int getCount() {
        return itemcol_title.length+2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout rootview=null;//(LinearLayout)convertView;//为了防止显示错误，有不用viewholder做法
        if(rootview==null){
            if(inflater==null)
                inflater=LayoutInflater.from(parent.getContext());
            if(position==0){//shownum
                rootview=(LinearLayout)inflater.inflate(R.layout.list_config1,null);
                TextView item_title= (TextView) rootview.findViewById(R.id.text_title);
                TextView item_value= (TextView) rootview.findViewById(R.id.text_value);
                item_title.setText(itemShownum_title);
                item_value.setText(itemShownum_value);
            }
            else if(position==1){
                rootview=(LinearLayout)inflater.inflate(R.layout.list_config2_1, null);
                item_title= (TextView) rootview.findViewById(R.id.text_title);
                item_value= (TextView) rootview.findViewById(R.id.text_value);
                item_image= (ImageView) rootview.findViewById(R.id.pic_image);
                item_title.setText(itembg_title);
                if(itembg_mode==0){//纯色
                    itembg_value=String.format("%#08x ",AppValues.pref.bgcol);
                    //itembg_value="#"+Integer.toHexString(AppValues.pref.bgcol);
                    item_image.setBackgroundColor(AppValues.pref.bgcol);
                }
                else if(itembg_mode==1){//资源
                    itembg_value="res("+Integer.toString(AppValues.pref.bgres)+")";
                    item_image.setImageDrawable(inflater.getContext().getResources().getDrawable(AppValues.pref.bgres));
                }
                else if(itembg_mode==2){//图片
                    if(item_bitmap==null) {//图片无效
                         Toast.makeText(context,R.string.error_invalid_path,Toast.LENGTH_SHORT).show();
                        item_image.setImageDrawable(inflater.getContext().getResources().getDrawable(android.R.drawable.ic_delete));
                    }
                    else{
                        item_image.setImageBitmap(item_bitmap);
                        AppValues.SetBg(2,itembg_value);//有效才更新
                    }
                }
                item_value.setText(itembg_value);
            }
            else if(position==2){//zoom mode
                rootview=(LinearLayout)inflater.inflate(R.layout.list_config1,null);
                TextView item_title= (TextView) rootview.findViewById(R.id.text_title);
                TextView item_value= (TextView) rootview.findViewById(R.id.text_value);
                item_title.setText(zoommode_title);
                item_value.setText(zoommode_value);
            }
            else {
                rootview=(LinearLayout)inflater.inflate(R.layout.list_config2_2, null);
                item_title= (TextView) rootview.findViewById(R.id.text_title);
                item_value= (TextView) rootview.findViewById(R.id.text_value);
                item_image= (ImageView) rootview.findViewById(R.id.col_image);
                item_title.setText(itemcol_title[position-3]);
                item_value.setText(String.format("#%08x ",itemcol_value[position-3]));
                //item_value.setText("#"+Integer.toHexString(itemcol_value[position-2]));
                item_image.setBackgroundColor(itemcol_value[position-3]);
            }
        }
        return rootview;
    }
}
