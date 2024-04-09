package com.ads.taskeaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ads.taskeaze.adapters.AttendanceAdapter;
import com.ads.taskeaze.adapters.MeetingsAdapter;
import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.model.AttendanceModel;
import com.ads.taskeaze.model.entities.OfflineAttendanceCheckIn;
import com.ads.taskeaze.model.entities.OfflineAttendanceCheckOut;
import com.ads.taskeaze.model.entities.OfflineMeetings;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AttendanceActivity extends SupportActivity {
    RecyclerView attendancerecyclerView = null;

    AppDatabase db = null;

    ArrayList<AttendanceModel> attendanceModelList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_attendance, relativeLayout);
        getSupportActionBar().setTitle(getResources().getString(R.string.attendance_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        attendancerecyclerView = findViewById(R.id.attendance_recycleview_id);
        attendancerecyclerView.setLayoutManager(new LinearLayoutManager(AttendanceActivity.this));

        db = Room.databaseBuilder(AttendanceActivity.this,
                AppDatabase.class, "taskeaze.db").build();

        loadDataFromDB();
    }

    private void loadDataFromDB() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<OfflineAttendanceCheckIn> offlineAttendanceCheckIns =
                        db.offlineAttendanceCheckInDao().getAllCheckIns();

                List<OfflineAttendanceCheckOut> offlineAttendanceCheckOuts =
                        db.offlineAttendanceCheckOutDao().getAllCheckOuts();

                attendanceModelList.add(null);

                for (OfflineAttendanceCheckOut checkOut : offlineAttendanceCheckOuts) {
                    // Find the corresponding check-in record based on checkInRefId
                    for (OfflineAttendanceCheckIn checkIn : offlineAttendanceCheckIns) {
                        if (checkOut.getCheckInRefId().equals(checkIn.getReferenceId())) {
                            AttendanceModel attendanceModel = new AttendanceModel();
                            attendanceModel.setAttendanceDate(CommonFunc.convertTimestampToDate(checkIn.getReferenceId()));
                            attendanceModel.setCheckintime(CommonFunc.convertTimestampToTime(checkIn.getReferenceId()));
                            attendanceModel.setCheckinlocation(checkIn.getLocation());
                            attendanceModel.setCheckouttime(CommonFunc.convertTimestampToTime(checkOut.getReferenceId()));
                            attendanceModel.setCheckoutlocation(checkOut.getLocation());
                            String workingHours = calculateWorkingHours(Long.parseLong(checkIn.getReferenceId()), Long.parseLong(checkOut.getReferenceId()));
                            attendanceModel.setWorkingHrs(workingHours);

                            attendanceModelList.add(attendanceModel);
                        }
                    }
                }

                if(attendanceModelList.size()>1){
                    findViewById(R.id.no_attendance_found).setVisibility(View.GONE);
                }
                else {
                    findViewById(R.id.no_attendance_found).setVisibility(View.VISIBLE);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AttendanceAdapter adapter = new AttendanceAdapter(AttendanceActivity.this,attendanceModelList, AttendanceActivity.this,findViewById(R.id.attendance_scrollview_id));
                        attendancerecyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private String calculateWorkingHours(Long checkInTime, Long checkOutTime) {

        long millis = checkOutTime - checkInTime;
        return CommonFunc.getDurationStringattendance(millis);
    }
}