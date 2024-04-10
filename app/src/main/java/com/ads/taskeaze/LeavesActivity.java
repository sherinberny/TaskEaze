package com.ads.taskeaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.ads.taskeaze.adapters.LeaveRequestAdapter;
import com.ads.taskeaze.model.LeaveRequest;
import com.ads.taskeaze.utils.PreferenceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeavesActivity extends SupportActivity {

    RecyclerView recyclerView;
    private LeaveRequestAdapter adapter;
    private List<LeaveRequest> leaveRequests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_leaves, relativeLayout);
        getSupportActionBar().setTitle(getResources().getString(R.string.leaves_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.leaves_recycleview_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(LeavesActivity.this));

        // Initialize leaveRequests list
        leaveRequests = new ArrayList<>();

        // Initialize adapter with empty list
        adapter = new LeaveRequestAdapter(leaveRequests);
        recyclerView.setAdapter(adapter);

        // Load leave requests data
        loadLeaveRequests();

        findViewById(R.id.add_leaves).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeavesActivity.this, NewLeavesActivity.class);
                intent.setAction("Add Leave");
                startActivity(intent);
            }
        });

        ((SwipeRefreshLayout)findViewById(R.id.leaves_swipe_referesh_id)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((SwipeRefreshLayout)findViewById(R.id.leaves_swipe_referesh_id)).setRefreshing(false);
                loadLeaveRequests();
            }
        });
    }

    private void loadLeaveRequests() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaveRequestsRef = database.getReference("leave_requests").child(PreferenceUtils.getUserIdFromThePreference(LeavesActivity.this));

        leaveRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leaveRequests.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LeaveRequest leaveRequest = snapshot.getValue(LeaveRequest.class);
                    leaveRequest.setId(snapshot.getKey()); // Set the unique identifier
                    leaveRequests.add(leaveRequest);
                }

                if(leaveRequests.size()>0){
                    findViewById(R.id.no_leaves_found).setVisibility(View.GONE);
                }
                else{
                    findViewById(R.id.no_leaves_found).setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}