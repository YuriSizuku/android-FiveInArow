#include <stdio.h>

#include "stdFunc.h"

//判断输赢
enum ChessType judgeWin(char ChessBoard[][MAX_COLUMN],int row,int column)//返回0，black或white
{
	int i,j,count;//count坚持多少个连续棋子
	for(i=0;i<row;i++)//检验行是否有5连子
	{
		count=1;
		for(j=1;j<column;j++)
		{
			if(ChessBoard[i][j]==ChessBoard[i][j-1] && ChessBoard[i][j]!='0')
			{
				count++;
				if(count>=5) return (enum ChessType)ChessBoard[i][j];
			}
			else
				count=1;
		}
	}
	for(j=0;j<column;j++)//检验列是否有5连子
	{
		count=1;
		for(i=1;i<row;i++)
		{
			if(ChessBoard[i][j]==ChessBoard[i-1][j] && ChessBoard[i][j]!='0')
			{
				count++;
				if(count>=5) return (enum ChessType)ChessBoard[i][j];
			}
			else
				count=1;
		}
	}
	for(i=0;i<row-4;i++)//检验下半主对角线方向五连子
	{
		count=1;
		for(j=1;j<row-i;j++)
		{
			if(ChessBoard[i+j][j]==ChessBoard[i+j-1][j-1] && ChessBoard[i+j][j]!='0')
			{
				count++;
				if(count>=5) return  (enum ChessType)ChessBoard[i+j][j];
			}
			else 
				count=1;
		}
	}
	for(j=1;j<column-4;j++)//检查上半主对角线方向五连子
	{
		count=1;
		for(i=1;i<column-j;i++)
		{
			if(ChessBoard[i][j+i]==ChessBoard[i-1][j+i-1] && ChessBoard[i][j+i]!='0')
			{
				count++;
				if(count>=5) return  (enum ChessType)ChessBoard[i][j+i];
			}
			else 
				count=1;
		}
	}
	for(i=4;i<row;i++)//检验上半副对角线方向五连子
	{
		count=1;
		for(j=1;j<=i;j++)
		{
			if(ChessBoard[i-j][j]==ChessBoard[i-j+1][j-1] && ChessBoard[i-j][j]!='0')
			{
				count++;
				if(count>=5) return  (enum ChessType)ChessBoard[i-j][j];
			}
			else 
				count=1;
		}
	}
	for(j=1;j<column-4;j++)//检查下半副对角线方向五连子
	{
		count=1;
		for(i=j+1;i<row;i++)
		{
			if(ChessBoard[i][(column-1)-(i-j)]==ChessBoard[i-1][(column-1)-(i-j)+1] && ChessBoard[i][(column-1)-(i-j)]!='0')
			{
				count++;
				if(count>=5) return  (enum ChessType)ChessBoard[i][(column-1)-(i-j)];
			}
			else 
			count=1;
		}
	}
	return  (enum ChessType)0;
}
//所有棋子边界检查
int isEdge(int x,int y,int row,int column)//不是边缘返回0，上下左右(1~4),左上右上左下右下(5~8)
{
	if(x>0 && x<row-1 && y>0 && y<column-1) return 0;
	if(x==0)
	{
		if(y==0) return 5;//左上
		if(y==column-1) return 6;//右上
		return 1;//上
	}
	if(x==row-1)
	{
		if(y==0) return 7;//左下
		if(y==column-1) return 8;//右下
		return 2;
	}
	if(y==0) return 3;//左
	if(y==column-1) return 4;//右
}
int checkEdge(Edge *edge,Position pos,int row,int column,int dis)
//增加一点后查看边界是否改变，若没变且都没到边界返回0，否则返回1
//dis为原来边界多dis距离，edge为所有点距离dis矩形包络矩形（若到边界则为到边界包络面）
{
	if(pos.x - edge->x1 >=dis && edge->x2 - pos.x >=dis
		&& pos.y - edge->y1 >=dis && edge->y2-pos.y >=dis) return 0;
	if(pos.x==0) edge->x1=0;
	else if(pos.x==row-1) edge->x2=row-1;
	else
	{
		if(pos.x - edge->x1 < dis) 
			edge->x1 = (pos.x-dis < 0) ? 0 : pos.x-dis;
		if(edge->x2 - pos.x < dis) 
			edge->x2 = (pos.x+dis >row-1) ? row-1 :pos.x+dis;
	}
	if(pos.y==0) edge->y1=0;
	else if(pos.y==column-1) edge->y2=column-1;
	else
	{
		if(pos.y - edge->y1 < dis) 
			edge->y1 = (pos.y-dis <0) ? 0 : pos.y-dis;
		if(edge->y2 - pos.y < dis) 
			edge->y2 = (pos.y+dis > column-1) ? column-1:pos.y+dis;
	}
	return 1;
}
int findEdge(char ChessBoard[][MAX_COLUMN],int row,int column,Edge *edge,int dis )
{
	int i,j,count=0;
	Position pos;
	for(i=0;i<row;i++)
	{
		for(j=0;j<column;j++)
		{
			if(ChessBoard[i][j]!=empty)
			{
				if(count==0)
				{
					edge->x1=edge->x2=i;
					edge->y1=edge->y2=j;
				}//这样防止到边界
				pos.x=i;pos.y=j;
				checkEdge(edge,pos,row,column,dis);
				count++;
			}
		}
	}
	return count;
}

//博弈树dps用的栈
int push_ScoreStack(ScoreStack *ps,ScoreStackNode pn)
{
	ps->stack[ps->count++]=pn;
	return ps->count;
}
ScoreStackNode pop_ScoreStack(ScoreStack *ps)
{
	return ps->stack[--ps->count];
}
ScoreStackNode* getTop_ScoreStack(ScoreStack *ps)
{
	return &ps->stack[ps->count-1];
}
int isEmpty_ScoreStack(ScoreStack *ps)
{
	if(ps->count>0) return 0;
	else return 1;
}
int iniScoreStack(ScoreStack *ps,int size)//成功返回0 否则返回-1 
{
	ps->count=0;
	ps->capacity=size;
	return 0;
}