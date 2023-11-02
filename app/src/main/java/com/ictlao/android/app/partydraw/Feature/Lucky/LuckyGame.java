package com.ictlao.android.app.partydraw.Feature.Lucky;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ictlao.android.app.partydraw.Core.CustomAdapter;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.Models.Item;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LuckyGame extends AppCompatActivity {
    // Memory data variable
    private ArrayList<String> mGetDataNameList;
    private ArrayList<String> mStoreDataToListView;
    private ArrayList<String> mStoreDataForRepeat;
    private ArrayList<String> mGetDataListToLuckyPeople;
    private ArrayList<String> mGetAddNewNames;
    private ArrayAdapter<String> mArrayAdapter;
    private Random mRandom;
    private int mResult, mDataNameListSizes;
    private boolean IsSpinClick;
    private int mIndexNameList;
    public static boolean isActive; // set boolean make it static for stop and start BGM
    private int mNumberLuckPeople = 1;
    private int mIndexListName;
    private boolean isFinish = false;
    private boolean isAdded = false;
    private ArrayAdapter<String> mAarrayAdapterRunTime;
    private CustomAdapter mCustomAdapter;
    private boolean isEffectSound;
    private boolean isBGMSound;
    private boolean isToastShow = false;

    // String SharePreference
    private final String BGM = "BGM";
    private final String FX = "FX";

    // UI variable
    private ListView mListView; // List view ເພື່ອສະແດງຂໍ້ມຸນລາຍຊື່
    private Button mRepeatButton, mExitButton, mSpinButton, mShowNameListButton;
    private TextView mSpinTextView;
    private AlertDialog mDialogFinishGame;
    private Button mOkFinishButton;
    private AlertDialog mDialogNameList;
    private ListView mListViewLuckyPeoPle;
    private Button mExitLuckyDialogButton;
    private AlertDialog mDialogDelete;
    private AlertDialog mDialogAddNameRunTime, mDialogDeleteRunTime;
    private Button mAddNameButtonRunTime, mOkDelete, mCancelDelete;
    private Button mExitDeleteButton, mAddNameButton;
    private EditText mInputNameRunTime;
    private ListView mListViewRunTime;
    private TextView mConfirmDeleteMessage;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mSharedPreferences;
    private MediaPlayer mMediaPlayer;
    private Toast mToastMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));  // ເອີ້ນ method ນີ້ ມາໃຊ້ ເພື່ອໃຫ້ປ່ຽນພາສາ
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lucky_game);
        getSupportActionBar().hide(); // ບໍ່ສະແດງ  toolbar

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // ກໍານົດຄ່າ ແລະ ເຊື່ອມໂຍງ ໄອດີ ໃຫ້ກັບຕົວປ່ຽນ.
        mRandom = new Random();
        mGetDataNameList = new ArrayList<>();
        mStoreDataToListView = new ArrayList<>();
        mStoreDataForRepeat = new ArrayList<>();
        mRepeatButton = findViewById(R.id.repeat_lucky_game);
        mExitButton = findViewById(R.id.exit_lucky_game);
        mListView = findViewById(R.id.ListPayers_lucky);
        mSpinButton = findViewById(R.id.roll_button);
        mSpinTextView = (TextView) findViewById(R.id.rollingText2);
        mShowNameListButton = findViewById(R.id.list_data_lucky);
        mGetAddNewNames = new ArrayList<>();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);

        mMediaPlayer = MediaPlayer.create(LuckyGame.this, R.raw.wood_sound);
        // ຮັບ ເອົາຂໍ້ມຸນ ຫຼື ຊື່ຂອງຜູ້ຫຼີ້ນເກມ ຈາກ class User_Player_Lucky_Game
        mGetDataNameList = getIntent().getStringArrayListExtra("Names");
        mGetDataListToLuckyPeople = new ArrayList<String>(mGetDataNameList);

        // ສ້າງ Event ໃຫ້ ປູ່ມ RepeatLuckyGame
        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RepeatLuckyGameMethod();
            }
        });
        // ສ້າງ Event ໃຫ້ ປູ່ມ ExitLuckyGame
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitLuckyGameMethod();
            }
        });
        // ສ້າງ Event ໃຫ້ ປູ່ມ Spin ໂດຍໃຫ້ ມີເງື່ອນໄຂ ວ່າ ຈໍານວນ ຂໍ້ມຸນ ຕ້ອງ ຫຼາຍກວ່າ ສຸນ ຖ້າເທົ່າກັບ 0 ແມ່ນ ໃຫ້ ຈົບເກມ.
        mSpinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGetDataNameList.size() == 0){
                    mSpinTextView.setText(R.string.LuckyGameActivitySpinButtonLucky);
                    mRepeatButton.setBackgroundResource(R.drawable.repeat_button);
                }else {
                    IsSpinClick = true;
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1){
                        AnimationSpin1();
                    }else{
                        AnimationFirst();
                    }
                }
            }
        });

        mShowNameListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogListDataLuckyPeople();
            }
        });
    }

    private void CustomToastMessage(String message){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        isToastShow = true;
        View view = getLayoutInflater().inflate(R.layout.toast_message_waring, (ViewGroup) findViewById(R.id.show_message));
        TextView body = view.findViewById(R.id.show_message_body);
        body.setText(message);
        mToastMessage = new Toast(getApplicationContext());
        mToastMessage.setGravity(Gravity.BOTTOM,0,0);
        mToastMessage.setView(view);
        mToastMessage.setDuration(Toast.LENGTH_LONG);
        mToastMessage.show();
    }

    @SuppressLint("NewApi")
    private void AnimationFirst(){
        mDataNameListSizes = mGetDataNameList.size();// ນໍາເອົາ ຈໍານວນ ຂອງຂໍ້ມຸນ ທັງໝົດມາເກັບໄວ້ໃນຕົວປ່ຽນ count
        mSpinButton.setEnabled(false);
        mShowNameListButton.setEnabled(false);
        mRepeatButton.setEnabled(false);
        mExitButton.setEnabled(false);
        StartSound();
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() { // 20
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationSecond();
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void AnimationSecond(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {  // 50
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationThird();
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void AnimationThird(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {  // 100
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationFourth();
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void AnimationFourth(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(400).withEndAction(new Runnable() {
            @Override
            public void run() { //150
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationFifth();
                    }
                });
            }
        });
    }
    @SuppressLint("NewApi")
    private void AnimationFifth(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() { //200
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationSixth();
                    }
                });
            }
        });
    }
    @SuppressLint("NewApi")
    private void AnimationSixth(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(600).withEndAction(new Runnable() {
            @Override
            public void run() { //250
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationSeventh();
                    }
                });
            }
        });
    }
    @SuppressLint("NewApi")
    private void AnimationSeventh(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(700).withEndAction(new Runnable() {
            @Override
            public void run() {  //270
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationEighth();
                    }
                });
            }
        });
    }
    @SuppressLint("NewApi")
    private void AnimationEighth(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(800).withEndAction(new Runnable() {
            @Override
            public void run() { // 350
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center);
                        mSpinTextView.animate().scaleY(1f);
                        AnimationNinth();
                    }
                });
            }
        });
    }

    @SuppressLint("NewApi")
    private void AnimationNinth(){
        mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
        mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
        mSpinTextView.setBackgroundResource(R.drawable.center);
        mSpinTextView.animate().scaleY(1f).setDuration(850).withEndAction(new Runnable() {
            @Override
            public void run() { //450
                mSpinTextView.setBackgroundResource(R.drawable.center1);
                mSpinTextView.animate().scaleY(0f).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        int reRandom = mRandom.nextInt(mDataNameListSizes);
                        mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                        mSpinTextView.setBackgroundResource(R.drawable.center1);
                        mIndexNameList = mGetDataNameList.indexOf(mSpinTextView.getText().toString());
                        mSpinTextView.animate().scaleY(1f).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                RunRandomMethod();
                            }
                        });
                    }
                });
            }
        });
    }

    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 1---------------------
    @SuppressLint("NewApi")
    private void AnimationSpin1(){
        mDataNameListSizes = mGetDataNameList.size();// ນໍາເອົາ ຈໍານວນ ຂອງຂໍ້ມຸນ ທັງໝົດມາເກັບໄວ້ໃນຕົວປ່ຽນ count
        // disable button when animation is running.
        mSpinButton.setEnabled(false);
        mShowNameListButton.setEnabled(false);
        mRepeatButton.setEnabled(false);
        mExitButton.setEnabled(false);
        try{
            mResult = mRandom.nextInt(mDataNameListSizes); // ດໍາເນີນ Random ເອົາຕົວເລກ.
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f); // ຕັ້ງຄ່າໃຫ້ TextView ເປັນຕົວ Animation
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f); // ຕັ້ງຄ່າໃຫ້ TextView ເປັນຕົວ Animation
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());  // ອະນຸຍາດໃຫ້ ນໍາເອົາ ຂໍ້ມຸນເຂົ້າ ໃນ Animation
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator()); // ອະນຸຍາດໃຫ້ ສາມາດ ໃຫ້ຕັ້ງຄ່າ ເວລາໃນການໝຸນ
            mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            AnimationSpin2();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            StartSound();
            objectAnimatorTextNumber1.setDuration(100);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }

    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 2---------------------
    private void AnimationSpin2(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            AnimationSpin3();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(100);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }

    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 3---------------------
    private void AnimationSpin3(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            AnimationSpin4();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(150);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }
    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 4---------------------
    private void AnimationSpin4(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            AnimationSpin5();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(200);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }
    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 5---------------------
    private void AnimationSpin5(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            mIndexNameList = mGetDataNameList.indexOf(mSpinTextView.getText().toString());
                            AnimationSpin6();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(300);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }

    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 6---------------------
    private void AnimationSpin6(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            mIndexNameList = mGetDataNameList.indexOf(mSpinTextView.getText().toString());
                            AnimationSpin7();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(400);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }
    // SpinTexViewLuckyGame Animation ສະແດງຜົນ ຄັ້ງທີ 7---------------------
    private void AnimationSpin7(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            mIndexNameList = mGetDataNameList.indexOf(mSpinTextView.getText().toString());
                            AnimationSpin8();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(550);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }
    // SpinTextViewLuckyGame Animation 8
    private void AnimationSpin8(){
        try{
            mResult = mRandom.nextInt(mDataNameListSizes);
            final ValueAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 1f,0f);
            final ValueAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(mSpinTextView, "scaleY", 0f,1f);
            objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSpinTextView.setBackgroundResource(R.drawable.center);
                    mSpinTextView.setText(""+ mGetDataNameList.get(mResult));
                    objectAnimatorTextNumber2.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            int reRandom = mRandom.nextInt(mDataNameListSizes);
                            mSpinTextView.setText(""+ mGetDataNameList.get(reRandom));
                            mSpinTextView.setBackgroundResource(R.drawable.center1);
                            mIndexNameList = mGetDataNameList.indexOf(mSpinTextView.getText().toString());
                            RunRandomMethod();
                        }
                    });
                    objectAnimatorTextNumber2.start();
                }
            });
            objectAnimatorTextNumber1.setDuration(700);
            objectAnimatorTextNumber1.start();
        }catch (Exception e){

        }
    }

    // method Game over ສະແດງຜົນເມື່ອ ບໍ່ມີຂໍ່້ມຸນໃນ ArrayList
    private void GameOver(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.game_over_layout, null);

        mOkFinishButton = view.findViewById(R.id.ok_dialog_lucky_game);

        mOkFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinish = true;
                mDialogFinishGame.dismiss();
            }
        });
        builder.setView(view);
        mDialogFinishGame = builder.create();
        mDialogFinishGame.setCancelable(false);
        mDialogFinishGame.setCanceledOnTouchOutside(false);
        mDialogFinishGame.show();
    }

    // method ຈະທໍາການ ເລີ່ມເກມ ໃໝ່ ໂດຍ ມີເງື່ອນໄຂ ວ່າ ຈໍານວນ ຜູ້ຫຼີ້ນນັ້ນ ເທົ່າກັບ 0 ແລ້ວ. ແຕ່ຖ້າຫາກບໍ່ເທົ່າກັບ 0 ໃຫ້ ກັບຄືນ ສູ່ Class User_Player_lucky_Game
    private void RepeatLuckyGameMethod(){
        if(mGetDataNameList.size() == 0){
            mGetDataNameList = new ArrayList<>(mStoreDataForRepeat);
            mGetDataNameList.addAll(mGetAddNewNames);
            mGetAddNewNames.clear();
            mStoreDataToListView.clear();
            mStoreDataForRepeat.clear();
            mArrayAdapter.notifyDataSetChanged();
            mRepeatButton.setBackgroundResource(R.drawable.back_arrow);
            mSpinButton.setEnabled(true);
            mNumberLuckPeople = 1;
            mIndexListName = 0;
            isFinish = false;
            isAdded = false;
        }else{
            Intent intent = new Intent(LuckyGame.this, User_Player_Lucky_Game.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    // method ນີ້ ແມ່ນ ຖ້າຫາກຜູ່ນໍາໃຊ້ ກົດ ສັນຍາລັກ ໃນການອອກຈາກເກມ ແມ່ນ ຈະ ກັບ ໄປຫາ ໜ້າ ເລືອກເກມ ກໍຄື ຈະກັບໄປ ຫາ Class SelectGame
    private void ExitLuckyGameMethod(){
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        if(isEffectSound){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        Intent intent = new Intent(LuckyGame.this, SelectGame.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    // method ນີ້ ແມ່ນຈະ ທໍາການລົບ ລາຍຊື່ທີຖືກ ການ Random ແລະ ນໍາໄປກັບໄວ້ໃນ StoreDataForRepeat ເພື່ອ ໃຊ້ ໃນການເລີ່ມ ຫຼີ້ນໃໝ່.
    private void RunRandomMethod(){
        try{
            if(IsSpinClick == true){
                int getStringText2 = mIndexNameList;
                mStoreDataForRepeat.add(mGetDataNameList.get(getStringText2));
                mGetDataNameList.remove(getStringText2);
                mStoreDataToListView.add( mNumberLuckPeople + ". "+ mStoreDataForRepeat.get(mIndexListName));
                mArrayAdapter = new ArrayAdapter<String>(LuckyGame.this, android.R.layout.simple_list_item_1, mStoreDataToListView){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        if(position % 2 == 1){
                            view.setBackgroundColor(getResources().getColor(R.color.colorList));
                        }else{
                            view.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                        }
                        return view;
                    }
                };
                mListView.setAdapter(mArrayAdapter);
                if(mGetDataNameList.size() == 1){
                    mStoreDataToListView.add(mNumberLuckPeople +1 +". "+ mGetDataNameList.get(0));
                    mStoreDataForRepeat.add(mGetDataNameList.get(0));
                    mGetDataNameList.remove(0);
                    mRepeatButton.setBackgroundResource(R.drawable.repeat_button);
                    mSpinTextView.setText("Lucky");
                    GameOver();
                }
                mIndexListName++;
                mNumberLuckPeople++;
                mSpinButton.setEnabled(true);
                IsSpinClick = false;
                mShowNameListButton.setEnabled(true);
                mRepeatButton.setEnabled(true);
                mExitButton.setEnabled(true);
            }else{

            }
        }catch (Exception e){

        }
    }
    // method sound effect for animation
    private void StartSound(){
        if(isEffectSound){
            mMediaPlayer = MediaPlayer.create(LuckyGame.this, R.raw.wood_sound);
            mMediaPlayer.start();
        }
    }

    private void DialogListDataLuckyPeople(){
        try{
            final AlertDialog.Builder builder = new AlertDialog.Builder(LuckyGame.this);
            final View view1 = getLayoutInflater().inflate(R.layout.popup_data_swap_draw, null);

            final ArrayList<Item> Collecter = new ArrayList<>();
            mExitLuckyDialogButton = view1.findViewById(R.id.exit_list_swap_draw);
            mAddNameButtonRunTime = view1.findViewById(R.id.Add_people_run_time);
            mListViewLuckyPeoPle = view1.findViewById(R.id.list_data);

            if(mStoreDataForRepeat.size() != 0){
                if(isAdded == true){
                    for (int i = 0; i < mStoreDataForRepeat.size(); i++) {
                        Collecter.add(new Item(mStoreDataForRepeat.get(i), R.drawable.present));
                    }

                    for (int d = 0; d < mGetAddNewNames.size(); d++) {
                        Collecter.add(new Item(mGetAddNewNames.get(d), R.drawable.empty));
                    }
                    mCustomAdapter = new CustomAdapter(this, Collecter) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                view.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }
                            return view;
                        }
                    };
                    mListViewLuckyPeoPle.setAdapter(mCustomAdapter);
                }else {
                    for (int i = 0; i < mStoreDataForRepeat.size(); i++) {
                        Collecter.add(new Item(mStoreDataForRepeat.get(i), R.drawable.present));
                    }

                    for (int d = 0; d < mGetDataNameList.size(); d++) {
                        Collecter.add(new Item(mGetDataNameList.get(d), R.drawable.empty));
                    }
                    mCustomAdapter = new CustomAdapter(this, Collecter) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                view.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }
                            return view;
                        }
                    };
                    mListViewLuckyPeoPle.setAdapter(mCustomAdapter);
                }
                // delete item in list view
                if(isFinish == true && mGetDataNameList.size() == 0){
                    mListViewLuckyPeoPle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            if (Collecter.size() <= 2) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteMoreToastMessagePupupDataSwapDraw);
                                CustomToastMessage(getStringMessage);
                            } else {
                                if(isToastShow){
                                    mToastMessage.cancel();
                                    isToastShow = false;
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LuckyGame.this);
                                View view11 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                final TextView textMessage = view11.findViewById(R.id.show_delete_mesage);
                                Button ok, cancel;
                                ok = view11.findViewById(R.id.ok_delete_player);
                                cancel = view11.findViewById(R.id.cancel_delete_player);

                                textMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + Collecter.get(position).getNames() + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDelete.dismiss();
                                    }
                                });

                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = Collecter.get(position).getNames();
                                        mDialogDelete.dismiss();
                                        Collecter.remove(position);
                                        mStoreDataForRepeat.remove(name);
                                        mGetAddNewNames.remove(name);
                                        mGetDataListToLuckyPeople.remove(name);
                                        mCustomAdapter.notifyDataSetChanged();
                                    }
                                });

                                builder1.setView(view11);
                                mDialogDelete = builder1.create();
                                mDialogDelete.show();
                            }
                        }
                    });
                }else {
                    mListViewLuckyPeoPle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            String name = Collecter.get(position).getNames();
                            if (mGetDataListToLuckyPeople.size() <= 2) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                                CustomToastMessage(getStringMessage);
                            } else if (mStoreDataForRepeat.contains(name)) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + " " + name + " " + getString(R.string.LuckyGameActivityLuckyOne);
                                CustomToastMessage(getStringMessage);
                            }else if(mGetDataNameList.size() <= 2){
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                                CustomToastMessage(getStringMessage);
                            } else {
                                if(isToastShow){
                                    mToastMessage.cancel();
                                    isToastShow = false;
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LuckyGame.this);
                                View view11 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                final TextView textMessage = view11.findViewById(R.id.show_delete_mesage);
                                Button ok, cancel;
                                ok = view11.findViewById(R.id.ok_delete_player);
                                cancel = view11.findViewById(R.id.cancel_delete_player);

                                textMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + Collecter.get(position).getNames() + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDelete.dismiss();
                                    }
                                });

                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = Collecter.get(position).getNames();
                                        mDialogDelete.dismiss();
                                        mGetDataListToLuckyPeople.remove(name);
                                        mGetDataNameList.remove(name);
                                        Collecter.remove(position);
                                        mCustomAdapter.notifyDataSetChanged();
                                    }
                                });

                                builder1.setView(view11);
                                mDialogDelete = builder1.create();
                                mDialogDelete.show();
                            }
                        }
                    });
                }

            }else{
                for(int i = 0; i < mGetDataListToLuckyPeople.size(); i++) {
                    Collecter.add(new Item(mGetDataListToLuckyPeople.get(i), R.drawable.empty));
                    mCustomAdapter = new CustomAdapter(this, Collecter) {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View newView = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                newView.setBackgroundColor(getResources().getColor(R.color.colorList));
                            } else {
                                newView.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            }
                            return newView;
                        }
                    };
                    mListViewLuckyPeoPle.setAdapter(mCustomAdapter);
                    mListViewLuckyPeoPle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            String name = Collecter.get(position).getNames();
                            if (mGetDataListToLuckyPeople.size() <= 2) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                                CustomToastMessage(getStringMessage);
                            }else if (mStoreDataForRepeat.contains(name)) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + "\n " + Collecter.get(position).getNames()+".\n"+ getString(R.string.LuckyGameActivityLuckyOne);
                                CustomToastMessage(getStringMessage);
                            } else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LuckyGame.this);
                                View view11 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                final TextView textMessage = view11.findViewById(R.id.show_delete_mesage);
                                Button ok, cancel;
                                ok = view11.findViewById(R.id.ok_delete_player);
                                cancel = view11.findViewById(R.id.cancel_delete_player);

                                textMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + Collecter.get(position).getNames() + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDelete.dismiss();
                                    }
                                });

                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name = mGetDataListToLuckyPeople.get(position);
                                        mDialogDelete.dismiss();
                                        mGetDataListToLuckyPeople.remove(name);
                                        mGetDataNameList.remove(name);
                                        Collecter.remove(position);
                                        mCustomAdapter.notifyDataSetChanged();
                                    }
                                });

                                builder1.setView(view11);
                                mDialogDelete = builder1.create();
                                mDialogDelete.show();
                            }
                        }
                    });
                }
            }

            mExitLuckyDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogNameList.dismiss();
                }
            });
            //----------------------------------------------------------------------------------------
            // dialog add people at run time
            mAddNameButtonRunTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isToastShow){
                        mToastMessage.cancel();
                        isToastShow = false;
                    }
                    mDialogNameList.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LuckyGame.this);
                    View view = getLayoutInflater().inflate(R.layout.add_player_game, null);

                    mInputNameRunTime = view.findViewById(R.id.edit_add_name);
                    mExitDeleteButton = view.findViewById(R.id.exit_edit_dialog);
                    mListViewRunTime = view.findViewById(R.id.list_data_edit);
                    mAddNameButton = view.findViewById(R.id.Add_name);

                    mAarrayAdapterRunTime = new ArrayAdapter<String>(LuckyGame.this, android.R.layout.simple_list_item_1, mGetDataListToLuckyPeople){
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                            View view2 = super.getView(position, convertView, parent);
                            if(position % 2 == 1){
                                view2.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }else {
                                view2.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            }
                            return view2;
                        }
                    };
                    mListViewRunTime.setAdapter(mAarrayAdapterRunTime);
                    // -------------------- dialog delete and add people run time ----------------------
                    if(isFinish == true && mGetDataNameList.size() == 0){
                        mListViewRunTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (mGetDataListToLuckyPeople.size() <= 2) {
                                    String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteMoreToastMessagePupupDataSwapDraw);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    if(isToastShow){
                                        mToastMessage.cancel();
                                        isToastShow = false;
                                    }
                                    AlertDialog.Builder builder3 = new AlertDialog.Builder(LuckyGame.this);
                                    View view2 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                    mOkDelete = view2.findViewById(R.id.ok_delete_player);
                                    mCancelDelete = view2.findViewById(R.id.cancel_delete_player);
                                    mConfirmDeleteMessage = view2.findViewById(R.id.show_delete_mesage);
                                    mConfirmDeleteMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mGetDataListToLuckyPeople.get(position) + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));

                                    // dismiss dialog
                                    mCancelDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteRunTime.dismiss();
                                        }
                                    });
                                    // delete
                                    mOkDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteRunTime.dismiss();
                                            String name = mGetDataListToLuckyPeople.get(position);
                                            mGetDataListToLuckyPeople.remove(name);
                                            mGetAddNewNames.remove(name);
                                            mStoreDataForRepeat.remove(name);
                                            mAarrayAdapterRunTime.notifyDataSetChanged();
                                        }
                                    });

                                    builder3.setView(view2);
                                    mDialogDeleteRunTime = builder3.create();
                                    mDialogDeleteRunTime.show();
                                }
                            }
                        });
                    }else {
                        mListViewRunTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                String name = mGetDataListToLuckyPeople.get(position);
                                if (mStoreDataForRepeat.contains(name)) {
                                    String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + "\n " + name+".\n"+ getString(R.string.LuckyGameActivityLuckyOne);
                                    CustomToastMessage(getStringMessage);
                                } else if (mGetDataNameList.size() <= 2) {
                                    String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteMoreToastMessagePupupDataSwapDraw);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    if(isToastShow){
                                        mToastMessage.cancel();
                                        isToastShow = false;
                                    }
                                    AlertDialog.Builder builder3 = new AlertDialog.Builder(LuckyGame.this);
                                    View view2 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                    mOkDelete = view2.findViewById(R.id.ok_delete_player);
                                    mCancelDelete = view2.findViewById(R.id.cancel_delete_player);
                                    mConfirmDeleteMessage = view2.findViewById(R.id.show_delete_mesage);
                                    mConfirmDeleteMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mGetDataListToLuckyPeople.get(position) + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));

                                    // dismiss dialog
                                    mCancelDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteRunTime.dismiss();
                                        }
                                    });
                                    // delete
                                    mOkDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteRunTime.dismiss();
                                            String name = mGetDataListToLuckyPeople.get(position);
                                            mGetDataListToLuckyPeople.remove(name);
                                            mGetDataNameList.remove(name);
                                            mAarrayAdapterRunTime.notifyDataSetChanged();
                                        }
                                    });

                                    builder3.setView(view2);
                                    mDialogDeleteRunTime = builder3.create();
                                    mDialogDeleteRunTime.show();
                                }
                            }
                        });
                    }
                    // dismiss dialog
                    mExitDeleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogAddNameRunTime.dismiss();
                        }
                    });

                    //--------------------- add player to list game -------------------------------
                    mAddNameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Name = mInputNameRunTime.getText().toString();
                            if(isFinish == true){
                                if (Name.equals("") || Name == null || Name.trim().equals("")) {
                                   String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                                    CustomToastMessage(getStringMessage);
                                } else if (mGetDataListToLuckyPeople.contains(Name)) {
                                    String getStringMessage =  getString(R.string.This_name) + " " + Name + " " + getString(R.string.already_exits);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    isAdded = true;
                                    mGetAddNewNames.add(Name);
                                    mGetDataListToLuckyPeople.add(Name);
                                    mAarrayAdapterRunTime.notifyDataSetChanged();
                                }
                            }else {
                                if (Name.equals("") || Name == null || Name.trim().equals("")) {
                                     String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                                    CustomToastMessage(getStringMessage);
                                } else if (mGetDataListToLuckyPeople.contains(Name)) {
                                    String getStringMessage = getString(R.string.This_name) + " " + Name + " " + getString(R.string.already_exits);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    mGetDataNameList.add(Name);
                                    mGetDataListToLuckyPeople.add(Name);
                                    mAarrayAdapterRunTime.notifyDataSetChanged();
                                }
                            }
                            mInputNameRunTime.setText("");
                        }
                    });

                    builder1.setView(view);
                    mDialogAddNameRunTime = builder1.create();
                    mDialogAddNameRunTime.show();
                }
            });

            builder.setView(view1);
            mDialogNameList = builder.create();
            mDialogNameList.show();
        }catch (Exception e){
            e.toString();
        }
    }

    @Override
    protected void onPause() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
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
                if(isEffectSound){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
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
                if(isEffectSound){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            }
        }
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        isActive = false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
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
    protected void onStop() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        if(HomeActivity.mBackgroundMusics != null && !(User_Player_Lucky_Game.isActive || HomeActivity.isActive || SelectGame.isActive || UserPlayerSwapGame.isActive || JokerGame.isActive || SwapGame.isActive || LuckyGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }
        if(isEffectSound && mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        mMediaPlayer = MediaPlayer.create(LuckyGame.this, R.raw.wood_sound);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(LuckyGame.this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(LuckyGame.this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.start();
                    HomeActivity.mBackgroundMusics.setLooping(true);
                }
            }
        }
        isActive = true;
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        if(isEffectSound){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        Intent userlucky = new Intent(LuckyGame.this, User_Player_Lucky_Game.class);
        startActivity(userlucky);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(LuckyGame.this, User_Player_Lucky_Game.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}