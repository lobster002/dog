package com.pro.start;

import com.pro.phone.OtherOrTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Start extends BroadcastReceiver {
	static final String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(action_boot)) {
			Intent ootStartIntent = new Intent(context, OtherOrTool.class);
			ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(ootStartIntent);
		}

	}

}
