package com.hsic.tmj.qppst;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.hsic.adapter.SaleDistrubutionAdapter;
import com.hsic.bean.CustomerInfo;
import com.hsic.bean.EmployeeInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleAll;
import com.hsic.bll.GetBasicInfo;
import com.hsic.listener.ImplSearchEmployee;
import com.hsic.listener.ImplSearchSale;
import com.hsic.listener.ImplUpdateSale;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.sy.dialoglibrary.AlertView;
import com.hsic.sy.dialoglibrary.OnDismissListener;
import com.hsic.task.SearchEmployeeTask;
import com.hsic.task.SearchSaleTask;
import com.hsic.task.UpdateSaleTask;
import com.hsic.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class DistrubutionOrderActivity extends AppCompatActivity implements ImplSearchSale, com.hsic.sy.dialoglibrary.OnItemClickListener,
        OnDismissListener,ImplSearchEmployee,ImplUpdateSale {
    private ListView listView1;
    private Button btn_dis;
    String LoginID;
    List<SaleAll> SaleAll_List;
    SaleDistrubutionAdapter saleListAdapter;
    AlertView choice, IsSunmitSale;
    GetBasicInfo getBasicInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distrubution_order);
        listView1 = this.findViewById(R.id.listView1);
        listView1.setOnItemClickListener(mOnItemClickListener);
        btn_dis = this.findViewById(R.id.btn_addqp);
        getBasicInfo=new GetBasicInfo(this);
        LoginID = getBasicInfo.getOperationID();
        /**
         * 获取订单信息
         */
        Sale sale = new Sale();
        sale.setStation(getBasicInfo.getStationID());
        String request = JSONUtils.toJsonWithGson(sale);
        HsicMessage hsic = new HsicMessage();
        hsic.setRespMsg(request);
        String requestData = JSONUtils.toJsonWithGson(hsic);
        SearchSaleTask task = new SearchSaleTask(DistrubutionOrderActivity.this, DistrubutionOrderActivity.this);
        task.execute(requestData);
        btn_dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SaleAll_List != null && SaleAll_List.size() > 0) {
                    if (isEixst()) {
                        SearchEmployeeTask task = new SearchEmployeeTask(DistrubutionOrderActivity.this, DistrubutionOrderActivity.this);
                        task.execute(getBasicInfo.getStationID());
                    } else {
                        Toast.makeText(DistrubutionOrderActivity.this, "无可分配的订单", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DistrubutionOrderActivity.this, "请选择要分配的订单", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {

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
    }
    private int scrolledX = 0;
    private int scrolledY = 0;
    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
            if (SaleAll_List.get(position).isUpdate) {
                SaleAll saleAll = new SaleAll();
                Sale Sale;//
                Sale = saleAll.getSale();
                CustomerInfo CustomerInfo;//
                CustomerInfo = saleAll.getCustomerInfo();
                saleAll.isUpdate = false;
                saleAll.setSale(SaleAll_List.get(position).getSale());
                saleAll.setCustomerInfo(SaleAll_List.get(position).getCustomerInfo());
//                saleAll.setSaleDetail(SaleAll_List.get(position).getSaleDetail());
                SaleAll_List.remove(position);
                SaleAll_List.add(position, saleAll);
                saleListAdapter = new SaleDistrubutionAdapter(DistrubutionOrderActivity.this, SaleAll_List);
                listView1.setAdapter(saleListAdapter);
                listView1.setSelectionFromTop(scrolledX, scrolledY);
            } else {
                SaleAll saleAll = new SaleAll();
                Sale Sale;//
                Sale = saleAll.getSale();
                CustomerInfo CustomerInfo;//
                CustomerInfo = saleAll.getCustomerInfo();
                saleAll.isUpdate = true;
                saleAll.setSale(SaleAll_List.get(position).getSale());
                saleAll.setCustomerInfo(SaleAll_List.get(position).getCustomerInfo());
//                saleAll.setSaleDetail(SaleAll_List.get(position).getSaleDetail());
                SaleAll_List.remove(position);
                SaleAll_List.add(position, saleAll);
                saleListAdapter = new SaleDistrubutionAdapter(DistrubutionOrderActivity.this, SaleAll_List);
                listView1.setAdapter(saleListAdapter);
                listView1.setSelectionFromTop(scrolledX, scrolledY);
            }


        }
    };
    private boolean isEixst() {
        boolean ret = false;
        int size = SaleAll_List.size();
        for (int i = 0; i < size; i++) {
            if (SaleAll_List.get(i).isUpdate) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    List<Sale> UploadList;

    private List<Sale> SortUploadData(List<SaleAll> RegionCode_Data, String staffID) {
        UploadList = new ArrayList<Sale>();
        for (int i = 0; i < RegionCode_Data.size(); i++) {
            if (RegionCode_Data.get(i).isUpdate) {
                SaleAll saleAll = RegionCode_Data.get(i);
                Sale sale = saleAll.getSale();
                sale.setAssignTime(TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                sale.setManagerID(LoginID);
                sale.setEmployeeID(staffID);
                saleAll.setSale(sale);
                RegionCode_Data.remove(i);
                RegionCode_Data.add(i, saleAll);
                UploadList.add(sale);
            }
        }
        return UploadList;
    }

    @Override
    public void SearchSaleTaskEnd(HsicMessage tag) {
        if (tag.getRespCode() == 0) {
            SaleAll_List = new ArrayList<SaleAll>();
            SaleAll_List = JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<SaleAll>>() {
            }.getType());//数据来源
            saleListAdapter = new SaleDistrubutionAdapter(DistrubutionOrderActivity.this, SaleAll_List);
            listView1.setAdapter(saleListAdapter);
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }

    @Override
    public void onDismiss(Object o) {

    }

    @Override
    public void onItemClick(Object o, int position) {
        if (o == IsSunmitSale) {
        }else{
            this.finish();
        }
    }

    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    List<EmployeeInfo> mDatas;
    public void ShowDialog() {
        Context context = DistrubutionOrderActivity.this;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.formcommonlist, null);
        ListView myListView = (ListView) layout.findViewById(R.id.formcustomspinner_list);
        TextView Lable = (TextView) layout.findViewById(R.id.label);
        Lable.setText("请选择被分单人");
        MyAdapter adapter = new MyAdapter(context, mDatas);
        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long id) {
                SortUploadData(SaleAll_List, mDatas.get(positon).getUserID());
                HsicMessage mHsicMessage = new HsicMessage();// 创建通信类
                String request = JSONUtils.toJsonWithGson(UploadList);// 将信息转换成json
                mHsicMessage.setRespMsg(request);// 给通信类设置信息
                String requestData = JSONUtils.toJsonWithGson(mHsicMessage);// 将通信类转换成json
                UpdateSaleTask task = new UpdateSaleTask(DistrubutionOrderActivity.this, DistrubutionOrderActivity.this);
                task.execute(requestData);
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void SearchEmployeeTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            mDatas=new ArrayList<EmployeeInfo>();
            mDatas= JSONUtils.toListWithGson(tag.getRespMsg(), new TypeToken<List<EmployeeInfo>>() {
            }.getType());//数据来源
            ShowDialog();
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }

    }

    @Override
    public void UpdateSaleTaskEnd(HsicMessage tag) {
        if(tag.getRespCode()==0){
            Toast.makeText(this, "分单成功", Toast.LENGTH_SHORT).show();
            /**
             * 获取订单信息
             */
            Sale sale = new Sale();
            sale.setStation(getBasicInfo.getStationID());
            String request = JSONUtils.toJsonWithGson(sale);
            HsicMessage hsic = new HsicMessage();
            hsic.setRespMsg(request);
            String requestData = JSONUtils.toJsonWithGson(hsic);
            SearchSaleTask task = new SearchSaleTask(DistrubutionOrderActivity.this, DistrubutionOrderActivity.this);
            task.execute(requestData);
        }else{
            new AlertView("提示", tag.getRespMsg(), null, new String[]{"确定"},
                    null, this, AlertView.Style.Alert, this)
                    .show();
        }
    }

    //自定义的适配器
    class MyAdapter extends BaseAdapter {
        private List<EmployeeInfo> mlist;
        private Context mContext;

        public MyAdapter(Context context, List<EmployeeInfo> list) {
            this.mContext = context;
            mlist = new ArrayList<EmployeeInfo>();
            this.mlist = list;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int position) {
            return mlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Person person = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(R.layout.rtu_item, null);
                person = new Person();
                person.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(person);
            } else {
                person = (Person) convertView.getTag();
            }
            person.name.setText(mlist.get(position).getUserName());
            return convertView;
        }

        class Person {
            TextView name;
        }
    }
}
