package com.ads.taskeaze.model;

public class LeaveRequestModel {

    private String userId;
    private int status;
    private String leaveStartDate;
    private String leaveEndDate;

    public LeaveRequestModel(){}

    public LeaveRequestModel(String userId, int status, String leaveStartDate, String leaveEndDate) {
        this.userId = userId;
        this.status = status;
        this.leaveStartDate = leaveStartDate;
        this.leaveEndDate = leaveEndDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLeaveStartDate() {
        return leaveStartDate;
    }

    public void setLeaveStartDate(String leaveStartDate) {
        this.leaveStartDate = leaveStartDate;
    }

    public String getLeaveEndDate() {
        return leaveEndDate;
    }

    public void setLeaveEndDate(String leaveEndDate) {
        this.leaveEndDate = leaveEndDate;
    }
}
