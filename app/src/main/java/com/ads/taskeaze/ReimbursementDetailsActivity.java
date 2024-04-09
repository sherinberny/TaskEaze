package com.ads.taskeaze;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Commit Re

public class ReimbursementDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_details);

        // Retrieve claim details and reimbursement amount from intent extras
        String date = getIntent().getStringExtra("date");
        double amount = getIntent().getDoubleExtra("amount", 0);
        String description = getIntent().getStringExtra("description");
        double reimbursementAmount = getIntent().getDoubleExtra("reimbursementAmount", 0);

        // Display claim details and reimbursement amount in TextViews
        TextView textViewDate = findViewById(R.id.textViewDate);
        TextView textViewAmount = findViewById(R.id.textViewAmount);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewReimbursementAmount = findViewById(R.id.textViewReimbursementAmount);

        textViewDate.setText("Date: " + date);
        textViewAmount.setText("Amount: " + amount);
        textViewDescription.setText("Description: " + description);
        textViewReimbursementAmount.setText("Reimbursement Amount: " + reimbursementAmount);
    }
    public void closeActivity(View view) {


        finish(); // Close the activity and return to the previous one


    }



}


