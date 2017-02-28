package com.webianks.library.scrollchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.webianks.library.scroll_choice.ScrollChoice;

public class MainActivity extends AppCompatActivity {

    private String[] choices = {"", "", "", "", "", "",
            "", "", "", "", ""};

    private ScrollChoice scrollChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
