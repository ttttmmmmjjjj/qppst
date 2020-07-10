package com.hsic.tmj.qppst;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hsic.adapter.DragDelListAdaper;
import com.hsic.bean.UserReginCode;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.DeliveryDB;
import com.hsic.nfc.GetTag;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.sy.dragdellistview.DragDelListView;
import com.hsic.tmj.wheelview.QPType;
import com.hsic.tmj.wheelview.WheelView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReadQPActivity extends AppCompatActivity implements com.hsic.sy.dialoglibrary.OnItemClickListener,
        OnDismissListener {
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    List<UserReginCode> UserReginCode_List;
    Toolbar toolbar;
    GetBasicInfo basicInfo;
    List<UserReginCode> EmptyList;//暂存已扫描到的空瓶
    List<UserReginCode> FullList;//暂存已扫描到的满瓶
    DeliveryDB dbData;
    NfcAdapter mAdapter;
    /**
     * 选择钢瓶种类对话框
     */
    android.support.v7.app.AlertDialog QPTypeDialog;
    List<com.hsic.tmj.wheelview.QPType> qpTypes = new ArrayList<com.hsic.tmj.wheelview.QPType>();
    private Button  btn_input;
    private Dialog mGoodsDialog;//
    private LinearLayout root;
    private EditText edt_qpTag;
    private DragDelListView mListView;
    private String Operation, GoodsCount;
    private int QPCount;
    private String SaleID;
    AlertView s;
    private String wghqp;//未归还气瓶

    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cansel:
                    mGoodsDialog.dismiss();
                    break;
                case R.id.btn_sure://保存数据
                    String readData = edt_qpTag.getText().toString();
                    if (!TextUtils.isEmpty(readData)) {
                        if (readData.length() < 8) {
                            Toast.makeText(getBaseContext(), "瓶号必须为8位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SortData(readData, "I", Operation);
                    } else {
                        Toast.makeText(getBaseContext(), "请输入气瓶号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    edt_qpTag.setText("");
                    mGoodsDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qp);
        toolbar = findViewById(R.id.toolbar5);
        mListView = this.findViewById(R.id.listView01);
        btn_input = this.findViewById(R.id.btn_input);
        InnitGoodsDialog();
        setNFC();
        basicInfo = new GetBasicInfo(ReadQPActivity.this);
        dbData = new DeliveryDB(this);
        UserReginCode_List = new ArrayList<UserReginCode>();
        EmptyList = new ArrayList<UserReginCode>();
        FullList = new ArrayList<UserReginCode>();
        /**
         *设置标签号 缓存
         */
        Intent intent = getIntent();
        if (intent != null) {
            String tag = intent.getStringExtra("QPNO");
            Bundle bundle = intent.getBundleExtra("bundle");
            Operation = intent.getStringExtra("Operation");
            GoodsCount = intent.getStringExtra("QPCount");
            SaleID = intent.getStringExtra("SaleID");
            QPCount = Integer.parseInt(GoodsCount);
            EmptyList = new ArrayList<UserReginCode>();
            FullList = new ArrayList<UserReginCode>();
            if (Operation.equals("E")) {
                EmptyList = (ArrayList<UserReginCode>) bundle.getSerializable("EmptyList");
                if (EmptyList.size() > 0) {
                    int size = EmptyList.size();
                    UserReginCode_List = new ArrayList<UserReginCode>();
                    for (int a = 0; a < size; a++) {
                        UserReginCode_List.add(EmptyList.get(a));
                    }
                } else {
                    UserReginCode_List = new ArrayList<UserReginCode>();
                }
                toolbar.setTitle("空瓶" + "【" + "点击瓶号，左划可删除" + "】");
            } else {
                toolbar.setTitle("满瓶" + "【" + "点击瓶号，左划可删除" + "】");
                FullList = (ArrayList<UserReginCode>) bundle.getSerializable("FullList");
                if (FullList.size() > 0) {
                    UserReginCode_List = new ArrayList<UserReginCode>();
                    int size = FullList.size();
                    for (int a = 0; a < size; a++) {
                        UserReginCode_List.add(FullList.get(a));
                    }
                } else {
                    UserReginCode_List = new ArrayList<UserReginCode>();
                }
            }
        }
        mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
        qpTypes = dbData.GetGoodsType(basicInfo.getOperationID(), basicInfo.getStationID(), SaleID);
        btn_input.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mGoodsDialog.show();
            }
        });
        mGoodsDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mGoodsDialog.dismiss();
                }
                return false;
            }

        });
        s=new AlertView("提示", "此瓶不在未归还瓶中，是否继续", "取消", new String[]{"确定"},
                null, this, AlertView.Style.Alert, this);
        List<Map<String, String>> saleInfo;//根据用户编号查询订单相关信息
        saleInfo = dbData.GetSaleInfoBySaleID(basicInfo.getOperationID(), basicInfo.getStationID(), SaleID);
        wghqp=saleInfo.get(0).get("wghqp");
    }

    private void InnitGoodsDialog() {
        mGoodsDialog = new Dialog(this, R.style.my_dialog);
        root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.qp_tag_input_layout, null);
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        edt_qpTag = (EditText) root.findViewById(R.id.edt_brand);
        edt_qpTag.setText("");
        mGoodsDialog.setContentView(root);
        Window dialogWindow = mGoodsDialog.getWindow();
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
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        try {
            mAdapter.disableForegroundDispatch(this);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showDiag();
            }
        }

        try {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (QPTypeDialog != null) {
                QPTypeDialog.dismiss();
            }
            setBackData();
            ReadQPActivity.this.finish();
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
        // TODO Auto-generated method stub

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        resolveIntent(intent);
    }

    /**
     * 返回给上一页面的数据
     */
    private void setBackData() {
        String userReginCodeStr = "";
        String ReceiveByInput;//手输回收瓶标识
        String DeliverByInput;//手输发出瓶标识
        StringBuffer userReginCodeStrBuff = new StringBuffer();
        StringBuffer ReceiveByInputStrBuff = new StringBuffer();
        StringBuffer DeliverByInputStrBuff = new StringBuffer();
        int size = UserReginCode_List.size();
        EmptyList = new ArrayList<>();
        FullList = new ArrayList<>();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String tmp = UserReginCode_List.get(i).getUserRegionCode();
                if (Operation.equals("E")) {
                    String tmp2=UserReginCode_List.get(i).getReceiveQPByInput();
                    if(!tmp2.equals("")){
                        ReceiveByInputStrBuff.append(UserReginCode_List.get(i).getReceiveQPByInput() + ",");
                    }
                    EmptyList.add(UserReginCode_List.get(i));

                } else {
                    String tmp3=UserReginCode_List.get(i).getSendQPByInput();
                    if(!tmp3.equals("")){
                        DeliverByInputStrBuff.append(UserReginCode_List.get(i).getSendQPByInput() + ",");
                    }
                    FullList.add(UserReginCode_List.get(i));
                }
                userReginCodeStrBuff.append(UserReginCode_List.get(i).getUserRegionCode() + ",");
            }
            userReginCodeStr = userReginCodeStrBuff.substring(0, userReginCodeStrBuff.length() - 1);
            String tmp = ReceiveByInputStrBuff.toString();
            if (!tmp.equals("")) {
                if (tmp.contains(",")) {
                    ReceiveByInput = ReceiveByInputStrBuff.substring(0, ReceiveByInputStrBuff.length() - 1);
                } else {
                    ReceiveByInput = tmp;
                }
            } else {
                ReceiveByInput = "";
            }
            tmp = DeliverByInputStrBuff.toString();
            if (!tmp.equals("")) {
                if (tmp.contains(",")) {
                    DeliverByInput = DeliverByInputStrBuff.substring(0, DeliverByInputStrBuff.length() - 1);
                } else {
                    DeliverByInput = tmp;
                }
            } else {
                DeliverByInput = "";
            }
            Intent data = new Intent();
            if (Operation.equals("E")) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("EmptyList", (Serializable) EmptyList);
                data.putExtra("bundle", bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("FullList", (Serializable) FullList);
                data.putExtra("bundle", bundle);
            }

            data.putExtra("userReginCode", userReginCodeStr);
            data.putExtra("ReceiveByInput", ReceiveByInput);
            data.putExtra("DeliverByInput", DeliverByInput);
            setResult(5, data);

        } else {
            Intent data = new Intent();
            if (Operation.equals("E")) {
                EmptyList = new ArrayList<UserReginCode>();
                Bundle bundle = new Bundle();
                bundle.putSerializable("EmptyList", (Serializable) EmptyList);
                data.putExtra("bundle", bundle);
            } else {
                FullList = new ArrayList<UserReginCode>();
                Bundle bundle = new Bundle();
                bundle.putSerializable("FullList", (Serializable) FullList);
                data.putExtra("bundle", bundle);
            }
            data.putExtra("userReginCode", "");
            data.putExtra("ReceiveByInput", "");
            data.putExtra("DeliverByInput", "");
            setResult(5, data);
        }
    }

    // NFC忙篓隆氓聺聴
    private void setNFC() {
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                showDiag();
            }
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass())
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            // Setup an intent filter for all MIME based dispatches
            IntentFilter ndef = new IntentFilter(
                    NfcAdapter.ACTION_TECH_DISCOVERED);

            try {
                ndef.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            mFilters = new IntentFilter[]{ndef,};

            // Setup a tech list for all NfcF tags
            mTechLists = new String[][]{
                    new String[]{MifareClassic.class.getName()},
                    new String[]{NfcA.class.getName()},
                    new String[]{NfcB.class.getName()},
                    new String[]{NfcF.class.getName()},
                    new String[]{NfcV.class.getName()},
                    new String[]{Ndef.class.getName()},
                    new String[]{NdefFormatable.class.getName()},
                    new String[]{MifareUltralight.class.getName()},
                    new String[]{IsoDep.class.getName()}};

            Intent intent = getIntent();
            resolveIntent(intent);
        }
    }

    void resolveIntent(Intent intent) {
        try {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
                GetTag getTag = new GetTag();
                String[] ret = new String[2];
                String readData = "";
                ret = getTag.getTag(intent, 3, 1, basicInfo.getDeviceType());
                readData = ret[1];
                if (readData != null) {
                    if (!readData.equals("")) {
                        SortData(readData, "", Operation);
                    }
                }

            }
        } catch (Exception ex) {
            ex.toString();
        }

    }

    private void SortData(String readData, String Mode, String Tag) {
        UserReginCode userReginCode = new UserReginCode();
        int size = UserReginCode_List.size();
        /**
         * 判断该瓶是否存在
         */
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (readData.equals(UserReginCode_List.get(i).getUserRegionCode())) {
                    if (QPTypeDialog != null) {
                        QPTypeDialog.dismiss();
                    }
                    return;
                }
            }
        }
        if (Operation.equals("E")) {
            if (qpTypes.size() > 1) {
                QPType(userReginCode, readData, Mode, Tag);
            } else {
                setQP(userReginCode, readData, Mode, Tag);
            }
        } else {
            //满瓶判断瓶数量
            if (size != QPCount) {
                if (qpTypes.size() > 1) {
                    QPType(userReginCode, readData, Mode, Tag);
                } else {
                    setQP(userReginCode, readData, Mode, Tag);
                }
            } else {
                Toast.makeText(this, "超过数量", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDiag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("NFC设备未打开，是否去打开？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    UserReginCode userReginCodeTmp;//用于判断是否在未归还瓶中
    private void QPType(final UserReginCode userReginCode, final String readData, final String Mode, final String Tag) {
        View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
        WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        wv.setOffset(2);
        wv.setItems(qpTypes);
        wv.setSeletion(3);
        wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, QPType item) {
                super.onSelected(selectedIndex, item);
                int count1=Integer.parseInt(item.getQPNum());//该类型瓶的数量
                if (QPTypeDialog != null) {
                    QPTypeDialog.dismiss();
                }
                /**
                 * 判断该瓶是否存在
                 */
                int size = UserReginCode_List.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if (readData.equals(UserReginCode_List.get(i).getUserRegionCode())) {
                            if(QPTypeDialog.isShowing()){
                                QPTypeDialog.dismiss();
                            }
                            if (QPTypeDialog != null) {
                                QPTypeDialog.dismiss();
                            }
                            return;
                        }
                    }
                }
                /**
                 * 判断该类型的瓶是否扫足够
                 */
                if(size>0){
                    int count2=0;
                    for(int a=0;a<size;a++){
                        if(UserReginCode_List.get(a).getQpType().equals(item.getQPType())){
                            count2++;
                        }
                    }
                    if(count2>=count1){
                        Toast.makeText(ReadQPActivity.this, "该类型的瓶数量已足够", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                }
                userReginCode.setQpType(item.getQPType());
                userReginCode.setQpName(item.getQPName());
                userReginCode.setUserRegionCode(readData);
                if (Mode.equals("I")) {
                    //手输
                    if (Tag.equals("E")) {
                        userReginCode.setReceiveQPByInput(readData);
                        //未归还瓶判断
                        if(!wghqp.equals("")){
                            if(!wghqp.contains(readData)){
                                //不在
                                userReginCodeTmp=userReginCode;
                                s.show();
                            }else{
                                //在
                                UserReginCode_List.add(userReginCode);
                                mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                            }
                        }else{
                            UserReginCode_List.add(userReginCode);
                            mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                        }

                    } else {
                        userReginCode.setSendQPByInput(readData);
                        UserReginCode_List.add(userReginCode);
                        mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                    }
                } else {
                    //读NFC
                    userReginCode.setReceiveQPByInput("");
                    userReginCode.setSendQPByInput("");
                    if(Tag.equals("E")){
                        //空瓶
                        if(!wghqp.equals("")){
                            if(!wghqp.contains(readData)){
                                //不在归还瓶中
                                userReginCodeTmp=userReginCode;
                                s.show();
                            }else{
                                //在归还瓶中
                                UserReginCode_List.add(userReginCode);
                                mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                            }
                        }else{
                            UserReginCode_List.add(userReginCode);
                            mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                        }
                    }else{
                        UserReginCode_List.add(userReginCode);
                        mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                    }

                }

            }
        });
        QPTypeDialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("选择扫描的钢瓶种类")
                .setView(outerView)
                .show();
    }

    private void setQP(UserReginCode userReginCode, String readData, String Mode, String Tag) {
        /**
         * 判断该瓶是否存在
         */
        int size = UserReginCode_List.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if (readData.equals(UserReginCode_List.get(i).getUserRegionCode())) {
                    return;
                }
            }
        }
        userReginCode.setQpType(qpTypes.get(0).getQPType());
        userReginCode.setQpName(qpTypes.get(0).getQPName());
        userReginCode.setUserRegionCode(readData);
        if (Mode.equals("I")) {
            if (Tag.equals("E")) {
                //空瓶
                userReginCode.setReceiveQPByInput(readData);
                if(!wghqp.equals("")){
                    if(wghqp.contains(readData)){
                        UserReginCode_List.add(userReginCode);
                        mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));

                    }else{
                        userReginCodeTmp=userReginCode;
                        s.show();
                    }
                }else{
                    UserReginCode_List.add(userReginCode);
                    mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                }

            } else {
                //满瓶
                userReginCode.setSendQPByInput(readData);
                UserReginCode_List.add(userReginCode);
                mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
            }
        } else {
            //读卡
            userReginCode.setReceiveQPByInput("");
            userReginCode.setSendQPByInput("");
            if(Tag.equals("E")){
                if(!wghqp.equals("")){
                    if(wghqp.contains(readData)){
                        UserReginCode_List.add(userReginCode);
                        mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));

                    }else{
                        userReginCodeTmp=userReginCode;
                        s.show();
                    }
                }else{
                    UserReginCode_List.add(userReginCode);
                    mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
                }

            }else{
                UserReginCode_List.add(userReginCode);
                mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
            }

        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if(o==s && position==0){
            s.dismiss();
            UserReginCode_List.add(userReginCodeTmp);
            mListView.setAdapter(new DragDelListAdaper(getBaseContext(), UserReginCode_List));
        }else{
            s.dismiss();
        }
    }
}
