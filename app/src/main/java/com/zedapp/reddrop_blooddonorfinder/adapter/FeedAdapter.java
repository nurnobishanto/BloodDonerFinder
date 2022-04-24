package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.squareup.picasso.Picasso;
import com.zedapp.reddrop_blooddonorfinder.BloodRequest.EditBloodRequestActivity;
import com.zedapp.reddrop_blooddonorfinder.FeedCommentActivity;
import com.zedapp.reddrop_blooddonorfinder.profile.ProfileActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>{

    public Context mContext;
    public List<FeedModels> modelsList;
    FirebaseAuth mAuth;

    public FeedAdapter(Context mContext, List<FeedModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.feed_item,parent,false);


        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position) {

        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        final FeedModels models  = modelsList.get(position);
        if(models.getImage().equals("no"))
        {
            holder.image.setVisibility(View.GONE);
        }else {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(models.getImage())
                    .placeholder(R.drawable.loading)
                    .into(holder.image);
        }
        holder.date.setText(models.getDate());
        holder.info.setOnClickListener(v -> {
            Intent i = new Intent(mContext, FeedCommentActivity.class);
            i.putExtra("nfkey", models.getNfKey());
            i.putExtra("name", holder.name.getText().toString());
            i.putExtra("desc", models.getDesc());
            i.putExtra("img", models.getImage());
            mContext.startActivity(i);
        });
        holder.comment.setOnClickListener(v -> {
            Intent i = new Intent(mContext, FeedCommentActivity.class);
            i.putExtra("nfkey", models.getNfKey());
            i.putExtra("name", holder.name.getText().toString());
            i.putExtra("desc", models.getDesc());
            i.putExtra("img", models.getImage());
            mContext.startActivity(i);
        });
        holder.desc.setText(models.getDesc().trim());
        getUserInfo(holder.profileImage,holder.name,models.getUserid());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("userId", models.getUserid());
                mContext.startActivity(i);
            }
        });
        getCommentCount(holder.comment,models.getNfKey());
        getLikeCount(holder.like,holder.liked,models.getNfKey());
        isFeedLiked(models.getNfKey(),mAuth.getCurrentUser().getUid(),holder.liked,holder.like);

        holder.like.setOnClickListener(v -> {
            holder.liked.setVisibility(View.VISIBLE);
            holder.like.setVisibility(View.GONE);
            doLike(models.getNfKey());
            getLikeCount(holder.like,holder.liked,models.getNfKey());

        });
        holder.liked.setOnClickListener(v -> {
            holder.liked.setVisibility(View.GONE);
            holder.like.setVisibility(View.VISIBLE);
            doDisLike(models.getNfKey());
            getLikeCount(holder.like,holder.liked,models.getNfKey());
        });
        if (mAuth.getCurrentUser().getUid().equals(models.getUserid())){
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.btnMore.setOnClickListener(v -> {

                PopupMenu popupMenu = new PopupMenu(mContext,holder.btnMore);
                popupMenu.inflate(R.menu.menu_more);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.post_edit:{
                            Intent i = new Intent(mContext, EditBloodRequestActivity.class);
                            i.putExtra("nfKey",models.getNfKey());
                            mContext.startActivity(i);
                        }
                        return true;
                        case R.id.post_delete:{
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Alert Delete")
                                    .setMessage("Are you sure you want to delete this Feed?")
                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        // Continue with delete operation

                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(R.drawable.ic_delete)
                                    .show();
                        }
                        return true;
                    }
                    return false;
                });
                popupMenu.show();

            });
        }

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

    private void getLikeCount(Button like, Button liked, String nfKey) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfKey).child("Likes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void getCommentCount(Button comment, String nfKey) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfKey).child("Comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comment.setText("("+dataSnapshot.getChildrenCount()+")");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInfo(CircleImageView profileImage, TextView name, String userid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (userid.equals(snapshot.child("userid").getValue().toString())){
                        if(snapshot.child("fullname").getValue()!=null) { name.setText(snapshot.child("fullname").getValue().toString()); }
                        if(snapshot.child("image").getValue()!=null) {
                            Picasso.get()
                                    .load(snapshot.child("image").getValue().toString())
                                    .placeholder(R.drawable.male_user)
                                    .error(R.drawable.male_user)
                                    .into(profileImage);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void isFeedLiked(String nfKey, String uid, Button liked, Button like) {


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed").child(nfKey).child("Likes");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (uid.equals(snapshot.child("myid").getValue())){
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

    @Override
    public int getItemCount() {
        return modelsList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name,desc,date;
        private ImageView image;
        private ImageButton info;
        private Button like,liked,comment;
        private ImageButton btnMore;
        private CircleImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            btnMore = itemView.findViewById(R.id.btnMore);
            image=itemView.findViewById(R.id.image);
            info=itemView.findViewById(R.id.info);
            name=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            comment=itemView.findViewById(R.id.comment);
            desc=itemView.findViewById(R.id.desc);
            like=itemView.findViewById(R.id.like);
            liked=itemView.findViewById(R.id.liked);
        }
    }
}
