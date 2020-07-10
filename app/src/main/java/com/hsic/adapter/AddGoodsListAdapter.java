package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hsic.bean.UserInfoDoorSale;
import com.hsic.tmj.qppst.PlaceOrderActivity;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2018/8/13.
 */

public class AddGoodsListAdapter extends BaseAdapter {
    List<UserInfoDoorSale.GoodsInfo> GoodsInfo_List;
    private Context context;
    private boolean isClick=false;
    UserInfoDoorSale userInfoDoorSale;
    PlaceOrderActivity.ShownDialogListener l;
    public AddGoodsListAdapter(Context context, List<UserInfoDoorSale.GoodsInfo> GoodsInfo_List,
                               boolean isClick, PlaceOrderActivity.ShownDialogListener l){
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
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view= LayoutInflater.from(context).inflate(R.layout.item_goods_info_layout,
                    null);
            hd.txt_ID = (TextView) view.findViewById(R.id.txt_ID);
            hd.checkBox = (CheckBox) view.findViewById(R.id.cb_isChecked);
            hd.txt_GoodsName = (TextView) view.findViewById(R.id.txt_GoodsName);// 灶眼类型
            hd.txt_GoodsCount = (TextView) view.findViewById(R.id.txt_GoodsCount);// 数量
            view.setTag(hd);
        }

        userInfoDoorSale=new UserInfoDoorSale();
       final  UserInfoDoorSale.GoodsInfo goodsInfo=GoodsInfo_List.get(i);
        int id=GoodsInfo_List.size()-i;
        hd = (ViewHoder) view.getTag();
        hd.txt_ID .setText(String.valueOf(id));
//        hd.txt_ID.setVisibility(View.GONE);
        hd.txt_GoodsCount .setText(goodsInfo.getGoodsCount());
        hd.txt_GoodsName.setText(goodsInfo.getGoodsName()+"\t\t"+goodsInfo.getGoodsCount()+"个"+"  "+goodsInfo.getUnitPrice()+"元/个");
        hd.checkBox.setChecked(goodsInfo.isCheck());
        hd.checkBox.setVisibility(View.GONE);
        final ViewHoder hdFinal = hd;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    l.onCheckedChanged(false,goodsInfo.getGoodsName(),goodsInfo.getGoodsCount(),goodsInfo.getGoodsCode());
                    checkBox.setChecked(false);
                    GoodsInfo_List.get(i).setCheck(false);
                } else {
                    checkBox.setChecked(true);
                    l.onCheckedChanged(true,goodsInfo.getGoodsName(),goodsInfo.getGoodsCount(),goodsInfo.getGoodsCode());
                    GoodsInfo_List.get(i).setCheck(true);
                }
            }
        });
        return view;
    }
    final static class ViewHoder {
        TextView txt_ID=null,txt_GoodsName=null,txt_GoodsCount=null;
        CheckBox checkBox=null;

    }
}
