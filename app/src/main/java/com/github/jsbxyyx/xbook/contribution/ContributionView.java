package com.github.jsbxyyx.xbook.contribution;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.github.jsbxyyx.xbook.common.LogUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 贡献图
 * https://github.com/shaoweijie/contribution-view
 */
public class ContributionView extends View {

    private static final String TAG = "ContributionView";

    private OnItemClickListener listener;
    private ContributionConfig config;
    private List<ContributionItem> data;
    private Date startDate;
    private int selectItem = -1;

    public ContributionView(Context context) {
        super(context);
    }

    public ContributionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContributionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContributionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ContributionConfig getConfig() {
        return config;
    }

    public ContributionView setConfig(ContributionConfig configs) {
        config = configs;

        config.setPaints(new Paint[config.getRankColor().length]);
        for (int i = 0; i < config.getRankColor().length; i++) {
            config.getPaints()[i] = new Paint();
            config.getPaints()[i].setColor(config.getRankColor()[i]);
        }

        config.getTxtPaint().setColor(config.getTxtColor());

        config.getBorderPaint().setStyle(Paint.Style.STROKE);
        config.getBorderPaint().setColor(config.getBorderColor());
        config.getBorderPaint().setStrokeWidth(config.getBorderWidth());

        config.getSelectPaint().setColor(config.getSelectColor());
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public ContributionView setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public List<ContributionItem> getData() {
        return data;
    }

    /**
     * 设置数据，更新布局
     *
     * @param startDate 开始日期
     * @param data      连续日期的条目
     */
    public void setData(Date startDate, List<ContributionItem> data) {
        if (config == null) {
            setConfig(new ContributionConfig());
        }
        this.setData(startDate, data, getConfig());
    }

    /**
     * 设置数据，更新布局
     *
     * @param startDate 开始日期
     * @param data      连续日期的条目
     * @param config    配置文件
     */
    public void setData(Date startDate, List<ContributionItem> data, ContributionConfig config) {
        setConfig(config);
        this.startDate = startDate;
        this.data = data;
        Collections.sort(this.data, new Comparator<ContributionItem>() {
            @Override
            public int compare(ContributionItem t0, ContributionItem t1) {
                return t0.getTime().compareTo(t1.getTime());
            }
        });
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        // 1获取with height 以及mode
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        int lineHeight = 300;
        if (data != null && data.size() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int week = calendar.get(Calendar.DAY_OF_WEEK);
            if (config.getStartOfWeek() == Calendar.MONDAY) {
                if (week == Calendar.SUNDAY) {
                    week = 7;
                } else {
                    week--;
                }
            }
            config.setStartEmpty(week - 1);//开始日期前补充空白
            int lineNumber = (data.size() + config.getStartEmpty()) / 7;
            if ((data.size() + config.getStartEmpty()) % 7 > 0) {
                lineNumber++;//末尾行数+1
            }
            int lineWidth = widthSize / (lineNumber + 2);//带padding的单行宽度
            lineHeight = (int) (lineWidth * 8.2);
            config.setItemWidth(lineWidth - config.getPadding());
            config.getTxtPaint().setTextSize(lineWidth - config.getPadding());
            config.setTxtSize(lineWidth - config.getPadding());
        }
        heightSize = lineHeight + getPaddingTop() + getPaddingBottom();
//        widthSize = widthSize + getPaddingLeft() + getPaddingRight();

        int measuredHeight = resolveSize(heightSize, heightMeasureSpec);//这里讲解解析高度
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight);//这里讲解设置度量
    }

    private int getLeftTextWidth() {
        return (config.getItemWidth() + config.getPadding()) * 2;
    }

    private int getTopTextHeight() {
        return (int) ((config.getItemWidth() + config.getPadding()) * 1.2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.size() == 0) {
            return;
        }
        int xOffset = getPaddingLeft() + getLeftTextWidth();
        int yOffset = getPaddingTop() + getTopTextHeight();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        long startTime = calendar.getTimeInMillis();
        long itemTime;
        int day;
        int perWidth = config.getItemWidth() + config.getPadding();
        for (int i = 0; i < data.size(); i++) {
            ContributionItem item = data.get(i);
            calendar.setTime(item.getTime());
            itemTime = calendar.getTimeInMillis();
            day = (int) ((itemTime - startTime) / 1000 / 3600 / 24) + config.getStartEmpty();
            item.setCol(day / 7);
            item.setRow(day % 7);
            RectF rect = new RectF(xOffset + item.getCol() * perWidth,
                    yOffset + item.getRow() * perWidth,
                    xOffset + item.getCol() * perWidth + config.getItemWidth(),
                    yOffset + item.getRow() * perWidth + config.getItemWidth());
            if (i == selectItem) {
                canvas.drawRoundRect(rect, config.getItemRound(), config.getItemRound(), config.getSelectPaint());
            } else {
                canvas.drawRoundRect(rect, config.getItemRound(), config.getItemRound(), getColorPaintByNumber(item));
            }
            canvas.drawRoundRect(rect, config.getItemRound(), config.getItemRound(), config.getBorderPaint());
            if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                canvas.drawText(config.getMonths()[calendar.get(Calendar.MONTH)],
                        xOffset + item.getCol() * (config.getItemWidth() + config.getPadding()),
                        config.getTxtSize(),
                        config.getTxtPaint());
            }
        }
        for (int i = 0; i < 7; i++) {
            if (config.getWeeks()[i] == null || config.getWeeks()[i].length() == 0) {
                continue;
            }
            canvas.drawText(config.getWeeks()[i],
                    0, i * (config.getItemWidth() + config.getPadding()) + getTopTextHeight() + config.getTxtSize(),
                    config.getTxtPaint());
        }
    }

    private Paint getColorPaintByNumber(ContributionItem item) {
        int current = 0;
        for (int i = 0; i < config.getRank().length; i++) {
            if (item.getNumber() >= config.getRank()[i]) {
                current = i;
            } else {
                break;
            }
        }
        return config.getPaints()[current];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener == null) {
            return super.onTouchEvent(event);
        }
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event);
        }
        if (event.getX() < getPaddingLeft() + getLeftTextWidth() ||
                event.getY() < getPaddingTop() + getTopTextHeight()) {
            return super.onTouchEvent(event);
        }
        int col = (int) ((event.getX() - getPaddingLeft() - getLeftTextWidth()) /
                (config.getItemWidth() + config.getPadding()));
        int row = (int) ((event.getY() - getPaddingTop() - getTopTextHeight()) /
                (config.getItemWidth() + config.getPadding()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        selectItem = col * 7 + row - (week - 1);
        if (selectItem >= data.size() || selectItem < 0) {
            return super.onTouchEvent(event);
        }
        LogUtil.d(TAG, "click: (%.0f, %.0f) (%d, %d) %d", event.getX(), event.getY(), row, col, selectItem);
        listener.onClick(selectItem, data.get(selectItem));
        invalidate();
        return true;
    }

    public void setOnItemClick(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position, ContributionItem item);
    }
}
