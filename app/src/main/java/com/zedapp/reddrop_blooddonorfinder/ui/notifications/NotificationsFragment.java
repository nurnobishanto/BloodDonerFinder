package com.zedapp.reddrop_blooddonorfinder.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zedapp.reddrop_blooddonorfinder.R;
import com.zedapp.reddrop_blooddonorfinder.adapter.FeedAdapter;
import com.zedapp.reddrop_blooddonorfinder.adapter.NotificationAdapter;
import com.zedapp.reddrop_blooddonorfinder.databinding.FragmentNotificationsBinding;
import com.zedapp.reddrop_blooddonorfinder.models.FeedModels;
import com.zedapp.reddrop_blooddonorfinder.models.NotificationsModels;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationsModels> modelsList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView check;
    private FirebaseAuth firebaseAuth;
    String myId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        myId = firebaseAuth.getCurrentUser().getUid().toString();
        check=root.findViewById(R.id.check);
        swipeRefreshLayout = root.findViewById(R.id.swipe);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setRefreshing(false);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        readNotifications();

        swipeRefreshLayout.setOnRefreshListener(() -> readNotifications());
        return root;
    }

    private void readNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        modelsList = new ArrayList<>();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String typeKey="";
                    String message= "";
                    String type="";
                    String nKey="";
                    String reciever ="";
                    String sender ="";

                    if(snapshot.child("typeKey").getValue()!=null) { typeKey = snapshot.child("typeKey").getValue().toString(); }
                    if(snapshot.child("message").getValue()!=null) { message = snapshot.child("message").getValue().toString(); }
                    if(snapshot.child("type").getValue()!=null) { type = snapshot.child("type").getValue().toString(); }
                    if(snapshot.child("nKey").getValue()!=null) { nKey = snapshot.child("nKey").getValue().toString(); }
                    if(snapshot.child("reciever").getValue()!=null) { reciever = snapshot.child("reciever").getValue().toString(); }
                    if(snapshot.child("sender").getValue()!=null) { sender = snapshot.child("sender").getValue().toString(); }

                    NotificationsModels obj = new NotificationsModels(typeKey,message,type,nKey,reciever,sender);
                    if (reciever.equals(myId) || reciever.equals("All") || reciever.equals("BR") || reciever.equals("MSG")){
                        if (!myId.equals(sender)){
                            modelsList.add(obj);

                        }

                    }

                    swipeRefreshLayout.setRefreshing(false);
                    adapter =new NotificationAdapter(getContext(),modelsList);
                    recyclerView.setAdapter(adapter);
                    if (modelsList.isEmpty()){
                        check.setVisibility(View.VISIBLE);
                    }else  check.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}