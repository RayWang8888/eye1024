package com.raywang.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 宽和高按照一定比例的图片控件
 * Created by Ray on 2015/7/1.
 */
public class ProportionImageView extends ImageView{



    public ProportionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProportionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProportionImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        width = (int) (height * 1.4f);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
