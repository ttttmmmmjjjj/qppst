package com.hsic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.bean.UserInfoDoorSale;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplCancelOrderTask;
import com.hsic.tmj.qppst.R;
import com.hsic.task.CancelOrderTask;

/**
 * Created by Administrator on 2018/8/13.
 */

public class SaleInfoDialogByCansel  {
    private Context context;
    private Dialog mGoodsDialog,SaleInfoDialog;//
    private LinearLayout root;
    private TextView info;
    private String msg;
    private Button btn_sure,btn_cansel;
    ImplCancelOrderTask l;
    private String deviceID;
    GetBasicInfo basicInfo;
    private EditText edt_reason;
    UserInfoDoorSale userInfoDoorSale;

    /**
     *1:下单 ，2:提货 ，3:作废 4：补打
     * @param context
     * @param msg
     */
    public SaleInfoDialogByCansel(final Context context, final ImplCancelOrderTask l,String msg, final UserInfoDoorSale userInfoDoorSale){
        this.context=context;
        this.msg=msg;
        this.l=l;
        basicInfo=new GetBasicInfo(context);
        deviceID=basicInfo.getDeviceID();
        this.userInfoDoorSale=userInfoDoorSale;
        mGoodsDialog = new Dialog(context, R.style.my_dialog);
        root = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.cansel_sale_layout, null);
        info= root.findViewById(R.id.saleinto);
        info.setText("");
        btn_sure=root.findViewById(R.id.btn_print);
        btn_cansel= root.findViewById(R.id.btn_cansel);
        edt_reason=root.findViewById(R.id.edt_reason);
        edt_reason.setHint("请输入作废原因");
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //作废 订单
                String reason=edt_reason.getText().toString();
                if(TextUtils.isEmpty(reason)){
                    Toast.makeText(context, "请输入作废原因", Toast.LENGTH_SHORT).show();
                    return ;
                }
                userInfoDoorSale.setCansel_reason(reason);
                String RequestData= JSONUtils.toJsonWithGson(userInfoDoorSale);
                new CancelOrderTask(context,l, deviceID,RequestData).execute();
                mGoodsDialog.dismiss();
            }
        });
        btn_sure.setText("作废订单");
        btn_cansel.setText("取消作废");
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
