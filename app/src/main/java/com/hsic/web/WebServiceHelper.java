package com.hsic.web;

import android.content.Context;

import com.hsic.qpmanager.util.json.JSONUtils;
import com.hsic.bean.HsicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/25.
 */

public class WebServiceHelper {
    private Context mContext;
    NetWorkHelper mHelper;//
    GetData orderdeclData;
    public WebServiceHelper(Context context) {//实现构造函数
        super();
        mContext = context;
        mHelper = new NetWorkHelper(mContext);// 判断网络是否可用
        orderdeclData = new GetData(mContext);

    }

    /***
     * 获取街道基本信息
     * @param DeviceID
     * @return
     */
    public HsicMessage GetStreetInfo(String DeviceID) {
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);
            return mess;
        }
        GetData orderdeclData = new GetData(mContext);
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        propertyList.add(device);
        try {
            Object object = orderdeclData.recevieData("loadStreetInfo", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 获取配送订单信息
     * @param DeviceID
     * @param EmployeeID
     * @return
     */
    public HsicMessage SearchAssignSale(String DeviceID,String EmployeeID) {
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);
            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> r = new HashMap<String, Object>();
        r.put("propertyName", "EmployeeID");// 参数名称
        r.put("propertyValue",EmployeeID);
        HashMap<String,Object> Exc=new HashMap<String,Object>();
        Exc.put("propertyName", "Exc");
        Exc.put("propertyValue",true);
        propertyList.add(device);
        propertyList.add(r);
        propertyList.add(Exc);
        try {
            Object object = orderdeclData.recevieData("SearchAssignSale", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 获取作废订单
     * @param DeviceID
     * @param EmployeeID
     * @return
     */
    public HsicMessage SearchCanselSale(String DeviceID,String EmployeeID) {
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);
            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> r = new HashMap<String, Object>();
        r.put("propertyName", "EmployeeID");// 参数名称
        r.put("propertyValue",EmployeeID);
        HashMap<String,Object> Exc=new HashMap<String,Object>();
        Exc.put("propertyName", "Exc");
        Exc.put("propertyValue",false);
        propertyList.add(device);
        propertyList.add(r);
        propertyList.add(Exc);
        try {
            Object object = orderdeclData.recevieData("SearchAssignSale", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 下载整改信息
     * @param DeviceID
     * @param EmployeeID
     * @return
     */
    public HsicMessage DownloadRectifyInfo(String DeviceID,String EmployeeID) {
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);
            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> r = new HashMap<String, Object>();
        r.put("propertyName", "empid");// 参数名称
        r.put("propertyValue",EmployeeID);
        propertyList.add(device);
        propertyList.add(r);

        try {
            Object object = orderdeclData.recevieData("DownloadRectifyInfo", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }
    /**
     * 根据登录获取基本信息
     * @param DeviceID
     * @param
     * @return
     */
    public HsicMessage EmployeeLogin(String DeviceID,String UserID,String Password) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("propertyName", "UserID");// 参数名称
        request.put("propertyValue",UserID);
        HashMap<String, Object> password = new HashMap<String, Object>();
        password.put("propertyName", "Password");// 参数名称
        password.put("propertyValue",Password);
        propertyList.add(request);
        propertyList.add(device);
        propertyList.add(password);
        try {
            Object object = orderdeclData.recevieData("EmployeeLogin", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }
    /**
     * 上传FTP
     * @param argsName
     * @param methodName
     * @param data
     * @return
     */
    public HsicMessage uploadInfo(String[] argsName, String methodName,
                                  String[] data) {
        HsicMessage mess = new HsicMessage();
        mess.setRespCode(8);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(3);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("network check error!");
            mess.setRespCode(4);
            return mess;
        }
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < data.length; i++) {
            HashMap<String, Object> map1 = new HashMap<String, Object>();
            map1.put("propertyName", argsName[i]);// 参数名称
            map1.put("propertyValue", data[i]);
            propertyList.add(map1);
        }
        try {
            Object object = orderdeclData.recevieData(methodName, propertyList, true);// 方法名：UpCJData

            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            } else {
                mess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(6);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 下载安检信息
     * @param DeviceID
     * @param EmployeeID
     * @return
     */
    public HsicMessage getUserInspection(String DeviceID,String EmployeeID) {
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);
            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> r = new HashMap<String, Object>();
        r.put("propertyName", "CustomerID");// 参数名称
        r.put("propertyValue",EmployeeID);
        propertyList.add(device);
        propertyList.add(r);

        try {
            Object object = orderdeclData.recevieData("getUserInspection", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }
    /**
     * 获取用户基本信息
     * @param requestData
     * @param DeviceID
     * @param StationId
     * @return
     */
    public HsicMessage getBasicUserInfo(String requestData,String DeviceID,String StationId){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }
        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "RequestData");// 参数名称
        map2.put("propertyValue",requestData);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "StationId");// 参数名称
        map3.put("propertyValue",StationId);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        try {
            Object object = getData.recevieData("getBasicUserInfo", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 下单
     * @param requestData
     * @param DeviceID
     * @return
     */
    public HsicMessage subscriberOrder(String requestData,String DeviceID,String StationId){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }
        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "RequestData");// 参数名称
        map2.put("propertyValue",requestData);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "StationId");// 参数名称
        map3.put("propertyValue",StationId);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        try {
            Object object = getData.recevieData("subscriberOrder", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /***
     * 根据订单号查询订单
     * @param requestData
     * @param DeviceID
     * @return
     */
    public HsicMessage getOrderBySaleId(String requestData,String DeviceID,String StationId){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }
        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "RequestData");// 参数名称
        map2.put("propertyValue",requestData);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "StationId");// 参数名称
        map3.put("propertyValue",StationId);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        try {
            Object object = getData.recevieData("getOrderBySaleId", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 提货
     * @param requestData
     * @param DeviceID
     * @return
     */
    public HsicMessage finishOrder(String requestData,String DeviceID,String StationId){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }
        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "RequestData");// 参数名称
        map2.put("propertyValue",requestData);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "StationId");// 参数名称
        map3.put("propertyValue",StationId);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        try {
            Object object = getData.recevieData("finishOrder", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 订单作废
     * @param DeviceID
     * @param StationID
     * @return
     */
    public HsicMessage cancelOrder(String DeviceID,String StationID, String RequestData){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }
        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "StationID");// 参数名称
        map2.put("propertyValue",StationID);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "RequestData");// 参数名称
        map3.put("propertyValue",RequestData);

        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        try {
            Object object = getData.recevieData("cancelOrder", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 获取该站点，该操作人员底下订单
     * @param DeviceID
     * @param StationID 站点
     * @param operationID  操作员
     * @param saleStatus  1:下单 ，2:提货 ，3:作废 4：补打
     * @return
     */
    public HsicMessage getAllSale(String DeviceID, String StationID, String operationID, String saleStatus){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "StationID");// 参数名称
        map2.put("propertyValue",StationID);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "operationID");// 参数名称
        map3.put("propertyValue",operationID);
        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("propertyName", "saleStatus");// 参数名称
        map4.put("propertyValue",saleStatus);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        propertyList.add(map4);
        try {
            Object object = getData.recevieData("getAllSale", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 获取设备信息
     * @param DeviceID
     * @param StationID
     * @return
     */
    public HsicMessage getDevice(String DeviceID, String StationID){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "StationID");// 参数名称
        map2.put("propertyValue",StationID);
        propertyList.add(map1);
        propertyList.add(map2);
        try {
            Object object = getData.recevieData("getDevice", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 换货
     * @param DeviceID
     * @param StationID
     * @param saleID 订单号
     * @param lastFullID  上次满瓶号
     * @param currentFullID 本次满瓶号
     * @return
     */
    public HsicMessage exchangeGoods(String DeviceID, String StationID,String saleID,
                                     String lastFullID,String currentFullID){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "StationID");// 参数名称
        map2.put("propertyValue",StationID);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "saleID");// 参数名称
        map3.put("propertyValue",saleID);
        HashMap<String, Object> map4 = new HashMap<String, Object>();
        map4.put("propertyName", "lastFullID");// 参数名称
        map4.put("propertyValue",lastFullID);
        HashMap<String, Object> map5 = new HashMap<String, Object>();
        map5.put("propertyName", "currentFullID");// 参数名称
        map5.put("propertyValue",currentFullID);
        propertyList.add(map1);
        propertyList.add(map2);
        propertyList.add(map3);
        propertyList.add(map4);
        propertyList.add(map5);
        try {
            Object object = getData.recevieData("exchangeGoods", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    public HsicMessage getSaleCount(String DeviceID, String StationID){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "StationID");// 参数名称
        map2.put("propertyValue",StationID);
        propertyList.add(map1);
        propertyList.add(map2);

        try {
            Object object = getData.recevieData("getSaleCount", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    public HsicMessage CustomerLogin_PhoneNew (String DeviceID, String Telphone){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "Telphone");// 参数名称
        map2.put("propertyValue",Telphone);
        propertyList.add(map1);
        propertyList.add(map2);

        try {
            Object object = getData.recevieData("CustomerLogin_PhoneNew", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    public HsicMessage CustomerLogin_Address (String DeviceID, String Address,String Station){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "Address");// 参数名称
        map2.put("propertyValue",Address);
        propertyList.add(map1);
        propertyList.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "Station");// 参数名称
        map3.put("propertyValue",Station);
        propertyList.add(map3);

        try {
            Object object = getData.recevieData("CustomerLogin_Address", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    public HsicMessage CustomerInfoLoginCustomerCardID (String DeviceID, String CustomerCardID){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "CustomerCardID");// 参数名称
        map2.put("propertyValue",CustomerCardID);
        propertyList.add(map1);
        propertyList.add(map2);

        try {
            Object object = getData.recevieData("CustomerInfoLoginCustomerCardID", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    public HsicMessage CustomerLogin_CustomerName (String DeviceID, String CustomerName,String Station){
        HsicMessage hsicMess=new HsicMessage();
        hsicMess.setRespCode(-1);
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用
        try{
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespCode(3);
                hsicMess.setRespMsg("网络连接失败");
                return hsicMess;
            }
        }catch(Exception ex){
            ex.printStackTrace();
            ex.toString();
            hsicMess.setRespMsg("获取网络状态异常"+ex.toString());
        }

        GetData getData=new GetData(mContext);
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("propertyName", "DeviceID");// 参数名称
        map1.put("propertyValue",DeviceID);
        HashMap<String, Object> map2 = new HashMap<String, Object>();
        map2.put("propertyName", "CustomerName");// 参数名称
        map2.put("propertyValue",CustomerName);
        propertyList.add(map1);
        propertyList.add(map2);
        HashMap<String, Object> map3 = new HashMap<String, Object>();
        map3.put("propertyName", "Station");// 参数名称
        map3.put("propertyValue",Station);
        propertyList.add(map3);

        try {
            Object object = getData.recevieData("CustomerLogin_CustomerName", propertyList,true);//添加参数调用recevieData方法返回结果
            if(object.toString().equals("false")){
                hsicMess.setRespCode(1);
                hsicMess.setRespMsg("调用失败");
            }else{
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 退单
     * @param DeviceID
     * @return
     */
    public HsicMessage Chargeback(String DeviceID,String RequestData) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("propertyName", "RequestData");// 参数名称
        request.put("propertyValue",RequestData);

        propertyList.add(request);
        propertyList.add(device);
        try {
            Object object = orderdeclData.recevieData("Chargeback", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 修改员工密码
     * @param DeviceID
     * @param RequestData
     * @return
     */
    public HsicMessage UpdateEmployeeMM(String DeviceID,String RequestData) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("propertyName", "RequestData");// 参数名称
        request.put("propertyValue",RequestData);

        propertyList.add(request);
        propertyList.add(device);
        try {
            Object object = orderdeclData.recevieData("UpdateEmployeeMM", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     * 获取代开户安检单
     * @param DeviceID
     * @param UserID
     * @return
     */
    public HsicMessage SearchAssignDKSaleInfo(String DeviceID,String UserID) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("propertyName", "UserID");// 参数名称
        request.put("propertyValue",UserID);

        propertyList.add(request);
        propertyList.add(device);
        try {
            Object object = orderdeclData.recevieData("SearchAssignDKSaleInfo", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
//            Log.e("yichang",e.toString());
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }

    /**
     *上传代开户安检信息
     * @param DeviceID
     * @param RequestData
     * @return
     */
    public HsicMessage UpDKUserInspectionInfo(String DeviceID,String RequestData) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object> request = new HashMap<String, Object>();
        request.put("propertyName", "RequestData");// 参数名称
        request.put("propertyValue",RequestData);

        propertyList.add(request);
        propertyList.add(device);
        try {
            Object object = orderdeclData.recevieData("UpDKUserInspectionInfo", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }
    /**
     * 获取二维码
     */
    public HsicMessage getUrl(String DeviceID,String  SaleID,String money) {
        // 参数的含义，其一是参数的名字，
        // 其二是调用webService的方法名字
        // 其三是调用webService的方法要传入的参数
        HsicMessage mess = new HsicMessage();// 创建HsicMessage
        mess.setRespCode(-1);// 设置RespCode属性
        try {
            if (!mHelper.checkNetworkStatus()) {
                mess.setRespMsg("网络连接失败");
                mess.setRespCode(-2);
                return mess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            mess.setRespMsg("网络检查失败");
            mess.setRespCode(-3);

            return mess;
        }
        // 在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> device = new HashMap<String, Object>();
        device.put("propertyName", "DeviceID");// 参数名称
        device.put("propertyValue",DeviceID);
        HashMap<String, Object>  saleID = new HashMap<String, Object>();
        saleID.put("propertyName", "SaleID");// 参数名称
        saleID.put("propertyValue", SaleID);
        HashMap<String, Object>  Money = new HashMap<String, Object>();
        Money.put("propertyName", "money");// 参数名称
        Money.put("propertyValue", money);
        propertyList.add( saleID);
        propertyList.add(device);
        propertyList.add(Money);
        try {
            Object object = orderdeclData.recevieData("getUrl", propertyList,
                    true);// 添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                mess.setRespCode(5);
                mess.setRespMsg("服务调用失败");
            }else{
                mess = (HsicMessage) JSONUtils.toObjectWithGson(object.toString(),
                        HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mess.setRespCode(-4);
            mess.setRespMsg("接口异常："+e.toString());
        }
        return mess;
    }
    /*
分配订单:获取订单信息
 */
    public HsicMessage SearchSale(String DeviceID,String RequestData) {
        //			参数的含义，其一是参数的名字，
        //			其二是调用webService的方法名字
        //			其三是调用webService的方法要传入的参数

        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage

        hsicMess.setRespCode(-1);//设置RespCode属性

        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {

            if (!mHelper.checkNetworkStatus()) {

                hsicMess.setRespMsg("网络连接失败");

                hsicMess.setRespCode(-2);

                return hsicMess;
            }
        } catch (Exception e1) {

            e1.printStackTrace();

            hsicMess.setRespMsg("网络检查失败");

            hsicMess.setRespCode(-3);

            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "RequestData");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("SearchSale", propertyList,true);//添加参数调用recevieData方法返回结果
//            Log.e("aa",JSONUtils.toJsonWithGson(object));
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    /*获取站点下员工
	 */
    public HsicMessage SearchEmployee(String DeviceID,String RequestData) {
        //			参数的含义，其一是参数的名字，
        //			其二是调用webService的方法名字
        //			其三是调用webService的方法要传入的参数

        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "Station");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("SearchEmployee", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    /*
	分单时:更新分单数据
	 */
    public HsicMessage UpdateSale(String DeviceID,String RequestData) {
        //			参数的含义，其一是参数的名字，
        //			其二是调用webService的方法名字
        //			其三是调用webService的方法要传入的参数

        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage

        hsicMess.setRespCode(-1);//设置RespCode属性

        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {

            if (!mHelper.checkNetworkStatus()) {

                hsicMess.setRespMsg("网络连接失败");

                hsicMess.setRespCode(-2);

                return hsicMess;
            }
        } catch (Exception e1) {

            e1.printStackTrace();

            hsicMess.setRespMsg("网络检查失败");

            hsicMess.setRespCode(-3);

            return hsicMess;
        }

        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "RequestData");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);

        try {
            Object object = orderdeclData.recevieData("UpdateSale", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 预约订单
     * @param DeviceID
     * @param Station
     * @return
     */
    public HsicMessage SearchYYAssignSale(String DeviceID,String Station) {
        //			参数的含义，其一是参数的名字，
        //			其二是调用webService的方法名字
        //			其三是调用webService的方法要传入的参数

        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage

        hsicMess.setRespCode(-1);//设置RespCode属性

        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {

            if (!mHelper.checkNetworkStatus()) {

                hsicMess.setRespMsg("网络连接失败");

                hsicMess.setRespCode(-2);

                return hsicMess;
            }
        } catch (Exception e1) {

            e1.printStackTrace();

            hsicMess.setRespMsg("网络检查失败");

            hsicMess.setRespCode(-3);

            return hsicMess;
        }

        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "Station");// 参数名称
        hp_RequestData.put("propertyValue",Station);
        propertyList.add(hp_RequestData);

        try {
            Object object = orderdeclData.recevieData("SearchYYAssignSale", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }

        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 新增订单
     * @param DeviceID
     * @param RequestData
     * @return
     */
    public HsicMessage AddSale_fendan(String DeviceID,String RequestData){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "RequestData");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("AddSale_fendan", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    /**
     * 客户登录
     * @param DeviceID
     * @param CustomerID
     * @return
     */
    public HsicMessage CustomerLogin(String DeviceID,String CustomerID){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "CustomerID");// 参数名称
        hp_RequestData.put("propertyValue",CustomerID);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("CustomerLogin", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 下载街道信息
     * @param DeviceID
     * @return
     */
    public HsicMessage loadStreetInfo(String DeviceID){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        try {
            Object object = orderdeclData.recevieData("loadStreetInfo", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }
    /**
     * 下载客户类型信息
     * @param DeviceID
     * @return
     */
    public HsicMessage SearchCustomerTypeInfo(String DeviceID){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        try {
            Object object = orderdeclData.recevieData("SearchCustomerTypeInfo", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 用户注册
     * @param DeviceID
     * @param RequestData
     * @return
     */
    public HsicMessage RegisterCustomer(String DeviceID,String RequestData){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "RequestData");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("RegisterCustomer_shouji", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }

    /**
     * 客户信息维护
     * @param DeviceID
     * @param RequestData
     * @return
     */
    public HsicMessage UpdateCustomer(String DeviceID,String RequestData){
        HsicMessage hsicMess = new HsicMessage();//创建HsicMessage
        hsicMess.setRespCode(-1);//设置RespCode属性
        NetWorkHelper mHelper = new NetWorkHelper(mContext);//判断网络是否可用

        try {
            if (!mHelper.checkNetworkStatus()) {
                hsicMess.setRespMsg("网络连接失败");
                hsicMess.setRespCode(-2);
                return hsicMess;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            hsicMess.setRespMsg("网络检查失败");
            hsicMess.setRespCode(-3);
            return hsicMess;
        }
        //在调用webService时的基本参数设置，这个是在构建GetData对象时完成的
        List<Map<String, Object>> propertyList = new ArrayList<Map<String, Object>>();//准备参数
        HashMap<String, Object> hp_DeviceID = new HashMap<String, Object>();
        hp_DeviceID.put("propertyName", "DeviceID");// 参数名称
        hp_DeviceID.put("propertyValue",DeviceID);
        propertyList.add(hp_DeviceID);
        HashMap<String, Object> hp_RequestData = new HashMap<String, Object>();
        hp_RequestData.put("propertyName", "RequestData");// 参数名称
        hp_RequestData.put("propertyValue",RequestData);
        propertyList.add(hp_RequestData);
        try {
            Object object = orderdeclData.recevieData("RegisterCustomer_shouji", propertyList,true);//添加参数调用recevieData方法返回结果
            if (object.toString().equals("false")) {
                hsicMess.setRespCode(5);
                hsicMess.setRespMsg("服务调用失败");
            } else {
                hsicMess = (HsicMessage) JSONUtils.toObjectWithGson(
                        object.toString(), HsicMessage.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            hsicMess.setRespCode(-4);
            hsicMess.setRespMsg("接口异常："+e.toString());
        }
        return hsicMess;
    }



}
