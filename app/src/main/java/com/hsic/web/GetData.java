package com.hsic.web;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hsic.bll.GetBasicInfo;
import com.hsic.tmj.qppst.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/22.
 */

public class GetData {
    private static String nameSpaceDefaultString = "http://tempuri.org/";

    private static String header = "http://";

    private static String end = "/QPPST_HSWs/QPPSTWs.asmx";

    private String nameSpace; // 命名空间

    private String endPoint; // EndPoint 接入网点

    private int overTime;// 接口超时时间
    GetBasicInfo getBasicInfo;

    public GetData(Context context) {// 构造函数，为webService的调用设置属性参数
        getBasicInfo=new GetBasicInfo(context);
        this.nameSpace = nameSpaceDefaultString;
        String serverDefault=context.getResources().getString(R.string.web_default);
        String server = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("WebServer", serverDefault);
        String portDefault=context.getResources().getString(R.string.web_port_default);
        String port=PreferenceManager.getDefaultSharedPreferences(context)
                .getString("WebServerPort", portDefault);

        String moddle = server+":"+port;
//        this.endPoint = header +moddle+ "/QPPSTPhone"+getBasicInfo.getCompanyCode() +"/QPPSTWebService.asmx";//配置地址
        String outTime = context.getResources().getString(R.string.outTime);
        try {
            overTime = Integer.parseInt(outTime);
        } catch (Exception e) {
            overTime = 60;
        }
        this.endPoint =server;

    }

    public Object recevieData(String methodName, List<Map<String, Object>> propertyList, Boolean isSimpleRet) {
        // 返回的查询结果
        String soapAction = nameSpace + methodName;
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(this.nameSpace, methodName);
        // 设置需要返回请求对象的参数
        for (Map<String, Object> map : propertyList) {

            request.addProperty(map.get("propertyName").toString(),

                    map.get("propertyValue"));

        }
//		Log.e("传入参数===", JSONUtils.toJsonWithGson(propertyList));
        // 设置soap的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        // 设置是否调用的是dotNet开发的
        envelope.dotNet = true;

        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(endPoint,
                overTime * 1000);
        // web service请求
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // 得到返回结果
        try {
            if (!isSimpleRet) {

                SoapObject o = (SoapObject) envelope.bodyIn;

                if (o != null) {

                    return o;
                }
            } else {
                return envelope.getResponse();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public KvmSerializable parseToObject(SoapObject soapObject,
                                         Class objectClass) throws InstantiationException,
            IllegalAccessException {

        KvmSerializable result = (KvmSerializable) objectClass.newInstance();

        int numOfAttr = result.getPropertyCount();

        for (int i = 0; i < numOfAttr; i++) {
            PropertyInfo info = new PropertyInfo();
            result.getPropertyInfo(i, null, info);
            // 处理property不存在的情况
            try {
                Object object = soapObject.getProperty(info.name);
                result.setProperty(i, object);
            } catch (Exception e) {
                continue;
            }
        }
        return result;
    }
}
