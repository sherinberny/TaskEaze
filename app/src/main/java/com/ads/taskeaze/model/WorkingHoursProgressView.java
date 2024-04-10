package com.ads.taskeaze.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WorkingHoursProgressView extends View {

    private static final int TOTAL_WORKING_HOURS = 8; // Total working hours
    private static final int MINUTES_IN_HOUR = 60;

    private Paint bluePaint;
    private Paint orangePaint;

    private int minutesWorked;

    public WorkingHoursProgressView(Context context) {
        super(context);
        init();
    }

    public WorkingHoursProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WorkingHoursProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);
        bluePaint.setAntiAlias(true);

        orangePaint = new Paint();
        orangePaint.setColor(Color.rgb(255, 165, 0)); // Orange color
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setAntiAlias(true);
    }

    public void setMinutesWorked(int minutesWorked) {
        this.minutesWorked = minutesWorked;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the fraction of progress
        float progressFraction = (float) minutesWorked / (TOTAL_WORKING_HOURS * MINUTES_IN_HOUR);

        // Calculate the angle based on the progress fraction
        float angle = progressFraction * 360f;

        // Draw blue circle
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, bluePaint);

        // If worked hours exceed 8 hours, fill the entire circle with orange color
        if (minutesWorked >= TOTAL_WORKING_HOURS * MINUTES_IN_HOUR) {
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, orangePaint);
        } else {
            // Draw blue progress up to the calculated angle
            canvas.drawArc(0, 0, getWidth(), getHeight(), -90, angle, true, bluePaint);
        }
    }





}
