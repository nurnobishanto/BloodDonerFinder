package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.MessageActivity;
import com.zedapp.reddrop_blooddonorfinder.R;

import com.zedapp.reddrop_blooddonorfinder.models.ChatUserModels;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder>{
    public Context mContext;
    public List<ChatUserModels> modelsList;
    FirebaseAuth mAuth;

    public ChatUserAdapter(Context mContext, List<ChatUserModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.chat_user_item,parent,false);


        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ViewHolder holder, int position) {

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        final ChatUserModels models  = modelsList.get(position);
        getUsernmaeImage(models.getUserid(),holder.name,holder.image,holder.status);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent i = new Intent(mContext, MessageActivity.class);
                    i.putExtra("userId", models.getUserid());
                    mContext.startActivity(i);
            }
        });

    }

    private void getUsernmaeImage(String userid, TextView name, CircleImageView image, ImageView status) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (userid.equals(snapshot.child("userid").getValue().toString())){
                        if(snapshot.child("fullname").getValue()!=null) {
                         name.setText(snapshot.child("fullname").getValue().toString());
                        }
                        if(snapshot.child("status").getValue()!=null) {
                            if(snapshot.child("status").getValue().equals("online")){
                                status.setImageResource(R.drawable.greendot);
                            }else {
                                status.setImageResource(R.drawable.reddot);
                            }
                         name.setText(snapshot.child("fullname").getValue().toString());
                        }

                         if(snapshot.child("sex").getValue()!=null) {
                             if(snapshot.child("sex").getValue().equals("Female")){
                                 if(snapshot.child("image").getValue()!=null || snapshot.child("image").getValue()!="default") {
                                     Glide.with(mContext).load(snapshot.child("image").getValue().toString())
                                             .placeholder(R.drawable.female_user)
                                             .into(image);
                                 }else {
                                    image.setImageResource(R.drawable.female_user);
                                 }

                             }else {
                                 if(snapshot.child("image").getValue()!=null || snapshot.child("image").getValue()!="default") {
                                     Glide.with(mContext).load(snapshot.child("image").getValue().toString())
                                             .placeholder(R.drawable.male_user)
                                             .into(image);
                                 }else {
                                     image.setImageResource(R.drawable.male_user);
                                 }
                             }
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
    public int getItemCount() {
        return modelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        CircleImageView image;
        LinearLayout card;
        ImageView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            image =itemView.findViewById(R.id.img);
            card =itemView.findViewById(R.id.card);
            status =itemView.findViewById(R.id.status);


        }
    }
}
