package com.webianks.library.scrollchoice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by R Ankit on 19-01-2017.
 */

public class ScrollChoiceAdapter extends RecyclerView.Adapter<ScrollChoiceAdapter.ViewHolder> {

    private Context context;
    private String[] choices;

    public ScrollChoiceAdapter(Context context, String[] choices) {
        this.context = context;
        this.choices = choices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ScrollChoiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(choices[position]);
    }

    @Override
    public int getItemCount() {
        return choices.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
