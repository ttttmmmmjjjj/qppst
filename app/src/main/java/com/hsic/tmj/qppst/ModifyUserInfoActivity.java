package com.hsic.tmj.qppst;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.bean.CustomerAddressInfo;
import com.hsic.bean.CustomerInfo;
import com.hsic.bean.CustomerTypeInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.StreetInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplUpdateCustomer;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.RegisterCustomerTask;
import com.hsic.task.UpdateCustomerTask;
import com.hsic.task.UpdateSaleTask;
import com.hsic.tmj.builder.OptionsPickerBuilder;
import com.hsic.tmj.interfaces.IPickerViewData;
import com.hsic.tmj.listener.OnOptionsSelectChangeListener;
import com.hsic.tmj.listener.OnOptionsSelectListener;
import com.hsic.tmj.pickerview.OptionsPickerView;
import com.hsic.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyUserInfoActivity extends AppCompatActivity implements ImplUpdateCustomer,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    TextView txt_userName,txt_userphone,txt_userIDCard,txt_userAddress,txt_userType,
            txt_userQX,txt_userDistance,txt_userFloor;
    EditText edt_userName,edt_userphone,edt_userIDCard,edt_userAddress,edt_userQX,
            edt_userType,edt_userDistance,edt_userFloor;
    Button btn_submit;
    DeliveryDB deliveryDB;
    private OptionsPickerView pvOptions,userTypeOptions,floorOptions,distanceOptions;
    /**             地区街道 信息          **/
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    /**               用户类型                   **/
    private ArrayList<ProvinceBean> userTypeOptionsItems = new ArrayList<>();
    /**               楼层                  **/
    private ArrayList<ProvinceBean> floorOptionsItems = new ArrayList<>();
    /**               距离                  **/
    private ArrayList<ProvinceBean> distanceOptionsItems = new ArrayList<>();

    private String areaCode ,userType,floorInfo,distanceInfo;

    private String userName,userPhone,userCardID,userAddress,QuCode,JieCode;
    GetBasicInfo getBasicInfo;
    CustomerInfo customerInfo;
    AlertView s,f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_info);
        innintView();
        deliveryDB=new DeliveryDB(this);
        getBasicInfo=new GetBasicInfo(this);
        getOptionData();
        initOptionPicker();
        Intent intent = getIntent();
        customerInfo = (CustomerInfo) intent.getSerializableExtra("cus");
        innnitData();
        s= new AlertView("提示", "修改用户成功", null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);
    }
    private void  innintView(){
        txt_userName=this.findViewById(R.id.txt_userName);
        txt_userphone=this.findViewById(R.id.txt_userphone);
        txt_userIDCard=this.findViewById(R.id.txt_userIDCard);
        txt_userAddress=this.findViewById(R.id.txt_userAddress);
        txt_userType=this.findViewById(R.id.txt_userType);
        txt_userQX=this.findViewById(R.id.txt_userDistance);
        txt_userDistance=this.findViewById(R.id.txt_userDistance);
        txt_userFloor=this.findViewById(R.id.txt_userFloor);

        edt_userName=this.findViewById(R.id.edt_userName);
        edt_userName.setText("");
        edt_userphone=this.findViewById(R.id.edt_userphone);
        edt_userphone.setText("");
        edt_userIDCard=this.findViewById(R.id.edt_userIDCard);
        edt_userIDCard.setText("");
        edt_userAddress=this.findViewById(R.id.edt_userAddress);
        edt_userAddress.setText("");
        edt_userQX=this.findViewById(R.id.edt_userQX);
        edt_userQX.setFocusable(false);
        edt_userQX.setText("");
        edt_userQX.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                pvOptions.show();
            }
        });

        edt_userType=this.findViewById(R.id.edt_userType);
        edt_userType.setFocusable(false);
        edt_userType.setText("");
        edt_userType.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                userTypeOptions.show();
            }
        });
        edt_userDistance=this.findViewById(R.id.edt_userDistance);
        edt_userDistance.setFocusable(false);
        edt_userDistance.setText("短距离");
        distanceInfo="Distance1";
        edt_userDistance.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                distanceOptions.show();
            }
        });
        edt_userFloor=this.findViewById(R.id.edt_userFloor);
        edt_userFloor.setFocusable(false);
        edt_userFloor.setText("1楼");
        floorInfo="Floor1";
        edt_userFloor.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                floorOptions.show();
            }
        });
        btn_submit=this.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userName=edt_userName.getText().toString();
                if(userName.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "用户姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                userPhone=edt_userphone.getText().toString();
                if(userPhone.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "用户电话不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                userCardID=edt_userIDCard.getText().toString();
                if(!userCardID.equals("")){
                    if ((!isMatches(userCardID))) {
                        Toast.makeText(ModifyUserInfoActivity.this, "身份证号不符合规则", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }
                userAddress=edt_userAddress.getText().toString();
                if(userAddress.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "用户地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(areaCode.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "区县街道信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(userType.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "用户类型不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(floorInfo.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "楼层信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(distanceInfo.equals("")){
                    Toast.makeText(ModifyUserInfoActivity.this, "距离信息不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userPhone.length() > 12 || userPhone.length() < 6) {

                    Toast.makeText(ModifyUserInfoActivity.this, "手机号必须大于等于6位小于等于12位", Toast.LENGTH_SHORT).show();
                    return ;
                }
                CustomerInfo customerInfo=new CustomerInfo();
                List<CustomerAddressInfo> customerAddressInfoList = new ArrayList<CustomerAddressInfo>();// 地址的集合
                CustomerAddressInfo customerAddressInfo = new CustomerAddressInfo();// 地址信息
                customerInfo = new CustomerInfo();// 用户信息
                customerInfo.setCustomerName(userName);// 用户姓名
                customerInfo.setTelphone(userPhone);// 用户联系方式
                customerInfo.setIdentityID(userCardID);// 用户身份证号码
                customerInfo.setStation(getBasicInfo.getStationID());// 用户所属站点号
                customerInfo.setCustomerType(userType);// 设置客户类型
                customerInfo.setBelongPeisong(getBasicInfo.getOperationID());// 设置绑定的员工号
                try {
                    customerInfo.setPassword(MD5Util.getFileMD5String("111111"));// 用户的密码
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // 用户的密码
                customerAddressInfo.setAddress(userAddress);// 用户地址
                customerAddressInfo.setDistanceType(distanceInfo);// 用户距离
                customerAddressInfo.setFloorType(floorInfo);// 用户楼层
                customerAddressInfo.setAreaCode(areaCode);
                customerAddressInfoList.add(customerAddressInfo);
                customerInfo.setCustomerAddressInfo(customerAddressInfoList);
                String requestString = JSONUtils.toJsonWithGson(customerInfo);
                // 把这些数据变换成公司通信类
                HsicMessage hiscMessage = new HsicMessage();
                hiscMessage.setRespMsg(requestString);// 给通信类赋值
                // 把通信类转换成json
                String requestData = JSONUtils.toJsonWithGson(hiscMessage);
                new UpdateCustomerTask(ModifyUserInfoActivity.this,ModifyUserInfoActivity.this).
                        execute(requestData);

            }
        });
    }
    private void innnitData(){
        edt_userName.setText(customerInfo.getCustomerName());
        userName=customerInfo.getCustomerName();
        userPhone=customerInfo.getTelphone();
        userCardID=customerInfo.getIdentityID();
        userAddress=customerInfo.getCustomerAddress();
        if(customerInfo.getCustomerAddressInfo().get(0).getAreaCode()!=null){
            areaCode=customerInfo.getCustomerAddressInfo().get(0).getAreaCode();
            edt_userQX.setText(deliveryDB.getQXName(areaCode));
        }else{
            areaCode="";
            edt_userQX.setText("");
        }
        userType=customerInfo.getCustomerType();
        edt_userphone.setText(customerInfo.getTelphone());
        edt_userIDCard.setText(customerInfo.getIdentityID());
        edt_userAddress.setText(customerInfo.getCustomerAddress());
        edt_userType.setText(deliveryDB.getUsetrTypeName(userType));
        if(customerInfo.getCustomerAddressInfo().get(0).getDistanceType()!=null){
            distanceInfo=customerInfo.getCustomerAddressInfo().get(0).getDistanceType();
            edt_userDistance.setText(getDistanceName(distanceInfo));
        }else{
            edt_userDistance.setText("短距离");
            distanceInfo="Distance1";
        }
        if(customerInfo.getCustomerAddressInfo().get(0).getFloorType()!=null){
            floorInfo=customerInfo.getCustomerAddressInfo().get(0).getFloorType();
            edt_userFloor.setText(getFloorName(floorInfo));
        }else{
            edt_userFloor.setText("1楼");
            floorInfo="Floor1";
        }




    }
    private void initOptionPicker() {//条件选择器初始化
        /******************              地区街道                  ********************/
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                edt_userQX.setText(options1Items.get(options1).getPickerViewText()+"区"+
                        options2Items.get(options1).get(options2));
                String temp=edt_userQX.getText().toString();
                String JieName=options2Items.get(options1).get(options2);
                ProvinceBean provinceBean=new ProvinceBean();
                provinceBean=(ProvinceBean)options1Items.get(options1);
                QuCode=provinceBean.getId();
                if(!temp.equals("")){
                    areaCode= deliveryDB.getAreaCode(provinceBean.getId(),JieName);
                }else{
                    areaCode="";
                }

            }
        })
                .setTitleText("区街道选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.GRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setSubmitColor(Color.BLUE)
                .setTextColorCenter(Color.BLACK)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("区", "", "")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();
        pvOptions.setPicker(options1Items, options2Items);
        /******************          用户类型                      ********************/
        userTypeOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                edt_userType.setText(userTypeOptionsItems.get(options1).getPickerViewText());
                String temp=edt_userType.getText().toString();
                if(!temp.equals("")){
                    ProvinceBean provinceBean=new ProvinceBean();
                    provinceBean=(ProvinceBean)userTypeOptionsItems.get(options1);
                    userType=provinceBean.getId();
                }else{
                    userType="";
                }
            }
        })
                .setTitleText("用户类型选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.GRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setSubmitColor(Color.BLUE)
                .setTextColorCenter(Color.BLACK)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();
        userTypeOptions.setPicker(userTypeOptionsItems);
        /******************          楼层                     ********************/
        floorOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                edt_userFloor.setText(floorOptionsItems.get(options1).getPickerViewText());
                String temp=edt_userFloor.getText().toString();
                if(!temp.equals("")){
                    ProvinceBean provinceBean=new ProvinceBean();
                    provinceBean=(ProvinceBean)floorOptionsItems.get(options1);
                    floorInfo=provinceBean.getId();
                }else{
                    floorInfo="";
                }
            }
        })
                .setTitleText("楼层选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.GRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setSubmitColor(Color.BLUE)
                .setTextColorCenter(Color.BLACK)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();
        floorOptions.setPicker(floorOptionsItems);
        /******************          距离                     ********************/
        distanceOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                edt_userDistance.setText(distanceOptionsItems.get(options1).getPickerViewText());
                String temp=edt_userDistance.getText().toString();
                if(!temp.equals("")){
                    ProvinceBean provinceBean=new ProvinceBean();
                    provinceBean=(ProvinceBean)distanceOptionsItems.get(options1);
                    distanceInfo=provinceBean.getId();
                }else{
                    distanceInfo="";
                }
            }
        })
                .setTitleText("距离选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.GRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setSubmitColor(Color.BLUE)
                .setTextColorCenter(Color.BLACK)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("", "", "")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();
        distanceOptions.setPicker(distanceOptionsItems);
    }
    private void getOptionData() {
        /*************            区，街道          *********************/
        List<StreetInfo> options4Items = new ArrayList<>();
        options4Items=deliveryDB. getQuCode();
        int size=options4Items.size();
        for(int i=0;i<size;i++){
            StreetInfo streetInfo=new StreetInfo();
            streetInfo= options4Items.get(i);
            ProvinceBean provinceBean=new ProvinceBean();
            provinceBean.setId(streetInfo.getQuCode());
            provinceBean.setName(streetInfo.getQuName());
            ArrayList<String> jieNames=  deliveryDB.getJieName(streetInfo.getQuCode());
            ArrayList<String> jieCode=deliveryDB.getJieCode(streetInfo.getQuCode());
            options1Items.add(provinceBean);
            options2Items.add(jieNames);
        }
        /*************            用户类型         *********************/
        List<CustomerTypeInfo> optionsItems = new ArrayList<>();
        optionsItems=deliveryDB.getCustomerTypeInfo();
        size =optionsItems.size();
        for(int i=0;i<size;i++){
            CustomerTypeInfo customerTypeInfo=new CustomerTypeInfo();
            customerTypeInfo= optionsItems.get(i);
            ProvinceBean provinceBean=new ProvinceBean();
            provinceBean.setId(customerTypeInfo.getCustomerType());
            provinceBean.setName(customerTypeInfo.getCustomerTypeName());
            userTypeOptionsItems.add(provinceBean);
        }
        /*************            距离        *********************/
        ProvinceBean provinceBean=new ProvinceBean();
        provinceBean.setName("短距离");
        provinceBean.setId("Distance1");
        distanceOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("中距离");
        provinceBean.setId("Distance2");
        distanceOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("长距离");
        provinceBean.setId("Distance3");
        distanceOptionsItems.add(provinceBean);
        /*************            楼层        *********************/
        provinceBean=new ProvinceBean();
        provinceBean.setName("1楼");
        provinceBean.setId("Floor1");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("2楼");
        provinceBean.setId("Floor2");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("3楼");
        provinceBean.setId("Floor2");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("3楼");
        provinceBean.setId("Floor2");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("4楼");
        provinceBean.setId("Floor4");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("5楼");
        provinceBean.setId("Floor5");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("6楼");
        provinceBean.setId("Floor6");
        floorOptionsItems.add(provinceBean);
        provinceBean=new ProvinceBean();
        provinceBean.setName("7楼");
        provinceBean.setId("Floor7");
        floorOptionsItems.add(provinceBean);
    }
    private String getDistanceName(String distanceDode){
            String ret="";
            if(distanceDode.equals("Distance1")){
                ret="短距离";
            }else if(distanceDode.equals("Distance2")){
                ret="中距离";
            }else  if(distanceDode.equals("Distance3")){
                ret="长距离";
            }else {
                 ret="";
            }
            return ret;
    }
    private String getFloorName(String distanceDode){
        String ret="";
        if(distanceDode.equals("Floor1")){
            ret="1楼";
        }else if(distanceDode.equals("Floor2")){
            ret="2楼";
        }else  if(distanceDode.equals("Floor3")){
            ret="3楼";
        }else  if(distanceDode.equals("Floor4")){
            ret="4楼";
        }else if(distanceDode.equals("Floor5")){
            ret="5楼";
        }else if(distanceDode.equals("Floor6")){
            ret="6楼";
        }else if(distanceDode.equals("Floor7")){
            ret="7楼";
        }else {
            ret="";
        }
        return ret;
    }
    @Override
    public void UpdateCustomerTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            s.show();
        }else{
            new AlertView("提示", "修改用户失败:"+tag.getRespMsg(), null, new String[]{"确定"},
                    null, ModifyUserInfoActivity.this, AlertView.Style.Alert, ModifyUserInfoActivity.this)
                    .show();
        }
    }

    // 判断身份证那是否符合规则
    public static boolean isMatches(String text) {
        String regex = "\\d{17}((\\d|X)|x)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if(o==s){
            s.dismiss();
            this.finish();
        }
    }

    public class ProvinceBean implements IPickerViewData {
        private String id;
        private String name;
        private String description;
        private String others;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getOthers() {
            return others;
        }

        public void setOthers(String others) {
            this.others = others;
        }

        //这个用来显示在PickerView上面的字符串,PickerView会通过getPickerViewText方法获取字符串显示出来。
        @Override
        public String getPickerViewText() {
            return name;
        }
    }
}
