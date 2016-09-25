package com.devseed.fiveinarow.adapter;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devseed.fiveinarow.ChessKernel;
import com.devseed.fiveinarow.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by misaki on 2015/11/17.
 */
public class ChessStepAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    public int selectedPos=-1;//listview中通过onItemClick得到的当前选中项
    int ai_seq=0;//0没ai，1ai先下，2人先下

    int color_selectd=Color.RED;
    int color_unselected=Color.rgb(0x06,0xfb,0xff);
    // int color_player=Color.rgb(0xff,0xab,0x00);
    //int color_ai=Color.rgb(0xff, 0x00, 0xdd);
    int color_ai=Color.rgb(0xff, 0x00, 0xdd);
    int color_player=Color.rgb(0xd8,0xd3,0xff);

    protected class ItemViewHolder {//防止过多次调用乱序
        TextView text_num;
        TextView text_pos;
        ImageView img_chess;
    }


    public class StepNode {
        int x, y;//x，y分别对应二维数组值
        char color;
        public StepNode(int x,int y,char color) {
            this.x=x;this.y=y;this.color=color;
        }
        public StepNode(){}
    }
    List<StepNode> steplog;
    public ChessStepAdapter(int ai_seq){
        steplog=new ArrayList<StepNode>();
        this.ai_seq=ai_seq;
    }
    public void setSelected(int position){
        selectedPos=position;
        notifyDataSetChanged();
    }
    public void clear(){
        steplog.clear();
        selectedPos=-1;
        notifyDataSetChanged();
    }
    public void pushStep(int x,int y,char color){
        steplog.add(new StepNode(x,y,color));
    }
    public void popnSteps(int n){
        if(n>=steplog.size()){
            steplog.clear();
            return;
        }
        int end=steplog.size();
        for(int i=0;i<n;i++){
            steplog.remove(end-i-1);
        }
    }
    @Override
    public int getCount() {
        return steplog.size();
    }

    @Override
    public Object getItem(int position) {

        return steplog.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout rootview=null;
        ItemViewHolder holder=null;
        if(convertView==null){ // 第一次调用时，什么UI对象也没有创建
            // 创建LayoutInflater对象，准备用于从XML布局文件中创建对象
            holder=new ItemViewHolder();
            if(inflater==null)
                inflater = LayoutInflater.from(parent.getContext());
            // 从用户定义的布局文件中实例化顶层对象
            rootview= (LinearLayout) inflater.inflate(R.layout.list_chessstep, null);
            holder.text_num=(TextView)rootview.findViewById(R.id.text_num);
            holder.text_pos =(TextView)rootview.findViewById(R.id.text_pos);
            holder.img_chess=(ImageView)rootview.findViewById(R.id.imageView);
            rootview.setTag(holder);
        }
        else {// 后续的调用，UI对象可重用
            rootview=(LinearLayout)convertView;
            holder=(ItemViewHolder)convertView.getTag();
        }
        //convertView中的引用在holder中
        holder.text_num.setText(String.format("%03d",position+1));//textview中空格和数字不等宽

        StepNode tnode = steplog.get(position);
        holder.text_pos.setText(String.format("(%d,%c)",tnode.x+1,tnode.y+0x41));
        if(position==selectedPos) holder.text_pos.setBackgroundColor(color_selectd);
        else holder.text_pos.setBackgroundColor(color_unselected);

        if(ai_seq==0) holder.img_chess.setBackgroundColor(color_player);
        else if(ai_seq==1){
            if(position%2==0) holder.img_chess.setBackgroundColor(color_ai);
            else holder.img_chess.setBackgroundColor(color_player);
        }
        else if(ai_seq==2){
            if(position%2==1) holder.img_chess.setBackgroundColor(color_ai);
            else holder.img_chess.setBackgroundColor(color_player);
        }
        Drawable chess=null;
        if(tnode.color==ChessKernel.black)
           chess= parent.getResources().getDrawable(R.drawable.chess_black);
        else  if(tnode.color== ChessKernel.white) chess= parent.getResources().getDrawable(R.drawable.chess_white);
        holder.img_chess.setImageDrawable(chess);

        return rootview;
    }
}
