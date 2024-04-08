package com.ads.taskeaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import com.ads.taskeaze.locationClasses.LocationUpdateListener;
import com.ads.taskeaze.locationClasses.MyLocationManager;
import com.ads.taskeaze.model.LastSubmitedModel;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.NetworkUtils;
import com.ads.taskeaze.utils.PreferenceUtils;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class NewMeetingsActivity extends SupportActivity implements LocationUpdateListener {

    private View viewFragment = null;
    private Timer timer = null;
    public LinearLayoutCompat linearLayoutCompat = null;
    public FrameLayout frameLayout = null;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private final int REQ_CODE_TO_GET_THE_DISTANCE_DATA = 2;


    private MyLocationManager locationManager;

    private String LastLat = null;
    private String LastLng = null;
    private String LastLoc = null;
    String DistToLastLocation = "";

    private long intimemillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_new_meetings, relativeLayout);
        getSupportActionBar().setTitle(getIntent().getAction());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        startLocationUpdates();
        findViewById(R.id.new_meeting_location_edittext_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewMeetingsActivity.this, "Location is fetched automatically", Toast.LENGTH_SHORT).show();
            }
        });


        findViewById(R.id.new_meeting_distance_travelled_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewMeetingsActivity.this, "Distance is calculated automatically", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.fragment_add_out_time_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInTime(v.getId());
            }
        });

        findViewById(R.id.fragment_add_in_time_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInTime(v.getId());
            }
        });

        findViewById(R.id.fragment_add_out_time_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOutTime(v.getId());
            }
        });



        findViewById(R.id.fragment_direct_followup_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)findViewById(R.id.fragment_direct_followup_checkbox)).isChecked()){
                    findViewById(R.id.followup_layout).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.followup_layout).setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.new_meeting_followup_time_edittext_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime(v.getId());
            }
        });

        findViewById(R.id.new_meeting_followup_date_edittext_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v.getId());
            }
        });
        
    }


    private Dialog dialogProgress = null;

    ////
    public void showProgressDialog() {
        if (dialogProgress == null) {
            dialogProgress = new Dialog(NewMeetingsActivity.this);
            dialogProgress.setCancelable(false);
            dialogProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogProgress.setContentView(R.layout.loading_layout);
            dialogProgress.show();
        } else {
            if (!dialogProgress.isShowing())
                dialogProgress.show();
        }
    }

    public void dismissProgressDialog() {
        if (dialogProgress != null) {
            if (dialogProgress.isShowing()) {
                dialogProgress.dismiss();
            }
            dialogProgress = null;
        }
    }


    private void startLocationUpdates() {
        showProgressDialog();

                locationManager = new MyLocationManager(NewMeetingsActivity.this, this);
                locationManager.startLocationUpdates();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start receiving location updates
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Please give the permission to access location", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement LocationUpdateListener interface method
    @Override
    public void onLocationUpdate(Location location) {
        // Handle location updates
        dismissProgressDialog();

        locationManager.stopLocationUpdates();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String address = CommonFunc.getAddressFromCoordinates(this, latitude, longitude);
        String timeRef = String.valueOf(CommonFunc.getCurrentSystemTimeStamp());

        ((EditText)findViewById(R.id.new_meeting_location_edittext_id)).setText(address);


        GetlastLocationDistance(location);

    }

    private void GetlastLocationDistance(Location locationOfUserCurrentLoc) {

        LastSubmitedModel lastSubmitedModel = PreferenceUtils.getThelastSubmitedAddress(this);
        assert lastSubmitedModel != null;
        LastLoc = lastSubmitedModel.getLastSubmitedAddress();
        LastLat = lastSubmitedModel.getLastSubmitedAddressLat();
        LastLng = lastSubmitedModel.getLastSubmitedAddressLng();

        Location LastLocation = new Location("");
            LastLocation.setLatitude(Double.parseDouble(LastLat));
            LastLocation.setLongitude(Double.parseDouble(LastLng));
        DistToLastLocation = CommonFunc.getOfflineDistance(locationOfUserCurrentLoc, LastLocation);

        ((EditText)findViewById(R.id.new_meeting_distance_travelled_id)).setText(DistToLastLocation);


    }

    private void selectInTime(final int viewId) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, R.style.MyTimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String s = selectedHour + ":" + selectedMinute;
                DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
                Date d = null;
                try {
                    d = f1.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat f2 = new SimpleDateFormat("hh:mm a");

                f2.format(d).toLowerCase();
                ///////////////////////////////
                Calendar datetime = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                datetime.set(Calendar.MINUTE, selectedMinute);
                // if (datetime.getTimeInMillis() >= (c.getTimeInMillis() + 3600000)) {
                ((EditText)findViewById(viewId)).setText(f2.format(d).toUpperCase());
                ((EditText) findViewById(R.id.fragment_add_out_time_id)).setText("");
                ((EditText)findViewById(viewId)).setError(null);
                intimemillis = datetime.getTimeInMillis();
                        /* }
                    else {
                        CommonFunc.commonDialog(this, getString(R.string.alert),
                                "Meeting should be assigned before 1 hour",
                                false,
                                (AppCompatActivity) this,findViewById(R.id.new_meeting_constraintlayout));
                    }*/


                ///////////////////////////////
                ((EditText)findViewById(viewId)).clearFocus();
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void selectOutTime(final int viewId) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, R.style.MyTimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String s = selectedHour + ":" + selectedMinute;
                DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
                Date d = null;
                try {
                    d = f1.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat f2 = new SimpleDateFormat("hh:mm a");

                f2.format(d).toLowerCase();
                ///////////////////////////////
                Calendar datetime = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                datetime.set(Calendar.MINUTE, selectedMinute);
                if (datetime.getTimeInMillis() >= intimemillis) {
                    if (datetime.getTimeInMillis() <= CommonFunc.getCurrentSystemTimeStamp()) {
                        ((EditText)findViewById(viewId)).setText(f2.format(d).toUpperCase());
                        ((EditText)findViewById(viewId)).setError(null);

                    } else {
                        ((EditText)findViewById(viewId)).setText("");
                        Toast.makeText(NewMeetingsActivity.this, "Outtime can't be a future time", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    ((EditText)findViewById(viewId)).setText("");
                    Toast.makeText(NewMeetingsActivity.this, "Outtime should be assigned after intime", Toast.LENGTH_SHORT).show();
                }
                ///////////////////////////////
                ((EditText)findViewById(viewId)).clearFocus();
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    private void selectTime(final int viewId) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(NewMeetingsActivity.this,R.style.MyTimePickerTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String s = selectedHour + ":" + selectedMinute;
                DateFormat f1 = new SimpleDateFormat("HH:mm"); //HH for hour of the day (0 - 23)
                Date d = null;
                try {
                    d = f1.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat f2 = new SimpleDateFormat("hh:mm a");

                f2.format(d).toLowerCase();
                ///////////////////////////////
                Calendar datetime = Calendar.getInstance();
                Calendar c = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                datetime.set(Calendar.MINUTE, selectedMinute);

                if (CommonFunc.getTodayDate().equals(((EditText) findViewById(R.id.new_meeting_followup_date_edittext_id)).getText().toString())) {
                    if (datetime.getTimeInMillis() >= (c.getTimeInMillis() + 3600000)) {
                        ((EditText) findViewById(viewId))
                                .setText(f2.format(d).toUpperCase());
                    } else {
                        Toast.makeText(NewMeetingsActivity.this, "Meeting should be assigned before 1 hour", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ((EditText) findViewById(viewId))
                            .setText(f2.format(d).toUpperCase());
                }

                ///////////////////////////////
                ((EditText) findViewById(viewId)).clearFocus();


            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void selectDate(final int viewId) {

        SimpleDateFormat mDF = new SimpleDateFormat("yyyy-mm-dd");
        Date today = new Date();
        mDF.format(today);
        final Calendar c = Calendar.getInstance();
        c.setTime(today);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.MyTimePickerTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String formattedDay = (String.valueOf(dayOfMonth));
                        int m = monthOfYear + 1;


                        String formattedMonth = (String.valueOf(m));

                        if (dayOfMonth < 10) {
                            formattedDay = "0" + dayOfMonth;
                        }

                        if (m < 10) {
                            formattedMonth = "0" + m;

                        }



                        ((EditText)findViewById(viewId)).setText(formattedDay + "/" + (formattedMonth) + "/" + year);
                        ((EditText)findViewById(viewId)).clearFocus();


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {


            }
        });
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
    }
}