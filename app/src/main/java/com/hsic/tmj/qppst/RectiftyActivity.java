package com.hsic.tmj.qppst;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.Toast;

import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserRectifyInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.GPrinterCommand;
import com.hsic.bluetooth.PrintQueue;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.db.RectifyDB;
import com.hsic.listener.ImplUploadRectify;
import com.hsic.picture.PictureHelper;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.UpLoadRectifyTask;
import com.hsic.utils.GetOperationTime;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;

public class RectiftyActivity extends AppCompatActivity implements ImplUploadRectify , com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    private TabHost mTabHost;
    private CheckBox StopSupplyType1, StopSupplyType2, StopSupplyType3,
            StopSupplyType4, StopSupplyType5, StopSupplyType6, StopSupplyType7,
            StopSupplyType8,StopSupplyType9,StopSupplyType10,StopSupplyType11,
            StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,
            UnInstallType1, UnInstallType2, UnInstallType3, UnInstallType4,
            UnInstallType5,UnInstallType6, UnInstallType7,UnInstallType8,
            UnInstallType9, UnInstallType10, UnInstallType11, UnInstallType12,
            UnInstallType13,UnInstallType14;// S:停止供气；N:不予接装:不予接装
    StringBuffer lastInspection = new StringBuffer();
    StringBuffer rectifyInfo;
    UserRectifyInfo userRectifyInfo;
    GetBasicInfo getBasicInfo;//20181019
    String RectifyName = "", UserName = "", DeliverAddress = "", Telphone = "", TypeClass = "";
    String deviceid,id,userID;
    String relationID,emplyeeID,stationID;//
    RectifyDB rectifyDB;
    public static String filePath;
    boolean isUpdate=false;
    int picCount=0;
    Button btn_UpLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectifty);
        Intent i=getIntent();
        userID=i.getStringExtra("UserID");
        getBasicInfo = new GetBasicInfo(RectiftyActivity.this);
        rectifyDB=new RectifyDB(RectiftyActivity.this);
        userRectifyInfo=rectifyDB.GetRectifyInfoByUserID(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
        id=userRectifyInfo.getId();
        TypeClass=userRectifyInfo.getTypeClass();
        initCheckBox();
        setData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    private void setData(){
        deviceid=getBasicInfo.getDeviceID();
        emplyeeID=getBasicInfo.getOperationID();
        stationID=getBasicInfo.getStationID();
        id=userRectifyInfo.getId();
        userID=userRectifyInfo.getUserid();
        UserName=userRectifyInfo.getUsername();
        DeliverAddress=userRectifyInfo.getDeliveraddress();
        Telphone=userRectifyInfo.getTelephone();
        relationID=deviceid + "e"+emplyeeID+"id" + id;
        if(!TypeClass.equals("1")){
            //居民用户
            if(userRectifyInfo.getLast_InspectionStatus().equals("2")){
                mTabHost.setCurrentTab(1);
                if (userRectifyInfo.getStopSupplyType1().equals("1")) {
                    StopSupplyType1.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType1)
                            + "\n");
                } else {
                    StopSupplyType1.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType2().equals("1")) {
                    StopSupplyType2.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType2)
                            + "\n");
                } else {
                    StopSupplyType2.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType3().equals("1")) {
                    StopSupplyType3.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType3)
                            + "\n");
                } else {
                    StopSupplyType3.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType4().equals("1")) {
                    StopSupplyType4.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType4)
                            + "\n");
                } else {
                    StopSupplyType4.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType5().equals("1")) {
                    StopSupplyType5.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType5)
                            + "\n");
                } else {
                    StopSupplyType5.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType6().equals("1")) {
                    StopSupplyType6.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType6)
                            + "\n");
                } else {
                    StopSupplyType6.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType7().equals("1")) {
                    StopSupplyType7.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType7)
                            + "\n");
                } else {
                    StopSupplyType7.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType8().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType8)
                            + "\n");
                } else {
                    StopSupplyType8.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType9().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType9)
                            + "\n");
                } else {
                    StopSupplyType9.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType10().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_StopSupplyType10)
                            + "\n");
                } else {
                    StopSupplyType10.setChecked(false);
                }
            }else if(userRectifyInfo.getLast_InspectionStatus().equals("1")){
                mTabHost.setCurrentTab(0);
                if (userRectifyInfo.getUnInstallType1().equals("1")) {
                    UnInstallType1.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType1)
                            + "\n");
                } else {
                    UnInstallType1.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType2().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType2)
                            + "\n");
                } else {
                }
                if (userRectifyInfo.getUnInstallType3().equals("1")) {
                    UnInstallType3.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType3)
                            + "\n");

                } else {
                    UnInstallType3.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType4().equals("1")) {
                    UnInstallType4.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType4)
                            + "\n");
                } else {
                    UnInstallType4.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType5().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType5)
                            + "\n");
                } else {
                    UnInstallType5.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType6().equals("1")) {
                    UnInstallType6.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType6)
                            + "\n");
                } else {
                    UnInstallType6.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType7().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType7)
                            + "\n");
                } else {
                    UnInstallType7.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType8().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType8)
                            + "\n");
                } else {
                    UnInstallType8.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType9().equals("1")) {
                    UnInstallType9.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType9)
                            + "\n");
                } else {
                    UnInstallType9.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType10().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType10)
                            + "\n");
                } else {
                    UnInstallType10.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType11().equals("1")) {
                    UnInstallType11.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType11)
                            + "\n");
                } else {
                    UnInstallType11.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType12().equals("1")) {
                    UnInstallType12.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType12)
                            + "\n");
                } else {
                    UnInstallType12.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType13().equals("1")) {
                    UnInstallType13.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.c_UnInstallType12)
                            + "\n");
                } else {
                    UnInstallType13.setChecked(false);
                }
            }

        }else{
            if(userRectifyInfo.getLast_InspectionStatus().equals("2")){
                mTabHost.setCurrentTab(1);
                if (userRectifyInfo.getStopSupplyType1().equals("1")) {
                    StopSupplyType1.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType1)
                            + "\n");
                } else {
                    StopSupplyType1.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType2().equals("1")) {
                    StopSupplyType2.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType2)
                            + "\n");
                } else {
                    StopSupplyType2.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType3().equals("1")) {
                    StopSupplyType3.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType3)
                            + "\n");
                } else {
                    StopSupplyType3.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType4().equals("1")) {
                    StopSupplyType4.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType4)
                            + "\n");
                } else {
                    StopSupplyType4.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType5().equals("1")) {
                    StopSupplyType5.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType5)
                            + "\n");
                } else {
                    StopSupplyType5.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType6().equals("1")) {
                    StopSupplyType6.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType6)
                            + "\n");
                } else {
                    StopSupplyType6.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType7().equals("1")) {
                    StopSupplyType7.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType7)
                            + "\n");
                } else {
                    StopSupplyType7.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType8().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType8)
                            + "\n");
                } else {
                    StopSupplyType8.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType9().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType9)
                            + "\n");
                } else {
                    StopSupplyType9.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType10().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType10)
                            + "\n");
                } else {
                    StopSupplyType10.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType11().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType11)
                            + "\n");
                } else {
                    StopSupplyType11.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType12().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType12)
                            + "\n");
                } else {
                    StopSupplyType12.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType13().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType13)
                            + "\n");
                } else {
                    StopSupplyType13.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType14().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType14)
                            + "\n");
                } else {
                    StopSupplyType14.setChecked(false);
                }
                if (userRectifyInfo.getStopSupplyType15().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_StopSupplyType15)
                            + "\n");
                } else {
                    StopSupplyType15.setChecked(false);
                }
            }else if(userRectifyInfo.getLast_InspectionStatus().equals("1")){
                mTabHost.setCurrentTab(0);
                if (userRectifyInfo.getUnInstallType1().equals("1")) {
                    UnInstallType1.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType1)
                            + "\n");
                } else {
                    UnInstallType1.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType2().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType2)
                            + "\n");
                } else {
                    UnInstallType2.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType3().equals("1")) {
                    UnInstallType3.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType3)
                            + "\n");
                } else {
                    UnInstallType3.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType4().equals("1")) {
                    UnInstallType4.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType4)
                            + "\n");
                } else {
                    UnInstallType4.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType5().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType5)
                            + "\n");
                } else {
                    UnInstallType5.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType6().equals("1")) {
                    UnInstallType6.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType6)
                            + "\n");
                } else {
                    UnInstallType6.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType7().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType7)
                            + "\n");
                } else {
                    UnInstallType7.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType8().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType8)
                            + "\n");
                } else {
                    UnInstallType8.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType9().equals("1")) {
                    UnInstallType9.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType9)
                            + "\n");
                } else {
                    UnInstallType9.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType10().equals("1")) {
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType10)
                            + "\n");
                } else {
                }
                if (userRectifyInfo.getUnInstallType11().equals("1")) {
                    UnInstallType11.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType11)
                            + "\n");
                } else {
                    UnInstallType11.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType12().equals("1")) {
                    UnInstallType12.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType12)
                            + "\n");
                } else {
                    UnInstallType12.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType13().equals("1")) {
                    UnInstallType13.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType13)
                            + "\n");
                } else {
                    UnInstallType13.setChecked(false);
                }
                if (userRectifyInfo.getUnInstallType14().equals("1")) {
                    UnInstallType14.setChecked(true);
                    lastInspection.append(getResources().getString(R.string.b_UnInstallType14)
                            + "\n");
                } else {
                    UnInstallType14.setChecked(false);
                }
            }

        }



    }
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
        TypeClass=userRectifyInfo.getTypeClass();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                String time= TimeUtils.getTime("yyyyMMddHHmmss");
                getImagePath(filePath,deviceid,getBasicInfo.getOperationID(),userID,time);
            }
        }
    }
    /**
     * 拍照事件
     *
     * @param view
     */
    public void takePhoto(View view) {
        takePhoto(deviceid, userID);
    }
    /***
     * 上传安检信息
     * @param view
     */
    public void upload(View view){
        //上传安检信息
        boolean ret=false;
        ret=rectifyDB.isUpLoadRectify(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),id);
        if(ret){
            new AlertView("提示", "整改信息已上传成功", null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }else{
            new UpLoadRectifyTask(RectiftyActivity.this,RectiftyActivity.this).execute(userID);
        }

    }

    /**
     * 进入预览页面
     *
     * @param view
     */
    public void preview(View view) {
        preview();
    }

    /**
     * 打印签字
     * @param view
     */
    public void save(View view) {
        try{
            String inspectedDate = GetOperationTime.getCurrentTime();//获取安检日期
            saveState();//保存安检状态
            getRectifyResult();//
            userRectifyInfo.setInspectionDate(inspectedDate);
            picCount=0;
            isWrite();//检查照片数量
            if(picCount>0){
                    //是否已拍照
                isUpdate=rectifyDB.updateRectifyInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userRectifyInfo);
                if(isUpdate){
                    btn_UpLoad.setEnabled(true);//
                    Toast.makeText(RectiftyActivity.this, "订单保存成功", Toast.LENGTH_SHORT).show();
                    print();
                }
            }else{
                //安检完成请拍照
                Toast.makeText(getApplicationContext(), "请拍照",
                        Toast.LENGTH_SHORT).show();
            }


        }catch(Exception ex){
            ex.toString();

        }
    }
    /**
     * 判断是否签字
     * @return
     */
    public boolean isWrite(){
        boolean ret=false;
        int flag = 0;
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
//            if (flag == 0) {
//                Toast.makeText(getApplicationContext(), "请先签字确认",
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            } else {
//                return true;
//            }
        } else {
            ret=false;
            Toast.makeText(getApplicationContext(), "请先拍照确认",
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
                Intent intent = new Intent(this, RectifyPreviewActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("UserID",userID);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "请先拍照后在预览",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void takePhoto(String deviceId, String userid) {
        filePath = Environment.getExternalStorageDirectory() + "/photoes/"
                + deviceId + "s" + ".jpg";
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
    /*

    */
    public void  getImagePath(String filePath,String deviceid,String employee, String user,String format) {
        if (user != null) {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                String path = PathUtil.getImagePath();
                File file1 = new File(path);
                if (!file1.exists()) {
                    file1.mkdirs();
                }
                String ImageFileName=relationID+"_" +format + "_" + "u" +userID+ ".jpg";
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
                rectifyDB.InsertRectifyAssociation(employee, userID, ImageFileName, relationID,FileName);//将照片信息插入到数据表中
            }
        }
    }

    /**
     * 保存安检项同时把安检项插入数据库
     */
    private void saveState(){
        boolean UNStatus = false;
        boolean StopStatus = false;
        if(UnInstallType1.isChecked()){
            UNStatus = true;
            userRectifyInfo.setUnInstallType1("1");
        }
        if(UnInstallType2.isChecked()){
            UNStatus = true;
            userRectifyInfo.setUnInstallType2("1");
        }
        if(UnInstallType3.isChecked()){
            UNStatus = true;
            userRectifyInfo.setUnInstallType3("1");
        }
        if(UnInstallType4.isChecked()){
            userRectifyInfo.setUnInstallType4("1");
            UNStatus = true;
        }
        if(UnInstallType5.isChecked()){
            UNStatus = true;
            userRectifyInfo.setUnInstallType5("1");
        }
        if(UnInstallType6.isChecked()){
            userRectifyInfo.setUnInstallType6("1");
            UNStatus = true;
        }
        if(UnInstallType7.isChecked()){
            UNStatus = true;
            userRectifyInfo.setUnInstallType7("1");
        }
        if(UnInstallType9.isChecked()){
            userRectifyInfo.setUnInstallType9("1");
            UNStatus = true;
        }
        if(UnInstallType11.isChecked()){
            userRectifyInfo.setUnInstallType11("1");
            UNStatus = true;
        }
        if(UnInstallType12.isChecked()){
            userRectifyInfo.setUnInstallType12("1");
            UNStatus = true;
        }
        if(UnInstallType13.isChecked()){
            userRectifyInfo.setUnInstallType11("1");
            UNStatus = true;
        }
        if(UnInstallType14.isChecked()){
            userRectifyInfo.setUnInstallType12("1");
            UNStatus = true;
        }
        if(StopSupplyType1.isChecked()){
            userRectifyInfo.setStopSupplyType1("1");
            StopStatus = true;
        }
        if(StopSupplyType2.isChecked()){
            userRectifyInfo.setStopSupplyType2("1");
            StopStatus = true;
        }
        if(StopSupplyType3.isChecked()){
            userRectifyInfo.setStopSupplyType3("1");
            StopStatus = true;
        }
        if(StopSupplyType4.isChecked()){
            userRectifyInfo.setStopSupplyType4("1");
            StopStatus = true;
        }
        if(StopSupplyType5.isChecked()){
            userRectifyInfo.setStopSupplyType5("1");
            StopStatus = true;
        }
        if(StopSupplyType6.isChecked()){
            userRectifyInfo.setStopSupplyType6("1");
            StopStatus = true;
        }
        if(StopSupplyType7.isChecked()){
            userRectifyInfo.setStopSupplyType7("1");
            StopStatus = true;
        }
        if(StopSupplyType8.isChecked()){
            userRectifyInfo.setStopSupplyType8("1");
            StopStatus = true;
        }
        if(StopSupplyType9.isChecked()){
            userRectifyInfo.setStopSupplyType9("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType9("0");
        }
        if(StopSupplyType10.isChecked()){
            userRectifyInfo.setStopSupplyType10("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType10("0");
        }
        if(StopSupplyType11.isChecked()){
            userRectifyInfo.setStopSupplyType11("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType11("0");
        }
        if(StopSupplyType12.isChecked()){
            userRectifyInfo.setStopSupplyType12("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType12("0");
        }
        if(StopSupplyType13.isChecked()){
            userRectifyInfo.setStopSupplyType13("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType13("0");
        }
        if(StopSupplyType14.isChecked()){
            userRectifyInfo.setStopSupplyType14("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType14("0");
        }
        if(StopSupplyType15.isChecked()){
            userRectifyInfo.setStopSupplyType15("1");
            StopStatus = true;
        }else{
            userRectifyInfo.setStopSupplyType15("0");
        }
        userRectifyInfo.setRelationID(relationID);
        if (StopStatus) {
            userRectifyInfo.setInspectionStatus("2");
            userRectifyInfo.setIsInspected("1");// 本地标识：做过安检
            userRectifyInfo.setUnInstallType1("0");
            userRectifyInfo.setUnInstallType2("0");
            userRectifyInfo.setUnInstallType3("0");
            userRectifyInfo.setUnInstallType4("0");
            userRectifyInfo.setUnInstallType5("0");
            userRectifyInfo.setUnInstallType6("0");
            userRectifyInfo.setUnInstallType7("0");
            userRectifyInfo.setUnInstallType9("0");
            userRectifyInfo.setUnInstallType11("0");
            userRectifyInfo.setUnInstallType12("0");
            userRectifyInfo.setUnInstallType13("0");
            userRectifyInfo.setUnInstallType14("0");

        } else if (UNStatus) {
            userRectifyInfo.setInspectionStatus("1");
            userRectifyInfo.setIsInspected("1");
            userRectifyInfo.setStopYY("");
            userRectifyInfo.setStopSupplyType1("0");
            userRectifyInfo.setStopSupplyType2("0");
            userRectifyInfo.setStopSupplyType3("0");
            userRectifyInfo.setStopSupplyType4("0");
            userRectifyInfo.setStopSupplyType5("0");
            userRectifyInfo.setStopSupplyType6("0");
            userRectifyInfo.setStopSupplyType7("0");
            userRectifyInfo.setStopSupplyType8("0");
            userRectifyInfo.setStopSupplyType9("0");
            userRectifyInfo.setStopSupplyType10("0");
            userRectifyInfo.setStopSupplyType11("0");
            userRectifyInfo.setStopSupplyType12("0");
            userRectifyInfo.setStopSupplyType13("0");
            userRectifyInfo.setStopSupplyType14("0");
            userRectifyInfo.setStopSupplyType15("0");

        } else {
            userRectifyInfo.setInspectionStatus("0");
            userRectifyInfo.setIsInspected("1");
            userRectifyInfo.setStopYY("");
            userRectifyInfo.setStopSupplyType1("0");
            userRectifyInfo.setStopSupplyType2("0");
            userRectifyInfo.setStopSupplyType3("0");
            userRectifyInfo.setStopSupplyType4("0");
            userRectifyInfo.setStopSupplyType5("0");
            userRectifyInfo.setStopSupplyType6("0");
            userRectifyInfo.setStopSupplyType7("0");
            userRectifyInfo.setStopSupplyType8("0");
            userRectifyInfo.setStopSupplyType9("0");
            userRectifyInfo.setStopSupplyType10("0");
            userRectifyInfo.setStopSupplyType11("0");
            userRectifyInfo.setStopSupplyType12("0");
            userRectifyInfo.setStopSupplyType13("0");
            userRectifyInfo.setStopSupplyType14("0");
            userRectifyInfo.setStopSupplyType15("0");
            userRectifyInfo.setUnInstallType1("0");
            userRectifyInfo.setUnInstallType2("0");
            userRectifyInfo.setUnInstallType3("0");
            userRectifyInfo.setUnInstallType4("0");
            userRectifyInfo.setUnInstallType5("0");
            userRectifyInfo.setUnInstallType6("0");
            userRectifyInfo.setUnInstallType7("0");
            userRectifyInfo.setUnInstallType9("0");
            userRectifyInfo.setUnInstallType11("0");
            userRectifyInfo.setUnInstallType12("0");
            userRectifyInfo.setUnInstallType13("0");
            userRectifyInfo.setUnInstallType14("0");
        }

    }
    /**
     * 获取一般隐患具体名称（用于历史记录）
     *
     * @return
     */
    private String getRectifyResult() {
        rectifyInfo=new StringBuffer();
        if(!TypeClass.equals("1")){
            //居民用户
            if(userRectifyInfo.getInspectionStatus().equals("2")){
                if(StopSupplyType1.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType1)+"\n");
                }
                if(StopSupplyType2.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType2)+"\n");
                }
                if(StopSupplyType3.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType3)+"\n");
                }
                if(StopSupplyType4.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType4)+"\n");
                }

                if(StopSupplyType5.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType5)+"\n");
                }
                if(StopSupplyType6.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType6)+"\n");
                }
                if(StopSupplyType7.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType7)+"\n");
                }
                if(StopSupplyType8.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType8)+"\n");
                }
                if(StopSupplyType9.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType9)+"\n");
                }
                if(StopSupplyType10.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_StopSupplyType10)+"\n");
                }
            }else if(userRectifyInfo.getInspectionStatus().equals("1")){
                if(UnInstallType1.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType1)+"\n");
                }
                if(UnInstallType2.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType2)+"\n");
                }
                if(UnInstallType3.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType3)+"\n");
                }
                if(UnInstallType4.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType4)+"\n");
                }
                if(UnInstallType5.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType5)+"\n");
                }
                if(UnInstallType6.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType6)+"\n");
                }
                if(UnInstallType7.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType7)+"\n");
                }
                if(UnInstallType9.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType9)+"\n");
                }
                if(UnInstallType11.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType11)+"\n");
                }
                if(UnInstallType12.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType12)+"\n");
                }
                if(UnInstallType13.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.c_UnInstallType13)+"\n");
                }
            }
        }else{
            if(userRectifyInfo.getInspectionStatus().equals("2")){
                if(StopSupplyType1.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType1)+"\n");
                }
                if(StopSupplyType2.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType2)+"\n");
                }
                if(StopSupplyType3.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType3)+"\n");
                }
                if(StopSupplyType4.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType4)+"\n");
                }

                if(StopSupplyType5.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType5)+"\n");
                }
                if(StopSupplyType6.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType6)+"\n");
                }
                if(StopSupplyType7.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType7)+"\n");
                }
                if(StopSupplyType8.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType8)+"\n");
                }
                if(StopSupplyType9.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType9)+"\n");
                }
                if(StopSupplyType10.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType10)+"\n");
                }
                if(StopSupplyType11.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType11)+"\n");
                }
                if(StopSupplyType12.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType12)+"\n");
                }
                if(StopSupplyType13.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType13)+"\n");
                }
                if(StopSupplyType14.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType14)+"\n");
                }
                if(StopSupplyType15.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_StopSupplyType15)+"\n");
                }
            }else if(userRectifyInfo.getInspectionStatus().equals("1")){
                if(UnInstallType1.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType1)+"\n");
                }
                if(UnInstallType2.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType2)+"\n");
                }
                if(UnInstallType3.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType3)+"\n");
                }
                if(UnInstallType4.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType4)+"\n");
                }
                if(UnInstallType5.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType5)+"\n");
                }
                if(UnInstallType6.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType6)+"\n");
                }
                if(UnInstallType7.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType7)+"\n");
                }
                if(UnInstallType9.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType9)+"\n");
                }
                if(UnInstallType11.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType11)+"\n");
                }
                if(UnInstallType12.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType12)+"\n");
                }
                if(UnInstallType13.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType13)+"\n");
                }
                if(UnInstallType14.isChecked()){
                    rectifyInfo.append(getResources().getString(R.string.b_UnInstallType14)+"\n");
                }
            }
        }
        return rectifyInfo.toString();
    }

    @Override
    public void UploadRectifyTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            new AlertView("提示", "整改信息上传成功", null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
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
        RectiftyActivity.this.finish();
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
            printBytes.add(PrintUtils.str2Byte("整改单\n"));
            printBytes.add(GPrinterCommand.print);
            printBytes.add(GPrinterCommand.bold_cancel);
            printBytes.add(GPrinterCommand.NORMAL);
            printBytes.add(GPrinterCommand.ALIGN_LEFT);
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("整改用户", userID + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("姓名", UserName + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("所属站点", getBasicInfo.getStationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(DeliverAddress + "\n"));//客户地址
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printThreeData("项目", "数量", "金额\n")));
            if (!lastInspection.toString().equals("")) {
                printBytes.add(PrintUtils.str2Byte("上次安检不合格项:" + "\n"));
                printBytes.add(PrintUtils.str2Byte(lastInspection.toString()));
            }
            if (!rectifyInfo.toString().equals("")) {
                printBytes.add(PrintUtils.str2Byte("本次安检结果:未整改" + "\n"));
                printBytes.add(PrintUtils.str2Byte("本次安检不合格项:" + "\n"));
                printBytes.add(PrintUtils.str2Byte(rectifyInfo.toString() + "\n"));
            }else{
                printBytes.add(PrintUtils.str2Byte("本次安检结果:已整改" + "\n"));
            }
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("整改人:", RectifyName + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("整改日期:", userRectifyInfo.getInspectionDate() + "\n")));
            printBytes.add(PrintUtils.str2Byte("\n\n\n\n\n"));
        }
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }
}
