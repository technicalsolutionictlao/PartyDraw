package com.ictlao.android.app.partydraw.Feature.Select;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ictlao.android.app.partydraw.Core.InternetProvider;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.ManagePlayerStatus;
import com.ictlao.android.app.partydraw.Core.Services.MyFirebaseInstanceIDService;
import com.ictlao.android.app.partydraw.Core.Services.OreoService;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.List;
import java.util.Locale;

public class SelectGame extends AppCompatActivity {
    // Memory data variable
    public static boolean isActive;
    private String mPlayerRegisterIdName;
    private boolean isBGMSound;
    private String mLanguage;
    private boolean isMessageShow = false;

    public static String RULE = "RULE";
    private boolean isRule = false;
    private boolean mConnectionNetwork = false;
    private ManagePlayerStatus mManagePlayerStatus;

    // UI variable
    private Button mSwapDraw, mLuckyDraw, mJokerDraw, mCoupleDraw;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView mTitle, mBody;
    private AlertDialog mPopupSelectedJokerDrawMode;
    private Button mOfflineButton, mOnlineButton, mExitPopupButton;
    private Intent mService;
    private Toast mToastMessage;
    private InternetProvider mInternetProvider;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));  // ເອີ້ນ method ນີ້ ມາໃຊ້ ເພື່ອໃຫ້ປ່ຽນພາສາ
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

        setContentView(R.layout.activity_select_game);

        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle(R.string.SelectGameActivity_ToolbarText);
        isRule = mSharedPreferences.getBoolean(RULE, true);

        if(isRule == false){
            if(isMessageShow){
                mToastMessage.cancel();
                isMessageShow = false;
            }
            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.Rule_body));
            mEditor = mSharedPreferences.edit();
            mEditor.putBoolean(RULE, true);
            mEditor.commit();
        }


        // ເຊື່ອມໂຍງ ໄອດີ ກັບ ຕົວປ່ຽນ.
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mSwapDraw = findViewById(R.id.SwapGame);
        mLuckyDraw = findViewById(R.id.lucky);
        mJokerDraw = findViewById(R.id.joker);
        mCoupleDraw = findViewById(R.id.couple);

        // all control background music

        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);


        // set Event click ໃຫ້ ປູ່ມເກມ.
        mSwapDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenSwapGame();
            }
        });

        mLuckyDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLuckyGame();
            }
        });

        mJokerDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupSelectedModeBuilderDialog();
            }
        });

        mCoupleDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCoupleGame();
            }
        });
    }
    // popup selection to users
    private void PopupSelectedModeBuilderDialog(){
        if(mPopupSelectedJokerDrawMode != null){
            mPopupSelectedJokerDrawMode.dismiss();
            mPopupSelectedJokerDrawMode = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_selected_mode_layout, null);

        mInternetProvider = new InternetProvider(SelectGame.this);
        mOfflineButton = view.findViewById(R.id.OffLineMode);
        mOnlineButton = view.findViewById(R.id.OnLineMode);
        mExitPopupButton = view.findViewById(R.id.ExitPopupMode);

        // set event
        mOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupSelectedJokerDrawMode.dismiss();
                OpenJokerGame();
            }
        });

        mOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // online view
                mConnectionNetwork = mInternetProvider.isConnected();
                if(mConnectionNetwork == true){
                    mPopupSelectedJokerDrawMode.dismiss();
                    OpenCreateGroup();
                }else{
                    if (isMessageShow) {
                        mToastMessage.cancel();
                        isMessageShow = false;
                    }
                    CustomToastMessage(getString(R.string.Waring_header), getString(R.string.NetWorkConnectionError));
                }
            }
        });

        mExitPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupSelectedJokerDrawMode.dismiss();
            }
        });

        builder.setView(view);
        mPopupSelectedJokerDrawMode = builder.create();
        mPopupSelectedJokerDrawMode.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mPopupSelectedJokerDrawMode.show();
        mPopupSelectedJokerDrawMode.setCancelable(false);
    }

    private  void OpenCreateGroup(){
        Intent creategroup = new Intent(SelectGame.this, CreateGroupActivity.class);
        startActivity(creategroup);
    }

    // set method ໃຫ້ navigate ຫາເກມ
    private void OpenSwapGame(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        Intent swapgame = new Intent(SelectGame.this, UserPlayerSwapGame.class);
        swapgame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(swapgame);
    }

    private void OpenLuckyGame(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        Intent luckygame = new Intent(SelectGame.this, User_Player_Lucky_Game.class);
        luckygame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(luckygame);
    }

    private void OpenJokerGame(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        Intent jokergame = new Intent(SelectGame.this, JokerGame.class);
        jokergame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(jokergame);
    }

    private void OpenCoupleGame(){
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        if(mLanguage.equals("en")){
            CustomToastMessage("Couple Draw","This game is coming soon!");
        }else{
            CustomToastMessage("Couple Draw","ເກມໃໝ່ກໍາລັງຈະມາ!");
        }
    }

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

    @Override
    protected void onStart() {

        mInternetProvider = new InternetProvider(SelectGame.this);
        mConnectionNetwork = mInternetProvider.isConnected();
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM, true);
        mService = new Intent(SelectGame.this, MyFirebaseInstanceIDService.class);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        mPlayerRegisterIdName = mSharedPreferences.getString(HomeActivity.PLAYERID, "");
        mManagePlayerStatus = new ManagePlayerStatus(SelectGame.this);
        if(mConnectionNetwork) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startService(new Intent(SelectGame.this, OreoService.class));
            } else {
                startService(mService);
            }
        }

        super.onStart();
    }

    @Override
    protected void onResume() {
        mLanguage = mSharedPreferences.getString(HomeActivity.DL, "en");
        isBGMSound = mSharedPreferences.getBoolean(HomeActivity.BGM,true);
        mPlayerRegisterIdName = mSharedPreferences.getString(HomeActivity.PLAYERID, "");

        if(mLanguage.equals("en")){
            getSupportActionBar().setTitle("  Games");
        }else{
            getSupportActionBar().setTitle("  ເກມ");
        }

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
        if(!mPlayerRegisterIdName.equals("") && mConnectionNetwork) {
            mManagePlayerStatus.removeStatusLobby(mPlayerRegisterIdName);
            mManagePlayerStatus.setStatusOnline(mPlayerRegisterIdName);
        }
        isActive = true;

        isRule = mSharedPreferences.getBoolean(RULE, true);

        if(isRule == false){
            if(isMessageShow){
                mToastMessage.cancel();
                isMessageShow = false;
            }
            CustomToastMessage(getString(R.string.Waring_header), getString(R.string.Rule_body));
            mEditor = mSharedPreferences.edit();
            mEditor.putBoolean(RULE, true);
            mEditor.commit();
        }

        super.onResume();
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
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        isActive = false;

        if(mPopupSelectedJokerDrawMode != null){
            mPopupSelectedJokerDrawMode.dismiss();
            mPopupSelectedJokerDrawMode = null;
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isMessageShow){
            mToastMessage.cancel();
            isMessageShow = false;
        }
        Intent mMainActivity = new Intent(SelectGame.this, HomeActivity.class);
        startActivity(mMainActivity);
    }

    @Override
    protected void onDestroy() {
        if(this.isFinishing()){
            ClearMemoryData();
        }
        super.onDestroy();
    }

    private void ClearMemoryData(){
        mPlayerRegisterIdName = "";
        isBGMSound = false;
        mLanguage = "";
        isMessageShow = false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(SelectGame.this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
