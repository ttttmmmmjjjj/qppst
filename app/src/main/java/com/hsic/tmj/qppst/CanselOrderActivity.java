package com.hsic.tmj.qppst;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dialoglibrary.OnItemClickListener;
import com.hsic.adapter.GoodsListAdaper;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserInfoDoorSale;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.dialog.SaleInfoDialogByCansel;
import com.hsic.listener.ImplCancelOrderTask;
import com.hsic.listener.ImplGetAllSaleByOperatorTask;
import com.hsic.listener.ImplGetOrderBySaleIdTask;
import com.hsic.task.GetAllSaleByOperatorTask;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class CanselOrderActivity extends AppCompatActivity implements OnDismissListener, OnItemClickListener,
        ImplGetAllSaleByOperatorTask,
        ImplCancelOrderTask,ImplGetOrderBySaleIdTask {
    ListView listView;
    UserInfoDoorSale userInfoDoorSale;
    List<UserInfoDoorSale.GoodsInfo> goodsInfo_List;//下载到的商品信息
    private GoodsListAdaper adapter;
    private String deviceID;
    GetBasicInfo basicInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cansel_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        basicInfo=new GetBasicInfo(CanselOrderActivity.this);
//        toolbar.inflateMenu(R.menu.zhihu_toolbar_menu);
//        toolbar.setNavigationIcon(R.mipmap.ic_drawer_home);
        toolbar.setTitle(basicInfo.getStationName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        listView=this.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2=new ArrayList<UserInfoDoorSale.GoodsInfo>();
                userInfoDoorSale=(UserInfoDoorSale)adapterView.getItemAtPosition(position);;
                String saleid=userInfoDoorSale.getSaleID();
                userInfoDoorSale=new UserInfoDoorSale();
                userInfoDoorSale.setSaleID(saleid);
                String requestStr= JSONUtils.toJsonWithGson(userInfoDoorSale);
//                new GetOrderBySaleIdTask(CanselOrderActivity.this,CanselOrderActivity.this,deviceID,requestStr).execute();
            }
        });
        deviceID=basicInfo.getDeviceID();
        /**
         *1:下单[已经生成订单] ，2:提货[订单已经完成]
         */
        new GetAllSaleByOperatorTask(CanselOrderActivity.this,CanselOrderActivity.this,
                deviceID,basicInfo.getDeviceID(),"1").execute();
    }
    List<UserInfoDoorSale> UserInfo_LIST;
    @Override
    public void GetAllSaleByOperatorTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            Type typeOfT = new TypeToken<List<UserInfoDoorSale>>() {
            }.getType();
            UserInfo_LIST= new ArrayList<UserInfoDoorSale>();
            UserInfo_LIST = JSONUtils.toListWithGson(tag.getRespMsg(), typeOfT);
            listView.setAdapter( new GoodsListAdaper(CanselOrderActivity.this,UserInfo_LIST));;
        }else{
            shownDialog(tag.getRespMsg());
//            Toast.makeText(this, tag.getRespMsg(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void CancelOrderTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            shownDialog("订单作废成功");
//            Toast.makeText(this, "订单作废成功", Toast.LENGTH_SHORT).show();
            //改变该订单颜色 userInfoDoorSale=new UserInfoDoorSale();
            List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2=new ArrayList<UserInfoDoorSale.GoodsInfo>();
            userInfoDoorSale= JSONUtils.toObjectWithGson(tag.getRespMsg(),UserInfoDoorSale.class);
            String saleID=userInfoDoorSale.getSaleID();
            int size=UserInfo_LIST.size();
            for(int i=0;i<size;i++){
                if(UserInfo_LIST.get(i).getSaleID().equals(saleID)){
                    UserInfo_LIST.remove(i);
                    break;
                }
            }
            UserInfo_LIST.add(userInfoDoorSale);
            listView.setAdapter( new GoodsListAdaper(CanselOrderActivity.this,UserInfo_LIST));;
        }else{
            shownDialog(tag.getRespMsg());
        }
    }
    // 是否退出程序
    private static Boolean isExit = false;
    // 定时触发器
    private static Timer tExit = null;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (isExit == false) {
//                isExit = true;
//                if (tExit != null) {
//                    tExit.cancel(); // 将原任务从队列中移除
//                }
//                // 重新实例一个定时器
//                tExit = new Timer();
//                TimerTask task = new TimerTask() {
//                    @Override
//                    public void run() {
//                        isExit = false;
//                    }
//                };
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                // 延时两秒触发task任务
//                tExit.schedule(task, 2000);
//            } else {
//                finish();
//                System.exit(0);
//            }
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        this.finish();
    }
    private void shownDialog(String msg){
        new AlertView("提示", msg, null, new String[] { "确定" },
                null,this, AlertView.Style.Alert, this)
                .show();
    }

    @Override
    public void GetOrderBySaleIdTaskEnd(HsicMessage tag) {
        try{
            if(tag.getRespCode()==0){
                List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2=new ArrayList<UserInfoDoorSale.GoodsInfo>();
                userInfoDoorSale= JSONUtils.toObjectWithGson(tag.getRespMsg(),UserInfoDoorSale.class);
                goodsInfo_List2=userInfoDoorSale.getGoodsInfo();
                int size=goodsInfo_List2.size();
                StringBuffer msg=new StringBuffer();
                msg.append("\n\n");
                msg.append(PrintUtils.printTwoData("用户站点:",userInfoDoorSale.getStationName()+"\n"));
                msg.append(PrintUtils.printTwoData("订单编号:",userInfoDoorSale.getSaleID()+"\n"));
                msg.append(PrintUtils.printTwoData("用户编号:",userInfoDoorSale.getUserID()+"\n"));
                msg.append(PrintUtils.printTwoData("用户姓名:",userInfoDoorSale.getUserName()+"\n"));
                msg.append(PrintUtils.printTwoData("电话:",userInfoDoorSale.getPhoneNumber()+"\n"));
                msg.append("用户地址:"+userInfoDoorSale.getAddress()+"\n");
                msg.append(("---------------------------------------------------------------\n")) ;
                msg.append("商品详情:"+"\n");
                for(int i=0;i<size;i++){
                    msg.append(PrintUtils.printTwoData(goodsInfo_List2.get(i).getGoodsName(),goodsInfo_List2.get(i).getGoodsCount()+"\n"));
                }
                msg.append(("---------------------------------------------------------------\n")) ;
                msg.append("归还空瓶:"+userInfoDoorSale.getEmptyNO()+"\n");
                msg.append(("---------------------------------------------------------------\n")) ;
                msg.append(PrintUtils.printTwoData("总价:",userInfoDoorSale.getTotalPrice()+"\n"));
                msg.append(PrintUtils.printTwoData("完成时间:",userInfoDoorSale.getOperationTime()+"\n"));
                msg.append(PrintUtils.printTwoData("操作人:",basicInfo.getOperationName()+"\n"));
                msg.append(PrintUtils.printTwoData("操作站点:",basicInfo.getStationName()+"\n"));
                SaleInfoDialogByCansel s=new SaleInfoDialogByCansel(CanselOrderActivity.this,CanselOrderActivity.this,msg.toString(),userInfoDoorSale);
                s.shown();
            }else{
                shownDialog(tag.getRespMsg());
            }
        }catch(Exception ex){

        }
    }
}
