package cn.edu.bit.cs.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.edu.bit.cs.R;
import cn.edu.bit.cs.utils.StringUtils;

/**
 * 封装AlertDialog简化使用，类似于.NET中的MessageBox 注意：此类必须在UI线程中进行实例化
 * 
 * @author JinXuLiang
 * 
 */
public class MessageBox {

	private AlertDialog.Builder dialogBuilder = null;
	private Context context = null;

	public MessageBox(Context context) {
		dialogBuilder = new AlertDialog.Builder(context);
		this.context = context;
	}

	/**
	 * 使用AlertDialog显示简单的信息，只有一个OK按钮
	 * 
	 * @param message
	 * @param title
	 */
	public void showSimpleMesage(String message, String title) {

		showSimpleMesage(message, title, null);

	}

	/**
	 * 使用AlertDialog显示简单的信息，只有一个OK按钮,可以给它附加一个事件监听器对象，其为null时什么也不做
	 * 
	 * @param message
	 * @param title
	 * @param buttonClickListener
	 */
	@SuppressWarnings("deprecation")
	public void showSimpleMesage(String message, String title,
			MessageBox.IButtonClick buttonClickListener) {
		dialogBuilder.setTitle(title);
		AlertDialog dialog = dialogBuilder.create();

		dialog.setMessage(message);

		final MessageBox.IButtonClick listener = buttonClickListener;
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null)
					listener.doSomething();
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	/**
	 * 使用AlertDialog显示一个单选列表，用户选择的结果直接显示在本方法参数所引用的TextView中
	 * 本方法可以应用于TextView和EditText
	 *
	 * @param items
	 * @param title
	 * @param textViewToBeSetResult
	 */
	public void showSingleChoiceItems(String[] items, String title,
			TextView textViewToBeSetResult) {
		dialogBuilder.setTitle(title);

		final String[] innerItems = items.clone();
		final TextView textView = textViewToBeSetResult;
		// dialogBuilder.setSingleChoiceItems(innerItems, 0,null);
		dialogBuilder.setSingleChoiceItems(innerItems, 0,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						textView.setText(innerItems[which]);
					}
				});
		dialogBuilder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	/**
	 * 显示一个文本数组，其中的选项作为菜单项，点击之后，会调用listener
	 *
	 * @param items
	 * @param title
	 * @param listener
	 */
	public void showMenuItems(String[] items, String title,
			DialogInterface.OnClickListener listener) {
		dialogBuilder.setTitle(title);

		final String[] innerItems = items.clone();

		final DialogInterface.OnClickListener clickListener = listener;
		dialogBuilder.setItems(innerItems,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// 调用外挂的事件响应对象
						if (clickListener != null) {
							clickListener.onClick(dialog, which);
						}
						dialog.dismiss();

					}
				});

		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}

	public void showYesOrNoDialog(String message, String title,
			MessageBox.IButtonClick yesButtonListener,
			MessageBox.IButtonClick noButtonListener) {
		showTwoButtonDialog(message, title, "是", "否", yesButtonListener,
				noButtonListener);
	}

	public void showOKOrCancelDialog(String message, String title,
			MessageBox.IButtonClick okButtonListener,
			MessageBox.IButtonClick cancelButtonListener) {
		showTwoButtonDialog(message, title, "确定", "取消", okButtonListener,
				cancelButtonListener);
	}

	/**
	 * 显示一个拥有两个按钮的对话框，两个按钮文本是可以定制的
	 *
	 * @param message
	 * @param title
	 * @param firstButtonText
	 * @param secondButtonText
	 * @param okButtonListener
	 * @param cancelButtonListener
	 */
	private void showTwoButtonDialog(String message, String title,
			String firstButtonText, String secondButtonText,
			MessageBox.IButtonClick okButtonListener,
			MessageBox.IButtonClick cancelButtonListener) {
		dialogBuilder.setTitle(title);
		dialogBuilder.setMessage(message);
		final MessageBox.IButtonClick okListener = okButtonListener;
		dialogBuilder.setPositiveButton(firstButtonText,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (okListener != null)
							okListener.doSomething();
						dialog.dismiss();

					}
				});
		final MessageBox.IButtonClick cancelListener = cancelButtonListener;
		dialogBuilder.setNegativeButton(secondButtonText,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (cancelListener != null)
							cancelListener.doSomething();
						dialog.dismiss();
					}
				});
		dialogBuilder.create().show();
	}

	public void showInputDialog(String dialogTitle, String inputDescription,
			String defaultText,
			final MessageBox.ISimpleInputDialogButtonClick okButtonListener,
			final MessageBox.ISimpleInputDialogButtonClick cancelButtonListener) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View dialogView = inflater.inflate(R.layout.inputdialog, null);

		final TextView messageTextView = (TextView) dialogView
				.findViewById(R.id.inputDialog_tvMessage);
		final EditText userInputEditText = (EditText) dialogView
				.findViewById(R.id.inputDialog_edtUserInput);

		
		dialogBuilder.setTitle(dialogTitle);
		messageTextView.setText(inputDescription);
		
		if(StringUtils.isNotNullOrEmpty(defaultText)){
			userInputEditText.setText(defaultText);
			
		}

		dialogBuilder.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (okButtonListener != null
								&& userInputEditText != null
								&& StringUtils
										.isNotNullOrEmpty(userInputEditText
                                                .getText().toString())) {
							okButtonListener.doSomething(userInputEditText
									.getText().toString());
						}

					}
				});

		dialogBuilder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (cancelButtonListener != null) {
							if (userInputEditText != null
									&& StringUtils
											.isNotNullOrEmpty(userInputEditText
                                                    .getText().toString())) {
								cancelButtonListener
										.doSomething(userInputEditText
												.getText().toString());
							} else {
								cancelButtonListener.doSomething("");
							}

						}

					}
				});
		dialogBuilder.setView(dialogView);
		dialogBuilder.create().show();

	}

	/**
	 * 用于指定响应消息框按钮单击事件的“外挂代码”
	 * 
	 * @author JinXuLiang
	 * 
	 */
	public interface IButtonClick {
		void doSomething();
	}

	/**
	 * 用于指定响应简单输入框（只有一个文本框）按钮单击事件的响应代码
	 * 
	 * @author JinXuLiang
	 * 
	 */
	public interface ISimpleInputDialogButtonClick {
		void doSomething(String userInput);
	}
}
