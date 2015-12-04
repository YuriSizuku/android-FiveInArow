package cn.edu.bit.cs.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

import cn.edu.bit.cs.R;

/**
 * 一个能被选中的RelativeLayout（实现Checkable接口），主要用于ListView的单选多选状态显示
 * @author jinxuliang
 *
 */
public class SelectableRelativeLayout extends RelativeLayout implements  Checkable{

	private int selectedBackgroundColor;
		
		public SelectableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			 TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.selectableRelativeLayout);
		     selectedBackgroundColor=typedArray.getColor(R.styleable.selectableRelativeLayout_selectedBackground, Color.argb(255, 0x00, 0x33, 0x99));
		     typedArray.recycle();
		}
		public SelectableRelativeLayout(Context context) {
			super(context);
			
		}
		public SelectableRelativeLayout(Context context, AttributeSet attrs) {
	        super(context, attrs, 0);
	        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.selectableRelativeLayout);
	        selectedBackgroundColor=typedArray.getColor(R.styleable.selectableRelativeLayout_selectedBackground, Color.argb(255, 0x00, 0x33, 0x99));
	        typedArray.recycle();
	    }
		private boolean _isChecked=false;
		public void setChecked(boolean checked) {
			_isChecked=checked;
			if(_isChecked){
				setBackgroundColor(selectedBackgroundColor);
			}
			else {
				setBackgroundDrawable(null);
			}
		}
		public boolean isChecked() {
			return _isChecked;
		}
		public void toggle() {
			_isChecked=!_isChecked;
			if(_isChecked){
				setBackgroundColor(selectedBackgroundColor);
			}
			else {


                setBackgroundDrawable(null);
			}
		}
		
	}
