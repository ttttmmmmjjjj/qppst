package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hsic.adapter.SaleListAdapter;
import com.hsic.bean.SaleAll;
import com.hsic.bean.SaleDetail;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bluetooth.Barcode;
import com.hsic.bluetooth.GPrinterCommand;
import com.hsic.bluetooth.PrintPic;
import com.hsic.bluetooth.PrintQueue;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.db.DeliveryDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveryFinishActivity extends AppCompatActivity {
    DeliveryDB deliveryDB;
    SaleAll saleAll;
    GetBasicInfo getBasicInfo;
    private String saleID;
    List<Map<String,String>> finish;
    List<SaleDetail> details;
    ListView listView;
    SaleListAdapter saleListAdapter;
    TextView txt_title;
    String PayMode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_finish);
        txt_title=this.findViewById(R.id.txt_title);
        deliveryDB=new DeliveryDB(this);
        getBasicInfo=new GetBasicInfo(this);
        listView=this.findViewById(R.id.lv_saleInfo);
        txt_title.setText("订单补打");
        saleAll=new SaleAll();
        details=new ArrayList<>();
        finish=new ArrayList<Map<String, String>>() ;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try{
                    if(finish.size()>0){
                        Map<String, String> data =  (Map<String, String>)adapterView.getItemAtPosition(position);
                        String UserID=data.get("UserID");
                        String SaleID=data.get("SaleID");
                        saleID=SaleID;
                        saleAll=deliveryDB.GetPrint(getBasicInfo.getOperationID(),saleID);
                        details=deliveryDB.GetSaleDetailByP(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleID);
                        String PayType="";
                        PayType=saleAll.getSale().getPayType();
                        if(PayType.equals("0")){
                            PayMode="现金";
                        }else  if(PayType.equals("1")){
                            PayMode="气票";
                        }else{
                            PayMode="月结";
                        }
                        msg=new StringBuilder();
                        msg.append(("---------------------------------------------------------------\n")) ;
                        msg.append(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n"));
                        msg.append(PrintUtils.printTwoData("销售单号", SaleID + "\n"));
                        msg.append(PrintUtils.printTwoData("客户编号", UserID + "\n"));
                        msg.append(PrintUtils.printTwoData("姓名", saleAll.getSale().getUserName() + "\n"));
                        msg.append(PrintUtils.printTwoData("客户类型", saleAll.getUserXJInfo().getCustomerTypeName() + "\n"));
                        msg.append(PrintUtils.printTwoData("联系电话", saleAll.getSale().getTelphone()+ "\n"));
                        msg.append(getBasicInfo.getCompanyName()+ "\n");//公司
                        msg.append(("---------------------------------------------------------------\n")) ;
                        msg.append("回收瓶：" + "\n");
                        msg.append(saleAll.getSale().getReceiveQP() + "\n");
                        msg.append("配送瓶：" + "\n");
                        msg.append(saleAll.getSale().getSendQP() + "\n");
                        msg.append(("---------------------------------------------------------------\n")) ;
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
                        msg.append(PrintUtils.printTwoData("交易金额",saleAll.getSale().getRealPirce()+"\n"));
                        msg.append(PrintUtils.printTwoData("支付方式", PayMode + "\n"));
                        DeliveryFinishActivity.SaleCodeDialog t = new DeliveryFinishActivity.SaleCodeDialog(DeliveryFinishActivity.this, msg.toString());
                        t.shown();
                    }else{

                    }
                }catch(Exception ex){
                    ex.toString();
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

        finish=deliveryDB.GetSaleInfoFinish(getBasicInfo.getOperationID(),getBasicInfo.getStationID());
        saleListAdapter=new SaleListAdapter(this,finish);
        listView.setAdapter(saleListAdapter);
    }
    StringBuilder msg = new StringBuilder();
    private int scrolledX = 0;
    private int scrolledY = 0;
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
//                    new PrintCodeTask(context).execute();
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
            info.setText(msg);
            mGoodsDialog.show();
        }
        public void dimiss(){
            mGoodsDialog.dismiss();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    /**
     * 打印交易信息
     */
    private void print(){
        int pCount = 1;
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
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("销售单号", saleAll.getSale().getSaleID() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户编号", saleAll.getSale().getCustomerID() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("供应站点", getBasicInfo.getStationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("姓名",saleAll.getSale().getUserName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("客户类型", saleAll.getUserXJInfo().getCustomerTypeName() + "\n")));
            String CustomerType=saleAll.getUserXJInfo().getCustomerType();
            String CustomerCardID=saleAll.getUserXJInfo().getCustomerCardID();
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
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("联系电话", saleAll.getUserXJInfo().getTelephone() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("来电电话", saleAll.getSale().getTelphone()) + "\n"));
            printBytes.add(PrintUtils.str2Byte(saleAll.getSale().getSaleAddress() + "\n"));//客户地址
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
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("合计", saleAll.getSale().getRealPirce() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("支付方式", PayMode + "\n")));
            String EmptyNo = "";
            EmptyNo = saleAll.getSale().getReceiveQP();
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
            FullNo = saleAll.getSale().getSendQP();
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
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("配送日期", saleAll.getSale().getFinishTime() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("送瓶员", getBasicInfo.getOperationName() + "\n")));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("送气热线："+getBasicInfo.getCompanyPhone(), "" + "\n")));
            String installType=saleAll.getSale().getAZType();
            if(installType.equals("0")){
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("接装类型", "自装" + "\n")));
            }else{
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("接装类型", "公司装" + "\n")));
            }
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            /***  打印安检信息  ****/
            String inspectionInfo=deliveryDB.getInspectionStr(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),saleID,getBasicInfo.getOperationName());
            printBytes.add(PrintUtils.str2Byte(inspectionInfo));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte("--------------------------------\n"));
            printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("用户签名", "" + "\n")));
            printBytes.add(PrintUtils.str2Byte("\n\n"));
            String Url=saleAll.getSale().getPS();
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
                printBytes.add(PrintUtils.str2Byte("电子发票二维码\n"));
            }else{
                printBytes.add(PrintUtils.str2Byte(PrintUtils.printTwoData("如需发票，一次收据换取", "" + "\n")));
            }
            printBytes.add(PrintUtils.str2Byte("\n\n\n\n\n"));
        }
        PrintQueue.getQueue(getApplicationContext()).add(printBytes);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrintQueue.getQueue(getApplicationContext()).disconnect();//
    }
}
