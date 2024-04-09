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
import com.ads.taskeaze.model.entities.ClaimRepository;

// Added code for claims
public class ClaimsFragment extends Fragment {

    private EditText editTextDate;
    private EditText editTextAmount;
    private EditText editTextDescription;
    private Button buttonSubmit;

    private Button buttonReimburse;
    private ClaimDAO claimDAO;
    private String distanceValue;

    private EditText editTextDistance;

    private ClaimRepository claimRepository;

    public ClaimsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        claimRepository = new ClaimRepository(getActivity());

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            distanceValue = bundle.getString("distance");
//        }
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
        editTextDistance = rootView.findViewById(R.id.editTextDistance);

        if (distanceValue != null) {
            editTextDistance.setText(distanceValue);
        }




        // Set OnClickListener for submit buttons
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                submitClaim();
            }
        });

        // Commit
        buttonReimburse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String date = editTextDate.getText().toString();
                double amount;
                String description;

                // Get amount and description
                try {
                    amount = Double.parseDouble(editTextAmount.getText().toString());
                    description = editTextDescription.getText().toString();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get distance value
                String distanceValue = editTextDistance.getText().toString();

                if (TextUtils.isEmpty(distanceValue)) {
                    // Handle null or empty distance value
                    Toast.makeText(getActivity(), "Distance value is null or empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Calculate reimbursement amount (example logic)
                double reimbursementAmount = calculateReimbursementAmount(amount, distanceValue, description);

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
    private double calculateReimbursementAmount(double amount, String distanceValue, String description) {
        double distance = Double.parseDouble(distanceValue);
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }

        double baseReimbursementRate = 0.75; // Example: 75%
        double distanceReimbursementRate = 0.0; // Initialize with 0%
        if (distance > 50) { // Example threshold for longer distances
            distanceReimbursementRate = 0.10; // Example: 10% additional reimbursement for distances greater than 50 units
        }

        double descriptionReimbursementRate = 0.0; // Initialize with 0%
        if (description.toLowerCase().contains("business")) { // Example keyword in description
            descriptionReimbursementRate = 0.20; // Example: 20% additional reimbursement for business-related claims
        }

        double reimbursementAmount = 0.0; // Initialize with 0
        if (description.toLowerCase().contains("personal")) {
            reimbursementAmount = amount * baseReimbursementRate; // 75% of claim amount
        } else if (description.toLowerCase().contains("food")) {
            reimbursementAmount = amount * 0.50; // Example: 50% of claim amount for food-related claims
        }

        // Calculate total reimbursement amount including distance and description rates
        reimbursementAmount += amount * distanceReimbursementRate;
        reimbursementAmount += amount * descriptionReimbursementRate;

        return reimbursementAmount;
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
    long newRowId = claimRepository.insertClaim(date, amount, description);


    Log.d("DB Values","Values entered in DB"+date);
    if (newRowId != -1) {
        // Claim submitted successfully
        Toast.makeText(getActivity(), "Claim submitted successfully", Toast.LENGTH_SHORT).show();
    } else {
        // Failed to submit claim
        Toast.makeText(getActivity(), "Failed to submit claim", Toast.LENGTH_SHORT).show();
    }}



    }
