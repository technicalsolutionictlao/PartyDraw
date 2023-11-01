package com.ictlao.android.app.partydraw;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ictlao.partydraw.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    // Firebase static variable
    // Status online
    public static DatabaseReference mStatus_Online_Reference;
    // Status Lobby
    public static DatabaseReference mStatus_Lobby_Reference;
    // Player Information
    public static DatabaseReference mPlayerRegistrationInformation;
    // Player in waiting room
    public static DatabaseReference mGroupInfo_PlayersLobby_GroupName_Reference;
    // Player is Playing game
    public static DatabaseReference mGroupInfo_PlayerPlaying_GroupName_Reference;
    // Values Player playing game
    public static DatabaseReference mGroupInfo_Values_GroupName_Reference;
    // Information Group
    public static DatabaseReference mGroupListsReference;
    // Player is joining
    public static DatabaseReference mPlayerJoinReference;
    // Notification message to Player
    public static DatabaseReference mNotificationMessagesReference;
    // Message in playing game
    public static DatabaseReference mMessageReference;
    // Setting while playing game
    public static DatabaseReference mSettingCardsOnlineJokerDrawReference;
    // Playing game Click on cards
    public static DatabaseReference mPlayerClickableValuesReference;
    // Information Admin
    public static DatabaseReference mAdminNameReference;
    // Version of the game
    public static DatabaseReference mGameVersionReference;
    // Player ready playing game
    public static DatabaseReference mReadyPlayingGameReference; // prevent on user press on home button
    // Check database version when database change to new Version
    public static DatabaseReference mDatabaseVersionReference;
    // Application version
    public static DatabaseReference mAppUpdateStringReference;
    // Check status of database problem
    public static DatabaseReference mManageOnlineJokerDrawReference;
    // Fix database time
    public static DatabaseReference mUnderMaintenanceTimeReference;


    // Firebase static String path
    // Under maintenance path
    public static String mTreeUnderMaintenance = "UnderMaintenanceDate/";
    // path of update database
    public static String mTreeUpdateMessage = "UpdateMessage/";
    // ຫົວຂໍ້ທີ່ໄດ້ອັບເດດ ແມ່ນຫຍັງ
    public static String mTreeStringHeader = "UpdateHeader";
    // ຄໍາອະທິບາຍ ການອັບເດດຂອງ ຖານຂໍ້ມຸນ
    public static String mTreeStringDescription = "UpdateDescription";
    // path firebase version
    public static String mTreeFirebaseVersion = "FirebaseVersion/";
    // path player ready playing game
    public static String mTreeFirebaseReadyPlayingPath = "GroupInfo/Ready/";
    // path online status
    public static String mTreeFirebaseStatusOnlineModePath = "Status/Online/";
    // path lobby status
    public static String mTreeFirebaseStatusLobbyModePath = "Status/Lobby/";
    // path player information
    public static String mTreeFirebasePlayerRegisterPath = "PlayerRegister/ID/";
    // path player waiting room
    public static String mTreeFirebaseGroupInfoPlayerLobbyPath = "GroupInfo/Lobby/"; // players
    // path player playing game
    public static String mTreeFirebaseGroupInfoPlayerPlayingPath = "GroupInfo/Playing/"; // player
    // path value information
    public static String mTreeFirebaseGroupInfoPlayGamePath = "GroupInfo/Values/";
    // path group information
    public static String mTreeFirebaseGroupListsPath = "GroupLists";
    // path Notification message
    public static String mTreeFirebaseNotificationMessagePath = "Message/";
    // Group password key
    public static String PLAYERPASSWORD = "PASSWORD";
    // path admin of the group
    public static String mTreeAdmin = "/admin";
    // Group name key
    public static String GROUPNAME = "GroupName";
    // Group password key
    public static String GROUPPASSWORD = "GroupPassword";
    // path message joining group or create group
    public static String mTreeMassage = "/message";
    // path setting in playing joker draw
    public static String mTreeSettings = "/settings";
    // path value of player click on cards
    public static String mTreeClickable = "/Clickable";
    // path game version
    public static String mTreeGameVersion = "GameVersion/";
    // path management database in app
    public static String mTreeManageOnlineJokerDraw = "Manage";
    // limit player can join the group
    public static int mLimitPlayerInGroupNumber = 8;

    // ສະຖານະຂອງ ຖານຂໍ້ມຸນ ຂອງ firebase party draw game

    // status normal nothing happened
    public static final int mManageNormalStatus = 0;
    // status ban new player can not register and old player can play only
    public static final int mManageBanNewPlayerStatus = 1;
    // status onMaintenance new playe and old player can not play the dou to set to other status
    public static final int mManageOnMaintenanceNotAlwaysStatus = 2;
    // status block all player can not play at all in online joker draw
    public static final int mManageOutOfServiceStatus = 3;

    /*
        ທຸກເທື່ອທີ່ ຖານຂໍ້ມຸນມີການປ່ຽນແປງໂຄງຮ່າງ ແລະ ເວີເຊີນ ຈະຕ້ອງປ່ຽນ
    */
    public static final int mDatabaseVersion = 101;   // Firebase database ຖ້າຕ້ອງການ ປ່ຽນ ເວີເຊີນຂອງເດີຕາເບດ ແມ່ນ ປ່ຽນນີ້ ແລະ firebase ໃຫ້ກົງກັນ.

    // Memory Data variable
    // check status of the activity
    public static boolean isActive;  // set boolean make it static for stop and start BGM
    // check status of the switch
    private boolean isSwitchBGM, isSwitchFX;
    // set time to exit
    private long mBackPressTimes;
    // id number of the languages
    private int mNumberLanguage;
    // check sound effect is on or off
    public boolean isEffectSound;
    // check background music is on or off
    public boolean isBGMSound;
    // get key of Local language
    public String getLanguage;
    // check the toast message show or not
    public boolean isBackToastShow = false;
    // check permission of the application
    private boolean mPermissions  = false;
    // player register id player on online joker draw
    public static String mPlayerRegisterId;
    // set or remove status of the player playing online joker draw
    private ManagePlayerStatus mManagePlayerStatus;
    // Group name of online joker draw
    public static String mGroupName;
    // Group password of online joker draw
    public static String mPassword;
    // check internet is on or off
    private InternetProvider mInternetProvider;
    // check internet
    private boolean isConnectToTheInternet = false;
    // get firebase version
    private String mFirebaseAppVersion = "";
    // check player allow the keep display dialog
    private boolean isAllow = false;
    // get new Version of the application
    private String mNewVersion = "";
    // get application version
    private String mDisplayVersion = "";


    // UI variable
    // variable Button UI
    private Button mPlayButton,mSetting_MainButton,mHelpButton,mFacebookButton;
    // image variable
    private ImageView mImageICTLAO;
    // button variable
    private Button mOk_main_settingButton, mCancel_main_settingButton;
    // popup menu select language variable
    private PopupMenu mPopupMenuChangeLanguage;

    private Spinner mChangeLangageSpinner = null;
    // setting alert dialog
    private AlertDialog mDialogSettings;
    // switch background music
    private Switch mSwitchBGMSound;
    // toast message variable
    private Toast mToastMessage;
    // switch sound effect variable
    private Switch mSwitchEffectSound;
    // powerManager variable
    private PowerManager mPowerManager;
    // Activity variable
    private ActivityManager mActivityManager;
    // shared object variable
    public SharedPreferences mSharedPreferences;
    // editor variable
    public SharedPreferences.Editor mEditor;
    // MediaPlayer background music variable
    public static MediaPlayer mBackgroundMusics = null;
    // intent service variable
    private Intent mService;
    // text view variable
    private TextView mVersion;
    // dialog confirm change version variable
    private AlertDialog mDialogChangeConfirmVersion;
    // textView update version variable
    private TextView mTextViewDisplayUpdateVersion;
    // ok and cancel button variable
    private Button mOKVersion, mCancelVersion;
    // checkBox not display dialog next time variable
    private CheckBox mCheckBoxAllow;

    // String SharePreference
    // Background music key
    public static String BGM = "BGM";
    // Sound effect key
    public static String FX = "FX";
    // Language key
    public static String DL = "Language";
    // Shared object key
    public static String Settings = "SoundSettings";
    // permission request code
    private final int REQUEST_PERMISSION_CODE = 3000;
    // Player online joker draw key
    public static String PLAYERID = "PLAYERID";
    // Player allow dialog key
    private String ALLOW = "ALLOW";
    // Application NewVersion key
    private String NewVersion = "NewVersion";


    // get local language to display resource language
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hide toolbar
        getSupportActionBar().hide(); // Toolbar ບໍ່ສະແດງຜົນ. !
        // keep the screen on when open this game
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // initialize player online status
        mStatus_Online_Reference = FirebaseDatabase.getInstance().getReference(mTreeFirebaseStatusOnlineModePath);
        // initialize player lobby status
        mStatus_Lobby_Reference = FirebaseDatabase.getInstance().getReference(mTreeFirebaseStatusLobbyModePath);
        // Initialize internet provider class
        mInternetProvider = new InternetProvider(HomeActivity.this);
        // check internet on or off from provider class
        isConnectToTheInternet = mInternetProvider.isConnected();
        //initialize Player status class
        mManagePlayerStatus = new ManagePlayerStatus(HomeActivity.this);
        // ນໍາເອົາຕົວປ່ຽນ ມາເຊື່ອມ ໂຍງ ກັບໄອດີ ແລະ ກໍານົດການທໍາງານ.
        // get id gear setting button
        mSetting_MainButton = findViewById(R.id.settingMain);
        // get id ictlao image
        mImageICTLAO = findViewById(R.id.ictlaosoft);
        // get id play game button
        mPlayButton = findViewById(R.id.play);
        // get id help icon
        mHelpButton = findViewById(R.id.help_button);
        // get id facebook icon
        mFacebookButton = findViewById(R.id.facebook_button);
        // get Power service
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // get id version
        mVersion = findViewById(R.id.version);
        // all control background music
        // initialize shared object
        mSharedPreferences = getSharedPreferences(Settings, 0);
        // check sound effect on or off
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        // check background music on or off
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        // check player allow display dialog or not
        isAllow = mSharedPreferences.getBoolean(ALLOW, false);
        // get intent service class
        mService = new Intent(HomeActivity.this, MyFirebaseInstanceIDService.class);
        // get player register id
        mPlayerRegisterId = mSharedPreferences.getString(PLAYERID,"");
        // check if application launcher on tasks
        if (!isTaskRoot() && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER) && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        //get local language from helper class
        getLanguage = LocaleHelper.getLanguage(this);
        // initialize editor
        mEditor = mSharedPreferences.edit();
        // save local language to shared object
        mEditor.putString(DL, getLanguage);
        // end command
        mEditor.commit();
        // set click to play button
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start select game activity
                StartSelectGameToPlay();
            }
        });
        // set click to image view
        mImageICTLAO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if toast message show  then cancel it
                if(isBackToastShow){
                    mToastMessage.cancel();
                    isBackToastShow = false;
                }
                // start to website uri
                Intent website = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ictlao-soft.com/"));
                startActivity(website);
            }
        });
        // ຕັ້ງຄ່າໃຫ້ ລີ້ງໄປຫາ Video ຫຼື ຄູ່ມືນໍາໃຊ້.
        mHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBackToastShow){
                    mToastMessage.cancel();
                    isBackToastShow = false;
                }
                // new link https://www.youtube.com/watch?v=opHPzEhMvD8
                // old link https://www.youtube.com/watch?v=fjPVj0XQHeo
                // start website uri
                Intent GotoYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=opHPzEhMvD8"));
                startActivity(GotoYoutube);
            }
        });
        // facebook link
        mFacebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBackToastShow){
                    mToastMessage.cancel();
                    isBackToastShow = false;
                }
                // start website uri
                Intent Gotofacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/ictlaosoft/"));
                startActivity(Gotofacebook);
            }
        });
        // setting Button
        mSetting_MainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open setting dialog
                HomeSetting();
            }
        });
        // get version android
        try{
            // get application information to show the version of the application
            PackageInfo packageInfo = HomeActivity.this.getPackageManager().getPackageInfo(getPackageName(),0);
            // get version name
            mDisplayVersion = packageInfo.versionName;
            // show version
            setVersionLanguageChange(mDisplayVersion);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        // initialize application start for first time
        Initialize();
    }
    // show the application version name
    private void setVersionLanguageChange(String version){
        String language = mSharedPreferences.getString(DL,"en");
        if(mVersion != null && !version.equals("")){
            if(language.equals("en")){
                mVersion.setText("Version : " + version);
            }else {
                mVersion.setText("ເວີເຊີນ : " + version);
            }
        }
    }
    // setting dialog
    private void HomeSetting(){
        if(isBackToastShow){
            mToastMessage.cancel();
            isBackToastShow = false;
        }

        if(mDialogSettings != null)
        {
            mDialogSettings = null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.setting_layout,null);

        // ເຊື່ອມໂຍງໄອດີ ກັບຕົວປ່ຽນທີ່ກໍານົດໄວ້.
        mSwitchBGMSound = view.findViewById(R.id.switchOnAndOff);
        mSwitchEffectSound = view.findViewById(R.id.effect_sound);
        mChangeLangageSpinner = view.findViewById(R.id.Language);
        mOk_main_settingButton = view.findViewById(R.id.Ok_main_setting);
        mCancel_main_settingButton = view.findViewById(R.id.cancel_main_setting);

        // set switch ໃຫ້ເປັນ on ຫຼື off
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        isSwitchBGM = isBGMSound;
        isSwitchFX = isEffectSound;
        mSwitchBGMSound.setChecked(isBGMSound);
        mSwitchEffectSound.setChecked(isEffectSound);

        // get default Language
        String getLanguage = LocaleHelper.getLanguage(HomeActivity.this);
        final boolean isEn;
        isEn = getLanguage.equals("en");


        onSelectLanguageChange(isEn);

        if(isEn){
            mSwitchBGMSound.setText("BGM");
            mSwitchEffectSound.setText("Sound FX");
            mOk_main_settingButton.setText("OK");
            mCancel_main_settingButton.setText("Cancel");
        }else {
            mSwitchBGMSound.setText("ສຽງເພງ");
            mSwitchEffectSound.setText("ສຽງປະກອບ");
            mOk_main_settingButton.setText("ຕົກລົງ");
            mCancel_main_settingButton.setText("ຍົກເລີກ");
        }

        mSwitchEffectSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSwitchEffectSound.setChecked(true);
                }else{
                    mSwitchEffectSound.setChecked(false);
                }
                isSwitchFX = isChecked;
            }
        });
        // switch on or off
        mSwitchBGMSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSwitchBGMSound.setChecked(true);
                }else{
                    mSwitchBGMSound.setChecked(false);
                }
                isSwitchBGM = isChecked;
            }
        });

        // set Event Click ໃຫ້ກັບປູ່ມ OK
        mOk_main_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // language
                if(mNumberLanguage == 2 && isEn == false) {
                    if(isSwitchBGM != isBGMSound && isSwitchFX == isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }else if(isSwitchFX != isEffectSound && isSwitchBGM == isBGMSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.commit();
                    }else if(isSwitchBGM != isBGMSound && isSwitchFX != isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }
                    mDialogSettings.dismiss();
                    updateView("en");
                }else if(mNumberLanguage == 1 && isEn == true){
                    if(isSwitchBGM != isBGMSound && isSwitchFX == isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }else if(isSwitchFX != isEffectSound && isSwitchBGM == isBGMSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.commit();
                    }else if(isSwitchBGM != isBGMSound && isSwitchFX != isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }
                    mDialogSettings.dismiss();
                    updateView("lo-rLA");
                }else{
                    if(isSwitchBGM != isBGMSound && isSwitchFX == isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }else if(isSwitchFX != isEffectSound && isSwitchBGM == isBGMSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.commit();
                    }else if(isSwitchBGM != isBGMSound && isSwitchFX != isEffectSound){
                        mEditor = mSharedPreferences.edit();
                        mEditor.putBoolean(FX, isSwitchFX);
                        mEditor.putBoolean(BGM, isSwitchBGM);
                        mEditor.commit();
                        PlaySoundMusic();
                    }
                    mDialogSettings.dismiss();
                }
            setVersionLanguageChange(mDisplayVersion);
            }
        });
        // set Event Click ໃຫ້ກັບປູ່ມ Cancel
        mCancel_main_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberLanguage = 0;
                mDialogSettings.dismiss();
            }
        });

        builder.setView(view);
        mDialogSettings = builder.create();
        mDialogSettings.show();
    }

    // set flags image and country name to spinner to select languages
    private void onSelectLanguageChange(boolean isEN)
    {
        final int[] flags = {R.drawable.laos_flag, R.drawable.english_flag};
        String[] countries = {"Lao language", "English language"};
        if(!isEN){
            countries = new String[]{"ພາສາລາວ","ພາສາອັງກິດ"};
        }
        AdapterChangeLanguages adapter = new AdapterChangeLanguages(this, flags, countries);
        mChangeLangageSpinner.setAdapter(adapter);
        if(isEN){
            mChangeLangageSpinner.setSelection(1);
        }
        mChangeLangageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onRealTimeChangeLanguage(flags[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // play music
    private void PlaySoundMusic(){
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        if(isBGMSound) {
            mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
            mBackgroundMusics.setLooping(true);
            mBackgroundMusics.start();
        }else{
            if(mBackgroundMusics != null) {
                mBackgroundMusics.stop();
                mBackgroundMusics.release();
                mBackgroundMusics = null;
            }
        }
    }

    // show language in real time selected
    private void onRealTimeChangeLanguage(int flag)
    {
        switch (flag){
            case R.drawable.laos_flag:
                mSwitchBGMSound.setText("ສຽງເພງ");
                mSwitchEffectSound.setText("ສຽງປະກອບ");
                mOk_main_settingButton.setText("ຕົກລົງ");
                mCancel_main_settingButton.setText("ຍົກເລີກ");
                mNumberLanguage = 1;
                break;

            case R.drawable.english_flag:
                mSwitchBGMSound.setText("BGM");
                mSwitchEffectSound.setText("Sound FX");
                mOk_main_settingButton.setText("OK");
                mCancel_main_settingButton.setText("Cancel");
                mNumberLanguage = 2;
                break;
        }
    }

    // Method ນີ້ ແມ່ນ ໃຫ້ Navigate ຫາ ໜ້າເລືອກເກມ or Class SelectGame
    @SuppressLint("NewApi")
    private void StartSelectGameToPlay(){
        if(isBackToastShow){
            mToastMessage.cancel();
            isBackToastShow = false;
        }
        Intent intent = new Intent(HomeActivity.this, SelectGame.class);
        startActivity(intent);
    }

    private void updateView(String language) {
        try{
            mEditor = mSharedPreferences.edit();
            mEditor.putString(DL, language);
            mEditor.commit();
            LocaleHelper localeHelper = new LocaleHelper();
            localeHelper.setLanguage(this, language);
        }catch (Exception e){

        }
    }
    //Method ນີ້ ແມ່ນການ ເຮັດໃຫ້ ເປັນ ການ ຍືນຍັນໃນການ ອອກຈາກ ເກມ.
    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        mPlayerRegisterId = mSharedPreferences.getString(PLAYERID,"");
        getLanguage = LocaleHelper.getLanguage(this);
        if(mBackPressTimes +2000 > System.currentTimeMillis()){
            super.onBackPressed();
            mToastMessage.cancel();
            isBackToastShow = false;
            if(!mPlayerRegisterId.equals("")) {
                mManagePlayerStatus.removeStatusOnline(mPlayerRegisterId);
                mManagePlayerStatus.removeStatusLobby(mPlayerRegisterId);
            }
            finishAffinity();
        }else{
            isBackToastShow = true;
            View view = getLayoutInflater().inflate(R.layout.message_exit_layout, (ViewGroup) findViewById(R.id.exit_root));
            TextView exit_message = view.findViewById(R.id.exit_message);
            if(getLanguage.equals("en")){
                exit_message.setText("Press back again to exits!");
            }else{
                exit_message.setText("ກົດປູ່ມກັບອີກຄັ້ງເພື່ອອອກ");
            }
            mToastMessage = new Toast(getApplicationContext());
            mToastMessage.setGravity(Gravity.BOTTOM,0,0);
            mToastMessage.setView(view);
            mToastMessage.setDuration(Toast.LENGTH_LONG);
            mToastMessage.show();
        }
        mBackPressTimes = System.currentTimeMillis();
    }

    // ແມ່ນການສັ່ງຢຸດການທໍາງານຂອງ mediaPlayer ຫຼັງຈາກອອກຈາກແອັບແລ້ວ
    @Override
    protected void onPause() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        isActive = false;
        Context context = getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                if(isBGMSound){
                    mBackgroundMusics.stop();
                    mBackgroundMusics.release();
                    mBackgroundMusics = null;
                }
            } else {
                // go to another activity
            }
        }
        if (!mPowerManager.isScreenOn()) {
            if (isBGMSound){
                mBackgroundMusics.stop();
                mBackgroundMusics.release();
                mBackgroundMusics = null;
            }
        }

        if(mDialogSettings != null){
            mDialogSettings.dismiss();
            mDialogSettings = null;
        }

        if(mDialogChangeConfirmVersion != null){
            mDialogChangeConfirmVersion.dismiss();
            mDialogChangeConfirmVersion = null;
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        getLanguage = LocaleHelper.getLanguage(this);
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
       if(mBackgroundMusics == null){
            if(isBGMSound){
                mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                mBackgroundMusics.setLooping(true);
                mBackgroundMusics.start();
            }
       }

       if(mPowerManager.isScreenOn()){
           if(mBackgroundMusics == null){
               if(isBGMSound){
                   mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                   mBackgroundMusics.setLooping(true);
                   mBackgroundMusics.start();
               }
           }
       }

        isActive = true;
        if(isConnectToTheInternet){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startService(new Intent(HomeActivity.this, OreoService.class));
            }else{
                startService(mService);
            }
            FirebaseChangeVersion();
        }
        super.onResume();
    }


    @Override
    protected void onStart() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        if(mBackgroundMusics == null){
            if(isBGMSound){
                mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
                mBackgroundMusics.start();
                mBackgroundMusics.setLooping(true);
            }
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        if(mBackgroundMusics != null && !(HomeActivity.isActive || SelectGame.isActive || JokerGame.isActive || User_Player_Lucky_Game.isActive || UserPlayerSwapGame.isActive || LuckyGame.isActive || SwapGame.isActive|| CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                mBackgroundMusics.stop();
                mBackgroundMusics.release();
                mBackgroundMusics = null;
            }
        }
        super.onStop();
    }

    private void Initialize(){
        if(mPermissions){
            //PlaySoundMusic();
        }else{
            VerifyPermissions();
        }

        if(!mPlayerRegisterId.equals("") && isConnectToTheInternet){
            mManagePlayerStatus.removeStatusLobby(mPlayerRegisterId);
            mManagePlayerStatus.setStatusOnline(mPlayerRegisterId);
        }
    }


    private void VerifyPermissions(){
        String[] permissions = {
                Manifest.permission.INTERNET
        };
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            mPermissions = true;
            Initialize();
        }
        else
        {
            ActivityCompat.requestPermissions(
                    HomeActivity.this,
                    permissions,
                    REQUEST_PERMISSION_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSION_CODE){
            Initialize();
        }else{
            VerifyPermissions();
        }

    }

    private void FirebaseChangeVersion(){
        mGameVersionReference = FirebaseDatabase.getInstance().getReference(mTreeGameVersion);
        mGameVersionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isAllow = mSharedPreferences.getBoolean(ALLOW, false);
                mNewVersion = mSharedPreferences.getString(NewVersion, "");
                String v = mDisplayVersion;

                String[] vsplit = v.split("\\.");

                mFirebaseAppVersion = dataSnapshot.getValue().toString();
                String[] mFirebaseVersion = mFirebaseAppVersion.split("\\.");

                boolean isNeedtoUpdate = false;
                int index = 0;
                while (index < vsplit.length){
                    if(vsplit.length <= mFirebaseVersion.length){
                        if(Integer.parseInt( vsplit[index]) < Integer.parseInt(mFirebaseVersion[index])) {
                            isNeedtoUpdate = true;
                            break;
                        }
                    }
                    index++;
                }

                if(isNeedtoUpdate && isAllow == false && mNewVersion.equals("")){
                    OpenDialogConfirmChangeVersion(mFirebaseAppVersion);
                }else{
                    if(isAllow && !mNewVersion.equals("") && isNeedtoUpdate){
                        String[] newSplit = mNewVersion.split("\\.");
                        boolean isNewUpdated = false;
                       for(int i = 0; i < newSplit.length; i++){
                           if(Integer.parseInt(newSplit[i]) < Integer.parseInt(mFirebaseVersion[i])){
                               isNewUpdated = true;
                               break;
                           }
                       }

                       if(isNewUpdated){
                           OpenDialogConfirmChangeVersion(mFirebaseAppVersion);
                       }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenDialogConfirmChangeVersion(final String new_version){
        if(mDialogChangeConfirmVersion != null){
            mDialogChangeConfirmVersion.dismiss();
            mDialogChangeConfirmVersion = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.version_change_confirm_layout, null);


        String language = mSharedPreferences.getString(DL, "en");
        isAllow = mSharedPreferences.getBoolean(ALLOW, false);

        mTextViewDisplayUpdateVersion = view.findViewById(R.id.show_version_to_update);
        mOKVersion = view.findViewById(R.id.Ok_version);
        mCancelVersion = view.findViewById(R.id.Cancel_version);
        mCheckBoxAllow = view.findViewById(R.id.showMessage_allow);

        if(language.equals("en")){
            mTextViewDisplayUpdateVersion.setText("Would you like to update new version "+new_version+ " or not ?");
            mOKVersion.setText("Ok");
            mCancelVersion.setText("Cancel");
            mCheckBoxAllow.setText("Do not show this massage again.");
        }else{
            mTextViewDisplayUpdateVersion.setText("ທ່ານຕ້ອງການອັບເດດເປັນເວີເຊີນໃໝ່ "+new_version+" ຫຼື ບໍ່ ?");
            mOKVersion.setText("ຕົກລົງ");
            mCancelVersion.setText("ຍົກເລີກ");
            mCheckBoxAllow.setText("ບໍ່ສະແດງ ໜ້ານີ້ອີກ.");
        }
        mOKVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogChangeConfirmVersion.dismiss();
                Intent GalaxyStore = new Intent(Intent.ACTION_VIEW, Uri.parse("http://apps.samsung.com/appquery/appDetail.as?appId=com.ictlao.partydraw"));
                startActivity(GalaxyStore);
            }
        });

        mCancelVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogChangeConfirmVersion.dismiss();
            }
        });

        mCheckBoxAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean(ALLOW, isChecked);
                    mEditor.putString(NewVersion, new_version);
                    mEditor.commit();
                }else{
                    mEditor = mSharedPreferences.edit();
                    mEditor.putBoolean(ALLOW,isChecked);
                    mEditor.putString(NewVersion, new_version);
                    mEditor.commit();
                }
            }
        });

        builder.setView(view);
        mDialogChangeConfirmVersion = builder.create();
        mDialogChangeConfirmVersion.show();
        mDialogChangeConfirmVersion.setCancelable(false);
    }
}