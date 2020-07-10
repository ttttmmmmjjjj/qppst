package com.hsic.tmj.qppst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.SaleListAdapter;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleAll;
import com.hsic.bean.SaleDetail;
import com.hsic.bean.UserXJInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchYYAssignSale;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.SearchYYAssignSaleTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/10/25.
 */

public class YOrderQueryActivity extends AppCompatActivity implements ImplSearchYYAssignSale,
        com.hsic.sy.dialoglibrary.OnItemClickListener,
        OnDismissListener {
    TextView stationName, operator;
    ListView listview;
    GetBasicInfo getBasicInfo;
    SaleListAdapter saleListAdapter;
    List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yyoder_layout);
        getBasicInfo = new GetBasicInfo(this);
        stationName = this.findViewById(R.id.txt_stationName);
        operator = this.findViewById(R.id.txt_operator);
        operator.setText(getBasicInfo.getOperationName());
        stationName.setText( getBasicInfo.getStationName());
        listview = (ListView) findViewById(R.id.lv_saleInfo);
        list = new ArrayList<Map<String, String>>();
        saleListAdapter = new SaleListAdapter(this, list);
        listview.setAdapter(saleListAdapter);
        new SearchYYAssignSaleTask(this,this).execute();
    }

    @Override
    public void SearchYYAssignSaleTaskEnd(HsicMessage tag) {
        List<SaleAll> list1 = new ArrayList<SaleAll>();
        if (tag.getRespCode() == 0) {
            //订单相关信息插入本地数据库
            list1 = JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<SaleAll>>() {
            }.getType());//数据来源
            if (list1.size() > 0) {
                //整理订单并插入数据库
                dealSale(list1);
            }
        } else {
            /**
             * 看本地是否还有订单
             */
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }

    private void dealSale(List<SaleAll> list1) {
        list = new ArrayList<Map<String, String>>();
        int size = list1.size();
        for (int i = 0; i < size; i++) {
            Map<String, String> map = new HashMap<String, String>();
            SaleAll saleAll = list1.get(i);
            Sale sale = saleAll.getSale();
            UserXJInfo userXJInfo = saleAll.getUserXJInfo();
            List<SaleDetail> sale_detail_info=new ArrayList<SaleDetail>();
            sale_detail_info = sale.getSale_detail_info();
            map.put("SaleID", sale.getSaleID());
            String SaleID = sale.getSaleID();//订单号
            map.put("UserID", userXJInfo.getUserid());
            map.put("CustomerName", userXJInfo.getUsername());
            map.put("Telephone", userXJInfo.getTelephone());
            map.put("Address", sale.getSaleAddress());
            map.put("CustomerCardID", userXJInfo.getCustomerCardID());
            map.put("CustomerType", userXJInfo.getCustomerTypeName());
            map.put("CallingTelephone", sale.getTelphone());
            map.put("UrgeGasInfoStatus", "");
            map.put("CreateTime", sale.getCreateTime());
            StringBuffer goodsInfo = new StringBuffer();
            for (SaleDetail saleDetail : sale_detail_info) {
                if (saleDetail.getSaleID().equals(SaleID)) {
                    goodsInfo.append(saleDetail.getQPName() + "/" + saleDetail.getPlanSendNum() + "，");

                }
            }
            String tmp = goodsInfo.toString();
            tmp = tmp.substring(0, tmp.length() - 1);
            map.put("goodsInfo", tmp);
            list.add(map);
        }
        saleListAdapter = new SaleListAdapter(this, list);
        listview.setAdapter(saleListAdapter);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        YOrderQueryActivity.this.finish();
    }
}
