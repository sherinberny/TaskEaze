package com.ads.taskeaze;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import com.ads.taskeaze.utils.PreferenceUtils;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private Dialog dialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

                try {
                    showProgressDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgressDialog();
                            if(!PreferenceUtils.checkUserisLogedin(SplashActivity.this)) {
                                startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
                            }
                            else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            }
                            finish();
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

