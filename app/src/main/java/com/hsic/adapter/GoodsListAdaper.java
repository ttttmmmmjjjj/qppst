package com.hsic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hsic.bean.UserInfoDoorSale;
import com.hsic.tmj.qppst.R;

import java.util.List;

/**
 * Created by Administrator on 2018/8/9.
 */

public class GoodsListAdaper extends BaseAdapter {
    List<UserInfoDoorSale> GoodsInfo_List;
    private Context context;
    private boolean isClick=false;
    public static interface IOnItemSelectListener{
        public void onItemClick(int pos);
    };
    /**
     *
     * @param context
     * @param GoodsInfo_List
     */
    public GoodsListAdaper(Context context, List<UserInfoDoorSale> GoodsInfo_List){
        this.context=context;
        this.GoodsInfo_List=GoodsInfo_List;
        this.isClick=isClick;
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
        TextView txt_saleID=null,txt_userID=null,txt_userName=null,txt_userAddress,txt_phoneNum;
        CheckBox checkBox=null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.sale_item_layout,
                    null);
        }else{
            convertView=view;
        }
        if(convertView!=null&&GoodsInfo_List.size()>0){
            txt_saleID=convertView.findViewById(R.id.txt_saleID);
            txt_userID=convertView.findViewById(R.id.txt_userID);
            txt_userName=convertView.findViewById(R.id.txt_userName);
            txt_userAddress=convertView.findViewById(R.id.txt_userAddress);
            txt_phoneNum=convertView.findViewById(R.id.txt_phoneNum);

            txt_saleID.setText("订单号:"+GoodsInfo_List.get(i).getSaleID());
            txt_userID.setText("用户号:"+GoodsInfo_List.get(i).getUserID());
            txt_userName.setText("用户姓名:"+GoodsInfo_List.get(i).getUserName());
            txt_userAddress.setText("用户地址:"+GoodsInfo_List.get(i).getAddress());
            txt_phoneNum.setText("联系电话:"+GoodsInfo_List.get(i).getPhoneNumber());
//            if(GoodsInfo_List.get(i).getSaleStatus().equals("3")){
//                convertView.setBackgroundColor(Color.RED);
//            }

        }
        return convertView;
    }
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        super.notifyDataSetChanged();
    }
}
