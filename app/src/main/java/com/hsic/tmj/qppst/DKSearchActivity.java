package com.hsic.tmj.qppst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TabHost;
import android.widget.Toast;

import com.hsic.bean.DKSale;
import com.hsic.bean.HsicMessage;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.db.DKSaleDB;
import com.hsic.listener.ImplUpDKInspectionTask;
import com.hsic.picture.PictureHelper;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.UpDKInspectionTask;
import com.hsic.utils.GetOperationTime;
import com.hsic.utils.PathUtil;
import com.hsic.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DKSearchActivity extends AppCompatActivity implements com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener,
        ImplUpDKInspectionTask {
    DKSale dkSale;
    String DKSaleID,CustomerID,relationID,deviceid,emplyeeID,stationID;
    private TabHost mTabHost;
    private CheckBox StopSupplyType1, StopSupplyType2, StopSupplyType3,
            StopSupplyType4, StopSupplyType5, StopSupplyType6, StopSupplyType7,StopSupplyType8,
            UnInstallType1, UnInstallType2, UnInstallType3, UnInstallType4,
            UnInstallType5,UnInstallType6, UnInstallType7,
            UnInstallType9,  UnInstallType11, UnInstallType12;// S:停止供气；N:不予接装
    private List<CheckBox> StopSupply = new ArrayList<CheckBox>();
    private List<CheckBox> UnInstall = new ArrayList<CheckBox>();
    GetBasicInfo getBasicInfo;
    int picCount;
    Button btn_UpLoad;
    public static String filePath;
    StringBuffer searchInfo,stopYY;//安检信息
    DKSaleDB dkSaleDB;
    boolean isUpdate=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        dkSale=new DKSale();
        dkSaleDB=new DKSaleDB(this);
        Intent i=getIntent();
        CustomerID=i.getStringExtra("CustomerID");
        DKSaleID=i.getStringExtra("DKSaleID");
        getBasicInfo=new GetBasicInfo(DKSearchActivity.this);
        deviceid=getBasicInfo.getDeviceID();
        emplyeeID=getBasicInfo.getOperationID();
        stationID=getBasicInfo.getStationID();
        relationID=deviceid + "id"+DKSaleID+"u" + CustomerID;//关联ID
        dkSale=dkSaleDB.getDKInfo(emplyeeID,stationID,DKSaleID);
        dkSale.setDKSaleID(DKSaleID);
        dkSale.setCustomerID(CustomerID);
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
        initCheckBox();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    /**
     * 初始化checkBox
     */
    public void initCheckBox() {
        StopSupplyType1 = (CheckBox) this.findViewById(R.id.basement);
        StopSupplyType2 = (CheckBox) this.findViewById(R.id.half_basement);
        StopSupplyType3 = (CheckBox) this.findViewById(R.id.Highrise);
        StopSupplyType4 = (CheckBox) this.findViewById(R.id.crowd_places);
        StopSupplyType5 = (CheckBox) this.findViewById(R.id.Airtight);
        StopSupplyType6 = (CheckBox) this.findViewById(R.id.bedroom);
        StopSupplyType7 = (CheckBox) this.findViewById(R.id.two_gas);
        StopSupplyType8 = (CheckBox) this.findViewById(R.id.Others);

        UnInstallType1 = (CheckBox) this.findViewById(R.id.no_safe_EXTRCTOR);
        UnInstallType2 = (CheckBox) this.findViewById(R.id.UnInstallType2);
        UnInstallType3 = (CheckBox) this.findViewById(R.id.no_safe_gas_cooker);
        UnInstallType4 = (CheckBox) this.findViewById(R.id.no_match);
        UnInstallType5 = (CheckBox) this.findViewById(R.id.UnInstallType5);
        UnInstallType6 = (CheckBox) this.findViewById(R.id.no_rubber);
        UnInstallType7 = (CheckBox) this.findViewById(R.id.UnInstallType7);
        UnInstallType9 = (CheckBox) this.findViewById(R.id.leak);
        UnInstallType11 = (CheckBox) this.findViewById(R.id.have_fire);
        UnInstallType12 = (CheckBox) this.findViewById(R.id.other);

        StopSupply.add(StopSupplyType1);
        StopSupply.add(StopSupplyType2);
        StopSupply.add(StopSupplyType3);
        StopSupply.add(StopSupplyType4);
        StopSupply.add(StopSupplyType5);
        StopSupply.add(StopSupplyType6);
        StopSupply.add(StopSupplyType7);
        StopSupply.add(StopSupplyType8);


        UnInstall.add(UnInstallType1);
        UnInstall.add(UnInstallType2);
        UnInstall.add(UnInstallType3);
        UnInstall.add(UnInstallType4);
        UnInstall.add(UnInstallType5);
        UnInstall.add(UnInstallType6);
        UnInstall.add(UnInstallType7);
        UnInstall.add(UnInstallType9);
        UnInstall.add(UnInstallType11);
        UnInstall.add(UnInstallType12);
        btn_UpLoad=this.findViewById(R.id.upload);
        btn_UpLoad.setEnabled(false);//

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /***
     * 上传代开户信息
     * @param view
     */
    public void upload(View view){
        //上传安检信息
        picCount=0;
        isWrite();//检查照片数量
        if(picCount>0){
            btn_UpLoad.setEnabled(false);
            boolean ret=false;
            ret=dkSaleDB.isUpLoadDKSale(emplyeeID,stationID,DKSaleID);
            if(ret){
                new AlertView("提示","代开户信息已上传成功", null, new String[]{"确定"},
                        null, DKSearchActivity.this, AlertView.Style.Alert, DKSearchActivity.this)
                        .show();
            }else{
                new UpDKInspectionTask(DKSearchActivity.this,DKSearchActivity.this).execute(DKSaleID,CustomerID);
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
        takePhotoes(CustomerID);
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
        try{
            String inspectedDate = GetOperationTime.getCurrentTime();//获取安检日期
            saveState();//保存安检状态
            getRectifyResult();//
            dkSale.setInspectionMan(emplyeeID);
            dkSale.setFinishTime(inspectedDate);
            isUpdate= dkSaleDB.updateDKSale(emplyeeID,stationID,DKSaleID,dkSale);
            update();

        }catch(Exception ex){
            ex.toString();

        }
    }

    /**
     * 更新本地数据库代开户信息 并打印
     */
    private void update(){
        if(isUpdate){
            btn_UpLoad.setEnabled(true);
            new PrintCodeTask(DKSearchActivity.this).execute();//直接打印
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String time=TimeUtils.getTime("yyyyMMddHHmmss");
                getImagePath(filePath,deviceid,getBasicInfo.getOperationID(),CustomerID,time);

            }
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    @SuppressLint("SimpleDateFormat")
    public void takePhotoes(String user) {
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
                intent.putExtra("SaleID",DKSaleID);
                intent.putExtra("UserID",CustomerID);
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
        searchInfo=new StringBuffer();
        if(UnInstallType1.isChecked()){
            UNStatus = true;
            dkSale.setUnInstallType1("1");
        }else{
            dkSale.setUnInstallType1("0");
        }
        if(UnInstallType2.isChecked()){
            UNStatus = true;
            dkSale.setUnInstallType2("1");
        }else{
            dkSale.setUnInstallType2("0");
        }
        if(UnInstallType3.isChecked()){
            UNStatus = true;
            dkSale.setUnInstallType3("1");
        }else{
            dkSale.setUnInstallType3("0");
        }
        if(UnInstallType4.isChecked()){
            dkSale.setUnInstallType4("1");
            UNStatus = true;
        }else{
            dkSale.setUnInstallType4("0");
        }
        if(UnInstallType5.isChecked()){
            UNStatus = true;
            dkSale.setUnInstallType5("1");
        }else{
            dkSale.setUnInstallType5("0");
        }
        if(UnInstallType6.isChecked()){
            dkSale.setUnInstallType6("1");
            UNStatus = true;
        }else{
            dkSale.setUnInstallType6("0");
        }
        if(UnInstallType7.isChecked()){
            UNStatus = true;
            dkSale.setUnInstallType7("1");
        }else{
            dkSale.setUnInstallType7("0");
        }
        dkSale.setUnInstallType8("0");
        if(UnInstallType9.isChecked()){
            dkSale.setUnInstallType9("1");
            UNStatus = true;
        }else{
            dkSale.setUnInstallType9("0");
        }
        dkSale.setUnInstallType10("0");
        if(UnInstallType11.isChecked()){
            dkSale.setUnInstallType11("1");
            UNStatus = true;
        }else{
            dkSale.setUnInstallType11("0");
        }
        if(UnInstallType12.isChecked()){
            dkSale.setUnInstallType12("1");
            UNStatus = true;
        }else{
            dkSale.setUnInstallType12("0");
        }
        if(StopSupplyType1.isChecked()){
            dkSale.setStopSupplyType1("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType1("0");
        }
        if(StopSupplyType2.isChecked()){
            dkSale.setStopSupplyType2("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType2("0");
        }
        if(StopSupplyType3.isChecked()){
            dkSale.setStopSupplyType3("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType3("0");
        }
        if(StopSupplyType4.isChecked()){
            dkSale.setStopSupplyType4("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType4("0");
        }
        if(StopSupplyType5.isChecked()){
            dkSale.setStopSupplyType5("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType5("0");
        }

        if(StopSupplyType6.isChecked()){
            dkSale.setStopSupplyType6("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType6("0");
        }
        if(StopSupplyType7.isChecked()){
            dkSale.setStopSupplyType7("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType7("0");
        }

        if(StopSupplyType8.isChecked()){
            dkSale.setStopSupplyType8("1");
            StopStatus = true;
        }else{
            dkSale.setStopSupplyType8("0");
        }
        dkSale.setAttachID(relationID);
        if (StopStatus) {
            dkSale.setInspectionStatus("2");
            dkSale.setIsInspected("1");// 本地标识：做过安检
            return;

        } else if (UNStatus) {
            dkSale.setInspectionStatus("1");
            dkSale.setIsInspected("1");
            dkSale.setStopSupplyType1("0");
            dkSale.setStopSupplyType2("0");
            dkSale.setStopSupplyType3("0");
            dkSale.setStopSupplyType4("0");
            dkSale.setStopSupplyType5("0");
            dkSale.setStopSupplyType6("0");
            dkSale.setStopSupplyType7("0");
            dkSale.setStopSupplyType8("0");
            return;
        } else {
            dkSale.setInspectionStatus("0");
            dkSale.setIsInspected("1");
            dkSale.setStopSupplyType1("0");
            dkSale.setStopSupplyType2("0");
            dkSale.setStopSupplyType3("0");
            dkSale.setStopSupplyType4("0");
            dkSale.setStopSupplyType5("0");
            dkSale.setStopSupplyType6("0");
            dkSale.setStopSupplyType7("0");
            dkSale.setStopSupplyType8("0");
            dkSale.setUnInstallType1("0");
            dkSale.setUnInstallType2("0");
            dkSale.setUnInstallType3("0");
            dkSale.setUnInstallType4("0");
            dkSale.setUnInstallType5("0");
            dkSale.setUnInstallType6("0");
            dkSale.setUnInstallType7("0");
            dkSale.setUnInstallType9("0");
            dkSale.setUnInstallType11("0");
            dkSale.setUnInstallType12("0");
            return;
        }
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
                String ImageFileName=relationID+"_" +format + "_" + "search" + ".jpg";
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
                dkSaleDB.InsertXJAssociation(getBasicInfo.getOperationID(), DKSaleID,
                        ImageFileName, relationID,FileName);//将照片信息插入到数据表中
            }
        }
    }
    private String getRectifyResult() {
        stopYY=new StringBuffer();
        searchInfo = new StringBuffer();
        if (StopSupplyType1.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType1)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType1)+"\n");
        }
        if (StopSupplyType2.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType2)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType2)+"\n");
        }
        if (StopSupplyType3.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType3)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType3)+"\n");
        }
        if (StopSupplyType4.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType4)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType4)+"\n");
        }

        if (StopSupplyType5.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType5)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType5)+"\n");
        }
        if (StopSupplyType6.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType6)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType6)+"\n");
        }
        if (StopSupplyType7.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType7)+",");
            searchInfo.append(getResources().getString(R.string.StopSupplyType7)+"\n");
        }
        if (StopSupplyType8.isChecked()) {
            stopYY.append(getResources().getString(R.string.StopSupplyType8));
            searchInfo.append(getResources().getString(R.string.StopSupplyType8)+"\n");
        }
        if (UnInstallType1.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType1)+"\n");
        }
        if (UnInstallType2.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType2)+"\n");
        }
        if (UnInstallType3.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType3)+"\n");
        }
        if (UnInstallType4.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType4)+"\n");
        }
        if (UnInstallType5.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType5)+"\n");
        }
        if (UnInstallType6.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType6)+"\n");
        }
        if (UnInstallType7.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType7)+"\n");
        }
        if (UnInstallType9.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType9)+"\n");
        }
        if (UnInstallType11.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType11)+"\n");
        }
        if (UnInstallType12.isChecked()) {
            searchInfo.append(getResources().getString(R.string.UnInstallType12));
        }
        return searchInfo.toString();
    }


    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        this.finish();
    }

    /**
     * 打印代开户信息
     */
    public class PrintCodeTask extends AsyncTask<Void, Void, Void> {
        private Context context = null;
        private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        private boolean isConnection = false;
        private BluetoothDevice device = null;
        private BluetoothSocket bluetoothSocket = null;
        private OutputStream outputStream;
        private final UUID uuid = UUID
                .fromString("00001101-0000-1000-8000-00805F9B34FB");
        SharedPreferences deviceSetting;
        String bluetoothadd = "";// 蓝牙MAC
        private ProgressDialog dialog;
        GetBasicInfo basicInfo;

        public PrintCodeTask(Context context) {
            this.context = context;
            deviceSetting = context.getSharedPreferences("DeviceSetting", 0);
            bluetoothadd = deviceSetting.getString("BlueToothAdd", "");// 蓝牙MAC
            dialog = new ProgressDialog(context);
            basicInfo = new GetBasicInfo(context);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("正在打印信息");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                int pCount = 2;
                String Intret = connectBT();
                PrintUtils.setOutputStream(outputStream);
                for (int a = 0; a < pCount; a++) {
                    PrintUtils.selectCommand(PrintUtils.RESET);
                    PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
                    PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
                    PrintUtils.selectCommand(PrintUtils.BOLD);
                    PrintUtils.printText("\n");
                    PrintUtils.printText("上海奉贤燃气股份有限公司\n");//公司
                    PrintUtils.printText("--------------------------------\n");
                    PrintUtils.printText("代开户信息\n");
                    PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
                    PrintUtils.selectCommand(PrintUtils.NORMAL);
                    PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                    PrintUtils.printText(PrintUtils.printTwoData("安检单号", dkSale.getDKSaleID() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("安检用户", dkSale.getCustomerID() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("用户姓名", dkSale.getCustomerName() + "\n"));
                    PrintUtils.printText(dkSale.getAddress() + "\n");
                    PrintUtils.printText("--------------------------------\n");
                    if (dkSale.getInspectionStatus().equals("1")) {
                        PrintUtils.printText("本次安检结果:一般隐患" + "\n");
                        PrintUtils.printText("本次安检不合格项:" + "\n");
                        PrintUtils.printText(searchInfo.toString() + "\n");
                    } else if(dkSale.getInspectionStatus().equals("2")){
                        PrintUtils.printText("本次安检结果:严重隐患" + "\n");
                        PrintUtils.printText("本次安检不合格项:" + "\n");
                        PrintUtils.printText(searchInfo.toString() + "\n");
                    }else{
                        PrintUtils.printText("本次安检结果:合格" + "\n");
                    }
                    PrintUtils.printText("--------------------------------\n");
//                    PrintUtils.printText(PrintUtils.printTwoData("安检人:", RectifyName + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("安检日期:", dkSale.getFinishTime() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("用户签字:",   "\n\n"));
                    PrintUtils.printText("\n\n\n\n\n");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                ex.toString();
                close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.setCancelable(true);
            dialog.dismiss();
            close();
        }
        public String connectBT() {
            String log = "connectBT()";
            // 先检查该设备是否支持蓝牙
            if (bluetoothAdapter == null) {
                return "1";// 该设备没有蓝牙功能
            } else {
                // 检查蓝牙是否打开
                boolean b = bluetoothAdapter.isEnabled();
                if (!bluetoothAdapter.isEnabled()) {
                    // 若没打开，先打开蓝牙
                    bluetoothAdapter.enable();
                    System.out.print("蓝牙未打开");
                    return "2";// 蓝牙未打开，程序强制打开蓝牙
                } else {
                    try {
                        this.device = bluetoothAdapter
                                .getRemoteDevice(bluetoothadd);
                        if (!this.isConnection) {
                            bluetoothSocket = this.device
                                    .createRfcommSocketToServiceRecord(uuid);
                            bluetoothSocket.connect();
                            outputStream = bluetoothSocket.getOutputStream();
                            this.isConnection = true;
                        }
                    } catch (Exception ex) {
                        System.out.print("远程获取设备出现异常" + ex.toString());
                        return "3";// 获取设备出现异常
                    }
                }
                return "0";// 连接成功
            }

        }

        private void close() {
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
    @Override
    public void UpDKInspectionEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            dkSaleDB.updateDKStatus(emplyeeID,stationID,DKSaleID);
        }
    }
}
