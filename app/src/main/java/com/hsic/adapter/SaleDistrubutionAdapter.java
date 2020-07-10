package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.hsic.bean.SaleAll;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2019/1/9.
 */

public class SaleDistrubutionAdapter extends BaseAdapter {
    Context context;
    List<SaleAll> data;
    public SaleDistrubutionAdapter(Context context, List<SaleAll> data){
        this.context=context;
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


    final static class ViewHolder {
        TextView tvLetter;
        CheckBox isChecked;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;

        if (data != null && data.size() > 0) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_sale_distrubution, null);
//            TextView size= (TextView) convertView.findViewById(R.id.item_size);
            TextView tvSaleID= (TextView) convertView.findViewById(R.id.saleID);
            TextView tvUserName= (TextView) convertView.findViewById(R.id.userName);
            TextView tvAddress= (TextView) convertView.findViewById(R.id.address);
            TextView tvPhone= (TextView) convertView.findViewById(R.id.phone);
            CheckBox isChecked= (CheckBox) convertView.findViewById(R.id.item_checkBox);
            int id=data.size()-position;
//            size.setText(""+id);
//            size.setVisibility(View.GONE);
            tvSaleID.setText("创建时间："+data.get(position).getSale().getCreateTime());
            tvUserName.setText("姓名："+data.get(position).getCustomerInfo().getCustomerName());
            String  address=data.get(position).getSale().getSaleAddress();
            if(address!=null){
                tvAddress.setText("地址："+data.get(position).getSale().getSaleAddress());
            }else{
                address=data.get(position).getCustomerInfo().getCustomerAddress();
                if(address==null){
                    address="";
                }
                tvAddress.setText("联系地址："+address);
            }
            String temp=data.get(position).getSale().getTelphone();
            isChecked.setChecked(data.get(position).isUpdate);
            if(temp==null){
                temp="";
            }
            if(temp.equals("")){
                tvPhone.setText("电话："+data.get(position).getCustomerInfo().getTelphone());
            }else{
                tvPhone.setText("电话："+data.get(position).getSale().getTelphone());
            }
            return convertView;
        }else{
            return null;
        }

    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
}
