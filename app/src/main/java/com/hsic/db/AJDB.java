package com.hsic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.bean.Sale;
import com.hsic.bean.UserXJInfo;
import com.hsic.utils.TimeUtils;
import com.hsic.web.WebServiceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/14.
 */

public class AJDB {
    private SQLiteDatabase mDatabase = null;
    public AJDB(Context context){
        mDatabase=DataBaseHelper.getInstance(context).getReadableDatabase();
    }
    /**
     * 将安检信息插入到本地
     * @param EmployeeID
     * @param StationID
     * @param userXJInfo
     */
    public void InsertData(String EmployeeID, String StationID,UserXJInfo userXJInfo){
        try{
            StringBuffer sql_insert;
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_AJINFO(ID,EmployeeID,StationID,UserID,UserName,CustomerCardID,UserCardStatus," +
                    "UserType,UserTypeName,Deliveraddress,Telephone,Last_InspectionStatus,IsInspected,StopSupplyType1,StopSupplyType2," +
                    "StopSupplyType3,StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,StopSupplyType9,StopSupplyType10," +
                    "StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,"+
                    "UnInstallType1,UnInstallType2," +
                    "UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                    "UnInstallType12,UnInstallType13,UnInstallType14,InsertTime,TagID,TypeClass,Last_InspectionStatus)");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String userID=userXJInfo.getUserid();
            if(!isEixst(EmployeeID,StationID,userID)||isFinished(EmployeeID,StationID,userID)){
                SQLiteStatement statement=    mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, "");
                statement.bindString(2, EmployeeID);
                statement.bindString(3, StationID);
                statement.bindString(4, userXJInfo.getUserid());
                statement.bindString(5, userXJInfo.getUsername());
                statement.bindString(6, userXJInfo.getCustomerCardID());
                statement.bindString(7, userXJInfo.getUserCardStatus());
                statement.bindString(8, userXJInfo.getCustomerType());
                statement.bindString(9, userXJInfo.getCustomerTypeName());
                statement.bindString(10,userXJInfo.getDeliveraddress());
                statement.bindString(11, userXJInfo.getTelephone());
                statement.bindString(12, userXJInfo.getInspectionStatus());
                statement.bindString(13, "0");
                statement.bindString(14, userXJInfo.getStopSupplyType1());
                statement.bindString(15, userXJInfo.getStopSupplyType2());
                statement.bindString(16, userXJInfo.getStopSupplyType3());
                statement.bindString(17, userXJInfo.getStopSupplyType4());
                statement.bindString(18, userXJInfo.getStopSupplyType5());
                statement.bindString(19, userXJInfo.getStopSupplyType6());
                statement.bindString(20, userXJInfo.getStopSupplyType7());
                statement.bindString(21, userXJInfo.getStopSupplyType8());
                statement.bindString(22, userXJInfo.getStopSupplyType9());
                statement.bindString(23, userXJInfo.getStopSupplyType10());
                statement.bindString(24, userXJInfo.getStopSupplyType11());
                statement.bindString(25, userXJInfo.getStopSupplyType12());
                statement.bindString(26, userXJInfo.getStopSupplyType13());
                statement.bindString(27, userXJInfo.getStopSupplyType14());
                statement.bindString(28, userXJInfo.getStopSupplyType15());
                statement.bindString(29,userXJInfo.getUnInstallType1());
                statement.bindString(30, userXJInfo.getUnInstallType2());
                statement.bindString(31, userXJInfo.getUnInstallType3());
                statement.bindString(32, userXJInfo.getUnInstallType4());
                statement.bindString(33, userXJInfo.getUnInstallType5());
                statement.bindString(34,userXJInfo.getUnInstallType6());
                statement.bindString(35, userXJInfo.getUnInstallType7());
                statement.bindString(36, userXJInfo.getUnInstallType8());
                statement.bindString(37, userXJInfo.getUnInstallType9());
                statement.bindString(38,userXJInfo.getUnInstallType10());
                statement.bindString(39, userXJInfo.getUnInstallType11());
                statement.bindString(40,userXJInfo.getUnInstallType12());
                statement.bindString(41,userXJInfo.getUnInstallType13());
                statement.bindString(42,userXJInfo.getUnInstallType14());
                statement.bindString(43, TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                statement.bindString(44, userXJInfo.getUserCardID());
                statement.bindString(45, userXJInfo.getTypeClass());
                statement.bindString(46, userXJInfo.getInspectionStatus());
                statement.executeInsert();
            }
            //本地订单和后台对比
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            data=rectifyID(EmployeeID,StationID);
            for (int h = 0; h < data.size(); h++) {
                String tmp=data.get(h).get("UserID");
                if (!userID.equals(tmp)) {
                    upDateAJStatus(EmployeeID,StationID,tmp);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * 本地安检单是否存在
     * @param EmployeeID
     * @param StationID
     * @param ID
     * @return
     */
    public boolean isEixst(String EmployeeID, String StationID,String ID){
        boolean result=false;
        try {
            String whereClause = "EmployeeID=? and StationID=? and UserID=?";
            String[] whereArgs = new String[] { EmployeeID, StationID,ID };
            Cursor cursor = mDatabase.query("T_B_AJINFO", null, whereClause,
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
    public boolean isFinished(String EmployeeID, String StationID,String ID){
        boolean result=false;
        try {
            String whereClause = "EmployeeID=? and StationID=? and UserID=? and IsInspected='2'";
            String[] whereArgs = new String[] { EmployeeID, StationID,ID };
            Cursor cursor = mDatabase.query("T_B_AJINFO", null, whereClause,
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
     * 查询本地安检单
     * @return
     */
    public List<Map<String, String>> rectifyID(String EmployeeID, String StationID) {
        List<Map<String, String>> saleID = new ArrayList<Map<String, String>>();
        String[] selectionArgs = { EmployeeID, StationID};
        try {
            String sql = "select UserID from T_B_AJINFO where EmployeeID=? and StationID=? and IsInspected='0'";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("UserID", cursor.getString(0));
                saleID.add(map);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return saleID;
    }
    /**
     * 将安检数据插入到本地
     */
    public boolean updateAJInfo(String EmployeeID, String StationID,UserXJInfo userXJInfo){
        boolean ret=false;
        try{
            /**
             * 更新安检信息
             */
            String UserID=userXJInfo.getUserid();
            String whereClauseBys = "EmployeeID=? and StationID=? and UserID=? ";
            String[] whereArgsBys = { EmployeeID,StationID, UserID};
            ContentValues valuesBys = new ContentValues();
            valuesBys.put("RelationID",userXJInfo.getAttachID());
            valuesBys.put("IsInspected","1");
            valuesBys.put("InspectionStatus",userXJInfo.getInspectionStatus());
            valuesBys.put("InspectionDate",userXJInfo.getInspectionDate());
            valuesBys.put("InspectionMan",userXJInfo.getInspectionMan());
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
            long i = mDatabase.update("T_B_AJINFO",valuesBys,
                    whereClauseBys, whereArgsBys);
            if(i>0){
                ret=true;
            }
            /**
             * 插入关联表信息
             */
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }
    /**
     * 上传安检信息
     * @param EmployeeID
     * @param StationID
     * @param CustomID
     */
    public HsicMessage upLoadAJInfo(Context context,String DeviceId,String EmployeeID, String StationID,String CustomID){
        HsicMessage hsicMess = new HsicMessage();
        WebServiceHelper web = new WebServiceHelper(context);
        try{
            hsicMess.setRespCode(10);
            UserXJInfo userXJInfo = new UserXJInfo();
            String[] selections = { EmployeeID, StationID,CustomID};
            String sql="select TypeClass,RelationID,InspectionStatus,InspectionDate,InspectionMan,StopSupplyType1,StopSupplyType2,StopSupplyType3," +
                    "StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,StopSupplyType9,StopSupplyType10,StopSupplyType11," +
                    "StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,UnInstallType1,UnInstallType2,UnInstallType3," +
                    "UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                    "UnInstallType12,UnInstallType13,UnInstallType14 from T_B_AJINFO where EmployeeID=? and StationID=? and UserID=?";
            Cursor query = mDatabase.rawQuery(sql, selections);
            if(query.moveToFirst()){
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
                userXJInfo.setUserid(CustomID);

            }
            query.close();
            String data = "";// 上传传输的数据
            data = JSONUtils.toJsonWithGson(userXJInfo);
            hsicMess.setRespMsg(data);
            String requestData = JSONUtils.toJsonWithGson(hsicMess);
            String[] selection = { "DeviceID", "RequestData" };
            String[] selectionArgs = { DeviceId, requestData };
            String methodName = "";
            methodName = "UpUserInspectionInfo_New";// 方法名称
            hsicMess = web.uploadInfo(selection, methodName, selectionArgs);
            int i = hsicMess.getRespCode();// 方法执行结果
            if(i ==0){
                upDateAJStatus(EmployeeID,StationID,CustomID);
            }

        }catch(Exception ex){
            ex.toString();
            ex.printStackTrace();
        }
        return hsicMess;
    }
    /**
     * 更新本地订单状态
     */
    public int upDateAJStatus(String EmployeeID, String StationID,String CustomID){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and StationID=? and UserID=?";
        String[] whereArgs={EmployeeID,StationID,CustomID};
        values.put("IsInspected","2");
        try{
            ret=mDatabase.update("T_B_AJINFO",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }
    /**
     * 将照片基本信息插入到数据表中  安检
     * @param
     * @param
     * @param
     * @param RelationID
     */
    public void InsertAJAssociation(String EmployeeID, String UserID, String ImageName, String RelationID, String FileName){
        try{
            ContentValues cValue = new ContentValues();
            cValue.put("EmployeeID", EmployeeID);
            cValue.put("UserID", UserID);
            cValue.put("ImageName", ImageName);
            cValue.put("RelationID", RelationID);
            cValue.put("FileName", FileName);
            cValue.put("IsUpload", "0");
            cValue.put("InsertTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
            long ret = mDatabase.insert("T_B_AJ_Association", null, cValue);
        }catch(Exception ex){
            ex.toString();

        }
    }
    /**
     * 上传附件关联信息
     * @param FileRelationInfo_LIST
     * @param deviceid
     * @param context
     * @return
     */
    public HsicMessage UpLoadA(List<FileRelationInfo> FileRelationInfo_LIST, String deviceid, Context context) {
        HsicMessage hsicMessage = new HsicMessage();
        hsicMessage.setRespCode(10);
        try {
            List<FileRelationInfo> upLoadSuc=new ArrayList<FileRelationInfo>();//暂存已经上传成功的附件信息
            int size=0;
            size=FileRelationInfo_LIST.size();
            if(size>0){
                for(int i=0;i<size;i++){
                    List<FileRelationInfo> list = new ArrayList<FileRelationInfo>();//上传附件信息
                    FileRelationInfo fri = FileRelationInfo_LIST.get(i);
                    fri.setFilePath(fri.getFilePath());
                    fri.setRelationID(fri.getRelationID());
                    String ImageName=fri.getImageName();
                    String TruckNoId=fri.getTruckNoId();
                    String SaleID=fri.getSaleID();
                    list.add(fri);
                    String respMsg = JSONUtils.toJsonWithGson(list);
                    hsicMessage.setRespMsg(respMsg);
                    WebServiceHelper web = new WebServiceHelper(context);
                    String requestData = JSONUtils.toJsonWithGson(hsicMessage);// web接口参数
                    String[] webRe = { "DeviceID", "RequestData" };
                    String webMethod = "UpFileReletionInfo";
                    String[] value = { deviceid, requestData };
                    hsicMessage = web.uploadInfo(webRe, webMethod, value);
                    if(hsicMessage.getRespCode()==0){
                        //关联表上传成功更新本地上传字段
                        FileRelationInfo f=new FileRelationInfo();
                        f.setTruckNoId(TruckNoId);
                        f.setSaleID(SaleID);
                        f.setImageName(ImageName);
                        upLoadSuc.add(f);

                    }
                }
            }
            //批量更新上传成功以后的关联表本地字段
            int size_up=upLoadSuc.size();
            for(int m=0;m<size_up;m++){
                String ImageName=upLoadSuc.get(m).getImageName();
                String TruckNoId=upLoadSuc.get(m).getTruckNoId();
                String SaleID=upLoadSuc.get(m).getSaleID();
                UpDateAJAssociation(TruckNoId, SaleID, ImageName);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            hsicMessage.setRespCode(5);
            hsicMessage.setRespMsg("调用借口异常");
        }
        return hsicMessage;
    }
    public void DeleteAJAssociation(String Path,String EmployeeID,String UserID){
        try{
            String whereClause_t="ImageName=? and EmployeeID=? and UserID=?";
            String[] whereArgs_t={Path,EmployeeID,UserID};
            long ret=mDatabase.delete("T_B_AJ_Association", whereClause_t, whereArgs_t);
        }catch(Exception ex){
            Log.e("删除照片异常",ex.toString());
        }
    }
    /**
     * 查询该车次该订单下所有的照片关联信息   安检
     *
     */
    public List<FileRelationInfo> GetAJFileRelationInfo(String EmployeeID, String UserID){
        List<FileRelationInfo> FileRelationInfo_LIST=new ArrayList<FileRelationInfo>();
        try{
            String[] selectionArgs = { EmployeeID, UserID};
            String sql = "select RelationID,ImageName,FileName  from  T_B_AJ_Association where EmployeeID=? and UserID=? and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                FileRelationInfo f=new FileRelationInfo();
                f.setRelationID(query.getString(0));
                String FilePath=query.getString(2)+"/"+query.getString(1);
                f.setFilePath(FilePath);
                f.setImageName(query.getString(1));
                f.setTruckNoId(EmployeeID);
                f.setSaleID(UserID);
                FileRelationInfo_LIST.add(f);
            }
            query.close();
        }catch(Exception ex){

        }
        return FileRelationInfo_LIST;
    }
    /**
     * 查询该车次该订单下所有的照片关联信息   安检
     *
     */
    public List<FileRelationInfo> GetHistoryAJFileRelationInfo(String EmployeeID){
        List<FileRelationInfo> FileRelationInfo_LIST=new ArrayList<FileRelationInfo>();
        try{
            String[] selectionArgs = { EmployeeID};
            String sql = "select RelationID,ImageName,FileName,UserID  from  T_B_AJ_Association where EmployeeID=? and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                FileRelationInfo f=new FileRelationInfo();
                f.setRelationID(query.getString(0));
                String FilePath=query.getString(2)+"/"+query.getString(1);
                f.setFilePath(FilePath);
                f.setImageName(query.getString(1));
                f.setTruckNoId(EmployeeID);
                f.setSaleID(query.getString(3));
                FileRelationInfo_LIST.add(f);
            }
            query.close();
        }catch(Exception ex){

        }
        return FileRelationInfo_LIST;
    }
    public int UpDateAJAssociation(String EmployeeID,String UserID,String Path){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and UserID=? and ImageName=?";
        String[] whereArgs={EmployeeID,UserID,Path};
        values.put("IsUpLoad","1");
        try{
            ret=mDatabase.update("T_B_AJ_Association",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }
    public List<Sale> UpHistoryAJ(String EmployeeID, String StationID){
        List<Sale> sales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select UserID from T_B_AJINFO where EmployeeID=? and StationID=? and IsInspected='1'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                Sale sale=new Sale();
                sale.setCustomerID(query.getString(0));
                sales.add(sale);
            }
            query.close();
        }catch(Exception  ex){

        }
        return sales;
    }
    /**
     * 上传安检历史信息
     * @param context
     * @param DeviceId
     * @param EmployeeID
     * @param StationID
     */
    public void UpHistoryAJ(Context context,String  DeviceId,String EmployeeID, String StationID){
        List<Sale> sales=new ArrayList<>();
        sales=UpHistoryAJ(EmployeeID,StationID);
        int size=sales.size();
        if(size>0){
            for(int i=0;i<size;i++){
                String CustomID;
                CustomID=sales.get(i).getCustomerID();
                upLoadAJInfo(context,DeviceId,EmployeeID,StationID,CustomID);
            }
        }
        /**
         *上传安检关联表历史信息
         */
        List<FileRelationInfo> fileRelationInfos=new ArrayList<>();
        fileRelationInfos=GetHistoryAJFileRelationInfo(EmployeeID);
        size=fileRelationInfos.size();
        if(size>0){
            UpLoadA(fileRelationInfos,DeviceId,context);
        }
    }
    /***
     * 根据用户编号查询用户整改相关信息
     * @param EmployeeID
     * @param StationID
     * @param UserID
     * @return
     */
    public UserXJInfo GetRectifyInfoByUserID(String EmployeeID,String StationID,String UserID){
        UserXJInfo userXJInfo=new UserXJInfo();
        try{
            String[] selectionArgs = { EmployeeID,StationID,UserID};
            String sql="select a.ID,a.UserID,a.UserName,a.Telephone,a.Deliveraddress,a.UserTypeName,a.Last_InspectionStatus,a.InspectionStatus," +
                    "a.StopSupplyType1,a.StopSupplyType2,a.StopSupplyType3,a.StopSupplyType4,a.StopSupplyType5,a.StopSupplyType6," +
                    "a.StopSupplyType7,a.StopSupplyType8,a.UnInstallType1,a.UnInstallType2,a.UnInstallType3,a.UnInstallType4,a.UnInstallType5," +
                    "a.UnInstallType6,a.UnInstallType7,a.UnInstallType8,a.UnInstallType9,a.UnInstallType10,a.UnInstallType11," +
                    "a.UnInstallType12,TypeClass from T_B_AJINFO a" +
                    " where a.EmployeeID=? and a.StationID=? and a.UserID=? and a.IsInspected='0' ";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            if(query.moveToFirst()){
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfo.setInspectionStatus(query.getString(7));
                userXJInfo.setStopSupplyType1(query.getString(8));
                userXJInfo.setStopSupplyType2(query.getString(9));
                userXJInfo.setStopSupplyType3(query.getString(10));
                userXJInfo.setStopSupplyType4(query.getString(11));
                userXJInfo.setStopSupplyType5(query.getString(12));
                userXJInfo.setStopSupplyType6(query.getString(13));
                userXJInfo.setStopSupplyType7(query.getString(14));
                userXJInfo.setStopSupplyType8(query.getString(15));
                userXJInfo.setUnInstallType1(query.getString(16));
                userXJInfo.setUnInstallType2(query.getString(17));
                userXJInfo.setUnInstallType3(query.getString(18));
                userXJInfo.setUnInstallType4(query.getString(19));
                userXJInfo.setUnInstallType5(query.getString(20));
                userXJInfo.setUnInstallType6(query.getString(21));
                userXJInfo.setUnInstallType7(query.getString(22));
                userXJInfo.setUnInstallType8(query.getString(23));
                userXJInfo.setUnInstallType9(query.getString(24));
                userXJInfo.setUnInstallType10(query.getString(25));
                userXJInfo.setUnInstallType11(query.getString(26));
                userXJInfo.setUnInstallType12(query.getString(27));
                userXJInfo.setTypeClass(query.getString(28));
            }
            query.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return userXJInfo;
    }
    /**
     *
     * @param EmployeeID
     * @param StationID
     * @param customCardID
     */
    public List<UserXJInfo> getInfoByCustom(String EmployeeID,String StationID,String customCardID){
        List<UserXJInfo> userXJInfos=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID,customCardID };
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_AJINFO where EmployeeID=? and StationID=? and CustomerCardID=?";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()){
                UserXJInfo userXJInfo=new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        }catch(Exception ex){

        }
        return userXJInfos;
    }
    public List<UserXJInfo> getInfoByAddress(String EmployeeID,String StationID,String Address ){
        List<UserXJInfo> userXJInfos=new ArrayList<>();
//        try{
        String[] selectionArgs = new String[] { EmployeeID, StationID };
        String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_AJINFO where EmployeeID=? and StationID=? and " +
                "Deliveraddress like  '%"+Address+"%' ";
        Cursor query = mDatabase.rawQuery(sql, selectionArgs);
        while(query.moveToNext()){
            UserXJInfo userXJInfo=new UserXJInfo();
            userXJInfo.setSaleid(query.getString(0));
            userXJInfo.setUserid(query.getString(1));
            userXJInfo.setUsername(query.getString(2));
            userXJInfo.setTelephone(query.getString(3));
            userXJInfo.setDeliveraddress(query.getString(4));
            userXJInfo.setCustomerTypeName(query.getString(5));
            userXJInfos.add(userXJInfo);
        }
        query.close();
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }
        return userXJInfos;
    }
    public List<UserXJInfo> getInfoByTelephone(String EmployeeID,String StationID,String Telephone){
        List<UserXJInfo> userXJInfos=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_AJINFO where EmployeeID=? and StationID=? and" +
                    " Telephone like  '%"+Telephone+"%'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                UserXJInfo userXJInfo=new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        }catch(Exception ex){

        }
        return userXJInfos;
    }
    public List<UserXJInfo> getInfoByUserName(String EmployeeID,String StationID,String userName){
        List<UserXJInfo> userXJInfos=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_AJINFO where EmployeeID=? and StationID=? and" +
                    " UserName  like  '%"+userName+"%'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()){
                UserXJInfo userXJInfo=new UserXJInfo();
                userXJInfo.setSaleid(query.getString(0));
                userXJInfo.setUserid(query.getString(1));
                userXJInfo.setUsername(query.getString(2));
                userXJInfo.setTelephone(query.getString(3));
                userXJInfo.setDeliveraddress(query.getString(4));
                userXJInfo.setCustomerTypeName(query.getString(5));
                userXJInfos.add(userXJInfo);
            }
            query.close();
        }catch(Exception ex){

        }
        return userXJInfos;
    }
    public boolean isUpLoadAJ(String EmployeeID,String StationID,String UserID){
        boolean result=false;
        try {
            String whereClause = "EmployeeID=? and StationID=? and UserID=? and IsInspected='2'";
            String[] whereArgs = new String[] { EmployeeID, StationID,UserID };
            Cursor cursor = mDatabase.query("T_B_AJINFO", null, whereClause,
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
     * 获取订单上次安检信息
     * @return
     */
    public Map<String, String> getXJ(String EmployeeID, String StationID, String UserID) {
        Map<String, String> map = new HashMap<String, String>();
        String[] selection = { EmployeeID, StationID, UserID };
        String sql = "select Last_InspectionStatus,StopSupplyType1,StopSupplyType2,StopSupplyType3,StopSupplyType4,"
                + "StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,StopSupplyType9,StopSupplyType10," +
                "StopSupplyType11,StopSupplyType12,StopSupplyType13,StopSupplyType14,StopSupplyType15,UnInstallType1,"
                + "UnInstallType2,UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6,"
                + "UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11,UnInstallType12,UnInstallType13,UnInstallType14"
                + " from T_B_AJINFO where EmployeeID=? and StationID=? and UserID=? and IsInspected='0'";
        try {
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
        } catch (Exception ex) {
            return null;
        }
        return map;
    }
}
