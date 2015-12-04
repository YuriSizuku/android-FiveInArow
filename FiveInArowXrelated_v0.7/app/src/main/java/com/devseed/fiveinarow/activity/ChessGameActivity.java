package com.devseed.fiveinarow.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.adapter.ChessStepAdapter;
import com.devseed.fiveinarow.data.AppValues;
import com.devseed.fiveinarow.data.ChessIO;
import com.devseed.fiveinarow.view.ChessBoardView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import cn.edu.bit.cs.myfileexplorer.FileExplorerFragmentContants;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;

public class ChessGameActivity extends Activity {

    ChessKernel chess_kernal;//处理五子棋内部逻辑的类
    ChessBoardView chess_view;//棋盘界面
    ListView chessStep_list;//下棋记录
    TextView chessCount_text;//下棋个数
    TextView text_playmode;//下棋对象
    ChessStepAdapter chessStep_adapter;
    ChessIO chess_io;
    int sc_width,sc_height;//屏幕宽度和高度

    char color_current=ChessKernel.black;//当前棋子颜色(黑先白后)
    char color_player;//若color_player==empty 则2个ai互相下棋(暂不考虑)，不允许color_player与color_ai相等
    char color_ai;
    String name_ai= AppValues.pref.ai;
    int isEnd=0;
    int isShowStepNum =AppValues.pref.isShowStepNum;//是否棋子上有数字
    int isLoad=0;
    String fpath=null;
    String CurrentSavefpath=null;

    int row=15;
    int column=15;
    class ViewPos{
        int x;
        int y;

        char color;
        ViewPos(int x,int y,char color){SetPosInfo(x, y, color);}
        ViewPos(){}
        void SetPosInfo(int x,int y,char color){this.x=x;this.y=y;this.color=color;}
    }//虽然造成冗余，但是使用起来方便,坐标是棋盘视图坐标
    ViewPos viewpos_last=null;

    void DrawCurrentChess(int x,int y,char color){
        chess_view.DrawCurrentChess(x, y, color);//绘制当前的棋子标记
        if(isShowStepNum==1) chess_view.DrawNumOnChess(x,y,chess_kernal.steplog.count,color);
    }
    void DrawChess(int x,int y,int num,char color){
        chess_view.DrawChess(x, y, color);
        if(isShowStepNum==1) chess_view.DrawNumOnChess(x,y,num,color);
    }
    void DrawChessBoard(char ChessBoard[][]){//画当前棋盘(有或没有数字)
        chess_view.DrawChessBoard(ChessBoard);
        if(isShowStepNum==1) {//为了降低耦合性，信息量大，有chesskernal的东西，故不放到ChessBoardView类中
            int x,y,count;
            char color;
            count = chess_kernal.steplog.count;
            for (int i = 0; i < count; i++) {
                x=chess_kernal.steplog.step[i].y;
                y=chess_kernal.steplog.step[i].x;
                color=chess_kernal.steplog.step[i].color;
                chess_view.DrawNumOnChess(x,y,i+1,color);
            }
        }
    }
    void DrawGrayChessBoard(char ChessBoard[][]){
        int x,y,count,i;
        char color;
        if(chessStep_adapter.selectedPos<0) return;
        count = chess_kernal.steplog.count;
        chess_view.ClearChessBoard();
        for(i=0;i<count;i++){
            x=chess_kernal.steplog.step[i].y;
            y=chess_kernal.steplog.step[i].x;
            color=chess_kernal.steplog.step[i].color;
            if(i<=chessStep_adapter.selectedPos)
                chess_view.DrawChess(x,y,color);
            else chess_view.DrawGrayChess(x,y,color);
            if(isShowStepNum>0)  chess_view.DrawNumOnChess(x, y, i + 1, color);
        }
    }
    int PlaceChess(int x,int y){
        int res=chess_kernal.PlaceChess(y, x, color_current);//内部逻辑
        if(res<=0) return res;
        chessCount_text.setText(String.format(getResources().getString(R.string.stepcount), chess_kernal.steplog.count));//当前步骤数量
        chessStep_adapter.pushStep(y, x, color_current);//适配器数据
        chessStep_adapter.notifyDataSetChanged();//通知视图更新
        chessStep_list.setSelection(chessStep_adapter.getCount() - 1);//滚动到底部

        if(chessStep_adapter.selectedPos>=0){
            DrawChessBoard(chess_kernal.ChessBoard);
            chessStep_adapter.setSelected(-1);
        }
        DrawCurrentChess(x, y, color_current);//绘制当前的棋子标记
        if(viewpos_last==null)//上一步棋子
            viewpos_last=new ViewPos(x,y,color_current);
        else {
            DrawChess(viewpos_last.x,viewpos_last.y,chess_kernal.steplog.count-1,viewpos_last.color);//擦除上一个棋子的当前棋子标记
            viewpos_last.SetPosInfo(x, y, color_current);//更新上一部棋子
        }
        color_current=color_current==ChessKernel.black ? ChessKernel.white:ChessKernel.black;//改变当前棋子颜色
        return res;//返回内部逻辑返回值
    }
    int BacknChess(int n){
        int res;
        isEnd=0;
        res=chess_kernal.BackChess(n);
        DrawChessBoard(chess_kernal.ChessBoard);
        if(res<0) {
            color_current=ChessKernel.black;
            viewpos_last=null;
        }
        else {
            if(n%2==1) color_current=color_current==ChessKernel.black ? ChessKernel.white:ChessKernel.black;//改变当前棋子颜色
            if(chess_kernal.steplog.count<1) {
                viewpos_last=null;
                DrawChessBoard(chess_kernal.ChessBoard);
            }
            else {
                viewpos_last.x = chess_kernal.steplog.step[chess_kernal.steplog.count - 1].y;
                viewpos_last.y = chess_kernal.steplog.step[chess_kernal.steplog.count - 1].x;
                viewpos_last.color = chess_kernal.steplog.step[chess_kernal.steplog.count - 1].color;
                DrawCurrentChess(viewpos_last.x, viewpos_last.y, viewpos_last.color);
            }
        }
        chessStep_adapter.popnSteps(n);
        chessStep_adapter.notifyDataSetChanged();
        chessCount_text.setText(String.format(getResources().getString(R.string.stepcount), chess_kernal.steplog.count));
        return res;
    }
    int judge_onWin(int chess_x,int chess_y){//判断与分出胜负的时候
        if(chess_kernal.steplog.count >= chess_kernal.getRow() * chess_kernal.getColumn() && chess_kernal.JudgeWin()==0) {//平手
            onWin(ChessKernel.empty);
            return ChessKernel.empty;
        }
        char wincolor=chess_kernal.JudgeWin();
        if(wincolor!=0) {
            Log.d("WIN!", wincolor + "win~~");
            onWin(wincolor);
            chess_view.DrawWinLine(chess_kernal.ChessBoard,chess_x,chess_y);
            return wincolor;
        }
        return 0;
    }
    void onPlaceChess(Message msg){//下棋
        Bundle bundle= msg.getData();
        int chess_x=bundle.getInt("chess_x");
        int chess_y=bundle.getInt("chess_y");
        int res=PlaceChess(chess_x, chess_y);
        judge_onWin(chess_x,chess_y);
        if(res>0) {

            if(color_ai==color_current) {
                chess_kernal.tstep = chess_kernal.AI_kuon(color_current);
                PlaceChess(chess_kernal.tstep.y, chess_kernal.tstep.x);
                judge_onWin(chess_kernal.tstep.y,chess_kernal.tstep.x);
            }
        }

    }
    void onBackChess(Message msg){//长按悔棋
        if(color_ai==ChessKernel.empty){
            BacknChess(1);
        }
        else{
            BacknChess(2);
        }
    }
    void onSaveChess(){
        if (chess_kernal.steplog.count <=0){
            Toast.makeText(this,R.string.error_nochess,Toast.LENGTH_SHORT).show();
            return;
        }
        if(color_ai==ChessKernel.black) chess_io.SetPlayerMode(1, 0);
        else if(color_ai==ChessKernel.white) chess_io.SetPlayerMode(0,1);
        else chess_io.SetPlayerMode(0,0);
        try {
            if(CurrentSavefpath==null) {
                CurrentSavefpath = chess_io.SaveStep_xml();
                AppValues.UpdateSteplogPath(CurrentSavefpath);
            }
            else chess_io.SaveStep_xml(CurrentSavefpath);
            Toast.makeText(this, getString(R.string.success_save) + "\n" + CurrentSavefpath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,getResources().getString(R.string.error_save),Toast.LENGTH_SHORT).show();
        }
    }
    void onPreLoadChess(){
        if(chess_kernal.steplog.count <=0) {
            Intent intent=ChessIO.MakeFileExplorerIntent(ChessGameActivity.this, ExplorerMode.CHOOSE_FILE_SINGLE);
            startActivityForResult(intent, 3);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.warning))
                .setMessage(R.string.warning_reset)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRestartChess();
                        Intent intent=ChessIO.MakeFileExplorerIntent(ChessGameActivity.this, ExplorerMode.CHOOSE_FILE_SINGLE);
                        startActivityForResult(intent, 3);
                    }
                }).show();
    }
    void onLoadChess(String path){
        if(ChessIO.CheckStepLogFile(path)!=0){
            Toast.makeText(this,getString(R.string.error_invalid_path),Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            chess_io.LoadStep_xml(path);
            CurrentSavefpath=path;
            Toast.makeText(this,getString(R.string.success_load)+"\n"+path,Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.error_load), Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.error_load), Toast.LENGTH_SHORT).show();
        }
        isEnd=0;
        if(chess_io.isAi[0]==1){
            color_ai=ChessKernel.black;
            color_player=ChessKernel.white;
            text_playmode.setText(String.format(getResources().getString(R.string.playmode),name_ai,color_ai));
        }
        else if(chess_io.isAi[1]==1){
            color_ai=ChessKernel.white;
            color_player=ChessKernel.black;
            text_playmode.setText(String.format(getResources().getString(R.string.playmode),name_ai,color_ai));
        }
        else if(chess_io.isAi[0]==0 && chess_io.isAi[1]==0){
            color_player=ChessKernel.black;
            color_ai=ChessKernel.empty;
            text_playmode.setText(String.format(getResources().getString(R.string.playmode),"player",""));
        }
        chessStep_adapter.clear();
        int i;
        for(i=0;i<chess_kernal.steplog.count;i++){
            int x=chess_kernal.steplog.step[i].x;
            int y=chess_kernal.steplog.step[i].y;
            char color=chess_kernal.steplog.step[i].color;
            chessStep_adapter.pushStep(x,y,color);
        }
        int x=chess_kernal.steplog.step[i-1].x;
        int y=chess_kernal.steplog.step[i-1].y;
        char color=chess_kernal.steplog.step[i-1].color;
        viewpos_last=new ViewPos(y,x,color);
        color_current=viewpos_last.color==ChessKernel.black ? ChessKernel.white: ChessKernel.black;
        chessStep_adapter.notifyDataSetChanged();
        chessStep_list.setSelection(chessStep_adapter.getCount() - 1);//滚动到底部
        DrawChessBoard(chess_kernal.ChessBoard);
        if(chess_kernal.JudgeWin()!=0)
            isEnd=1;
        else  isEnd=0;
    }
    void onPreRestartChess(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.warning_reset)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRestartChess();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }
    void onRestartChess(){
        chess_kernal.clear();
        chessStep_adapter.clear();
        chessCount_text.setText((String.format(getResources().getString(R.string.stepcount), 0)));
        DrawChessBoard(chess_kernal.ChessBoard);
        color_current=ChessKernel.black;
        viewpos_last=null;
        if (color_ai == ChessKernel.black) {
            int x = column / 2;int y = row / 2;
            PlaceChess(x,y);
        }
        CurrentSavefpath=null;
        isEnd=0;
    }
    void onPreBackChess(){
        if(chessStep_adapter.selectedPos<0){
            return;
        }
        final int selected=chessStep_adapter.selectedPos;
        final int count=chessStep_adapter.getCount();
        final int n=count-selected-1;
        if(n<0) return;
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(String.format(getString(R.string.warning_popto),selected+1,n,selected+2,count))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chessStep_adapter.setSelected(-1);
                        BacknChess(n);
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .show();
    }
    void onPreReturnMainActivity(){
        if(chess_kernal.steplog.count<=5)
        {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.warning_returnmain)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.return_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveChess();
                        finish();
                    }
                })
                .setNeutralButton(R.string.return_withoutsave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }
    void onWin(int wincolor) {//ChessKernel.white,black,empty
        isEnd=1;
        String message;
        String title=getString(R.string.title_win);
        if(wincolor==ChessKernel.empty){
            title=getString(R.string.title_even);
            message=getString(R.string.title_even);
        }
        if(wincolor==color_ai){
            message=String.format(getString(R.string.message_win),name_ai+"("+color_ai+")",chess_kernal.steplog.count);
        }
        else {
            if(wincolor==ChessKernel.black)
                message=String.format(getString(R.string.message_win),AppValues.pref.player_1+"("+wincolor+")",chess_kernal.steplog.count);
            else
                message=String.format(getString(R.string.message_win),AppValues.pref.player_2+"("+wincolor+")",chess_kernal.steplog.count);
        }
        if(AppValues.pref.isSaveStepLog==1) {//保存系统日志
            onWriteSysLog();
        }
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRestartChess();
                    }
                }).show();
        //Toast.makeText(getApplicationContext(), wincolor + "win~~~", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "EVEN~~~", Toast.LENGTH_SHORT).show();
    }
    void onWriteSysLog(){//记录系统日志，xml与sql
        chess_io.SetPlayerMode(0,0);
        try {
            chess_io.SaveSyslogStep_xml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void onAutoSave(){
        if(chess_kernal.steplog.count >=2) {
            if (color_ai == ChessKernel.black) chess_io.SetPlayerMode(1, 0);
            else if (color_ai == ChessKernel.white) chess_io.SetPlayerMode(0, 1);
            else chess_io.SetPlayerMode(0, 0);
            try {
                chess_io.SaveSyslogStep_xml(AppValues.res.autosave_name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class RecvTouch_Hander extends Handler {//主线程消息队列，处理下棋等操作
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ChessBoardView.OnPlaceChess) {//落子的时候
                if(isEnd>0) return;
                onPlaceChess(msg);
            }
            else if(msg.what == ChessBoardView.OnBackChess) {//悔棋的时候
                onBackChess(msg);
            }
        }
    };
    public RecvTouch_Hander recvTouch=new RecvTouch_Hander();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //各种变量初始化
        chess_kernal=new ChessKernel();//内部逻辑函数类生成
        chess_io=new ChessIO(chess_kernal);

        final Intent intent=getIntent();
        final Bundle bundle=intent.getExtras();
        isLoad=bundle.getInt("isLoad",0);
        if(isLoad!=1){
            isShowStepNum=bundle.getInt("isShowStepNum",0);
            color_player=bundle.getChar("color_player");
            color_ai=bundle.getChar("color_ai");
            row=bundle.getInt("row", row);
            column=bundle.getInt("column", column);
            chess_kernal.SetChessBoardSize(row,column);
        }

        //界面全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 取得屏幕大小
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sc_height=dm.heightPixels;sc_width=dm.widthPixels;
        setContentView(R.layout.activity_chessview);

        //棋谱视图生成(棋盘视图和此模块耦合性较大，故不用fragment生成代码了，元素分开时为了在显示步骤中用)
        chessCount_text= (TextView) findViewById(R.id.text_stepcount);
        chessCount_text.setText((String.format(getResources().getString(R.string.stepcount), 0)));
        chessStep_list =(ListView)findViewById(R.id.listview_ChessStep);
        int ai_seq;
        if(color_ai==ChessKernel.empty) ai_seq=0;else if(color_ai==ChessKernel.black) ai_seq=1;else ai_seq=2;
        chessStep_adapter = new ChessStepAdapter(ai_seq);
        //chessStep_list.requestFocusFromTouch();
        chessStep_list.setAdapter(chessStep_adapter);
        chessStep_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {//点击
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (chessStep_adapter.selectedPos == position) {
                    chessStep_adapter.setSelected(-1);//重复点击清零
                    DrawChessBoard(chess_kernal.ChessBoard);
                } else {
                    chessStep_adapter.setSelected(position);
                    DrawGrayChessBoard(chess_kernal.ChessBoard);
                }
            }
        });
        //弹出菜单不做了，不好看

        //右侧菜单
        final RelativeLayout right_view=(RelativeLayout)findViewById(R.id.view_right);

        //棋盘生成
        FrameLayout root=(FrameLayout)findViewById(R.id.chessboard_root);
        final FrameLayout chessborad=(FrameLayout)root.findViewById(R.id.chessboard_view);
        chess_view=new ChessBoardView(ChessGameActivity.this, row,column);
        chessborad.addView(chess_view);

        //各种按钮，文本框
        final EditText edit_goto=(EditText)findViewById(R.id.edit_stepgoto);//跳转到某步
        edit_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_goto.requestFocus();
            }
        });
        Button button_goto=(Button)findViewById(R.id.button_stepgoto);
        button_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_goto.length() <= 0) return;//判断输入是否为空
                int scrollto = Integer.parseInt(edit_goto.getText().toString())-1;
                chessStep_list.setSelection(scrollto);
                edit_goto.clearFocus();//使光标不再闪烁
            }
        });
        final Button button_back1Step=(Button)findViewById(R.id.button_rollback); //悔1步棋
        button_back1Step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackChess(null);
            }
        });
        Button button_backto=(Button)findViewById(R.id.button_backto);//回退到某步棋
        button_backto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreBackChess();
            }
        });
        Button button_saveAsci=(Button)findViewById(R.id.button_saveAsci);//保存棋盘
        button_saveAsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveChess();
            }
        });
        Button button_loadAsci=(Button)findViewById(R.id.button_loadAsci);
        button_loadAsci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreLoadChess();
            }});
        final Button button_showstep=(Button)findViewById(R.id.button_showstep);//是否显示步骤数字
        if(isShowStepNum==0) button_showstep.setText(getString(R.string.showstep));
        else button_showstep.setText(R.string.hidestep);
        button_showstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=null;
                if(isShowStepNum==0){
                    isShowStepNum=1;
                    text=getResources().getString(R.string.hidestep);
                }
                else if(isShowStepNum==1){
                    isShowStepNum=0;
                    text=getResources().getString(R.string.showstep);
                }
                button_showstep.setText(text);
                if(chessStep_adapter.selectedPos<0)
                    DrawChessBoard(chess_kernal.ChessBoard);
                else DrawGrayChessBoard(chess_kernal.ChessBoard);
            }
        });
        Button button_restart=(Button)findViewById(R.id.button_restart);//重新开始
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreRestartChess();
            }
        });
        Button button_back=(Button)findViewById(R.id.button_toMainMenu);//回主菜单
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreReturnMainActivity();
            }
        });
        TextView text_appVerName=(TextView)findViewById(R.id.text_appNameVer);//应用名称与版本
        text_appVerName.setText(getResources().getString(R.string.app_name)+"_"+getResources().getString(R.string.app_ver));
        TextView text_appinfo=(TextView)findViewById(R.id.text_appinfo);//应用介绍文本
        text_appinfo.setMovementMethod(ScrollingMovementMethod.getInstance());//设置竖直滚动条
        text_playmode=(TextView)findViewById(R.id.text_playmode);//下棋对象
        if(color_ai!=ChessKernel.empty)
            text_playmode.setText(String.format(getResources().getString(R.string.playmode),name_ai,color_ai));
        else
            text_playmode.setText(String.format(getResources().getString(R.string.playmode),"player",color_player));

        //监听是否布局测量完成，来使棋盘到正确位置
        ViewTreeObserver vto2 = chessStep_list.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                chessStep_list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int cbleft = chessStep_list.getMeasuredWidth();
                int x1 = cbleft / 3;
                //棋盘动态适配
                chess_view.SetChessBoardView(new Rect(x1, 0, x1 + sc_height, sc_height));
                chess_view.DrawBoard();
                //右侧菜单动态适配
                android.view.ViewGroup.LayoutParams vp = right_view.getLayoutParams();
                vp.width = sc_width - cbleft - x1 - sc_height - x1;//单位像素
                right_view.setLayoutParams(vp);
                //棋子初始化
                if (color_ai == ChessKernel.black) {
                    int x = column / 2;
                    int y = row / 2;
                    PlaceChess(x, y);
                }
                if (isLoad > 0) {
                    fpath = bundle.getString("fpath", null);
                    if (fpath == null) {
                        finish();
                    }
                    onLoadChess(fpath);
                }
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chess_game, menu);
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
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 3://load
                if(data!=null) {
                    String path=data.getStringExtra(FileExplorerFragmentContants.SELECTED_PATH);
                    onLoadChess(path);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onPreReturnMainActivity();
        //super.onBackPressed();
    }

    @Override
    protected void onPause() {
        onAutoSave();
        super.onPause();
    }

    @Override
    protected void onStop() {
        onAutoSave();
        super.onStop();
    }
}
