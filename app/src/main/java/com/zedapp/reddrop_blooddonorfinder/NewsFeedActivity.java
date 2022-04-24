package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.zedapp.reddrop_blooddonorfinder.adapter.FeedAdapter;

import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedAdapter adapter;
    private List<FeedModels> list;
    private Dialog dialog;
    private TextView check;
    private FirebaseAuth mAuth;
    private int Image_Request_Code = 7;
    private Uri FilePathUri;
    private StorageReference storageReference;
    private ImageView imageView;
    private EditText desc;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_news_feed);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("NewsFeedImage");
        check=findViewById(R.id.check);
        userId = mAuth.getCurrentUser().getUid();
        recyclerView = findViewById(R.id.recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(NewsFeedActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readFeedrdata();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            readFeedrdata();
        });


    }

    private void readFeedrdata() {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    FeedModels obj = new FeedModels();

                    if(snapshot.child("userid").getValue()!=null) { obj.setUserid(snapshot.child("userid").getValue().toString()); }
                    else continue;
                    if(snapshot.child("nfKey").getValue()!=null) { obj.setNfKey(snapshot.child("nfKey").getValue().toString()); }
                    else continue;
                    if(snapshot.child("image").getValue()!=null) { obj.setImage(snapshot.child("image").getValue().toString()); }
                    if(snapshot.child("desc").getValue()!=null) { obj.setDesc(snapshot.child("desc").getValue().toString()); }
                    if(snapshot.child("date").getValue()!=null) { obj.setDate(snapshot.child("date").getValue().toString()); }

                    list.add(obj);
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter =new FeedAdapter(NewsFeedActivity.this,list);
                recyclerView.setAdapter(adapter);
                if (list.isEmpty()){
                    check.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add:
                dialog =  new Dialog(NewsFeedActivity.this);
                dialog.setContentView(R.layout.add_feed);
                dialog.getWindow().setLayout(1000,2000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                dialog.show();
                dialog.setCancelable(false);
                imageView = dialog.findViewById(R.id.image);
                imageView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent = new Intent();
                         intent.setType("image/*");
                         intent.setAction(Intent.ACTION_GET_CONTENT);
                         startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);
                     }
                 });
                 desc = dialog.findViewById(R.id.desc);
                Button add = dialog.findViewById(R.id.add);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadImage(imageView,desc);
                        dialog.dismiss();
                    }
                });
                Button cancel = dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items3, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    public void UploadImage(ImageView imageView, EditText desc) {

        if (FilePathUri != null) {

            final ProgressDialog pd = new ProgressDialog(NewsFeedActivity.this);
            pd.setMessage("Image Uploading..");
            pd.show();
            final StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask = ref.putFile(FilePathUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                        uploadInfo(downloadUri.toString(),desc.getText().toString());
                        pd.dismiss();
                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(getApplicationContext(), "Image Uploaded Failed ", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            });



        }
        else {
            uploadInfo("no",desc.getText().toString());
            //Toast.makeText(AddBloodRequestActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }
    private void uploadInfo(String image,String desc) {

        final ProgressDialog pd2 = new ProgressDialog(NewsFeedActivity.this);
        pd2.setMessage("Please wait..");
        pd2.show();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("News Feed");
        String key = dbRef.push().getKey();
        assert key != null;
        dbRef = dbRef.child(key);
        Map userInfo = new HashMap();

        userInfo.put("userid",userId);
        userInfo.put("nfKey",key);
        userInfo.put("image",image);
        userInfo.put("desc",desc);


        dbRef.updateChildren(userInfo);
        pd2.dismiss();
        Toast.makeText(NewsFeedActivity.this,"Feed Added Successful",Toast.LENGTH_LONG).show();
        readFeedrdata();


    }


}