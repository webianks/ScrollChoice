package com.webianks.library.scrollchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ScrollChoice scrollChoice = (ScrollChoice) findViewById(R.id.scroll_choice);


        List<String> data = new ArrayList<>();
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

        scrollChoice.addItems(data,5);
        scrollChoice.setOnItemSelectedListener(new ScrollChoice.OnItemSelectedListener() {
            @Override
            public void onItemSelected(ScrollChoice scrollChoice, int position, String name) {
                Log.d("webi",name);
            }
        });
    }
}
