package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.adapter.MenuGridAdapter;
import com.hsic.bean.Count;
import com.hsic.bean.DKSale;
import com.hsic.bean.EmployeeInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DKSaleDB;
import com.hsic.db.DeliveryDB;
import com.hsic.dialog.CountDialog;
import com.hsic.listener.ImplModifyPassWord;
import com.hsic.listener.ImplSearchAssignDKTask;
import com.hsic.task.ModifyPassWordTask;
import com.hsic.task.SearchAssignDKTask;
import com.hsic.tmj.gridview.MyGridView;
import com.hsic.utils.MD5Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计
 */
public class OtherServiceActivity extends AppCompatActivity implements ImplModifyPassWord, ImplSearchAssignDKTask {
    private MyGridView gridview;
    private TextView station, info;
    GetBasicInfo getBasicInfo;
    String EmployeeID, StationID;
    DeliveryDB deliveryDB;
    List<Sale> sales;
    Count count;
    List<Count> mDataCount;
    DKSaleDB dkSaleDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count);
        getBasicInfo = new GetBasicInfo(this);
        deliveryDB = new DeliveryDB(this);
        dkSaleDB=new DKSaleDB(this);
        EmployeeID = getBasicInfo.getOperationID();//登录员工号
        StationID = getBasicInfo.getStationID();
        innitView();
        InnitInputDialog();
    }

    private void innitView() {
        gridview = (MyGridView) findViewById(R.id.gridview);
        String[] img_text = {"订单补打", "退单管理", "配送统计", "整改统计", "安检统计", "修改密码", "开户安检","报修"};
        int[] imgs = {
                R.drawable.search, R.drawable.i, R.drawable.c,
                R.drawable.gas, R.drawable.order,
                R.drawable.truck, R.drawable.h,
                R.drawable.search, R.drawable.c};
        gridview.setAdapter(new MenuGridAdapter(this, img_text, imgs));
        info = (TextView) this.findViewById(R.id.info);
        info.setText(getBasicInfo.getOperationName());
        station = (TextView) this.findViewById(R.id.station);
        station.setText(getBasicInfo.getStationName());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch ((int) i) {
                    case 0:
                        Intent login = new Intent(OtherServiceActivity.this, DeliveryFinishActivity.class);
                        startActivity(login);
                        break;

                    case 1:
                        Intent login3 = new Intent(OtherServiceActivity.this, ReturnOrderActivity.class);
                        startActivity(login3);
                        break;
                    case 2:
                        int QPCount = deliveryDB.SaleQPCount(EmployeeID, StationID);
                        count = new Count();
                        count.setName("配送数量");
                        count.setValue("" + QPCount);
                        mDataCount = new ArrayList<>();
                        mDataCount.add(count);
                        new CountDialog(OtherServiceActivity.this, R.style.dialog, mDataCount, new CountDialog.OnCloseListener() {

                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                dialog.dismiss();
                            }
                        }).setTitle("配送数量说明").show();
                        break;
                    case 3:
                        sales = new ArrayList<>();
                        sales = deliveryDB.RectifyFinishCount(EmployeeID, StationID);
                        count = new Count();
                        count.setName("整改数量");
                        count.setValue("" + sales.size());
                        List<Count> mDataCount;
                        mDataCount = new ArrayList<>();
                        mDataCount.add(count);
                        new CountDialog(OtherServiceActivity.this, R.style.dialog, mDataCount, new CountDialog.OnCloseListener() {

                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                dialog.dismiss();
                            }
                        }).setTitle("整改数量说明").show();
                        break;
                    case 4:
                        sales = new ArrayList<>();
                        sales = deliveryDB.SearchFinishCount(EmployeeID, StationID);
                        count = new Count();
                        count.setName("安检数量");
                        count.setValue("" + sales.size());
                        mDataCount = new ArrayList<>();
                        mDataCount.add(count);
                        new CountDialog(OtherServiceActivity.this, R.style.dialog, mDataCount, new CountDialog.OnCloseListener() {

                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                dialog.dismiss();
                            }
                        }).setTitle("安检数量说明").show();

                        break;
                    case 5://修改密码
                        ModifyPassDialog saleCodeDialog = new ModifyPassDialog(OtherServiceActivity.this);
                        saleCodeDialog.shown();
                        break;
                    case 6://开户安检
//                        inputDialog.show();
                        break;
                    case 7://报修
                        break;
                }
            }
        });
    }

    /**
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void ModifyPassWordTaskEnd(HsicMessage tag) {
        if (tag.getRespCode() == 0) {
            Toast.makeText(OtherServiceActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(OtherServiceActivity.this, tag.getRespMsg(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void SearchAssignDKTaskEnd(HsicMessage tag) {
        if (tag.getRespCode() == 0) {
            DKSale dkSale= JSONUtils.toObjectWithGson(tag.getRespMsg(),DKSale.class);
            dkSaleDB.insertData(dkSale,StationID,EmployeeID);
            msg = new StringBuilder();
            msg.append("---------------------------------------------------------------\n");
            msg.append("用户编号："+dkSale.getCustomerID()+"\n");
            msg.append("用户类型："+dkSale.getDKSaleID()+"\n");
            msg.append("用户姓名："+dkSale.getCustomerName()+"\n");
            msg.append("用户地址："+dkSale.getAddress()+"\n");
            msg.append("用户电话："+dkSale.getTelphone()+"\n");
            String s=msg.toString();
            UserInfoDialog userInfoDialog=new UserInfoDialog(this,s);
            userInfoDialog.shown();
        } else {

        }
    }

    /***
     *修改密码
     */
    public class ModifyPassDialog {
        private Context context;
        private Dialog modifyPassDialog;//
        private LinearLayout root;
        private EditText edt_pass1, edt_pass2;
        private Button btn_sure, btn_cansel;

        public ModifyPassDialog(final Context context) {
            this.context = context;
            modifyPassDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.staff_pass_modify_dialog, null);
            edt_pass1 = root.findViewById(R.id.edt_pass1);
            edt_pass1.setText(getBasicInfo.getPass());
            edt_pass2 = root.findViewById(R.id.edt_pass2);
            btn_sure = root.findViewById(R.id.btn_Modify);
            btn_cansel = root.findViewById(R.id.btn_cansel);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pass1 = edt_pass1.getText().toString();
                    String pass2 = edt_pass2.getText().toString();
                    if (TextUtils.isEmpty(pass2)) {
                        Toast.makeText(OtherServiceActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dimiss();
                    try {
                        EmployeeInfo employeeInfo = new EmployeeInfo();
                        employeeInfo.setPrePassword(MD5Util.getFileMD5String(pass1));
                        employeeInfo.setPassword(MD5Util.getFileMD5String(pass2));
                        employeeInfo.setUserID(EmployeeID);
                        employeeInfo.setStation(StationID);
                        String request = JSONUtils
                                .toJsonWithGson(employeeInfo);// 把员工信息转换成字符串
                        HsicMessage hsicMessage = new HsicMessage();
                        hsicMessage.setRespMsg(request);// 把员工转换成的json字符串赋值给hsicMessage
                        String requestData = JSONUtils
                                .toJsonWithGson(hsicMessage);// 把公司用的hsicMessg转换成json字符串
                        new ModifyPassWordTask(OtherServiceActivity.this, OtherServiceActivity.this)
                                .execute(getBasicInfo.getDeviceID(), requestData);
                    } catch (Exception ex) {

                    }

                }
            });
            btn_cansel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyPassDialog.dismiss();
                }
            });
            modifyPassDialog.setContentView(root);
            Window dialogWindow = modifyPassDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 250; // 新位置Y坐标
            lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            lp.alpha = 9f; // 透明度
            dialogWindow.setAttributes(lp);
            modifyPassDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        modifyPassDialog.dismiss();
                    }
                    return false;
                }
            });
        }

        public void shown() {
            modifyPassDialog.show();
        }

        public void dimiss() {
            modifyPassDialog.dismiss();
        }
    }

    /**
     * 查询代开户安检单子
     */
    String DKSaleID,CustomerID;
    private Dialog inputDialog;//手输登录对话框
    private LinearLayout root;
    private EditText edt_userid;
    private RelativeLayout rl_cbView;
    private CheckBox cb_oldCrad, cb_newCrad;
    private TextView tv_pre;
    StringBuilder msg ;
    private void InnitInputDialog() {
        inputDialog = new Dialog(this, R.style.my_dialog);
        root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.qp_tag_input_layout, null);
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        edt_userid = (EditText) root.findViewById(R.id.edt_brand);
        tv_pre = root.findViewById(R.id.tv_pre);
        tv_pre.setVisibility(View.GONE);
        rl_cbView = root.findViewById(R.id.rl_checkview);
        rl_cbView.setVisibility(View.GONE);
//        cb_oldCrad=root.findViewById(R.id.cb_oldCard);
//        cb_newCrad=root.findViewById(R.id.cb_NewCard);
        edt_userid.setHint("在此输入送气编号");
        edt_userid.setText("");
        inputDialog.setContentView(root);
        Window dialogWindow = inputDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 450; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        inputDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    inputDialog.dismiss();

                }
                return false;
            }

        });

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            String readData = edt_userid.getText().toString();
            if (!TextUtils.isEmpty(readData)) {
                SearchAssignDKTask searchAssignDKTask = new SearchAssignDKTask(OtherServiceActivity.this, OtherServiceActivity.this);
                searchAssignDKTask.execute(getBasicInfo.getDeviceID(), readData);
            }
        }
    };
    /***
     *代开户用户信息详情
     */
    public class UserInfoDialog {
        private Context context;
        private Dialog mGoodsDialog;//
        private LinearLayout root;
        private TextView info,title;
        private String msg;
        private Button btn_sure,btn_cansel;
        public UserInfoDialog(final Context context, String msg){
            this.context=context;
            this.msg=msg;
            mGoodsDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.sale_info_dialog_layout, null);
            info= root.findViewById(R.id.saleinto);
            title=root.findViewById(R.id.title);
            info.setText("");
            title.setText("用户信息");
            btn_sure=root.findViewById(R.id.btn_print);
            btn_sure.setText("代开户安检");
            btn_cansel= root.findViewById(R.id.btn_cansel);
            btn_cansel.setVisibility(View.GONE);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoodsDialog.dismiss();
                    Intent i=new Intent(OtherServiceActivity.this,DKSearchActivity.class);
                    i.putExtra("CustomerID",CustomerID);
                    i.putExtra("DKSaleID",DKSaleID);
                    startActivity(i);
                    OtherServiceActivity.this.finish();
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
//            lp.height = root.getMeasuredHeight();
            lp.height=760;
            lp.alpha = 5f; // 透明度
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
}
