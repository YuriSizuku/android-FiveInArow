#include <stdio.h>
//AI_KUON_VER_0.2.7.6   修正棋子下满后闪退bug
#include "Ai_kuon.h"
#include "JniFunc.h"
#include "stdValue.h"

int evalTable_kuon[EVA_TYPE]=
{
	//检查横竖主副对角线上棋子情况评分，*己方棋子，#对方，0空地
	//在*不被#阻断，0保持原来是否可以隔开*情况下或，0*位置可以互换；
	//轴对称变换等效
	/*0*/100000,//*****
	/*1*/30000, //0****0,
	/*2*/10000,  //0***0*0,
	/*3*/3100,	//?**00**?
	/*4*/8000,  //#****0
	/*5*/5000,  //#***0*
	/*6*/9000,  //0***0
	/*7*/4000,	//0*0**0
	/*8*/400,   //#***00
	/*9*/1350,   //#**0*0
	/*10*/1200,  //00**0
	/*11*/300,	//0*0*0
	/*12*/100,  //#**000
	/*13*/160,  //#*0*00
	/*14*/10,  //0*0000
	/*15*/5,	//#*0000
	/*16*/10,   //#**.# X
	/*17*/150, //0*0*0*0
	/*18*/450,  //#*0*0*0
	/*19*/150,  //#**.*#
	/*20*/2200, //#**0**#
	/*21*/2500, //0**0**0
	/*22*/2300, //#**0**0
	/*23*/30000,//3+3，(#4)X(3)
};
int _eva_pos_isBreak1(int i,int sn,int flag)//判断是否因为已经是边界停止搜索
{
	if(flag==1 || flag==5 || flag==6)//上
	{
		if(i==0 && sn==-1) return 1;
		if(i==2 && sn==-1) return 1;
		if(i==3 && sn==-1) return 1;
	}
	if(flag==2 || flag==7 || flag==8)//下
	{
		if(i==0 && sn==1) return 2;
		if(i==2 && sn==1) return 2;
		if(i==3 && sn==1) return 2;
	}
	if(flag==3 || flag==5 || flag==7)//左
	{
		if(i==1 && sn==-1) return 3;
		if(i==2 && sn==-1) return 3;
		if(i==3 && sn==1) return 3;
	}
	if(flag==4 || flag==6 || flag==8)//右
	{
		if(i==1 && sn==1) return 4;
		if(i==2 && sn==1) return 4;
		if(i==3 && sn==-1) return 4;
	}
	return 0;
}
int evaluatePos(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,int row,int column,StepNode tstep)
//点评分
{
	int score[4]={1,1,1,1};//四个方得分
	int move[4][2]={{1,0},{0,1},{1,1},{1,-1}};//移动方向
	int i,j,sn;//循环变量
	int count_dead1=0;//记录两线上必堵情况
	enum ChessType color_old;
	StepNode step[2];//初步记录取得直线上的坐标起点与终点
	
	color_old=(enum ChessType)ChessBoard[tstep.x][tstep.y];
	ChessBoard[tstep.x][tstep.y]=tstep.color;
	
	for(i=0;i<4;i++)//阴线和阳线上的
	{
		int flag_edge;//边界标志
		int count_empty_s=0;//检测连续几个空位置
		int count_step[2]={0,0};//计算步数
		int count_my=1;//我的棋子数
		int count_my_maxs=0;//我的最多连续棋子数
		int count_my_ts=0;
		int j_my_maxs=0;
		step[0]=step[1]=tstep;

		//提取出来没有对手棋子的向量，且连续空格不超过4个，每半边长度不超过4的列
		//此过程对手棋子和到边界等价
		for(sn=-1;sn<2;sn+=2)
		{
			flag_edge=isEdge(tstep.x,tstep.y,row,column);
			if(_eva_pos_isBreak1(i,sn,flag_edge)!=0) continue;//判断是否此点为边界，是否可以前进
			
			count_empty_s=0;//半边连续的个数
			for(j=1;;j++)
			{
				int tx=tstep.x+j*sn*move[i][0];
				int ty=tstep.y+j*sn*move[i][1];
				
				if(ChessBoard[tx][ty]!='0' && ChessBoard[tx][ty]!=tstep.color) break;//遇到对手棋子
				
				step[(sn+1)/2].x=tx;step[(sn+1)/2].y=ty;
				count_step[(sn+1)/2]++;
				flag_edge=isEdge(tx,ty,row,column);
				
				
				if(ChessBoard[tx][ty]==tstep.color) count_my++;
				if(ChessBoard[tx][ty]=='0') count_empty_s++;
				else count_empty_s=0;
				
				if(_eva_pos_isBreak1(i,sn,flag_edge)!=0) break;//走到边界
				if(count_empty_s >= 3) break;//连续3个空格
				if(count_step[(sn+1)/2] >=  4) break;//每半边最多搜4步骤(不能比4更少了，否则出现#****0不堵)
			}
		}
		//估分
		for(j=0;j<(count_step[0]+count_step[1]+1);j++)//统计最多连续出现次数
		{
			if( ChessBoard[step[0].x+j*move[i][0]][step[0].y+j*move[i][1]]==step[0].color)
				count_my_ts++;
			else 
			{
				if(count_my_maxs < count_my_ts)
				{
					count_my_maxs=count_my_ts;
					j_my_maxs=j;
				}
				count_my_ts=0;
			}
		}
		if(count_my_maxs < count_my_ts)
		{
			count_my_maxs=count_my_ts;
			j_my_maxs=j;
		}
		
		if(count_my==1)//只有1个棋子
		{
			if(count_step[0]+count_step[1]>=4 &&count_step[0]>0 && count_step[1]>0) 
				score[i]=EvaluateTable[14];//0*0000
			else score[i]=EvaluateTable[15];
		}
		else if(count_my==2)//有2个自己棋子
		{
			if(count_step[0]+count_step[1]>=4 
				&& ChessBoard[step[0].x][step[0].y]=='0' 
				&& ChessBoard[step[1].x][step[1].y]=='0'  )
			{
				if(ChessBoard[tstep.x+move[i][0]][tstep.y+move[i][1]] == tstep.color ||
					ChessBoard[tstep.x-move[i][0]][tstep.y-move[i][1]] == tstep.color)
					score[i]=EvaluateTable[10];//00**0
				else
					score[i]=EvaluateTable[11];//0*0*0
			}
			else
			{
				if(count_step[0]+count_step[1]>=4 &&
					(ChessBoard[step[0].x][step[0].y]!='0' || ChessBoard[step[1].x][step[1].y]!='0' ))
				{
					if(ChessBoard[tstep.x+move[i][0]][tstep.y+move[i][1]] == tstep.color ||
						ChessBoard[tstep.x-move[i][0]][tstep.y-move[i][1]] == tstep.color)
						score[i]=EvaluateTable[12];//#00**0
					else
						score[i]=EvaluateTable[13];//#*0*0
				}
				else
				{
					score[i]=EvaluateTable[16]*(count_step[0]+count_step[1]);
				}
			}
		}
		else if(count_my==3)//有3个自己棋子
		{
			if(count_step[0]+count_step[1]>=4 
				&& ChessBoard[step[0].x][step[0].y]=='0' 
				&& ChessBoard[step[1].x][step[1].y]=='0'  )
			//两边都没被堵
			{
				if(count_my_maxs==3) 
				{
					score[i]=EvaluateTable[6];//0***0
					count_dead1++;
				}
				else if(count_my_maxs==2) 
				{
					score[i]=EvaluateTable[7];//0*0**0
					count_dead1++;
				}
				else score[i]=EvaluateTable[17];//0*0*0*0
			}
			else if( count_step[0]+count_step[1]>=4 ||
					(ChessBoard[step[0].x][step[0].y]=='0' && ChessBoard[step[1].x][step[1].y]=='0'))
			//一边被堵
			{
				if(count_my_maxs==3) score[i]=EvaluateTable[8];//#***00
				if(count_my_maxs==2) score[i]=EvaluateTable[9];//#**0*0
				if(count_my_maxs==1) score[i]=EvaluateTable[18];//#*0*0*0
			}
			else
			{
				score[i]=EvaluateTable[19]+10*(count_step[0]+count_step[1])+20*count_my_maxs;
			}
		}
		else if(count_my==4)//有4个自己棋子
		{
			if(count_my_maxs==2 && count_step[0]+count_step[1]>=5) 
				score[i]=EvaluateTable[3];//?**00**?
			else if(count_step[0]+count_step[1]>=4 
				&& ChessBoard[step[0].x][step[0].y]=='0' 
				&& ChessBoard[step[1].x][step[1].y]=='0'  )
			//两边都没被堵
			{
				if(count_my_maxs==4) score[i]=EvaluateTable[1];//0****0
				else if(count_my_maxs==3) score[i]=EvaluateTable[2];//0*0***0
				else if(count_my_maxs==2) score[i]=EvaluateTable[21];//0**0**0
				else score[i]=EvaluateTable[17];//0*0*0*0
			}
			else if(count_step[0]+count_step[1]>=4 
				&& (ChessBoard[step[0].x][step[0].y]=='0' || ChessBoard[step[1].x][step[1].y]=='0'))
			//一边被堵
			{
				if(count_my_maxs==4) 
				{
					score[i]=EvaluateTable[4];//#****0
					count_dead1++;
				}
				else if(count_my_maxs==3) 
				{
					score[i]=EvaluateTable[5];//#***0*
					count_dead1++;
				}
				else if(count_my_maxs==2) 
				{
					score[i]=EvaluateTable[22];//#**0**0
					count_dead1++;
				}
				else score[i]=EvaluateTable[17]-400;
			}
			else score[i]=130;
		}
		else if(count_my>=5)//有5个自己棋子
		{
			if(count_my_maxs>=5) score[i]=EvaluateTable[0];
			else score[i]=EvaluateTable[0]/2;
		}

	}
	ChessBoard[tstep.x][tstep.y]=color_old;
	if(count_dead1>=2) return EvaluateTable[23];
	return score[0]+score[1]+score[2]+score[3];
}
int evaluateBoard(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,Edge edge,int row,int column,enum ChessType color_max)
//对局面评分
{
	//srand(time(0));
	//return rand()%10000;
	int i,j;
	int tscore;
	int score_max=0,score_min=0;
	enum ChessType color_min;
	StepNode tstep;

	if(color_max==black) color_min=white;
	else color_min=black;
	for(i=edge.x1;i<=edge.x2;i++)
	{
		for(j=edge.y1;j<=edge.y2;j++)
		{
			tstep.x=i;tstep.y=j;
			if(ChessBoard[i][j]==color_max)
			{
				tstep.color=color_max;
				tscore=evaluatePos(ChessBoard,EvaluateTable,row,column,tstep);
				if(tscore >= EvaluateTable[0]) return EvaluateTable[0];//不能是INF否则迭代到上层将没法操作
				if(tscore > score_max) score_max=tscore;
			}
			if(ChessBoard[i][j]==color_min)
			{
				tstep.color=color_min;
				tscore=evaluatePos(ChessBoard,EvaluateTable,row,column,tstep);
				if(tscore >= EvaluateTable[0]) return -EvaluateTable[0];
				if(tscore > score_min) score_min=tscore;
			}
		}
	}
	//return score_max-score_min;
	if(score_max - score_min >=1*score_min) return score_max;
	else if(score_min - score_max <= 1*score_max ) return -score_min;
	else return score_max-score_min;
	//if(score_max > score_min) return score_max;
	//else return -score_min;
}
ScoreStackNode findMaxScore(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,int row,int column,Edge edge,PF_EVA_POS eval_p,enum ChessType color)
//暂时没用
{
	int i,j;
	int tscore,score_max;
	StepNode tstep,point_max;
	ScoreStackNode snode;
	for(i=edge.x1;i<=edge.x2;i++)//每个点普查
	{
		for(j=edge.y1;j<=edge.y2;j++)
		{
			if(ChessBoard[i][j]!='0') continue;
			tstep.x=i;tstep.y=j;tstep.color=color;
			tscore=eval_p(ChessBoard,EvaluateTable,row,column,tstep);
			if(tscore>score_max)
			{
				score_max=tscore;
				point_max=tstep;
			}
		}
	}
	snode.edge=edge;
	snode.pos.x=tstep.x;snode.pos.y=tstep.y;
	snode.score=score_max;
	return snode;
}
StepNode AI_kuon_pos(char ChessBoard[][MAX_COLUMN],
								int *EvaluateTable,
								int row,
								int column,
								enum ChessType color_max)
//此算法在棋盘上的位置检测每个点，取出我方max与敌方min评分
//max-min>=阈值 放子在max位置进攻
//max-min<阈值  放子在min位置防守
{
	StepNode point_max={0,0};
	StepNode point_min={0,0};
	StepNode tstep; 
	PF_EVA_POS eval_p=&evaluatePos;//用此估值函数
	enum ChessType color_min;//对手颜色
	int score_max=-1;
	int score_min=1;//评分
	int i,j;
	int tscore;


	if(color_max==white) color_min=black;
	else color_min=white;

	for(i=0;i<row;i++)//每个点普查
	{
		for(j=0;j<column;j++)
		{
			if(ChessBoard[i][j]!='0') continue;
			tstep.x=i;tstep.y=j;tstep.color=color_max;
			tscore=eval_p(ChessBoard,EvaluateTable,row,column,tstep);
			if(tscore>score_max)
			{
				score_max=tscore;
				point_max=tstep;
			}
			
			tstep.color=color_min;
			tscore=-eval_p(ChessBoard,EvaluateTable,row,column,tstep);
			if(tscore<score_min)
			{
				score_min=tscore;
				point_min=tstep;
			}
		}
	}
	if(score_max+score_min>-3*score_min/10) return point_max;
	else return point_min;
}
StepNode AI_kuon_gametree(char ChessBoard[][MAX_COLUMN],
							int *EvaluateTable,
							ChessAiExtend *ai_info,
							enum ChessType color_max)
//博弈树决策+alpha_beta剪枝
//第0层为max下，第1层为max下完后min考虑的局面（改谁下是谁的节点，如max节点是min下完后的局势）
//		alpha剪枝:min和所有上层max节点比较，min不能小于上层max
//      beta剪枝:max和所有的上层min节点比较，max不能大于上传min
//所有节点记录分数都是针对MAX的
//最后的评价函数是对整体局势，而不是单独一个点，从叶子节点倒推
//暂时先不考虑棋盘下满

//貌似博弈树没什么大问题了，就是估值函数太慢，最多迭代深度2，有时判断值不对，以后再说估值函数吧。。。。
{	
	StepNode tstep;
	ScoreStackNode *pnode=NULL,bnode,tnode;//pnode父节点（栈顶），bnode子节点（刚弹栈的节点）,tnode临时节点
	int depth=0;//当前树深度
	int score_max=INF,score_min=-INF,tscore;
	int i,j,k;
	int tx,ty;
	enum ChessType color_min,tcolor;
	Edge tedge;

	int flag_next=1;//是否是1层的最后一个
	int flag_back=0;//是否返回上一层的标志
	int flag_break=0;//跳出循环的标志
	int count_step=0;//迭代次数
	
	//这些只是为了增强可读性
	int max_depth=ai_info->in.max_depth;//树最大深度
	int row=ai_info->in.row;
	int column=ai_info->in.column;
	int edge_dis=ai_info->in.edge_dis;
	PF_EVA_BOARD eval_b=ai_info->in.evel_b;
	ScoreStack *ps_stack=ai_info->in.ps_stack;//评分堆栈,栈中压入路径，max，min共2个算1层深度
	tedge=ai_info->in.edge;
	tstep.color=color_max;

	iniScoreStack(ps_stack,MAX_STACK);
	if(color_max==white) color_min=black;
	else color_min=white;
	
	//第0层
	tnode.edge=tedge;
	tnode.pos_child.x=-1;
	tnode.score=-INF;
	push_ScoreStack(ps_stack,tnode);

	while(1)
	//DFS每个点只有1次从上到下路径
	{
		count_step++;
		pnode=getTop_ScoreStack(ps_stack);
		flag_next=0;
		
		//if(count_step>=1)
		//{
		//	PrintChessBoard(ChessBoard,15,15);
		//	printf("count= %d\n",count_step);
		//	system("sleep 10");
		//	//system("pause");
		//}

		//还没有到底层，要找到下一个搜索点(tx,ty)
		if(flag_back==1)//为了不重复搜索
		{
			for(j=bnode.pos.y+1;j<=pnode->edge.y2;j++)
			{
				if(ChessBoard[bnode.pos.x][j]=='0')
				{
					flag_next=1;
					tx=bnode.pos.x;ty=j;
					break;
				}
			}
			if(flag_next==0)
			{
				for(i=bnode.pos.x+1;i<=pnode->edge.x2;i++)
				{
					for(j=pnode->edge.y1;j<=pnode->edge.y2;j++)
					{
						if(ChessBoard[i][j]=='0')
						{
							flag_next=1;
							tx=i;ty=j;
							goto _break_s1;
						}
					}
				}
			}
		}
		else
		{
			for(i=pnode->edge.x1;i<=pnode->edge.x2;i++)
			{
				for(j=pnode->edge.y1;j<=pnode->edge.y2;j++)
				{
					if(ChessBoard[i][j]=='0')
					{
						flag_next=1;
						tx=i;ty=j;
						goto _break_s1;
					}
				}
			}
		}
_break_s1:
		if(depth<1 && flag_next==0) //只剩0层一个顶点的时候
			break;
		
		//当下一步是叶子节点时，计算叶节点评分，且仅有叶节点需要调用评分函数
		if(depth == max_depth)
		{
			bnode=pop_ScoreStack(ps_stack);
			pnode=getTop_ScoreStack(ps_stack);		
			tscore=eval_b(ChessBoard,EvaluateTable,bnode.edge,row,column,color_max);
			if(tscore < pnode->score)
			{
				pnode->score=tscore;
				pnode->pos_child=bnode.pos;
			}		
			ChessBoard[bnode.pos.x][bnode.pos.y]='0';
			depth--;
			flag_back=1;
			
			for(k=0;k<depth;k+=2)//beta剪枝
			{
				if(pnode->score <= ps_stack->stack[k].score)
				{
					flag_break=1;
					break;
				}
			}
			if(flag_break>0) //剪枝则此时下面节点不再搜索
			{
				bnode=pop_ScoreStack(ps_stack);
				depth--;
				flag_back=1;
				ChessBoard[bnode.pos.x][bnode.pos.y]='0';
			}
			continue;
		}

		//查看是否需要剪枝
		flag_break=0;
		if(depth%2==1)//MIN
		{
			for(k=0;k<depth;k+=2)//beta剪枝
			{
				if(pnode->score <= ps_stack->stack[k].score)
				{
					flag_break=1;
					break;
				}
			}
		}
		else//MAX
		{
			for(k=1;k<depth;k+=2)//alpha剪枝
			{
				if(pnode->score >= ps_stack->stack[k].score)
				{
					flag_break=1;
					break;
				}
			}
		}
		if(flag_break>0 && depth>1) //剪枝则此时下面节点不再搜索
		{
			bnode=pop_ScoreStack(ps_stack);
			depth--;
			flag_back=1;
			ChessBoard[bnode.pos.x][bnode.pos.y]='0';
			continue;
		}
		
		//if(depth>=max_depth-1 && flag_back==1) 
		//	continue; 
		
		if(flag_next > 0)//有点可以增加，初始化点
		{
			flag_back=0;
			depth++;
			
			tnode.pos.x=tx;tnode.pos.y=ty;
			//tnode.pos_child.x=-1;tnode.pos_child.y=-1;
			tnode.edge=pnode->edge;
			checkEdge(&tnode.edge,tnode.pos,row,column,edge_dis);
		
			if(depth%2==1) 
			{
				tnode.score=INF;//MIN决策的局面(MAX下完后)
				ChessBoard[tnode.pos.x][tnode.pos.y]=color_max;
			}
			else 
			{
				tnode.score=-INF;//MAX决策的局面(MIN下完后)
				ChessBoard[tnode.pos.x][tnode.pos.y]=color_min;
			}
			push_ScoreStack(ps_stack,tnode);
			
			//检查是否有人赢了
			pnode=getTop_ScoreStack(ps_stack);
			tstep.x=pnode->pos.x;tstep.y=pnode->pos.y;
			tscore=evaluatePos(ChessBoard,EvaluateTable,row,column,tstep);
			if(tscore >= EvaluateTable[0])
			{
				if(ChessBoard[pnode->pos.x][pnode->pos.y]==color_max)
				{
					for(i=1;i<=depth;i++)
						ChessBoard[ps_stack->stack[i].pos.x][ps_stack->stack[i].pos.y]=empty;
					tstep.x=ps_stack->stack[1].pos.x;tstep.y=ps_stack->stack[1].pos.y;
					return tstep;

				}
				else
				{
					depth--;
					flag_back=1;
					bnode=pop_ScoreStack(ps_stack);
					ChessBoard[bnode.pos.x][bnode.pos.y]=empty;
					continue;
				}
			}
		}
		else//这支已经搜索完了，向上回溯(所有分支都遍历完了，不是在这里减枝)
		{
			flag_back=1;
			depth--;
			bnode=pop_ScoreStack(ps_stack);
			pnode=getTop_ScoreStack(ps_stack);
			
			if(depth%2==1)//MIN
			{
				if(bnode.score < pnode->score)
				{
					pnode->score=bnode.score;
					pnode->pos_child=bnode.pos;
				}	
			}
			else//MAX
			{
				if(bnode.score > pnode->score)
				{
					pnode->score=bnode.score;
					pnode->pos_child=bnode.pos;
				}	
			}
			ChessBoard[bnode.pos.x][bnode.pos.y]='0';//恢复棋盘
		}
	}
	ai_info->out.count_step=count_step;
	if(ps_stack->stack[0].pos_child.x == -1)//没找到，防止程序闪退
	{
		int i,j;
		for(i=0;i<row;i++)
		{
			for(j=0;j<column;j++)
			{
				if(ChessBoard[i][j]==empty)
				{
					tstep.x=i;tstep.y=j;
					return  tstep;
				}
			}
		}
		tstep.x=-1;tstep.y=-1;//没有点可以走了
		return tstep;
	}
	tstep.x=ps_stack->stack[0].pos_child.x;tstep.y=ps_stack->stack[0].pos_child.y;
	return tstep;
}