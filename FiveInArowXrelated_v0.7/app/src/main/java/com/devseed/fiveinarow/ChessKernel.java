package com.devseed.fiveinarow;

import java.util.Random;

/**
 * Created by misaki on 2015/11/13.
 */
//棋盘内部逻辑相关的类
public class ChessKernel {//由于jni与包名有关，不再移动这个类了
    static {
        System.loadLibrary("AiKuon");
    }

    private int row,column;
    public static final char white = 'H';//逻辑数组各种棋的颜色
    public static final char black = '*';
    public static final char empty = '0';

    public class StepNode {
        public int x, y;//x，y分别对应二维数组值
        public char color;
        public StepNode(int x,int y,char color) {
            this.x=x;this.y=y;this.color=color;
        }
        public StepNode(){}
    }
    public class ChessStep{//只所以没有getTop函数是因为，java创建内部类必须new外部类，而不能传递多个变量，故直接访问
        public StepNode[] step;
        public int count;
        ChessStep(int MAX_STEP) {
            count=0;step=new StepNode[MAX_STEP];
        }
    }
    public char[][] ChessBoard=null;//棋盘
    public ChessStep steplog=null;//下棋的记录
    public StepNode tstep;

    public ChessKernel(){};
    public ChessKernel(int row,int column) {
        this();
        SetChessBoardSize(row, column);
    }
    public void SetChessBoardSize(int row,int column){
        if(ChessBoard==null || row!=this.row || column!=this.column) {
            ChessBoard = new char[row][column];
            for (int i = 0; i < row; i++)
                for (int j = 0; j < column; j++)
                    ChessBoard[i][j] = empty;
            steplog = new ChessStep(row * column);
        }
        this.row=row;
        this.column=column;
    }
    public void clear(){
        for(int i=0;i<row;i++)
            for(int j=0;j<column;j++)
                ChessBoard[i][j]=empty;
        clearLog();
    }
    public void clearLog(){
        steplog.count=0;
    }
    public int getRow(){
        return row;
    }
    public int getColumn(){
        return column;
    }
    public int PlaceChess(int x,int y,char color){//下棋，成功返回步数，否则返回0
        if(ChessBoard[x][y]!=empty) return  0;
        if(x<0 || x>=row || y<0 || y>=column) return -1;//出边界判断
        ChessBoard[x][y]=color;
        steplog.step[steplog.count]=new StepNode(x,y,color);//数组中的元素，每增加一个都要new
        steplog.count++;
        return steplog.count;
    }
    public  int BackChess(int n){//回退n步,成功返回记录步数，否则返回0
        for(int i=0;i<n;i++) {
            if (steplog.count <= 0) return -1;
            ChessBoard[steplog.step[steplog.count - 1].x][steplog.step[steplog.count - 1].y] = empty;
            (steplog.count)--;
        }
        return steplog.count;
    }

    //原生库逻辑函数
    public native char JudgeWin(char ChessBoard[][],int row,int column);
    public native StepNode AI_kuon_pos(char ChessBoard[][],int row,int column,char color);
    public native StepNode AI_kuon_gametree(char ChessBoard[][],int row,int column,char color);
    //封装的原生函数
    public char JudgeWin(){
        return JudgeWin(ChessBoard,row,column);
    }
    public StepNode AI_kuon(char color){
        StepNode step;
        Random rand=new Random();
        if(rand.nextInt(100)<50) {
            step=AI_kuon_gametree(ChessBoard,row,column,color);
        }
        else{
            step=AI_kuon_pos(ChessBoard,row,column,color);
        }
        return step;
    }
}
