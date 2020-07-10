package com.hsic.tmj.qppst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hsic.adapter.RectifyListAdapter;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.RectifyDB;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dialoglibrary.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RectifyDeleteActivity extends AppCompatActivity implements OnDismissListener, OnItemClickListener {
    RectifyDB rectifyDB;
    TextView txt_title;
    GetBasicInfo getBasicInfo;
    List<Map<String,String>> finish;
    ListView listView;
    RectifyListAdapter rectifyListAdapter;
    AlertView IsSunmitSale;
    String ID,userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectify_delete);
        rectifyDB=new RectifyDB(this);
        getBasicInfo=new GetBasicInfo(this);
        ID="";userID="";
        txt_title=this.findViewById(R.id.txt_title);
        listView=this.findViewById(R.id.lv_saleInfo);
        txt_title.setText("删除整改单");
        IsSunmitSale=new AlertView("提示", "确认删除该订单", "取消", new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);//
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, String> data =  (Map<String, String>)adapterView.getItemAtPosition(i);
                ID=data.get("ID");
                userID=data.get("UserID");
            }
        });
        finish=new ArrayList<>();
        finish=rectifyDB.GetRectifyInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        rectifyListAdapter=new RectifyListAdapter(this,finish);
        listView.setAdapter(rectifyListAdapter);
    }

    @Override
    public void onDismiss(Object o) {
        if (o == IsSunmitSale) {
            RectifyDeleteActivity.this.finish();
        }
    }

    @Override
    public void onItemClick(Object o, int position) {
        if (o == IsSunmitSale) {
            if(position==0){
                //确认删除该订单,删除成功以后删除本地数据
                boolean ret=false;
                ret= rectifyDB.deleteByID(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),ID,userID);
                if(ret){
                    finish=new ArrayList<>();
                    finish=rectifyDB.GetRectifyInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID());
                    rectifyListAdapter=new RectifyListAdapter(this,finish);
                    listView.setAdapter(rectifyListAdapter);
                }
            }
        }else{
            RectifyDeleteActivity.this.finish();
        }
    }
}
