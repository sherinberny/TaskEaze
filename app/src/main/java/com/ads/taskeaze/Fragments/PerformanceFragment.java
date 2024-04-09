package com.ads.taskeaze.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ads.taskeaze.R;
import com.ads.taskeaze.adapters.MeetingsAdapter;
import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerformanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerformanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View viewFragment = null;

    AppDatabase db = null;

    GraphView graphView = null;
    List<OfflineMeetings> offlineMeetings;


    public PerformanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerformanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerformanceFragment newInstance(String param1, String param2) {
        PerformanceFragment fragment = new PerformanceFragment();
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
        viewFragment =  inflater.inflate(R.layout.fragment_performance, container, false);

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "taskeaze.db").build();

        graphView = viewFragment.findViewById(R.id.graph);



        getWeeklyMeetingData();

        return viewFragment;
    }

    private void getWeeklyMeetingData() {
        // Simulated data for demonstration
        /*Map<Integer, Integer> weeklyData = new HashMap<>();
        weeklyData.put(5, 2); // Week 5: 2 meetings
        weeklyData.put(10, 4); // Week 10: 4 meetings
        weeklyData.put(15, 1); // Week 15: 1 meeting
        // Add more data as needed
        return weeklyData;*/

        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // Initialize a map to store weekly meeting counts
        Map<Integer, Integer> weeklyData = new HashMap<>();

        // Retrieve all meetings from the database
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        offlineMeetings =
                                                db.offlineMeetingsDAO().getAllMeetings();

                                        for (OfflineMeetings meeting : offlineMeetings) {
                                            // Extract the week number of the meeting date
                                            Calendar meetingCalendar = Calendar.getInstance();
                                            meetingCalendar.setTimeInMillis(Long.valueOf(meeting.getMeetingDateRefNo()));
                                            int meetingWeek = meetingCalendar.get(Calendar.WEEK_OF_YEAR);

                                            // Increment the meeting count for the corresponding week
                                            weeklyData.put(meetingWeek, weeklyData.getOrDefault(meetingWeek, 0) + 1);
                                        }

                                        // Add 0 counts for weeks without meetings
                                        for (int week = currentWeek - 4; week <= currentWeek; week++) {
                                            if (!weeklyData.containsKey(week)) {
                                                weeklyData.put(week, 0);
                                            }
                                        }

                                        Map<Integer, Integer> weeklyMeetingData = weeklyData;
                                        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

                                        // Populate the series with data points
                                        for (Map.Entry<Integer, Integer> entry : weeklyMeetingData.entrySet()) {
                                            series.appendData(new DataPoint(entry.getKey(), entry.getValue()), true, 7);
                                        }


                                        series.setColor(Color.BLUE);
                                        series.setThickness(8);

                                        // Add the series to the graph
                                        graphView.addSeries(series);

                                        // Customize the appearance of the graph
                                        graphView.getViewport().setXAxisBoundsManual(true);
                                        graphView.getViewport().setMinX(1);
                                        graphView.getViewport().setMaxX(52); // Assuming 52 weeks in a year
                                        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                            @Override
                                            public String formatLabel(double value, boolean isValueX) {
                                                if (isValueX) {
                                                    // Convert week number to month name
                                                    int week = (int) value;
                                                    Calendar cal = Calendar.getInstance();
                                                    cal.set(Calendar.WEEK_OF_YEAR, week);
                                                    return cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, getResources().getConfiguration().locale);
                                                } else {
                                                    // Show integer values for Y-axis
                                                    return super.formatLabel(value, isValueX);
                                                }
                                            }
                                        });
                                    }
                                });




    }
}