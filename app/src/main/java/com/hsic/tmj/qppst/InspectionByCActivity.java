package com.hsic.tmj.qppst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.Toast;

import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserXJInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplUpLoadInspection;
import com.hsic.picture.PictureHelper;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.UpLoadInspectionTask;
import com.hsic.utils.GetOperationTime;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/3.
 */

public class InspectionByCActivity extends AppCompatActivity implements ImplUpLoadInspection,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    UserXJInfo userXJInfo;
    private TabHost mTabHost;
    private CheckBox StopSupplyType1, StopSupplyType2, StopSupplyType3,
            StopSupplyType4, StopSupplyType5, StopSupplyType6, StopSupplyType7,
            StopSupplyType8,StopSupplyType9,StopSupplyType10,StopSupplyType11,
            StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,
            UnInstallType1, UnInstallType2, UnInstallType3, UnInstallType4,
            UnInstallType5,UnInstallType6, UnInstallType7,UnInstallType8,
            UnInstallType9, UnInstallType10, UnInstallType11, UnInstallType12,
            UnInstallType13,UnInstallType14;// S:停止供气；N:不予接装
    public static String filePath;
    String userId = "",saleId = "",deviceid = "",TypeClass="";
    GetBasicInfo getBasicInfo;
    StringBuffer InspectionItem;//【传给后台】
    String relationID;//
    DeliveryDB dbData;
    int picCount=0;
    Button btn_UpLoad,btn_sign,btn_takepic,btn_preview;
    boolean isUpdate=false;
    AlertView imageInfoDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_c_search);
        userXJInfo=new UserXJInfo();
        userXJInfo.setInspectionStatus("0");
        userXJInfo.setIsInspected("0");
        getBasicInfo=new GetBasicInfo(InspectionByCActivity.this);
        deviceid=getBasicInfo.getDeviceID();
        Intent i=getIntent();
        saleId=i.getStringExtra("SaleID");
        userId=i.getStringExtra("UserID");
        TypeClass=i.getStringExtra("TypeClass");//区分居民用户及非居民用户
        initCheckBox();
        relationID=deviceid + "e"+getBasicInfo.getOperationID()+"s" + saleId;
        dbData=new DeliveryDB(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        imageInfoDialog=  new AlertView("提示", "必须拍照", null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);
        innitData();
    }
    /**
     * 初始化checkBox
     */
    public void initCheckBox() {
        mTabHost = (TabHost) findViewById(R.id.tabhost);
        mTabHost.setup();
        final TabHost.TabSpec mTabSpec = mTabHost.newTabSpec("stop");
        TabHost.TabSpec mTabSpec2 = mTabHost.newTabSpec("UNstall");
        mTabSpec2.setIndicator("",
                getResources().getDrawable(R.drawable.u_tab_selector));
        mTabSpec2.setContent(R.id.page2);
        mTabHost.addTab(mTabSpec2);//默认显示一般隐患

        mTabSpec.setIndicator("",
                getResources().getDrawable(R.drawable.stop_tab_seletcor));
        mTabSpec.setContent(R.id.page1);
        mTabHost.addTab(mTabSpec);

        StopSupplyType1 = (CheckBox) this.findViewById(R.id.StopSupplyType1);
        StopSupplyType2 = (CheckBox) this.findViewById(R.id.StopSupplyType2);
        StopSupplyType3 = (CheckBox) this.findViewById(R.id.StopSupplyType3);
        StopSupplyType4 = (CheckBox) this.findViewById(R.id.StopSupplyType4);
        StopSupplyType5 = (CheckBox) this.findViewById(R.id.StopSupplyType5);
        StopSupplyType6 = (CheckBox) this.findViewById(R.id.StopSupplyType6);
        StopSupplyType7 = (CheckBox) this.findViewById(R.id.StopSupplyType7);
        StopSupplyType8 = (CheckBox) this.findViewById(R.id.StopSupplyType8);
        StopSupplyType9 = (CheckBox) this.findViewById(R.id.StopSupplyType9);
        StopSupplyType10 = (CheckBox) this.findViewById(R.id.StopSupplyType10);
        StopSupplyType11 = (CheckBox) this.findViewById(R.id.StopSupplyType11);
        StopSupplyType12 = (CheckBox) this.findViewById(R.id.StopSupplyType12);
        StopSupplyType13 = (CheckBox) this.findViewById(R.id.StopSupplyType13);
        StopSupplyType14 = (CheckBox) this.findViewById(R.id.StopSupplyType14);
        StopSupplyType15 = (CheckBox) this.findViewById(R.id.StopSupplyType15);
        UnInstallType1 = (CheckBox) this.findViewById(R.id.UnInstallType1);
        UnInstallType2 = (CheckBox) this.findViewById(R.id.UnInstallType2);
        UnInstallType3 = (CheckBox) this.findViewById(R.id.UnInstallType3);
        UnInstallType4 = (CheckBox) this.findViewById(R.id.UnInstallType4);
        UnInstallType5 = (CheckBox) this.findViewById(R.id.UnInstallType5);
        UnInstallType6 = (CheckBox) this.findViewById(R.id.UnInstallType6);
        UnInstallType7 = (CheckBox) this.findViewById(R.id.UnInstallType7);
        UnInstallType8 = (CheckBox) this.findViewById(R.id.UnInstallType8);
        UnInstallType9 = (CheckBox) this.findViewById(R.id.UnInstallType9);
        UnInstallType10 = (CheckBox) this.findViewById(R.id.UnInstallType10);
        UnInstallType11 = (CheckBox) this.findViewById(R.id.UnInstallType11);
        UnInstallType12 = (CheckBox) this.findViewById(R.id.UnInstallType12);
        UnInstallType13 = (CheckBox) this.findViewById(R.id.UnInstallType13);
        UnInstallType14 = (CheckBox) this.findViewById(R.id.UnInstallType14);
        if(!TypeClass.equals("1")){
            StopSupplyType1.setText(R.string.c_StopSupplyType1);
            StopSupplyType2.setText(R.string.c_StopSupplyType2);
            StopSupplyType3.setText(R.string.c_StopSupplyType3);
            StopSupplyType4.setText(R.string.c_StopSupplyType4);
            StopSupplyType5.setText(R.string.c_StopSupplyType5);
            StopSupplyType6.setText(R.string.c_StopSupplyType6);
            StopSupplyType7.setText(R.string.c_StopSupplyType7);
            StopSupplyType8.setText(R.string.c_StopSupplyType8);
            StopSupplyType9.setText(R.string.c_StopSupplyType9);
            StopSupplyType10.setText(R.string.c_StopSupplyType10);

            UnInstallType1.setText(R.string.c_UnInstallType1);
            UnInstallType2.setText(R.string.c_UnInstallType2);
            UnInstallType3.setText(R.string.c_UnInstallType3);
            UnInstallType4.setText(R.string.c_UnInstallType4);
            UnInstallType5.setText(R.string.c_UnInstallType5);
            UnInstallType6.setText(R.string.c_UnInstallType6);
            UnInstallType7.setText(R.string.c_UnInstallType7);
            UnInstallType8.setText(R.string.c_UnInstallType8);
            UnInstallType9.setText(R.string.c_UnInstallType9);
            UnInstallType10.setText(R.string.c_UnInstallType10);
            UnInstallType11.setText(R.string.c_UnInstallType11);
            UnInstallType12.setText(R.string.c_UnInstallType12);
            UnInstallType13.setText(R.string.c_UnInstallType13);
        }else{

            StopSupplyType11.setVisibility(View.VISIBLE);
            StopSupplyType12.setVisibility(View.VISIBLE);
            StopSupplyType13.setVisibility(View.VISIBLE);
            StopSupplyType14.setVisibility(View.VISIBLE);
            StopSupplyType15.setVisibility(View.VISIBLE);
            UnInstallType14.setVisibility(View.VISIBLE);

            StopSupplyType1.setText(R.string.b_StopSupplyType1);
            StopSupplyType2.setText(R.string.b_StopSupplyType2);
            StopSupplyType3.setText(R.string.b_StopSupplyType3);
            StopSupplyType4.setText(R.string.b_StopSupplyType4);
            StopSupplyType5.setText(R.string.b_StopSupplyType5);
            StopSupplyType6.setText(R.string.b_StopSupplyType6);
            StopSupplyType7.setText(R.string.b_StopSupplyType7);
            StopSupplyType8.setText(R.string.b_StopSupplyType8);
            StopSupplyType9.setText(R.string.b_StopSupplyType9);
            StopSupplyType10.setText(R.string.b_StopSupplyType10);
            StopSupplyType11.setText(R.string.b_StopSupplyType11);
            StopSupplyType12.setText(R.string.b_StopSupplyType12);
            StopSupplyType13.setText(R.string.b_StopSupplyType13);
            StopSupplyType14.setText(R.string.b_StopSupplyType14);
            StopSupplyType15.setText(R.string.b_StopSupplyType15);

            UnInstallType1.setText(R.string.b_UnInstallType1);
            UnInstallType2.setText(R.string.b_UnInstallType2);
            UnInstallType3.setText(R.string.b_UnInstallType3);
            UnInstallType4.setText(R.string.b_UnInstallType4);
            UnInstallType5.setText(R.string.b_UnInstallType5);
            UnInstallType6.setText(R.string.b_UnInstallType6);
            UnInstallType7.setText(R.string.b_UnInstallType7);
            UnInstallType8.setText(R.string.b_UnInstallType8);
            UnInstallType9.setText(R.string.b_UnInstallType9);
            UnInstallType10.setText(R.string.b_UnInstallType10);
            UnInstallType11.setText(R.string.b_UnInstallType11);
            UnInstallType12.setText(R.string.b_UnInstallType12);
            UnInstallType13.setText(R.string.b_UnInstallType13);
            UnInstallType14.setText(R.string.b_UnInstallType14);
        }

        btn_UpLoad=this.findViewById(R.id.upload);
        btn_UpLoad.setEnabled(false);//
        btn_sign=this.findViewById(R.id.sign);
        btn_takepic=this.findViewById(R.id.takepic);
        btn_preview=this.findViewById(R.id.preview);
    }
    private void innitData(){
        Map<String, String> xj = new HashMap<String, String>();
        xj=dbData.getXJ(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleId);
        if (xj.containsKey("InspectionStatus")) {
            if (xj.get("InspectionStatus").equals("1")) {
                mTabHost.setCurrentTab(0);
                if (xj.containsKey("UnInstallType1")) {
                    if (xj.get("UnInstallType1").equals("1")) {
                        UnInstallType1.setChecked(true);
                    } else {
                        UnInstallType1.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType2")) {
                    if (xj.get("UnInstallType2").equals("1")) {
                        UnInstallType2.setChecked(true);
                    } else {
                        UnInstallType2.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType3")) {
                    if (xj.get("UnInstallType3").equals("1")) {
                        UnInstallType3.setChecked(true);
                    } else {
                        UnInstallType3.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType4")) {
                    if (xj.get("UnInstallType4").equals("1")) {
                        UnInstallType4.setChecked(true);
                    } else {
                        UnInstallType4.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType5")) {
                    if (xj.get("UnInstallType5").equals("1")) {
                        UnInstallType5.setChecked(true);
                    } else {
                        UnInstallType5.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType6")) {
                    if (xj.get("UnInstallType6").equals("1")) {
                        UnInstallType6.setChecked(true);
                    } else {
                        UnInstallType6.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType7")) {
                    if (xj.get("UnInstallType7").equals("1")) {
                        UnInstallType7.setChecked(true);
                    } else {
                        UnInstallType7.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType8")) {
                    if (xj.get("UnInstallType8").equals("1")) {
                        UnInstallType8.setChecked(true);
                    } else {
                        UnInstallType8.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType9")) {
                    if (xj.get("UnInstallType9").equals("1")) {
                        UnInstallType9.setChecked(true);
                    } else {
                        UnInstallType9.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType10")) {
                    if (xj.get("UnInstallType10").equals("1")) {
                        UnInstallType10.setChecked(true);
                    } else {
                        UnInstallType10.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType11")) {
                    if (xj.get("UnInstallType11").equals("1")) {
                        UnInstallType11.setChecked(true);
                    } else {
                        UnInstallType11.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType12")) {
                    if (xj.get("UnInstallType12").equals("1")) {
                        UnInstallType12.setChecked(true);
                    } else {
                        UnInstallType12.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType13")) {
                    if (xj.get("UnInstallType13").equals("1")) {
                        UnInstallType13.setChecked(true);
                    } else {
                        UnInstallType13.setChecked(false);
                    }
                }
                if (xj.containsKey("UnInstallType14")) {
                    if (xj.get("UnInstallType14").equals("1")) {
                        UnInstallType14.setChecked(true);
                    } else {
                        UnInstallType14.setChecked(false);
                    }
                }

            } else if (xj.get("InspectionStatus").equals("2")) {
                mTabHost.setCurrentTab(1);
                if (xj.containsKey("StopSupplyType1")) {
                    if (xj.get("StopSupplyType1").equals("1")) {
                        StopSupplyType1.setChecked(true);
                    } else {
                        StopSupplyType1.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType2")) {
                    if (xj.get("StopSupplyType2").equals("1")) {
                        StopSupplyType2.setChecked(true);
                    } else {
                        StopSupplyType2.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType3")) {
                    if (xj.get("StopSupplyType3").equals("1")) {
                        StopSupplyType3.setChecked(true);
                    } else {
                        StopSupplyType3.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType4")) {
                    if (xj.get("StopSupplyType4").equals("1")) {
                        StopSupplyType4.setChecked(true);
                    } else {
                        StopSupplyType4.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType5")) {
                    if (xj.get("StopSupplyType5").equals("1")) {
                        StopSupplyType5.setChecked(true);
                    } else {
                        StopSupplyType5.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType6")) {
                    if (xj.get("StopSupplyType6").equals("1")) {
                        StopSupplyType6.setChecked(true);
                    } else {
                        StopSupplyType6.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType7")) {
                    if (xj.get("StopSupplyType7").equals("1")) {
                        StopSupplyType7.setChecked(true);
                    } else {
                        StopSupplyType7.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType8")) {
                    if (xj.get("StopSupplyType8").equals("1")) {
                        StopSupplyType8.setChecked(true);
                    } else {
                        StopSupplyType8.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType9")) {
                    if (xj.get("StopSupplyType9").equals("1")) {
                        StopSupplyType9.setChecked(true);
                    } else {
                        StopSupplyType9.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType10")) {
                    if (xj.get("StopSupplyType10").equals("1")) {
                        StopSupplyType10.setChecked(true);
                    } else {
                        StopSupplyType10.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType11")) {
                    if (xj.get("StopSupplyType11").equals("1")) {
                        StopSupplyType11.setChecked(true);
                    } else {
                        StopSupplyType11.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType12")) {
                    if (xj.get("StopSupplyType12").equals("1")) {
                        StopSupplyType12.setChecked(true);
                    } else {
                        StopSupplyType12.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType13")) {
                    if (xj.get("StopSupplyType13").equals("1")) {
                        StopSupplyType13.setChecked(true);
                    } else {
                        StopSupplyType13.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType14")) {
                    if (xj.get("StopSupplyType14").equals("1")) {
                        StopSupplyType14.setChecked(true);
                    } else {
                        StopSupplyType14.setChecked(false);
                    }
                }
                if (xj.containsKey("StopSupplyType15")) {
                    if (xj.get("StopSupplyType15").equals("1")) {
                        StopSupplyType15.setChecked(true);
                    } else {
                        StopSupplyType15.setChecked(false);
                    }
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(userXJInfo.getIsInspected()!=null){
                if(userXJInfo.getIsInspected().equals("1")){
                    picCount=0;
                    isWrite();//检查照片数量
                    if(picCount<=0){
                        imageInfoDialog.show();
                        return true;
                    }else{
                        this.finish();
                    }
                }else{
                    this.finish();
                }
            }else{
                this.finish();
            }


        } else {
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /***
     * 上传安检信息
     * @param view
     */
    public void upload(View view){
        //上传安检信息
        picCount=0;
        isWrite();//检查照片数量
        if(picCount>0){
            btn_UpLoad.setEnabled(false);
            btn_sign.setEnabled(false);//
            btn_takepic.setEnabled(false);//
            btn_preview.setEnabled(false);//
            boolean ret=false;
            ret=dbData.isUpLoadInspection(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleId);
            if(ret){
                new AlertView("提示","安检信息已上传成功", null, new String[]{"确定"},
                        null, InspectionByCActivity.this, AlertView.Style.Alert, InspectionByCActivity.this)
                        .show();
            }else{
                new UpLoadInspectionTask(InspectionByCActivity.this,InspectionByCActivity.this).execute(saleId,userId);
            }
        }else{
            btn_UpLoad.setEnabled(true);
            Toast.makeText(getApplicationContext(), "请先拍照",
                    Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 拍照保存页面
     *
     * @param view
     */
    public void click1(View view) {
        takePhotoes(userId, saleId);
    }

    /**
     * 进入预览页面
     *
     * @param view
     */
    public void click2(View view) {
        preview();
    }

    /**
     * 打印签字
     * @param view
     */
    public void click3(View view) {
//        try{
            String inspectedDate = GetOperationTime.getCurrentTime();//获取安检日期
            saveState();//保存安检状态
            getRectifyResult();//
            userXJInfo.setInspectionMan(getBasicInfo.getOperationID());
            userXJInfo.setInspectionDate(inspectedDate);
            isUpdate=dbData.updateInspectionInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleId,userXJInfo);
            update();
//        }catch(Exception ex){
//            ex.toString();
//
//        }
    }

    /**
     * 更新本地数据库安检想信息 并打印
     */
    private void update(){
        if(isUpdate){
            //更新sale表字段InspectionItem
            String itemInspection=InspectionItem.toString();
            if(!itemInspection.equals("")){
                if(itemInspection.contains(",")){
                    int l=itemInspection.length();
                    itemInspection=itemInspection.substring(0,l-1);
                }
            }else{
                itemInspection="";
            }
            dbData.InspectionItem(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleId,itemInspection);
            btn_UpLoad.setEnabled(true);
            Toast.makeText(InspectionByCActivity.this, "安检信息保存成功", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String time= TimeUtils.getTime("yyyyMMddHHmmss");
                getImagePath(filePath,getBasicInfo.getOperationID(),saleId, userId,time);

            }
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    @SuppressLint("SimpleDateFormat")
    public void takePhotoes(String user, String checkSaleid) {
        filePath = Environment.getExternalStorageDirectory() + "/photoes/"
                + deviceid + "s" + ".jpg";
        File file = new File(filePath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fileDir = new File(Environment.getExternalStorageDirectory(),
                "/photoes/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // 保存图片的位置
        startActivityForResult(intent, 1);
    }

    /**
     * 判断是否签字
     * @return
     */
    public boolean isWrite(){
        boolean ret=false;
        String filePath = PathUtil.getImagePath();
        File file = new File(filePath);
        String[] paths = file.list();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (path.contains(relationID)) {
                    picCount++;//照片数量
                }
            }
        } else {
            ret=false;
            Toast.makeText(getApplicationContext(), "请先拍照",
                    Toast.LENGTH_SHORT).show();
        }
        return ret;
    }
    public void preview() {
        int flag = 0;
        String filePath = PathUtil.getImagePath();
        File file = new File(filePath);
        String[] paths = file.list();
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (path.contains(relationID)) {
                    flag++;
                }
            }
            if (flag == 0) {
                Toast.makeText(getApplicationContext(), "请先拍照后在预览",
                        Toast.LENGTH_SHORT).show();
                return;
            } else {
                Intent intent = new Intent(this, InsepctionPreviewActivity.class);
                intent.putExtra("SaleID",saleId);
                intent.putExtra("UserID",userId);
                intent.putExtra("RelationID",relationID);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "请先拍照后在预览",
                    Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * 保存安检项同时把安检项插入数据库
     */
    private void saveState(){
        boolean UNStatus = false;
        boolean StopStatus = false;
        InspectionItem=new StringBuffer();
        if(UnInstallType1.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType1("1");
        }else{
            userXJInfo.setUnInstallType1("0");
        }
        if(UnInstallType2.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType2("1");
        }else{
            userXJInfo.setUnInstallType2("0");
        }
        if(UnInstallType3.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType3("1");
        }else{
            userXJInfo.setUnInstallType3("0");
        }
        if(UnInstallType4.isChecked()){
            userXJInfo.setUnInstallType4("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType4("0");
        }
        if(UnInstallType5.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType5("1");
        }else{
            userXJInfo.setUnInstallType5("0");
        }
        if(UnInstallType6.isChecked()){
            userXJInfo.setUnInstallType6("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType6("0");
        }
        if(UnInstallType7.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType7("1");
        }else{
            userXJInfo.setUnInstallType7("0");
        }
        if(UnInstallType8.isChecked()){
            UNStatus = true;
            userXJInfo.setUnInstallType8("1");
        }else{
            userXJInfo.setUnInstallType8("0");
        }
        if(UnInstallType9.isChecked()){
            userXJInfo.setUnInstallType9("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType9("0");
        }
        if(UnInstallType10.isChecked()){
            userXJInfo.setUnInstallType10("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType10("0");
        }
        if(UnInstallType11.isChecked()){
            userXJInfo.setUnInstallType11("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType11("0");
        }
        if(UnInstallType12.isChecked()){
            userXJInfo.setUnInstallType12("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType12("0");
        }
        if(UnInstallType13.isChecked()){
            userXJInfo.setUnInstallType13("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType13("0");
        }
        if(UnInstallType14.isChecked()){
            userXJInfo.setUnInstallType14("1");
            UNStatus = true;
        }else{
            userXJInfo.setUnInstallType14("0");
        }
        if(StopSupplyType1.isChecked()){
            userXJInfo.setStopSupplyType1("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType1("0");
        }
        if(StopSupplyType2.isChecked()){
            userXJInfo.setStopSupplyType2("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType2("0");
        }
        if(StopSupplyType3.isChecked()){
            userXJInfo.setStopSupplyType3("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType3("0");
        }
        if(StopSupplyType4.isChecked()){
            userXJInfo.setStopSupplyType4("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType4("0");
        }
        if(StopSupplyType5.isChecked()){
            userXJInfo.setStopSupplyType5("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType5("0");
        }

        if(StopSupplyType6.isChecked()){
            userXJInfo.setStopSupplyType6("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType6("0");
        }
        if(StopSupplyType7.isChecked()){
            userXJInfo.setStopSupplyType7("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType7("0");
        }

        if(StopSupplyType8.isChecked()){
            userXJInfo.setStopSupplyType8("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType8("0");
        }
        if(StopSupplyType9.isChecked()){
            userXJInfo.setStopSupplyType9("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType9("0");
        }
        if(StopSupplyType10.isChecked()){
            userXJInfo.setStopSupplyType10("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType10("0");
        }
        if(StopSupplyType11.isChecked()){
            userXJInfo.setStopSupplyType11("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType11("0");
        }
        if(StopSupplyType12.isChecked()){
            userXJInfo.setStopSupplyType12("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType12("0");
        }
        if(StopSupplyType13.isChecked()){
            userXJInfo.setStopSupplyType13("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType13("0");
        }
        if(StopSupplyType14.isChecked()){
            userXJInfo.setStopSupplyType14("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType14("0");
        }
        if(StopSupplyType15.isChecked()){
            userXJInfo.setStopSupplyType15("1");
            StopStatus = true;
        }else{
            userXJInfo.setStopSupplyType15("0");
        }
        userXJInfo.setAttachID(relationID);
        if (StopStatus) {
            userXJInfo.setInspectionStatus("2");
            userXJInfo.setIsInspected("1");// 本地标识：做过安检
            return;

        } else if (UNStatus) {
            userXJInfo.setInspectionStatus("1");
            userXJInfo.setIsInspected("1");
            userXJInfo.setStopSupplyType1("0");
            userXJInfo.setStopSupplyType2("0");
            userXJInfo.setStopSupplyType3("0");
            userXJInfo.setStopSupplyType4("0");
            userXJInfo.setStopSupplyType5("0");
            userXJInfo.setStopSupplyType6("0");
            userXJInfo.setStopSupplyType7("0");
            userXJInfo.setStopSupplyType8("0");
            userXJInfo.setStopSupplyType9("0");
            userXJInfo.setStopSupplyType10("0");
            userXJInfo.setStopSupplyType11("0");
            userXJInfo.setStopSupplyType12("0");
            userXJInfo.setStopSupplyType13("0");
            userXJInfo.setStopSupplyType14("0");
            userXJInfo.setStopSupplyType15("0");
            return;
        } else {
            userXJInfo.setInspectionStatus("0");
            userXJInfo.setIsInspected("1");
            userXJInfo.setStopSupplyType1("0");
            userXJInfo.setStopSupplyType2("0");
            userXJInfo.setStopSupplyType3("0");
            userXJInfo.setStopSupplyType4("0");
            userXJInfo.setStopSupplyType5("0");
            userXJInfo.setStopSupplyType6("0");
            userXJInfo.setStopSupplyType7("0");
            userXJInfo.setStopSupplyType8("0");
            userXJInfo.setStopSupplyType9("0");
            userXJInfo.setStopSupplyType10("0");
            userXJInfo.setStopSupplyType11("0");
            userXJInfo.setStopSupplyType12("0");
            userXJInfo.setStopSupplyType13("0");
            userXJInfo.setStopSupplyType14("0");
            userXJInfo.setStopSupplyType15("0");
            userXJInfo.setUnInstallType1("0");
            userXJInfo.setUnInstallType2("0");
            userXJInfo.setUnInstallType3("0");
            userXJInfo.setUnInstallType4("0");
            userXJInfo.setUnInstallType5("0");
            userXJInfo.setUnInstallType6("0");
            userXJInfo.setUnInstallType7("0");
            userXJInfo.setUnInstallType8("0");
            userXJInfo.setUnInstallType9("0");
            userXJInfo.setUnInstallType10("0");
            userXJInfo.setUnInstallType11("0");
            userXJInfo.setUnInstallType12("0");
            userXJInfo.setUnInstallType13("0");
            userXJInfo.setUnInstallType14("0");
            return;
        }
    }
    /*
    保存照片
     */
    public void  getImagePath(String filePath,String employee, String checkSaleid, String user,String format) {
        if (checkSaleid != null) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                String path = PathUtil.getImagePath();
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                String ImageFileName=relationID+"_" +format + "_" + user + ".jpg";
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
    private void getRectifyResult() {
        if (StopSupplyType1.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType1)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType1)+",");
            }
        }
        if (StopSupplyType2.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType2)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType2)+",");
            }
        }
        if (StopSupplyType3.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType3)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType3)+",");
            }
        }
        if (StopSupplyType4.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType4)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType4)+",");
            }
        }

        if (StopSupplyType5.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType5)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType5)+",");
            }
        }
        if (StopSupplyType6.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType6)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType6)+",");
            }
        }
        if (StopSupplyType7.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType7)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType7)+",");
            }
        }
        if (StopSupplyType8.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType8)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType8)+",");
            }
        }
        if (StopSupplyType9.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType9)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType9)+",");
            }
        }
        if (StopSupplyType10.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_StopSupplyType10)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_StopSupplyType10)+",");
            }
        }
        if (StopSupplyType11.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_StopSupplyType11)+",");

        }
        if (StopSupplyType12.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_StopSupplyType12)+",");

        }
        if (StopSupplyType13.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_StopSupplyType13)+",");

        }
        if (StopSupplyType14.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_StopSupplyType14)+",");

        }
        if (StopSupplyType15.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_StopSupplyType15)+",");

        }
        if (UnInstallType1.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType1)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType1)+",");
            }

        }
        if (UnInstallType2.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType2)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType2)+",");
            }
        }
        if (UnInstallType3.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType3)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType3)+",");
            }
        }
        if (UnInstallType4.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType4)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType4)+",");
            }
        }
        if (UnInstallType5.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType5)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType5)+",");
            }
        }
        if (UnInstallType6.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType6)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType6)+",");
            }
        }
        if (UnInstallType7.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType7)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType7)+",");
            }
        }
        if (UnInstallType8.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType8)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType8)+",");
            }
        }
        if (UnInstallType9.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType9)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType9)+",");
            }
        }
        if (UnInstallType10.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType10)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType10)+",");
            }
        }
        if (UnInstallType11.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType11)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType11)+",");
            }
        }
        if (UnInstallType12.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType12)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType12)+",");
            }
        }
        if (UnInstallType13.isChecked()) {
            if(!TypeClass.equals("1")){
                InspectionItem.append(getResources().getString(R.string.c_UnInstallType13)+",");
            }else{
                InspectionItem.append(getResources().getString(R.string.b_UnInstallType13)+",");
            }
        }
        if (UnInstallType14.isChecked()) {
            InspectionItem.append(getResources().getString(R.string.b_UnInstallType14)+",");
        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if(o==imageInfoDialog){
            imageInfoDialog.dismiss();
        }else{
            this.finish();
        }

    }
    @Override
    public void UpLoadInspectionTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0||tag.getRespCode()==2){
            new AlertView("提示", "安检信息上传成功", null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }
}
