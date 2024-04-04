package com.ads.taskeaze.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.taskeaze.R;
import com.ads.taskeaze.RegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OTPFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OTPFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View viewFragment = null;

    private long minOTPWaitTime = 30000;

    String userId;

    public OTPFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OTPFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OTPFragment newInstance(String param1, String param2) {
        OTPFragment fragment = new OTPFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewFragment = inflater.inflate(R.layout.fragment_o_t_p, container, false);
        countDownTimer.start();


        assert getArguments() != null;
        String mob = getArguments().getString("mobno");
        String countryCode = "+1 ";
        ((TextView) viewFragment.findViewById(R.id.phonenumber_tv)).setText(countryCode + mob);
        userId = getArguments().getString("userID");

        viewFragment.findViewById(R.id.numberEdit_IV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
            }
        });


        viewFragment.findViewById(R.id.fragment_verify_otp_submit_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.requireNonNull(((AppCompatEditText) viewFragment.findViewById(R.id.fragment_verify_otp_enter_otp_edittext_id))
                        .getText()).toString().isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Objects.requireNonNull(requireArguments().getString("mVerificationId")),
                            Objects.requireNonNull(((AppCompatEditText) viewFragment.findViewById(R.id.fragment_verify_otp_enter_otp_edittext_id))
                                    .getText()).toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewFragment.findViewById(R.id.fragment_verify_otp_resend_otp_textview_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((AppCompatTextView) viewFragment.findViewById(R.id.fragment_verify_otp_resend_otp_textview_id))
                        .getText().toString().equals("Resend OTP?"))
                    ((RegistrationActivity) getActivity()).triggerFireBaseOTP();

                else if (v.getId() == R.id.fragment_verify_otp_submit_button_id)

                    if (!((AppCompatEditText) viewFragment.findViewById(R.id.fragment_verify_otp_enter_otp_edittext_id))
                            .getText().toString().isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(Objects.requireNonNull(requireArguments().getString("mVerificationId")),
                                ((AppCompatEditText) viewFragment.findViewById(R.id.fragment_verify_otp_enter_otp_edittext_id))
                                        .getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        Toast.makeText(getActivity(), "Please enter OTP", Toast.LENGTH_SHORT).show();
                    }
            }
        });
        return viewFragment;
    }


    private final CountDownTimer countDownTimer = new CountDownTimer(minOTPWaitTime, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            ((AppCompatTextView) viewFragment.findViewById(R.id.fragment_verify_otp_resend_otp_textview_id))
                    .setText("Max wait time : " + millisUntilFinished / 1000);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onFinish() {
            ((AppCompatTextView) viewFragment.findViewById(R.id.fragment_verify_otp_resend_otp_textview_id))
                    .setText("Resend OTP?");

        }
    };


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            ((RegistrationActivity) requireActivity()).getUserDetailsById(getActivity(), userId);

                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getActivity(), "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissProgressDialog();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////// progress. . .
    private Dialog dialogProgress = null;

    //
    public void showProgressDialog() {
        if (dialogProgress == null)
            dialogProgress = new Dialog(getActivity());
        dialogProgress.setCancelable(false);
        dialogProgress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogProgress.setContentView(R.layout.loading_layout);
        if (!dialogProgress.isShowing())
            dialogProgress.show();

    }

    public void dismissProgressDialog() {
        if (dialogProgress != null) {
            if (dialogProgress.isShowing()) {
                dialogProgress.dismiss();
            }
            dialogProgress = null;
        }
    }
}