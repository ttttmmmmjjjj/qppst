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
import com.hsic.bean.UserRectifyInfo;
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

public class RectifyDB {
    private SQLiteDatabase mDatabase = null;
    public RectifyDB(Context context){
        mDatabase=DataBaseHelper.getInstance(context).getReadableDatabase();
    }

    /**
     * 将整改信息插入到本地
     * @param EmployeeID
     * @param StationID
     * @param userRectifyInfoList
     */
    public void InsertData(String EmployeeID, String StationID,List<UserRectifyInfo> userRectifyInfoList){
//        try{
            StringBuffer sql_insert;
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_UserRectifyInfo(ID,EmployeeID,StationID,UserID,UserName,CustomerCardID,UserCardStatus," +
                    "UserType,UserTypeName,Deliveraddress,Telephone,Last_InspectionStatus,IsInspected,StopSupplyType1,StopSupplyType2," +
                    "StopSupplyType3,StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,UnInstallType1,UnInstallType2," +
                    "UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10," +
                    "UnInstallType11,UnInstallType12,InsertTime,TagID,TypeClass,StopSupplyType9,StopSupplyType10,StopSupplyType11,StopSupplyType12," +
                    "StopSupplyType13,StopSupplyType14,StopSupplyType15,UnInstallType13,UnInstallType14)");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            List<UserRectifyInfo> userRectifyInfos=new ArrayList<>();
            int size=userRectifyInfoList.size();
            StringBuffer ID_Web=new StringBuffer();
            for(int i=0;i<size;i++){
                String ID=userRectifyInfoList.get(i).getId();
                ID_Web.append(ID+",");
                if(!isEixst(EmployeeID,StationID,ID)){
                    userRectifyInfos.add(userRectifyInfoList.get(i));
                }
            }
            /**
             * 开始插入数据
             */
            for(UserRectifyInfo userRectifyInfo:userRectifyInfos){
                SQLiteStatement statement=    mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, userRectifyInfo.getId());
                statement.bindString(2, EmployeeID);
                statement.bindString(3, StationID);
                statement.bindString(4, userRectifyInfo.getUserid());
                statement.bindString(5, userRectifyInfo.getUsername());
                statement.bindString(6, userRectifyInfo.getCustomerCardID());
                statement.bindString(7, userRectifyInfo.getUserCardStatus());
                statement.bindString(8, userRectifyInfo.getCustom_type());
                statement.bindString(9, userRectifyInfo.getExtendValue1());
                statement.bindString(10,userRectifyInfo.getDeliveraddress());
                statement.bindString(11, userRectifyInfo.getTelephone());
                statement.bindString(12, userRectifyInfo.getInspectionStatus());
                statement.bindString(13, "0");
                statement.bindString(14, userRectifyInfo.getStopSupplyType1());
                statement.bindString(15, userRectifyInfo.getStopSupplyType2());
                statement.bindString(16, userRectifyInfo.getStopSupplyType3());
                statement.bindString(17, userRectifyInfo.getStopSupplyType4());
                statement.bindString(18, userRectifyInfo.getStopSupplyType5());
                statement.bindString(19, userRectifyInfo.getStopSupplyType6());
                statement.bindString(20, userRectifyInfo.getStopSupplyType7());
                statement.bindString(21, userRectifyInfo.getStopSupplyType8());
                statement.bindString(22,userRectifyInfo.getUnInstallType1());
                statement.bindString(23, userRectifyInfo.getUnInstallType2());
                statement.bindString(24, userRectifyInfo.getUnInstallType3());
                statement.bindString(25, userRectifyInfo.getUnInstallType4());
                statement.bindString(26, userRectifyInfo.getUnInstallType5());
                statement.bindString(27,userRectifyInfo.getUnInstallType6());
                statement.bindString(28, userRectifyInfo.getUnInstallType7());
                statement.bindString(29, userRectifyInfo.getUnInstallType8());
                statement.bindString(30, userRectifyInfo.getUnInstallType9());
                statement.bindString(31,userRectifyInfo.getUnInstallType10());
                statement.bindString(32, userRectifyInfo.getUnInstallType11());
                statement.bindString(33,userRectifyInfo.getUnInstallType12());
                statement.bindString(34, TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
                statement.bindString(35, userRectifyInfo.getUserCardID());
                statement.bindString(36, userRectifyInfo.getTypeClass());
                statement.bindString(37, userRectifyInfo.getStopSupplyType9());
                statement.bindString(38, userRectifyInfo.getStopSupplyType10());
                statement.bindString(39, userRectifyInfo.getStopSupplyType11());
                statement.bindString(40, userRectifyInfo.getStopSupplyType12());
                statement.bindString(41, userRectifyInfo.getStopSupplyType13());
                statement.bindString(42, userRectifyInfo.getStopSupplyType14());
                statement.bindString(43, userRectifyInfo.getStopSupplyType15());
                statement.bindString(44, userRectifyInfo.getUnInstallType13());
                statement.bindString(45, userRectifyInfo.getUnInstallType14());
                statement.executeInsert();
            }
            /**
             *
             */
            String ID=ID_Web.toString();
            int l=ID.length();
            ID=ID.substring(0,l);
            //本地订单和后台对比
            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
            data=rectifyID(EmployeeID,StationID);
            for (int h = 0; h < data.size(); h++) {
                String tmp=data.get(h).get("ID");
                if (!ID.contains(tmp)) {
                    upDateRectifyStatus(EmployeeID,StationID,tmp);
                }
            }
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }

    }

    /**
     * 本地整改单是否存在
     * @param EmployeeID
     * @param StationID
     * @param ID
     * @return
     */
    public boolean isEixst(String EmployeeID, String StationID,String ID){
        boolean result=false;
        try {
            String whereClause = "EmployeeID=? and StationID=? and ID=?";
            String[] whereArgs = new String[] { EmployeeID, StationID,ID };
            Cursor cursor = mDatabase.query("T_B_UserRectifyInfo", null, whereClause,
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
     * 查询本地整改单
     * @return
     */
    public List<Map<String, String>> rectifyID(String EmployeeID, String StationID) {
        List<Map<String, String>> saleID = new ArrayList<Map<String, String>>();
        String[] selectionArgs = { EmployeeID, StationID};
        try {
            String sql = "select ID from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and IsInspected='0'";
            Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ID", cursor.getString(0));
                saleID.add(map);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return saleID;
    }
    /**
     * 更新整改交易数据
     */
    public boolean updateRectifyInfo(String EmployeeID, String StationID,UserRectifyInfo userRectifyInfo){
        boolean ret=false;
        try{
            /**
             * 更新安检信息
             */
            String ID=userRectifyInfo.getId();
            String whereClauseBys = "EmployeeID=? and StationID=? and ID=? ";
            String[] whereArgsBys = { EmployeeID,StationID, ID};
            ContentValues valuesBys = new ContentValues();
            valuesBys.put("ID",userRectifyInfo.getId());
            valuesBys.put("RelationID",userRectifyInfo.getRelationID());
            valuesBys.put("IsInspected","1");
            valuesBys.put("InspectionStatus",userRectifyInfo.getInspectionStatus());
            valuesBys.put("InspectionDate",userRectifyInfo.getInspectionDate());
//            valuesBys.put("InspectionMan",userRectifyInfo.getRectifyMan());
            valuesBys.put("StopSupplyType1",userRectifyInfo.getStopSupplyType1());
            valuesBys.put("StopSupplyType2",userRectifyInfo.getStopSupplyType2());
            valuesBys.put("StopSupplyType3",userRectifyInfo.getStopSupplyType3());
            valuesBys.put("StopSupplyType4",userRectifyInfo.getStopSupplyType4());
            valuesBys.put("StopSupplyType5",userRectifyInfo.getStopSupplyType5());
            valuesBys.put("StopSupplyType6",userRectifyInfo.getStopSupplyType6());
            valuesBys.put("StopSupplyType7",userRectifyInfo.getStopSupplyType7());
            valuesBys.put("StopSupplyType8",userRectifyInfo.getStopSupplyType8());
            valuesBys.put("StopSupplyType9",userRectifyInfo.getStopSupplyType9());
            valuesBys.put("StopSupplyType10",userRectifyInfo.getStopSupplyType10());
            valuesBys.put("StopSupplyType11",userRectifyInfo.getStopSupplyType11());
            valuesBys.put("StopSupplyType12",userRectifyInfo.getStopSupplyType12());
            valuesBys.put("StopSupplyType13",userRectifyInfo.getStopSupplyType13());
            valuesBys.put("StopSupplyType14",userRectifyInfo.getStopSupplyType14());
            valuesBys.put("StopSupplyType15",userRectifyInfo.getStopSupplyType15());
            valuesBys.put("UnInstallType1",userRectifyInfo.getUnInstallType1());
            valuesBys.put("UnInstallType2",userRectifyInfo.getUnInstallType2());
            valuesBys.put("UnInstallType3",userRectifyInfo.getUnInstallType3());
            valuesBys.put("UnInstallType4",userRectifyInfo.getUnInstallType4());
            valuesBys.put("UnInstallType5",userRectifyInfo.getUnInstallType5());
            valuesBys.put("UnInstallType6",userRectifyInfo.getUnInstallType6());
            valuesBys.put("UnInstallType7",userRectifyInfo.getUnInstallType7());
            valuesBys.put("UnInstallType8",userRectifyInfo.getUnInstallType8());
            valuesBys.put("UnInstallType9",userRectifyInfo.getUnInstallType9());
            valuesBys.put("UnInstallType10",userRectifyInfo.getUnInstallType10());
            valuesBys.put("UnInstallType11",userRectifyInfo.getUnInstallType11());
            valuesBys.put("UnInstallType12",userRectifyInfo.getUnInstallType12());
            valuesBys.put("UnInstallType13",userRectifyInfo.getUnInstallType13());
            valuesBys.put("UnInstallType14",userRectifyInfo.getUnInstallType14());
            valuesBys.put("RectifyStatus",userRectifyInfo.getRectifyStatus());
            valuesBys.put("RectifyMan",userRectifyInfo.getRectifyMan());
            valuesBys.put("RectifyDate",userRectifyInfo.getInspectionDate());
            valuesBys.put("OperationResult",userRectifyInfo.getOperationResult());
            int i = mDatabase.update("T_B_UserRectifyInfo",valuesBys,
                    whereClauseBys, whereArgsBys);
            if(i>0){
                ret=true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }
    /**
     * 上传整改信息
     * @param EmployeeID
     * @param StationID
     * @param CustomID
     */
    public HsicMessage upLoadRectifyInfo(Context context, String DeviceId, String EmployeeID, String StationID, String CustomID){
        HsicMessage hsicMess = new HsicMessage();
        WebServiceHelper web = new WebServiceHelper(context);
        try{
            hsicMess.setRespCode(10);
            UserXJInfo userXJInfo = new UserXJInfo();
            String[] selections = { EmployeeID, StationID,CustomID};
            String sql="select RelationID,InspectionStatus,InspectionDate,RectifyMan,StopSupplyType1,StopSupplyType2,StopSupplyType3," +
                    "StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,UnInstallType1,UnInstallType2,UnInstallType3," +
                    "UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                    "UnInstallType12,Last_InspectionStatus,ID,StopSupplyType9,StopSupplyType10,StopSupplyType11,StopSupplyType12,StopSupplyType13," +
                    "StopSupplyType14,StopSupplyType15," +
                    "UnInstallType13,UnInstallType14,TypeClass from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and UserID=?";
            Cursor query = mDatabase.rawQuery(sql, selections);
            String ID="";
            if(query.moveToFirst()){
                userXJInfo.setAttachID(query.getString(0));
                userXJInfo.setInspectionStatus(query.getString(1));
                userXJInfo.setInspectionDate(query.getString(2));
                userXJInfo.setInspectionMan(EmployeeID);
                userXJInfo.setStopSupplyType1(query.getString(4));
                userXJInfo.setStopSupplyType2(query.getString(5));
                userXJInfo.setStopSupplyType3(query.getString(6));
                userXJInfo.setStopSupplyType4(query.getString(7));
                userXJInfo.setStopSupplyType5(query.getString(8));
                userXJInfo.setStopSupplyType6(query.getString(9));
                userXJInfo.setStopSupplyType7(query.getString(10));
                userXJInfo.setStopSupplyType8(query.getString(11));
                userXJInfo.setUnInstallType1(query.getString(12));
                userXJInfo.setUnInstallType2(query.getString(13));
                userXJInfo.setUnInstallType3(query.getString(14));
                userXJInfo.setUnInstallType4(query.getString(15));
                userXJInfo.setUnInstallType5(query.getString(16));
                userXJInfo.setUnInstallType6(query.getString(17));
                userXJInfo.setUnInstallType7(query.getString(18));
                userXJInfo.setUnInstallType8(query.getString(19));
                userXJInfo.setUnInstallType9(query.getString(20));
                userXJInfo.setUnInstallType10(query.getString(21));
                userXJInfo.setUnInstallType11(query.getString(22));
                userXJInfo.setUnInstallType12(query.getString(23));
                userXJInfo.setLast_InspectionStatus(query.getString(24));
                userXJInfo.setStationcode(StationID);
                userXJInfo.setUserid(CustomID);
                userXJInfo.setIsBackup("0");
                userXJInfo.setRefuseInspection("0");
                userXJInfo.setErrorNature("0");
                userXJInfo.setErrorAddress("0");
                ID=query.getString(25);
                userXJInfo.setStopSupplyType9(query.getString(26));
                userXJInfo.setStopSupplyType10(query.getString(27));
                userXJInfo.setStopSupplyType11(query.getString(28));
                userXJInfo.setStopSupplyType12(query.getString(29));
                userXJInfo.setStopSupplyType13(query.getString(30));
                userXJInfo.setStopSupplyType14(query.getString(31));
                userXJInfo.setStopSupplyType15(query.getString(32));
                userXJInfo.setUnInstallType13(query.getString(33));
                userXJInfo.setUnInstallType14(query.getString(34));
                userXJInfo.setTypeClass(query.getString(35));
            }
            query.close();
            String data = "";// 上传传输的数据
            data = JSONUtils.toJsonWithGson(userXJInfo);
            hsicMess.setRespMsg(data);
            String requestData = JSONUtils.toJsonWithGson(hsicMess);
            String[] selection = { "DeviceID", "RequestData" };
            String[] selectionArgs = { DeviceId, requestData };
            String methodName = "";
            methodName = "UpUserRectifyInfo";// 方法名称
            hsicMess = web.uploadInfo(selection, methodName, selectionArgs);
            int i = hsicMess.getRespCode();// 方法执行结果
            if(i ==0){
                upDateRectifyStatus(EmployeeID,StationID,ID);
            }

        }catch(Exception ex){
            ex.toString();
            ex.printStackTrace();
        }
        return hsicMess;
    }
    /**
     * 更新本地整改单状态
     */
    public int upDateRectifyStatus(String EmployeeID, String StationID,String ID){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and StationID=? and ID=?";
        String[] whereArgs={EmployeeID,StationID,ID};
        values.put("IsInspected","2");
        try{
            ret=mDatabase.update("T_B_UserRectifyInfo",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }
    /**
     * 将照片基本信息插入到数据表中  整改
     * @param
     * @param
     * @param
     * @param RelationID
     */
    public void InsertRectifyAssociation(String EmployeeID, String UserID, String ImageName, String RelationID, String FileName){
        try{
            ContentValues cValue = new ContentValues();
            cValue.put("EmployeeID", EmployeeID);
            cValue.put("UserID", UserID);
            cValue.put("ImageName", ImageName);
            cValue.put("RelationID", RelationID);
            cValue.put("FileName", FileName);
            cValue.put("IsUpload", "0");
            cValue.put("InsertTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));
            long ret = mDatabase.insert("T_B_ZG_Association", null, cValue);
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
                UpDateRectifyAssociation(TruckNoId, SaleID, ImageName);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            hsicMessage.setRespCode(5);
            hsicMessage.setRespMsg("调用借口异常");
        }
        return hsicMessage;
    }
    public void DeleteRectifyAssociation(String Path,String EmployeeID,String UserID){
        try{
            String whereClause_t="ImageName=? and EmployeeID=? and UserID=?";
            String[] whereArgs_t={Path,EmployeeID,UserID};
            long ret=mDatabase.delete("T_B_ZG_Association", whereClause_t, whereArgs_t);
        }catch(Exception ex){
            Log.e("删除照片异常",ex.toString());
        }
    }
    /**
     * 查询该车次该订单下所有的照片关联信息   整改
     *
     */
    public List<FileRelationInfo> GetRectifyFileRelationInfo(String EmployeeID, String UserID){
        List<FileRelationInfo> FileRelationInfo_LIST=new ArrayList<FileRelationInfo>();
        try{
            String[] selectionArgs = { EmployeeID, UserID};
            String sql = "select RelationID,ImageName,FileName  from  T_B_ZG_Association where EmployeeID=? and UserID=? and IsUpload='0'";
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
     * 查询该车次该订单下所有的照片关联信息   整改
     *
     */
    public List<FileRelationInfo> GetHistoryRectifyFileRelationInfo(String EmployeeID){
        List<FileRelationInfo> FileRelationInfo_LIST=new ArrayList<FileRelationInfo>();
        try{
            String[] selectionArgs = { EmployeeID};
            String sql = "select RelationID,ImageName,FileName,UserID  from  T_B_ZG_Association where EmployeeID=? and IsUpload='0'";
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
    public int UpDateRectifyAssociation(String EmployeeID,String UserID,String Path){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and UserID=? and ImageName=?";
        String[] whereArgs={EmployeeID,UserID,Path};
        values.put("IsUpLoad","1");
        try{
            ret=mDatabase.update("T_B_ZG_Association",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }

    /**
     * 获取已经整改未上传的整改单[IState:4]
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> UpHistoryRectify(String EmployeeID, String StationID){
        List<Sale> sales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select UserID from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and IsInspected='1'";
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
     * 上传整改历史信息
     * @param context
     * @param DeviceId
     * @param EmployeeID
     * @param StationID
     */
    public void UpHistoryRectify(Context context,String  DeviceId,String EmployeeID, String StationID){
        List<Sale> sales=new ArrayList<>();
        sales=UpHistoryRectify(EmployeeID,StationID);
        int size=sales.size();
        if(size>0){
            for(int i=0;i<size;i++){
                String SaleID;String CustomID;
                SaleID= sales.get(i).getSaleID();
                CustomID=sales.get(i).getCustomerID();
                upLoadRectifyInfo(context,DeviceId,EmployeeID,StationID,CustomID);
            }
        }
        /**
         *上传安检关联表历史信息
         */
        List<FileRelationInfo> fileRelationInfos=new ArrayList<>();
        fileRelationInfos=GetHistoryRectifyFileRelationInfo(EmployeeID);
        size=fileRelationInfos.size();
        if(size>0){
            UpLoadA(fileRelationInfos,DeviceId,context);
        }
    }
    /**
     *本地所有整改单
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> AllSale(String EmployeeID,String StationID){
        List<Sale> sales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID from T_B_UserRectifyInfo where EmployeeID=? and StationID=?";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                Sale sale=new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        }catch(Exception  ex){

        }
        return sales;
    }

    /**
     * 已上传整改
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> uploadList(String EmployeeID,String StationID){
        List<Sale> sales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and IsInspected='2'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                Sale sale=new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        }catch(Exception  ex){

        }
        return sales;
    }
    /**
     * 已完成整改[完成+上传]
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Sale> finishList(String EmployeeID,String StationID){
        List<Sale> sales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and (IsInspected='1'or IsInspected='2')";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                Sale sale=new Sale();
                sale.setSaleID(query.getString(0));
                sale.setCustomerID(query.getString(1));
                sales.add(sale);
            }
            query.close();
        }catch(Exception  ex){

        }
        return sales;
    }
    /**
     * 获取整改信息[未完成]
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<Map<String, String>>GetRectifyInfo(String EmployeeID,String StationID){
        long start=System.currentTimeMillis();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] selectionArgs = { EmployeeID,StationID};
        String sql="select a.ID,a.UserID,a.UserName,a.Telephone,a.Deliveraddress,a.CustomerCardID,TypeClass from T_B_UserRectifyInfo a" +
                " where a.EmployeeID=? and a.StationID=? and a.IsInspected='0' ";

        try{
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while (query.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("ID",query.getString(0));
                map.put("UserID",query.getString(1));
                map.put("UserName",query.getString(2));
                map.put("Telephone",query.getString(3));
                map.put("Deliveraddress",query.getString(4));
                map.put("CustomerCardID",query.getString(5));
                map.put("TypeClass",query.getString(6));
                list.add(map);
            }
            query.close();

        }catch(Exception ex){
            ex.printStackTrace();
        }
        long end = System.currentTimeMillis();
        return list;
    }

    /***
     * 根据用户编号查询用户整改相关信息
     * @param EmployeeID
     * @param StationID
     * @param UserID
     * @return
     */
    public UserRectifyInfo GetRectifyInfoByUserID(String EmployeeID,String StationID,String UserID){
        UserRectifyInfo userRectifyInfo=new UserRectifyInfo();
        try{
            String[] selectionArgs = { EmployeeID,StationID,UserID};
            String sql="select a.ID,a.UserID,a.UserName,a.Telephone,a.Deliveraddress,a.UserTypeName,a.Last_InspectionStatus,a.InspectionStatus," +
                    "a.StopSupplyType1,a.StopSupplyType2,a.StopSupplyType3,a.StopSupplyType4,a.StopSupplyType5,a.StopSupplyType6," +
                    "a.StopSupplyType7,a.StopSupplyType8,a.UnInstallType1,a.UnInstallType2,a.UnInstallType3,a.UnInstallType4,a.UnInstallType5," +
                    "a.UnInstallType6,a.UnInstallType7,a.UnInstallType8,a.UnInstallType9,a.UnInstallType10,a.UnInstallType11," +
                    "a.UnInstallType12,a.TypeClass,a.StopSupplyType9,a.StopSupplyType10,a.StopSupplyType11,a.StopSupplyType12,a.StopSupplyType13," +
                    "a.StopSupplyType14,a.StopSupplyType15," +
                    "a.UnInstallType13,a.UnInstallType14  from T_B_UserRectifyInfo a" +
                    " where a.EmployeeID=? and a.StationID=? and a.UserID=? and a.IsInspected='0' ";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            if(query.moveToFirst()){
                userRectifyInfo.setId(query.getString(0));
                userRectifyInfo.setUserid(query.getString(1));
                userRectifyInfo.setUsername(query.getString(2));
                userRectifyInfo.setTelephone(query.getString(3));
                userRectifyInfo.setDeliveraddress(query.getString(4));
                userRectifyInfo.setUsertype(query.getString(5));
                userRectifyInfo.setLast_InspectionStatus(query.getString(6));
                userRectifyInfo.setInspectionStatus(query.getString(7));
                userRectifyInfo.setStopSupplyType1(query.getString(8));
                userRectifyInfo.setStopSupplyType2(query.getString(9));
                userRectifyInfo.setStopSupplyType3(query.getString(10));
                userRectifyInfo.setStopSupplyType4(query.getString(11));
                userRectifyInfo.setStopSupplyType5(query.getString(12));
                userRectifyInfo.setStopSupplyType6(query.getString(13));
                userRectifyInfo.setStopSupplyType7(query.getString(14));
                userRectifyInfo.setStopSupplyType8(query.getString(15));
                userRectifyInfo.setUnInstallType1(query.getString(16));
                userRectifyInfo.setUnInstallType2(query.getString(17));
                userRectifyInfo.setUnInstallType3(query.getString(18));
                userRectifyInfo.setUnInstallType4(query.getString(19));
                userRectifyInfo.setUnInstallType5(query.getString(20));
                userRectifyInfo.setUnInstallType6(query.getString(21));
                userRectifyInfo.setUnInstallType7(query.getString(22));
                userRectifyInfo.setUnInstallType8(query.getString(23));
                userRectifyInfo.setUnInstallType9(query.getString(24));
                userRectifyInfo.setUnInstallType10(query.getString(25));
                userRectifyInfo.setUnInstallType11(query.getString(26));
                userRectifyInfo.setUnInstallType12(query.getString(27));
                userRectifyInfo.setTypeClass(query.getString(28));
                userRectifyInfo.setStopSupplyType9(query.getString(29));
                userRectifyInfo.setStopSupplyType10(query.getString(30));
                userRectifyInfo.setStopSupplyType11(query.getString(31));
                userRectifyInfo.setStopSupplyType12(query.getString(32));
                userRectifyInfo.setStopSupplyType13(query.getString(33));
                userRectifyInfo.setStopSupplyType14(query.getString(34));
                userRectifyInfo.setStopSupplyType15(query.getString(35));
                userRectifyInfo.setUnInstallType13(query.getString(36));
                userRectifyInfo.setUnInstallType14(query.getString(37));
            }
            query.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return userRectifyInfo;
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
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and CustomerCardID=?";
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
        String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and " +
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
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and" +
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
//        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select ID,UserID,UserName,Telephone,Deliveraddress,UserTypeName from T_B_UserRectifyInfo where EmployeeID=? and StationID=? and" +
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
//        }catch(Exception ex){
//
//        }
        return userXJInfos;
    }
    public boolean deleteByID(String EmployeeID,String StationID,String ID ,String UserID){
        boolean ret=false;
        try{
            String[] args={StationID,EmployeeID,ID,UserID};
            String sql="delete from T_B_UserRectifyInfo where  StationID=? and EmployeeID=?  and ID=? and UserID=?";//销售表
            mDatabase.execSQL(sql,args);
            ret=true;
        }catch(Exception ex){
            ret=false;
        }

        return  ret;
    }
    public boolean isUpLoadRectify(String EmployeeID,String StationID,String ID){
        boolean result=false;
        try {
            String whereClause = "EmployeeID=? and StationID=? and ID=? and IsInspected='2'";
            String[] whereArgs = new String[] { EmployeeID, StationID,ID };
            Cursor cursor = mDatabase.query("T_B_UserRectifyInfo", null, whereClause,
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
}
