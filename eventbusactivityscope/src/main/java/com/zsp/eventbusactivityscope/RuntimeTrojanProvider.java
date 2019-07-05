package com.zsp.eventbusactivityscope;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;

/**
 * @decs: RuntimeTrojanProvider
 * Internal class to initialize EventBusActivityScope.
 * @author: 郑少鹏
 * @date: 2019/6/17 18:10
 */
public class RuntimeTrojanProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        EventBusActivityScope.init(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
