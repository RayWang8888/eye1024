package com.raywang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.raywang.rayutils.R;

/**
 * 自定义的画水平虚线的控件
 * @author Ray Wang
 * @date 2015年6月8日14:27:56
 * @version 1.0
 */
public class HorizontalLine extends View {
    /** 空白地方的长度*/
    private int dashGap = 1;
    /** 实际画出来的线的长度*/
    private int dashWidth = 1;
    /** 颜色*/
    private int color = Color.BLACK;
    /** 线的宽度*/
    private int dashGapWidth = 1;

    public HorizontalLine(Context context){
        super(context);
    }
    public HorizontalLine(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }
    public HorizontalLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalLine);
        dashGap = a.getDimensionPixelSize(0, 1);
        dashWidth = a.getDimensionPixelSize(1, 1);
        dashGapWidth = a.getDimensionPixelSize(2, 1);
        color = a.getColor(3, Color.BLACK);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);//空心
        paint.setColor(color);
        paint.setStrokeWidth(dashGapWidth);
        Path path = new Path();

        //通过moveto，lineto的x，y坐标确定虚线实横，纵，还是倾斜
        path.moveTo(0, 0);//Set the beginning of the next contour to the point (x,y)
        path.lineTo(getWidth(),0);//Add a line from the last point to the specified point (x,y).
        //DashPathEffect  可以使用DashPathEffect来创建一个虚线的轮廓(短横线/小圆点)，而不是使用实线
        //float[] { 5, 5, 5, 5 }值控制虚线间距，密度
        PathEffect effects = new DashPathEffect(new float[]{dashWidth,dashGap,dashWidth,dashGap}, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}
