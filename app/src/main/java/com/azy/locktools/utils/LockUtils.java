package com.azy.locktools.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.azy.locktools.MainActivity;
import com.azy.locktools.activity.PasswordActivity;
import com.azy.locktools.entity.LockInfo;
import com.google.gson.Gson;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.callback.CreateCustomPasscodeCallback;
import com.ttlock.bl.sdk.callback.DeleteICCardCallback;
import com.ttlock.bl.sdk.callback.DeletePasscodeCallback;
import com.ttlock.bl.sdk.callback.GetAllValidICCardCallback;
import com.ttlock.bl.sdk.callback.GetAllValidPasscodeCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;

public class LockUtils {

    private static ProgressDialog progressDialog;

    public static Gson gson = new Gson();

    /**
     * use eKey for controlLock interface
     */
    public static void doUnlock(Activity activity, LockInfo lockInfo) {
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, lockInfo.getDoorBluetoothKey(), lockInfo.getDoorBluetoothMac(), new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(ControlLockResult controlLockResult) {
                cancelProgressDialog();
                toast(activity, "lock is unlock  success!");
            }

            @Override
            public void onFail(LockError error) {
                cancelProgressDialog();
                toast(activity, "unLock fail!--" + error.getDescription());
            }
        });
    }


    /**
     * get all valid passcodes in lock .
     */
    public static void getAllValidPasscodes(Activity activity, GetAllValidPasscodeCallback callback, LockInfo lockInfo) {
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        TTLockClient.getDefault().getAllValidPasscodes(lockInfo.getDoorBluetoothKey(), lockInfo.getDoorBluetoothMac(), callback);
    }

    public static void deletePasscode(Activity activity, DeletePasscodeCallback callback, LockInfo lockInfo, String password) {
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        TTLockClient.getDefault().deletePasscode(password, lockInfo.getDoorBluetoothKey(), lockInfo.getDoorBluetoothMac(), callback);
    }

    /**
     * NOTICE !!
     * Passcode range : 4 - 9 Digits in length.
     */
    public static void createCustomPasscode(Activity activity, CreateCustomPasscodeCallback callback, String password, LockInfo info) {
        long startDate = System.currentTimeMillis();
        long endDate = 0;
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        if (TextUtils.isEmpty(password)) {
            toast(activity, "passcode is required");
            return;
        }
        TTLockClient.getDefault().createCustomPasscode(password, startDate, endDate, info.getDoorBluetoothKey(), info.getDoorBluetoothMac(), callback);
    }


    public static void getAllCards(Activity activity, GetAllValidICCardCallback callback, LockInfo info) {
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        TTLockClient.getDefault().getAllValidICCards(info.getDoorBluetoothKey(), info.getDoorBluetoothMac(), callback);
    }

    public static void deleteCard(Activity activity, DeleteICCardCallback callback, LockInfo info, String cardNumber) {
        ensureBluetoothIsEnabled(activity);
        showConnectLockLoading(activity);
        TTLockClient.getDefault().deleteICCard(cardNumber, info.getDoorBluetoothKey(), info.getDoorBluetoothMac(), callback);
    }

    public static void dialog(Context context, String msg, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("提示")//设置对话框 标题
                .setMessage(msg);
        builder.setPositiveButton("确认", clickListener);
        builder.setNegativeButton("取消", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    public static void dialogTip(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("提示")//设置对话框 标题
                .setMessage(msg);
        builder.setPositiveButton("确认", (dialogInterface, i) -> {
        });
        builder.create().show();
    }

    /**
     * make sure Bluetooth is enabled.
     */
    public static void ensureBluetoothIsEnabled(Activity activity) {
        if (!TTLockClient.getDefault().isBLEEnabled(activity)) {
            TTLockClient.getDefault().requestBleEnable(activity);
        }
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showConnectLockLoading(Activity activity) {
        cancelProgressDialog();
        buildProgressDialog("start connect lock...", activity);
    }

    /**
     * 加载框
     */
    public static void buildProgressDialog(String msg, Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                TTLockClient.getDefault().clearAllCallback();
                TTLockClient.getDefault().disconnect();
                Log.w("c", "取消");
            }
        });
        progressDialog.show();
    }

    /**
     * @Description: TODO 取消加载框
     * @author Sunday
     * @date 2015年12月25日
     */
    public static void cancelProgressDialog() {
        if (progressDialog != null)
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
    }

}
