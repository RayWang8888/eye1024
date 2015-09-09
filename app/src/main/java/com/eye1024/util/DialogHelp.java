package com.eye1024.util;

import android.content.Context;
import android.view.View;

import com.eye1024.R;
import com.eye1024.view.Dialog;
import com.eye1024.view.SimpleDialog;

/**
 * 弹出对话框的一些相关公共方法
 * Created by Ray on 2015/8/31.
 */
public class DialogHelp {



    /**
     * 显示普通的Dailog对象
     * @param title dailog的标题
     * @param msg 消息
     * @param positive 确定按钮显示文字
     * @param negative 取消按钮显示文章
     * @param context 程序上下文
     * @param listener 按钮点击监听
     * @return
     */
    public static Dialog showOkAndCancel(String title,String msg,String positive,String negative,
                                         Context context,View.OnClickListener listener){
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight);
        builder.message(msg).
                title(title)
                .positiveAction(positive)
                .negativeAction(negative);
        Dialog dialog = builder.build(context);
        dialog.findViewById(Dialog.ACTION_POSITIVE).setOnClickListener(listener);
        dialog.findViewById(Dialog.ACTION_NEGATIVE).setOnClickListener(listener);
        dialog.show();
        return dialog;
    }


}
