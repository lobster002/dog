package com.pro.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.android.internal.telephony.ITelephony;
import com.pro.voice.Voice_teshu;

@SuppressLint("CommitPrefEdits")
public class CallPhoneView extends View {

	private ITelephony iTelephony;
	private Context context;
	private Voice_teshu voice = new Voice_teshu();
	private String name = "";

	public CallPhoneView(Context context) {
		super(context);
		this.context = context;
		this.setBackgroundResource(R.drawable.callphone);

	}

	int starx = 0;
	int stary = 0;
	int endx = 0;
	int endy = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			starx = (int) event.getX();
			stary = (int) event.getY();
			return true;
		case MotionEvent.ACTION_MOVE:

			return true;
		case MotionEvent.ACTION_UP:
			endx = (int) event.getX();
			endy = (int) event.getY();
			zhixing();
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void zhixing() {
		// 静音
		toggleRingerMute(context);

		int x = endx - starx;
		int y = endy - stary;

		if (x == 0 && y == 0) {
			SharedPreferences sharedata = context.getSharedPreferences("call",
					0);
			name = sharedata.getString("callname", null);
			voice.voice(context, name + "的电话");
		} else if (x < 0) {
			if (y == 0 || Math.abs(x) > Math.abs(y)) {// 左
				del();
				voice.voice(context, "为您接通！");
				jiePhone();

			} else if (y < 0) {// 上
				SharedPreferences sharedata = context.getSharedPreferences(
						"call", 0);
				name = sharedata.getString("callname", null);
				voice.voice(context, name + "的电话");
			} else {// 下
				SharedPreferences sharedata = context.getSharedPreferences(
						"call", 0);
				name = sharedata.getString("callname", null);
				voice.voice(context, name + "的电话");
			}
		} else {
			if (y == 0 || Math.abs(x) > Math.abs(y)) { // 右
				del();
				voice.voice(context, "为您挂断！");
				closePhone();

			} else if (y < 0) { // 上
				SharedPreferences sharedata = context.getSharedPreferences(
						"call", 0);
				name = sharedata.getString("callname", null);
				voice.voice(context, name + "的电话");
			} else { // 下
				SharedPreferences sharedata = context.getSharedPreferences(
						"call", 0);
				name = sharedata.getString("callname", null);
				voice.voice(context, name + "的电话");
			}
		}
	}

	// 清除数据库
	private void del() {
		Editor sharedata = context.getSharedPreferences("call", 0).edit();
		sharedata.putString("callname", "");
		sharedata.putString("callnumber", "");
		sharedata.putString("calltime", "");
		sharedata.commit();
	}

	// 挂电话
	private void closePhone() {
		try {
			getTelephony();
			iTelephony.endCall();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// 得到电话管理器
	public void getTelephony() {

		TelephonyManager telMgr = (TelephonyManager) context
				.getSystemService(Service.TELEPHONY_SERVICE);

		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr,
					(Object[]) null);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/*
	 * //自动挂断 public static void endPhone(Context c,TelephonyManager tm) { try {
	 * ITelephony iTelephony; Method getITelephonyMethod =
	 * TelephonyManager.class .getDeclaredMethod("getITelephony", (Class[])
	 * null); getITelephonyMethod.setAccessible(true); iTelephony = (ITelephony)
	 * getITelephonyMethod.invoke(tm, (Object[]) null); // 挂断电话
	 * iTelephony.endCall(); } catch (Exception e) { e.printStackTrace(); } }
	 */

	// 接听电话
	private void jiePhone() {
		answerRingingCalls(context);
	}

	public synchronized static void answerRingingCalls(Context context) {
		try {
			if (android.os.Build.VERSION.SDK_INT >= 16) {
				Intent meidaButtonIntent = new Intent(
						Intent.ACTION_MEDIA_BUTTON);
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
				context.sendOrderedBroadcast(meidaButtonIntent,
						"android.permission.CALL_PRIVILEGED");
			} else {
				Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent1.putExtra("state", 1);
				localIntent1.putExtra("microphone", 1);
				localIntent1.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent1,
						"android.permission.CALL_PRIVILEGED");
				Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent2.putExtra("android.intent.extra.KEY_EVENT",
						localKeyEvent1);
				context.sendOrderedBroadcast(localIntent2,
						"android.permission.CALL_PRIVILEGED");
				Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent3.putExtra("android.intent.extra.KEY_EVENT",
						localKeyEvent2);
				context.sendOrderedBroadcast(localIntent3,
						"android.permission.CALL_PRIVILEGED");
				Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent4.putExtra("state", 0);
				localIntent4.putExtra("microphone", 1);
				localIntent4.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent4,
						"android.permission.CALL_PRIVILEGED");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 来电静音
	 */
	private static int previousMuteMode = -1;

	private void toggleRingerMute(Context context) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (previousMuteMode == -1) {
			previousMuteMode = am.getRingerMode();
			am.setRingerMode(0);
		}
		am.setRingerMode(previousMuteMode);
		previousMuteMode = -1;
	}

}
