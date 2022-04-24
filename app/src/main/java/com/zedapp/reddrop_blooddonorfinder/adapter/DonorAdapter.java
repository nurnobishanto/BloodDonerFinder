package com.zedapp.reddrop_blooddonorfinder.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zedapp.reddrop_blooddonorfinder.profile.ProfileActivity;
import com.zedapp.reddrop_blooddonorfinder.R;

import com.zedapp.reddrop_blooddonorfinder.models.DonorModels;


import java.util.ArrayList;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> implements Filterable{
    public Context mContext;
    public List<DonorModels> mDonor;
    public List<DonorModels> mFilterDonor;
    private FirebaseUser fuser;

    public DonorAdapter(Context mContext, List<DonorModels> mDonor) {
        this.mContext = mContext;
        this.mDonor = mDonor;
        this.mFilterDonor = mDonor;
    }

    @NonNull
    @Override
    public DonorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.donor_item,parent,false);

        return new DonorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorAdapter.ViewHolder holder, int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        final DonorModels donorModels  = mDonor.get(position);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("userId", donorModels.getUserid());
                mContext.startActivity(i);
            }
        });
        if(!donorModels.getFullName().equals("")){
            holder.donor.setVisibility(View.VISIBLE);
        }
        holder.donor.setText(donorModels.getFullName());
        holder.donor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProfileActivity.class);
                i.putExtra("userId", donorModels.getUserid());
                mContext.startActivity(i);
            }
        });
        if(!donorModels.getBloodgroup().equals("")){
            holder.bloodgroup.setVisibility(View.VISIBLE);
            holder.bloodgroup.setText(donorModels.getBloodgroup());
        }
        if(!donorModels.getCity().equals("")){
            holder.city.setVisibility(View.VISIBLE);
            holder.city.setText(donorModels.getCity());
        }
        if(!donorModels.getAddress().equals("")){
            holder.address.setVisibility(View.VISIBLE);
            holder.address.setText(donorModels.getAddress());
        }
        if(!donorModels.getCnumber().equals("Not Added Yet!")){
            holder.cnumber.setVisibility(View.VISIBLE);
            holder.cnumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "880"+donorModels.getCnumber(), null)));
                }
            });
        }

        if(!donorModels.getEnumber().equals("Not Added Yet!")) {
            holder.enumber.setVisibility(View.VISIBLE);

        holder.enumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "880"+donorModels.getEnumber(), null)));
            }
        });

    }


    }

    @Override
    public int getItemCount() {
        return mDonor.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilterDonor = (List<DonorModels>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<DonorModels> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mDonor;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<DonorModels> getFilteredResults(String constraint) {
        List<DonorModels> results = new ArrayList<>();

        for (DonorModels item : mDonor) {
            if (item.getFullName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView  donor,city,address,bloodgroup,userid;
        ImageView cnumber,enumber;
        CardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            donor=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);

            card=itemView.findViewById(R.id.card);

            bloodgroup=itemView.findViewById(R.id.bloodgroup);
            city=itemView.findViewById(R.id.city);
            cnumber=itemView.findViewById(R.id.cnumber);
            enumber=itemView.findViewById(R.id.enumber);
        }
    }
}
