package com.ads.taskeaze.Fragments;

import static com.ads.taskeaze.utils.ConstantUtils.FORMAT_CALENDAR_EVENT;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.ads.taskeaze.NewMeetingsActivity;
import com.ads.taskeaze.R;
import com.ads.taskeaze.adapters.MeetingsAdapter;
import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.PreferenceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeetingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View viewFragment = null;

    RecyclerView meetingrecyclerView = null;

    AppDatabase db = null;
    public MeetingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeetingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetingsFragment newInstance(String param1, String param2) {
        MeetingsFragment fragment = new MeetingsFragment();
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
        viewFragment =  inflater.inflate(R.layout.fragment_meetings, container, false);

        meetingrecyclerView = viewFragment.findViewById(R.id.fragment_meeting_recycleview_id);
        meetingrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "taskeaze.db").build();

        loadDataFromDB();
        ((FloatingActionButton)viewFragment.findViewById(R.id.fragment_meeting_add_meeting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PreferenceUtils.isCheckedIn(getActivity()) && PreferenceUtils.isCheckedinToday(getActivity())) {
                    Intent intent = new Intent(getActivity(), NewMeetingsActivity.class);
                    intent.setAction("New meeting");
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(), "Please checkin to add a meeting", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ((EditText) viewFragment.findViewById(R.id.fragment_meeting_date_id)).setText(CommonFunc.getTodayDate());


        viewFragment.findViewById(R.id.fragment_meeting_date_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendatePickerToGetTheMeetingDate();
            }
        });

        return viewFragment;
    }

    private void loadDataFromDB() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<OfflineMeetings> offlineMeetings =
                        db.offlineMeetingsDAO().getAllMeetingsDate(((EditText)viewFragment.findViewById(R.id.fragment_meeting_date_id)).getText().toString());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MeetingsAdapter adapter = new MeetingsAdapter(offlineMeetings);
                        meetingrecyclerView.setAdapter(adapter);
                    }
                });
            }
        });

    }


    private void opendatePickerToGetTheMeetingDate() {

        SimpleDateFormat mDF = new SimpleDateFormat(FORMAT_CALENDAR_EVENT);
        Date today = new Date();
        mDF.format(today);
        final Calendar c = Calendar.getInstance();
        c.setTime(today);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.MyTimePickerTheme,
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
                            Log.e("month ", " " + m);
                        }

                        ((EditText) viewFragment.findViewById(R.id.fragment_meeting_date_id)).setText(formattedDay + "-" + (formattedMonth) + "-" + year);
                        loadDataFromDB();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {


            }
        });
        datePickerDialog.show();
//        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
    }

}