package com.webianks.library.scrollchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String[] choices = {"", "", "", "", "", "",
            "", "", "", "", ""};

    private ScrollChoice scrollChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        ScrollChoiceAdapter scrollChoiceAdapter = new ScrollChoiceAdapter(this, choices);
        recyclerView.setAdapter(scrollChoiceAdapter);

      /*  List<String> items = new ArrayList<>();
        items.add("Hindi");
        items.add("English");
        items.add("French");
        items.add("German");
        items.add("Spanish");
        items.add("Belgi");
        items.add("Urdu");
        items.add("Malyalam");
        items.add("Udiya");
        items.add("Telgu");
        items.add("Tamil");

        scrollChoice.setItems(items);*/

    }
}
