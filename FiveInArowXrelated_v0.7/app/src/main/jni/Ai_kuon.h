#include "stdFunc.h"
#ifndef _AI_kuon_h
#define _AI_kuon_h

int evaluateBoard(char ChessBoard[][MAX_COLUMN],int *EvaluateTable,Edge edge,int row,int column,enum ChessType color_max);
StepNode AI_kuon_pos(char ChessBoard[][MAX_COLUMN],
								int *EvaluateTable,
								int row,
								int column,
								enum ChessType color_max);
StepNode AI_kuon_gametree(char ChessBoard[][MAX_COLUMN],
							int *EvaluateTable,
							ChessAiExtend *ai_info,
							enum ChessType color_max);
#endif