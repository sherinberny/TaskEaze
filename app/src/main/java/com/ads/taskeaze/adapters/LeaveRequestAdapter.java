package com.ads.taskeaze.adapters;

import static com.ads.taskeaze.utils.ConstantUtils.KEY_LEAVE_REQUEST_TABLE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_STATUS;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_USER;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.taskeaze.R;
import com.ads.taskeaze.model.LeaveRequest;
import com.ads.taskeaze.model.LeaveRequestModel;
import com.ads.taskeaze.model.UserModel;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.PreferenceUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class LeaveRequestAdapter extends RecyclerView.Adapter<LeaveRequestAdapter.ViewHolder> {
    private List<LeaveRequest> leaveRequests;

    public LeaveRequestAdapter(List<LeaveRequest> leaveRequests) {
        this.leaveRequests = leaveRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leave_fragment_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveRequest leaveRequest = leaveRequests.get(position);
        holder.bind(leaveRequest);
    }

    @Override
    public int getItemCount() {
        return leaveRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewStartDate;
        private TextView textViewEndDate;
        private TextView textViewLeaveType;
        private TextView textViewNoOfDays;
        private TextView textViewRemark;
        private TextView textViewAppliedOn;
        private TextView textViewStatus;
        private Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStartDate = itemView.findViewById(R.id.leave_fragment_row_from_textview_id);
            textViewEndDate = itemView.findViewById(R.id.leave_fragment_row_to_textview_id);
            textViewLeaveType = itemView.findViewById(R.id.leave_fragment_row_leave_type_id);
            textViewNoOfDays = itemView.findViewById(R.id.leave_fragment_row_no_of_days_id);
            textViewRemark = itemView.findViewById(R.id.leave_fragment_row_remark_id);
            textViewAppliedOn = itemView.findViewById(R.id.leave_fragment_row_applied_on);
            textViewStatus = itemView.findViewById(R.id.leave_fragment_row_status_id);
            cancelButton = itemView.findViewById(R.id.leave_fragment_row_cancel_button_id);
        }

        public void bind(LeaveRequest leaveRequest) {
            textViewStartDate.setText(leaveRequest.getStartDate());
            textViewEndDate.setText(leaveRequest.getEndDate());
            textViewLeaveType.setText(leaveRequest.getLeaveType());
            textViewNoOfDays.setText(String.valueOf(CommonFunc.getDaysBetween(leaveRequest.getStartDate(), leaveRequest.getEndDate())));
            textViewRemark.setText(leaveRequest.getNotes());
            textViewAppliedOn.setText(leaveRequest.getDate());

            textViewStatus.setText(leaveRequest.getStatus());
            if(leaveRequest.getStatus().equals("Applied")) {
                textViewStatus.setTextColor(Color.BLUE);
            }
            else if(leaveRequest.getStatus().equals("Approved")) {
                textViewStatus.setTextColor(Color.GREEN);
            }
            else if(leaveRequest.getStatus().equals("REJECTED")) {
                textViewStatus.setTextColor(Color.RED);
            }

            // Set onClickListener for cancel button if needed
            // cancelButton.setOnClickListener(v -> {
            //     // Handle cancel button click
            // });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update status in Firebase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference leaveRequestsRef = database.getReference("leave_requests").child(PreferenceUtils.getUserIdFromThePreference(v.getContext())).child(leaveRequest.getId());

                    // Update the status to "Cancelled"
                    leaveRequestsRef.child("status").setValue("Cancelled")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update the UI or show a toast message indicating success
                                    textViewStatus.setText("Cancelled");
                                    textViewStatus.setTextColor(Color.RED);
                                    Toast.makeText(itemView.getContext(), "Leave request cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors
                                    Toast.makeText(itemView.getContext(), "Failed to cancel leave request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
    }
}
