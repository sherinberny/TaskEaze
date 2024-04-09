package com.ads.taskeaze.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offline_meetings")
public class OfflineMeetings {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String MeetingDateRefNo;



    public String onDate;
    public String InTime;
    public String OutTime;
    public String Status;
    public String Purpose;

    public String FullName;

    public String startLocation;

    public String startLat;

    public String startLng;

    public String Phone;

    public String Attachment;

    public String ModeOfTravel;
    

    public String Remarks;

    public String FollowupDateRefNo;

    public String FollowUpTime;

    public String EndLatitude;

    public String EndLongitude;

    public String EndLocation;

    public String isMockLocation;

    public String followupmobiledate;
    
    public String setRootDistanceTravelled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMeetingDateRefNo() {
        return MeetingDateRefNo;
    }

    public void setMeetingDateRefNo(String meetingDateRefNo) {
        MeetingDateRefNo = meetingDateRefNo;
    }

    public String getInTime() {
        return InTime;
    }

    public void setInTime(String inTime) {
        InTime = inTime;
    }

    public String getOutTime() {
        return OutTime;
    }

    public void setOutTime(String outTime) {
        OutTime = outTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPurpose() {
        return Purpose;
    }

    public void setPurpose(String purpose) {
        Purpose = purpose;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAttachment() {
        return Attachment;
    }

    public void setAttachment(String attachment) {
        Attachment = attachment;
    }

    public String getModeOfTravel() {
        return ModeOfTravel;
    }

    public void setModeOfTravel(String modeOfTravel) {
        ModeOfTravel = modeOfTravel;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getFollowupDateRefNo() {
        return FollowupDateRefNo;
    }

    public void setFollowupDateRefNo(String followupDateRefNo) {
        FollowupDateRefNo = followupDateRefNo;
    }

    public String getFollowUpTime() {
        return FollowUpTime;
    }

    public void setFollowUpTime(String followUpTime) {
        FollowUpTime = followUpTime;
    }

    public String getEndLatitude() {
        return EndLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        EndLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return EndLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        EndLongitude = endLongitude;
    }

    public String getEndLocation() {
        return EndLocation;
    }

    public void setEndLocation(String endLocation) {
        EndLocation = endLocation;
    }

    public String getIsMockLocation() {
        return isMockLocation;
    }

    public void setIsMockLocation(String isMockLocation) {
        this.isMockLocation = isMockLocation;
    }

    public String getFollowupmobiledate() {
        return followupmobiledate;
    }

    public void setFollowupmobiledate(String followupmobiledate) {
        this.followupmobiledate = followupmobiledate;
    }

    public String getRootDistanceTravelled() {
        return setRootDistanceTravelled;
    }

    public void setRootDistanceTravelled(String setRootDistanceTravelled) {
        this.setRootDistanceTravelled = setRootDistanceTravelled;
    }

    public String getOnDate() {
        return onDate;
    }

    public void setOnDate(String onDate) {
        this.onDate = onDate;
    }
}
