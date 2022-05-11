package com.example.myapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreference {
    static String PREF_USER_EMAIL = "user_email";

    static public SharedPreferences getSharedPreferences(Context ctx) {//모든 액티비티에서 인스턴스 얻음
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String user_email) {//이메일 저장
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_EMAIL, user_email);
        editor.commit();//커밋은 필수
    }

    public static String getUserName(Context ctx) {//저장된 이메일 가져오기
        return getSharedPreferences(ctx).getString(PREF_USER_EMAIL, "");
    }

    public static void clearUserName(Context ctx) {//로그아웃 시 데이터 삭제
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();//커밋은 필수
    }
}