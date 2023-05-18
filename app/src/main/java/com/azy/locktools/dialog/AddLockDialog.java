package com.azy.locktools.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.azy.locktools.R;


/**
 * Created by Administrator on 2016/9/5.
 */
public class AddLockDialog extends AlertDialog {
    private TextView tv_dialogTitle;
    private EditText name, lockKey;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private View view;
    private onLoginListener loginListener = null;

    //创建并初始化LoginDialog
    public AddLockDialog(Context context) {
        super(context);
        builder = new AlertDialog.Builder(context);
        //动态加载布局
        view = LayoutInflater.from(context).inflate(R.layout.add_lock, null);

        tv_dialogTitle = (TextView) view.findViewById(R.id.tv_dialogTitle);
        name = (EditText) view.findViewById(R.id.name);
        lockKey = (EditText) view.findViewById(R.id.lockKey);

        //设置登录按钮(btnLogin)的监听事件
        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginListener.onClick(v);
            }
        });

        builder.setView(view);
    }

    public EditText getName() {
        return name;
    }

    public EditText getLockKey() {
        return lockKey;
    }

    //设置LoginDialog的标题是否显示
    public void setShowTitle(boolean isShowTitle) {
        if (isShowTitle)
            tv_dialogTitle.setVisibility(TextView.VISIBLE);
        else
            tv_dialogTitle.setVisibility(TextView.INVISIBLE);
    }

    //显示LoginDialog
    public void show() {
        dialog = builder.create();
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    //自定义onLoginListener接口
    public interface onLoginListener {
        void onClick(View v);
    }

    //自定义setLoginListener
    public void setLoginListener(onLoginListener loginListener) {
        this.loginListener = loginListener;
    }

}