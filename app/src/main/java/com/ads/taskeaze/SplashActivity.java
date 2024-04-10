package com.ads.taskeaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.taskeaze.database.AppDatabase;
import com.ads.taskeaze.model.entities.OfflineAttendanceCheckOut;
import com.ads.taskeaze.utils.CommonFunc;
import com.ads.taskeaze.utils.PreferenceUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {
    private Dialog dialog = null;
    AppDatabase db = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "taskeaze.db").build();
                try {
                    showProgressDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!PreferenceUtils.checkUserisLogedin(SplashActivity.this)) {
                                startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
                            } else {

                                if (PreferenceUtils.isCheckedIn(SplashActivity.this)) {
                                    if (!PreferenceUtils.isCheckedinToday(SplashActivity.this)) {
                                        OfflineAttendanceCheckOut checkout = new OfflineAttendanceCheckOut();
                                        checkout.checkInRefId = PreferenceUtils.getCheckedInUserAttenceId(SplashActivity.this);
                                        checkout.latitude = "0.0";
                                        checkout.longitude = "0.0";
                                        checkout.location = "Automatic check-out";
                                        checkout.referenceId = String.valueOf(CommonFunc.getAutoCheckoutTimeInMillis(Long.valueOf(PreferenceUtils.getCheckedInUserAttenceId(SplashActivity.this))));
                                        checkout.checkOutTime = String.valueOf(CommonFunc.getAutoCheckoutTimeInMillis(Long.valueOf(PreferenceUtils.getCheckedInUserAttenceId(SplashActivity.this))));
                                        checkout.date = CommonFunc.convertTimestampToDate(PreferenceUtils.getCheckedInUserAttenceId(SplashActivity.this));
                                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                                        executorService.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                db.offlineAttendanceCheckOutDao().insertCheckOut(checkout);
                                            }
                                        });

                                        if (PreferenceUtils.setUserHasCheckedOut(SplashActivity.this)) {
                                            PreferenceUtils.addCheckOutTimeToSharedPreference(SplashActivity.this, CommonFunc.getAutoCheckoutTimeInMillis(Long.valueOf(PreferenceUtils.getCheckedInUserAttenceId(SplashActivity.this))));
                                        }
                                    }
                                    dismissProgressDialog();
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                }
                                finish();
                            }
                        }
                    }, 3000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }


    public void showProgressDialog()
    {
        try {
            if (dialog == null) {
                dialog = new Dialog(SplashActivity.this);
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.loading_layout);
            }
            if (!dialog.isShowing())
                dialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}

