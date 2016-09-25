package com.devseed.fiveinarow.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.provider.DocumentsContract;
import android.util.Log;
import android.util.Xml;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.XMLFormatter;
import java.util.zip.Inflater;

import cn.edu.bit.cs.myfileexplorer.FileExplorerActivity;
import cn.edu.bit.cs.myfileexplorer.model.ExplorerMode;

/**
 * Created by misaki on 2015/11/25.
 */
public class ChessIO {//主要处理下棋过程的保存于读取
    ChessKernel chess_kernel;
    String work_path=AppValues.pref.workpath;//工作路径
    String steplog_path=AppValues.res.steplog_path;//棋盘保存路径
    String syslog_path=AppValues.res.syslog_path;//系统日志路径
    String steplog_ext=AppValues.res.steplog_ext;//后缀名
    String appname=AppValues.res.appname;
    String version=AppValues.res.version;
    int row=AppValues.pref.row;
    int column=AppValues.pref.column;
    public int []isAi=new int[2];

    public static int CheckStepLogFile(String path){//判断是否为有效的棋盘文件,0表示文件正常
        if(path==null){
            return 1;
        }
        File file=new File(path);
        boolean res=file.exists();
        if(!file.exists()) {
            return 2;
        }
        else if(file.isDirectory()){
            return 4;
        }
        else if (path.indexOf('.')<0){
            return 3;
        }
        else if(!path.substring(path.lastIndexOf('.')).equals(AppValues.res.steplog_ext)){
            return 3;
        }
        return 0;
    }
    public static Intent MakeFileExplorerIntent(Context packageContext, ExplorerMode mode,String title){
        String path=AppValues.pref.workpath+"/"+AppValues.res.steplog_path;
        File file=new File(path);
        if(!file.exists()) path="/mnt";
        return MakeFileExplorerIntent(packageContext,mode,path,title);
    }
    public static Intent MakeFileExplorerIntent(Context packageContext, ExplorerMode mode){
        String title="";
        if(mode==ExplorerMode.CHOOSE_FILE_SINGLE) {
            title=packageContext.getString(R.string.select_steplog);
        }
        else if(mode==ExplorerMode.CHOOSE_DIRECTORY_SINGLE){
            title=packageContext.getString(R.string.select_workpath);
        }
        else{
            title=packageContext.getString(R.string.select_bgpic);
        }
        return MakeFileExplorerIntent(packageContext,mode,title);
    }
    public static Intent MakeFileExplorerIntent(Context packageContext, ExplorerMode mode,String path,String title){
        Intent intent=new Intent(packageContext,FileExplorerActivity.class);
        intent.putExtra("title", title);
        intent.putExtra(FileExplorerActivity.EXPLORER_MODE_KEY, (ExplorerMode)mode);
        intent.putExtra(FileExplorerActivity.INITPATH_KEY, path);
        intent.putExtra(FileExplorerActivity.FILELIST_BACKGROUND, "93,255,177");//fbddff
        return intent;
    }
    public void ini(){

    }
    public ChessIO(ChessKernel chess_kernel){
        this.chess_kernel=chess_kernel;
        SetPlayerMode(0,0);
    }
    public ChessIO(ChessKernel chess_kernel,String work_path){
        this.chess_kernel=chess_kernel;
        SetWorkPath(work_path);
        SetPlayerMode(0, 0);
    }
    public void SetWorkPath(String work_path){//设置目录
        this.work_path=work_path;
    }
    public void SetPlayerMode(int player1,int player2){//1表示为ai，0表示为玩家
        isAi[0]=player1;
        isAi[1]=player2;
    }
    public void SetChessBoardSize(int row,int column){
        this.row=row;
        this.column=column;
    }
    public String SaveSyslogStep_xml() throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
        Date date=new Date(System.currentTimeMillis());
        return SaveSyslogStep_xml(formatter.format(date));
    }
    public String SaveSyslogStep_xml(String name) throws IOException {

        File dic=new File(work_path+ File.separator+syslog_path);
        dic.mkdirs();
        String fpath=work_path+ File.separator+syslog_path+File.separator+name+steplog_ext;
        return SaveStep_xml(fpath);
    }
    public String SaveStep_xml() throws IOException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
        Date date=new Date(System.currentTimeMillis());
        File dic=new File(work_path+ File.separator+steplog_path);
        dic.mkdirs();
        String fpath=work_path+ File.separator+steplog_path+File.separator+formatter.format(date)+steplog_ext;
        return SaveStep_xml(fpath);
    }
    public String SaveStep_xml(String fpath) throws IOException {//存储失败返回null
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
        Date date=new Date(System.currentTimeMillis());
        String enter = System.getProperty("line.separator");//换行

        OutputStream out=new FileOutputStream(fpath);
        XmlSerializer serializer= Xml.newSerializer();
        serializer.setOutput(out, "utf-8");
        serializer.startDocument("utf-8", true);serializer.text(enter);

        serializer.startTag(null, "FiveInArow");serializer.text(enter);
        serializer.startTag(null, "title");serializer.text(enter);
        serializer.startTag(null, "name");
        serializer.text(appname);
        serializer.endTag(null, "name");serializer.text(enter);
        serializer.startTag(null, "version");
        serializer.text(version);
        serializer.endTag(null, "version");serializer.text(enter);
        serializer.startTag(null, "date");
        serializer.text(formatter.format(date));
        serializer.endTag(null, "date");serializer.text(enter);
        serializer.endTag(null, "title");serializer.text(enter);

        serializer.startTag(null, "chessboard");serializer.text(enter);
        serializer.startTag(null, "row");
        serializer.text(String.valueOf(row));
        serializer.endTag(null, "row");serializer.text(enter);
        serializer.startTag(null, "column");
        serializer.text(String.valueOf(column));
        serializer.endTag(null, "column");serializer.text(enter);
        serializer.endTag(null, "chessboard");serializer.text(enter);

        serializer.startTag(null, "game");serializer.text(enter);
        serializer.startTag(null, "player");serializer.text(enter);
        serializer.startTag(null, "p");
        serializer.attribute(null, "id", "0");
        serializer.attribute(null, "isAi", String.valueOf(isAi[0]));
        serializer.endTag(null, "p");serializer.text(enter);
        serializer.startTag(null, "p");
        serializer.attribute(null, "id", "1");
        serializer.attribute(null, "isAi", String.valueOf(isAi[1]));
        serializer.endTag(null, "p");serializer.text(enter);
        serializer.endTag(null, "player");serializer.text(enter);

        serializer.startTag(null, "steplog");serializer.text(enter);
        for(int i=0;i<chess_kernel.steplog.count;i++){
            int x=chess_kernel.steplog.step[i].x;
            int y=chess_kernel.steplog.step[i].y;
            char color=chess_kernel.steplog.step[i].color;

            serializer.startTag(null, "step");
            serializer.attribute(null, "num", String.valueOf(i));serializer.text(enter);
            serializer.startTag(null, "x");
            serializer.text(String.valueOf(x));
            serializer.endTag(null, "x");serializer.text(enter);
            serializer.startTag(null, "y");
            serializer.text(String.valueOf(y));
            serializer.endTag(null, "y");serializer.text(enter);
            serializer.startTag(null, "color");
            serializer.text(String.valueOf(color));
            serializer.endTag(null,"color");serializer.text(enter);
            serializer.endTag(null, "step");serializer.text(enter);
        }
        serializer.endTag(null,"steplog");serializer.text(enter);
        serializer.endTag(null, "game");serializer.text(enter);
        serializer.endTag(null,"FiveInArow");serializer.text(enter);

        serializer.endDocument();
        out.flush();
        out.close();
        return fpath;
    }
    public int LoadStep_xml(String fpath) throws IOException, XmlPullParserException {
        int row=15,column=15;
        int x=0,y=0;
        char color=ChessKernel.black;
        boolean isSteplog=false;
        boolean isStep=false;
        boolean isTitle=false;
        boolean isPlayer=false;

        InputStream in=new FileInputStream(fpath);
        XmlPullParser parser=Xml.newPullParser();
        parser.setInput(in,"utf-8");
        int eventType=parser.getEventType();

        while (eventType!=XmlPullParser.END_DOCUMENT){
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(parser.getName().equalsIgnoreCase("steplog")) {
                        isSteplog=true;
                    }
                    if(parser.getName().equalsIgnoreCase("step")) {
                        isStep=true;
                    }
                    if(parser.getName().equalsIgnoreCase("title")) {
                        isTitle=true;
                    }
                    if(parser.getName().equalsIgnoreCase("player")){
                        isPlayer=true;
                    }
                    if(parser.getName().equalsIgnoreCase("row")){
                        row = Integer.parseInt(parser.nextText());
                    }
                    if(parser.getName().equalsIgnoreCase("column")) {
                        column=Integer.parseInt(parser.nextText());
                    }
                    if(parser.getName().equalsIgnoreCase("p")){
                        if(isPlayer){
                            int count=parser.getAttributeCount();
                            int id=0,tmp=0;
                            for(int i=0;i<count;i++){
                                if(parser.getAttributeName(i).equalsIgnoreCase("id")){
                                    id=Integer.parseInt(parser.getAttributeValue(i));
                                }
                                if(parser.getAttributeName(i).equalsIgnoreCase("isAi")){
                                    tmp=Integer.parseInt(parser.getAttributeValue(i));
                                }
                            }
                            isAi[id]=tmp;
                        }
                    }
                    if(parser.getName().equalsIgnoreCase("x")){
                        if(isStep && isSteplog){
                            x=Integer.parseInt(parser.nextText());
                        }
                    }
                    if(parser.getName().equalsIgnoreCase("y")){
                        if(isStep && isSteplog){
                            y=Integer.parseInt(parser.nextText());
                        }
                    }
                    if(parser.getName().equalsIgnoreCase("color")){
                        if(isStep && isSteplog){
                            color=parser.nextText().charAt(0);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(parser.getName().equalsIgnoreCase("steplog")) {
                        isSteplog=false;
                    }
                    if(parser.getName().equalsIgnoreCase("step")) {
                        isStep=false;
                        if(isSteplog){
                            chess_kernel.PlaceChess(x,y,color);
                        }
                    }
                    if(parser.getName().equalsIgnoreCase("title")) {
                        isTitle=false;
                    }
                    if(parser.getName().equalsIgnoreCase("player")){
                        isPlayer=false;
                    }
                    if(parser.getName().equalsIgnoreCase("chessboard")){
                        chess_kernel.SetChessBoardSize(row,column);
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
            }
            eventType=parser.next();
        }
        in.close();
        return chess_kernel.steplog.count;
    }
}
