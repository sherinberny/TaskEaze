package com.ads.taskeaze.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ads.taskeaze.R;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import java.util.List;

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingViewHolder> {

    private List<OfflineMeetings> meetingsList;

    public MeetingsAdapter(List<OfflineMeetings> meetingsList) {
        this.meetingsList = meetingsList;
    }

    @NonNull
    @Override
    public MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_list_card, parent, false);
        return new MeetingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        OfflineMeetings meeting = meetingsList.get(position);
        holder.bind(meeting);
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }

    static class MeetingViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewPurpose;
        private TextView textViewClient;
        private TextView textViewStatus;
        private TextView textViewLocation;
        // Add more TextViews for other meeting details

        MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_meeting_date_time);
            textViewClient = itemView.findViewById(R.id.text_meeting_attendee);
            textViewPurpose = itemView.findViewById(R.id.text_meeting_purpose);
            textViewLocation = itemView.findViewById(R.id.text_meeting_location);
            textViewStatus = itemView.findViewById(R.id.text_meeting_status);
        }

        void bind(OfflineMeetings meeting) {
            textViewDate.setText(meeting.getOnDate() + "   " + meeting.getInTime() + " - " + meeting.getOutTime());
            textViewClient.setText(meeting.getFullName());
            textViewLocation.setText(meeting.getEndLocation());
            textViewPurpose.setText(meeting.getPurpose());
            textViewStatus.setText(meeting.getStatus());
        }
    }
}

