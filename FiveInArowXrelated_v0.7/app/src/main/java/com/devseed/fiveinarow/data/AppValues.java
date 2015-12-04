package com.devseed.fiveinarow.data;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Environment;
import com.devseed.fiveinarow.R;

import com.devseed.fiveinarow.ChessKernel;

import java.io.File;

/**
 * Created by misaki on 2015/11/25.
 */
public class AppValues{// 各种属性值的类

        static Context context;
        public static void SetLanguage(int num){
            pref.language=num;
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            share_editor.putInt("language", num);
            share_editor.commit();
        }
        public static void UpdateSteplogPath(String path){
        if(path==null) return;
        pref.last_steplogpath=path;
        SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
        SharedPreferences.Editor share_editor=share.edit();
        share_editor.putString("last_steplogpath", path);
        share_editor.commit();
    }
        public static void SetPlayerName(int num,String name){
            String[] player=new String[]{"player1","player2"};
            if(num==0) pref.player_1=name;
            else if(num==1) pref.player_2=name;
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            share_editor.putString(player[num], name);
            share_editor.commit();
        }
        public static void ChangeAiColor(){
            pref.color_ai=pref.color_ai==ChessKernel.white ? ChessKernel.black:ChessKernel.white;
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            share_editor.putInt("language", pref.color_ai);
            share_editor.commit();
        }
        public static void ChangeShowNum(){
            pref.isShowStepNum=pref.isShowStepNum==0 ? 1:0;
        }
        public static void SetColor(int index,int color){
            if (index==0)                 AppValues.pref.col_chessw=color;//白棋子颜色
            else if (index==1)                 AppValues.pref.col_chessb=color;//黑棋子颜色
            else if (index==2)                 AppValues.pref.col_chesswg=color;//灰色白棋子颜色
            else if (index==3)                 AppValues.pref.col_chessbg=color;//灰色黑棋子颜色
            else if (index==4)                 AppValues.pref.col_bline=color;//棋盘线颜色
            else if (index==5)                 AppValues.pref.col_eline=color;//棋盘边界线颜色666600
            else if (index==6)                 AppValues.pref.col_winline=color;//五连珠时候绘图的颜色
            else if (index==7)                 AppValues.pref.col_str=color;//文字颜色
            else if (index==8)                 AppValues.pref.col_curflag =color;//当前棋子标记的颜色
            else if (index==9)                 AppValues.pref.col_numw=color;//白棋子上数字的颜色
            else if (index==10)                AppValues.pref.col_numb=color;//黑棋子上数字颜色
        }
        public static <T> void SetBg(int mode,T value){
            if (mode>2) return;
            pref.bgmode=mode;
            switch (mode){
                case 0:
                    pref.bgcol=(Integer)value;
                    break;
                case 1:
                    pref.bgres=(Integer)value;
                    break;
                case 2:
                    pref.bgsrc=(String)value;
                    break;
            }
        }
        public static void SetWorkpath(String path){
            if(path==null){
                return;
            }
            else{
                pref.workpath=path;
            }
        }

        public static class pref {
            public static String workpath;
            public static String last_steplogpath="";
            public static int row=15, column=15;
            public static char color_player= ChessKernel.black;
            public static char color_ai=ChessKernel.white;
            public static int language=2;//0跟随系统,1英文，2日文，3中文
            public static String ai;//暂时只有くおん
            public static String player_1="player1";
            public static String player_2="player2";
            public static int isShowStepNum=0;
            public static int isSaveStepLog=1;
            public static int count_syslog=-1;//最多保存的记录数，-1表示不受限制

            //棋盘线与棋子
            public static int col_chessw= Color.WHITE;//白棋子颜色
            public static int col_chessb=Color.BLACK;//黑棋子颜色
            public static int col_chesswg=Color.argb(0x88,0xff,0xff,0xff);//灰色白棋子颜色
            public static int col_chessbg=Color.argb(0x44, 0, 0, 0);//灰色黑棋子颜色
            public static int col_bline=Color.argb(0x77,0x33, 0x33, 0x99);//棋盘线颜色
            public static int col_eline=Color.rgb(0x66,0x66,0x00);//棋盘边界线颜色666600
            public static int col_winline=Color.rgb(0xfb,0x1b,0xff);//五连珠时候绘图的颜色
            public static int col_str=Color.RED;//文字颜色
            public static int col_curflag =Color.rgb(0xff,0xce,0xe9);//当前棋子标记的颜色
            public static int col_numw=Color.rgb(0xff,0x10,0x39);//白棋子上数字的颜色
            public static int col_numb=Color.rgb(0xff,0xff,0xff);//黑棋子上数字颜色

            //棋盘背景
            public static int bgmode=1;//0为用纯色绘图，1为用资源图片，2为用自定义图片
            public static int bgcol=Color.rgb(0xcc,0xff,0x99);//当bgmode=0时，棋盘背景色
            public static int bgres=R.mipmap.bg2;//bgmode=1时，mipmap的资源id
            public static int bgscale=0;//当bgmode=2时，0为居中显示，1为拉伸显示，2为自定义区域（暂时不考虑）
            public static String bgsrc="/storage/emulated/0/1.jpg";//当bgmode=2时,bg的绝对路径
        }
        public static class res {
            public static String name_ai_1 = "クオン" ;
            public static String version;
            public static String appname;
            public static String steplog_ext;//后缀名
            public static String steplog_path;//棋盘保存路径
            public static String syslog_path;//系统自动保存日志路径
            public static String res_path;//资源路径
            public static String autosave_name;//自动保存棋盘名称
            public static int []bg_res;//资源bg数组
        }
        public AppValues(Context context) {
            this.context=context;
            ini_res();
            ini_pref();
        }
        void ini_pref(){
            pref.ai=res.name_ai_1;
            String c= Environment.getExternalStorageState();
            pref.workpath=context.getExternalFilesDir(null).getPath();//"/storage/emulated/0/Android/data/com.devseed.fiveinarow/files
            boolean isFirst;
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            isFirst=share.getBoolean("isFirst",true);
            if(isFirst){//第一次加载
                share_editor.putBoolean("isFirst", false);
                save_prefs();
                share_editor.commit();
            }
            else{
                load_prefs();
            }
        }
        void ini_res(){
            res.version= context.getResources().getString(R.string.app_ver);
            res.appname=context.getString(R.string.app_name);
            res.steplog_ext=context.getString(R.string.steplog_ext);
            res.steplog_path=context.getString(R.string.steplog_path);
            res.syslog_path=context.getString(R.string.syslog_path);
            res.autosave_name=context.getString(R.string.autosave_name);
            res.res_path=context.getString(R.string.res_path);
            res.bg_res=new int[]{R.mipmap.bg2,R.mipmap.bg3};
        }

        public static void save_prefs(){
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            share_editor.putString("workpath", pref.workpath);
            share_editor.putString("player1", pref.player_1);
            share_editor.putString("player2", pref.player_2);
            share_editor.putString("ai", "クオン");
            share_editor.putInt("language",pref.language);
            share_editor.putInt("color_player", pref.color_player);
            share_editor.putInt("color_ai",pref.color_ai);
            share_editor.putInt("row",pref.row);
            share_editor.putInt("column",pref.column);
            share_editor.putInt("isShowStepNum",pref.isShowStepNum);
            //棋盘线与棋子
            share_editor.putInt("col_chessw",pref.col_chessw);//白棋子颜色
            share_editor.putInt("col_chessb",pref.col_chessb);//黑棋子颜色
            share_editor.putInt("col_chesswg",pref.col_chesswg);//灰色白棋子颜色
            share_editor.putInt("col_chessbg",pref.col_chessbg);//灰色黑棋子颜色
            share_editor.putInt("col_bline",pref.col_bline);//棋盘线颜色
            share_editor.putInt("col_eline",pref.col_eline);//棋盘边界线颜色666600
            share_editor.putInt("col_winline",pref.col_winline);//五连珠时候绘图的颜色
            share_editor.putInt("col_str",pref.col_str);//文字颜色
            share_editor.putInt("col_curflag ",pref.col_curflag );//当前棋子标记的颜色
            share_editor.putInt("col_numw",pref.col_numw);//白棋子上数字的颜色
            share_editor.putInt("col_numb",pref.col_numb);//黑棋子上数字颜色
            //棋盘背景
            share_editor.putInt("bgmode",pref.bgmode);//0为用纯色绘图，1为用资源图片，2为用自定义图片
            share_editor.putInt("bgcol",pref.bgcol);//当m_bgmode=0时，棋盘背景色
            share_editor.putInt("bgres",pref.bgres);//m_bgmode=1时，mipmap的资源id
            share_editor.putInt("bgscale",pref.bgscale);//当m_bgmode=2时，0为居中显示，1为拉伸显示，2为自定义区域（暂时不考虑）
            share_editor.putString("bgsrc", pref.bgsrc);//当m_bgmode=2时,bg的绝对路径
            share_editor.commit();
        }
        public static void load_prefs(){
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            pref.workpath=share.getString("workpath",pref.workpath);
            pref.player_1=share.getString("player1","player1");
            pref.player_2=share.getString("player2","player2");
            pref.ai=share.getString("ai", "クオン");
            pref.language=share.getInt("language", pref.language);
            pref.color_player=(char)share.getInt("color_player",ChessKernel.black);
            pref.color_ai=(char)share.getInt("color_ai",ChessKernel.white);
            pref.row=share.getInt("row",15);
            pref.column=share.getInt("column",15);
            pref.isShowStepNum=share.getInt("isShowStepNum", 0);
            pref.last_steplogpath=share.getString("last_steplogpath", "");
            //棋盘线与棋子
            pref.col_chessw=share.getInt("col_chessw",Color.WHITE);//白棋子颜色
            pref.col_chessb=share.getInt("col_chessb",Color.BLACK);//黑棋子颜色
            pref.col_chesswg=share.getInt("col_chesswg",Color.argb(0x88,0xff,0xff,0xff));//灰色白棋子颜色
            pref.col_chessbg=share.getInt("col_chessbg",Color.argb(0x44, 0, 0, 0));//灰色黑棋子颜色
            pref.col_bline=share.getInt("col_bline",Color.argb(0x77, 0x33, 0x33, 0x99));//棋盘线颜色
            pref.col_eline=share.getInt("col_eline",Color.rgb(0x66,0x66,0x00));//棋盘边界线颜色666600
            pref.col_winline=share.getInt("col_winline",Color.rgb(0xfb,0x1b,0xff));//五连珠时候绘图的颜色
            pref.col_str=share.getInt("col_str",Color.RED);//文字颜色
            pref.col_curflag =share.getInt("col_curflag ",Color.rgb(0xff,0xce,0xe9));//当前棋子标记的颜色
            pref.col_numw=share.getInt("col_numw",Color.rgb(0xff,0x10,0x39));//白棋子上数字的颜色
            pref.col_numb=share.getInt("col_numb",Color.rgb(0xff,0xff,0xff));//黑棋子上数字颜色
            //棋盘背景
            pref.bgmode=share.getInt("bgmode",1);//0为用纯色绘图，1为用资源图片，2为用自定义图片
            pref.bgcol=share.getInt("bgcol",Color.rgb(0xcc,0xff,0x99));//当m_bgmode=0时，棋盘背景色
            pref.bgres=share.getInt("bgres",R.mipmap.bg2);//m_bgmode=1时，mipmap的资源id
            pref.bgscale=share.getInt("bgscale",1);//当m_bgmode=2时，0为居中显示，1为拉伸显示，2为自定义区域（暂时不考虑）
            pref.bgsrc=share.getString("bgsrc","/storage/emulated/0/1.jpg");//当m_bgmode=2时,bg的绝对路径
        }
        public static void reset_prefs(){
            SharedPreferences share=context.getSharedPreferences("pref", Context.MODE_PRIVATE);;
            SharedPreferences.Editor share_editor=share.edit();
            share_editor.putBoolean("isFirst",true);
            share_editor.commit();
        }
}
