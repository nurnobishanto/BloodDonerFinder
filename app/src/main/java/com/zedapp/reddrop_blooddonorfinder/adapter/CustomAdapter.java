package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.annotation.SuppressLint;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.models.CustomModels;


import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>  {
    public Context mContext;
    public List<CustomModels> modelsList;
    public List<CustomModels> filterList;
    private FirebaseUser fuser;

    public CustomAdapter(Context mContext, List<CustomModels> modelsList) {
        this.mContext = mContext;
        this.modelsList = modelsList;
        filterList =  new ArrayList<>(modelsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.custom_item,parent,false);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final CustomModels models  = modelsList.get(position);

        holder.name.setText(models.getName());
        holder.city.setText(models.getCity());
        holder.icon.setImageResource(models.getIcon());
        holder.address.setText(models.getAddress());
        holder.cnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "880"+models.getPhonenumber(), null)));
            }
        });
        holder.enumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "880"+models.getHotline(), null)));
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name,city,address;
        CardView cardView;
        ImageView cnumber,enumber,icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card);
            icon=itemView.findViewById(R.id.item_icon);
            address=itemView.findViewById(R.id.address);
            name=itemView.findViewById(R.id.name);
            city=itemView.findViewById(R.id.city);
            cnumber=itemView.findViewById(R.id.cnumber);
            enumber=itemView.findViewById(R.id.enumber);
        }
    }
}
