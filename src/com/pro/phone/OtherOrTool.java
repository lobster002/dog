package com.pro.phone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.internal.telephony.ITelephony;
import com.pgyersdk.crash.PgyCrashManager;
import com.pro.lbs.Lbs;
import com.pro.tool.Tool_Frist;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;

public class OtherOrTool extends Activity {

	TextToSpeech mSpeech; // 语音辅助
	Time time = new Time("GMT+8");
	private ITelephony iTelephony;

	private Voice voice = new Voice();
	private View topWindow;

	private boolean XiangLing = false;// 正在响铃
	private ImageView main_view_bg;
	private ImageView up_img;
	private ImageView down_img;

	private AnimationDrawable main_view_anim;
	private LinearLayout animlayout;
	private RelativeLayout mainview;
	private GestureHelper gh; // 手势判定

	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences sp = getPreferences(MODE_PRIVATE);// 取得对象，模式为私有
		String uid = sp.getString("panduan", null);// 查询 "panduan"
		if (uid == null) {// 如果为空
			SharedPreferences.Editor editor = sp.edit();// 取得编辑对象
			editor.putString("panduan", "");// 储存
			editor.commit();// 保存更改

			// 短信存起来
			Editor sharedata = getSharedPreferences("miss", 0).edit();
			sharedata.putString("missmessages", "");
			sharedata.putString("missphone", "");
			sharedata.commit();

			Intent intent = new Intent(OtherOrTool.this,
					TestWeiXinWhatsNewActivity.class);
			startActivity(intent);// 跳转到欢迎界面
			OtherOrTool.this.finish();// 关闭该Activity
		} else {
			voice.voice(this, "欢迎进入老人助手!");
			mSpeech = voice.mSpeech;
		}

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 窗体充满屏幕
		setContentView(R.layout.otherortool);
		
		String pid = "169758e1c9faf65a360d6eea61aeef43";
		PgyCrashManager.register(this,pid);

		mainview = (RelativeLayout) findViewById(R.id.mainview);
		animlayout = (LinearLayout) findViewById(R.id.animLayout);
		up_img = (ImageView) findViewById(R.id.up_img);
		down_img = (ImageView) findViewById(R.id.down_img);
		main_view_bg = (ImageView) findViewById(R.id.main_view_bg);
		main_view_bg.setBackgroundResource(R.anim.main_view_anim);
		main_view_anim = (AnimationDrawable) main_view_bg.getBackground();

		main_view_bg.post(new Runnable() {
			@Override
			public void run() {
				main_view_anim.start();
			}
		});

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		MyPhoneCallListener myPhoneCallListener = new MyPhoneCallListener();
		tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
		topWindow = new CallPhoneView(this);

		zhixing();

		gesture(); // 手势
	}

	// 手势
	private void gesture() {
		gh = new GestureHelper(this);
		gh.setOnFlingListener(new OnFlingListener() {
			@Override
			public void OnFlingLeft() { // 左边
				mSpeech.stop();
				if (zhixing()) {
					voice.voice(OtherOrTool.this, "无效动作，上、下滑动为选择项");
					mSpeech = voice.mSpeech;
				}

			}

			@Override
			public void OnFlingRight() { // 右边
				mSpeech.stop();
				if (zhixing()) {
					voice.voice(OtherOrTool.this, "无效动作，上、下滑动为选择项");
					mSpeech = voice.mSpeech;
				}
			}

			@Override
			public void OnFlingUp() { // 上
				mSpeech.stop();
				if (zhixing()) {
					runUpAnimation();
				}
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				if (zhixing()) {
					voice.voice(OtherOrTool.this, "老人助手主界面");
					mSpeech = voice.mSpeech;
				}
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				SharedPreferences sharedata = getSharedPreferences(
						"yijianhujiu", 0);
				String name = sharedata.getString("name", null);
				if (name.equals("abc")) {
					mSpeech.stop();
					voice.voice(OtherOrTool.this, "未设置亲友号码，建议查询自己位置后拨打电话");
					mSpeech = voice.mSpeech;
				} else {
					new Lbs().fasong(name, OtherOrTool.this);
					mSpeech.stop();
					voice.voice(OtherOrTool.this, "已经将您的求救短信发送出去，请在原地等待");
					mSpeech = voice.mSpeech;
				}

			}

			@Override
			public void OnFlingDown() {
				runDownAnimation();
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	private void runUpAnimation() {
		main_view_bg.setVisibility(View.GONE);
		animlayout.setVisibility(View.VISIBLE);
		mainview.setBackgroundResource(R.drawable.mian_bg);
		Animation TopOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_top);
		Animation BottomOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_bottom);
		up_img.setAnimation(TopOutAnimation);
		down_img.setAnimation(BottomOutAnimation);
		TopOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// mainRLayout.setBackgroundColor(R.color.bgColor);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				up_img.setVisibility(View.GONE);
				down_img.setVisibility(View.GONE);
				Intent intent = new Intent(OtherOrTool.this, Other_first.class);
				OtherOrTool.this.startActivity(intent);
				OtherOrTool.this.finish();
				overridePendingTransition(R.anim.zoom_out_enter,
						R.anim.zoom_out_exit);
			}
		});
	}

	private void runDownAnimation() {
		main_view_bg.setVisibility(View.GONE);
		animlayout.setVisibility(View.VISIBLE);
		mainview.setBackgroundResource(R.drawable.mian_bg);
		Animation TopOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_top);
		Animation BottomOutAnimation = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.translate_bottom);
		up_img.setAnimation(TopOutAnimation);
		down_img.setAnimation(BottomOutAnimation);
		TopOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				// mainRLayout.setBackgroundColor(R.color.bgColor);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				up_img.setVisibility(View.GONE);
				down_img.setVisibility(View.GONE);
				//
				Intent intent = new Intent(OtherOrTool.this, Tool_Frist.class);
				OtherOrTool.this.startActivity(intent);
				OtherOrTool.this.finish();
				overridePendingTransition(R.anim.zoom_out_enter,
						R.anim.zoom_out_exit);
			}
		});
	}

	// 锁屏
	private void suoping() {
		showTopWindow(topWindow);
	}

	// 清除锁屏
	private void clear() {
		clearTopWindow(topWindow);
	}

	// 锁死home，back按钮 不添加编辑按钮
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			break;
		case KeyEvent.KEYCODE_HOME:
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			break;
		// 音量大 电量
		case KeyEvent.KEYCODE_VOLUME_UP:
			break;
		}

		return false;
	}

	// 震动效果方法
	private void virbate() {
		Vibrator vibrator = (Vibrator) OtherOrTool.this
				.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	// 监控来电
	public class MyPhoneCallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_OFFHOOK: // 通话中
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃
				time.setToNow();
				int minute = time.minute;
				int hour = time.hour;
				if (hour >= 0 && hour <= 16)
					hour += 8;
				else
					hour -= 17;
				String time = hour + "点" + minute + "分 ";

				Editor sharedata = getSharedPreferences("call", 0).edit();
				sharedata.putString(
						"callname",
						getContactNameFromPhoneBook(OtherOrTool.this,
								incomingNumber));
				sharedata.putString("callnumber", incomingNumber);
				sharedata.putString("calltime", time);
				sharedata.commit();

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
				suoping();
				break;
			case TelephonyManager.CALL_STATE_IDLE: // 空闲

				SharedPreferences sp1 = getSharedPreferences("call", 0);
				String uid1 = sp1.getString("callname", null);
				if (sp1 != null && uid1 != null) {

					// 未接电话存起来
					SharedPreferences sharedata1 = getSharedPreferences("call",
							0);
					String ssname = sharedata1.getString("callname", null);
					String ssnumber = sharedata1.getString("callnumber", null);
					String sstime = sharedata1.getString("calltime", null);
					if (!ssname.equals("")) {
						// 取出以前的数据
						SharedPreferences sharedata2 = getSharedPreferences(
								"call", 0);
						String str = sharedata1.getString("callname", null);
						str += "%" + ssname + "," + ssnumber + "," + sstime;
						// 存进新数据
						Editor sharedata3 = getSharedPreferences("miss", 0)
								.edit();
						sharedata3.putString("missphone", str);
						sharedata3.commit();
					}

					// 重置
					Editor sharedata4 = getSharedPreferences("call", 0).edit();
					sharedata4.putString("callname", "");
					sharedata4.putString("callnumber", "");
					sharedata4.putString("calltime", "");
					sharedata4.commit();
				}
				clear();
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

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

		TelephonyManager telMgr = (TelephonyManager) this
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

	// 电源键监听
	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 退出程序...
				if (XiangLing) {
					mSpeech.stop();
					voice.voice(OtherOrTool.this, "来电已经拒听！");
					mSpeech = voice.mSpeech;
					closePhone();
					XiangLing = false;
				}
			}
		}
	};

	// 通讯录模块 进入一个电话号码返回一个人姓名
	public String getContactNameFromPhoneBook(Context context, String phoneNum) {

		String contactName = "陌生人";
		if (phoneNum != null) {
			ContentResolver cr = context.getContentResolver();
			Cursor pCur = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
					new String[] { phoneNum }, null);
			if (pCur.moveToFirst()) {
				contactName = pCur
						.getString(pCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				pCur.close();
			}
		}
		return contactName;
	}

	// 让view显示在最顶端
	public void showTopWindow(View view) {
		// window管理器
		WindowManager windowManager = (WindowManager) getApplicationContext()
				.getSystemService(WINDOW_SERVICE);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		// params.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置全屏显示 可以根据自己需要设置大小
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		params.height = WindowManager.LayoutParams.MATCH_PARENT;
		// 设置显示初始位置 屏幕左上角为原点
		params.x = 0;
		params.y = 0;
		// topWindow显示到最顶部
		windowManager.addView(view, params);
	}

	// 清除view方法
	/**
	 * @Method: clearTopWindow
	 * @Description: 移除最顶层view
	 */
	public void clearTopWindow(View view) {
		if (view != null && view.isShown()) {
			WindowManager windowManager = (WindowManager) getApplicationContext()
					.getSystemService(WINDOW_SERVICE);
			windowManager.removeView(view);
		}
	}

	// 查看是否应该跳转到miss
	private boolean zhixing() {
		SharedPreferences sharedata = getSharedPreferences("miss", 0);
		String strmess = sharedata.getString("missmessages", null);
		String strphone = sharedata.getString("missphone", null);

		String[] mess = strmess.split("%");
		String[] phone = strphone.split("%");

		int boo = mess.length + phone.length;

		if (boo > 2) {
			Intent intent = new Intent(OtherOrTool.this, MissPhone.class);
			startActivity(intent);
			OtherOrTool.this.finish();
			return false;
		}
		return true;

	}

}
