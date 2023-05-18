package com.azy.locktools.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azy.locktools.R;
import com.azy.locktools.adapter.ICCardInfoAdapter;
import com.azy.locktools.entity.ICCardInfo;
import com.azy.locktools.entity.LockInfo;
import com.azy.locktools.utils.LockUtils;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.AddICCardCallback;
import com.ttlock.bl.sdk.callback.DeleteICCardCallback;
import com.ttlock.bl.sdk.callback.GetAllValidICCardCallback;
import com.ttlock.bl.sdk.entity.LockError;

public class ICCardActivity extends AppCompatActivity implements ICCardInfoAdapter.onListItemClick {

    private LockInfo lockInfo;

    private ICCardInfoAdapter icCardInfoAdapter;

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
        icCardInfoAdapter = new ICCardInfoAdapter(this);
        icCardInfoAdapter.setOnListItemClick(this);
        mRecyclerView.setAdapter(icCardInfoAdapter);

        //读取传递参数
        String lockInfoStr = this.getIntent().getExtras().getString("lock");
        lockInfo = LockUtils.gson.fromJson(lockInfoStr, LockInfo.class);
        Log.d("lock----->", lockInfo.getLockAlias());
        loadICCardInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("添加房卡").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LockUtils.dialog(ICCardActivity.this,"确认要添加房卡吗？",(dialogInterface, i) -> {
            addICCard();
        });
        return super.onOptionsItemSelected(item);
    }

    public void loadICCardInfo() {
        GetAllValidICCardCallback callback = new GetAllValidICCardCallback() {
            @Override
            public void onGetAllValidICCardSuccess(String cardDataStr) {
                Log.d("-all ic cards info ", cardDataStr);
                ICCardInfo[] infoList = LockUtils.gson.fromJson(cardDataStr, ICCardInfo[].class);
                icCardInfoAdapter.updateData(infoList);
                LockUtils.cancelProgressDialog();
                LockUtils.toast(getApplicationContext(), "获取房卡信息成功");
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(ICCardActivity.this, error.getErrorMsg());
            }
        };
        LockUtils.getAllCards(this, callback, lockInfo);
    }

    @Override
    public void onItemClick(ICCardInfo icCardInfo) {
        Log.d("lock----->", lockInfo.getLockAlias());
        Log.d("cardNumber----->", icCardInfo.getCardNumber());
        LockUtils.dialog(ICCardActivity.this,"是否确定删除此房卡",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delICCard(icCardInfo.getCardNumber());
            }
        });
    }


    public void delICCard(String cardNumber) {
        DeleteICCardCallback callback = new DeleteICCardCallback() {
            @Override
            public void onDeleteICCardSuccess() {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(ICCardActivity.this, "删除房卡成功");
                loadICCardInfo();
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(ICCardActivity.this, error.getErrorMsg());
            }
        };
        LockUtils.deleteCard(this, callback, lockInfo, cardNumber);
    }

    public void addICCard() {
        LockUtils.ensureBluetoothIsEnabled(this);
        LockUtils.showConnectLockLoading(this);
        long addStartDate = 0;
        long addEndDate = 0;
        TTLockClient.getDefault().addICCard(addStartDate, addEndDate, lockInfo.getDoorBluetoothKey(), lockInfo.getDoorBluetoothMac(), new AddICCardCallback() {
            @Override
            public void onEnterAddMode() {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(ICCardActivity.this,"请把房卡放在门上");
            }

            @Override
            public void onAddICCardSuccess(long cardNum) {
                LockUtils.dialogTip(ICCardActivity.this, "房卡写入成功");
                loadICCardInfo();
            }

            @Override
            public void onFail(LockError error) {
                LockUtils.cancelProgressDialog();
                LockUtils.dialogTip(ICCardActivity.this, error.getErrorMsg());
            }
        });
    }
}
