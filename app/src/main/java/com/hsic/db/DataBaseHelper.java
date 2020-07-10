package com.hsic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库
 * Created by Administrator on 2019/2/20.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper mInstance = null;
    public static final String DATABASE_NAME = "FXQPManager.db";
    private static final int DATABASE_VERSION = 24;
    private static final String T_B_AddressInfo="create table if not exists T_B_ADDRESS(ID TEXT,AeraCode TEXT," +
            "QuName TEXT,JieName TEXT,QuCode TEXT,JieCode TEXT)";//地址配置信息表
    private static final String T_B_Sale="create table if not exists T_B_SALE(SaleID TEXT ," +
            "UserID TEXT,StationID TEXT,StationName TEXT,ReceiveQP TEXT,SendQP TEXT,Match TEXT,IState TEXT," +
            "GPS_J TEXT,GPS_W TEXT,FinishTime TEXT,ReceiveByInput TEXT,DeliveryByInput TEXT,Remark TEXT,Address TEXT," +
            "AssignTime TEXT,EmployeeID TEXT,AllPrice TEXT,InsertTime TEXT,Telephone TEXT,RealPriceInfo TEXT,UrgeGasInfoStatus TEXT," +
            "InspectionItem TEXT,CreateTime TEXT,PayType TEXT,AZType TEXT,URL TEXT,ISZS TEXT,ISNeedZS TEXT)";//销售表
    private static final String T_B_SaleDetail="create table if not exists T_B_SALEDETAIL(SaleID TEXT ," +
            "QPType TEXT,QPName TEXT,PlanSendNum TEXT,PlanReceiveNum TEXT,ReceiveNum TEXT,SendNum TEXT,QTPrice TEXT," +
            "EmployeeID TEXT,StationID TEXT,IsEx TEXT,InsertTime TEXT)";//销售详细表
    private static final String T_B_CustomerInfo="create table if not exists T_B_CUSTOMERINFO(SaleID TEXT,UserID TEXT," +
            " StationID TEXT,CustomerType TEXT,CustomerTypeName TEXT,IsNew TEXT," +
            "CustomerCardID TEXT,UserCardStatus TEXT,RelationID TEXT,IsInspected TEXT,InspectionStatus TEXT,Last_InspectionStatus TEXT," +
            "StopSupplyType1 TEXT,StopSupplyType2 TEXT,StopSupplyType3 TEXT,StopSupplyType4 TEXT,StopSupplyType5 TEXT,StopSupplyType6 TEXT," +
            "StopSupplyType7 TEXT,StopSupplyType8 TEXT,StopSupplyType9 TEXT,StopSupplyType10 TEXT,StopSupplyType11 TEXT,StopSupplyType12 TEXT," +
            "StopSupplyType13 TEXT,StopSupplyType14 TEXT,StopSupplyType15 TEXT,"+
            "UnInstallType1 TEXT,UnInstallType2 TEXT,UnInstallType3 TEXT,UnInstallType4 TEXT," +
            "UnInstallType5 TEXT,UnInstallType6 TEXT,UnInstallType7 TEXT,UnInstallType8 TEXT,UnInstallType9 TEXT,UnInstallType10 TEXT," +
            "UnInstallType11 TEXT,UnInstallType12 TEXT,UnInstallType13 TEXT,UnInstallType14 TEXT," +
            "InspectionDate TEXT,EmployeeID TEXT,CustomerName TEXT,IState TEXT,InspectionMan TEXT," +
            " InsertTime TEXT,Telephone TEXT,Address TEXT,TagID TEXT,TypeClass TEXT,wghqp TEXT)";//用户表
    //安检信息配置表
    private static final String T_B_SearchConfig="create table if not exists T_B_SEARCH(ID TEXT,SearchKey TEXT," +
        "SearchValue TEXT)";
    //ScanHistory
    private static final String T_B_ScanHistory=" create table if not exists T_B_SCANHISTORY(SaleID TEXT ,UserID TEXT,EmployeeID TEXT,StationID TEXT,QPType TEXT," +
            "TypeFlag TEXT,UseRegCode TEXT,InsertTime TEXT)";
    private static final String T_B_XJASSOCIATION = "create table if not exists T_B_XJ_Association "
            + "(EmployeeID TEXT,SaleID TEXT,ImageName TEXT,RelationID TEXT,IsUpload TEXT,ImagStatus TEXT,FileName TEXT,InsertTime TEXT)";//巡检关联表
    private static final String T_B_AJASSOCIATION = "create table if not exists T_B_AJ_Association "
            + "(EmployeeID TEXT,UserID TEXT,ImageName TEXT,RelationID TEXT,IsUpload TEXT,ImagStatus TEXT,FileName TEXT,InsertTime TEXT)";//安检关联表
    private static final String T_B_ZGASSOCIATION = "create table if not exists T_B_ZG_Association "
            + "(EmployeeID TEXT,UserID TEXT,ID TEXT,ImageName TEXT,RelationID TEXT,IsUpload TEXT,ImagStatus TEXT,FileName TEXT,InsertTime TEXT)";//安检关联表
    //安检表
    private static final String T_B_AJnfo="create table if not exists T_B_AJINFO"
            +"(Deliveraddress TEXT,UserStatus TEXT,ExUserid TEXT,UserID TEXT,RectifyStatus TEXT,InspectionMan TEXT,"
            +" UserCardStatus TEXT,CustomerCardID TEXT,UserName TEXT,Telephone TEXT,Handphone TEXT,Handphone2 TEXT,"
            +" Handphone3 TEXT,UserType TEXT,UserTypeName TEXT,ExtendValue1 TEXT,Insurance TEXT,Fireno2 TEXT,"
            +" InspectionStatus TEXT,Last_InspectionStatus TEXT,InspectionDate TEXT,IsBackup TEXT,ID TEXT,Empname TEXT,"
            + " StopSupplyType1 TEXT,StopSupplyType2 TEXT,StopSupplyType3 TEXT,StopSupplyType4 TEXT,"
            + " StopSupplyType5 TEXT,StopSupplyType6 TEXT,StopSupplyType7 TEXT,StopSupplyType8 TEXT,"
            + " StopSupplyType9 TEXT,StopSupplyType10 TEXT,StopSupplyType11 TEXT,StopSupplyType12 TEXT,"
            + " StopSupplyType13 TEXT,StopSupplyType14 TEXT,StopSupplyType15 TEXT,"
            + " UnInstallType1 TEXT,UnInstallType2 TEXT,UnInstallType3 TEXT,UnInstallType4 TEXT,"
            + " UnInstallType5 TEXT,UnInstallType6 TEXT,UnInstallType7 TEXT,UnInstallType8 TEXT,"
            + " UnInstallType9 TEXT,UnInstallType10 TEXT,UnInstallType11 TEXT,UnInstallType12 TEXT,UnInstallType13 TEXT,UnInstallType14 TEXT,"
            + " RelationID TEXT,IsInspected TEXT,StationID  TEXT,RectifyDate TEXT,OperationResult TEXT,Remark TEXT,"
            + " EmployeeID TEXT,InsertTime TEXT,TagID TEXT,TypeClass TEXT)";//用户表
    //整改表
    private static final String T_B_ZGInfo="create table if not exists T_B_UserRectifyInfo"
            +"(Deliveraddress TEXT,UserStatus TEXT,ExUserid TEXT,UserID TEXT,RectifyStatus TEXT,RectifyMan TEXT,"
            +" UserCardStatus TEXT,CustomerCardID TEXT,UserName TEXT,Telephone TEXT,Handphone TEXT,Handphone2 TEXT,"
            +" Handphone3 TEXT,UserType TEXT,UserTypeName TEXT,ExtendValue1 TEXT,Insurance TEXT,Fireno2 TEXT,"
            +" InspectionStatus TEXT,Last_InspectionStatus TEXT,InspectionDate TEXT,IsBackup TEXT,ID TEXT,Empname TEXT,"
            + " StopSupplyType1 TEXT,StopSupplyType2 TEXT,StopSupplyType3 TEXT,StopSupplyType4 TEXT,"
            + " StopSupplyType5 TEXT,StopSupplyType6 TEXT,StopSupplyType7 TEXT,StopSupplyType8 TEXT,"
            + " StopSupplyType9 TEXT,StopSupplyType10 TEXT,StopSupplyType11 TEXT,StopSupplyType12 TEXT,"
            + " StopSupplyType13 TEXT,StopSupplyType14 TEXT,StopSupplyType15 TEXT,"
            + " UnInstallType1 TEXT,UnInstallType2 TEXT,UnInstallType3 TEXT,UnInstallType4 TEXT,"
            + " UnInstallType5 TEXT,UnInstallType6 TEXT,UnInstallType7 TEXT,UnInstallType8 TEXT,"
            + " UnInstallType9 TEXT,UnInstallType10 TEXT,UnInstallType11 TEXT,UnInstallType12 TEXT,UnInstallType13 TEXT,UnInstallType14 TEXT,"
            + " RelationID TEXT,IsInspected TEXT,StationID  TEXT,RectifyDate TEXT,OperationResult TEXT,Remark TEXT,"
            + " EmployeeID TEXT,InsertTime TEXT,TagID TEXT,TypeClass TEXT)";
    //代开户表
    private static final String T_B_DKSale=" create table if not exists T_B_DKSale (DKSaleID TEXT,CustomerID TEXT,CustomerName TEXT,"
            +" StationID TEXT,StationName TEXT,Address TEXT,Telephone TEXT,CreateTime TEXT,StopSupplyType1 TEXT,StopSupplyType2 TEXT,"
            + " StopSupplyType3 TEXT,StopSupplyType4 TEXT,StopSupplyType5 TEXT,StopSupplyType6 TEXT,StopSupplyType7 TEXT,StopSupplyType8 TEXT,"
            +" StopSupplyType9 TEXT,StopSupplyType10 TEXT,StopSupplyType11 TEXT,StopSupplyType12 TEXT,"
            + " StopSupplyType13 TEXT,StopSupplyType14 TEXT,StopSupplyType15 TEXT,"
            +" UnInstallType1 TEXT,UnInstallType2 TEXT,UnInstallType3 TEXT,UnInstallType4 TEXT,UnInstallType5 TEXT,UnInstallType6 TEXT,"
            +" UnInstallType7 TEXT,UnInstallType8 TEXT,UnInstallType9 TEXT,UnInstallType10 TEXT,UnInstallType11 TEXT,UnInstallType12 TEXT,"
            +" UnInstallType13 TEXT,UnInstallType14 TEXT,"
            +" InspectionStatus TEXT,InspectionDate TEXT,InspectionMan TEXT,AttachID TEXT,InspectionItem TEXT,IsInspected TEXT,TypeClass TEXT )";
    //用户类型
    private static final String T_B_CustomerType="create table if not exists T_B_CustomerType(CustomerType TEXT, CustomerTypeName TEXT)";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }
    /** 单例模式 **/
    public static synchronized DataBaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseHelper(context);
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        creatTables(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        /** 可以拿到当前数据库的版本信息 与之前数据库的版本信息 用来更新数据库 **/
        if (newVersion > oldVersion) {
            deleteTables(db);
            creatTables(db);
        }
    }
    private void creatTables(SQLiteDatabase db) {
        /** 向数据中添加表 **/
        db.execSQL(T_B_AddressInfo);
        db.execSQL(T_B_Sale);
        db.execSQL(T_B_SaleDetail);
        db.execSQL(T_B_CustomerInfo);
        db.execSQL(T_B_SearchConfig);
        db.execSQL(T_B_ScanHistory);
        db.execSQL(T_B_XJASSOCIATION);
        db.execSQL(T_B_AJASSOCIATION);
        db.execSQL(T_B_ZGASSOCIATION);
        db.execSQL(T_B_AJnfo);
        db.execSQL(T_B_ZGInfo);
        db.execSQL(T_B_DKSale);
        db.execSQL(T_B_CustomerType);
    }

    private void deleteTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS T_B_CustomerType");
        db.execSQL("DROP TABLE IF EXISTS T_B_ADDRESS");
        db.execSQL("DROP TABLE IF EXISTS T_B_SALE");
        db.execSQL("DROP TABLE IF EXISTS T_B_SALEDETAIL");
        db.execSQL("DROP TABLE IF EXISTS T_B_CUSTOMERINFO");
        db.execSQL("DROP TABLE IF EXISTS T_B_SEARCH");
        db.execSQL("DROP TABLE IF EXISTS T_B_SCANHISTORY");
        db.execSQL("DROP TABLE IF EXISTS T_B_XJ_Association");
        db.execSQL("DROP TABLE IF EXISTS T_B_AJ_Association");
        db.execSQL("DROP TABLE IF EXISTS T_B_ZG_Association");
        db.execSQL("DROP TABLE IF EXISTS T_B_AJINFO");
        db.execSQL("DROP TABLE IF EXISTS T_B_ZGINFO");
        db.execSQL("DROP TABLE IF EXISTS T_B_DKSale");
    }
}
