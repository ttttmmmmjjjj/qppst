package com.hsic.tmj.qppst;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.SaleListAdapter;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleAll;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplSearchAssignSale;
import com.hsic.listener.ImplUpHistory;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.service.PushSaleService;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.SearchAssignSaleTask;
import com.hsic.task.UpLoadHistoryTask;
import com.hsic.tmj.floatbutton.WSuspensionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 配送订单列表页面
 */
public class SaleListActivity extends AppCompatActivity implements ImplSearchAssignSale,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener,ImplUpHistory {
    GetBasicInfo getBasicInfo;
    TextView stationName, operator,taskCounts,finishCounts,unfinish,upload,txt_warn;
    ListView saleListInfo;//订单List列表
    Button delivery;//上门配送
    private String cardStatus;
    DeliveryDB deliveryDB;
    String EmployeeID,StationID;
    SaleListAdapter saleListAdapter;
    ListView listview;
    List<Map<String, String>> list;//未完成
    List<Sale> finishList;//已完成
    List<Sale> allList;//本地所有订单
    List<Sale> loadList;//已上传
    AlertView choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_list);
        getBasicInfo = new GetBasicInfo(this);
        deliveryDB=new DeliveryDB(this);
        innitView();
        setFloatWindow();
        Intent intent=new Intent(this, PushSaleService.class);
        startService(intent);
        choice=new AlertView("请选择用户类型", null, null, null,
                new String[]{"用户未发卡", "用户已发卡"},
                this, AlertView.Style.ActionSheet, this);
        registNotificatuinReciver();
        /***
         * 加载该配送员底下的可配送订单信息
         */
        EmployeeID=getBasicInfo.getOperationID();//登录员工号
        StationID=getBasicInfo.getStationID();
//        operator.setText("配送员:"+getBasicInfo.getOperationName());
        operator.setText(""+getBasicInfo.getOperationName());
//        stationName.setText("配送站:"+getBasicInfo.getStationName());
        stationName.setText(""+getBasicInfo.getStationName());
        taskCounts.setText("总任务:0");
        finishCounts.setText("已完成:0");
        unfinish.setText("未完成:0");
        upload.setText("已上传:0");
        if(checkNetworkState()){
            new SearchAssignSaleTask(this, this).execute(EmployeeID);
        }else{
            /**
             * 离线版
             */
            setUIData("无可做的任务");
        }

    }
    /***
     * 设置浮动窗口
     */
    // 20160830
    DisplayMetrics dm;
    SharedPreferences preferences;
    private void setFloatWindow() {
        preferences = getSharedPreferences("setting", 0);
        dm = getBaseContext().getResources().getDisplayMetrics();
        WSuspensionButton suspensionButton = (WSuspensionButton) findViewById(R.id.btnSus);
        int size = suspensionButton.getSize();
        suspensionButton.setBackground(this.getResources().getDrawable(
                R.drawable.hsic));
        suspensionButton.setAlpha(10);
        FrameLayout.LayoutParams wmParams = new FrameLayout.LayoutParams(size,
                size);
        wmParams.leftMargin = preferences.getInt("suspend_btn_x",
                dm.widthPixels - size);// 720-96
        wmParams.topMargin = preferences.getInt("suspend_btn_y",
                (dm.heightPixels - size) / 2);// (1280-96)/2
        suspensionButton.setLayoutParams(wmParams);// 取本地存入的位置信息，并从新设定控件的坐标
        suspensionButton
                .setClickListener(new WSuspensionButton.ClickListener() {

                    public void onClick(View v) {
                        showPopupWindow(v);
                    }
                });
        suspensionButton
                .setCompleteMoveListener(new WSuspensionButton.CompleteMoveListener() {

                    public void onCompleteMove(View v, int left, int top) {
                        saveInLocal(left, top);
                    }

                    private void saveInLocal(int left, int top) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("suspend_btn_x", left);
                        editor.putInt("suspend_btn_y", top);
                        editor.commit();
                    }
                });
    }
    /**
     * 浮动窗口
     *
     * @param view
     */
    private void showPopupWindow(View view) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(SaleListActivity.this).inflate(
                R.layout.bottom_layout, null);
        // 设置按钮的点击事件
        ImageButton query = (ImageButton) contentView
                .findViewById(R.id.id_tab_weixin_img);
        query.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //查询
                Intent i=new Intent(SaleListActivity.this,OtherServiceActivity.class);
                startActivity(i);
            }
        });
        ImageButton tobu = (ImageButton) contentView
                .findViewById(R.id.id_tab_address_img);
        tobu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               //下载
                new SearchAssignSaleTask(SaleListActivity.this, SaleListActivity.this).execute(EmployeeID);
            }
        });
        ImageButton setting = (ImageButton) contentView
                .findViewById(R.id.id_tab_frd_img);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 20161117 上传
                new UpLoadHistoryTask(SaleListActivity.this,SaleListActivity.this).execute();

            }
        });
        ImageButton about = (ImageButton) contentView
                .findViewById(R.id.id_tab_settings_img);
        about.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.layoutbackground));

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }
    private void innitView() {
        txt_warn=this.findViewById(R.id.txt_warn);
        txt_warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchAssignSaleTask(SaleListActivity.this, SaleListActivity.this).execute(EmployeeID);
            }
        });
        listview = (ListView) findViewById(R.id.lv_saleInfo);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(list.size()>0){
                    Intent d=new Intent(SaleListActivity.this,DeliveryActivity.class);
                    Map<String, String> data =  (Map<String, String>)adapterView.getItemAtPosition(position);
                    String UserID=data.get("UserID");
                    String SaleID=data.get("SaleID");
                    d.putExtra("UserID",UserID);
                    d.putExtra("SaleID",SaleID);
                    startActivityForResult(d,1);
                }else{
                    new AlertView("提示", "无可做的任务", null, new String[]{"确定"},
                            null, SaleListActivity.this, AlertView.Style.Alert, SaleListActivity.this)
                            .show();
                }
//                listview.setSelectionFromTop(scrolledX, scrolledY);
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
        delivery.setText("新增订单");
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SaleListActivity.this,UserLoginNoCardActivity.class);
                i.putExtra("LR","1");//上门配送时用户登录
                startActivity(i);
//                choice.show();
            }
        });
    }
    private int scrolledX = 0;
    private int scrolledY = 0;
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
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        if(checkNetworkState()){
            new SearchAssignSaleTask(this, this).execute(EmployeeID);
        }else{
            /**
             * 离线版
             */
            setUIData("无可做的任务");
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void SearchAssignSaleTaskEnd(HsicMessage tag) {
        List<SaleAll> list1=new ArrayList<SaleAll>();
        if (tag.getRespCode() == 0) {
            //订单相关信息插入本地数据库
            list1= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<SaleAll>>() {
            }.getType());//数据来源
            if(list1.size()>0){
                //整理订单并插入数据库
                deliveryDB.InsertSaleInfo(list1,EmployeeID,StationID);
            }
            setUIData("无可做的任务");
        }else{
            /**
             * 看本地是否还有订单
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
                    i=new Intent(SaleListActivity.this,UserLoginNoCardActivity.class);
                    i.putExtra("LR","1");//上门配送时用户登录
                    startActivity(i);
                    break;
                case 1:
                    i=new Intent(SaleListActivity.this,UserLoginByReadActivity.class);
                    i.putExtra("LR","1");//上门配送时用户登录
                    startActivity(i);
                    choice.dismiss();
                    break;
            }
        }else{
//            finish();
        }
    }

    @Override
    public void UpLoadHistoryTaskEnd(HsicMessage tag) {

    }

    /**
     * 推送消息广播接受
     */
    // 写的广播接收者
    class RefreshUiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals("com.hsic.fxqpmanager.activity.NewsReceiver")) {
                int count =intent.getIntExtra("NewCount", 0);
                //更新UI
                //更新UI
                if(count>0){
                    txt_warn.setBackgroundColor(Color.YELLOW);
                    txt_warn.setTextColor(Color.WHITE);
                    txt_warn.setText("新消息:" + count + "条");

                }
                new SearchAssignSaleTask(SaleListActivity.this, SaleListActivity.this).execute(EmployeeID);
            }
        }
    }
    private RefreshUiReceiver receiver;
    // 动态注册广播接受者
    public void registNotificatuinReciver() {
        receiver = new RefreshUiReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction("com.hsic.tmj.qppst.activity.NewsReceiver");
        registerReceiver(receiver, itf);
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
     * 获取整理本地订单信息【未完成，已完成，已上传，未上传.......】
     * @param msg
     */
    private void setUIData(String msg){
        list= new ArrayList<Map<String, String>>();
        finishList=new ArrayList<>();
        loadList=new ArrayList<>();
        allList=new ArrayList<>();
        list=deliveryDB.GetSaleInfo(EmployeeID,StationID);
        finishList=deliveryDB.finishList(EmployeeID,StationID);//已完成
        allList=deliveryDB.AllSale(EmployeeID,StationID);//本地所有订单
        loadList=deliveryDB.uploadList(EmployeeID,StationID);//已上传
        saleListAdapter=new SaleListAdapter(this,list);
        listview.setAdapter(saleListAdapter);
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
