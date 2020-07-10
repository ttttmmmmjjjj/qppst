package com.hsic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.hsic.bean.CustomerTypeInfo;
import com.hsic.bean.StreetInfo;
import com.hsic.bean.UserQPInfo;
import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.SaleAll;
import com.hsic.bean.SaleDetail;
import com.hsic.bean.ScanHistory;
import com.hsic.bean.UserXJInfo;
import com.hsic.bluetooth.PrintUtils;
import com.hsic.tmj.qppst.R;
import com.hsic.tmj.wheelview.QPType;
import com.hsic.utils.TimeUtils;
import com.hsic.web.WebServiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmj on 2019/2/26.
 */

public class DeliveryDB {
    private StringBuffer sql_insert;
    private SQLiteDatabase mDatabase = null;
    private Context context;

    public DeliveryDB(Context context) {
        mDatabase = DataBaseHelper.getInstance(context).getReadableDatabase();
        this.context = context;
    }

    /**
     * 基础配置信息 包括 街道信息
     * 插入街道信息
     */
    public boolean InsertStreetInfo(List<StreetInfo> list) {
        boolean ret = false;
        try {
            mDatabase.beginTransaction();// 开启事务
            for (int i = 0; i < list.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("AeraCode", list.get(i).getAreaCode());
                cv.put("QuCode", list.get(i).getQuCode());
                cv.put("QuName", list.get(i).getQuName());
                cv.put("JieCode", list.get(i).getJieCode());
                cv.put("JieName", list.get(i).getJieName());
                long insert = mDatabase.insert("T_B_ADDRESS", null, cv);
            }
            mDatabase.setTransactionSuccessful();

        } catch (Exception ex) {
            mDatabase.endTransaction();
        } finally {
            mDatabase.endTransaction();
        }
        return ret;
    }
    /**
     * 基础配置信息 包括 街道信息
     * 插入客户类型
     */
    public boolean InsertCustomTypeInfo(List<CustomerTypeInfo> list) {
        boolean ret = false;
        try {
            mDatabase.beginTransaction();// 开启事务
            for (int i = 0; i < list.size(); i++) {
                ContentValues cv = new ContentValues();
                cv.put("CustomerType", list.get(i).getCustomerType());
                cv.put("CustomerTypeName", list.get(i).getCustomerTypeName());
                long insert = mDatabase.insert("T_B_CustomerType", null, cv);
            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception ex) {
            mDatabase.endTransaction();
        } finally {
            mDatabase.endTransaction();
        }
        return ret;
    }
    /***
     * 删除街道基本信息
     */
    public void DeleteConfigInfo() {
        String sql="delete from T_B_ADDRESS ;delete from T_B_CustomerType ";//销售表
        mDatabase.execSQL(sql,null);
    }

    /**
     * 插入销售相关信息
     */
    public void InsertSaleInfo(List<SaleAll> saleAllList, String EmployeeID, String StationID) {
        long start = System.currentTimeMillis();
        try {
            List<Sale> sales = new ArrayList<Sale>();
            List<UserXJInfo> userXJInfos = new ArrayList<UserXJInfo>();
            List<SaleDetail> saleDetails = new ArrayList<SaleDetail>();
            List<UserQPInfo> UserQPInfo_lsit=new ArrayList<>();//未归还气瓶
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_SALE(SaleID,UserID,StationID,StationName,Match,IState," +
                    "Remark,Address,AssignTime,EmployeeID,AllPrice,InsertTime,Telephone,RealPriceInfo," +
                    "UrgeGasInfoStatus,CreateTime,URL,ISNeedZS)");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)");
            int size = saleAllList.size();
            StringBuffer saleID_Web = new StringBuffer();
            for (int i = 0; i < size; i++) {
//                Log.e(""+i,JSONUtils.toJsonWithGson(saleAllList.get(i)));
                String SaleID = saleAllList.get(i).getSale().getSaleID();
                String urgeStatus = saleAllList.get(i).getSale().getUrgeGasInfoStatus();
                saleID_Web.append(SaleID + ",");
                if (!isExist(EmployeeID, StationID, SaleID)) {
                    sales.add(saleAllList.get(i).getSale());
//                    userXJInfos.add(saleAllList.get(i).getUserXJInfo());

                    /***********       //未归还气瓶          *******************/
                    UserQPInfo_lsit=saleAllList.get(i).getUserQPInfo_lsit();
                    StringBuffer wghqpStr=new StringBuffer();
                    String wghqp="";
                    int list=UserQPInfo_lsit.size();
                    if(list>0){
                        for(int a=0;a<list;a++){
                            wghqpStr.append(UserQPInfo_lsit.get(a).getQPNO()+",");
                        }
                        wghqp=(wghqpStr.toString());
                        wghqp=wghqp.substring(0,wghqp.length()-1);
                    }
                    UserXJInfo userXJInfo=saleAllList.get(i).getUserXJInfo();
                    userXJInfo.setWhpqk(wghqp);
                    userXJInfos.add(userXJInfo);
                    /**********                              ******************/
                    List<SaleDetail> tmp = new ArrayList<SaleDetail>();
                    tmp = saleAllList.get(i).getSale().getSale_detail_info();
                    int s = tmp.size();
                    for (int j = 0; j < s; j++) {
                        saleDetails.add(tmp.get(j));
                    }
                } else {
                    //查看催单状态
                    if (urgeStatus.equals("0")) {
                        upUrgeStatus(EmployeeID, StationID, SaleID, urgeStatus);
                    }
                    if (isReturnSale(EmployeeID, StationID, SaleID)) {
                        //将该订单状态更改为3
                        ChargeBackStatus(EmployeeID, StationID, SaleID, "3");
                    }

                }
            }
            for (Sale sale : sales) {
                SQLiteStatement statement = mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, sale.getSaleID());
                statement.bindString(2, sale.getCustomerID());
                statement.bindString(3, sale.getStation());
                statement.bindString(4, "");
                statement.bindLong(5, sale.getMatch());
                statement.bindLong(6, sale.getiState());
                statement.bindString(7, sale.getRemark() != null ? sale.getRemark() : "");
                statement.bindString(8, sale.getSaleAddress());
                statement.bindString(9, sale.getCreateTime());
                statement.bindString(10, sale.getEmployeeID().toUpperCase());
                statement.bindString(11, sale.getPlanPirce());
                statement.bindString(12, TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                statement.bindString(13, sale.getTelphone());//来电电话
                statement.bindString(14, sale.getPlanPirceInfo());//来电电话
                statement.bindString(15, sale.getUrgeGasInfoStatus());//催单状态
                statement.bindString(16, sale.getCreateTime());//生成时间
                statement.bindString(17, "");//
                statement.bindString(18, sale.getISNeedZS());//
                statement.executeInsert();
            }
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_CUSTOMERINFO(SaleID,UserID,StationID,CustomerType,IsNew,CustomerCardID,UserCardStatus," +
                    "StopSupplyType1,StopSupplyType2,StopSupplyType3,StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7," +
                    "StopSupplyType8,StopSupplyType9,StopSupplyType10,StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15," +
                    "UnInstallType1,UnInstallType2,UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9," +
                    "UnInstallType10,UnInstallType11,UnInstallType12,UnInstallType13,UnInstallType14," +
                    "EmployeeID,CustomerName,IState,CustomerTypeName,InsertTime,Telephone,Address,TagID,TypeClass,Last_InspectionStatus,wghqp)");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            for (UserXJInfo userXJInfo : userXJInfos) {
                SQLiteStatement statement = mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, userXJInfo.getSaleid());
                statement.bindString(2, userXJInfo.getUserid());
                statement.bindString(3, userXJInfo.getStationcode());
                statement.bindString(4, userXJInfo.getCustomerType());
                statement.bindLong(5, userXJInfo.getIsNew());
                statement.bindString(6, userXJInfo.getCustomerCardID());
                statement.bindString(7, userXJInfo.getUserCardStatus());
                statement.bindString(8, userXJInfo.getStopSupplyType1());
                statement.bindString(9, userXJInfo.getStopSupplyType2());
                statement.bindString(10, userXJInfo.getStopSupplyType3());
                statement.bindString(11, userXJInfo.getStopSupplyType4());
                statement.bindString(12, userXJInfo.getStopSupplyType5());
                statement.bindString(13, userXJInfo.getStopSupplyType6());
                statement.bindString(14, userXJInfo.getStopSupplyType7());
                statement.bindString(15, userXJInfo.getStopSupplyType8());
                statement.bindString(16, userXJInfo.getStopSupplyType9());
                statement.bindString(17, userXJInfo.getStopSupplyType10());
                statement.bindString(18, userXJInfo.getStopSupplyType11());
                statement.bindString(19, userXJInfo.getStopSupplyType12());
                statement.bindString(20, userXJInfo.getStopSupplyType13());
                statement.bindString(21, userXJInfo.getStopSupplyType14());
                statement.bindString(22, userXJInfo.getStopSupplyType15());
                statement.bindString(23, userXJInfo.getUnInstallType1());
                statement.bindString(24, userXJInfo.getUnInstallType2());
                statement.bindString(25, userXJInfo.getUnInstallType3());
                statement.bindString(26, userXJInfo.getUnInstallType4());
                statement.bindString(27, userXJInfo.getUnInstallType5());
                statement.bindString(28, userXJInfo.getUnInstallType6());
                statement.bindString(29, userXJInfo.getUnInstallType7());
                statement.bindString(30, userXJInfo.getUnInstallType8());
                statement.bindString(31, userXJInfo.getUnInstallType9());
                statement.bindString(32, userXJInfo.getUnInstallType10());
                statement.bindString(33, userXJInfo.getUnInstallType11());
                statement.bindString(34, userXJInfo.getUnInstallType12());
                statement.bindString(35, userXJInfo.getUnInstallType13());
                statement.bindString(36, userXJInfo.getUnInstallType14());
                statement.bindString(37, EmployeeID);
                statement.bindString(38, userXJInfo.getUsername());
                statement.bindString(39, "3");
                statement.bindString(40, userXJInfo.getCustomerTypeName());
                statement.bindString(41, TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                statement.bindString(42, userXJInfo.getTelephone());
                statement.bindString(43, userXJInfo.getDeliveraddress());
                statement.bindString(44, userXJInfo.getUserCardID());
                statement.bindString(45, userXJInfo.getTypeClass());
                statement.bindString(46, userXJInfo.getInspectionStatus());
                statement.bindString(47, userXJInfo.getWhpqk());
                statement.executeInsert();
            }
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_SALEDETAIL(SaleID,QPType,QPName,PlanSendNum,PlanReceiveNum,QTPrice,EmployeeID,StationID,IsEx,InsertTime" +
                    ")");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?,?,?)");
            for (SaleDetail saleDetail : saleDetails) {
                SQLiteStatement statement = mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, saleDetail.getSaleID());
                statement.bindString(2, saleDetail.getQPType());
                statement.bindString(3, saleDetail.getQPName());
                statement.bindLong(4, saleDetail.getPlanSendNum());
                statement.bindLong(5, saleDetail.getPlanReceiveNum());
                statement.bindString(6, saleDetail.getRealQTPrice());
                statement.bindString(7, EmployeeID);
                statement.bindString(8, StationID);
                statement.bindLong(9, saleDetail.getIsEx());
                statement.bindString(10, TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                statement.executeInsert();
            }
            String saleID = saleID_Web.toString();
            int l = saleID.length();
            saleID = saleID.substring(0, l);
            //本地订单和后台对比
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            data = saleID(EmployeeID, StationID);
            for (int h = 0; h < data.size(); h++) {
                String tmp = data.get(h).get("SaleID");
                if (!saleID.contains(tmp)) {
                    updateSaleStatus(EmployeeID, StationID, tmp);
                }
            }
        } catch (Exception ex) {
            Log.e("数据插入出现异常", ex.toString());
        } finally {
        }
        long end = System.currentTimeMillis();
    }

    /**
     * 删除本地无效订单及相关信息
     *
     * @param saleAllList
     * @param EmployeeID
     */
    public void deleteCanselSale(List<SaleAll> saleAllList, String EmployeeID, String StationID) {
        long start = System.currentTimeMillis();
        try {
            int size = saleAllList.size();
            List<Sale> sales = new ArrayList<Sale>();
            for (int i = 0; i < size; i++) {
                String SaleID = saleAllList.get(i).getSale().getSaleID();
                if (isExist(EmployeeID, StationID, SaleID)) {
                    sales.add(saleAllList.get(i).getSale());
                }

            }
            if (sales.size() > 0) {
                for (Sale sale : sales) {
                    String SaleID = sale.getSaleID();
                    /**
                     * 删除该订单对应的所有相关信息
                     */
                    String whereClause_t = "SaleID=? and EmployeeID=?";
                    String[] whereArgs_t = {SaleID, EmployeeID};
                    mDatabase.delete("T_B_SALE", whereClause_t, whereArgs_t);
                    mDatabase.delete("T_B_SALEDETAIL", whereClause_t, whereArgs_t);
                    mDatabase.delete("T_B_CUSTOMERINFO", whereClause_t, whereArgs_t);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long end = System.currentTimeMillis();
        Log.e("删除异常订单", "=" + (end - start));
    }

    /**
     * 获取订单信息[未完成]
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Map<String, String>> GetSaleInfo(String EmployeeID, String StationID) {
        long start = System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] selectionArgs = {EmployeeID};
        String sql = "select a.SaleID,a.UserID,b.CustomerName,b.Telephone,a.Address,b.CustomerCardID,b.CustomerType,a.Telephone," +
                " a.UrgeGasInfoStatus,a.CreateTime from T_B_SALE a left join T_B_CUSTOMERINFO b on a.SaleID=b.SaleID " +
                " where a.EmployeeID=? and a.IState='3' order by a.AssignTime desc";
        String sqld = "select PlanSendNum,QPName from T_B_SALEDETAIL where SaleID=? and EmployeeID=?  ";
//        try {
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SaleID", query.getString(0));
                String SaleID = query.getString(0);//订单号
                map.put("UserID", query.getString(1));
                map.put("CustomerName", query.getString(2));
                map.put("Telephone", query.getString(3));
                map.put("Address", query.getString(4));
                map.put("CustomerCardID", query.getString(5));
                map.put("CustomerType", query.getString(6));
                map.put("CallingTelephone", query.getString(7));
                map.put("UrgeGasInfoStatus", query.getString(8));
                map.put("CreateTime", query.getString(9));
                StringBuffer goodsInfo = new StringBuffer();
                Cursor rawQuery = mDatabase.rawQuery(sqld,
                        new String[]{SaleID, EmployeeID});
                while (rawQuery.moveToNext()) {
                    goodsInfo.append(rawQuery.getString(1) + "/" + rawQuery.getString(0) + "，");
                }
                String tmp = goodsInfo.toString();
                tmp = tmp.substring(0, tmp.length() - 1);
                map.put("goodsInfo", tmp);
                list.add(map);
                rawQuery.close();
            }
            query.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        long end = System.currentTimeMillis();
        return list;
    }

    /**
     * 获取打印信息
     *
     * @param EmployeeID
     * @param SaleID
     * @return
     */
    public SaleAll GetPrint(String EmployeeID, String SaleID) {
        SaleAll saleAll = new SaleAll();
        Sale sale = new Sale();
        UserXJInfo userXJInfo = new UserXJInfo();
        List<SaleDetail> saleDetailList = new ArrayList<SaleDetail>();
        try {
            String[] selectionArgs = {EmployeeID, SaleID};
            String sql = "select a.SaleID,a.UserID,b.CustomerName,b.Telephone,a.Address,b.CustomerCardID,a.SendQP,a.ReceiveQP,a.FinishTime,b.CustomerTypeName," +
                    " a.AllPrice,a.Address,a.Telephone,b.CustomerType,a.PayType,a.AZType,a.URL from T_B_SALE a left join T_B_CUSTOMERINFO b on a.SaleID=b.SaleID " +
                    " where a.EmployeeID=? and a.SaleID=? and (a.IState='4'or  a.IState='5')order by a.AssignTime desc";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            if (query.moveToFirst()) {
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sale.setUserName(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));//联系电话
                sale.setAddress(query.getString(4));
                userXJInfo.setCustomerCardID(query.getString(5));
                sale.setSendQP(query.getString(6));
                sale.setReceiveQP(query.getString(7));
                sale.setFinishTime(query.getString(8));
                userXJInfo.setCustomerTypeName(query.getString(9));//客户类型
                sale.setRealPirce(query.getString(10));
                sale.setSaleAddress(query.getString(11));
                sale.setTelphone(query.getString(12));//来电电话
                userXJInfo.setCustomerType(query.getString(13));
                sale.setPayType(query.getString(14));
                sale.setAZType(query.getString(15));
                sale.setPS(query.getString(16));
                saleAll.setSale(sale);
                saleAll.setUserXJInfo(userXJInfo);
            }
            query.close();
        } catch (Exception ex) {
            ex.toString();
        }
        return saleAll;
    }

    /**
     * 已完成订单
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Map<String, String>> GetSaleInfoFinish(String EmployeeID, String StationID) {
        long start = System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] selectionArgs = {EmployeeID};
        String sql = "select a.SaleID,a.UserID,b.CustomerName,b.Telephone,a.Address,b.CustomerCardID,b.CustomerType,a.Telephone," +
                "a.CreateTime from T_B_SALE a left join T_B_CUSTOMERINFO b on a.SaleID=b.SaleID " +
                " where a.EmployeeID=? and (a.IState='4'or  a.IState='5')order by a.FinishTime desc";
        String sqld = "select PlanSendNum,QPName from T_B_SALEDETAIL where SaleID=? and EmployeeID=? and IsEx='0'";
        try {
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SaleID", query.getString(0));
                String SaleID = query.getString(0);//订单号
                map.put("UserID", query.getString(1));
                map.put("CustomerName", query.getString(2));
                map.put("Telephone", query.getString(3));
                map.put("Address", query.getString(4));
                map.put("CustomerCardID", query.getString(5));
                map.put("CustomerType", query.getString(6));
                map.put("CallingTelephone", query.getString(7));
                map.put("CreateTime", query.getString(8));
                StringBuffer goodsInfo = new StringBuffer();
                Cursor rawQuery = mDatabase.rawQuery(sqld,
                        new String[]{SaleID, EmployeeID});
                while (rawQuery.moveToNext()) {
                    goodsInfo.append(rawQuery.getString(1) + "/" + rawQuery.getString(0) + "，");
                }
                String tmp = goodsInfo.toString();
                tmp = tmp.substring(0, tmp.length() - 1);
                map.put("goodsInfo", tmp);
                list.add(map);
            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return list;
    }

    /**
     * 看订单是否存在
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isExist(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?  and SaleID=?";
            String[] whereArgs = new String[]{EmployeeID, SaleID};
            Cursor cursor = mDatabase.query("T_B_SALE", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断该订单是否为退单
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isReturnSale(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=? and SaleID=? and IState='8'";
            String[] whereArgs = new String[]{EmployeeID, SaleID};
            Cursor cursor = mDatabase.query("T_B_SALE", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询该订单是否已完成
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isDone(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?  and SaleID=? and IState='4'";
            String[] whereArgs = new String[]{EmployeeID, SaleID};
            Cursor cursor = mDatabase.query("T_B_SALE", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 订单信息上传成功
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isSaleUpLoad(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?  and SaleID=? and IState='5'";
            String[] whereArgs = new String[]{EmployeeID, SaleID};
            Cursor cursor = mDatabase.query("T_B_SALE", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 安检信息上传成功
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isInspectionUpLoad(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=? and SaleID=? and IsInspected='2'";
            String[] whereArgs = new String[]{EmployeeID,SaleID};
            Cursor cursor = mDatabase.query("T_B_CUSTOMERINFO", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 存在关联文件未上传
     * @param EmployeeID
     * @param SaleID
     * @return
     */
    public boolean isFileUpLoad(String EmployeeID, String SaleID) {
        boolean result = false;
        try {
            String[] selectionArgs = {EmployeeID, SaleID};
            String sql = "select RelationID,ImageName,FileName  from  T_B_XJ_Association where EmployeeID=? and SaleID=? and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                return true;
            }
            query.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 该用户是否存在可配送的订单
     *
     * @param EmployeeID
     * @param StationID
     * @param UserID
     * @return
     */
    public boolean userIsExist(String EmployeeID, String StationID, String UserID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=? and  UserID=? and IState='3'";
            String[] whereArgs = new String[]{EmployeeID, UserID};
            Cursor cursor = mDatabase.query("T_B_SALE", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询该用户是否有需要整改的订单
     *
     * @param EmployeeID
     * @param StationID
     * @param UserID
     * @return
     */
    public boolean userIsRectify(String EmployeeID, String StationID, String UserID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?  and  UserID=? and IsInspected='0'";
            String[] whereArgs = new String[]{EmployeeID, UserID};
            Cursor cursor = mDatabase.query("T_B_UserRectifyInfo", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 该用户是否已发卡
     *
     * @param EmployeeID
     * @param StationID
     * @param TagID
     * @return
     */
    public boolean IsMakeCard(String EmployeeID, String StationID, String TagID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=? and  TagID=? ";
            String[] whereArgs = new String[]{EmployeeID, TagID};
            Cursor cursor = mDatabase.query("T_B_CUSTOMERINFO", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                String tagID = cursor.getString(0);
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据用户号获取订单相关信息
     *
     * @param EmployeeID
     * @param StationID
     * @param UserID
     * @return
     */
    public List<Map<String, String>> GetSaleInfoByUserID(String EmployeeID, String StationID, String UserID) {
        long start = System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] selectionArgs = { EmployeeID, UserID};
        String sql = "select a.SaleID,a.UserID,b.CustomerName,b.Telephone,a.Address,b.IsNew,b.CustomerCardID,b.CustomerTypeName,a.AllPrice,a.Telephone," +
                "b.CustomerType,b.TypeClass,a.ISNeedZS,b.wghqp" +
                " from  T_B_SALE a  " +
                " left join T_B_CUSTOMERINFO b on a.SaleID=b.SaleID " +
                " where  a.EmployeeID=? and a.IState='3'and a.UserID=?";

        String sqld = "select PlanSendNum,QPName,IsEx from T_B_SALEDETAIL where SaleID=? and EmployeeID=?  and (IsEx='0'or IsEx='2')";
        try {
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SaleID", query.getString(0));
                String SaleID = query.getString(0);//订单号
                map.put("UserID", query.getString(1));
                map.put("CustomerName", query.getString(2));
                map.put("Telephone", query.getString(3));
                map.put("Address", query.getString(4));
                map.put("IsNew", query.getString(5));
                map.put("CustomerCardID", query.getString(6));
                map.put("CustomerTypeName", query.getString(7));
                map.put("AllPrice", query.getString(8));
                map.put("CallingTelephone", query.getString(9));
                map.put("CustomerType", query.getString(10));
                map.put("TypeClass", query.getString(11));
                map.put("ISNeedZS", query.getString(12));
                map.put("wghqp", query.getString(13));
                StringBuffer goodsInfo = new StringBuffer();
                int GoodsCount = 0;
                Cursor rawQuery = mDatabase.rawQuery(sqld,
                        new String[]{SaleID, EmployeeID});
                while (rawQuery.moveToNext()) {
                    String IsEx="";
                    IsEx=rawQuery.getString(2);//0表示是商品
                    if(IsEx.equals("0")){
                        String c = rawQuery.getString(0);
                        int tmp = Integer.parseInt(c);
                        GoodsCount = GoodsCount + tmp;
                    }
                    goodsInfo.append(rawQuery.getString(1) + "/" + rawQuery.getString(0) + "，");
                }
                String tmp = goodsInfo.toString();
                tmp = tmp.substring(0, tmp.length() - 1);
                map.put("GoodsInfo", tmp);
                String GoodsCountStr = String.valueOf(GoodsCount);
                map.put("GoodsCount", GoodsCountStr);
                list.add(map);
                rawQuery.close();
            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return list;
    }

    /**
     * 根据订单号获取订单相关信息
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public List<Map<String, String>> GetSaleInfoBySaleID(String EmployeeID, String StationID, String SaleID) {
        long start = System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] selectionArgs = { EmployeeID, SaleID};
        String sql = "select a.SaleID,a.UserID,b.CustomerName,b.Telephone,a.Address,b.IsNew,b.CustomerCardID," +
                "b.CustomerTypeName,a.AllPrice,a.Telephone," +
                "b.CustomerType,b.TypeClass,a.ISNeedZS,b.wghqp" +
                " from  T_B_SALE a  " +
                " left join T_B_CUSTOMERINFO b on a.SaleID=b.SaleID  " +
                " where  a.EmployeeID=? and a.IState='3'and a.SaleID=?";

        String sqld = "select PlanSendNum,QPName,IsEx from T_B_SALEDETAIL where SaleID=? and EmployeeID=?  and (IsEx='0'or IsEx='2')";
        try {
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SaleID", query.getString(0));
                map.put("UserID", query.getString(1));
                map.put("CustomerName", query.getString(2));
                map.put("Telephone", query.getString(3));
                map.put("Address", query.getString(4));
                map.put("IsNew", query.getString(5));
                map.put("CustomerCardID", query.getString(6));
                map.put("CustomerTypeName", query.getString(7));
                map.put("AllPrice", query.getString(8));
                map.put("CallingTelephone", query.getString(9));
                map.put("CustomerType", query.getString(10));
                map.put("TypeClass", query.getString(11));
                map.put("ISNeedZS", query.getString(12));
                map.put("wghqp", query.getString(13));

                StringBuffer goodsInfo = new StringBuffer();
                int GoodsCount = 0;
                Cursor rawQuery = mDatabase.rawQuery(sqld,
                        new String[]{SaleID, EmployeeID});
                while (rawQuery.moveToNext()) {
                    String IsEx="";
                    IsEx=rawQuery.getString(2);//0表示是商品
                    if(IsEx.equals("0")){
                        String c = rawQuery.getString(0);
                        int tmp = Integer.parseInt(c);
                        GoodsCount = GoodsCount + tmp;
                    }

                    goodsInfo.append(rawQuery.getString(1) + "/" + rawQuery.getString(0) + "，");
                }
                String tmp = goodsInfo.toString();
                tmp = tmp.substring(0, tmp.length() - 1);
                map.put("GoodsInfo", tmp);
                String GoodsCountStr = String.valueOf(GoodsCount);
                map.put("GoodsCount", GoodsCountStr);
                list.add(map);
            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return list;
    }

    /**
     * 获取商品类型
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public List<QPType> GetGoodsType(String EmployeeID, String StationID, String SaleID) {
        List<QPType> saleDetails = new ArrayList<QPType>();
        try {
            String[] selectionArgs = new String[]{EmployeeID, SaleID};
            String sql = "select  QPType,QPName,PlanSendNum  from T_B_SALEDETAIL where EmployeeID=?  and SaleID=? and IsEx='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                QPType qpType = new QPType();
                qpType.setQPType(query.getString(0));
                qpType.setQPName(query.getString(1));
                qpType.setQPNum(query.getString(2));
                saleDetails.add(qpType);
            }
            query.close();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return saleDetails;
    }

    public List<String> GetSaleDetail(String EmployeeID, String StationID, String SaleID) {
        List<String> s = new ArrayList<String>();
        try {
            String[] selectionArgs = new String[]{EmployeeID, SaleID};
            String sql = "select  QPType,QPName,PlanSendNum,PlanReceiveNum  from T_B_SALEDETAIL where EmployeeID=?  and SaleID=? and IsEx='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                s.add(query.getString(0));
            }
            query.close();
        } catch (Exception Ex) {
            Ex.printStackTrace();
        }
        return s;
    }

    /**
     * 保存订单数据到本地数据库
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param sale
     * @param saleDetails
     * @param scanHistories
     * @return
     */
    public boolean updateSaleInfo(String EmployeeID, String StationID, String SaleID, Sale sale, List<SaleDetail> saleDetails,
                                  List<ScanHistory> scanHistories) {
        boolean ret = false;
        try {
            String whereClauseBys = "EmployeeID=?  and SaleID=? and IState='3'";
            String[] whereArgsBys = {EmployeeID, SaleID};
            ContentValues valuesBys = new ContentValues();
            valuesBys.put("SendQP", sale.getSendQP());
            valuesBys.put("ReceiveQP", sale.getReceiveQP());
            valuesBys.put("DeliveryByInput", sale.getDeliverByInput());
            valuesBys.put("ReceiveByInput", sale.getReceiveByInput());
            valuesBys.put("GPS_J", sale.getGPS_J());
            valuesBys.put("GPS_W", sale.getGPS_W());
            valuesBys.put("FinishTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
            valuesBys.put("IState", "4");
            valuesBys.put("PayType", sale.getPayType());
            valuesBys.put("AZType", sale.getAZType());
            valuesBys.put("ISZS", sale.getISZS());
            int i = mDatabase.update("T_B_SALE", valuesBys,
                    whereClauseBys, whereArgsBys);
            if (i > 0) {
                ret = true;
            } else {
                ret = false;
            }
            int size = saleDetails.size();
            for (int a = 0; a < size; a++) {
                String QPType="";
                QPType=saleDetails.get(a).getQPType();
                String[] detail = {EmployeeID,  SaleID};
                if(saleDetails.get(a).getIsEx()==2){
                    whereClauseBys = "EmployeeID=? and SaleID=? and IsEx='2'";
                    detail = new String[]{EmployeeID,  SaleID};
                }else if(saleDetails.get(a).getIsEx()==1){
                    whereClauseBys = "EmployeeID=? and SaleID=? and IsEx='1'";
                    detail = new String[]{EmployeeID,  SaleID};
                }else{
                    detail = new String[] {EmployeeID,  SaleID,QPType};
                    whereClauseBys = "EmployeeID=? and  SaleID=? and IsEx='0' and QPType=?";
                }
                valuesBys = new ContentValues();
                valuesBys.put("SendNum", saleDetails.get(a).getSendNum());
                valuesBys.put("ReceiveNum", saleDetails.get(a).getReceiveNum());
                i = mDatabase.update("T_B_SALEDETAIL", valuesBys,
                        whereClauseBys, detail);
                if (i > 0) {
                    ret = true;
                } else {
                    ret = false;
                }
            }
            size = scanHistories.size();
            for (int b = 0; b < size; b++) {
                valuesBys = new ContentValues();
                valuesBys.put("EmployeeID", EmployeeID);
                valuesBys.put("StationID", StationID);
                valuesBys.put("UserID", scanHistories.get(b).getCustomerID());
                valuesBys.put("SaleID", scanHistories.get(b).getSaleID());
                valuesBys.put("UseRegCode", scanHistories.get(b).getUseRegCode());
                valuesBys.put("TypeFlag", scanHistories.get(b).getTypeFlag());
                valuesBys.put("QPType", scanHistories.get(b).getQPType());
                valuesBys.put("InsertTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                long h = mDatabase.insert("T_B_SCANHISTORY", null, valuesBys);
                if (h > 0) {
                    ret = true;
                } else {
                    ret = false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("更新订单异常", ex.toString());
        }
        return ret;

    }

    public boolean isExistScan(String EmployeeID, String StationID, String SaleID,String UseRegCode,String TypeFlag,String QPType) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?  and SaleID=? and UseRegCode=? and TypeFlag=? and QPType=?";
            String[] whereArgs = new String[]{EmployeeID, SaleID,UseRegCode,TypeFlag,QPType};
            Cursor cursor = mDatabase.query("T_B_SCANHISTORY", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 整理上传销售信息
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     */
    public HsicMessage uploadSaleInfo(Context context, String DeviceId, String EmployeeID, String StationID, String SaleID, String CustomID) {
        HsicMessage hsicMess = new HsicMessage();
        hsicMess.setRespCode(-1);
        SaleAll saleAll = new SaleAll();
        Sale sale = new Sale();
        List<ScanHistory> scanHistories = new ArrayList<ScanHistory>();
        List<SaleDetail> saleDetails = new ArrayList<SaleDetail>();
        try {
            String[] selectionArgs = new String[]{EmployeeID, SaleID};
            String sql = " select SendQP,ReceiveQP,DeliveryByInput,ReceiveByInput,GPS_J,GPS_W,AllPrice,RealPriceInfo,InspectionItem,FinishTime" +
                    ",PayType,AZType,ISZS from T_B_SALE where EmployeeID=? and  SaleID=?";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            if (query.moveToFirst()) {
                sale.setSaleID(SaleID);
                sale.setStation(StationID);
                sale.setCustomerID(CustomID);
                sale.setSendQP(query.getString(0));
                sale.setReceiveQP(query.getString(1));
                sale.setDeliverByInput(query.getString(2));
                sale.setReceiveByInput(query.getString(3));
                sale.setGPS_J(query.getDouble(4));
                sale.setGPS_W(query.getDouble(5));
                sale.setRealPirce(query.getString(6));
                sale.setRealPirceInfo(query.getString(7));
                sale.setInspectionItem(query.getString(8));
                sale.setFinishTime(query.getString(9));
                sale.setPayType(query.getString(10));
                sale.setAZType(query.getString(11));
                sale.setISZS(query.getString(12));
            }
            /**
             * saledetail
             */
            sql = "select SendNum,ReceiveNum,QPType,QPName,QTPrice,IsEx from T_B_SALEDETAIL where EmployeeID=? and SaleID=?";
            query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setSaleID(SaleID);
                saleDetail.setSendNum(query.getInt(0));
                saleDetail.setReceiveNum(query.getInt(1));
                saleDetail.setQPType(query.getString(2));
                saleDetail.setQPName(query.getString(3));
                saleDetail.setRealQTPrice(query.getString(4));
                int IsEx=Integer.parseInt(query.getString(5));
                saleDetail.setIsEx(IsEx);
                saleDetails.add(saleDetail);
            }
            /**
             * scanhistory
             */
            sql = "select QPType,TypeFlag,UseRegCode from T_B_SCANHISTORY where EmployeeID=?  and SaleID=?";
            query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                ScanHistory scanHistory = new ScanHistory();
                scanHistory.setQPType(query.getString(0));
                scanHistory.setTypeFlag(query.getString(1));
                scanHistory.setUseRegCode(query.getString(2));
                scanHistories.add(scanHistory);
            }
            query.close();
            sale.setSale_detail_info(saleDetails);
            saleAll.setSale(sale);
            saleAll.setScanHistory(scanHistories);
            String data = "";// 上传传输的数据
            data = JSONUtils.toJsonWithGson(saleAll);
            hsicMess.setRespMsg(data);
            String requestData = JSONUtils.toJsonWithGson(hsicMess);
            WebServiceHelper web = new WebServiceHelper(context);
            String[] selection = {"DeviceID", "RequestData"};
            selectionArgs = new String[]{DeviceId, requestData};
            String methodName = "";
            methodName = "UpdateSaleInfo";// 方法名称
            hsicMess = web.uploadInfo(selection, methodName, selectionArgs);
            if (hsicMess.getRespCode() == 0) {
                updateSaleStatus(EmployeeID, StationID, SaleID);
            }
        } catch (Exception ex) {
            hsicMess.setRespCode(-2);
            hsicMess.setRespMsg("上传销售信息时接口异常");
            ex.printStackTrace();
        }
        return hsicMess;
    }

    /**
     * 打印
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public List<SaleDetail> GetSaleDetailByP(String EmployeeID, String StationID, String SaleID) {
        List<SaleDetail> details = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID, SaleID};
            String sql = "select SendNum,QPType,QPName,QTPrice,PlanSendNum,IsEx from T_B_SALEDETAIL where EmployeeID=?  and SaleID=?";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                SaleDetail saleDetail = new SaleDetail();
                saleDetail.setSaleID(SaleID);
                saleDetail.setSendNum(query.getInt(0));
                saleDetail.setQPType(query.getString(1));
                saleDetail.setQPName(query.getString(2));
                saleDetail.setQPPrice(query.getString(3));
                int PlanSendNum=Integer.parseInt(query.getString(4));
                saleDetail.setPlanSendNum(PlanSendNum);
                int IsEx=Integer.parseInt(query.getString(5));
                saleDetail.setIsEx(IsEx);
                details.add(saleDetail);
            }
            query.close();
        } catch (Exception ex) {
            ex.toString();
            ex.printStackTrace();
        }
        return details;
    }

    /**
     * 更新本地订单状态
     */
    public int updateSaleStatus(String EmployeeID, String StationID, String SaleID) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=?  and SaleID=?";
        String[] whereArgs = {EmployeeID, SaleID};
        values.put("IState", "5");
        try {
            ret = mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }
    /**
     * 更新本地订单状态
     */
    public int updateURL(String EmployeeID, String StationID, String SaleID,String URL) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and SaleID=?";
        String[] whereArgs = {EmployeeID,  SaleID};
        values.put("URL", URL);
        try {
            ret = mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }
    /**
     * 退单成功 8
     */
    public int ChargeBackStatus(String EmployeeID, String StationID, String SaleID, String IState) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=?  and SaleID=?";
        String[] whereArgs = {EmployeeID, SaleID};
        values.put("IState", IState);
        try {
            ret = mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 更新催单状态
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param UrgeGasInfoStatus
     * @return
     */
    public int upUrgeStatus(String EmployeeID, String StationID, String SaleID, String UrgeGasInfoStatus) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=?  and SaleID=?";
        String[] whereArgs = {EmployeeID, SaleID};
        values.put("UrgeGasInfoStatus", UrgeGasInfoStatus);
        try {
            ret = mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 将安检信息保存到本地
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param userXJInfo
     * @return
     */
    public boolean updateInspectionInfo(String EmployeeID, String StationID, String SaleID, UserXJInfo userXJInfo) {
        boolean ret = false;
//        try {
        /**
         * 更新安检信息
         */
        String whereClauseBys = "EmployeeID=?  and SaleID=? ";
        String[] whereArgsBys = {EmployeeID, SaleID};
        ContentValues valuesBys = new ContentValues();
        valuesBys.put("RelationID", userXJInfo.getAttachID());
        valuesBys.put("IsInspected", "1");
        valuesBys.put("InspectionStatus", userXJInfo.getInspectionStatus());
        valuesBys.put("InspectionDate", userXJInfo.getInspectionDate());
        valuesBys.put("InspectionMan", userXJInfo.getInspectionMan());
        valuesBys.put("StopSupplyType1", userXJInfo.getStopSupplyType1() != null ? userXJInfo.getStopSupplyType1() : "0");
        valuesBys.put("StopSupplyType2", userXJInfo.getStopSupplyType2() != null ? userXJInfo.getStopSupplyType2() : "0");
        valuesBys.put("StopSupplyType3", userXJInfo.getStopSupplyType3() != null ? userXJInfo.getStopSupplyType3() : "0");
        valuesBys.put("StopSupplyType4", userXJInfo.getStopSupplyType4() != null ? userXJInfo.getStopSupplyType4() : "0");
        valuesBys.put("StopSupplyType5", userXJInfo.getStopSupplyType5() != null ? userXJInfo.getStopSupplyType5() : "0");
        valuesBys.put("StopSupplyType6", userXJInfo.getStopSupplyType6() != null ? userXJInfo.getStopSupplyType6() : "0");
        valuesBys.put("StopSupplyType7", userXJInfo.getStopSupplyType7() != null ? userXJInfo.getStopSupplyType7() : "0");
        valuesBys.put("StopSupplyType8", userXJInfo.getStopSupplyType8() != null ? userXJInfo.getStopSupplyType8() : "0");
        valuesBys.put("StopSupplyType9", userXJInfo.getStopSupplyType9() != null ? userXJInfo.getStopSupplyType9() : "0");
        valuesBys.put("StopSupplyType10", userXJInfo.getStopSupplyType10() != null ? userXJInfo.getStopSupplyType10() : "0");
        valuesBys.put("StopSupplyType11", userXJInfo.getStopSupplyType11() != null ? userXJInfo.getStopSupplyType11() : "0");
        valuesBys.put("StopSupplyType12", userXJInfo.getStopSupplyType12() != null ? userXJInfo.getStopSupplyType12() : "0");
        valuesBys.put("StopSupplyType13", userXJInfo.getStopSupplyType13() != null ? userXJInfo.getStopSupplyType13() : "0");
        valuesBys.put("StopSupplyType14", userXJInfo.getStopSupplyType14() != null ? userXJInfo.getStopSupplyType14() : "0");
        valuesBys.put("StopSupplyType15", userXJInfo.getStopSupplyType15() != null ? userXJInfo.getStopSupplyType15() : "0");

        valuesBys.put("UnInstallType1", userXJInfo.getUnInstallType1() != null ? userXJInfo.getUnInstallType1() : "0");
        valuesBys.put("UnInstallType2", userXJInfo.getUnInstallType2() != null ? userXJInfo.getUnInstallType2() : "0");
        valuesBys.put("UnInstallType3", userXJInfo.getUnInstallType3() != null ? userXJInfo.getUnInstallType3() : "0");
        valuesBys.put("UnInstallType4", userXJInfo.getUnInstallType4() != null ? userXJInfo.getUnInstallType4() : "0");
        valuesBys.put("UnInstallType5", userXJInfo.getUnInstallType5() != null ? userXJInfo.getUnInstallType5() : "0");
        valuesBys.put("UnInstallType6", userXJInfo.getUnInstallType6() != null ? userXJInfo.getUnInstallType6() : "0");
        valuesBys.put("UnInstallType7", userXJInfo.getUnInstallType7() != null ? userXJInfo.getUnInstallType7() : "0");
        valuesBys.put("UnInstallType8", userXJInfo.getUnInstallType8() != null ? userXJInfo.getUnInstallType8() : "0");
        valuesBys.put("UnInstallType9", userXJInfo.getUnInstallType9() != null ? userXJInfo.getUnInstallType9() : "0");
        valuesBys.put("UnInstallType10", userXJInfo.getUnInstallType10() != null ? userXJInfo.getUnInstallType10() : "0");
        valuesBys.put("UnInstallType11", userXJInfo.getUnInstallType11() != null ? userXJInfo.getUnInstallType11() : "0");
        valuesBys.put("UnInstallType12", userXJInfo.getUnInstallType12() != null ? userXJInfo.getUnInstallType12() : "0");
        valuesBys.put("UnInstallType13", userXJInfo.getUnInstallType13() != null ? userXJInfo.getUnInstallType13() : "0");
        valuesBys.put("UnInstallType14", userXJInfo.getUnInstallType14() != null ? userXJInfo.getUnInstallType14() : "0");
        int i = mDatabase.update("T_B_CUSTOMERINFO", valuesBys,
                whereClauseBys, whereArgsBys);
        if (i > 0) {
            ret = true;
        }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return ret;
    }

    /**
     * 上传安检信息
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param UserID
     */
    public HsicMessage uploadXJInfo(Context context, String DeviceId, String EmployeeID, String StationID, String SaleID, String UserID) {
        HsicMessage hsicMess = new HsicMessage();
        WebServiceHelper web = new WebServiceHelper(context);
        try {
            hsicMess.setRespCode(10);
            UserXJInfo userXJInfo = new UserXJInfo();
            String[] selections = {EmployeeID, SaleID};
            String sql = "select TypeClass,RelationID,InspectionStatus,InspectionDate,InspectionMan,StopSupplyType1,StopSupplyType2,StopSupplyType3," +
                    "StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8," +
                    "StopSupplyType9,StopSupplyType10,StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15," +
                    "UnInstallType1,UnInstallType2,UnInstallType3," +
                    "UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                    "UnInstallType12,UnInstallType13,UnInstallType14 from T_B_CUSTOMERINFO where EmployeeID=?  and SaleID=? and IsInspected='1'";
            Cursor query = mDatabase.rawQuery(sql, selections);
            if (query.moveToFirst()) {
                userXJInfo.setTypeClass(query.getString(0));
                userXJInfo.setAttachID(query.getString(1));
                userXJInfo.setInspectionStatus(query.getString(2));
                userXJInfo.setInspectionDate(query.getString(3));
                userXJInfo.setInspectionMan(query.getString(4));
                userXJInfo.setStopSupplyType1(query.getString(5));
                userXJInfo.setStopSupplyType2(query.getString(6));
                userXJInfo.setStopSupplyType3(query.getString(7));
                userXJInfo.setStopSupplyType4(query.getString(8));
                userXJInfo.setStopSupplyType5(query.getString(9));
                userXJInfo.setStopSupplyType6(query.getString(10));
                userXJInfo.setStopSupplyType7(query.getString(11));
                userXJInfo.setStopSupplyType8(query.getString(12));
                userXJInfo.setStopSupplyType9(query.getString(13));
                userXJInfo.setStopSupplyType10(query.getString(14));
                userXJInfo.setStopSupplyType11(query.getString(15));
                userXJInfo.setStopSupplyType12(query.getString(16));
                userXJInfo.setStopSupplyType13(query.getString(17));
                userXJInfo.setStopSupplyType14(query.getString(18));
                userXJInfo.setStopSupplyType15(query.getString(19));
                userXJInfo.setUnInstallType1(query.getString(20));
                userXJInfo.setUnInstallType2(query.getString(21));
                userXJInfo.setUnInstallType3(query.getString(22));
                userXJInfo.setUnInstallType4(query.getString(23));
                userXJInfo.setUnInstallType5(query.getString(24));
                userXJInfo.setUnInstallType6(query.getString(25));
                userXJInfo.setUnInstallType7(query.getString(26));
                userXJInfo.setUnInstallType8(query.getString(27));
                userXJInfo.setUnInstallType9(query.getString(28));
                userXJInfo.setUnInstallType10(query.getString(29));
                userXJInfo.setUnInstallType11(query.getString(30));
                userXJInfo.setUnInstallType12(query.getString(31));
                userXJInfo.setUnInstallType13(query.getString(32));
                userXJInfo.setUnInstallType14(query.getString(33));
                userXJInfo.setStationcode(StationID);
                userXJInfo.setSaleid(SaleID);
                userXJInfo.setUserid(UserID);
            }
            query.close();
            String data = "";// 上传传输的数据
            data = JSONUtils.toJsonWithGson(userXJInfo);
            hsicMess.setRespMsg(data);
            String requestData = JSONUtils.toJsonWithGson(hsicMess);
            String[] selection = {"DeviceID", "RequestData"};
            String[] selectionArgs = {DeviceId, requestData};
            String methodName = "";
            methodName = "UpUserInspectionInfo_New";// 方法名称
            hsicMess = web.uploadInfo(selection, methodName, selectionArgs);
            int i = hsicMess.getRespCode();// 方法执行结果
            if (i == 0||i == 2) {
                updateXJStatus(EmployeeID, StationID, SaleID);
            }
        } catch (Exception ex) {
            ex.toString();
            ex.printStackTrace();
        }
        return hsicMess;
    }

    /**
     * 更新本地订单状态
     */
    public int updateXJStatus(String EmployeeID, String StationID, String SaleID) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=?  and SaleID=?";
        String[] whereArgs = {EmployeeID, SaleID};
        values.put("IsInspected", "2");
        try {
            ret = mDatabase.update("T_B_CUSTOMERINFO", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 将照片基本信息插入到数据表中  安检
     *
     * @param
     * @param SaleID
     * @param
     * @param RelationID
     */
    public void InsertXJAssociation(String EmployeeID, String SaleID, String ImageName, String RelationID, String FileName) {
        try {
            ContentValues cValue = new ContentValues();
            cValue.put("EmployeeID", EmployeeID);
            cValue.put("SaleID", SaleID);
            cValue.put("ImageName", ImageName);
            cValue.put("RelationID", RelationID);
            cValue.put("FileName", FileName);
            cValue.put("IsUpload", "0");
            cValue.put("InsertTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));

            long ret = mDatabase.insert("T_B_XJ_Association", null, cValue);
        } catch (Exception ex) {
            ex.toString();

        }
    }

    public void DeleteXJAssociation(String Path, String EmployeeID, String SaleID) {
        try {
            String whereClause_t = "ImageName=? and EmployeeID=? and SaleID=?";
            String[] whereArgs_t = {Path, EmployeeID, SaleID};
            long ret = mDatabase.delete("T_B_XJ_Association", whereClause_t, whereArgs_t);
        } catch (Exception ex) {
            Log.e("删除照片异常", ex.toString());
        }
    }

    /**
     * 查询该车次该订单下所有的照片关联信息 安检
     */
    public List<FileRelationInfo> GetXJFileRelationInfo(String EmployeeID, String SaleID) {
        List<FileRelationInfo> FileRelationInfo_LIST = new ArrayList<FileRelationInfo>();
        try {
            String[] selectionArgs = {EmployeeID, SaleID};
            String sql = "select RelationID,ImageName,FileName  from  T_B_XJ_Association where EmployeeID=? and SaleID=? and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                FileRelationInfo f = new FileRelationInfo();
                f.setRelationID(query.getString(0));
                String FilePath = query.getString(2) + "/" + query.getString(1);
                f.setFilePath(FilePath);
                f.setImageName(query.getString(1));
                f.setTruckNoId(EmployeeID);
                f.setSaleID(SaleID);
                FileRelationInfo_LIST.add(f);
            }
            query.close();
        } catch (Exception ex) {

        }
        return FileRelationInfo_LIST;
    }

    /**
     * 查询该车次该订单下所有的照片关联信息 安检
     */
    public List<FileRelationInfo> GetHistoryXJFileRelationInfo(String EmployeeID) {
        List<FileRelationInfo> FileRelationInfo_LIST = new ArrayList<FileRelationInfo>();
        try {
            String[] selectionArgs = {EmployeeID};
            String sql = "select RelationID,ImageName,FileName,SaleID  from  T_B_XJ_Association where EmployeeID=?  and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                FileRelationInfo f = new FileRelationInfo();
                f.setRelationID(query.getString(0));
                String FilePath = query.getString(2) + "/" + query.getString(1);
                f.setFilePath(FilePath);
                f.setImageName(query.getString(1));
                f.setTruckNoId(EmployeeID);
                f.setSaleID(query.getString(3));
                FileRelationInfo_LIST.add(f);
            }
            query.close();
        } catch (Exception ex) {

        }
        return FileRelationInfo_LIST;
    }

    public int UpDateXJAssociation(String EmployeeID, String SaleID, String Path) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and SaleID=? and ImageName=?";
        String[] whereArgs = {EmployeeID, SaleID, Path};
        values.put("IsUpLoad", "1");
        try {
            ret = mDatabase.update("T_B_XJ_Association", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 上传附件关联信息
     *
     * @param FileRelationInfo_LIST
     * @param deviceid
     * @param context
     * @return
     */
    public HsicMessage UpLoadA(List<FileRelationInfo> FileRelationInfo_LIST, String deviceid, Context context) {
        HsicMessage hsicMessage = new HsicMessage();
        hsicMessage.setRespCode(10);
        try {
            List<FileRelationInfo> upLoadSuc = new ArrayList<FileRelationInfo>();//暂存已经上传成功的附件信息
            int size = 0;
            size = FileRelationInfo_LIST.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    List<FileRelationInfo> list = new ArrayList<FileRelationInfo>();//上传附件信息
                    FileRelationInfo fri = FileRelationInfo_LIST.get(i);
                    fri.setFilePath(fri.getFilePath());
                    fri.setRelationID(fri.getRelationID());
                    String ImageName = fri.getImageName();
                    String TruckNoId = fri.getTruckNoId();
                    String SaleID = fri.getSaleID();
                    list.add(fri);
                    String respMsg = JSONUtils.toJsonWithGson(list);
                    hsicMessage.setRespMsg(respMsg);
                    WebServiceHelper web = new WebServiceHelper(context);
                    String requestData = JSONUtils.toJsonWithGson(hsicMessage);// web接口参数
                    String[] webRe = {"DeviceID", "RequestData"};
                    String webMethod = "UpFileReletionInfo";
                    String[] value = {deviceid, requestData};
                    hsicMessage = web.uploadInfo(webRe, webMethod, value);
                    if (hsicMessage.getRespCode() == 0) {
                        //关联表上传成功更新本地上传字段
                        FileRelationInfo f = new FileRelationInfo();
                        f.setTruckNoId(TruckNoId);
                        f.setSaleID(SaleID);
                        f.setImageName(ImageName);
                        upLoadSuc.add(f);

                    }
                }
            }
            //批量更新上传成功以后的关联表本地字段
            int size_up = upLoadSuc.size();
            for (int m = 0; m < size_up; m++) {
                String ImageName = upLoadSuc.get(m).getImageName();
                String TruckNoId = upLoadSuc.get(m).getTruckNoId();
                String SaleID = upLoadSuc.get(m).getSaleID();
                UpDateXJAssociation(TruckNoId, SaleID, ImageName);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            hsicMessage.setRespCode(5);
            hsicMessage.setRespMsg("调用借口异常");
        }
        return hsicMessage;
    }

    /**
     * 查询本地订单
     *
     * @return
     */
    public List<Map<String, String>> saleID(String EmployeeID, String StationID) {
        List<Map<String, String>> saleID = new ArrayList<Map<String, String>>();
        String[] selectionArgs = {EmployeeID};
        try {
            String sql = "select SaleID from T_B_SALE where EmployeeID=?  and IState='3'";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("SaleID", cursor.getString(0));
                saleID.add(map);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return saleID;
    }

    /**
     * 获取已经做完未上传的订单[IState:4]
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> UpHistorySale(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID from T_B_SALE where EmployeeID=?  and IState='4'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {

        }
        return sales;
    }

    public List<Sale> UpHistoryXJ(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID from T_B_CUSTOMERINFO where EmployeeID=?  and IsInspected='1'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sales;
    }

    /**
     * 上传历史订单
     *
     * @param context
     * @param DeviceId
     * @param EmployeeID
     * @param StationID
     */
    public void UpHistorySale(Context context, String DeviceId, String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        sales = UpHistorySale(EmployeeID, StationID);
        int size = sales.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String SaleID;
                String CustomID;
                SaleID = sales.get(i).getSaleID();
                CustomID = sales.get(i).getCustomerID();
                uploadSaleInfo(context, DeviceId, EmployeeID, StationID, SaleID, CustomID);
                /**
                 *上传安检关联表历史信息
                 */
                List<FileRelationInfo> fileRelationInfos = new ArrayList<>();
                fileRelationInfos = GetXJFileRelationInfo(EmployeeID, SaleID);
                size = fileRelationInfos.size();
                if (size > 0) {
                    UpLoadA(fileRelationInfos, DeviceId, context);
                }
            }

        }
    }

    /**
     * 上传安检历史信息
     *
     * @param context
     * @param DeviceId
     * @param EmployeeID
     * @param StationID
     */
    public void UpHistoryXJ(Context context, String DeviceId, String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        sales = UpHistoryXJ(EmployeeID, StationID);
        int size = sales.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String SaleID;
                String CustomID;
                SaleID = sales.get(i).getSaleID();
                CustomID = sales.get(i).getCustomerID();
                uploadXJInfo(context, DeviceId, EmployeeID, StationID, SaleID, CustomID);
            }

        }
        /**
         *上传安检关联表历史信息
         */
        List<FileRelationInfo> fileRelationInfos = new ArrayList<>();
        fileRelationInfos = GetHistoryXJFileRelationInfo(EmployeeID);
        size = fileRelationInfos.size();
        if (size > 0) {
            UpLoadA(fileRelationInfos, DeviceId, context);
        }
    }

    /**
     * 本地所有订单
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> AllSale(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID from T_B_SALE where EmployeeID=? and  (IState='3'or IState='4' or IState='5')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {

        }
        return sales;
    }

    /**
     * 已上传
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> uploadList(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID from T_B_SALE where EmployeeID=? and IState='5'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {

        }
        return sales;
    }

    /**
     * 已完成[完成+上传]
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> finishList(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID from T_B_SALE where EmployeeID=?  and (IState='4'or IState='5')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {
            ex.toString();
        }
        return sales;
    }

    /**
     * @param EmployeeID
     * @param StationID
     * @param customCardID
     */
    public List<UserXJInfo> getInfoByCustom(String EmployeeID, String StationID, String customCardID) {
        List<UserXJInfo> userXJInfos = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID, customCardID};
            String sql = " select SaleID,UserID,CustomerName,Telephone,Address,CustomerTypeName from T_B_CUSTOMERINFO where EmployeeID=? and CustomerCardID=?";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                UserXJInfo userXJInfo = new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        } catch (Exception ex) {

        }
        return userXJInfos;
    }

    public List<UserXJInfo> getInfoByAddress(String EmployeeID, String StationID, String Address) {
        List<UserXJInfo> userXJInfos = new ArrayList<>();
        String[] selectionArgs = new String[]{EmployeeID};
        String sql = " select SaleID,UserID,CustomerName,Telephone,Address,CustomerTypeName from T_B_CUSTOMERINFO where EmployeeID=?  and " +
                "Address like  '%" + Address + "%' ";
        Cursor query = mDatabase.rawQuery(sql, selectionArgs);
        while (query.moveToNext()) {
            UserXJInfo userXJInfo = new UserXJInfo();
            userXJInfo.setSaleid(query.getString(0));
            userXJInfo.setUserid(query.getString(1));
            userXJInfo.setUsername(query.getString(2));
            userXJInfo.setTelephone(query.getString(3));
            userXJInfo.setDeliveraddress(query.getString(4));
            userXJInfo.setCustomerTypeName(query.getString(5));
            userXJInfos.add(userXJInfo);
        }
        query.close();
        return userXJInfos;
    }

    public List<UserXJInfo> getInfoByTelephone(String EmployeeID, String StationID, String Telephone) {
        List<UserXJInfo> userXJInfos = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID,CustomerName,Telephone,Address,CustomerTypeName from T_B_CUSTOMERINFO where EmployeeID=? and" +
                    " Telephone like  '%" + Telephone + "%'";
            Log.e("sql", sql);
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                UserXJInfo userXJInfo = new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        } catch (Exception ex) {

        }
        return userXJInfos;
    }

    public List<UserXJInfo> getInfoByUserName(String EmployeeID, String StationID, String userName) {
        List<UserXJInfo> userXJInfos = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select SaleID,UserID,CustomerName,Telephone,Address,CustomerTypeName from T_B_CUSTOMERINFO where EmployeeID=?  and" +
                    " CustomerName  like  '%" + userName + "%'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                UserXJInfo userXJInfo = new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        } catch (Exception ex) {

        }
        return userXJInfos;
    }

    /***
     * 统计已经完成配送的订单数量
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> SaleFinishCount(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID,};
            String sql = " select SaleID,UserID from T_B_SALE where EmployeeID=?  and (IState='4' or IState='5')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sales;
    }

    /**
     * 统计瓶数
     *
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public int SaleQPCount(String EmployeeID, String StationID) {
        int ret = 0;
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select b.SendNum,a.SaleID,b.IsEx from T_B_SALE a left join T_B_SALEDETAIL b on " +
                    "a.SaleID=b.SaleID  where a.EmployeeID=?  and  (a.IState='4'or a.IState='5') and b.IsEx='0' and date(a.FinishTime)= date('now') ";
            ;
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                String tmp = query.getString(0);
                if (tmp == null) {
                    tmp = "";
                }
                if (!tmp.equals("")) {
                    ret = ret + Integer.parseInt(tmp);
                } else {
                    ret = ret + 0;
                }

            }
            query.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    /***
     * 统计已经完成整改订单数量
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> RectifyFinishCount(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select UserID from T_B_UserRectifyInfo where EmployeeID=?  and (IsInspected='1' or IsInspected='2')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setCustomerID(query.getString(0));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {

        }
        return sales;
    }

    /***
     * 统计已经完成安检订单数量
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> SearchFinishCount(String EmployeeID, String StationID) {
        List<Sale> sales = new ArrayList<>();
        try {
            String[] selectionArgs = new String[]{EmployeeID};
            String sql = " select UserID from T_B_AJINFO where EmployeeID=?  and (IsInspected='1' or IsInspected='2')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Sale sale = new Sale();
                sale.setCustomerID(query.getString(0));
                sales.add(sale);
            }
            query.close();
        } catch (Exception ex) {

        }
        return sales;
    }
    /**
     * 安检信息上传成功
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean isUpLoadInspection(String EmployeeID, String StationID, String SaleID) {
        boolean result = false;
        try {
            String whereClause = "EmployeeID=?   and IsInspected='2'";
            String[] whereArgs = new String[]{EmployeeID, SaleID};
            Cursor cursor = mDatabase.query("T_B_CUSTOMERINFO", null, whereClause,
                    whereArgs, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新安检项目字段
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param InspectionItem
     * @return
     */
    public int InspectionItem(String EmployeeID, String StationID, String SaleID, String InspectionItem) {
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and SaleID=?";
        String[] whereArgs = {EmployeeID, SaleID};
        values.put("InspectionItem", InspectionItem);
        try {
            ret = mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 获取 RelationID
     * @return
     */
    public String getRelationID(String EmployeeID, String StationID, String SaleID, String InspectionMan){
        String ret="";
        String[] selections = {EmployeeID, SaleID};
        String sql="select RelationID from T_B_CUSTOMERINFO  where EmployeeID=?  and SaleID=? and " +
                "(IsInspected='1'or IsInspected='2')";
        Cursor query = mDatabase.rawQuery(sql, selections);
        if(query.moveToFirst()){
            ret=query.getString(0);
        }
        query.close();
        return ret;

    }


    /**
     * 打印安检信息
     *
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public String getInspectionStr(String EmployeeID, String StationID, String SaleID, String InspectionMan) {
        StringBuffer out = new StringBuffer();
        String TypeClass = "";
        String[] selections = {EmployeeID, SaleID};
        String sql = "select InspectionStatus,TypeClass,StopSupplyType1,StopSupplyType2,StopSupplyType3," +
                "StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,StopSupplyType9," +
                "StopSupplyType10,StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15," +
                "UnInstallType1,UnInstallType2,UnInstallType3," +
                "UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                "UnInstallType12,UnInstallType13,UnInstallType14,UserID,Address,InspectionMan,InspectionDate,CustomerName " +
                "from T_B_CUSTOMERINFO where EmployeeID=?  and SaleID=? and " +
                "(IsInspected='1'or IsInspected='2')";
        Cursor query = mDatabase.rawQuery(sql, selections);
        if (query.moveToFirst()) {
            TypeClass = query.getString(1);
            out.append("安检收据\n");
            out.append("--------------------------------\n");
            out.append(PrintUtils.printTwoData("安检单", query.getString(31) + "\n"));
            out.append(PrintUtils.printTwoData("安检用户", query.getString(35) + "\n"));
            out.append(query.getString(32) + "\n");
            if (query.getString(0).equals("0")) {
                out.append(PrintUtils.printTwoData("本次安检结果:", "合格\n"));
            } else if (query.getString(0).equals("1")) {
                out.append(PrintUtils.printTwoData("本次安检结果:", "一般隐患\n"));
                out.append("本次安检不合格项:" + "\n");
                if (!TypeClass.equals("1")) {
                    //民用
                    if (query.getString(17).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType1) + "\n");
                    }
                    if (query.getString(18).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType2) + "\n");
                    }
                    if (query.getString(19).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType3) + "\n");
                    }
                    if (query.getString(20).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType4) + "\n");
                    }
                    if (query.getString(21).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType5) + "\n");
                    }
                    if (query.getString(22).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType6) + "\n");
                    }
                    if (query.getString(23).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType7) + "\n");
                    }
                    if (query.getString(24).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType8) + "\n");
                    }
                    if (query.getString(25).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType9) + "\n");
                    }
                    if (query.getString(26).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType10) + "\n");
                    }
                    if (query.getString(27).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType11) + "\n");
                    }
                    if (query.getString(28).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType12) + "\n");
                    }
                    if (query.getString(29).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_UnInstallType13) + "\n");
                    }
                } else {
                    //非民用
                    if (query.getString(17).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType1) + "\n");
                    }
                    if (query.getString(18).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType2) + "\n");
                    }
                    if (query.getString(19).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType3) + "\n");
                    }
                    if (query.getString(20).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType4) + "\n");
                    }
                    if (query.getString(21).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType5) + "\n");
                    }
                    if (query.getString(22).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType6) + "\n");
                    }
                    if (query.getString(23).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType7) + "\n");
                    }
                    if (query.getString(24).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType8) + "\n");
                    }
                    if (query.getString(25).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType9) + "\n");
                    }
                    if (query.getString(26).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType10) + "\n");
                    }
                    if (query.getString(27).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType11) + "\n");
                    }
                    if (query.getString(28).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType12) + "\n");
                    }
                    if (query.getString(29).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType13) + "\n");
                    }
                    if (query.getString(30).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_UnInstallType14) + "\n");
                    }
                }
            } else {
                out.append(PrintUtils.printTwoData("本次安检结果:", "严重隐患\n"));
                out.append("本次安检不合格项:" + "\n");
                if (!TypeClass.equals("1")) {
                    //民用
                    if (query.getString(2).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType1) + "\n");
                    }
                    if (query.getString(3).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType2) + "\n");
                    }
                    if (query.getString(4).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType3) + "\n");
                    }
                    if (query.getString(5).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType4) + "\n");
                    }
                    if (query.getString(6).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType5) + "\n");
                    }
                    if (query.getString(7).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType6) + "\n");
                    }
                    if (query.getString(8).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType7) + "\n");
                    }
                    if (query.getString(9).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType8) + "\n");
                    }
                    if (query.getString(10).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType9) + "\n");
                    }
                    if (query.getString(11).equals("1")) {
                        out.append(context.getResources().getString(R.string.c_StopSupplyType10) + "\n");
                    }
                } else {
                    //非民用
                    if (query.getString(2).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType1) + "\n");
                    }
                    if (query.getString(3).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType2) + "\n");
                    }
                    if (query.getString(4).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType3) + "\n");
                    }
                    if (query.getString(5).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType4) + "\n");
                    }
                    if (query.getString(6).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType5) + "\n");
                    }
                    if (query.getString(7).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType6) + "\n");
                    }
                    if (query.getString(8).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType7) + "\n");
                    }
                    if (query.getString(9).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType8) + "\n");
                    }
                    if (query.getString(10).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType9) + "\n");
                    }
                    if (query.getString(11).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType10) + "\n");
                    }
                    if (query.getString(12).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType11) + "\n");
                    }
                    if (query.getString(13).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType12) + "\n");
                    }
                    if (query.getString(14).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType13) + "\n");
                    }
                    if (query.getString(15).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType14) + "\n");
                    }
                    if (query.getString(16).equals("1")) {
                        out.append(context.getResources().getString(R.string.b_StopSupplyType15) + "\n");
                    }
                }
            }
            out.append(PrintUtils.printTwoData("安检人:", InspectionMan + "\n"));
            out.append(PrintUtils.printTwoData("安检日期:", query.getString(34) + "\n"));
        } else {
            //没有做安检
            out.append("本次没有做安检" + "\n");
        }
        query.close();
        return out.toString();
    }
    /**
     * 获取订单上次安检信息
     * @return
     */
    public Map<String, String> getXJ(String EmployeeID, String StationID, String SaleID) {
        Map<String, String> map = new HashMap<String, String>();
        String[] selection = { EmployeeID, SaleID };
        String sql = "select Last_InspectionStatus,StopSupplyType1,StopSupplyType2,StopSupplyType3,StopSupplyType4,"
                + "StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,StopSupplyType9,StopSupplyType10," +
                "StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,UnInstallType1,"
                + "UnInstallType2,UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6,"
                + "UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11,UnInstallType12,UnInstallType13,UnInstallType14"
                + " from T_B_CUSTOMERINFO where EmployeeID=?  and SaleID=?";
//        try {
            Cursor query = mDatabase.rawQuery(sql, selection);
            if (query.moveToFirst()) {
                map.put("InspectionStatus", query.getString(0));
                map.put("StopSupplyType1", query.getString(1));
                map.put("StopSupplyType2", query.getString(2));
                map.put("StopSupplyType3", query.getString(3));
                map.put("StopSupplyType4", query.getString(4));
                map.put("StopSupplyType5", query.getString(5));
                map.put("StopSupplyType6", query.getString(6));
                map.put("StopSupplyType7", query.getString(7));
                map.put("StopSupplyType8", query.getString(8));
                map.put("StopSupplyType9", query.getString(9));
                map.put("StopSupplyType10", query.getString(10));
                map.put("StopSupplyType11", query.getString(11));
                map.put("StopSupplyType12", query.getString(12));
                map.put("StopSupplyType13", query.getString(13));
                map.put("StopSupplyType14", query.getString(14));
                map.put("StopSupplyType15", query.getString(15));
                map.put("UnInstallType1", query.getString(16));
                map.put("UnInstallType2", query.getString(17));
                map.put("UnInstallType3", query.getString(18));
                map.put("UnInstallType4", query.getString(19));
                map.put("UnInstallType5", query.getString(20));
                map.put("UnInstallType6", query.getString(21));
                map.put("UnInstallType7", query.getString(22));
                map.put("UnInstallType8", query.getString(23));
                map.put("UnInstallType9", query.getString(24));
                map.put("UnInstallType10", query.getString(25));
                map.put("UnInstallType11", query.getString(26));
                map.put("UnInstallType12", query.getString(27));
                map.put("UnInstallType13", query.getString(28));
                map.put("UnInstallType14", query.getString(29));

            }
        query.close();
//        } catch (Exception ex) {
//            return null;
//        }
        return map;
    }

    /**
     * 橡皮管赠送
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @return
     */
    public boolean rubberTube(String EmployeeID, String StationID, String SaleID) {
       boolean ret=false;
        String sqld = "select PlanSendNum,QPName,IsEx from T_B_SALEDETAIL where SaleID=? and EmployeeID=? and (IsEx='0'or IsEx='2')";
        try {
            Cursor query = mDatabase.rawQuery(sqld,
                    new String[]{SaleID, EmployeeID});
            while (query.moveToNext()) {
                String IsEx="";
                IsEx=query.getString(2);
                if(IsEx.equals("2")){
                    ret=true;
                    return  ret;
                }

            }
            query.close();
        } catch (Exception ex) {
            ret=false;
            ex.printStackTrace();
        }
        return  ret;
    }
    public List<SaleDetail>  getGoodsPrice(String SaleID){
        List<SaleDetail> saleDetailList=new ArrayList<SaleDetail>();
        String sqld = "select QPType,QPName,QTPrice,IsEx from T_B_SALEDETAIL where SaleID=? ";
        try {
            Cursor query = mDatabase.rawQuery(sqld,
                    new String[]{SaleID});
            while (query.moveToNext()) {
             SaleDetail saleDetail=new SaleDetail();
                saleDetail.setQPType(query.getString(0));
                saleDetail.setQPName(query.getString(1));
                saleDetail.setQPPrice(query.getString(2));
                saleDetailList.add(saleDetail);
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return saleDetailList;
    }

    /**
     * 更新商品价格明细表
     */
    public  boolean updateGoodsPrice(String saleID,String QPType,String newPrice){
        int ret = -1;
        ContentValues values = new ContentValues();
        String whereClause = " SaleID=? and QPType=?";
        String[] whereArgs = {saleID, QPType};
        values.put("QTPrice", newPrice);
        try {
            ret = mDatabase.update("T_B_SALEDETAIL", values, whereClause, whereArgs);
            if(ret>0){
                return true;
            }else{
                return false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            ret = -1;
            return false;
        }
    }
    /**
     * 更新商品价格总表
     */
    public boolean   updateALLPrice(String saleID){
        String salePriceStr;
        float salePrice=0;
        String sqld = "select QPType,QPName,QTPrice,IsEx from T_B_SALEDETAIL where SaleID=? ";
        try {
            Cursor query = mDatabase.rawQuery(sqld,
                    new String[]{saleID});
            while (query.moveToNext()) {
                String tmp=query.getString(2);
                salePrice=salePrice+Float.parseFloat(tmp);
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        salePriceStr=String.format("%.2f",salePrice);
        ContentValues values = new ContentValues();
        String whereClause = " SaleID=? ";
        String[] whereArgs = {saleID};
        values.put("AllPrice", salePriceStr);
        try {
            long l=mDatabase.update("T_B_SALE", values, whereClause, whereArgs);
            if(l>0){
                return true;
            }else{
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public String getQPSalePrice(String saleID){
        String ret="";
        String sqld = "select AllPrice from T_B_SALE where SaleID=? ";
        Cursor query = mDatabase.rawQuery(sqld,
                new String[]{saleID});
        if(query.moveToFirst()){
            ret=query.getString(0);
        }
        query.close();
        return ret;
    }

    /**
     * 看街道信息是否存在
     *
     * @return
     */
    public boolean streetIsExist() {
        boolean result = false;
        try {
            Cursor cursor = mDatabase.query("T_B_ADDRESS", null, null,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 看客户类型信息是否存在
     *
     * @return
     */
    public boolean customerInfoIsExist() {
        boolean result = false;
        try {
            Cursor cursor = mDatabase.query("T_B_CustomerType", null, null,
                    null, null, null, null);
            if (cursor.moveToFirst()) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取区代码
     * @return
     */
    public List<StreetInfo> getQuCode(){
        List<StreetInfo> streetInfos=new ArrayList<>();
        String sql = "select distinct QuCode,QuName from T_B_ADDRESS ";
        try {
            Cursor query = mDatabase.rawQuery(sql, null);
            while (query.moveToNext()) {
                StreetInfo streetInfo=new StreetInfo();
                streetInfo.setQuCode(query.getString(0));
                streetInfo.setQuName(query.getString(1));
                streetInfos.add(streetInfo);
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return  streetInfos;
    }
    /**
     * 获取街道信息
     * @return
     */
    public List<StreetInfo> getStreetInfo(){
        List<StreetInfo> streetInfos=new ArrayList<>();
        String sql = "select  QuCode,QuName,JieCode,JieName,AeraCode from T_B_ADDRESS ";
        try {
            Cursor query = mDatabase.rawQuery(sql, null);
            while (query.moveToNext()) {
                StreetInfo streetInfo=new StreetInfo();
                streetInfo.setQuCode(query.getString(0));
                streetInfo.setQuName(query.getString(1));
                streetInfo.setJieCode(query.getString(2));
                streetInfo.setJieName(query.getString(3));
                streetInfo.setAreaCode(query.getString(4));
                streetInfos.add(streetInfo);
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return  streetInfos;
    }
    /**
     * 获取街道信息(根据区代码)
     * @return
     */
    public List<CustomerTypeInfo> getCustomerTypeInfo(){
        List<CustomerTypeInfo> customerTypeInfos=new ArrayList<>();
        String sql = "select  distinct CustomerType,CustomerTypeName from T_B_CustomerType ";
        try {
            Cursor query = mDatabase.rawQuery(sql, null);
            while (query.moveToNext()) {
                CustomerTypeInfo customerTypeInfo=new CustomerTypeInfo();
                customerTypeInfo.setCustomerType(query.getString(0));
                customerTypeInfo.setCustomerTypeName(query.getString(1));
                customerTypeInfos.add(customerTypeInfo);
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return  customerTypeInfos;
    }
    public ArrayList<String> getJieName(String QuCode){
        ArrayList<String> jieNames=new ArrayList<>();
        String sql = "select  distinct JieName from T_B_ADDRESS where QuCode=? ";
        try {
            Cursor query = mDatabase.rawQuery(sql,  new String[]{QuCode});
            while (query.moveToNext()) {
                jieNames.add(query.getString(0));
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return  jieNames;
    }
    public ArrayList<String> getJieCode(String QuCode){
        ArrayList<String> jieNames=new ArrayList<>();
        String sql = "select  distinct JieCode from T_B_ADDRESS where QuCode=? ";
        try {
            Cursor query = mDatabase.rawQuery(sql,  new String[]{QuCode});
            while (query.moveToNext()) {
                jieNames.add(query.getString(0));
            }
            query.close();
        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return  jieNames;
    }

    public String  getAreaCode(String QuCode ,String JieName){
        String ret="";
        String[] selectionArgs = {QuCode,JieName};
        try {
            String sql = "select distinct AeraCode from T_B_ADDRESS where QuCode=?  and JieName=? ";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                ret=cursor.getString(0);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    public String  getUsetrTypeName(String CustomerType ){
        String ret="";
        String[] selectionArgs = {CustomerType};
        try {
            String sql = "select distinct CustomerTypeName from T_B_CustomerType where CustomerType=?   ";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                ret=cursor.getString(0);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    public String  getQXName(String AeraCode ){
        String ret="";
        String[] selectionArgs = {AeraCode};
        try {
            String sql = "select distinct QuName,JieName from T_B_ADDRESS where AeraCode=?   ";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            if (cursor.moveToFirst()) {
                ret=cursor.getString(0)+cursor.getString(1);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
    public void deleteJD(){
        String sql="delete from T_B_ADDRESS ";
        mDatabase.execSQL(sql);
    }
    public void deleteUserType(){
        String sql="delete from T_B_CustomerType ";
        mDatabase.execSQL(sql);
    }
 }
