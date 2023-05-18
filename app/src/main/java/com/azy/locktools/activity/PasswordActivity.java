package com.azy.locktools.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azy.locktools.R;
import com.azy.locktools.adapter.PasswordInfoAdapter;
import com.azy.locktools.entity.LockInfo;
import com.azy.locktools.entity.PasswordInfo;
import com.azy.locktools.utils.LockUtils;
import com.ttlock.bl.sdk.callback.CreateCustomPasscodeCallback;
import com.ttlock.bl.sdk.callback.DeletePasscodeCallback;
import com.ttlock.bl.sdk.callback.GetAllValidPasscodeCallback;
import com.ttlock.bl.sdk.entity.LockError;

public class PasswordActivity extends AppCompatActivity implements PasswordInfoAdapter.onListItemClick {

    private LockInfo lockInfo;

    private PasswordInfoAdapter passwordInfoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        passwordInfoAdapter = new PasswordInfoAdapter(this);
        passwordInfoAdapter.setOnListItemClick(this);
        mRecyclerView.setAdapter(passwordInfoAdapter);

        //读取传递参数
        String lockInfoStr = this.getIntent().getExtras().getString("lock");
        lockInfo = LockUtils.gson.fromJson(lockInfoStr, LockInfo.class);
        Log.d("lock----->", lockInfo.getName());
        loadPasswordInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("添加密码").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("请输入密码");//设置对话框 标题
        final EditText edit = new EditText(this);

        builder.setPositiveButton("确定",(dialogInterface, i) -> {
            addPassword(edit.getText().toString());
        });

        builder.setView(edit).setNegativeButton("取消",(dialogInterface, i) -> {

        });
        builder.create().show();
        return super.onOptionsItemSelected(item);
    }

    public void loadPasswordInfo() {
        GetAllValidPasscodeCallback callback = new GetAllValidPasscodeCallback() {
            @Override
            public void onGetAllValidPasscodeSuccess(String passcodeStr) {
                Log.d("-all password info ", passcodeStr);
                PasswordInfo[] passwordInfoList = LockUtils.gson.fromJson(passcodeStr, PasswordInfo[].class);
                passwordInfoAdapter.updateData(passwordInfoList);
                LockUtils.cancelProgressDialog();
                LockUtils.toast(getApplicationContext(), "获取密码信息成功");
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(PasswordActivity.this, error.getErrorMsg());
            }
        };
        LockUtils.getAllValidPasscodes(this, callback, lockInfo);
    }

    @Override
    public void onItemClick(PasswordInfo info) {
        Log.d("lock----->", lockInfo.getLockAlias());
        Log.d("password----->", info.getNewKeyboardPwd());
        LockUtils.dialog(this, "是否确定删除此密码？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delPassword(info.getNewKeyboardPwd());
            }
        });
    }

    public void delPassword(String password) {
        DeletePasscodeCallback callback = new DeletePasscodeCallback() {
            @Override
            public void onDeletePasscodeSuccess() {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(PasswordActivity.this, "删除密码成功");
                loadPasswordInfo();
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(PasswordActivity.this, error.getErrorMsg());
            }
        };
        LockUtils.deletePasscode(this, callback, lockInfo, password);
    }


    public void addPassword(String password){
        CreateCustomPasscodeCallback callback = new CreateCustomPasscodeCallback() {
            @Override
            public void onCreateCustomPasscodeSuccess(String s) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(PasswordActivity.this, "添加密码成功");
                loadPasswordInfo();
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(PasswordActivity.this, error.getErrorMsg());
            }
        };
        LockUtils.createCustomPasscode(this,callback,password,lockInfo);
    }


}
