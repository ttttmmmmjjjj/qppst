package com.hsic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2019/3/29.
 */

public class DeleteData {
    private SQLiteDatabase mDatabase = null;
    public DeleteData(Context context){
        mDatabase=DataBaseHelper.getInstance(context).getReadableDatabase();
    }
    public void delete(String EmployeeID,String StationID){
//
        try{
//            mDatabase.beginTransaction();
            String[] args={StationID,EmployeeID};
            String sql="delete from T_B_SALE where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=? ";//销售表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_CUSTOMERINFO where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=? ";//安检表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_AJINFO where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=? ";//安检表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_UserRectifyInfo where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=? ";//整改表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_SALEDETAIL where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=?";//销售详细表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_SCANHISTORY where  date(InsertTime)< date('now','-2 day') and StationID=? and EmployeeID=? ";//ScanHistory
            mDatabase.execSQL(sql,args);

            String[] args2={EmployeeID};
            sql="delete from T_B_XJ_Association where  date(InsertTime)< date('now','-2 day') and  EmployeeID=? ";//安检附件关联表
            mDatabase.execSQL(sql,args2);

            sql="delete from T_B_AJ_Association where  date(InsertTime)< date('now','-2 day') and  EmployeeID=? ";//安检附件关联表
            mDatabase.execSQL(sql,args2);

            sql="delete from T_B_ZG_Association where  date(InsertTime)< date('now','-2 day') and  EmployeeID=? ";//整改附件关联表
            mDatabase.execSQL(sql,args2);
        }catch (Exception ex){
            Log.e("删除","清除数据异常:"+ex.toString());
            ex.printStackTrace();
        }

    }
    public void deleteALL(String EmployeeID,String StationID){
//
        try{
//            mDatabase.beginTransaction();
            String[] args={StationID,EmployeeID};
            String sql="delete from T_B_SALE where   StationID=? and EmployeeID=? ";//销售表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_CUSTOMERINFO where   StationID=? and EmployeeID=? ";//安检表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_AJINFO where   StationID=? and EmployeeID=? ";//安检表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_UserRectifyInfo where  StationID=? and EmployeeID=? ";//整改表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_SALEDETAIL where  StationID=? and EmployeeID=?";//销售详细表
            mDatabase.execSQL(sql,args);

            sql="delete from T_B_SCANHISTORY where   StationID=? and EmployeeID=? ";//ScanHistory
            mDatabase.execSQL(sql,args);

            String[] args2={EmployeeID};
            sql="delete from T_B_XJ_Association where    EmployeeID=? ";//安检附件关联表
            mDatabase.execSQL(sql,args2);

            sql="delete from T_B_AJ_Association where   EmployeeID=? ";//安检附件关联表
            mDatabase.execSQL(sql,args2);

            sql="delete from T_B_ZG_Association where   EmployeeID=? ";//整改附件关联表
            mDatabase.execSQL(sql,args2);
        }catch (Exception ex){
            Log.e("删除","清除数据异常:"+ex.toString());
            ex.printStackTrace();
        }

    }
}
