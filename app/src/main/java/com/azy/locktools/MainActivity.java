package com.azy.locktools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.azy.locktools.activity.ICCardActivity;
import com.azy.locktools.activity.PasswordActivity;
import com.azy.locktools.adapter.LockInfoAdapter;
import com.azy.locktools.entity.LockInfo;
import com.azy.locktools.dialog.AddLockDialog;
import com.azy.locktools.retrofit.ApiService;
import com.azy.locktools.retrofit.RetrofitAPIManager;
import com.azy.locktools.utils.LockUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ttlock.bl.sdk.api.EncryptionUtil;
import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ScanLockCallback;
import com.ttlock.bl.sdk.entity.LockData;
import com.ttlock.bl.sdk.entity.LockError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements LockInfoAdapter.onListItemClick {

    private LockInfoAdapter lockInfoAdapter;

    //private boolean isAutoCon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取RecyclerView的引用，并对其进行设置
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.card_list);
        mRecyclerView.setHasFixedSize(true);

        //这里我们选择创建一个LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //为RecyclerView对象指定我们创建得到的layoutManager
        mRecyclerView.setLayoutManager(layoutManager);

        lockInfoAdapter = new LockInfoAdapter(this);
        lockInfoAdapter.setOnListItemClick(this);
        mRecyclerView.setAdapter(lockInfoAdapter);

        TTLockClient.getDefault().prepareBTService(getApplicationContext());


        loadLockInfoList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.title_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                AddLockDialog addLockDialog = new AddLockDialog(this);
                addLockDialog.setLoginListener(v -> {
                    String name = addLockDialog.getName().getText().toString();
                    String lockKey = addLockDialog.getLockKey().getText().toString();
                    if ("".equals(name) || "".equals(lockKey)) {
                        LockUtils.toast(getApplicationContext(), "不能为空");
                        return;
                    }
                    if (saveLockKey(name, lockKey)) {
                        loadLockInfoList();
                    } else {
                        LockUtils.toast(this, "密钥无效，请检查是否填写错误!");
                    }
                    addLockDialog.dismiss();

                });
                addLockDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public Boolean saveLockKey(String name, String key) {
        LockData lockData = EncryptionUtil.parseLockData(key);
        if (lockData == null) {
            return false;
        }
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("lockInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(name, key);
            editor.commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void getLockInfo(String studentCode, String password) {
        LockUtils.buildProgressDialog("正在添加锁...", this);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        HashMap<String, String> param = new HashMap<>(6);
        param.put("student", studentCode);
        param.put("password", password);
        Call<ResponseBody> call = apiService.login(param);
        RetrofitAPIManager.enqueue(call, new TypeToken<Object>() {
        }, result -> {
            if (!result.success) {
                LockUtils.cancelProgressDialog();
                Toast.makeText(this, result.getMsg(), Toast.LENGTH_SHORT).show();
                return;
            }
            Call<ResponseBody> call2 = apiService.GetStudentDoor();
            RetrofitAPIManager.enqueue(call2, new TypeToken<LockInfo>() {
            }, result1 -> {
                //保存锁信息
                LockInfo lockInfo = result1.getResult();
                SharedPreferences sharedPreferences = getSharedPreferences("lockInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String msg = "添加锁成功";

                if (!"".equals(sharedPreferences.getString(lockInfo.getDoorBluetoothName(), ""))) {
                    msg = "锁信息已更新";
                }

                editor.putString(lockInfo.getDoorBluetoothName(), new Gson().toJson(lockInfo));
                editor.commit();

                loadLockInfoList();

                LockUtils.cancelProgressDialog();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }, requestError -> {
                LockUtils.cancelProgressDialog();
                LockUtils.toast(this, "添加锁失败");
            });
        }, requestError -> {
            LockUtils.cancelProgressDialog();
            LockUtils.toast(this, "添加锁失败");
        });


    }

    public void loadLockInfoList() {
        List<LockInfo> lockInfoList = new ArrayList<LockInfo>();
        SharedPreferences sharedPreferences = getSharedPreferences("lockInfo", Context.MODE_PRIVATE);
        Map<String, ?> all = sharedPreferences.getAll();
        Gson gson = new Gson();
        for (String key : all.keySet()) {
            LockData lockData = EncryptionUtil.parseLockData(all.get(key).toString());
            if (lockData != null) {
                LockInfo lockInfo = new LockInfo(key, lockData.getLockName(), lockData.getLockMac(), all.get(key).toString());
                lockInfoList.add(lockInfo);
            }
        }
        //启动软件默认连接第一个蓝牙
//        if (lockInfoList.size() != 0 && !isAutoCon) {
//            String key = lockInfoList.get(0).getDoorBluetoothKey();
//            TTLockClient.getDefault().connectLock(key, new ConnectLockCallback() {
//                @Override
//                public void onConnectSuccess() {
//                    isAutoCon = true;
//                    LockUtils.toast(MainActivity.this, "连接第一个蓝牙成功");
//                }
//
//                @Override
//                public void onFail(LockError lockError) {
//                    LockUtils.toast(MainActivity.this, "默认连接第一个蓝牙失败");
//                }
//            });
//        }
        //加载锁列表
        lockInfoAdapter.updateData(lockInfoList);
    }

    public void delLockInfo(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("lockInfo", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(key).commit();
        loadLockInfoList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTLockClient.getDefault().stopBTService();
    }

    @Override
    public void onItemClick(LockInfo info) {

        Log.d("info----->", info.getName());
        final String[] items = {"蓝牙开锁", "密码管理", "房卡管理", "复制lockKey", "删除"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle(info.getName());
        listDialog.setItems(items, (dialog, which) -> {
            if (items[which].equals("蓝牙开锁")) {

                LockUtils.doUnlock(this, info);
                return;
            }
            if (items[which].equals("密码管理")) {
                Intent mIntent = new Intent(this, PasswordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("lock", LockUtils.gson.toJson(info));
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                return;
            }
            if (items[which].equals("房卡管理")) {
                Intent mIntent = new Intent(this, ICCardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("lock", LockUtils.gson.toJson(info));
                mIntent.putExtras(bundle);
                startActivity(mIntent);
                return;
            }
            if (items[which].equals("复制lockKey")) {
                //获取剪贴板管理器
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                SharedPreferences sharedPreferences = getSharedPreferences("lockInfo", Context.MODE_PRIVATE);
                sharedPreferences.getAll().forEach((k, v) -> {
                    if (k.equals(info.getName())) {
                        ClipData mClipData = ClipData.newPlainText("Label", v.toString());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        LockUtils.toast(this, "复制成功");
                    }
                });
                return;
            }
            if (items[which].equals("删除")) {
                delLockInfo(info.getName());
            }
        });
        listDialog.show();
    }
}