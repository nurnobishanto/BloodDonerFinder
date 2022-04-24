package com.zedapp.reddrop_blooddonorfinder.BloodRequest;

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
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.adapter.BRCommentAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.BRCommentModels;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BloodRequestCommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar pb;
    private BRCommentAdapter adapter;
    private List<BRCommentModels> models;
    public TextView fullname,comments,publisher,date,messages,bloodgroup,hospital,cnumber,enumber,c,e;
    public ImageView image,submit;
    EditText addComment;
    String brkey,myid,publisherid;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Blood Request Details");
        setContentView(R.layout.activity_blood_request_comment);

        brkey = getIntent().getStringExtra("brkey");
        mAuth = FirebaseAuth.getInstance();
        myid = mAuth.getCurrentUser().getUid();
        addComment = findViewById(R.id.addcomment);
        submit = findViewById(R.id.submit);
        image=findViewById(R.id.image);
        comments=findViewById(R.id.comment);
        fullname=findViewById(R.id.name);
        publisher=findViewById(R.id.publisher);
        // date=findViewById(R.id.date);
        messages=findViewById(R.id.message);
        bloodgroup=findViewById(R.id.bloodgroup);
        hospital=findViewById(R.id.hospitalName);
        cnumber=findViewById(R.id.cnumber);
        enumber=findViewById(R.id.enumber);
        c=findViewById(R.id.contact);
        e=findViewById(R.id.emergency);

        pb = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(BloodRequestCommentActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        models =new ArrayList<>();
        adapter =new BRCommentAdapter(BloodRequestCommentActivity.this,models);
        recyclerView.setAdapter(adapter);
        getCommentsCount(brkey, comments);
        readBloodRequest(brkey);
        readBloodRequestComment(brkey);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addComment.getText().toString().isEmpty()){
                    Toast.makeText(BloodRequestCommentActivity.this,"Write Comment",Toast.LENGTH_LONG).show();
                }else {
                    final ProgressDialog pd = new ProgressDialog(BloodRequestCommentActivity.this);
                    pd.setMessage("Please wait..");
                    pd.show();


                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("BloodRequest");
                    String key = dbRef.push().getKey();
                    assert key != null;
                    dbRef = dbRef.child(brkey).child("Comments").child(key);
                    Map userInfo = new HashMap();

                    userInfo.put("ckey",key);
                    userInfo.put("userid",myid);
                    userInfo.put("comment",addComment.getText().toString());


                    dbRef.updateChildren(userInfo);
                    pd.dismiss();
                    addComment.setText("");
                    Toast.makeText(BloodRequestCommentActivity.this,"Commented",Toast.LENGTH_LONG).show();
                    readBloodRequestComment(brkey);
                    getCommentsCount(brkey, comments);
                    PushNotification("Commented on Your blood request","BR",brkey,publisherid);

                }
            }
        });


    }
    private  void getCommentsCount(String brkey, final TextView comments){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("BloodRequest").child(brkey).child("Comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("("+dataSnapshot.getChildrenCount()+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void readBloodRequestPublisher(String publisherid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (publisherid.equals(snapshot.child("userid").getValue())){
                        if(snapshot.child("fullname").getValue()!=null) {
                            publisher.setText("Requested by: "+snapshot.child("fullname").getValue().toString());
                        }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readBloodRequestComment(String brkey) {

        models.clear();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("BloodRequest").child(brkey).child("Comments");
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

    private void readBloodRequest(String brkey) {


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("BloodRequest");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    if(brkey.equals(snapshot.child("brKey").getValue())) {


                        if(snapshot.child("BloodGroup").getValue()!=null) { bloodgroup.setText("Blood Group : "+snapshot.child("BloodGroup").getValue().toString()); }
                        if(snapshot.child("hospital").getValue()!=null) { hospital.setText("Hospital : "+snapshot.child("hospital").getValue().toString()); }
                        if(snapshot.child("message").getValue()!=null) { messages.setText(snapshot.child("message").getValue().toString()); }
                        if(snapshot.child("fullname").getValue()!=null) {  fullname.setText("Name : "+snapshot.child("fullname").getValue().toString()); }
                        if(snapshot.child("contactNO").getValue()!=null) { cnumber.setText("Contact Number : "+snapshot.child("contactNO").getValue().toString()); }
                        if(snapshot.child("emergencyNO").getValue()!=null) { enumber.setText("Emergency Number : "+snapshot.child("emergencyNO").getValue().toString()); }
                        if(snapshot.child("userid").getValue()!=null) {
                            readBloodRequestPublisher(snapshot.child("userid").getValue().toString());
                            publisherid = snapshot.child("userid").getValue().toString();
                        }
                        if(snapshot.child("image").getValue()!=null) {
                            if(snapshot.child("image").getValue().toString().equals("no"))
                            {
                                image.setVisibility(View.GONE);
                            }else {
                                image.setVisibility(View.VISIBLE);
                                Glide.with(BloodRequestCommentActivity.this).load(snapshot.child("image").getValue().toString())
                                        .placeholder(R.drawable.loading)
                                        .into(image);

                            }

                        }


                        c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", snapshot.child("contactNO").getValue().toString(), null)));
                            }
                        });
                        e.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",snapshot.child("emergencyNO").getValue().toString(), null)));
                            }
                        });

                    }



                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
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
}