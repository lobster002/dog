package com.pro.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		 Intent i = new Intent(context, AlterActivity.class); 
	     Bundle bundleRet = new Bundle(); 
	     bundleRet.putString("STR_CALLER", ""); 
	     i.putExtras(bundleRet); 
	     i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	     context.startActivity(i); 
	}
}
