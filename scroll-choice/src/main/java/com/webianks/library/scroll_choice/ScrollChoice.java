package com.webianks.library.scroll_choice;

/**
 * Created by R Ankit on 17-02-2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;

public class ScrollChoice extends WheelPicker {

    private OnItemSelectedListener onItemSelectedListener;

    WheelPicker.Adapter adapter;
    private int defaultIndex;


    public ScrollChoice(Context context) {
        this(context, null);
    }

    public ScrollChoice(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.adapter = new Adapter();
        setAdapter(adapter);

        updateItems();
        updateDefaultItem();
    }



    @Override
    protected void onItemSelected(int position, Object item) {
        if (null != onItemSelectedListener) {
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


    private void updateItems() {

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


    public void setOnDaySelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    private void updateDefaultItem() {setSelectedItemPosition(defaultIndex);}

    public int getDefaultItemIndex() {return defaultIndex;}

    public String getCurrentSelection() {
        return adapter.getItemText(getCurrentItemPosition());
    }

    public interface OnItemSelectedListener {
        void onItemSelected(ScrollChoice scrollChoice, int position, String name);
    }
}