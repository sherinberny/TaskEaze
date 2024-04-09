package com.ads.taskeaze;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.locationClasses.LocationUpdateListener;
import com.ads.taskeaze.locationClasses.MyLocationManager;
import com.ads.taskeaze.model.LastSubmitedModel;
import com.ads.taskeaze.model.TravelImageModel;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.ImageUtility;
import com.ads.taskeaze.utils.PermissionUtils;
import com.ads.taskeaze.utils.PreferenceUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
    private Uri uriCachedBuffer = null;

    private boolean isMockLocEnabled = false;

    private LinkedList<TravelImageModel> meetingImageModels = new LinkedList<>();
    private NewMeetingImageAdapter travelImageAdapter = null;

    private final int SELECT_FILE = 20;
    private final int SELECT_MOBILE_CONTACT = 22;
    public static final int IMAGE_CAPTURE_CODE = 654;
    Uri image_uri;

    String mot = "";
    Location locationOfUserCurrentLoc = null;

    AppDatabase db = null;
    

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

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "taskeaze.db").build();

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

        setTheInitialPlaceholderImage();

        findViewById(R.id.new_meeting_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               saveMeeting();
            }
        });

        ((Spinner)findViewById(R.id.new_meeting_travel_by_spinner_id)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mot = ((Spinner)findViewById(R.id.new_meeting_travel_by_spinner_id)).getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
    }

    private void saveMeeting() {
        String tempStatus = "";
        String tempAttachment = "";
        String tempInTime = "";
        String tempOutTime = "";
        String tempFullName = "";
        String tempPhone = "";
        String tempLoc = "";
        String tempRemark = "";
        String travelleddist = "";
        String tempMot = "";
        String tempPurpose = "";
        String tempFOllowUpDate = "";
        String tempFollowupTime = "";
        LatLng latLngCurrent = new LatLng(locationOfUserCurrentLoc.getLatitude(),
                locationOfUserCurrentLoc.getLongitude());
        LastSubmitedModel lastSubmitedModel = PreferenceUtils.getThelastSubmitedAddress(NewMeetingsActivity.this);
        tempStatus = "Completed";
        tempAttachment = "";
        if (meetingImageModels.size() > 1) {
            ArrayList<TravelImageModel> models = new ArrayList<>();
            for (int i = 0; i < meetingImageModels.size(); i++) {
                if (!meetingImageModels.get(i).isAddImageButton()) {
                    models.add(meetingImageModels.get(i));
                }
            }
            for (int i = 0; i < models.size(); i++) {
                if (models.size() == 1) {
                    tempAttachment = models.get(i).get_b64String();
                } else {
                    if (!tempAttachment.isEmpty())
                        tempAttachment = tempAttachment + "," + models.get(i).get_b64String();
                    else
                        tempAttachment = models.get(i).get_b64String();
                }
            }
        }
          /*  tempOnDate = CommonFunc.getTodayDate();
            tempOnTime = ((EditText) findViewById(R.id.fragment_direct_vist_time_edittext_id)).getText().toString();*/
        tempFullName = ((EditText) findViewById(R.id.new_meeting_customer_name_edittext_id)).getText().toString();
        tempPhone = ((EditText) findViewById(R.id.new_meeting_mobile_no_edittext_id)).getText().toString();
        tempLoc = ((EditText) findViewById(R.id.new_meeting_location_edittext_id)).getText().toString();
        tempRemark = ((EditText) findViewById(R.id.new_meeting_notes_edittext_id)).getText().toString();
        tempInTime = ((EditText) findViewById(R.id.fragment_add_in_time_id)).getText().toString();
        tempOutTime = ((EditText) findViewById(R.id.fragment_add_out_time_id)).getText().toString();
        tempMot = String.valueOf(((Spinner) findViewById(R.id.new_meeting_travel_by_spinner_id)).getSelectedItem());
        travelleddist = ((EditText) findViewById(R.id.new_meeting_distance_travelled_id)).getText().toString().replace("Km", "").replace(" ", "");
        tempPurpose = String.valueOf(((Spinner) findViewById(R.id.fragment_directvisit_meeting_purpose_id)).getSelectedItem());
        if (((CheckBox) findViewById(R.id.fragment_direct_followup_checkbox)).isChecked()) {
            tempFOllowUpDate = ((EditText) findViewById(R.id.new_meeting_followup_date_edittext_id)).
                    getText().toString();
            tempFollowupTime = ((EditText) findViewById(R.id.new_meeting_followup_time_edittext_id)).
                    getText().toString();
        }
        if (LastLat.equals(null) || LastLat.equals("")) {
            assert lastSubmitedModel != null;
            LastLoc = lastSubmitedModel.getLastSubmitedAddress();
            LastLat = lastSubmitedModel.getLastSubmitedAddressLat();
            LastLng = lastSubmitedModel.getLastSubmitedAddressLng();
        }

        if (tempFullName.equals("") || tempPhone.equals("") || tempInTime.equals("") || tempOutTime.equals("") ||
                (((CheckBox) findViewById(R.id.fragment_direct_followup_checkbox)).isChecked() && (tempFollowupTime.equals("") || tempFOllowUpDate.equals("")))
                ||(((Spinner) findViewById(R.id.new_meeting_travel_by_spinner_id)).getSelectedItem().toString()).contains("Select")
                ||(((Spinner) findViewById(R.id.new_meeting_travel_by_spinner_id)).getSelectedItem().toString()).contains("Select")
        ) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewMeetingsActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            OfflineMeetings offlineMeetings = new OfflineMeetings();
            offlineMeetings.setAttachment(tempAttachment);
            offlineMeetings.setEndLatitude(String.valueOf(latLngCurrent.latitude));
            offlineMeetings.setEndLocation(tempLoc);
            offlineMeetings.setEndLongitude(String.valueOf(latLngCurrent.longitude));
            if (tempFOllowUpDate != null && tempFollowupTime != null) {
                offlineMeetings.setFollowupDateRefNo(CommonFunc.getDateToTimeStamp(tempFOllowUpDate + " " + tempFollowupTime, NewMeetingsActivity.this));
                offlineMeetings.setFollowupmobiledate(tempFOllowUpDate.replace("/", "-"));
                offlineMeetings.setFollowUpTime(tempFollowupTime);
            }
            offlineMeetings.setInTime(tempInTime);
            offlineMeetings.setOutTime(tempOutTime);
            offlineMeetings.setStatus(tempStatus);
            offlineMeetings.setFullName(tempFullName);
            offlineMeetings.setPhone(tempPhone.replace(" ", ""));
            offlineMeetings.setRemarks(tempRemark);
            offlineMeetings.setIsMockLocation((isMockLocEnabled ? "Yes" : "No"));
            offlineMeetings.setModeOfTravel(tempMot);
            offlineMeetings.setRootDistanceTravelled(travelleddist);
            offlineMeetings.setStartLocation(LastLoc);
            offlineMeetings.setStartLat(LastLat);
            offlineMeetings.setStartLng(LastLng);
            offlineMeetings.setModeOfTravel(tempMot);
            offlineMeetings.setPurpose(tempPurpose);
            offlineMeetings.setMeetingDateRefNo(CommonFunc.getCurrentSystemTimeStamp() + "");
            offlineMeetings.setOnDate(CommonFunc.getTodayDate());

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    db.offlineMeetingsDAO().insertMeetings(offlineMeetings);
                }
            });
            ResetAllViews();

        }
    }

    private void ResetAllViews() {
        ((EditText)findViewById(R.id.new_meeting_customer_name_edittext_id)).setText("");
        ((EditText)findViewById(R.id.new_meeting_mobile_no_edittext_id)).setText("");
        ((EditText)findViewById(R.id.fragment_add_in_time_id)).setText("");
        ((EditText)findViewById(R.id.fragment_add_out_time_id)).setText("");
        ((EditText)findViewById(R.id.new_meeting_notes_edittext_id)).setText("");
        ((EditText)findViewById(R.id.new_meeting_followup_time_edittext_id)).setText("");
        ((EditText)findViewById(R.id.new_meeting_followup_date_edittext_id)).setText("");
        ((EditText)findViewById(R.id.new_meeting_distance_travelled_id)).setText("0.0 Km");
        ((CheckBox)findViewById(R.id.fragment_direct_followup_checkbox)).setChecked(false);
        ((Spinner)findViewById(R.id.new_meeting_travel_by_spinner_id)).setSelection(0);
        ((Spinner)findViewById(R.id.fragment_directvisit_meeting_purpose_id)).setSelection(0);
        findViewById(R.id.followup_layout).setVisibility(View.GONE);
        setTheInitialPlaceholderImage();
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
        isMockLocEnabled = location.isFromMockProvider();
        locationManager.stopLocationUpdates();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String address = CommonFunc.getAddressFromCoordinates(this, latitude, longitude);
        String timeRef = String.valueOf(CommonFunc.getCurrentSystemTimeStamp());

        ((EditText)findViewById(R.id.new_meeting_location_edittext_id)).setText(address);
        
        locationOfUserCurrentLoc = location;
        GetlastLocationDistance();

    }

    private void GetlastLocationDistance() {
       
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



                        ((EditText)findViewById(viewId)).setText(formattedDay + "-" + (formattedMonth) + "-" + year);
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

    private void onSelectFromGalleryResult(Intent data) {
        loadImageFromUri(data.getData());
    }

    private void onCaptureImageResult(Intent data) {
        loadImageFromUri(uriCachedBuffer);
    }

    private void loadImageFromUri(Uri uri) {

        Glide.with(NewMeetingsActivity.this).asBitmap().load(uri)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource != null) {
                            Bitmap bitmap = ImageUtility.getResizedBitmap(resource, 400);
                            addattachmentImageToLinkedList(bitmap);
                        }
                    }
                });
    }

    ///////////////////////////////////////// image adapter
    private class NewMeetingImageAdapter extends RecyclerView.Adapter<NewMeetingImageAdapter.TravelImageHolder> {
        @Override
        public NewMeetingImageAdapter.TravelImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NewMeetingsActivity.this).inflate(R.layout.fragment_travel_image_row, parent, false);
            return new NewMeetingImageAdapter.TravelImageHolder(view);
        }

        @Override
        public void onBindViewHolder(final NewMeetingImageAdapter.TravelImageHolder holder, int position) {
            if (meetingImageModels.get(holder.getAdapterPosition()).isAddImageButton()) {
                holder.imageViewDelete.setVisibility(View.GONE);
                holder.imageViewTravelImageView.setImageDrawable(ContextCompat.getDrawable(NewMeetingsActivity.this, R.drawable.ic_add_image));
            } else {
                holder.imageViewDelete.setVisibility(View.VISIBLE);
                holder.imageViewTravelImageView.setImageBitmap(meetingImageModels.get(holder.getAdapterPosition()).getBitmap());


            }
            holder.imageViewTravelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (meetingImageModels.get(holder.getAdapterPosition()).isAddImageButton()) {
                        if (meetingImageModels.size() < 4)
                            selectUploadImage();
                        else
                            Toast.makeText(NewMeetingsActivity.this, "Maximum image upload limit is 3.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    meetingImageModels.remove(holder.getAdapterPosition());
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            });

        }

        @Override
        public int getItemCount() {
            return meetingImageModels.size();
        }

        class TravelImageHolder extends RecyclerView.ViewHolder {
            AppCompatImageView imageViewTravelImageView = null, imageViewDelete = null;
            AppCompatTextView textViewImageCaption = null;
            AppCompatTextView textViewAmount = null;

            TravelImageHolder(View itemView) {
                super(itemView);
                this.imageViewTravelImageView = itemView.findViewById(R.id.fragment_travel_image_row_imageview_id);
                this.imageViewDelete = itemView.findViewById(R.id.fragment_travel_image_row_delete_imageview);

            }
        }
    }

    /////// image uplode stuff
    private void setTheInitialPlaceholderImage() {
        meetingImageModels.clear();
        meetingImageModels.add(new TravelImageModel(null, null, true));
        travelImageAdapter = new NewMeetingImageAdapter();
        ((RecyclerView) findViewById(R.id.new_meeting_add_image_recycleview)).setLayoutManager(new
                LinearLayoutManager(NewMeetingsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        ((RecyclerView) findViewById(R.id.new_meeting_add_image_recycleview)).setAdapter(travelImageAdapter);
    }

    private void addattachmentImageToLinkedList(Bitmap bitmap) {

        String b64 = ImageUtility.convertImageToBase64(bitmap, NewMeetingsActivity.this);
        meetingImageModels.addFirst(new TravelImageModel(b64, bitmap, false));
        travelImageAdapter.notifyDataSetChanged();
        dismissProgressDialog();
    }
    private void selectUploadImage() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(NewMeetingsActivity.this,android.R.style.Theme_Dialog));
        builder.setTitle("Upload Pictures Option");
        builder.setMessage("How do you want to set your picture?");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (PermissionUtils.checkStoragePermission(NewMeetingsActivity.this)) {
                            openGalary();
                        } else {
                            PermissionUtils.openPermissionDialog(NewMeetingsActivity.this, "Please grant Storage Permission");
                        }
                    }
                }
        );
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (PermissionUtils.checkCameraPermissionAndStoragePermission(NewMeetingsActivity.this)) {
                            opencamera();
                        } else {
                            PermissionUtils.openPermissionDialog(NewMeetingsActivity.this, "Please grant Camera and Storage Permission");
                        }
                    }
                }
        );
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void opencamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    private void openContacts(int select) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, select);
    }

    private void openGalary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE) {
            if (resultCode == RESULT_OK) {
                showProgressDialog();
                onSelectFromGalleryResult(data);
            }
        }  else if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            //imageView.setImageURI(image_uri);
            Bitmap bitmap = uriToBitmap(image_uri);
            bitmap = ImageUtility.getResizedBitmap(bitmap, 400);
            addattachmentImageToLinkedList(bitmap);
            //  imageView.setImageBitmap(bitmap);
        }
    }
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}