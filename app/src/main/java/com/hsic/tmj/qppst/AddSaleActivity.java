package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.adapter.AddSaleGoodsListAdapter;
import com.hsic.adapter.GoodsPriceAdapter;
import com.hsic.bean.CustomerInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Price_QPQT;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleAll;
import com.hsic.bean.SaleDetail;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplAddNewSale;
import com.hsic.listener.ImplCustomerLogin;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.task.AddNewSaleTask;
import com.hsic.task.CustomerLoginTask;

import java.util.ArrayList;
import java.util.List;

public class AddSaleActivity extends AppCompatActivity  implements ImplCustomerLogin,ImplAddNewSale {
    private TextView  txt_userID,txt_userName,txt_telephone,txt_userAddress;
    private ListView lv_goodsInfo;
    private Button btn_addSale;
    /**
     *新增订单
     */
    private Dialog editDialog;//
    private LinearLayout root;
    private TextView tv_pre,edt_brand,tv_number,edt_quailty;
    private EditText edt_GoodsCounts;
    private Button btn_cansel,btn_sure;
    private Price_QPQT addGoods;//
    private List<Price_QPQT> addGoodsList;
    private String UserID;
    CustomerInfo customerInfo;
    GetBasicInfo getBasicInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sale);
        innitView();
        InnitEditDialog();
        Intent i = getIntent();
        UserID = i.getStringExtra("UserID");
        getBasicInfo=new GetBasicInfo(this);
        new CustomerLoginTask(this,this,UserID).execute();
    }
    private void innitView(){
        txt_userID=this.findViewById(R.id.txt_userID);
        txt_userID.setText("");
        txt_userName=this.findViewById(R.id.txt_userName);
        txt_userName.setText("");
        txt_telephone=this.findViewById(R.id.txt_telephone);
        txt_telephone.setText("");
        txt_userAddress=this.findViewById(R.id.txt_userAddress);
        txt_userAddress.setText("");
        lv_goodsInfo=this.findViewById(R.id.lv_goodsInfo);
        btn_addSale=this.findViewById(R.id.btn_addSale);
        lv_goodsInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                addGoods=(Price_QPQT)adapterView.getItemAtPosition(position);
                edt_brand.setText(addGoods.getName());
                edt_quailty.setText(addGoods.getCount());
                editDialog.show();
            }
        });
        btn_addSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///确认新增
                SaleAll saleAll=new SaleAll();
                Sale sale=new Sale();
                sale.setCreateType(0);//配送员自接单
                sale.setCustomerID(UserID);
                sale.setStation(getBasicInfo.getStationID());
                sale.setEmployeeID(getBasicInfo.getOperationID());
                sale.setManagerID(getBasicInfo.getOperationID());
                sale.setCreateManID(getBasicInfo.getOperationID());
                sale.setSaleAddress(customerInfo.getCustomerAddress());
                CustomerInfo customerInfo1=new CustomerInfo();
                customerInfo1.setCustomerType(customerInfo.getCustomerType());
                saleAll.setCustomerInfo(customerInfo1);
                int size=addGoodsList.size();
                List<SaleDetail> saleDetails=new ArrayList<>();
                for(int a=0;a<size;a++){
                    String tmp=addGoodsList.get(a).getCount();
                    int count=Integer.parseInt(tmp);
                    if(count>0){
                        SaleDetail saleDetail=new SaleDetail();
                        saleDetail.setQPType(addGoodsList.get(a).getType());
                        saleDetail.setPlanSendNum(count);
                        saleDetail.setPlanReceiveNum(count);
                        saleDetails.add(saleDetail);
//                        saleDetail.setQPName(addGoodsList.get(a).getName());
                    }
                }
                sale.setSale_detail_info(saleDetails);
                saleAll.setSale(sale);
                String RequestData=JSONUtils.toJsonWithGson(saleAll);
                HsicMessage hsicMessage=new HsicMessage();
                hsicMessage.setRespMsg(RequestData);
                RequestData=JSONUtils.toJsonWithGson(hsicMessage);
                new AddNewSaleTask(AddSaleActivity.this,AddSaleActivity.this,RequestData).execute();
            }
        });
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
                    int t=Integer.parseInt(newPrice);
                    if(t>0){
                        addGoods.setCount(newPrice);//购买的气瓶数量
                        int size=addGoodsList.size();
                        for(int a=0;a<size;a++){
                            if(addGoods.getType().equals(addGoodsList.get(a).getType())){
                                addGoodsList.remove(a);
                                addGoodsList.add(a,addGoods);
                            }
                        }
                        lv_goodsInfo.setAdapter( new AddSaleGoodsListAdapter(AddSaleActivity.this,addGoodsList));
                        editDialog.dismiss();
                    }else{
                        Toast.makeText(AddSaleActivity.this, "输入的数量必须大于0", Toast.LENGTH_SHORT).show();
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
        btn_cansel = root.findViewById(R.id.btn_cansel);
        btn_cansel.setText("取消");
        btn_sure = root.findViewById(R.id.btn_sure);
        btn_sure.setText("确认");
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        tv_pre = root.findViewById(R.id.tv_pre);
        tv_pre.setVisibility(View.VISIBLE);
        tv_pre.setText("商品");
        tv_number = root.findViewById(R.id.tv_number);
        tv_number.setVisibility(View.VISIBLE);
        tv_number.setText("数量");
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

    @Override
    public void CustomerLoginTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            customerInfo= JSONUtils.toObjectWithGson(tag.getRespMsg(), CustomerInfo.class);
            addGoodsList=customerInfo.getPrice_QPQT();
            txt_userID.setText("客户编号:"+customerInfo.getCustomerID()+"["+customerInfo.getCustomerTypeName()+"]");
            txt_userName.setText("客户姓名:"+customerInfo.getCustomerName());
            txt_telephone.setText("电话:"+customerInfo.getTelphone());
            txt_userAddress.setText("客户地址:"+customerInfo.getCustomerAddress());
            int s=addGoodsList.size();
            for(int a=0;a<s;a++){
                Price_QPQT price_qpqt=addGoodsList.get(a);
                price_qpqt.setCount("0");
                addGoodsList.remove(a);
                addGoodsList.add(a,price_qpqt);
            }
            lv_goodsInfo.setAdapter( new AddSaleGoodsListAdapter(AddSaleActivity.this,addGoodsList));
        }else{
            Toast.makeText(AddSaleActivity.this, "用户信息获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void AddNewSaleTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            Toast.makeText(AddSaleActivity.this, "新增订单成功", Toast.LENGTH_SHORT).show();
            AddSaleActivity.this.finish();
        }else{
            Log.e("tag",tag.getRespMsg());
            Toast.makeText(AddSaleActivity.this, "新增订单失败", Toast.LENGTH_SHORT).show();

        }
    }
}
