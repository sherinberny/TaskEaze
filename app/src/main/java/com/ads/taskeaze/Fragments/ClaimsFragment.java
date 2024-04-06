package com.ads.taskeaze.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ads.taskeaze.MainActivity;
import com.ads.taskeaze.R;
import com.ads.taskeaze.RegistrationActivity;
import com.ads.taskeaze.ReimbursementDetailsActivity;
import com.ads.taskeaze.model.ClaimDAO;

// Added code for claims
public class ClaimsFragment extends Fragment {

    private EditText editTextDate;
    private EditText editTextAmount;
    private EditText editTextDescription;
    private Button buttonSubmit;

    private Button buttonReimburse;
    private ClaimDAO claimDAO;

    public ClaimsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        claimDAO = new ClaimDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_claims, container, false);

        // Initialize views
        editTextDate = rootView.findViewById(R.id.editTextDate);
        editTextAmount = rootView.findViewById(R.id.editTextAmount);
        editTextDescription = rootView.findViewById(R.id.editTextDescription);
        buttonSubmit = rootView.findViewById(R.id.buttonSubmit);
        buttonReimburse = rootView.findViewById(R.id.buttonReimburse);


        // Set OnClickListener for submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                submitClaim();
            }
        });
        buttonReimburse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editTextDate.getText().toString();
                double amount = Double.parseDouble(editTextAmount.getText().toString());
                String description = editTextDescription.getText().toString();

                // Calculate reimbursement amount (example logic)
                double reimbursementAmount = calculateReimbursementAmount(amount, description);

                // Start ReimbursementDetailsActivity and pass claim details and reimbursement amount
                Intent intent = new Intent(getActivity(), ReimbursementDetailsActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("amount", amount);
                intent.putExtra("description", description);
                intent.putExtra("reimbursementAmount", reimbursementAmount);
                startActivity(intent);
            }
        });
        return rootView;


    }

    // Method to calculate reimbursement amount (example logic)
    private double calculateReimbursementAmount(double amount, String description) {
        // Your reimbursement logic here
        // For demonstration, let's assume reimbursement amount is 75% if description contains specific keyword
        if (description.toLowerCase().contains("personal")) {
            return amount * 0.75; // 75% of claim amount
        } else {
            return 0; // No reimbursement
        }
    }
public void submitClaim(){
    // Get input values
    String date = editTextDate.getText().toString();
    String amountText = editTextAmount.getText().toString();
    String description = editTextDescription.getText().toString();

    // Check if any of the fields are empty
    if (TextUtils.isEmpty(date)) {
        // Date is empty
        Toast.makeText(getActivity(), "Please enter a date", Toast.LENGTH_SHORT).show();
        return;
    }

    if (TextUtils.isEmpty(amountText)) {
        // Amount is empty
        Toast.makeText(getActivity(), "Please enter an amount", Toast.LENGTH_SHORT).show();
        return;
    }

    // Check if amount is valid
    double amount;
    try {
        amount = Double.parseDouble(amountText);
        if (amount <= 0) {
            // Amount is not positive
            Toast.makeText(getActivity(), "Please enter a positive amount", Toast.LENGTH_SHORT).show();
            return;
        }
    } catch (NumberFormatException e) {
        // Amount is not a valid number
        Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        return;
    }

    if (TextUtils.isEmpty(description)) {
        // Description is empty
        Toast.makeText(getActivity(), "Please enter a description", Toast.LENGTH_SHORT).show();
        return;
    }

    // Insert claim into database
    long newRowId = claimDAO.insertClaim(date, amount, description);


    Log.d("DB Values","Values entered in DB"+date);
    if (newRowId != -1) {
        // Claim submitted successfully
        Toast.makeText(getActivity(), "Claim submitted successfully", Toast.LENGTH_SHORT).show();
    } else {
        // Failed to submit claim
        Toast.makeText(getActivity(), "Failed to submit claim", Toast.LENGTH_SHORT).show();
    }}

    }
