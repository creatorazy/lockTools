package com.azy.locktools.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.azy.locktools.R;
import com.azy.locktools.activity.PasswordActivity;
import com.azy.locktools.utils.LockUtils;
import com.azy.locktools.entity.LockInfo;

import java.util.ArrayList;
import java.util.List;

public class LockInfoAdapter extends RecyclerView.Adapter<LockInfoAdapter.ContactViewHolder> {

    //MyAdapter的成员变量infoList, 这里被我们用作数据的来源
    private List<LockInfo> infoList = new ArrayList<>();

    private Activity activity;

    onListItemClick mListener;

    //MyAdapter的构造器
    public LockInfoAdapter(Activity activity) {
        this.activity = activity;
    }

    public interface onListItemClick{
        void onItemClick(LockInfo info);
    }

    public void setOnListItemClick(onListItemClick listItemClick){
        this.mListener = listItemClick;
    }

    //重写3个抽象方法
    //onCreateViewHolder()方法 返回我们自定义的 ContactViewHolder对象
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.lock_card_view, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        //infoList中包含的都是ContactInfo类的对象
        //通过其get()方法可以获得其中的对象
        LockInfo li = infoList.get(position);
        holder.info = li;

        //将viewHolder中hold住的各个view与数据源进行绑定(bind)
        holder.vTitle.setText(li.getName());
//        holder.vName.setText(li.getDoorBluetoothName());
//        holder.vMac.setText(li.getDoorBluetoothMac());
    }

    //此方法返回列表项的数目
    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void updateData(List<LockInfo> list){
        infoList.clear();
        infoList.addAll(list);
        notifyDataSetChanged();
    }


    class ContactViewHolder extends RecyclerView.ViewHolder {
        //create the viewHolder class
        protected TextView vName;
        protected TextView vMac;
        protected TextView vTitle;
        protected LockInfo info;

        public ContactViewHolder(View itemView) {
            super(itemView);
//            vName = itemView.findViewById(R.id.text_name);
//            vMac = itemView.findViewById(R.id.text_mac);
            vTitle = itemView.findViewById(R.id.title);

            //点击卡片弹出操作列表
            itemView.setOnClickListener(view -> {
                mListener.onItemClick(info);
            });
        }
    }
}
