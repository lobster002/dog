package com.pro.phone;

import pinyin.CharacterParser;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;

public class Other_SmsOrPhone extends Activity {

	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private String contcants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 取消标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.other_smsorphone);

		Intent intent = getIntent();
		contcants = intent.getStringExtra("cont");

		voice.voice(Other_SmsOrPhone.this, "手动通信，方式选择界面");
		mSpeech = voice.mSpeech;
		gesture(); // 手势
	}

	/*
	 * public void onWindowFocusChanged(boolean hasFocus) { if(hasFocus){
	 * unsolve_img=(ImageView)findViewById(R.id.unsolve_img);
	 * unsolve_img.setBackgroundResource(R.anim.unsolve_anim);
	 * unsolve_anim=(AnimationDrawable) unsolve_img.getBackground();
	 * unsolve_anim.start();
	 * phone_and_message_img=(ImageView)findViewById(R.id.phone_and_message_img
	 * );
	 * phone_and_message_img.setBackgroundResource(R.anim.phone_and__message_anim
	 * ); phone_and_message_anim=(AnimationDrawable)
	 * phone_and_message_img.getBackground(); phone_and_message_anim.start();
	 * 
	 * } }
	 */

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
				voice.voice(Other_SmsOrPhone.this, "上下滑动选择通信方式。如需帮助请长按屏幕");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingRight() { // 右边
				mSpeech.stop();
				Intent intent = new Intent();
				intent.setClass(Other_SmsOrPhone.this, OtherOrTool.class);
				startActivity(intent);
				Other_SmsOrPhone.this.finish();

			}

			@Override
			public void OnFlingUp() { // 上
				mSpeech.stop();
				phone(contcants);
			}

			@Override
			public void OnFlingDown() { // 下
				mSpeech.stop();
				Intent intent = new Intent();
				intent.setClass(Other_SmsOrPhone.this, Other_Sms.class);
				intent.putExtra("cont", contcants);
				startActivity(intent);
				Other_SmsOrPhone.this.finish();
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Other_SmsOrPhone.this, "手动通信，方式选择界面");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Other_SmsOrPhone.this, "向上滑动选择拨打电话，向下滑动选择发送短信。");
				mSpeech = voice.mSpeech;
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	// 通讯录遍历 for 拨打电话
	private void phone(String st) {
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
							call(phoneNumber);
							break;
						} while (phonesCur.moveToNext());

					}
					break;
				} else {
					continue;
				}

			} while (cur.moveToNext());
		}
		cur.close();
	}

	// 拨打电话
	private void call(String Phonenumber) {
		Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ Phonenumber));
		startActivity(myIntentDial);
		Intent intent = new Intent(Other_SmsOrPhone.this, OtherOrTool.class);
		startActivity(intent);
		Other_SmsOrPhone.this.finish();
	}

}
