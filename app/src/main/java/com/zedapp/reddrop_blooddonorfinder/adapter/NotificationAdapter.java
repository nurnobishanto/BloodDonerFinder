package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.BloodRequest.BloodRequestCommentActivity;
import com.zedapp.reddrop_blooddonorfinder.FeedCommentActivity;
import com.zedapp.reddrop_blooddonorfinder.MessageActivity;
import com.zedapp.reddrop_blooddonorfinder.R;

import com.zedapp.reddrop_blooddonorfinder.models.NotificationsModels;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    public Context mContext;
    public List<NotificationsModels> modelsList;

    public NotificationAdapter(Context mContext, List<NotificationsModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notifications_item,parent,false);


        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        final NotificationsModels models  = modelsList.get(position);
        getUserName(models.getSender(),models.getMessage(),holder.text);
      //  holder.text.setText(models.getMessage());
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (models.getType().equals("BR")){
                    Intent i = new Intent(mContext, BloodRequestCommentActivity.class);
                    i.putExtra("brkey", models.getTypeKey());
                    mContext.startActivity(i);

                }else if (models.getType().equals("NF")){
                    Intent i = new Intent(mContext, FeedCommentActivity.class);
                    i.putExtra("nfkey", models.getTypeKey());
                    mContext.startActivity(i);

                }else if (models.getType().equals("MSG")){
                    Intent i = new Intent(mContext, MessageActivity.class);
                    i.putExtra("sender", models.getSender());
                    mContext.startActivity(i);

                }
            }
        });
    }

    private void getUserName(String sender, String message, TextView text) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (sender.equals(snapshot.child("userid").getValue().toString())){

                        if(snapshot.child("fullname").getValue()!=null) {
                            text.setText(snapshot.child("fullname").getValue().toString()+", "+ message);
                           break;
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
        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text=itemView.findViewById(R.id.text);


        }
    }
}
