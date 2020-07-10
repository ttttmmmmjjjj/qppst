package com.hsic.tmj.qppst;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.CustomerInfoAdapter;
import com.hsic.adapter.UserInfoAdapter;
import com.hsic.bean.CustomerInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserXJInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;
import com.hsic.db.DeliveryDB;
import com.hsic.db.RectifyDB;
import com.hsic.listener.ImplByAddress;
import com.hsic.listener.ImplDownLoadSearch;
import com.hsic.listener.ImplLoginByCardID;
import com.hsic.listener.ImplLoginByName;
import com.hsic.listener.ImplLoginByPhone;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.DownLoadSearchTask;
import com.hsic.task.LoginByAddressTask;
import com.hsic.task.LoginByCardIDTask;
import com.hsic.task.LoginByNameTask;
import com.hsic.task.LoginByPhoneTask;

import java.util.ArrayList;
import java.util.List;

public class UserLoginNoCardActivity extends AppCompatActivity implements ImplDownLoadSearch,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener,
        ImplLoginByPhone,ImplByAddress,ImplLoginByCardID,ImplLoginByName {
    private EditText edt_cardID,edt_telephone,edt_userName,edt_address;
    private Button btn_cardID,btn_telephone,btn_userName,btn_address,btn_login;
    private TextView txt_info;
    private String  tag;
    DeliveryDB deliveryDB;
    AJDB ajdb;
    RectifyDB rectifyDB;
    UserXJInfo userXJInfo;
    CustomerInfo customerInfo;
    GetBasicInfo getBasicInfo;
    StringBuilder msg;
    String UserID,TypeClass ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_no_card);
        Intent i=getIntent();//上门配送：1   安检：2  (区分二维码扫描时使用) 3 整改
        tag=i.getStringExtra("LR");
        deliveryDB=new DeliveryDB(this);
        ajdb=new AJDB(this);
        rectifyDB=new RectifyDB(this);
        getBasicInfo=new GetBasicInfo(this);
        innitView();

    }
    private void  innitView(){
        edt_cardID=this.findViewById(R.id.edt_cardID);
        edt_telephone=this.findViewById(R.id.edt_telephone);
        edt_userName=this.findViewById(R.id.edt_userName);
        edt_address=this.findViewById(R.id.edt_address);
        txt_info=this.findViewById(R.id.txt_info);
        txt_info.setText("");
        btn_cardID=this.findViewById(R.id.btn_cardID);
        if(tag.equals("1")){
            edt_cardID.setVisibility(View.GONE);
            btn_cardID.setVisibility(View.GONE);
        }
        if(tag.equals("12")||tag.equals("11")){
            edt_cardID.setVisibility(View.GONE);
            btn_cardID.setVisibility(View.GONE);
        }
        btn_cardID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardID="";
                cardID=edt_cardID.getText().toString();
                cardID=cardID.toUpperCase();
                if(TextUtils.isEmpty(cardID)){
                    Toast.makeText(getBaseContext(),"请输入客户卡号",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tag.equals("1")){
                    //配送
                    mDatas=new ArrayList<>();
                    mDatas=deliveryDB.getInfoByCustom(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),cardID);
                    if(mDatas.size()>0){
                        ShowDialog("s");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else if(tag.equals("2")){
                    //安检
                    new LoginByCardIDTask(UserLoginNoCardActivity.this,UserLoginNoCardActivity.this).execute(cardID);
                }else{
                    //整改
                    mDatas=new ArrayList<>();
                    mDatas=rectifyDB.getInfoByCustom(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),cardID);
                    if(mDatas.size()>0){
                        ShowDialog("u");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        btn_telephone=this.findViewById(R.id.btn_telephone);
        btn_telephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telephone="";
                telephone=edt_telephone.getText().toString();
                if(TextUtils.isEmpty(telephone)){
                    Toast.makeText(getBaseContext(),"请输入电话号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tag.equals("1")){
                    //配送
                    mDatas=new ArrayList<>();
                    mDatas=deliveryDB.getInfoByTelephone(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),telephone);
                    if(mDatas.size()>0){
                        ShowDialog("s");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else if(tag.equals("11")||tag.equals("12")||tag.equals("2")){
                    //修改用户登录信息查询
                    new LoginByPhoneTask(UserLoginNoCardActivity.this,UserLoginNoCardActivity.this).execute(telephone);
                } else{
                    //整改
                    mDatas=new ArrayList<>();
                    mDatas=rectifyDB.getInfoByTelephone(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),telephone);
                    if(mDatas.size()>0){
                        ShowDialog("u");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        btn_userName=this.findViewById(R.id.btn_userName);
        btn_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName="";
                userName=edt_userName.getText().toString();
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(getBaseContext(),"请输入客户姓名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tag.equals("1")){
                    //配送
                    mDatas=new ArrayList<>();
                    mDatas=deliveryDB.getInfoByUserName(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userName);
                    if(mDatas.size()>0){
                        ShowDialog("s");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else if(tag.equals("11")||tag.equals("12")||tag.equals("2")){
                    //修改用户登录信息查询
                    new LoginByNameTask(UserLoginNoCardActivity.this,UserLoginNoCardActivity.this).execute(userName,getBasicInfo.getStationID());
                } else{
                    //整改
                    mDatas=new ArrayList<>();
                    mDatas=rectifyDB.getInfoByUserName(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userName);
                    if(mDatas.size()>0){
                        ShowDialog("u");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        btn_address=this.findViewById(R.id.btn_address);
        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address="";
                address=edt_address.getText().toString();
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(getBaseContext(),"请输入客户地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tag.equals("1")){
                    //配送
                    mDatas=new ArrayList<>();
                    mDatas=deliveryDB.getInfoByAddress(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),address);
                    if(mDatas.size()>0){
                        ShowDialog("s");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else if(tag.equals("11")||tag.equals("12")||tag.equals("2")){
                    //修改用户登录信息查询
                    new LoginByAddressTask(UserLoginNoCardActivity.this,UserLoginNoCardActivity.this).execute(address,getBasicInfo.getStationID());
                }else{
                    //整改
                    mDatas=new ArrayList<>();
                    mDatas=rectifyDB.getInfoByAddress(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),address);
                    if(mDatas.size()>0){
                        ShowDialog("u");
                    }else{
                        Toast.makeText(getBaseContext(),"没有查询到数据",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
        btn_login=this.findViewById(R.id.btn_login);
        if(tag.equals("1")){
            btn_login.setText("新增订单");
        }else if(tag.equals("2")){
            btn_login.setText("上门安检");
        }else if(tag.equals("11")){
            //修改用户登录信息查询
            btn_login.setText("修改用户");
        } else if(tag.equals("12")){
            btn_login.setText("用户退瓶");
        } else{
            btn_login.setText("上门整改");
        }
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag.equals("1")){
                    //配送
                    String info=txt_info.getText().toString();
                    if(TextUtils.isEmpty(info)){
                        return ;
                    }
                    boolean userIsExist=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserID);
                    if(userIsExist){
                        Intent i=new Intent(UserLoginNoCardActivity.this,DeliveryActivity.class);
                        i.putExtra("UserID",UserID);
                        i.putExtra("SaleID","");
                        startActivity(i);
                        UserLoginNoCardActivity. this.finish();
                    }else{
                        Intent i=new Intent(UserLoginNoCardActivity.this,AddSaleActivity.class);
                        i.putExtra("UserID",UserID);
                        i.putExtra("SaleID","");
                        startActivity(i);
                        UserLoginNoCardActivity. this.finish();
                    }

                }else if(tag.equals("2")){
                    //安检
                    new DownLoadSearchTask(UserLoginNoCardActivity.this,UserLoginNoCardActivity.this).execute(UserID);
                }else if(tag.equals("11")){
                    //修改用户
                    Intent i=new Intent(UserLoginNoCardActivity.this,ModifyUserInfoActivity.class);
                    i.putExtra("cus", customerInfo);
                    startActivity(i);
                    UserLoginNoCardActivity. this.finish();
                }else if(tag.equals("12")){
                    //用户退瓶
                    Intent i=new Intent(UserLoginNoCardActivity.this,ReturnBottleActivity.class);
                    i.putExtra("UserID",UserID);
                    i.putExtra("SaleID","");
                    startActivity(i);
                    UserLoginNoCardActivity. this.finish();
                } else{
                    //整改
                    boolean userIsExist=deliveryDB.userIsRectify(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserID);
                    if(userIsExist){
                    Intent i=new Intent(UserLoginNoCardActivity.this,RectiftyActivity.class);
                    i.putExtra("UserID",UserID);
                    i.putExtra("TypeClass",TypeClass);
                    startActivity(i);
                    UserLoginNoCardActivity. this.finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "该用户无需要整改的订单",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    List<UserXJInfo> mDatas;
    public void ShowDialog(String tag) {
        Context context = UserLoginNoCardActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.formcommonlist, null);
        ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
        TextView Lable = (TextView) layout.findViewById(R.id.label);
        Lable.setText("请选择用户");
        UserInfoAdapter adapter = new UserInfoAdapter(context, mDatas,tag);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                txt_info.setText("");
                userXJInfo=mDatas.get(positon);
                UserID=userXJInfo.getUserid();
                TypeClass=userXJInfo.getTypeClass();
                txt_info.append("用户编号："+userXJInfo.getUserid()+"\n\n");
                txt_info.append("用户姓名："+userXJInfo.getUsername()+"\n\n");
                txt_info.append("用户类型："+userXJInfo.getCustomerTypeName()+"\n\n");
                txt_info.append("用户电话："+userXJInfo.getTelephone()+"\n\n");
                txt_info.append("用户地址："+userXJInfo.getDeliveraddress()+"\n\n");
                alertDialog.dismiss();
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }
    List<CustomerInfo> customerInfos;
    public void ShowDialog2(String tag) {
        Context context = UserLoginNoCardActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.formcommonlist, null);
        ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
        TextView Lable = (TextView) layout.findViewById(R.id.label);
        Lable.setText("请选择用户");
        CustomerInfoAdapter adapter = new CustomerInfoAdapter(context, customerInfos,tag);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                txt_info.setText("");
                customerInfo=customerInfos.get(positon);
                UserID=customerInfo.getCustomerID();
                TypeClass=customerInfo.getTypeClass();
//                txt_info.append("送气号："+userXJInfo.getSaleid()+"\n");
                txt_info.append("用户编号："+customerInfo.getCustomerID()+"\n\n");
                txt_info.append("用户姓名："+customerInfo.getCustomerName()+"\n\n");
                txt_info.append("用户类型："+customerInfo.getCustomerTypeName()+"\n\n");
                txt_info.append("用户电话："+customerInfo.getTelphone()+"\n\n");
                txt_info.append("用户地址："+customerInfo.getCustomerAddress()+"\n\n");
                alertDialog.dismiss();
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void DownLoadSearchTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            UserXJInfo userXJInfo= JSONUtils.toObjectWithGson(tag.getRespMsg(),UserXJInfo.class);
            if(userXJInfo!=null){
                msg = new StringBuilder();
                ajdb.InsertData(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userXJInfo);
                userXJInfo=ajdb.GetRectifyInfoByUserID(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserID);
                if(userXJInfo.getUserid()!=null){
                    String TypeClass=userXJInfo.getTypeClass();
                    Intent i=new Intent(UserLoginNoCardActivity.this,SearchByCActivity.class);
                    i.putExtra("UserID",UserID);
                    i.putExtra("TypeClass",TypeClass);
                    startActivity(i);
                    UserLoginNoCardActivity.this.finish();
                }else{
                    new AlertView("提示", "该用户已做过安检，提交安检信息即可", null, new String[]{"确定"},
                            null, this, AlertView.Style.Alert, this)
                            .show();
                }

            }else{
                new AlertView("提示", "查询不到该用户相关信息", null, new String[]{"确定"},
                        null, this, AlertView.Style.Alert, this)
                        .show();
            }
        }else{
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

    }

    @Override
    public void LoginByAddressTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
//            List<CustomerInfo>
            customerInfos=new ArrayList<CustomerInfo>();
            customerInfos= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<CustomerInfo>>() {
            }.getType());//数据来源
//            Log.e("customerInfos",JSONUtils.toJsonWithGson(customerInfos));
            if(customerInfos.size()>0){
                ShowDialog2("");
            }
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }

    @Override
    public void LoginByPhoneTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            customerInfos=new ArrayList<CustomerInfo>();
            customerInfos= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<CustomerInfo>>() {
            }.getType());//数据来源
            if(customerInfos.size()>0){
                ShowDialog2("");
            }
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }

    @Override
    public void LoginByCardIDTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            List<CustomerInfo> list1=new ArrayList<CustomerInfo>();
            CustomerInfo customerInfo=new CustomerInfo();
            customerInfo= JSONUtils.toObjectWithGson(tag.getRespMsg(), CustomerInfo.class);//数据来源
            customerInfos=new ArrayList<>();
            customerInfos.add(customerInfo);
            if(customerInfos.size()>0){
                ShowDialog2("");
            }
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }

    @Override
    public void LoginByNameTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            customerInfos=new ArrayList<CustomerInfo>();
            customerInfos= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<CustomerInfo>>() {
            }.getType());//数据来源
            if(customerInfos.size()>0){
                ShowDialog2("");
            }
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }
}
