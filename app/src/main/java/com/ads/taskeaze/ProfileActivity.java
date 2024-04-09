package com.ads.taskeaze;

import static com.ads.taskeaze.utils.ConstantUtils.KEY_ADDRESS;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_DEPARTMENT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_EMAIL_ADDRESS;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_FIRST_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_IMAGE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_LAST_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_USER_NAME;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_USER;
import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.ads.taskeaze.model.UserModel;
import com.ads.taskeaze.utils.CommonFunc;


import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProfileActivity  extends SupportActivity {


    private TextView emname, empdep, email, phone, empadd, empUserName;
    private EditText edt_name, edt_dept, edt_email, edt_address, edt_userName;
    private ImageView ivimage;
    private Button  profileCancelBtn, profileUpdateBtn;
    private ImageButton editBtn;
    private FirebaseAuth firebaseAuth;
    private String userId;
    LinearLayout buttonLayout;
    LinearLayout buttonLinearLayout;
    private DatabaseReference usersRef;

    String profileB64;

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout relativeLayout = findViewById(R.id.content_support);
        getLayoutInflater().inflate(R.layout.activity_profile_actiivty, relativeLayout);
        getSupportActionBar().setTitle(getResources().getString(R.string.myprofile_txt));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        emname = findViewById(R.id.profileName);
        empdep = findViewById(R.id.profileDepartment);
        email = findViewById(R.id.profileEmail);
        phone = findViewById(R.id.profilePhoneNumber);
        empadd = findViewById(R.id.profileAddress);
        empUserName = findViewById(R.id.profileUserName);
//
        edt_name = findViewById(R.id.edit_profileName);
        edt_dept = findViewById(R.id.edit_profileDepartment);
        edt_email = findViewById(R.id.edit_Email);
        edt_address = findViewById(R.id.edit_Address);
        edt_userName = findViewById(R.id.edit_UserName);
//
         ivimage = findViewById(R.id.profile_image);

        editBtn = findViewById(R.id.edit_btn);
        profileCancelBtn = findViewById(R.id.profileCancel_btn);
        profileUpdateBtn = findViewById(R.id.profileUpdate_btn);
        buttonLinearLayout = findViewById(R.id.button_layout);

        ivimage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectUploadImage();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Edit button clicked", Toast.LENGTH_SHORT).show();
               showEditViewProfile();

            }
        });
            profileCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Edit button clicked", Toast.LENGTH_SHORT).show();
                    showTextViewProfile();
                }
            });

            profileUpdateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertUpdateDialog();
                }
            });

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        getUserInfo(userId);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK)
        {
            bitmap = uriToBitmap(image_uri);
            bitmap = CommonFunc.getResizedBitmap(bitmap, 1024);

        }
        else if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK){
            bitmap = uriToBitmap(data.getData());
            bitmap = CommonFunc.getResizedBitmap(bitmap, 1024);

        }
        converttob64(bitmap);

    }

    private void convertb64ToImage(String base64){


        final String pureBase64Encoded = base64.substring(base64.indexOf(",") + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        ivimage.setImageBitmap(decodedBitmap);
    }

    private void openGalary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_GALLERY);
    }

    private void opencamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(selectedFileUri, "r");

            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }

    private void selectUploadImage() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Upload Pictures Option");
        builder.setMessage("How do you want to set your picture?");
        builder.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        openGalary();

                    }
                }
        );
        builder.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        opencamera();

                    }
                }
        );
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
@NonNull
    private void converttob64(Bitmap bitmap) {

        Bitmap bit = CommonFunc.getResizedBitmap(bitmap, 100);
        profileB64 = CommonFunc.convertImageToBase64(bitmap,this);
        String thumbb64 = CommonFunc.convertImageToBase64(bit, this);

        ivimage.setImageBitmap(bit);

    }

    private final int REQUEST_CAMERA = 123;

    private final int REQUEST_GALLERY = 124;

    private void onRefresh(){
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        getUserInfo(userId);
    }


    public void onClick(View view) {
        if(view.getId() == R.id.edit_btn){
            showEditViewProfile();
        }

        else if(view.getId() == R.id.profileCancel_btn){
            showTextViewProfile();
        }

        else if(view.getId() == R.id.profileUpdate_btn){
            alertUpdateDialog();
        }

    }

    private String[] splitFullName(String fullName) {
        // Split the full name using space as a delimiter
        String[] names = fullName.split(" ");

        // Check if there are at least two parts (first name and last name)
        if (names.length >= 2) {
            // Extract the first name and last name
            String firstName = names[0];
            String lastName = names[names.length - 1];

            // Log or return the names as needed
            Log.d("SplitFullName", "First Name: " + firstName);
            Log.d("SplitFullName", "Last Name: " + lastName);

            // You can also return the names if needed
            return new String[]{firstName, lastName};
        } else {
            // Handle the case where the full name doesn't contain both first and last names
            Log.w("SplitFullName", "Invalid full name format");
            return null;
        }
    }

    private void processFullName(String fullName) {
        String[] names = splitFullName(fullName);

        if (names != null) {
            // Now you can use names[0] as the first name and names[1] as the last name
            String firstName = names[0];
            String lastName = names[1];
        }
    }

    private void updateUserInfo(String userId, String newfName, String newlName, String newEmail, String newUserName, String newAddress, String newDept) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put(KEY_FIRST_NAME, newfName);
        userInfo.put(KEY_LAST_NAME, newlName);
        userInfo.put(KEY_USER_NAME, newUserName);
        userInfo.put(KEY_ADDRESS, newAddress);
        userInfo.put(KEY_DEPARTMENT, newDept);
        userInfo.put(KEY_EMAIL_ADDRESS, newEmail);
        userInfo.put(KEY_IMAGE, profileB64);


        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if(user.getUserId().equals(userId)) {
                        usersRef.child(dataSnapshot.getKey()).updateChildren(userInfo);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        onRefresh();

        Log.d("UpdateUserInfo", "User details updated in the database");
    }


    private void alertUpdateDialog() {
        if(userId == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            userId = firebaseAuth.getCurrentUser().getUid();
        }
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(this);

        builder1.setMessage("Do you want to update the profile ?");

        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        try {
                            String[] names = splitFullName(edt_name.getText().toString().trim());

                            if (names != null) {
                                // Now you can use names[0] as the first name and names[1] as the last name
                                String firstName = names[0];
                                String lastName = names[1];

                                if(edt_email.getText().toString().trim().length()>3 && edt_email.getText().toString().contains("@")) {
                                    if(edt_dept.getText().toString().trim().length()>2) {
                                        if(edt_address.getText().toString().trim().length()>3) {
                                            updateUserInfo(userId, firstName, lastName, edt_email.getText().toString().trim(), edt_userName.getText().toString().trim(),
                                                    edt_address.getText().toString().trim(), edt_dept.getText().toString().trim());
                                        } else {
                                            edt_address.setError("Please enter proper address");
                                        }

                                    } else {
                                        edt_dept.setError("Please enter a valid department");
                                    }
                                } else {
                                    edt_email.setError("Please enter valid email");
                                }
                            }
                            else {
                                edt_name.setError("Please enter name");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        showTextViewProfile();
                        getUserInfo(userId);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        android.app.AlertDialog alertCheckInAlertDialog = builder1.create();
        alertCheckInAlertDialog.show();
    }

    private void getUserInfo(String userId) {
        usersRef = FirebaseDatabase.getInstance().getReference(KEY_USER);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if(user.getUserId() != null) {
                        if(user.getUserId().equals(userId)) {
                            emname.setText(user.getFirstName() + " " + user.getLastName());
                            edt_name.setText(user.getFirstName() + " " + user.getLastName());
                            empdep.setText(user.getDept());
                            edt_dept.setText(user.getDept());
                            empadd.setText(user.getAddress());
                            edt_address.setText(user.getAddress());
                            email.setText(user.getEmailAddress());
                            edt_email.setText(user.getEmailAddress());
                            empUserName.setText(user.getUserName());
                            edt_userName.setText(user.getUserName());
                            phone.setText(user.getPhonenumber());
                            profileB64 = user.getUserImage();
                            if(profileB64!=null) {
                                convertb64ToImage(profileB64);
                            }

                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showTextViewProfile() {
        emname.setVisibility(View.VISIBLE);
        empdep.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        empadd.setVisibility(View.VISIBLE);
        empUserName.setVisibility(View.VISIBLE);

        edt_name.setVisibility(View.GONE);
        edt_email.setVisibility(View.GONE);
        edt_dept.setVisibility(View.GONE);
        edt_address.setVisibility(View.GONE);
        edt_userName.setVisibility(View.GONE);

        buttonLinearLayout.setVisibility(View.GONE);
    }

    private void showEditViewProfile() {
        emname.setVisibility(View.GONE);
        empdep.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        empadd.setVisibility(View.GONE);
        empUserName.setVisibility(View.GONE);
        editBtn.setVisibility((View.GONE));
//
        edt_name.setVisibility(View.VISIBLE);
        edt_email.setVisibility(View.VISIBLE);
        edt_dept.setVisibility(View.VISIBLE);
        edt_address.setVisibility(View.VISIBLE);
        edt_userName.setVisibility(View.VISIBLE);
        buttonLinearLayout.setVisibility(View.VISIBLE);
        profileUpdateBtn.setVisibility(View.VISIBLE);

    }


    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

}