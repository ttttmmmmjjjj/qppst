package com.hsic.bll;

import android.content.Context;
import android.content.Intent;

;import com.hsic.tmj.qppst.AdvConfigActivity;

public class ActivityUtil {
	public static void JumpToAdvConfig(Context context) {
		Intent intent = new Intent(context, AdvConfigActivity.class);
		context.startActivity(intent);
	}
}
