package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hsic.bean.SaleDetail;
import com.hsic.bean.UserInfoDoorSale;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2020/6/16.
 */

public class GoodsPriceAdapter extends BaseAdapter {
    List<SaleDetail> SaleDetail_List;
    private Context context;
    private boolean isClick=false;;
    /**
     *
     * @param context
     * @param SaleDetail_List
     */
    public GoodsPriceAdapter(Context context, List<SaleDetail> SaleDetail_List){
        this.context=context;
        this.SaleDetail_List=SaleDetail_List;
        this.isClick=isClick;
    }

    @Override
    public int getCount() {
        if(SaleDetail_List.size()>0){
            return SaleDetail_List.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return SaleDetail_List.get(i);
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
            txt_goodsName.setText(SaleDetail_List.get(i).getQPName());
            txt_goodsPrice.setText(SaleDetail_List.get(i).getQPPrice());
        }else{
            convertView=view;
        }
        if(convertView!=null&&SaleDetail_List.size()>0){


        }
        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
}
