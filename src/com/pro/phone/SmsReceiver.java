package com.pro.phone;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.text.format.Time;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	private String st1 = "";
	Time time = new Time("GMT+8");

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			this.abortBroadcast();

			MediaPlayer mediaPlayer01 = new MediaPlayer();
			mediaPlayer01 = MediaPlayer.create(context, R.raw.message);
			mediaPlayer01.start();

			StringBuffer sb = new StringBuffer();
			String sender = null;
			String content = null;
			String sendtime = null;
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] mges = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage mge : mges) {
					sb.append("发件人：" + mge.getDisplayOriginatingAddress());
					st1 = mge.getDisplayOriginatingAddress();
					sb.append("内容：" + mge.getMessageBody());
					sender = mge.getDisplayOriginatingAddress();
					content = mge.getMessageBody();
					time.setToNow();
					int minute = time.minute;
					int hour = time.hour;
					if (hour >= 0 && hour <= 16)
						hour += 8;
					else
						hour -= 17;
					sendtime = hour + "点" + minute + "分 ";
					st1 = st1.substring(3, st1.length());

					String sst = getContactNameFromPhoneBook(context, st1);

					// 姓名，电话号码，时间，内容
					String cunqilai = sst + "," + st1 + "," + sendtime + ","
							+ content;
					// 自己储存起来
					SharedPreferences sharedata = context.getSharedPreferences(
							"miss", 0);
					String str = sharedata.getString("missmessages", null);

					Editor sharedata1 = context.getSharedPreferences("miss", 0)
							.edit();
					sharedata1.putString("missmessages", "%" + cunqilai);
					sharedata1.commit();
					Toast.makeText(context, cunqilai, Toast.LENGTH_LONG).show();

				}
				Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public String getContactNameFromPhoneBook(Context context, String phoneNum) {
		String contactName = phoneNum;
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
		return contactName;
	}
}