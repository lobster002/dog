package com.pro.tool;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.pro.phone.R;
import com.pro.touch.GestureHelper;
import com.pro.touch.GestureHelper.OnFlingListener;
import com.pro.voice.Voice;

public class AlterActivity extends Activity {

	TextToSpeech mSpeech;
	private Voice voice = new Voice();
	private GestureHelper gh; // 手势判定
	MediaPlayer mediaPlayer01;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alter_alarm);
		/*
		 * alarm_alter_img=(ImageView)findViewById(R.id.alarm_alater_img);
		 * alarm_alter_img.setBackgroundResource(R.anim.tool_alter_anim);
		 * alarm_alter_anim=(AnimationDrawable) alarm_alter_img.getBackground();
		 * alarm_alter_img.post(new Runnable() {
		 * 
		 * @Override public void run() { alarm_alter_anim.start(); } });
		 */
		Toast.makeText(AlterActivity.this, "时间到了!", Toast.LENGTH_LONG).show();

		mediaPlayer01 = new MediaPlayer();
		mediaPlayer01 = MediaPlayer.create(this, R.raw.colock);
		mediaPlayer01.start();

		voice.voice(AlterActivity.this, "");
		mSpeech = voice.mSpeech;

		gesture(); // 手势
	}

	// 手势
	private void gesture() {
		gh = new GestureHelper(this);
		gh.setOnFlingListener(new OnFlingListener() {
			@Override
			public void OnFlingLeft() {
				mediaPlayer01.stop();
				mediaPlayer01.release();
				mSpeech.stop();
				AlterActivity.this.finish();
			}

			@Override
			public void OnFlingRight() {
				mediaPlayer01.stop();
				mediaPlayer01.release();
				mSpeech.stop();
				// mediaPlayer01.stop();
				AlterActivity.this.finish();
			}

			@Override
			public void OnFlingUp() {
				mediaPlayer01.stop();
				mediaPlayer01.release();
				mSpeech.stop();
				AlterActivity.this.finish();

			}

			@Override
			public void OnFlingDown() {
				mediaPlayer01.stop();
				mediaPlayer01.release();
				mSpeech.stop();
				AlterActivity.this.finish();
			}

			@Override
			public void OnFling() {
				mediaPlayer01.stop();
				mediaPlayer01.release();
				mSpeech.stop();
				AlterActivity.this.finish();
			}

			@Override
			public void OnFlingLong() {
			}
		});

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gh.onTouchEvent(event);
	}

}
