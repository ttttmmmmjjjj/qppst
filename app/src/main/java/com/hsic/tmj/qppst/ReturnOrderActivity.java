package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.adapter.SaleListAdapter;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleDetail;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplChargeBack;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.ChargeBackTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;

public class ReturnOrderActivity extends AppCompatActivity implements ImplChargeBack,
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener {
    DeliveryDB deliveryDB;
    Sale sale;
    GetBasicInfo getBasicInfo;
    private String saleID,CustomerType;
    List<Map<String,String>> finish;
    List<SaleDetail> details;
    ListView listView;
    SaleListAdapter saleListAdapter;
    TextView txt_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_finish);
        deliveryDB=new DeliveryDB(this);
        getBasicInfo=new GetBasicInfo(this);
        listView=this.findViewById(R.id.lv_saleInfo);
        txt_title=this.findViewById(R.id.txt_title);
        txt_title.setText("退单管理");

        sale=new Sale();
        details=new ArrayList<>();
        finish=new ArrayList<Map<String, String>>() ;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(finish.size()>0){
                    Map<String, String> data =  (Map<String, String>)adapterView.getItemAtPosition(position);
                    String UserID=data.get("UserID");
                    String SaleID=data.get("SaleID");
                    CustomerType=data.get("CustomerType");
                    sale.setSaleID(SaleID);
                    sale.setBackEmployeeID(getBasicInfo.getOperationID());
                    sale.setStation(getBasicInfo.getStationID());
                    sale.setUserName(data.get("CustomerName"));
                    sale.setTelphone(data.get("Telephone"));
                    sale.setUserType("2");
                    saleID=SaleID;
                    msg=new StringBuilder();
                    msg.append(("-----------------------------------------------\n")) ;
                    msg.append(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n"));
                    msg.append(PrintUtils.printTwoData("收据单", SaleID + "\n"));
                    msg.append(PrintUtils.printTwoData("送气号", UserID + "\n"));
                    msg.append(PrintUtils.printTwoData("姓名", sale.getUserName() + "\n"));
                    msg.append(PrintUtils.printTwoData("联系电话", sale.getTelphone()+ "\n"));
                    msg.append(getBasicInfo.getCompanyName() + "\n\n");//公司
                    SaleCodeDialog t = new SaleCodeDialog(ReturnOrderActivity.this, msg.toString());
                    t.shown();
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 滚动状态改变时调用
             */

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    try {
                        scrolledX = view.getFirstVisiblePosition();
                        Log.i("scroll X", String.valueOf(scrolledX));
                        scrolledY = view.getChildAt(0).getTop();
                        Log.i("scroll Y", String.valueOf(scrolledY));
                    } catch (Exception e) {
                    }
                }
            }

        });

        finish=deliveryDB.GetSaleInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        saleListAdapter=new SaleListAdapter(this,finish);
        listView.setAdapter(saleListAdapter);
    }
    StringBuilder msg = new StringBuilder();
    private int scrolledX = 0;
    private int scrolledY = 0;

    @Override
    public void ChargeBackTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            deliveryDB.ChargeBackStatus(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleID,"8");
            Toast.makeText(ReturnOrderActivity.this, "退单成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ReturnOrderActivity.this, "退单失败", Toast.LENGTH_SHORT).show();

        }
        finish=deliveryDB.GetSaleInfo(getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        saleListAdapter=new SaleListAdapter(this,finish);
        listView.setAdapter(saleListAdapter);
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {

    }

    /**
     * 打印交易信息
     */
    public class PrintCodeTask extends AsyncTask<Void, Void, Void> {
        private Context context = null;
        private BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        private boolean isConnection = false;
        private BluetoothDevice device = null;
        private BluetoothSocket bluetoothSocket = null;
        private OutputStream outputStream;
        private  final UUID uuid = UUID
                .fromString("00001101-0000-1000-8000-00805F9B34FB");
        SharedPreferences deviceSetting;
        String bluetoothadd = "";// 蓝牙MAC
        private ProgressDialog dialog;
        public PrintCodeTask(Context context) {
            this.context = context;
            deviceSetting = context.getSharedPreferences("DeviceSetting", 0);
            bluetoothadd = deviceSetting.getString("BlueToothAdd", "");// 蓝牙MAC
            dialog = new ProgressDialog(context);
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
//                int pCount = 1;
//                pCount = Integer.parseInt("1");
                //测试(最新测试)
                String Intret = connectBT();
                PrintUtils.setOutputStream(outputStream);
                for(int a=0;a<pCount;a++){
                    PrintUtils.selectCommand(PrintUtils.RESET);
                    PrintUtils.selectCommand(PrintUtils.LINE_SPACING_DEFAULT);
                    PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
                    PrintUtils.selectCommand(PrintUtils.BOLD);
                    PrintUtils.printText("上海奉贤燃气股份有限公司" + "\n");//公司
                    PrintUtils.printText("--------------------------------\n");
//                    PrintUtils.printText(companyInfo.getString("Company","") + "\n");//公司
                    PrintUtils.printText("销售收据\n");
                    PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
//                    PrintUtils.selectCommand(PrintUtils.NORMAL);
//                    PrintUtils.selectCommand(PrintUtils.ALIGN_CENTER);
                    PrintUtils.selectCommand(PrintUtils.NORMAL);
                    PrintUtils.selectCommand(PrintUtils.ALIGN_LEFT);
                    PrintUtils.printText(PrintUtils.printTwoData("收据单", sale.getSaleID() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("送气号", sale.getCustomerID() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("姓名",sale.getUserName() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("客户类型", sale.getRemark() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("联系电话", sale.getTelphone() + "\n"));
//                    PrintUtils.printText(PrintUtils.printTwoData("联系电话", CustomerInfo.getTelphone() + "\n"));
                    PrintUtils.printText(sale.getSaleAddress() + "\n");//客户地址
                    PrintUtils.printText("--------------------------------\n");
                    PrintUtils.selectCommand(PrintUtils.BOLD);
                    PrintUtils.printText(PrintUtils.printThreeData("项目", "数量", "金额\n"));
                    int size = details.size();
                    for (int i = 0; i < size; i++) {

                        PrintUtils.printText(PrintUtils.printThreeData(details.get(i).getQPName(), ""+details.get(i).getSendNum(), details.get(i).getQPPrice() + "\n"));
                    }

                    PrintUtils.selectCommand(PrintUtils.BOLD_CANCEL);
                    PrintUtils.printText("--------------------------------\n");
                    PrintUtils.printText(PrintUtils.printTwoData("合计", sale.getRealPirce() + "\n"));
                    String EmptyNo = "";
                    EmptyNo = sale.getReceiveQP();
                    if (!EmptyNo.equals("")) {
                        PrintUtils.printText("空瓶\n");
                        if (EmptyNo.contains(",")) {
                            String[] tmp = EmptyNo.split(",");
                            int length = tmp.length;
                            for (int i = 0; i < length; i++) {
                                PrintUtils.printText(PrintUtils.printTwoData("", tmp[i] + "\n"));
                            }
                        } else {
                            PrintUtils.printText(PrintUtils.printTwoData("", EmptyNo + "\n"));
                        }
                    }
                    String FullNo = "";
                    FullNo = sale.getSendQP();
                    if (!FullNo.equals("")) {
                        PrintUtils.printText("满瓶\n");
                        if (FullNo.contains(",")) {
                            String[] tmp = FullNo.split(",");
                            int length = tmp.length;
                            for (int i = 0; i < length; i++) {
                                PrintUtils.printText(PrintUtils.printTwoData("", tmp[i] + "\n"));
                            }
                        } else {
                            PrintUtils.printText(PrintUtils.printTwoData("", FullNo + "\n"));
                        }
                    }

                    PrintUtils.printText("--------------------------------\n");
                    PrintUtils.printText(PrintUtils.printTwoData("配送日期", sale.getFinishTime() + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("送瓶员", getBasicInfo.getOperationName() + "\n"));
                    PrintUtils.printText("--------------------------------\n");//一般隐患
                    PrintUtils.printText("--------------------------------\n");//用户签名
                    PrintUtils.printText(PrintUtils.printTwoData("用户签名", "" + "\n"));
                    PrintUtils.printText("\n\n");//一般隐患
                    PrintUtils.printText("--------------------------------\n");//用户签名
//                    PrintUtils.printText(PrintUtils.printTwoData("应急投诉：67183737", "" + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("送气热线：67183737", "" + "\n"));
                    PrintUtils.printText(PrintUtils.printTwoData("如需发票，一次收据换取", "" + "\n"));
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
            btn_sure.setText("退单");
            btn_cansel= root.findViewById(R.id.btn_cansel);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dimiss();
                    CharkBackDialog charkBackDialog=new CharkBackDialog(ReturnOrderActivity.this);
                    charkBackDialog.shown();
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
            info.setText(msg);
            mGoodsDialog.show();
        }
        public void dimiss(){
            mGoodsDialog.dismiss();
        }
    }

    /**
     * 退单原因
     */
    public class CharkBackDialog {
        private Context context;
        private Dialog mGoodsDialog2;//
        private LinearLayout root;
        private RadioButton radioButton1,radioButton2,radioButton3,radioButton4,radioButton5;
        private RadioGroup RadioGroup01;
        private EditText edt_reason;
        private Button btn_sure,btn_cansel;
        public CharkBackDialog(final Context context){
            this.context=context;
            sale.setBackInfo("");
            mGoodsDialog2 = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.activity_return_order, null);
            btn_sure=root.findViewById(R.id.btn_print);
            btn_sure.setText("退单");
            RadioGroup01=root.findViewById(R.id.RadioGroup01);
             RadioGroup01.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

                 @Override
                 public void onCheckedChanged(RadioGroup group, int checkedId) {
                     if (group.getId() == R.id.RadioGroup01) {
                         if (checkedId == R.id.radioButton1) {
                             sale.setBackInfo("客户想取消");
                             edt_reason.setVisibility(View.GONE);
                             edt_reason.setText("");
                         } else if (checkedId == R.id.radioButton2) {
                             sale.setBackInfo("地址不符");
                             edt_reason.setVisibility(View.GONE);
                             edt_reason.setText("");
                         } else if(checkedId == R.id.radioButton3){
                             sale.setBackInfo("订单信息错误");
                             edt_reason.setVisibility(View.GONE);
                             edt_reason.setText("");
                         }else if(checkedId == R.id.radioButton4){
                             sale.setBackInfo("其他");
                             edt_reason.setVisibility(View.GONE);
                             edt_reason.setText("");
                         }else if(checkedId == R.id.radioButton5){
                             edt_reason.setVisibility(View.VISIBLE);
                         }
                     }
                 }
             });
            radioButton1=root.findViewById(R.id.radioButton1);
            radioButton2=root.findViewById(R.id.radioButton2);
            radioButton3=root.findViewById(R.id.radioButton3);
            radioButton4=root.findViewById(R.id.radioButton4);
            radioButton5=root.findViewById(R.id.radioButton5);
            edt_reason=root.findViewById(R.id.edt_reason);
            edt_reason.setText("");
            btn_cansel= root.findViewById(R.id.btn_cansel);

            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dimiss();
                    if(radioButton5.isChecked()){
                        sale.setBackInfo(edt_reason.getText().toString());
                    }

              if(!sale.getBackInfo().equals("")){
                  String request= JSONUtils.toJsonWithGson(sale);

                    HsicMessage hsicMessage=new HsicMessage();
                  hsicMessage.setRespMsg(request);
                  request=  JSONUtils.toJsonWithGson(hsicMessage);
                  Log.e("request",request);
                    new ChargeBackTask(ReturnOrderActivity.this,ReturnOrderActivity.this).execute(request);
              }else{
                  Toast.makeText(ReturnOrderActivity.this, "请输入退单原因", Toast.LENGTH_SHORT).show();
                  return;
              }


                }
            });
            btn_cansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoodsDialog2.dismiss();
                }
            });
            mGoodsDialog2.setContentView(root);
            Window dialogWindow = mGoodsDialog2.getWindow();
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
            mGoodsDialog2.setOnKeyListener(new DialogInterface.OnKeyListener(){

                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        if(getWindow().getAttributes().softInputMode==WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
                        {
                            //隐藏软键盘
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        }else{
                            mGoodsDialog2.dismiss();
                        }
                    }
                    return false;
                }
            });
        }
        public  void shown(){
            mGoodsDialog2.show();
        }
        public void dimiss(){
            mGoodsDialog2.dismiss();
        }
    }
    private static Boolean isExit = false;
    // 定时触发器
    private static Timer tExit = null;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if (isExit == false) {
//                    isExit = true;
//                    if (tExit != null) {
//                        tExit.cancel(); // 将原任务从队列中移除
//                    }
//                    // 重新实例一个定时器
//                    tExit = new Timer();
//                    TimerTask task = new TimerTask() {
//                        @Override
//                        public void run() {
//                            isExit = false;
//                        }
//                    };
//                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                    // 延时两秒触发task任务
//                    tExit.schedule(task, 2000);
//                } else {
//                    finish();
//                    System.exit(0);
//                }
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
