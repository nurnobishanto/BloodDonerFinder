package com.zedapp.reddrop_blooddonorfinder.adapter;

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


import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.models.HelpLineModels;

import java.util.List;

public class HelpLineAdapter extends RecyclerView.Adapter<HelpLineAdapter.ViewHolder>{
    public Context mContext;
    public List<HelpLineModels> helpLineModelsList;

    public HelpLineAdapter(Context mContext, List<HelpLineModels> helpLineModelsList) {
        this.mContext = mContext;
        this.helpLineModelsList = helpLineModelsList;
    }

    @NonNull
    @Override
    public HelpLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.help_item,parent,false);
        return new HelpLineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpLineAdapter.ViewHolder holder, int position) {
        final HelpLineModels models  = helpLineModelsList.get(position);
        holder.name.setText(models.getName());
        holder.image.setImageResource(models.getIcon());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+880"+models.getNumber(), null)));
            }
        });
        holder.number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+880"+models.getNumber(), null)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return helpLineModelsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView image,number;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.image);
            cardView=itemView.findViewById(R.id.card);
            number=itemView.findViewById(R.id.number);
        }
    }
}
