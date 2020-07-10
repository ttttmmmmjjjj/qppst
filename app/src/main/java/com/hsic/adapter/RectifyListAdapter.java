package com.hsic.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.hsic.tmj.qppst.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/21.
 */

public class RectifyListAdapter extends BaseAdapter {
    Context context;
    List<Map<String, String>> data ;
    public RectifyListAdapter(Context context,List<Map<String, String>> data) {
        this.context = context;
        this.data=data;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = null;
        try{
            TextView RowID = null, UserName = null, HandPhone3 = null, Address = null, GoodsName = null,
                    userid=null,saleID=null,userCardID=null;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.sale_list,
                        null);

            } else {
                view = convertView;
            }
            if (view != null && data.size() > 0) {
                userCardID=(TextView) view.findViewById(R.id.userCardID);
                userCardID.setVisibility(View.VISIBLE);
                UserName = (TextView) view.findViewById(R.id.username);//用户姓名
                UserName.setText("");
                HandPhone3 = (TextView) view.findViewById(R.id.handPhone3);
                HandPhone3.setText("");
                Address = (TextView) view.findViewById(R.id.address);
                Address.setText("");
                GoodsName = (TextView) view.findViewById(R.id.goodsname);
                GoodsName.setText("");
                userid=(TextView) view.findViewById(R.id.userid);
                userid.setText("");
                saleID=(TextView) view.findViewById(R.id.saleID);
                saleID.setVisibility(View.GONE);
                int i=position+1;
                UserName.setText("姓名:"+data.get(position).get("UserName"));
                HandPhone3.setText("联系电话::"+data.get(position).get("Telephone"));//来电电话
                Address.setText("地址:"+data.get(position).get("Deliveraddress"));
                GoodsName.setVisibility(View.GONE);
                userid.setText(String.valueOf(i)+" "+"用户编号:"+data.get(position).get("UserID"));
                userCardID.setText("卡号:"+data.get(position).get("CustomerCardID"));

            }

        }catch(Exception ex){
            ex.toString();
            ex.printStackTrace();
        }
        return view;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
}
