package com.pro.phone;

import java.util.ArrayList;

import pinyin.CharacterParser;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.voice.Voiceshibie;
import com.pro.voice.Voiceshibie.shibiefrist;

public class Other_Sms extends Activity {
	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private String contcants; // 联系人
	private String cont = ""; // 内容
	private Voiceshibie vv; // 语音识别

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 取消标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.other_sms);

		Intent intent = getIntent();
		contcants = intent.getStringExtra("cont");

		voice.voice(Other_Sms.this, "手动通信，短信录入界面");
		mSpeech = voice.mSpeech;
		gesture(); // 手势
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
		}
		return false;
	}

	// 手势
	private void gesture() {
		gh = new GestureHelper(this);
		gh.setOnFlingListener(new OnFlingListener() {
			@Override
			public void OnFlingLeft() { // 左边
				mSpeech.stop();
				if (cont.equals("")) {
					vvv();
				} else {
					sms(contcants);
				}
			}

			@Override
			public void OnFlingRight() { // 右边
				mSpeech.stop();
				if (!cont.equals("")) {
					cont = "";
					voice.voice(Other_Sms.this, "取消，请向左滑动，重新输入");
					mSpeech = voice.mSpeech;

				} else {
					Intent intent = new Intent();
					intent.setClass(Other_Sms.this, OtherOrTool.class);
					startActivity(intent);
					Other_Sms.this.finish();
				}

			}

			@Override
			public void OnFlingUp() { // 上
				mSpeech.stop();
				voice.voice(Other_Sms.this, "左右滑动，选择发送短信。如需帮助请长按屏幕。");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingDown() { // 下
				mSpeech.stop();
				voice.voice(Other_Sms.this, "左右滑动，选择发送短信。如需帮助请长按屏幕。");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Other_Sms.this, "手动通信，短信录入界面");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Other_Sms.this,
						"向左滑动为短信录入，如需更改短信内容，向右滑动取消即可。再次向左滑动，将短信发出。");
				mSpeech = voice.mSpeech;

			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	// 语音识别
	private void vvv() {
		vv = new Voiceshibie(this);
		vv.setVoiceListener(new shibiefrist() {

			@Override
			public void zhixing(String ss) {
				mSpeech.stop();
				voice.voice(Other_Sms.this, "短信内容为," + ss + ",再次向左滑动发送");
				mSpeech = voice.mSpeech;
				cont = ss;
			}
		});
	}

	// 通讯录遍历 for 拨打电话
	private void sms(String st) {
		Cursor cur = this.getContentResolver().query(
				Contacts.People.CONTENT_URI, null, null, null,
				Contacts.People.DISPLAY_NAME + " COLLATE LOCALIZED ASC");

		// 循环遍历
		if (cur.moveToFirst()) {
			CharacterParser cha = new CharacterParser();
			int idColumn = cur.getColumnIndex(Contacts.People._ID);
			int displayNameColumn = cur
					.getColumnIndex(Contacts.People.DISPLAY_NAME);
			boolean flag = false;
			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);

				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);
				if (st.contains(disPlayName)) {
					flag = true;
					// 获取联系人的电话号码
					Cursor phonesCur = this.getContentResolver().query(
							Contacts.Phones.CONTENT_URI, null,
							Contacts.Phones.PERSON_ID + "=" + contactId, null,
							null);
					if (phonesCur.moveToFirst()) {
						do {
							// 遍历所有的电话号码
							String phoneType = phonesCur
									.getString(phonesCur
											.getColumnIndex(Contacts.PhonesColumns.TYPE));
							String phoneNumber = phonesCur
									.getString(phonesCur
											.getColumnIndex(Contacts.PhonesColumns.NUMBER));
							// 自己的逻辑处理代码
							fasong(phoneNumber, cont);
							break;
						} while (phonesCur.moveToNext());

					}
					break;
				} else {
					continue;
				}

			} while (cur.moveToNext());
			if (!flag)
				mSpeech.speak("没有找到联系人：" + st, TextToSpeech.QUEUE_FLUSH, null);
		}
		cur.close();
	}

	// 发送短信
	private void fasong(String st, String tt) {
		SmsManager manager_sms = SmsManager.getDefault();// 得到短信管理器
		// 由于短信可能较长，故将短信拆分
		ArrayList<String> texts = manager_sms.divideMessage(tt);
		for (String text : texts) {
			manager_sms.sendTextMessage(st, null, text, null, null);// 分别发送每一条短信
		}
		voice.voice(Other_Sms.this, "发送成功");
		Intent intent = new Intent();
		intent.setClass(Other_Sms.this, OtherOrTool.class);
		startActivity(intent);
		Other_Sms.this.finish();

	}

}
