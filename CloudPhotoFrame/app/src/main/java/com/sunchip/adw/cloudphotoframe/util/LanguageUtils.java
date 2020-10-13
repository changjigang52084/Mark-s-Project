package com.sunchip.adw.cloudphotoframe.util;

import android.app.backup.BackupManager;
import android.content.res.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * 更新系统语言
 */
public class LanguageUtils {

    public static void updateLanguage(Locale locale) {
        try {
            Object objIActMag, objActMagNative;

            Class clzIActMag = Class.forName("android.app.IActivityManager");

            Class clzActMagNative = Class.forName("android.app.ActivityManagerNative");

            Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");

            objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);

            Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");

            Configuration configuration = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);

            configuration.locale = locale;

            Class clzConfig = Class.forName("android.content.res.Configuration");

            java.lang.reflect.Field userSetLocale = clzConfig.getField("userSetLocale");

            userSetLocale.set(configuration, true);

            Class[] clzParams = {Configuration.class};

            Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);

            mtdIActMag$updateConfiguration.invoke(objIActMag, configuration);

            BackupManager.dataChanged("com.android.providers.settings");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
