package com.hsic.tmj.qppst;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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
import android.support.v7.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hsic.bean.HsicMessage;
import com.hsic.bean.UserXJInfo;
import com.hsic.bll.GetBasicInfo;
import com.hsic.db.AJDB;
import com.hsic.db.DeliveryDB;
import com.hsic.listener.ImplDownLoadSearch;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.DownLoadSearchTask;

public class UserLoginByReadActivity extends AppCompatActivity implements
        com.hsic.sy.dialoglibrary.OnItemClickListener, OnDismissListener,ImplDownLoadSearch {
    private Dialog mGoodsDialog;//手输登录对话框
    private LinearLayout root;
    private EditText edt_userid;
    private RelativeLayout rl_cbView;
    private CheckBox cb_oldCrad,cb_newCrad;
    private TextView tv_pre;
    private String  tag;
    StringBuilder msg ;
    /**
     * NFC
     * @param savedInstanceState
     */
    NfcAdapter mAdapter;
    private static PendingIntent mPendingIntent;
    private static IntentFilter[] mFilters;
    private static String[][] mTechLists;
    private Button btn_LoginByScan,btn_LoginByInput;
    GetBasicInfo getBasicInfo;
    AJDB ajdb;
    DeliveryDB deliveryDB;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login_by_read);
        btn_LoginByScan=(Button) this.findViewById(R.id.btn_userbyscan);
        btn_LoginByInput=(Button) this.findViewById(R.id.btn_userbyinput);
        getBasicInfo=new GetBasicInfo(UserLoginByReadActivity.this);
        ajdb=new AJDB(this);
        deliveryDB=new DeliveryDB(this);
        InnitGoodsDialog();
        Intent i=getIntent();//上门配送：1   安检：2  (区分二维码扫描时使用)
        tag=i.getStringExtra("LR");
        setNFC();
        btn_LoginByScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tag.equals("1")){
                    //配送页面 请求扫描二维码
                    Intent i=new Intent(UserLoginByReadActivity.this,ScanQRCodeActivity.class);
                    i.putExtra("RequestMode","user");
                    startActivityForResult(i,3);
                }else if(tag.equals("2")){
                   // 安检页面 请求扫描二维码
                    Intent i=new Intent(UserLoginByReadActivity.this,ScanQRCodeActivity.class);
                    i.putExtra("RequestMode","user");
                    startActivityForResult(i,4);
                }else if(tag.equals("3")){
                    Intent i=new Intent(UserLoginByReadActivity.this,ScanQRCodeActivity.class);
                    i.putExtra("RequestMode","user");
                    startActivityForResult(i,7);
                }
            }
        });
        btn_LoginByInput.setOnClickListener(new View.OnClickListener() {
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
    }
    private void InnitGoodsDialog(){
        mGoodsDialog = new Dialog(this, R.style.my_dialog);
        root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.qp_tag_input_layout, null);
        root.findViewById(R.id.btn_cansel).setOnClickListener(btnListener);
        root.findViewById(R.id.btn_sure).setOnClickListener(btnListener);
        edt_userid = (EditText) root.findViewById(R.id.edt_brand);
        tv_pre=root.findViewById(R.id.tv_pre);
        tv_pre.setVisibility(View.GONE);
        rl_cbView=root.findViewById(R.id.rl_checkview);
        cb_oldCrad=root.findViewById(R.id.cb_oldCard);
        cb_newCrad=root.findViewById(R.id.cb_NewCard);
        edt_userid.setHint("在此输入送气编号");
        edt_userid.setText("");
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);

    }
    private View.OnClickListener btnListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cansel:
                    mGoodsDialog.dismiss();
                    break;
                case R.id.btn_sure://保存数据
                    String readData=edt_userid.getText().toString();
                    if(!TextUtils.isEmpty(readData)){
                        readData=addZero(readData,8);
                        userID=readData;
                       //首先选择卡号类型
                        if(tag.equals("1")){
                            //配送页面 请求输入用户编号
                            boolean userIsExist=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
                            if(userIsExist){
                                Intent i=new Intent(UserLoginByReadActivity.this,DeliveryActivity.class);
                                i.putExtra("UserID",readData);
                                i.putExtra("SaleID","");
                                startActivityForResult(i,5);
                                UserLoginByReadActivity.this.finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "该用户无可配送的订单",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }else if(tag.equals("2")){
                            // 安检页面 请求输入用户编号
                            //用户安检
                            new DownLoadSearchTask(UserLoginByReadActivity.this,UserLoginByReadActivity.this).execute(readData);
                        }else if(tag.equals("3")){
                            boolean userIsExist=deliveryDB.userIsRectify(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
                            if(userIsExist){
                                Intent i=new Intent(UserLoginByReadActivity.this,RectiftyActivity.class);
                                i.putExtra("UserID",readData);
                                startActivityForResult(i,8);
                                UserLoginByReadActivity.this.finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "该用户无需要整改的订单",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }else{
                        Toast.makeText(getBaseContext(),"请输入用户送气号",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    edt_userid.setText("");
                    mGoodsDialog.dismiss();
                    break;
            }
        }
    };

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
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        btn_LoginByScan.setEnabled(true);//20170215
        btn_LoginByInput.setEnabled(true);//20170215
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (mAdapter != null) {
            Log.i("mAdapter.isEnabled()", "=" + mAdapter.isEnabled());
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
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3){
            String UserID="";
            UserID=data.getStringExtra("userID");
            userID=UserID;
            if(!userID.equals("")){
                boolean userIsExist=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
                if(userIsExist){
                    Intent i=new Intent(UserLoginByReadActivity.this,DeliveryActivity.class);
                    i.putExtra("UserID",UserID);
                    i.putExtra("SaleID","");
                    startActivity(i);
                    this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "该用户无可配送的订单",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
        if(requestCode==4){
            //用户安检
            String UserID="";
            UserID=data.getStringExtra("userID");
            userID=UserID;
            if(!userID.equals("")){
                new DownLoadSearchTask(this,this).execute(UserID);
            }
        }
        if(requestCode==7){
            String UserID="";
            UserID=data.getStringExtra("userID");
            userID=UserID;
            if(!userID.equals("")){
                boolean userIsExist=deliveryDB.userIsRectify(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
                if(userIsExist){
                    Intent i=new Intent(UserLoginByReadActivity.this,RectiftyActivity.class);
                    i.putExtra("UserID",UserID);
                    startActivity(i);
                    this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "该用户无需要整改的订单",
                            Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
    // NFC模块
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
            mFilters = new IntentFilter[] { ndef, };

            // Setup a tech list for all NfcF tags
            mTechLists = new String[][] {
                    new String[] { MifareClassic.class.getName() },
                    new String[] { NfcA.class.getName() },
                    new String[] { NfcB.class.getName() },
                    new String[] { NfcF.class.getName() },
                    new String[] { NfcV.class.getName() },
                    new String[] { Ndef.class.getName() },
                    new String[] { NdefFormatable.class.getName() },
                    new String[] { MifareUltralight.class.getName() },
                    new String[] { IsoDep.class.getName() } };

            Intent intent = getIntent();
            resolveIntent(intent);
        }
    }
    private String UserCardUID="";
    void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            try {
                String readUID=printHexString(tagFromIntent.getId());//用户卡UID
                UserCardUID=readUID.toUpperCase();
                // 用戶卡
                Ndef ndef = Ndef.get(tagFromIntent);
                ndef.connect();
                NdefMessage mNdefMessage = ndef.getNdefMessage();
                if(mNdefMessage==null){
                    Toast.makeText(getApplicationContext(), "该卡未初始化",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] read = mNdefMessage.getRecords()[0].getPayload();// byte[]
                if (read != null) {
                    String out = printHexString(read);
                    String temp = out.substring(6, 22);
                    String userid = toStringHex2(temp);// 用户ID
                    String jx = out.substring(22, 24);// 校验位
                    if (jx.equals("0d") && isNumber(userid)) {
                        if(tag.equals("1")){
                            //配送页面
//                            boolean isMakeCard=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserCardUID);
//                            if(isMakeCard){
                                boolean userIsExist=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userid);
                                if(userIsExist){
                                    Intent login=new Intent(UserLoginByReadActivity.this,DeliveryActivity.class);
                                    login.putExtra("UserID",userid);
                                    login.putExtra("SaleID","");
                                    startActivity(login);
                                }else{
                                    Toast.makeText(getApplicationContext(), "该用户无可配送的订单",
                                            Toast.LENGTH_SHORT).show();
                                }

//                            }else{
//                                Toast.makeText(getApplicationContext(), "该卡为无效卡",
//                                        Toast.LENGTH_SHORT).show();
//                            }

                        }else if(tag.equals("2")){
                            //调用获取用户信息跳入安检页面
//                            boolean isMakeCard=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserCardUID);
//                            if(isMakeCard){
//                                userID=userid;
//                                new DownLoadSearchTask(this,this).execute(userID);
//                            }else{
//                                Toast.makeText(getApplicationContext(), "该卡为无效卡",
//                                        Toast.LENGTH_SHORT).show();
//                            }
                            userID=userid;
                            new DownLoadSearchTask(this,this).execute(userID);
                        }else{
                            //整改
//                            boolean isMakeCard=deliveryDB.userIsExist(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),UserCardUID);
//                            if(isMakeCard){
                                boolean userIsExist=deliveryDB.userIsRectify(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userid);
                                if(userIsExist){
                                    Intent i=new Intent(UserLoginByReadActivity.this,RectiftyActivity.class);
                                    i.putExtra("UserID",userid);
                                    startActivity(i);
                                    UserLoginByReadActivity. this.finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "该用户无需要整改的订单",
                                            Toast.LENGTH_SHORT).show();
                                }

//                            }else{
//                                Toast.makeText(getApplicationContext(), "该卡为无效卡",
//                                        Toast.LENGTH_SHORT).show();
//                            }
                        }

                    }else{
                        Toast.makeText(getApplicationContext(), "非用户卡",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            btn_LoginByScan.setEnabled(true);//20170215
            btn_LoginByInput.setEnabled(true);//20170215
        }
    }
    @Override
    public void onNewIntent(Intent intent) {
        resolveIntent(intent);
    }

    public static String toStringHex2(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "ASCII");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 将byte[]转换成16进制字符串
     *
     * @param data
     *            要转换成字符串的字节数组
     * @return 16进制字符串
     */
    private String printHexString(byte[] data) {
        StringBuffer s = new StringBuffer();
        ;
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            s.append(hex);
        }
        return s.toString();
    }

    private static char[] integernumber = new char[] { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9' };

    /**
     * 检测String是否全是数字
     *
     * @param name
     * @return
     */
    public static boolean isNumber(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isInteger(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 检测是否是整数
     */
    public static boolean isInteger(char c) {
        boolean res = false;
        for (char param : integernumber) {
            if (param == c) {
                res = true;
                break;
            }
        }
        return res;
    }

    private void showDiag() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserLoginByReadActivity.this);
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
    /**
     * 长度不够前面补0
     *
     * @param str
     * @param strLength
     * @return
     */
    private String addZero(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
            sb.append("0").append(str);// 左(前)补0
            // sb.append(str).append("0");//右(后)补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {

    }

    @Override
    public void DownLoadSearchTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            UserXJInfo userXJInfo= JSONUtils.toObjectWithGson(tag.getRespMsg(),UserXJInfo.class);
            if(userXJInfo!=null){
                msg = new StringBuilder();
                ajdb.InsertData(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userXJInfo);
                userXJInfo=ajdb.GetRectifyInfoByUserID(getBasicInfo.getOperationID(),getBasicInfo.getStationID(),userID);
               if(userXJInfo.getUserid()!=null){
                   String userid=userXJInfo.getUserid();
                   Log.e("userXJInfo", JSONUtils.toJsonWithGson(userXJInfo));
                   msg.append("---------------------------------------------------------------\n");
                   msg.append("用户编号："+userXJInfo.getUserid()+"\n");
                   msg.append("用户类型："+userXJInfo.getCustomerTypeName()+"\n");
                   msg.append("用户姓名："+userXJInfo.getUsername()+"\n");
                   msg.append("用户地址："+userXJInfo.getDeliveraddress()+"\n");
                   msg.append("用户电话："+userXJInfo.getTelephone()+"\n");
                   String s=msg.toString();
                   UserInfoDialog userInfoDialog=new UserInfoDialog(this,s,userXJInfo.getTypeClass());
                   userInfoDialog.shown();
               }else{
                   new AlertView("提示", "该用户已做过安检，提交安检信息即可", null, new String[]{"确定"},
                           null, this, AlertView.Style.Alert, this)
                           .show();
               }

            }else{
                new AlertView("提示", "查询不到该用户相关信息", null, new String[]{"确定"},
                        null, this, AlertView.Style.Alert, this)
                        .show();
            }
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }
    /***
     *用户信息详情
     */
    public class UserInfoDialog {
        private Context context;
        private Dialog mGoodsDialog;//
        private LinearLayout root;
        private TextView info,title;
        private String msg;
        private final String TypeClass;
        private Button btn_sure,btn_cansel;
        public UserInfoDialog(final Context context, String msg,final String TypeClass){
            this.context=context;
            this.msg=msg;
            this.TypeClass=TypeClass;
            mGoodsDialog = new Dialog(context, R.style.my_dialog);
            root = (LinearLayout) LayoutInflater.from(context).inflate(
                    R.layout.sale_info_dialog_layout, null);
            info= root.findViewById(R.id.saleinto);
            title=root.findViewById(R.id.title);
            info.setText("");
            title.setText("用户信息");
            btn_sure=root.findViewById(R.id.btn_print);
            btn_sure.setText("安检");
            btn_cansel= root.findViewById(R.id.btn_cansel);
            btn_cansel.setVisibility(View.GONE);
            btn_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoodsDialog.dismiss();
                    Intent i=new Intent(UserLoginByReadActivity.this,SearchByCActivity.class);
                    i.putExtra("UserID",userID);
                    i.putExtra("TypeClass",TypeClass);
                    startActivity(i);
                    UserLoginByReadActivity.this.finish();

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
