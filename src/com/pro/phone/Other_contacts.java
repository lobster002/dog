package com.pro.phone;

import pinyin.CharacterParser;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;

public class Other_contacts extends Activity {

	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private TextView te;
	private char letter = 'a'; // 当前字母
	private String contacts = "";// 当前联系人名字
	private int flagname = 0; // 当前联系人的次序
	private String stnumber = "";

	private String[][] tongxun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 取消标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.other_contacts);

		te = (TextView) findViewById(R.id.zi);
		te.setText("   " + letter);
		tongxun = new String[30][50];
		tongxunlu();
		voice.voice(Other_contacts.this, "手动通信，联系人选择界面");
		mSpeech = voice.mSpeech;
		gesture(); // 手势
	}

	/*
	 * public void onWindowFocusChanged(boolean hasFocus) { if(hasFocus){
	 * phone_message_voice_img
	 * =(ImageView)findViewById(R.id.phone_message_voice_img);
	 * phone_message_voice_img
	 * .setBackgroundResource(R.anim.phone_message__voice_anim);
	 * phone_message_voice_anim=(AnimationDrawable)
	 * phone_message_voice_img.getBackground();
	 * phone_message_voice_anim.start();
	 * other_contacts_img=(ImageView)findViewById(R.id.other_contacts_img);
	 * other_contacts_img.setBackgroundResource(R.anim.other_contacts_anim);
	 * other_contacts_anim=(AnimationDrawable)
	 * other_contacts_img.getBackground(); other_contacts_anim.start();
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
				if (stnumber.equals("")) {
					if (contacts.equals("")) {
						if (tongxun[letter - 'a'][0] == null) {
							mSpeech.speak("没有相关联系人", TextToSpeech.QUEUE_FLUSH,
									null);
						} else {
							flagname = 0;
							contacts = tongxun[letter - 'a'][0];
							te.setText(contacts);
							voice.voice(Other_contacts.this, contacts);
							mSpeech = voice.mSpeech;
						}
					} else {
						stnumber = "yes";
						voice.voice(Other_contacts.this, "是否选" + contacts
								+ "作为联系人");
						mSpeech = voice.mSpeech;
					}
				} else {
					// 跳转
					Intent intent = new Intent();
					intent.setClass(Other_contacts.this, Other_SmsOrPhone.class);
					intent.putExtra("cont", contacts);
					startActivity(intent);
					Other_contacts.this.finish();
				}
			}

			@Override
			public void OnFlingRight() { // 右边
				mSpeech.stop();
				if (contacts.equals("")) {
					if (stnumber.equals("")) {
						Intent intent = new Intent();
						intent.setClass(Other_contacts.this, OtherOrTool.class);
						startActivity(intent);
						Other_contacts.this.finish();
					} else {
						stnumber = "";
						voice.voice(Other_contacts.this, "取消，请重新输入姓名");
						mSpeech = voice.mSpeech;
					}
				} else {
					contacts = "";
					stnumber = "";
					voice.voice(Other_contacts.this, "取消，请重新输入字母");
					mSpeech = voice.mSpeech;
				}

			}

			@Override
			public void OnFlingUp() { // 上
				mSpeech.stop();
				if (contacts.equals("")) {
					if (letter - 'a' > 0)
						letter--;
					te.setText("   " + letter);
					voice.voice(Other_contacts.this, letter + "");
					mSpeech = voice.mSpeech;
				} else {
					if (flagname > 0) {
						flagname--;
						contacts = tongxun[letter - 'a'][flagname];
					}
					te.setText(contacts);
					voice.voice(Other_contacts.this, contacts);
					mSpeech = voice.mSpeech;
				}
			}

			@Override
			public void OnFlingDown() { // 下
				mSpeech.stop();
				if (contacts.equals("")) {
					if (letter - 'z' < 0)
						letter++;
					te.setText("   " + letter);
					voice.voice(Other_contacts.this, letter + "");
					mSpeech = voice.mSpeech;
				} else {
					if (tongxun[letter - 'a'][flagname + 1] != null) {
						flagname++;
						contacts = tongxun[letter - 'a'][flagname];
					}
					te.setText(contacts);
					voice.voice(Other_contacts.this, contacts + "");
					mSpeech = voice.mSpeech;
				}
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Other_contacts.this, "手动通信，联系人选择界面");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Other_contacts.this,
						"当前为快捷通信助手手动模式界面，上下滑动为筛选联系人姓名首字母操作，向左滑动为确认操作，向右滑动返回上一界面。");
				mSpeech = voice.mSpeech;

			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	private void tongxunlu() {
		/*
		 * Cursor cur = this.getContentResolver().query(
		 * Contacts.People.CONTENT_URI, null, null, null,
		 * Contacts.People.DISPLAY_NAME +" COLLATE LOCALIZED ASC");
		 */
		/*
		 * Cursor cur = this.getContentResolver().query(
		 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
		 * ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME +
		 * " COLLATE LOCALIZED asc");
		 */
		Cursor cur = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");

		// 循环遍历
		if (cur.moveToFirst()) {
			CharacterParser cha = new CharacterParser();
			// int idColumn = cur.getColumnIndex(Contacts.People._ID);
//			int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
			// int displayNameColumn =
			// cur.getColumnIndex(Contacts.People.DISPLAY_NAME);
			int displayNameColumn = cur
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
			do {
				// 获得联系人的ID号
//				String contactId = cur.getString(idColumn);

				// 获得联系人姓名
				String disPlayName = cur.getString(displayNameColumn);

				int aa;
				for (aa = 0; aa < 50; aa++) {
					if (tongxun[cha.getSelling(disPlayName).charAt(0) - 'a'][aa] == null)
						break;
				}
				tongxun[cha.getSelling(disPlayName).charAt(0) - 'a'][aa] = disPlayName;
			} while (cur.moveToNext());
		}
		cur.close();
	}

}