package com.hsic.appupdate.net;

import android.util.Log;
import android.util.Xml;

import com.hsic.appupdate.NetCallBack;
import com.hsic.appupdate.bean.Version;
import com.hsic.appupdate.utils.MD5Util;
import com.hsic.qpmanager.util.json.JSONUtils;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager implements INetManager{
    @Override
    public void get(final String url, final NetCallBack callBack) {
        try {
            URL request = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) request.openConnection();
            httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestMethod("GET");
            httpConnection.setReadTimeout(5000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream in = httpConnection.getInputStream();
                final String rsp = getVersionInfo(in);
                if (callBack != null) {
                    callBack.success(rsp);
                }

            }else{
                callBack.failed("文件下载失败，返回码:"+responseCode);
            }
        } catch (Exception ex) {
            if (callBack != null) {
                callBack.failed(ex.toString());
            }
        }

    }

    @Override
    public void download(final String url,final File targetFile, final INetDownloadCallBack callBack,Object tag,final String check) {
        if(!targetFile.exists())
        {
            targetFile.getParentFile().mkdirs();
        }
        try {
            //下载apk
            InputStream inputStream = null;
            OutputStream outputStream = null;
            URL request = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) request.openConnection();
            httpConnection.setConnectTimeout(5000);
            httpConnection.setReadTimeout(5000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == 200) {
                inputStream = httpConnection.getInputStream();
                outputStream = new FileOutputStream(targetFile);

                final int contentLength = httpConnection.getContentLength();
                int len = 0;
                int sum = 0;
                byte[] bytes = new byte[1024*12];
                while ((len = inputStream.read(bytes))!= -1 )
                {
                    sum += len;
                    outputStream.write(bytes,0,len);
                    outputStream.flush();
                    final int sumLen = sum ;
                    callBack.progress((int) ((sumLen * 1.0f/contentLength*100)) +"%" );

                }
                targetFile.setExecutable(true,false);
                targetFile.setReadable(true,false);
                targetFile.setWritable(true,false);
                //apk验证
                String fileMD5String = MD5Util.getFileMD5String(targetFile);//md5
                if(check.equals(fileMD5String)){
                    callBack.success(targetFile);
                }else{
                    Log.e("aaaa","文件校验失败");
                    callBack.failed("文件校验失败");
                }

            }else{
                callBack.failed("文件下载失败，返回码:"+responseCode);
            }
        } catch (Exception ex) {
            if (callBack != null) {
                callBack.failed("文件下载失败，异常");
            }
        }
    }

    @Override
    public void cancel(Object tag) {
        
    }


    /**
     * 解析XML文件
     */
    public static String  getVersionInfo(InputStream in) throws Exception {
        Version v=new Version();
        String json = "";
        if (in != null) {
            XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(in, "gb2312");
            int type = xpp.getEventType();

            while (type != XmlPullParser.END_DOCUMENT) {

                switch (type) {

                    case XmlPullParser.START_TAG:
                        if ("update".equals(xpp.getName())) {
                        } else if ("version".equals(xpp.getName())) {
                            String version = xpp.nextText();
                            v.setVersionCode(version);
                        } else if ("file_real_path".equals(xpp.getName())) {
                            String file_real_path = xpp.nextText();
                            v.setFile_real_path(file_real_path);

                        } else if ("file_MD5".equals(xpp.getName())) {
                            String file_MD5 = xpp.nextText();
                            v.setFile_MD5(file_MD5);
                        }
                        if(xpp.getName()!=null){
                            if ("min_version".equals(xpp.getName())) {
                                String min_version = xpp.nextText();
                                v.setMin_version(min_version);
                            }
                        }else{
                            v.setMin_version("");
                        }
                        if(xpp.getName()!=null){
                            if ("version_explain".equals(xpp.getName())) {
                                String version_explain = xpp.nextText();
                                v.setVersion_explain(version_explain);
                            }
                        }else{
                            v.setVersion_explain("");
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }

                type = xpp.next();
            }

        }
        json= JSONUtils.toJsonWithGson(v);
        return json;
    }
}
