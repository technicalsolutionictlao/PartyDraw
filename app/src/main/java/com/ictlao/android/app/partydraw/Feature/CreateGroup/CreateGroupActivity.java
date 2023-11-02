package com.ictlao.android.app.partydraw.Feature.CreateGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.partydraw.Core.InternetProvider;
import com.ictlao.android.app.partydraw.Core.InvitationArrayAdapter;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.ManagePlayerStatus;
import com.ictlao.android.app.partydraw.Core.Models.InviteMessageItems;
import com.ictlao.android.app.partydraw.Core.Models.MessageItems;
import com.ictlao.android.app.partydraw.Core.Models.PlayerRegistrationItem;
import com.ictlao.android.app.partydraw.Core.Services.MyFirebaseInstanceIDService;
import com.ictlao.android.app.partydraw.Core.Services.OreoService;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CreateGroupActivity extends AppCompatActivity {
    // shared Object variable
    private SharedPreferences mSharedPreferences;
    // firebase query variable
    private Query mQueryPlayerRegister, mQueryGroupList;
    // Memory data variable
    // String variable in popup create group
    private String mCreateGroupPlayerName, mCreateGroupName, mCreateGroupPassword;
    // String variable in popup join group
    private String mJoinGroupName, mJoinPlayerName, mJoinPassword;
    // ArrayList variable store player register id
    private ArrayList<String> mPlayerNameList;
    // HasMap variable store all group names and passwords
    private HashMap<String, String> mGroupNameAndPassword;
    // boolean variable to check toast message show or not
    private boolean isMessageShow = false;
    // boolean variable to check activity running or not
    public static boolean isActive;
    // boolean variable to check Background Music stop or play
    private boolean isBGMSound = false;
    // player register item
    private PlayerRegistrationItem mPlayRegisterItems;
    // ArrayList variable to store all player register
    public ArrayList<String> mUserRegisterNameList;
    // String variable to get Player id
    private String mPlayerRegisterId;
    // check player size in the group before join
    private int mPlayerSizes = 1;
    // check count by not running on the firebase loop function
    private int mCountTimes = 0;
    // String variable get Text from edit Text player id
    private String mEditIDName;
    // String variable get Player invite name to display
    private String mInviteName;
    // String variable get group invite
    private String mInviteGroupName;
    // string variable get Group password invite
    private String mInvitePassword;
    // String variable get invite time
    private String mInviteTime;
    // check when player click on invite list then allow firebase function running
    private boolean isMessageInvitedClicked = false;
    // check count by not running on the firebase loop function
    private int mCountTimeInvited = 0;
    // Manage player status play the game
    private ManagePlayerStatus mManagePlayerStatus;
    // check if player join
    private boolean isPlayerJoinActivateClick = false;
    private boolean isPlayerCreatedActivateClick = false;
    private boolean isConnectedInternet = false;
    private ArrayList<String> mMessageKey;
    private MessageItems mMessageItems;
    private boolean isCreateGroupActivityActivate = false;
    private AlertDialog mDialogConfirmPassword;
    private EditText mEditTextPassword;
    private Button mOkPassword, mCancelPassword;
    private HashMap<String, String> mMessageAppUpdate;
    private String mCheckMessageString = "";
    private int mShowPasswordEnabled;
    private int mShowPasswordDisabled;
    private boolean isHide = false;
    private String mManage = "";
    private String mLanguage = "";
    private boolean isRule = false;

    // Variable UI
    private AlertDialog mDialogJoin, mDialogCreateGroup;
    private Button mJoinButton, mCreateButton;
    private EditText mInputJoinPassword, mInputJoinGroupName;
    private Button mJoin;
    private EditText mInputCreateGroupName, mInputCreateGroupPassword;
    private Button mCreateGroup;
    private AlertDialog mProgressBar;
    private Toast mToastMessage;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private AlertDialog mDialogRegister;
    private EditText mInputIdRegister, mInputPasswordRegister;
    private Button mRegister;
    private TextView mTitle, mBody;
    private SharedPreferences.Editor mEditor;
    //private Intent mService;
    private AlertDialog mDialogEditPlayerProfile;
    private EditText mEditTextEditIdName;
    private Button mOkEditProfile, mCancelEditProfile;
    private ListView mListViewMessage;
    private InvitationArrayAdapter mInvitationArrayAdapter;
    private ArrayList<InviteMessageItems> mArrayListInviteMessageItems;
    private ArrayList<InviteMessageItems> mPreviewInviteMessageItems;
    private AlertDialog mDialogConfirmationInvitedFromFriend;
    private TextView mTextViewDisplayNameGroupName;
    private Button mOkConfirmationInvited, mCancelConfirmationInvited;
    private ImageView mImageViewInvitationHeader;
    private InternetProvider mInternetProvider;
    private Button mCancelJoinPopup;
    private Button mCancelCreatePopup;
    private Button mCancelRegisterPopup;
    private AlertDialog mDialogNewUpdate;
    private Button mButtonOkUpdate;
    private ImageView mImageViewEditProfile;
    private TextView mUpdateHeader;
    private TextView mUpdateDescription;
    private ImageView mImageViewTogglePassword;
    private ImageView mImageViewShowHidePassword;
    private AlertDialog mDialogSystemFixing;
    private AlertDialog mDialogSuspendGame;
    private Button mOkSuspendButton;
    private Button mOkSystemFixingButton;
    private Button mButtonCancelUpdate;
    private TextView mTextViewUnderMaintenanceDescription;

    // class variable
    private Intent mService;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        mLanguage = mSharedPreferences.getString(HomeActivity.DL, "en");

        Locale locale = new Locale(mLanguage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_create_group);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setTitle(getString(R.string.ToolBarJokerDrawOnlineGame));

        isCreateGroupActivityActivate = true;

        mInternetProvider = new InternetProvider(CreateGroupActivity.this);
        mPlayerNameList = new ArrayList<>();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);

        mJoinButton = findViewById(R.id.JoinGroup);
        mCreateButton = findViewById(R.id.CreateGroup);

        mJoinButton.setEnabled(false);
        mCreateButton.setEnabled(false);

        mShowPasswordDisabled = R.drawable.ic_password_visibility_off_24;

        mShowPasswordEnabled = R.drawable.ic_password_visibility_24;

        mListViewMessage = findViewById(R.id.ListViewMessageNotification);
        mImageViewInvitationHeader = findViewById(R.id.header_invitation);

        mImageViewInvitationHeader.setVisibility(View.INVISIBLE);

        mUserRegisterNameList = new ArrayList<>();

        mMessageKey = new ArrayList<>();

        mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");

        mGroupNameAndPassword = new HashMap<>();

        mArrayListInviteMessageItems = new ArrayList<>();

        mMessageAppUpdate = new HashMap<>();

        HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference();

        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference();

        //event function
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartDialogJoin();
            }
        });
        // Open to create group
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartDialogCreateGroup();
            }
        });
    }

    private void RetrievesNotificationMessage(){
        HomeActivity.mNotificationMessagesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseNotificationMessagePath +mPlayerRegisterId);
        HomeActivity.mNotificationMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isCreateGroupActivityActivate) {
                    mArrayListInviteMessageItems.clear();
                    mMessageKey.clear();
                    for (int i = 10; i > 0; i--) {
                        for (DataSnapshot snapshot : dataSnapshot.child("" + i).getChildren()) {
                            if (snapshot.getKey().equals(mPlayerRegisterId)) {
                                mMessageKey.add("" + i);
                                mInviteName = snapshot.child("n").getValue().toString();
                                mInviteGroupName = snapshot.child("g").getValue().toString();
                                mInvitePassword = snapshot.child("p").getValue().toString();
                                mInviteTime = snapshot.child("t").getValue().toString();

                                String mInviteDataTimeString = mInviteTime;
                                SimpleDateFormat mDataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                mDataFormat.setTimeZone(TimeZone.getDefault());
                                Date date = null;
                                try {
                                    date = mDataFormat.parse(mInviteDataTimeString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mDataFormat.setTimeZone(TimeZone.getDefault());
                                String mFormatDateTime = mDataFormat.format(date);

                                mArrayListInviteMessageItems.add(new InviteMessageItems(mInviteName, mInviteGroupName, mFormatDateTime, mInvitePassword));

                                mImageViewInvitationHeader.setVisibility(View.VISIBLE);

                                mPreviewInviteMessageItems = new ArrayList<>();
                                for (int p = 0; p < mArrayListInviteMessageItems.size(); p++) {
                                    mPreviewInviteMessageItems.add(new InviteMessageItems(mArrayListInviteMessageItems.get(p).getInviteName()+" "+ getString(R.string.CreateGroupActivityMessageInvitationListPlayOnlineJokerDraw), mArrayListInviteMessageItems.get(p).getInviteGroupName(),
                                            mArrayListInviteMessageItems.get(p).getInviteTime(), mArrayListInviteMessageItems.get(p).getInviteGroupPassword()));

                                    mInvitationArrayAdapter = new InvitationArrayAdapter(CreateGroupActivity.this, mPreviewInviteMessageItems) {
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View mView = super.getView(position, convertView, parent);
                                            if (position % 2 == 1) {
                                                mView.setBackgroundColor(getResources().getColor(R.color.colorList));
                                            } else {
                                                mView.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                                            }
                                            return mView;
                                        }
                                    };

                                    mInvitationArrayAdapter.notifyDataSetChanged();
                                    mListViewMessage.setAdapter(mInvitationArrayAdapter);

                                    mListViewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            InviteMessageItems items = (InviteMessageItems) mListViewMessage.getAdapter().getItem(position);
                                            OpenDialogConfirmationJoiningGroupInvitedFromFriend(position, items.getInviteGroupPassword());
                                        }
                                    });
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

    // create option menu to see player profile
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.online_joker_draw_menu_layout, menu);
        return true;
    }

    // get id from user click the menu player profile
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.player_profile:
                OpenEditPlayerProfile();
                return true;

            case android.R.id.home:
                Intent intent = new Intent(CreateGroupActivity.this, SelectGame.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void OpenDialogConfirmationJoiningGroupInvitedFromFriend(final int position, final String password){
        if(mDialogConfirmationInvitedFromFriend != null){
            mDialogConfirmationInvitedFromFriend.dismiss();
            mDialogConfirmationInvitedFromFriend = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.confirm_joining_group_invited_from_friend_layout, null);

        mTextViewDisplayNameGroupName = view.findViewById(R.id.display_player_group_name);
        mOkConfirmationInvited = view.findViewById(R.id.Ok_Invited_Confirm);
        mCancelConfirmationInvited = view.findViewById(R.id.Cancel_Invited_Confirm);

        mTextViewDisplayNameGroupName.setText(mArrayListInviteMessageItems.get(position).getInviteName()+" "+getString(R.string.CreateGroupActivityDialogConfirmJoiningGroupInviteJoiningGroup)+" "+mArrayListInviteMessageItems.get(position).getInviteGroupName()+" " +getString(R.string.CreateGroupActivityWouldYouToJoin));

        mOkConfirmationInvited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMessageInvitedClicked = true;
                ProgressBarDialog();
                ListPlayerInvited(mArrayListInviteMessageItems.get(position).getInviteGroupName(), password);
                mDialogConfirmationInvitedFromFriend.dismiss();
                HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference();
                if(mMessageKey.size() > 0) {
                    HomeActivity.mNotificationMessagesReference = HomeActivity.mGroupListsReference.child(HomeActivity.mTreeFirebaseNotificationMessagePath + mPlayerRegisterId + "/" + mMessageKey.get(position % mMessageKey.size()));
                    RemoveMessageInvited();
                }
                mPreviewInviteMessageItems.remove(position);
                mArrayListInviteMessageItems.remove(position);
                mInvitationArrayAdapter.notifyDataSetChanged();
            }
        });

        mCancelConfirmationInvited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogConfirmationInvitedFromFriend.dismiss();
            }
        });

        builder.setView(view);
        mDialogConfirmationInvitedFromFriend = builder.create();
        mDialogConfirmationInvitedFromFriend.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogConfirmationInvitedFromFriend.show();
        mDialogConfirmationInvitedFromFriend.setCancelable(false);
    }

    private void OpenEditPlayerProfile(){
        if(mDialogEditPlayerProfile != null){
            mDialogEditPlayerProfile.dismiss();
            mDialogEditPlayerProfile = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.player_edite_profile_layout, null);

        mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");
        mEditTextEditIdName = view.findViewById(R.id.edit_id_name);
        mOkEditProfile = view.findViewById(R.id.Ok_edit_Profile);
        mCancelEditProfile = view.findViewById(R.id.Cancel_edit_Profile);
        mImageViewEditProfile = view.findViewById(R.id.editProfileEnabled);

        mEditTextEditIdName.setText(mPlayerRegisterId+"");

        mEditTextEditIdName.setEnabled(false);
        mEditTextEditIdName.setTextColor(Color.WHITE);

        mOkEditProfile.setEnabled(false);

        mEditTextEditIdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals(mPlayerRegisterId)){
                    mOkEditProfile.setEnabled(false);
                }else {
                    mOkEditProfile.setEnabled(true);
                }
            }
        });

        mOkEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String mOldIdName = mSharedPreferences.getString(HomeActivity.PLAYERID,"");
            String mOldPassword = mSharedPreferences.getString(HomeActivity.PLAYERPASSWORD,"");

            mEditIDName = mEditTextEditIdName.getText().toString();
            if(mEditIDName.equals("") || mEditIDName.trim().equals("")) {
                if (isMessageShow) {
                    mToastMessage.cancel();
                    isMessageShow = false;
                }
                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityPleaseEnterId));
            }else if(!mOldIdName.equals(mEditIDName)){

                if(mUserRegisterNameList.contains(mEditIDName)){
                    if(isMessageShow){
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), mEditIDName+" "+ getString(R.string.already_exits));
                }else {
                    mDialogEditPlayerProfile.dismiss();
                    OpenDialogConfirmPasswordToChange(mEditIDName, mOldPassword);
                }
            }
            }
        });

        mCancelEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogEditPlayerProfile.dismiss();
                mEditTextEditIdName.requestFocus();
                //hideKeyBoardAuto();

            }
        });

        mImageViewEditProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                mEditTextEditIdName.setEnabled(true);
                mEditTextEditIdName.setBackground(getDrawable(R.drawable.edit_text_design));
                mEditTextEditIdName.setTextColor(Color.BLACK);
                mImageViewEditProfile.setEnabled(false);
                mEditTextEditIdName.requestFocus();
                mEditTextEditIdName.setSelection(mEditTextEditIdName.getText().length());
                showKeyboard(mEditTextEditIdName);
                mImageViewEditProfile.setVisibility(View.INVISIBLE);
            }
        });

        builder.setView(view);
        mDialogEditPlayerProfile = builder.create();
        mDialogEditPlayerProfile.show();
        mDialogEditPlayerProfile.setCancelable(false);
        mDialogEditPlayerProfile.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mEditTextEditIdName.clearFocus();
            }
        });
    }

    private void OpenDialogConfirmPasswordToChange(final String id, final String password){
        if(mDialogConfirmPassword != null){
            mDialogConfirmPassword.dismiss();
            mDialogConfirmPassword = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.confirm_password_change_id_layout, null);

        mImageViewShowHidePassword = view.findViewById(R.id.showHidePassword);
        mEditTextPassword = view.findViewById(R.id.password_confirm);
        mOkPassword = view.findViewById(R.id.Ok_Confirm_Id);
        mCancelPassword = view.findViewById(R.id.Cancel_Confirm_Id);

        mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        isHide = true;

        mImageViewShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextPassword.getText().length() > 0) {
                    if (isHide) {
                        mImageViewShowHidePassword.setBackgroundResource(mShowPasswordEnabled);
                        mEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    } else {
                        mImageViewShowHidePassword.setBackgroundResource(mShowPasswordDisabled);
                        mEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }
                }
            }
        });

        mOkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = mEditTextPassword.getText().toString();
                if(password.equals(pass)){
                    AddNewIdNameAndPasswordToFirebase(id, password);
                    RemoveOldIdNameAndPasswordFromFirebase(mPlayerRegisterId);
                    removeOldIdNameFromOnlineStatus(mPlayerRegisterId);
                    mDialogConfirmPassword.dismiss();
                    OpenEditPlayerProfile();
                }else{
                 if(isMessageShow){
                     mToastMessage.cancel();
                     isMessageShow = false;
                 }
                 CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityPasswordNotCorrect));
                }
            }
        });


        mCancelPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogConfirmPassword.dismiss();
                OpenEditPlayerProfile();
            }
        });

        builder.setView(view);
        mDialogConfirmPassword = builder.create();
        mDialogConfirmPassword.show();
        mDialogConfirmPassword.setCancelable(false);
        mDialogConfirmPassword.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mEditTextPassword.clearFocus();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditTextPassword.requestFocus();
                showKeyboard(mEditTextPassword);
            }
        },500);
    }

    // create progress bar dialog
    private void ProgressBarDialog(){
        if(mProgressBar != null){
            mProgressBar.dismiss();
            mProgressBar = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.progress_bar_dialog, null);
        builder.setView(view);
        mProgressBar = builder.create();
        mProgressBar.show();
        mProgressBar.setCancelable(false);
        mProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    // retreives player register
    private  void UserRegistrationRetreive(){
        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference();
        mQueryPlayerRegister = HomeActivity.mPlayerRegistrationInformation.child(HomeActivity.mTreeFirebasePlayerRegisterPath);
        mQueryPlayerRegister.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isCreateGroupActivityActivate) {
                    mUserRegisterNameList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mUserRegisterNameList.add(snapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // create dialog to take the player register
    private void PlayerRegisterDialog(){
        if(mDialogRegister != null){
            mDialogRegister.dismiss();
            mDialogRegister = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.user_registration_layout, null);

        mInputIdRegister = view.findViewById(R.id.RegisterId);
        mInputPasswordRegister = view.findViewById(R.id.RegisterPassword);
        mCancelRegisterPopup = view.findViewById(R.id.CancelRegister);
        mImageViewTogglePassword = view.findViewById(R.id.togglePassword);

        mInputPasswordRegister.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mRegister = view.findViewById(R.id.Register);
        isHide = true;

        mImageViewTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInputPasswordRegister.getText().length() > 0) {
                    if (isHide) {
                        mImageViewTogglePassword.setBackgroundResource(mShowPasswordEnabled);
                        mInputPasswordRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        isHide = false;
                    } else {
                        mImageViewTogglePassword.setBackgroundResource(mShowPasswordDisabled);
                        mInputPasswordRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        isHide = true;
                    }
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Id = mInputIdRegister.getText().toString();
                String Pass = mInputPasswordRegister.getText().toString();

                if(Id.trim().equals("") || Id.equals("")) {
                    if (isMessageShow) {
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityPleaseEnterId));
                }else if(Pass.trim().equals("") || Pass.equals(""))
                {
                    if (isMessageShow) {
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityPleaseEnterPassword));
                }else{
                    if(mUserRegisterNameList.contains(Id)){
                        if(isMessageShow){
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header),Id+" "+ getString(R.string.already_exits));
                    } else {
                        ProgressBarDialog();
                        SaveIdUserRegistrationToFirebase(Id,Pass);
                        mRegister.setEnabled(false);
                        mEditor = mSharedPreferences.edit();
                        mEditor.putString(HomeActivity.PLAYERID,Id);
                        mEditor.putString(HomeActivity.PLAYERPASSWORD,Pass);
                        mEditor.commit();
                        mCreateButton.setVisibility(View.VISIBLE);
                        mJoinButton.setVisibility(View.VISIBLE);
                        mCreateButton.setEnabled(true);
                        mJoinButton.setEnabled(true);
                    }
                }
            }
        });

        mCancelRegisterPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogRegister.dismiss();
                Intent intent = new Intent(CreateGroupActivity.this, SelectGame.class);
                startActivity(intent);
            }
        });

        builder.setView(view);
        mDialogRegister = builder.create();
        mDialogRegister.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialogRegister.show();
        mDialogRegister.setCancelable(false);
        mDialogRegister.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mInputIdRegister.clearFocus();
                mInputPasswordRegister.clearFocus();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputIdRegister.requestFocus();
                showKeyboard(mInputIdRegister);
            }
        },500);
    }
    // save id and password of the user to firebase
    private  void SaveIdUserRegistrationToFirebase(String id, String password){
        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebasePlayerRegisterPath+id);
        mPlayRegisterItems = new PlayerRegistrationItem(id,password);
        HomeActivity.mPlayerRegistrationInformation.setValue(mPlayRegisterItems);
        if(mDialogRegister != null){
            mDialogRegister.dismiss();
        }
        if(mProgressBar != null) {
            mProgressBar.dismiss();
        }
        mManagePlayerStatus = new ManagePlayerStatus(CreateGroupActivity.this);
        mManagePlayerStatus.setStatusOnline(id);
        mService = new Intent(CreateGroupActivity.this, MyFirebaseInstanceIDService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(new Intent(CreateGroupActivity.this, OreoService.class));
        }else{
            startService(mService);
        }
    }

    // create custom toast messages
    private void CustomToastMessage(String string_title, String string_body){
        isMessageShow = true;
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.message_layout, (ViewGroup) findViewById(R.id.root_message));
        mTitle = layout.findViewById(R.id.message_title);
        mBody = layout.findViewById(R.id.message_body);
        mTitle.setText(string_title);
        mBody.setText(string_body);
        mToastMessage = new Toast(getApplicationContext());
        mToastMessage.setGravity(Gravity.BOTTOM,0,0);
        mToastMessage.setDuration(Toast.LENGTH_LONG);
        mToastMessage.setView(layout);
        mToastMessage.show();
    }

    // create join dialog to take player to join the group
    private void StartDialogJoin(){
        if(mDialogJoin != null){
            mDialogJoin.dismiss();
            mDialogJoin = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_join_layout, null);

        mInternetProvider = new InternetProvider(CreateGroupActivity.this);
        isConnectedInternet = mInternetProvider.isConnected();

        mInputJoinPassword = view.findViewById(R.id.group_join_password);
        mJoin = view.findViewById(R.id.join_popup);
        mInputJoinGroupName = view.findViewById(R.id.group_name_join);
        mCancelJoinPopup = view.findViewById(R.id.Cancel_Join_Popup);
        mInputJoinPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hideKeyBoardAuto();
                mJoinPassword = mInputJoinPassword.getText().toString();
                mJoinPlayerName = mPlayerRegisterId;
                mJoinGroupName = mInputJoinGroupName.getText().toString();
                mInputJoinPassword.setText("");
                mInputJoinGroupName.setText("");
                if (!mJoinPassword.equals("") && !mJoinPlayerName.equals("") && !mJoinGroupName.equals("")) {
                    if (mGroupNameAndPassword.containsKey(mJoinGroupName) && mGroupNameAndPassword.containsValue(mJoinPassword)) {
                        // check if game started;
                        if(isConnectedInternet) {

                            boolean isMatch = false;
                            int mPosition = 0;
                            for(int position = 0; position < mArrayListInviteMessageItems.size(); position++){
                                if(mArrayListInviteMessageItems.get(position).getInviteGroupName().equals(mJoinGroupName)){
                                    isMatch = true;
                                    mPosition = position;
                                    break;
                                }
                            }

                            if(isMatch){
                                String mPass = mArrayListInviteMessageItems.get(mPosition).getInviteGroupPassword();
                                isMessageInvitedClicked = true;
                                ProgressBarDialog();
                                ListPlayerInvited(mJoinGroupName, mPass);
                                HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference();
                                if(mMessageKey.size() > 0) {
                                    HomeActivity.mNotificationMessagesReference = HomeActivity.mGroupListsReference.child(HomeActivity.mTreeFirebaseNotificationMessagePath + mPlayerRegisterId + "/" + mMessageKey.get(mPosition % mMessageKey.size()));
                                    RemoveMessageInvited();
                                }
                            }else {
                                ProgressBarDialog();
                                isPlayerJoinActivateClick = true;
                                ListPlayer(mJoinGroupName);
                            }
                        }else{
                            if(isMessageShow){
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.NetWorkConnectionError));
                        }
                    } else {
                        if(isMessageShow){
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header),getString(R.string.CreateGroupActivity_group_name_password_not_correct));
                    }
                } else {
                    if(isMessageShow){
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header),getString(R.string.FieldEmpty));
                }
            }
        });


        mCancelJoinPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mDialogJoin.dismiss();
            }
        });

        builder.setView(view);
        mDialogJoin = builder.create();
        mDialogJoin.show();
        mDialogJoin.setCancelable(false);
        mDialogJoin.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mInputJoinGroupName.clearFocus();
                mInputJoinPassword.clearFocus();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputJoinGroupName.requestFocus();
                showKeyboard(mInputJoinGroupName);
            }
        }, 500);
    }

    // take the player to go to lobby room
    private void addEventJoinListener(){
        HomeActivity.mPlayerJoinReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isPlayerJoinActivateClick) {
                    if (!mJoinPassword.equals("") && !mJoinPlayerName.equals("") && !mJoinGroupName.equals("")) {
                        Intent intent = new Intent(CreateGroupActivity.this, Waiting_Activity.class);
                        intent.putExtra(HomeActivity.GROUPNAME, mJoinGroupName);
                        intent.putExtra(HomeActivity.GROUPPASSWORD, mJoinPassword);
                        isPlayerJoinActivateClick = false;
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addEventJoinFromMessageInvited(final String group_name, final String password){
        if(isMessageInvitedClicked) {
            mMessageItems = new MessageItems(mPlayerRegisterId, "1");
            HomeActivity.mMessageReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + group_name + HomeActivity.mTreeMassage);
            HomeActivity.mMessageReference.setValue(mMessageItems);
            Intent intent = new Intent(CreateGroupActivity.this, Waiting_Activity.class);
            intent.putExtra(HomeActivity.GROUPNAME, group_name);
            intent.putExtra(HomeActivity.GROUPPASSWORD, password);
            startActivity(intent);
            isMessageInvitedClicked = false;
        }
    }

    private void RemoveMessageInvited(){
        HomeActivity.mNotificationMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isCreateGroupActivityActivate) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(mPlayerRegisterId)) {
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

    // create group for the player create a group
    private void StartDialogCreateGroup(){
        if(mDialogCreateGroup != null){
            mDialogCreateGroup.dismiss();
            mDialogCreateGroup = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_create_group_layout, null);

        mInternetProvider = new InternetProvider(CreateGroupActivity.this);
        isConnectedInternet = mInternetProvider.isConnected();

        mInputCreateGroupName = view.findViewById(R.id.group_name);
        mCreateGroup = view.findViewById(R.id.creategroup_popup);
        mInputCreateGroupPassword = view.findViewById(R.id.group_password);
        mCancelCreatePopup = view.findViewById(R.id.Cancel_CreateGroup_Popup);

        mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");

        mInputCreateGroupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //hideKeyBoardAuto();

                mCreateGroupName = mInputCreateGroupName.getText().toString();
                mInputCreateGroupName.setText("");
                mCreateGroupPlayerName = mPlayerRegisterId;
                mCreateGroupPassword = mInputCreateGroupPassword.getText().toString();
                mInputCreateGroupPassword.setText("");
                if(!mCreateGroupName.equals("") && !mCreateGroupPlayerName.equals("")&& !mCreateGroupPassword.equals("")){
                    if (mGroupNameAndPassword.containsKey(mCreateGroupName)) {
                        if(isMessageShow){
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header),mCreateGroupName +" "+getString(R.string.already_exits));
                    }else {
                        if(isConnectedInternet) {
                            ProgressBarDialog();
                            HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mCreateGroupName + "/" + mPlayerSizes);
                            HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.setValue(mCreateGroupPlayerName);

                            HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference();
                            HomeActivity.mGroupListsReference.child(HomeActivity.mTreeFirebaseGroupListsPath).child(mCreateGroupName).setValue(mCreateGroupPassword);
                            //stopService(mService);
                            isPlayerCreatedActivateClick = true;
                            addEventCreateGroupListener();
                            //hideKeyBoard(mInputCreateGroupPassword);
                        }else{
                            if(isMessageShow){
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.NetWorkConnectionError));
                        }
                    }
                }else{
                    if(isMessageShow){
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.FieldEmpty));
                }
            }
        });

        mCancelCreatePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCreateGroup.dismiss();
            }
        });

        builder.setView(view);
        mDialogCreateGroup = builder.create();
        mDialogCreateGroup.show();
        mDialogCreateGroup.setCancelable(false);
        mDialogCreateGroup.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mInputCreateGroupName.clearFocus();
                mInputCreateGroupPassword.clearFocus();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputCreateGroupName.requestFocus();
                showKeyboard(mInputCreateGroupName);
            }
        }, 500);
    }
    // create group event
    private void addEventCreateGroupListener(){
        if (isPlayerCreatedActivateClick) {
            if(mDialogCreateGroup != null){
                mDialogCreateGroup.dismiss();
            }
            if(mProgressBar != null) {
                mProgressBar.dismiss();
            }
            HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mCreateGroupName + HomeActivity.mTreeAdmin);
            HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(mPlayerRegisterId);

            mMessageItems = new MessageItems(mPlayerRegisterId, "0");
            HomeActivity.mMessageReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mCreateGroupName + HomeActivity.mTreeMassage);
            HomeActivity.mMessageReference.setValue(mMessageItems);
            isPlayerCreatedActivateClick = false;
            Intent intent = new Intent(CreateGroupActivity.this, Waiting_Activity.class);
            intent.putExtra(HomeActivity.GROUPNAME, mCreateGroupName);
            intent.putExtra(HomeActivity.GROUPPASSWORD, mCreateGroupPassword);
            startActivity(intent);
        }
    }
    // retrieves Group to prepare
    private void GroupListId(){
        HomeActivity.mGroupListsReference = FirebaseDatabase.getInstance().getReference();
        mQueryGroupList = HomeActivity.mGroupListsReference.child(HomeActivity.mTreeFirebaseGroupListsPath);
        mQueryGroupList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isCreateGroupActivityActivate) {
                    mGroupNameAndPassword.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mGroupNameAndPassword.put(snapshot.getKey(), snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ListPlayerInvited(final String GroupName, String password){
        final String gname = GroupName;
        final String pass = password;
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath+gname);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isMessageInvitedClicked)
                {
                    mCountTimes++;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("message")) {
                            mPlayerNameList.add(snapshot.getKey());
                        }else{
                            mCheckMessageString = snapshot.getKey();
                        }
                    }

                    if (mCountTimes == 1 && mPlayerNameList.size() > 0 && mPlayerNameList.size() <= HomeActivity.mLimitPlayerInGroupNumber ) {
                        mPlayerSizes = Integer.parseInt(mPlayerNameList.get(mPlayerNameList.size() - 1)) + 1;
                        if(mProgressBar != null) {
                            mProgressBar.dismiss();
                        }
                        HomeActivity.mPlayerJoinReference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + gname + "/" + mPlayerSizes);
                        HomeActivity.mPlayerJoinReference.setValue(mPlayerRegisterId);
                        addEventJoinFromMessageInvited(gname, pass);

                    } else if(mCountTimes == 1 && mPlayerNameList.size() > HomeActivity.mLimitPlayerInGroupNumber){
                        if (isMessageShow) {
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceiveMessageActivityCouldNotJoinGroupNow));
                        isMessageInvitedClicked = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(mProgressBar != null) {
                                    mProgressBar.dismiss();
                                }
                                RetrievesNotificationMessage();
                            }
                        }, 300);
                    } else {
                        if (mCheckMessageString.equals("message") && mPlayerNameList.size() <= 0) {
                            mPlayerSizes = 1;
                            if(mProgressBar != null) {
                                mProgressBar.dismiss();
                            }
                            HomeActivity.mPlayerJoinReference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + gname + "/" + mPlayerSizes);
                            HomeActivity.mPlayerJoinReference.setValue(mPlayerRegisterId);
                            addEventJoinFromMessageInvited(gname, pass);
                        } else {
                            if (isMessageShow) {
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityGroupNameAndPasswordNotMatchAnyGroup));
                            isMessageInvitedClicked = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(mProgressBar != null) {
                                        mProgressBar.dismiss();
                                    }
                                    RetrievesNotificationMessage();
                                }
                            }, 300);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ListPlayer(final String group_name){
        final String gname = group_name;
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath+gname);
        HomeActivity.mGroupInfo_PlayersLobby_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isPlayerJoinActivateClick) {
                    mCountTimeInvited++;
                    mPlayerNameList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("message")) {
                            mPlayerNameList.add(snapshot.getKey());
                        }else{
                            mCheckMessageString = snapshot.getKey();
                        }
                    }

                    if (mCountTimeInvited == 1 && mPlayerNameList.size() > 0 && mPlayerNameList.size() <= HomeActivity.mLimitPlayerInGroupNumber) {
                        mPlayerSizes = Integer.parseInt(mPlayerNameList.get(mPlayerNameList.size() - 1)) + 1;
                        if(mProgressBar != null) {
                            mProgressBar.dismiss();
                        }
                        HomeActivity.mPlayerJoinReference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mJoinGroupName + "/" + mPlayerSizes);
                        HomeActivity.mPlayerJoinReference.setValue(mJoinPlayerName);
                        addEventJoinListener();
                    }else if(mCountTimeInvited == 1 && mPlayerNameList.size() >= HomeActivity.mLimitPlayerInGroupNumber){
                        if (isMessageShow) {
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        CustomToastMessage(getString(R.string.Waring_header), getString(R.string.ReceiveMessageActivityCouldNotJoinGroupNow));
                        if(mProgressBar != null) {
                            mProgressBar.dismiss();
                        }
                    }else{
                        if(mCheckMessageString.equals("message") && mPlayerNameList.size() <= 0){
                            mPlayerSizes = 1;
                            if(mProgressBar != null) {
                                mProgressBar.dismiss();
                            }
                            HomeActivity.mPlayerJoinReference = FirebaseDatabase.getInstance().getReference().child(HomeActivity.mTreeFirebaseGroupInfoPlayerLobbyPath + mJoinGroupName + "/" + mPlayerSizes);
                            HomeActivity.mPlayerJoinReference.setValue(mJoinPlayerName);
                            addEventJoinListener();
                        }else {
                            if (isMessageShow) {
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.CreateGroupActivityGroupNameAndPasswordNotMatchAnyGroup));
                            if(mProgressBar != null) {
                                mProgressBar.dismiss();
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
    @Override
    protected void onStart() {
        mManagePlayerStatus = new ManagePlayerStatus(CreateGroupActivity.this);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);
        mService = new Intent(CreateGroupActivity.this, MyFirebaseInstanceIDService.class);

        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(!mPlayerRegisterId.equals("")) {
            mManagePlayerStatus.setStatusOnline(mPlayerRegisterId);
            mManagePlayerStatus.removeStatusLobby(mPlayerRegisterId);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(new Intent(CreateGroupActivity.this, OreoService.class));
        }else{
            startService(mService);
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);

        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.setLooping(true);
                HomeActivity.mBackgroundMusics.start();
            }
        }

        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.setLooping(true);
                    HomeActivity.mBackgroundMusics.start();
                }
            }
        }
        isCreateGroupActivityActivate = true;
        RetrievesNotificationMessage();
        GroupListId();
        UserRegistrationRetreive();
        isActive = true;
        mInternetProvider = new InternetProvider(CreateGroupActivity.this);
        isConnectedInternet = mInternetProvider.isConnected();
        if (isConnectedInternet) {
            ManageOnlineJokerDraw();
        } else {
            mJoinButton.setEnabled(true);
            mCreateButton.setEnabled(true);
        }

        isRule = mSharedPreferences.getBoolean(SelectGame.RULE, true);
        if(isRule == false){
            if(isMessageShow){
                mToastMessage.cancel();
                isMessageShow = false;
            }
            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.Rule_body));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean(SelectGame.RULE, true);
                    mEditor.commit();
                }
            }, 2000);
        }

        super.onResume();
    }

    private void ManageOnlineJokerDraw(){
        HomeActivity.mManageOnlineJokerDrawReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeManageOnlineJokerDraw);
        HomeActivity.mManageOnlineJokerDrawReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isCreateGroupActivityActivate){
                    mManage = dataSnapshot.getValue().toString();
                    int status = Integer.parseInt(mManage);
                    onOpenManageMode(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onOpenManageMode(int status){
        switch (status){
            case HomeActivity.mManageNormalStatus:
                // register and normal mode
                if(mPlayerRegisterId.equals("")) {
                    mCreateButton.setVisibility(View.INVISIBLE);
                    mJoinButton.setVisibility(View.INVISIBLE);
                    PlayerRegisterDialog();
                }
                FirebaseVersionChange();
                break;

            case HomeActivity.mManageBanNewPlayerStatus:
                // can not register new user
                // Create dialog System is fixing now Can not play or register online
                if(mPlayerRegisterId.equals("")) {
                    UnderMaintenanceTime(false);
                }else{
                    mJoinButton.setEnabled(true);
                    mCreateButton.setEnabled(true);
                }

                break;

            case HomeActivity.mManageOnMaintenanceNotAlwaysStatus:
                // dialog fixing status
                UnderMaintenanceTime(true);
                break;

            case HomeActivity.mManageOutOfServiceStatus:
                // can not play or do not things
                // Create dialog message block not to play this game
                OpenDialogSuspendGame();
                mCreateButton.setEnabled(false);
                mJoinButton.setEnabled(false);
                break;
        }
    }

    private void UnderMaintenanceTime(final boolean isTwo){
        HomeActivity.mUnderMaintenanceTimeReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeUnderMaintenance);
        HomeActivity.mUnderMaintenanceTimeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Under_Maintenance = snapshot.getValue().toString();
                if(!Under_Maintenance.equals("0") && !Under_Maintenance.equals("")){
                    OpenDialogSystemFixing(Under_Maintenance, isTwo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void OpenDialogSystemFixing(String underMaintenance, boolean isTwo){
        if(mDialogSystemFixing != null){
            mDialogSystemFixing.dismiss();
            mDialogSystemFixing = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_system_fixing_layout, null);
        mTextViewUnderMaintenanceDescription = view.findViewById(R.id.textView_UnderMaintenance);
        mOkSystemFixingButton = view.findViewById(R.id.system_fixing_ok_button);

        if(isTwo){
            mTextViewUnderMaintenanceDescription.setText(getString(R.string.description_underMaintenance_2) +"\n"+underMaintenance);
        }else{
            mTextViewUnderMaintenanceDescription.setText(getString(R.string.description_underMaintenance) +"\n"+underMaintenance);
        }

        mOkSystemFixingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSystemFixing.dismiss();
                Intent intent = new Intent(CreateGroupActivity.this, SelectGame.class);
                startActivity(intent);
            }
        });

        builder.setView(view);
        mDialogSystemFixing = builder.create();
        mDialogSystemFixing.show();
        mDialogSystemFixing.setCancelable(false);
    }

    private void OpenDialogSuspendGame(){
        if(mDialogSuspendGame != null){
            mDialogSuspendGame.dismiss();
            mDialogSuspendGame = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_block_game, null);

        mOkSuspendButton = view.findViewById(R.id.suspend_ok_button);

        mOkSuspendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSuspendGame.dismiss();
                Intent intent = new Intent(CreateGroupActivity.this, SelectGame.class);
                startActivity(intent);
            }
        });

        builder.setView(view);
        mDialogSuspendGame = builder.create();
        mDialogSuspendGame.show();
        mDialogSuspendGame.setCancelable(false);
    }

    private void FirebaseVersionChange(){
        HomeActivity.mDatabaseVersionReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseVersion);
        HomeActivity.mDatabaseVersionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String databaseVersion = dataSnapshot.getValue().toString();
                int databaseConvert = Integer.parseInt(databaseVersion);
                if(HomeActivity.mDatabaseVersion < databaseConvert){
                    retrievesUpdateMessage(databaseConvert);
                    if(mDialogRegister != null){
                        mDialogRegister.dismiss();
                        mDialogRegister = null;
                    }
                }else {
                    mJoinButton.setEnabled(true);
                    mCreateButton.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void retrievesUpdateMessage(final int databaseVersion){
        HomeActivity.mAppUpdateStringReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeUpdateMessage);
        HomeActivity.mAppUpdateStringReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessageAppUpdate.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mMessageAppUpdate.put(snapshot.getKey(), snapshot.getValue().toString());
                }
                String header = mMessageAppUpdate.get(HomeActivity.mTreeStringHeader);
                String description = mMessageAppUpdate.get(HomeActivity.mTreeStringDescription);

                if(!header.equals("0") && !description.equals("0") && HomeActivity.mDatabaseVersion < databaseVersion){
                    OpenDialogUpdateVersions(header, description);
                }else{
                    mJoinButton.setEnabled(true);
                    mCreateButton.setEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenDialogUpdateVersions(String header, String description){
        if(mDialogNewUpdate != null){
            mDialogNewUpdate.dismiss();
            mDialogNewUpdate = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_update_layout, null);

        mButtonOkUpdate = view.findViewById(R.id.OkUpdateVersion);
        mUpdateHeader = view.findViewById(R.id.textView_UpdateHeader);
        mUpdateDescription = view.findViewById(R.id.textView_UpdateDescription);
        mButtonCancelUpdate = view.findViewById(R.id.CancelUpdateVersion);

        mUpdateHeader.setText(R.string.DatabaseNeedToUpdate);
        mLanguage = mSharedPreferences.getString(HomeActivity.DL, "en");
        if(mLanguage.equals("en")){
            mUpdateDescription.setText("Would you like to update new version "+description+ " or not ?");
        }else {
            mUpdateDescription.setText("ທ່ານຕ້ອງການອັບເດດເປັນເວີເຊີນໃໝ່ " + description + " ຫຼື ບໍ່ ?");
        }
        mButtonOkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalaxyStore = new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=com.ictlao.partydraw"));
                startActivity(GalaxyStore);
            }
        });

        mButtonCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateGroupActivity.this, SelectGame.class);
                startActivity(intent);
            }
        });

        builder.setView(view);
        mDialogNewUpdate = builder.create();
        mDialogNewUpdate.show();
        mDialogNewUpdate.setCancelable(false);
    }

    @Override
    protected void onStop() {
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);
        if(HomeActivity.mBackgroundMusics != null && !(HomeActivity.isActive || SelectGame.isActive || UserPlayerSwapGame.isActive || User_Player_Lucky_Game.isActive || JokerGame.isActive || LuckyGame.isActive || SwapGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }

        super.onStop();
    }

    @Override
    protected void onPause() {
        isCreateGroupActivityActivate = false;
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);
        Context context = getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics.stop();
                    HomeActivity.mBackgroundMusics.release();
                    HomeActivity.mBackgroundMusics = null;
                }
            } else {
                // go to another activity
            }
        }

        if (!mPowerManager.isScreenOn()) {
            if (HomeActivity.mBackgroundMusics != null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics.stop();
                    HomeActivity.mBackgroundMusics.release();
                    HomeActivity.mBackgroundMusics = null;
                }
            }
        }

        if(mDialogJoin != null){
            mDialogJoin.dismiss();
            mDialogJoin = null;
        }

        if(mDialogCreateGroup != null){
            mDialogCreateGroup.dismiss();
            mDialogCreateGroup = null;
        }

        if(mDialogConfirmationInvitedFromFriend != null){
            mDialogConfirmationInvitedFromFriend.dismiss();
            mDialogConfirmationInvitedFromFriend = null;
        }
        hideKeyBoardAuto();
        isActive = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        Intent mSelectGame = new Intent(CreateGroupActivity.this, SelectGame.class);
        startActivity(mSelectGame);
    }

    private void ClearMemoryData(){
        mCreateGroupPlayerName = "";
        mCreateGroupName = "";
        mCreateGroupPassword = "";
        mJoinGroupName = "";
        mJoinPlayerName = "";
        mJoinPassword = "";
        mPlayerNameList.clear();
        mGroupNameAndPassword.clear();
        isMessageShow = false;
        isBGMSound = false;
        mPlayRegisterItems = null;
        mUserRegisterNameList.clear();
        mPlayerRegisterId = "";
        mPlayerSizes = 1;
        mCountTimes = 0;
        isPlayerCreatedActivateClick = false;
        isPlayerJoinActivateClick = false;
    }

    private void RemoveOldIdNameAndPasswordFromFirebase(final String oldIdName){
        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebasePlayerRegisterPath+oldIdName);
        HomeActivity.mPlayerRegistrationInformation.orderByChild(oldIdName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddNewIdNameAndPasswordToFirebase(String newIdName, String password){
        mPlayRegisterItems = new PlayerRegistrationItem(newIdName, password);
        HomeActivity.mPlayerRegistrationInformation = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebasePlayerRegisterPath+newIdName);
        HomeActivity.mPlayerRegistrationInformation.setValue(mPlayRegisterItems);

        mEditor = mSharedPreferences.edit();
        mEditor.putString(HomeActivity.PLAYERID, newIdName);
        mEditor.putString(HomeActivity.PLAYERPASSWORD, password);
        mEditor.commit();
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        HomeActivity.mStatus_Online_Reference.child(newIdName).setValue(newIdName);
    }

    private void removeOldIdNameFromOnlineStatus(final String oldIdName){
        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);
        HomeActivity.mStatus_Online_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals(oldIdName))
                    {
                        snapshot.getRef().removeValue();
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
        if(this.isFinishing()){
            ClearMemoryData();
        }
        super.onDestroy();
    }

    public void showKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) CreateGroupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private void hideKeyBoardAuto(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}