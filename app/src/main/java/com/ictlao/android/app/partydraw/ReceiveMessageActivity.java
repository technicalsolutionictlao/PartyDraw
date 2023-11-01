package com.ictlao.android.app.partydraw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ReceiveMessageActivity extends AppCompatActivity {

    private final String TAG = "RECEIVE";

    // Memory data variable
    private String mGroupName = "";
    private String mPlayerRegisterIdName;
    private String mGroupPassword = "";
    private  int mCountTimes = 0;
    private ArrayList<String> mPlayerLists;
    private int mPlayerSizes = 0;
    private boolean isMessageShow = false;
    private ArrayList<String> mArrayMessageSize;
    private AlertDialog mProgressBarWaitingInternet;
    private boolean isReceivesMessageActivityActivate = false;
    private MessageItems mMessageItems;
    private InternetProvider mInternetProvider;
    private boolean isConnected = false;
    private ArrayList<String> mPlayerOnlineLists;
    private HashMap<String, String> mHashMapGroupLists;
    private String mCheckMessageString = "";

    // UI variable
    private SharedPreferences mSharePreference;
    private Intent mService;
    private TextView mTitle, mBody;
    private Toast mToastMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);
        mHashMapGroupLists = new HashMap<>();
        mArrayMessageSize = new ArrayList<>();
        mPlayerLists = new ArrayList<>();
        mPlayerOnlineLists = new ArrayList<>();
        mService = new Intent(ReceiveMessageActivity.this, MyFirebaseInstanceIDService.class);
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        mSharePreference = getSharedPreferences(HomeActivity.Settings, 0);
        mPlayerRegisterIdName = mSharePreference.getString(HomeActivity.PLAYERID,"");
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath);
        mInternetProvider = new InternetProvider(ReceiveMessageActivity.this);
        isConnected = mInternetProvider.isConnected();
        if(isConnected) {
            progressBarDialog();
            reCeivesMessageLastNotifications();
        }else {
            if (isMessageShow) {
                mToastMessage.cancel();
                isMessageShow = false;
            }
            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.NetWorkConnectionError));
            ReceiveMessageActivity.this.finish();

        }
    }

    private void reCeivesMessageLastNotifications(){
        HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath +mPlayerRegisterIdName);
        HomeActivity.mNotificationMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mArrayMessageSize.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    mArrayMessageSize.add(snapshot1.getKey());
                }

                if(mArrayMessageSize.size() > 0){
                    String g = snapshot.child(mArrayMessageSize.get(mArrayMessageSize.size() - 1) + "/" + mPlayerRegisterIdName).child("g").getValue().toString();
                    String p = snapshot.child(mArrayMessageSize.get(mArrayMessageSize.size() - 1) + "/" + mPlayerRegisterIdName).child("p").getValue().toString();

                    mGroupName = g;
                    mGroupPassword = p;
                    CheckPlayerOnlineMode();
                }else{
                    ReceiveMessageActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckPlayerOnlineMode(){
        HomeActivity.mStatus_Online_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPlayerOnlineLists.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    mPlayerOnlineLists.add(snapshot1.getValue().toString());
                }

                checkIfPlayerInOnlineAndInternetConnection();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfPlayerInOnlineAndInternetConnection(){

        if(mPlayerOnlineLists.contains(mPlayerRegisterIdName)) {
            retrievesGroupLists();
        }else {
            if(isMessageShow){
                mToastMessage.cancel();
                isMessageShow = false;
            }
            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceiveMessageActivityCouldNotJoinGroupNow));
            ReceiveMessageActivity.this.finish();
            Intent intent = new Intent(ReceiveMessageActivity.this, Waiting_Activity.class);
            startActivity(intent);
        }
    }

    // add player name to group
    private void addPlayerListener() {
        if(HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference != null) {
            HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.child(mGroupName);
            HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(isReceivesMessageActivityActivate) {
                        if (!mGroupName.equals("") && !mPlayerRegisterIdName.equals("")) {
                            mCountTimes++;
                            mPlayerLists.clear();
                            for (DataSnapshot snapshot : dataSnapshot.child(mGroupName).getChildren()) {
                                if (!snapshot.getKey().equals("message")) {
                                    mPlayerLists.add(snapshot.getKey());
                                }else{
                                    mCheckMessageString = snapshot.getKey();
                                }
                            }
                            if (mCountTimes == 1 && !mGroupName.equals("") && !mPlayerRegisterIdName.equals("") && mPlayerLists.size() > 0 && mPlayerLists.size() <= HomeActivity.mLimitPlayerInGroupNumber) {
                                mPlayerSizes = Integer.parseInt(mPlayerLists.get(mPlayerLists.size() - 1)) + 1;
                                HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName + "/" + mPlayerSizes);
                                HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.setValue(mPlayerRegisterIdName);
                                addEventJoinListerner();
                                RemoveMessageListner();
                            } else if(mCountTimes == 1 && mPlayerLists.size() <= 0) {
                                if(mCheckMessageString.equals("message")){
                                    mPlayerSizes = Integer.parseInt(mPlayerLists.get(mPlayerLists.size()));
                                    HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName + "/" + mPlayerSizes);
                                    HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.setValue(mPlayerRegisterIdName);
                                    addEventJoinListerner();
                                    RemoveMessageListner();
                                }else {
                                    RemoveMessageListner();
                                    if (isMessageShow) {
                                        mToastMessage.cancel();
                                        isMessageShow = false;
                                    }
                                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceivesMessageActivityNoGroupInTheList));
                                    ReceiveMessageActivity.this.finish();
                                }
                                RemoveMessageListner();
                                if(mProgressBarWaitingInternet != null) {
                                    mProgressBarWaitingInternet.dismiss();
                                    mProgressBarWaitingInternet = null;
                                }
                            } else if(mCountTimes == 1 && mPlayerLists.size() > HomeActivity.mLimitPlayerInGroupNumber){
                                if(isMessageShow){
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }
                                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceiveMessageActivityCouldNotJoinGroupNow));
                                if(mProgressBarWaitingInternet != null){
                                    mProgressBarWaitingInternet.dismiss();
                                    mProgressBarWaitingInternet = null;
                                }
                                ReceiveMessageActivity.this.finish();
                            }else{
                                ReceiveMessageActivity.this.finish();
                            }
                        }else {
                            ReceiveMessageActivity.this.finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // check player joining group
    private void addEventJoinListerner() {
        if (isReceivesMessageActivityActivate) {

            RemoveMessageListner();
            mMessageItems = new MessageItems(mPlayerRegisterIdName, "1");
            HomeActivity.mMessageReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName + HomeActivity.mTreeMassage);
            HomeActivity.mMessageReference.setValue(mMessageItems);
            Intent intent = new Intent(getApplicationContext(), Waiting_Activity.class);
            intent.putExtra(HomeActivity.GROUPNAME, mGroupName);
            intent.putExtra(HomeActivity.GROUPPASSWORD, mGroupPassword);
            startActivity(intent);
            stopService(mService);
            RemoveOnlineStatus();
            ReceiveMessageActivity.this.finish();
        }
    }

    // remove message
    private void RemoveMessageListner(){
        HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath + mPlayerRegisterIdName + "/" + mArrayMessageSize.get(mArrayMessageSize.size() - 1));
        HomeActivity.mNotificationMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isReceivesMessageActivityActivate) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(mPlayerRegisterIdName)) {
                            snapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // remove status
    private void RemoveOnlineStatus(){
        if(HomeActivity.mStatus_Online_Reference != null)
        {
            HomeActivity.mStatus_Online_Reference.orderByChild(mPlayerRegisterIdName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isReceivesMessageActivityActivate) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getValue().toString().equals(mPlayerRegisterIdName)) {
                                snapshot.getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void CustomToastMessage(String string_title, String string_body){
        try {
            isMessageShow = true;
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.message_layout, (ViewGroup) findViewById(R.id.root_message));
            mTitle = layout.findViewById(R.id.message_title);
            mBody = layout.findViewById(R.id.message_body);
            mTitle.setText(string_title);
            mBody.setText(string_body);
            mToastMessage = new Toast(ReceiveMessageActivity.this);
            mToastMessage.setGravity(Gravity.BOTTOM, 0, 0);
            mToastMessage.setDuration(Toast.LENGTH_LONG);
            mToastMessage.setView(layout);
            mToastMessage.show();
        }catch (WindowManager.BadTokenException ex){
            ex.printStackTrace();
        }
    }

    private void ClearMemoryData(){
        mGroupName = "";
        mPlayerRegisterIdName = "";
        mGroupPassword = "";
        mCountTimes = 0;
        mPlayerLists.clear();
        mPlayerSizes = 0;
    }

    private void ClearFirebaseDatabasePath(){
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = null;
        HomeActivity.mNotificationMessagesReference = null;
        HomeActivity.mStatus_Online_Reference = null;
    }

    private void progressBarDialog(){
        if(mProgressBarWaitingInternet != null){
            mProgressBarWaitingInternet.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveMessageActivity.this);
        View view = getLayoutInflater().inflate(R.layout.progress_bar_dialog, null);

        builder.setView(view);
        mProgressBarWaitingInternet = builder.create();
        mProgressBarWaitingInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressBarWaitingInternet.show();
        mProgressBarWaitingInternet.setCancelable(false);
    }

    @Override
    protected void onResume() {
        isReceivesMessageActivityActivate = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        if(mProgressBarWaitingInternet != null){
            mProgressBarWaitingInternet.dismiss();
            mProgressBarWaitingInternet = null;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(this.isFinishing()){
            ClearFirebaseDatabasePath();
            ClearMemoryData();
        }
        isReceivesMessageActivityActivate = false;
        super.onDestroy();
    }

    private void retrievesGroupLists(){
        HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupListsPath);
        HomeActivity.mGroupListsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isReceivesMessageActivityActivate) {
                    mHashMapGroupLists.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mHashMapGroupLists.put(snapshot.getKey(), snapshot.getValue().toString());
                    }

                    if(mHashMapGroupLists.containsKey(mGroupName) && mHashMapGroupLists.containsValue(mGroupPassword)){
                        addPlayerListener();
                    }else{
                        RemoveMessageListner();
                        if(mProgressBarWaitingInternet != null){
                            mProgressBarWaitingInternet.dismiss();
                            mProgressBarWaitingInternet = null;
                        }
                        if(isMessageShow){
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceivesMessageActivityNoGroupInTheList));
                        ReceiveMessageActivity.this.finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}