package com.ads.taskeaze.model;

public class LeaveRequest {
    private String startDate;

    private String id;
    private String endDate;
    private String leaveType;
    private String notes;
    private String status;
    private String date;

    // Empty constructor required for Firebase serialization
    public LeaveRequest() {
    }

    public LeaveRequest(String startDate, String endDate, String leaveType, String notes, String status, String date) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.notes = notes;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

