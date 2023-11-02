package com.ictlao.android.app.partydraw.Feature.Waiting;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.partydraw.Core.ArrayAdapterWaitingLobby;
import com.ictlao.android.app.partydraw.Core.InvitationFilterAdapter;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.ManagePlayerStatus;
import com.ictlao.android.app.partydraw.Core.Models.ItemFilterInvitation;
import com.ictlao.android.app.partydraw.Core.Models.LobbyItems;
import com.ictlao.android.app.partydraw.Core.Models.MessageItems;
import com.ictlao.android.app.partydraw.Core.Models.NotificationItems;
import com.ictlao.android.app.partydraw.Core.OnlineJokerDrawHelper;
import com.ictlao.android.app.partydraw.Core.WaitingActivityHelper;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.partydraw.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Waiting_Activity extends AppCompatActivity {

    private SharedPreferences mSharePreference;
    private SharedPreferences.Editor mEditor;
    // Memory data variable
    public static String mGroupName;
    public static String mGroupPassword;
    public static boolean isActive;
    private ArrayList<String> mPlayerIdOnlineMode;
    private ArrayList<ItemFilterInvitation> mPreViewPlayerOnlineMode;
    private boolean isBGMSound;
    private boolean isMessageShow = false;
    private String mPlayerRegisterId = "";
    private MessageItems mMessageItems;
    private ArrayAdapterWaitingLobby mArrayAdapterWaitingLobby;
    private ArrayList<String> mAllPlayerPlayingGame;
    public static boolean isWaitingGameActive = false;
    private String mAdmin = "";
    //private ArrayAdapter mArrayAdapter;
    private InvitationFilterAdapter mInvitationFilterAdapter;
    private ArrayList<String> mAllPlayerListLobby;
    private boolean isGetAllPlayerLobby = false;
    private int mNotificationEnable;
    private int mNotificationDisable;
    private int mLimitSize = 13;
    private int mIndexRange = 0;
    private ArrayList<String> mArrayListReadyPlayingGame;
    private AlertDialog mDialogInformation;
    private TextView mTextViewDisplayGroupName, mTextViewDisplayGroupPassword;
    private TextView mDisplayPlayerSize;
    private Button mExitInformation;
    private String mInsertAdmin = "";
    private String mLanguage = "";

    // Sending Notification
    private String mDateFormat = "";
    private ArrayList<String> mArrayListStorePositionOfPlayerInvited;
    private NotificationItems mNotificationItems;
    private ArrayList<String> mArrayListNotificationMessages;
    private int mCountTimes = 1;
    private int mMessageSize = 0;
    private boolean isInviteActivateClick = false;
    private boolean isOnlineInvitedClick = false;

    // UI variable
    //private TextView mDisplayGroupName;
    private ListView mAllPlayerListView;
    private Button mStartGameButton;
    private ArrayList<String> mPlayerNameLists;
    private ArrayList<LobbyItems> mPreviewPlayerNameLists;
    private AlertDialog mDialogInvitation;
    private Button InviteFriend;
    private TextView mHeaderInvitation;
    private ListView mPlayerOnlineLists;
    private Button mCancelInvitation;
    private EditText mPlayerIdInput;
    //private Button mSearchButton;
    private AlertDialog mConfirmInvitationDialog;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private Toast mToastMessage;
    private TextView mTitle, mBody;
    private AlertDialog mWaitingProgressBarDialog;
    private AlertDialog mBackPressPopupConfirmation;
    private Button mOkPopupBackPress, mCancelPopupBackPress;
    private TextView mTextViewPopupBackPressBody;
    //private TextView mTextViewOnline, mTextViewLobby, mTextViewAllPlayerIdName;
    private TextView mTextViewDisplayNameId;
    private Button mOkConfirmInvitation, mCancelConfirmInvitation;
    private TextView mDisplayAdminPlayer;
    private ImageView mImageViewBack, mImageViewForward;
    private Button mButtonRefresh;
    private TextView mSinglePageSize;
    private AlertDialog mDialogFullNine;
    private TextView mTextViewFullNineMessage;
    private Button mOkFullNine, mCancelFullNine;
    private boolean isDestroy = false;

    // class variable
    private WaitingActivityHelper mWaitingActivityHelper;
    private OnlineJokerDrawHelper mOnlineJokerDrawHelper;
    private ManagePlayerStatus mManagePlayerStatus;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharePreference = getSharedPreferences(HomeActivity.Settings, 0);
        mLanguage = mSharePreference.getString(HomeActivity.DL, "en");

        Locale locale = new Locale(mLanguage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_waiting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setTitle(R.string.ToolBarJokerDrawOnlineGame);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mManagePlayerStatus = new ManagePlayerStatus(Waiting_Activity.this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mGroupName = getIntent().getExtras().getString(HomeActivity.GROUPNAME);
            mGroupPassword = getIntent().getExtras().getString(HomeActivity.GROUPPASSWORD);

            mEditor = mSharePreference.edit();
            mEditor.putString(HomeActivity.GROUPNAME, mGroupName);
            mEditor.putString(HomeActivity.GROUPPASSWORD, mGroupPassword);
            mEditor.commit();
        }
        isWaitingGameActive = true;
    }

    private void InitializeFirebaseValuePath() {
        HomeActivity.mGroupName = mSharePreference.getString(HomeActivity.GROUPNAME, "");
        HomeActivity.mPassword = mSharePreference.getString(HomeActivity.GROUPPASSWORD, "");
        mPlayerRegisterId = mSharePreference.getString(HomeActivity.PLAYERID, "");

        mGroupName = HomeActivity.mGroupName;
        mGroupPassword = HomeActivity.mPassword;

        mNotificationDisable = R.drawable.invitation_icon_disable;
        mNotificationEnable = R.drawable.invitation_icon_enable;

        mArrayListStorePositionOfPlayerInvited = new ArrayList<>();
        mArrayListNotificationMessages = new ArrayList<>();
        mAllPlayerPlayingGame = new ArrayList<>();
        mPreviewPlayerNameLists = new ArrayList<>();
        mPlayerIdOnlineMode = new ArrayList<>();
        mPreViewPlayerOnlineMode = new ArrayList<>();
        mDisplayAdminPlayer = findViewById(R.id.show_admin_player);
        mAllPlayerListView = findViewById(R.id.waitPlayerName);
        mStartGameButton = findViewById(R.id.Start_Game);
        mPlayerNameLists = new ArrayList<>();
        mAllPlayerListLobby = new ArrayList<>();
        mArrayListReadyPlayingGame = new ArrayList<>();

        mStartGameButton.setEnabled(false);
        mStartGameButton.setBackgroundResource(R.drawable.play_background_button_online_joker_draw_disabled);

        InviteFriend = findViewById(R.id.Invite);

        InviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenInviteListFriend();
            }
        });

        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        mManagePlayerStatus.removeStatusOnline(mPlayerRegisterId);

        HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath);

        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath + mGroupName);

        HomeActivity.mMessageReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName + HomeActivity.mTreeMassage);

        HomeActivity.mStatus_Lobby_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusLobbyModePath);

        HomeActivity.mStatus_Lobby_Reference.child(mPlayerRegisterId).setValue(mPlayerRegisterId);

        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName);

        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);

        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);

        if (mPlayerNameLists.size() < 2) {

            mStartGameButton.setEnabled(false);

        }
    }

    // start the game
    private void startGame(){
        mMessageItems = new MessageItems(mPlayerRegisterId, "2");
        HomeActivity.mMessageReference.setValue(mMessageItems);
        mStartGameButton.setEnabled(false);
        addMessageListner();
    }

    // send and show message to other users
    private void addMessageListner() {
        HomeActivity.mMessageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = "";
                if (isWaitingGameActive) {
                    if (dataSnapshot.hasChild("mName")) {
                        String name = dataSnapshot.child("mName").getValue().toString();
                        status = dataSnapshot.child("mStatus").getValue().toString();

                        if (status.equals("0")) {
                            if (mPlayerRegisterId.equals(name)) {
                                if (isMessageShow) {
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }
                                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.WaitingActivityMessageCreateGroupToAdmin));
                            }
                        } else if (status.equals("1")) {
                            if (mPlayerRegisterId.equals(name)) {
                                if (isMessageShow) {
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }
                                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.WaitingActivityMessageToMemberJoiningGroup));
                            } else {
                                if (isMessageShow) {
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }
                                CustomToastMessage(getString(R.string.Waring_header), name +" "+ getString(R.string.WaitingActivityMessageToOtherJoiningGroup));
                            }
                        } else if (status.equals("2")) {
                            if (isMessageShow) {
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            AdminStartFirst();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // start the game
    private void StartTheGame() {
        if(mArrayListReadyPlayingGame.size() > 1 && !mInsertAdmin.equals("")) {
            ArrayList<String> ClonePlayerNameLists = new ArrayList<>(mArrayListReadyPlayingGame);

            if (ClonePlayerNameLists.contains(mInsertAdmin)) {
                HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.child("0").setValue(mInsertAdmin);
                ClonePlayerNameLists.remove(mInsertAdmin);
            }
            for (int i = 0; i < ClonePlayerNameLists.size(); i++) {
                HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.child(i + 1 + "").setValue(ClonePlayerNameLists.get(i));
            }
            ClearMemoryData();
            Intent intent = new Intent(Waiting_Activity.this, OnlineJokerDraw.class);
            intent.putExtra(HomeActivity.GROUPNAME, mGroupName);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.waiting_menu, menu);
        MenuItem mCountPlayerSizes = menu.findItem(R.id.PlayerSize);
        mDisplayPlayerSize = (TextView) MenuItemCompat.getActionView(mCountPlayerSizes);
        mDisplayPlayerSize.setTextSize(16f);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.group_information:
                 OpenInformationGroupDialog();
                return true;

            case android.R.id.home:
                mPlayerRegisterId = mSharePreference.getString(HomeActivity.PLAYERID, "");
                mGroupName = mSharePreference.getString(HomeActivity.GROUPNAME,"");
                mWaitingActivityHelper.removePlayerWaiting(mGroupName, mPlayerRegisterId);
                isGetAllPlayerLobby = true;
                getAllPlayerLobbyRoom();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // show information in dialog to users
    private void OpenInformationGroupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Waiting_Activity.this);
        View view = getLayoutInflater().inflate(R.layout.group_information_layout, null);


        mTextViewDisplayGroupName = view.findViewById(R.id.GroupName_Info);
        mTextViewDisplayGroupPassword = view.findViewById(R.id.GroupPassword_Info);
        mExitInformation = view.findViewById(R.id.exit_Info);

        if(!mGroupName.equals("")  && !mGroupPassword.equals("")){
            mTextViewDisplayGroupName.setText(getString(R.string.GroupName)+" "+mGroupName+"");
            mTextViewDisplayGroupPassword.setText(getString(R.string.GroupPassword)+" "+mGroupPassword+"");
        }

        mExitInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInformation.dismiss();
            }
        });

        builder.setView(view);
        mDialogInformation = builder.create();
        mDialogInformation.setCancelable(false);
        mDialogInformation.show();
    }

    // read online users
    private void RetreivesPlayerOnline() {
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        HomeActivity.mStatus_Online_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isWaitingGameActive) {
                    mPlayerIdOnlineMode.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mPlayerIdOnlineMode.add(snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // show progress dialog with transparent background
    private void progressBarInternetWaiting() {
        if (mWaitingProgressBarDialog != null) {
            mWaitingProgressBarDialog.dismiss();
            mWaitingProgressBarDialog = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.progress_bar_dialog, null);
        builder.setView(view);
        mWaitingProgressBarDialog = builder.create();
        mWaitingProgressBarDialog.show();
        mWaitingProgressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // show list of online user to show people to invite
    private void OpenInviteListFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Waiting_Activity.this);
        View view = getLayoutInflater().inflate(R.layout.invite_layout, null);

        isOnlineInvitedClick = true;
        mCancelInvitation = view.findViewById(R.id.CloseInvite);
        mHeaderInvitation = view.findViewById(R.id.titleInvite);
        mPlayerOnlineLists = view.findViewById(R.id.listInvite);
        mPlayerIdInput = view.findViewById(R.id.NameIDInput);
        mImageViewBack = view.findViewById(R.id.back_list);
        mImageViewForward = view.findViewById(R.id.forward_list);
        mButtonRefresh = view.findViewById(R.id.RefreshInvite);
        mSinglePageSize = view.findViewById(R.id.singlePageSize);

        mImageViewBack.setEnabled(false);
        mImageViewBack.setImageResource(R.drawable.previous_arrow_disabled);
        mImageViewForward.setEnabled(false);
        mImageViewForward.setImageResource(R.drawable.forward_arrow_disabled);

        if(mPlayerIdOnlineMode.size() > mLimitSize) {
            mImageViewForward.setEnabled(true);
            mImageViewForward.setImageResource(R.drawable.forward_arrow);
        }

        mImageViewForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLimitSize <= mPlayerIdOnlineMode.size()) {
                    mIndexRange = mLimitSize;
                    mLimitSize = mLimitSize + 13;
                    mImageViewBack.setEnabled(true);
                    mImageViewBack.setImageResource(R.drawable.previous_arrow);
                }
                CalculateListViewItem(false,"");
            }
        });

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLimitSize > 1) {
                    mIndexRange = mIndexRange - 13;
                    mLimitSize = mLimitSize - 13;
                    mImageViewForward.setEnabled(true);
                    mImageViewForward.setImageResource(R.drawable.forward_arrow);
                }
                CalculateListViewItem(false, "");
            }
        });

        mHeaderInvitation.setText(R.string.WaitingActivityPopupSearchInvitation_header);
        mCancelInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInvitation.dismiss();
            }
        });

        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarInternetWaiting();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mWaitingProgressBarDialog != null) {
                            mWaitingProgressBarDialog.dismiss();
                        }
                        CalculateListViewItem(false, "");
                    }
                },1000);
            }
        });

        mPlayerIdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // mInvitationFilterAdapter.getFilter().filter(s.toString());
                if(s.length() == 0){
                    CalculateListViewItem(false, "");
                }else{
                    CalculateListViewItem(true, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

       CalculateListViewItem(false, "");

        //----------------------------------------------------------------------------------------

        builder.setView(view);
        mDialogInvitation = builder.create();
        mDialogInvitation.show();
        mDialogInvitation.setCancelable(false);
    }

    // calculate the user to show the count of list in the page
    private void CalculateListViewItem(boolean isFilter, String mFilterString){ // send argument filter next week
        String mFilter = mFilterString;
        mPreViewPlayerOnlineMode.clear();

        if(mLimitSize >= mPlayerIdOnlineMode.size()){
            mImageViewForward.setEnabled(false);
            mImageViewForward.setImageResource(R.drawable.forward_arrow_disabled);
            if(mLimitSize > 13){
                mImageViewBack.setEnabled(true);
                mImageViewBack.setImageResource(R.drawable.previous_arrow);
            }else{
                mImageViewBack.setEnabled(false);
                mImageViewBack.setImageResource(R.drawable.previous_arrow_disabled);
            }
        }else{
            mImageViewForward.setEnabled(true);
            mImageViewForward.setImageResource(R.drawable.forward_arrow);
            if(mLimitSize > 13){
                mImageViewBack.setEnabled(true);
                mImageViewBack.setImageResource(R.drawable.previous_arrow);
            }else{
                mImageViewBack.setEnabled(false);
                mImageViewBack.setImageResource(R.drawable.previous_arrow_disabled);
            }
        }

        if(isFilter == true)
        {
            for(int i = 0; i < mPlayerIdOnlineMode.size(); i++){
                String newName = mPlayerIdOnlineMode.get(i);
                if(!mPlayerRegisterId.equals(newName)){
                    if (mArrayListStorePositionOfPlayerInvited.contains(newName)) {
                        mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationDisable));
                    } else {
                        mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationEnable));
                    }
                }
            }
        }else {
            if (mPlayerIdOnlineMode.size() >= mLimitSize) {
                for (int i = mIndexRange; i < mLimitSize; i++) {
                    String newName = mPlayerIdOnlineMode.get(i);
                    if (!mPlayerRegisterId.equals(newName)) {
                        if (mArrayListStorePositionOfPlayerInvited.contains(newName)) {
                            mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationDisable));
                        } else {
                            mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationEnable));
                        }
                    }
                }
            }else{
                for (int i = mIndexRange; i < mPlayerIdOnlineMode.size(); i++) {
                    String newName = mPlayerIdOnlineMode.get(i);
                    if (!mPlayerRegisterId.equals(newName)) {
                        if (mArrayListStorePositionOfPlayerInvited.contains(newName)) {
                            mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationDisable));
                        } else {
                            mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(newName, mNotificationEnable));
                        }
                    }
                }
            }
        }

        mInvitationFilterAdapter = new InvitationFilterAdapter(Waiting_Activity.this, mPreViewPlayerOnlineMode){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 1) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorList));
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                }
                return view;
            }
        };

        mPlayerOnlineLists.setAdapter(mInvitationFilterAdapter);
        mInvitationFilterAdapter.notifyDataSetChanged();
        mPlayerOnlineLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isOnlineInvitedClick) {
                    if(mInvitationFilterAdapter.getCount() > 0) {
                        ItemFilterInvitation items = (ItemFilterInvitation) mPlayerOnlineLists.getAdapter().getItem(position);
                        String Name = items.getName();
                        if (!mArrayListStorePositionOfPlayerInvited.contains(Name)) {
                            OpenDialogInviteConfirm(Name);
                        } else {
                            if (isMessageShow) {
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.WaitingActivityAlreadyInvitedThisPerson));
                        }
                    }
                }
            }
        });

        if(isFilter == true && !mFilter.equals("")){
            mInvitationFilterAdapter.getFilter().filter(mFilter);
        }

        int range = mIndexRange +1;
        int limit = mLimitSize;

        if(mPlayerIdOnlineMode.size() <= limit){
            // range - all / all;
            mSinglePageSize.setTextSize(20f);
            mSinglePageSize.setText(range+" - "+mPlayerIdOnlineMode.size()+"/"+mPlayerIdOnlineMode.size());
            if(mPlayerIdOnlineMode.size() == 0){
                range = 0;
                mSinglePageSize.setText(range+" - "+mPlayerIdOnlineMode.size()+"/"+mPlayerIdOnlineMode.size());
            }
        }else if(mPlayerIdOnlineMode.size() > limit){
            // range - limit / all
            mSinglePageSize.setTextSize(20f);
            mSinglePageSize.setText(range+" - "+limit+"/"+mPlayerIdOnlineMode.size());
        }else {
            mSinglePageSize.setTextSize(20f);
            mSinglePageSize.setText(range+" - "+limit+"/"+mPlayerIdOnlineMode.size());
            if(mPlayerIdOnlineMode.size() == 0){
                range = 0;
                mSinglePageSize.setText(range+" - "+mPlayerIdOnlineMode.size()+"/"+mPlayerIdOnlineMode.size());
            }
        }
    }

    // show dialog to confirm inviting
    public void OpenDialogInviteConfirm(final String Name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.invite_confirm_layout, null);

        mTextViewDisplayNameId = view.findViewById(R.id.showNameId);
        mOkConfirmInvitation = view.findViewById(R.id.Okinvite);
        mCancelConfirmInvitation = view.findViewById(R.id.CancelInvite);

        mTextViewDisplayNameId.setText(getString(R.string.WaitingActivityPopupConfirmInviteSomePersonBelow)+" "+Name);
        mOkConfirmInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialogInvitation != null){
                    mDialogInvitation.dismiss();
                }
                progressBarInternetWaiting();
                mConfirmInvitationDialog.dismiss();
                // -------------------------------------------------------------------------------------
                isInviteActivateClick = true;
                mCountTimes = 1;
                OpenCheckMessageBeforeSend(Name);
                mArrayListStorePositionOfPlayerInvited.add(Name);
                mPreViewPlayerOnlineMode.clear();
                for(int i = 0; i < mPlayerIdOnlineMode.size(); i++){
                    if(mArrayListStorePositionOfPlayerInvited.contains(mPlayerIdOnlineMode.get(i))){
                        mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(mPlayerIdOnlineMode.get(i), mNotificationDisable));
                    }else{
                        mPreViewPlayerOnlineMode.add(new ItemFilterInvitation(mPlayerIdOnlineMode.get(i), mNotificationEnable));
                    }
                }
                mInvitationFilterAdapter = new InvitationFilterAdapter(Waiting_Activity.this, mPreViewPlayerOnlineMode){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view1 = super.getView(position, convertView, parent);
                        if (position % 2 == 1) {
                            view1.setBackgroundColor(getResources().getColor(R.color.colorList));
                        } else {
                            view1.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                        }
                        return view1;
                    }
                };
                mPlayerOnlineLists.setAdapter(mInvitationFilterAdapter);
                mInvitationFilterAdapter.notifyDataSetChanged();
            }
        });

        mCancelConfirmInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmInvitationDialog.dismiss();
            }
        });

        builder.setView(view);
        mConfirmInvitationDialog = builder.create();
        mConfirmInvitationDialog.show();
    }

    // check the message before send to other user
    private void OpenCheckMessageBeforeSend(final String name){
        HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath +name);
        HomeActivity.mNotificationMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isInviteActivateClick)
                {
                    mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                    mArrayListNotificationMessages.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mArrayListNotificationMessages.add(snapshot.getKey());
                    }

                    if (mCountTimes == 1) {
                        mMessageSize = mArrayListNotificationMessages.size() + 1;
                        if (mMessageSize == 1) {
                            mNotificationItems = new NotificationItems(mGroupName, mGroupPassword, mDateFormat, mPlayerRegisterId);
                            HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath);
                            HomeActivity.mNotificationMessagesReference.child(name).child(mMessageSize + "").child(name).setValue(mNotificationItems);
                        } else {
                            mNotificationItems = new NotificationItems(mGroupName, mGroupPassword, mDateFormat, mPlayerRegisterId);
                            HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath);
                            HomeActivity.mNotificationMessagesReference.child(name).child(mMessageSize + "").child(name).setValue(mNotificationItems);
                        }
                        mCountTimes++;
                    }

                    //------------------------------------------------------------------------------------
                    if(mWaitingProgressBarDialog != null) {
                        mWaitingProgressBarDialog.dismiss();
                    }
                    OpenInviteListFriend();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // open preview name of all player
    private void OPenPreViewName() {
        progressBarInternetWaiting();
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isWaitingGameActive) {
                    mPlayerNameLists.clear();
                    ArrayList<String> KeyPeople = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("message")) {
                            if (!mPlayerNameLists.contains(snapshot.getValue().toString())) {
                                mPlayerNameLists.add(snapshot.getValue().toString());
                                KeyPeople.add(snapshot.getKey());
                            }
                        }
                    }

                    checkAdminName();
                    RetrievesPlayerPlaying();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // get all the players is playing game right now
    private void RetrievesPlayerPlaying() {
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath+mGroupName);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isWaitingGameActive) {
                    mAllPlayerPlayingGame.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mAllPlayerPlayingGame.add(snapshot.getValue().toString());
                    }

                    int count = mPlayerNameLists.size();

                    mPreviewPlayerNameLists.clear();
                    for (int i = 0; i < count; i++) {
                        if (mAllPlayerPlayingGame.contains(mPlayerNameLists.get(i))) {
                            mPreviewPlayerNameLists.add(new LobbyItems(mPlayerNameLists.get(i), getString(R.string.Drawing)));
                        } else {
                            mPreviewPlayerNameLists.add(new LobbyItems(mPlayerNameLists.get(i), getString(R.string.Waiting)));
                        }
                    }

                    mArrayAdapterWaitingLobby = new ArrayAdapterWaitingLobby(Waiting_Activity.this, mPreviewPlayerNameLists) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                view.setBackgroundColor(getResources().getColor(R.color.colorList));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            }
                            return view;
                        }
                    };
                    mArrayAdapterWaitingLobby.notifyDataSetChanged();
                    mAllPlayerListView.setAdapter(mArrayAdapterWaitingLobby);
                }
                if(mDisplayPlayerSize != null && mPreviewPlayerNameLists != null) {
                    mDisplayPlayerSize.setText(getString(R.string.WaitingActivityPlayerSize)+" " + mPreviewPlayerNameLists.size() + "/9");
                }

                if (!mAdmin.equals("")) {
                    if (mAdmin.equals(mPlayerRegisterId) && mArrayListReadyPlayingGame.size() > 1 && mAllPlayerPlayingGame.size() == 0) {
                        mStartGameButton.setEnabled(true);
                        mStartGameButton.setBackgroundResource(R.drawable.play_game_button);
                    }else{
                        mStartGameButton.setEnabled(false);
                        mStartGameButton.setBackgroundResource(R.drawable.play_game_button_disabled);
                    }
                }


                if (mPreviewPlayerNameLists.size() > 0 && !mPlayerRegisterId.equals("")) {
                    if (mPlayerNameLists.contains(mPlayerRegisterId)) {
                    } else {
                        mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
                        mWaitingActivityHelper.removePlayerReadyToPlay(mGroupName, mPlayerRegisterId);
                        mWaitingActivityHelper.removePlayerWaiting(mGroupName, mPlayerRegisterId);
                        isGetAllPlayerLobby = true;
                        getAllPlayerLobbyRoom();
                        mEditor = mSharePreference.edit();
                        mEditor.putBoolean(SelectGame.RULE, false);
                        mEditor.commit();
                    }
                }else if(mPreviewPlayerNameLists.size() == 0 && !mPlayerRegisterId.equals("")){
                    if(!mPlayerNameLists.contains(mPlayerRegisterId)){
                        mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
                        mWaitingActivityHelper.removePlayerReadyToPlay(mGroupName, mPlayerRegisterId);
                        mWaitingActivityHelper.removePlayerWaiting(mGroupName, mPlayerRegisterId);
                        isGetAllPlayerLobby = true;
                        getAllPlayerLobbyRoom();
                        mEditor = mSharePreference.edit();
                        mEditor.putBoolean(SelectGame.RULE, false);
                        mEditor.commit();
                    }
                }


                if (mPreviewPlayerNameLists.size() >= 9) {
                    if(mAdmin.equals(mPlayerRegisterId)) {
                        OpenDialogConfirmStartingGame();
                    }
                }

                if(mWaitingProgressBarDialog != null){
                    mWaitingProgressBarDialog.dismiss();
                    mWaitingProgressBarDialog = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // open dialog to confirm if has 9 people
    private void OpenDialogConfirmStartingGame(){
        if(mDialogFullNine != null){
            mDialogFullNine.dismiss();
            mDialogFullNine = null;
        }
        // Create dialog Confirm to admin to start the game
        AlertDialog.Builder builder = new AlertDialog.Builder(Waiting_Activity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_confirm_player_full_nine_layout, null);

        mTextViewFullNineMessage = view.findViewById(R.id.full_nine_message);
        mOkFullNine = view.findViewById(R.id.full_nine_ok);
        mCancelFullNine = view.findViewById(R.id.full_nine_cancel);

        mTextViewFullNineMessage.setText(R.string.ConfirmLetsStartTheGame);
        mOkFullNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFullNine.dismiss();
                startGame();
            }
        });

        mCancelFullNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFullNine.dismiss();
            }
        });

        builder.setView(view);
        mDialogFullNine = builder.create();
        mDialogFullNine.show();
    }

    @Override
    protected void onStart() {
        mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
        isBGMSound = mSharePreference.getBoolean(HomeActivity.BGM, true);
        if (HomeActivity.mBackgroundMusics == null) {
            if (isBGMSound) {
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        InitializeFirebaseValuePath();
        super.onStart();
    }

    @Override
    protected void onResume() {
        isBGMSound = mSharePreference.getBoolean(HomeActivity.BGM, true);
        mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
        mWaitingActivityHelper.setPlayerReadyToPlay(mGroupName, mPlayerRegisterId);
        if (HomeActivity.mBackgroundMusics == null) {
            if (isBGMSound) {
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.setLooping(true);
                HomeActivity.mBackgroundMusics.start();
            }
        }

        if (mPowerManager.isScreenOn()) {
            if (HomeActivity.mBackgroundMusics == null) {
                if (isBGMSound) {
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.setLooping(true);
                    HomeActivity.mBackgroundMusics.start();
                }
            }
        }
        isWaitingGameActive = true;
        isActive = true;
        OpenRetrievesPlayerReadyToPlay();
        OPenPreViewName();
        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebasePlayerRegisterPath);
        RetreivesPlayerOnline();
        addMessageListner();
        super.onResume();
    }

    @Override
    protected void onStop() {
        isBGMSound = mSharePreference.getBoolean(HomeActivity.BGM, true);
        if (HomeActivity.mBackgroundMusics != null && !(HomeActivity.isActive || SelectGame.isActive || UserPlayerSwapGame.isActive || User_Player_Lucky_Game.isActive || JokerGame.isActive || LuckyGame.isActive || SwapGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)) {
            if (isBGMSound) {
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }

        mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
        mWaitingActivityHelper.removePlayerReadyToPlay(mGroupName, mPlayerRegisterId);

        super.onStop();
    }

    @Override
    protected void onPause() {
        isBGMSound = mSharePreference.getBoolean(HomeActivity.BGM, true);
        Context context = getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                if (isBGMSound) {
                    HomeActivity.mBackgroundMusics.stop();
                    HomeActivity.mBackgroundMusics.release();
                    HomeActivity.mBackgroundMusics = null;
                }

            } else {
                // go to another activity
            }
        }

        if (!mPowerManager.isScreenOn()) {
            if (HomeActivity.mBackgroundMusics != null) {
                if (isBGMSound) {
                    HomeActivity.mBackgroundMusics.stop();
                    HomeActivity.mBackgroundMusics.release();
                    HomeActivity.mBackgroundMusics = null;
                }
            }
        }

        if(mDialogInvitation != null){
            mDialogInvitation.dismiss();
            mDialogInvitation = null;
        }

        if(mDialogFullNine != null){
            mDialogFullNine.dismiss();
            mDialogFullNine = null;
        }

        if(isMessageShow){
            mToastMessage.cancel();
            mToastMessage = null;
            isMessageShow = false;
        }

        isWaitingGameActive = false;
        isActive = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        OpenDialogBackPressPopupConfirmation();
    }

    private void CustomToastMessage(String string_title, String string_body) {
        if (isMessageShow || isWaitingGameActive == false) {
            if(mToastMessage != null) {
                mToastMessage.cancel();
                isMessageShow = false;
            }
        }else {
            isMessageShow = true;
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.message_layout, (ViewGroup) findViewById(R.id.root_message));
            mTitle = layout.findViewById(R.id.message_title);
            mBody = layout.findViewById(R.id.message_body);
            mTitle.setText(string_title);
            mBody.setText(string_body);
            mToastMessage = new Toast(getApplicationContext());
            mToastMessage.setGravity(Gravity.BOTTOM, 0, 0);
            mToastMessage.setDuration(Toast.LENGTH_LONG);
            mToastMessage.setView(layout);
            mToastMessage.show();
        }
    }

    private void ClearMemoryData() {
        isWaitingGameActive = false;
        isInviteActivateClick = false;
        mAllPlayerPlayingGame.clear();
    }

    private void OpenDialogBackPressPopupConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Waiting_Activity.this);
        View view = getLayoutInflater().inflate(R.layout.online_joker_draw_backpress_popup_layout, null);

        mTextViewPopupBackPressBody = view.findViewById(R.id.back_popup_text_body);
        mOkPopupBackPress = view.findViewById(R.id.Ok_back_popup);
        mCancelPopupBackPress = view.findViewById(R.id.Cancel_back_popup);

        mTextViewPopupBackPressBody.setText(R.string.WaitingActivityBackPressedConfirmToExit);

        mOkPopupBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackPressPopupConfirmation.dismiss();
                mWaitingActivityHelper.removePlayerWaiting(mGroupName, mPlayerRegisterId);
                mWaitingActivityHelper.removePlayerReadyToPlay(mGroupName, mPlayerRegisterId);
                isGetAllPlayerLobby = true;
                getAllPlayerLobbyRoom();
            }
        });

        mCancelPopupBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackPressPopupConfirmation.dismiss();
            }
        });

        builder.setView(view);
        mBackPressPopupConfirmation = builder.create();
        mBackPressPopupConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBackPressPopupConfirmation.show();
    }

    private void checkAdminName() {
        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
        HomeActivity.mAdminNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isWaitingGameActive) {
                    mPlayerRegisterId = mSharePreference.getString(HomeActivity.PLAYERID, "");
                    mAdmin = dataSnapshot.getValue().toString() + "";
                    if (mAdmin.equals(mPlayerRegisterId)) {
                        if(mArrayListReadyPlayingGame.size() <= 1){
                            mDisplayAdminPlayer.setText(getString(R.string.WaitingActivityYouAreAdminWaitingMembers));
                        }else{
                            mDisplayAdminPlayer.setText(R.string.WaitingActivityYouAreAdminYouCanClickToStart);
                        }
                    } else {
                        mDisplayAdminPlayer.setText(getString(R.string.WaitinActivityAdmin) +" "+ mAdmin+" "+getString(R.string.WaitingActivityWaitingForAdminClickToStart));
                    }

                    if (!mAdmin.equals("")) {
                        if (mAdmin.equals(mPlayerRegisterId) && mArrayListReadyPlayingGame.size() > 1 && mAllPlayerPlayingGame.size() == 0) {
                            mStartGameButton.setEnabled(true);
                            mStartGameButton.setBackgroundResource(R.drawable.play_game_button);
                        } else {
                            mStartGameButton.setEnabled(false);
                            mStartGameButton.setBackgroundResource(R.drawable.play_game_button_disabled);
                        }
                    }

                    if (mWaitingProgressBarDialog != null) {
                        mWaitingProgressBarDialog.dismiss();
                        mWaitingProgressBarDialog = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenRetrievesPlayerReadyToPlay(){
        HomeActivity.mReadyPlayingGameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseReadyPlayingPath+mGroupName);
        HomeActivity.mReadyPlayingGameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isWaitingGameActive) {
                    mArrayListReadyPlayingGame.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mArrayListReadyPlayingGame.add(snapshot.getKey());

                        if (!mAdmin.equals("")) {
                            if (mAdmin.equals(mPlayerRegisterId) && mArrayListReadyPlayingGame.size() > 1 && mAllPlayerPlayingGame.size() == 0) {
                                mStartGameButton.setEnabled(true);
                                mStartGameButton.setBackgroundResource(R.drawable.play_game_button);
                                if(mPlayerRegisterId.equals(mAdmin)){
                                    mDisplayAdminPlayer.setText(R.string.WaitingActivityYouAreAdminYouCanClickToStart);
                                } else {
                                    mDisplayAdminPlayer.setText(getString(R.string.WaitinActivityAdmin) +" "+ mAdmin+" "+getString(R.string.WaitingActivityWaitingForAdminClickToStart));
                                }
                            }else{
                                mStartGameButton.setEnabled(false);
                                mStartGameButton.setBackgroundResource(R.drawable.play_game_button_disabled);
                                if(mPlayerRegisterId.equals(mAdmin)){
                                    mDisplayAdminPlayer.setText(getString(R.string.WaitingActivityYouAreAdminWaitingMembers));
                                } else {
                                    mDisplayAdminPlayer.setText(getString(R.string.WaitinActivityAdmin) +" "+ mAdmin+" "+getString(R.string.WaitingActivityWaitingForAdminClickToStart));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPlayerLobbyRoom(){
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mGroupName);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isWaitingGameActive) {
                    mAllPlayerListLobby.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("message")) {
                            mAllPlayerListLobby.add(snapshot.getValue().toString());
                        }
                    }

                    if (isGetAllPlayerLobby) {
                        if (mAdmin.equals(mPlayerRegisterId)) {
                            if (mAllPlayerListLobby.size() > 1) {
                                String NextAdmin = mAllPlayerListLobby.get(0);
                                if (NextAdmin.equals(mAdmin)) {
                                    NextAdmin = mAllPlayerListLobby.get(1 % mAllPlayerListLobby.size());
                                }
                                HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                                HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);
                            }
                        }
                        ClearMemoryData();
                        Intent intent = new Intent(Waiting_Activity.this, CreateGroupActivity.class);
                        startActivity(intent);
                        finish();
                        finishAffinity();
                        isGetAllPlayerLobby = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        mGroupName = mSharePreference.getString(HomeActivity.GROUPNAME, "");
        mPlayerRegisterId = mSharePreference.getString(HomeActivity.PLAYERID, "");
        if(!mGroupName.equals("") && !mPlayerRegisterId.equals("") && isWaitingGameActive == false) {
            mWaitingActivityHelper = new WaitingActivityHelper(Waiting_Activity.this);
            mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(Waiting_Activity.this);
            mWaitingActivityHelper.removePlayerWaiting(mGroupName, mPlayerRegisterId);
            mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName, mPlayerRegisterId);
            mWaitingActivityHelper.removePlayerReadyToPlay(mGroupName, mPlayerRegisterId);
            removeValueEventListener(mGroupName);
        }
        super.onDestroy();
    }

    private void removeValueEventListener(final String group_name) {
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference();
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + group_name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isWaitingGameActive == false) {
                    ArrayList<String> mPlayerResult = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("message")) {
                            mPlayerResult.add(snapshot.getValue().toString());
                        }
                    }


                    int size = (int) dataSnapshot.getChildrenCount() - 1;
                    if (mPlayerResult.size() == 1) {
                        String name = mPlayerResult.get(0);
                        if (mPlayerRegisterId.equals(name)) {
                            mWaitingActivityHelper.removeGroupInList(group_name);
                            mWaitingActivityHelper.removeLobbyGroup(group_name);
                            mWaitingActivityHelper.removePlayingGroup(group_name);
                            mWaitingActivityHelper.removeValuesGroup(group_name);
                        }
                    }
                    if (mPlayerRegisterId.equals(mAdmin) && mPlayerResult.size() >= 1) {
                        String NextAdmin = mPlayerResult.get(0);
                        if (NextAdmin.equals(mAdmin)) {
                            NextAdmin = mPlayerResult.get(1 % mPlayerResult.size());
                        }
                        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                        HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);
                    }
                    if (size <= 0) {
                        mWaitingActivityHelper.removeGroupInList(group_name);
                        mWaitingActivityHelper.removeLobbyGroup(group_name);
                        mWaitingActivityHelper.removePlayingGroup(group_name);
                        mWaitingActivityHelper.removeValuesGroup(group_name);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void AdminStartFirst(){
        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
        HomeActivity.mAdminNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isWaitingGameActive){
                    mInsertAdmin = dataSnapshot.getValue().toString() + "";
                    StartTheGame();
                    HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath + mGroupName);
                    mMessageItems = new MessageItems(mPlayerRegisterId, "3");
                    HomeActivity.mMessageReference.setValue(mMessageItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
