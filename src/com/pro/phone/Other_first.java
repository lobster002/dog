package com.pro.phone;

import java.util.ArrayList;

import pinyin.CharacterParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.telephony.gsm.SmsManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.voice.Voiceshibie;
import com.pro.voice.Voiceshibie.shibiefrist;

public class Other_first extends Activity {
	private ImageView other_first_img;
	private AnimationDrawable other_first_anim;
	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private Voiceshibie vv; // 语音识别
	private int sum; // 统计分析错误次数

	private String Phonenumber = ""; // 拨打电话的号码
	private String Smsnumber = ""; // 发送短信的号码
	private String SmsString = "";

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.other_first);
		sum = 0;
		voice.voice(Other_first.this, "快捷通信助手");
		mSpeech = voice.mSpeech;
		gesture(); // 手势

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			other_first_img = (ImageView) findViewById(R.id.other_first_img);
			other_first_img
					.setBackgroundResource(R.anim.phone_message__voice_anim);
			other_first_anim = (AnimationDrawable) other_first_img
					.getBackground();
			other_first_anim.start();

		}
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

	// 手势
	private void gesture() {
		gh = new GestureHelper(this);
		gh.setOnFlingListener(new OnFlingListener() {
			@Override
			public void OnFlingLeft() {
				mSpeech.stop();
				if (!Phonenumber.equals("")) {
					call();
				} else if (!Smsnumber.equals("")) {
					fasong();
				} else
					vvv();
			}

			@Override
			public void OnFlingRight() {
				mSpeech.stop();
				if (!Phonenumber.equals("")) {
					Phonenumber = "";
					voice.voice(Other_first.this, "取消，请重新输入");
					mSpeech = voice.mSpeech;
				} else if (!Smsnumber.equals("")) {
					Smsnumber = "";
					SmsString = "";
					voice.voice(Other_first.this, "取消，请重新输入");
					mSpeech = voice.mSpeech;
				} else {
					Intent intent = new Intent(Other_first.this,
							OtherOrTool.class);
					startActivity(intent);
					Other_first.this.finish();
				}
			}

			@Override
			public void OnFlingUp() {
				mSpeech.stop();
				if (!Phonenumber.equals("")) {
					call();
				} else if (!Smsnumber.equals("")) {
					fasong();
				} else
					vvv();
			}

			@Override
			public void OnFlingDown() {
				mSpeech.stop();
				Intent intent = new Intent(Other_first.this,
						Other_contacts.class);
				startActivity(intent);
				Other_first.this.finish();
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Other_first.this, "快捷通信助手");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Other_first.this,
						"当前为快捷通信助手语音模式界面，向左滑动选择语音录入拨打电话或发送短信，向下切换至手动录入模式，向右滑动返回主界面");
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
				System.out.println(ss);
				analyze(ss);
			}
		});
	}

	// 分析语音结果
	private void analyze(String res) {
		int flag = 0;
		if (res.contains("电话") || res.contains("拨通") || res.contains("拨打"))
			flag = 1;
		if (res.contains("短信"))
			flag = 2;

		if (flag == 1)
			phone(res);
		else if (flag == 2) {
			if (res.contains("说") && res.split("说").length > 1) {
				SmsString = res.split("说")[1];
				Sms(res);
			} else {
				voice.voice(Other_first.this, "对不起，识别错误");
				mSpeech = voice.mSpeech;
				sum++;
			}
		}

		if (flag == 0) {
			if (sum >= 3) {
				mSpeech.stop();
				voice.voice(Other_first.this, "您已经多次语音识别错误，建议采用手动模式");
				mSpeech = voice.mSpeech;
			} else {
				mSpeech.stop();
				voice.voice(Other_first.this, "识别错误");
				mSpeech = voice.mSpeech;
				sum++;
			}
		}

	}

	// 通讯录遍历 for 拨打电话
	private void phone(String st) {
		Cursor cur = this.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED asc");

		// 循环遍历
		if (cur.moveToFirst()) {
			CharacterParser cha = new CharacterParser();
			int idColumn = cur
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			boolean flag = false;
			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);

				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);
				// cha.getSelling(disPlayName).equals(cha.getSelling(st.substring(0,
				// st.length()-1)))||disPlayName.equals(st.substring(0,
				// st.length()-1))
				if (st.contains(disPlayName)
						|| cha.getSelling(st).contains(
								cha.getSelling(disPlayName))) {
					flag = true;
					// 获取联系人的电话号码
					Cursor phonesCur = this.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + contactId, null, null);
					if (phonesCur.moveToFirst()) {
						do {
							// 遍历所有的电话号码
							String phoneType = phonesCur
									.getString(phonesCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							String phoneNumber = phonesCur
									.getString(phonesCur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							// 自己的逻辑处理代码
							voice.voice(Other_first.this, "为你找到联系人，"
									+ disPlayName + ",向左确认拨通，向右取消");
							Phonenumber = phoneNumber;
							mSpeech = voice.mSpeech;
							break;
						} while (phonesCur.moveToNext());

					}
					break;
				} else {
					continue;
				}

			} while (cur.moveToNext());
			if (!flag) {
				mSpeech.stop();
				voice.voice(Other_first.this, "对不起，未找到相关联系人");
				mSpeech = voice.mSpeech;
			}
		}
		cur.close();
	}

	// 拨打电话
	private void call() {
		Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ Phonenumber));
		startActivity(myIntentDial);
		Intent intent = new Intent(Other_first.this, OtherOrTool.class);
		startActivity(intent);
		Other_first.this.finish();
	}

	// 通讯录遍历 for 发短信
	private void Sms(String st) {
		/*
		 * Cursor cur = this.getContentResolver().query(
		 * Contacts.People.CONTENT_URI, null, null, null,
		 * Contacts.People.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		 * 
		 * <!-- Old_Method -->
		 */
		Cursor cur = this.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		// 循环遍历
		if (cur.moveToFirst()) {
			CharacterParser cha = new CharacterParser();
			int idColumn = cur.getColumnIndex(Contacts.People._ID);
			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			boolean flag = false;
			do {
				// 获得联系人的ID号
				String contactId = cur.getString(idColumn);

				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);
				if (st.contains(disPlayName)) {
					flag = true;
					// 获取联系人的电话号码
					/*
					 * Cursor phonesCur = this.getContentResolver().query(
					 * 
					 * Contacts.Phones.CONTENT_URI, null,
					 * Contacts.Phones.PERSON_ID + "=" + contactId, null, null);
					 * <!-- Old_Method -->
					 */

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
							mSpeech.stop();
							voice.voice(Other_first.this, "为你找到短信接收人，"
									+ disPlayName + ",识别短信内容为，" + SmsString
									+ ",向左发送短信，向右取消");
							Smsnumber = phoneNumber;
							mSpeech = voice.mSpeech;
							break;
						} while (phonesCur.moveToNext());

					}
					break;
				} else {
					continue;
				}

			} while (cur.moveToNext());
			if (!flag) {
				mSpeech.speak("没有找到联系人：" + st, TextToSpeech.QUEUE_FLUSH, null);
				SmsString = "";
			}
		}
		cur.close();
	}

	// 发送短信
	private void fasong() {
		SmsManager manager_sms = SmsManager.getDefault();// 得到短信管理器
		// 由于短信可能较长，故将短信拆分
		ArrayList<String> texts = manager_sms.divideMessage(SmsString);
		for (String text : texts) {
			manager_sms.sendTextMessage(Smsnumber, null, text, null, null);// 分别发送每一条短信
		}
		voice.voice(Other_first.this, "短信发送成功！");
		Intent intent = new Intent();
		intent.setClass(Other_first.this, OtherOrTool.class);

		startActivity(intent);
		Other_first.this.finish();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
