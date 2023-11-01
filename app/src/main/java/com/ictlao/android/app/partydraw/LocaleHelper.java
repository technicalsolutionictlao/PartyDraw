package com.ictlao.android.app.partydraw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {
    private static final String SELECTED_LANGUAGE = "Language.Helper.Selected.Language";

    // Method ນີ້ ແມ່ນ ຈະ ຖືກນໍໍາໄປຂຽນໃວ້ໃນ ທຸກ Class ເພື່ອ ນໍາເອົາ Default ພາສາ ມາສະແດງ.
    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLanguage(context, lang);
    }

    // sets application locale with default locale persisted in preference manager on each new launch of application and
    // returns Context having application default locale
    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLanguage(context, lang);
    }

    // returns language code persisted in preference manager
    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    // persists new language code change in preference manager and updates application default locale
    // returns Context having application default locale
    public static Context setLanguage(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        else {
            return updateResourcesLegacy(context, language);
        }
    }

    // returns language code persisted in preference manager
    public static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    // persists new language code in preference manager
    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    // For android device versions above Nougat (7.0)
    // updates application default locale configurations and
    // returns new Context object for the current Context but whose resources are adjusted to match the given Configuration
    @SuppressLint("NewApi")
    private static Context updateResources(Context context, String language) {

        Locale locale = new Locale(language);

        Configuration configuration = context.getResources().getConfiguration();

        LocaleList localeList = new LocaleList(locale);
        localeList.setDefault(localeList);
        configuration.setLocales(localeList);

        return context.createConfigurationContext(configuration);
    }

    // For android device versions below Nougat (7.0)
    // updates application default locale configurations and
    // returns new Context object for the current Context but whose resources are adjusted to match the given Configuration
    private static Context updateResourcesLegacy(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        Locale.setDefault(locale);
        configuration.setLocale(locale);

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

}
