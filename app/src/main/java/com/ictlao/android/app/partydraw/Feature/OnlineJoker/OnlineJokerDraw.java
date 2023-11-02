package com.ictlao.android.app.partydraw.Feature.OnlineJoker;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.android.app.partydraw.Core.ArrayAdapterInfoJokerDraw;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.Models.InfoJokerDrawItems;
import com.ictlao.android.app.partydraw.Core.Models.PlayerEventItem;
import com.ictlao.android.app.partydraw.Core.Models.SettingRowAndColumnItems;
import com.ictlao.android.app.partydraw.Core.OnlineJokerDrawHelper;
import com.ictlao.android.app.partydraw.Core.WaitingActivityHelper;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class OnlineJokerDraw extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    // Memory data variable
    private Random mRandom;  // set random variable ເພື່ອ ສຸ່ມເອົາຕົວເລກ.
    public static int mResultNumber = 0, mCardNumber = 1, mCardNumberDisabled = 1;  // set number ຂອງປູ່ມ
    private int NUM_ROW = 6;  // ຕັ້ງຄ່າຈໍານວນຂອງແຖວ
    private int NUM_COL = 3; // ຕັ້ງຄ່າຈໍານວນຂອງຖັນ
    private int mFirebaseRow = 0;
    private int mFirebaseCol = 0;
    public static boolean isActive;   // set boolean make it static for stop and start BGM
    private SoundPool mCatSoundPool, mDogSoundPool;
    private int mCatSoundId, mDogSoundId;
    private int mDogPosition;
    private int mOneTimeDoing = 1;
    private String mGroupName = "";
    private ArrayList<Integer> mArrayListValueCardNumber;
    public static ArrayList<String> mAllPlayerLists;
    private ArrayList<Integer> mArrayListCheckContainNumber;
    public int num = 0;
    private PlayerEventItem mPlayerEventItems;
    private boolean isEffectSound;
    private boolean isBGMSound;
    private SharedPreferences.Editor mEditor;
    private ActivityManager mActivityManager;
    private PowerManager mPowerManager;
    private String mAdmin = "";
    private int mTurnIndex = 0;
    private Handler mHandlerProgress;
    private boolean isMessageShow = false;
    private int mCountWait = 0;
    private int mTimes = 0;
    private SettingRowAndColumnItems mSettingItems;
    private int mSelectedNumberPickerRow;
    private int mSelectedNumberPickerColumn;
    private boolean isOnlineJokerDrawActive = false;
    public static CountDownTimer mCountDownTimer;
    private ArrayList<String> mPlayerListPlayingGame;
    private boolean isGetAllPlayerPlayingGame = false;
    //private String mFirebaseNextName = "";
    //private boolean isPlayerLeft = false;
    private int mStartState = 0;
    private boolean isStatePause = false;
    private boolean isCountDownFinish = false;
    private ArrayAdapterInfoJokerDraw mArrayAdapterListJokerDraw;
    private String mPlayerName_DrawTurn = "";
    private boolean isInfoDialog = false;
    private boolean isPressDisabled = false;

    // UI variable
    private Button mCardButtonDisabled;
    private AlertDialog mDialogDogFlipCard;
    private TableLayout mTableLayout;  // set table layout ເພື່ອ ໃຫ້ການສະແດງຜົນຂອງ Dynamic button ເປັນໃນຮຸບແບບຂອງຕາຕະລາງ
    private GifImageView mDogFlipCard;
    private TextView mJokerText;
    private AlertDialog mProgressBarDialog = null;
    private Toast mToastMessage;
    private TextView mTitle, mBody;
    private Button mCardButton;
    private Button mRepeatAnimationFlipOnlineJokerDraw;
    private AlertDialog mDialogSettings;
    private RadioButton mRadioButtonNine, mRadioButtonTwenty, mRadioButtonFifty;
    private Button mCustomizeCol_RowButton, mOkSetting, mCancelSetting;
    private AlertDialog mDialogCustomize;
    private Button mOkCustomizeSettings, mCancelCustomizeSettings;
    private NumberPicker mNumberPickerRow, mNumberPickerColumn;
    private AlertDialog mDialogBackPressPopup;
    private TextView mTextViewBackPressPopupBody;
    private Button mOkBackPressPopup, mCancelBackPressPopup;
    private TextView mTextViewCountDownNumber, mTextViewCountDownText;
    private AlertDialog mDialogOutOfTime;
    private Button mOkExitPlaying;
    private AlertDialog mDialogInfoOnlineJokerDraw;
    private ListView mListViewInfoJokerDraw;
    private Button mExitInfoJokerDraw;
    private TextView mAdminJokerDrawInfo;
    private TextView mTextViewPlayerOnlineJokerDrawSize;

    // class variable
    private OnlineJokerDrawHelper mOnlineJokerDrawHelper;
    private WaitingActivityHelper mWaitingActivityHelper;

    // method ນີ້ແມ່ນ ເອີ້ນມາໃຊ້ເພື່ອໃຫ້ Class ນີ້ປ່ຽນພາສາ.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_joker_draw);
        getSupportActionBar().setTitle(getString(R.string.ToolBarJokerDrawOnlineGame)); // ຕັ້ງຊື່ໃຫ້ ສ່ວນ ຂອງ toolbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCatSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        mCatSoundId = mCatSoundPool.load(OnlineJokerDraw.this, R.raw.cat_sound,1);
        mDogSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mDogSoundId = mDogSoundPool.load(OnlineJokerDraw.this, R.raw.dog_sound,1);

        mTableLayout = findViewById(R.id.tableforbutton);
        mAllPlayerLists = new ArrayList<>();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(HomeActivity.FX,true);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);

        HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID,"");

        mGroupName = mSharedPreferences.getString(HomeActivity.GROUPNAME, "");

        mPlayerListPlayingGame = new ArrayList<>();
        mArrayListValueCardNumber = new ArrayList<>();
        mArrayListCheckContainNumber = new ArrayList<>();

        HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeClickable);

        HomeActivity.mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusOnlineModePath);

        HomeActivity.mStatus_Lobby_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseStatusLobbyModePath);

        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeAdmin);

        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath+mGroupName);

        mSettingItems = new SettingRowAndColumnItems(NUM_ROW,NUM_COL);
        
        HomeActivity.mSettingCardsOnlineJokerDrawReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeSettings);
        
        HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);

        mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(OnlineJokerDraw.this);
        isOnlineJokerDrawActive = true;
        getFirebaseRowAndColumn();
    }

    private void getAdminName(final boolean isJoker){
        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeAdmin);
        HomeActivity.mAdminNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOnlineJokerDrawActive) {
                    if(isJoker) {
                        mAdmin = dataSnapshot.getValue().toString()+"";
                        if (isMessageShow) {
                            mToastMessage.cancel();
                            isMessageShow = false;
                        }
                        if(mCountDownTimer != null){
                            mCountDownTimer.cancel();
                            mCountDownTimer = null;
                        }
                        mArrayListCheckContainNumber.add(num);
                        mDogPosition = mResultNumber;
                        DisableAllOfButton(mDogPosition);
                        DialogForRepeatTheGame(mOneTimeDoing);
                        mOneTimeDoing++;
                    }else{
                        mAdmin = dataSnapshot.getValue().toString()+"";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void delayPlayingGame(){
        if(isOnlineJokerDrawActive)
        {
            ProgressBarDialogWaitingSlowInterNet();
            mHandlerProgress = new Handler();
            mHandlerProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    RetrivesAllPlayerPlayingGame();
                }
            }, 1500);
        }
    }

    private void getFirebaseRowAndColumn(){
        HomeActivity.mSettingCardsOnlineJokerDrawReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeSettings);
        HomeActivity.mSettingCardsOnlineJokerDrawReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOnlineJokerDrawActive) {
                    if (dataSnapshot.hasChild("mCOL") && NUM_ROW != 0 && NUM_COL != 0) {
                        mFirebaseCol = Integer.parseInt(dataSnapshot.child("mCOL").getValue().toString());
                        mFirebaseRow = Integer.parseInt(dataSnapshot.child("mROW").getValue().toString());
                        if (mFirebaseCol != NUM_COL || mFirebaseRow != NUM_ROW) {
                            NUM_COL = mFirebaseCol;
                            NUM_ROW = mFirebaseRow;
                            delayPlayingGame();
                        } else {
                            delayPlayingGame();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void RetrivesAllPlayerPlayingGame(){
        mGroupName = mSharedPreferences.getString(HomeActivity.GROUPNAME, "");
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath+mGroupName);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOnlineJokerDrawActive) {
                    mAllPlayerLists.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(!mAllPlayerLists.contains(snapshot.getValue().toString())) {
                            mAllPlayerLists.add(snapshot.getValue().toString());
                        }
                    }
                    if (NUM_COL != 0 && NUM_ROW != 0) {
                        if (mStartState == 0) {
                            int time = NUM_COL * NUM_ROW;
                            mRandom = new Random();
                            int r = mRandom.nextInt(time) + 1;

                            mPlayerEventItems = new PlayerEventItem(HomeActivity.mPlayerRegisterId, 0, 0, r);
                            HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                            CheckValueClickable();
                        }
                    }
                    if (mAllPlayerLists.size() == 1) {
                        if (!mAllPlayerLists.contains(HomeActivity.mPlayerRegisterId)) {
                            mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(OnlineJokerDraw.this);
                            mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName, HomeActivity.mPlayerRegisterId);
                        } else {
                            if (mCountDownTimer != null) {
                                mCountDownTimer.cancel();
                                mCountDownTimer = null;
                            }
                            mStartState = 0;
                            if(isMessageShow){
                                mToastMessage.cancel();
                                isMessageShow = false;
                            }
                            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawMessageCouldNotPlayThisGameAloneOutOfUsers));

                            mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName,HomeActivity.mPlayerRegisterId);
                            mEditor = mSharedPreferences.edit();
                            mEditor.putBoolean(SelectGame.RULE, true);
                            mEditor.commit();
                            ClearDataMemory();
                            ClearFirebaseDatabasePath();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(OnlineJokerDraw.this, Waiting_Activity.class);
                                    startActivity(intent);
                                }
                            },2000);
                        }
                    }
                    getAdminName(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckValueClickable(){
        HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeClickable);
        HomeActivity.mPlayerClickableValuesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("clickNumber")) {
                    String NUMBER = dataSnapshot.child("clickNumber").getValue().toString();
                    String index = dataSnapshot.child("index").getValue().toString();
                    String resultText = dataSnapshot.child("result").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();

                    mResultNumber = Integer.parseInt(resultText);
                    mTurnIndex = Integer.parseInt(index);
                    num = Integer.parseInt(NUMBER);
                    mArrayListValueCardNumber.add(num);

                    if (isOnlineJokerDrawActive) {
                        if (mAllPlayerLists.size() > 1 && !HomeActivity.mPlayerRegisterId.equals("")) {
                            if(mProgressBarDialog != null){
                                mProgressBarDialog.dismiss();
                                mProgressBarDialog = null;
                            }
                            if (num == 0 && mStartState == 0) {
                                if (HomeActivity.mPlayerRegisterId.equals(mAllPlayerLists.get(mTurnIndex))) {
                                    if (isMessageShow) {
                                        mToastMessage.cancel();
                                        isMessageShow = false;
                                    }
                                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityMessageYourTurn));
                                    InitializeCountDown();
                                    mCountDownTimer.start();
                                    mPlayerName_DrawTurn = HomeActivity.mPlayerRegisterId;
                                } else {
                                    if (isMessageShow) {
                                        mToastMessage.cancel();
                                        isMessageShow = false;
                                    }
                                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityTurn)+" "+mAllPlayerLists.get(mTurnIndex % mAllPlayerLists.size()));
                                    if (mCountDownTimer != null) {
                                        mCountDownTimer.cancel();
                                        mCountDownTimer = null;
                                        mTextViewCountDownNumber.setText("");
                                        mTextViewCountDownText.setText("");
                                    }
                                }
                                mStartState = 1;
                                mTableLayout.removeAllViews();
                                TransformCards();
                            } else if (mResultNumber == num) {
                                if(mDialogInfoOnlineJokerDraw != null){
                                    mDialogInfoOnlineJokerDraw.dismiss();
                                    mDialogInfoOnlineJokerDraw = null;
                                }
                                getAdminName(true);
                            } else if (num == 1000) {
                                if(mDialogDogFlipCard != null){
                                    mDialogDogFlipCard.dismiss();
                                    mDialogDogFlipCard = null;
                                }
                               isOnlineJokerDrawActive = true;
                                getFirebaseRowAndColumn();
                            } else if (num == 2000) {
                                if (HomeActivity.mPlayerRegisterId.equals(mAllPlayerLists.get(mTurnIndex % mAllPlayerLists.size()))) {
                                    if (isMessageShow) {
                                        mToastMessage.cancel();
                                        isMessageShow = false;
                                    }
                                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityMessageYourTurn));
                                    InitializeCountDown();
                                    mCountDownTimer.start();
                                    mPlayerName_DrawTurn = HomeActivity.mPlayerRegisterId;
                                } else {
                                    if (isMessageShow) {
                                        mToastMessage.cancel();
                                        isMessageShow = false;
                                    }
                                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityTurn)+" "+mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()));
                                    if (mCountDownTimer != null) {
                                        mCountDownTimer.cancel();
                                        mCountDownTimer = null;
                                        mTextViewCountDownNumber.setText("");
                                        mTextViewCountDownText.setText("");
                                    }
                                }
                            } else {
                                if (num != 0) {
                                    if (!mArrayListCheckContainNumber.contains(num)) {
                                        SoundEffectControl(0);
                                    }
                                }
                                if(mAllPlayerLists.size() > 1) {
                                    if (HomeActivity.mPlayerRegisterId.equals(mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()))) {
                                        if (isMessageShow) {
                                            mToastMessage.cancel();
                                            isMessageShow = false;
                                        }
                                        CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityMessageYourTurn));
                                        InitializeCountDown();
                                        mCountDownTimer.start();
                                        mPlayerName_DrawTurn = HomeActivity.mPlayerRegisterId;
                                    } else {
                                        if (isMessageShow) {
                                            mToastMessage.cancel();
                                            isMessageShow = false;
                                        }
                                        CustomToastMessage(getString(R.string.Waring_header),getString(R.string.OnlineJokerDrawActivityTurn)+" "+ mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()));
                                        if (mCountDownTimer != null) {
                                            mCountDownTimer.cancel();
                                            mCountDownTimer = null;
                                            mTextViewCountDownNumber.setText("");
                                            mTextViewCountDownText.setText("");
                                        }
                                    }
                                }

                                mArrayListCheckContainNumber.add(num);
                                mTableLayout.removeAllViews();
                                TransformCards();
                            }
                        } else {
                            if(mAllPlayerLists.size() <= 1 && !HomeActivity.mPlayerRegisterId.equals("")){

                                if (mCountDownTimer != null) {
                                    mCountDownTimer.cancel();
                                    mCountDownTimer = null;
                                }
                                mStartState = 0;
                                if(isMessageShow){
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }

                                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawMessageCouldNotPlayThisGameAloneOutOfUsers));

                                mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName,HomeActivity.mPlayerRegisterId);
                                mEditor = mSharedPreferences.edit();
                                mEditor.putBoolean(SelectGame.RULE, true);
                                mEditor.commit();
                                ClearDataMemory();
                                ClearFirebaseDatabasePath();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(mProgressBarDialog != null){
                                            mProgressBarDialog.dismiss();
                                            mProgressBarDialog = null;
                                        }
                                        Intent intent = new Intent(OnlineJokerDraw.this, Waiting_Activity.class);
                                        startActivity(intent);
                                    }
                                },2000);

                            }
                        }
                    } else {
                        onStatePause(num);
                    }
                }else {
                    if (mProgressBarDialog != null) {
                        mProgressBarDialog.dismiss();
                        mProgressBarDialog = null;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void onStatePause(int number){
        switch (number){

            case 0:
                break;

            case 1000:
                Case1000();
                break;

            case 2000:
                break;

            default:
                if(number != 0 && mAllPlayerLists.size() > 0){
                    if(HomeActivity.mPlayerRegisterId.equals(mAllPlayerLists.get(mTurnIndex % mAllPlayerLists.size()))){
                        InitializeCountDown();
                        mCountDownTimer.start();
                    }
                }
                break;
        }
    }

    private void Case1000(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        mTableLayout.removeAllViews();
        mTurnIndex = 0;
        NUM_ROW = mFirebaseRow;
        NUM_COL = mFirebaseCol;
        mCardNumber = 1;
        mResultNumber = 0;
        mDogPosition = 0;
        mOneTimeDoing = 1;
        num = 0;
        mCardNumberDisabled = 1;
        mArrayListValueCardNumber.clear();
        mArrayListCheckContainNumber.clear();
        mTimes = 0;
        mCountWait = 0;
        mStartState = 0;
        isStatePause = false;
        isPressDisabled = false;
    }


    // method ນີ້ ແມ່ນ ຈະຖືກເອີ້ນເມື່ອ ຜູ່ນໍາໃຊ້ ກົດ ທີສັນຍາລັກ ຮູບ ກົງຈັກ ກໍ່ຄືປູ່ມຕັ້ງຄ່າ.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_joker_game_online, menu);
        MenuItem mCountDownMenu = menu.findItem(R.id.CountDownNumberOnlineJokerDraw);
        MenuItem mCountDownMenuText = menu.findItem(R.id.CountDownTextOnlineJokerDraw);
        mTextViewCountDownNumber = (TextView) MenuItemCompat.getActionView(mCountDownMenu);
        mTextViewCountDownText = (TextView) MenuItemCompat.getActionView(mCountDownMenuText);
        InitializeCountDown();
        return  true;
    }

    private void InitializeCountDown(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        mCountDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewCountDownText.setText(R.string.OnlineJokerDrawToolbarDrawWithin);
                mTextViewCountDownText.setTextColor(Color.WHITE);
                mTextViewCountDownText.setTextSize(16f);
                mTextViewCountDownNumber.setTextColor(Color.WHITE);
                mTextViewCountDownNumber.setTextSize(25f);
                mTextViewCountDownNumber.setText(""+millisUntilFinished / 1000);
                if((millisUntilFinished / 1000) <= 10){
                    mTextViewCountDownNumber.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish() {
                isPressDisabled = true;
                mGroupName = mSharedPreferences.getString(HomeActivity.GROUPNAME, "");
                HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");
                mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(OnlineJokerDraw.this);
                mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName, HomeActivity.mPlayerRegisterId);
                HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeClickable);
                if(mAllPlayerLists.size() > 1 && !mPlayerName_DrawTurn.equals("") && isOnlineJokerDrawActive) {
                    if (HomeActivity.mPlayerRegisterId.equals(mPlayerName_DrawTurn)) {
                        mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()), 2000, mTurnIndex + 1, mResultNumber);
                        HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                    }
                }

                if (mAllPlayerLists.size() > 0) {

                    if(mAdmin.equals(HomeActivity.mPlayerRegisterId)) {
                        String NextAdmin = mAllPlayerLists.get(0);

                        if(NextAdmin.equals(mAdmin)){
                            NextAdmin = mAllPlayerLists.get(1 % mAllPlayerLists.size());
                        }

                        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                        HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);

                    }
                }

                mStartState = 0;
                if(isStatePause == false) {
                    OpenDialogPlayerOutOfTime();
                }
                isGetAllPlayerPlayingGame = true;
                getAllPlayerPlayingGame();
                isCountDownFinish = true;
            }
        };
    }

    private void OpenDialogPlayerOutOfTime(){
        if(isOnlineJokerDrawActive) {
            try {
                if (mDialogOutOfTime != null) {
                    mDialogOutOfTime.dismiss();
                    mDialogOutOfTime = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
                View view = getLayoutInflater().inflate(R.layout.outoftime_dialog_layout, null);

                mOkExitPlaying = view.findViewById(R.id.ok_exit_to_waiting);

                mOkExitPlaying.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogOutOfTime.dismiss();
                        Intent intent = new Intent(OnlineJokerDraw.this, Waiting_Activity.class);
                        startActivity(intent);
                    }
                });

                builder.setView(view);
                mDialogOutOfTime = builder.create();
                mDialogOutOfTime.show();
            } catch (WindowManager.BadTokenException ex) {
                ex.printStackTrace();
            }
        }
    }
    // method ນີ້ ຈະຖຶກເອີ້ນ ເມື່ອ ຜູ່ນໍາໃຊ້ ກົດ ປູ່ມ ຫຼື ເລືອກ ຢູ່ໃນ ການຕັ້ງຄ່າ
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID,"");
        if(isOnlineJokerDrawActive) {
            if (item.getItemId() == android.R.id.home) {
                mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName, HomeActivity.mPlayerRegisterId);
                HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeClickable);
                if (mAllPlayerLists.size() > 1 && !mPlayerName_DrawTurn.equals("") && isOnlineJokerDrawActive) {
                    if (HomeActivity.mPlayerRegisterId.equals(mPlayerName_DrawTurn)) {
                        mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()), 2000, mTurnIndex + 1, mResultNumber);
                        HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                    }
                }

                if (mAllPlayerLists.size() > 0) {

                    if(mAdmin.equals(HomeActivity.mPlayerRegisterId)) {
                        String NextAdmin = mAllPlayerLists.get(0);

                        if(NextAdmin.equals(mAdmin)){
                            NextAdmin = mAllPlayerLists.get(1 % mAllPlayerLists.size());
                        }

                        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                        HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);

                    }
                }

                isGetAllPlayerPlayingGame = true;
                getAllPlayerPlayingGame();

                Intent intent = new Intent(OnlineJokerDraw.this, Waiting_Activity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.infoJokerGame) {
                OpenDialogInfoJokerGame();
            }
            if (mAdmin.equals(HomeActivity.mPlayerRegisterId) && num == 0) {
                switch (item.getItemId()) {
                    case R.id.setting_joker_draw:
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }
                        OpenDialogSettingsJokerGameOnline();
                        return true;
                    default:
                        return super.onOptionsItemSelected(item);
                }
            } else if(mAdmin.equals(HomeActivity.mPlayerRegisterId) && num != 0) {
                if (isMessageShow) {
                    mToastMessage.cancel();
                    isMessageShow = false;
                }
                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityCouldNotSettingNow));
            }
            else{
                if(!HomeActivity.mPlayerRegisterId.equals(mAdmin)){
                    if(isMessageShow){
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawActivityCouldNotSettingNow));
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    // this method ຈະເຮັດ ໜ້າທີ່ໃນການ ສະແດງ ຜົນ ແລະ ສ້າງ ປູ່ມ ຂື້ນມາໂດຍອັດຕະໂນມັດ.
    @SuppressLint({"ResourceAsColor", "NewApi"})
    private void TransformCards(){
        mTimes = NUM_ROW * NUM_COL;
        mTableLayout = findViewById(R.id.tableforbutton);
        for(int row = 0; row < NUM_ROW; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
            mTableLayout.addView(tableRow);
            for(int col = 0; col < NUM_COL; col++) {
                final Button button = new Button(this);
                final int number = mCardNumber;
                button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
                button.setPadding(0, 0, 0, 0);

                if(num == number){
                    button.setEnabled(false);
                    button.setBackgroundResource(R.drawable.card_animation);
                    button.animate().scaleX(0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            button.setBackgroundResource(R.drawable.cat_disabled);
                            button.animate().scaleX(1f);
                        }
                    });
                }else if(mArrayListValueCardNumber.contains(number)){
                    button.setBackgroundResource(R.drawable.cat_disabled);
                    button.setEnabled(false);
                }else{
                    button.setBackgroundResource(R.drawable.card_animation);
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeClickable);
                        if(isPressDisabled == false){
                            if (HomeActivity.mPlayerRegisterId.equals(mPlayerName_DrawTurn) && !mPlayerName_DrawTurn.equals("")) {
                                if (mResultNumber == number) {
                                    mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex) % mAllPlayerLists.size()), number, mTurnIndex, mResultNumber);
                                    HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                                } else {
                                    mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex + 1) % mAllPlayerLists.size()), number, mTurnIndex + 1, mResultNumber);
                                    if (mPlayerName_DrawTurn.equals(mAllPlayerLists.get((mTurnIndex + 1) % mAllPlayerLists.size()))) {
                                        mTurnIndex = mTurnIndex + 1;
                                        mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex + 1) % mAllPlayerLists.size()), number, mTurnIndex + 1, mResultNumber);
                                    }
                                    HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                                    mPlayerName_DrawTurn = "";
                                }
                            } else {
                                if (isMessageShow) {
                                    mToastMessage.cancel();
                                    isMessageShow = false;
                                }
                                CustomToastMessage(getString(R.string.Waring_header), getString(R.string.OnlineJokerDrawPleaseWaitingForYourTurn));
                            }
                        }

                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                            mCountDownTimer = null;
                            mTextViewCountDownNumber.setText("");
                            mTextViewCountDownText.setText("");
                        }
                    }
                });
                mCardNumber++;
                tableRow.addView(button);
            }
        }
        mCardNumber = 1;
        mFirebaseCol = NUM_COL;
        mFirebaseRow = NUM_ROW;
    }
    // this method  ຈະສະແດງຜົນ Animation ເມື່ອຜູ່່ນໍາໃຊ້ ກົດ ປູ່ມທີ່ ກົງກັບ ຕົວເລກ ທີ່ ຖືກການ Random
    @SuppressLint("NewApi")
    private void DialogForRepeatTheGame(int oneTime){
        if(oneTime == 1){
            mHandlerProgress = new Handler();
            mHandlerProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                try {
                    if(mDialogDogFlipCard != null){
                        mDialogDogFlipCard.dismiss();
                        mDialogDogFlipCard = null;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
                    View view = getLayoutInflater().inflate(R.layout.endgame_dialog, null);
                    mDogFlipCard = view.findViewById(R.id.opp_up_card);
                    mJokerText = view.findViewById(R.id.Joker_Text);
                    mRepeatAnimationFlipOnlineJokerDraw = view.findViewById(R.id.RepeatOnlineJokerDrawButton);

                    mRepeatAnimationFlipOnlineJokerDraw.setVisibility(View.INVISIBLE);

                    if(mAllPlayerLists.size() > 1) {
                        mJokerText.setText(mAllPlayerLists.get((mTurnIndex % mAllPlayerLists.size())));
                    }
                    mJokerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    mJokerText.setTextSize(20);
                    mJokerText.setTextColor(getResources().getColor(R.color.colorSubList));
                    mDogFlipCard.setEnabled(false);
                    mJokerText.setEnabled(false);
                    mDogFlipCard.setBackgroundResource(R.drawable.card_animation_dialog);


                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDogFlipCard.animate().scaleX(0f).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    SoundEffectControl(1);
                                    mDogFlipCard.setBackgroundResource(R.drawable.dog_dialog);
                                    mDogFlipCard.animate().scaleX(1f);
                                    DisableAllOfButton(mDogPosition);
                                    mDogFlipCard.setEnabled(true);
                                    mJokerText.setEnabled(true);
                                    if(mAdmin.equals(HomeActivity.mPlayerRegisterId)){
                                        mRepeatAnimationFlipOnlineJokerDraw.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }, 500);

                    mRepeatAnimationFlipOnlineJokerDraw.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogDogFlipCard.dismiss();
                        }
                    });

                    builder.setView(view);
                    mDialogDogFlipCard = builder.create();
                    mDialogDogFlipCard.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    mDialogDogFlipCard.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialogDogFlipCard.setCanceledOnTouchOutside(false);
                    if(!isFinishing()) {
                        mDialogDogFlipCard.show();
                    }
                    mDialogDogFlipCard.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mPlayerEventItems = new PlayerEventItem(HomeActivity.mPlayerRegisterId, 1000, 0, mResultNumber);
                            HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                            onCustomDialogDismiss();
                            mDogSoundPool.stop(mDogSoundId);
                        }
                    });
                }catch (WindowManager.BadTokenException ex){
                    ex.printStackTrace();
                }
                }
            },0);
        }
    }

    private void onCustomDialogDismiss(){
       // AfterEnd();
        Waiting_repeat();
    }
    // repeat game
    private void Waiting_repeat(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        mTableLayout.removeAllViews();
        mTurnIndex = 0;
        NUM_ROW = mFirebaseRow;
        NUM_COL = mFirebaseCol;
        mCardNumber = 1;
        mResultNumber = 0;
        mDogPosition = 0;
        mOneTimeDoing = 1;
        num = 0;
        mCardNumberDisabled = 1;
        mArrayListValueCardNumber.clear();
        mArrayListCheckContainNumber.clear();
        mTimes = 0;
        mCountWait = 0;
        mStartState = 0;
        isStatePause = false;
        mPlayerName_DrawTurn = "";
        isInfoDialog = false;
        isOnlineJokerDrawActive = true;
        isPressDisabled = false;
        getFirebaseRowAndColumn();
    }
/*    @SuppressLint("NewApi")
    private void AfterEnd(){
        final int getResult = mResultNumber;
        mTableLayout.removeAllViews();
        for(int row = 0; row < NUM_ROW; row++){
            final TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
            mTableLayout.addView(tableRow);

            for(int col = 0; col < NUM_COL; col++){
                mCardButton = new Button(this);
                mCardButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f));
                final int number = mCardNumberDisabled;
                mCardButton.setEnabled(false);
                if(number == getResult) {
                    mCardButton.setBackgroundResource(R.drawable.dog);
                    mCountWait++;
                    FlipAnimation(mCardButton);
                }else {
                    mCardButton.setBackgroundResource(R.drawable.cat_disabled);
                    mCountWait++;
                    FlipAnimation(mCardButton);
                }
                mCardNumberDisabled++;
                tableRow.addView(mCardButton);
            }
        }
        mCardNumberDisabled = 1;

    }*/

/*    private void FlipAnimation(View view){
        final ObjectAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(view, "scaleX", 1f,0f);
        final ObjectAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(view, "scaleX", 0f,1f);
        objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
        objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        objectAnimatorTextNumber1.setDuration(300);
        objectAnimatorTextNumber1.start();

        if(mCountWait == mTimes){
            Waiting_repeat();
        }
    }*/
    // Method ນີ້ແມ່ນເປັນບ່ອນ ຄວບຄູມສຽງຂອງ Animation
    private void SoundEffectControl(int oneDog){
        isEffectSound = mSharedPreferences.getBoolean(HomeActivity.FX,true);

        if(oneDog == 1 && isEffectSound){
            if(isOnlineJokerDrawActive == false){
                if(isMessageShow){
                    mToastMessage.cancel();
                    isMessageShow = false;
                }
            }else {
                mDogSoundPool.play(mDogSoundId, 0.99f, 0.99f, 0, 0, 0.99f);
            }
        }else if(oneDog == 0 && isEffectSound) {
            if(isOnlineJokerDrawActive == false){
                if(isMessageShow){
                    mToastMessage.cancel();
                    isMessageShow = false;
                }
            }else {
                mCatSoundPool.play(mCatSoundId, 0.99f, 0.99f, 0, 0, 0.99f);
            }
        }
    }
    // button get disable when number of button equal to result random
    private void DisableAllOfButton(int result){
        int getResult = result;
        mTableLayout.removeAllViews();
        for(int row = 0; row < NUM_ROW; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
            mTableLayout.addView(tableRow);

            for(int col = 0; col < NUM_COL; col++){
                mCardButtonDisabled = new Button(this);
                mCardButtonDisabled.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f));
                final int number = mCardNumberDisabled;
                mCardButtonDisabled.setPadding(0,0,0,0);
                mCardButtonDisabled.setEnabled(false);
                if(number == getResult){
                    mCardButtonDisabled.setBackgroundResource(R.drawable.dog);
                }else{
                    mCardButtonDisabled.setBackgroundResource(R.drawable.cat_disabled);
                }
                mCardNumberDisabled++;
                tableRow.addView(mCardButtonDisabled);
            }
        }
        mCardNumberDisabled = 1;
    }

    @Override
    protected void onStart() {
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        mCatSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        mCatSoundId = mCatSoundPool.load(this, R.raw.cat_sound,1);
        mDogSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mDogSoundId = mDogSoundPool.load(this, R.raw.dog_sound,1);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.start();
                    HomeActivity.mBackgroundMusics.setLooping(true);
                }
            }
        }
        isOnlineJokerDrawActive = true;
        isActive = true;
        if(isStatePause){
            mTableLayout.removeAllViews();
            TransformCards();
            isStatePause = false;
            if(isCountDownFinish){
                OpenDialogPlayerOutOfTime();
                isCountDownFinish = false;
            }
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        mGroupName = mSharedPreferences.getString(HomeActivity.GROUPNAME,"");
        HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");
        isEffectSound = mSharedPreferences.getBoolean(HomeActivity.FX, true);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);
        if(HomeActivity.mBackgroundMusics != null && !(SelectGame.isActive || HomeActivity.isActive || JokerGame.isActive || User_Player_Lucky_Game.isActive || UserPlayerSwapGame.isActive || SwapGame.isActive || LuckyGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }

        }
        if(isEffectSound){
            mDogSoundPool.stop(mDogSoundId);
            mDogSoundPool.release();
            mCatSoundPool.stop(mCatSoundId);
            mCatSoundPool.release();
        }

        if(mDialogDogFlipCard != null){
            mDialogDogFlipCard.dismiss();
            mDialogDogFlipCard = null;
        }
        isOnlineJokerDrawActive = false;
        mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(OnlineJokerDraw.this);
        mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName,HomeActivity.mPlayerRegisterId);
        super.onStop();
    }

    @Override
    protected void onPause() {
        isEffectSound = mSharedPreferences.getBoolean(HomeActivity.FX, true);
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);
        Context context = getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                if(isEffectSound){
                    mDogSoundPool.stop(mDogSoundId);
                    mDogSoundPool.release();
                    mCatSoundPool.stop(mCatSoundId);
                    mCatSoundPool.release();
                }
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
            if(isEffectSound){
                mDogSoundPool.stop(mDogSoundId);
                mDogSoundPool.release();
                mCatSoundPool.stop(mCatSoundId);
                mCatSoundPool.release();
            }
        }

        if(mDialogOutOfTime != null){
            mDialogOutOfTime.dismiss();
            mDialogOutOfTime = null;
        }

        if(mDialogInfoOnlineJokerDraw != null){
            mDialogInfoOnlineJokerDraw.dismiss();
            mDialogInfoOnlineJokerDraw = null;
        }

        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }

        isOnlineJokerDrawActive = false;
        isActive = false;
        isStatePause = true;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        OpenDialogBackPressPopupConfirmation();
    }

    private void ProgressBarDialogWaitingSlowInterNet(){
        try {
            if(mProgressBarDialog != null){
                mProgressBarDialog.dismiss();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
            View view = getLayoutInflater().inflate(R.layout.progress_bar_dialog, null);
            builder.setView(view);
            mProgressBarDialog = builder.create();
            mProgressBarDialog.setCancelable(false);
            mProgressBarDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if(!this.isFinishing()) {
                mProgressBarDialog.show();
            }
        }catch (WindowManager.BadTokenException ex){
            ex.printStackTrace();
        }
    }

    private void CustomToastMessage(String string_title, String string_body){
        try {
            if(isMessageShow || isOnlineJokerDrawActive == false){
                mToastMessage.cancel();
                isMessageShow = false;
            }else {
                isMessageShow = true;
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.message_layout, (ViewGroup) findViewById(R.id.root_message));
                mTitle = layout.findViewById(R.id.message_title);
                mBody = layout.findViewById(R.id.message_body);
                mTitle.setText(string_title);
                mBody.setText(string_body);
                mToastMessage = new Toast(OnlineJokerDraw.this);
                mToastMessage.setGravity(Gravity.BOTTOM, 0, 0);
                mToastMessage.setDuration(Toast.LENGTH_LONG);
                mToastMessage.setView(layout);
                if (mAllPlayerLists.contains(HomeActivity.mPlayerRegisterId)) {
                    mToastMessage.show();
                }
            }
        }catch (WindowManager.BadTokenException ex){
            ex.printStackTrace();
        }
    }

    private void OpenDialogSettingsJokerGameOnline(){
        if(mDialogSettings != null){
            mDialogSettings.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
        View view = getLayoutInflater().inflate(R.layout.setting_col_and_row_joker_game, null);

        mRadioButtonNine = view.findViewById(R.id.RadioButtonNine);
        mRadioButtonTwenty = view.findViewById(R.id.RadioButtonTwenty);
        mRadioButtonFifty = view.findViewById(R.id.RadioButtonFifty);
        mCustomizeCol_RowButton = view.findViewById(R.id.customizeColandRow);
        mOkSetting = view.findViewById(R.id.OkButtonJokerGameSetting);
        mCancelSetting = view.findViewById(R.id.CancelButtonJokerGameSetting);

        // event listener
        mOkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRadioButtonNine.isChecked()){
                    mSettingItems = new SettingRowAndColumnItems(3, 3);
                    HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                    getFirebaseRowAndColumn();
                }else if(mRadioButtonTwenty.isChecked()){
                    mSettingItems = new SettingRowAndColumnItems(5, 4);
                    HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                    getFirebaseRowAndColumn();
                }else if(mRadioButtonFifty.isChecked()){
                    mSettingItems = new SettingRowAndColumnItems(10, 5);
                    HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                    getFirebaseRowAndColumn();
                }
                mDialogSettings.dismiss();
                mStartState = 0;
            }
        });

        mCancelSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSettings.dismiss();
                if(mCountDownTimer != null){
                    mCountDownTimer.start();
                }
            }
        });

        mCustomizeCol_RowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSettings.dismiss();
                OpenDialogPickerNumberColAndRow();
                mStartState = 0;
            }
        });

        builder.setView(view);
        mDialogSettings = builder.create();
        mDialogSettings.show();
        mDialogSettings.setCancelable(false);
    }

    private void ClearDataMemory(){
        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        mCountDownTimer = null;
        isOnlineJokerDrawActive = false;
    }

    private void ClearFirebaseDatabasePath(){
        HomeActivity.mSettingCardsOnlineJokerDrawReference = null;
        HomeActivity.mPlayerClickableValuesReference = null;
        HomeActivity.mStatus_Lobby_Reference = null;
        HomeActivity.mStatus_Online_Reference = null;
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = null;
        HomeActivity.mSettingCardsOnlineJokerDrawReference = null;
    }

    private void OpenDialogPickerNumberColAndRow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
        View view = getLayoutInflater().inflate(R.layout.custom_col_and_row, null);

        mNumberPickerColumn = view.findViewById(R.id.column);
        mNumberPickerRow = view.findViewById(R.id.row);
        mOkCustomizeSettings = view.findViewById(R.id.ok_customize_settings);
        mCancelCustomizeSettings = view.findViewById(R.id.cancel_customize_setting);

        mNumberPickerColumn.setMinValue(3);
        mNumberPickerColumn.setMaxValue(5);
        mNumberPickerColumn.setValue(NUM_COL);

        mNumberPickerRow.setMinValue(3);
        mNumberPickerRow.setMaxValue(10);
        mNumberPickerRow.setValue(NUM_ROW);

        // On NumberPicker Value Change
        mNumberPickerColumn.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(mNumberPickerColumn.getValue() == oldVal){
                    mSelectedNumberPickerColumn = oldVal;
                }else{
                    mSelectedNumberPickerColumn = newVal;
                }
            }
        });

        mNumberPickerRow.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(mNumberPickerRow.getValue() == oldVal){
                    mSelectedNumberPickerRow = oldVal;
                }else{
                    mSelectedNumberPickerRow = newVal;
                }
            }
        });


        // setting event
        mOkCustomizeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedNumberPickerColumn == 0 && mSelectedNumberPickerRow == 0)
                {

                }
                else if(mSelectedNumberPickerColumn == 0 && mSelectedNumberPickerRow != 0)
                {
                   mSettingItems = new SettingRowAndColumnItems(mSelectedNumberPickerRow, NUM_COL);
                   HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                   getFirebaseRowAndColumn();
                }
                else if(mSelectedNumberPickerColumn != 0 && mSelectedNumberPickerRow == 0)
                {
                    mSettingItems = new SettingRowAndColumnItems(NUM_ROW, mSelectedNumberPickerColumn);
                    HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                    getFirebaseRowAndColumn();
                }
                else {
                    mSettingItems = new SettingRowAndColumnItems(mSelectedNumberPickerRow, mSelectedNumberPickerColumn);
                    HomeActivity.mSettingCardsOnlineJokerDrawReference.setValue(mSettingItems);
                    getFirebaseRowAndColumn();
                }
                mDialogCustomize.dismiss();
                mStartState = 0;
            }
        });

        mCancelCustomizeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCustomize.dismiss();
                if(mCountDownTimer != null){
                    mCountDownTimer.start();
                }
            }
        });

        builder.setView(view);
        mDialogCustomize = builder.create();
        mDialogCustomize.show();
        mDialogCustomize.setCancelable(false);
    }

    private void OpenDialogBackPressPopupConfirmation(){
        if(mDialogBackPressPopup != null){
            mDialogBackPressPopup.dismiss();
            mDialogBackPressPopup = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
        View view = getLayoutInflater().inflate(R.layout.online_joker_draw_backpress_popup_layout, null);

        mTextViewBackPressPopupBody = view.findViewById(R.id.back_popup_text_body);
        mOkBackPressPopup = view.findViewById(R.id.Ok_back_popup);
        mCancelBackPressPopup = view.findViewById(R.id.Cancel_back_popup);
        mTextViewBackPressPopupBody.setText(getString(R.string.OnlineJokerDrawBackPressedConfirmExit));

        mOkBackPressPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBackPressPopup.dismiss();

                mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName,HomeActivity.mPlayerRegisterId);

                HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeClickable);

                if(mAllPlayerLists.size() > 1 && !mPlayerName_DrawTurn.equals("")) {
                    if (HomeActivity.mPlayerRegisterId.equals(mPlayerName_DrawTurn)) {
                        mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex + 1) % mAllPlayerLists.size()), 2000, mTurnIndex + 1, mResultNumber);
                        HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                    }
                }

                if (mAllPlayerLists.size() > 0) {

                    if(mAdmin.equals(HomeActivity.mPlayerRegisterId)) {
                        String NextAdmin = mAllPlayerLists.get(0);

                        if(NextAdmin.equals(mAdmin)){
                            NextAdmin = mAllPlayerLists.get(1 % mAllPlayerLists.size());
                        }

                        HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                        HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);

                    }
                }

                isGetAllPlayerPlayingGame = true;
                getAllPlayerPlayingGame();
                Intent intent = new Intent(OnlineJokerDraw.this, Waiting_Activity.class);
                startActivity(intent);
            }
        });

        mCancelBackPressPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBackPressPopup.dismiss();
            }
        });

        builder.setView(view);
        mDialogBackPressPopup = builder.create();
        mDialogBackPressPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogBackPressPopup.show();
        mDialogBackPressPopup.setCancelable(false);
    }

    private void getAllPlayerPlayingGame(){
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.child(mGroupName);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOnlineJokerDrawActive){
                    mPlayerListPlayingGame.clear();
                    for(DataSnapshot snapshot : dataSnapshot.child(mGroupName).getChildren()){
                        mPlayerListPlayingGame.add(snapshot.getValue().toString());
                    }

                    if(isGetAllPlayerPlayingGame){

                        ClearDataMemory();
                        ClearFirebaseDatabasePath();
                        isGetAllPlayerPlayingGame = false;
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
        mGroupName = mSharedPreferences.getString(HomeActivity.GROUPNAME, "");
        HomeActivity.mPlayerRegisterId = mSharedPreferences.getString(HomeActivity.PLAYERID, "");

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        if (mDialogOutOfTime != null) {
            mDialogOutOfTime.dismiss();
            mDialogOutOfTime = null;
        }

        if(mAllPlayerLists.size() > 1) {
            mOnlineJokerDrawHelper = new OnlineJokerDrawHelper(OnlineJokerDraw.this);
            mOnlineJokerDrawHelper.removePlayerNameFromPlayingGame(mGroupName, HomeActivity.mPlayerRegisterId);

            HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeClickable);
            if(mAllPlayerLists.size() > 1 && !mPlayerName_DrawTurn.equals("")) {
                if (HomeActivity.mPlayerRegisterId.equals(mPlayerName_DrawTurn)) {
                    mPlayerEventItems = new PlayerEventItem(mAllPlayerLists.get((mTurnIndex + 1) % mAllPlayerLists.size()), 2000, mTurnIndex + 1, mResultNumber);
                    HomeActivity.mPlayerClickableValuesReference.setValue(mPlayerEventItems);
                }
            }
        }

        if (mAllPlayerLists.size() > 0) {

            if(mAdmin.equals(HomeActivity.mPlayerRegisterId)) {
                String NextAdmin = mAllPlayerLists.get(0);

                if(NextAdmin.equals(mAdmin)){
                    NextAdmin = mAllPlayerLists.get(1 % mAllPlayerLists.size());
                }

                HomeActivity.mGroupInfo_Values_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath + mGroupName + HomeActivity.mTreeAdmin);
                HomeActivity.mGroupInfo_Values_GroupName_Reference.setValue(NextAdmin);

            }
        }

        isOnlineJokerDrawActive = false;
        super.onDestroy();
    }

    private void OpenDialogInfoJokerGame(){
        if(mDialogInfoOnlineJokerDraw != null){
            mDialogInfoOnlineJokerDraw.dismiss();
            mDialogInfoOnlineJokerDraw = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(OnlineJokerDraw.this);
        View view = getLayoutInflater().inflate(R.layout.online_joker_game_info_layout, null);

        mExitInfoJokerDraw = view.findViewById(R.id.exit_info_jokerGame);
        mListViewInfoJokerDraw = view.findViewById(R.id.ListViewInfoJokerDraw);
        mAdminJokerDrawInfo = view.findViewById(R.id.AdminJokerDraw);
        mTextViewPlayerOnlineJokerDrawSize = view.findViewById(R.id.textViewPlayerSizeOnlineJokerDraw);


        mExitInfoJokerDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogInfoOnlineJokerDraw.dismiss();
                isInfoDialog = false;
            }
        });

        builder.setView(view);
        mDialogInfoOnlineJokerDraw = builder.create();
        if(!this.isFinishing()) {
            mDialogInfoOnlineJokerDraw.show();
        }
        mDialogInfoOnlineJokerDraw.setCancelable(false);
        isInfoDialog = true;
        realTimeTellerPlayerTurn();
        realTimeAdminName();
    }

    private void realTimeTellerPlayerTurn(){
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayerPlayingPath+mGroupName);
        HomeActivity.mGroupInfo_PlayerPlaying_GroupName_Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isOnlineJokerDrawActive) {
                    if (isInfoDialog) {
                        ArrayList<String> mArrayListRealTime = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (!mArrayListRealTime.contains(snapshot.getValue().toString())) {
                                mArrayListRealTime.add(snapshot.getValue().toString());
                            }
                        }
                        mTextViewPlayerOnlineJokerDrawSize.setText(getString(R.string.WaitingActivityPlayerSize)+ " "+mArrayListRealTime.size());
                        realTimeTurnIndex(mArrayListRealTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void realTimeAdminName(){
        HomeActivity.mAdminNameReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeAdmin);
        HomeActivity.mAdminNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isOnlineJokerDrawActive) {
                    if (isInfoDialog) {
                        String admin = dataSnapshot.getValue().toString();
                        mAdminJokerDrawInfo.setText(getString(R.string.WaitinActivityAdmin) + " " + admin);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void realTimeTurnIndex(final ArrayList<String> list){
        HomeActivity.mPlayerClickableValuesReference = FirebaseDatabase.getInstance().getReference(HomeActivity.mTreeFirebaseGroupInfoPlayGamePath+ mGroupName +HomeActivity.mTreeClickable);
        HomeActivity.mPlayerClickableValuesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("clickNumber")) {
                    if (isOnlineJokerDrawActive) {
                        if (isInfoDialog) {
                            String index = dataSnapshot.child("index").getValue().toString();
                            int mIndex = Integer.parseInt(index);
                            ArrayList<InfoJokerDrawItems> mListInfoJokerDraw = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(mIndex % list.size()).equals(list.get(i))) {
                                    mListInfoJokerDraw.add(new InfoJokerDrawItems(list.get(i), getString(R.string.OnlineJokerDrawDialogInfoThisPersionTurn)));
                                } else {
                                    mListInfoJokerDraw.add(new InfoJokerDrawItems(list.get(i), ""));
                                }
                            }
                            mArrayAdapterListJokerDraw = new ArrayAdapterInfoJokerDraw(OnlineJokerDraw.this, mListInfoJokerDraw) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    if (position % 2 == 1) {
                                        view.setBackgroundResource(R.color.colorList);
                                    } else {
                                        view.setBackgroundResource(R.color.colorSubList);
                                    }
                                    return view;
                                }
                            };
                            mArrayAdapterListJokerDraw.notifyDataSetChanged();
                            mListViewInfoJokerDraw.setAdapter(mArrayAdapterListJokerDraw);
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
