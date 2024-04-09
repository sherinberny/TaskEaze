package com.ads.taskeaze.model;


public class Payslip {

    private String employeeName;
    private String employeeId;
    private String month;
    private int totalHoursWorked;
    private double hourlyRate;
    private double totalEarnings;

    // Constructor
    public Payslip(String employeeName, String employeeId, String month, int totalHoursWorked, double hourlyRate) {
        this.employeeName = employeeName;
        this.employeeId = employeeId;
        this.month = month;
        this.totalHoursWorked = totalHoursWorked;
        this.hourlyRate = hourlyRate;
        this.totalEarnings = calculateTotalEarnings(totalHoursWorked, hourlyRate);
    }

    // Getters and setters

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(int totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    // Method to calculate total earnings
    private double calculateTotalEarnings(int totalHoursWorked, double hourlyRate) {
        return totalHoursWorked * hourlyRate;
    }
}
