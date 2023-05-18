package com.azy.locktools.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.azy.locktools.R;
import com.azy.locktools.entity.LockInfo;
import com.azy.locktools.entity.PasswordInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PasswordInfoAdapter extends RecyclerView.Adapter<PasswordInfoAdapter.ContactViewHolder> {


    class ContactViewHolder extends RecyclerView.ViewHolder {
        //create the viewHolder class
        protected TextView vStart;
        protected TextView vEnd;
        protected TextView vTitle;
        protected PasswordInfo passwordInfo;

        public ContactViewHolder(View itemView) {
            super(itemView);
            vStart = itemView.findViewById(R.id.text_start);
            vEnd = itemView.findViewById(R.id.text_end);
            vTitle = itemView.findViewById(R.id.title);

            //点击卡片弹出操作列表
            itemView.setOnClickListener(view->{
                mListener.onItemClick(passwordInfo);
            });
        }
    }

    //MyAdapter的成员变量infoList, 这里被我们用作数据的来源
    private PasswordInfo[] infoList = new PasswordInfo[0];

    private onListItemClick mListener;

    private Activity activity;

    //MyAdapter的构造器
    public PasswordInfoAdapter(Activity activity) {
        this.activity = activity;
    }

    public interface onListItemClick{
        void onItemClick(PasswordInfo info);
    }

    public void setOnListItemClick(onListItemClick listItemClick){
        this.mListener = listItemClick;
    }

    public void updateData(PasswordInfo[] infoList){
        this.infoList= null;
        this.infoList = infoList;
        notifyDataSetChanged();
    }

    //重写3个抽象方法
    //onCreateViewHolder()方法 返回我们自定义的 ContactViewHolder对象
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.password_card_view, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        //contactInfoList中包含的都是ContactInfo类的对象
        //通过其get()方法可以获得其中的对象
        PasswordInfo pi = infoList[position];

        //将viewHolder中hold住的各个view与数据源进行绑定(bind)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.vTitle.setText("密码：" + pi.getKeyboardPwd());
        holder.vStart.setText(simpleDateFormat.format(new Date(pi.getStartDate())));
        holder.vEnd.setText(simpleDateFormat.format(new Date(pi.getEndDate())));
        holder.passwordInfo = pi;
    }

    //此方法返回列表项的数目
    @Override
    public int getItemCount() {
        return infoList.length;
    }
}
