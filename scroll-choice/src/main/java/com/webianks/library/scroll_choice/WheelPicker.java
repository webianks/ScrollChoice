package com.webianks.library.scroll_choice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by R Ankit on 17-02-2017.
 */
public abstract class WheelPicker extends View {

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;

    public static final int ALIGN_CENTER = 0;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;

    private final Handler handler = new Handler();
    private final Paint paintBackground;

    private final Paint paint;
    private final Scroller scroller;
    private VelocityTracker tracker;

    private OnItemSelectedListener onItemSelectedListener;
    private OnWheelChangeListener onWheelChangeListener;

    private final Rect rectDrawn;
    private final Rect rectIndicatorHead;
    private final Rect rectIndicatorFoot;
    private final Rect rectCurrentItem;

    private BaseAdapter adapter;
    private String maxWidthText;

    private int mVisibleItemCount, mDrawnItemCount;
    private int mHalfDrawnItemCount;
    private int mTextMaxWidth, mTextMaxHeight;
    private int mItemTextColor, mSelectedItemTextColor;
    private int mItemTextSize;
    private int mIndicatorSize;
    private int mIndicatorColor;
    private int mItemSpace;
    private int mItemAlign;
    private int mItemHeight, mHalfItemHeight;
    private int mHalfWheelHeight;
    private int selectedItemPosition;
    private int currentItemPosition;
    private int minFlingY, maxFlingY;
    private int minimumVelocity = 50, maximumVelocity = 8000;
    private int wheelCenterX, wheelCenterY;
    private int drawnCenterX, drawnCenterY;
    private int scrollOffsetY;
    private int textMaxWidthPosition;
    private int lastPointY;
    private int downPointY;
    private int touchSlop = 8;

    private boolean hasIndicator;
    private boolean hasAtmospheric;

    private boolean isClick;
    private boolean isForceFinishScroll;

    private final int backgroundColor;
    private final int backgroundOfSelectedItem;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null == adapter) return;
            final int itemCount = adapter.getItemCount();
            if (itemCount == 0) return;
            if (scroller.isFinished() && !isForceFinishScroll) {
                if (mItemHeight == 0) return;
                int position = (-scrollOffsetY / mItemHeight + selectedItemPosition) % itemCount;
                position = position < 0 ? position + itemCount : position;
                currentItemPosition = position;
                onItemSelected();
                if (null != onWheelChangeListener) {
                    onWheelChangeListener.onWheelSelected(position);
                    onWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_IDLE);
                }
            }
            if (scroller.computeScrollOffset()) {
                if (null != onWheelChangeListener) {
                    onWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_SCROLLING);
                }

                scrollOffsetY = scroller.getCurrY();

                int position = (-scrollOffsetY / mItemHeight + selectedItemPosition) % itemCount;
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onCurrentItemOfScroll(WheelPicker.this, position);
                }
                onItemCurrentScroll(position, adapter.getItem(position));

                postInvalidate();
                handler.postDelayed(this, 16);
            }
        }
    };

    public WheelPicker(Context context) {
        this(context, null);
    }

    public WheelPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        adapter = new Adapter();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelPicker);

        mItemTextSize = a.getDimensionPixelSize(R.styleable.WheelPicker_scroll_item_text_size,
                getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        mVisibleItemCount = a.getInt(R.styleable.WheelPicker_scroll_visible_item_count, 7);
        selectedItemPosition = a.getInt(R.styleable.WheelPicker_scroll_selected_item_position, 0);
        textMaxWidthPosition = a.getInt(R.styleable.WheelPicker_scroll_maximum_width_text_position, -1);
        maxWidthText = a.getString(R.styleable.WheelPicker_scroll_maximum_width_text);
        mSelectedItemTextColor = a.getColor(R.styleable.WheelPicker_scroll_selected_item_text_color, -1);
        mItemTextColor = a.getColor(R.styleable.WheelPicker_scroll_item_text_color, 0xFF424242);
        backgroundColor = a.getColor(R.styleable.WheelPicker_scroll_background_color, 0xFFF5F5F5);
        backgroundOfSelectedItem = a.getColor(R.styleable.WheelPicker_scroll_selected_item_background, 0xFFFFFFFF);
        mItemSpace = a.getDimensionPixelSize(R.styleable.WheelPicker_scroll_item_space,
                getResources().getDimensionPixelSize(R.dimen.WheelItemSpace));
        hasIndicator = a.getBoolean(R.styleable.WheelPicker_scroll_indicator, false);
        mIndicatorColor = a.getColor(R.styleable.WheelPicker_scroll_indicator_color, 0xFFDDDDDD);
        mIndicatorSize = a.getDimensionPixelSize(R.styleable.WheelPicker_scroll_indicator_size,
                getResources().getDimensionPixelSize(R.dimen.WheelIndicatorSize));
        hasAtmospheric = a.getBoolean(R.styleable.WheelPicker_scroll_atmospheric, false);
        mItemAlign = a.getInt(R.styleable.WheelPicker_scroll_item_align, ALIGN_CENTER);
        a.recycle();

        updateVisibleItemCount();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG );
        paint.setTextSize(mItemTextSize);

        updateItemTextAlign();

        computeTextSize();

        scroller = new Scroller(getContext());


        ViewConfiguration conf = ViewConfiguration.get(getContext());
        minimumVelocity = conf.getScaledMinimumFlingVelocity();
        maximumVelocity = conf.getScaledMaximumFlingVelocity();
        touchSlop = conf.getScaledTouchSlop();
        rectDrawn = new Rect();

        rectIndicatorHead = new Rect();
        rectIndicatorFoot = new Rect();

        rectCurrentItem = new Rect();
    }

    private void updateVisibleItemCount() {
        if (mVisibleItemCount < 2)
            throw new ArithmeticException("Wheel's visible item count can not be less than 2!");

        if (mVisibleItemCount % 2 == 0) mVisibleItemCount += 1;
        mDrawnItemCount = mVisibleItemCount + 2;
        mHalfDrawnItemCount = mDrawnItemCount / 2;
    }

    private void computeTextSize() {
        mTextMaxWidth = mTextMaxHeight = 0;

        if (isPosInRang(textMaxWidthPosition)) {
            mTextMaxWidth = (int) paint.measureText(adapter.getItemText(textMaxWidthPosition));
        } else if (!TextUtils.isEmpty(maxWidthText)) {
            mTextMaxWidth = (int) paint.measureText(maxWidthText);
        } else {
            final int itemCount = adapter.getItemCount();
            for (int i = 0; i < itemCount; ++i) {
                String text = adapter.getItemText(i);
                int width = (int) paint.measureText(text);
                mTextMaxWidth = Math.max(mTextMaxWidth, width);
            }
        }
        Paint.FontMetrics metrics = paint.getFontMetrics();
        mTextMaxHeight = (int) (metrics.bottom - metrics.top);
    }

    private void updateItemTextAlign() {
        switch (mItemAlign) {
            case ALIGN_LEFT:
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case ALIGN_RIGHT:
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
            default:
                paint.setTextAlign(Paint.Align.CENTER);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Correct sizes of original content
        int resultWidth = mTextMaxWidth;
        int resultHeight = mTextMaxHeight * mVisibleItemCount + mItemSpace * (mVisibleItemCount - 1);

        // Consideration padding influence the view sizes
        resultWidth += getPaddingLeft() + getPaddingRight();
        resultHeight += getPaddingTop() + getPaddingBottom();

        // Consideration sizes of parent can influence the view sizes
        resultWidth = measureSize(modeWidth, sizeWidth, resultWidth);
        resultHeight = measureSize(modeHeight, sizeHeight, resultHeight);

        setMeasuredDimension(resultWidth, resultHeight);
    }

    private int measureSize(int mode, int sizeExpect, int sizeActual) {
        int realSize;
        if (mode == MeasureSpec.EXACTLY) {
            realSize = sizeExpect;
        } else {
            realSize = sizeActual;
            if (mode == MeasureSpec.AT_MOST) realSize = Math.min(realSize, sizeExpect);
        }
        return realSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        // Set content region
        rectDrawn.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom());

        // Get the center coordinates of content region
        wheelCenterX = rectDrawn.centerX();
        wheelCenterY = rectDrawn.centerY();

        // Correct item drawn center
        computeDrawnCenter();

        mHalfWheelHeight = rectDrawn.height() / 2;

        mItemHeight = rectDrawn.height() / mVisibleItemCount;
        mHalfItemHeight = mItemHeight / 2;

        // Initialize fling max Y-coordinates
        computeFlingLimitY();

        // Correct region of indicator
        computeIndicatorRect();

        // Correct region of current select item
        computeCurrentItemRect();
    }

    private void computeDrawnCenter() {
        switch (mItemAlign) {
            case ALIGN_LEFT:
                drawnCenterX = rectDrawn.left;
                break;
            case ALIGN_RIGHT:
                drawnCenterX = rectDrawn.right;
                break;
            default:
                drawnCenterX = wheelCenterX;
                break;
        }
        drawnCenterY = (int) (wheelCenterY - ((paint.ascent() + paint.descent()) / 2));
    }

    private void computeFlingLimitY() {
        int currentItemOffset = selectedItemPosition * mItemHeight;
        minFlingY = -mItemHeight * (adapter.getItemCount() - 1) + currentItemOffset;
        maxFlingY = currentItemOffset;
    }

    private void computeIndicatorRect() {
        if (!hasIndicator) return;
        int halfIndicatorSize = mIndicatorSize / 2;
        int indicatorHeadCenterY = wheelCenterY + mHalfItemHeight;
        int indicatorFootCenterY = wheelCenterY - mHalfItemHeight;
        rectIndicatorHead.set(rectDrawn.left, indicatorHeadCenterY - halfIndicatorSize, rectDrawn.right,
                indicatorHeadCenterY + halfIndicatorSize);
        rectIndicatorFoot.set(rectDrawn.left, indicatorFootCenterY - halfIndicatorSize, rectDrawn.right,
                indicatorFootCenterY + halfIndicatorSize);
    }

    private void computeCurrentItemRect() {
        if (mSelectedItemTextColor == -1) return;
        rectCurrentItem.set(rectDrawn.left, wheelCenterY - mHalfItemHeight, rectDrawn.right,
                wheelCenterY + mHalfItemHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (null != onWheelChangeListener) onWheelChangeListener.onWheelScrolled(scrollOffsetY);
        int drawnDataStartPos = -scrollOffsetY / mItemHeight - mHalfDrawnItemCount;

        //this sets background color of the whole view
        paintBackground.setColor(backgroundColor);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),paintBackground);

        //this sets background color of the selected item
        paintBackground.setColor(backgroundOfSelectedItem);
        paintBackground.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectCurrentItem,paintBackground);

        for (int drawnDataPos = drawnDataStartPos + selectedItemPosition,
             drawnOffsetPos = -mHalfDrawnItemCount;
             drawnDataPos < drawnDataStartPos + selectedItemPosition + mDrawnItemCount;
             drawnDataPos++, drawnOffsetPos++) {
            String data = "";

            if (isPosInRang(drawnDataPos)) data = adapter.getItemText(drawnDataPos);

            paint.setColor(mItemTextColor);
            paint.setTextSize(mItemTextSize);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            int mDrawnItemCenterY = drawnCenterY + (drawnOffsetPos * mItemHeight) +
                    scrollOffsetY % mItemHeight;

            if (hasAtmospheric) {
                int alpha =
                        (int) ((drawnCenterY - Math.abs(drawnCenterY - mDrawnItemCenterY)) * 1.0F / drawnCenterY * 255);
                alpha = Math.max(alpha, 0);
                paint.setAlpha(alpha);
            }
            // Correct item's drawn centerY base on curved state
            int drawnCenterY = mDrawnItemCenterY;

            // Judges need to draw different color for current item or not
            if (mSelectedItemTextColor != -1) {
                canvas.save();
                canvas.clipRect(rectCurrentItem, Region.Op.DIFFERENCE);
                canvas.drawText(data, drawnCenterX, drawnCenterY, paint);
                canvas.restore();

                paint.setColor(mSelectedItemTextColor);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextSize(mItemTextSize+10);

                canvas.save();
                canvas.clipRect(rectCurrentItem);
                canvas.drawText(data, drawnCenterX, drawnCenterY, paint);
                canvas.restore();
            } else {
                canvas.save();
                canvas.clipRect(rectDrawn);
                canvas.drawText(data, drawnCenterX, drawnCenterY, paint);
                canvas.restore();
            }
        }
        // Need to draw indicator or not
        if (hasIndicator) {
            paint.setColor(mIndicatorColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(rectIndicatorHead, paint);
            canvas.drawRect(rectIndicatorFoot, paint);
        }

    }

    private boolean isPosInRang(int position) {
        return position >= 0 && position < adapter.getItemCount();
    }

    private int computeSpace(int degree) {
        return (int) (Math.sin(Math.toRadians(degree)) * mHalfWheelHeight);
    }

    private int computeDepth(int degree) {
        return (int) (mHalfWheelHeight - Math.cos(Math.toRadians(degree)) * mHalfWheelHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(true);
                if (null == tracker) {
                    tracker = VelocityTracker.obtain();
                } else {
                    tracker.clear();
                }
                tracker.addMovement(event);
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    isForceFinishScroll = true;
                }
                downPointY = lastPointY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(downPointY - event.getY()) < touchSlop) {
                    isClick = true;
                    break;
                }
                isClick = false;
                tracker.addMovement(event);
                if (null != onWheelChangeListener) {
                    onWheelChangeListener.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                }

                // Scroll WheelPicker's content
                float move = event.getY() - lastPointY;
                if (Math.abs(move) < 1) break;
                scrollOffsetY += move;
                lastPointY = (int) event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(false);
                if (isClick) break;
                tracker.addMovement(event);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                    tracker.computeCurrentVelocity(1000, maximumVelocity);
                } else {
                    tracker.computeCurrentVelocity(1000);
                }

                // Judges the WheelPicker is scroll or fling base on current velocity
                isForceFinishScroll = false;
                int velocity = (int) tracker.getYVelocity();
                if (Math.abs(velocity) > minimumVelocity) {
                    scroller.fling(0, scrollOffsetY, 0, velocity, 0, 0, minFlingY, maxFlingY);
                    scroller.setFinalY(
                            scroller.getFinalY() + computeDistanceToEndPoint(scroller.getFinalY() % mItemHeight));
                } else {
                    scroller.startScroll(0, scrollOffsetY, 0, computeDistanceToEndPoint(scrollOffsetY % mItemHeight));
                }

                handler.post(runnable);
                if (null != tracker) {
                    tracker.recycle();
                    tracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (null != getParent()) getParent().requestDisallowInterceptTouchEvent(false);
                if (null != tracker) {
                    tracker.recycle();
                    tracker = null;
                }
                break;
        }
        return true;
    }

    private int computeDistanceToEndPoint(int remainder) {
        if (Math.abs(remainder) > mHalfItemHeight) {
            if (scrollOffsetY < 0) {
                return -mItemHeight - remainder;
            } else {
                return mItemHeight - remainder;
            }
        } else {
            return -remainder;
        }
    }

    public void scrollTo(final int itemPosition) {
        if (itemPosition != currentItemPosition) {
            final int differencesLines = currentItemPosition - itemPosition;
            final int newScrollOffsetY = scrollOffsetY + (differencesLines * mItemHeight); // % adapter.getItemCount();

            ValueAnimator va = ValueAnimator.ofInt(scrollOffsetY, newScrollOffsetY);
            va.setDuration(300);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    scrollOffsetY = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            va.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentItemPosition = itemPosition;
                    onItemSelected();
                }
            });
            va.start();
        }
    }

    private void onItemSelected() {
        int position = currentItemPosition;
        final Object item = this.adapter.getItem(position);
        if (null != onItemSelectedListener) {
            onItemSelectedListener.onItemSelected(this, item, position);
        }
        onItemSelected(position, item);
    }

    protected abstract void onItemSelected(int position, Object item);

    protected abstract void onItemCurrentScroll(int position, Object item);

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setVisibleItemCount(int count) {
        mVisibleItemCount = count;
        updateVisibleItemCount();
        requestLayout();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        onItemSelectedListener = listener;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int position) {
        position = Math.min(position, adapter.getItemCount() - 1);
        position = Math.max(position, 0);
        selectedItemPosition = position;
        currentItemPosition = position;
        scrollOffsetY = 0;
        computeFlingLimitY();
        requestLayout();
        invalidate();
    }

    public int getCurrentItemPosition() {
        return currentItemPosition;
    }

    public abstract int getDefaultItemPosition();

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        notifyDatasetChanged();
    }

    public void notifyDatasetChanged() {
        if (selectedItemPosition > adapter.getItemCount() - 1 || currentItemPosition > adapter.getItemCount() - 1) {
            selectedItemPosition = currentItemPosition = adapter.getItemCount() - 1;
        } else {
            selectedItemPosition = currentItemPosition;
        }
        scrollOffsetY = 0;
        computeTextSize();
        computeFlingLimitY();
        requestLayout();
        invalidate();
    }

    public void setOnWheelChangeListener(OnWheelChangeListener listener) {
        onWheelChangeListener = listener;
    }

    public String getMaximumWidthText() {
        return maxWidthText;
    }

    public void setMaximumWidthText(String text) {
        if (null == text) throw new NullPointerException("Maximum width text can not be null!");
        maxWidthText = text;
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getMaximumWidthTextPosition() {
        return textMaxWidthPosition;
    }

    public void setMaximumWidthTextPosition(int position) {
        if (!isPosInRang(position)) {
            throw new ArrayIndexOutOfBoundsException("Maximum width text Position must in [0, " +
                    adapter.getItemCount() + "), but current is " + position);
        }
        textMaxWidthPosition = position;
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getSelectedItemTextColor() {
        return mSelectedItemTextColor;
    }

    public void setSelectedItemTextColor(int color) {
        mSelectedItemTextColor = color;
        computeCurrentItemRect();
        invalidate();
    }

    public int getItemTextColor() {
        return mItemTextColor;
    }

    public void setItemTextColor(int color) {
        mItemTextColor = color;
        invalidate();
    }

    public int getItemTextSize() {
        return mItemTextSize;
    }

    public void setItemTextSize(int size) {
        mItemTextSize = size;
        paint.setTextSize(mItemTextSize);
        computeTextSize();
        requestLayout();
        invalidate();
    }

    public int getItemSpace() {
        return mItemSpace;
    }

    public void setItemSpace(int space) {
        mItemSpace = space;
        requestLayout();
        invalidate();
    }

    public void setIndicator(boolean hasIndicator) {
        this.hasIndicator = hasIndicator;
        computeIndicatorRect();
        invalidate();
    }

    public boolean hasIndicator() {
        return hasIndicator;
    }

    public int getIndicatorSize() {
        return mIndicatorSize;
    }

    public void setIndicatorSize(int size) {
        mIndicatorSize = size;
        computeIndicatorRect();
        invalidate();
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        invalidate();
    }

    public void setAtmospheric(boolean hasAtmospheric) {
        this.hasAtmospheric = hasAtmospheric;
        invalidate();
    }

    public boolean hasAtmospheric() {
        return hasAtmospheric;
    }

    public int getItemAlign() {
        return mItemAlign;
    }

    public void setItemAlign(int align) {
        mItemAlign = align;
        updateItemTextAlign();
        computeDrawnCenter();
        invalidate();
    }

    public Typeface getTypeface() {
        if (null != paint) return paint.getTypeface();
        return null;
    }

    public void setTypeface(Typeface tf) {
        if (null != paint) paint.setTypeface(tf);
        computeTextSize();
        requestLayout();
        invalidate();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    public interface BaseAdapter {

        int getItemCount();

        Object getItem(int position);

        String getItemText(int position);
    }

    private interface OnItemSelectedListener {
        void onItemSelected(WheelPicker picker, Object data, int position);
        void onCurrentItemOfScroll(WheelPicker picker, int position);
    }

    public interface OnWheelChangeListener {
        /**
         * <p>
         * Invoke when WheelPicker scroll stopped
         * WheelPicker will return a distance offset which between current scroll position and
         * initial position, this offset is a positive or a negative, positive means WheelPicker is
         * scrolling from bottom to top, negative means WheelPicker is scrolling from top to bottom
         *
         * @param offset <p>
         *               Distance offset which between current scroll position and initial position
         */
        void onWheelScrolled(int offset);

        /**
         * <p>
         * Invoke when WheelPicker scroll stopped
         * This method will be called when WheelPicker stop and return current selected item data's
         * position in list
         *
         * @param position <p>
         *                 Current selected item data's position in list
         */
        void onWheelSelected(int position);

        /**
         * <p>
         * Invoke when WheelPicker's scroll state changed
         * The state of WheelPicker always between idle, dragging, and scrolling, this method will
         * be called when they switch
         *
         * @param state {@link WheelPicker#SCROLL_STATE_IDLE}
         *              {@link WheelPicker#SCROLL_STATE_DRAGGING}
         *              {@link WheelPicker#SCROLL_STATE_SCROLLING}
         *              <p>
         *              State of WheelPicker, only one of the following
         *              {@link WheelPicker#SCROLL_STATE_IDLE}
         *              Express WheelPicker in state of idle
         *              {@link WheelPicker#SCROLL_STATE_DRAGGING}
         *              Express WheelPicker in state of dragging
         *              {@link WheelPicker#SCROLL_STATE_SCROLLING}
         *              Express WheelPicker in state of scrolling
         */
        void onWheelScrollStateChanged(int state);
    }

    public static class Adapter implements BaseAdapter {
        private final List data;

        public Adapter() {
            this(new ArrayList());
        }

        public Adapter(List data) {
            this.data = new ArrayList();
            this.data.addAll(data);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            final int itemCount = getItemCount();
            return data.get((position + itemCount) % itemCount);
        }

        @Override
        public String getItemText(int position) {
            return String.valueOf(data.get(position));
        }

        public void setData(List data) {
            this.data.clear();
            this.data.addAll(data);
        }

        public void addData(List data) {
            this.data.addAll(data);
        }
    }
}