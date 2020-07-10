package com.hsic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.bean.DKSale;
import com.hsic.bean.FileRelationInfo;
import com.hsic.bean.HsicMessage;
import com.hsic.utils.TimeUtils;
import com.hsic.web.WebServiceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 代开户
 * 注：这里涉及的SaleID相当于代开户ID
 * Created by Administrator on 2019/8/12.
 */

public class DKSaleDB {
    private StringBuffer sql_insert;
    private SQLiteDatabase mDatabase = null;
    public DKSaleDB(Context context){
        mDatabase=DataBaseHelper.getInstance(context).getReadableDatabase();
    }

    /**
     * 后台数据插入到本地数据库
     * @param stationID
     * @param emplyeeID
     */
    public void insertData(DKSale dkSale, String stationID, String emplyeeID){
        try{
            sql_insert = new StringBuffer();
            sql_insert.append("INSERT INTO T_B_DKSale(DKSaleID,CustomerID,CustomerName,StationID,StationName,Address," +
                    "Telephone,CreateTime,StopSupplyType1,StopSupplyType2,StopSupplyType3,StopSupplyType4,StopSupplyType5,StopSupplyType6," +
                    "StopSupplyType7,StopSupplyType8,UnInstallType1,UnInstallType2,UnInstallType3,UnInstallType4,UnInstallType5,UnInstallType6," +
                    "UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11,UnInstallType12,InspectionMan,IsInspected)");
            sql_insert.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            String DKSaleID="";
            DKSaleID=dkSale.getDKSaleID();
            if(!isExist(emplyeeID,stationID,DKSaleID)){
                SQLiteStatement statement=    mDatabase.compileStatement(sql_insert.toString());
                statement.bindString(1, dkSale.getDKSaleID());
                statement.bindString(2, dkSale.getCustomerID());
                statement.bindString(3, dkSale.getCustomerName());
                statement.bindString(4, dkSale.getStationID());
                statement.bindString(5, dkSale.getStationName());
                statement.bindString(6, dkSale.getAddress());
                statement.bindString(7, dkSale.getTelphone());
                statement.bindString(8, dkSale.getCreateTime());
                statement.bindString(9, dkSale.getStopSupplyType1());
                statement.bindString(10, dkSale.getStopSupplyType2());
                statement.bindString(11, dkSale.getStopSupplyType3());
                statement.bindString(12, dkSale.getStopSupplyType4());
                statement.bindString(13, dkSale.getStopSupplyType5());
                statement.bindString(14, dkSale.getStopSupplyType6());
                statement.bindString(15,dkSale.getStopSupplyType7());
                statement.bindString(16,dkSale.getStopSupplyType8());
                statement.bindString(17,dkSale.getUnInstallType1());
                statement.bindString(18,dkSale.getUnInstallType2());
                statement.bindString(19,dkSale.getUnInstallType3());
                statement.bindString(20,dkSale.getUnInstallType4());
                statement.bindString(21,dkSale.getUnInstallType5());
                statement.bindString(22,dkSale.getUnInstallType6());
                statement.bindString(23,dkSale.getUnInstallType7());
                statement.bindString(24,dkSale.getUnInstallType8());
                statement.bindString(25,dkSale.getUnInstallType9());
                statement.bindString(26,dkSale.getUnInstallType10());
                statement.bindString(27,dkSale.getUnInstallType11());
                statement.bindString(28,dkSale.getUnInstallType12());
                statement.bindString(29,emplyeeID);
                statement.bindString(30,"0");
                statement.executeInsert();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 查询代开户相关信息
     * @param EmployeeID
     * @param StationID
     * @param DKSaleID
     * @return
     */
    public DKSale getDKInfo(String EmployeeID, String StationID, String DKSaleID) {
        DKSale dkSale = new DKSale();
        try {
            String[] selections = {EmployeeID, StationID, DKSaleID};
            String sql = "select CustomerName,StationName,Address,Telephone from T_B_DKSale where InspectionMan=? and StationID=? and DKSaleID=? and IsInspected='0'";
            Cursor query = mDatabase.rawQuery(sql, selections);
            if (query.moveToFirst()) {
                dkSale.setCustomerName(query.getString(0));
                dkSale.setStationName(query.getString(1));
                dkSale.setAddress(query.getString(2));
                dkSale.setTelphone(query.getString(3));
            }
            query.close();
        } catch (Exception ex) {
            dkSale = null;
        }
        return dkSale;
    }
    /**
     * 该订单是否做过代开户
     * @param EmployeeID
     * @param StationID
     * @param DKSaleID
     * @return
     */
    public boolean isExist(String EmployeeID,String StationID,String DKSaleID){
        boolean result=false;
        try {
            String whereClause = "InspectionMan=? and StationID=? and DKSaleID=? and IsInspected='0'";
            String[] whereArgs = new String[] { EmployeeID, StationID,DKSaleID };
            Cursor cursor = mDatabase.query("T_B_DKSale", null, whereClause,
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
     * 更新代开户表
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param dkSale
     * @return
     */
    public boolean updateDKSale(String EmployeeID, String StationID, String SaleID, DKSale dkSale){
        boolean ret=false;
        try{
            /**
             * 更新安检信息
             */
            String whereClauseBys = "InspectionMan=? and StationID=? and DKSaleID=? ";
            String[] whereArgsBys = { EmployeeID,StationID, SaleID};
            ContentValues valuesBys = new ContentValues();
            valuesBys.put("RelationID",dkSale.getAttachID());
            valuesBys.put("IsInspected","1");
            valuesBys.put("InspectionStatus",dkSale.getInspectionStatus());
            valuesBys.put("InspectionDate",dkSale.getCreateTime());
            valuesBys.put("InspectionMan",dkSale.getInspectionMan());
            valuesBys.put("StopSupplyType1",dkSale.getStopSupplyType1());
            valuesBys.put("StopSupplyType2",dkSale.getStopSupplyType2());
            valuesBys.put("StopSupplyType3",dkSale.getStopSupplyType3());
            valuesBys.put("StopSupplyType4",dkSale.getStopSupplyType4());
            valuesBys.put("StopSupplyType5",dkSale.getStopSupplyType5());
            valuesBys.put("StopSupplyType6",dkSale.getStopSupplyType6());
            valuesBys.put("StopSupplyType7",dkSale.getStopSupplyType7());
            valuesBys.put("StopSupplyType8",dkSale.getStopSupplyType8());
            valuesBys.put("UnInstallType1",dkSale.getUnInstallType1());
            valuesBys.put("UnInstallType2",dkSale.getUnInstallType2());
            valuesBys.put("UnInstallType3",dkSale.getUnInstallType3());
            valuesBys.put("UnInstallType4",dkSale.getUnInstallType4());
            valuesBys.put("UnInstallType5",dkSale.getUnInstallType5());
            valuesBys.put("UnInstallType6",dkSale.getUnInstallType6());
            valuesBys.put("UnInstallType7",dkSale.getUnInstallType7());
            valuesBys.put("UnInstallType8",dkSale.getUnInstallType8());
            valuesBys.put("UnInstallType9",dkSale.getUnInstallType9());
            valuesBys.put("UnInstallType10",dkSale.getUnInstallType10());
            valuesBys.put("UnInstallType11",dkSale.getUnInstallType11());
            valuesBys.put("UnInstallType12",dkSale.getUnInstallType12());
            int i = mDatabase.update("T_B_DKSale", valuesBys,
                    whereClauseBys, whereArgsBys);
            if (i>0) {
                ret=true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }

    public int updateDKStatus(String EmployeeID, String StationID, String SaleID){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "InspectionMan=? and StationID=? and DKSaleID=?";
        String[] whereArgs={EmployeeID,StationID,SaleID};
        values.put("IsInspected","2");
        try{
            ret=mDatabase.update("T_B_DKSale",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }
    public boolean isUpLoadDKSale(String EmployeeID, String StationID, String SaleID){
        boolean result=false;
        try {
            String whereClause = "InspectionMan=? and StationID=? and DKSaleID=? and IsInspected='2'";
            String[] whereArgs = new String[] { EmployeeID, StationID,SaleID };
            Cursor cursor = mDatabase.query("T_B_DKSale", null, whereClause,
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
     * 上传代开户信息
     * @param EmployeeID
     * @param StationID
     * @param SaleID
     * @param UserID
     */
    public HsicMessage uploadDKSaleInfo(Context context, String DeviceId, String EmployeeID, String StationID, String SaleID, String UserID){
        HsicMessage hsicMess = new HsicMessage();
        WebServiceHelper web = new WebServiceHelper(context);
        try{
            hsicMess.setRespCode(10);
            DKSale dkSale = new DKSale();
            String[] selections = { EmployeeID, StationID,SaleID};
            String sql="select RelationID,InspectionStatus,InspectionDate,InspectionMan,StopSupplyType1,StopSupplyType2,StopSupplyType3," +
                    "StopSupplyType4,StopSupplyType5,StopSupplyType6,StopSupplyType7,StopSupplyType8,UnInstallType1,UnInstallType2,UnInstallType3," +
                    "UnInstallType4,UnInstallType5,UnInstallType6,UnInstallType7,UnInstallType8,UnInstallType9,UnInstallType10,UnInstallType11," +
                    "UnInstallType12 from T_B_DKSale where InspectionMan=? and StationID=? and DKSaleID=?";
            Cursor query = mDatabase.rawQuery(sql, selections);
            if(query.moveToFirst()){
                dkSale.setAttachID(query.getString(0));
                dkSale.setInspectionStatus(query.getString(1));
                dkSale.setFinishTime(query.getString(2));
                dkSale.setInspectionMan(query.getString(3));
                dkSale.setStopSupplyType1(query.getString(4));
                dkSale.setStopSupplyType2(query.getString(5));
                dkSale.setStopSupplyType3(query.getString(6));
                dkSale.setStopSupplyType4(query.getString(7));
                dkSale.setStopSupplyType5(query.getString(8));
                dkSale.setStopSupplyType6(query.getString(9));
                dkSale.setStopSupplyType7(query.getString(10));
                dkSale.setStopSupplyType8(query.getString(11));
                dkSale.setUnInstallType1(query.getString(12));
                dkSale.setUnInstallType2(query.getString(13));
                dkSale.setUnInstallType3(query.getString(14));
                dkSale.setUnInstallType4(query.getString(15));
                dkSale.setUnInstallType5(query.getString(16));
                dkSale.setUnInstallType6(query.getString(17));
                dkSale.setUnInstallType7(query.getString(18));
                dkSale.setUnInstallType8(query.getString(19));
                dkSale.setUnInstallType9(query.getString(20));
                dkSale.setUnInstallType10(query.getString(21));
                dkSale.setUnInstallType11(query.getString(22));
                dkSale.setUnInstallType12(query.getString(23));
                dkSale.setStationID(StationID);
                dkSale.setDKSaleID(SaleID);
                dkSale.setCustomerID(UserID);
            }
            query.close();
            String data = "";// 上传传输的数据
            data = JSONUtils.toJsonWithGson(dkSale);
            hsicMess.setRespMsg(data);
            String requestData = JSONUtils.toJsonWithGson(hsicMess);
            String[] selection = { "DeviceID", "RequestData" };
            String[] selectionArgs = { DeviceId, requestData };
            String methodName = "";
            methodName = "UpDKUserInspectionInfo";// 方法名称
            hsicMess = web.uploadInfo(selection, methodName, selectionArgs);
            int i = hsicMess.getRespCode();// 方法执行结果
            if(i ==0){
                updateDKStatus(EmployeeID,StationID,SaleID);
            }
        }catch(Exception ex){
            ex.toString();
            ex.printStackTrace();
        }
        return hsicMess;
    }

    /**
     * 查询未上传的数据
     * @param EmployeeID
     * @param StationID
     * @return
     */
    public List<DKSale> UpHistoryDKSaleInfo(String EmployeeID, String StationID){
        List<DKSale> dkSales=new ArrayList<>();
        try{
            String[] selectionArgs = new String[] { EmployeeID, StationID };
            String sql=" select DKSaleID,CustomerID from T_B_DKSale where InspectionMan=? and StationID=? and IsInspected='1'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                DKSale dkSale=new DKSale();
                dkSale.setDKSaleID(query.getString(0));
                dkSale.setCustomerID(query.getString(1));
                dkSales.add(dkSale);
            }
            query.close();
        }catch(Exception  ex){
            ex.printStackTrace();
        }
        return dkSales;
    }
    /**
     * 上传代开户历史信息
     * @param context
     * @param DeviceId
     * @param EmployeeID
     * @param StationID
     */
    public void UpHistoryDKSale(Context context,String  DeviceId,String EmployeeID, String StationID){
        List<DKSale> dkSales=new ArrayList<>();
        dkSales=UpHistoryDKSaleInfo(EmployeeID,StationID);
        int size=dkSales.size();
        if(size>0){
            for(int i=0;i<size;i++){
                String SaleID;String CustomID;
                SaleID= dkSales.get(i).getDKSaleID();
                CustomID=dkSales.get(i).getCustomerID();
                uploadDKSaleInfo(context,DeviceId,EmployeeID,StationID,SaleID,CustomID);
                /**
                 *上传代开户关联表历史信息
                 */
                List<FileRelationInfo> fileRelationInfos=new ArrayList<>();
                fileRelationInfos=GetXJFileRelationInfo(EmployeeID,SaleID);
                size=fileRelationInfos.size();
                if(size>0){
                    UpLoadA(fileRelationInfos,DeviceId,context);
                }
            }

        }

    }
    /**
     * 将照片基本信息插入到数据表中  安检
     * @param
     * @param DKSaleID
     * @param
     * @param RelationID
     */
    public void InsertXJAssociation(String EmployeeID, String DKSaleID, String ImageName, String RelationID, String FileName){
        try{
            ContentValues cValue = new ContentValues();
            cValue.put("EmployeeID", EmployeeID);
            cValue.put("SaleID", DKSaleID);
            cValue.put("ImageName", ImageName);
            cValue.put("RelationID", RelationID);
            cValue.put("FileName", FileName);
            cValue.put("IsUpload", "0");
            cValue.put("InsertTime", TimeUtils.getTime("yyyy-MM-dd HH:mm:ss"));

            long ret = mDatabase.insert("T_B_XJ_Association", null, cValue);
        }catch(Exception ex){
            ex.toString();

        }
    }
    /**
     * 查询代开户所有的照片关联信息
     * SaleID:
     */
    public List<FileRelationInfo> GetXJFileRelationInfo(String EmployeeID, String DKSaleID){
        List<FileRelationInfo> FileRelationInfo_LIST=new ArrayList<FileRelationInfo>();
        try{
            String[] selectionArgs = { EmployeeID, DKSaleID};
            String sql = "select RelationID,ImageName,FileName  from  T_B_XJ_Association where EmployeeID=? and SaleID=? and IsUpload='0'";
            Cursor query = mDatabase.rawQuery(sql, selectionArgs);
            while(query.moveToNext()){
                FileRelationInfo f=new FileRelationInfo();
                f.setRelationID(query.getString(0));
                String FilePath=query.getString(2)+"/"+query.getString(1);
                f.setFilePath(FilePath);
                f.setImageName(query.getString(1));
                f.setTruckNoId(EmployeeID);
                f.setSaleID(DKSaleID);
                FileRelationInfo_LIST.add(f);
            }
            query.close();
        }catch(Exception ex){

        }
        return FileRelationInfo_LIST;
    }
    public int UpDateXJAssociation(String EmployeeID,String DKSaleID,String Path){
        int ret=-1;
        ContentValues values = new ContentValues();
        String whereClause = "EmployeeID=? and SaleID=? and ImageName=?";
        String[] whereArgs={EmployeeID,DKSaleID,Path};
        values.put("IsUpLoad","1");
        try{
            ret=mDatabase.update("T_B_XJ_Association",values,whereClause,whereArgs);
        }catch (Exception ex){
            ex.printStackTrace();
            ret=-1;
        }
        return ret;
    }

    /**
     * 上传附件关联信息
     * @param FileRelationInfo_LIST
     * @param deviceid
     * @param context
     * @return
     */
    public HsicMessage UpLoadA(List<FileRelationInfo> FileRelationInfo_LIST, String deviceid,Context context) {
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
                UpDateXJAssociation(TruckNoId, SaleID, ImageName);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            hsicMessage.setRespCode(5);
            hsicMessage.setRespMsg("调用借口异常");
        }
        return hsicMessage;
    }

}
