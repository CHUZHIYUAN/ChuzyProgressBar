package czy.progressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by george on 2017/2/6.
 * 圆形进度条(可设置增长动画)
 * 有进度点表示
 */

public class ChuzyProgressBar extends View {
    public static final int STROKE = 0;
    public static final int FILL = 1;

    // 圆环的宽度
    private float circleWidth;
    //圆环的颜色
    private int circleColor;
    //圆环进度的颜色
    private int progressColor;
    //最大进度
    private int maxProgress;
    //当前进度
    private int curProgress;
    //增长动画中的临时进度
    private float tempProgress = 0;
    //进度值的颜色
    private int progressValueColor;
    //进度值大小
    private float progressValueSize;
    //是否显示进度值
    private boolean showValue;
    //是否显示进度末端的点
    public boolean isShowPoint = false;
    //进度的风格，实心或者空心
    private int style;


    public ChuzyProgressBar(Context context) {
        this(context, null);
    }

    public ChuzyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChuzyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ChuzyProgressBar);

        //获取自定义属性和默认值
        circleWidth = mTypedArray.getDimension(R.styleable.ChuzyProgressBar_circleWidth, 5);
        circleColor = mTypedArray.getColor(R.styleable.ChuzyProgressBar_circleColor, Color.RED);
        progressColor = mTypedArray.getColor(R.styleable.ChuzyProgressBar_progressColor, Color.GREEN);
        progressValueColor = mTypedArray.getColor(R.styleable.ChuzyProgressBar_progressValueColor, Color.GREEN);
        progressValueSize = mTypedArray.getDimension(R.styleable.ChuzyProgressBar_progressValueSize, 15);
        maxProgress = mTypedArray.getInteger(R.styleable.ChuzyProgressBar_maxProgress, 100);
        showValue = mTypedArray.getBoolean(R.styleable.ChuzyProgressBar_showValue, true);
        style = mTypedArray.getInt(R.styleable.ChuzyProgressBar_style, 0);
        mTypedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        // 画最外层的大圆环
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - 2 * circleWidth); //圆环的半径
        paint.setColor(circleColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(circleWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        //设置进度值的大小颜色，字体样式
        paint.setStrokeWidth(0);
        paint.setColor(progressValueColor);
        paint.setTextSize(progressValueSize);
        paint.setTypeface(Typeface.MONOSPACE);


        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        int percent = (int) (((float) curProgress / (float) maxProgress) * 100);
        //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        float textWidth = paint.measureText(percent + "");

        if (showValue && style == STROKE) {
            //画出进度百分比
            canvas.drawText(percent + "", centre - textWidth / 2, centre + progressValueSize / 2, paint);
            paint.setColor(progressValueColor);
            paint.setTextSize(progressValueSize / 2);
            paint.setTypeface(Typeface.DEFAULT);
            //画 %，字体（%）绘画是以一行字的左下角为基线的（不是左上角，即%的左下角）
            canvas.drawText("%", centre + textWidth / 2, centre + progressValueSize / 8, paint);
        }

        paint.setStrokeWidth(circleWidth); //设置圆环的宽度
        paint.setColor(progressColor);  //设置进度的颜色

        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, 360 * tempProgress / maxProgress, false, paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawArc(oval, 270, 360 * tempProgress / maxProgress, true, paint);
                break;
            }
        }

        // 画进度点   30°角度 的弧度 = 2 * PI / 360 * 30
        if (isShowPoint) {
            //弧度
            int rangle = 0;
            if (tempProgress == 0) {
                rangle = 360 / maxProgress;
            } else {
                rangle = 360 * (int) tempProgress / maxProgress;
            }

            double a = 0.0;//角度
            int pointX = 0;
            int pointY = 0;

            if (rangle > 0 && rangle <= 90) {
                a = 2 * Math.PI / 360 * (90 - rangle);
                pointX = centre + (int) (radius * Math.cos(a));
                pointY = centre - (int) (radius * Math.sin(a));
            } else if (rangle > 90 && rangle <= 180) {
                a = 2 * Math.PI / 360 * (rangle - 90);
                pointX = centre + (int) (radius * Math.cos(a));
                pointY = centre + (int) (radius * Math.sin(a));
            } else if (rangle > 180 && rangle <= 270) {
                a = 2 * Math.PI / 360 * (rangle - 180);
                pointX = centre - (int) (radius * Math.sin(a));
                pointY = centre + (int) (radius * Math.cos(a));
            } else if (rangle > 270 && rangle <= 360) {
                a = 2 * Math.PI / 360 * (rangle - 270);
                pointX = centre - (int) (radius * Math.cos(a));
                pointY = centre - (int) (radius * Math.sin(a));
            }

            paint.setColor(progressColor);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);  //消除锯齿
            Log.d("TAG", "pointX = " + pointX + "||pointY = " + pointY);
            canvas.drawCircle(pointX, pointY, centre - radius, paint);
        }


    }

    public synchronized void isShowPoint(boolean isShow) {
        this.isShowPoint = isShow;
    }

    public synchronized void setProgress(int curProgress) {
        if (curProgress < 0) {
            throw new IllegalArgumentException("curProgress not less than 0");
        }
        if (curProgress > maxProgress) {
            curProgress = maxProgress;
        }
        if (curProgress <= maxProgress) {
            this.curProgress = curProgress;
            postInvalidate();
        }
    }

    //增长值动画
    public void setProgressWithAnimation(int curProgress) {
        this.curProgress = curProgress;
        final ValueAnimator animator = ValueAnimator.ofInt(0, curProgress);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tempProgress = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }


    public int getCricleColor() {
        return circleColor;
    }

    public void setCricleColor(int cricleColor) {
        this.circleColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return progressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.progressColor = cricleProgressColor;
    }

    public int getValueColor() {
        return progressValueColor;
    }

    public void setValueColor(int progressValueColor) {
        this.progressValueColor = progressValueColor;
    }

    public float getValueSize() {
        return progressValueSize;
    }

    public void setValueSize(float progressValueSize) {
        this.progressValueSize = progressValueSize;
    }

    public float getCircleWidth() {
        return circleWidth;
    }

    public void setCircleWidth(float circleWidth) {
        this.circleWidth = circleWidth;
    }


    public synchronized int getMax() {
        return maxProgress;
    }

    public synchronized void setMax(int maxProgress) {
        if (maxProgress < 0) {
            throw new IllegalArgumentException("maxProgress not less than 0");
        }
        this.maxProgress = maxProgress;
    }

    public synchronized int getProgress() {
        return curProgress;
    }
    // case


}
