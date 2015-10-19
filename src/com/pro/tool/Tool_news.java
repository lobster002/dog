package com.pro.tool;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import com.pro.phone.R;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;
import com.pro.web.Web;

public class Tool_news extends Activity {
	private GestureHelper gh; // 手势判定
	Voice voice = new Voice();
	TextToSpeech mSpeech;
	Web web = new Web();
	private int flag = 0; // 当前的条数
	private String[] arr;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tool_news);
		gesture(); // 手势
		arr = new String[] {
				"新华网7月19日，记者李建敏，杨光，正在对阿根廷进行国事访问的国家主席习近平19日参观了共和国庄园，考察阿根廷农牧业，了解阿根廷农牧民文化特色。",
				"中新网7月20日电 据美联社报道，乌克兰紧急情况部一名发言人20日说，马航MH17航班坠毁后，在坠机现场发现的遇难者遗体目前为196具，已全数被乌克兰顿涅茨克当地民间武装转移走，被移至未知地点。",
				"新华网北京7月19日电(记者 张正富 郭信峰) 在中国经济2014年“半年报”发布的本周，李克强总理连续主持召开三次会议，并在把脉中国经济形势的基础上，明确了下半年宏观调控的新指南——要“喷灌”“滴灌”，不搞“大水漫灌”。",
				"受台风影响，琼粤桂三省区几十万群众水电中断。在灾区普遍35℃高温下，红十字会调拨几千条棉被和夹克衫引质疑。对此，中国红十字会回应称，三伏天在海南、广东，一些受灾山区、丘陵地区群众，因早晚温差大、湿气过重等原因，仍需要被褥。",
				"当地时间2014年7月19日，乌克兰Grabovo，救援人员在马航M17坠机事故的现场。国际社会要求控制马航MH17坠机事故现场的亲俄武装提供独立调查击落该机的凶手提供”迅速、全面、可靠、畅通无阻的”配合。" };
		voice.voice(Tool_news.this, "新闻播报界面");
		mSpeech = voice.mSpeech;
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
				voice.voice(Tool_news.this, "无效动作，如需获得帮助，请长按屏幕");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingRight() {
				mSpeech.stop();
				voice.voice(Tool_news.this, "退出");
				mSpeech = voice.mSpeech;
				Tool_news.this.finish();
			}

			@Override
			public void OnFlingUp() {

				if (flag < 0)
					voice.voice(Tool_news.this, "请等待更新！");
				else {
					if (flag > 4)
						flag = 4;
					// web.GetData("http://122.207.182.59:8080/dog/servlet/selectnews?id="+flag,
					// Tool_news.this);
					mSpeech.stop();
					voice.voice(Tool_news.this, arr[flag]);
					mSpeech = voice.mSpeech;
					flag--;
				}

			}

			@Override
			public void OnFlingDown() {
				mSpeech.stop();
				if (flag <= 4) {
					if (flag < 0)
						flag = 0;
					voice.voice(Tool_news.this, arr[flag]);
					mSpeech = voice.mSpeech;
					flag++;
				} else {
					voice.voice(Tool_news.this, "当前已经没有更多新闻了。");
					mSpeech = voice.mSpeech;
				}
				// web.GetData("http://122.207.182.59:8080/dog/servlet/selectnews?id="+flag,
				// Tool_news.this);

			}

			@Override
			public void OnFling() {
				mSpeech.stop();
				voice.voice(Tool_news.this, "新闻播报界面");
				mSpeech = voice.mSpeech;
			}

			@Override
			public void OnFlingLong() {
				mSpeech.stop();
				voice.voice(Tool_news.this, "上、下滑动切换新闻，向右滑动退出，其他动作无效。");
				mSpeech = voice.mSpeech;
			}

		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}
}
