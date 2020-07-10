package com.hsic.tmj.qppst;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.appupdate.Dialog.UpdateVersionShowDialog;
import com.hsic.appupdate.NetCallBack;
import com.hsic.appupdate.bean.Version;
import com.hsic.appupdate.updater.AppUpdater;
import com.hsic.appupdate.utils.AppUtils;
import com.hsic.bean.EmployeeInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bll.ActivityUtil;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bll.SaveBasicInfo;
import com.hsic.bluetooth.PrintUtil;
import com.hsic.constant.Constant;
import com.hsic.db.DeleteData;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplLogin;
import com.hsic.listener.ImplUpHistory;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.LoginTask;
import com.hsic.task.UpLoadHistoryTask;
import com.hsic.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements ImplLogin,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener,ImplUpHistory {
    private EditText edt_user, edt_pass;
    private Button btn_Login;
    private CheckBox remeberUserAndPass;//是否记住密码
    TextView txt_Version;
    SaveBasicInfo saveBasicInfo;
    GetBasicInfo getBasicInfo;
    String version;
    Build bd = new Build();
    private String DB_Version;
    DeliveryDB insertData;
    SharedPreferences deviceSetting;
    AlertView IsSunmitSale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        saveBasicInfo = new SaveBasicInfo(LoginActivity.this);
        getBasicInfo = new GetBasicInfo(LoginActivity.this);
        insertData = new DeliveryDB(LoginActivity.this);
        IsSunmitSale= new AlertView("提示", "有未上传订单，请先上传订单", null, new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);//
        innitView();
        setDeviceID();
        DB_Version = getBasicInfo.getDBVersion();
        if (DB_Version.equals("")) {
            DB_Version = getResources().getString(R.string.data_version);//数据库版本
            saveBasicInfo.saveDBVersion(DB_Version);
        }
        String UnitCode=getBasicInfo.getCompanyCode();
        if(UnitCode.equals("")){
            TurnSetting();
        }
        deviceSetting = getSharedPreferences("DeviceSetting", 0);
        String bluetooth = deviceSetting.getString("BlueToothAdd", "");
        if (bluetooth.equals("")) {
            Intent i = new Intent(LoginActivity.this, AdvConfigActivity.class);
            this.startActivity(i);
        }else{
            PrintUtil.setDefaultBluetoothDeviceAddress(this,bluetooth);
        }
        checkVersion();
        DeleteData deleteData = new DeleteData(this);
        Calendar cal = Calendar.getInstance();//得到当前时间
        int day=cal.get(Calendar.DATE);//日
//        if(day==10){
//            /**
//             *删除本地数据[每月10号调价]
//             */
//            deleteData.deleteALL(getBasicInfo.getOperationID(), getBasicInfo.getStationID());
//        }else{
            /**
             * 删除历史数据[三天前]
             */
            deleteData.delete(getBasicInfo.getOperationID(), getBasicInfo.getStationID());
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    private void innitView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UnitCode=getBasicInfo.getCompanyCode();
                if(UnitCode.equals("")){
                    TurnSetting();
                }else{
                    //弹出基本设置

                    if(UnitCode.equals("1910")){
                        isPass(LoginActivity.this,"191001");
                    }
                    if(UnitCode.equals("1607")){
                        isPass(LoginActivity.this,"160702");
                    }
                    if(UnitCode.equals("17607")){
                        isPass(LoginActivity.this,"170703");
                    }
                    if(UnitCode.equals("2010")){
                        isPass(LoginActivity.this,"201004");
                    }
                }


            }
        });
        edt_user = this.findViewById(R.id.edt_stuffid);
        edt_user.setText("");
        edt_pass = this.findViewById(R.id.edt_password);
        edt_pass.setText("");
        txt_Version = this.findViewById(R.id.Version);
        String version = getLocalVersionName(LoginActivity.this);
        txt_Version.setText("应用版本：" + version);
        remeberUserAndPass = this.findViewById(R.id.userAndPass);
        if (getBasicInfo.getIsChecked()) {
            remeberUserAndPass.setChecked(true);
        } else {
            remeberUserAndPass.setChecked(false);
        }
        if (remeberUserAndPass.isChecked()) {
            //初始化登录账户及密码
            edt_user.setText(getBasicInfo.getAccount());
            edt_pass.setText(getBasicInfo.getPass());
        }
        btn_Login = this.findViewById(R.id.btn_login);
        btn_Login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String UnitCode=getBasicInfo.getCompanyCode();
                if(UnitCode.equals("")){
//                    TurnSetting();
                    Toast.makeText(getBaseContext(), "请先去配置配送公司!", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn_Login.setEnabled(false);
                String account = edt_user.getText().toString();
                String pass = edt_pass.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    btn_Login.setEnabled(true);
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    btn_Login.setEnabled(true);
                    return;
                }
                try {
                    pass = MD5Util.getFileMD5String(pass);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    btn_Login.setEnabled(true);
                    e.printStackTrace();
                }
                if (checkNetworkState()) {
                    new LoginTask(LoginActivity.this, LoginActivity.this).execute(account, pass);
                } else {
                    //没有网络状态
                    String userID = (edt_user.getText().toString()).toUpperCase();
                    if (userID.equals(getBasicInfo.getOperationID())) {
                        Intent login = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(login);
                        LoginActivity.this.finish();
                    } else {
                        btn_Login.setEnabled(true);
                        new AlertView("提示", "由于该用户无最近登陆记录，无网络状态登陆不支持", null, new String[]{"确定"},
                                null, LoginActivity.this, AlertView.Style.Alert, LoginActivity.this)
                                .show();
                    }

                }
            }
        });
    }

    private void TurnSetting() {
        ActivityUtil.JumpToAdvConfig(this);
    }

    /**
     * 获取设备基本信息
     */
    private void setDeviceID() {
        try {
            TelephonyManager tm = (TelephonyManager) this
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String deviceN0 = "";
            deviceN0 = tm.getDeviceId();
            int l = deviceN0.length();
            deviceN0 = deviceN0.substring(l - 8, l);
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
            saveBasicInfo.saveDeviceID(deviceN0);//
            saveBasicInfo.saveDeviceType(bd.MODEL);
            Constant.DeviceType = bd.MODEL;
            Constant.DeviceID = deviceN0;
        } catch (SecurityException ex) {
            ex.toString();
            saveBasicInfo.saveDeviceID("00000000");//
            Constant.DeviceID = "00000000";
        } catch (PackageManager.NameNotFoundException ex2) {
            ex2.toString();
            version = "0.0.0";
            saveBasicInfo.saveDeviceID("00000000");//
            Constant.DeviceID = "00000000";
        }
    }

    @Override
    public void LoginTaskEnd(HsicMessage tag) {
        if (tag.getRespCode() == 0) {
            EmployeeInfo employeeInfo = (EmployeeInfo) JSONUtils.toObjectWithGson(
                    tag.getRespMsg(), EmployeeInfo.class);
            saveBasicInfo.saveOperationID(employeeInfo.getUserID().toUpperCase());
            saveBasicInfo.saveOperationName(employeeInfo.getUserName());
            saveBasicInfo.saveStationName(employeeInfo.getStationName());
            saveBasicInfo.saveStationID(employeeInfo.getStation());
            saveBasicInfo.saveOperationType(employeeInfo.getUserType());
            if (remeberUserAndPass.isChecked()) {
                saveBasicInfo.saveIsChecked(true);
                saveBasicInfo.saveAccount(edt_user.getText().toString());
                saveBasicInfo.savePass(edt_pass.getText().toString());

            }
            /**
             * 根据数据库版本决定是否需要更新本地数据库
             */
//            if (insertData.InsertConfigInfo()) {
//                saveBasicInfo.saveDBVersion("");
//            }
            Intent login = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(login);
            this.finish();

        } else {
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
        btn_Login.setEnabled(true);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if(o.equals(IsSunmitSale)){
            IsSunmitSale.dismiss();
            new UpLoadHistoryTask(LoginActivity.this,LoginActivity.this).execute();
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
     * 检查版本更新
     */
    private void checkVersion() {
        boolean net = false;
        net = checkNetworkState();
        if (net) {
            /**
             *
             */
            DeliveryDB deliveryDB=new DeliveryDB(this);
            List<Sale> sales=new ArrayList<>();
            sales=deliveryDB.UpHistorySale(getBasicInfo.getOperationID(), getBasicInfo.getStationID());
            if(sales.size()>0){
                //强制上传数据
                IsSunmitSale.show();
            }else{
//                VersionAsyncTask vat = new VersionAsyncTask(LoginActivity.this, this);
//                vat.execute();
                version();
            }

        }
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    @Override
    public void UpLoadHistoryTaskEnd(HsicMessage tag) {
        version();
    }
    private void  version() {
        String endPointDefaultString = "", port = "";
        endPointDefaultString = this.getResources().getString(
                R.string.web_default);
        final String IP = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("WebServer",
                        endPointDefaultString);
        port = this.getResources().getString(R.string.web_port_default);
        final String url = "http://" +
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString("WebServer",
                                endPointDefaultString) + ":" + PreferenceManager.getDefaultSharedPreferences(this)
                .getString("WebServerPort", port) +"/QPPSTPhone"+getBasicInfo.getCompanyCode()+ "/apk/AppVersion.xml";
        final String Port = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("WebServerPort", port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppUpdater.getInstance().getNetManager().get(url, new NetCallBack() {
                    @Override
                    public void success(String response) {
                        try {
                            Version downLoadBean = JSONUtils.toObjectWithGson(response, Version.class);
                            if (downLoadBean == null) {
                                return;
                            }
                            String downPath = downLoadBean.getFile_real_path();
                            if (!downPath.contains(IP)) {
                                //考虑到上液移动卡
                                String[] temp = downPath.split("//");// http://10.123.16.81/SHLPGPhoneWs/apk/QPManagerMoblieV52.apk
                                downPath = temp[0] + "//" + IP + ":" + Port + "/";
                                String[] temp2 = temp[1].split("/");
                                int len = temp2.length;
                                if (len > 1) {
                                    for (int i = 1; i < len; i++) {
                                        downPath = downPath + temp2[i] + "/";
                                    }
                                }
                                len = downPath.length();
                                downPath = downPath.substring(0, len - 1);
                                downLoadBean.setFile_real_path(downPath);
                            }
                            // 检测是否需要弹窗
                            long versionCode = Long.parseLong(downLoadBean.getVersionCode());
                            if (versionCode > AppUtils.getVersionCode(LoginActivity.this)) {
                                UpdateVersionShowDialog.show(LoginActivity.this, downLoadBean);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(String throwable) {
//						Toast.makeText(LoginActivity.this, "接口调用失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    /**
     * 密码数据输入框
     *
     * @param context
     */
    public void isPass(final Context context,final String Pass) {
        AlertDialog.Builder pass = new AlertDialog.Builder(context);
        pass.setTitle("请输入密码");
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.password, null);
        final EditText editText = (EditText) view.findViewById(R.id.tagID);
        pass.setView(view);
        pass.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String pass = editText.getText().toString().trim()
                        .toUpperCase();
                if(!pass.equals("")){
                    if (pass.equals(Pass)) {
                        TurnSetting();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(context, "密码不正确!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "请输入密码!", Toast.LENGTH_SHORT).show();
                }

            }

        });
        pass.create().show();

    }
}

