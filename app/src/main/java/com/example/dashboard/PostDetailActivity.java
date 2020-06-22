package com.example.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PostDetailActivity extends AppCompatActivity {

    TextView mTitleTv, mDetailTv;
    ImageView mImageIv;
    Button mWikiBtn, mWeb, mMap, mShareBtn;

    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Brand Detail");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mTitleTv = findViewById(R.id.nameDetail);
        mDetailTv = findViewById(R.id.descriptionDetail);
        mImageIv = findViewById(R.id.imageViewDetail);
        mWikiBtn = findViewById(R.id.wikiBtn);
        mWeb = findViewById(R.id.webBtn);
        mMap = findViewById(R.id.mapBtn);
        mShareBtn = findViewById(R.id.shareBtn);


        final String image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        final String wiki = getIntent().getStringExtra("wiki");
        final String web = getIntent().getStringExtra("website");
        final String map = getIntent().getStringExtra("map");

        mTitleTv.setText(title);
        mDetailTv.setText(desc);
        Picasso.get().load(image).into(mImageIv);

        mWikiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(wiki));
                    startActivity(i);
                }catch (Exception e){
                    Toast.makeText(PostDetailActivity.this, "This brand will update wikipedia soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(web));
                    startActivity(i);
                }catch (Exception e){
                    Toast.makeText(PostDetailActivity.this, "This brand will update website soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);
                }catch (Exception e){
                    Toast.makeText(PostDetailActivity.this, "This brand will update location soon", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, web);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, "Share with friends..");
                    startActivity(shareIntent);
                }catch (Exception e){
                    Toast.makeText(PostDetailActivity.this, "This brand will update share soon", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
