package com.hsic.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hsic.bean.UserInfoDoorSale;
import com.hsic.tmj.qppst.R;
import com.hsic.task.PrintCodeTask;

/**
 * Created by Administrator on 2018/8/16.
 */

public class SaleCodeDialog {
    private Context context;
    private Dialog mGoodsDialog,SaleInfoDialog;//
    private LinearLayout root;
    private TextView info;
    private String msg;
    private Button btn_sure,btn_cansel;
    private byte[] saleID;
    UserInfoDoorSale userInfoDoorSale;

    public SaleCodeDialog(final Context context, String msg, final byte[] saleID, final UserInfoDoorSale userInfoDoorSale){
        this.context=context;
        this.msg=msg;
        this.saleID=saleID;
        this.userInfoDoorSale=userInfoDoorSale;
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
                String msg=info.getText().toString();
                new PrintCodeTask(context,saleID,userInfoDoorSale).execute();

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
