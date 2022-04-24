package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.profile.ProfileActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.models.BRCommentModels;


import java.util.List;

public class BRCommentAdapter extends RecyclerView.Adapter<BRCommentAdapter.ViewHolder>{

    public Context mContext;
    public List<BRCommentModels> models;
    private FirebaseUser fuser;

    public BRCommentAdapter(Context mContext, List<BRCommentModels> models) {
        this.mContext = mContext;
        this.models = models;
    }

    @NonNull
    @Override
    public BRCommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);


        return new BRCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BRCommentAdapter.ViewHolder holder, int position) {

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final BRCommentModels modelslist  = models.get(position);
        holder.comments.setText(modelslist.getComment());
        holder.name.setText(modelslist.getName());
        getRequestPublisher(holder.name,modelslist.getUserid());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("userId", modelslist.getUserid());
                i.putExtra("userName", holder.name.getText());
                mContext.startActivity(i);

            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView name,comments;
        LinearLayout card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


           comments=itemView.findViewById(R.id.comment);
           name=itemView.findViewById(R.id.name);
           card=itemView.findViewById(R.id.card);

        }
    }
    private void getRequestPublisher(TextView publisher, String userid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (userid.equals(snapshot.child("userid").getValue())){
                        if(snapshot.child("fullname").getValue()!=null) { publisher.setText(snapshot.child("fullname").getValue().toString()); }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
