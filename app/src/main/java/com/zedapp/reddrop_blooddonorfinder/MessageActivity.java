package com.zedapp.reddrop_blooddonorfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.adapter.MessageAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.MessageModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private Intent intent;
    private EditText text_send;
    private ImageView img_on;
    private ImageView img_off;
    private ImageButton btn_send;
    private MessageAdapter messageAdapter;
    private List<MessageModels> mChat;
    private RecyclerView recyclerView;
    private ValueEventListener seenListener;
    private String sex="Male",  profileImageurl="default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img_on =(ImageView) findViewById(R.id.on);
        img_off =(ImageView) findViewById(R.id.off);

        recyclerView= findViewById(R.id.recycler_view) ;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        profile_image =(CircleImageView)findViewById(R.id.profile_image);
        username=(TextView)findViewById(R.id.username);
        text_send =(EditText) findViewById(R.id.text_send);
        btn_send =(ImageButton) findViewById(R.id.btn_send);
        intent=getIntent();
        final String userid =intent.getStringExtra("userId");
        String ai=userid;
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= text_send.getText().toString();
                if(!message.equals("")){
                    sendMessage (fuser.getUid(),userid ,message);
                    text_send.setText("");
                    //readMessage(fuser.getUid(),userid,profileImageurl,sex);
                }

                else {
                    Toast.makeText(MessageActivity.this,"You Can't Send Message!",Toast.LENGTH_LONG).show();
                }

                //Push notifications
 /*               DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
                HashMap<String,Object> hashMap= new HashMap<>();
                hashMap.put("userid",fuser.getUid());
                hashMap.put("text","Message you");
                hashMap.put("postid","");
                hashMap.put("ispost",false);
                reference.push().setValue(hashMap);
*/



            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {

                    Map<String,Object> map =(Map<String, Object>)dataSnapshot.getValue();


                    if(map.get("fullname")!=null)
                    {
                        username.setText(map.get("fullname").toString());
                    }
                    if(map.get("status")!=null)
                    {
                        if(map.get("status").equals("online")){
                            img_on.setVisibility(View.VISIBLE);
                            img_off.setVisibility(View.GONE);

                        }else {
                            img_on.setVisibility(View.GONE);
                            img_off.setVisibility(View.VISIBLE);
                        }
                    }

                   // Glide.clear(profile_image);
                    if(map.get("sex")!=null && map.get("sex").equals("Female"))
                    {
                        sex = "Female";
                        if(map.get("image")!=null)
                        {
                             profileImageurl = map.get("image").toString();

                            switch (profileImageurl)
                            {

                                case "default":
                                    profile_image.setImageResource(R.drawable.female_user);
                                    break;
                                default:
                                    Glide.with(getApplicationContext()).load(profileImageurl).placeholder(R.drawable.female_user).into(profile_image);
                                    break;

                            }

                        }
                    }else {
                        sex = "Male";
                        if(map.get("image")!=null)
                        {
                            String profileImageurl = map.get("image").toString();

                            switch (profileImageurl)
                            {

                                case "default":
                                    profile_image.setImageResource(R.drawable.male_user);
                                    break;
                                default:
                                    Glide.with(getApplicationContext()).load(profileImageurl).placeholder(R.drawable.male_user).into(profile_image);
                                    break;

                            }

                        }
                    }
                    readMessage(fuser.getUid(),userid,profileImageurl,sex);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        seenMessage(userid);

    }
    private void  seenMessage(final String userid)
    {
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    MessageModels chat =snapshot.getValue(MessageModels.class);
                    if(chat.getReciever().equals(fuser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void sendMessage (String sender,String reciever ,String message)
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",reciever);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);

        final String userid=intent.getStringExtra("userId");
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(fuser.getUid())
                .child("ConnectWith").child(userid);
        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance()
                .getReference("Users").child(userid)
                .child("ConnectWith").child(fuser.getUid());
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRef.child("userid").setValue(userid);
                    chatRef1.child("userid").setValue(fuser.getUid().toString());


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    public void  readMessage (final String myid, final String userId, final String imageurl,final String sex)
    {
        mChat =new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {


                    try {
                        MessageModels chat= snapshot.getValue(MessageModels.class);
                        if(chat.getReciever().equals(myid) && chat.getSender().equals(userId) ||
                                chat.getReciever().equals(userId) && chat.getSender().equals(myid) ) {
                            mChat.add(chat);

                        }


                        messageAdapter = new MessageAdapter(MessageActivity.this,mChat,imageurl,sex);
                        recyclerView.setAdapter(messageAdapter);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void status (String status)
    {
        reference=FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }



    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
