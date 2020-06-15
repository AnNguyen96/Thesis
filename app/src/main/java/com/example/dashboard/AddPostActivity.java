package com.example.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AddPostActivity extends AppCompatActivity {

    EditText mTitleEt, mDescrEt;
    ImageView mPostIv;
    Button mUploadBtn;
    ProgressBar mProgressBar;

    String mStoragePath = "uploads/";
    String mDatabasePath = "Data";

    Uri mFilePathUri;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;

    ProgressDialog mProgressDialog;

    int IMAGE_REQUEST_CODE = 5;

    String cTitle, cDescr, cImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add new brand");

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mTitleEt = findViewById(R.id.pTitleEt);
        mDescrEt = findViewById(R.id.pDescrEt);
        mPostIv = findViewById(R.id.pImageIv);
        mUploadBtn = findViewById(R.id.pUploadBtn);
        mProgressBar = findViewById(R.id.progress_bar);

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            cTitle = intent.getString("cTitle");
            cDescr = intent.getString("cDescr");
            cImage = intent.getString("cImage");

            mTitleEt.setText(cTitle);
            mDescrEt.setText(cDescr);
            Picasso.get().load(cImage).into(mPostIv);

            actionBar.setTitle("Update Post");
            mUploadBtn.setText("Update");
        }

        mPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"),IMAGE_REQUEST_CODE);
            }
        });

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUploadBtn.getText().equals("Upload")){
                    uploadDataToFirebase();
                }
                else{
                    beginUpdate();
                }


            }
        });

        mStorageReference = getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);

        mProgressDialog = new ProgressDialog(AddPostActivity.this);
    }

    private void beginUpdate() {
        mProgressDialog.setMessage("Updating..");
        mProgressDialog.show();

        deletePreviousImage();
    }

    private void deletePreviousImage() {
        StorageReference mPictureRef = getInstance().getReferenceFromUrl(cImage);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddPostActivity.this, "Previous brand deleted..", Toast.LENGTH_SHORT).show();

                uploadNewImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddPostActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void uploadNewImage() {
        String imageName = System.currentTimeMillis() + ".png";

        StorageReference storageReference2 = mStorageReference.child(mStoragePath + imageName);

        Bitmap bitmap = ((BitmapDrawable)mPostIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddPostActivity.this, "New image uploaded..", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                updateDatabase(downloadUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void updateDatabase(final String s) {
        final String title = mTitleEt.getText().toString();
        final String descr = mDescrEt.getText().toString();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference("Data");

        Query query = mRef.orderByChild("title").equalTo(cTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().child("title").setValue(title);
                    ds.getRef().child("search").setValue(title.toLowerCase());
                    ds.getRef().child("description").setValue(descr);
                    ds.getRef().child("image").setValue(s);
                }
                mProgressDialog.dismiss();
                Toast.makeText(AddPostActivity.this, "Database Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadDataToFirebase(){
        if (mFilePathUri != null){
            mProgressDialog.setTitle("Uploading..");
            mProgressDialog.show();

            StorageReference storageReference2nd = mStorageReference.child(mStoragePath + System.currentTimeMillis() + "." + getFileExtension(mFilePathUri));

            storageReference2nd.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String mPostTitle = mTitleEt.getText().toString().trim();
                            String mPostDescr = mDescrEt.getText().toString().trim();

                            mProgressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mPostTitle, mPostDescr, downloadUrl.toString(), mPostTitle.toLowerCase());
                            String imageUploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            //mProgressDialog.setTitle("Uploading..");
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            Toast.makeText(this, "Please select image or add image name", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null){
            mFilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePathUri);

                mPostIv.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
