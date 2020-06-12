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
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class PostDetailActivity extends AppCompatActivity {

    TextView mTitleTv, mDetailTv;
    ImageView mImageIv;
    Bitmap bitmap;
    Button mSaveBtn, mShareBtn, mWiki, mWeb, mMap;
    OutputStream outputStream;

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
        mSaveBtn = findViewById(R.id.saveBtn);
        mShareBtn = findViewById(R.id.shareBtn);
        mWiki = findViewById(R.id.wikiBtn);
        mWeb = findViewById(R.id.webBtn);
        mMap = findViewById(R.id.mapBtn);

//        byte[] bytes = getIntent().getByteArrayExtra("image");
//        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        String image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        final String wiki = getIntent().getStringExtra("wiki");
        final String web = getIntent().getStringExtra("website");
        final String map = getIntent().getStringExtra("map");

        mTitleTv.setText(title);
        mDetailTv.setText(desc);
        //mImageIv.setImageBitmap(bmp);
        Picasso.get().load(image).into(mImageIv);

        mWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = wiki;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, WRITE_EXTERNAL_STORAGE_CODE);
                    }
                    else{
                        saveImage();
                    }
                }
                else{
                    saveImage();
                }
            }
        });



        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Share this brand with other people";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            }
        });
    }

    private void saveImage(){
        bitmap = ((BitmapDrawable)mImageIv.getDrawable()).getBitmap();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(System.currentTimeMillis());

        File path = Environment.getExternalStorageDirectory().getAbsoluteFile();

        File dir = new File(path + "/Brand");
        dir.mkdirs();

        String imageName = timeStamp + ".PNG";
        File file = new File(dir, imageName);
        FileOutputStream out = null;

        Toast.makeText(PostDetailActivity.this, "Saved abc", Toast.LENGTH_SHORT).show();

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, imageName + " saved to" + dir, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }
                else{
                    Toast.makeText(this, "enable permission to save image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
