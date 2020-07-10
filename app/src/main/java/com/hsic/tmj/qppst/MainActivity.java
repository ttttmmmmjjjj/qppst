package com.hsic.tmj.qppst;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.MenuGridAdapter;
import com.hsic.bean.CustomerTypeInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.SaleAll;
import com.hsic.bean.StreetInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeleteData;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplLoadCustomerTypeInfo;
import com.hsic.listener.ImplLoadStreetInfo;
import com.hsic.listener.ImplUpHistory;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.LoadCustomerTypeTask;
import com.hsic.task.LoadStreetInfoTask;
import com.hsic.task.UpLoadHistoryTask;
import com.hsic.tmj.gridview.MyGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements  com.hsic.sy.dialoglibrary.OnItemClickListener,
        OnDismissListener,ImplUpHistory,ImplLoadStreetInfo,ImplLoadCustomerTypeInfo {
    private MyGridView gridview;
    private TextView station, info;
    GetBasicInfo getBasicInfo;
    AlertView choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBasicInfo = new GetBasicInfo(MainActivity.this);
        innitView();
        choice = new AlertView("请选择用户类型", null, null, null,
                new String[]{"用户未发卡", "用户已发卡"},
                this, AlertView.Style.ActionSheet, this);
        /**
         * 删除历史数据
         */
        DeleteData deleteData = new DeleteData(this);
        deleteData.delete(getBasicInfo.getOperationID(), getBasicInfo.getStationID());

        DeliveryDB deliveryDB=new DeliveryDB(this);
        boolean isExist=false;
        isExist=deliveryDB.streetIsExist();
        if(!isExist){
            new LoadStreetInfoTask(this,this).execute();
        }
        isExist=false;
        isExist=deliveryDB.customerInfoIsExist();
        if(!isExist){
            new LoadCustomerTypeTask(this,this).execute();
        }
    }
    private void innitView() {
        gridview = (MyGridView) findViewById(R.id.gridview);
        String[] img_text = {"在线分单", "上门配送", "安全检查", "隐患整改","在线门售", "其他业务", "数据上传",
                "预约查询","用户开户","用户修改","信息下载",""};
        int[] imgs = {
                R.drawable.xd, R.drawable.cc,
                R.drawable.h, R.drawable.c,
                R.drawable.count,R.drawable.xd,
                R.drawable.setting,R.drawable.h,
                R.drawable.add_user,R.drawable.edit_page,
                R.drawable.download, R.drawable.bank
        };
        gridview.setAdapter(new MenuGridAdapter(this, img_text, imgs));
        info = (TextView) this.findViewById(R.id.info);
        info.setText(getBasicInfo.getOperationName());
        station = (TextView) this.findViewById(R.id.station);
        station.setText(getBasicInfo.getStationName());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch ((int) i) {
                    case 0://在线分单
                        if(getBasicInfo.getOperationType().equals("2")){
                            Intent d=new Intent(MainActivity.this,DistrubutionOrderActivity.class);
//                            d.putExtra("staffID",getBasicInfo.getOperationID());
                            startActivity(d);
                        }else{
                            shownDialog("无权限使用!");
                        }
                        break;
                    case 1://上门配送
                        Intent saleList = new Intent(MainActivity.this, SaleListActivity.class);
                        startActivity(saleList);//
                        break;
                    case 2://安全检查
                        //安检
                        if(checkNetworkState()){
//                            choice.show();
                           Intent aj = new Intent(MainActivity.this, UserLoginNoCardActivity.class);
                            aj.putExtra("LR", "2");//上门配送时用户登录
                            startActivity(aj);
                        }else{
                            new AlertView("提示", "安检只支持在线状态", null, new String[]{"确定"},
                                    null, MainActivity.this, AlertView.Style.Alert, MainActivity.this)
                                    .show();
                        }
                        break;
                    case 3://隐患整改
                        Intent rectifty = new Intent(MainActivity.this, RectiftyListActivity.class);
                        startActivity(rectifty);
                        break;
                    case 4://门售
                        Intent order = new Intent(MainActivity.this, PlaceOrderActivity.class);
                        startActivity(order);
                        break;
                    case 5://其他业务操作
                        Intent count = new Intent(MainActivity.this, OtherServiceActivity.class);
                        startActivity(count);
                        break;
                    case 6://数据上传
                        new UpLoadHistoryTask(MainActivity.this,MainActivity.this).execute();
                        break;
                    case 7://数据上传
                        Intent yy = new Intent(MainActivity.this, YOrderQueryActivity.class);
                        startActivity(yy);
                        break;
                    case 8://用户开户
                        Intent open = new Intent(MainActivity.this, OpenUserInfoActivity.class);
                        startActivity(open);
                        break;
                    case 9://用户修改
                        Intent aj2 = new Intent(MainActivity.this, UserLoginNoCardActivity.class);
                        aj2.putExtra("LR", "11");//用户修改用户登录
                        startActivity(aj2);
                        break;
                    case 10://信息下载
                        new LoadCustomerTypeTask(MainActivity.this,MainActivity.this).execute();
                        new LoadStreetInfoTask(MainActivity.this,MainActivity.this).execute();
                        break;
                    case 11://用户退瓶
                        Intent t = new Intent(MainActivity.this, UserLoginNoCardActivity.class);
                        t.putExtra("LR", "12");//用户修改用户登录
                        startActivity(t);


                }
            }
        });
    }
    // 是否退出程序
    private static Boolean isExit = false;
    // 定时触发器
    private static Timer tExit = null;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(choice.isShowing()){
                choice.dismiss();
                return true;
            }else{
                if (isExit == false) {
                    isExit = true;
                    if (tExit != null) {
                        tExit.cancel(); // 将原任务从队列中移除
                    }
                    // 重新实例一个定时器
                    tExit = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            isExit = false;
                        }
                    };
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    // 延时两秒触发task任务
                    tExit.schedule(task, 2000);
                } else {
                    finish();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if (o == choice) {
            Intent i;
            switch (position) {
                case 0:
                    //跳进
                    choice.dismiss();
                    i = new Intent(MainActivity.this, UserLoginNoCardActivity.class);
                    i.putExtra("LR", "2");//上门配送时用户登录
                    startActivity(i);
                    break;
                case 1:
                    i = new Intent(MainActivity.this, UserLoginByReadActivity.class);
                    i.putExtra("LR", "2");//上门配送时用户登录
                    startActivity(i);
                    choice.dismiss();
                    break;
            }
        }
    }

    private void shownDialog(String text) {
        new AlertView("提示", text, null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this)
                .show();

    }
    /**
     * 检测网络是否连接
     *
     * @return
     */
    private boolean checkNetworkState() {
        boolean flag = false;
        // 得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    @Override
    public void UpLoadHistoryTaskEnd(HsicMessage tag) {

    }

    @Override
    public void LoadStreetInfoTaskEnd(HsicMessage tag) {
        List<StreetInfo> list1=new ArrayList<>();
        if(tag.getRespCode()==0){
            list1= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<StreetInfo>>() {
            }.getType());//数据来源
            if(list1.size()>0){
                //整理订单并插入数据库
                DeliveryDB deliveryDB=new DeliveryDB(this);
                deliveryDB.deleteJD();
                deliveryDB.InsertStreetInfo(list1);
            }
        }else{
            Toast.makeText(this, "街道信息同步失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void LoadCustomerTypeInfoTaskEnd(HsicMessage tag) {
        List<CustomerTypeInfo> list1=new ArrayList<>();
        if(tag.getRespCode()==0){
            list1= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<CustomerTypeInfo>>() {
            }.getType());//数据来源
            if(list1.size()>0){
                //整理订单并插入数据库
                DeliveryDB deliveryDB=new DeliveryDB(this);
                deliveryDB.deleteUserType();
                deliveryDB.InsertCustomTypeInfo(list1);
            }
        }else{
            Toast.makeText(this, "用户类型信息同步失败", Toast.LENGTH_SHORT).show();
        }
    }
}
