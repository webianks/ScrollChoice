package com.webianks.library.scroll_choice;

/**
 * Created by R Ankit on 17-02-2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScrollChoice extends WheelPicker {

    private SimpleDateFormat simpleDateFormat;

    private OnDaySelectedListener onDaySelectedListener;

    WheelPicker.Adapter adapter;
    private int defaultIndex;


    public ScrollChoice(Context context) {
        this(context, null);
    }

    public ScrollChoice(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.simpleDateFormat = new SimpleDateFormat("EEE d MMM", getCurrentLocale());
        this.adapter = new Adapter();
        setAdapter(adapter);

        updateDays();

        updateDefaultDay();
    }

    @Override
    protected void onItemSelected(int position, Object item) {
        if (null != onDaySelectedListener) {
            final String itemText = (String) item;
        }
    }

    @Override
    protected void onItemCurrentScroll(int position, Object item) {

    }

    @Override
    public int getDefaultItemPosition() {
        return defaultIndex;
    }

    private void updateDays() {

        final List<String> data = new ArrayList<>();
        data.add("Hindi");
        data.add("English");
        data.add("French");
        data.add("German");
        data.add("Spanish");
        data.add("Portuguese");
        data.add("Urdu");
        data.add("Malayalam");
        data.add("Udiya");
        data.add("Telgu");
        data.add("Tamil");

        defaultIndex = 5;

        adapter.setData(data);
    }

    protected String getFormattedValue(Object value) {
        return simpleDateFormat.format(value);
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        this.onDaySelectedListener = onDaySelectedListener;
    }

    private void updateDefaultDay() {
        setSelectedItemPosition(defaultIndex);
    }

    public int getDefaultDayIndex() {
        return defaultIndex;
    }

    public String getCurrentDay() {
        return adapter.getItemText(getCurrentItemPosition());
    }

    public interface OnDaySelectedListener {
        void onDaySelected(ScrollChoice scrollChoice, int position, String name, Date date);
    }
}