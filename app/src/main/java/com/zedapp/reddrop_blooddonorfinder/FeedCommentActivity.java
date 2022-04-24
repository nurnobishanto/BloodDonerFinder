package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.adapter.BRCommentAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.BRCommentModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedCommentActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String nfkey,myid,name,desc,img,publisherid;
    EditText addComment;
    public ImageView image,submit;
    public TextView description,publisher;
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private BRCommentAdapter adapter;
    private List<BRCommentModels> models;
    Button comment,like,liked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("News Feed Details");
        setContentView(R.layout.activity_feed_comment);

        nfkey = getIntent().getStringExtra("nfkey");
        name = getIntent().getStringExtra("name");
        desc = getIntent().getStringExtra("desc");
        img = getIntent().getStringExtra("img");
        mAuth = FirebaseAuth.getInstance();
        myid = mAuth.getCurrentUser().getUid();

        like=findViewById(R.id.like);
        liked=findViewById(R.id.liked);
        getLikeCount(like,liked,nfkey);
        isFeedLiked(nfkey,myid,liked,like);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liked.setVisibility(View.VISIBLE);
                like.setVisibility(View.GONE);
                doLike(nfkey);
                getLikeCount(like,liked,nfkey);

            }
        });
        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liked.setVisibility(View.GONE);
                like.setVisibility(View.VISIBLE);
                doDisLike(nfkey);
                getLikeCount(like,liked,nfkey);
            }
        });
        publisher = findViewById(R.id.name);
        description = findViewById(R.id.desc);
        publisher.setText(name);
        description.setText(desc);
        addComment = findViewById(R.id.addcomment);
        submit = findViewById(R.id.submit);
        image=findViewById(R.id.image);
        comment = findViewById(R.id.comment);

        pb = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(FeedCommentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        models =new ArrayList<>();
        adapter =new BRCommentAdapter(FeedCommentActivity.this,models);
        recyclerView.setAdapter(adapter);
        getCommentsCount(nfkey, comment);
        readNewsFeed(nfkey);
        readFeedComment(nfkey);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addComment.getText().toString().isEmpty()){
                    Toast.makeText(FeedCommentActivity.this,"Write Comment",Toast.LENGTH_LONG).show();
                }else {
                    final ProgressDialog pd = new ProgressDialog(FeedCommentActivity.this);
                    pd.setMessage("Please wait..");
                    pd.show();


                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("News Feed");
                    String key = dbRef.push().getKey();
                    assert key != null;
                    dbRef = dbRef.child(nfkey).child("Comments").child(key);
                    Map userInfo = new HashMap();

                    userInfo.put("ckey",key);
                    userInfo.put("userid",myid);
                    userInfo.put("comment",addComment.getText().toString());


                    dbRef.updateChildren(userInfo);
                    pd.dismiss();
                    addComment.setText("");

                    readFeedComment(nfkey);
                    Toast.makeText(FeedCommentActivity.this,"Commented",Toast.LENGTH_LONG).show();
                    PushNotification("Commented on Your Post","NF",nfkey,publisherid);

                }
            }
        });

    }


    private void doDisLike(String nfKey) {
        mAuth = FirebaseAuth.getInstance();
        String myid = mAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("News Feed").child(nfKey).child("Likes");
        dbRef.child(myid).removeValue();

    }

    private void doLike(String nfKey) {
        mAuth = FirebaseAuth.getInstance();
        String myid = mAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("News Feed");
        dbRef = dbRef.child(nfKey).child("Likes").child(myid);
        Map userInfo = new HashMap();
        userInfo.put("myid",myid);
        dbRef.updateChildren(userInfo);
    }
    private void isFeedLiked(String nfKey, String myid, Button liked, Button like) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfKey).child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (myid.equals(snapshot.child("myid").getValue())){
                        liked.setVisibility(View.VISIBLE);
                        like.setVisibility(View.GONE);
                    }else {
                        liked.setVisibility(View.GONE);
                        like.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikeCount(Button like, Button liked, String nfKey) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfKey).child("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                like.setText("("+dataSnapshot.getChildrenCount()+")");
                liked.setText("("+dataSnapshot.getChildrenCount()+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readNewsFeed(String nfkey) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    if(nfkey.equals(snapshot.child("nfKey").getValue())) {

                        if(snapshot.child("desc").getValue()!=null) { description.setText(snapshot.child("desc").getValue().toString()); }
                        if(snapshot.child("userid").getValue()!=null) {
                            readPublisher(snapshot.child("userid").getValue().toString());
                        }
                        if(snapshot.child("image").getValue()!=null) {
                            if(snapshot.child("image").getValue().toString().equals("no"))
                            {
                                image.setVisibility(View.GONE);
                            }else {
                                image.setVisibility(View.VISIBLE);
                                try {
                                    Glide.with(FeedCommentActivity.this).load(snapshot.child("image").getValue().toString())
                                            .placeholder(R.drawable.loading)
                                            .into(image);
                                }catch (Exception e){

                                }


                            }
                            if(snapshot.child("userid").getValue()!=null) {
                                publisherid = snapshot.child("userid").getValue().toString();
                            }

                        }


                    }

                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readFeedComment(String nfkey) {
        models.clear();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfkey).child("Comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    String ckey,brkey,userid,comment,name;
                    ckey ="";
                    brkey ="";
                    userid ="";
                    comment ="";
                    name ="";


                    if(snapshot.child("name").getValue()!=null) { name = snapshot.child("name").getValue().toString(); }
                    if(snapshot.child("comment").getValue()!=null) { comment = snapshot.child("comment").getValue().toString(); }
                    if(snapshot.child("userid").getValue()!=null) { userid = snapshot.child("userid").getValue().toString(); }
                    if(snapshot.child("ckey").getValue()!=null) { ckey = snapshot.child("ckey").getValue().toString(); }

                    BRCommentModels obj = new BRCommentModels(ckey,userid,comment,name);
                    models.add(obj);
                    adapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCommentsCount(String nfkey, Button comment) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfkey).child("Comments");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comment.setText("("+dataSnapshot.getChildrenCount()+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void PushNotification(String s, String type, String typeKey, String reciber) {
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        String myId = mauth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String key = dbRef.push().getKey();
        assert key != null;
        dbRef = dbRef.child(key);
        Map userInfo = new HashMap();

        userInfo.put("typeKey",typeKey);
        userInfo.put("message",s);
        userInfo.put("type",type);
        userInfo.put("nKey",key);
        userInfo.put("reciever",reciber);
        userInfo.put("sender",myId);

        dbRef.updateChildren(userInfo);

    }

    private void readPublisher(String publisherid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (publisherid.equals(snapshot.child("userid").getValue())){
                        if(snapshot.child("fullname").getValue()!=null) {
                            publisher.setText(snapshot.child("fullname").getValue().toString());
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}