package com.hsic.task;//package com.hsic.task;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Handler;
//import android.os.Message;
//import android.preference.PreferenceManager;
//
//import com.hsic.fxqpmanager.R;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Administrator on 2019/3/4.
// */
//
//public class DBVersionTask extends
//        AsyncTask<Void, Void, Map<String, String>> {
//    VersionHelper versionHelper = null;
//    Context context;
//    Activity activity;
//    String endPointDefaultString = "",  port = "";
//    String db_version = "", IP="";
//    public DBVersionTask(Context context, Activity activity){
//        this.activity = activity;
//        this.context = context;
//        versionHelper = new VersionHelper(context);
//        endPointDefaultString = context.getResources().getString(
//                R.string.web_port_default);
//        db_version= context.getResources().getString(
//                R.string.data_version);
//        port = context.getResources().getString(R.string.web_port_default);
//
//    }
//    @Override
//    protected Map<String, String> doInBackground(Void... params) {
//        // TODO Auto-generated method stub
//        Map<String, String> map = new HashMap<String, String>();
//        try {
//            IP= PreferenceManager.getDefaultSharedPreferences(context)
//                    .getString("WebServer",
//                            endPointDefaultString);
//            String path = "http://"+
//                    PreferenceManager.getDefaultSharedPreferences(context)
//                            .getString("WebServer",
//                                    endPointDefaultString)+":"+PreferenceManager.getDefaultSharedPreferences(context)
//                    .getString("WebServerPort", port)+"/apk/AppVersion.xml";
//            URL url = new URL(path);
//            HttpURLConnection httpConnection = (HttpURLConnection) url
//                    .openConnection();
//            httpConnection.setConnectTimeout(5000);
//            httpConnection.setRequestMethod("GET");
//            httpConnection.setReadTimeout(5000);
//            int responseCode = httpConnection.getResponseCode();
//
//            if (responseCode == 200) {
//                InputStream in = httpConnection.getInputStream();
//                String[] versionInfo = XmlParser.getVersionInfo(in);
//                if (versionInfo != null && versionInfo.length > 0) {
//                    map.put("newVersion", versionInfo[0]);
//                    map.put("file_real_path", versionInfo[1]);
//                    map.put("file_MD5", versionInfo[2]);
//                    map.put("min_version", versionInfo[3]);
//                    map.put("version_explain", versionInfo[4]);
//                }
//            }
//        } catch (Exception ex) {
//            return map;
//        }
//        return map;
//    }
//
//    @Override
//    protected void onPostExecute(Map<String, String> result) {
//        // TODO Auto-generated method stub
//        super.onPostExecute(result);
//        if (result.size() > 0) {
////            Message message = Message.obtain();
////            message.obj = result;
////            handler.sendMessage(message);
//        }
//    }
//    @SuppressLint("HandlerLeak")
//    public Handler handler = new Handler() {
//
//        public void handleMessage(Message msg) {
//
//            @SuppressWarnings("unchecked")
//            Map<String, String> map = (Map<String, String>) (msg.obj);
//            String dbVersion=map.get("newVersion");
//            String newVersion = map.get("newVersion");
////            path = map.get("file_real_path");//http://10.123.16.81/SHLPGPhoneWs/apk/QPManagerMoblieV52.apk
////            file_MD5 = map.get("file_MD5");
////            min_version = map.get("min_version");
////            version_explain = map.get("version_explain");
//            int older = versionHelper.getVersionCode(context);
//            String olderVersion = String.valueOf(older);
//            float dbverion = Float.parseFloat(newVersion);
//            float localVersion = Float.parseFloat(db_version);
//            if(newVersion!=null){
//                if(!newVersion.equals("")){
//                   if(dbverion>localVersion){
//                       //更新本地基本配置
//                   }
//                }
//            }
//
//        };
//    };
//}
