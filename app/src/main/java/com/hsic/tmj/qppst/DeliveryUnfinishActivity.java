package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.hsic.bean.Sale;
import com.hsic.bean.SaleDetail;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeliveryUnfinishActivity extends AppCompatActivity {
    DeliveryDB deliveryDB;
    Sale sale;
    GetBasicInfo getBasicInfo;
    private String saleID;
    List<Map<String, String>> finish;
    List<SaleDetail> details;
    ListView listView;
    SaleListAdapter saleListAdapter;
    TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_finish);
        deliveryDB = new DeliveryDB(this);
        getBasicInfo = new GetBasicInfo(this);
        txt_title = this.findViewById(R.id.txt_title);
        listView = this.findViewById(R.id.lv_saleInfo);
        txt_title.setText("退单管理");
        sale = new Sale();
        details = new ArrayList<>();
        finish = new ArrayList<Map<String, String>>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //testdayin
//                createQRImage("https://cli.im/text?ec3a01e3f91547e9d123fde6a13555ce");
//                new PrintCodeTask(DeliveryUnfinishActivity.this).execute();
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

        finish = deliveryDB.GetSaleInfo(getBasicInfo.getOperationID(), getBasicInfo.getStationID());
        saleListAdapter = new SaleListAdapter(this, finish);
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
        private Button btn_sure, btn_cansel;

        public SaleCodeDialog(final Context context, String msg) {
            this.context = context;
            this.msg = msg;
            mGoodsDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.sale_info_dialog_layout, null);
            info = root.findViewById(R.id.saleinto);
            info.setText("");
            btn_sure = root.findViewById(R.id.btn_print);
            btn_cansel = root.findViewById(R.id.btn_cansel);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dimiss();
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
            mGoodsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        mGoodsDialog.dismiss();
                    }
                    return false;
                }
            });
        }

        public void shown() {
            info.setText(msg);
            mGoodsDialog.show();
        }

        public void dimiss() {
            mGoodsDialog.dismiss();
        }
    }

}
