package cn.edu.bit.cs.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bit.cs.R;

/**
 * 一个水平滚动的列表，点击时文本显示为红色，具有圆角背景
 * @author JinXuLiang
 *
 */
public class HorizontalScrollList extends HorizontalScrollView {

	private LinearLayout itemContainer=null;
	
	private List<TextView> itemTextViews=null;
	/**
	 * 当前选中项的索引
	 */
	private int selectedIndex=-1;
	
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	/**
	 * 用于选择当前激活项中左边的一项
	 * 如果没有激活项，返回-1
	 * 如果激活项己经是第1项，则当前项不改变，返回0
	 * 否则，返回激活的左边一项的索引
	 * @return
	 */
	public int chooseLeftItemOfCurrentItem(){
		if(selectedIndex==-1)
			return -1;
		if(selectedIndex==0){
			scrollToShowItem(0);
			return 0;
		}
		
			selectedIndex--;
			setSelection(selectedIndex);
			scrollToShowItem(selectedIndex);
			//printTextViewWidth();
		return selectedIndex;
		
	}
	/**
	 * 判断指定位置的列表项是否在屏幕上可见
	 * 可见，返回true
	 * @param index
	 * @return
	 */
	public boolean isVisiable(int index){
		int width=getWidth();
		int scrollx=getScrollX();
		int left=getLeftXOfItem(index);
		int Right=getRightXOfItem(index);
		if(left>=scrollx && left<=scrollx+width
				|| Right>=scrollx && Right<=scrollx+width){
			return true;
		}
		return false;
	}
	/**
	 * 获取指定索引的控件左端点的横坐标
	 * @param index
	 * @return
	 */
	private int getLeftXOfItem(int index){
		int left=0;
		for(int i=0;i<index;i++){
			left+=itemTextViews.get(i).getWidth();
		}
		return left;
	}
	/**
	 * 获取指定索引的控件右端点的横坐标
	 * @param index
	 * @return
	 */
	private int getRightXOfItem(int index){
		int left=0;
		for(int i=0;i<=index;i++){
			left+=itemTextViews.get(i).getWidth();
		}
		return left;
	}
	/**
	 * 滚动显示指定的位置的项目，如果己经显示完整了，不作任何事
	 * @param index
	 */
	public void scrollToShowItem(int index){
		int width=getWidth();
		int scrollx=getScrollX();
		int leftX=getLeftXOfItem(index);
		int rightX=getRightXOfItem(index);
		if(leftX<scrollx){
			smoothScrollTo(leftX, 0);
		}
		if(rightX>scrollx+width){
			int deltaX=rightX-(scrollx+width);
			smoothScrollTo(scrollx+deltaX, 0);
			
		}
	}
	
	/**
	 * 用于选择当前激活项中右边的一项
	 * 如果没有激活项，返回-1
	 * 如果激活项己经是最后一项，则当前项不改变，返回当前索引
	 * 否则，返回激活的右边一项的索引
	 * @return
	 */
	public int chooseRightItemOfCurrentItem(){
		if(selectedIndex==-1)
			return -1;
		if(selectedIndex==itemTextViews.size()-1){
			scrollToShowItem(selectedIndex);
			return selectedIndex;
		}
		
			selectedIndex++;
			setSelection(selectedIndex);
			scrollToShowItem(selectedIndex);
		return selectedIndex;
		
	}
	
	public HorizontalScrollList(Context context, AttributeSet attrs,
                                int defStyle) {
		super(context, attrs, defStyle);
		initItemContainer(context);
	}
	public HorizontalScrollList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initItemContainer(context);
	}
	public HorizontalScrollList(Context context) {
		super(context);
		initItemContainer(context);
		
	}
	/**
	 * 从布局文件中加载ScrollView的列表容器，一个水平排列的LinearLayout
	 * @param context
	 */
	private void initItemContainer(Context context) {
		itemContainer=(LinearLayout)inflate(context, R.layout.horizontal_scrollist, null);
		
		addView(itemContainer);
		
	}
	/**
	 * 传入一个字串数组，由它来创建TextView对象，将清除己有的所有表项
	 * @param items
	 */
	public void loadData(String[] items) {
		if(itemTextViews!=null){
			itemTextViews.clear();
		}
		else {
			itemTextViews=new ArrayList<TextView>();
		}
		itemContainer.removeAllViews();
		
		for(int i=0;i<items.length;i++){
			View itemParentView=inflate(getContext(), R.layout.horizontal_scrolllist_item, null);
			TextView textView=(TextView)(itemParentView.findViewById(R.id.horiznotal_list_txtItem));
			textView.setText(items[i]);
			//记录下本项的索引
			textView.setTag(i);
			textView.setOnClickListener(textViewClickListener);
			itemTextViews.add(textView);
			itemContainer.addView(itemParentView);
		}
	}
	
	private OnClickListener textViewClickListener=new OnClickListener() {
		
		public void onClick(View v) {
			for (TextView textView: itemTextViews) {
				textView.setTextColor(Color.BLACK);
			}
			TextView tv=(TextView)v;
			tv.setTextColor(Color.RED);
			selectedIndex=Integer.parseInt(tv.getTag().toString());
			if(itemClickResponser!=null){
				itemClickResponser.onClick(v);
			}
		}
	};
	/**
	 * 外界提供的事件响应方法
	 */
	private ItemClickResponser itemClickResponser=null;
	
	
	public ItemClickResponser getItemClickResponser() {
		return itemClickResponser;
	}
	public void setItemClickResponser(ItemClickResponser responsor) {
		this.itemClickResponser = responsor;
	}

	/**
	 * 设定第几项选中
	 * @param index
	 */
	public void setSelection(int index) {
		if(index>itemTextViews.size()-1){
			return ;
		}
		selectedIndex=index;
		for (TextView textView: itemTextViews) {
			textView.setTextColor(Color.BLACK);
		}
		TextView curTextView=itemTextViews.get(index);
		curTextView.setTextColor(Color.RED);
//		if(itemClickResponser!=null){
//			itemClickResponser.onClick(curTextView);
//		}
		
	}

	/**
	 * 对应水平滚动列表项的单击事件
	 * @author JinXuLiang
	 *
	 */
	public interface ItemClickResponser{
		void onClick(View item);
	}
	
	

	
}
