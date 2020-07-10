package com.hsic.tmj.qppst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.adapter.GoodsPriceAdapter;
import com.hsic.bdlocation.BDLocationUtils;
import com.hsic.bdlocation.Const;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleDetail;
import com.hsic.bean.ScanHistory;
import com.hsic.bean.UserReginCode;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.Barcode;
import com.hsic.bluetooth.GPrinterCommand;
import com.hsic.bluetooth.PrintPic;
import com.hsic.bluetooth.PrintQueue;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplGetBill;
import com.hsic.listener.ImplUpHistory;
import com.hsic.listener.ImplUploadSale;
import com.hsic.picture.PictureHelper;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dialoglibrary.OnItemClickListener;
import com.hsic.task.UpLoadHistoryTask;
import com.hsic.task.UploadSaleTask;
import com.hsic.utils.DESEncrypt;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ****************** 20200616     ********************************************
 * TypeClass 0 居民 1 非居
 * 配送
 */
public class DeliveryActivity extends AppCompatActivity implements OnDismissListener,
        OnItemClickListener,
        ImplUploadSale,ImplGetBill,ImplUpHistory {
    List<UserReginCode> EmptyList;//暂存已扫描到的空瓶
    List<UserReginCode> FullList;//暂存已扫描到的满瓶
    AlertView infoDialog,ChoicePayType;//确认提交订单对话框
    List<Map<String, String>> saleInfo;//根据用户编号查询订单相关信息
    private TextView txt_userName, txt_cardID, txt_phone, txt_goods, txt_address, txt_sendQP,
            txt_receiverQP,txt_SaleID,txt_price,txt_userType,txt_callingPhone;
    private Button btn_readQP, btn_search, btn_print, btn_submit, btn_checkLast, btn_checkNext;
    private String[] QPType = {"满瓶", "空瓶"};//选择扫描的气瓶类型
    private String GoodsCount;//气瓶数量
    private List<com.hsic.bean.SaleDetail> SaleDetail_LIST;
    private String ReceiveByInput;//手输回收瓶标识
    private String DeliverByInput;//手输发出瓶标识
    private String wghqp;//未归还气瓶
    private String StationID, SaleID, UserID, OperationID, IsNew;
    private GetBasicInfo getBasicInfo;
    private DeliveryDB dbData;
    private AlertDialog alertDialog1; //信息框
    List<ScanHistory> scanHistories;
    Sale sale;
    StringBuilder msg = new StringBuilder();
    String userName="",phone="",goods="",address="",CustomerCardID="",userType="",AllPrice="",
            callingTphone="",CustomerType="",PayMode="",installType="",Url="",TypeClass="",ISNeedZS="";
    private boolean isSave=false;
    List<SaleDetail> details;
    BDLocationUtils bdLocationUtils;
    public static String filePath;
    int Match;
    /**
     * 20190722 新增监装到位需求
     * @param savedInstanceState
     */
    private RadioGroup rg_installType;
    private RadioButton rb_c,rb_s;//公司装,自装 ,赠送橡皮管
    private CheckBox cb_rubberTube;//赠送橡皮管
    private TabHost mTabHost;

    private List<SaleDetail> goodsPrice;//商品价格列表
    private ListView lv_goodprice;

    /**
     *价格修改
     */
    private Dialog editDialog;//
    private LinearLayout root;
    private TextView tv_pre,edt_brand,tv_number,edt_quailty;
    private EditText edt_GoodsCounts;
    private Button btn_cansel,btn_sure;
    private SaleDetail modifyPrice;//价格修改
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        innitView();
        getBasicInfo = new GetBasicInfo(this);
        StationID = getBasicInfo.getStationID();
        OperationID = getBasicInfo.getOperationID();
        dbData = new DeliveryDB(this);
        saleInfo = new ArrayList<Map<String, String>>();
        goodsPrice=new ArrayList<>();
        GoodsCount = "";
        PayMode="";//支付方式
        Intent i = getIntent();
        UserID = i.getStringExtra("UserID");
        SaleID=i.getStringExtra("SaleID");
        EmptyList = new ArrayList<UserReginCode>();
        FullList = new ArrayList<UserReginCode>();
        details=new ArrayList<>();
        infoDialog=  new AlertView("提示", "请提交订单", null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);
        ChoicePayType=new AlertView("请选择支付方式", null, null, null,
                new String[]{"现金", "气票","月结"},
                this, AlertView.Style.ActionSheet, this);
        /**
         * 根据用户编号查询用户订单信息
         */
        if(!SaleID.equals("")){
            saleInfo = dbData.GetSaleInfoBySaleID(OperationID, StationID, SaleID);
        }else{
            saleInfo = dbData.GetSaleInfoByUserID(OperationID, StationID, UserID);
        }
        setViewData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        bdLocationUtils=new BDLocationUtils(this);//百度地图定位
        bdLocationUtils.doLocation();
        bdLocationUtils.mLocationClient.start();
        InnitEditDialog();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        bdLocationUtils.mLocationClient.stop();
        PrintQueue.getQueue(getApplicationContext()).disconnect();//
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==event.KEYCODE_BACK){
        //若是该订单已做  提示提交该订单
            if(dbData.isDone(OperationID,StationID,SaleID)){
                infoDialog.show();
                return true;
            }
            if(ChoicePayType.isShowing()){
                ChoicePayType.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void innitView() {
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();

        TabHost.TabSpec specOne = mTabHost.newTabSpec("选项卡一");
        specOne.setIndicator(composeLayout("基本信息"));
        specOne.setContent(R.id.tab_basicInfo);
        mTabHost.addTab(specOne);

        TabHost.TabSpec specTwo = mTabHost.newTabSpec("选项卡二");
        specTwo.setIndicator(composeLayout("附件信息"));
        specTwo.setContent(R.id.tab_operation);
        mTabHost.addTab(specTwo);
        mTabHost.setCurrentTab(0);
        lv_goodprice =this.findViewById(R.id.iv_goodprice);
        lv_goodprice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(!getBasicInfo.getCompanyCode().equals("2010")){
                    modifyPrice=(SaleDetail)adapterView.getItemAtPosition(position);
                    edt_brand.setText(modifyPrice.getQPName());
                    edt_quailty.setText(modifyPrice.getQPPrice());
                    editDialog.show();
                }

            }
        });
        txt_SaleID=this.findViewById(R.id.txt_saleID);
        txt_SaleID.setText("订单：");
        txt_userName = this.findViewById(R.id.txt_userName);
        txt_userName.setText("姓名：");
        txt_cardID = this.findViewById(R.id.txt_cardID);
        txt_cardID.setText("编号：");
        txt_phone = this.findViewById(R.id.txt_phone);
        txt_phone.setText("联系电话：");
        txt_phone.setVisibility(View.GONE);
        txt_callingPhone=this.findViewById(R.id.txt_callingPhone);
        txt_callingPhone.setText("来电电话：");
        txt_callingPhone.setVisibility(View.GONE);
        txt_goods = this.findViewById(R.id.txt_goods);
        txt_goods.setText("商品：");
        txt_address = this.findViewById(R.id.txt_address);
        txt_address.setText("地址：");
        txt_address.setVisibility(View.GONE);
        txt_price=this.findViewById(R.id.txt_price);
        txt_price.setText("交易金额：");
        txt_userType=this.findViewById(R.id.txt_userType);
        txt_userType.setText("客户类型：");
        txt_receiverQP = this.findViewById(R.id.txt_gpinfo2);
        txt_receiverQP.setText("");
        txt_sendQP = this.findViewById(R.id.txt_gpinfo4);
        txt_sendQP.setText("");
        rg_installType=this.findViewById(R.id.rg_installType);
        cb_rubberTube=(CheckBox) this.findViewById(R.id.cb_rubberTube);//橡皮管赠送
        installType="";
        rb_c=this.findViewById(R.id.rb_c);
        rb_s=this.findViewById(R.id.rb_s);
        rg_installType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(group.getId()==R.id.rg_installType){
                    rg_installType.setBackgroundColor(Color.WHITE);
                    switch (checkedId){
                        case R.id.rb_c://公司装
                            installType="1";
                            break;
                        case R.id.rb_s://自装
                            installType="0";
                            break;
                    }
                }
            }
        });
        btn_readQP = this.findViewById(R.id.btn_readQP);
        btn_search = this.findViewById(R.id.btn_search);
        btn_print = this.findViewById(R.id.btn_print);
        btn_submit = this.findViewById(R.id.btn_submit);
        btn_submit.setEnabled(false);
        btn_readQP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先弹出扫空瓶还是满瓶
                showList();//选择
            }
        });
        /**
         * 安检
         */
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(DeliveryActivity.this, InspectionByCActivity.class);
                search.putExtra("SaleID",SaleID);
                search.putExtra("UserID",UserID);
                search.putExtra("TypeClass",TypeClass);
                startActivity(search);


            }
        });
        /**
         * 保存打印
         */
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交订单
                //校验瓶子数量
                int goodsCount = 0;
                String FullNo = "";
                FullNo = txt_sendQP.getText().toString();
                if (!TextUtils.isEmpty(FullNo)) {
                    int count = 0;
                    if (FullNo.contains(",")) {
                        String[] tmp = FullNo.split(",");
                        count = tmp.length;
                    } else {
                        count = 1;
                    }
                    //校验满瓶数量
                    goodsCount = Integer.parseInt(GoodsCount);
                    if (count != goodsCount) {
                        mTabHost.setCurrentTab(1);
                        Toast.makeText(DeliveryActivity.this, "请核对满瓶数量", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(DeliveryActivity.this, "满瓶数量不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String EmptyNo = "";
                EmptyNo = txt_receiverQP.getText().toString();
                if (!IsNew.equals("0")) {
                    //非新用户必须有归还瓶
                    if (TextUtils.isEmpty(EmptyNo)) {
                        mTabHost.setCurrentTab(1);
                        Toast.makeText(DeliveryActivity.this, "空瓶数量不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                /**
                 * 同一钢瓶瓶不能既是满瓶又是空瓶
                 */
               if(goodsCount>1){
                   if(!EmptyNo.equals("")){
                       if(EmptyNo.contains(",")){
                           String[] tmp = EmptyNo.split(",");
                           int size=tmp.length;
                           for(int a=0;a<size;a++){
                               if(FullNo.contains(tmp[a])){
                                   mTabHost.setCurrentTab(1);
                                   Toast.makeText(DeliveryActivity.this, "同一钢瓶瓶不能既是满瓶又是空瓶", Toast.LENGTH_SHORT).show();
                                   return;
                               }
                           }
                       }else{
                          if(FullNo.contains(EmptyNo)){
                              mTabHost.setCurrentTab(1);
                              Toast.makeText(DeliveryActivity.this, "同一钢瓶瓶不能既是满瓶又是空瓶", Toast.LENGTH_SHORT).show();
                              return;
                          }

                       }
                   }
               }else {
                   if(!EmptyNo.equals("")){
                       if( EmptyNo.equals(FullNo)){
                           mTabHost.setCurrentTab(1);
                           Toast.makeText(DeliveryActivity.this, "同一钢瓶瓶不能既是满瓶又是空瓶", Toast.LENGTH_SHORT).show();
                           return;
                       }

                   }
               }
                //请确定本订单监装类型
                if(installType.equals("")){
                    mTabHost.setCurrentTab(1);
                    rg_installType.setBackgroundColor(Color.RED);
                    Toast.makeText(DeliveryActivity.this, "请确定本订单监装类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ISNeedZS.equals("1")){
                    //赠送橡皮管
                    if(!cb_rubberTube.isChecked()){
                        mTabHost.setCurrentTab(1);
                        cb_rubberTube.setBackgroundColor(Color.RED);
                        Toast.makeText(DeliveryActivity.this, "该用户应赠送橡皮管", Toast.LENGTH_LONG).show();
                    }
                }
                sale=new Sale();
                sale.setFinishTime(TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                sale.setSendQP(FullNo);
                sale.setReceiveQP(EmptyNo);
                sale.setReceiveByInput(ReceiveByInput);
                sale.setDeliverByInput(DeliverByInput);
                sale.setGPS_J(Const.LONGITUDE);
                sale.setGPS_W(Const.LATITUDE);
                sale.setAZType(installType);
                /************       未归还瓶是否匹配    ***********/
                if(!IsNew.equals("0")){
                    if(!EmptyNo.equals("")){
                        if(EmptyNo.contains(",")){
                            String[] tmp = EmptyNo.split(",");
                            int size=tmp.length;
                            for(int a=0;a<size;a++){
                                if(!wghqp.contains(tmp[a])){
                                    sale.setMatch(1);
                                    return;
                                }
                            }
                        }else{
                            if(!wghqp.contains(EmptyNo)){
                                sale.setMatch(1);
                                return;
                            }
                        }
                    }
                }
                SortSaleDetail(sale);
                msg=new StringBuilder();
                msg.append(("---------------------------------------------------------------\n")) ;
                msg.append(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n"));
                msg.append(PrintUtils.printTwoData("销售单号", SaleID + "\n"));
                msg.append(PrintUtils.printTwoData("客户编号", UserID + "\n"));
                msg.append(PrintUtils.printTwoData("姓名", userName + "\n"));
                msg.append(PrintUtils.printTwoData("客户类型", userType + "\n"));
                msg.append(PrintUtils.printTwoData("联系电话", phone+ "\n"));
                msg.append(getBasicInfo.getCompanyName()+ "\n");//公司
                msg.append(("---------------------------------------------------------------\n")) ;
                msg.append("回收瓶：" + "\n");
                msg.append(EmptyNo + "\n");
                msg.append("配送瓶：" + "\n");
                msg.append(FullNo + "\n");
                msg.append(("---------------------------------------------------------------\n")) ;
                if(!isSave){
                    ChoicePayType.show();//选择支付方式【现金，月结，气票，支付宝，微信】
                }else{
                    cb_rubberTube.setEnabled(false);
                    rg_installType.setEnabled(false);
                    rb_c.setEnabled(false);
                    rb_s.setEnabled(false);
                    btn_submit.setEnabled(true);
                    btn_search.setEnabled(false);
                    btn_readQP.setEnabled(false);
                    btn_checkLast.setEnabled(false);
                    btn_checkNext.setEnabled(false);
                    Toast.makeText(DeliveryActivity.this, "订单保存成功", Toast.LENGTH_SHORT).show();
                    details=dbData.GetSaleDetailByP(OperationID,StationID,SaleID);
                    int size=details.size();
                    for (int i = 0; i < size; i++) {
                        if(details.get(i).getIsEx()!=0){
                            if(details.get(i).getIsEx()==2){
                                msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                            }else {
                                msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getPlanSendNum(), details.get(i).getQPPrice() + "\n"));
                            }
                        }else{
                            msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                        }
                    }
                    msg.append(PrintUtils.printTwoData("交易金额",AllPrice+"\n"));
                    SaleCodeDialog t = new SaleCodeDialog(DeliveryActivity.this, msg.toString());
                    t.shown();
                }
            }
        });
        /**
         * 上传提交
         */
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 上传订单信息
                 */
                boolean ret=false;
                ret=dbData.isSaleUpLoad(OperationID,StationID,SaleID);
                IsImage();
            }
        });
        btn_checkLast = this.findViewById(R.id.checklook);
        btn_checkNext = this.findViewById(R.id.checklook1);
        /**
         * 查看回收瓶
         */
        btn_checkLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EmptyNO = "";
                EmptyNO = txt_receiverQP.getText().toString();
                Intent e = new Intent(DeliveryActivity.this, ReadQPActivity.class);
                e.putExtra("Operation", "E");
                e.putExtra("QPNO", EmptyNO);
                e.putExtra("SaleID", SaleID);
                e.putExtra("QPCount", GoodsCount);
                Bundle bundle = new Bundle();
                bundle.putSerializable("EmptyList", (Serializable) EmptyList);
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
                String FullNO = "";
                FullNO = txt_sendQP.getText().toString();
                Intent f = new Intent(DeliveryActivity.this, ReadQPActivity.class);
                f.putExtra("Operation", "F");
                f.putExtra("SaleID", SaleID);
                f.putExtra("QPNO", FullNO);
                f.putExtra("QPCount", GoodsCount);
                Bundle bundle = new Bundle();
                bundle.putSerializable("FullList", (Serializable) FullList);
                f.putExtra("bundle", bundle);
                startActivityForResult(f, 1);
            }
        });
    }

    /**
     * 初始化控件信息
     */
    private void setViewData() {
        if(saleInfo.size()>0){
            SaleID=saleInfo.get(0).get("SaleID");
            userName=saleInfo.get(0).get("CustomerName");
            CustomerCardID=saleInfo.get(0).get("CustomerCardID");
            goods=saleInfo.get(0).get("GoodsInfo");
            phone=saleInfo.get(0).get("Telephone");
            callingTphone=saleInfo.get(0).get("CallingTelephone");
            address=saleInfo.get(0).get("Address");
            userType=saleInfo.get(0).get("CustomerTypeName");
            CustomerType=saleInfo.get(0).get("CustomerType");//CT03老人卡
            TypeClass=saleInfo.get(0).get("TypeClass");//0 居民 1 非居
            AllPrice=saleInfo.get(0).get("AllPrice");
            wghqp=saleInfo.get(0).get("wghqp");
            Log.e("wghqp",wghqp);
            txt_SaleID.setText("订单编号："+SaleID);
            txt_cardID.setText("客户编号：" + UserID);
            IsNew = saleInfo.get(0).get("IsNew");
            if (IsNew.equals("0")) {
                Spanned strA = Html.fromHtml("姓名：" +userName + "["+"<font color=#ff0000>" + "新用户" + "</font>"+"]");
                txt_userName.setText(strA);
            } else {
                txt_userName.setText("姓名：" + userName);
            }
            txt_userType.setText("客户类型："+userType);
            txt_phone.setText("联系电话：" + phone);
            txt_callingPhone.setText("来电电话："+callingTphone);
            txt_address.setText("地址：" + address);
            txt_goods.setText("商品：" + goods);
            txt_price.setText("交易金额："+AllPrice);
            GoodsCount = saleInfo.get(0).get("GoodsCount");
            ISNeedZS=saleInfo.get(0).get("ISNeedZS");
            if(ISNeedZS.equals("0")){
                cb_rubberTube.setVisibility(View.GONE);
            }
            goodsPrice=dbData.getGoodsPrice(SaleID);
            lv_goodprice.setAdapter(new GoodsPriceAdapter(this,goodsPrice));;
        }else{
            new AlertView("提示","该用户无可配送的订单", null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //满瓶
            DeliverByInput = "";
            FullList = new ArrayList<UserReginCode>();
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Bundle bundle = data.getBundleExtra("bundle");
            String userRegionCode = b.getString("userReginCode");
            DeliverByInput = b.getString("DeliverByInput");
            FullList = (ArrayList<UserReginCode>) bundle.getSerializable("FullList");
            txt_sendQP.setText(userRegionCode);
        }
        if (requestCode == 2) {
            //空瓶
            ReceiveByInput = "";
            EmptyList = new ArrayList<UserReginCode>();
            Bundle b = data.getExtras(); //data为B中回传的Intent
            Bundle bundle = data.getBundleExtra("bundle");
            EmptyList = (ArrayList<UserReginCode>) bundle.getSerializable("EmptyList");
            String userRegionCode = b.getString("userReginCode");
            ReceiveByInput = b.getString("ReceiveByInput");
            txt_receiverQP.setText(userRegionCode);
        }
        if(requestCode==10){
            if(resultCode == Activity.RESULT_OK){
                String time=TimeUtils.getTime("yyyyMMddHHmmss");
                getImagePath(filePath,getBasicInfo.getOperationID(),SaleID, UserID,time);
            }
        }
    }

    /**
     * 选择扫描气瓶类型
     */
    public void showList() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择扫描气瓶类型");
        alertBuilder.setItems(QPType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (QPType[i].equals("满瓶")) {
                    String FullNO = "";
                    FullNO = txt_sendQP.getText().toString();
                    Intent f = new Intent(DeliveryActivity.this, ReadQPActivity.class);
                    f.putExtra("Operation", "F");
                    f.putExtra("SaleID", SaleID);
                    f.putExtra("QPNO", FullNO);
                    f.putExtra("QPCount", GoodsCount);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("FullList", (Serializable) FullList);
                    f.putExtra("bundle", bundle);
                    startActivityForResult(f, 1);
                }
                if (QPType[i].equals("空瓶")) {
                    String EmptyNO = "";
                    EmptyNO = txt_receiverQP.getText().toString();
                    Intent e = new Intent(DeliveryActivity.this, ReadQPActivity.class);
                    e.putExtra("Operation", "E");
                    e.putExtra("QPNO", EmptyNO);
                    e.putExtra("SaleID", SaleID);
                    e.putExtra("QPCount", GoodsCount);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EmptyList", (Serializable) EmptyList);
                    e.putExtra("bundle", bundle);
                    startActivityForResult(e, 2);
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = alertBuilder.create();
        alertDialog1.show();
    }

    @Override
    public void onDismiss(Object o) {
        if(o==infoDialog){
            infoDialog.dismiss();
        }else if(o==ChoicePayType){
            ChoicePayType.dismiss();
        }
        else{
            DeliveryActivity.this.finish();
        }
    }

    @Override
    public void onItemClick(Object o, int position) {
        try {
           if(o==infoDialog){
               infoDialog.dismiss();
            }else if(o==ChoicePayType){
                switch(position){
                    case 0:
                        sale.setPayType("0");
                        PayMode="现金";
                        //保存订单数据到本地数据库
                        isSave=dbData.updateSaleInfo(OperationID,StationID,SaleID,sale,SaleDetail_LIST,scanHistories);
                        if(isSave){
                            btn_submit.setEnabled(true);
                            cb_rubberTube.setEnabled(false);
                            rg_installType.setEnabled(false);
                            rb_c.setEnabled(false);
                            rb_s.setEnabled(false);
                            Toast.makeText(DeliveryActivity.this, "订单保存成功", Toast.LENGTH_SHORT).show();
                            details=dbData.GetSaleDetailByP(OperationID,StationID,SaleID);
                            int size=details.size();
                            for (int i = 0; i < size; i++) {
                                if(details.get(i).getIsEx()!=0){
                                    if(details.get(i).getIsEx()==2){
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                    }else {
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getPlanSendNum(), details.get(i).getQPPrice() + "\n"));
                                    }
                                }else{
                                    msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                }
                            }
                            msg.append(PrintUtils.printTwoData("交易金额",AllPrice+"\n"));
                            /***     获取发票信息                                   /*/
//                            Url=getUrl();
                            SaleCodeDialog t = new SaleCodeDialog(DeliveryActivity.this, msg.toString());
                            t.shown();
                        }

                        break;
                    case 1:
                        sale.setPayType("1");
                        PayMode="气票";
                        //保存订单数据到本地数据库
                        isSave=dbData.updateSaleInfo(OperationID,StationID,SaleID,sale,SaleDetail_LIST,scanHistories);
                        if(isSave){
                            btn_submit.setEnabled(true);
                            cb_rubberTube.setEnabled(false);
                            rg_installType.setEnabled(false);
                            rb_c.setEnabled(false);
                            rb_s.setEnabled(false);
                            Toast.makeText(DeliveryActivity.this, "订单保存成功", Toast.LENGTH_SHORT).show();
                            details=dbData.GetSaleDetailByP(OperationID,StationID,SaleID);
                            int size=details.size();
                            for (int i = 0; i < size; i++) {
                                if(details.get(i).getIsEx()!=0){
                                    if(details.get(i).getIsEx()==2){
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                    }else {
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getPlanSendNum(), details.get(i).getQPPrice() + "\n"));
                                    }
                                }else{
                                    msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                }                            }
                            msg.append(PrintUtils.printTwoData("交易金额",AllPrice+"\n"));
                            /***     获取发票信息                                   /*/
//                            Url=getUrl();
                            SaleCodeDialog t = new SaleCodeDialog(DeliveryActivity.this, msg.toString());
                            t.shown();
                        }
                        break;
                    case 2:
                        sale.setPayType("2");
                        PayMode="月结";
                        //保存订单数据到本地数据库
                        isSave=dbData.updateSaleInfo(OperationID,StationID,SaleID,sale,SaleDetail_LIST,scanHistories);
                        if(isSave){
                            rg_installType.setEnabled(false);
                            cb_rubberTube.setEnabled(false);
                            rb_c.setEnabled(false);
                            rb_s.setEnabled(false);
                            btn_submit.setEnabled(true);
                            Toast.makeText(DeliveryActivity.this, "订单保存成功", Toast.LENGTH_SHORT).show();
                            details=dbData.GetSaleDetailByP(OperationID,StationID,SaleID);
                            int size=details.size();
                            for (int i = 0; i < size; i++) {
                                if(details.get(i).getIsEx()!=0){
                                    if(details.get(i).getIsEx()==2){
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                    }else {
                                        msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), "" + details.get(i).getPlanSendNum(), details.get(i).getQPPrice() + "\n"));
                                    }
                                }else{
                                    msg.append(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));

                                }                            }
                            msg.append(PrintUtils.printTwoData("交易金额",AllPrice+"\n"));
                            /***     获取发票信息                                   /*/
//                            Url=getUrl();
                            SaleCodeDialog t = new SaleCodeDialog(DeliveryActivity.this, msg.toString());
                            t.shown();

                        }
                        break;
                }
            }else{
                DeliveryActivity.this.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 整理交易detail表详情[保存订单信息时使用]
     */
    private void SortSaleDetail(Sale sale) {
        SaleDetail_LIST = new ArrayList<com.hsic.bean.SaleDetail>();
        List<String> strings=new ArrayList<>();
        strings=dbData.GetSaleDetail(OperationID,StationID,SaleID);
        int typeSize=strings.size();
        int fullSize = FullList.size();
        int  EmptySize = EmptyList.size();
        if(typeSize>0){
            for(int a=0;a<typeSize;a++){
                String type=strings.get(a);
                String typeName="";
                int count=0;
                SaleDetail saleDetail = new SaleDetail();
                scanHistories=new ArrayList<ScanHistory>();
                /**
                 * 设置ScanHistory
                 */
                for (int f = 0; f < fullSize; f++) {
                    ScanHistory sh = new ScanHistory();
                    sh.setSaleID(SaleID);
                    sh.setUseRegCode(FullList.get(f).getUserRegionCode());
                    sh.setTypeFlag("09");//
                    sh.setQPType(FullList.get(f).getQpType());
                    scanHistories.add(sh);
                    if(type.equals(FullList.get(f).getQpType())){
                        typeName=FullList.get(f).getQpName();
                        count=count+1;
                    }
                }
                /**
                 * 设置saleDetail
                 */
                saleDetail.setQPType(type);
                saleDetail.setQPName(typeName);
                saleDetail.setSendNum(count);
                count=0;
                for (int e = 0; e < EmptySize; e++) {
                    ScanHistory sh = new ScanHistory();
                    sh.setCustomerID(UserID);
                    sh.setSaleID(SaleID);
                    sh.setUseRegCode(EmptyList.get(e).getUserRegionCode());
                    sh.setTypeFlag("01");
                    sh.setQPType(EmptyList.get(e).getQpType());
                    scanHistories.add(sh);
                    if(type.equals(EmptyList.get(e).getQpType())){
                        count=count+1;
                    }
                }
                /**
                 * 设置saleDetail
                 */
                saleDetail.setReceiveNum(count);
                SaleDetail_LIST.add(saleDetail);
            }

        }
        /**
         * 赠送橡皮管
         */
        if(ISNeedZS.equals("1")){
            if(cb_rubberTube.isChecked()){
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setIsEx(2);
                saleDetail.setSendNum(1);
                saleDetail.setReceiveNum(1);
                SaleDetail_LIST.add(saleDetail);
                sale.setISZS("1");
            }else{
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setIsEx(2);
                saleDetail.setSendNum(0);
                saleDetail.setReceiveNum(0);
                SaleDetail_LIST.add(saleDetail);
                sale.setISZS("0");
            }
        }
        /***
         * 更新运费
         */
        details=dbData.GetSaleDetailByP(OperationID,StationID,SaleID);
        int d=details.size();
        for(int h=0;h<d;h++){
            int isEX=details.get(h).getIsEx();
            if(isEX==1){
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setIsEx(1);
                saleDetail.setSendNum(details.get(h).getPlanSendNum());
                saleDetail.setReceiveNum(details.get(h).getPlanSendNum());
                SaleDetail_LIST.add(saleDetail);
            }
        }
    }

    @Override
    public void UpLoadSaleTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }else{
            btn_submit.setEnabled(true);
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }

    @Override
    public void GetBillTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            Url=tag.getRespMsg();
        }else{
            Url="";
        }
        SaleCodeDialog t = new SaleCodeDialog(DeliveryActivity.this, msg.toString());
        t.shown();
    }

    @Override
    public void UpLoadHistoryTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            new AlertView("提示", "订单信息上传成功", null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }else{
            btn_submit.setEnabled(true);
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

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
        private Button btn_sure,btn_cansel;
        public SaleCodeDialog(final Context context, String msg){
            this.context=context;
            this.msg=msg;
            mGoodsDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.sale_info_dialog_layout, null);
            info= root.findViewById(R.id.saleinto);
            info.setText("");
            btn_sure=root.findViewById(R.id.btn_print);
            btn_cansel= root.findViewById(R.id.btn_cansel);
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
            mGoodsDialog.setOnKeyListener(new DialogInterface.OnKeyListener(){

                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        mGoodsDialog.dismiss();
                    }
                    return false;
                }
            });
        }
        public  void shown(){
            boolean isDone=false;
            isDone=dbData.isDone(OperationID,StationID,SaleID);
            if(isDone){
                rg_installType.setEnabled(false);
                cb_rubberTube.setEnabled(false);
                rb_c.setEnabled(false);
                rb_s.setEnabled(false);
                btn_submit.setEnabled(true);
                btn_search.setEnabled(false);
                btn_readQP.setEnabled(false);
                btn_checkLast.setEnabled(false);
                btn_checkNext.setEnabled(false);
            }
            info.setText(msg);
            mGoodsDialog.show();
        }
        public void dimiss(){
            mGoodsDialog.dismiss();
        }
    }
    /**
     * 打印交易信息
     */
    private void print(){
        int pCount = 2;
        ArrayList<byte[]> printBytes = new ArrayList<byte[]>();
        for(int a=0;a<pCount;a++){
            printBytes.add(GPrinterCommand.reset);
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold);
            printBytes.add(GPrinterCommand.LINE_SPACING_DEFAULT);
            printBytes.add(GPrinterCommand.ALIGN_CENTER);
            printBytes.add(PrintUtils.str2Byte(getBasicInfo.getCompanyName() + "\n"));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte("销售收据\n"));
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold_cancel);
            printBytes.add(GPrinterCommand.NORMAL);
            printBytes.add(GPrinterCommand.ALIGN_LEFT);
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("销售单号", SaleID + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户编号", UserID + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("姓名",userName + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户类型", userType + "\n")));
            if(CustomerType.equals("CT03")){
                if(CustomerCardID!=null){
                    if(CustomerCardID.contains("|")){
                        String[] tem=CustomerCardID.split("|");
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户卡号", tem[0] + "\n")));
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("老人卡号", tem[1] + "\n")));
                    }
                }


            }else{
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户卡号", CustomerCardID + "\n")));
            }
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("联系电话", phone + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("来电电话", callingTphone + "\n")));
            printBytes.add(PrintUtils.str2Byte(address + "\n"));//客户地址
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold);
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData("项目", "数量", "金额\n")));
            int size = details.size();
            for (int i = 0; i < size; i++) {
                if(details.get(i).getIsEx()!=0){
                    if(details.get(i).getIsEx()==2){
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n")));

                    }else{
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getPlanSendNum(), details.get(i).getQPPrice() + "\n")));

                    }

                }else{
                    printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n")));

                }
            }
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold_cancel);
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("合计", AllPrice + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("支付方式", PayMode + "\n")));
            String EmptyNo = "";
            EmptyNo = sale.getReceiveQP();
            if (!EmptyNo.equals("")) {
                printBytes.add(PrintUtils.str2Byte("空瓶\n"));
                if (EmptyNo.contains(",")) {
                    String[] tmp = EmptyNo.split(",");
                    int length = tmp.length;
                    for (int i = 0; i < length; i++) {
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("", tmp[i] + "\n")));
                    }
                } else {
                    printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("", EmptyNo + "\n")));
                }
            }
            String FullNo = "";
            FullNo = sale.getSendQP();
            if (!FullNo.equals("")) {
                printBytes.add(PrintUtils.str2Byte("满瓶\n"));
                if (FullNo.contains(",")) {
                    String[] tmp = FullNo.split(",");
                    int length = tmp.length;
                    for (int i = 0; i < length; i++) {
                        printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("", tmp[i] + "\n")));
                    }
                } else {
                    printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("", FullNo + "\n")));
                }
            }

            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("配送日期", sale.getFinishTime() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("送瓶员", getBasicInfo.getOperationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("送气热线："+getBasicInfo.getCompanyPhone(), "" + "\n")));
            if(installType.equals("0")){
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("接装类型", "自装" + "\n")));
            }else{
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("接装类型", "公司装" + "\n")));
            }
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            /***  打印安检信息  ****/
            String inspectionInfo=dbData.getInspectionStr(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),SaleID,getBasicInfo.getOperationName());
            printBytes.add(PrintUtils.str2Byte(inspectionInfo));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户签名", "" + "\n")));
            printBytes.add(PrintUtils.str2Byte("\n\n"));
            if(!Url.equals("")){
                Bitmap bitmap = Barcode.QRCode(Url, 400, 400);
                PrintPic printPic = PrintPic.getInstance();
                printPic.init(bitmap);
                if (null != bitmap) {
                    if (bitmap.isRecycled()) {
                        bitmap = null;
                    } else {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
                byte[] bytes = printPic.printDraw();
                printBytes.add(bytes);
                printBytes.add(GPrinterCommand.ALIGN_CENTER);
//                printBytes.add(PrintUtils.str2Byte("电子发票二维码\n"));
                printBytes.add(PrintUtils.str2Byte("电子发票二维码\n"));
            }else{
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("如需发票，一次收据换取", "" + "\n")));
            }
            printBytes.add(PrintUtils.str2Byte("\n\n\n\n\n"));
        }
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }
    /***
     * 上传订单之前拍照提醒
     */
       /*
    保存照片
     */
    public void  getImagePath(String filePath,String employee, String checkSaleid, String user,String format) {
       String relationID=getBasicInfo.getDeviceID() + "e"+getBasicInfo.getOperationID()+"s" + SaleID;
        if (checkSaleid != null) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                String path = PathUtil.getImagePath();
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                String ImageFileName=relationID+"_" +format + "_" + user +"sign"+ ".jpg";
                File file2;
                file2 = new File(file1.getPath(), ImageFileName);
                PictureHelper.compressPicture(file.getAbsolutePath(),
                        file2.getAbsolutePath(), 720, 1280);
                if (file.exists()) {
                    file.delete();
                }
                File fileDir = new File(
                        Environment.getExternalStorageDirectory(), "/photoes/");
                if (fileDir.exists()) {
                    fileDir.delete();
                }
                String FileName=PathUtil.getFilePath();
                dbData.InsertXJAssociation(employee, checkSaleid, ImageFileName, relationID,FileName);//将照片信息插入到数据表中
            }
        }
    }
    @SuppressLint("SimpleDateFormat")
    public void takePhotoes(String user, String checkSaleid) {
        filePath = Environment.getExternalStorageDirectory() + "/photoes/"
                + getBasicInfo.getDeviceID() + "s" + ".jpg";
        File file = new File(filePath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory(),
                "/photoes/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 保存图片的位置
        startActivityForResult(intent, 10);
    }

    /**
     * 是否拍照
     */
    protected void IsImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryActivity.this);
        builder.setMessage("提交前是否需要拍照,订单一旦提交将不能更改");
        builder.setTitle("提示");
        builder.setIcon(R.drawable.hsic);
        builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                takePhotoes(UserID, SaleID);
            }
        });

        builder.setNegativeButton("提交", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                try {
                    /**
                     * 上传必须拍照
                     */
                    if(!isWrite()){
                        Toast.makeText(getApplicationContext(), "请先拍照",
                                Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    btn_submit.setEnabled(false);
                    boolean ret=false;
                    ret=dbData.isSaleUpLoad(OperationID,StationID,SaleID);//订单信息上传成功
                    if(ret){
                        ret=dbData.isUpLoadInspection(OperationID,StationID,SaleID);//安检信息上传成功
                        if(ret){
                            ret=dbData.isFileUpLoad(OperationID,SaleID);//存在关联文件未上传
                            if(!ret){
//                                btn_submit.setEnabled(true);
                                new AlertView("提示","订单已上传成功", null, new String[]{"确定"},
                                        null, DeliveryActivity.this, AlertView.Style.Alert, DeliveryActivity.this)
                                        .show();
                            }else{
                                new UpLoadHistoryTask(DeliveryActivity.this,DeliveryActivity.this).execute();
                            }
                        }else{
                            new UpLoadHistoryTask(DeliveryActivity.this,DeliveryActivity.this).execute();
                        }
                    }else{
                        new UploadSaleTask(DeliveryActivity.this,DeliveryActivity.this).execute(SaleID,UserID);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    btn_submit.setEnabled(true);
                }
            }
        });
        builder.create().show();
    }

    /**
     * 必须拍照
     * @return
     */
    public boolean isWrite(){
        int picCount=0;
        boolean ret=false;
        String filePath = PathUtil.getImagePath();
        File file = new File(filePath);
        String[] paths = file.list();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (path.contains(SaleID)) {
                    if(path.contains("sign")){
                        picCount++;//照片数量
                        ret=true;
                    }

                }
            }
        } else {
            ret=false;
            Toast.makeText(getApplicationContext(), "请先拍照",
                    Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    /**
     * 生成发票二维码
     * @return
     */
    public String getUrl(){
        String ret="";
        try{
            String url= "SaleID=" + SaleID + "&money=" +AllPrice + "&jy=hsic8888";
            DESEncrypt des = new DESEncrypt();// 实例化一个对像
            des.getKey("63333045");// 生成密匙P
            String strEnc1 = des.getEncString(url);// 加密字符串,返回String的密文
            ret= "http://www.qpyun.net/FXRQBill/ApplyBill.html?"+strEnc1;
            dbData.updateURL(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),SaleID,ret);
        }catch(Exception ex){
           return "";
        }
        return  ret;
    }

    /**
     * tab切换框
     * @param s
     * @return
     */
    public View composeLayout(String s) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
//        ImageView iv = new ImageView(this);
//        iv.setImageResource(i);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(60,10, 0, 0);
//        layout.addView(iv, lp);
        TextView tv = new TextView(this);
        tv.setBackgroundResource(R.drawable.bg_btn_selector);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setText(s);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setPadding(0, 10, 0, 10);;
        layout.addView(tv, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return layout;
    }
    /**
     *新增商品
     */
    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cansel:
                    editDialog.dismiss();
                    break;
                case R.id.btn_sure://保存数据
                   String newPrice=edt_quailty.getText().toString();
                    float t=Float.parseFloat(newPrice);
                    if(t>0){

//                        if(String.format("%.2f",t).equals(modifyPrice.getQPPrice())){
//
//                        }else{
                            //更新价格
                            //刷新页面
                        newPrice=String.format("%.2f",t);
                            boolean ret=false;
                            ret=dbData.updateGoodsPrice(SaleID,modifyPrice.getQPType(),newPrice);
                            if(ret){
                                ret=false;
                                ret=dbData.updateALLPrice(SaleID);
                                if(ret){
                                    goodsPrice=dbData.getGoodsPrice(SaleID);
                                    lv_goodprice.setAdapter(new GoodsPriceAdapter(DeliveryActivity.this,goodsPrice));;
                                    AllPrice=dbData.getQPSalePrice(SaleID);
                                    txt_price.setText("交易金额："+AllPrice);
                                    editDialog.dismiss();
                                }
                            }
//                        }


                    }else{
                        Toast.makeText(DeliveryActivity.this, "输入的新价格必须大于0", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private void InnitEditDialog() {
        editDialog = new Dialog(this, R.style.my_dialog);
        editDialog.setCancelable(true);
        root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.spinner_goods_layout, null);
        btn_cansel= root.findViewById(R.id.btn_cansel);
        btn_cansel.setText("取消");
        btn_sure= root.findViewById(R.id.btn_sure);
        btn_sure.setText("确认");
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        tv_pre = root.findViewById(R.id.tv_pre);
        tv_pre.setVisibility(View.VISIBLE);
        tv_pre.setText("商品");
        tv_number= root.findViewById(R.id.tv_number);
        tv_number.setVisibility(View.VISIBLE);
        tv_number.setText("价格");
        edt_brand = root.findViewById(R.id.edt_brand);
        edt_brand.setEnabled(false);
        edt_quailty = root.findViewById(R.id.edt_quailty);
        editDialog.setContentView(root);
        Window dialogWindow = editDialog.getWindow();
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
        edt_quailty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) edt_quailty.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

}
