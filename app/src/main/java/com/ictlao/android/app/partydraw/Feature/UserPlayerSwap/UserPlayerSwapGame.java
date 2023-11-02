package com.ictlao.android.app.partydraw.Feature.UserPlayerSwap;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ictlao.android.app.partydraw.Core.AdapterChangeLanguages;
import com.ictlao.android.app.partydraw.Core.LocaleHelper;
import com.ictlao.android.app.partydraw.Feature.CreateGroup.CreateGroupActivity;
import com.ictlao.android.app.partydraw.Feature.Home.HomeActivity;
import com.ictlao.android.app.partydraw.Feature.Joker.JokerGame;
import com.ictlao.android.app.partydraw.Feature.Lucky.LuckyGame;
import com.ictlao.android.app.partydraw.Feature.OnlineJoker.OnlineJokerDraw;
import com.ictlao.android.app.partydraw.Feature.Select.SelectGame;
import com.ictlao.android.app.partydraw.Feature.Swap.SwapGame;
import com.ictlao.android.app.partydraw.Feature.Swap.UserPlayerLucky.User_Player_Lucky_Game;
import com.ictlao.android.app.partydraw.Feature.Waiting.Waiting_Activity;
import com.ictlao.partydraw.R;

import java.util.ArrayList;
import java.util.List;

public class UserPlayerSwapGame extends AppCompatActivity {
    // Memory data variable
    public static boolean isActive;    // set boolean make it static for stop and start BGM
    private ArrayList<String> mAddNameToArray;
    private String mNameGetFromInput;
    private ArrayAdapter<String> mArrayAdapter;
    private int mIncrementPreviewNumber =1;
    private ArrayList<String> mPreviewArray;
    private int mNumberLanguage;
    private boolean isEffectSound;
    private boolean isBGMSound;
    private boolean isSwitchBGM,isSwitchFX;
    private boolean isToastShow = false;
    private String getLanguage;

    // String SharePreference
    private final String BGM = "BGM";
    private final String FX = "FX";
    private final String DL = "Language";

    // UI variable
    private Button mAddButton, mStartButtton;
    private EditText mInputName;
    private ListView mListName;
    private AlertDialog mDialogDeleteConfirm, mDialogWarning;
    private TextView mMessageConfirm;
    private Button mOkDeleteButton, mCancelDeleteButton;
    private Button mOkWaring;
    private TextView mMessageWaring;
    private AlertDialog mDialogSettings;
    private Switch mSwitchBGMSound, mSwitchEffectSound;
    private Button mOk_settingButton, mCancel_settingButton;
    private Spinner mChangeLanguageSpinner = null;
    private AlertDialog mDialogConfirmChangeLanguage;
    private Button mOkChangeLanguage, mExitChangeLanguage;
    private TextView mMessageConfirmChangeLanguage;
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
        setContentView(R.layout.activity_user_player_swap_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportActionBar().setTitle(R.string.swap_draw_game_title); // ສະແດງ Toolbar
        // all control background music
        mSharedPreferences = getSharedPreferences(HomeActivity.Settings, 0);
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);

        // ເຊີ່ອມໂຍງ ໄອດີ  ແລະ ຕັ້ງຄ່າໃຫ້ຕົວປ່ຽນ.
        mPreviewArray = new ArrayList<>();
        mAddNameToArray = new ArrayList<>();
        mAddButton = findViewById(R.id.Add);
        mStartButtton = findViewById(R.id.Start);
        mInputName = findViewById(R.id.Name);
        mListName = findViewById(R.id.ListView);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        // set Event Click to AddButton
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNameGetFromInput = mInputName.getText().toString();

                if(mAddNameToArray.contains(mNameGetFromInput)) {   // ຖ້າຫາກ ຊື່ທີ່ປ້ອນ ແລະ ຢູ່ໃນ List ຄື່ກັນ ຈະ ບໍ່ໃຫ້ບັນທືກ ຈະ ສະແດງ Message ແທນ.
                    //Toast.makeText(getBaseContext(), getString(R.string.This_name) +" "+ GetInput+" " + getString(R.string.already_exits), Toast.LENGTH_LONG).show();
                    String getStringMessage = getString(R.string.This_name) + " "+ mNameGetFromInput + " "+ getString(R.string.already_exits);
                    CustomToastMessage(getStringMessage);
                }else if(mNameGetFromInput == null || mNameGetFromInput.trim().equals("")) {  // ຖ້າຫາກ ບໍ່ມີການປ້ອນຂໍ້ມຸນ ກໍ່ ຈະສະແດງ Message ແຈ້ງເຕືອນ.
                    //Toast.makeText(getBaseContext(),  R.string.Enter_Player_befor_home, Toast.LENGTH_LONG).show();
                    String getStringMessage = getString(R.string.UserPlayerLuckyGameActivityPleaseEnterPlayerNameBefore);
                    CustomToastMessage(getStringMessage);
                }else {  // ຖ້າບໍ່່ກົງກັບເງື່ອນໄຂ ຂ້າງເທິງແລ້ວ ຈະນໍາໃໄປ ເກັບໄວ້ໃນ ຕົວປ່ຽນ ArrayList ເພື່ອ ສະແດງ.
                    mAddNameToArray.add(mNameGetFromInput);
                    if(mAddNameToArray.size() != 0){
                        mPreviewArray.add(mIncrementPreviewNumber +". "+ mNameGetFromInput);

                        mArrayAdapter = new ArrayAdapter<String>(UserPlayerSwapGame.this, android.R.layout.simple_list_item_1, mPreviewArray) {  // ທຸກລາຍຊື່ທີ່ປ້ອນ ຈະ ຕ້ອງຜ່ານການ ປ່ຽນແປງ ໂດຍຜ່ານ ArrayAdapter ກ່ອນຈະ ໄປສະແດງ ໃນ ListView
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
                        mListName.setAdapter(mArrayAdapter);  // ນໍາລາຍຊື່ມາສະແດງຜົນ.
                        ((EditText) findViewById(R.id.Name)).setText(""); // ຈາກນັ້ນໃຫ້ ລ້າງລາຍຊື່ ອອກຈາກ EditText

                        // set Event click ໃຫ້ກັບ ລາຍຊຶ່ທີ່ຢູ່ໃນ List  ເພື່ອ ນໍາລາຍຊື່ນັ້ນອອກ ຫຼື ບໍ່.
                        mListName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(UserPlayerSwapGame.this);
                                View viewDialog = getLayoutInflater().inflate(R.layout.delete_player_popup, null);

                                mMessageConfirm = viewDialog.findViewById(R.id.show_delete_mesage);
                                mOkDeleteButton = viewDialog.findViewById(R.id.ok_delete_player);
                                mCancelDeleteButton = viewDialog.findViewById(R.id.cancel_delete_player);
                                if(getLanguage.equals("en")) {
                                    mMessageConfirm.setText("Would you like to delete this name" + "\n" + mAddNameToArray.get(position) + "\n" + "from the list?");
                                    mOkDeleteButton.setText("OK");
                                    mCancelDeleteButton.setText("Cancel");
                                }else{
                                    mMessageConfirm.setText("ທ່ານຕ້ອງການລົບ ລາຍຊື່" + "\n" + mAddNameToArray.get(position) + "\n" + "ອອກຈາກການຫຼີ້ນເກມຫຼືບໍ່?");
                                    mOkDeleteButton.setText("ຕົກລົງ");
                                    mCancelDeleteButton.setText("ຍົກເລີກ");
                                }
                                mOkDeleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDeleteConfirm.dismiss();
                                        mPreviewArray.clear();
                                        mAddNameToArray.remove(position);
                                        mArrayAdapter.notifyDataSetChanged();
                                        if(mAddNameToArray.size() > 0){
                                            for(int a = 0; a < mAddNameToArray.size(); a++ ){
                                                mPreviewArray.add(a+1+". "+ mAddNameToArray.get(a));
                                                mArrayAdapter = new ArrayAdapter<String>(UserPlayerSwapGame.this, android.R.layout.simple_list_item_1, mPreviewArray){
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
                                                mListName.setAdapter(mArrayAdapter);
                                            }
                                            mIncrementPreviewNumber = mAddNameToArray.size()+1;
                                        }else{
                                            mIncrementPreviewNumber = 1;
                                        }
                                    }
                                });

                                mCancelDeleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mDialogDeleteConfirm.dismiss();
                                    }
                                });

                                builder.setView(viewDialog);
                                mDialogDeleteConfirm = builder.create();
                                mDialogDeleteConfirm.show();

                            }
                        });
                        mIncrementPreviewNumber++;
                    }
                }
            }
        });
        // set Event Click to Start Button ຫຼຶ ເລີ່ມເກມ  ໂດຍ ມີເງື່ອນໄຂ ວ່າ ຜູ່ ຈະຫຼີ້ນເກມ ຕ້ອງຫຼາຍກວ່າ 1 ຂຶ້ນໄປ.
        mStartButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAddNameToArray.size() > 1){
                    if(isToastShow){
                        mToastMessage.cancel();
                        isToastShow = false;
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    NavigateToSwapGame();
                }else{
                    if(isToastShow){
                        mToastMessage.cancel();
                        isToastShow = false;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserPlayerSwapGame.this);
                    View view = getLayoutInflater().inflate(R.layout.notification_for_user_swap_and_lucky_game,null);

                    mOkWaring = view.findViewById(R.id.ok_notification);
                    mMessageWaring = view.findViewById(R.id.message_notification);
                    if(getLanguage.equals("en")){
                        mMessageWaring.setText("Add the name of player more than one person!");
                        mOkWaring.setText("OK");
                    }else {
                        mMessageWaring.setText("ກະລຸນາປ້ອນຊື່ຜູ່ຫຼີ້ນໃຫ້ຫຼາຍກວ່າໜຶ່ງຄົນ!");
                        mOkWaring.setText("ຕົກລົງ");
                    }
                    mOkWaring.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialogWarning.dismiss();
                        }
                    });

                    builder.setView(view);
                    mDialogWarning = builder.create();
                    mDialogWarning.show();
                }
            }
        });
    }

    // create toast message with custome layout
    private void CustomToastMessage(String message){
        if(isToastShow){
            mToastMessage.cancel();
            isToastShow = false;
        }
        isToastShow = true;
        View view = getLayoutInflater().inflate(R.layout.toast_message_waring,(ViewGroup) findViewById(R.id.show_message));
        TextView message_body = view.findViewById(R.id.show_message_body);
        message_body.setText(message);
        mToastMessage = new Toast(getApplicationContext());
        mToastMessage.setGravity(Gravity.BOTTOM,0,0);
        mToastMessage.setDuration(Toast.LENGTH_LONG);
        mToastMessage.setView(view);
        mToastMessage.show();
    }

    // ສົ່ງລາຍຊື່ ທັງໝົດທີ່ມີຢູ່ ໃນ List ໄປຫາ Class SwapGame
    private void NavigateToSwapGame(){
        Intent intent = new Intent(this, SwapGame.class);
        intent.putStringArrayListExtra("Names", mAddNameToArray);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // ເມືອຜູ່ນໍາໃຊ້ ກົດສັນຍາລັບ ສາມ ຈໍ້າຈະ ເປັນ ການເອີ້ນ Method ນີ້.
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
    // ເມື່ອ ຜູ່ນໍາໃຊ້ ກົດ Reset item ຈະເປັນການເອີ້ນ Method ນີ້
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        UpdateMunuTitle();
        switch (item.getItemId()){
            case R.id.reset:
                if(mAddNameToArray.size() != 0) {
                    mAddNameToArray.clear();
                    mPreviewArray.clear();
                    mArrayAdapter.notifyDataSetChanged();
                    mListName.clearTextFilter();
                    mIncrementPreviewNumber = 1;
                }
                return true;
            case R.id.settings:
                SettingDialog();
                return true;

            case android.R.id.home:
                Intent intent = new Intent(UserPlayerSwapGame.this, SelectGame.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // show setting dialog
    private void SettingDialog(){

        if(mDialogSettings != null)
        {
            mDialogSettings = null;
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(UserPlayerSwapGame.this);
        View view = getLayoutInflater().inflate(R.layout.setting_layout, null);

        mSwitchBGMSound = view.findViewById(R.id.switchOnAndOff);
        mSwitchEffectSound = view.findViewById(R.id.effect_sound);
        mChangeLanguageSpinner = view.findViewById(R.id.Language);
        mOk_settingButton = view.findViewById(R.id.Ok_main_setting);
        mCancel_settingButton = view.findViewById(R.id.cancel_main_setting);
        final String defLanguage = LocaleHelper.getLanguage(UserPlayerSwapGame.this);
        boolean isEn = defLanguage.equals("en");
        onSelectLanguageChange(isEn);
        if(isEn){
            mSwitchEffectSound.setText("Sound FX");
            mSwitchBGMSound.setText("BGM");
            mOk_settingButton.setText("OK");
            mCancel_settingButton.setText("Cancel");
        }else{
            mSwitchEffectSound.setText("ສຽງປະກອບ");
            mSwitchBGMSound.setText("ສຽງພື້ນຫຼັງ");
            mOk_settingButton.setText("ຕົກລົງ");
            mCancel_settingButton.setText("ຍົກເລີກ");
        }
        isEffectSound = mSharedPreferences.getBoolean(FX,true);
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        isSwitchBGM = isBGMSound;
        isSwitchFX = isEffectSound;
        mSwitchBGMSound.setChecked(isBGMSound);
        mSwitchEffectSound.setChecked(isEffectSound);
        // ------------------ switch sound effect -------------------------------------
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

        // -------------------switch BGM -----------------------------------------------
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

        //---------------------------- dismiss dialog -------------------------------------
        mCancel_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNumberLanguage = 0;
                mDialogSettings.dismiss();
            }
        });

        //------------------------------ confirm setting ------------------------------
        mOk_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumberLanguage == 2 && !(defLanguage.equals("en"))){
                    if(mAddNameToArray.size() == 0){
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
                }else if(mNumberLanguage == 1 && !(defLanguage.equals("lo-rLA"))){
                    if(mAddNameToArray.size() == 0){
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

    // set flags and country names into the spinner to select language
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

    // change the language at the time when player change selection
    private void onRealTimeChangeLanguage(int flag)
    {
        switch (flag){
            case R.drawable.laos_flag:
                //mChangeLangageButton.setBackgroundResource(R.drawable.laos_background_flag);
                mSwitchBGMSound.setText("ສຽງເພງ");
                mSwitchEffectSound.setText("ສຽງປະກອບ");
                mOk_settingButton.setText("ຕົກລົງ");
                mCancel_settingButton.setText("ຍົກເລີກ");
                mNumberLanguage = 1;
                break;

            case R.drawable.english_flag:
                //mChangeLangageButton.setBackgroundResource(R.drawable.en_background_flag);
                mSwitchBGMSound.setText("BGM");
                mSwitchEffectSound.setText("Sound FX");
                mOk_settingButton.setText("OK");
                mCancel_settingButton.setText("Cancel");
                mNumberLanguage = 2;
                break;
        }
    }

    // check to play and stop background music
    private void PlaySoundMusic(){
        isEffectSound = mSharedPreferences.getBoolean(FX, true);
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

    @SuppressLint("NewApi")
    private void UpdateLanguage(String language){
        LocaleHelper.setLanguage(this, language);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(DL, language);
        mEditor.commit();
        finish();
        startActivity(getIntent());
    }

    // show dialog to player
    private void WarningDialog(final String lang){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.warning_layout, null);

        mExitChangeLanguage = view.findViewById(R.id.exit_dialog_waring);
        mMessageConfirmChangeLanguage = view.findViewById(R.id.show_message_waring);
        mOkChangeLanguage = view.findViewById(R.id.ok_dialog_waring);
        if(mNumberLanguage == 2){
            mMessageConfirmChangeLanguage.setText("Change language will remove all of name in the list"+". "+"Are you sure ?");
            mExitChangeLanguage.setText("Cancel");
            mOkChangeLanguage.setText("OK");
        }else{
            mMessageConfirmChangeLanguage.setText("ປ່ຽນພາສາ ຈະເຮັດໃຫ້ລາຍຊື່ທັງໝົດຖືກລືບ"+". "+"ທ່ານຕ້ອງການປ່ຽນພາສາຫຼືບໍ່?");
            mExitChangeLanguage.setText("ຍົກເລີກ");
            mOkChangeLanguage.setText("ຕົກລົງ");
        }
        mExitChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogConfirmChangeLanguage.dismiss();
                //updateView(lang);
            }
        });

        mOkChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogConfirmChangeLanguage.dismiss();
                UpdateLanguage(lang);
            }
        });

        builder1.setView(view);
        mDialogConfirmChangeLanguage = builder1.create();
        mDialogConfirmChangeLanguage.show();
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
                if(isBGMSound){
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
            mInputName.setHint("Please enter the player right here");
            mStartButtton.setText("Start");
            mAddButton.setText("Add");
            getSupportActionBar().setTitle("Swap Draw Game");
        }else {
            mInputName.setHint("ກະລຸນາປ້ອນຊື່ຜູ່ຫຼີ້ນບ່ອນນີ້");
            mStartButtton.setText("ເລີ່ມເກມ");
            mAddButton.setText("ເພີ່ມຊື່");
            getSupportActionBar().setTitle("ເກມ Swap Draw");
        }
        isBGMSound = mSharedPreferences.getBoolean(BGM, true);
        if(HomeActivity.mBackgroundMusics == null){
            if(isBGMSound){
                HomeActivity.mBackgroundMusics = MediaPlayer.create(UserPlayerSwapGame.this, R.raw.chrismastown);
                HomeActivity.mBackgroundMusics.start();
                HomeActivity.mBackgroundMusics.setLooping(true);
            }
        }
        if(mPowerManager.isScreenOn()){
            if(HomeActivity.mBackgroundMusics == null){
                if(isBGMSound){
                    HomeActivity.mBackgroundMusics = MediaPlayer.create(UserPlayerSwapGame.this, R.raw.chrismastown);
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
        if(HomeActivity.mBackgroundMusics != null && !(SelectGame.isActive || SwapGame.isActive || HomeActivity.isActive || User_Player_Lucky_Game.isActive || UserPlayerSwapGame.isActive || LuckyGame.isActive || JokerGame.isActive || CreateGroupActivity.isActive || Waiting_Activity.isActive || OnlineJokerDraw.isActive)){
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
    public void onBackPressed() {
        super.onBackPressed();
        if (isToastShow) {
            mToastMessage.cancel();
            isToastShow = false;
        }
        Intent selectGame = new Intent(UserPlayerSwapGame.this, SelectGame.class);
        startActivity(selectGame);
    }
}
