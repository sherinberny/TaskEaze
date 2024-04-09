package com.ads.taskeaze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class LeavesActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_leaves, relativeLayout);
        getSupportActionBar().setTitle(getResources().getString(R.string.leaves_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.add_leaves).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeavesActivity.this, NewLeavesActivity.class);
                intent.setAction("Add Leave");
                startActivity(intent);
            }
        });
    }
}