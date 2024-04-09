package com.ads.taskeaze.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "claims")
public class Claim {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private double amount;
    private String description;

    // Constructor
    public Claim(String date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
