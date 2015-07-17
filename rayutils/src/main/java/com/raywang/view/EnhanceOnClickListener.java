package com.raywang.view;

import android.view.View;
import android.view.View.OnClickListener;
/**
 * 增强的点击监听，点击后会禁用控件，防止点击过快出现2次事件
 * 事件处理完成后会恢复控件的启用状态
 * @author Ray
 * @version 1.0
 */
public abstract class EnhanceOnClickListener implements OnClickListener {

	public void onClick(View v) {
		v.setEnabled(false);
		click(v.getId());
		v.setEnabled(true);
	}

	public abstract void click(int id);
	
	
}
