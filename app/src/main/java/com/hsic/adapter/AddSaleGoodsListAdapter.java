package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hsic.bean.Price_QPQT;
import com.hsic.bean.UserInfoDoorSale;
import com.hsic.tmj.qppst.PlaceOrderActivity;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2020/6/18.
 */

public class AddSaleGoodsListAdapter extends BaseAdapter {
    List<Price_QPQT> GoodsInfo_List;
    private Context context;
    private boolean isClick=false;
    UserInfoDoorSale userInfoDoorSale;
    PlaceOrderActivity.ShownDialogListener l;
    public AddSaleGoodsListAdapter(Context context, List<Price_QPQT> GoodsInfo_List){
        this.context=context;
        this.GoodsInfo_List=GoodsInfo_List;
        this.isClick=isClick;
        this.l=l;
    }
    @Override
    public int getCount() {
        if(GoodsInfo_List.size()>0){
            return GoodsInfo_List.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return GoodsInfo_List.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = null;
        TextView txt_goodsName=null,txt_goodsPrice=null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goods_price,
                    null);
            txt_goodsName=convertView.findViewById(R.id.txt_goodsName);
            txt_goodsPrice=convertView.findViewById(R.id.txt_goodsPrice);
            txt_goodsName.setText(GoodsInfo_List.get(i).getName());
            txt_goodsPrice.setText(GoodsInfo_List.get(i).getCount());
        }else{
            convertView=view;
        }
        if(convertView!=null && GoodsInfo_List.size()>0){


        }
        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
}
