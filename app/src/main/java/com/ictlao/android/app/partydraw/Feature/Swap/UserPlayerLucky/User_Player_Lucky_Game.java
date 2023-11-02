package com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ictlao.android.app.partydraw.Core.AdapterChangeLanguages;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.UserPlayerSwap.UserPlayerSwapGame;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;

public class User_Player_Lucky_Game extends AppCompatActivity {
    // Memory data variable
    private ArrayList<String> mArrayListAddPlayerNames;
    private String mGetInputFromPlayer;
    private ArrayAdapter<String> mArrayAdapter;
    private int mIndex = 1;
    private ArrayList<String> mArrayListPreview;
    public static boolean isActive;    // set boolean make it static for stop and start BGM
    private boolean isEffectSound;
    private boolean isBGMSound;
    private boolean isSwitchBGM, isSwitchFX;
    private boolean isToastShow = false;
    private String getLanguage;
    private int mLanguageNumber;

    // String SharePreference
    private final String BGM = "BGM";
    private final String FX = "FX";
    private final String DL = "Language";


    // UI variable
    private Button mAddPlayerButton, mStartGameButton;
    private EditText mEditTextInputPlayerName;
    private ListView mListViewName;
    private AlertDialog mDialogDeletePlayer;
    private TextView mTextViewDeleteMessage;
    private Button mOkDeleteButton, mCancelDeleteButton;
    private Button mOkNotificationButton;
    private TextView mTextViewMessageNotification;
    private AlertDialog mDialogNotification;
    private AlertDialog mDialogSettings;
    private Switch mSwitchBGM, mSwitchEffect;
    private Button mOkSetting, mCancelSetting;
    private Spinner mChangeLanguageSpinner = null;
    private AlertDialog mDialogWarning;
    private Button mOkWarning, mExitWarning;
    private TextView mTextViewShowMessage;
    private PowerManager mPowerManager;
    private ActivityManager mActivityManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Toast mToastMessage;
    private Menu mMenu;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));    // function ນິ້ຈະຕ້ອງ ຂຽນໄວ້ທຸກ class ເພື່ອຈະໃຫ້ປ່ຽນພາສາໃນ class.
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__player__lucky__game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle(R.string.UserPlayerLuckyGameToolBarLuckyGame); // ສະແດງ Toolbar

        // ເຊີ່ອມໂຍງ ໄອດີ ແລະ ຕັ້ງຄ່າ ໃຫ້ຕົວປ່ຽນ
        mArrayListPreview = new ArrayList<>();
        mArrayListAddPlayerNames = new ArrayList<String>();
        mAddPlayerButton = findViewById(R.id.Add);
        mStartGameButton = findViewById(R.id.Start);
        mEditTextInputPlayerName = findViewById(R.id.Name);
        mListViewName = findViewById(R.id.ListView);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        // Set Event click to AddButton
        mAddPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetInputFromPlayer = mEditTextInputPlayerName.getText().toString();

                if(mArrayListAddPlayerNames.contains(mGetInputFromPlayer)) { // ຖ້າຫາກ ຊື່ທີ່ປ້ອນ ແລະ ຢູ່ໃນ List ຄື່ກັນ ຈະ ບໍ່ໃຫ້ບັນທືກ ຈະ ສະແດງ Message ແທນ.
                    String getStringMessage = getString(R.string.This_name) +" "+ mGetInputFromPlayer +" " + getString(R.string.already_exits);
                    CustomToastMessage(getStringMessage);
                }else if(mGetInputFromPlayer == null || mGetInputFromPlayer.trim().equals("")) { // ຖ້າຫາກ ບໍ່ມີການປ້ອນຂໍ້ມຸນ ກໍ່ ຈະສະແດງ Message ແຈ້ງເຕືອນ.
                    String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                    CustomToastMessage(getStringMessage);
                }else {  // ຖ້າບໍ່່ກົງກັບເງື່ອນໄຂ ຂ້າງເທິງແລ້ວ ຈະນໍາໃໄປ ເກັບໄວ້ໃນ ຕົວປ່ຽນ ArrayList ເພື່ອ ສະແດງ.
                    mArrayListAddPlayerNames.add(mGetInputFromPlayer);
                    if(mArrayListAddPlayerNames.size() != 0){
                        mArrayListPreview.add(mIndex +". "+ mGetInputFromPlayer);

                        mArrayAdapter = new ArrayAdapter<String>(User_Player_Lucky_Game.this, android.R.layout.simple_list_item_1, mArrayListPreview) { // ທຸກລາຍຊື່ທີ່ປ້ອນ ຈະ ຕ້ອງຜ່ານການ ປ່ຽນແປງ ໂດຍຜ່ານ ArrayAdapter ກ່ອນຈະ ໄປສະແດງ ໃນ ListView
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
                        mListViewName.setAdapter(mArrayAdapter);  // ນໍາລາຍຊື່ມາສະແດງຜົນ.
                        ((EditText) findViewById(R.id.Name)).setText("");  // ຈາກນັ້ນໃຫ້ ລ້າງລາຍຊື່ ອອກຈາກ EditText

                        // set Event click ໃຫ້ກັບ ລາຍຊຶ່ທີ່ຢູ່ໃນ List  ເພື່ອ ນໍາລາຍຊື່ນັ້ນອອກ ຫຼື ບໍ່.
                        mListViewName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(User_Player_Lucky_Game.this);
                                View viewDialog = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                mTextViewDeleteMessage = viewDialog.findViewById(R.id.show_delete_mesage);
                                mOkDeleteButton = viewDialog.findViewById(R.id.ok_delete_player);
                                mCancelDeleteButton = viewDialog.findViewById(R.id.cancel_delete_player);

                                if(getLanguage.equals("en")) {
                                    mTextViewDeleteMessage.setText("Would you like to delete this name" + "\n" + mArrayListAddPlayerNames.get(position) + "\n" + "from the list?");
                                    mOkDeleteButton.setText("OK");
                                    mCancelDeleteButton.setText("Cancel");
                                }else{
                                    mTextViewDeleteMessage.setText("ທ່ານຕ້ອງການລົບ ລາຍຊື່" + "\n" + mArrayListAddPlayerNames.get(position) + "\n" + "ອອກຈາກການຫຼີ້ນເກມຫຼືບໍ່?");
                                    mOkDeleteButton.setText("ຕົກລົງ");
                                    mCancelDeleteButton.setText("ຍົກເລີກ");
                                }

                                mOkDeleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDeletePlayer.dismiss();
                                        mArrayListPreview.clear();
                                        mArrayListAddPlayerNames.remove(position);
                                        mArrayAdapter.notifyDataSetChanged();
                                        if(mArrayListAddPlayerNames.size() > 0){
                                            for(int a = 0; a < mArrayListAddPlayerNames.size(); a++ ){
                                                mArrayListPreview.add(a+1+". "+ mArrayListAddPlayerNames.get(a));
                                                mArrayAdapter = new ArrayAdapter<String>(User_Player_Lucky_Game.this, android.R.layout.simple_list_item_1, mArrayListPreview){
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
                                                mListViewName.setAdapter(mArrayAdapter);
                                            }
                                            mIndex = mArrayListAddPlayerNames.size()+1;
                                        }else{
                                            mIndex = 1;
                                        }
                                    }
                                });

                                mCancelDeleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDeletePlayer.dismiss();
                                    }
                                });

                                builder.setView(viewDialog);
                                mDialogDeletePlayer = builder.create();
                                mDialogDeletePlayer.show();
                            }
                        });
                        mIndex++;
                    }
                }
            }
        });
        // set Event Click to Start Button ຫຼຶ ເລີ່ມເກມ  ໂດຍ ມີເງື່ອນໄຂ ວ່າ ຜູ່ ຈະຫຼີ້ນເກມ ຕ້ອງຫຼາຍກວ່າ 1 ຂຶ້ນໄປ.
        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mArrayListAddPlayerNames.size() > 1){
                    if(isToastShow){
                        mToastMessage.cancel();
                        isToastShow = false;
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    NavigateToLuckyGame();
                }else{
                    if(isToastShow){
                        mToastMessage.cancel();
                        isToastShow = false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(User_Player_Lucky_Game.this);
                    View view = getLayoutInflater().inflate(R.layout.notification_for_user_swap_and_lucky_game,null);

                    mOkNotificationButton = view.findViewById(R.id.ok_notification);
                    mTextViewMessageNotification = view.findViewById(R.id.message_notification);
                    if(getLanguage.equals("en")){
                        mTextViewMessageNotification.setText("Add the name of player more than one person!");
                        mOkNotificationButton.setText("OK");
                    }else {
                        mTextViewMessageNotification.setText("ກະລຸນາປ້ອນຊື່ຜູ່ຫຼີ້ນໃຫ້ຫຼາຍກວ່າໜຶ່ງຄົນ!");
                        mOkNotificationButton.setText("ຕົກລົງ");
                    }
                    mOkNotificationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogNotification.dismiss();
                        }
                    });

                    builder.setView(view);
                    mDialogNotification = builder.create();
                    mDialogNotification.show();
                }
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
        mToastMessage.setDuration(Toast.LENGTH_LONG);
        mToastMessage.setView(view);
        mToastMessage.show();
    }
    // navigate to swap game
    private void NavigateToLuckyGame(){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        Intent drawIntent = new Intent(User_Player_Lucky_Game.this, LuckyGame.class);
        drawIntent.putStringArrayListExtra("Names", mArrayListAddPlayerNames);
        drawIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(drawIntent);
    }
    // ເມື່ອຜູ່ຫຼີ້ນ ກົມ ໃສ່ ຮູບສັນຍາລັກ ສາມຈໍ້າ ຈະເປັນການເອີ້ນ Method ນີ້
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.home_menu, menu);
        this.mMenu = menu;
        return true;
    }

    private void UpdateMunuTitle(){
        MenuItem reset = mMenu.findItem(R.id.reset);
        MenuItem settings = mMenu.findItem(R.id.settings);
        if(getLanguage.equals("en")){
            reset.setTitle("Reset players");
            settings.setTitle("Settings");
        }else{
            reset.setTitle("ລ້າງລາຍຊື່ທັງໝົດ");
            settings.setTitle("ຕັ້ງຄ່າ");
        }
    }
    // Method ນີ້ ແມ່ນ ຜູ້ນໍາໃຊ້ ກົດ Reset Item ຈະເປັນການເອີ້ນ Method ນີ້.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        UpdateMunuTitle();
        switch (item.getItemId()){
            case R.id.reset:
                if(mArrayListAddPlayerNames.size() != 0) {
                    mArrayListAddPlayerNames.clear();
                    mArrayListPreview.clear();
                    mArrayAdapter.notifyDataSetChanged();
                    mListViewName.clearTextFilter();
                    mIndex = 1;
                }else{

                }
                return true;
            case R.id.settings:
                SettingDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SettingDialog(){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(User_Player_Lucky_Game.this);
        View view = getLayoutInflater().inflate(R.layout.setting_layout, null);

        mSwitchBGM = view.findViewById(R.id.switchOnAndOff);
        mSwitchEffect = view.findViewById(R.id.effect_sound);
        mChangeLanguageSpinner = view.findViewById(R.id.Language);
        mOkSetting = view.findViewById(R.id.Ok_main_setting);
        mCancelSetting = view.findViewById(R.id.cancel_main_setting);
        final String defLanguage = LocaleHelper.getLanguage(User_Player_Lucky_Game.this);
        boolean isEn = defLanguage.equals("en");
        onSelectLanguageChange(isEn);
        if(isEn){
            mSwitchBGM.setText("BGM");
            mSwitchEffect.setText("Sound FX");
            mOkSetting.setText("OK");
            mCancelSetting.setText("Cancel");
        }else{
            mSwitchBGM.setText("ສຽງເພງ");
            mSwitchEffect.setText("ສຽງປະກອບ");
            mOkSetting.setText("ຕົກລົງ");
            mCancelSetting.setText("ຍົກເລີກ");
        }
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);

        isSwitchBGM = isBGMSound;
        isSwitchFX = isEffectSound;
        mSwitchBGM.setChecked(isBGMSound);
        mSwitchEffect.setChecked(isEffectSound);

        // ------------------ switch sound effect -------------------------------------
        mSwitchEffect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSwitchEffect.setChecked(true);
                }else{
                    mSwitchEffect.setChecked(false);
                }
                isSwitchFX = isChecked;
            }
        });

        // -------------------switch BGM -----------------------------------------------
        mSwitchBGM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSwitchBGM.setChecked(true);
                }else{
                    mSwitchBGM.setChecked(false);
                }
                isSwitchBGM = isChecked;
            }
        });

        //---------------------------- dismiss dialog -------------------------------------
        mCancelSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLanguageNumber = 0;
                mDialogSettings.dismiss();
            }
        });

        //------------------------------ confirm setting ------------------------------
        mOkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLanguageNumber == 2 && !(defLanguage.equals("en"))){
                    if(mArrayListAddPlayerNames.size() == 0){
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
                        UpdateLanguage("en");
                    }else {
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
                        WarningDialog("en");
                    }
                }else if(mLanguageNumber == 1 && !(defLanguage.equals("lo-rLA"))){
                    if(mArrayListAddPlayerNames.size() == 0){
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
                        UpdateLanguage("lo-rLA");
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
                        WarningDialog("lo-rLA");
                    }
                }else {
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
            }
        });


        builder1.setView(view);
        mDialogSettings = builder1.create();
        mDialogSettings.show();
    }

    private void onSelectLanguageChange(boolean isEN)
    {
        final int[] flags = {R.drawable.laos_flag, R.drawable.english_flag};
        String[] countries = {"Lao language", "English language"};
        if(!isEN){
            countries = new String[]{"ພາສາລາວ","ພາສາອັງກິດ"};
        }
        AdapterChangeLanguages adapter = new AdapterChangeLanguages(this, flags, countries);
        mChangeLanguageSpinner.setAdapter(adapter);
        if(isEN){
            mChangeLanguageSpinner.setSelection(1);
        }
        mChangeLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onRealTimeChangeLanguage(flags[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void onRealTimeChangeLanguage(int flag)
    {
        switch (flag){
            case R.drawable.laos_flag:
                //mChangeLangageButton.setBackgroundResource(R.drawable.laos_background_flag);
                mSwitchBGM.setText("ສຽງເພງ");
                mSwitchEffect.setText("ສຽງປະກອບ");
                mOkSetting.setText("ຕົກລົງ");
                mCancelSetting.setText("ຍົກເລີກ");
                mLanguageNumber = 1;
                break;

            case R.drawable.english_flag:
                //mChangeLangageButton.setBackgroundResource(R.drawable.en_background_flag);
                mSwitchBGM.setText("BGM");
                mSwitchEffect.setText("Sound FX");
                mOkSetting.setText("OK");
                mCancelSetting.setText("Cancel");
                mLanguageNumber = 2;
                break;
        }
    }

    private void PlaySoundMusic(){
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(isBGMSound){
            HomeActivity.mBackgroundMusics = MediaPlayer.create(this, R.raw.chrismastown);
            HomeActivity.mBackgroundMusics.setLooping(true);
            HomeActivity.mBackgroundMusics.start();
        }else{
            HomeActivity.mBackgroundMusics.stop();
            HomeActivity.mBackgroundMusics.release();
            HomeActivity.mBackgroundMusics = null;
        }
    }

    private void UpdateLanguage(String language){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(DL, language);
        mEditor.commit();
        LocaleHelper.setLanguage(this, language);
        finish();
        startActivity(getIntent());
    }

    private void WarningDialog(final String lang){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.warning_layout, null);

        mExitWarning = view.findViewById(R.id.exit_dialog_waring);
        mTextViewShowMessage = view.findViewById(R.id.show_message_waring);
        mOkWarning = view.findViewById(R.id.ok_dialog_waring);

        if(mLanguageNumber == 2){
            mTextViewShowMessage.setText("Change language will remove all of name in the list"+". "+"Are you sure ?");
            mExitWarning.setText("Cancel");
            mOkWarning.setText("OK");
        }else{
            mTextViewShowMessage.setText("ປ່ຽນພາສາ ຈະເຮັດໃຫ້ລາຍຊື່ທັງໝົດຖືກລືບ"+". "+"ທ່ານຕ້ອງການປ່ຽນພາສາຫຼືບໍ່?");
            mExitWarning.setText("ຍົກເລີກ");
            mOkWarning.setText("ຕົກລົງ");
        }
        mExitWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogWarning.dismiss();
                //updateView(lang);
            }
        });

        mOkWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogWarning.dismiss();
                UpdateLanguage(lang);
            }
        });

        builder1.setView(view);
        mDialogWarning = builder1.create();
        mDialogWarning.show();
    }

    @Override
    protected void onStop() {
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics != null && !(SelectGame.isActive || LuckyGame.isActive || HomeActivity.isActive || UserPlayerSwapGame.isActive || User_Player_Lucky_Game.isActive || JokerGame.isActive || SwapGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics.stop();
                HomeActivity.mBackgroundMusics.release();
                HomeActivity.mBackgroundMusics = null;
            }
        }
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        super.onStop();
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
        getLanguage = LocaleHelper.getLanguage(this);
        if(getLanguage.equals("en")){
            mEditTextInputPlayerName.setHint("Please enter the player right here");
            mStartGameButton.setText("Start");
            mAddPlayerButton.setText("Add");
            getSupportActionBar().setTitle("Lucky Draw Game");
        }else {
            mEditTextInputPlayerName.setHint("ກະລຸນາປ້ອນຊື່ຜູ່ຫຼີ້ນບ່ອນນີ້");
            mStartGameButton.setText("ເລີ່ມເກມ");
            mAddPlayerButton.setText("ເພີ່ມຊື່");
            getSupportActionBar().setTitle("ເກມ Lucky Draw");
        }
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(User_Player_Lucky_Game.this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }

        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(User_Player_Lucky_Game.this, R.raw.chrismastown);
                    HomeActivity.mBackgroundMusics.start();
                    HomeActivity.mBackgroundMusics.setLooping(true);
                }
            }
        }
        isActive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isBGMSound = mSharedPreferences.getBoolean(BGM,true);
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
                if (isBGMSound) {
                    HomeActivity.mBackgroundMusics.stop();
                    HomeActivity.mBackgroundMusics.release();
                    HomeActivity.mBackgroundMusics = null;
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
        Intent selectGame = new Intent(User_Player_Lucky_Game.this, SelectGame.class);
        startActivity(selectGame);
    }
}
