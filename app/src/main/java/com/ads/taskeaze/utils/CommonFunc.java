package com.ads.taskeaze.utils;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import static com.ads.taskeaze.utils.ConstantUtils.FORMAT_CALENDAR_EVENT;
import static com.ads.taskeaze.utils.ConstantUtils.FORMAT_CALENDAR_HOME;
import static com.ads.taskeaze.utils.ConstantUtils.FORMAT_TIME_HOME;
import static com.ads.taskeaze.utils.ConstantUtils.IMAGE_STORAGE_DIRECTORY_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_CHANNEL_ID_OF_COMM_NOTIFICATION;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_CHANNEL_NAME_OF_COMM_NOTIFICATION;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_GRANT_PERMISSION;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_GRANT_PERMISSION_ID;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_NOTHING;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_NOTHING_ID;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_ON_GPS;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_ON_GPS_ID;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_RELOG_USER;
import static com.ads.taskeaze.utils.ConstantUtils.NOTIFICATION_TYPE_RELOG_USER_ID;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.window.SplashScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.ads.taskeaze.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;


public class CommonFunc {


    public static void commonDialog(final Context context, String title, String subjuct, final boolean isInternetError,
                                    AppCompatActivity appCompatActivity, final View viewDisplay) {
        if (isInternetError) {
            if (NetworkUtils.getConnectionType(context).equals("NA")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Enable your mobile data or wifi", "Settings");
            } else if (NetworkUtils.getConnectionType(context).equals("W")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Your wifi connection is limited. Try alternatives", "Settings");

            } else if (NetworkUtils.getConnectionType(context).equals("M")) {
                showNoInternetSnackBar(appCompatActivity, context, viewDisplay, "Your mobile data connection is limited. Try alternatives", "Settings");

            }

        } else {
            try {
                Snackbar snackbar1 = Snackbar.make(viewDisplay, subjuct, Snackbar.LENGTH_LONG);
                snackbar1.setDuration(7000);
                snackbar1.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    public static String getDateToTimeStamp(String str_date, Context context) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        Date date = null;
        try {
            date = formatter.parse(str_date);
            return date.getTime() + "";
        } catch (ParseException e) {
            Toast.makeText(context, "Error:- " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return getCurrentSystemTimeStamp() + "";
        }
    }
    public static String convertTimestampToDateWithTimeToServer(String dateInMilliseconds) {
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String convertImageToBase64(Bitmap bitmap, Context context) {
        try {
            String encodedImage = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            return encodedImage;
        } catch (OutOfMemoryError error) {

            Toast.makeText(context, context.getString(R.string.justErrorCode)+" 10", Toast.LENGTH_SHORT).show();
        }
        return null;

    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap getResizedCameraBitmap(Bitmap image, int maxSize) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }


    public static boolean checkAndCreateACACHADirectoryForImages() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), IMAGE_STORAGE_DIRECTORY_NAME);
        System.out.println("oooooo--------------r1---"+rootPath.getAbsolutePath());
        if (!rootPath.exists()) {
            rootPath.mkdirs();

            return false;
        } else {
            return true;
        }

    }

    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_CALENDAR_EVENT);
        return df.format(c.getTime());
    }

    public static String getTodayDateForHome() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_CALENDAR_HOME);
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    public static String getTodayTimeForHome() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_TIME_HOME);
        String formattedTime = df.format(c.getTime());
        return formattedTime;
    }


    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static String convertTimestampToTime(String dateInMilliseconds) {
        return DateFormat.format("hh:mm a", Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String convertTimestampToDate(String dateInMilliseconds) {
        return DateFormat.format(FORMAT_CALENDAR_EVENT, Long.parseLong(dateInMilliseconds)).toString();
    }

    ///// convert sec to hhmmss
    ///////////
    public static String getDurationString(long millis) {

        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }

    public static String getDurationStringattendance(long millis) {

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));

    }

    //// convert bytes to mb


    public static long getCurrentSystemTimeStamp() {
        Calendar calendar = new GregorianCalendar();
        return calendar.getTimeInMillis();
    }


    private static boolean isTimeAutomaticTime(Context c) {
        return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
    }

    private static boolean isTimeAutomaticTimeZone(Context c) {
        return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
    }


    public static void showNotification(String tickerMessage, String contentMessage, String notificationType,
                                                            Context context, boolean autoCancel, boolean Ongoing) {


        CreateCommNotificationChannel(context);


        Bitmap logoBitmapIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_taskeaze);
        Intent intentForPendingIntent = null;
        if (notificationType.equals(NOTIFICATION_TYPE_RELOG_USER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                intentForPendingIntent = new Intent(context, SplashScreen.class);
            }
            intentForPendingIntent.setAction("CLEAR_ALL_USER_PREFERENCES");
        }
        if (notificationType.equals(NOTIFICATION_TYPE_ON_GPS)) {
            intentForPendingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        }
        if (notificationType.equals(NOTIFICATION_TYPE_GRANT_PERMISSION)) {
            intentForPendingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        }
        if (notificationType.equals(NOTIFICATION_TYPE_NOTHING)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                intentForPendingIntent = new Intent(context, SplashScreen.class);
            }
        }
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

            pendingIntent =  PendingIntent.getActivity(context, 0, intentForPendingIntent, PendingIntent.FLAG_IMMUTABLE);
        else   pendingIntent =  PendingIntent.getActivity(context, 0, intentForPendingIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_OF_COMM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_taskeaze);
        builder.setLargeIcon(logoBitmapIcon);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setTicker(tickerMessage);
        builder.setContentText(contentMessage);
        builder.setAutoCancel(autoCancel);
        builder.setOngoing(Ongoing);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentMessage));
        builder.setContentIntent(pendingIntent);
        builder.setPriority(PRIORITY_HIGH);
        final Notification notification = builder.build();
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (notificationType.equals(NOTIFICATION_TYPE_RELOG_USER)) {
            mNotificationManager.notify(NOTIFICATION_TYPE_RELOG_USER_ID, notification);
        }
        if (notificationType.equals(NOTIFICATION_TYPE_ON_GPS)) {
            mNotificationManager.notify(NOTIFICATION_TYPE_ON_GPS_ID, notification);
        }
        if (notificationType.equals(NOTIFICATION_TYPE_GRANT_PERMISSION)) {
            mNotificationManager.notify(NOTIFICATION_TYPE_GRANT_PERMISSION_ID, notification);
        }
        if (notificationType.equals(NOTIFICATION_TYPE_NOTHING)) {
            mNotificationManager.notify(NOTIFICATION_TYPE_NOTHING_ID, notification);
        }


        //
    }


    public static void CreateCommNotificationChannel(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID_OF_COMM_NOTIFICATION,
                    NOTIFICATION_CHANNEL_NAME_OF_COMM_NOTIFICATION,
                    importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }



    private static void showNoInternetSnackBar(final AppCompatActivity activity, final Context context,
                                               View displayView, String message, String buttonName) {

        try {
            Snackbar snackbar = Snackbar
                    .make(displayView, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                            context.startActivity(myIntent);
                        }
                    });

            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int getLocationMode(Context context) {
        try {
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            return 2;
        }
    }

    public static boolean isGpsOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean status = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return status;
    }

    public static void gotoLocationSettings(final Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.app_name) + " asking to maintain high accuracy GPS state.")
                .setTitle(context.getString(R.string.alert));

        builder.setMessage(context.getString(R.string.app_name) + " asking to maintain high accuracy GPS state.")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setTitle(context.getString(R.string.alert));
        alert.show();
    }

    private static Map<String, ?> getAllSharedPreference(Context context, String sharedPreferenceName) {

        SharedPreferences preferences = context.getSharedPreferences(sharedPreferenceName, 0);

        return preferences.getAll();
    }


    public static String getAddressFromCoordinates(Context context, Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        String address = "NA",city = "",state = "",country = "",postalCode = "", knownName = "";
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public static String getOfflineDistance(Location location_1, Location location_2){
        double distance=location_1.distanceTo(location_2);
        distance = distance * 0.001;
        return String.format("%.2f", distance);
    }


    public static String makeserverConnectionComm(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
