package com.hsic.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hsic.bean.HsicMessage;
import com.hsic.listener.AddressListener;
import com.hsic.web.WebServiceHelper;

public class AddressAsyncTask extends AsyncTask<String, Void, HsicMessage>{
	private Context context;
	private ProgressDialog dialog;
	WebServiceHelper wb;
	AddressListener l;
	public AddressAsyncTask(Context context,AddressListener l){
		this.context = context;
		dialog = new ProgressDialog(context);
		this.l=l;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		dialog.setMessage("正在下载街道信息");
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected HsicMessage doInBackground(String... params) {
		// TODO Auto-generated method stub

		WebServiceHelper wsh = new WebServiceHelper(context);
		HsicMessage mess = wsh.GetStreetInfo(params[0]);
		return mess;
	}

	@Override
	protected void onPostExecute(HsicMessage result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		dialog.setCancelable(true);
		dialog.dismiss();
		l.AddressListenerEnd(result);

	}

}
