package com.pro.tool;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pro.phone.R;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.voice.Voiceshibie;
import com.pro.voice.Voiceshibie.shibiefrist;

public class Tool_alarm extends Activity {
	private TextView timeshow;
	private Calendar calendar;
	private int current_hour, current_minute;
	private int hour, minute;

	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private Voiceshibie vv;

	private String time_str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tool_alarm);
		// 实例化组件
		timeshow = (TextView) findViewById(R.id.timeshow_tv);
		voice.voice(Tool_alarm.this, "向左滑动，设置闹钟提醒");
		mSpeech = voice.mSpeech;
		gesture(); // 手势
	}

	// 手势
	private void gesture() {
		gh = new GestureHelper(this);
		gh.setOnFlingListener(new OnFlingListener() {
			@Override
			public void OnFlingLeft() {
				mSpeech.stop();
				vvv();
			}

			@Override
			public void OnFlingRight() {
				mSpeech.stop();
				voice.voice(Tool_alarm.this, "退出");
				mSpeech = voice.mSpeech;
				Tool_alarm.this.finish();
			}

			@Override
			public void OnFlingUp() {
				mSpeech.stop();
				voice.voice(Tool_alarm.this, "无效动作,如需帮助请长按屏幕");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingDown() {
				mSpeech.stop();
				voice.voice(Tool_alarm.this, "无效动作,如需帮助请长按屏幕");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Tool_alarm.this, "闹钟设置界面");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Tool_alarm.this,
						"向左滑动，语音设置闹钟提醒，支持时间点提醒和时间段提醒，向右滑动退出，其他动作无效");
				mSpeech = voice.mSpeech;
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	private void vvv() {
		vv = new Voiceshibie(this);
		vv.setVoiceListener(new shibiefrist() {

			@Override
			public void zhixing(String ss) {
				time_str = StringToNumber.bulidTextZHToALB(ss);
				initAlarm();
			}
		});
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

	// 设置闹钟
	private void setAlarm() {
		/* 获取闹钟对象 */

		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		/* 通过广播来监听闹钟事件 */
		Intent intent = new Intent(Tool_alarm.this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				Tool_alarm.this, 0, intent, 0);

		/* 设置闹钟响铃的时间以及方式 */
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				pendingIntent);
		/* 设置闹钟重复响铃的时间 */
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
				+ (1 * 1000), (24 * 60 * 60 * 1000), pendingIntent);
	}

	// 创建实例化方法
	private void initAlarm() {

		// 获取日历对象
		calendar = Calendar.getInstance();
		// 获取当前时间
		calendar.setTimeInMillis(System.currentTimeMillis());
		// 获取当前的小时数和分钟数
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		setTime();

	}

	private void getTime(int hour, int minute) {
		// 获取要设置的闹钟时间
		// setTime();
		String tmpS;
		if (isTomorrow(time_str)
				|| (calendar.getTimeInMillis() <= System.currentTimeMillis())) {
			calendar.set(Calendar.DAY_OF_YEAR,
					calendar.get(Calendar.DAY_OF_YEAR) + 1);
			tmpS = "闹钟设置的时间为：" + "明天" + StringToNumber.NumberToString(hour)
					+ "点" + StringToNumber.NumberToString(minute) + "分";
			// String tmpS = "闹钟设置的时间为：" +"明天"+ format(hour)
			// + ":" + format(minute);
			timeshow.setText(tmpS);
		} else {
			tmpS = "闹钟设置的时间为：" + StringToNumber.NumberToString(hour) + "点"
					+ StringToNumber.NumberToString(minute) + "分";
			// String tmpS = "闹钟设置的时间为：" + format(hour)
			// + ":" + format(minute);

			timeshow.setText(tmpS);
		}

		setAlarm();
		voice.voice(Tool_alarm.this, tmpS);
		mSpeech = voice.mSpeech;

	}

	// 设置闹钟时间
	private void setTime() {
		int[] mnumber = getNumber(time_str);
		int n = getDurtion(time_str);
		if (n > 0) {
			timeshow.setText("");
			if (n == 1) {
				hour = hour + mnumber[0];
				if (hour >= 24) {
					calendar.set(Calendar.DAY_OF_YEAR,
							calendar.get(Calendar.DAY_OF_YEAR) + 1);
					hour = hour - 24;
					if (mnumber[1] > 0) {
						minute = minute + mnumber[1];
						System.out.println("###################youmingming"
								+ minute);
						if (minute >= 60) {
							hour += 1;
							minute = minute - 60;

						}
					}
					timeshow.append("闹钟的时间设置为：" + "明天");
				} else {
					if (mnumber[1] > 0) {
						minute = minute + mnumber[1];
						System.out.println("###################youmingming"
								+ minute);
						if (minute >= 60) {
							hour += 1;
							minute = minute - 60;

						}
					}
					timeshow.append("闹钟的时间设置为：");
				}

			} else {
				minute = minute + mnumber[0];
				System.out.println("###################youmingming" + minute);
				if (minute >= 60) {
					hour += 1;
					minute = minute - 60;

				}

				timeshow.append("闹钟的时间设置为：");
			}
			calendar.set(Calendar.HOUR_OF_DAY, hour);// 设置当前小时
			calendar.set(Calendar.MINUTE, minute);// 设置当前分钟
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			timeshow.append(StringToNumber.NumberToString(hour) + "点"
					+ StringToNumber.NumberToString(minute) + "分");
			mSpeech.stop();
			voice.voice(Tool_alarm.this, timeshow.getText().toString());
			mSpeech = voice.mSpeech;
			// timeshow.append( format(hour)+":"+ format(minute));
			setAlarm();
		} else {
			if (mnumber[2] > 0) {
				current_hour = mnumber[0] + 12;
				current_minute = mnumber[1];
			} else {
				current_hour = mnumber[0];
				current_minute = mnumber[1];
			}
			if (mnumber[0] == 0) {
				timeshow.setText("您没有设置时间");
				mSpeech.stop();
				voice.voice(Tool_alarm.this, "您没有设置时间，请重新设置");
				mSpeech = voice.mSpeech;
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, current_hour);// 设置当前小时
				calendar.set(Calendar.MINUTE, current_minute);// 设置当前分钟
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				getTime(current_hour, current_minute);
				setAlarm();
			}
		}

	}

	/* 从字符串中读取数字 */
	private int[] getNumber(String str) {
		boolean existence = false;
		int[] number = new int[3];
		number[0] = number[1] = number[2] = 0;
		String[] durtion = new String[3];
		durtion[0] = "下午";
		durtion[1] = "晚上";
		durtion[2] = "夜里";

		Pattern p = Pattern.compile("[0-9\\.]+");
		Matcher m = p.matcher(str);
		int i = -1;
		while (m.find() && (i < 1)) {
			i++;
			number[i] = Integer.parseInt(m.group());
			// System.out.print(m.group()+",");
		}
		// 如果出现半小时，两小时，两分钟这个关键字的话，九零number【0】=2，number[0]=2,number[1]=30；
		String[] otherkey = { "两小时", "半小时", "两分钟" };
		Boolean iskey = false;
		int key = 0;
		while ((key < otherkey.length) && !iskey) {
			Pattern pattern = Pattern.compile(otherkey[key]);
			Matcher matcher = pattern.matcher(str);
			while (matcher.find() && !iskey) {
				if ("两小时".equals(matcher.group())) {
					number[0] = 2;
					iskey = true;
				}
				if ("半小时".equals(matcher.group())) {
					number[1] = 30;
					iskey = true;
				}
				if ("两分钟".equals(matcher.group())) {
					number[0] = 2;
					iskey = true;
				}

			}
			key++;
		}
		System.out.println("*************######" + number[0] + "另一个为：****"
				+ number[1]);
		// 判断时候有下午晚上这些关键字
		int j = 0;
		while (j < durtion.length && !existence) {
			Pattern p1 = Pattern.compile(durtion[j]);
			Matcher m1 = p1.matcher(str);
			while (m1.find() && !existence) {
				if (!"".equals(m1.group())) {
					number[2] = 1;
					existence = true;
				}

			}
			j++;
		}

		return number;
	}

	/*
	 * 判断是否有“小时”，“分钟”这些时间段 如果有“小时”这个时间段的话，返回1，如果有“分钟”则返回2
	 */
	private int getDurtion(String str) {
		int d = 0;
		int j = 0;
		String[] durtion = new String[2];
		durtion[0] = "小时";
		durtion[1] = "分钟后";

		boolean existence = false;
		while (j < durtion.length && !existence) {
			Pattern p1 = Pattern.compile(durtion[j]);
			Matcher m1 = p1.matcher(str);
			while (m1.find() && !existence) {
				if ("小时".equals(m1.group())) {// 如果出现"小时后的时间段"，则在当前小时上加上获得的小时数
					d = 1;
					existence = true;
				}
				if ("分钟后".equals(m1.group())) {// 如果出现"分钟后"，则在当前小时上加上获得的分钟数
					d = 2;
					existence = true;
				}

			}
			j++;
		}
		return d;
	}

	// 判断是否明天这样的字样的方法
	private boolean isTomorrow(String str) {
		boolean istomorrow = false;
		Pattern p = Pattern.compile("明天");
		Matcher m = p.matcher(str);
		while (m.find() && !istomorrow) {
			if (!"".equals(m.group())) {
				istomorrow = true;
			}
		}
		return istomorrow;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
