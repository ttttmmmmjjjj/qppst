package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hsic.bean.CustomerInfo;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2019/4/11.
 */

public class CustomerInfoAdapter extends BaseAdapter {
    Context context;
    List<CustomerInfo> data ;
    String tag;
    public CustomerInfoAdapter(Context context, List<CustomerInfo> data, String tag) {
        this.context = context;
        this.data=data;
        this.tag=tag;
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
            TextView  UserName = null, HandPhone3 = null, Address = null, GoodsName = null,
                    userid=null,saleID=null;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.sale_list,
                        null);

            } else {
                view = convertView;
            }
            if (view != null && data.size() > 0) {
                UserName = (TextView) view.findViewById(R.id.username);//用户姓名
                UserName.setText("");
                HandPhone3 = (TextView) view.findViewById(R.id.handPhone3);
                HandPhone3.setText("");
                Address = (TextView) view.findViewById(R.id.address);
                Address.setText("");
                GoodsName = (TextView) view.findViewById(R.id.goodsname);
                GoodsName.setText("");
                GoodsName.setVisibility(View.GONE);
                userid=(TextView) view.findViewById(R.id.userid);
                userid.setText("");
                saleID=(TextView) view.findViewById(R.id.saleID);
                saleID.setText("");
                int i=position+1;
                UserName.setText("用户姓名:"+data.get(position).getCustomerName());
                HandPhone3.setText("联系电话:"+data.get(position).getTelphone());//来电电话
                Address.setText("地址:"+data.get(position).getCustomerAddress());
                GoodsName.setText("用户类型:"+data.get(position).getCustomerTypeName());//商品
                if(tag.equals("s")){
                    userid.setText(String.valueOf(i)+" "+"送气编号:"+data.get(position).getCustomerID());
                }else{
                    userid.setText(String.valueOf(i)+" "+"用户编号:"+data.get(position).getCustomerID());
                }
                saleID.setText("卡号:"+data.get(position).getCustomerCardID());

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
