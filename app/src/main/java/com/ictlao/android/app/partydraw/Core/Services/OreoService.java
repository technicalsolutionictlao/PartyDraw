package com.ictlao.android.app.partydraw.Core.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Core.ManagePlayerStatus;
import com.ictlao.android.app.partydraw.Feature.ReceiveMessage.ReceiveMessageActivity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;

public class OreoService extends Service {
    private static final String CHANNEL_ID = "MyFirebaseInstanceIDService";
    private DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
    private Query mCheckNotificationMessage;
    public String Name;
    public static String mGroupName;
    public static String mGroupPassword;
    private SharedPreferences mSharedPreferences;
    private DatabaseReference mStatusOnlineModeReference;
    private NotificationManager mNotificationManager = null;
    public static ArrayList<String> mArrayMessageSizes;
    public static ArrayList<String> mPlayerOnlineStatusMode;
    private ManagePlayerStatus mManagePlayerStatus = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mManagePlayerStatus = new ManagePlayerStatus();
        mArrayMessageSizes = new ArrayList<>();
        mPlayerOnlineStatusMode = new ArrayList<>();
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID,"");
        mCheckNotificationMessage = mFirebaseDatabase.child(HomeActivity.mTreeFirebaseNotificationMessagePath +HomeActivity.mPlayerRegisterId);
        mStatusOnlineModeReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        if(!HomeActivity.mPlayerRegisterId.equals("")) {
            mStatusOnlineModeReference.child(HomeActivity.mPlayerRegisterId).setValue(HomeActivity.mPlayerRegisterId);
            RetrieveMessage();
        }
        retrievesPlayerOnlineStatusMode();
    }

    private void retrievesPlayerOnlineStatusMode(){
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        HomeActivity.mStatus_Online_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPlayerOnlineStatusMode.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mPlayerOnlineStatusMode.add(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void RetrieveMessage(){
        mCheckNotificationMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mArrayMessageSizes.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mArrayMessageSizes.add(snapshot.getKey());
                }
                if (mArrayMessageSizes.size() > 0) {
                    String g = dataSnapshot.child(mArrayMessageSizes.get(mArrayMessageSizes.size() - 1) + "/" + HomeActivity.mPlayerRegisterId).child("g").getValue().toString();
                    String p = dataSnapshot.child(mArrayMessageSizes.get(mArrayMessageSizes.size() - 1) + "/" + HomeActivity.mPlayerRegisterId).child("p").getValue().toString();
                    String n = dataSnapshot.child(mArrayMessageSizes.get(mArrayMessageSizes.size() - 1) + "/" + HomeActivity.mPlayerRegisterId).child("n").getValue().toString();
                    mGroupName = g;
                    mGroupPassword = p;
                    int requestCode = 1;
                    Intent intent = new Intent(getApplicationContext(), ReceiveMessageActivity.class);
                    if (mNotificationManager != null) {
                        mNotificationManager.cancel(requestCode);
                    }
                    showNotification(getApplicationContext(), getString(R.string.ServiceNotificationTitle),   n +" "+ getString(R.string.ServiceMessageBodyNotificationInviteToJokerDraw), intent, requestCode);
                } else {
                    if (mNotificationManager != null) {
                        mNotificationManager.cancel(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Party Draw";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mNotificationManager.notify(reqCode, notificationBuilder.build()); // 0 is the request code, it should be unique id
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManagePlayerStatus != null)
        {
            if(!HomeActivity.mPlayerRegisterId.equals("")) {
                mManagePlayerStatus.deleteOnlineStatus(HomeActivity.mPlayerRegisterId);
            }
        }
    }
}
