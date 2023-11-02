package com.ictlao.android.app.partydraw.Feature.Swap;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ictlao.android.app.partydraw.Core.CustomAdapter;
import com.ictlao.android.app.partydraw.Core.CustomAdapterSwapGame;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Core.Models.Item;
import com.ictlao.android.app.partydraw.Core.Models.TwoItem;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SwapGame extends AppCompatActivity {
    // ວາງຕົວປ່ຽນທີ່ນິີເພື່ອໃຫ້ທຸກ ຟັງເຊີນ ເຫັນ
    private ArrayList<String> mGetDataNameList, mCopyDataNameList;
    private ArrayList<TwoItem> mStoreDataToDisplay;
    private ArrayList<String> mRecycleDataForRepeat;
    private ArrayList<String> mCompareNameList;
    private ArrayList<String> mAddNewName;
    private Button mRepeatButton, mExitButton, mStartButton, mShowNameListButton;
    private int mCountSizes, mOutputRandom;
    private ListView mListView;
    private int mIndexFirstPerson = 0;
    private int mIndexSecondPerson = 1;
    private int mRange = 1;
    private int mSwapRange = 1;
    private TextView mGiveName;
    private TextView mRecieveName;
    // variable for animation countdown
    private AlertDialog mDialogCountDown;
    private AlertDialog mDialogShowPlayerName, mDialogShowLastPlayerName;

    public static boolean isActive;    // set boolean make it static for stop and start BGM

    // set variable to dialog swap list
    private Button mExitSwapDrawListButton;
    private ListView mListViewAllPlayer;
    private AlertDialog mDialogViewAllPlayer;
    private ArrayList<Item> mPlayerNameList = new ArrayList<>();

    // set game over variable
    private Button mOkGameFinishButton;
    private AlertDialog mDialogGameFinish;
    private Handler mCountDownHandler;
    private CustomAdapter mCustomAdapter;

    private SoundPool mCountDownSound;
    private int mSoundExposion;
    private AlertDialog mDialogFindingStarter;

    private AlertDialog mDialogConfirmDelete, mDialogConfirmDelete2;

    private AlertDialog mDialogAddNameAddRunTime;
    private EditText mAddNameRunTime;
    private Button mAddNameRunTimeButton, mAddNameButton, mExitDialogAddNameRunTimeButton;
    private ListView mListViewAddNewNameRunTime;
    private AlertDialog mDialogDeleteNameRunTime;
    private Button mOkRunTimeDelete, mCancelRunTimeDelete;
    private TextView mMessageConfirmDeleteRunTime;

    private boolean isFinish = false;
    private boolean isAdded = false;
    private ArrayAdapter<String> mArrayAdapterRunTime;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private CustomAdapterSwapGame mCustomAdapterShowCouplePlayer;

    // set variable control music
    private final String BGM = "BGM";
    private final String FX = "FX";
    private SharedPreferences mSharedPreferences;
    private boolean isEffectSound;
    private boolean isBGMSound;

    private boolean isToastShow = false;
    private Toast mToastMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));  // ເອີ້ນ method ນີ້ ມາໃຊ້ ເພື່ອໃຫ້ປ່ຽນພາສາ
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_game);

        getSupportActionBar().hide();

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // ຕັ້ງຄ່າໃຫ້ກັບ ຕົວທີ່ ຈະເປັນ ບ່ອນເກັບ Data
        mGetDataNameList = new ArrayList<>();
        mStoreDataToDisplay = new ArrayList<>();
        mRecycleDataForRepeat = new ArrayList<>();
        mCompareNameList = new ArrayList<>();
        mAddNewName = new ArrayList<>();
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);

        // ດື່ງ ເອົາຕົວປ່ຽນຂ້າງເທີງ ໃຫ້ເຂື່ອມຕໍ່ກັບ ໄອດີ ຈາກ xml file
        mGetDataNameList = getIntent().getStringArrayListExtra("Names"); // ຮັບເອົາ ຂໍ້ມຸນລາຍຊື່ ຈາກ Class UserPlayerSwapGame
        mCopyDataNameList = new ArrayList<>(mGetDataNameList);
        mRepeatButton = findViewById(R.id.repeat_swap_game);
        mListView = findViewById(R.id.detail);
        mExitButton = findViewById(R.id.exit_swap_game);
        mCountSizes = mGetDataNameList.size();
        mStartButton = findViewById(R.id.swap_button_S_N);
        mGiveName = findViewById(R.id.RoundName);
        mShowNameListButton = findViewById(R.id.list_data_button);


        mCountDownSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundExposion = mCountDownSound.load(this, R.raw.countdown_sound_effect, 1);

        //===================

        // set Event Click to Button
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackToSelectGame();
            }
        });
        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReturnDataForRepeat();
            }
        });

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTheGame();
            }
        });

        mShowNameListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogListData();
            }
        });
        //Start Countdown
        //soundPoolCountDown.play(soundExplosion, 0.99f,0.99f,0,0,0.99f); // soundCountdown
        FindingStarterDialog();
    }

    private void FindingStarterDialog(){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);

        builder.setView(view);
        mDialogFindingStarter = builder.create();
        mDialogFindingStarter.setCanceledOnTouchOutside(false);
        mDialogFindingStarter.setCancelable(false);
        //findingStarter.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogFindingStarter.show();
        mCountDownHandler = new Handler();
        mCountDownHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialogFindingStarter.dismiss();
                RandomRunFunction();
            }
        },3000);
    }
    // ເລີ່ມເກມໃໝ່ ໂດຍມີເງື່ອນໄຂ ວ່າ ຈໍານວນ ຂໍ້ມຸນ ທີ່ມ ຈະຕ້ອງເທົ່າ 0 . ຖ້າວ່າບໍ່ ເທົ່າກັບ 0 ແມ່ນ ຈະ ກັບຄືນສູ່ Class UserPlayerSwapGame
    private void ReturnDataForRepeat() {
        if (mGetDataNameList.size() == 0) {
            mGetDataNameList = new ArrayList<>(mRecycleDataForRepeat);
            mGetDataNameList.addAll(mAddNewName);
            mAddNewName.clear();
            mStoreDataToDisplay.clear();
            mRecycleDataForRepeat.clear();
            mCustomAdapterShowCouplePlayer.notifyDataSetChanged();
            mStartButton.setBackgroundResource(R.drawable.start_button);
            mRepeatButton.setBackgroundResource(R.drawable.back_arrow);
            mIndexFirstPerson = 0;
            mIndexSecondPerson = 1;
            mRange = 1;
            mSwapRange = 1;
            mStartButton.setEnabled(true);
            mPlayerNameList.clear();
            mCompareNameList.clear();
            isFinish = false;
            isAdded = false;
            FindingStarterDialog();
        } else {
            if(isToastShow){
                mToastMessage.cancel();
                isToastShow = false;
            }
            isEffectSound = mSharedPreferences.getBoolean(FX,true);
            if(isEffectSound){
                mCountDownSound.stop(mSoundExposion);
                mCountDownSound.release();
            }
            Intent intent = new Intent(SwapGame.this, UserPlayerSwapGame.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    // ອອກຈາກ ໜ້າເກັບ ກັບຄືນສູ່ SelectGame Class ກໍ່ຄື ໜ້າເລືອກເກມ.
    private void BackToSelectGame() {
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        if(isEffectSound){
            mCountDownSound.stop(mSoundExposion);
            mCountDownSound.release();
        }
        Intent intent = new Intent(SwapGame.this, SelectGame.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // ເລີ່ມເກມ ໂດຍ ຈະສະແດງ Animation Count down ພາຍໃນ 3 ວິນາທີ
    private void StartTheGame() {
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        CountDownSound(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(SwapGame.this);
        View view = getLayoutInflater().inflate(R.layout.countdown, null);

        builder.setView(view);
        mDialogCountDown = builder.create();
        mDialogCountDown.setCanceledOnTouchOutside(false);
        mDialogCountDown.setCancelable(false);
        mDialogCountDown.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogCountDown.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDialogCountDown.dismiss();
                //soundPoolCountDown.pause(soundExplosion);
                RandomRunFunction();
            }
        }, 3000);

    }

    // Randowm ຫາຕົວເລກ ທີ່ຈະ ດຶງເອົາຊື່ຈາກ ArrayList ມາສະແດງ ຜົນ.
    private void RandomRunFunction() {
        try {
            Random r = new Random();
            mCountSizes = mGetDataNameList.size();
            mOutputRandom = r.nextInt(mCountSizes);
            getResult();
        } catch (Exception e) {
            if (mCountSizes == 0) {
                mRepeatButton.setBackgroundResource(R.drawable.repeat_button);
                mStartButton.setBackgroundResource(R.drawable.start_button);
                mGiveName.setText("");
                GameOverDialog();
            }
        }
    }

    // Method ນີ້ ຈະລົບລາຍຊື່ທີ່ຖືກ Random ແລະ ນໍາໄປເກັບໄວ້ ໃນ ArrayList StoreDataForRepeat ເພື່ອ ຜູ່ຫຼີ້ນຕ້ອງການ ເລີ່ມໃໝ່.
    private void getResult() {
        try {
            final int result = mOutputRandom;
            //final ArrayList<String> StringArray = new ArrayList<String>();
            //Astring2.add(arrayList.get(result));
            mRecycleDataForRepeat.add(mGetDataNameList.get(result));
            mCompareNameList.add(mGetDataNameList.get(result));
            if (mRecycleDataForRepeat.size() == 1) {
                mGetDataNameList.remove(result);
                // than go to function below
                // isFirstOne = true;
                // AlertForShowPlayerName();
                mGiveName.setText(mRecycleDataForRepeat.get(0));
                mCompareNameList.remove(0);
                mStartButton.setBackgroundResource(R.drawable.start_button);
            } else {
               // StoreDataToList.add(range + ". " + StoreDataForRepeat.get(a) + " " + " --> " + " " + StoreDataForRepeat.get(b));
                mStoreDataToDisplay.add(new TwoItem(mRecycleDataForRepeat.get(mIndexFirstPerson), R.drawable.swap_arrow, mRecycleDataForRepeat.get(mIndexSecondPerson)));
                mGiveName.setText(mRecycleDataForRepeat.get(mIndexSecondPerson));
                mStartButton.setBackgroundResource(R.drawable.next_button);
                // dialog show player name
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.show_player_name_swap_game, null);

                mRecieveName = view.findViewById(R.id.swap_player);
                mRecieveName.setText(mRecycleDataForRepeat.get(mIndexSecondPerson));

                builder.setView(view);
                mDialogShowPlayerName = builder.create();
                mDialogShowPlayerName.setCanceledOnTouchOutside(false);
                mDialogShowPlayerName.setCancelable(false);
                mDialogShowPlayerName.show();
                mCountDownHandler = new Handler();
                mCountDownHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialogShowPlayerName.dismiss();
                    }
                }, 2000);
                // arrayAdapterToListView = new ArrayAdapter<String>(SwapGame.this, android.R.layout.simple_list_item_1, StoreDataToList) {
                mCustomAdapterShowCouplePlayer = new CustomAdapterSwapGame(SwapGame.this, mStoreDataToDisplay){
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
                mListView.setAdapter(mCustomAdapterShowCouplePlayer);
                mGetDataNameList.remove(result);

                if (mGetDataNameList.size() == 0) {
                    //StoreDataToList.add(range + 1 + ". " + StoreDataForRepeat.get(b) + " " + " --> " + " " + StoreDataForRepeat.get(0));
                    mStoreDataToDisplay.add(new TwoItem(mRecycleDataForRepeat.get(mIndexSecondPerson), R.drawable.swap_arrow, mRecycleDataForRepeat.get(0)));

                    mCompareNameList.add(mRecycleDataForRepeat.get(0));
                    mRepeatButton.setBackgroundResource(R.drawable.repeat_button);
                    mStartButton.setEnabled(false);
                    mStartButton.setBackgroundResource(R.drawable.start_button);
                    mGiveName.setText("");
                    // dialog show player name
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    View view1 = getLayoutInflater().inflate(R.layout.show_player_name_swap_game, null);

                    mRecieveName = view1.findViewById(R.id.swap_player);
                    mRecieveName.setText(mRecycleDataForRepeat.get(mIndexSecondPerson));

                    builder1.setView(view1);
                    mDialogShowLastPlayerName = builder1.create();
                    mDialogShowLastPlayerName.setCanceledOnTouchOutside(false);
                    mDialogShowLastPlayerName.setCancelable(false);
                    mDialogShowLastPlayerName.show();
                    mCountDownHandler = new Handler();
                    mCountDownHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDialogShowLastPlayerName.dismiss();
                            GameOverDialog();
                        }
                    }, 2000);
                }
                mIndexFirstPerson++;
                mIndexSecondPerson++;
                mRange++;
            }
        } catch (Exception e) {

        }
    }
    // ເມື່ອ ບໍ່ມີ ຂໍ້ມຸນໃນ ArrayList ແລ້ວ ຈະສະແດງ Animation Game Over.
    private void GameOverDialog() {
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SwapGame.this);
        View view = getLayoutInflater().inflate(R.layout.swap_game_over, null);

        mOkGameFinishButton = view.findViewById(R.id.ok_swap_over);
        mOkGameFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFinish = true;
                mDialogGameFinish.dismiss();
            }
        });
        builder.setView(view);
        mDialogGameFinish = builder.create();
        mDialogGameFinish.setCanceledOnTouchOutside(false);
        mDialogGameFinish.setCancelable(false);
        mDialogGameFinish.show();
    }

    private void DialogListData() {
        try {
            if(isToastShow){
                mToastMessage.cancel();
                isToastShow = false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View view = getLayoutInflater().inflate(R.layout.popup_data_swap_draw, null);

            mExitSwapDrawListButton = view.findViewById(R.id.exit_list_swap_draw);
            mAddNameRunTimeButton = view.findViewById(R.id.Add_people_run_time);
            mListViewAllPlayer = view.findViewById(R.id.list_data);
            mPlayerNameList.clear();
            ArrayList<String> setString = new ArrayList<>(mCompareNameList);
            final ArrayList<String> getString = new ArrayList<>(mCopyDataNameList);

            if(setString.size() != 0){
                if(isAdded == true && mGetDataNameList.size() == 0){
                    for (int l = 0; l < setString.size(); l++) {
                        mPlayerNameList.add(new Item(setString.get(l), R.drawable.present));
                        getString.remove(setString.get(l));
                    }
                    for (int g = 0; g < getString.size(); g++) {
                        mPlayerNameList.add(new Item(getString.get(g), R.drawable.empty));
                    }
                    mCustomAdapter = new CustomAdapter(this, mPlayerNameList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view1 = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                view1.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            } else {
                                view1.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }
                            return view1;
                        }
                    };
                    mListViewAllPlayer.setAdapter(mCustomAdapter);
                }else {
                    for (int l = 0; l < setString.size(); l++) {
                        mPlayerNameList.add(new Item(setString.get(l), R.drawable.present));
                        getString.remove(setString.get(l));
                    }
                    for (int g = 0; g < getString.size(); g++) {
                        mPlayerNameList.add(new Item(getString.get(g), R.drawable.empty));
                    }
                    mCustomAdapter = new CustomAdapter(this, mPlayerNameList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view1 = super.getView(position, convertView, parent);
                            if (position % 2 == 1) {
                                view1.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            } else {
                                view1.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }
                            return view1;
                        }
                    };
                    mListViewAllPlayer.setAdapter(mCustomAdapter);
                }

               if(isFinish == true && mGetDataNameList.size() == 0){
                   mListViewAllPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                           if (mPlayerNameList.size() <= 2) {
                               //Toast.makeText(getApplicationContext(), getString(R.string.not_delete_now) + " ", Toast.LENGTH_SHORT).show();
                               String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                               CustomToastMessage(getStringMessage);
                           } else {
                               if(isToastShow){
                                   mToastMessage.cancel();
                                   isToastShow = false;
                               }
                               AlertDialog.Builder builder1 = new AlertDialog.Builder(SwapGame.this);
                               View view1 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                               final TextView playerMessage;
                               Button ok_delete, cancel_delete;
                               playerMessage = view1.findViewById(R.id.show_delete_mesage);
                               ok_delete = view1.findViewById(R.id.ok_delete_player);
                               cancel_delete = view1.findViewById(R.id.cancel_delete_player);

                               playerMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mPlayerNameList.get(position).getNames() + " \n"+getString(R.string.LuckyGameActivity_Message_delete_user_end));
                               cancel_delete.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       mDialogConfirmDelete2.dismiss();
                                   }
                               });

                               ok_delete.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       String name = mPlayerNameList.get(position).getNames();
                                       mDialogConfirmDelete2.dismiss();
                                       mPlayerNameList.remove(position);
                                       mCompareNameList.remove(name);
                                       mCopyDataNameList.remove(name);
                                       mRecycleDataForRepeat.remove(name);
                                       mCustomAdapter.notifyDataSetChanged();
                                   }
                               });

                               builder1.setView(view1);
                               mDialogConfirmDelete2 = builder1.create();
                               mDialogConfirmDelete2.show();
                           }
                       }
                   });
               }else {
                   mListViewAllPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                       @Override
                       public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                           int PresentId = R.drawable.present;
                           int PresentPosition = mPlayerNameList.get(position).getImage();
                           String name = mPlayerNameList.get(position).getNames();
                           if (mPlayerNameList.size() <= 2) {
                               String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                               CustomToastMessage(getStringMessage);
                           } else if (PresentId == PresentPosition) {
                               String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + " " + mPlayerNameList.get(position).getNames() + "\n" + getString(R.string.SwapGameActivity_got_present);
                               CustomToastMessage(getStringMessage);
                           } else if (mRecycleDataForRepeat.get(0).equals(name)) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + " " + mPlayerNameList.get(position).getNames() + "\n" +  getString( R.string.SwapGameActivity_given);
                               CustomToastMessage(getStringMessage);
                           }else if(mGetDataNameList.size() <= 2){
                               String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                               CustomToastMessage(getStringMessage);
                           } else {
                               AlertDialog.Builder builder1 = new AlertDialog.Builder(SwapGame.this);
                               View view1 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                               final TextView playerMessage;
                               Button ok_delete, cancel_delete;
                               playerMessage = view1.findViewById(R.id.show_delete_mesage);
                               ok_delete = view1.findViewById(R.id.ok_delete_player);
                               cancel_delete = view1.findViewById(R.id.cancel_delete_player);

                               playerMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mPlayerNameList.get(position).getNames() + "\n"+getString(R.string.LuckyGameActivity_Message_delete_user_end));
                               cancel_delete.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       mDialogConfirmDelete2.dismiss();
                                   }
                               });

                               ok_delete.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       String name = mPlayerNameList.get(position).getNames();
                                       mDialogConfirmDelete2.dismiss();
                                       mPlayerNameList.remove(position);
                                       mCompareNameList.remove(name);
                                       mGetDataNameList.remove(name);
                                       mCopyDataNameList.remove(name);
                                       mCustomAdapter.notifyDataSetChanged();
                                   }
                               });

                               builder1.setView(view1);
                               mDialogConfirmDelete2 = builder1.create();
                               mDialogConfirmDelete2.show();
                           }
                       }
                   });
               }

            }else {
                for(int f = 0; f < mCopyDataNameList.size(); f++){
                    mPlayerNameList.add(new Item(mCopyDataNameList.get(f), R.drawable.empty));
                    mCustomAdapter = new CustomAdapter(this, mPlayerNameList){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view1 = super.getView(position, convertView, parent);
                            if(position % 2 == 1){
                                view1.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                            }else{
                                view1.setBackgroundColor(getResources().getColor(R.color.colorList));
                            }
                            return view1;
                        }
                    };
                    mListViewAllPlayer.setAdapter(mCustomAdapter);
                    mListViewAllPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final String name = mPlayerNameList.get(position).getNames();
                            String viewName = mGiveName.getText().toString();
                            if (mPlayerNameList.size() <= 2) {
                               String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                                CustomToastMessage(getStringMessage);
                            }else if (name.equals(viewName)) {
                                String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete)+" " + mGiveName.getText().toString() + " "+ getString(R.string.SwapGameActivityHasStarted);
                                CustomToastMessage(getStringMessage);
                            } else {
                                AlertDialog.Builder builderDialog = new AlertDialog.Builder(SwapGame.this);
                                View viewDelete = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                TextView playerMessage;
                                Button ok_delete, cancel_delete;
                                playerMessage = viewDelete.findViewById(R.id.show_delete_mesage);
                                ok_delete = viewDelete.findViewById(R.id.ok_delete_player);
                                cancel_delete = viewDelete.findViewById(R.id.cancel_delete_player);

                                playerMessage.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mPlayerNameList.get(position).getNames() + "\n"+getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                cancel_delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogConfirmDelete.dismiss();
                                    }
                                });
                                ok_delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogConfirmDelete.dismiss();
                                        mCopyDataNameList.remove(name);
                                        mGetDataNameList.remove(name);
                                        mPlayerNameList.remove(position);
                                        mCustomAdapter.notifyDataSetChanged();
                                    }
                                });
                                builderDialog.setView(viewDelete);
                                mDialogConfirmDelete = builderDialog.create();
                                mDialogConfirmDelete.show();
                            }
                        }
                    });
                }
            }

            mExitSwapDrawListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogViewAllPlayer.dismiss();
                }
            });
            //----------------------------------------------------------------------------------------
            // button add people at run time
            mAddNameRunTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogViewAllPlayer.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SwapGame.this);
                    View view1 = getLayoutInflater().inflate(R.layout.add_player_game, null);

                    mAddNameRunTime = view1.findViewById(R.id.edit_add_name);
                    mAddNameButton = view1.findViewById(R.id.Add_name);
                    mListViewAddNewNameRunTime = view1.findViewById(R.id.list_data_edit);
                    mExitDialogAddNameRunTimeButton = view1.findViewById(R.id.exit_edit_dialog);

                     mArrayAdapterRunTime = new ArrayAdapter<String>(SwapGame.this, android.R.layout.simple_list_item_1, mCopyDataNameList){
                         @NonNull
                         @Override
                         public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                             View colorView = super.getView(position, convertView, parent);
                             if(position % 2 == 1){
                                 colorView.setBackgroundColor(getResources().getColor(R.color.colorSubList));
                             }else{
                                 colorView.setBackgroundColor(getResources().getColor(R.color.colorList));
                             }
                             return  colorView;
                         }
                     };
                     mListViewAddNewNameRunTime.setAdapter(mArrayAdapterRunTime);

                     //== set delete item in list view
                    if(isFinish == true && mGetDataNameList.size() == 0){
                        mListViewAddNewNameRunTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                //----------------------dialog delete edit ---------------------------------
                                if (mCopyDataNameList.size() <= 2) {
                                   String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteMoreToastMessagePupupDataSwapDraw);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    AlertDialog.Builder EBuilder = new AlertDialog.Builder(SwapGame.this);
                                    View view2 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                    mOkRunTimeDelete = view2.findViewById(R.id.ok_delete_player);
                                    mCancelRunTimeDelete = view2.findViewById(R.id.cancel_delete_player);
                                    mMessageConfirmDeleteRunTime = view2.findViewById(R.id.show_delete_mesage);
                                    mMessageConfirmDeleteRunTime.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mCopyDataNameList.get(position) + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                    //------- dismiss dialog ----------------------------------
                                    mCancelRunTimeDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteNameRunTime.dismiss();
                                        }
                                    });

                                    //-------------------- delete -------------------------------
                                    mOkRunTimeDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String name = mCopyDataNameList.get(position);
                                            mDialogDeleteNameRunTime.dismiss();
                                            mCopyDataNameList.remove(name);
                                            mAddNewName.remove(name);
                                            mRecycleDataForRepeat.remove(name);
                                            mCompareNameList.remove(name);
                                            mArrayAdapterRunTime.notifyDataSetChanged();
                                        }
                                    });

                                    EBuilder.setView(view2);
                                    mDialogDeleteNameRunTime = EBuilder.create();
                                    mDialogDeleteNameRunTime.show();
                                }
                            }
                        });
                    }else {

                        mListViewAddNewNameRunTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                //----------------------dialog delete edit ---------------------------------
                                String name = mCopyDataNameList.get(position);
                                if (mRecycleDataForRepeat.contains(name)) {
                                    String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDelete) + " " + name + " " + getString(R.string.SwapGameActivityHasStarted);
                                    CustomToastMessage(getStringMessage);
                                } else if (mGetDataNameList.size() <= 2) {
                                     String getStringMessage = getString(R.string.LuckyGameActivityCouldNotDeleteNow);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    AlertDialog.Builder EBuilder = new AlertDialog.Builder(SwapGame.this);
                                    View view2 = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                    mOkRunTimeDelete = view2.findViewById(R.id.ok_delete_player);
                                    mCancelRunTimeDelete = view2.findViewById(R.id.cancel_delete_player);
                                    mMessageConfirmDeleteRunTime = view2.findViewById(R.id.show_delete_mesage);
                                    mMessageConfirmDeleteRunTime.setText(getString(R.string.LuckyGameActivity_Message_delete_user_start) + "\n" + mCopyDataNameList.get(position) + "\n" + getString(R.string.LuckyGameActivity_Message_delete_user_end));
                                    //------- dismiss dialog ----------------------------------
                                    mCancelRunTimeDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mDialogDeleteNameRunTime.dismiss();
                                        }
                                    });

                                    //-------------------- delete -------------------------------
                                    mOkRunTimeDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String name = mCopyDataNameList.get(position);
                                            mDialogDeleteNameRunTime.dismiss();
                                            mCopyDataNameList.remove(name);
                                            mGetDataNameList.remove(name);
                                            mCompareNameList.remove(name);
                                            mArrayAdapterRunTime.notifyDataSetChanged();
                                        }
                                    });

                                    EBuilder.setView(view2);
                                    mDialogDeleteNameRunTime = EBuilder.create();
                                    mDialogDeleteNameRunTime.show();
                                }
                            }
                        });
                    }

                    // add people to list data game
                    mAddNameButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isFinish == true){
                                String Names = mAddNameRunTime.getText().toString();
                                if (Names.equals("") || Names == null || Names.trim().equals("")) {
                                    String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                                    CustomToastMessage(getStringMessage);
                                } else if (mCopyDataNameList.contains(Names)) {
                                    String getStringMessage = getString(R.string.This_name) + " " + mAddNameRunTime.getText().toString() + " " + getString(R.string.already_exits);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    isAdded = true;
                                    mAddNewName.add(Names);
                                    mCopyDataNameList.add(Names);
                                    mArrayAdapterRunTime.notifyDataSetChanged();
                                }
                            }else {
                                String Names = mAddNameRunTime.getText().toString();
                                if (Names.equals("") || Names == null || Names.trim().equals("")) {
                                    String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                                    CustomToastMessage(getStringMessage);
                                } else if (mCopyDataNameList.contains(Names)) {
                                     String getStringMessage = getString(R.string.This_name) + " " + mAddNameRunTime.getText().toString() + " " + getString(R.string.already_exits);
                                    CustomToastMessage(getStringMessage);
                                } else {
                                    mGetDataNameList.add(Names);
                                    mCopyDataNameList.add(Names);
                                    mArrayAdapterRunTime.notifyDataSetChanged();
                                }
                            }
                            mAddNameRunTime.setText("");
                        }
                    });
                    // exit dialog
                    mExitDialogAddNameRunTimeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogAddNameAddRunTime.dismiss();
                        }
                    });

                    builder1.setView(view1);
                    mDialogAddNameAddRunTime = builder1.create();
                    mDialogAddNameAddRunTime.show();
                }
            });

             builder.setView(view);
             mDialogViewAllPlayer = builder.create();
             mDialogViewAllPlayer.show();
            }catch(Exception e){

        }
    }

    private void CustomToastMessage(String message){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        isToastShow = true;
        View view = getLayoutInflater().inflate(R.layout.toast_message_waring, (ViewGroup) findViewById(R.id.show_message));
        TextView toast_body = view.findViewById(R.id.show_message_body);
        toast_body.setText(message);
        mToastMessage = new Toast(getApplicationContext());
        mToastMessage.setGravity(Gravity.BOTTOM,0,0);
        mToastMessage.setView(view);
        mToastMessage.setDuration(Toast.LENGTH_LONG);
        mToastMessage.show();
    }

    private void CountDownSound(boolean isPlay){
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        if(isPlay == true && isEffectSound){
            mCountDownSound.play(mSoundExposion, 0.99f,0.99f,0,0,0.99f);
        }
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
    protected void onResume() {
        mCountDownSound = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundExposion = mCountDownSound.load(this, R.raw.countdown_sound_effect, 1);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(SwapGame.this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(SwapGame.this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.start();
                    HomeActivity.mBackgroundMusics.setLooping(true);
                }
            }
        }
        isActive = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
        if(HomeActivity.mBackgroundMusics != null && !(UserPlayerSwapGame.isActive || HomeActivity.isActive || SelectGame.isActive || SwapGame.isActive || LuckyGame.isActive || JokerGame.isActive || User_Player_Lucky_Game.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }
        if(isEffectSound){
            mCountDownSound.stop(mSoundExposion);
            mCountDownSound.release();
        }
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
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
                    mCountDownSound.stop(mSoundExposion);
                    mCountDownSound.release();
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
                    mCountDownSound.stop(mSoundExposion);
                    mCountDownSound.release();
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
    public void onBackPressed() {
        super.onBackPressed();
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        Intent userSwapgame = new Intent(SwapGame.this, UserPlayerSwapGame.class);
        startActivity(userSwapgame);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(SwapGame.this, UserPlayerSwapGame.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
