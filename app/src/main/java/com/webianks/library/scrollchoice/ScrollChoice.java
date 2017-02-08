package com.webianks.library.scrollchoice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by R Ankit on 06-02-2017.
 */

public class ScrollChoice extends View {


    private ArrayList<String> mItems;
    private Paint paint;

    public ScrollChoice(Context context) {
        super(context);
    }

    public ScrollChoice(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScrollChoice(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(45);
        canvas.drawText("Some Text", 100, 250, paint);
    }

    public void setItems(List<String> items) {

        if (mItems == null) {
            mItems = new ArrayList<>();
        } else {
            mItems.clear();
        }
        mItems.addAll(items);
    }

    public List<String> getItems() {
        return mItems;
    }

}
