package com.ads.taskeaze.Fragments;

import static com.ads.taskeaze.utils.ConstantUtils.CHECK_IN_BUTTON_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_NOTHING;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ads.taskeaze.R;
import com.ads.taskeaze.locationClasses.LocationUpdateListener;
import com.ads.taskeaze.locationClasses.MyLocationManager;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.PreferenceUtils;

import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, LocationUpdateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View viewFragment = null;
    private Timer timer = null;
    public LinearLayoutCompat linearLayoutCompat = null;
    public FrameLayout frameLayout = null;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final long MIN_TIME_INTERVAL = 10000; // 10 seconds
    private static final float MIN_DISTANCE_CHANGE = 10.0f; // 10 meters

    private MyLocationManager locationManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        viewFragment = inflater.inflate(R.layout.fragment_home, container, false);
        viewFragment.findViewById(R.id.checkinLayout).setOnClickListener(this);
        viewFragment.findViewById(R.id.checkoutLayout).setOnClickListener(this);

        frameLayout = viewFragment.findViewById(R.id.fragment_home_scroolview);

        viewFragment.findViewById(R.id.checkin_loc).setOnClickListener(this);

        ((TextView)viewFragment.findViewById(R.id.currentTime)).setText(CommonFunc.getTodayTimeForHome());
        ((TextView)viewFragment.findViewById(R.id.currentDate)).setText(CommonFunc.getTodayDateForHome());

        timercheck();

        settingView();

        return viewFragment;
    }

    private void timercheck() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToRepeat = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToRepeat);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (PreferenceUtils.isCheckedinToday(getActivity())) {
                                if(PreferenceUtils.isCheckedIn(getActivity())) {
                                    setTimeAndLocation(PreferenceUtils.getCheckInLocationToSharedPreference(getActivity()),
                                            CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckInTimeStamp(getActivity()) + ""), "--");
                                    // runContiniouslyWorkTime();
                                    ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                                            .setVisibility(View.VISIBLE);
                                    ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                                            .setVisibility(View.GONE);
                                }
                                else{
                                    setTimeAndLocation(PreferenceUtils.getCheckInLocationToSharedPreference(getActivity()),
                                            CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckInTimeStamp(getActivity()) + ""),
                                            CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckOutTimeStamp(getActivity()) + ""));
                                    // runContiniouslyWorkTime();
                                    ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                                            .setVisibility(View.VISIBLE);
                                    ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                                            .setVisibility(View.GONE);
                                }
                            } else {
                                setTimeAndLocation("Check-In for location", "--", "--");
                                if (timer != null)
                                    timer.cancel();
                                ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                                        .setVisibility(View.GONE);
                                ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                                        .setVisibility(View.VISIBLE);
                                ((TextView) viewFragment.findViewById(R.id.work_time))
                                        .setText("00:00:00");
                            }
                        }

                        timercheck();
                    }
                });
            }
        }).start();
    }


    public void settingView() {
        try {
            setTheButtonTextDefaultText(false, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PreferenceUtils.isCheckedinToday(getActivity())&& PreferenceUtils.isCheckedIn(getActivity())) {
            runContiniouslyWorkTime();
        } else {
            setTimeAndLocation("NA", "--", "--");
            if (timer != null)
                timer.cancel();
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onResume() {
        super.onResume();
        settingView();
    }


    void setTimeAndLocation(String address, String checkintime, String checkouttime) {
        ((TextView) viewFragment.findViewById(R.id.checkin_time))
                .setText(checkintime);

        ((TextView) viewFragment.findViewById(R.id.checkout_time))
                .setText(checkouttime);

        ((TextView) viewFragment.findViewById(R.id.checkin_loc))
                .setText(getResources().getString(R.string.location_txt) + " " +address);
    }

    public void setTheButtonTextDefaultText(boolean showSnakebar, String message) throws Exception {
        if (!PreferenceUtils.isCheckedinToday(getActivity())) {
            setTimeAndLocation("Check-In for location", "--", "--");
            if (timer != null)
                timer.cancel();
            ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                    .setVisibility(View.GONE);
            ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                    .setVisibility(View.VISIBLE);
            ((TextView) viewFragment.findViewById(R.id.work_time))
                    .setText("00:00:00");
            runContiniouslyWorkTime();

            if (showSnakebar) {
                CommonFunc.commonDialog(getActivity(),
                        getString(R.string.alert),
                        message, false,
                        (AppCompatActivity) getActivity(),
                        viewFragment.findViewById(R.id.fragment_home_scroolview));
            }
        } else {
            if(PreferenceUtils.isCheckedIn(getActivity())) {
                setTimeAndLocation(PreferenceUtils.getCheckInLocationToSharedPreference(getActivity()),
                        CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckInTimeStamp(getActivity()) + ""), "--");
                 runContiniouslyWorkTime();
                ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                        .setVisibility(View.VISIBLE);
                ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                        .setVisibility(View.GONE);
            }
            else{
                setTimeAndLocation(PreferenceUtils.getCheckInLocationToSharedPreference(getActivity()),
                        CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckInTimeStamp(getActivity()) + ""),
                        CommonFunc.convertTimestampToTime(PreferenceUtils.getTodayCheckOutTimeStamp(getActivity()) + ""));
                // runContiniouslyWorkTime();
                ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                        .setVisibility(View.VISIBLE);
                ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout))
                        .setVisibility(View.GONE);
            }

            if (showSnakebar) {
                CommonFunc.commonDialog(getActivity(),
                        getString(R.string.alert),
                        message, false,
                        (AppCompatActivity) getActivity(),
                        viewFragment.findViewById(R.id.fragment_home_scroolview));
            }
        }

    }

    void runContiniouslyWorkTime() {
        long millis = CommonFunc.getCurrentSystemTimeStamp() - Long.parseLong(PreferenceUtils.getCheckedInUserAttenceId(getActivity()));
        CommonFunc.getDurationString(millis);
        if (isAdded()) {
            if (PreferenceUtils.isCheckedinToday(getActivity()) && PreferenceUtils.isCheckedIn(getActivity())) {
                ((TextView) viewFragment.findViewById(R.id.work_time))
                        .setText(CommonFunc.getDurationString(millis));
            } else {
                if (timer != null)
                    timer.cancel();
                ((TextView) viewFragment.findViewById(R.id.work_time))
                        .setText("00:00:00");
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.checkinLayout || view.getId() == R.id.checkoutLayout )
            checkIsCheckingInRunning();

        else if (view.getId() == R.id.checkin_loc) {
            if (PreferenceUtils.isCheckedinToday(getActivity()) && PreferenceUtils.isCheckedIn(getActivity()))
                Toast.makeText(getActivity(), "Check-in Address:: "
                        + ((TextView) viewFragment.findViewById(R.id.checkin_loc)).getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void checkIsCheckingInRunning() {

            alertCheckInOuTDialog();

    }

    private void alertCheckInOuTDialog() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),android.R.style.Theme_Dialog));
        if (((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                .getVisibility() == View.VISIBLE) {
            builder1.setMessage("Do you want to check-in ?");
        }
        else{
                builder1.setMessage("Do you want to check-out ?");
        }
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            startLocationUpdates();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        }
                        /*if (PermissionUtils.checkLoactionPermission(getActivity())) {
                            connectToGoogleApiClientAndGetTheAddress(true);
                            // showProgressDialog();
                        } else {
                            PermissionUtils.openPermissionDialog(getActivity(), "Please grant location permission");
                        }*/

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertCheckInAlertDialog = builder1.create();
        alertCheckInAlertDialog.show();
    }


    private Dialog dialogProgress = null;

    ////
    public void showProgressDialog() {
        if (dialogProgress == null) {
            dialogProgress = new Dialog(getActivity());
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
        // Initialize MyLocationManager and pass this fragment as the listener
        if (isAdded()) { // Check if the fragment is added to an activity
            Context context = requireContext();
            if (context != null) {
                locationManager = new MyLocationManager(context, this);
                locationManager.startLocationUpdates();
            } else {
                Log.e("YourFragment", "Fragment's context is null");
                dismissProgressDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start receiving location updates
            startLocationUpdates();
        } else {
            Toast.makeText(getActivity(), "Please give the permission to access location", Toast.LENGTH_SHORT).show();
        }
    }

    // Implement LocationUpdateListener interface method
    @Override
    public void onLocationUpdate(Location location) {
        // Handle location updates

        locationManager.stopLocationUpdates();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String address = CommonFunc.getAddressFromCoordinates(getActivity(), latitude, longitude);
        String timeRef = String.valueOf(CommonFunc.getCurrentSystemTimeStamp());


        if (((LinearLayout) viewFragment.findViewById(R.id.checkinLayout))
                .getVisibility() == View.VISIBLE) {
          /*  if ((new OfflineDatabase(getActivity()).addCheckInDetails(timeRef, String.valueOf(latitude), String.valueOf(longitude),
                    address, timeRef, CommonFunc.getTodayDate())) == -1) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), "Something wrong happened", Toast.LENGTH_SHORT).show();
            } else {*/
                if (PreferenceUtils.setUserHasCheckedIn(getActivity(), timeRef)) {
                    if (PreferenceUtils.addCheckInTimeToSharedPreference(getActivity(), Long.parseLong(timeRef))) {
                        PreferenceUtils.setCheckInLocationToSharedPreference(getActivity(), address);
                        PreferenceUtils.addLastSubmitedAddress(String.valueOf(latitude), String.valueOf(longitude), address, getActivity());
                        setTimeAndLocation(address, CommonFunc.convertTimestampToTime(timeRef), "--");
                        runContiniouslyWorkTime();
                        ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout)).setVisibility(View.GONE);
                        ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout)).setVisibility(View.VISIBLE);
                        /*CommonFunc.showNotification("Check-in", "You have successfully checked-in", NOTIFICATION_TYPE_NOTHING,
                                getActivity(), false, false);*/
                        dismissProgressDialog();

                    }
                }
         //   }
        }
        else{
           /* if ((new OfflineDatabase(getActivity()).addCheckOutDetails(timeRef, timeRef, String.valueOf(latitude), String.valueOf(longitude),
                    address, CommonFunc.getTodayDate(), PreferenceUtils.getCheckedInUserAttenceId(getActivity()))) == -1) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), "Something wrong happened", Toast.LENGTH_SHORT).show();
            } else {*/
            if (PreferenceUtils.setUserHasCheckedOut(getActivity())) {
                if (PreferenceUtils.addCheckOutTimeToSharedPreference(getActivity(), Long.parseLong(timeRef))) {
                    PreferenceUtils.setCheckInLocationToSharedPreference(getActivity(), address);
                    // setTimeAndLocation("NA", "--", "--");

                    ((LinearLayout) viewFragment.findViewById(R.id.checkinLayout)).setVisibility(View.VISIBLE);
                    ((LinearLayout) viewFragment.findViewById(R.id.checkoutLayout)).setVisibility(View.GONE);
                    if (timer != null) {
                        timer.cancel();
                        ((TextView) viewFragment.findViewById(R.id.work_time))
                                .setText(" 00:00:00");
                    }
                }

                    /*CommonFunc.showNotification("Check-out", "You have successfully checked-out", NOTIFICATION_TYPE_NOTHING,
                            getActivity(), false, false);*/

                    dismissProgressDialog();
                }
            }
       // }
    }


    @Override
    public void onStop() {
        super.onStop();
        dismissProgressDialog();
        if (timer != null)
            timer.cancel();
    }
}