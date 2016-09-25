#define MAX_ROW 15
#define MAX_COLUMN 15
#define MAX_STEP MAX_ROW*MAX_COLUMN
#define MAX_STACK 50
#define MAX(a,b) (a>b ? a:b)
#define EVA_TYPE 30
#define INF 9999999

#ifndef _stdValue_h
#define _stdValue_h
enum ChessType{white='H',black='*',empty='0'};
typedef struct
{
	int x1;
	int x2;
	int y1;
	int y2;
}Edge;
typedef struct
{
	int x;
	int y;
}Position;
typedef struct
{
	int x;
	int y;
	enum ChessType color;
}StepNode;
typedef struct
{
	StepNode step[MAX_STEP+10];
	int count;
}ChessStep; 
typedef int (*PF_EVA_POS)//指向点评分
	(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,int row,int column,StepNode tstep);
typedef int (*PF_EVA_BOARD)//指向局势评分函数
	(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,Edge edge,int row,int column,enum ChessType color);
typedef struct
{
	Position pos;
	Position pos_child;//MAX,MIN搜索的子节点
	Edge edge;
	int score;
}ScoreStackNode;
typedef struct
{
	ScoreStackNode stack[MAX_STACK];
	int count;
	int capacity;
}ScoreStack;//遍历博弈树记录分数的堆栈
typedef struct
{
	//输入
	struct 
	{
		Edge edge;//搜索坐标限制
		int edge_dis;//距离边界距离
		int row,column;
		int max_depth;//最大树深度（一定是偶数）
		int max_count;//最大搜索数量
		PF_EVA_BOARD evel_b;//每个点MAX,MIN整合的评分回掉函数
		ScoreStack *ps_stack;
	}in;
	//输出
	struct
	{
		int count_step;//迭代步数
	}out;
	//为了方便拓展 
	void *other;
}ChessAiExtend;
#endif