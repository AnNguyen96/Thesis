package com.example.dashboard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutPage extends AppCompatActivity {

    private LinearLayout mListLayout1, mListLayout2;

    private ImageView mSharedImg1, mSharedImg2;
    private TextView mSharedName1, mSharedName2;
    private TextView mSharedDesc1, mSharedDesc2;

    TextView textyoutube, textgithub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Know more about us");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        textyoutube = findViewById(R.id.text_youtube);
        textyoutube.setMovementMethod(LinkMovementMethod.getInstance());
        textgithub = findViewById(R.id.text_github);
        textgithub.setMovementMethod(LinkMovementMethod.getInstance());

        mListLayout1 = findViewById(R.id.mListLayout1);
        mListLayout2 = findViewById(R.id.mListLayout2);
        mSharedImg1 = findViewById(R.id.profile_img1);
        mSharedImg2 = findViewById(R.id.profile_img2);
        mSharedName1 = findViewById(R.id.profile_name1);
        mSharedName2 = findViewById(R.id.profile_name2);
        mSharedDesc1 = findViewById(R.id.profile_desc1);
        mSharedDesc2 = findViewById(R.id.profile_desc2);

        mListLayout1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent sharedIntent = new Intent(AboutPage.this, SharedActivity1.class);

                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(mSharedImg1, "imageTransition");
                pairs[1] = new Pair<View, String>(mSharedName1, "nameTransition");
                pairs[2] = new Pair<View, String>(mSharedDesc1, "descTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AboutPage.this, pairs);

                startActivity(sharedIntent, options.toBundle());
            }
        });

        mListLayout2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent sharedIntent = new Intent(AboutPage.this, SharedActivity2.class);

                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(mSharedImg2, "imageTransition");
                pairs[1] = new Pair<View, String>(mSharedName2, "nameTransition");
                pairs[2] = new Pair<View, String>(mSharedDesc2, "descTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AboutPage.this, pairs);

                startActivity(sharedIntent, options.toBundle());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
