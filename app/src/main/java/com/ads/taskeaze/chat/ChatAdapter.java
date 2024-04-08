package com.ads.taskeaze.chat;

//import static android.os.Build.VERSION_CODES.R;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_CANCEL;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_CHAT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_DATE_FORMAT_CHAT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_DELETE;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_MESSAGE_TYPE_LEFT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_MESSAGE_TYPE_RIGHT;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_SENDER;
import static com.ads.taskeaze.utils.ConstantUtils.KEY_TIME_STAMP;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.ads.taskeaze.R;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Myholder>{
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Context context;
    String userId;
    String message;
    String timeStamp;
    String timeDate;
    String iconUrl;
    List<ChatModel> chatList;
    DatabaseReference dbref;
    Query query;

    public ChatAdapter(Context _context, List<ChatModel> _chatList, String _iconUrl) {
        this.context = _context;
        this.chatList = _chatList;
        this.iconUrl = _iconUrl;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == KEY_MESSAGE_TYPE_LEFT) {
           View view = LayoutInflater.from(context).inflate(R.layout.layout_row_chat_left, parent, false);
           return new Myholder(view);

        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_row_chat_right, parent, false);
            return new Myholder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, @SuppressLint("RecyclerView") int position) {
        message = chatList.get(position).getMessage();
        timeStamp = chatList.get(position).getTimeStamp();
        Calendar calendar = Calendar.getInstance(Locale.CANADA);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        timeDate = DateFormat.format(KEY_DATE_FORMAT_CHAT, calendar).toString();
        holder.txtViewMessage.setText(message);
        holder.txtViewMessage.setVisibility(View.VISIBLE);
        holder.txtViewTime.setText(timeDate);

        if(iconUrl != null) {
            if(holder.iconImage != null) {
                holder.iconImage.setImageBitmap(convertb64ToImage(iconUrl));
            }
        }

        getCurrentUser();

//        try {
//            Glide.with(context).load(iconUrl).into(holder.iconImage);
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }

        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Message");
                builder.setMessage("Are You Sure To Delete this Message?");
                builder.setPositiveButton(KEY_DELETE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(position);
                    }
                });

                builder.setNegativeButton(KEY_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatList.get(position).getSender().equals(userId)) {
            return KEY_MESSAGE_TYPE_RIGHT;
        } else {
            return KEY_MESSAGE_TYPE_LEFT;
        }
    }

    private void deleteMessage(int position) {
        String messageTimeStamp = chatList.get(position).getTimeStamp();
        dbref = FirebaseDatabase.getInstance().getReference().child(KEY_CHAT);
        query = dbref.orderByChild(KEY_TIME_STAMP).equalTo(messageTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child(KEY_SENDER).getValue().equals(userId)) {
                        snapshot.getRef().removeValue();
                    } else {
                        Toast.makeText(context, "Fail to delete your message", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            userId = user.getUid();
        }
    }

    class Myholder extends RecyclerView.ViewHolder {
        CircleImageView circleImgView;
        CircleImageView iconImage;
        TextView txtViewMessage;
        TextView txtViewTime;
        LinearLayout messageLayout;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconChat);
            txtViewMessage = itemView.findViewById(R.id.txtViewMessage);
            txtViewTime = itemView.findViewById(R.id.txtViewTime);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

    private Bitmap convertb64ToImage(String base64){
        final String pureBase64Encoded = base64.substring(base64.indexOf(",") + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        return decodedBitmap;
    }
}
