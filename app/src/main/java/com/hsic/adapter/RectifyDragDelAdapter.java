package com.hsic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hsic.sy.dragdellistview.DragDelItem;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.RectifyDB;
import com.hsic.tmj.qppst.R;
import com.hsic.tmj.qppst.RectiftyActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/20.
 */

public class RectifyDragDelAdapter extends BaseAdapter  {
    Context context;
    List<Map<String, String>> data ;
    RectifyDB rectifyDB;
    GetBasicInfo getBasicInfo;
    public RectifyDragDelAdapter(Context context,List<Map<String, String>> data){
        this.context=context;
        this.data=data;
        rectifyDB=new RectifyDB(context);
        getBasicInfo=new GetBasicInfo(context);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(data.size()>0){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       ViewHolder holder=null;
        View menuView=null;
        if (convertView == null) {
            convertView = View.inflate(context,
                    R.layout.item_rectify_list, null);
            menuView = View.inflate(context,
                    R.layout.swipemenu, null);
            convertView = new DragDelItem(convertView,menuView);
            holder=new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final  String  UserID=data.get(position).get("UserID");

        int i=position+1;
        holder.tv_rowID.setText("序号："+String.valueOf(i));
        holder.tv_userID.setText("用户编号："+data.get(position).get("UserID"));

        holder.tv_telephone.setText("联系电话："+data.get(position).get("Telephone"));
        holder.tv_address.setText("地址："+data.get(position).get("Deliveraddress"));
        holder.tv_customCardID.setText("卡号："+data.get(position).get("CustomerCardID"));
        holder.tv_userName.setText("用户姓名："+data.get(position).get("UserName"));
        holder.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //删除该条数据
                String ID= data.get(position).get("ID");

            }
        });
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent d=new Intent(context,RectiftyActivity.class);
                d.putExtra("UserID",UserID);
                context.startActivity(d);
            }
        });
        return convertView;
    }
    class ViewHolder {
        TextView tv_rowID ,tv_userID,tv_userName,tv_phone,tv_telephone,tv_address,tv_customCardID;
        TextView tv_del;
        LinearLayout relativeLayout;
        public ViewHolder(View view) {
            tv_rowID = (TextView) view.findViewById(R.id.rowID);
            tv_userID = (TextView) view.findViewById(R.id.userID);
            tv_userName = (TextView) view.findViewById(R.id.userName);
            tv_phone = (TextView) view.findViewById(R.id.phone);
            tv_telephone = (TextView) view.findViewById(R.id.telephone);
            tv_address = (TextView) view.findViewById(R.id.address);
            tv_customCardID = (TextView) view.findViewById(R.id.customCardID);
            tv_del=(TextView)view.findViewById(R.id.tv_del);
            relativeLayout = (LinearLayout) view.findViewById(R.id.rl_layout);
            //改变relativeLayout宽度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            relativeLayout.setMinimumWidth(width-60);
            view.setTag(this);
        }
    }
}
