//
// Created by misaki on 2015/11/14.
//
#include "JniFunc.h"

char ChessBoard[MAX_ROW][MAX_COLUMN];
ScoreStack s_stack;
extern int evalTable_kuon[EVA_TYPE];

void jcb2ccb(JNIEnv *env, jobject obj,
             jobjectArray jcb,char ccb[][MAX_COLUMN],
             jint row,jint column)//c与java二维数组传递
{
    int i,j;
    for(i=0;i<row;i++)
    {
        jarray rowarr=(jarray)env->GetObjectArrayElement(jcb,i);
        jchar *coldata=env->GetCharArrayElements((jcharArray)rowarr,false);
        for(j=0;j<column;j++)
            ccb[i][j]=coldata[j];
        env->ReleaseCharArrayElements((jcharArray)rowarr,coldata,0);
    }
}
jobject cstep2jstep(JNIEnv *env,jobject obj,jchar color,jint row,jint column,StepNode cstep)//c结构转为java内部类
{
    //由于此obj就是外部类，故此处不用再获取了
    //jclass outclass=env->FindClass("com/devseed/fiveinarow/ChessKernel");//获取外部类引用
    //jmethodID outcons=env->GetMethodID(outclass,"<init>","(II)V");
    //jobject outobj=env->NewObject(outclass,outcons,row,column);
    //创建返回的内部类,此处不要再初始化新外部类了，因为棋盘用的是静态变量，会影响棋盘
    jclass inclass=env->FindClass("com/devseed/fiveinarow/ChessKernel$StepNode");
    jmethodID incons=env->GetMethodID(inclass,"<init>","(Lcom/devseed/fiveinarow/ChessKernel;IIC)V");//注意内部类要传入外部类作为参数
    jobject inobj=env->NewObject(inclass,incons,obj,cstep.x,cstep.y,color);
    return inobj;
}
JNIEXPORT jchar JNICALL Java_com_devseed_fiveinarow_ChessKernel_JudgeWin
        (JNIEnv *env, jobject obj, jobjectArray jcb, jint row, jint column)
{
    jcb2ccb(env,obj,jcb,ChessBoard,row,column);
    return (jchar)judgeWin(ChessBoard,row,column);
}
JNIEXPORT jobject JNICALL Java_com_devseed_fiveinarow_ChessKernel_AI_1kuon_1pos
        (JNIEnv *env, jobject obj, jobjectArray jcb, jint row, jint column, jchar color)//ai_kuon_gametree数据交互层
{
    jcb2ccb(env,obj,jcb,ChessBoard,row,column);
    StepNode cstep=AI_kuon_pos(ChessBoard,
                      evalTable_kuon,
                      row,column,(enum ChessType)color);

    return cstep2jstep(env,obj,color,row,column,cstep);
}
JNIEXPORT jobject JNICALL Java_com_devseed_fiveinarow_ChessKernel_AI_1kuon_1gametree
        (JNIEnv *env, jobject obj, jobjectArray jcb, jint row, jint column, jchar color)//ai_kuon_pos数据交互层
{
    jcb2ccb(env,obj,jcb,ChessBoard,row,column);

    Edge edge;
    findEdge(ChessBoard,row,column,&edge,1);
    ChessAiExtend ai_info;
    ai_info.in.row=row;
    ai_info.in.column=column;
    ai_info.in.max_depth=2;
    ai_info.in.edge_dis=1;
    ai_info.in.ps_stack=&s_stack;
    ai_info.in.evel_b=&evaluateBoard;
    ai_info.in.edge=edge;

    StepNode cstep=AI_kuon_gametree(ChessBoard,
                                    evalTable_kuon,
                                    &ai_info,(enum ChessType)color);
    return cstep2jstep(env,obj,color,row,column,cstep);
}