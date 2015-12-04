package com.devseed.fiveinarow.view;

/**
 * Created by misaki on 2015/11/10.
 */
//棋盘界面显示的类
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;
import com.devseed.fiveinarow.data.AppValues;

import java.io.File;
import java.lang.reflect.Field;

public class ChessBoardView extends View {

    long LongTouchTime=500;//超过这个毫秒数就是长按
    public final static int OnPlaceChess=1;
    public final static int OnBackChess=2;

    Context m_context;
    Object m_sendObj;//将消息发送的类
    String m_handlerName="recvTouch";//发送消息接受类的名称
    Bitmap m_bitmap;//canvas的绘图缓存
    Bitmap m_picbuff;

    Rect m_ChessEdge;//可下棋子的范围
    Rect m_FullEdge;//整个棋盘大小
    int row,column;//棋盘行列数
    float row_len,col_len;//行列间的像素间距
    float chess_radius;//棋子大小

    //棋盘线与棋子
    public int m_col_chessw=AppValues.pref.col_chessw;//白棋子颜色
    public int m_col_chessb=AppValues.pref.col_chessb;//黑棋子颜色
    public int m_col_chesswg=AppValues.pref.col_chesswg;//灰色白棋子颜色
    public int m_col_chessbg=AppValues.pref.col_chessbg;//灰色黑棋子颜色
    public int m_col_bline=AppValues.pref.col_bline;//棋盘线颜色
    public int m_col_eline=AppValues.pref.col_eline;//棋盘边界线颜色666600
    public int m_col_winline=AppValues.pref.col_winline;//五连珠时候绘图的颜色
    public int m_col_str=AppValues.pref.col_str;//文字颜色
    public int m_col_curflag =AppValues.pref.col_curflag ;//当前棋子标记的颜色
    public int m_col_numw=AppValues.pref.col_numw;//白棋子上数字的颜色
    public int m_col_numb=AppValues.pref.col_numb;//黑棋子上数字颜色

    //棋盘背景
    public int m_bgmode=AppValues.pref.bgmode;//0为用纯色绘图，1为用资源图片，2为用自定义图片
    public int m_bgcol=AppValues.pref.bgcol;//当m_bgmode=0时，棋盘背景色
    public int m_bgres=AppValues.pref.bgres;//m_bgmode=1时，mipmap的资源id
    public int m_bgscale= AppValues.pref.bgscale;//当m_bgmode=2时，0为居中显示，1为拉伸显示，2为自定义区域（暂时不考虑）
    public String m_bgsrc=AppValues.pref.bgsrc;//当m_bgmode=2时,bg的绝对路径

    private Bitmap formBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        if(m_bgsrc==null) m_bgsrc="";
        File file=new File(m_bgsrc);
        if(m_bgmode==0 ||( m_bgmode==2 && !file.exists())){
            m_picbuff=Bitmap.createBitmap(m_FullEdge.width(), m_FullEdge.height(), Bitmap.Config.RGB_565);
        }
        else {
            if(m_bgmode==1) {
                m_picbuff = BitmapFactory.decodeResource(getResources(), m_bgres, options);
            }
            else if(m_bgmode==2){
                m_picbuff=BitmapFactory.decodeFile(m_bgsrc,options);
            }
            if (m_bgscale == 0) {//居中显示
                m_picbuff = ThumbnailUtils.extractThumbnail(m_picbuff, m_FullEdge.width(), m_FullEdge.height());//自动居中选取
            } else if (m_bgscale == 1) {//拉伸显示
                Matrix matrix = new Matrix();
                float sx = (float) m_FullEdge.width() / (float) options.outWidth;
                float sy = (float) m_FullEdge.height() / (float) options.outHeight;
                matrix.setScale(sx, sy);
                //将原图像区域经过矩阵变换后的图像创建为新图像
                m_picbuff = Bitmap.createBitmap(m_picbuff, 0, 0, options.outWidth, options.outHeight, matrix, true);
            }
        }
        return Bitmap.createBitmap(m_picbuff);
    }
    public ChessBoardView (Context context){
        super(context);
        m_sendObj= m_context=context;
    }
    public ChessBoardView(Context context,int row,int column,Rect FullEdge,Rect ChessEdge){
        super(context);
        m_sendObj=m_context=context;
        SetChessBoardSize(row, column);
        SetFullEdge(FullEdge);
        SetChessEdge(ChessEdge);
    }
    public ChessBoardView(Context context,int row,int column,Rect FullEdge) {
        super(context);
        m_sendObj=m_context=context;
        SetChessBoardSize(row, column);
        SetChessBoardView(FullEdge);

    }
    public ChessBoardView(Context context,int row,int column){
        super(context);
        m_sendObj=m_context=context;
        SetChessBoardSize(row, column);
    }
    public void SetChessBoardView(Rect FullEdge){
        SetFullEdge(FullEdge);
        int div=15*row/15;//以15标准棋盘为基准
        int ex=FullEdge.width()/(div*3);
        int ey=FullEdge.height()/(div*3);//为改善浮点数与整数的误差，使棋盘不均匀
        SetChessEdge(new Rect(
                FullEdge.left + FullEdge.width() / div + ex,
                FullEdge.top + FullEdge.height() / div + ey,
                FullEdge.right - FullEdge.width() / div + ex,
                FullEdge.bottom - FullEdge.height() / div + ey
        ));

    }
    public void SetFullEdge(Rect rect){
        this.m_FullEdge=rect;
        m_bitmap=formBitmap();
    }
    public void SetChessEdge(Rect rect){//一定要在SetFullEdge之后调用，且要小于FullEdge
        this.m_ChessEdge=rect;
        col_len= (m_ChessEdge.width())/(row-1);
        row_len= (m_ChessEdge.height())/(column-1);
        chess_radius=(float)(row_len+col_len)/6;
    }
    public void SetChessBoardSize(int row,int column){
        this.row=row;this.column=column;
    }
    public void ResetChessBoardSize(int row,int column){
        this.row=row;this.column=column;
        col_len= (m_ChessEdge.width())/(row-1);
        row_len= (m_ChessEdge.height())/(column-1);
        chess_radius=(float)(row_len+col_len)/6;
    }

    public void DrawBoard() {//绘制棋盘，坐标为m_Fulledge中的
        float []pts=new float[row*column];
        float blinewidth=(row_len + col_len) / 20;//画笔宽度，防止有缝隙

       //画布与笔
        Canvas canvas=new Canvas(m_bitmap);

        if(m_bgmode==0) canvas.drawColor(m_bgcol);
        Paint paint=new Paint();
        paint.setAntiAlias(true);//抗锯齿功能
        paint.setColor(m_col_bline);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE
        paint.setStrokeWidth(blinewidth);//设置画笔宽度

        //采用相对坐标
        float []pts_shift=new float[]{
                m_ChessEdge.top-m_FullEdge.top,
                m_ChessEdge.left-m_FullEdge.left,
                m_FullEdge.bottom-m_ChessEdge.bottom,
                m_FullEdge.right-m_ChessEdge.right
        };
        for(int i=0;i<row;i++)//绘制行
        {
            pts[4*i]=0+pts_shift[1]-blinewidth/2;
            pts[4*i+1]=i*row_len+pts_shift[0];
            pts[4*i+2]=(column-1)*col_len+pts_shift[1]+blinewidth/2;//消除浮点数的误差
            pts[4*i+3]=i*row_len+pts_shift[0];
        }
        canvas.drawLines(pts, 0, 4 * (row), paint);
        for(int i=0;i<column;i++)//绘制列
        {
            pts[4*i]=i*col_len+pts_shift[1];
            pts[4*i+1]=0+pts_shift[0]-blinewidth/2;
            pts[4*i+2]=i*col_len+pts_shift[1];
            pts[4*i+3]= (row-1)*row_len+pts_shift[0]+blinewidth/2;
        }
        canvas.drawLines(pts, 0, 4 * (column), paint);
        //注意如果先画边界边界线会被覆盖
        DrawOuterEdge();
        //invalidate();
    }
    private void DrawOuterEdge() {//画棋盘边界，坐标为m_Fulledge中的
        float elinesize=(row_len + col_len) / 10;
        float elineshift=1;
        float textsize=2*chess_radius;
        float textxshift= (float) m_ChessEdge.left-m_FullEdge.left-7*chess_radius/2;
        float textyshift= (float) m_ChessEdge.top-m_FullEdge.top-3*chess_radius/2;

        Canvas canvas=new Canvas(m_bitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);//抗锯齿功能
        paint.setColor(m_col_eline);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE
        paint.setStrokeWidth(elinesize);//设置画笔宽度

        //设置棋盘边界颜色(注意坐标系)
        canvas.drawLine(0 + elineshift, 0, 0 + elineshift, m_FullEdge.height(), paint);//左
        canvas.drawLine(m_FullEdge.width() - elineshift, 0, m_FullEdge.width() - elineshift, m_FullEdge.height(), paint);//右
        //canvas.drawLine(0, 0 + elineshift, m_FullEdge.width(), 0 + elineshift, paint);//上
       // canvas.drawLine(0, m_FullEdge.height() - elineshift, m_FullEdge.width(), m_FullEdge.height() - elineshift, paint);//下

        //绘制棋盘数字
        paint.setTextSize(textsize);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(m_col_str);
        for(int i=0;i<row;i++){//列
            String text=String.format("%d",i+1);
            canvas.drawText(text,textxshift,m_ChessEdge.top-m_FullEdge.top+row_len*i,paint);
        }
        for(int i=0;i<column;i++) {//行
            String text=String.format("%c",i+'A');
            canvas.drawText(text,m_ChessEdge.left-m_FullEdge.left+col_len*i-chess_radius/2,textyshift,paint);
        }
        invalidate();


    }
    public void DrawChess(int x,int y,int color){//绘制棋子，坐标为相对棋盘棋子坐标
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);//抗锯齿功能

        float []pts_shift=new float[]{
                m_ChessEdge.top-m_FullEdge.top,
                m_ChessEdge.left-m_FullEdge.left,
                m_FullEdge.bottom-m_ChessEdge.bottom,
                m_FullEdge.right-m_ChessEdge.right
        };

        Canvas canvas=new Canvas(m_bitmap);
        canvas.drawCircle(x * col_len + pts_shift[1],
                y * row_len + pts_shift[0],
                chess_radius, paint);
        invalidate();
    }
    public void DrawChess(int x,int y,char color){
        if(color== ChessKernel.black) DrawChess(x,y,m_col_chessb);
        else if(color==ChessKernel.white)DrawChess(x,y,m_col_chessw);
        //DrawNumOnChess(x,y,x*y,color);
    }
    public void DrawGrayChess(int x,int y,char color){
        if(color==ChessKernel.black) DrawChess(x,y,m_col_chessbg);
        else if(color==ChessKernel.white)DrawChess(x,y,m_col_chesswg);
    }
    public void DrawCurrentChess(int x,int y,char color){
        DrawChess(x, y, color);
        DrawDot(x, y, m_col_curflag);
    }
    public void DrawDot(int x,int y,int color){
        Paint paint=new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);//抗锯齿功能

        float []pts_shift=new float[]{
                m_ChessEdge.top-m_FullEdge.top,
                m_ChessEdge.left-m_FullEdge.left,
                m_FullEdge.bottom-m_ChessEdge.bottom,
                m_FullEdge.right-m_ChessEdge.right
        };
        Canvas canvas=new Canvas(m_bitmap);
        canvas.drawCircle(x * col_len + pts_shift[1],
                y * row_len + pts_shift[0],
                chess_radius / 3, paint);
        invalidate();

    }
    public void DrawNumOnChess(int x,int y,int num,int color){
        float []pts_shift=new float[]{
                m_ChessEdge.top-m_FullEdge.top,
                m_ChessEdge.left-m_FullEdge.left,
                m_FullEdge.bottom-m_ChessEdge.bottom,
                m_FullEdge.right-m_ChessEdge.right
        };

        float textsize=3*chess_radius/2;
        Paint paint=new Paint();
        float startx=pts_shift[1]+x*col_len;
        float starty=pts_shift[0]+y*row_len+chess_radius/2;

        Canvas canvas=new Canvas(m_bitmap);
        paint.setColor(color);
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);//抗锯齿功能
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        String text=String.format("%d",num);
        canvas.drawText(text, startx, starty, paint);
        invalidate();
    }
    public void DrawNumOnChess(int x,int y,int num,char color){
        int drawcolor=0;
        if(color==ChessKernel.black) drawcolor=m_col_numb;
        else if(color==ChessKernel.white) drawcolor=m_col_numw;
        DrawNumOnChess(x, y, num, drawcolor);
    }
    public void DrawChessBoard(char ChessBoard[][]) {
        ClearChessBoard();
        for(int i=0;i<row;i++)
            for(int j=0;j<column;j++) {
                if(ChessBoard[i][j]==ChessKernel.white) DrawChess(j,i,m_col_chessw);
                else if(ChessBoard[i][j]==ChessKernel.black) DrawChess(j,i,m_col_chessb);
            }
    }
    public void ClearChessBoard(){
        //m_bitmap=Bitmap.createBitmap(m_FullEdge.width(), m_FullEdge.height(), Bitmap.Config.RGB_565);//清空原来棋盘
        m_bitmap= m_picbuff.copy(Bitmap.Config.RGB_565, true);
        DrawBoard();
    }
    public void DrawWinLine(char ChessBoard[][],int chess_x,int chess_y) {//画棋盘视图上以x，y为坐标的连成五个棋子的直线

        int kernal_x1=chess_y-4 < 0 ? 0:chess_y-4;//转换为kernel坐标，对调
        int kernal_x2=chess_y+4 >=column ? column-1:chess_y+4;
        int kernal_y1=chess_x-4 < 0 ? 0:chess_x-4;
        int kernal_y2=chess_x+4 >= row ? row-1:chess_x+4;
        int kernal_xLow=0,kernal_yLow=0,i,j;
        int count;
        char wincolor=ChessBoard[chess_y][chess_x];
        findwinline:{
            j=chess_x;count=0;
            for(i=kernal_x1;i<=kernal_x2;i++){//行
                if(ChessBoard[i][j]==wincolor){
                    if(count==0) {
                        kernal_xLow=i;kernal_yLow=j;
                    }
                    count++;
                }
                else count=0;
                if(count >=5) break findwinline;
            }
            i=chess_y;count=0;
            for(j=kernal_y1;j<=kernal_y2;j++){//列
                if(ChessBoard[i][j]==wincolor){
                    if(count==0) {
                        kernal_xLow=i;kernal_yLow=j;
                    }
                    count++;
                }
                else count=0;
                if(count >=5) break findwinline;
            }
            //到斜线考虑到行列均衡，从左到右，对左边进行修正
            kernal_x1=chess_y-4;kernal_y1=chess_x-4;
            count=0;
            for(i=kernal_x1,j=kernal_y1;i<=kernal_x2 && j<=kernal_y2;i++,j++){//主对角线
                    if(i<0 || j<0) continue;
                    if(ChessBoard[i][j]==wincolor){
                        if(count==0) {
                            kernal_xLow=i;kernal_yLow=j;
                        }
                        count++;
                    }
                    else count=0;
                    if(count >=5) break findwinline;
            }
            kernal_x2=chess_y+4;
            count=0;
            for(j=kernal_y1,i=kernal_x2;j<=kernal_y2  && i>=kernal_x1;j++,i--){//副对角线
                if(i<0 || j<0 || i>=row || j>=column) continue;
                if(ChessBoard[i][j]==wincolor){
                    if(count==0) {
                        kernal_xLow=i;kernal_yLow=j;
                    }
                    count++;
                }
                else count=0;
                if(count >=5) break findwinline;
            }
        }
        if(count<5) return;

        float linesize=(row_len + col_len) / 15;
        float []pts_shift=new float[]{
                m_ChessEdge.top-m_FullEdge.top,
                m_ChessEdge.left-m_FullEdge.left,
                m_FullEdge.bottom-m_ChessEdge.bottom,
                m_FullEdge.right-m_ChessEdge.right
        };

        Canvas canvas=new Canvas(m_bitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);//抗锯齿功能
        paint.setColor(m_col_winline);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE
        paint.setStrokeWidth(linesize);//设置画笔宽度
        canvas.drawLine(kernal_yLow*col_len+pts_shift[1]
                        ,kernal_xLow*row_len+pts_shift[0]
                        ,j*col_len+pts_shift[1]
                        ,i*row_len+pts_shift[0],paint);//貌似从右到左也可以绘制
        invalidate();
    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        //paint.setAlpha(178);
        paint.setAntiAlias(true);
        //canvas.drawBitmap(m_bitmap,0,0,paint);
        canvas.drawBitmap(m_bitmap, m_FullEdge.left, m_FullEdge.top, paint);//将bitmap绘制到从坐标起始
    }
    public boolean onTouchEvent(MotionEvent event) {//触摸事件，坐标为相对屏幕左上角的
        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                long event_time=event.getEventTime()-event.getDownTime();
                Log.d("Event time:","="+event_time);

                float sc_x=event.getX();//屏幕x坐标
                float sc_y=event.getY();//屏幕y坐标
                /*if(sc_x <m_ChessEdge.left || sc_x >m_ChessEdge.right
                        || sc_y<m_ChessEdge.top || sc_y>m_ChessEdge.bottom ) return true;//棋盘边界检查*/

                if(event_time >=LongTouchTime) {//长按事件
                    new Thread(new Runnable() {//将点击点棋盘坐标传给主线程
                        @Override
                        public void run() {
                            Handler sendObj = null;
                            try {
                                Field fsend=m_sendObj.getClass().getField(m_handlerName);//反射找到传入类中的接受m_handlerName Field
                                sendObj=( Handler)fsend.get(m_sendObj);//获取传入类中的m_handlerName名字的对象

                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            Message msg=sendObj.obtainMessage();
                            msg.what=OnBackChess;
                            msg.sendToTarget();
                        }
                    }).run();
                    return true;
                }

                //坐标不要搞反
                final int chess_x=Math.round((sc_x- m_ChessEdge.left) / col_len);// 棋子x相对坐标
                final int chess_y=Math.round((sc_y- m_ChessEdge.top) / row_len);//棋子y相对坐标
                Log.d("*****","chess_x="+chess_x+",chess_y="+chess_y);
                Log.d("*****","sc_x="+sc_x+"sc__y"+sc_y);
                if(chess_x < 0 || chess_x >row-1 || chess_y <0 || chess_y>column-1)
                    return true;

                float ex=(float)Math.abs((sc_x- m_ChessEdge.left) / col_len - chess_x);//与交叉点的x误差位置
                float ey=(float)Math.abs((sc_y- m_ChessEdge.top) / row_len - chess_y);//与交叉点的y误差位置
                float e=(float)0.45;//屏幕点击的误差

                if(ex<=e && ey<=e) {
                    new Thread(new Runnable() {//将点击点棋盘坐标传给主线程
                        @Override
                        public void run() {
                            Handler sendObj = null;
                            try {
                                Field fsend=m_sendObj.getClass().getField(m_handlerName);//反射找到传入类中的接受m_handlerName Field
                                sendObj=( Handler)fsend.get(m_sendObj);//获取传入类中的m_handlerName名字的对象

                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            Message msg=sendObj.obtainMessage();
                            msg.what=OnPlaceChess;
                            Bundle bundle=new Bundle();
                            bundle.putInt("chess_x",chess_x);bundle.putInt("chess_y",chess_y);
                            msg.setData(bundle);
                            msg.sendToTarget();
                        }
                    }).run();
                }
                break;
            default:return true;
        }
        return true;
    }
}