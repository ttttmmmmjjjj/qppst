package com.hsic.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hsic.tmj.qppst.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/27.
 */

public class SaleListAdapter extends BaseAdapter {
    Context context;
    List<Map<String, String>> data ;
    public SaleListAdapter(Context context,List<Map<String, String>> data) {
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
            TextView  UserName = null, HandPhone3 = null, TelePhone = null, Address = null, GoodsName = null,
                    userid=null,saleID=null,userCardID=null,
                    createTime=null;
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
                saleID.setText("");
                createTime=(TextView) view.findViewById(R.id.createTime);
                createTime.setVisibility(View.VISIBLE);
                int i=position+1;
                String urge=data.get(position).get("UrgeGasInfoStatus");
                String RowID="";
                RowID=String.valueOf(i);
                saleID.setText(RowID+" "+"订单编号:"+data.get(position).get("SaleID"));
                userid.setText("送气编号:"+data.get(position).get("UserID"));
                userid.setVisibility(View.GONE);
                if(urge==null){
                    saleID.setText(RowID+" "+"订单编号:"+data.get(position).get("SaleID"));

                }else{
                    if(urge.equals("0")){
                        Spanned s= Html.fromHtml(RowID+" "+"<font size=32 color=#ff0000>" + "正在催单" + "</font>"+" "+"订单编号:"+
                                data.get(position).get("SaleID"));
                        saleID.setText(s);
                    }else{
                        saleID.setText(RowID+" "+"订单编号:"+data.get(position).get("SaleID"));
                    }

                }
                UserName.setText("姓名:"+data.get(position).get("CustomerName"));
                String CallingTelephone=data.get(position).get("CallingTelephone");
                if (CallingTelephone!=null){
                    if(!CallingTelephone.equals("")){
                        HandPhone3.setText("联系/来电 电话:"+data.get(position).get("Telephone")+"/"+data.get(position).get("CallingTelephone"));//联系电话
                    }else{
                        HandPhone3.setText("联系电话:"+data.get(position).get("Telephone"));//联系电话
                    }
                }else{
                    HandPhone3.setText("联系电话:"+data.get(position).get("Telephone"));//联系电话
                }

                Address.setText("地址 :"+data.get(position).get("Address"));
                GoodsName.setText("商品:"+data.get(position).get("goodsInfo"));//商品
                userCardID.setText("卡号:"+data.get(position).get("CustomerCardID"));
                String CreateTime=data.get(position).get("CreateTime");
                if (CreateTime.contains(".")){
                    String[] t=CreateTime.split("\\.");
                    createTime.setText("生成时间:"+t[0]);
                }else{
                    createTime.setText("生成时间:"+CreateTime);
                }

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
