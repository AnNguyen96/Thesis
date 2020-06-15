package com.example.dashboard;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class SharedActivity2 extends AppCompatActivity {

    TextView textfb, textinsta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Second author");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        textfb = findViewById(R.id.text_fbtho);
        textfb.setMovementMethod(LinkMovementMethod.getInstance());
        textinsta = findViewById(R.id.text_instatho);
        textinsta.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
