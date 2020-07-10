package com.hsic.tmj.qppst;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.bean.StreetInfo;
import com.hsic.bluetooth.Barcode;
import com.hsic.db.DeliveryDB;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.tmj.builder.OptionsPickerBuilder;
import com.hsic.tmj.interfaces.IPickerViewData;
import com.hsic.tmj.listener.OnOptionsSelectChangeListener;
import com.hsic.tmj.listener.OnOptionsSelectListener;
import com.hsic.tmj.pickerview.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WePayActivity extends AppCompatActivity implements  com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    ImageView imageView;
    TextView txt_SaleID;
    TextView txt_title;
    private ProgressDialog dialog;

    private OptionsPickerView pvOptions;
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private List<StreetInfo> options4Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        imageView=this.findViewById(R.id.imageView);
        txt_SaleID=this.findViewById(R.id.txt_SaleID);
        txt_title=this.findViewById(R.id.txt_title);
        txt_title.setText("微信支付");
        dialog = new ProgressDialog(this);
        getOptionData();
        initOptionPicker();
        txt_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvOptions.show(); //弹出条件选择器
            }
        });

        /**
         * 付款测试
         */
//       new HttpsDemo().execute();

    }
    private void initOptionPicker() {//条件选择器初始化

        /**
         * 注意 ：如果是三级联动的数据(省市区等)，请参照 JsonDataActivity 类里面的写法。
         */

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
               String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(options2);
                        /* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*/;
//                btn_Options.setText(tx);
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
                .setLabels("区", "", "区")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String tx = options1Items.get(options1).getPickerViewText()
                                + options2Items.get(options1).get(options2);
                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                        Toast.makeText(WePayActivity.this, tx, Toast.LENGTH_SHORT).show();
//                        pvOptions.dismiss();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        /*pvOptions.setPicker(options1Items);//一级选择器*/
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
    }
    private void getOptionData() {

        DeliveryDB deliveryDB=new DeliveryDB(this);
        options4Items=deliveryDB. getQuCode();
        int size=options4Items.size();
        for(int i=0;i<size;i++){
            StreetInfo streetInfo=new StreetInfo();
            streetInfo= options4Items.get(i);
            ProvinceBean provinceBean=new ProvinceBean();
            provinceBean.setDescription(streetInfo.getQuCode());
            provinceBean.setId(Long.parseLong(streetInfo.getQuCode()));
            provinceBean.setName(streetInfo.getQuName());
            options1Items.add(provinceBean);
            ArrayList<String> jieNames=  deliveryDB.getJieName(streetInfo.getQuCode());
            options2Items.add(jieNames);
        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        this.finish();
    }

//    class HttpsDemo extends AsyncTask<Void,Void,PayMsg> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.setMessage("获取支付信息");
//            dialog.setCancelable(false);
//            dialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(PayMsg payMsg) {
//            super.onPostExecute(payMsg);
//            if(payMsg.getPayinfo()!=null&& !payMsg.getPayinfo().equals("")){
//                Bitmap bitmap= Barcode.QRCode(payMsg.getPayinfo(),400,400);
//                imageView.setImageBitmap(bitmap);
//                txt_SaleID.setText("单号："+payMsg.getReqsn()+"\n"+
//                        "交易号："+payMsg.getTrxid());
//            }else{
//                String msg= payMsg.getRetmsg();
//                new AlertView("提示", msg, null, new String[]{"确定"},
//                        null, WePayActivity.this, AlertView.Style.Alert, WePayActivity.this)
//                        .show();
//            }
//
//        }
//
//        @Override
//        protected PayMsg doInBackground(Void... voids) {
//            dialog.setCancelable(true);
//            dialog.dismiss();
//            PayMsg payMsg=new PayMsg();
//          try{
//              SybPayService service = new SybPayService();
//              String reqsn = String.valueOf(System.currentTimeMillis());//订单号
//              Map<String, String> map = service.pay(1, reqsn, "W01", "送气费用", "备注", "", "123","http://106.14.9.217/PhoneNetTest/NativeNotifyPage.aspx","","","","");
//              String strJson= JSONUtils.toJsonWithGson(map);
//              payMsg= JSONUtils.toObjectWithGson(strJson,PayMsg.class);
//
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }
//            return payMsg;
//        }
//    }
    public class ProvinceBean implements IPickerViewData {
        private long id;
        private String name;
        private String description;
        private String others;

        public long getId() {
            return id;
        }

        public void setId(long id) {
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
