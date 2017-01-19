package com.webianks.library.scrollchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String[] choices = {"Hindi", "English", "French", "German", "Spanish", "Belgi",
            "Urdu", "Malyalam", "Udiya", "Telgu", "Tamil"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        ScrollChoiceAdapter scrollChoiceAdapter = new ScrollChoiceAdapter(this, choices);
        recyclerView.setAdapter(scrollChoiceAdapter);


    }
}
