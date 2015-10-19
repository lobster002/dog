package com.pro.tool;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.pro.lbs.Lbs;
import com.pro.phone.OtherOrTool;
import com.pro.phone.R;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.voice.Voiceshibie;
import com.pro.voice.Voiceshibie.shibiefrist;

public class Tool_Frist extends Activity {
	private ImageView voice_img;
	private AnimationDrawable voice_anim;

	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	private Voiceshibie vv;
	// 天气url
	private String url = "http://www.weather.com.cn/data/cityinfo/101070101.html";
	Time time = new Time("GMT+8");
	private String dianliang = "";

	// 语义理解对象（文本到语义）。
	private TextUnderstander mTextUnderstander;

	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tool_frist);
		voice_img = (ImageView) findViewById(R.id.voice_img);
		voice_img.setBackgroundResource(R.anim.voice_anim);
		voice_anim = (AnimationDrawable) voice_img.getBackground();
		voice_img.post(new Runnable() {
			@Override
			public void run() {
				voice_anim.start();
			}
		});

		mTextUnderstander = new TextUnderstander(this, textUnderstanderListener);
		voice.voice(Tool_Frist.this, "个性语音助手平台");
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
				vvv();
			}

			@Override
			public void OnFlingRight() {
				mSpeech.stop();
				Intent intent = new Intent(Tool_Frist.this, OtherOrTool.class);
				startActivity(intent);
				Tool_Frist.this.finish();
			}

			@Override
			public void OnFlingUp() {
				mSpeech.stop();
				vvv();

			}

			@Override
			public void OnFlingDown() {
				mSpeech.stop();
				vvv();
			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Tool_Frist.this, "个性语音助手平台");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(
						Tool_Frist.this,
						"个性语音助手平台，向右滑动退出平台，其他任意滑动后均可以输入语音指令，如需了解平台功能，请语音询问，温馨提醒，想要正常启动第三方应用，所输入的语音指令应包含，应用，二字，以及完整的应用名");
				mSpeech = voice.mSpeech;
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

	// 语音结果分析
	private void analyze(String res) {

		if ((res.contains("为我") || res.contains("做") || res.contains("干"))
				&& res.contains("什么")) {
			mSpeech.stop();
			voice.voice(
					Tool_Frist.this,
					"我可以为您，查天气，听歌曲，定闹钟，简单计算器，讲笑话，听新闻，定位置，看黄历，查时间，看电量，向亲友求救，常识问答，提供第三方的语音服务，以及第三方应用服务");
			mSpeech = voice.mSpeech;
		} else if (res.contains("应用")) {
			mSpeech.stop();
			if (res.contains("音乐")) {
				voice.voice(Tool_Frist.this, "为您打开第三方应用，盲人音乐播放器，请稍候");
				mSpeech = voice.mSpeech;
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {

						Intent intent = getPackageManager()
								.getLaunchIntentForPackage(
										"com.blind.blindmusic");
						startActivity(intent);
					}
				}, 5000);
			} else {
				voice.voice(Tool_Frist.this, "找不到相关应用");
				mSpeech = voice.mSpeech;
			}
		} else if (res.contains("天气")) {
			GetWeather();
		}

		else if (res.contains("歌") || res.contains("音乐")) {
			Intent intent = new Intent(Tool_Frist.this, Tool_music.class);
			startActivity(intent);
		} else if (res.contains("闹钟")) {
			Intent intent = new Intent(Tool_Frist.this, Tool_alarm.class);
			startActivity(intent);
		} else if (res.contains("笑话")) {
			Intent intent = new Intent(Tool_Frist.this, Tool_joke.class);
			startActivity(intent);
		} else if (res.contains("新闻")) {
			Intent intent = new Intent(Tool_Frist.this, Tool_news.class);
			startActivity(intent);
		} else if (res.contains("位置") || res.contains("哪儿")) {
			new Lbs().fasong("lbs", Tool_Frist.this);
		} else if (res.contains("呼救") || res.contains("求救")
				|| res.contains("救助")) {
			SharedPreferences sharedata = getSharedPreferences("yijianhujiu", 0);
			String name = sharedata.getString("name", null);
			if (name.equals("abc")) {
				mSpeech.stop();
				voice.voice(Tool_Frist.this, "未设置亲友号码，建议查询自己位置后拨打电话");
				mSpeech = voice.mSpeech;
			} else {
				new Lbs().fasong(name, Tool_Frist.this);
				mSpeech.stop();
				voice.voice(Tool_Frist.this, "已经将您的求救短信发送出去，请在原地等待");
				mSpeech = voice.mSpeech;
			}
		} else if (res.contains("黄历")) {
			mSpeech.stop();
			voice.voice(
					Tool_Frist.this,
					"今日是，公历,2014年7月25日星期五,狮子座,农历,2014年六月二十九，回历，1435年9月27日，冲煞，冲兔，煞东，岁次，甲午年辛未月丁酉日，五行，山下火，满执位");
			mSpeech = voice.mSpeech;
			// Web we = new Web();
			// we.GetData("http://122.207.182.59:8080/dog/servlet/selectdate?id=1",Tool_Frist.this);
		} else if (res.contains("时间")) {
			time.setToNow();
			int minute = time.minute;
			int hour = time.hour;
			if (hour >= 0 && hour <= 16)
				hour += 8;
			else
				hour -= 17;
			String ss = "主人，当前时间为：" + hour + "点" + minute + "分 ";
			voice.voice(Tool_Frist.this, ss);
			mSpeech = voice.mSpeech;
		} else if (res.contains("电量")) {
			batteryLevel();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					batteryLevel();
					voice.voice(Tool_Frist.this, dianliang);
					mSpeech = voice.mSpeech;
				}
			}, 200);

		} else {
			understandText(res);
		}

	}

	/***************************** 电量 ******************************************/

	private void batteryLevel() {

		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int rawlevel = intent.getIntExtra("level", -1);// 获得当前电量
				int scale = intent.getIntExtra("scale", -1);
				// 获得总电量
				int level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				if (level > 0.7)
					dianliang = "主人，还剩电量:百分之" + level + ",电量还很充足！";
				else
					dianliang = "主人，还剩电量:百分之" + level + ",电量不是很充足了！";

			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);

	}

	/***************************** 电量 ******************************************/

	/************************** 语音识别 **************************************/
	private void vvv() {
		vv = new Voiceshibie(this);
		vv.setVoiceListener(new shibiefrist() {

			@Override
			public void zhixing(String ss) {
				analyze(ss);
			}
		});
	}

	/************************** 语音识别 **************************************/

	/*********************** 天气 **********************************/

	public void GetWeather() {
		connServerForResult(url);
	}

	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			String va = data.getString("Result");
			// 使用json解析
			parseJson(va);
		}
	};

	// 获取数据
	private void connServerForResult(final String strUrl) {
		// 重新开线程 获取网络数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// HttpGet对象
					HttpGet httpRequest = new HttpGet(strUrl);
					// HttpClient对象
					HttpClient httpClient = new DefaultHttpClient();
					// 获得HttpResponse对象
					HttpResponse httpResponse = httpClient.execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// 取得返回的数据
						String Result = EntityUtils.toString(httpResponse
								.getEntity());
						// 延迟等待 用handler
						Message msg = new Message();
						Bundle date = new Bundle();
						date.putString("Result", Result);
						msg.setData(date);
						msg.what = 0;
						handle.sendMessage(msg);
					}
				} catch (ClientProtocolException e) {

					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	// 解析数据

	// 一个Json数据解析
	private void parseJson(String strResult) {
		try {
			JSONObject jsonObj = new JSONObject(strResult)
					.getJSONObject("weatherinfo");
			String name = jsonObj.getString("city");
			// int id = jsonObj.getInt("cityid");
			String hig = jsonObj.getString("temp1");
			String low = jsonObj.getString("temp2");
			String weather = jsonObj.getString("weather");
			String ss = "城市名：" + name + "，，天气：" + weather + "，，最高温度：" + low
					+ "，，最低温度：" + hig;
			voice.voice(Tool_Frist.this, ss);
			mSpeech = voice.mSpeech;
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	/*************************** 天气 **********************************/

	/*************************** 调侃 *********************************/

	public void understandText(String st) {
		System.out.println(st + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();

		} else {
			mTextUnderstander.understandText(st, textListener);

		}
	}

	/**
	 * 初始化监听器（文本到语义）。
	 */
	private InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code == ErrorCode.SUCCESS) {
				// findViewById(R.id.text_understander).setEnabled(true);
			}
		}
	};

	private TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
						String text = result.getResultString();
						if (!TextUtils.isEmpty(text)) {
							UnderstanderParseJson(text);
						}
					} else {

					}
				}
			});
		}

		@Override
		public void onError(SpeechError error) {

		}
	};

	// 一个Json数据解析
	private void UnderstanderParseJson(String strResult) {
		try {
			JSONObject jsonObj = new JSONObject(strResult)
					.getJSONObject("answer");
			String ss = jsonObj.getString("text");
			voice.voice(Tool_Frist.this, ss);
			mSpeech = voice.mSpeech;
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	/*************************** 调侃 *********************************/

	@Override
	protected void onResume() {
		super.onResume();
	}

}
