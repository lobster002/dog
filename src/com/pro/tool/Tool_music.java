package com.pro.tool;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.pro.phone.R;
import com.pro.voice.Voice;

public class Tool_music extends Activity {
	private MusicHelper mMusicHelper;

	private TextView musicInfo;
	private TextView name_tv;
	private ImageView music_img;

	private int startX, endX, startY, endY, dx, dy, moveX, moveY;

	private Voice voice = new Voice();
	TextToSpeech mSpeech; // 语音辅佐

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.tool_music);
		mMusicHelper = new MusicHelper(Tool_music.this);
		musicInfo = (TextView) findViewById(R.id.music_info);
		musicInfo.setText(mMusicHelper.getMusicName());

		voice.voice(Tool_music.this, "当前歌曲为：" + mMusicHelper.getMusicName());
		mSpeech = voice.mSpeech;

		name_tv = (TextView) findViewById(R.id.name_tv);
		music_img = (ImageView) findViewById(R.id.music_img);
		mMusicHelper.playMusic();
	}

	// 释放音乐空间
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			startX = (int) event.getRawX();
			startY = (int) event.getRawY();
			// downtime=event.getDownTime();
			break;
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getRawX();
			moveY = (int) event.getRawY();
			// movetime=event.getEventTime();
			// mlongpress=islongpress(startX, startY, moveX, moveY, downtime,
			// movetime, 1500);
			/*
			 * if(mlongpress){ if(mMusicHelper.isPlaying()){
			 * mMusicHelper.pause(); }
			 * musicInfo.setText(name_tv.getText().toString());
			 * mMusicHelper.nameplayMusic(name_tv.getText().toString()); }
			 */
			break;
		case MotionEvent.ACTION_UP:
			endX = (int) event.getRawX();
			endY = (int) event.getRawY();
			dx = startX - endX;
			dy = startY - endY;
			/*
			 * if(!mlongpress){ play(dx,dy); }
			 */
			play(dx, dy);
			break;
		}
		return true;
	}

	// 判断是否为长按
	private boolean islongpress(int startx, int starty, int movex, int movey,
			long downtime, long movetime, long longpresstime) {
		int offsetX = Math.abs(moveX - startX);
		int offsetY = Math.abs(moveY - startY);
		long intervalTime = movetime - downtime;
		if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longpresstime) {
			return true;
		} else {
			return false;
		}
	}

	// 播放音乐
	private void play(int dx, int dy) {
		if (dx * dx + dy * dy < 800) {
			if (mMusicHelper.isPlaying()) {
				mMusicHelper.pause();
			} else {
				mMusicHelper.playMusic();
			}
		} else {
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx <= 0) {
					mMusicHelper.release();
					mSpeech.stop();
					voice.voice(Tool_music.this, "退出");
					mSpeech = voice.mSpeech;
					if (mSpeech != null)
						mSpeech.shutdown();
					Tool_music.this.finish();
				}
			}
			if (Math.abs(dx) < Math.abs(dy)) {
				if (dy >= 0) {
					mMusicHelper.nextMusic();
					musicInfo.setText(mMusicHelper.getMusicName());

				} else {

					mMusicHelper.prevMusic();
					musicInfo.setText(mMusicHelper.getMusicName());

				}
			}
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

}
