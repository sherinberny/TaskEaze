package com.ads.taskeaze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.taskeaze.R;
import com.ads.taskeaze.model.LeaveRequestModel;
import com.ads.taskeaze.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.ads.taskeaze.utils.ConstantUtils.KEY_DEPARTMENT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_FIRST_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_LAST_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_LEAVE_END_DATE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_LEAVE_REQUEST_TABLE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_LEAVE_START_DATE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_STATUS;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_USER;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_USER_ID;
import static com.ads.taskeaze.utils.ConstantUtils.MAIL_ADDRESS_CC;
import static com.ads.taskeaze.utils.ConstantUtils.MAIL_ADDRESS_MANAGER;
import static com.ads.taskeaze.utils.ConstantUtils.MAIL_EMPLOYER;
import static com.ads.taskeaze.utils.ConstantUtils.MAIL_PW;

public class NewLeavesActivity extends SupportActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbref;
    String userId;
    String firstName;
    String lastName;
    String department;
    String status;
    LocalDate sDate;
    LocalDate eDate;
    EditText editTxtLeaveStartDate;
    EditText editTxtLeaveEndDate;
    TextView txtViewRequestStatus;
    Button btnLeaveRequestConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_new_leaves, relativeLayout);
        getSupportActionBar().setTitle(getResources().getString(R.string.leaves_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.btnSendLeaveRequest).setBackground(getDrawable(R.drawable.button_rounded_blue));
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbref = firebaseDatabase.getReference(KEY_USER);
        userId = firebaseAuth.getCurrentUser().getUid();
        status = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        editTxtLeaveStartDate = findViewById(R.id.editTxtLeaveStartDate);
        editTxtLeaveEndDate = findViewById(R.id.editTxtLeaveEndDate);

        DatePicker datePicker = new DatePicker();
        editTxtLeaveStartDate.setOnClickListener(datePicker);
        editTxtLeaveEndDate.setOnClickListener(datePicker);

        txtViewRequestStatus = findViewById(R.id.txtViewRequestStatus);
        btnLeaveRequestConfirm = findViewById(R.id.btnLeaveRequestConfirm);
        getRequestStatus();

        Button btnSendLeaveRequest = findViewById(R.id.btnSendLeaveRequest);
        btnSendLeaveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startDate = editTxtLeaveStartDate.getText().toString().trim();
                String endDate = editTxtLeaveEndDate.getText().toString().trim();

                if(startDate.length() == 0 || endDate.length() == 0) {
                    Toast.makeText(view.getContext(), "Please enter both date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sDate = LocalDate.parse(convertStringDate(startDate));
                    eDate = LocalDate.parse(convertStringDate(endDate));

                    if(sDate.isAfter(eDate)) {
                        Toast.makeText(view.getContext(), "Invalid date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }



                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, String> requestUser = new HashMap<>();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            if(user.getUserId() != null) {
                                if(user.getUserId().equals(userId)) {
                                    firstName = user.getFirstName();
                                    lastName = user.getLastName();
                                    department = user.getDept();

                                    requestUser = new HashMap<>();
                                    requestUser.put(KEY_FIRST_NAME, firstName);
                                    requestUser.put(KEY_LAST_NAME, lastName);
                                    requestUser.put(KEY_DEPARTMENT, department);
                                    requestUser.put(KEY_LEAVE_START_DATE, startDate);
                                    requestUser.put(KEY_LEAVE_END_DATE, endDate);

                                    if(sendLeaveRequest(requestUser)) {
                                        Toast.makeText(view.getContext(), "Success Leave Request", Toast.LENGTH_SHORT).show();
                                        createLeaveRequestRecord(requestUser);
                                    } else {
                                        Toast.makeText(view.getContext(), "Fail Leave Request, Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private class DatePicker implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            EditText editTxtDate;
            if(view.getId() == R.id.editTxtLeaveStartDate) {
                editTxtDate = view.findViewById(R.id.editTxtLeaveStartDate);
            } else {
                editTxtDate = view.findViewById(R.id.editTxtLeaveEndDate);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    view.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(android.widget.DatePicker datePicker, int i, int i1, int i2) {
                    String dateMessage = i2 + "-" + (i1 + 1) + "-" + year;
                    editTxtDate.setText(dateMessage);
                }
            }, year, month, day);
            datePickerDialog.show();
        }
    }

    private String convertStringDate(String date) {

        String[] arr = date.split("-");
        if (arr[1].length() == 1) {
            arr[1] = "0" + arr[1];
        }

        if (arr[0].length() == 1) {
            arr[0] = "0" + arr[0];
        }

        date = arr[2] + "-" + arr[1] + "-" + arr[0];

        return date;
    }

    private boolean sendLeaveRequest(HashMap<String, String> requestUser) {
        boolean mailFlag = false;
        String mailAddress = MAIL_ADDRESS_MANAGER;
        String password = MAIL_PW;
        String destination = MAIL_EMPLOYER;
        String smtpHostServer = "smtp-mail.outlook.com";

        String firstName = requestUser.get(KEY_FIRST_NAME);
        String lastName = requestUser.get(KEY_LAST_NAME);
        String department = requestUser.get(KEY_DEPARTMENT);
        String startDate = requestUser.get(KEY_LEAVE_START_DATE);
        String endDate = requestUser.get(KEY_LEAVE_END_DATE);
        StringBuilder body = new StringBuilder();
        body.append("REQUEST EMPLOYEE: " + firstName + " " + lastName + "\n");
        body.append("DEPARTMENT: " + department + "\n");
        body.append("START DATE: " +  startDate + "\n");
        body.append("END DATE: " + endDate + "\n");
        String subject = "[LEAVE REQUEST] FROM " + firstName + " " + lastName;

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAddress, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.addHeader("Content-type", "text/HTML: charset=UTF-8");
            message.addHeader("format", "flowed");
            message.addHeader("Content-Transfer-Encoding", "8bit");
            message.setFrom(new InternetAddress(MAIL_ADDRESS_MANAGER, "WORK FLOW [LEAVE REQUEST]"));
            message.setReplyTo(InternetAddress.parse(MAIL_ADDRESS_MANAGER, false));
            message.addRecipients(MimeMessage.RecipientType.CC, new InternetAddress[]{new InternetAddress(MAIL_ADDRESS_CC, "ANOTHER RECIPIENT")});
            message.setSubject(subject, "UTF-8");
            message.setText(body.toString(), "UTF-8");
            message.setSentDate(new Date());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destination, false));
            Transport.send(message);

            mailFlag = true;
        } catch(Exception ex) {
            mailFlag = false;
            return mailFlag;
        }

        return mailFlag;
    }

    private void createLeaveRequestRecord(HashMap<String, String> user) {
        Map<String, Object> leaveRequest = new HashMap<>();

        if (userId == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            userId = firebaseAuth.getCurrentUser().getUid();
        }

        DatabaseReference dbLeave = FirebaseDatabase.getInstance().getReference(KEY_LEAVE_REQUEST_TABLE).child(userId);
        dbLeave.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LeaveRequestModel leaveRequestModel = dataSnapshot.getValue(LeaveRequestModel.class);
                    if (leaveRequestModel.getUserId().equals(userId)) {
                        leaveRequest.put(KEY_LEAVE_START_DATE, user.get(KEY_LEAVE_START_DATE));
                        leaveRequest.put(KEY_LEAVE_END_DATE, user.get(KEY_LEAVE_END_DATE));
                        leaveRequest.put(KEY_STATUS, 1);
                        dbLeave.child(dataSnapshot.getKey().toString()).updateChildren(leaveRequest);
                        flag = true;
                        break;
                    }
                }

                if (flag == false) {
                    leaveRequest.clear();
                    leaveRequest.put(KEY_USER_ID, userId);
                    leaveRequest.put(KEY_LEAVE_START_DATE, user.get(KEY_LEAVE_START_DATE));
                    leaveRequest.put(KEY_LEAVE_END_DATE, user.get(KEY_LEAVE_END_DATE));
                    leaveRequest.put(KEY_STATUS, 1);
                    dbLeave.push().setValue(leaveRequest);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getRequestStatus() {
        if (userId == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            userId = firebaseAuth.getCurrentUser().getUid();
        }

        DatabaseReference dbLeave = FirebaseDatabase.getInstance().getReference(KEY_LEAVE_REQUEST_TABLE).child(userId);
        dbLeave.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    LeaveRequestModel leaveRequestModel = dataSnapshot.getValue(LeaveRequestModel.class);
                    switch(leaveRequestModel.getStatus()) {
                        case 1:
                            status = "Proceeding";
                            break;
                        case 2:
                            status = "Approved";
                            break;
                        case 3:
                            status = "Denied";
                            break;
                        default:
                            status = "";
                            break;
                    }
                }

                if(status.length() != 0) {
                    txtViewRequestStatus.setVisibility(View.VISIBLE);
                    txtViewRequestStatus.setText("Your request is " + status + ".");
                    if((status.equals("Proceeding")) == false) {
                        btnLeaveRequestConfirm.setVisibility(View.VISIBLE);
                    }

                    btnLeaveRequestConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dbref = FirebaseDatabase.getInstance().getReference(KEY_LEAVE_REQUEST_TABLE).child(userId);
                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        LeaveRequestModel leaveRequest = dataSnapshot.getValue(LeaveRequestModel.class);
                                        if (userId.equals(leaveRequest.getUserId())) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put(KEY_STATUS, 0);
                                            dbref.child(dataSnapshot.getKey().toString()).updateChildren(map);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            txtViewRequestStatus.setVisibility(View.GONE);
                            btnLeaveRequestConfirm.setVisibility(View.GONE);
                        }
                    });
                } else{
                    txtViewRequestStatus.setVisibility(View.GONE);
                    txtViewRequestStatus.setText("");
                    btnLeaveRequestConfirm.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}