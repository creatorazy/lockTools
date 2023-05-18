package com.azy.locktools;

import android.app.Application;
import android.content.Context;

import com.azy.locktools.entity.LockInfo;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }
}
