package com.hsic.tmj.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.hsic.bll.GetBasicInfo;
import com.hsic.bll.SaveBasicInfo;
import com.hsic.constant.Constant;
import com.hsic.tmj.qppst.R;
import com.hsic.tmj.qppst.SetBlueToothActivity;
import android.content.DialogInterface.OnClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class AdvConfigFragment extends PreferenceFragment implements
		Preference.OnPreferenceChangeListener {
	private SharedPreferences mPreferences;
	private static String WebServer;
	private static String bluetooth;
	private static String WebServerPort;
	SharedPreferences device;
	private static String  FTPServer;
	private static String  FTPPort;
	private static ListPreference company;
	GetBasicInfo getBasicInfo;
	SaveBasicInfo saveBasicInfo;
	private static String endPointDefaultString,ftpServer;
	private static String version;
	private Context context;
	public Context getContext(){
		return getActivity();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.config);
		version = getActivity().getResources().getString(
				R.string.version_default);
		device = getActivity().getSharedPreferences("DeviceSetting", 0);
		bluetooth = device.getString("BlueToothAdd", "");
		mPreferences = getPreferenceScreen().getSharedPreferences();
		endPointDefaultString = getActivity().getResources().getString(
				R.string.web_default);
		WebServer = getActivity().getResources().getString(R.string.web_default);
		WebServerPort=getActivity().getResources().getString(R.string.web_port_default);
		ftpServer = getActivity().getResources()
				.getString(R.string.ftp_default);
		FTPPort=getActivity().getResources()
				.getString(R.string.ftp_port_default);
		findPreference("UnitCode").setSummary(mPreferences.getString("UnitCode", ""));
		getBasicInfo=new GetBasicInfo(getActivity());
		saveBasicInfo=new SaveBasicInfo(getActivity());
		company = (ListPreference) findPreference(getString(R.string.key_str));
		String temp = getBasicInfo.getCompanyCode();//获取公司名称
		CharSequence[] name = company.getEntries();
		CharSequence[] entries = company.getEntryValues();
		int l = entries.length;
		for (int i = 0; i < l; i++) {
			if (entries[i].equals(temp)) {
				company.setSummary(name[i]);
				company.setValueIndex(i);
				setIP(i);
				break;
			}

		}
		company.setOnPreferenceChangeListener(this);
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		super.getActivity().onKeyDown(keyCode, event);
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.getActivity().finish();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		String deviceid= Constant.DeviceID;
		String devieType=Constant.DeviceType;
		findPreference("devicetype").setSummary(devieType);
		findPreference("deviceid").setSummary(deviceid);
		findPreference("WebServer").setSummary(// ws
				mPreferences.getString("WebServer", WebServer));
		findPreference("WebServerPort").setSummary(// ws
				mPreferences.getString("WebServerPort", WebServerPort));
		findPreference("FTPServer").setSummary(// ws
				mPreferences.getString("FTPServer", FTPServer));//端口号
		findPreference("FTPPort").setSummary(// ws
				mPreferences.getString("FTPPort", FTPPort));//端口号
		String verString = getLocalVersionName(getActivity());

		findPreference("version").setSummary(verString);
		if (!bluetooth.equals("")) {
			findPreference("bluetooth").setSummary(bluetooth);
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
										 Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("bluetooth")) {
			// 在这里进行蓝牙搜索操作
//			Log.e("在这里发出蓝牙广播", "1");
			Intent i = new Intent(getActivity(), SetBlueToothActivity.class);
			startActivity(i);

		}
		if(preference.getKey().equals("version")){
//			UpDateVersionTask vat = new UpDateVersionTask(getActivity(), getActivity());
//			vat.execute();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public String getMac(Context context) {
		try {
			for (Enumeration<NetworkInterface> e = NetworkInterface
					.getNetworkInterfaces(); e.hasMoreElements();) {
				NetworkInterface item = e.nextElement();
				byte[] mac = item.getHardwareAddress();
				if (mac != null && mac.length > 0) {
					return new String(mac);
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(
						mOnSharedPreferenceChangeListener);
		bluetooth = device.getString("BlueToothAdd", "");
		if (!bluetooth.equals("")) {
			findPreference("bluetooth").setSummary(bluetooth);
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(
						mOnSharedPreferenceChangeListener);
	}

	private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			Preference pref = findPreference(key);
			if (pref instanceof EditTextPreference) {
				EditTextPreference etp = (EditTextPreference) pref;
				pref.setSummary(etp.getText());
			}
			if (pref.getKey().equals("bluetooth")) {
			}
		}
	};

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		getActivity().finish();

	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference instanceof ListPreference) {
			// 把preference这个Preference强制转化为ListPreference类型
			ListPreference listPreference = (ListPreference) preference;
			// 获取ListPreference中的实体内容
			CharSequence[] name = listPreference.getEntries();
			//获取ListPreference中的实体内容的下标值
			int index = listPreference.findIndexOfValue((String) newValue);
			// 把listPreference中的摘要显示为当前ListPreference的实体内容中选择的那个项目
			listPreference.setSummary(name[index]);
			saveBasicInfo.saveCompanyCode((String) newValue);
			setIP(index);

		}
		if(preference instanceof SwitchPreference){
			boolean isChecked = (Boolean) newValue;
			SwitchPreference sp = (SwitchPreference) preference;
			sp.setChecked(isChecked);
		}
		return true;
	}
	private void setIP(int index) {
		String ftp="";
		String ip="";
		String port="";
		String ftp_port="";
		switch (index) {
			case 0://
//				ip="47.100.12.195";
				ip="http://47.100.12.195/QPPST_HSWs/QPPSTWs.asmx";
				ftp="47.100.12.195";
				port="3391";
				ftp_port="22";
				setSummary("FTPServer",ftp);
				setSummary("WebServer",ip);
				setSummary("WebServerPort",port);
				setSummary("FTPPort",ftp_port);
				findPreference("WebServer").setSummary(// ws
						mPreferences.getString("WebServer", endPointDefaultString));
				findPreference("FTPServer").setSummary(// ws
						mPreferences.getString("FTPServer", ftpServer));
				findPreference("WebServerPort").setSummary(// ws
						mPreferences.getString("WebServerPort", port));//端口号
				findPreference("FTPPort").setSummary(// ws
						mPreferences.getString("FTPPort", ftp_port));//端口号
				saveBasicInfo.saveCompanyCode("1910");
				saveBasicInfo.saveCompanyName("上海恒申燃气发展有限公司");
				saveBasicInfo.saveCompanyPhone("58150000");
				break;
			case 1://
				ip="47.100.12.195";
				ftp="47.100.12.195";
				port="3392";
				ftp_port="22";
				setSummary("FTPServer",ftp);
				setSummary("WebServer",ip);
				setSummary("WebServerPort",port);
				setSummary("FTPPort",ftp_port);
				findPreference("WebServer").setSummary(// ws
						mPreferences.getString("WebServer", endPointDefaultString));
				findPreference("FTPServer").setSummary(// ws
						mPreferences.getString("FTPServer", ftpServer));
				findPreference("WebServerPort").setSummary(// ws
						mPreferences.getString("WebServerPort", port));//端口号
				findPreference("FTPPort").setSummary(// ws
						mPreferences.getString("FTPPort", ftp_port));//端口号
				saveBasicInfo.saveCompanyCode("1607");
				saveBasicInfo.saveCompanyName("上海金山燃气有限公司");
				saveBasicInfo.saveCompanyPhone("37917917");
				break;
			case 2://
				ip="47.100.12.195";
				ftp="47.100.12.195";
				port="3393";
				ftp_port="22";
				setSummary("FTPServer",ftp);
				setSummary("WebServer",ip);
				setSummary("WebServerPort",port);
				setSummary("FTPPort",ftp_port);
				findPreference("WebServer").setSummary(// ws
						mPreferences.getString("WebServer", endPointDefaultString));
				findPreference("FTPServer").setSummary(// ws
						mPreferences.getString("FTPServer", ftpServer));
				findPreference("WebServerPort").setSummary(// ws
						mPreferences.getString("WebServerPort", port));//端口号
				findPreference("FTPPort").setSummary(// ws
						mPreferences.getString("FTPPort", ftp_port));//端口号
				saveBasicInfo.saveCompanyCode("1707");
				saveBasicInfo.saveCompanyName("上海中信燃气有限公司");
				saveBasicInfo.saveCompanyPhone("57861777");
				break;
			case 3://
				ip="47.100.12.195";
				ftp="47.100.12.195";
				port="3308";
				ftp_port="22";
				setSummary("FTPServer",ftp);
				setSummary("WebServer",ip);
				setSummary("WebServerPort",port);
				setSummary("FTPPort",ftp_port);
				findPreference("WebServer").setSummary(// ws
						mPreferences.getString("WebServer", endPointDefaultString));
				findPreference("FTPServer").setSummary(// ws
						mPreferences.getString("FTPServer", ftpServer));
				findPreference("WebServerPort").setSummary(// ws
						mPreferences.getString("WebServerPort", port));//端口号
				findPreference("FTPPort").setSummary(// ws
						mPreferences.getString("FTPPort", ftp_port));//端口号
				saveBasicInfo.saveCompanyCode("2010");
				saveBasicInfo.saveCompanyName("上海奉贤交通液化气有限公司");
				saveBasicInfo.saveCompanyPhone("57575050");
				break;
			default:
				break;
		}
	}

	private void setSummary(String key,String text) {
		Preference pref = findPreference(key);
		pref.setSummary(text);
		if (pref instanceof EditTextPreference) {
			EditTextPreference etp = (EditTextPreference) pref;
			etp.setText(text);
//			findPreference(key).setSummary(// ws
//					mPreferences.getString(key, text));
		}
	}
	public static int getLocalVersion(Context ctx) {
		int localVersion = 0;
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionCode;
			Log.d("TAG", "当前版本号：" + localVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}
	/**
	 * 获取本地软件版本号名称
	 */
	public static String getLocalVersionName(Context ctx) {
		String localVersion = "";
		try {
			PackageInfo packageInfo = ctx.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(ctx.getPackageName(), 0);
			localVersion = packageInfo.versionName;
			Log.d("TAG", "当前版本名称：" + localVersion);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return localVersion;
	}
}
