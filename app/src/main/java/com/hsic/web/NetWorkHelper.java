package com.hsic.web;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * ������ģ��
 */
public class NetWorkHelper {
	
	private Context mContext;

	private static NetWorkHelper mInstance = null;

	public NetWorkHelper(Context context) {
		super();
		mContext = context;
	}

	public static synchronized NetWorkHelper getInstance(Context context) {
		
		if (mInstance == null) {
			
			mInstance = new NetWorkHelper(context);
			
		}
		return mInstance;
	}

	/*
	 * �������״̬
	 */
	public boolean checkNetworkStatus() throws Exception {
		
		boolean result = false;
		
		ConnectivityManager cm = (ConnectivityManager) mContext
			.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		boolean isWifiAvail = ni.isAvailable();
		
		boolean isWifiConn = ni.isConnected();
		
		if (isWifiAvail && isWifiConn) {
			
			result = true;
			
		} else {
			
			ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			boolean isMobileAvail = ni.isAvailable();
			
			boolean isMobileConn = ni.isConnected();
			
			if (isMobileAvail && isMobileConn) {
				result = true;
			}

		}
		
		return result;
	}
	
}
