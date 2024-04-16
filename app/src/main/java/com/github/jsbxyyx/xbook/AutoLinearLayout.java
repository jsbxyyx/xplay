package com.github.jsbxyyx.xbook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.github.jsbxyyx.xbook.common.LogUtil;

import java.util.Hashtable;

/**
 * @author jsbxyyx
 */
public class AutoLinearLayout extends LinearLayout {

    int mLeft, mRight, mTop, mBottom;
    Hashtable map = new Hashtable();

    public AutoLinearLayout(Context context) {
        super(context);
    }

    public AutoLinearLayout(Context context, int horizontalSpacing, int verticalSpacing) {
        super(context);
    }

    public AutoLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();
        int mX = 0;
        int mY = 0;
        mLeft = 0;
        mRight = 0;
        mTop = 5;
        mBottom = 0;

        int j = 0;

        for (int i = 0; i < mCount; i++) {
            final View child = getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int childW = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            int childH = child.getMeasuredHeight();
            mX += childW;

            Position position = new Position();
            mLeft = getPosition(i - j, i);
            mRight = mLeft + child.getMeasuredWidth();
            if (mX >= mWidth) {
                mX = childW;
                mY += childH;
                j = i;
                mLeft = 0;
                mRight = mLeft + child.getMeasuredWidth();
                mTop = mY + params.topMargin;
                // PS：如果发现高度还是有问题就得自己再细调了
            }
            mBottom = mTop + child.getMeasuredHeight() + params.bottomMargin;
            mY = mTop;
            position.left = mLeft;
            position.top = mTop + 3;
            position.right = mRight;
            position.bottom = mBottom;
            map.put(child, position);
        }
        setMeasuredDimension(mWidth, mBottom);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(0, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = (Position) map.get(child);
            if (pos != null) {
                child.layout(pos.left, pos.top, pos.right, pos.bottom);
            } else {
                LogUtil.i(getClass().getSimpleName(), "error");
            }
        }
    }

    private static class Position {
        int left, top, right, bottom;
    }

    public int getPosition(int indexInRow, int childIndex) {
        if (indexInRow > 0) {
            return getPosition(indexInRow - 1, childIndex - 1) + getChildAt(childIndex - 1).getMeasuredWidth() + 30;
        }
        return getPaddingLeft();
    }

}