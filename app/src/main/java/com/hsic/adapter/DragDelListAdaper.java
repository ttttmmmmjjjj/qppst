package com.hsic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hsic.sy.dragdellistview.DragDelItem;
import com.hsic.bean.UserReginCode;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 */

public class DragDelListAdaper extends BaseAdapter {
    List<UserReginCode> UserReginCode_List;
    private Context context;
    public DragDelListAdaper(Context context, List<UserReginCode> UserReginCode_List){
        this.context=context;
        this.UserReginCode_List=UserReginCode_List;
    }
    @Override
    public int getCount() {
        if(UserReginCode_List.size()>0){
            return UserReginCode_List.size();
        }
        return 0;

    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;
        View menuView=null;
        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.swipecontent, null);
            menuView = View.inflate(context,
                    R.layout.swipemenu, null);
            convertView = new DragDelItem(convertView,menuView);
            holder=new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(UserReginCode_List.get(position).getUserRegionCode()+"\b\b\b\b\b"+UserReginCode_List.get(position).getQpName()+"");
        holder.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //进行发车，作废，结束车次等操作
                String userRegionCode=UserReginCode_List.get(position).getUserRegionCode();
                int i=UserReginCode_List.size();
                for(int a=0;a<i;a++){
                    if(userRegionCode.equals(UserReginCode_List.get(a).getUserRegionCode())){
                        UserReginCode_List.remove(a);
                        notifyDataSetChanged();
                        break;
                    }
                }

            }
        });

        return convertView;
    }
    class ViewHolder {
        TextView tv_name;
        TextView tv_del;
        RelativeLayout relativeLayout;
        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_del=(TextView)view.findViewById(R.id.tv_del);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_layout);
            //改变relativeLayout宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            relativeLayout.setMinimumWidth(width-60);
            view.setTag(this);
        }
    }
}
