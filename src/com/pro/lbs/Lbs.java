package com.pro.lbs;

import java.util.ArrayList;

import android.app.Activity;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.widget.RadioGroup;

import com.pro.voice.Voice;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApi;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiListener;
import com.tencent.tencentmap.lbssdk.TencentMapLBSApiResult;

public class Lbs {
	private String number;
	private String lbs = "";
	private String dateString;

	RadioGroup mEditReqGeoType;
	RadioGroup mEditReqLevel;
	int mReqType, mReqGeoType, mReqLevel;
	LocListener mListener;
	PowerManager.WakeLock mWakeLock;
	Activity act;

	private Voice voice = new Voice();

	// 腾讯lbs定位
	// 一件呼救
	public void fasong(String st, Activity act) {
		this.act = act;
		if (st.equals("lbs")) {
			lbs = st;
			mReqGeoType = TencentMapLBSApi.GEO_TYPE_GCJ02;
			mReqLevel = TencentMapLBSApi.LEVEL_NAME;
			mListener = new LocListener(mReqGeoType, mReqLevel, 1);
			int req = TencentMapLBSApi.getInstance().requestLocationUpdate(
					act.getApplicationContext(), mListener);
			TencentMapLBSApi.getInstance().setGPSUpdateInterval(5000);
		} else {
			number = st;
			mReqGeoType = TencentMapLBSApi.GEO_TYPE_GCJ02;
			mReqLevel = TencentMapLBSApi.LEVEL_NAME;
			mListener = new LocListener(mReqGeoType, mReqLevel, 1);
			int req = TencentMapLBSApi.getInstance().requestLocationUpdate(
					act.getApplicationContext(), mListener);
			TencentMapLBSApi.getInstance().setGPSUpdateInterval(5000);
		}

	}

	public class LocListener extends TencentMapLBSApiListener {
		public LocListener(int reqGeoType, int reqLevel, int reqDelay) {
			super(reqGeoType, reqLevel, reqDelay);
		}

		@Override
		public void onLocationUpdate(TencentMapLBSApiResult locRes) {
			String res = locResToString(locRes);
			if (lbs.equals("lbs")) {
				voice.voice(act, "您现在的位置是，" + res);
			} else {
				dateString = "我需要帮助！我现在的位置是：" + res;
				SmsManager manager_sms = SmsManager.getDefault();// 得到短信管理器
				// 由于短信可能较长，故将短信拆分
				ArrayList<String> texts = manager_sms.divideMessage(dateString);
				for (String text : texts) {
					manager_sms.sendTextMessage(number, null, text, null, null);// 分别发送每一条短信
				}
			}
			TencentMapLBSApi.getInstance().removeLocationUpdate();
		}

		@Override
		public void onStatusUpdate(int state) {
			String strState = null;
			switch (state) {
			case TencentMapLBSApi.STATE_GPS_DISABLED:
				strState = "Gps Disabled";
				break;
			case TencentMapLBSApi.STATE_GPS_ENABLED:
				strState = "Gps Enabled";
				break;
			case TencentMapLBSApi.STATE_WIFI_DISABLED:
				strState = "Wifi Disabled";
				break;
			case TencentMapLBSApi.STATE_WIFI_ENABLED:
				strState = "Wifi Enabled";
				break;
			}
			dateString = strState;
		}
	}

	public String locResToString(TencentMapLBSApiResult res) {
		StringBuilder strBuilder = new StringBuilder();

		if (res.Info == TencentMapLBSApi.LEVEL_NAME) {
			strBuilder.append(res.Address).append("   ").append(res.Name);
		}
		return strBuilder.toString();
	}
}
