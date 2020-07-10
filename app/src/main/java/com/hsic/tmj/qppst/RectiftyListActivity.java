package com.hsic.tmj.qppst;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.RectifyDragDelAdapter;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.UserRectifyInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.RectifyDB;
import com.hsic.listener.ImplDownloadRectifyInfo;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dragdellistview.DragDelListView;
import com.hsic.task.DownloadRectifyInfoTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RectiftyListActivity extends AppCompatActivity implements ImplDownloadRectifyInfo,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    GetBasicInfo getBasicInfo;
    TextView stationName, operator,taskCounts,finishCounts,unfinish,upload;
    ListView saleListInfo;//订单List列表
    Button delivery;//上门配送
    RectifyDB rectifyDB;
    String EmployeeID,StationID;
    DragDelListView listview;
    List<Map<String, String>> list;//未完成
    List<Sale> finishList;//已完成
    List<Sale> allList;//本地所有订单
    List<Sale> loadList;//已上传
    RectifyDragDelAdapter rectifyListAdapter;
    AlertView choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectify_list);
        getBasicInfo = new GetBasicInfo(this);
        rectifyDB=new RectifyDB(this);
        innitView();
        choice=new AlertView("请选择用户类型", null, null, null,
                new String[]{"用户未发卡", "用户已发卡"},
                this, AlertView.Style.ActionSheet, this);
        /***
         * 加载该配送员底下的可配送订单信息
         */
        EmployeeID=getBasicInfo.getOperationID();//登录员工号
        StationID=getBasicInfo.getStationID();
        operator.setText(getBasicInfo.getOperationName());
        stationName.setText(getBasicInfo.getStationName());
        taskCounts.setText("总任务:0");
        finishCounts.setText("已整改:0");
        unfinish.setText("未整改:0");
        upload.setText("已上传:0");

        if(checkNetworkState()){
            new DownloadRectifyInfoTask(this, this).execute(EmployeeID);
        }else{
            setUIData("无可做的任务");
        }
    }
    private void innitView() {
        listview = (DragDelListView) findViewById(R.id.lv_saleInfo);
        stationName = this.findViewById(R.id.txt_stationName);
        operator = this.findViewById(R.id.txt_operator);
        taskCounts=this.findViewById(R.id.txt_taskCount);
        taskCounts.setText("");
        finishCounts=this.findViewById(R.id.txt_finishCount);
        finishCounts.setText("");
        unfinish=this.findViewById(R.id.txt_unFinish);
        unfinish.setText("");
        upload=this.findViewById(R.id.txt_upload);
        upload.setText("");
        saleListInfo = this.findViewById(R.id.lv_saleInfo);
        delivery = this.findViewById(R.id.btn_delivery);
        delivery.setText("整改");
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                choice.show();
                Intent i;
                i=new Intent(RectiftyListActivity.this,UserLoginNoCardActivity.class);
                i.putExtra("LR","3");//上门配送时用户登录
                startActivity(i);
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(list.size()>0){
                    Intent d=new Intent(RectiftyListActivity.this,RectiftyActivity.class);
                    Map<String, String> data =  (Map<String, String>)adapterView.getItemAtPosition(position);
                    String UserID=data.get("UserID");
                    d.putExtra("UserID",UserID);
                    d.putExtra("TypeClass",data.get("TypeClass"));
                    startActivityForResult(d,1);
                }else{
                    new AlertView("提示", "无可做的任务", null, new String[]{"确定"},
                            null, RectiftyListActivity.this, AlertView.Style.Alert, RectiftyListActivity.this)
                            .show();
                }
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 滚动状态改变时调用
             */

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    try {
                        scrolledX = view.getFirstVisiblePosition();
                        Log.i("scroll X", String.valueOf(scrolledX));
                        scrolledY = view.getChildAt(0).getTop();
                        Log.i("scroll Y", String.valueOf(scrolledY));
                    } catch (Exception e) {
                    }
                }
            }

        });
    }
    private int scrolledX = 0;
    private int scrolledY = 0;
    @Override
    protected void onRestart() {
        super.onRestart();
        if(checkNetworkState()){
            new DownloadRectifyInfoTask(this, this).execute(EmployeeID);
        }else{
            setUIData("无可做的任务");
        }
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
    public void DownloadRectifyInfoTaskEnd(HsicMessage tag) {
        if (tag.getRespCode() == 0) {
            //订单相关信息插入本地数据库
            List<UserRectifyInfo> list1=new ArrayList<UserRectifyInfo>();
            list1= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<UserRectifyInfo>>() {
            }.getType());//数据来源
            if(list1.size()>0){
                //整理订单并插入数据库
                rectifyDB.InsertData(EmployeeID,StationID,list1);
            }
            setUIData("无可做的任务");
        }else{
            /**
             * 调用失败 errocode=1
             */
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }


    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if(o==choice){
            Intent i;
            switch(position){
                case 0:
                    //跳进
                    choice.dismiss();
                    i=new Intent(RectiftyListActivity.this,UserLoginNoCardActivity.class);
                    i.putExtra("LR","3");//上门配送时用户登录
                    startActivity(i);
                    break;
                case 1:
                    i=new Intent(RectiftyListActivity.this,UserLoginByReadActivity.class);
                    i.putExtra("LR","3");//上门配送时用户登录
                    startActivity(i);
                    choice.dismiss();
                    break;
            }
        }else{
            RectiftyListActivity.this.finish();
        }

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
    /**
     * 获取整理本地整改订单信息【未完成，已完成，已上传，未上传】
     * @param msg
     */
    private void setUIData(String msg){
        list= new ArrayList<Map<String, String>>();
        finishList=new ArrayList<>();
        loadList=new ArrayList<>();
        allList=new ArrayList<>();
        list=rectifyDB.GetRectifyInfo(EmployeeID,StationID);
        finishList=rectifyDB.finishList(EmployeeID,StationID);//已完成
        allList=rectifyDB.AllSale(EmployeeID,StationID);//本地所有订单
        loadList=rectifyDB.uploadList(EmployeeID,StationID);//已上传
        rectifyListAdapter=new RectifyDragDelAdapter(this,list);
        listview.setAdapter(rectifyListAdapter);
        int all=allList.size();
        int unfinishCounts=list.size();
        int uploadCounts=loadList.size();
        int finish=finishList.size();
        taskCounts.setText("总任务："+all);
        finishCounts.setText("已完成："+finish);
        unfinish.setText("未完成："+unfinishCounts);
        upload.setText("已上传："+uploadCounts);
        listview.setSelectionFromTop(scrolledX, scrolledY);
        if(list.size()==0){
            new AlertView("提示", msg, null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }
}
