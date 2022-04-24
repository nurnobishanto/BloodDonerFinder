package com.zedapp.reddrop_blooddonorfinder.profile;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zedapp.reddrop_blooddonorfinder.NewsFeedActivity;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.adapter.FeedAdapter;
import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;

import java.util.ArrayList;
import java.util.List;


public class MyFeedFragment extends Fragment {

    private String userId;

    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedAdapter adapter;
    private List<FeedModels> list;
    private Dialog dialog;
    private TextView check;
    int Image_Request_Code = 7;
    private Uri FilePathUri;

    private ImageView imageView;
    private EditText desc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_feed, container, false);


        userId = getActivity().getIntent().getStringExtra("userId");

        storageReference = FirebaseStorage.getInstance().getReference("NewsFeedImage");
        check = view.findViewById(R.id.check);

        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        readFeedData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readFeedData();
            }
        });

        return view;
    }
    private void readFeedData() {
        swipeRefreshLayout.setRefreshing(true);
        list = new ArrayList<>();
        list.clear();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("News Feed");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {

                    if(snapshot.child("userid").getValue().toString().equals(userId))
                    {
                        FeedModels obj = new FeedModels();
                        obj.setUserid(snapshot.child("userid").getValue().toString());
                        if(snapshot.child("nfKey").getValue()!=null) { obj.setNfKey(snapshot.child("nfKey").getValue().toString()); }
                        if(snapshot.child("image").getValue()!=null) { obj.setImage(snapshot.child("image").getValue().toString()); }
                        if(snapshot.child("desc").getValue()!=null) { obj.setDesc(snapshot.child("desc").getValue().toString()); }
                        list.add(obj);

                    }

                }

                swipeRefreshLayout.setRefreshing(false);
                adapter = new FeedAdapter(getContext(),list);
                recyclerView.setAdapter(adapter);
                if (list.size()<1){
                    check.setVisibility(View.VISIBLE);
                }else {
                    check.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}