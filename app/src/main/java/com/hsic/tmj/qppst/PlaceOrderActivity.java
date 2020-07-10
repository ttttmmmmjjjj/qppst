package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.AddGoodsListAdapter;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserInfoDoorSale;
import com.hsic.bean.UserReginCode;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.GPrinterCommand;
import com.hsic.bluetooth.PrintQueue;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.listener.ImplGetBasicUserInfoTask;
import com.hsic.listener.ImplGetDevicesTask;
import com.hsic.listener.ImplSubscriberOrderTask;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.spinner.AbstractSpinerAdapter;
import com.hsic.spinner.SpinerPopWindow;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dialoglibrary.OnItemClickListener;
import com.hsic.task.GetBasicUserInfoTask;
import com.hsic.task.SubscriberOrderTask;
import com.hsic.tmj.wheelview.QPType;
import com.hsic.utils.TimeUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderActivity extends AppCompatActivity implements AbstractSpinerAdapter.IOnItemSelectListener, OnDismissListener, OnItemClickListener,
        ImplGetBasicUserInfoTask,
        ImplSubscriberOrderTask, ImplGetDevicesTask {
    UserInfoDoorSale userInfoDoorSale;
    List<UserInfoDoorSale.GoodsInfo> goodsInfo_List;//下载到的商品信息
    List<UserInfoDoorSale.GoodsInfo> goodsInfo_List_add;//订单新增商品信息 (新增)
    List<UserInfoDoorSale.GoodsInfo> goodsInfo_List_Accessary;//订单新增商品信息 (下拉列表信息来源)
    List<UserInfoDoorSale.GoodsInfo> goodsInfo_List_Goods;//订单新增商品信息 (下拉列表信息来源)
    Toolbar toolbar;
    GetBasicInfo basicInfo;
    List<UserReginCode> EmptyList;//暂存已扫描到的空瓶
    List<UserReginCode> FullList;//暂存已扫描到的满瓶
    List<QPType> qpTypes = new ArrayList<QPType>();
    int QPCount = 0;//新增的商品数量
    UserInfoDoorSale userInfoFinish;
    private SearchView mSearchView;//搜索框
    private ListView listView;
    private String requestStr, deviceIDStr;
    private TextView txt_userID, txt_userAddress, txt_goods, txt_username, txt_EmptyNo, txt_fullID,
            txt_stationName, txt_phoneNum;
    private Dialog mGoodsDialog;//
    private LinearLayout root;
    private TextView mtvGoodsName,edt_unitPrice,edt_goodType;
    private EditText edt_GoodsCounts;
    private Button btn_upGoods, btn_scanEmpty, btn_submit, btn_scanFull, btn_checkLast, btn_checkNext;
    private String upGoodsType;//0.气瓶，1.配件
    private String goodsName, goodsCode,unitPrice;
    private String edt_Input;
    private List<UserInfoDoorSale.GoodsInfo> nameList;//可购买商品信息列表
    private SpinerPopWindow mSpinerPopWindow;
    private String EmptyNO;
    private String ReceiveByInput, DeliverByInput;//手输发出瓶标识;//手输回收瓶标识
    private  double totalPrice=0;
    /**
     *新增商品
     */
    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cansel:
                    mGoodsDialog.dismiss();
                    break;
                case R.id.btn_sure://保存数据
                    //数量，品牌都不能为空
                    String goodsName = "", goodsCount = "",unitPricre="",goodType="";
                    goodsName = mtvGoodsName.getText().toString();
                    goodsCount = edt_GoodsCounts.getText().toString();
                    unitPricre=edt_unitPrice.getText().toString();
                    goodType=edt_goodType.getText().toString();
                    int goodsCount_I = 0;
                    if (TextUtils.isEmpty(goodsName)) {
                        Toast.makeText(getBaseContext(), "商品名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(goodsCount)) {
                        Toast.makeText(getBaseContext(), "商品数量不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        goodsCount_I = Integer.parseInt(goodsCount);
                        if (goodsCount_I <= 0) {
                            Toast.makeText(getBaseContext(), "商品数量必须大于0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    UserInfoDoorSale.GoodsInfo goodsInfo;
                    goodsInfo = userInfoDoorSale.new GoodsInfo();
                    goodsInfo.setGoodsName(goodsName);
                    goodsInfo.setGoodsCount(goodsCount);
                    goodsInfo.setGoodsCode(goodsCode);
                    goodsInfo.setUnitPrice(unitPricre);
                    QPCount=QPCount+Integer.parseInt(goodsCount);
                    if (upGoodsType.equals("0")) {
                        goodsInfo.setGoodsType("0");//商品类型：0：气瓶，1：配件
                        QPType qpType = new QPType();
                        qpType.setQPType(goodType);
                        qpType.setQPName(goodsName);
                        qpType.setQPNum(goodsCount);
                        //气瓶类型
                        int typeSize=qpTypes.size();
                        if(typeSize>0){
                            for(int t=0;t<typeSize;t++){
                                //避免商品种类重复 先去重
                                if(goodType.equals(qpTypes.get(t).getQPType())){
                                    qpTypes.remove(qpTypes.get(t));
                                    break;
                                }
                            }
                        }
                        qpTypes.add(qpType);
                    } else if (upGoodsType.equals("1")) {
                        goodsInfo.setGoodsType("1");
                    }
                    int size = goodsInfo_List_add.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            String temp = goodsInfo_List_add.get(i).getGoodsCode();
                            if (goodsCode.equals(temp)) {
                                UserInfoDoorSale.GoodsInfo goodsInfo2;
                                goodsInfo2 = goodsInfo_List_add.get(i);//之前已经添加过该商品
                                goodsInfo_List_add.remove(goodsInfo2);
                                //若是同一种商品多次添加，应该减去前一次的数量
                                String strGoodsCount=goodsInfo2.getGoodsCount();
                                QPCount=QPCount-Integer.parseInt(strGoodsCount);
                                break;
                            }
                        }
                        goodsInfo_List_add.add(goodsInfo);
                    } else {
                        goodsInfo_List_add.add(goodsInfo);
                    }
                    setAdapter(false);
                    mGoodsDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 解决软键盘弹出，布局被顶上去的问题
         */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_place_order);
        basicInfo = new GetBasicInfo(PlaceOrderActivity.this);
        deviceIDStr = basicInfo.getDeviceID();
        txt_stationName = this.findViewById(R.id.txt_stationName);
        txt_userID = this.findViewById(R.id.txt_userID);
        txt_userAddress = this.findViewById(R.id.txt_userAddress);
        txt_goods = this.findViewById(R.id.txt_goods);
        txt_username = this.findViewById(R.id.txt_username);
        txt_phoneNum = this.findViewById(R.id.txt_phoneNum);
        txt_EmptyNo = this.findViewById(R.id.txt_EmptyNo);
        txt_fullID = this.findViewById(R.id.txt_fullID);
        btn_checkLast = this.findViewById(R.id.btn_checkEmpty);
        btn_checkNext = this.findViewById(R.id.btn_checkFull);
        txt_EmptyNo.setText("");
        txt_fullID.setText("");
        listView = this.findViewById(R.id.listView);
        btn_upGoods = this.findViewById(R.id.btn_upGoods);
        btn_scanEmpty = this.findViewById(R.id.btn_scanEmpty);
        btn_scanFull = this.findViewById(R.id.btn_scanFull);
        btn_submit = this.findViewById(R.id.btn_submit);
        mSearchView = this.findViewById(R.id.search);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        /**
         * 设置提示框字体颜色
         * SearchView去掉默认的下划线
         */
        if (mSearchView == null) {
            return;
        }
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(Color.BLACK);//字体颜色
        textView.setTextSize(18);//字体、提示字体大小
        textView.setHintTextColor(Color.GRAY);//提示字体颜色
        if (mSearchView != null) {
            try {        //--拿到字节码
                Class<?> argClass = mSearchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(mSearchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EmptyList = new ArrayList<UserReginCode>();
        FullList = new ArrayList<UserReginCode>();
        goodsInfo_List_add = new ArrayList<UserInfoDoorSale.GoodsInfo>();
        goodsInfo_List = new ArrayList<UserInfoDoorSale.GoodsInfo>();
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                edt_Input = mSearchView.getQuery().toString();
                if (TextUtils.isEmpty(edt_Input)) {
                    Toast.makeText(getBaseContext(), "查询条件不能为空", Toast.LENGTH_SHORT).show();
                    return false;
                }
                edt_Input = addZero(edt_Input, 8);
                userInfoDoorSale = new UserInfoDoorSale();
                userInfoDoorSale.setUserID(edt_Input);
                requestStr = JSONUtils.toJsonWithGson(userInfoDoorSale);
                GetBasicUserInfoTask getTask = new GetBasicUserInfoTask(PlaceOrderActivity.this, PlaceOrderActivity.this, deviceIDStr, requestStr);
                getTask.execute();
                return false;
            }
            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                } else {
                }
                return false;
            }
        });
        userInfoDoorSale = new UserInfoDoorSale();
        EmptyNO = "";
        InnitGoodsDialog();
        /**
         * 查看回收瓶
         */
        btn_checkLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = userInfoDoorSale.getUserID();
                if(userid!=null){
                    if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                String EmptyNO = "";
                EmptyNO = txt_EmptyNo.getText().toString();
                Intent e = new Intent(PlaceOrderActivity.this, ScanQPTagActivity.class);
                e.putExtra("Operation", "E");
                e.putExtra("QPNO", EmptyNO);
                e.putExtra("SaleID", "");
                e.putExtra("QPCount", String.valueOf(QPCount));
                Bundle bundle = new Bundle();
                bundle.putSerializable("EmptyList", (Serializable) EmptyList);
                bundle.putSerializable("qpTypes", (Serializable) qpTypes);
                e.putExtra("bundle", bundle);
                startActivityForResult(e, 2);
            }
        });
        /**
         * 查看交付瓶
         */
        btn_checkNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = userInfoDoorSale.getUserID();
                if(userid!=null){
                    if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                String FullNO = "";
                FullNO = txt_fullID.getText().toString();
                Intent f = new Intent(PlaceOrderActivity.this, ScanQPTagActivity.class);
                f.putExtra("Operation", "F");
                f.putExtra("SaleID", "");
                f.putExtra("QPNO", FullNO);
                f.putExtra("QPCount", String.valueOf(QPCount));
                Bundle bundle = new Bundle();
                bundle.putSerializable("FullList", (Serializable) FullList);
                bundle.putSerializable("qpTypes", (Serializable) qpTypes);
                f.putExtra("bundle", bundle);
                startActivityForResult(f, 1);
            }
        });
        /**
         *
         */
        btn_upGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = userInfoDoorSale.getUserID();
                if(userid!=null){
                    if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                setmSpinerPopWindow(goodsInfo_List_Goods);
                upGoodsType = "0";
                mtvGoodsName.setText("");
                edt_GoodsCounts.setText("");
                mGoodsDialog.show();
            }
        });
        btn_scanEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = userInfoDoorSale.getUserID();
                if(userid!=null){
                    if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                String EmptyNO = "";
                EmptyNO = txt_EmptyNo.getText().toString();
                Intent e = new Intent(PlaceOrderActivity.this, ScanQPTagActivity.class);
                e.putExtra("Operation", "E");
                e.putExtra("QPNO", EmptyNO);
                e.putExtra("SaleID", userid);
                e.putExtra("QPCount", String.valueOf(QPCount));
                Bundle bundle = new Bundle();
                bundle.putSerializable("EmptyList", (Serializable) EmptyList);
                bundle.putSerializable("qpTypes", (Serializable) qpTypes);
                e.putExtra("bundle", bundle);
                startActivityForResult(e, 2);

            }
        });
        btn_scanFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = txt_userID.getText().toString();
                if (TextUtils.isEmpty(userid)) {
                    Toast.makeText(getBaseContext(), "请先查询订单信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(goodsInfo_List_add.size()==0){
                    Toast.makeText(getBaseContext(), "请先新增商品", Toast.LENGTH_SHORT).show();
                    return;
                }
                String FullNO = "";
                FullNO = txt_fullID.getText().toString();
                Intent f = new Intent(PlaceOrderActivity.this, ScanQPTagActivity.class);
                f.putExtra("Operation", "F");
                f.putExtra("SaleID", "");
                f.putExtra("QPNO", FullNO);
                f.putExtra("QPCount", String.valueOf(QPCount));
                Bundle bundle = new Bundle();
                bundle.putSerializable("FullList", (Serializable) FullList);
                bundle.putSerializable("qpTypes", (Serializable) qpTypes);
                f.putExtra("bundle", bundle);
                startActivityForResult(f, 1);

            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userid = userInfoDoorSale.getUserID();
                if(userid!=null){
                    if (TextUtils.isEmpty(userid)) {
                        Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getBaseContext(), "请先查询用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(goodsInfo_List_add.size()==0){
                    Toast.makeText(getBaseContext(), "请先新增商品", Toast.LENGTH_SHORT).show();
                    return;
                }
                String empty = txt_EmptyNo.getText().toString();
                if (TextUtils.isEmpty(empty)) {
                    Toast.makeText(getBaseContext(), "请先扫空瓶", Toast.LENGTH_SHORT).show();
                    return;
                }
                EmptyNO = txt_EmptyNo.getText().toString();
                String fullNo = txt_fullID.getText().toString();
                if (TextUtils.isEmpty(fullNo)) {
                    Toast.makeText(getBaseContext(), "请先扫满瓶", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断满瓶数量
                int scanCount = 0;
                if (fullNo.contains(",")) {
                    String[] size = fullNo.split(",");
                    scanCount = size.length;
                } else {
                    scanCount = 1;
                }
                if (scanCount != QPCount) {
                    Toast.makeText(getBaseContext(), "满瓶数量不匹配", Toast.LENGTH_SHORT).show();
                    return;
                }
                int size = goodsInfo_List_add.size();
                totalPrice=0;
                if (size > 0) {
                    userInfoDoorSale.setPayMode("0");
                    userInfoDoorSale.setGoodsInfo(goodsInfo_List_add);
                    userInfoDoorSale.setOperatorID(basicInfo.getOperationID());
                    userInfoDoorSale.setOperationTime(TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                    userInfoDoorSale.setEmptyNO(EmptyNO);
                    userInfoDoorSale.setFullNO(fullNo);
                    userInfoDoorSale.setSendQPByhand(DeliverByInput);
                    userInfoDoorSale.setReceiveQPByhand(ReceiveByInput);
                    StringBuffer totalInfo=new StringBuffer();
                    for(int m=0;m<size;m++){
                        String strUnitPrice=goodsInfo_List_add.get(m).getUnitPrice();
                        String strGoodsCount=goodsInfo_List_add.get(m).getGoodsCount();
                        totalPrice=totalPrice+(Double.valueOf(strUnitPrice)*Integer.parseInt(strGoodsCount));
                        totalInfo.append(goodsInfo_List_add.get(m).getGoodsName()+"气体费:"+goodsInfo_List_add.get(m).getUnitPrice()+"元*"+goodsInfo_List_add.get(m).getGoodsCount()+"+");
                    }
                    String s=totalInfo.toString();
                    s=s.substring(0,s.length()-1);
                    userInfoDoorSale.setTotalPrice(String.valueOf(totalPrice));
                    userInfoDoorSale.setTotalPriceInfo(s);
                    requestStr = JSONUtils.toJsonWithGson(userInfoDoorSale);
                    SubscriberOrderTask task = new SubscriberOrderTask(PlaceOrderActivity.this, PlaceOrderActivity.this, deviceIDStr, requestStr);
                    task.execute();
                } else {
                    Toast.makeText(getBaseContext(), "无可提交的商品", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        mGoodsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mGoodsDialog.dismiss();
                }
                return false;
            }

        });
        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        toolbar.inflateMenu(R.menu.pickgoods_toolbar_menu);
        toolbar.setTitle(basicInfo.getStationName());
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_search) {
                    Intent i = new Intent(PlaceOrderActivity.this, ScanQRCodeActivity.class);
                    i.putExtra("RequestMode", "user");
                    startActivityForResult(i, 3);
                }

                return false;
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrintQueue.getQueue(getApplicationContext()).disconnect();//
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            //空瓶
            ReceiveByInput = "";
            EmptyList = new ArrayList<UserReginCode>();
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Bundle bundle = data.getBundleExtra("bundle");
            EmptyList = (ArrayList<UserReginCode>) bundle.getSerializable("EmptyList");
            String userRegionCode = b.getString("userReginCode");
            ReceiveByInput = b.getString("ReceiveByInput");
            txt_EmptyNo.setText(userRegionCode);
            EmptyNO = userRegionCode;
        }
        if (requestCode == 1) {
            DeliverByInput = "";
            FullList = new ArrayList<UserReginCode>();
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Bundle bundle = data.getBundleExtra("bundle");
            String userRegionCode = b.getString("userReginCode");
            Log.e("111",userRegionCode);
            DeliverByInput = b.getString("DeliverByInput");
            FullList = (ArrayList<UserReginCode>) bundle.getSerializable("FullList");
            txt_fullID.setText(userRegionCode);
        }
        if (requestCode == 3) {
            String UserID = "";
            UserID = data.getStringExtra("userID");
            if (!UserID.equals("")) {
                userInfoDoorSale = new UserInfoDoorSale();
                userInfoDoorSale.setUserID(UserID);
                requestStr = JSONUtils.toJsonWithGson(userInfoDoorSale);
                GetBasicUserInfoTask getTask = new GetBasicUserInfoTask(PlaceOrderActivity.this, PlaceOrderActivity.this, deviceIDStr, requestStr);
                getTask.execute();
            }

        }
    }

    private void showSpinWindow() {
        mSpinerPopWindow.setWidth(mtvGoodsName.getWidth());
        mSpinerPopWindow.showAsDropDown(mtvGoodsName);
        mtvGoodsName.setFocusable(false);
    }

    /**
     *
     */
    private void InnitGoodsDialog() {
        mGoodsDialog = new Dialog(this, R.style.my_dialog);
        root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.spinner_goods_layout, null);
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        mtvGoodsName = (EditText) root.findViewById(R.id.edt_brand);
        mtvGoodsName.setText("");
        edt_GoodsCounts = (EditText) root.findViewById(R.id.edt_quailty);
        edt_GoodsCounts.setText("");
        edt_unitPrice=(EditText) root.findViewById(R.id.edt_unitPrice);
        edt_unitPrice.setText("");
        edt_goodType=(EditText) root.findViewById(R.id.edt_goodType);
        edt_goodType.setText("");
        mGoodsDialog.setContentView(root);
        Window dialogWindow = mGoodsDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 400; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

    /**
     * 获取用户基本信息
     *
     * @param tag
     */
    @Override
    public void GetBasicUserInfoTaskListenerEnd(HsicMessage tag) {
        hintKeyBoard();
        btn_upGoods.setEnabled(true);
        btn_scanEmpty.setEnabled(true);
        btn_scanFull.setEnabled(true);
        btn_checkNext.setEnabled(true);
        btn_checkLast.setEnabled(true);
        if (tag.getRespCode() == 0) {
            /**
             * 用户基本信息查询完成以后，初始化
             */
            goodsInfo_List_Goods = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            goodsInfo_List_add = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            goodsInfo_List = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            totalPrice=0;
            QPCount=0;
            setAdapter(false);
            Type typeOfT = new TypeToken<List<UserInfoDoorSale>>() {
            }.getType();
            List<UserInfoDoorSale> UserInfo_LIST = new ArrayList<UserInfoDoorSale>();
            UserInfo_LIST = JSONUtils.toListWithGson(tag.getRespMsg(), typeOfT);
            userInfoDoorSale = new UserInfoDoorSale();
            userInfoDoorSale = UserInfo_LIST.get(0);
            txt_stationName.setText("站点:" + userInfoDoorSale.getStationName());
            txt_userID.setText("编号:" + userInfoDoorSale.getUserID());
            txt_username.setText("姓名:" + userInfoDoorSale.getUserName() );
            txt_userAddress.setText("地址:" + userInfoDoorSale.getAddress());
            txt_phoneNum.setText("电话:" + userInfoDoorSale.getPhoneNumber());
            goodsInfo_List = userInfoDoorSale.getGoodsInfo();
            int size = goodsInfo_List.size();
            String goods = "";
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    if (goodsInfo_List.get(i).getGoodsType().equals("0")) {
                        goods += goodsInfo_List.get(i).getGoodsName() +  ",";
                        goodsInfo_List_Goods.add(goodsInfo_List.get(i));
                    }
                }
                if (goods.length() > 1) {
                    goods = goods.substring(0, goods.length() - 1);
                }
            }

            txt_goods.setText("可购商品:" + goods);
            setmSpinerPopWindow(goodsInfo_List_Goods);
        } else {
            txt_EmptyNo.setText("");
            txt_fullID.setText("");
            txt_stationName.setText("站点:" );
            txt_userID.setText("编号");
            txt_username.setText("姓名:");
            txt_userAddress.setText("地址:" );
            txt_phoneNum.setText("电话:" );
            txt_goods.setText("可购商品:" );
            goodsInfo_List_add = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            qpTypes = new ArrayList<QPType>();
            EmptyList = new ArrayList<UserReginCode>();
            FullList = new ArrayList<UserReginCode>();
            goodsInfo_List_add = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            goodsInfo_List = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            qpTypes = new ArrayList<QPType>();
            EmptyNO = "";
            userInfoDoorSale = new UserInfoDoorSale();
            totalPrice=0;
            QPCount=0;
            setAdapter(false);
            shownDialog(tag.getRespMsg());
        }
    }

    /**
     *
     新增商品下拉框
     * @param pos
     */
    @Override
    public void onItemClick(int pos) {
        goodsCode = "";
        goodsName = "";
        unitPrice="";
        if (pos >= 0 && pos <= nameList.size()) {
            String value = nameList.get(pos).getGoodsName();
            String key = nameList.get(pos).getGoodsCode();
            unitPrice= nameList.get(pos).getUnitPrice();
            goodsCode = key;
            goodsName = value;
            mtvGoodsName.setText(value);
            edt_unitPrice.setText( nameList.get(pos).getUnitPrice());
            edt_goodType.setText( nameList.get(pos).getGoodsCode());
            mSpinerPopWindow.dismiss();
        }
    }

    /**
     * 设置可购买商品信息列表
     *
     * @param goodsInfo
     */
    private void setmSpinerPopWindow(List<UserInfoDoorSale.GoodsInfo> goodsInfo) {
        nameList = new ArrayList<UserInfoDoorSale.GoodsInfo>();
        int size = goodsInfo.size();
        for (int i = 0; i < size; i++) {
            nameList.add(goodsInfo.get(i));
        }
        mSpinerPopWindow = new SpinerPopWindow(this);
        mSpinerPopWindow.refreshData(nameList, 0);
        mSpinerPopWindow.setItemListener(this);
        mtvGoodsName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mtvGoodsName.getWindowToken(), 0);
                mSpinerPopWindow.refreshData(nameList, 0);
                showSpinWindow();
            }
        });
    }

    /**
     * 下订单
     *
     * @param tag
     */
    @Override
    public void SubscriberOrderTaskListenerEnd(HsicMessage tag) {
        hintKeyBoard();
        try {
            if (tag.getRespCode() == 0) {
                EmptyList = new ArrayList<UserReginCode>();
                FullList = new ArrayList<UserReginCode>();
                goodsInfo_List_add = new ArrayList<UserInfoDoorSale.GoodsInfo>();
                goodsInfo_List = new ArrayList<UserInfoDoorSale.GoodsInfo>();
                qpTypes = new ArrayList<QPType>();
                EmptyNO = "";
                totalPrice=0;
                QPCount=0;
                userInfoFinish = new UserInfoDoorSale();
                btn_upGoods.setEnabled(false);
                btn_scanEmpty.setEnabled(false);
                btn_scanFull.setEnabled(false);
                btn_checkNext.setEnabled(false);
                btn_checkLast.setEnabled(false);
                List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2 = new ArrayList<UserInfoDoorSale.GoodsInfo>();
                userInfoFinish = JSONUtils.toObjectWithGson(tag.getRespMsg(), UserInfoDoorSale.class);
                goodsInfo_List2 = userInfoFinish.getGoodsInfo();
                int size = goodsInfo_List2.size();
                //显示订单信息并且打印
                Toast.makeText(getBaseContext(), "门售订单成功", Toast.LENGTH_SHORT).show();
                StringBuffer msg = new StringBuffer();
                msg.append("门售收据\n");
                msg.append(("---------------------------------------------------------------\n"));
                msg.append(PrintUtils.printTwoData("用户站点:", userInfoFinish.getStationName() + "\n"));
                msg.append(PrintUtils.printTwoData("订单编号:", userInfoFinish.getSaleID() + "\n"));
                msg.append(PrintUtils.printTwoData("用户编号:", userInfoFinish.getUserID() + "\n"));
                msg.append(PrintUtils.printTwoData("用户姓名:", userInfoFinish.getUserName() + "\n"));
                msg.append(PrintUtils.printTwoData("电话:", userInfoFinish.getPhoneNumber() + "\n"));
                msg.append("用户地址:" + userInfoFinish.getAddress() + "\n");
                msg.append(("---------------------------------------------------------------\n"));
                msg.append(PrintUtils.printThreeData("项目", "数量", "金额"+ "\n"));
                for (int i = 0; i < size; i++) {
                    msg.append(PrintUtils.printThreeData(goodsInfo_List2.get(i).getGoodsName(), goodsInfo_List2.get(i).getGoodsCount(),goodsInfo_List2.get(i).getUnitPrice()+ "\n"));
                }
                msg.append(("---------------------------------------------------------------\n"));
                msg.append(PrintUtils.printTwoData("空瓶", userInfoFinish.getEmptyNO() + "\n"));
                msg.append(PrintUtils.printTwoData("满瓶", userInfoFinish.getFullNO() + "\n"));
                msg.append(("---------------------------------------------------------------\n"));
                msg.append(PrintUtils.printTwoData("总价:", userInfoFinish.getTotalPrice() + "\n"));
                msg.append(PrintUtils.printTwoData("完成时间:", userInfoFinish.getOperationTime() + "\n"));
                msg.append(PrintUtils.printTwoData("操作人:", basicInfo.getOperationName() + "\n"));
                msg.append(PrintUtils.printTwoData("操作站点:", basicInfo.getStationName() + "\n"));
                SaleCodeDialog t = new SaleCodeDialog(PlaceOrderActivity.this, msg.toString());
                t.shown();
            } else {
                shownDialog(tag.getRespMsg());
            }
        } catch (Exception ex) {
            ex.toString();
        }

    }

    @Override
    public void GetDevicesTaskEnd(HsicMessage tag) {
        try {
            if (tag.getRespCode() == 0) {
                goodsInfo_List_Accessary = new ArrayList<UserInfoDoorSale.GoodsInfo>();
                UserInfoDoorSale userInfo = new UserInfoDoorSale();
                userInfo = JSONUtils.toObjectWithGson(tag.getRespMsg(), UserInfoDoorSale.class);
                goodsInfo_List_Accessary = userInfo.getGoodsInfo();
                setmSpinerPopWindow(goodsInfo_List_Accessary);
            } else {
                shownDialog(tag.getRespMsg());
            }
        } catch (Exception ex) {

        }
    }

    /**
     * 订单新增商品信息ListView
     *
     * @param isClick
     */
    private void setAdapter(boolean isClick) {
        listView.setAdapter(new AddGoodsListAdapter(this, goodsInfo_List_add, isClick,
                new ShownDialogListener() {
                    @Override
                    public void onCheckedChanged(boolean b, String GoodsName, String GoodsCount, String GoodsCode) {
                        goodsCode = GoodsCode;
                        mtvGoodsName.setText(GoodsName);
                        mGoodsDialog.show();
                    }
                }));
    }

    /**
     * 关闭软键盘
     */
    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 用户编号补0
     *
     * @param str
     * @param strLength
     * @return
     */
    private String addZero(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左(前)补0
            // sb.append(str).append("0");//右(后)补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    private void shownDialog(String msg) {
        new AlertView("提示", msg, null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this)
                .show();
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        PlaceOrderActivity.this.finish();
    }

    /**
     * 打印交易信息
     */
    private void print() {
        int pCount = 2;
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        for (int a = 0; a < pCount; a++) {
            printBytes.add(GPrinterCommand.reset);
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold);
            printBytes.add(GPrinterCommand.LINE_SPACING_DEFAULT);
            printBytes.add(GPrinterCommand.ALIGN_CENTER);
            printBytes.add(PrintUtils.str2Byte(basicInfo.getCompanyName()+ "\n"));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte("门售收据\n"));
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold_cancel);
            printBytes.add(GPrinterCommand.NORMAL);
            printBytes.add(GPrinterCommand.ALIGN_LEFT);
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户站点", userInfoFinish.getStationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("订单编号", userInfoFinish.getSaleID() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户编号", userInfoFinish.getUserID() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户类型", userInfoFinish.getUserType() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户姓名", userInfoFinish.getUserName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("电话", userInfoFinish.getPhoneNumber() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("", userInfoFinish.getAddress() + "\n")));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData("项目", "数量", "金额\n")));
            List<UserInfoDoorSale.GoodsInfo> goodsInfo_List2 = new ArrayList<UserInfoDoorSale.GoodsInfo>();
            goodsInfo_List2 = userInfoFinish.getGoodsInfo();
            int size = goodsInfo_List2.size();
            for (int i = 0; i < size; i++) {
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData(goodsInfo_List2.get(i).getGoodsName(), "" + goodsInfo_List2.get(i).getGoodsCount(), goodsInfo_List2.get(i).getUnitPrice() + "\n")));

            }
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("总价", userInfoFinish.getTotalPrice() + "\n")));
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold_cancel);
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("空瓶", userInfoFinish.getEmptyNO() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("满瓶", userInfoFinish.getFullNO() + "\n")));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("合计", userInfoFinish.getTotalPrice() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("操作人", basicInfo.getOperationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("完成时间", userInfoFinish.getOperationTime() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("操作站点", basicInfo.getStationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte("\n\n\n\n\n"));
        }
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }

    public interface ShownDialogListener {
        void onCheckedChanged(boolean b, String GoodsName, String GoodsCount,
                              String GoodsCode);
    }

    /***
     *交易信息详情
     */
    public class SaleCodeDialog {
        private Context context;
        private Dialog mGoodsDialog;//
        private LinearLayout root;
        private TextView info;
        private String msg;
        private Button btn_sure, btn_cansel;

        public SaleCodeDialog(final Context context, String msg) {
            this.context = context;
            this.msg = msg;
            mGoodsDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.sale_info_dialog_layout, null);
            info = root.findViewById(R.id.saleinto);
            info.setText("");
            btn_sure = root.findViewById(R.id.btn_print);
            btn_cansel = root.findViewById(R.id.btn_cansel);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dimiss();
                    print();
                }
            });
            btn_cansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoodsDialog.dismiss();
                }
            });
            mGoodsDialog.setContentView(root);
            Window dialogWindow = mGoodsDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 450; // 新位置Y坐标
            lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
//        lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            mGoodsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        mGoodsDialog.dismiss();
                    }
                    return false;
                }
            });
        }

        public void shown() {
            info.setText(msg);
            mGoodsDialog.show();
        }

        public void dimiss() {
            mGoodsDialog.dismiss();
        }
    }


}
