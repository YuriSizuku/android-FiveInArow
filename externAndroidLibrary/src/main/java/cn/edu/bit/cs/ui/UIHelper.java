package cn.edu.bit.cs.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * 封装一些与UI设计相关的一些类
 * @author JinXuLiang
 * 
 */
public class UIHelper {
	
	/**
	 * 获取手机屏幕的宽（像素为单位）
	 * @param activity
	 * @return
	 */
	public static int getScreenWidth(Activity activity){
		
		DisplayMetrics metrics=new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);  //相关信息被填充到了metrics中。
        return metrics.widthPixels;
	}
	/**
	 * 获取手机屏幕的高（像素为单位）
	 * @param activity
	 * @return
	 */
	public static int getScreenHeight(Activity activity){
		DisplayMetrics metrics=new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);  //相关信息被填充到了metrics中。
        return metrics.heightPixels;
	}
	

	/**
	 * 按照Toast.LENGTH_LONG显示一则消息，不是线程安全的
	 * @param context
	 * @param message
	 */
	public static void toastShowMessageLong(Context context,String message){
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
	/**
	 * 按照Toast.LENGTH_SHORT显示一则消息，不是线程安全的
	 * @param context
	 * @param message
	 */
	public static void toastShowMessageShort(Context context,String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 取消状态栏提示
	 */
	public static void cancelNotificationIcon(Context context,int notificationID) {
		// 信息已经处理过了，可以取消通知信息，让用户不再反复点击
		NotificationManager notificationManager = (NotificationManager)(context.getSystemService(Context.NOTIFICATION_SERVICE));
		notificationManager.cancel(notificationID);
	}
	/**
	 * 将指定字符串的指定范围[beginIndex,endIndex]内的字符设置为指定的颜色（或粗体）
	 * @param source
	 * @param beginIndex
	 * @param endIndex
	 * @param color
	 * @param bold
	 * @return
	 */
	public static SpannableString getColorString(CharSequence source,int beginIndex,int endIndex,int color,boolean bold){
		SpannableString  spannableString=new SpannableString(source);
		ForegroundColorSpan colorSpan=new ForegroundColorSpan(color);
		if(endIndex+1<source.length()){
			endIndex++;
		}
		if(bold){
			StyleSpan styleSpan=new StyleSpan(android.graphics.Typeface.BOLD);
			spannableString.setSpan(styleSpan, beginIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		}
		spannableString.setSpan(colorSpan, beginIndex, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return spannableString;
	}
}
