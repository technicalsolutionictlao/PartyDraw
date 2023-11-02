package com.ictlao.android.app.partydraw.Feature.Joker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class JokerGame extends AppCompatActivity {
    // Memory data variable
    private Random mRandom;  // set random variable ເພື່ອ ສຸ່ມເອົາຕົວເລກ.
    private int mResult, mCardNumber = 1, mCardNumberDisable = 1, mCardNumberAfterEnd = 1;  // set number ຂອງປູ່ມ
    private int NUM_ROW ;  // ຕັ້ງຄ່າຈໍານວນຂອງແຖວ
    private int NUM_COL ;// ຕັ້ງຄ່າຈໍານວນຂອງຖັນ
    private int mSelectedColumn, mSelectedRow;
    public static boolean isActive;   // set boolean make it static for stop and start BGM
    private int mCatSoundExplosion, mDogSoundExplosion;
    private Handler mGameFinishHandler;
    private int mCopyResult;
    private boolean isEffectSound;
    private boolean isBGMSound;
    private int mCheckedNumber;
    private boolean isHasUsed;
    private int mROWS;
    private int mCOLUMNS;
    private int mCountWait = 0;
    private int mTime = 0;
    private boolean isDogOpen = false; // flag block user clicks on card when dog was pressed.

    // String SharePreference
    private final String BGM = "BGM";
    private final String FX = "FX";
    private final String RD = "RD";
    private final String USED = "USED";
    private final String ROW = "ROW";
    private final String COL = "COL";


    // UI variable
    private AlertDialog mDialogSettingJokerGame, mDialogCustomRowColumn; // set dialog ເພື່ອ ເປັນຕົວ popup ຕັ້ງຄ່າ ແລະ ຕົວ Animation
    private RadioButton mCard3_3, mCard4_5, mCard5_10; // set radio button ສໍາລັບໃຫ້ຜູ້ ໃຊ້ກໍານົດຈໍານວນ ຂອງປູ່ມ
    private NumberPicker mNumberPickerColumn, mNumberPickerRow;
    private TableLayout mTableLayout;  // set table layout ເພື່ອ ໃຫ້ການສະແດງຜົນຂອງ Dynamic button ເປັນໃນຮຸບແບບຂອງຕາຕະລາງ
    private Button mCustomizeButton, mOkCustomButton, mCancelCustomButton;  // set variable ໃຫ້ປຸ່ມ ຢູ່ໃນຕັ້ງຄ່າທີ່ ກໍານົດໃຫ້ popup ຂື້ນມາ.
    private Button mOkSettingButton, mCancelSettingButton;
    private SoundPool mCatSound, mDogSound;
    private AlertDialog mCustomDialog;
    private Button mCardButton;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private GifImageView mCardFlip; // gif image view
    private TextView mJokerText; // text view
    private Button mRepeatButtonAnimationFlip;

    private AlertDialog mBackConfirmPopup = null;


    // method ນີ້ແມ່ນ ເອີ້ນມາໃຊ້ເພື່ອໃຫ້ Class ນີ້ປ່ຽນພາສາ.
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joker_game);
        getSupportActionBar().setTitle(R.string.joker_game_title); // ຕັ້ງຊື່ໃຫ້ ສ່ວນ ຂອງ toolbar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // all setting sound effect
        mCatSound = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        mCatSoundExplosion = mCatSound.load(JokerGame.this, R.raw.cat_sound,1);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mDogSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mDogSoundExplosion = mDogSound.load(JokerGame.this, R.raw.dog_sound,1);

        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        isHasUsed = mSharedPreferences.getBoolean(USED, false);
        mCheckedNumber = mSharedPreferences.getInt(RD, 1);
        mROWS = mSharedPreferences.getInt(ROW, 6);
        mCOLUMNS = mSharedPreferences.getInt(COL, 3);
        if(isHasUsed){
            NUM_ROW = mROWS;
            NUM_COL = mCOLUMNS;
        }else{
            NUM_COL = mCOLUMNS;
            NUM_ROW = mROWS;
        }

        mTableLayout = findViewById(R.id.tableforbutton);

        CheckedRadioBox();  // ເມື່ອເຂົ້າ ມາສູ່ເກມນີ້ function ນີ້ ຈະຖືກເອີ້ນ ກ່ອນ ເພື່ອສະແດງຜົນຂອງເກມ.
    }
    // method ນີ້ ແມ່ນ ຈະຖືກເອີ້ນເມື່ອ ຜູ່ນໍາໃຊ້ ກົດ ທີສັນຍາລັກ ຮູບ ກົງຈັກ ກໍ່ຄືປູ່ມຕັ້ງຄ່າ.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_joker_game_menu, menu);
        return  true;
    }
    // method ນີ້ ຈະຖຶກເອີ້ນ ເມື່ອ ຜູ່ນໍາໃຊ້ ກົດ ປູ່ມ ຫຼື ເລືອກ ຢູ່ໃນ ການຕັ້ງຄ່າ
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting_joker_draw:
                OpenDialogJokerGame();
                return true;

            case android.R.id.home:
                onConfirmToGoBack();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void OpenDialogJokerGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(JokerGame.this);
        View view = getLayoutInflater().inflate(R.layout.setting_col_and_row_joker_game, null);

        mCheckedNumber = mSharedPreferences.getInt(RD, 1);
        // get id from xml file to variable
        mCard3_3 = view.findViewById(R.id.RadioButtonNine);
        mCard4_5 = view.findViewById(R.id.RadioButtonTwenty);
        mCard5_10 = view.findViewById(R.id.RadioButtonFifty);

        if(mCheckedNumber == 1){
            mCard3_3.setChecked(true);
            mCard4_5.setChecked(false);
            mCard5_10.setChecked(false);
        }else if(mCheckedNumber == 2){
            mCard4_5.setChecked(true);
            mCard3_3.setChecked(false);
            mCard5_10.setChecked(false);
        }else {
            mCard5_10.setChecked(true);
            mCard3_3.setChecked(false);
            mCard4_5.setChecked(false);
        }

        mCustomizeButton = view.findViewById(R.id.customizeColandRow);
        mOkSettingButton = view.findViewById(R.id.OkButtonJokerGameSetting);
        mCancelSettingButton = view.findViewById(R.id.CancelButtonJokerGameSetting);
        // ສ້າງອີເວັ້ນ ຄຣິກໃຫ້ປູ່ມ Ok Popup setting
        mOkSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCard3_3.isChecked()){
                    mDialogSettingJokerGame.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_ROW = 3;
                    NUM_COL = 3;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putInt(RD, 1);
                    mEditor.commit();
                    CheckedRadioBox();
                }else if(mCard4_5.isChecked()){
                    mDialogSettingJokerGame.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_ROW = 5;
                    NUM_COL = 4;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putInt(RD, 2);
                    mEditor.commit();
                    CheckedRadioBox();
                }else if(mCard5_10.isChecked()){
                    mDialogSettingJokerGame.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_ROW = 10;
                    NUM_COL = 5;
                    mEditor = mSharedPreferences.edit();
                    mEditor.putInt(RD, 3);
                    mEditor.commit();
                    CheckedRadioBox();
                }else{
                    mDialogSettingJokerGame.dismiss();
                }
                mEditor = mSharedPreferences.edit();
                mEditor.putInt(ROW, NUM_ROW);
                mEditor.putInt(COL, NUM_COL);
                mEditor.putBoolean(USED, true);
                mEditor.commit();
            }
        });
        // create event to cancel popup setting
        mCancelSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSettingJokerGame.dismiss();
            }
        });
        // custom dialog for user change length of card
        mCustomizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogSettingJokerGame.dismiss();
                OpenDialogCustomizeCards();
            }
        });
        builder.setView(view);
        mDialogSettingJokerGame = builder.create();
        mDialogSettingJokerGame.show();
    }

    private void OpenDialogCustomizeCards(){
        AlertDialog.Builder BuilderCustem = new AlertDialog.Builder(JokerGame.this);
        View viewCustom = getLayoutInflater().inflate(R.layout.custom_col_and_row, null);

        mNumberPickerColumn = viewCustom.findViewById(R.id.column);
        mNumberPickerRow = viewCustom.findViewById(R.id.row);

        mROWS = mSharedPreferences.getInt(ROW, 6);
        mCOLUMNS = mSharedPreferences.getInt(COL, 3);
        // set number to Number Column
        mNumberPickerColumn.setMinValue(3);
        mNumberPickerColumn.setMaxValue(5);
        mNumberPickerColumn.setValue(mCOLUMNS);
        // set number to Number Row
        mNumberPickerRow.setMinValue(3);
        mNumberPickerRow.setMaxValue(10);
        mNumberPickerRow.setValue(mROWS);

        mNumberPickerColumn.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(mNumberPickerColumn.getValue() == oldVal){
                    mSelectedColumn = oldVal;
                }else {
                    mSelectedColumn = newVal;
                }
            }
        });

        mNumberPickerRow.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(mNumberPickerRow.getValue() == oldVal){
                    mSelectedRow = oldVal;
                }else {
                    mSelectedRow = newVal;
                }
            }
        });

        mOkCustomButton = viewCustom.findViewById(R.id.ok_customize_settings);
        mCancelCustomButton = viewCustom.findViewById(R.id.cancel_customize_setting);


        mOkCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedColumn == 0 && mSelectedRow == 0) {
                    mDialogCustomRowColumn.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_COL = mCOLUMNS;
                    NUM_ROW = mROWS;
                    CheckedRadioBox();
                }else if(mSelectedRow == 0 && mSelectedColumn != 0) {
                    mDialogCustomRowColumn.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_COL = mSelectedColumn;
                    NUM_ROW = mROWS;
                    CheckedRadioBox();
                }else if(mSelectedColumn == 0 && mSelectedRow != 0){
                    mDialogCustomRowColumn.dismiss();
                    //tableLayout.removeAllViews();
                    NUM_COL = mCOLUMNS;
                    NUM_ROW = mSelectedRow;
                    CheckedRadioBox();
                }else {
                    mDialogCustomRowColumn.dismiss();
                    // tableLayout.removeAllViews();
                    NUM_COL = mSelectedColumn;
                    NUM_ROW = mSelectedRow;
                    CheckedRadioBox();
                }
                mEditor = mSharedPreferences.edit();
                mEditor.putInt(COL,NUM_COL);
                mEditor.putInt(ROW, NUM_ROW);
                mEditor.putBoolean(USED, true);
                mEditor.commit();
            }
        });

        mCancelCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogCustomRowColumn.dismiss();
            }
        });
        BuilderCustem.setView(viewCustom);
        mDialogCustomRowColumn = BuilderCustem.create();
        //alertDialogforcustom.getWindow().getAttributes().windowAnimations = R.style.Dialog_Left_Right;
        mDialogCustomRowColumn.show();
    }

    // this method ຈະເຮັດ ໜ້າທີ່ໃນການ ສະແດງ ຜົນ ແລະ ສ້າງ ປູ່ມ ຂື້ນມາໂດຍອັດຕະໂນມັດ.
    @SuppressLint({"ResourceAsColor", "NewApi"})
    private void CheckedRadioBox(){
        mTime = NUM_ROW * NUM_COL;
        mResult = ResultRandomInt(mTime);
        mTableLayout.removeAllViews();
        for(int row = 0; row < NUM_ROW; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
            mTableLayout.addView(tableRow);
            for(int col = 0; col < NUM_COL; col++){
               final Button button = new Button(this);
               final int number = mCardNumber;
                button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f));
                button.setBackgroundResource(R.drawable.card_animation);
                button.setPadding(0,0,0,0);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        CheckDogPosition(mResult, number, v);
                    }
                });
                mCardNumber++;
                tableRow.addView(button);
            }
        }
        mCardNumber = 1;
    }

    private void CheckDogPosition(int dogResult, int clickableNumber, View v)
    {
        if(dogResult == clickableNumber) {
            isDogOpen = true;
            v.setEnabled(false);
            mCopyResult = mResult;
            DialogForRepeatTheGame();
        }else{
            if(!isDogOpen){
                v.setEnabled(false);
                SoundEffectControl(0);
                FlipAnimationClick(v);
            }
        }
    }

    // this method  ຈະສະແດງຜົນ Animation ເມື່ອຜູ່່ນໍາໃຊ້ ກົດ ປູ່ມທີ່ ກົງກັບ ຕົວເລກ ທີ່ ຖືກການ Random
    @SuppressLint("NewApi")
    private void DialogForRepeatTheGame(){
            mGameFinishHandler = new Handler();
            mGameFinishHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(JokerGame.this);
                    View view = getLayoutInflater().inflate(R.layout.endgame_dialog, null);
                    mCardFlip = view.findViewById(R.id.opp_up_card);
                    mJokerText = view.findViewById(R.id.Joker_Text);
                    mRepeatButtonAnimationFlip = view.findViewById(R.id.RepeatOnlineJokerDrawButton);
                    mRepeatButtonAnimationFlip.setVisibility(View.INVISIBLE);

                    mCardFlip.setEnabled(false);
                    mJokerText.setEnabled(false);
                    mJokerText.setBackgroundResource(R.drawable.joker_text);
                    mCardFlip.setBackgroundResource(R.drawable.card_animation_dialog);
                    Handler h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCardFlip.animate().scaleX(0f).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    SoundEffectControl(1);
                                    mCardFlip.setBackgroundResource(R.drawable.dog_dialog);
                                    mCardFlip.animate().scaleX(1f);
                                    DisableAllOfButton(mCopyResult);
                                    mCardFlip.setEnabled(true);
                                    mJokerText.setEnabled(true);
                                }
                            });
                        }
                    }, 500);

                    mCardFlip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCustomDialog.dismiss();
                        }
                    });

                    mJokerText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCustomDialog.dismiss();
                        }
                    });

                    builder.setView(view);
                    mCustomDialog = builder.create();
                    mCustomDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    mCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mCustomDialog.setCanceledOnTouchOutside(false);
                    mCustomDialog.show();
                    mCustomDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mDogSound.stop(mDogSoundExplosion);
                            mDogSound.release();
                            AfterEnd();
                        }
                    });
                }
            },0);
    }
    // repeat game
    private void Waiting_repeat(){
        mDogSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mDogSoundExplosion = mDogSound.load(JokerGame.this, R.raw.dog_sound,1);
        mROWS = mSharedPreferences.getInt(ROW, 6);
        mCOLUMNS = mSharedPreferences.getInt(COL, 3);
        NUM_COL = 0;
        NUM_ROW = 0;
        NUM_ROW = mROWS;
        NUM_COL = mCOLUMNS;
        mCardNumber = 1;
        mResult = 0;
        mCopyResult = 0;
        mCardNumberDisable = 1;
        isDogOpen = false;
        mTime = 0;
        mCountWait = 0;
        //tableLayout.removeAllViews();
        CheckedRadioBox();
    }
   @SuppressLint("NewApi")
   private void AfterEnd(){
       final int getResult = mResult;
       mTableLayout.removeAllViews();
       for(int row = 0; row < NUM_ROW; row++){
           final TableRow tableRow = new TableRow(this);
           tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT,1.0f));
           mTableLayout.addView(tableRow);

           for(int col = 0; col < NUM_COL; col++){
               mCardButton = new Button(this);
               mCardButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f));
               final int number = mCardNumberAfterEnd;
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
               mCardNumberAfterEnd++;
               tableRow.addView(mCardButton);
           }
       }
       mCardNumberAfterEnd = 1;
   }

   private void FlipAnimation(View view){
       final ObjectAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(view, "scaleX", 1f,0f);
       final ObjectAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(view, "scaleX", 0f,1f);
       objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
       objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
       objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
           @Override
           public void onAnimationEnd(Animator animation) {
               super.onAnimationEnd(animation);
               if(mCountWait == mTime){
                   Waiting_repeat();
               }
           }
       });
       objectAnimatorTextNumber1.setDuration(300);
       objectAnimatorTextNumber1.start();
   }

   private void FlipAnimationClick(final View view)
   {
       final ObjectAnimator objectAnimatorTextNumber1 = ObjectAnimator.ofFloat(view, "scaleX", 1f,0f);
       final ObjectAnimator objectAnimatorTextNumber2 = ObjectAnimator.ofFloat(view, "scaleX", 0f,1f);
       objectAnimatorTextNumber1.setInterpolator(new DecelerateInterpolator());
       objectAnimatorTextNumber2.setInterpolator(new AccelerateDecelerateInterpolator());
       objectAnimatorTextNumber1.addListener(new AnimatorListenerAdapter() {
           @Override
           public void onAnimationEnd(Animator animation) {
               super.onAnimationEnd(animation);
               view.setBackgroundResource(R.drawable.cat_disabled);
               objectAnimatorTextNumber2.start();
           }
       });
       objectAnimatorTextNumber1.setDuration(300);
       objectAnimatorTextNumber1.start();
   }

    // Method ນີ້ແມ່ນເປັນບ່ອນ ຄວບຄູມສຽງຂອງ Animation
    private void SoundEffectControl(int oneDog){
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        if(oneDog == 1 && isEffectSound){
            mDogSound.play(mDogSoundExplosion, 0.99f,0.99f,0,0,0.99f);
        }else if(oneDog == 0 && isEffectSound) {
            mCatSound.play(mCatSoundExplosion, 0.99f,0.99f,0,0,0.99f);
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
                mCardButton = new Button(this);
                mCardButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT,1.0f));
                final int number = mCardNumberDisable;
                mCardButton.setPadding(0,0,0,0);
                mCardButton.setEnabled(false);
                if(number == getResult){
                    mCardButton.setBackgroundResource(R.drawable.dog);
                }else{
                    mCardButton.setBackgroundResource(R.drawable.cat_disabled);
                }
                mCardNumberDisable++;
                tableRow.addView(mCardButton);
            }
        }
        mCardNumberDisable = 1;
    }

    private int ResultRandomInt(int sum){
        mRandom = new Random();
        int ResultR = mRandom.nextInt(sum)+1;
        return ResultR;
    }

    @Override
    protected void onResume() {
        mCatSound = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        mCatSoundExplosion = mCatSound.load(JokerGame.this, R.raw.cat_sound,1);
        mDogSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mDogSoundExplosion = mDogSound.load(JokerGame.this, R.raw.dog_sound,1);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(JokerGame.this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(JokerGame.this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.start();
                    HomeActivity.mBackgroundMusics.setLooping(true);
                }
            }
        }
        isActive = true;
        super.onResume();
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
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        if(HomeActivity.mBackgroundMusics != null && !(SelectGame.isActive || HomeActivity.isActive || JokerGame.isActive || User_Player_Lucky_Game.isActive || UserPlayerSwapGame.isActive || SwapGame.isActive || LuckyGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }
        if(isEffectSound){
            mDogSound.stop(mDogSoundExplosion);
            mDogSound.release();
            mCatSound.stop(mCatSoundExplosion);
            mCatSound.release();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        Context context = getApplicationContext();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                if(isEffectSound){
                    mDogSound.stop(mDogSoundExplosion);
                    mDogSound.release();
                    mCatSound.stop(mCatSoundExplosion);
                    mCatSound.release();
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
                mDogSound.stop(mDogSoundExplosion);
                mDogSound.release();
                mCatSound.stop(mCatSoundExplosion);
                mCatSound.release();
            }
        }
        isActive = false;
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onConfirmToGoBack();
    }

    private void ClearMemoryData(){
        mRandom = null;
        mResult = 0;
        mCardNumber = 1;
        mCardNumberDisable = 1;
        mCardNumberAfterEnd = 1;
        NUM_ROW = 0;
        NUM_COL = 0;
        mSelectedColumn = 0;
        mSelectedRow = 0;
        mCatSoundExplosion = 0;
        mDogSoundExplosion = 0;
        mGameFinishHandler = null;
        mCopyResult = 0;
        isEffectSound = false;
        isBGMSound = false;
        mCheckedNumber = 0;
        isHasUsed = false;
        mROWS = 0;
        mCOLUMNS = 0;
        mCountWait = 0;
        mTime = 0;
        isDogOpen = false;
    }

    @Override
    protected void onDestroy() {
        if(this.isFinishing()){
            ClearMemoryData();
        }
        super.onDestroy();
    }

    private void onConfirmToGoBack()
    {
        mBackConfirmPopup = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(JokerGame.this);
        View view = getLayoutInflater().inflate(R.layout.online_joker_draw_backpress_popup_layout, null);

        TextView textBody = (TextView) view.findViewById(R.id.back_popup_text_body);
        Button okBackPopup = (Button) view.findViewById(R.id.Ok_back_popup);
        Button cancelBackPopup = (Button) view.findViewById(R.id.Cancel_back_popup);

        textBody.setText(R.string.ConfirmToExitPlayingGame);

        cancelBackPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackConfirmPopup.dismiss();
            }
        });

        okBackPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackConfirmPopup.dismiss();
                Intent selectGame = new Intent(JokerGame.this, SelectGame.class);
                startActivity(selectGame);
            }
        });

        builder.setView(view);
        mBackConfirmPopup = builder.create();
        mBackConfirmPopup.setCancelable(false);
        mBackConfirmPopup.show();
    }
}
