package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.BloodRequest.EditBloodRequestActivity;
import com.zedapp.reddrop_blooddonorfinder.BloodRequest.BloodRequestCommentActivity;
import com.zedapp.reddrop_blooddonorfinder.profile.ProfileActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.models.BloodRequestModel;

import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.ViewHolder>{

    public Context mContext;
    public List<BloodRequestModel> mBloodRequest;
    private FirebaseUser fuser;

    public BloodRequestAdapter(Context mContext, List<BloodRequestModel> mBloodRequest) {
        this.mContext = mContext;
        this.mBloodRequest = mBloodRequest;
    }

    @NonNull
    @Override
    public BloodRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.blood_request_item,parent,false);


        return new BloodRequestAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BloodRequestAdapter.ViewHolder holder, int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final BloodRequestModel bloodRequest  = mBloodRequest.get(position);
        if(bloodRequest.getImage().equals("no"))
        {
            holder.image.setVisibility(View.GONE);
        }else {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(bloodRequest.getImage())
                    .placeholder(R.drawable.loading)
                    .into(holder.image);

        }

        if (fuser.getUid().equals(bloodRequest.getUserid())){
            holder.btnMore.setVisibility(View.VISIBLE);
            holder.btnMore.setOnClickListener(v -> {

                PopupMenu popupMenu = new PopupMenu(mContext,holder.btnMore);
                popupMenu.inflate(R.menu.menu_more);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.post_edit:{
                            Intent i = new Intent(mContext, EditBloodRequestActivity.class);
                            i.putExtra("brKey",bloodRequest.getBrkey());
                            mContext.startActivity(i);
                        }
                        return true;
                        case R.id.post_delete:{
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Alert Delete")
                                    .setMessage("Are you sure you want to delete this Request?")
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
        getComments(bloodRequest.getBrkey(), holder.comments);
        getRequestPublisher(holder.publisher,bloodRequest.getUserid());
        holder.messages.setText(bloodRequest.getMessage());
        holder.fullname.setText("Name : "+bloodRequest.getFullname());
        holder.bloodgroup.setText("Blood Group : "+bloodRequest.getBloodgroup());
        holder.hospital.setText("Hospital : "+bloodRequest.getHospital());
        holder.cnumber.setText("Contact Number : "+bloodRequest.getCnumber());
        holder.enumber.setText("Emergency Number : "+bloodRequest.getEnumber());
        holder.publisher.setOnClickListener(v -> {
            Intent i = new Intent(mContext, ProfileActivity.class);
            i.putExtra("userId", bloodRequest.getUserid());
            mContext.startActivity(i);
        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, BloodRequestCommentActivity.class);
                i.putExtra("brkey", bloodRequest.getBrkey());
                i.putExtra("publisherid", bloodRequest.getUserid());
                mContext.startActivity(i);

            }
        });
        holder.c.setOnClickListener(v -> mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", bloodRequest.getCnumber(), null))));
        holder.e.setOnClickListener(v -> mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", bloodRequest.getEnumber(), null))));

    }




    @Override
    public int getItemCount() {
        return mBloodRequest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageButton btnMore;
        public ImageView image;
        public TextView fullname,comments,publisher,messages,bloodgroup,hospital,cnumber,enumber,c,e;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            btnMore = itemView.findViewById(R.id.btnMore);
            comments = itemView.findViewById(R.id.comment);
            fullname = itemView.findViewById(R.id.name);
            publisher = itemView.findViewById(R.id.publisher);
            messages = itemView.findViewById(R.id.message);
            bloodgroup = itemView.findViewById(R.id.bloodgroup);
            hospital = itemView.findViewById(R.id.hospitalName);
            cnumber = itemView.findViewById(R.id.cnumber);
            enumber = itemView.findViewById(R.id.enumber);
            c = itemView.findViewById(R.id.contact);
            e = itemView.findViewById(R.id.emergency);
        }
    }

    private void getComments(String brkey, TextView comments) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("BloodRequest").child(brkey).child("Comments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("("+dataSnapshot.getChildrenCount()+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getRequestPublisher(TextView publisher, String userid) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (userid.equals(snapshot.child("userid").getValue())){
                        if(snapshot.child("fullname").getValue()!=null) { publisher.setText("Requested by: "+snapshot.child("fullname").getValue().toString()); }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
